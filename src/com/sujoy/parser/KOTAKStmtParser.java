package com.sujoy.parser;

import au.com.bytecode.opencsv.CSVReader;
import com.sujoy.common.MSMoney;
import com.sujoy.common.Util;

import java.io.*;
import java.text.ParseException;

/**
 * @author sujoy
 */
public class KOTAKStmtParser implements StatementParser {

    private static void writeHeader(BufferedWriter writer) throws IOException {
        writer.write("!Type:Bank");
        writer.newLine();
    }

//    private static final String[] formats = {
//            "dd/MM/yyyy", "dd-MM-yyyy"            };
//    private static boolean isValidLine(String[] line) {
//            for(String parse : formats){
//                SimpleDateFormat sdf = new SimpleDateFormat(parse);
//                try {
//                    sdf.parse(line[2]);
//                    System.out.println(line[2]);
//                }
//            catch (ParseException | IndexOutOfBoundsException e) {
//                System.out.println("Date Exception ------------" + line[2] );
//            }
//            return true;
//        }
//            return false;
//    }

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
            msMoneyFormat.setDate(Util.parse(line[2]));
            msMoneyFormat.setPayee(line[3].replaceAll("[=,\"]", ""));
            msMoneyFormat.setRemarks(line[3].replaceAll("[=,\"]", ""));
            msMoneyFormat.setChequeNo(line[4].replaceAll("[=,\"]", ""));

            if (line[6].trim().equals("DR")) {
                msMoneyFormat.setTransactionAmount('-' + line[5].trim());
            } else {
                msMoneyFormat.setTransactionAmount(line[5].trim());
            }
            msMoneyFormat.write(writer);
        } catch (ArrayIndexOutOfBoundsException | IOException e) {
            System.out.println("HHHHHHHHHHHHHHH");
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
                try {
                    if (Util.isValidLine(line[2])) {
                        parseNWriteLine(line, writer);
                    }
                } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                    //ignore
                }
            }
        } finally {
            //FileUtil.closeReaderWriter(reader, writer);
            reader.close();
            writer.close();
        }
    }

}
