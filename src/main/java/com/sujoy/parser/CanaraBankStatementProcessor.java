package com.sujoy.parser;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.sujoy.common.handlers.CSVFileHandler;
import com.sujoy.common.handlers.ErrorHandler;
import com.sujoy.model.Transaction;

/**
 * Canara Bank statement processor implementation.
 */
public class CanaraBankStatementProcessor implements StatementProcessor {

    private static final String BANK_NAME = "CANARABANK";
    private static final DateTimeFormatter INPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    @Override
    public String getBankName() {
        return BANK_NAME;
    }
    
    @Override
    public List<Transaction> processStatement(String path, String filename, String ext) 
            throws IOException, ParseException {
        
        List<Transaction> transactions = new ArrayList<>();
        CSVFileHandler fileHandler = new CSVFileHandler();
        
        try {
            fileHandler.openFile(path, filename, ext);
            String[] line;
            
            while ((line = fileHandler.readNext()) != null) {
                if (isValidLine(line)) {
                    Transaction transaction = processLine(line);
                    if (transaction != null) {
                        transactions.add(transaction);
                    }
                }
            }
            
            ErrorHandler.logInfo("Processed " + transactions.size() + " transactions from Canara Bank statement");
            return transactions;
            
        } finally {
            fileHandler.closeResources();
        }
    }
    
    /**
     * Check if a CSV line represents a valid transaction.
     * 
     * @param line The CSV line split into fields
     * @return true if the line is valid, false otherwise
     */
    private boolean isValidLine(String[] line) {
        if (line == null || line.length < 7) {
            return false;
        }
        
        // Check if the first column has a valid date
        String dateStr = line[0];
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }
        
        dateStr = dateStr.replace("=", "").trim();
        
        try {
            LocalDate.parse(dateStr, INPUT_DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    /**
     * Process a single CSV line and convert it to a Transaction object.
     * 
     * @param line The CSV line split into fields
     * @return A Transaction object or null if the line couldn't be processed
     */
    private Transaction processLine(String[] line) {
        try {
            Transaction transaction = new Transaction();
            
            // Date (first column)
            String dateStr = line[0].replace("=", "").trim();
            transaction.setDate(LocalDate.parse(dateStr, INPUT_DATE_FORMATTER));
            
            // Payee/Description (fourth column)
            transaction.setPayee(line[3]);
            transaction.setDescription(line[3]);
            
            // Amount (sixth and seventh columns)
            String debitStr = line[5].trim();
            String creditStr = line[6].trim();
            
            if (!debitStr.isEmpty()) {
                // Debit amount
                BigDecimal amount = new BigDecimal(debitStr);
                transaction.setAmount(amount);
                transaction.setType(Transaction.TransactionType.DEBIT);
            } else if (!creditStr.isEmpty()) {
                // Credit amount
                BigDecimal amount = new BigDecimal(creditStr);
                transaction.setAmount(amount);
                transaction.setType(Transaction.TransactionType.CREDIT);
            } else {
                // No amount found
                ErrorHandler.logWarning("No transaction amount found in line: " + String.join(",", line));
                return null;
            }
            
            return transaction;
            
        } catch (Exception e) {
            ErrorHandler.logWarning("Error processing Canara Bank line: " + String.join(",", line), e);
            return null;
        }
    }
}