package com.sujoy.parser;

import com.sujoy.model.Transaction;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Interface for processing bank statements and extracting transactions.
 * This represents the core business logic of parsing bank statements,
 * independent of input/output formats.
 */
public interface StatementProcessor {

    /**
     * Process a bank statement file and extract transactions.
     *
     * @param path     The directory path containing the statement file
     * @param filename The filename without extension
     * @param ext      The file extension
     * @return List of extracted transactions
     * @throws IOException    If an I/O error occurs
     * @throws ParseException If a parsing error occurs
     */
    List<Transaction> processStatement(String path, String filename, String ext)
            throws IOException, ParseException;

    /**
     * Get the bank name associated with this processor
     *
     * @return The bank name
     */
    String getBankName();
}