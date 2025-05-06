package com.sujoy.parser.legacy;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;

import com.sujoy.common.ErrorHandler;
import com.sujoy.common.FileUtil;
import com.sujoy.common.MSMoney;
import com.sujoy.common.Util;

/**
 * @author sujoy
 * @deprecated This class is deprecated in favor of {@link SBIStatementProcessor}.
 * Use SBIStatementProcessor which provides better transaction handling and follows
 * the modern domain model approach.
 */
@Deprecated
public class SBIStmtParser implements StatementParser {

    private static void writeHeader(BufferedWriter writer) throws IOException {
        writer.write("!Type:Bank");
        writer.newLine();
    }

    @SuppressWarnings("UseSpecificCatch")
    private static boolean isValidLine(String line) {
        if (line != null && line.length() > 20) {
//			SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MMM-yyyy");
//            SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMM yyyy"); //post 2018

            String[] spl = line.split("\\t");
            try {
                Util.parse(spl[0]);
                //sdfDate.parse(spl[0]);
//                System.out.println(Util.parse(spl[0]));
                ErrorHandler.logInfo("Parsed Value: " + Util.parse(spl[0]),null);
                return Boolean.TRUE;
            } catch (Exception e) {
                ErrorHandler.logWarning(spl[0] + "--- Exception Date", e);
                // System.out.println("Date Exception " + spl[0]);
                // e.printStackTrace();
                return Boolean.FALSE;
            }
        }
        return Boolean.FALSE;
    }

//	/**
//	 * @param line
//	 * @return
//	 */
//
//	private static int dtStart =23;
//	private static int dtEnd =35;
//	
//	private static int payeeStart =55;
//	private static int payeeEnd =105;
//
//	private static int debitStart =106;
//	private static int debitEnd =114;
//
//	private static int amountStart =118;
//	private static int amountEnd =128;
//

    /**
     * @param line
     * @param writer
     * @throws IOException
     * @throws ParseException
     */
    private static void parseNWriteLine(String line, BufferedWriter writer)
            throws IOException, ParseException {
        String[] st = line.split("\\t");

//        System.out.println("payee" + st[2]);
//        System.out.println("cheq" + st[3]);
//        System.out.println("dr" + st[4]);
//        System.out.println("cr" + st[5]);

        MSMoney msMoneyFormat = new MSMoney();

//		msMoneyFormat.setDate(Util.interchangeMonthDate(st[0],"dd-MMM-yy"));
        msMoneyFormat.setDate(Util.parse(st[0]));
//        msMoneyFormat.setDate(Util.interchangeMonthDate(st[0], "dd MMM yyyy")); //Post 2018
        msMoneyFormat.setPayee(st[2]);
        msMoneyFormat.setRemarks(st[2]);
        msMoneyFormat.setChequeNo(st[3].replaceAll("TRANSFER TO ", "").replaceAll("TRANSFER FROM ", "").replaceAll("TRANSFER T", "").trim());
        String debit = st[4].replaceAll(",", "").replaceAll("\"", "");
        String credit = st[5].replaceAll(",", "").replaceAll("\"", "");
        System.out.println(credit);
        msMoneyFormat.setTransactionAmount(((debit.trim()).equals("")) ? credit : "-" + debit);
        msMoneyFormat.write(writer);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.sujoy.parser.StatementParser#parse(java.lang.String)
     */
    @Override
    public void parse(String path, String filename, String ext)
            throws IOException, ParseException {
        String line;
        BufferedReader reader = null;
        BufferedWriter writer = null;


        try {
            reader = new BufferedReader(new FileReader(path + File.separator + filename
                    + ((ext.length() > 0) ? "." + ext : "")));
            writer = new BufferedWriter(new FileWriter(path + File.separator + "Converted"
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
