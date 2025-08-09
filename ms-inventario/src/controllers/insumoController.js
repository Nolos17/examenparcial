const insumoService = require("../services/insumoService");

async function getInsumos(req, res) {
  try {
    const insumos = await insumoService.listarInsumos();
    res.json(insumos);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
}

async function createInsumo(req, res) {
  try {
    const insumo = await insumoService.crearInsumo(req.body);
    res.status(201).json(insumo);
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
}

async function updateInsumo(req, res) {
  try {
    const insumo = await insumoService.actualizarInsumo(
      req.params.id,
      req.body
    );
    res.json(insumo);
  } catch (error) {
    res.status(404).json({ error: error.message });
  }
}

async function deleteInsumo(req, res) {
  try {
    await insumoService.eliminarInsumo(req.params.id);
    res.status(204).send();
  } catch (error) {
    res.status(404).json({ error: error.message });
  }
}

module.exports = {
  getInsumos,
  createInsumo,
  updateInsumo,
  deleteInsumo,
};
