package com.test.microstreaming.controllers.statistics;

import com.test.microstreaming.managers.statistics.IStatisticsManager;
import com.test.microstreaming.models.Statistics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/statistics")
@Tag(name = "Statistics", description = "Statistics Controller")
public class StatisticsController {

    @Autowired
    protected IStatisticsManager statisticsManager;

    @GetMapping
    @Operation(summary = "Return all Statistics documents stored in the database")
    public List<Statistics> findAll() {
        return statisticsManager.getAllStatistics();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Returns the Statistics with the deviceId passed as parameter, in case it doesn't exist it will throw an execption")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "Statistics not found")})
    public Statistics findById(@Parameter(description = "Id of the device") @PathVariable String id) {
        return statisticsManager.findById(id);
    }

    @GetMapping("/processedat")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "400", description = "Bad use of params")})
    @Operation(summary = "If both params are present, it will return the statistics processed between the start and end date, if only the start param is present, it will return statistics processed after said date, if only the end is present it will return statistics processed before said date")
    public List<Statistics> findByProcessedAt(@Parameter(description = "Must be in yyyy-MM-dd'T'HH:mm:ss format") @RequestParam(required = false) String startDate, @Parameter(description = "Must be in yyyy-MM-dd'T'HH:mm:ss format") @RequestParam(required = false) String endDate) {
        return statisticsManager.findByDate(startDate, endDate);
    }

    @GetMapping("/messagesprocessed")
    @Operation(summary = "Return statistics that processed the same number of messages as the passed one")
    public List<Statistics> findByMessagesProcessed(@RequestParam int messagesProcessed) {
        return statisticsManager.findByMessagesProcessed(messagesProcessed);
    }
}
