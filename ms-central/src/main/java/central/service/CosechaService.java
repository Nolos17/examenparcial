package central.service;

import central.model.Cosecha;
import central.repository.CosechaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CosechaService {

    private final CosechaRepository cosechaRepository;

    public CosechaService(CosechaRepository cosechaRepository) {
        this.cosechaRepository = cosechaRepository;
    }

    public List<Cosecha> findAll() {
        return cosechaRepository.findAll();
    }

    public Optional<Cosecha> findById(UUID id) {
        return cosechaRepository.findById(id);
    }

    public Cosecha save(Cosecha cosecha) {
        return cosechaRepository.save(cosecha);
    }

    public void deleteById(UUID id) {
        cosechaRepository.deleteById(id);
    }
}
