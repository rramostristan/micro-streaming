package com.test.microstreaming.rabbitmq;

import com.test.microstreaming.managers.statistics.IStatisticsManager;
import com.test.microstreaming.models.Statistics;
import com.test.microstreaming.models.StatisticsResults;
import com.test.microstreaming.models.message.OpenGateDataStreamMessage;
import com.test.microstreaming.models.message.OpenGateDatapoint;
import com.test.microstreaming.models.message.OpenGateMessage;
import com.test.microstreaming.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class RabbitConsumer {

    protected static Logger logger = LoggerFactory.getLogger(RabbitConsumer.class);

    @Value("${rabbitmq.queue}")
    protected String queue;

    @Value("${rabbitmq.batch.size:10}")
    protected int batchSize;

    @Autowired
    protected AmqpTemplate amqpTemplate;

    @Autowired
    protected IStatisticsManager statisticsManager;

    @Autowired
    protected RabbitConfig rabbitConfig;

    @Scheduled(fixedRateString = "${rabbitmq.consume.rate:60000}")
    public void consume() {
        if (rabbitConfig.checkConnectionAvailable()) {
            OpenGateMessage message = new OpenGateMessage();
            int receivedMessages = 0;
            List<Integer> values = new ArrayList<>();
            byte[] content = new byte[]{};
            while (content != null && receivedMessages < batchSize) {
                try {
                    content = (byte[]) amqpTemplate.receiveAndConvert(queue);
                    if (content != null) {
                        message = JSONUtils.fromJson(new String(content), OpenGateMessage.class);
                        for (OpenGateDataStreamMessage dataStreamMessage : message.getDatastreams()) {
                            for (OpenGateDatapoint datapoint : dataStreamMessage.getDatapoints()) {
                                values.add((int) datapoint.getValue());
                            }
                        }
                        receivedMessages++;
                    }
                } catch (Exception e) {
                    logger.error("Error consuming message", e);
                }
            }
            processMessages(values, receivedMessages);
        } else {
            logger.warn("Connection not availble");
        }
    }

    public void processMessages(List<Integer> values, int messagesProcessed) {
        if (CollectionUtils.isEmpty(values)) {
            logger.warn("No values were found");
            return;
        }
        Statistics statistics = new Statistics();
        statistics.setDataResults(getStatisticsResults(values));
        statistics.setMessagesProcessed(messagesProcessed);
        statisticsManager.save(statistics);
        logger.info("Processed new batch of {} messages", messagesProcessed);
    }

    protected StatisticsResults getStatisticsResults(List<Integer> values) {
        StatisticsResults statisticsResults = new StatisticsResults();
        statisticsResults.setMean(getMean(values));
        statisticsResults.setMode(getMode(values));
        statisticsResults.setMedian(getMedian(values));
        statisticsResults.setMaximum(Collections.max(values));
        statisticsResults.setMinimum(Collections.min(values));
        statisticsResults.setStandardDeviation(getStandardDeviation(values));
        statisticsResults.setQuartiles(List.of(getPercentile(values, 0.25), getPercentile(values, 0.5), getPercentile(values, 0.75)));
        return statisticsResults;
    }

    protected double getMean(List<Integer> values) {
        int sum = 0;
        for (int num : values) {
            sum += num;
        }
        return (double) sum / values.size();
    }

    protected double getMedian(List<Integer> values) {
        int n = values.size();
        if (n % 2 == 0) {
            return (values.get(n / 2 - 1) + values.get(n / 2)) / 2.0;
        } else {
            return values.get(n / 2);
        }
    }

    protected int getMode(List<Integer> values) {
        int maxCount = 0;
        int moda = 0;
        for (int i = 0; i < values.size(); ++i) {
            int count = 0;
            for (int j = 0; j < values.size(); ++j) {
                if (values.get(j) == values.get(i)) ++count;
            }
            if (count > maxCount) {
                maxCount = count;
                moda = values.get(i);
            }
        }
        return moda;
    }

    protected double getPercentile(List<Integer> values, double percentile) {
        int n = values.size();
        double index = percentile * (n - 1);
        if (index % 1 == 0) {
            return values.get((int) index);
        } else {
            int lower = (int) Math.floor(index);
            int upper = (int) Math.ceil(index);
            return (values.get(lower) + values.get(upper)) / 2.0;
        }
    }

    protected double getStandardDeviation(List<Integer> values) {
        double media = getMean(values);
        double sum = 0;
        for (int num : values) {
            sum += Math.pow(num - media, 2);
        }
        return Math.sqrt(sum / values.size());
    }
}
