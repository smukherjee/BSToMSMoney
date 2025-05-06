package com.sujoy.parser.legacy;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.ParseException;

import com.sujoy.common.CSVFileHandler;
import com.sujoy.common.ErrorHandler;
import com.sujoy.common.MSMoney;
import com.sujoy.common.Util;

/**
 * @author sujoy
 * @deprecated This class is deprecated in favor of {@link BOIStatementProcessor}.
 * Use BOIStatementProcessor which provides better transaction handling and follows
 * the modern domain model approach.
 */
@Deprecated
public class BOIStmtParserCSV implements StatementParser {

    // private static void writeHeader(BufferedWriter writer) throws IOException {
    //     writer.write("!Type:Bank");
    //     writer.newLine();
    // }

    @SuppressWarnings("UseSpecificCatch")
    private static boolean isValidLine(String[] line) {
//        SimpleDateFormat sdfDate = new SimpleDateFormat("MM-dd-yyyy");
        try {
//            sdfDate.parse(line[1]);
            Util.interchangeMonthDate(line[1],"dd-MM-yyyy");
            // Util.parse(line[1]);

            // System.out.println(line[1]);
            ErrorHandler.logInfo("Line content: " + line[1],null);
            return Boolean.TRUE;
//        } catch (ParseException | IndexOutOfBoundsException e) {
        } catch (Exception e) {
            // System.out.println("Date Exception ------------" + java.util.Arrays.toString(line));
            ErrorHandler.logWarning("Date Exception ------------" + java.util.Arrays.toString(line),e);
            return Boolean.FALSE;
        }
    }

    /**
     * @param line
     * @param writer
     * @throws IOException
     * @throws ParseException
     */
    private static void processLine(String[] line, BufferedWriter writer) {
        try {
            //"Sr No","Date","Remarks","Debit","Credit","Balance Amount"
            //"1","25-04-2025","ATM Card Maint Charge + GST","295.00","","4267.99"
            MSMoney msMoneyFormat = new MSMoney();

            msMoneyFormat.setDate(Util.interchangeMonthDate(line[1],"dd-MM-yyyy"));
            msMoneyFormat.setPayee(line[2].replaceAll("[=,\"]", ""));
            msMoneyFormat.setRemarks(line[2].replaceAll("[=,\"]", ""));
            // msMoneyFormat.setChequeNo(line[3].replaceAll("[=,\"]", ""));

            if (!line[3].trim().equals("")) {
                msMoneyFormat.setTransactionAmount('-' + line[3].trim());
            } else {
                msMoneyFormat.setTransactionAmount(line[4].trim());
            }
            msMoneyFormat.write(writer);
        } catch (ArrayIndexOutOfBoundsException | IOException | ParseException e) {
            // System.out.println("HHHHHHHHHHHHHHH" + line);
            ErrorHandler.logWarning("Invalid Line Content: " + java.util.Arrays.toString(line),e);
            //Do nothing as the line is not a valid line, but has a date??
        }
    }

    @Override
    public void parse(String path, String filename, String ext) throws IOException, ParseException {
        CSVFileHandler fileHandler = new CSVFileHandler();
        try {
            fileHandler.openFile(path, filename, ext);
            BufferedWriter writer = fileHandler.getWriter();

            //writeHeader(writer);

            String[] line;
            while ((line = fileHandler.readNext()) != null) {
                if (isValidLine(line)) {
                    processLine(line, writer);
                }
            }
        } finally {
            fileHandler.closeResources();
        }
    }
    /*
     * (non-Javadoc)
     *
     * @see com.sujoy.parser.StatementParser#parse(java.lang.String)
     */
    // public void parse(String path, String filename, String ext)
    //         throws IOException, ParseException {
    //     String[] line;
    //     CSVReader reader = null;
    //     BufferedWriter writer = null;

    //     try {
    //         reader = new CSVReader(new FileReader(path + File.separator + filename
    //                 + ((ext.length() > 0) ? "." + ext : "")));
    //         writer = new BufferedWriter(new FileWriter(path + File.separator + "Converted"
    //                 + filename + ".qif"));
    //         writeHeader(writer);
    //         while ((line = reader.readNext()) != null) {
    //             if (isValidLine(line)) {
    //                 parseNWriteLine(line, writer);
    //             }
    //         }
    //     } finally {
    //         //FileUtil.closeReaderWriter(reader, writer);
    //         reader.close();
    //         writer.close();
    //     }
    // }
}
