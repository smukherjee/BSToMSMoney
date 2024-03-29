package com.sujoy.common;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
//import java.util.Date;

/**
 * @author sujoy
 */
public class Util {

    /**
     * change input format type to specified formats
     */
    private static final SimpleDateFormat sdfMSMoneyDate = new SimpleDateFormat("MM/dd/yyyy");
    private static final String[] formats = {
//            "yyyy-MM-dd'T'HH:mm:ss'Z'",   "yyyy-MM-dd'T'HH:mm:ssZ",
//            "yyyy-MM-dd'T'HH:mm:ss",      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
//            "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd HH:mm:ss",
//            "MM/dd/yyyy HH:mm:ss",        "MM/dd/yyyy'T'HH:mm:ss.SSS'Z'",
//            "MM/dd/yyyy'T'HH:mm:ss.SSSZ", "MM/dd/yyyy'T'HH:mm:ss.SSS",
//            "MM/dd/yyyy'T'HH:mm:ssZ",     "MM/dd/yyyy'T'HH:mm:ss",
//            "yyyy:MM:dd HH:mm:ss",        "yyyyMMdd",
            "dd/MM/yyyy",
            "dd-MM-yyyy",
            "dd-MMM-yy", //Canara
            "dd-MM-yyyy HH:MM:SS", //Canara
            "yyyy/MMM/dd",
            "dd-MMM-yyyy", //DBS
            "dd-MMM", //DBS
            "dd MMM yyyy" //SBI
    };

    public static String interchangeMonthDate(String date, String format)
            throws ParseException {

        SimpleDateFormat sdfInputDate = new SimpleDateFormat(format);

        //Date givenDate = sdfDate.parse(date);
        System.out.println(sdfMSMoneyDate.format(sdfInputDate.parse(date)));
        return sdfMSMoneyDate.format(sdfInputDate.parse(date));
    }

    public static String parse(String d) {
        SimpleDateFormat sdf;
        String finalDate = null;
        if (d != null) {

            for (String parseFormat : formats) {
                sdf = new SimpleDateFormat(parseFormat);
                try {
                    finalDate = sdfMSMoneyDate.format(sdf.parse(d));
                    if (sdf.parse(d).getYear() == 70) { // if the year is not passed then java instantiates it to 1970, then replace
                        Calendar now = Calendar.getInstance();
                        int year = now.get(Calendar.YEAR);
                        String yearInString = String.valueOf(year);
                        finalDate = finalDate.replace("1970", yearInString);
                    }
                    System.out.println("Converted Date --- " + finalDate);
                    return finalDate;
                } catch (ParseException e) {
                    //e.printStackTrace();

                }
            }
        }
        if (!finalDate.equals(null)) {
            System.out.println("Couldn't convert date " + d);
        }
        return finalDate;
    }

    public static boolean isValidLine(String d) {
        SimpleDateFormat sdf;
        boolean converted = false;
        if (d != null) {
            for (String parseFormat : formats) {
                sdf = new SimpleDateFormat(parseFormat);
                try {
                    sdfMSMoneyDate.format(sdf.parse(d));
                    converted = true;
                    return converted;
                } catch (ParseException e) {
                    //e.printStackTrace();
//                    System.out.println("Couldn't convert date " + d +" with format " + parseFormat);
                }
            }
        }
        if (!converted) {
            System.out.println("Couldn't convert date --- " + d);
        }
        return converted;
    }

    //open excel file for writhing
    public static Map<String ,Object> openExcelFile(String path, String filename, String ext) {
        BufferedWriter writer = null;
        BufferedReader reader = null;
        Workbook workbook = null;
        Sheet datatypeSheet = null;
        Iterator <Row> iterator = null;
        boolean writeToFile = false;

        try {
            FileInputStream excelFile = new FileInputStream(new File(path + File.separator + filename + "." + ext));
            Double amt = 0.0;
            //Workbook workbook = new XSSFWorkbook(excelFile);
             workbook = new HSSFWorkbook(excelFile);
             datatypeSheet = workbook.getSheetAt(0);
             iterator = datatypeSheet.iterator();


            writer = new BufferedWriter(new FileWriter(path + File.separator + "Converted"
                    + filename + ".qif"));
            writeHeader(writer);

        } catch (Exception e) {
            System.out.println("Error opening file " + filename);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("reader", reader);
        result.put("writer", writer);
        result.put("workbook", workbook);
        result.put("datatypeSheet", datatypeSheet);

        return result;
    }
    private static void writeHeader(BufferedWriter writer) throws IOException {
        writer.write("!Type:Bank");
        writer.newLine();
    }

    public static void main(String[] args) {
        String yyyyMMdd = "22-06-2022 08:19:24";
        parse(yyyyMMdd);
        System.out.println(isValidLine(yyyyMMdd));

//        yyyyMMdd = "03-10-2021";
//        parse(yyyyMMdd);
//        System.out.println(isValidLine(yyyyMMdd));

//        System.out.println(interchangeMonthDate("16/06/2008", "dd/MM/yyyy"));
//        System.out.println(interchangeMonthDate("06/16/2008", "MM/dd/yyyy"));
//        System.out.println(interchangeMonthDate("16-Jun-2008", "dd-MMM-yyyy"));
//        System.out.println(interchangeMonthDate("06-16-2008", "MM-dd-yyyy"));
    }


}
