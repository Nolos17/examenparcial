package central.controller;

import central.model.Agricultor;
import central.service.AgricultorService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/agricultores")
public class AgricultorController {

    private final AgricultorService agricultorService;

    public AgricultorController(AgricultorService agricultorService) {
        this.agricultorService = agricultorService;
    }

    @GetMapping
    public List<Agricultor> getAll() {
        return agricultorService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agricultor> getById(@PathVariable UUID id) {
        return agricultorService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Agricultor create(@Validated @RequestBody Agricultor agricultor) {
        return agricultorService.save(agricultor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Agricultor> update(@PathVariable UUID id, @Validated @RequestBody Agricultor agricultor) {
        return agricultorService.findById(id)
                .map(existing -> {
                    agricultor.setAgricultorId(id);
                    return ResponseEntity.ok(agricultorService.save(agricultor));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (agricultorService.findById(id).isPresent()) {
            agricultorService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
