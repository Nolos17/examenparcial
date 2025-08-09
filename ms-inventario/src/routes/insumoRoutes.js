const express = require("express");
const router = express.Router();
const insumoController = require("../controllers/insumoController");

router.get("/insumos", insumoController.getInsumos);
router.post("/insumos", insumoController.createInsumo);
router.put("/insumos/:id", insumoController.updateInsumo);
router.delete("/insumos/:id", insumoController.deleteInsumo);

module.exports = router;
