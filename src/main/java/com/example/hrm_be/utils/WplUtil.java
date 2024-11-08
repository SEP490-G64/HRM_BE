package com.example.hrm_be.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class WplUtil {
  private static int counter = 1; // to generate the sequential number

  public static String generateNoteCode(LocalDateTime theDate, String type) {
    // Format the date to YYMMDD
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyMMdd");
    String datePart = dateFormat.format(theDate);

    // Format the time to HHMMSS
    DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HHmmss");
    String timePart = timeFormat.format(theDate);

    // Generate the sequential part (3 digits, padded with zeros)
    String sequentialPart = String.format("%03d", counter);

    // Construct the code
    String code = type + "." + datePart + "." + timePart + "." + sequentialPart;
    // Increment the counter
    counter++;

    return code;
  }
}
