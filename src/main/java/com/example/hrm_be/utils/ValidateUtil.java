package com.example.hrm_be.utils;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ValidateUtil {
  public static boolean validateGetByPaging(
      int pageNo, int pageSize, String sortBy, Class<?> targetClass) {
    return pageNo < 0
        || pageSize < 1
        || sortBy == null
        || Arrays.stream(targetClass.getDeclaredFields())
            .map(Field::getName)
            .noneMatch(fieldName -> fieldName.equals(sortBy));
  }
}
