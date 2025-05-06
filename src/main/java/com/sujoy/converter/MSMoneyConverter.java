package com.sujoy.converter;

import java.io.IOException;
import java.io.Writer;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.sujoy.common.ErrorHandler;
import com.sujoy.model.Transaction;

/**
 * Converter implementation for MS Money QIF format.
 * Converts Transaction objects to MS Money QIF format.
 */
public class MSMoneyConverter implements TransactionConverter {
    
    private static final String DATE_FORMAT_PATTERN = "MM/dd/yyyy";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);
    
    /**
     * Writes the QIF header to the output.
     * 
     * @param writer the writer to write to
     * @throws IOException if an I/O error occurs
     */
    private void writeHeader(Writer writer) throws IOException {
        writer.write("!Type:Bank\n");
    }
    
    @Override
    public void writeTransaction(Transaction transaction, Writer writer) throws IOException {
        if (transaction == null || transaction.getDate() == null) {
            ErrorHandler.logWarning("Skipping transaction with null date");
            return;
        }
        
        StringBuilder sb = new StringBuilder(256); // Pre-allocate buffer size
        
        // Format: D{date}
        sb.append('D').append(transaction.getFormattedDate(DATE_FORMAT_PATTERN)).append('\n');
        
        // Format: T{amount}
        sb.append('T').append(transaction.getSignedAmount()).append('\n');
        
        // Format: N{check number}
        sb.append('N').append(transaction.getChequeNumber()).append('\n');
        
        // Format: P{payee}
        sb.append('P').append(transaction.getPayee()).append('\n');
        
        // Format: M{memo/description}
        sb.append('M').append(transaction.getDescription()).append('\n');
        
        // End of transaction
        sb.append('^').append('\n');
        
        writer.write(sb.toString());
    }
    
    @Override
    public void writeTransactions(List<Transaction> transactions, Writer writer) throws IOException {
        if (transactions == null || transactions.isEmpty()) {
            ErrorHandler.logWarning("No transactions to write");
            return;
        }
        
        // Write header before any transactions
        writeHeader(writer);
        
        // Write each transaction
        for (Transaction transaction : transactions) {
            writeTransaction(transaction, writer);
        }
    }
}