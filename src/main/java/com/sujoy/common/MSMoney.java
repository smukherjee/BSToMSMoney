/**
 *
 */
package com.sujoy.common;

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
