/**
 *
 */
package com.sujoy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sujoy.common.BankName;
import com.sujoy.common.handlers.ErrorHandler;
import com.sujoy.converter.MSMoneyConverter;
import com.sujoy.converter.TransactionConverter;
import com.sujoy.model.Transaction;
import com.sujoy.parser.StatementProcessor;

/**
 * Main application class that processes bank statements and converts them to MS Money format.
 */
public class RunParser {

    @SuppressWarnings("UseSpecificCatch")
    public static void main(String[] args) {
        // Create the QIF converter
        TransactionConverter converter = new MSMoneyConverter();

        // Parse the configuration file
        Document doc = parseXmlFile("msmoney.xml", false);

        if (doc == null) {
            ErrorHandler.logError("Failed to load configuration file", new RuntimeException("Document is null"));
            return;
        }

        // Process each bank element
        NodeList nl = doc.getElementsByTagName("Bank");
        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            String bankNameStr = e.getAttribute("name");
            String filePath = e.getAttribute("filepath");
            String fileName = e.getAttribute("filename");
            String fileExt = e.getAttribute("ext");

            try {
                BankName bankName = BankName.fromString(bankNameStr);

                // Create processor directly from the BankName enum
                StatementProcessor processor = bankName.createProcessor();

                // Process the statement
                processWithDomainModel(processor, converter, filePath, fileName, fileExt);
                ErrorHandler.logInfo("Successfully processed " + bankNameStr + " statement: " + fileName);

            } catch (IllegalArgumentException ex) {
                ErrorHandler.logError("Invalid bank name: " + bankNameStr, ex);
            } catch (Exception ex) {
                ErrorHandler.logError("Error processing bank statement: " + bankNameStr, ex);
            }
        }
    }

    /**
     * Process a bank statement using the domain model.
     */
    private static void processWithDomainModel(StatementProcessor processor, TransactionConverter converter,
                                               String path, String filename, String ext)
            throws IOException, ParseException {

        // Extract transactions
        ErrorHandler.logInfo("Processing statement using " + processor.getBankName() + " processor");
        List<Transaction> transactions = processor.processStatement(path, filename, ext);

        // Write to output file
        String outputPath = path + File.separator + "Converted" + filename + ".qif";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            converter.writeTransactions(transactions, writer);
        }

        ErrorHandler.logInfo("Processed " + transactions.size() + " transactions to " + outputPath);
    }

    // Parses an XML file and returns a DOM document.
    // If validating is true, the contents are validated against the DTD
    // specified in the file.
    public static Document parseXmlFile(String filename, boolean validating) {
        try {
            // Create a builder factory
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            factory.setValidating(validating);

            // Create the builder and parse the file
            return factory.newDocumentBuilder().parse(
                    new File(filename));
        } catch (SAXException e) {
            // A parsing error occurred; the xml input is not valid
            ErrorHandler.logError("XML parsing error in file: " + filename, e);
        } catch (ParserConfigurationException e) {
            ErrorHandler.logError("XML parser configuration error for file: " + filename, e);
        } catch (IOException e) {
            ErrorHandler.logError("I/O error reading XML file: " + filename, e);
        }
        return null;
    }
}
