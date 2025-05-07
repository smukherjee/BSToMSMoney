package com.sujoy.parser;

import com.sujoy.common.handlers.CSVFileHandler;
import com.sujoy.common.handlers.ErrorHandler;
import com.sujoy.model.Transaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Bank of India (BOI) statement processor implementation.
 */
public class BOIStatementProcessor implements StatementProcessor {

    private static final String BANK_NAME = "BOI";
    private static final DateTimeFormatter INPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    
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
            
            ErrorHandler.logInfo("Processed " + transactions.size() + " transactions from BOI statement");
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
        if (line == null || line.length < 5) {
            return false;
        }
        
        try {
            // Check if the date (second column) is valid
            String dateStr = line[1].trim();
            LocalDate.parse(dateStr, INPUT_DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException | ArrayIndexOutOfBoundsException e) {
            ErrorHandler.logWarning("Invalid date format or line structure: " + 
                                    (line.length > 0 ? line[0] : "empty line"), e);
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
        if (line == null || line.length < 5) {
            return null;
        }
        
        try {
            Transaction transaction = new Transaction();
            
            // Date (column 1)
            String dateStr = line[1].trim();
            transaction.setDate(LocalDate.parse(dateStr, INPUT_DATE_FORMATTER));
            
            // Payee/Remarks (column 2)
            String remarks = line[2].replaceAll("[=,\"]", "").trim();
            transaction.setPayee(remarks);
            transaction.setDescription(remarks);
            
            // Amount (columns 3 and 4)
            String debitStr = line[3].trim();
            String creditStr = line[4].trim();
            
            if (!debitStr.isEmpty()) {
                // Debit amount (column 3)
                BigDecimal amount = new BigDecimal(debitStr);
                transaction.setAmount(amount);
                transaction.setType(Transaction.TransactionType.DEBIT);
            } else if (!creditStr.isEmpty()) {
                // Credit amount (column 4)
                BigDecimal amount = new BigDecimal(creditStr);
                transaction.setAmount(amount);
                transaction.setType(Transaction.TransactionType.CREDIT);
            } else {
                // No amount found
                ErrorHandler.logWarning("No transaction amount found in line: " + line[0]);
                return null;
            }
            
            return transaction;
            
        } catch (DateTimeParseException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            ErrorHandler.logWarning("Error processing line: " + (line.length > 0 ? line[0] : "empty line"), e);
            return null;
        }
    }
}