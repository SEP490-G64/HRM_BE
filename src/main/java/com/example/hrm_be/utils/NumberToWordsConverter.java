package com.example.hrm_be.utils;

import java.math.BigDecimal;

public class NumberToWordsConverter {

  private static final String[] numNames = {
    "", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"
  };

  private static final String[] tensNames = {
    "", "mười", "hai mươi", "ba mươi", "bốn mươi",
    "năm mươi", "sáu mươi", "bảy mươi", "tám mươi", "chín mươi"
  };

  private static final String[] bigUnits = {
    "", "nghìn", "triệu", "tỷ", "nghìn tỷ", "triệu tỷ", "tỷ tỷ"
  };

  public static String convert(BigDecimal number) {
    if (number == null) {
      return "không";
    }

    // Handle negative numbers
    if (number.compareTo(BigDecimal.ZERO) < 0) {
      return "âm " + convert(number.abs());
    }

    // Handle zero
    if (number.compareTo(BigDecimal.ZERO) == 0) {
      return "không";
    }

    BigDecimal integerPart = number.setScale(0, BigDecimal.ROUND_DOWN);
    BigDecimal fractionalPart = number.subtract(integerPart);

    // Convert integer part
    String integerInWords = convertIntegerPart(integerPart);

    // Convert fractional part
    String fractionalInWords = convertFractionalPart(fractionalPart);

    return (integerInWords + fractionalInWords).trim();
  }

  private static String convertIntegerPart(BigDecimal integerPart) {
    String snumber = integerPart.toPlainString();

    // Split the number into groups of three digits
    StringBuilder words = new StringBuilder();
    int groupCount = 0;

    while (snumber.length() > 0) {
      int groupLength = Math.min(snumber.length(), 3);
      String group = snumber.substring(snumber.length() - groupLength);
      snumber = snumber.substring(0, snumber.length() - groupLength);

      int groupNumber = Integer.parseInt(group);
      if (groupNumber > 0) {
        if (groupCount >= bigUnits.length) {
          throw new IllegalArgumentException("Số quá lớn để xử lý với các đơn vị hiện tại.");
        }

        String groupInWords = convertLessThanOneThousand(groupNumber);
        words.insert(0, groupInWords + " " + bigUnits[groupCount] + " ");
      }
      groupCount++;
    }

    return words.toString().trim();
  }

  private static String convertFractionalPart(BigDecimal fractionalPart) {
    if (fractionalPart.compareTo(BigDecimal.ZERO) == 0) {
      return ""; // No fractional part
    }

    StringBuilder fractionalWords = new StringBuilder(" phẩy");
    String fractionalString =
        fractionalPart.toPlainString().split("\\.")[1]; // Get the decimal part

    for (char digit : fractionalString.toCharArray()) {
      int numericValue = Character.getNumericValue(digit);
      fractionalWords.append(" ").append(numNames[numericValue]);
    }

    return fractionalWords.toString();
  }

  private static String convertLessThanOneThousand(int number) {
    if (number < 0 || number > 999) {
      throw new IllegalArgumentException("Number must be between 0 and 999.");
    }

    String current = "";

    // Lấy hàng đơn vị và giảm số
    if (number % 10 != 0) { // Nếu có hàng đơn vị
      current = numNames[number % 10];
    }
    number /= 10; // Loại bỏ hàng đơn vị

    // Lấy hàng chục và giảm số
    if (number % 10 != 0) { // Nếu có hàng chục
      current = tensNames[number % 10] + (current.isEmpty() ? "" : " " + current);
    }
    number /= 10; // Loại bỏ hàng chục

    // Lấy hàng trăm
    if (number != 0) { // Nếu có hàng trăm
      current = numNames[number] + " trăm" + (current.isEmpty() ? "" : " " + current);
    }

    return current.trim();
  }
}
