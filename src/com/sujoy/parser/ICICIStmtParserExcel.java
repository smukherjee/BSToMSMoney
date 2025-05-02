package com.sujoy.parser;

import com.sujoy.common.ExcelFileHandler;
import com.sujoy.common.MSMoney;
import com.sujoy.common.Util;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;

/**
 * @author sujoy
 */
public class ICICIStmtParserExcel implements StatementParser {

    /*
     * (non-Javadoc)
     *
     * @see com.sujoy.parser.StatementParser#parse(java.lang.String)
     */
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
                    writeToFile = false;
                }
            }
        } finally {
            fileHandler.closeResources();
        }
    }

    private boolean processRow(Row currentRow, MSMoney msMoneyFormat) {

        Iterator<Cell> cellIterator = currentRow.iterator();
        while (cellIterator.hasNext()) {
            Cell currentCell = cellIterator.next();
            switch (currentCell.getColumnIndex()) {
                case 2: //Date
                    try {
                        msMoneyFormat.setDate(Util.interchangeMonthDate(currentCell.getStringCellValue(), "MM/dd/yyyy"));
                    } catch (Exception e) {
                        System.out.println(currentCell.getStringCellValue() + "--- Exception Date");
                        return false;
                    }
                    break;

                case 4:// Cheque
                    if (currentCell.getCellType() == CellType.STRING) {
                        msMoneyFormat.setChequeNo((currentCell.getStringCellValue()));
                    }
                    break;
                case 5: //Remarks - Payee
                    if (currentCell.getCellType() == CellType.STRING) {
                        msMoneyFormat.setPayee(currentCell.getStringCellValue());
                        msMoneyFormat.setRemarks(currentCell.getStringCellValue());
                    }
                    break;
                case 6: //withdrawal
                    try {
                        if (currentCell.getStringCellValue().trim() != "" && Double.parseDouble(currentCell.getStringCellValue()) > 0.0)
                            msMoneyFormat.setTransactionAmount("-" + Double.parseDouble(currentCell.getStringCellValue()));
                    } catch (Exception ignored) {

                    }
                    break;
                case 7: // Deposit
                    try {

                        if (currentCell.getStringCellValue().trim() != "" && Double.parseDouble(currentCell.getStringCellValue()) > 0.0)
                            msMoneyFormat.setTransactionAmount("" + Double.parseDouble(currentCell.getStringCellValue()));
                    } catch (Exception ignored) {

                    }

                    break;
            }
        }
        return true;

    }
}
