package com.sujoy.parser;

import com.sujoy.common.FileUtil;
import com.sujoy.common.MSMoney;
import com.sujoy.common.Util;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.text.ParseException;
import java.util.Iterator;
//import java.util.Map;

/**
 * @author sujoy
 */
public class AxisStmtParserExcel implements StatementParser {

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

//        Map<String,Object> result= Util.openExcelFile( path,  filename,  ext);

        BufferedWriter writer = null;
        BufferedReader reader = null;
        MSMoney msMoneyFormat = new MSMoney();

        try {

            FileInputStream excelFile = new FileInputStream(path + File.separator + filename + "." + ext);
            double amt ;
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
                for (Cell currentCell : currentRow) {
                    //						SRL NO	Tran Date	CHQNO	PARTICULARS	DR	CR	BAL	SOL
                    System.out.println(currentCell.getCellType() + " +++++++++ " + currentCell);

                    switch (currentCell.getColumnIndex()) {
                        case 1: //Date
                            try {
                                msMoneyFormat.setDate(Util.interchangeMonthDate(currentCell.getStringCellValue(), "dd-MM-yyyy"));
                                writeToFile = true;
                            } catch (Exception e) {
                                writeToFile = false;
                                System.out.println(currentCell.getStringCellValue() + "--- Exception Date");
                            }
                            break;

                        case 2:// Cheque
                            if (currentCell.getCellType() == CellType.STRING) {
                                msMoneyFormat.setChequeNo((currentCell.getStringCellValue()));
                            }
                            break;
                        case 3: //Remarks - Payee
                            if (currentCell.getCellType() == CellType.STRING) {
                                msMoneyFormat.setPayee(currentCell.getStringCellValue());
                                msMoneyFormat.setRemarks(currentCell.getStringCellValue());
                            }
                            break;
                        case 4: //withdrawal - For axis this comes as string
                            if (currentCell.getCellType() == CellType.STRING) {
                                try {
                                    amt = Double.parseDouble(currentCell.getStringCellValue());
                                    if (amt > 0.0) {
                                        msMoneyFormat.setTransactionAmount("-" + currentCell.getStringCellValue().trim());
                                    }
                                } catch (Exception e) {
//										writeToFile=false;

                                }

                            }
                            break;
                        case 5: // Deposit
                            if (currentCell.getCellType() == CellType.STRING) {
                                try {
                                    amt = Double.parseDouble(currentCell.getStringCellValue());
                                    if (amt > 0.0) {
                                        msMoneyFormat.setTransactionAmount("" + currentCell.getStringCellValue().trim());
                                    }
                                } catch (Exception e) {
//										writeToFile=false;

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
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtil.closeReaderWriter(reader, writer);
        }
    }

}
