package com.sujoy.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.poi.ss.usermodel.Cell;

import com.sujoy.common.handlers.ErrorHandler;

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
                    // String result = parse(date);
                    // ErrorHandler.logInfo("Parsed result: " + result, null);
                }
            } catch (Exception e) {
                ErrorHandler.logWarning("Parse exception for date: " + date, e);
            }
        }
    }
}
