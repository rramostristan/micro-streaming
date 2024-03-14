package com.test.microstreaming.rabbitmq;

import com.test.microstreaming.models.message.OpenGateDataStreamMessage;
import com.test.microstreaming.models.message.OpenGateDatapoint;
import com.test.microstreaming.models.message.OpenGateMessage;
import com.test.microstreaming.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Component
@ConditionalOnProperty(name = "rabbitmq.producer.enabled", havingValue = "true", matchIfMissing = false)
public class RabbitProducer {

    protected static Logger logger = LoggerFactory.getLogger(RabbitProducer.class);
    @Value("${rabbitmq.queue}")
    protected String queue;

    @Autowired
    protected AmqpTemplate rabbitTemplate;

    @Autowired
    protected RabbitConfig rabbitConfig;

    @Scheduled(fixedRateString = "${rabbitmq.produce.rate:60000}")
    public void sendMessage() {
        if (rabbitConfig.checkConnectionAvailable()) {
            try {
                rabbitTemplate.convertAndSend(queue, JSONUtils.toJSON(generateMessage("Device 1")).getBytes(StandardCharsets.UTF_8));
                rabbitTemplate.convertAndSend(queue, JSONUtils.toJSON(generateMessage("Device 2")).getBytes(StandardCharsets.UTF_8));
                logger.info("Two new messages sent");
            } catch (Exception e) {
                logger.error("Error sending messages", e);
            }
        }
    }

    protected OpenGateMessage generateMessage(String device) {
        OpenGateMessage message = new OpenGateMessage();
        message.setDevice(device);
        message.setVersion("0.1");
        message.setPath("192.168.0.1");
        message.setTrustedBoot("REST");
        message.setDatastreams(List.of(getDataStream("temperature" + System.currentTimeMillis(), "temperature"), getDataStream("pressure" + System.currentTimeMillis(),"pressure")));
        return message;
    }

    protected OpenGateDataStreamMessage getDataStream(String id, String feed) {
        OpenGateDataStreamMessage stream = new OpenGateDataStreamMessage();
        stream.setId(id);
        stream.setFeed(id);
        int limit = Objects.equals("temperature", id) ? 40 : 300;
        List<OpenGateDatapoint> datapoints = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            datapoints.add(getDatapoint(generateRandomInt(limit)));
        }
        stream.setDatapoints(datapoints);
        return stream;
    }

    protected OpenGateDatapoint getDatapoint(Object value) {
        OpenGateDatapoint datapoint = new OpenGateDatapoint();
        datapoint.setAt(System.currentTimeMillis());
        datapoint.setFrom(System.currentTimeMillis());
        datapoint.setValue(value);
        return datapoint;
    }

    protected int generateRandomInt(int limit) {
        Random rand = new Random();
        return rand.nextInt(limit);
    }
}
