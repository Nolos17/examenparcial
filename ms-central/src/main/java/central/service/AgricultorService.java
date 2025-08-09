package central.service;

import central.model.Agricultor;
import central.repository.AgricultorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AgricultorService {

    private final AgricultorRepository agricultorRepository;

    public AgricultorService(AgricultorRepository agricultorRepository) {
        this.agricultorRepository = agricultorRepository;
    }

    public List<Agricultor> findAll() {
        return agricultorRepository.findAll();
    }

    public Optional<Agricultor> findById(UUID id) {
        return agricultorRepository.findById(id);
    }

    public Agricultor save(Agricultor agricultor) {
        return agricultorRepository.save(agricultor);
    }

    public void deleteById(UUID id) {
        agricultorRepository.deleteById(id);
    }
}
