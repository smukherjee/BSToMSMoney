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
public class ICICIStmtParserExcel implements StatementParser {

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

//	                    S No.--Value Date--Transaction Date--Cheque Number--Transaction Remarks--Withdrawal Amount (INR )--Deposit Amount (INR )--Balance (INR )--
                    System.out.println(currentCell.getCellType() + " +++++++++ " + currentCell.toString());

//	                    if (currentCell.getCellType() == CellType.STRING) {
//	                        System.out.println(currentCell.getStringCellValue() );
//	                    } else if (currentCell.getCellType() == CellType.NUMERIC) {
//	                        System.out.println(currentCell.getNumericCellValue() );
//	                    } else{
//	                    	continue;
//						}

                    switch (currentCell.getColumnIndex()) {
                        case 2: //Date
                            try {
                                msMoneyFormat.setDate(Util.interchangeMonthDate(currentCell.getStringCellValue(), "MM/dd/yyyy"));
                                writeToFile = true;
                            } catch (Exception e) {
                                writeToFile = false;
                                System.out.println(currentCell.getStringCellValue() + "--- Exception Date");
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
                                //if (currentCell.getCellType() == CellType.NUMERIC) {
//                                amt = currentCell.getNumericCellValue();
//                                if (amt > 0.0) {
//                                    msMoneyFormat.setTransactionAmount("-" + currentCell.getNumericCellValue());
//                                }
//                            }
                            }
                            catch (Exception e){

                            }
                            break;
                        case 7: // Deposit
                            try {

                                if (currentCell.getStringCellValue().trim() != "" && Double.parseDouble(currentCell.getStringCellValue()) > 0.0)
                                    msMoneyFormat.setTransactionAmount("" + Double.parseDouble(currentCell.getStringCellValue()));
//                            if (currentCell.getCellType() == CellType.NUMERIC) {
//                                amt = currentCell.getNumericCellValue();
//                                if (amt > 0.0) {
//                                    msMoneyFormat.setTransactionAmount("" + currentCell.getNumericCellValue());
//                                }
//                            }
                            }catch (Exception e)
                            {

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
