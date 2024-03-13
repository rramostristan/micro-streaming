package com.test.microstreaming.managers.statistics;

import com.test.microstreaming.models.Statistics;

import java.util.List;
import java.util.NoSuchElementException;

public interface IStatisticsManager {

    List<Statistics> getAllStatistics();

    Statistics save(Statistics statistics);

    Statistics findById(String id) throws NoSuchElementException;

    boolean existsById(String id);

    List<Statistics> findByDate(String startDate, String endDate);

    List<Statistics> findByMessagesProcessed(int messagesProcessed);
}
