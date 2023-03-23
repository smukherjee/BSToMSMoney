/**
 *
 */
package com.sujoy.common;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * @author sujoy
 */
public class MSMoney {

    String date;
    String transactionAmount = "";
    String chequeNo = "";
    String payee = "";
    String remarks = "";

    /**
     * Writes MSMoney object in below format D11-14-2008 T-30.00 N000000000896
     * PREMARKSSSSSSS MTXN TIME 19:35:25 ^
     *
     * @param writer
     * @throws IOException
     */
    public void write(BufferedWriter writer) throws IOException {
        if (getDate() != null) {
            writer.write('D' + getDate());
            writer.newLine();
            writer.write('T' + getTransactionAmount());
            writer.newLine();
            writer.write('N' + getChequeNo());
            writer.newLine();
            writer.write('P' + getPayee());
            writer.newLine();
            writer.write('M' + getRemarks());
            writer.newLine();
            writer.write('^');
            writer.newLine();
        }
    }

    /**
     * returns date
     *
     * @return Returns the date.
     */
    public String getDate() {
        return date;
    }

    /**
     * sets date
     *
     * @param date The date to set.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * returns chequeNo
     *
     * @return Returns the chequeNo.
     */
    public String getChequeNo() {
        return chequeNo;
    }

    /**
     * sets chequeNo
     *
     * @param chequeNo The chequeNo to set.
     */
    public void setChequeNo(String chequeNo) {
        this.chequeNo = chequeNo;
    }

    /**
     * returns payee
     *
     * @return Returns the payee.
     */
    public String getPayee() {
        return payee;
    }

    /**
     * sets payee
     *
     * @param payee The payee to set.
     */
    public void setPayee(String payee) {
        this.payee = payee;
    }

    /**
     * returns remarks
     *
     * @return Returns the remarks.
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * sets remarks
     *
     * @param remarks The remarks to set.
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * returns transactionAmount
     *
     * @return Returns the transactionAmount.
     */
    public String getTransactionAmount() {
        return transactionAmount;
    }

    /**
     * sets transactionAmount
     *
     * @param transactionAmount The transactionAmount to set.
     */
    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

}
