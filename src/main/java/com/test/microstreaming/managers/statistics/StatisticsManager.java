package com.test.microstreaming.managers.statistics;

import com.test.microstreaming.models.DataStream;
import com.test.microstreaming.models.Statistics;
import com.test.microstreaming.models.StatisticsResults;
import com.test.microstreaming.models.message.OpenGateDataStreamMessage;
import com.test.microstreaming.models.message.OpenGateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class StatisticsManager implements IStatisticsManager {

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Override
    public List<Statistics> getAllStatistics() {
        return statisticsRepository.findAll();
    }

    @Override
    public Statistics save(Statistics statistics) {
        return statisticsRepository.save(statistics);
    }

    @Override
    public Statistics findById(String id) throws NoSuchElementException {
        Optional<Statistics> statistics = statisticsRepository.findById(id);
        if (statistics.isEmpty()) {
            throw new NoSuchElementException("Statistics not found with id: " + id);
        }
        return statistics.get();
    }

    @Override
    public boolean existsById(String id) {
        return statisticsRepository.existsById(id);
    }

    @Override
    public List<Statistics> findByFeed(String feed) {
        return statisticsRepository.findByDataStreams_Feed(feed);
    }

    @Override
    public List<Statistics> findByPath(String path) {
        return statisticsRepository.findByPath(path);
    }

    @Override
    public List<Statistics> findByVersion(String version) {
        return statisticsRepository.findByVersion(version);
    }

    @Override
    public List<Statistics> findByTrustedBoot(String trustedBoot) {
        return statisticsRepository.findByTrustedBoot(trustedBoot);
    }

    @Override
    public void processMessage(OpenGateMessage message) {
        Statistics statistics;
        List<DataStream> dataStreams;
        if (existsById(message.getDevice())) {
            statistics = findById(message.getDevice());
            dataStreams = statistics.getDataStreams();
        } else {
            statistics = new Statistics(message.getDevice(), message.getPath(), message.getTrustedBoot(), message.getVersion());
            dataStreams = new ArrayList<>();
        }

        List<OpenGateDataStreamMessage> dataStreamMessages = message.getDatastreams();
        for (OpenGateDataStreamMessage dataStreamMessage : dataStreamMessages) {
            List<Integer> values = dataStreamMessage.getDatapoints().stream().map(datapoint -> (Integer) datapoint.getValue()).collect(Collectors.toList());
            Collections.sort(values);
            dataStreams.add(generateDataStream(dataStreamMessage.getId(), dataStreamMessage.getFeed(), getStatisticsResults(values)));
        }
        statistics.setDataStreams(dataStreams);
        save(statistics);
    }

    protected DataStream generateDataStream(String id, String feed, StatisticsResults results) {
        DataStream dataStream = new DataStream();
        dataStream.setId(id);
        dataStream.setFeed(feed);
        dataStream.setResults(results);
        return dataStream;
    }

    protected StatisticsResults getStatisticsResults(List<Integer> values) {
        StatisticsResults statisticsResults = new StatisticsResults();
        statisticsResults.setMedia(getMedia(values));
        statisticsResults.setModa(getModa(values));
        statisticsResults.setMediana(getMediana(values));
        statisticsResults.setMaximo(Collections.max(values));
        statisticsResults.setMinimo(Collections.min(values));
        statisticsResults.setDesviacionEstandar(getDesviacionEstandar(values));
        statisticsResults.setCuartiles(List.of(getPercentil(values, 0.25), getPercentil(values, 0.5), getPercentil(values, 0.75)));
        return statisticsResults;
    }

    protected double getMedia(List<Integer> values) {
        int sum = 0;
        for (int num : values) {
            sum += num;
        }
        return (double) sum / values.size();
    }

    protected double getMediana(List<Integer> values) {
        int n = values.size();
        if (n % 2 == 0) {
            return (values.get(n / 2 - 1) + values.get(n / 2)) / 2.0;
        } else {
            return values.get(n / 2);
        }
    }

    protected int getModa(List<Integer> values) {
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

    protected double getPercentil(List<Integer> values, double percentile) {
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

    protected double getDesviacionEstandar(List<Integer> values) {
        double media = getMedia(values);
        double sum = 0;
        for (int num : values) {
            sum += Math.pow(num - media, 2);
        }
        return Math.sqrt(sum / values.size());
    }
}
