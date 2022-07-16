package com.sujoy.parser;

import com.sujoy.common.FileUtil;
import com.sujoy.common.MSMoney;
import com.sujoy.common.Util;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.text.ParseException;
import java.util.Iterator;

/**
 * @author sujoy
 */
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
                    System.out.println(currentCell.getCellType() + " +++++++++ " + currentCell.toString());

                    switch (currentCell.getColumnIndex()) {
                        case 1: //Date
                            try {
                                msMoneyFormat.setDate(Util.parse(currentCell.getStringCellValue()));
//                                msMoneyFormat.setDate(Util.interchangeMonthDate(currentCell.getStringCellValue(), "dd/MM/yyyy"));
                                writeToFile = true;
                            } catch (Exception e) {
                                writeToFile = false;
                                System.out.println(currentCell.getStringCellValue() + "--- Exception Date");
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
                System.out.println("----------------------");
                if (writeToFile) {
                    msMoneyFormat.write(writer);
                    writeToFile = false;
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtil.closeReaderWriter(reader, writer);
        }
    }

}
