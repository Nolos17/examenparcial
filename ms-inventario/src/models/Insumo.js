const { DataTypes } = require('sequelize');
const sequelize = require('../config/db');

const Insumo = sequelize.define('Insumo', {
  insumoId: {
    type: DataTypes.UUID,
    primaryKey: true,
    defaultValue: DataTypes.UUIDV4,
  },
  nombreInsumo: {
    type: DataTypes.STRING(100),
    unique: true,
    allowNull: false,
  },
  stock: {
    type: DataTypes.INTEGER,
    allowNull: false,
    defaultValue: 0,
    validate: { min: 0 },
  },
  unidadMedida: {
    type: DataTypes.STRING(10),
    allowNull: false,
    defaultValue: 'kg',
  },
  categoria: {
    type: DataTypes.STRING(30),
    allowNull: false,
  },
  ultimaActualizacion: {
    type: DataTypes.DATE,
    allowNull: false,
    defaultValue: DataTypes.NOW,
  },
}, {
  tableName: 'insumos',
  timestamps: false,
});

module.exports = Insumo;
