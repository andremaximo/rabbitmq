package br.com.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class Consumer {

    @RabbitListener(queues = { "QUEUE" })
    public void receive(Message message) {

        var mensagem = new String(message.getBody());

        log.info("Consumindo mensagem: {}", mensagem);

        var headers = message.getMessageProperties().getHeaders();

        log.info("Quantidade de processamentos: {}", headers.get("x-death") == null ? 0 : headers.get("count"));

        if(mensagem.contains("erro")){

            throw new AmqpRejectAndDontRequeueException("erro");

        }else if(mensagem.contains("sucesso")){

            log.info("Mensagem consumida : {}", mensagem);
        }
    }
}
