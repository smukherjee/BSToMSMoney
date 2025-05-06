/**
 *
 */
package com.sujoy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.htmlparser.util.ParserException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sujoy.common.BankName;
import com.sujoy.common.ErrorHandler;
import com.sujoy.converter.MSMoneyConverter;
import com.sujoy.converter.TransactionConverter;
import com.sujoy.model.Transaction;
import com.sujoy.parser.AxisStatementProcessor;
import com.sujoy.parser.BOIStatementProcessor;
import com.sujoy.parser.CanaraBankStatementProcessor;
import com.sujoy.parser.ICICIStatementProcessor;
import com.sujoy.parser.SBIStatementProcessor;
import com.sujoy.parser.StatementProcessor;
import com.sujoy.parser.UnitedBankStatementProcessor;
import com.sujoy.parser.legacy.StatementParser;

/**
 * Main application class that processes bank statements and converts them to MS Money format.
 * 
 */
public class RunParser {

    // Map to store bank name to processor mappings
    private static final Map<BankName, StatementProcessor> PROCESSOR_MAP = new HashMap<>();
    
    static {
        // Register all statement processors
        PROCESSOR_MAP.put(BankName.ICICI, new ICICIStatementProcessor());
        PROCESSOR_MAP.put(BankName.Axis, new AxisStatementProcessor());
        PROCESSOR_MAP.put(BankName.BOI, new BOIStatementProcessor());
        PROCESSOR_MAP.put(BankName.SBI, new SBIStatementProcessor());
        PROCESSOR_MAP.put(BankName.CANARABANK, new CanaraBankStatementProcessor());
        PROCESSOR_MAP.put(BankName.UNITEDBANK, new UnitedBankStatementProcessor());
    }

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
                BankName bankName = BankName.valueOf(bankNameStr);
                
                // Check if we have a processor for this bank
                StatementProcessor processor = PROCESSOR_MAP.get(bankName);
                
                if (processor != null) {
                    // Use new domain model approach
                    processWithDomainModel(processor, converter, filePath, fileName, fileExt);
                } else {
                    // Fall back to legacy approach (should not happen since we've implemented all processors)
                    ErrorHandler.logWarning("No statement processor found for " + bankNameStr + ", using legacy parser");
                    processWithLegacyParser(bankName, filePath, fileName, fileExt);
                }
                
                ErrorHandler.logInfo("Successfully processed " + bankNameStr + " statement: " + fileName);
                
            } catch (IllegalArgumentException ex) {
                ErrorHandler.logError("Invalid bank name: " + bankNameStr, ex);
            } catch (Exception ex) {
                ErrorHandler.logError("Error processing bank statement: " + bankNameStr, ex);
            }
        }
    }

    /**
     * Process a bank statement using the new domain model.
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
    
    /**
     * Process using legacy parser (compatibility mode).
     */
    private static void processWithLegacyParser(BankName bankName, String path, String filename, String ext) 
            throws IOException, ParseException, ParserException {
        
        ErrorHandler.logInfo("Using legacy parser for " + bankName);
        StatementParser parser = com.sujoy.common.legacy.ParserFactory.getParser(bankName);
        parser.parse(path, filename, ext);
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
