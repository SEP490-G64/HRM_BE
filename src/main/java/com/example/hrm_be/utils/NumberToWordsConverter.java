package com.example.hrm_be.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class NumberToWordsConverter {
  private static final String[] tensNames = {
          "", "mười", "hai mươi", "ba mươi", "bốn mươi",
          "năm mươi", "sáu mươi", "bảy mươi", "tám mươi", "chín mươi"
  };

  private static final String[] numNames = {
          "", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"
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

    // Limit the integer part to a maximum of 999 triệu
    BigDecimal integerPart = number.setScale(0, BigDecimal.ROUND_DOWN);
    if (integerPart.compareTo(new BigDecimal("999999999")) > 0) {
      throw new IllegalArgumentException("Số quá lớn để chuyển đổi");
    }

    // Separate integer and fractional parts
    BigDecimal fractionalPart = number.subtract(integerPart);

    // Convert integer part to words
    String integerInWords = convertIntegerPart(integerPart);

    // Convert fractional part to words if it exists
    String fractionalInWords = convertFractionalPart(fractionalPart);

    return (integerInWords + fractionalInWords).trim();
  }

  private static String convertIntegerPart(BigDecimal integerPart) {
    String snumber = String.valueOf(integerPart);

    // Formatting the integer part to a 9-digit string
    String mask = "000000000";
    DecimalFormat df = new DecimalFormat(mask);
    snumber = df.format(integerPart);

    int millions = Integer.parseInt(snumber.substring(0, 3));
    int hundredThousands = Integer.parseInt(snumber.substring(3, 6));
    int thousands = Integer.parseInt(snumber.substring(6, 9));

    String tradMillions = (millions == 0) ? "" : convertLessThanOneThousand(millions) + " triệu ";
    String tradHundredThousands =
            (hundredThousands == 0) ? "" :
                    (hundredThousands == 1 ? "một nghìn " : convertLessThanOneThousand(hundredThousands) + " nghìn ");
    String tradThousand = convertLessThanOneThousand(thousands);

    return (tradMillions + tradHundredThousands + tradThousand).replaceAll("\\s+", " ").trim();
  }

  private static String convertFractionalPart(BigDecimal fractionalPart) {
    if (fractionalPart.compareTo(BigDecimal.ZERO) == 0) {
      return ""; // No fractional part
    }

    StringBuilder fractionalWords = new StringBuilder(" phẩy");
    String fractionalString = fractionalPart.toPlainString().split("\\.")[1]; // Get the decimal part

    for (char digit : fractionalString.toCharArray()) {
      int numericValue = Character.getNumericValue(digit);
      // Check if the digit is valid for numNames
      if (numericValue >= 0 && numericValue < numNames.length) {
        fractionalWords.append(" ").append(numNames[numericValue]);
      } else {
        throw new IllegalArgumentException("Invalid digit in fractional part: " + digit);
      }
    }

    return fractionalWords.toString();
  }

  private static String convertLessThanOneThousand(int number) {
    String current;

    if (number % 100 < 10) { // Đối với số từ 0-9
      current = numNames[number % 10];
      number /= 10;
    } else { // Đối với số từ 10-99
      current = numNames[number % 10]; // Lấy chữ số đơn vị
      number /= 10;

      current = tensNames[number % 10] + " " + current; // Lấy chữ số chục
      number /= 10;
    }

    // Xử lý số trăm
    if (number > 0) {
      // Nếu không có phần chục và đơn vị thì không cần khoảng trắng sau "trăm"
      return current.isEmpty() ? numNames[number] + " trăm" : numNames[number] + " trăm " + current;
    }

    return current.trim(); // Trả về kết quả
  }
}

