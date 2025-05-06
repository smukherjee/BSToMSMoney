package com.sujoy.converter;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import com.sujoy.model.Transaction;

/**
 * Interface for converting between Transaction objects and various file formats.
 */
public interface TransactionConverter {
    
    /**
     * Convert a transaction to a specific file format and write it to the provided writer.
     * 
     * @param transaction The transaction to convert
     * @param writer The writer to write the converted transaction to
     * @throws IOException If an I/O error occurs
     */
    void writeTransaction(Transaction transaction, Writer writer) throws IOException;
    
    /**
     * Convert a list of transactions to a specific file format and write them to the provided writer.
     * 
     * @param transactions The list of transactions to convert
     * @param writer The writer to write the converted transactions to
     * @throws IOException If an I/O error occurs
     */
    void writeTransactions(List<Transaction> transactions, Writer writer) throws IOException;
}