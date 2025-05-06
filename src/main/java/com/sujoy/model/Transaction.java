package com.sujoy.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Domain model class representing a bank transaction.
 * This class provides a format-independent representation of a transaction.
 */
public class Transaction {

    private LocalDate date;
    private BigDecimal amount;
    private String chequeNumber;
    private String payee;
    private String description;
    private TransactionType type;
    private String accountNumber;
    private BigDecimal balance;
    /**
     * Default constructor
     */
    public Transaction() {
        this.chequeNumber = "";
        this.payee = "";
        this.description = "";
        this.type = TransactionType.UNKNOWN;
        this.accountNumber = "";
    }

    /**
     * Constructor with essential fields
     *
     * @param date   transaction date
     * @param amount transaction amount
     * @param type   transaction type (debit/credit)
     */
    public Transaction(LocalDate date, BigDecimal amount, TransactionType type) {
        this();
        this.date = date;
        this.amount = amount;
        this.type = type;
    }

    /**
     * Full constructor
     */
    public Transaction(LocalDate date, BigDecimal amount, String chequeNumber,
                       String payee, String description, TransactionType type,
                       String accountNumber, BigDecimal balance) {
        this.date = date;
        this.amount = amount;
        this.chequeNumber = chequeNumber;
        this.payee = payee;
        this.description = description;
        this.type = type;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public LocalDate getDate() {
        return date;
    }

    // Getters and setters

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getChequeNumber() {
        return chequeNumber;
    }

    public void setChequeNumber(String chequeNumber) {
        this.chequeNumber = chequeNumber != null ? chequeNumber : "";
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee != null ? payee : "";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber != null ? accountNumber : "";
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    /**
     * Get the sign-adjusted amount based on transaction type
     *
     * @return positive amount for credit, negative for debit
     */
    public BigDecimal getSignedAmount() {
        if (amount == null) return BigDecimal.ZERO;

        return type == TransactionType.DEBIT
                ? amount.negate()
                : amount;
    }

    /**
     * Format the date using the specified pattern
     *
     * @param pattern date format pattern
     * @return formatted date string or empty string if date is null
     */
    public String getFormattedDate(String pattern) {
        if (date == null) return "";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(chequeNumber, that.chequeNumber) &&
                Objects.equals(payee, that.payee) &&
                Objects.equals(description, that.description) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, amount, chequeNumber, payee, description, type);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "date=" + (date != null ? date : "null") +
                ", amount=" + (amount != null ? amount : "null") +
                ", type=" + type +
                ", payee='" + payee + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    /**
     * Transaction type enumeration
     */
    public enum TransactionType {
        DEBIT, CREDIT, UNKNOWN
    }
}