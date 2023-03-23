package com.sujoy.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
