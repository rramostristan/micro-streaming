package com.test.microstreaming.managers.statistics;

import com.test.microstreaming.models.Statistics;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Date;
import java.util.List;

public interface StatisticsRepository extends MongoRepository<Statistics, String> {

    List<Statistics>  findByProcessedAtLessThan(Date processedAt);

    List<Statistics>  findByProcessedAtGreaterThan(Date processedAt);

    List<Statistics>  findByProcessedAtBetween(Date startDate, Date endDate);

    List<Statistics>  findByMessagesProcessed(int messagesProcessed);
}
