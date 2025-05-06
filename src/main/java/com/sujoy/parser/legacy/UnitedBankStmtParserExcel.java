package com.sujoy.parser.legacy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.sujoy.common.ErrorHandler;
import com.sujoy.common.FileUtil;
import com.sujoy.common.MSMoney;
import com.sujoy.common.Util;

/**
 * @author sujoy
 * @deprecated This class is deprecated in favor of {@link UnitedBankStatementProcessor}.
 * Use UnitedBankStatementProcessor which provides better transaction handling and follows
 * the modern domain model approach.
 */
@Deprecated
public class UnitedBankStmtParserExcel implements StatementParser {

    /*
     * (non-Javadoc)
     *
     * @see com.sujoy.parser.StatementParser#parse(java.lang.String)
     */


    private static void writeHeader(BufferedWriter writer) throws IOException {
        writer.write("!Type:Bank");
        writer.newLine();
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public void parse(String path, String filename, String ext) throws IOException, ParseException {

        BufferedWriter writer = null;
        BufferedReader reader = null;
        MSMoney msMoneyFormat = new MSMoney();
        try {

            FileInputStream excelFile = new FileInputStream(new File(path + File.separator + filename + "." + ext));
            Double amt = 0.0;
            //Workbook workbook = new XSSFWorkbook(excelFile);
            Workbook workbook = new HSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();


            writer = new BufferedWriter(new FileWriter(path + File.separator + "Converted"
                    + filename + ".qif"));
            writeHeader(writer);

            boolean writeToFile = false;
            while (iterator.hasNext()) {

                Row currentRow = iterator.next();

                Iterator<Cell> cellIterator = currentRow.iterator();

                while (cellIterator.hasNext()) {
                    Cell currentCell = cellIterator.next();
//Transaction Date		Cheque Number		Withdrawal		Deposit	Balance	Narration
                    // System.out.println(currentCell.getCellType() + " +++++++++ " + currentCell);
                    ErrorHandler.logInfo(currentCell.getCellType() + " +++++++++ " + currentCell,null);

                    switch (currentCell.getColumnIndex()) {
                        case 1: //Date
                            try {
                                msMoneyFormat.setDate(Util.parse(currentCell.getStringCellValue()));
//                                msMoneyFormat.setDate(Util.interchangeMonthDate(currentCell.getStringCellValue(), "dd/MM/yyyy"));
                                writeToFile = true;
                            } catch (Exception e) {
                                writeToFile = false;
                                // System.out.println(currentCell.getStringCellValue() + "--- Exception Date");
                                ErrorHandler.logWarning(currentCell.getStringCellValue() + "--- Exception Date",e);
                            }
                            break;

                        case 3:// Cheque
                            if (currentCell.getCellType() == CellType.STRING) {
                                msMoneyFormat.setChequeNo((currentCell.getStringCellValue()));
                            }
                            break;
                        case 9: //Remarks - Payee
                            if (currentCell.getCellType() == CellType.STRING) {
                                msMoneyFormat.setPayee(currentCell.getStringCellValue());
                                msMoneyFormat.setRemarks(currentCell.getStringCellValue());
                            }
                            break;
                        case 5: //withdrawal
                            if (currentCell.getCellType() == CellType.STRING) {
                                try {
                                    amt = Double.valueOf(currentCell.getStringCellValue().replaceAll(",", ""));
                                    if (amt > 0.0) {
                                        msMoneyFormat.setTransactionAmount("-" + amt);
                                    }
                                } catch (Exception e) {

                                }
                            }
                            break;
                        case 7: // Deposit
                            if (currentCell.getCellType() == CellType.STRING) {
                                try {
                                    amt = Double.valueOf(currentCell.getStringCellValue().replaceAll(",", ""));
                                    if (amt > 0.0) {
                                        msMoneyFormat.setTransactionAmount("" + amt);
                                    }
                                } catch (Exception e) {

                                }

                            }
                            break;
                    }
                }
                if (writeToFile) {
                    msMoneyFormat.write(writer);
                    writeToFile = false;
                }

            }
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
            ErrorHandler.logError("Excel file not found: " + path + File.separator + filename + "." + ext, e);

        } catch (IOException e) {
            ErrorHandler.logError(ext, e);
        } finally {
            FileUtil.closeReaderWriter(reader, writer);
        }
    }

}
