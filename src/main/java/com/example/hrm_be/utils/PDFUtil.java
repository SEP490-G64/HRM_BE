package com.example.hrm_be.utils;

import com.example.hrm_be.models.dtos.*;
import com.example.hrm_be.models.responses.InboundDetail;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

public class PDFUtil {


  public static ByteArrayOutputStream createReceiptPdf(InboundDetail inbound)
      throws DocumentException, IOException {
    LocalDateTime dateNow = LocalDateTime.now(); // Initializes with the current date and time

    // Create a new PDF document
    com.itextpdf.text.Document document = new com.itextpdf.text.Document();
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    try {
      // Initialize PDF writer
      PdfWriter.getInstance(document, out);
      document.open();

      // Load font file from resources
      String fontPath =
          Objects.requireNonNull(PDFUtil.class.getResource("/fonts/Arial.ttf")).getPath();

      // Create fonts
      Font fontTitle = createFontFromPath(fontPath, 18, Font.BOLD, BaseColor.BLACK);
      Font fontSubTitle = createFontFromPath(fontPath, 12, Font.NORMAL, BaseColor.BLACK);
      Font fontTableHeader = createFontFromPath(fontPath, 12, Font.BOLD, BaseColor.BLACK);
      Font fontFooter = createFontFromPath(fontPath, 12, Font.NORMAL, BaseColor.BLACK);

      // Add company information table to the document
      document.add(createCompanyInfoTable(inbound, fontSubTitle, fontTableHeader));
      // Create a borderless empty table to add space
      PdfPTable emptyTable = new PdfPTable(1); // Table with 1 column
      emptyTable.setWidthPercentage(100); // Set table width to 100%
      // Create an empty cell with a space character to ensure visibility
      PdfPCell emptyCell = new PdfPCell(new Phrase(" ")); // Empty cell
      emptyCell.setBorder(PdfPCell.NO_BORDER); // No border for the cell
      emptyCell.setMinimumHeight(20f); // Set a minimum height for spacing (adjust as needed)
      // Add the empty cell to the table
      emptyTable.addCell(emptyCell);
      // Add the empty table to the document
      document.add(emptyTable); // Add the empty table to the document

      // Add title
      PdfPTable titleTable = new PdfPTable(2);
      titleTable.setWidthPercentage(100);
      titleTable.setWidths(new float[] {10, 2});

      // Add title "PHIẾU NHẬP KHO"
      PdfPCell titleCell = new PdfPCell(getCenteredParagraph("PHIẾU NHẬP KHO", fontTitle));
      titleCell.setBorder(PdfPCell.NO_BORDER); // Remove border
      titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
      titleTable.addCell(titleCell);

      // Add an empty cell for the right column
      PdfPCell cell = new PdfPCell(new Phrase(""));
      cell.setBorder(PdfPCell.NO_BORDER);
      titleTable.addCell(cell);

      // Add date row
      PdfPCell dateCell =
          new PdfPCell(
              //getCenteredParagraph(formatInboundDate(inbound.getInboundDate()), fontTableHeader));
              getCenteredParagraph(formatInboundDate(dateNow), fontTableHeader));
      dateCell.setBorder(PdfPCell.NO_BORDER); // Remove border
      dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
      titleTable.addCell(dateCell);

//      // Add debit information on the right
//      PdfPCell debitCell = new PdfPCell(new Phrase("Nợ: 1561", fontSubTitle));
//      debitCell.setBorder(PdfPCell.NO_BORDER); // Remove border
//      debitCell.setHorizontalAlignment(Element.ALIGN_LEFT); // Align left
//      titleTable.addCell(debitCell);

      // Add number row
      PdfPCell numberCell = new PdfPCell(getCenteredParagraph("Số: " + inbound.getId(), fontSubTitle));
      numberCell.setBorder(PdfPCell.NO_BORDER); // Remove border
      numberCell.setHorizontalAlignment(Element.ALIGN_CENTER);
      titleTable.addCell(numberCell);

//      // Add credit information on the right
//      PdfPCell creditCell = new PdfPCell(new Phrase("Có: 331", fontSubTitle));
//      creditCell.setBorder(PdfPCell.NO_BORDER); // Remove border
//      creditCell.setHorizontalAlignment(Element.ALIGN_LEFT); // Align left
//      titleTable.addCell(creditCell);

      // Add title table to the document
      document.add(titleTable);
      document.add(Chunk.NEWLINE);

      // Supplier and invoice details
      document.add(
          new Paragraph(
              "- Họ và tên người giao: " + inbound.getSupplier().getSupplierName(), fontSubTitle));
      document.add(
          new Paragraph(
              "- Theo hóa đơn số "
                  + inbound.getInboundCode()
                  + " "
                 // + formatInboundDate(inbound.getInboundDate())
                  + formatInboundDate(dateNow)
                  + " của "
                  + inbound.getSupplier().getSupplierName(),
              fontSubTitle));

      // Add location
      PdfPTable locationTable = new PdfPTable(2);
      locationTable.setWidthPercentage(100);
      locationTable.setWidths(new float[] {5, 5});

      PdfPCell locationCell =
          new PdfPCell(
              new Phrase("- Nhập tại kho: " + inbound.getToBranch().getBranchName(), fontSubTitle));
      locationCell.setBorder(PdfPCell.NO_BORDER);
      locationTable.addCell(locationCell);

      PdfPCell addressCell =
          new PdfPCell(
              new Phrase("Địa điểm: " + inbound.getToBranch().getLocation(), fontSubTitle));
      addressCell.setBorder(PdfPCell.NO_BORDER);
      locationTable.addCell(addressCell);

      document.add(locationTable);
      document.add(Chunk.NEWLINE);

      // Product details table
      PdfPTable productTable = createProductTable(inbound, fontTableHeader, fontSubTitle);
      document.add(productTable);
      document.add(Chunk.NEWLINE);

      // Initialize the total amount for the invoice
      BigDecimal total = BigDecimal.ZERO;

      // Calculate total for all product batch details
      for (InboundProductDetailDTO detail : inbound.getProductBatchDetails()) {
        // Initialize the total value for the current detail
        BigDecimal totalDetailAmount = BigDecimal.ZERO;

        // Check if there are any batches for the current detail
        if (detail.getBatches() != null && !detail.getBatches().isEmpty()) {
          // Loop through each batch to calculate the total value
          for (Batch batch : detail.getBatches()) {
            // Ensure the batch quantity and price are valid
            if (batch.getInboundBatchQuantity() > 0 && batch.getInboundPrice() != null) {
              // Calculate the total price for the current batch
              BigDecimal batchTotalPrice = batch.getInboundPrice()
                      .multiply(BigDecimal.valueOf(batch.getInboundBatchQuantity()));
              // Add the batch total price to the total detail amount
              totalDetailAmount = totalDetailAmount.add(batchTotalPrice);
            }
          }
        } else {
          // If no batches, calculate the total based on the detail price and quantity received
          if (detail.getPrice() != null && detail.getReceiveQuantity() > 0) {
            totalDetailAmount = detail.getPrice()
                    .multiply(BigDecimal.valueOf(detail.getReceiveQuantity()));
          }
        }
        // Add the total detail amount to the overall total
        total = total.add(totalDetailAmount);
      }
     // Convert the total amount to words for display or documentation
      String amountInWords;
      if (total.compareTo(BigDecimal.ZERO) > 0) {
        amountInWords = NumberToWordsConverter.convert(total);
        // Use or display amountInWords as needed
      } else {
        // Handle the case when the total is zero
        amountInWords ="";
      }
      //String amountInWords = "";

      Paragraph totalAmountParagraph = new Paragraph();
      totalAmountParagraph.add(new Phrase("- Tổng số tiền (Viết bằng chữ): ", fontFooter));
      totalAmountParagraph.add(new Phrase(amountInWords, fontTableHeader));
      document.add(totalAmountParagraph);

      document.add(new Paragraph("- Số chứng từ gốc kèm theo:", fontFooter));
      document.add(Chunk.NEWLINE);

      // Footer table
      document.add(createFooterTable(inbound, fontFooter, fontTableHeader));

    } finally {
      document.close();
    }

    return out;
  }

