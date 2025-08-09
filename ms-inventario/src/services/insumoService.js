const Insumo = require("../models/Insumo");

async function listarInsumos() {
  return await Insumo.findAll();
}

async function crearInsumo(data) {
  return await Insumo.create(data);
}

async function actualizarInsumo(id, data) {
  const insumo = await Insumo.findByPk(id);
  if (!insumo) throw new Error("Insumo no encontrado");
  return await insumo.update(data);
}

async function eliminarInsumo(id) {
  const insumo = await Insumo.findByPk(id);
  if (!insumo) throw new Error("Insumo no encontrado");
  return await insumo.destroy();
}

async function descontarStock(nombreInsumo, cantidad) {
  const insumo = await Insumo.findOne({ where: { nombreInsumo } });
  if (!insumo) throw new Error(`Insumo ${nombreInsumo} no encontrado`);
  if (insumo.stock < cantidad)
    throw new Error(`Stock insuficiente para ${nombreInsumo}`);
  insumo.stock -= cantidad;
  insumo.ultimaActualizacion = new Date();
  await insumo.save();
  return insumo;
}

async function actualizarStock(nombre, cantidad) {
  const insumo = await Insumo.findOne({ where: { nombre } });
  if (!insumo) throw new Error(`Insumo ${nombre} no encontrado`);
  // Permitir sumar o restar stock según cantidad positiva o negativa
  if (insumo.stock + cantidad < 0) {
    throw new Error(`Stock insuficiente para ${nombre}`);
  }
  insumo.stock += cantidad;
  insumo.ultimaActualizacion = new Date();
  await insumo.save();
  return insumo;
}

module.exports = {
  listarInsumos,
  crearInsumo,
  actualizarInsumo,
  eliminarInsumo,
  actualizarStock,
};
