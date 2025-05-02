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
import com.sujoy.common.ErrorHandler;
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
            "dd MMM yyyy", //SBI
            "dd-MMM-yy", //SBI
            "d-MMM-yy",

    };

    public static String interchangeMonthDate(String date, String format)
            throws ParseException {

        SimpleDateFormat sdfInputDate = new SimpleDateFormat(format);

        //Date givenDate = sdfDate.parse(date);
        System.out.println("Converted Date - " + sdfMSMoneyDate.format(sdfInputDate.parse(date)));
        return sdfMSMoneyDate.format(sdfInputDate.parse(date));
    }

    /**
     * Parse a date string using multiple formats and convert to MS Money format
     * 
     * @param d The date string to parse
     * @return The formatted date string, or null if parsing failed
     * @throws ParseException If the date cannot be parsed with any of the supported formats
     */
    public static String parse(String d) throws ParseException {
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
                    // Try next format
                    ErrorHandler.logInfo("Failed to parse date '" + d + "' with format '" + parseFormat + "'", e);
                    finalDate = null;
                }
            }
        }
        if (finalDate == null) {
            String message = "Couldn't convert date " + d;
            System.out.println(message);
            // Only throw if we've tried all formats and none worked
            if (d != null) {
                throw new ParseException(message, 0);
            }
        }
        return finalDate;
    }

    /**
     * Check if a string can be parsed as a date using any of the supported formats
     * 
     * @param d The date string to check
     * @return true if the string can be parsed as a date, false otherwise
     */
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
                    // Try next format
                    ErrorHandler.logInfo("Failed to validate date '" + d + "' with format '" + parseFormat + "'", e);
                }
            }
        }
        if (!converted) {
            String message = "Couldn't convert date --- " + d;
            System.out.println(message);
            ErrorHandler.logWarning(message, new ParseException("No matching date format found", 0));
        }
        return converted;
    }

    /**
     * Open an Excel file for reading and prepare a QIF file for writing
     * 
     * @param path The directory path
     * @param filename The filename without extension
     * @param ext The file extension
     * @return A map containing the reader, writer, workbook, and datatype sheet
     */
    public static Map<String, Object> openExcelFile(String path, String filename, String ext) {
        BufferedWriter writer = null;
        BufferedReader reader = null;
        Workbook workbook = null;
        Sheet datatypeSheet = null;
        Iterator<Row> iterator = null;
        FileInputStream excelFile = null;

        Map<String, Object> result = new HashMap<>();

        try {
            // Open the Excel file
            File file = new File(path + File.separator + filename + "." + ext);
            excelFile = new FileInputStream(file);

            // Create the workbook and get the first sheet
            workbook = new HSSFWorkbook(excelFile);
            datatypeSheet = workbook.getSheetAt(0);
            iterator = datatypeSheet.iterator();

            // Create the output QIF file
            File outputFile = new File(path + File.separator + "Converted" + filename + ".qif");
            writer = new BufferedWriter(new FileWriter(outputFile));
            writeHeader(writer);

            // Store all resources in the result map
            result.put("reader", reader);
            result.put("writer", writer);
            result.put("workbook", workbook);
            result.put("datatypeSheet", datatypeSheet);
            result.put("iterator", iterator);

        } catch (FileNotFoundException e) {
            ErrorHandler.logError("Excel file not found: " + path + File.separator + filename + "." + ext, e);
        } catch (IOException e) {
            ErrorHandler.logError("I/O error opening Excel file: " + path + File.separator + filename + "." + ext, e);
        } catch (Exception e) {
            ErrorHandler.logError("Unexpected error opening Excel file: " + path + File.separator + filename + "." + ext, e);
        } finally {
            // Close the input stream if there was an error
            if (excelFile != null && result.get("workbook") == null) {
                try {
                    excelFile.close();
                } catch (IOException e) {
                    ErrorHandler.logWarning("Error closing Excel file input stream", e);
                }
            }
        }

        return result;
    }
    private static void writeHeader(BufferedWriter writer) throws IOException {
        writer.write("!Type:Bank");
        writer.newLine();
    }

    public static void main(String[] args) {
        String yyyyMMdd = "22-06-2022 08:19:24";
        try {
            parse(yyyyMMdd);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
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
