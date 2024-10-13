package com.example.hrm_be.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class WplUtil {
  private static int counter = 1; // to generate the sequential number

  public static String generateInboundCode(Date theDate) {
    // Format the date to YYMMDD
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
    String datePart = dateFormat.format(theDate);

    // Format the time to HHMMSS
    SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
    String timePart = timeFormat.format(theDate);

    // Generate the sequential part (3 digits, padded with zeros)
    String sequentialPart = String.format("%03d", counter);

    // Construct the code
    String code = "IP." + datePart + "." + timePart + "." + sequentialPart;
    // Increment the counter
    counter++;

    return code;
  }


}