  // Create a company information table
  private static PdfPTable createCompanyInfoTable(
      InboundDetail inbound, Font fontSubTitle, Font fontHeader) throws DocumentException {
    // Create a new table with 2 columns
    PdfPTable infoTable = new PdfPTable(2);
    infoTable.setWidthPercentage(100);
    infoTable.setWidths(new int[] {4, 2}); // Set column ratios

    // Create a cell for company information (left side)
    PdfPCell companyInfo = new PdfPCell();
    companyInfo.addElement(new Paragraph("Hệ Thống Nhà Thuốc Long Tâm", fontSubTitle));
    companyInfo.addElement(new Paragraph(inbound.getToBranch().getBranchName(), fontSubTitle));
    companyInfo.addElement(new Paragraph(inbound.getToBranch().getLocation(), fontSubTitle));
    companyInfo.setBorder(Rectangle.NO_BORDER); // Remove cell border
    infoTable.addCell(companyInfo); // Add company info cell to the table

    // Create a cell to hold receipt information (right side)
    PdfPCell receiptInfo = new PdfPCell();

    // Add text paragraphs to the receipt info cell
    receiptInfo.addElement(new Paragraph("Mẫu số: 01 - VT", fontHeader));
    receiptInfo.addElement(
        new Paragraph(
            "(Ban hành theo Thông tư số 200/2014/TT-BTC Ngày 22/12/2014 của Bộ Tài chính)",
            fontSubTitle));

    // Center align the text horizontally
    receiptInfo.setHorizontalAlignment(Element.ALIGN_CENTER);
    // Center align the text vertically
    receiptInfo.setVerticalAlignment(Element.ALIGN_MIDDLE);
    // Remove the cell border
    receiptInfo.setBorder(Rectangle.NO_BORDER);
    // Set a minimum height for the cell to ensure content is vertically centered
    receiptInfo.setMinimumHeight(50f); // You can adjust this value as needed

    // Add the receipt info cell to the company information table
    infoTable.addCell(receiptInfo);

    return infoTable; // Return the completed table
  }

