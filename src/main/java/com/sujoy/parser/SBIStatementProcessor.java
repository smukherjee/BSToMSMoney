package com.sujoy.parser;

import com.sujoy.common.ErrorHandler;
import com.sujoy.model.Transaction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * State Bank of India (SBI) statement processor implementation.
 */
public class SBIStatementProcessor implements StatementProcessor {

    private static final String BANK_NAME = "SBI";
    // SBI uses different date formats across different statements
    private static final DateTimeFormatter[] DATE_FORMATTERS = {
        DateTimeFormatter.ofPattern("dd MMM yyyy"),
        DateTimeFormatter.ofPattern("dd-MMM-yy")
    };
    
    @Override
    public String getBankName() {
        return BANK_NAME;
    }
    
    @Override
    public List<Transaction> processStatement(String path, String filename, String ext) 
            throws IOException, ParseException {
        List<Transaction> transactions = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(
                path + File.separator + filename + (ext.length() > 0 ? "." + ext : "")))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (isValidLine(line)) {
                    Transaction transaction = processLine(line);
                    if (transaction != null) {
                        transactions.add(transaction);
                    }
                }
            }
            
            ErrorHandler.logInfo("Processed " + transactions.size() + " transactions from SBI statement");
            return transactions;
        }
    }
    
    /**
     * Check if a line represents a valid transaction.
     * 
     * @param line The line from the statement file
     * @return true if the line is valid, false otherwise
     */
    private boolean isValidLine(String line) {
        if (line == null || line.length() < 20) {
            return false;
        }
        
        try {
            String[] parts = line.split("\\t");
            if (parts.length < 6) {
                return false;
            }
            
            // Try to parse the date using available formatters
            parseDate(parts[0]);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Process a single line and convert it to a Transaction object.
     * 
     * @param line The line from the statement file
     * @return A Transaction object or null if the line couldn't be processed
     */
    private Transaction processLine(String line) {
        if (line == null) {
            return null;
        }
        
        try {
            String[] parts = line.split("\\t");
            if (parts.length < 6) {
                return null;
            }
            
            Transaction transaction = new Transaction();
            
            // Date (first column)
            transaction.setDate(parseDate(parts[0]));
            
            // Payee/Description (third column)
            transaction.setPayee(parts[2]);
            transaction.setDescription(parts[2]);
            
            // Cheque number (fourth column, clean up 'TRANSFER' prefixes)
            String chequeNo = parts[3].replaceAll("TRANSFER TO ", "")
                                     .replaceAll("TRANSFER FROM ", "")
                                     .replaceAll("TRANSFER T", "")
                                     .trim();
            transaction.setChequeNumber(chequeNo);
            
            // Amount (fifth and sixth columns)
            String debit = parts[4].replaceAll(",", "").replaceAll("\"", "").trim();
            String credit = parts[5].replaceAll(",", "").replaceAll("\"", "").trim();
            
            if (!debit.isEmpty()) {
                // Debit amount
                BigDecimal amount = new BigDecimal(debit);
                transaction.setAmount(amount);
                transaction.setType(Transaction.TransactionType.DEBIT);
            } else if (!credit.isEmpty()) {
                // Credit amount
                BigDecimal amount = new BigDecimal(credit);
                transaction.setAmount(amount);
                transaction.setType(Transaction.TransactionType.CREDIT);
            } else {
                // No amount found
                ErrorHandler.logWarning("No transaction amount found in line: " + line);
                return null;
            }
            
            return transaction;
            
        } catch (Exception e) {
            ErrorHandler.logWarning("Error processing SBI line: " + line, e);
            return null;
        }
    }
    
    /**
     * Parse a date string using multiple available formatters.
     * 
     * @param dateStr The date string to parse
     * @return A LocalDate object
     * @throws DateTimeParseException If the date couldn't be parsed with any formatter
     */
    private LocalDate parseDate(String dateStr) throws DateTimeParseException {
        if (dateStr == null) {
            throw new DateTimeParseException("Null date string", "", 0);
        }
        
        DateTimeParseException lastException = null;
        
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                lastException = e;
                // Try the next formatter
            }
        }
        
        // If we get here, no formatter worked
        if (lastException != null) {
            throw lastException;
        }
        
        throw new DateTimeParseException("Failed to parse date: " + dateStr, dateStr, 0);
    }
}