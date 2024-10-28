package com.example.hrm_be.utils;

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

    // Xử lý số âm
    if (number.compareTo(BigDecimal.ZERO) < 0) {
      return "âm " + convert(number.abs());
    }

    // Xử lý số bằng 0
    if (number.compareTo(BigDecimal.ZERO) == 0) {
      return "không";
    }

    // Giới hạn số tối đa là 999 triệu
    if (number.compareTo(new BigDecimal("999999999")) > 0) {
      throw new IllegalArgumentException("Số quá lớn để chuyển đổi");
    }

    String snumber = String.valueOf(number);

    String mask = "000000000";
    DecimalFormat df = new DecimalFormat(mask);
    snumber = df.format(number);

    int millions = Integer.parseInt(snumber.substring(0, 3));
    int hundredThousands = Integer.parseInt(snumber.substring(3, 6));
    int thousands = Integer.parseInt(snumber.substring(6, 9));

    String tradMillions = (millions == 0) ? "" : convertLessThanOneThousand(millions) + " triệu ";
    String tradHundredThousands =
        (hundredThousands == 0)
            ? ""
            : (hundredThousands == 1
                ? "một nghìn "
                : convertLessThanOneThousand(hundredThousands) + " nghìn ");
    String tradThousand = convertLessThanOneThousand(thousands);

    return (tradMillions + tradHundredThousands + tradThousand).replaceAll("\\s+", " ").trim();
  }

  private static String convertLessThanOneThousand(int number) {
    String current;

    if (number % 100 < 10) {
      current = numNames[number % 10];
      number /= 10;
    } else {
      current = numNames[number % 10];
      number /= 10;

      current = tensNames[number % 10] + " " + current;
      number /= 10;
    }
    if (number == 0) return current;
    return numNames[number] + " trăm " + current;
  }
}