  // Create a product detail table
  private static PdfPTable createProductTable(
      InboundDetail inbound, Font fontTableHeader, Font fontSubTitle) throws DocumentException {
    // Create a table with 8 columns to fit the structure shown
    BigDecimal total = BigDecimal.ZERO;
    PdfPTable table = new PdfPTable(8);
    table.setWidthPercentage(100);
    table.setWidths(new int[] {1, 4, 2, 1, 1, 1, 1, 2}); // Adjust column ratios

    // Main header row
    PdfPCell cell = new PdfPCell(new Phrase("STT", fontTableHeader));
    cell.setRowspan(2);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    table.addCell(cell);

    cell =
        new PdfPCell(
            new Phrase(
                "Tên, nhãn hiệu, quy cách, \nphẩm chất vật tư, dụng cụ \nsản phẩm, hàng hóa",
                fontTableHeader));
    cell.setRowspan(2);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    table.addCell(cell);

    cell = new PdfPCell(new Phrase("Mã số", fontTableHeader));
    cell.setRowspan(2);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    table.addCell(cell);

    cell = new PdfPCell(new Phrase("Đơn vị tính", fontTableHeader));
    cell.setRowspan(2);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    table.addCell(cell);

    cell = new PdfPCell(new Phrase("Số lượng", fontTableHeader));
    cell.setColspan(2);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    table.addCell(cell);

    cell = new PdfPCell(new Phrase("Đơn giá", fontTableHeader));
    cell.setRowspan(2);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    table.addCell(cell);

    cell = new PdfPCell(new Phrase("Thành tiền", fontTableHeader));
    cell.setRowspan(2);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    table.addCell(cell);

    // Sub-header row for "Số lượng" (Quantity)
    cell = new PdfPCell(new Phrase("Theo chứng từ", fontTableHeader));
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    table.addCell(cell);

    cell = new PdfPCell(new Phrase("Thực nhập", fontTableHeader));
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    table.addCell(cell);

    table.addCell(new PdfPCell(new Phrase("A", fontTableHeader))); // STT (serial number)
    table.addCell(new PdfPCell(new Phrase("B", fontTableHeader))); // Product name
    table.addCell(new PdfPCell(new Phrase("C", fontTableHeader))); // Registration code
    table.addCell(new PdfPCell(new Phrase("D", fontTableHeader))); // Unit of measurement
    table.addCell(
        new PdfPCell(
            new Phrase(String.valueOf(1), fontTableHeader))); // Quantity according to the document
    table.addCell(
        new PdfPCell(new Phrase(String.valueOf(2), fontTableHeader))); // Actual quantity received
    table.addCell(new PdfPCell(new Phrase(String.valueOf(3), fontTableHeader))); // Unit price
    table.addCell(new PdfPCell(new Phrase(String.valueOf(4), fontTableHeader))); // Total amount
    // Product data rows from InboundDetails
    int index = 1;
    for (InboundProductDetailDTO detail : inbound.getProductBatchDetails()) {
      table.addCell(
          new PdfPCell(new Phrase(String.valueOf(index++), fontSubTitle))); // STT (serial number)
      table.addCell(
          new PdfPCell(new Phrase(detail.getProductName(), fontSubTitle))); // Product name
      table.addCell(
          new PdfPCell(new Phrase(detail.getRegistrationCode(), fontSubTitle))); // Registration code
      table.addCell(
          new PdfPCell(
              new Phrase(detail.getBaseUnit().getUnitName(), fontSubTitle))); // Unit of measurement
      table.addCell(
          new PdfPCell(
              new Phrase(
                  String.valueOf(detail.getRequestQuantity()),
                  fontSubTitle))); // Quantity according to the document
      table.addCell(
          new PdfPCell(
              new Phrase(
                  String.valueOf(detail.getReceiveQuantity()),
                  fontSubTitle))); // Actual quantity received

      // Initialize the total value for the current detail
      BigDecimal totalDetailAmount = BigDecimal.ZERO;
// Initialize the unit price for the current detail (if needed for reference)
      BigDecimal unitPrice = BigDecimal.ZERO;

// Check if there are any batches
      if (detail.getBatches() != null && !detail.getBatches().isEmpty()) {
        // Loop through each batch to calculate the total value
        for (Batch batch : detail.getBatches()) {
          // Set the unit price only for the first batch encountered (optional)
          if (unitPrice.compareTo(BigDecimal.ZERO) == 0) {
            unitPrice = batch.getInboundPrice();
          }

          // Calculate the total price for the current batch
          BigDecimal batchTotalPrice =
                  batch.getInboundPrice().multiply(BigDecimal.valueOf(batch.getInboundBatchQuantity()));

          // Add the batch total price to the total detail amount
          totalDetailAmount = totalDetailAmount.add(batchTotalPrice);
        }
      } else {
          totalDetailAmount = detail.getPrice().multiply(BigDecimal.valueOf(detail.getReceiveQuantity()));
          unitPrice = detail.getPrice();
      }
      total = total.add(totalDetailAmount);

      table.addCell(
          new PdfPCell(new Phrase(String.valueOf(unitPrice), fontSubTitle))); // Unit price
          //new PdfPCell(new Phrase())); // Unit price
      table.addCell(
          new PdfPCell(new Phrase(totalDetailAmount.toString(), fontSubTitle))); // Total amount
          //new PdfPCell(new Phrase())); // Total amount
    }

    // "Total" row at the bottom of the table
    cell = new PdfPCell(new Phrase("Cộng", fontTableHeader));
    cell.setColspan(4);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    table.addCell(cell);

    table.addCell(new PdfPCell(new Phrase("", fontSubTitle))); // Empty cell for "Theo chứng từ"
    table.addCell(new PdfPCell(new Phrase("", fontSubTitle))); // Empty cell for "Thực nhập"
    table.addCell(new PdfPCell(new Phrase("", fontSubTitle))); // Empty cell for "Đơn giá"
    PdfPCell pdfPCell = table.addCell(
            new PdfPCell(
                    new Phrase(String.valueOf(total), fontSubTitle))); // Total amount
                    //new Phrase()));// Total amount

    return table; // Return the completed table
  }

