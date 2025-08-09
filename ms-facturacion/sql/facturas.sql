CREATE TABLE IF NOT EXISTS facturas (
  factura_id VARCHAR(36) PRIMARY KEY NOT NULL,
  cosecha_id VARCHAR(36) NOT NULL,
  monto_total DECIMAL(10,2) NOT NULL CHECK (monto_total > 0),
  pagado BOOLEAN DEFAULT FALSE,
  fecha_emision TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  metodo_pago VARCHAR(30),
  codigo_qr TEXT
);
