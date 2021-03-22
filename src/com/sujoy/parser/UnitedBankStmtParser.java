/**
 *
 */

package com.sujoy.parser;

import com.sujoy.common.FileUtil;
import com.sujoy.common.MSMoney;
import com.sujoy.common.Util;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.lexer.Page;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * @author sujoy
 *
 */
public class UnitedBankStmtParser implements StatementParser {

    /*
     * (non-Javadoc)
     *
     * @see com.sujoy.parser.StatementParser#parse(java.lang.String)
     */
    public void parse(String path, String filename, String ext)
            throws IOException, ParseException, ParserException {

        Parser parser;
        NodeFilter filter;
        BufferedWriter writer = null;
        ArrayList<MSMoney> transactions = new ArrayList<MSMoney>();

        parser = new Parser();
        filter = new TagNameFilter("table");

        Page.getConnectionManager().setRedirectionProcessingEnabled(true);
        Page.getConnectionManager().setCookieProcessingEnabled(true);
        parser.setResource(path + File.separator + filename
                + ((ext.length() > 0) ? "." + ext : ""));
        NodeList nodes = parser.parse(filter);
        SimpleNodeIterator iterator = nodes.elements();

        while (iterator.hasMoreNodes()) {
            TableTag table = (TableTag) iterator.nextNode();
            for (TableRow row : table.getRows()) {
                if (isValidRow(row)) {
                    transactions.add(parseColumnData(row));
                }
            }
        }

        try {
            // write all transactions in output file
            writer = new BufferedWriter(new FileWriter(path + File.separator + "Converted"
                    + filename + ".qif"));
            writeHeader(writer);
            for (MSMoney transaction : transactions) {
                transaction.write(writer);
            }
        } finally {
            FileUtil.closeReaderWriter(null, writer);
        }

    }

    /**
     * @param row
     * @param dd
     * @throws ParseException
     */
    private MSMoney parseColumnData(TableRow row) throws ParseException {
        MSMoney msMoneyFormat = new MSMoney();
        String debit;
        String credit;

        String date = row.getColumns()[1].toPlainTextString().trim();
        msMoneyFormat.setDate(Util.interchangeMonthDate(date, "MM/dd/yyyy"));

        String description = row.getColumns()[2].toPlainTextString().trim();
        description = description.replaceAll("\n", " ");
        description = description.replaceAll("\\s+", " ");
        msMoneyFormat.setPayee(description);
        msMoneyFormat.setRemarks(description);

        debit = row.getColumns()[4].toPlainTextString().trim();
        credit = row.getColumns()[5].toPlainTextString().trim();

        if (debit.length() > 0 && !debit.contains("nbsp")) {
            msMoneyFormat.setTransactionAmount('-' + debit);
        } else {
            msMoneyFormat.setTransactionAmount(credit);
        }

        return msMoneyFormat;
    }

    /**
     * Validates row
     *
     * @param row
     * @return
     */
    private boolean isValidRow(TableRow row) {
        if (row.getColumnCount() > 4 && isValidDateColumn(row.getColumns()[1])) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;

    }

    /**
     * Validates column if its a valid date column or not
     *
     * @param column
     * @return
     */
    private boolean isValidDateColumn(TableColumn column) {
        String columnData = column.toPlainTextString().trim();

        if (columnData != null) {
            SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy");
            try {
                //columnData.replaceAll("\'", "");
                sdfDate.parse(columnData);
                return Boolean.TRUE;
            } catch (ParseException e) {
                return Boolean.FALSE;
            }
        }
        return Boolean.FALSE;
    }

    private void writeHeader(BufferedWriter writer) throws IOException {
        writer.write("!Type:Bank");
        writer.newLine();
    }

    /*
     * public static void main(String[] args) throws IOException,
     * ParseException, ParserException { UnitedBankStmtParser citiparser = new
     * UnitedBankStmtParser(); citiparser.parse("D:/CitibankSample.html"); }
     */
}
