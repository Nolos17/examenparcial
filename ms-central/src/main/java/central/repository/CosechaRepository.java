package central.repository;

import central.model.Cosecha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CosechaRepository extends JpaRepository<Cosecha, UUID> {
}

