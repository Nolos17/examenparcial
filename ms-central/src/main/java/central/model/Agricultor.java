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
@Table(name = "agricultores")
public class Agricultor {

    @Id
    @Column(name = "agricultor_id", nullable = false, updatable = false)
    private UUID agricultorId = UUID.randomUUID();

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El nombre de la finca es obligatorio")
    private String finca;

    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;

    @Email(message = "Correo inválido")
    private String correo;

    @NotNull
    private Instant fechaRegistro = Instant.now();
}
