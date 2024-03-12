package com.test.microstreaming.rabbitmq;

import com.test.microstreaming.managers.statistics.IStatisticsManager;
import com.test.microstreaming.models.message.OpenGateMessage;
import com.test.microstreaming.utils.JSONUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RabbitConsumer {

    @Value("${rabbitmq.queue}")
    protected String queue;

    @Value("${rabbitmq.batch.size:10}")
    protected int batchSize;

    @Autowired
    protected AmqpTemplate amqpTemplate;

    @Autowired
    protected IStatisticsManager statisticsManager;

    @Scheduled(fixedRateString = "${rabbitmq.consume.rate:60000}")
    public void consume() {
        OpenGateMessage message = new OpenGateMessage();
        int receivedMessages = 0;
        while (message != null && receivedMessages < batchSize) {
            message = JSONUtils.fromJson((String) amqpTemplate.receiveAndConvert(queue), OpenGateMessage.class);
            if (message != null) {
                statisticsManager.processMessage(message);
                receivedMessages++;
            }
        }
    }
}