  // Create the footer table
  private static PdfPTable createFooterTable(
      InboundDetail inbound, Font fontFooter, Font fontTableHeader) throws DocumentException {
    LocalDateTime dateNow = LocalDateTime.now(); // Initializes with the current date and time
    PdfPTable footerTable = new PdfPTable(4);
    footerTable.setWidthPercentage(100);
    footerTable.setWidths(new float[] {1, 1, 1, 1.6f}); // Column 4 is wider than the others

    // Remove borders for all cells
    footerTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

    // Column 1: Creator of the document
    footerTable.addCell(createCenteredCell("Người lập phiếu", fontTableHeader));
    footerTable.addCell(createCenteredCell("Người giao hàng", fontTableHeader));
    footerTable.addCell(createCenteredCell("Thủ kho", fontTableHeader));

    // Column 4: Date
    footerTable.addCell(
        //createCenteredCell(formatInboundDate(inbound.getInboundDate()), fontFooter));
        createCenteredCell(formatInboundDate(dateNow), fontFooter));

    // Add the second row
    footerTable.addCell(createCenteredCell("(Ký, họ tên)", fontFooter));
    footerTable.addCell(createCenteredCell("(Ký, họ tên)", fontFooter));
    footerTable.addCell(createCenteredCell("(Ký, họ tên)", fontFooter));

    // Column 4: Chief Accountant
    footerTable.addCell(createCenteredCell("Kế toán trưởng", fontTableHeader));

    // Third row (empty cells)
    footerTable.addCell(createCenteredCell("", fontFooter));
    footerTable.addCell(createCenteredCell("", fontFooter));
    footerTable.addCell(createCenteredCell("", fontFooter));

    // Column 4: (Or department in need of import)
    footerTable.addCell(createCenteredCell("(Hoặc bộ phận có nhu cầu nhập)", fontTableHeader));

    // Fourth row (empty cells)
    footerTable.addCell(createCenteredCell("", fontFooter));
    footerTable.addCell(createCenteredCell("", fontFooter));
    footerTable.addCell(createCenteredCell("", fontFooter));

    // Column 4: (Signature, full name)
    footerTable.addCell(createCenteredCell("(Ký, họ tên)", fontFooter));

    return footerTable; // Return the completed footer table
  }

