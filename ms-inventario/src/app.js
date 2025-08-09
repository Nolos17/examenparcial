const express = require("express");
const insumoRoutes = require("./routes/insumoRoutes");
require("dotenv").config();

const app = express();

app.use(express.json());
app.use(insumoRoutes);

app.get("/", (req, res) => {
  res.send("Microservicio Inventario OK");
});

module.exports = app;
