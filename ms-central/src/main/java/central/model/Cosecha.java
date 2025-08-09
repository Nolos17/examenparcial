package central.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cosechas")
public class Cosecha {

    @Id
    @Column(name = "cosecha_id", nullable = false, updatable = false)
    private UUID cosechaId = UUID.randomUUID();

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agricultor_id", nullable = false)
    private Agricultor agricultor;

    @NotBlank(message = "Producto es obligatorio")
    private String producto;

    @DecimalMin(value = "0.0", inclusive = true, message = "Toneladas debe ser mayor o igual a 0")
    private double toneladas;

    @NotBlank
    @Column(length = 20)
    private String estado = "REGISTRADA";

    @NotNull
    private Instant creadoEn = Instant.now();

    private UUID facturaId;
}
