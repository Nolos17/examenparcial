require("dotenv").config(); // cargar .env al inicio

const express = require("express");
const bodyParser = require("body-parser");
const amqp = require("amqplib");

const insumoRoutes = require("./src/routes/insumoRoutes"); // ajustar ruta
const insumoService = require("./src/services/insumoService"); // ajustar ruta
const sequelize = require("./src/config/db"); // ajustar ruta

const app = express();
const PORT = process.env.PORT || 3000;
const RABBITMQ_URL = process.env.RABBITMQ_URL || "amqp://localhost";

app.use(bodyParser.json());
app.use("/", insumoRoutes);

async function start() {
  try {
    // Conectar a MySQL y sincronizar modelos
    await sequelize.authenticate();
    await sequelize.sync();
    console.log("Conectado a MySQL correctamente");

    // Conectar a RabbitMQ
    const connection = await amqp.connect(RABBITMQ_URL);
    const channel = await connection.createChannel();

    const exchange = "cosechas_exchange";
    const queue = "cola_inventario";
    const routingKey = "nueva_cosecha";

    await channel.assertExchange(exchange, "direct", { durable: true });
    await channel.assertQueue(queue, { durable: true });
    await channel.bindQueue(queue, exchange, routingKey);

    console.log("[*] Esperando mensajes en cola_inventario...");

    channel.consume(queue, async (msg) => {
      if (msg !== null) {
        try {
          const content = JSON.parse(msg.content.toString());
          console.log("[>] Evento recibido:", content);

          const toneladas = content.payload.toneladas;
          const cosechaId = content.payload.cosecha_id;
          const insumosNecesarios = [
            { nombre: "Semilla Arroz L-23", kgPorTonelada: 5 },
            { nombre: "Fertilizante N-PK", kgPorTonelada: 2 },
          ];

          for (const insumo of insumosNecesarios) {
            const cantidad = toneladas * insumo.kgPorTonelada;
            await insumoService.descontarStock(insumo.nombre, cantidad);
            console.log(`Stock descontado: ${cantidad} kg de ${insumo.nombre}`);
          }

          // Publicar evento confirmación inventario ajustado
          const eventoConfirmacion = {
            event_type: "inventario_ajustado",
            cosecha_id: cosechaId,
            status: "OK",
            timestamp: new Date().toISOString(),
          };

          channel.publish(
            exchange,
            "inventario_ajustado",
            Buffer.from(JSON.stringify(eventoConfirmacion))
          );
          console.log("[>] Evento inventario_ajustado publicado");

          channel.ack(msg);
        } catch (err) {
          console.error("Error procesando mensaje:", err.message);
          channel.nack(msg, false, false);
        }
      }
    });

    app.listen(PORT, () => {
      console.log(`Servidor Inventario escuchando en puerto ${PORT}`);
    });
  } catch (error) {
    console.error("Error iniciando servidor:", error);
  }
}

start();
