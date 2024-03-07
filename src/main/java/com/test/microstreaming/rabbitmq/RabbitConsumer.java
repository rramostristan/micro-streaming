package com.test.microstreaming.rabbitmq;

import com.test.microstreaming.models.OpenGateMessage;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RabbitConsumer {

    @Value("${rabbitmq.queue}")
    protected String queue;

    protected int batchSize = 10;

    @Autowired
    protected AmqpTemplate amqpTemplate;

    @Scheduled(fixedRate = 6000)
    public void consume() {
        OpenGateMessage message = new OpenGateMessage();
        int receivedMessages = 0;
        while (message != null || receivedMessages < 10) {
            message = (OpenGateMessage) amqpTemplate.receiveAndConvert(queue);
            if (message != null) {
                System.out.println(message.toString());
                receivedMessages++;
            }
        }
    }
}
