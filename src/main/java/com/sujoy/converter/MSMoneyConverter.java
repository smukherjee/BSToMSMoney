package com.sujoy.converter;

import com.sujoy.common.handlers.ErrorHandler;
import com.sujoy.model.Transaction;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Converter implementation for MS Money QIF format.
 * Converts Transaction objects to MS Money QIF format.
 */
public class MSMoneyConverter implements TransactionConverter {

    private static final String DATE_FORMAT_PATTERN = "MM/dd/yyyy";
    // private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);

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

        // Format: D{date}

        // Pre-allocate buffer size
        String sb = 'D' + transaction.getFormattedDate(DATE_FORMAT_PATTERN) + '\n' +

                // Format: T{amount}
                'T' + transaction.getSignedAmount() + '\n' +

                // Format: N{check number}
                'N' + transaction.getChequeNumber() + '\n' +

                // Format: P{payee}
                'P' + transaction.getPayee() + '\n' +

                // Format: M{memo/description}
                'M' + transaction.getDescription() + '\n' +

                // End of transaction
                '^' + '\n';

        writer.write(sb);
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