package br.com.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
public class Config {

    private final ConnectionFactory connectionFactory;

    static final String EXCHANGE = "EXCHANGE";

    //QUEUE
    static final String QUEUE = "QUEUE";
    static final String  BINDING_KEY = "TO-QUEUE";

    //WAIT
    private static final String WAIT = ".WAIT";
    private static final String WAIT_QUEUE = QUEUE + WAIT;
    private static final String WAIT_BINDING_KEY = BINDING_KEY + WAIT;

    //DLQ
    private static final String DLQ = ".DLQ";
    private static final String DLQ_QUEUE = QUEUE + DLQ;
    static final String DLQ_BINDING_KEY = BINDING_KEY + DLQ;

    private static final int tll = 10000;

    @PostConstruct
    public void createRabbitElements() {

        var rabbitAdmin = new RabbitAdmin(connectionFactory);

        createQueue(rabbitAdmin);
        createWait(rabbitAdmin);
        createDLQ(rabbitAdmin);
    }

    private void createWait(final RabbitAdmin rabbitAdmin) {

        var exchange = ExchangeBuilder
                .directExchange(EXCHANGE)
                .durable(true)
                .build();

        var queue = QueueBuilder
                .durable(WAIT_QUEUE)
                .ttl(tll)
                .deadLetterExchange(EXCHANGE)
                .deadLetterRoutingKey(BINDING_KEY)
                .build();

        var binding = new Binding(
                WAIT_QUEUE,
                Binding.DestinationType.QUEUE,
                EXCHANGE,
                WAIT_BINDING_KEY,
                null);

        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(binding);
    }

    private void createDLQ(final RabbitAdmin rabbitAdmin) {

        var exchange = ExchangeBuilder
                .directExchange(EXCHANGE)
                .durable(true)
                .build();

        var queue = QueueBuilder
                .durable(DLQ_QUEUE)
                .build();

        var binding = new Binding(
                DLQ_QUEUE,
                Binding.DestinationType.QUEUE,
                EXCHANGE,
                DLQ_BINDING_KEY,
                null);

        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(binding);
    }

    private void createQueue(final RabbitAdmin rabbitAdmin) {

        var exchange = ExchangeBuilder
                .directExchange(EXCHANGE)
                .durable(true)
                .build();


        var queue = QueueBuilder
                .durable(QUEUE)
                .deadLetterExchange(EXCHANGE)
                .deadLetterRoutingKey(WAIT_BINDING_KEY)
                .build();

        var binding = new Binding(
                QUEUE,
                Binding.DestinationType.QUEUE,
                EXCHANGE,
                BINDING_KEY,
                null);

        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(binding);
    }
}