  // Create a cell with centered content
  private static PdfPCell createCenteredCell(String text, Font font) {
    PdfPCell cell = new PdfPCell(new Phrase(text, font));
    cell.setBorder(PdfPCell.NO_BORDER); // Remove cell border
    cell.setHorizontalAlignment(Element.ALIGN_CENTER); // Center the content
    return cell; // Return the centered cell
  }

  // Method to create a font from a font file with UTF-8 encoding
  private static Font createFontFromPath(
      String fontPath, float fontSize, int fontStyle, BaseColor color)
      throws DocumentException, IOException {
    BaseFont baseFont =
        BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED); // Load font
    return new Font(baseFont, fontSize, fontStyle, color); // Return the font
  }

  // Method to create a centered paragraph
  private static Paragraph getCenteredParagraph(String text, Font font) {
    Paragraph paragraph = new Paragraph(text, font);
    paragraph.setAlignment(Element.ALIGN_CENTER); // Center the paragraph text
    return paragraph; // Return the centered paragraph
  }

  // Method to create a font from a font file with UTF-8 encoding (duplicate method removed)
  private static Font createFontFromPath(String fontPath, int size, int style, BaseColor color)
      throws DocumentException, IOException {
    // Load font from path with UTF-8 encoding
    BaseFont baseFont =
        BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED); // Load font
    Font font = new Font(baseFont, size, style, color); // Create font instance
    return font; // Return the created font
  }

  public static String formatInboundDate(LocalDateTime inboundDate) {
    if (inboundDate == null) {
      return "Ngày không xác định";
    }

    // Định dạng ngày
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Ngày' dd 'tháng' MM 'năm' yyyy");
    return inboundDate.format(formatter);
  }

}
