package com.test.microstreaming.managers.statistics;

import com.test.microstreaming.models.Statistics;
import com.test.microstreaming.models.message.OpenGateMessage;

import java.util.List;
import java.util.NoSuchElementException;

public interface IStatisticsManager {

    List<Statistics> getAllStatistics();

    Statistics save(Statistics statistics);

    Statistics findById(String id) throws NoSuchElementException;

    boolean existsById(String id);

    List<Statistics> findByFeed(String feed);

    List<Statistics> findByPath(String path);

    List<Statistics> findByVersion(String version);

    List<Statistics> findByTrustedBoot(String trustedBoot);

    void processMessage(OpenGateMessage message);
}
