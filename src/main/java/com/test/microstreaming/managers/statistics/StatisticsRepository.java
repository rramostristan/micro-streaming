package com.test.microstreaming.managers.statistics;

import com.test.microstreaming.models.Statistics;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface StatisticsRepository extends MongoRepository<Statistics, String> {

    List<Statistics> findByDataStreams_Feed(String feed);

    List<Statistics> findByVersion(String version);

    List<Statistics> findByPath(String path);

    List<Statistics> findByTrustedBoot(String trustedBoot);
}
