package central.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import central.model.Cosecha;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public EventPublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishNuevaCosecha(Cosecha cosecha) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("cosecha_id", cosecha.getCosechaId());
        payload.put("producto", cosecha.getProducto());
        payload.put("toneladas", cosecha.getToneladas());
        payload.put("timestamp", Instant.now().toString());

        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "nueva_cosecha");
        event.put("timestamp", Instant.now().toString());
        event.put("payload", payload);

        try {
            String message = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend("cosechas", "nueva", message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
