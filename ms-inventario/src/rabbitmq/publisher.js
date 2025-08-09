let channel;

async function setChannel(ch) {
  channel = ch;
}

async function publicarInventarioAjustado(payload) {
  if (!channel) throw new Error("Canal RabbitMQ no inicializado");
  const exchange = "inventario_exchange";
  const routingKey = "inventario_ajustado";
  channel.assertExchange(exchange, "direct", { durable: true });
  channel.publish(exchange, routingKey, Buffer.from(JSON.stringify(payload)));
  console.log("[x] Evento inventario_ajustado publicado:", payload);
}

module.exports = {
  setChannel,
  publicarInventarioAjustado,
};
