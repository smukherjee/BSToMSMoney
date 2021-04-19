package com.sujoy.parser;

import au.com.bytecode.opencsv.CSVReader;
import com.sujoy.common.MSMoney;
import com.sujoy.common.Util;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author sujoy
 */
public class SYNDICATEStmtParser implements StatementParser {

    private static void writeHeader(BufferedWriter writer) throws IOException {
        writer.write("!Type:Bank");
        writer.newLine();
    }

    private static boolean isValidLine(String[] line) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            sdfDate.parse(line[1]);
            System.out.println(line[1]);
            return Boolean.TRUE;
        } catch (ParseException e) {
            System.out.println("Date Exception" + line[1]);
            return Boolean.FALSE;
        } catch (Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
    }

    /**
     * @param line
     * @param writer
     * @throws IOException
     * @throws ParseException
     */
    private static void parseNWriteLine(String[] line, BufferedWriter writer)
            throws IOException, ParseException {
        String amount = line[4].trim();
        System.out.println("Date: [" + line[0] + "]\nDescription: [" + line[3] + "]\nchq: [" + line[2] + "]" + "]\nCredit: [" + line[5] + "]" + "]\nDebit: [" + line[4] + "]");

        MSMoney msMoneyFormat = new MSMoney();
        msMoneyFormat.setDate(Util.interchangeMonthDate(line[0], "dd-MMM-yyyy"));
        msMoneyFormat.setPayee(line[3].replaceAll("[=,\"]", ""));
        msMoneyFormat.setRemarks(line[3].replaceAll("[=,\"]", ""));
        msMoneyFormat.setChequeNo(line[2].replaceAll("[=,\"]", ""));
//		System.out.println(line.substring(debitStart, debitEnd).trim());
        if (line[4].trim().length() > 0) {
            msMoneyFormat.setTransactionAmount('-' + amount);
        } else {
            msMoneyFormat.setTransactionAmount(line[5].trim());
        }
        msMoneyFormat.write(writer);
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
