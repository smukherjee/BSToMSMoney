
package com.sujoy.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
//import java.util.Date;

/**
 * @author sujoy
 *
 */
public class Util {

    /**
     * change input format type to MM/DD/YYYY format
     *
     * @param
     * @return
     * @throws ParseException
     */
    public static String interchangeMonthDate(String date, String format)
            throws ParseException {
        SimpleDateFormat sdfMSMoneyDate = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat sdfInputDate = new SimpleDateFormat(format);

        //Date givenDate = sdfDate.parse(date);
        System.out.println(sdfMSMoneyDate.format(sdfInputDate.parse(date)));
        return sdfMSMoneyDate.format(sdfInputDate.parse(date));
    }

    public static void main(String[] args) throws Exception {
        System.out.println(interchangeMonthDate("16/06/2008", "dd/MM/yyyy"));
        System.out.println(interchangeMonthDate("06/16/2008", "MM/dd/yyyy"));
        System.out.println(interchangeMonthDate("16-Jun-2008", "dd-MMM-yyyy"));
        System.out.println(interchangeMonthDate("06-16-2008", "MM-dd-yyyy"));
    }
}
