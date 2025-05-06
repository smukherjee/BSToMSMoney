package com.sujoy.parser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import com.sujoy.common.ErrorHandler;
import com.sujoy.common.ExcelFileHandler;
import com.sujoy.common.MSMoney;
import com.sujoy.common.Util;

/**
 * Parser for Axis Bank Excel statements.
 * Implements the StatementParser interface.
 */
public class AxisStmtParserExcel implements StatementParser {

    @Override
    public void parse(String path, String filename, String ext) throws IOException, ParseException {
        ExcelFileHandler fileHandler = new ExcelFileHandler();
        try {
            fileHandler.openFile(path, filename, ext);

            Iterator<Row> iterator = fileHandler.getRowIterator();
            BufferedWriter writer = fileHandler.getWriter();

            boolean writeToFile;
            MSMoney msMoneyFormat = new MSMoney();

            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                writeToFile = processRow(currentRow, msMoneyFormat);

                if (writeToFile) {
                    msMoneyFormat.write(writer);
                }
            }
        } finally {
            fileHandler.closeResources();
        }
    }

    private boolean processRow(Row currentRow, MSMoney msMoneyFormat) {
        boolean writeToFile = false;

        for (Cell currentCell : currentRow) {
            int columnIndex = currentCell.getColumnIndex();
            String cellValue = getCellValueAsString(currentCell).trim();

            switch (columnIndex) {
                case 1: // Date
                    try {
                        msMoneyFormat.setDate(Util.interchangeMonthDate(cellValue, "dd-MM-yyyy"));
                        writeToFile = true;
                    } catch (Exception e) {
                        ErrorHandler.logWarning(currentCell.getStringCellValue() + "--- Exception Date", e);
                        writeToFile = false;
                    }
                    break;

                case 2: // Cheque Number
                    if (currentCell.getCellType() == CellType.STRING) {
                        msMoneyFormat.setChequeNo(currentCell.getStringCellValue().trim());
                    }
                    break;

                case 3: // Remarks - Payee
                    if (currentCell.getCellType() == CellType.STRING) {
                        msMoneyFormat.setPayee(cellValue);
                        msMoneyFormat.setRemarks(cellValue);
                    }
                    break;

                case 4: // Withdrawal
                    if (currentCell.getCellType() == CellType.STRING) {
                        Util.processTransactionAmount(cellValue, msMoneyFormat, true);
                    }
                    break;

                case 5: // Deposit
                    if (currentCell.getCellType() == CellType.STRING) {
                        Util.processTransactionAmount(cellValue, msMoneyFormat, false);

                    }
                    break;
            }
        }
        return writeToFile;
    }
    /**
 * Utility method to get the cell value as a String, regardless of its type.
 *
 * @param cell The cell to retrieve the value from.
 * @return The cell value as a String.
 */
private String getCellValueAsString(Cell cell) {
    switch (cell.getCellType()) {
        case STRING:
            return cell.getStringCellValue();
        case NUMERIC:
            return String.valueOf(cell.getNumericCellValue());
        case BOOLEAN:
            return String.valueOf(cell.getBooleanCellValue());
        case FORMULA:
            return String.valueOf(cell.getCellFormula());
        default:
            return "";
    }
}

}