package com.sujoy.parser;

import com.sujoy.common.ErrorHandler;
import com.sujoy.common.ExcelFileHandler;
import com.sujoy.model.Transaction;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Axis Bank statement processor implementation.
 */
public class AxisStatementProcessor implements StatementProcessor {

    private static final String BANK_NAME = "Axis";
    private static final DateTimeFormatter INPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    
    @Override
    public String getBankName() {
        return BANK_NAME;
    }
    
    @Override
    public List<Transaction> processStatement(String path, String filename, String ext) 
            throws IOException, ParseException {
        List<Transaction> transactions = new ArrayList<>();
        ExcelFileHandler fileHandler = new ExcelFileHandler();
        
        try {
            fileHandler.openFile(path, filename, ext);
            Iterator<Row> iterator = fileHandler.getRowIterator();
            
            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                Transaction transaction = processRow(currentRow);
                
                if (transaction != null) {
                    transactions.add(transaction);
                }
            }
            
            ErrorHandler.logInfo("Processed " + transactions.size() + " transactions from Axis Bank statement");
            return transactions;
            
        } finally {
            fileHandler.closeResources();
        }
    }
    
    /**
     * Process a single row from the Excel file and convert it to a Transaction object.
     * 
     * @param row The Excel row
     * @return A Transaction object or null if the row couldn't be processed
     */
    private Transaction processRow(Row row) {
        if (row == null) return null;
        
        Transaction transaction = new Transaction();
        boolean validTransaction = false;
        
        for (Cell cell : row) {
            int columnIndex = cell.getColumnIndex();
            String cellValue = getCellValueAsString(cell).trim();
            
            switch (columnIndex) {
                case 1: // Date
                    try {
                        LocalDate date = LocalDate.parse(cellValue, INPUT_DATE_FORMATTER);
                        transaction.setDate(date);
                        validTransaction = true;
                    } catch (DateTimeParseException e) {
                        ErrorHandler.logWarning(cellValue + " - Invalid date format", e);
                        return null;
                    }
                    break;
                    
                case 2: // Cheque Number
                    transaction.setChequeNumber(cellValue);
                    break;
                    
                case 3: // Remarks - Payee
                    transaction.setPayee(cellValue);
                    transaction.setDescription(cellValue);
                    break;
                    
                case 4: // Withdrawal
                    if (!cellValue.isEmpty()) {
                        try {
                            BigDecimal amount = new BigDecimal(cellValue);
                            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                                transaction.setAmount(amount);
                                transaction.setType(Transaction.TransactionType.DEBIT);
                            }
                        } catch (NumberFormatException e) {
                            ErrorHandler.logWarning("Invalid withdrawal amount: " + cellValue, e);
                        }
                    }
                    break;
                    
                case 5: // Deposit
                    if (!cellValue.isEmpty()) {
                        try {
                            BigDecimal amount = new BigDecimal(cellValue);
                            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                                transaction.setAmount(amount);
                                transaction.setType(Transaction.TransactionType.CREDIT);
                            }
                        } catch (NumberFormatException e) {
                            ErrorHandler.logWarning("Invalid deposit amount: " + cellValue, e);
                        }
                    }
                    break;
            }
        }
        
        return validTransaction ? transaction : null;
    }
    
    /**
     * Get cell value as a string regardless of the cell type.
     * 
     * @param cell The cell to get the value from
     * @return The cell value as a string
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
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
}