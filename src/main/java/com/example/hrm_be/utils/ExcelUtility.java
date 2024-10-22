package com.example.hrm_be.utils;

import com.example.hrm_be.models.dtos.User;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ExcelUtility {
    public static final String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static final String SHEET = "Sheet";
    // Check if the file has Excel format
    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    // Generic method for importing data from Excel with row-level error handling
    public static <T> List<String> importFromExcelWithErrors(
            MultipartFile file,
            Function<Row, T> rowMapper,
            BiConsumer<T, List<String>> validator,
            int headerRowIndex) {

        List<String> errors = new ArrayList<>();

        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = headerRowIndex + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    try {
                        T entity = rowMapper.apply(row);

                        if (entity != null) {
                            List<String> rowErrors = new ArrayList<>();
                            validator.accept(entity, rowErrors); // Validate the entity and pass any errors

                            if (!rowErrors.isEmpty()) {
                                errors.add("Row " + (rowIndex + 1) + ": " + String.join(", ", rowErrors));
                            }
                        }
                    } catch (Exception e) {
                        errors.add("Row " + (rowIndex + 1) + ": " + e.getMessage());
                    }
                }
            }

            workbook.close();
        } catch (IOException e) {
            errors.add("Failed to parse Excel file: " + e.getMessage());
        }

        return errors;
    }

    // Common export method with flexible row mapping
    public static <T> ByteArrayInputStream exportToExcelWithErrors(
            List<T> dataList,
            String[] headers,
            Function<T, List<String>> rowMapper) throws IOException {

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(SHEET);

            // Create the header row
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < headers.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(headers[col]);
            }

            // Populate the rows with data
            int rowIdx = 1;
            for (T data : dataList) {
                Row row = sheet.createRow(rowIdx++);

                // Map the data object (T) to a list of strings (row cells)
                List<String> cellValues = rowMapper.apply(data);

                for (int col = 0; col < cellValues.size(); col++) {
                    Cell cell = row.createCell(col);
                    cell.setCellValue(cellValues.get(col) != null ? cellValues.get(col) : "");
                }
            }

            // Write the content to the output stream
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to export data to Excel file: " + e.getMessage());
        }
    }

}
