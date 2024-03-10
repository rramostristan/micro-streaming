package com.test.microstreaming.rabbitmq;

import com.test.microstreaming.models.message.OpenGateDataStreamMessage;
import com.test.microstreaming.models.message.OpenGateDatapoint;
import com.test.microstreaming.models.message.OpenGateMessage;
import com.test.microstreaming.utils.JSONUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Component
public class RabbitProducer {

    @Value("${rabbitmq.queue}")
    protected String queue;

    @Autowired
    protected AmqpTemplate rabbitTemplate;

    @Scheduled(fixedRateString = "${rabbitmq.produce.rate:60000}")
    public void sendMessage() {
        rabbitTemplate.convertAndSend(queue, JSONUtils.toJSON(generateMessage("Device 1")));
        rabbitTemplate.convertAndSend(queue, JSONUtils.toJSON(generateMessage("Device 2")));
    }

    protected OpenGateMessage generateMessage(String device) {
        OpenGateMessage message = new OpenGateMessage();
        message.setDevice(device);
        message.setVersion("0.1");
        message.setDatastreams(List.of(getDataStream("temperature"), getDataStream("pressure")));
        return message;
    }

    protected OpenGateDataStreamMessage getDataStream(String id) {
        OpenGateDataStreamMessage stream = new OpenGateDataStreamMessage();
        stream.setId(id);
        stream.setFeed(id);
        int limit = Objects.equals("temperature", id) ? 40 : 300;
        Long currentTime = System.currentTimeMillis();
        List<OpenGateDatapoint> datapoints = new ArrayList<>();
        for (int i=0; i<10; i++) {
            datapoints.add(getDatapoint(currentTime++,generateRandomInt(limit)));
        }
        stream.setDatapoints(datapoints);
        return stream;
    }


    protected OpenGateDatapoint getDatapoint(Long time, Object value) {
        OpenGateDatapoint datapoint = new OpenGateDatapoint();
        datapoint.setAt(time);
        datapoint.setValue(value);
        return datapoint;
    }

    protected int generateRandomInt(int limit) {
        Random rand = new Random();
        return rand.nextInt(limit);
    }
}
