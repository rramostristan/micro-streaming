package com.test.microstreaming.managers.statistics;

import com.test.microstreaming.exceptions.CustomException;
import com.test.microstreaming.exceptions.ResourceNotFoundException;
import com.test.microstreaming.models.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class StatisticsManager implements IStatisticsManager {

    protected static Logger logger = LoggerFactory.getLogger(StatisticsManager.class);
    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    protected String dateFormatRegex =  "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}";

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
    public Statistics findById(String id) throws ResourceNotFoundException {
        Optional<Statistics> statistics = statisticsRepository.findById(id);
        if (statistics.isEmpty()) {
            logger.error("Tried to found statitis with id {} not present", id);
            throw new ResourceNotFoundException("Statistics not found with id: " + id);
        }
        return statistics.get();
    }

    @Override
    public boolean existsById(String id) {
        return statisticsRepository.existsById(id);
    }

    @Override
    public List<Statistics> findByDate(String startDate, String endDate) {
        checkDates(startDate, endDate);
        if (!ObjectUtils.isEmpty(startDate) && !ObjectUtils.isEmpty(endDate)) {
            Date start = convertToDate(startDate);
            Date end = convertToDate(endDate);
            if (start.after(end)) {
                throw new CustomException("startDate must be previous to endDate");
            }
            return statisticsRepository.findByProcessedAtBetween(start, end);
        } else if (!ObjectUtils.isEmpty(startDate)) {
            return statisticsRepository.findByProcessedAtGreaterThan(convertToDate(startDate));
        } else {
            return statisticsRepository.findByProcessedAtLessThan(convertToDate(endDate));
        }
    }

    protected void checkDates(String startDate, String endDate) {
        if (ObjectUtils.isEmpty(startDate) && ObjectUtils.isEmpty(endDate)) {
            throw new CustomException("At least one parameter must be present", HttpStatus.BAD_REQUEST);
        }
        if (!ObjectUtils.isEmpty(startDate) && !Pattern.matches(dateFormatRegex, startDate)) {
            throw new CustomException("Start Date must be in yyyy-MM-dd'T'HH:mm:ss format", HttpStatus.BAD_REQUEST);
        }
        if (!ObjectUtils.isEmpty(endDate) && !Pattern.matches(dateFormatRegex, endDate)) {
            throw new CustomException("End Date must be in yyyy-MM-dd'T'HH:mm:ss format", HttpStatus.BAD_REQUEST);
        }
    }

    protected Date convertToDate(String date) {
        return Date.from(LocalDateTime.parse(date, formatter).atZone(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public List<Statistics> findByMessagesProcessed(int messagesProcessed) {
        return statisticsRepository.findByMessagesProcessed(messagesProcessed);
    }
}
