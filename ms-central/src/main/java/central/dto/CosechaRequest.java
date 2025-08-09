package central.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class CosechaRequest {

    @NotNull(message = "El agricultorId no debe ser nulo")
    private UUID agricultorId;

    @NotBlank(message = "El producto es obligatorio")
    private String producto;

    @NotNull(message = "Las toneladas son obligatorias")
    private Double toneladas;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    // Getters y Setters
    public UUID getAgricultorId() {
        return agricultorId;
    }

    public void setAgricultorId(UUID agricultorId) {
        this.agricultorId = agricultorId;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public Double getToneladas() {
        return toneladas;
    }

    public void setToneladas(Double toneladas) {
        this.toneladas = toneladas;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
