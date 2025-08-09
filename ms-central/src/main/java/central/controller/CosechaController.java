package central.controller;

import central.model.Agricultor;
import central.model.Cosecha;
import central.service.AgricultorService;
import central.service.CosechaService;
import central.dto.CosechaRequest;
import central.rabbitmq.EventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cosechas")
public class CosechaController {

    private final CosechaService cosechaService;
    private final AgricultorService agricultorService;
    private final EventPublisher eventPublisher;

    public CosechaController(CosechaService cosechaService, AgricultorService agricultorService, EventPublisher eventPublisher) {
        this.cosechaService = cosechaService;
        this.agricultorService = agricultorService;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping
    public List<Cosecha> getAll() {
        return cosechaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cosecha> getById(@PathVariable UUID id) {
        return cosechaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Cosecha> create(@Valid @RequestBody CosechaRequest request) {
        Agricultor agricultor = agricultorService.findById(request.getAgricultorId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agricultor no encontrado"));

        Cosecha cosecha = new Cosecha();
        cosecha.setAgricultor(agricultor);
        cosecha.setProducto(request.getProducto());
        cosecha.setToneladas(request.getToneladas());
        cosecha.setEstado(request.getEstado());

        Cosecha saved = cosechaService.save(cosecha);

        eventPublisher.publishNuevaCosecha(saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cosecha> update(@PathVariable UUID id, @Validated @RequestBody Cosecha cosecha) {
        return cosechaService.findById(id)
                .map(existing -> {
                    cosecha.setCosechaId(id);
                    return ResponseEntity.ok(cosechaService.save(cosecha));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (cosechaService.findById(id).isPresent()) {
            cosechaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
