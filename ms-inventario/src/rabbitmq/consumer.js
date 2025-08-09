const amqp = require("amqplib");
const insumoService = require("../services/insumoService");
const publisher = require("./publisher");

const RABBITMQ_URL =
  process.env.RABBITMQ_URL || "amqp://admin:admin@localhost:5672";

async function startConsumer() {
  const connection = await amqp.connect(RABBITMQ_URL);
  const channel = await connection.createChannel();

  await channel.assertExchange("cosechas_exchange", "direct", {
    durable: true,
  });
  const q = "cola_inventario";
  await channel.assertQueue(q, { durable: true });
  await channel.bindQueue(q, "cosechas_exchange", "nueva_cosecha");

  publisher.setChannel(channel);

  console.log(" [*] Esperando mensajes en cola_inventario...");

  channel.consume(
    q,
    async (msg) => {
      if (msg !== null) {
        try {
          const contenido = JSON.parse(msg.content.toString());
          console.log("[>] Evento recibido:", contenido);

          // Ejemplo fórmula: semilla = toneladas * 5, fertilizante = toneladas * 2
          const toneladas = contenido.payload.toneladas;
          const insumosNecesarios = [
            { nombre: "Semilla Arroz L-23", cantidad: -5 * toneladas },
            { nombre: "Fertilizante N-PK", cantidad: -2 * toneladas },
          ];

          for (const insumo of insumosNecesarios) {
            await insumoService.actualizarStock(insumo.nombre, insumo.cantidad);
            console.log(
              `Stock actualizado: ${insumo.nombre} -> ${insumo.cantidad}`
            );
          }

          // Publicar confirmación
          const eventoConfirmacion = {
            event_id: contenido.event_id || null,
            event_type: "inventario_ajustado",
            timestamp: new Date().toISOString(),
            payload: {
              cosecha_id: contenido.payload.cosecha_id,
              status: "OK",
            },
          };

          await publisher.publicarInventarioAjustado(eventoConfirmacion);

          channel.ack(msg);
        } catch (error) {
          console.error("Error procesando mensaje:", error.message);
          channel.nack(msg, false, false); // descartar mensaje
        }
      }
    },
    { noAck: false }
  );
}

module.exports = { startConsumer };
