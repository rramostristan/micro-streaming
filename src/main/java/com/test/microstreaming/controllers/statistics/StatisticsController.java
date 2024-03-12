package com.test.microstreaming.controllers.statistics;

import com.test.microstreaming.managers.statistics.IStatisticsManager;
import com.test.microstreaming.models.Statistics;
import com.test.microstreaming.models.message.OpenGateMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    protected IStatisticsManager statisticsManager;

    @GetMapping
    @Operation(description = "Return all Statistics documents stored in the database")
    public List<Statistics> findAll() {
        return statisticsManager.getAllStatistics();
    }

    @GetMapping("/{id}")
    @Operation(description = "Returns the Statistics with the deviceId passed as parameter, in case it doesn't exist it will throw an execption")
    public Statistics findById(@Parameter(description = "Id of the device") @PathVariable String id) {
        return statisticsManager.findById(id);
    }

    @GetMapping("/feed/{feed}")
    @Operation(description = "Returns the Statistics with at least one dataStream that has the same feed as the one passed by parameter, it's important to notice it doesn't actually filter the dataStreams with feeds other than the one passed by parameter")
    public List<Statistics> findByFeed(@PathVariable String feed) {
        return statisticsManager.findByFeed(feed);
    }

    @GetMapping("/version/{version}")
    @Operation(description = "Returns the Statistics with the same version as the passed as parameter")
    public List<Statistics> findByVersion(@PathVariable String version) {
        return statisticsManager.findByVersion(version);
    }

    @GetMapping("/path/{path}")
    @Operation(description = "Returns the Statistics with the deviceId passed as parameter, in case it doesn't exist it will throw an execption")
    public List<Statistics> findByPath(@PathVariable String path) {
        return statisticsManager.findByPath(path);
    }

    @GetMapping("/trustedboot/{trustedBoot}")
    @Operation(description = "Returns the Statistics with the deviceId passed as parameter, in case it doesn't exist it will throw an execption")
    public List<Statistics> findByTrustedBoot(@PathVariable String trustedBoot) {
        return statisticsManager.findByTrustedBoot(trustedBoot);
    }

    @PostMapping
    @Operation(description = "Returns the Statistics with the deviceId passed as parameter, in case it doesn't exist it will throw an execption")
    public void proccessMessage(@RequestBody OpenGateMessage message) {
        statisticsManager.processMessage(message);
    }
}
