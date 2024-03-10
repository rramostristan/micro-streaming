package com.test.microstreaming.controllers.statistics;

import com.test.microstreaming.managers.statistics.StatisticsManager;
import com.test.microstreaming.models.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    protected StatisticsManager statisticsManager;

    @GetMapping
    public List<Statistics> findAll() {
        return statisticsManager.getAllStatistics();
    }

    @GetMapping("/{id}")
    public Statistics findById(@PathVariable String id) {
        return statisticsManager.findById(id);
    }


}
