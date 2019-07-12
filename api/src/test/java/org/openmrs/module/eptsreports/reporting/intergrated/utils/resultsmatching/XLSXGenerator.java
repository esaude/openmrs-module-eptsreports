package org.openmrs.module.eptsreports.reporting.intergrated.utils.resultsmatching;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openmrs.Location;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class XLSXGenerator {
  private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

  public static void creatResultsMatchingResultsXlsx(
      List<RunResult> results, String eptsReportsResultsMatchingOutPut) throws IOException {
    Workbook workbook = new XSSFWorkbook();
    for (RunResult result : results) {
      Sheet sheet = workbook.createSheet(result.getCurrentReport());
      sheet.autoSizeColumn(14);
      Font headerFont = workbook.createFont();
      headerFont.setBold(true);
      headerFont.setFontHeightInPoints((short) 14);

      CellStyle headerCellStyle = workbook.createCellStyle();
      headerCellStyle.setFont(headerFont);

      // create header row for params, skip the first row
      Row paramsHeader = sheet.createRow(1);
      Cell paramsHeaderCell0 = paramsHeader.createCell(0);
      paramsHeaderCell0.setCellStyle(headerCellStyle);
      paramsHeaderCell0.setCellValue("Current Report");
      Cell paramsHeaderCell1 = paramsHeader.createCell(1);
      paramsHeaderCell1.setCellStyle(headerCellStyle);
      paramsHeaderCell1.setCellValue("Master Report");
      Cell paramsHeaderCell2 = paramsHeader.createCell(2);
      paramsHeaderCell2.setCellStyle(headerCellStyle);
      paramsHeaderCell2.setCellValue("Current Report EvaluationTime (ms)");
      Cell paramsHeaderCell3 = paramsHeader.createCell(3);
      paramsHeaderCell3.setCellStyle(headerCellStyle);
      paramsHeaderCell3.setCellValue("Master Report EvaluationTime (ms)");
      Cell paramsHeaderCell4 = paramsHeader.createCell(4);
      paramsHeaderCell4.setCellStyle(headerCellStyle);
      paramsHeaderCell4.setCellValue("Start Date");
      Cell paramsHeaderCell5 = paramsHeader.createCell(5);
      paramsHeaderCell5.setCellStyle(headerCellStyle);
      paramsHeaderCell5.setCellValue("End Date");
      Cell paramsHeaderCell6 = paramsHeader.createCell(6);
      paramsHeaderCell6.setCellStyle(headerCellStyle);
      paramsHeaderCell6.setCellValue("Location");

      // create params value row
      Row paramsValue = sheet.createRow(2);
      Cell paramsValueCell0 = paramsValue.createCell(0);
      paramsValueCell0.setCellValue(result.getCurrentReport());
      Cell paramsValueCell1 = paramsValue.createCell(1);
      paramsValueCell1.setCellValue(result.getMasterReport());
      Cell paramsValueCell2 = paramsValue.createCell(2);
      paramsValueCell2.setCellValue(result.getCurrentReportEvaluationTime());
      Cell paramsValueCell3 = paramsValue.createCell(3);
      paramsValueCell3.setCellValue(result.getMasterReportEvaluationTime());
      Cell paramsValueCell4 = paramsValue.createCell(4);
      paramsValueCell4.setCellValue(
          sdf.format((Date) result.getParameterValues().get("startDate")));
      Cell paramsValueCell5 = paramsValue.createCell(5);
      paramsValueCell5.setCellValue(sdf.format((Date) result.getParameterValues().get("endDate")));
      Cell paramsValueCell6 = paramsValue.createCell(6);
      Location location = (Location) result.getParameterValues().get("location");
      paramsValueCell6.setCellValue(location.getName() + "#" + location.getLocationId());

      // create indicator header, current data, master data and matching rows skipping row 3 and 4
      Row indicatorHeader = sheet.createRow(5);
      Row currentData = sheet.createRow(6);
      Row masterData = sheet.createRow(7);
      Row currentOffSetData = sheet.createRow(8);
      Row masterOffSetData = sheet.createRow(9);
      for (int i = 0; i < result.getMatches().size(); i++) {
        Match match = result.getMatches().get(i);
        Cell indicatorHeaderValue = indicatorHeader.createCell(i);
        indicatorHeaderValue.setCellValue(match.getMapping());
        Cell currentDataValue = currentData.createCell(i);
        currentDataValue.setCellValue(match.getCurrentValue());
        Cell masterDataValue = masterData.createCell(i);
        masterDataValue.setCellValue(match.getMasterValue());
        Integer currentOffSet = match.getCurrentOffSet();
        Cell currentOffSetDataValue = currentOffSetData.createCell(i);
        currentOffSetDataValue.setCellValue(currentOffSet);
        // currentOffSet or miss-matches set to cell as a comment
        if (currentOffSet > 0) {
          currentOffSetDataValue.setCellComment(
              generateCellComment(
                  sheet,
                  workbook.getCreationHelper(),
                  currentOffSetData,
                  currentOffSetDataValue,
                  match.getCurrentOffSetPatientIds().toString()));
        }
        Integer masterOffSet = match.getMasterOffSet();
        Cell masterOffSetDataValue = masterOffSetData.createCell(i);
        masterOffSetDataValue.setCellValue(masterOffSet);
        // masterOffSet or miss-matches set to cell as a comment
        if (masterOffSet > 0) {
          masterOffSetDataValue.setCellComment(
              generateCellComment(
                  sheet,
                  workbook.getCreationHelper(),
                  masterOffSetData,
                  masterOffSetDataValue,
                  match.getMasterOffSetPatientIds().toString()));
        }
      }
    }

    // Write the output to a results file
    FileOutputStream fileOut = new FileOutputStream(eptsReportsResultsMatchingOutPut);
    workbook.write(fileOut);
    fileOut.close();
  }

  private static Comment generateCellComment(
      Sheet sheet, CreationHelper factory, Row row, Cell cell, String message) {
    Drawing drawing = ((Sheet) sheet).createDrawingPatriarch();

    // When the comment box is visible, have it show in a 1x3 space
    ClientAnchor anchor = factory.createClientAnchor();
    anchor.setCol1(cell.getColumnIndex());
    anchor.setCol2(cell.getColumnIndex() + 1);
    anchor.setRow1(row.getRowNum());
    anchor.setRow2(row.getRowNum() + 3);

    // Create the comment and set the text+author
    Comment comment = drawing.createCellComment(anchor);
    comment.setString(factory.createRichTextString(message));
    comment.setAuthor(System.getProperty("user.name"));
    return comment;
  }
}
