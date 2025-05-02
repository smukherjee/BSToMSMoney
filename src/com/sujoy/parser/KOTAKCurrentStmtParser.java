package com.sujoy.parser;

import com.sujoy.common.FileUtil;
import com.sujoy.common.MSMoney;
import com.sujoy.common.Util;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author sujoy
 */
@Deprecated public class KOTAKCurrentStmtParser implements StatementParser {

    /**
     * @param line
     * @return
     */
    private static final int dtStart = 6;
    private static final int dtEnd = 16;
    private static final int payeeStart = 16;
    private static final int payeeEnd = 51;
    private static final int amountStart = 67;
    private static final int amountEnd = 75;
    private static final int debitStart = 75;
    private static final int debitEnd = 78;
    private static final int chequeStart = 51;
    private static final int chequeEnd = 65;

    private static void writeHeader(BufferedWriter writer) throws IOException {
        writer.write("!Type:Bank");
        writer.newLine();
    }

    private static boolean isValidLine(String line) {
        if (line != null && line.length() > 9) {
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
            //SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
            try {
                sdfDate.parse(line.substring(dtStart, dtEnd));
                //Util.interchangeMonthDate(line.substring(0, 10), "dd-MM-yyyy")
                System.out.println(line.substring(dtStart, dtEnd));
                return Boolean.TRUE;
            } catch (ParseException e) {
                System.out.println("Date Exception" + line.substring(dtStart, dtEnd));

                return Boolean.FALSE;
            } catch (Exception e) {
                //System.out.println("Other Exception" + );
                e.printStackTrace();
                return Boolean.FALSE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * @param line
     * @param writer
     * @throws IOException
     * @throws ParseException
     */
    private static void parseNWriteLine(String line, BufferedWriter writer)
            throws IOException, ParseException {
        String debit;
        String credit;
        MSMoney msMoneyFormat = new MSMoney();
        msMoneyFormat.setDate(Util.interchangeMonthDate(line.substring(dtStart, dtEnd), "dd/MM/yyyy"));
        msMoneyFormat.setPayee(line.substring(payeeStart, payeeEnd).trim());
        msMoneyFormat.setRemarks(line.substring(payeeStart, payeeEnd).trim());
        msMoneyFormat.setChequeNo(line.substring(chequeStart, chequeEnd).trim());
        System.out.println(line.substring(debitStart, debitEnd).trim());
        if (line.substring(debitStart, debitEnd).trim().equals("DR")) {
            debit = line.substring(amountStart, amountEnd).trim();
            msMoneyFormat.setTransactionAmount('-' + debit);
        } else {
            credit = line.substring(amountStart, amountEnd).trim();
            msMoneyFormat.setTransactionAmount(credit);
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
        String line;
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            reader = new BufferedReader(new FileReader(path + "\\" + filename
                    + ((ext.length() > 0) ? "." + ext : "")));
            writer = new BufferedWriter(new FileWriter(path + "\\Converted"
                    + filename + ".qif"));
            writeHeader(writer);
            while ((line = reader.readLine()) != null) {
                if (isValidLine(line)) {
                    parseNWriteLine(line, writer);
                }
            }
        } finally {
            FileUtil.closeReaderWriter(reader, writer);
        }
    }

}
