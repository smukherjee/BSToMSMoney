package com.sujoy.parser;

import au.com.bytecode.opencsv.CSVReader;
import com.sujoy.common.MSMoney;
import com.sujoy.common.Util;

import java.io.*;
import java.text.ParseException;

/**
 * @author sujoy
 */
public class DigiBankStmtParser implements StatementParser {

    private static void writeHeader(BufferedWriter writer) throws IOException {
        writer.write("!Type:Bank");
        writer.newLine();
    }


    /**
     * @param line
     * @param writer
     * @throws IOException
     * @throws ParseException
     */
    private static void parseNWriteLine(String[] line, BufferedWriter writer) {
        try {
            MSMoney msMoneyFormat = new MSMoney();
//            msMoneyFormat.setDate(Util.interchangeMonthDate(line[2], "dd/MM/yyyy"));
            msMoneyFormat.setDate(Util.parse(line[0]));
            msMoneyFormat.setPayee(line[1]);
            msMoneyFormat.setRemarks(line[1]);

            if (!line[2].trim().equals("")) {
                msMoneyFormat.setTransactionAmount('-' + line[2].trim());
            } else {
                msMoneyFormat.setTransactionAmount(line[3].trim());
            }
            msMoneyFormat.write(writer);
        } catch (ArrayIndexOutOfBoundsException | IOException e) {
            System.out.println("Error in parsing" + e.getMessage());
            //Do nothing as the line is not a valid line, but has a date??
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.sujoy.parser.StatementParser#parse(java.lang.String)
     */
    public void parse(String path, String filename, String ext)
            throws IOException, ParseException {
        String[] line;
        CSVReader reader = null;
        BufferedWriter writer = null;


        try {
            reader = new CSVReader(new FileReader(path + File.separator + filename
                    + ((ext.length() > 0) ? "." + ext : "")));
            writer = new BufferedWriter(new FileWriter(path + File.separator + "Converted"
                    + filename + ".qif"));
            writeHeader(writer);


            while ((line = reader.readNext()) != null) {
                if (Util.isValidLine(line[0])) {
                    parseNWriteLine(line, writer);
                }
            }
        } finally {
            //FileUtil.closeReaderWriter(reader, writer);
            reader.close();
            writer.close();
        }
    }

}
