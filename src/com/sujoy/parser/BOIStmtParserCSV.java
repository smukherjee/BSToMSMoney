package com.sujoy.parser;

import au.com.bytecode.opencsv.CSVReader;
import com.sujoy.common.MSMoney;
import com.sujoy.common.Util;

import java.io.*;
import java.text.ParseException;

/**
 * @author sujoy
 */
public class BOIStmtParserCSV implements StatementParser {

    private static void writeHeader(BufferedWriter writer) throws IOException {
        writer.write("!Type:Bank");
        writer.newLine();
    }

    private static boolean isValidLine(String[] line) {
//        SimpleDateFormat sdfDate = new SimpleDateFormat("MM-dd-yyyy");
        try {
//            sdfDate.parse(line[1]);
            Util.parse(line[1]);

            System.out.println(line[1]);
            return Boolean.TRUE;
//        } catch (ParseException | IndexOutOfBoundsException e) {
        } catch (Exception e) {
            System.out.println("Date Exception ------------" + java.util.Arrays.toString(line));
            return Boolean.FALSE;
        }
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
            msMoneyFormat.setDate(Util.parse(line[1]));
            msMoneyFormat.setPayee(line[2].replaceAll("[=,\"]", ""));
            msMoneyFormat.setRemarks(line[2].replaceAll("[=,\"]", ""));
            msMoneyFormat.setChequeNo(line[3].replaceAll("[=,\"]", ""));

            if (!line[4].trim().equals("CR")) {
                msMoneyFormat.setTransactionAmount('-' + line[5].trim());
            } else {
                msMoneyFormat.setTransactionAmount(line[5].trim());
            }
            msMoneyFormat.write(writer);
        } catch (ArrayIndexOutOfBoundsException | IOException e) {
            System.out.println("HHHHHHHHHHHHHHH" + line);
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
                if (isValidLine(line)) {
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
