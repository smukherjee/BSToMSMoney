package com.sujoy.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.poi.ss.usermodel.Cell;

/**
 * Utility class for various date parsing and conversion operations.
 * 
 * @author sujoy
 */
public class Util {

    /**
     * change input format type to specified formats
     */
    private static final SimpleDateFormat MS_MONEY_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
    
    // Thread-local SimpleDateFormat to avoid concurrency issues
    private static final ThreadLocal<ConcurrentMap<String, SimpleDateFormat>> DATE_FORMAT_THREAD_LOCAL = 
        ThreadLocal.withInitial(ConcurrentHashMap::new);
    
    // List of supported date formats
    private static final List<String> SUPPORTED_DATE_FORMATS = Arrays.asList(
            "dd/MM/yyyy",
            "dd-MM-yyyy",
            "dd-MMM-yy", //Canara
            "dd-MM-yyyy HH:MM:SS", //Canara
            "yyyy/MMM/dd",
            "dd-MMM-yyyy", //DBS
            "dd-MMM", //DBS
            "dd MMM yyyy", //SBI
            "dd-MMM-yy", //SBI
            "d-MMM-yy"
    );

    /**
     * Get a thread-safe date format for the specified pattern
     * 
     * @param pattern The date format pattern
     * @return A SimpleDateFormat instance
     */
    private static SimpleDateFormat getDateFormat(String pattern) {
        return DATE_FORMAT_THREAD_LOCAL.get()
                .computeIfAbsent(pattern, SimpleDateFormat::new);
    }

    /**
     * Converts a date string from one format to MS Money format
     * 
     * @param date   The date string to convert
     * @param format The format of the input date string
     * @return The date string formatted for MS Money
     * @throws ParseException If the date cannot be parsed
     */
    public static String interchangeMonthDate(String date, String format) throws ParseException {
        if (date == null || date.trim().isEmpty()) {
            throw new ParseException("Date string is null or empty", 0);
        }

        SimpleDateFormat sdfInputDate = getDateFormat(format);
        
        try {
            String result = MS_MONEY_DATE_FORMAT.format(sdfInputDate.parse(date));
            ErrorHandler.logInfo("Converted Date - " + result, null);
            return result;
        } catch (ParseException e) {
            ErrorHandler.logWarning("Failed to convert date: " + date + " with format: " + format, e);
            throw e;
        }
    }

    /**
     * Parse a date string using multiple formats and convert to MS Money format
     * 
     * @param d The date string to parse
     * @return The formatted date string, or null if parsing failed
     * @throws ParseException If the date cannot be parsed with any of the supported formats
     */
    public static String parse(String d) throws ParseException {
        if (d == null || d.trim().isEmpty()) {
            throw new ParseException("Date string is null or empty", 0);
        }
        
        String finalDate = null;
        Calendar now = Calendar.getInstance();
        int currentYear = now.get(Calendar.YEAR);
        String currentYearStr = String.valueOf(currentYear);
        
        // Try each supported format until one works
        for (String parseFormat : SUPPORTED_DATE_FORMATS) {
            SimpleDateFormat sdf = getDateFormat(parseFormat);
            sdf.setLenient(false); // Strict parsing
            
            try {
                java.util.Date parsedDate = sdf.parse(d);
                finalDate = MS_MONEY_DATE_FORMAT.format(parsedDate);
                
                // If the year is 1970 (default year), replace with current year
                if (parsedDate.getYear() == 70) {
                    finalDate = finalDate.replace("1970", currentYearStr);
                }
                
                ErrorHandler.logInfo("Converted Date --- " + finalDate, null);
                return finalDate;
            } catch (ParseException e) {
                // Try next format, only log at debug level to avoid console spam
                ErrorHandler.logError("Failed to parse date '" + d + "' with format '" + parseFormat + "'", e);
            }
        }
        
        // If we got here, no format worked
        String message = "Couldn't convert date " + d;
        throw new ParseException(message, 0);
    }

  

    /**
     * Processes transaction amounts from a string value to a MSMoney object
     * 
     * @param cellValue The string value representing the transaction amount
     * @param msMoneyFormat The MSMoney object to update
     * @param isWithdrawal Whether the amount represents a withdrawal (debit)
     */
    public static void processTransactionAmount(String cellValue, MSMoney msMoneyFormat, boolean isWithdrawal) {
        if (cellValue == null || cellValue.isEmpty()) {
            return;
        }
        
        try {
            // Remove any currency symbols and commas
            String cleanedValue = cellValue.replaceAll("[^0-9.-]", "");
            double amount = Double.parseDouble(cleanedValue);
            
            if (amount > 0.0) {
                String transactionAmount = (isWithdrawal ? "-" : "") + amount;
                msMoneyFormat.setTransactionAmount(transactionAmount);
            }
        } catch (NumberFormatException e) {
            ErrorHandler.logWarning("Invalid transaction amount: " + cellValue, e);
        }
    }
        /**
     * Get cell value as a string regardless of the cell type.
     * 
     * @param cell The cell to get the value from
     * @return The cell value as a string
     */
    public static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return String.valueOf(cell.getCellFormula());
            default:
                return "";
        }
    }
    
    public static void main(String[] args) {
        String[] testDates = {
            "22-06-2022 08:19:24",
            "03-10-2021",
            "16/06/2008",
            "16-Jun-2008",
            null,
            "invalid-date"
        };
        
        for (String date : testDates) {
            try {
                if (date != null) {
                    ErrorHandler.logInfo("Testing date: " + date, null);
                    String result = parse(date);
                    ErrorHandler.logInfo("Parsed result: " + result, null);
                }
            } catch (ParseException e) {
                ErrorHandler.logWarning("Parse exception for date: " + date, e);
            }
        }
    }
}
