package com.example.hrm_be.components;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
public class DateUtil {
    public Date addHours(int hours) {
        LocalDateTime now = LocalDateTime.now();

        // Add one hour to the current time
        LocalDateTime oneHourAhead = now.plusHours(hours);

        // Convert LocalDateTime to Date
        return Date.from(oneHourAhead.atZone(ZoneId.systemDefault()).toInstant());
    }
}
