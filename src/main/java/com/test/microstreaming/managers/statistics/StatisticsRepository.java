package com.test.microstreaming.managers.statistics;

import com.test.microstreaming.models.Statistics;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StatisticsRepository extends MongoRepository<Statistics, String> {
}
