package com.test.microstreaming.managers.statistics;

import com.test.microstreaming.models.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
public class StatisticsManager {

    @Autowired
    private StatisticsRepository statisticsRepository;

    public List<Statistics> getAllStatistics() {
        return statisticsRepository.findAll();
    }

    public Statistics save(Statistics statistics) {
        return statisticsRepository.save(statistics);
    }

    public Statistics findById(String id) throws NoSuchElementException {
        Optional<Statistics> statistics = statisticsRepository.findById(id);
        if (statistics.isEmpty()) {
            throw new NoSuchElementException("Statistics not found with id: " + id);
        }
        return statistics.get();
    }

    public boolean existsById(String id) {
        return statisticsRepository.existsById(id);
    }
}
