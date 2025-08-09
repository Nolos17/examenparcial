import json
import uuid
import threading
import pika
import mysql.connector
from flask import Flask
import requests
import time

# Configuración DB MariaDB
DB_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': '',
    'database': 'facturacion_db'
}

# Configuración RabbitMQ
RABBITMQ_URL = 'amqp://admin:admin@localhost:5672/'


# Configuración Microservicio Central
CENTRAL_API_URL = 'http://localhost:8080'  # Ajusta según tu entorno

app = Flask(__name__)

def crear_factura(cosecha_id, producto, toneladas):
    PRECIOS = {
        "Arroz Oro": 120,
        "Cafe Premium": 300
    }
    precio_unitario = PRECIOS.get(producto, 100)  # Precio default 100 si no está
    monto = toneladas * precio_unitario
    factura_id = str(uuid.uuid4())

    # Insertar en DB
    conn = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor()
    cursor.execute("""
        INSERT INTO facturas (factura_id, cosecha_id, monto_total, pagado)
        VALUES (%s, %s, %s, %s)
    """, (factura_id, cosecha_id, monto, False))
    conn.commit()
    cursor.close()
    conn.close()

    return factura_id, monto

def actualizar_estado_cosecha(cosecha_id, factura_id):
    url = f"{CENTRAL_API_URL}/cosechas/{cosecha_id}/estado"
    payload = {
        "estado": "FACTURADA",
        "factura_id": factura_id
    }
    try:
        resp = requests.put(url, json=payload)
        if resp.status_code == 200:
            print(f"Estado de cosecha {cosecha_id} actualizado a FACTURADA")
        else:
            print(f"Error actualizando estado: {resp.status_code} {resp.text}")
    except Exception as e:
        print(f"Error en request a Central: {e}")

def consumir_mensajes():
    params = pika.URLParameters(RABBITMQ_URL)
    conexion = pika.BlockingConnection(params)
    canal = conexion.channel()

    exchange = "cosechas_exchange"
    queue = "cola_facturacion"
    routing_key = "nueva_cosecha"

    canal.exchange_declare(exchange=exchange, exchange_type='direct', durable=True)
    canal.queue_declare(queue=queue, durable=True)
    canal.queue_bind(queue=queue, exchange=exchange, routing_key=routing_key)

    def callback(ch, method, properties, body):
        print("[x] Evento recibido en facturación")
        try:
            mensaje = json.loads(body)
            cosecha_id = mensaje['payload']['cosecha_id']
            producto = mensaje['payload']['producto']
            toneladas = mensaje['payload']['toneladas']

            factura_id, monto = crear_factura(cosecha_id, producto, toneladas)
            print(f"Factura creada: {factura_id} por monto ${monto}")

            actualizar_estado_cosecha(cosecha_id, factura_id)

            # Publicar evento "inventario_ajustado" para notificar al Central si quieres
            # (opcional, si requiere otro evento)
            
            ch.basic_ack(delivery_tag=method.delivery_tag)
        except Exception as e:
            print(f"Error procesando mensaje: {e}")
            ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)

    canal.basic_consume(queue=queue, on_message_callback=callback)
    print("[*] Esperando mensajes en cola_facturacion...")
    canal.start_consuming()

@app.route("/")
def home():
    return "Microservicio Facturación activo"

if __name__ == "__main__":
    # Ejecutar consumidor RabbitMQ en hilo aparte para que Flask no bloquee
    thread = threading.Thread(target=consumir_mensajes)
    thread.start()

    app.run(host='0.0.0.0', port=5000)
