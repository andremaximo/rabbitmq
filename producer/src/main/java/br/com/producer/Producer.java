package br.com.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("producers")
@RequiredArgsConstructor
@Slf4j
public class Producer {

    private final RabbitTemplate rabbitTemplate;

    @PostMapping("{exchange}/{routingKey}")
    public ResponseEntity<?> post(@PathVariable String exchange,
                                  @PathVariable String routingKey,
                                  @RequestBody String message){

        log.info("Enviando msg: {}\nExchange: {}\nRoutingKey: {}", message, exchange, routingKey);

        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        return ResponseEntity.ok().build();
    }
}
