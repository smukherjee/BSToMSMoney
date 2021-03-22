/**
 *
 */
package com.sujoy;

import com.sujoy.common.BankName;
import com.sujoy.common.ParserHandler;
import com.sujoy.parser.StatementParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author sujoy
 *
 */
public class RunParser {

    /**
     * @param args
     */
    /**
     * @param args
     */
    public static void main(String[] args) {

        ParserHandler handler;
        StatementParser parser;
        Document doc = parseXmlFile("msmoney.xml", false);

        try {
            // Create an appending file handler
            boolean append = true;
            FileHandler loghandler = new FileHandler("my.log", append);

            // Add to the desired logger
            Logger logger = Logger.getLogger("com.sujoy");
            logger.addHandler(loghandler);
        } catch (IOException e) {
        }

        NodeList nl = doc.getElementsByTagName("Bank");
        Logger.getLogger("com.msmoney.log").log(Level.ALL,
                doc.getElementsByTagName("Bank").toString());
        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            handler = new ParserHandler();
            try {
                parser = handler.getParser(BankName.valueOf(e
                        .getAttribute("name")));
                parser.parse(e.getAttribute("filepath"),
                        e.getAttribute("filename"), e.getAttribute("ext"));
                Logger.getLogger("com.msmoney.log").log(
                        Level.INFO,
                        "Parsed : - " + e.getAttribute("name") + " - "
                                + e.getAttribute("filename"));
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.getLogger("com.msmoney.log").log(Level.ALL,
                        "ex.printStackTrace()");

            }
        }

    }

    // Parses an XML file and returns a DOM document.
    // If validating is true, the contents is validated against the DTD
    // specified in the file.
    public static Document parseXmlFile(String filename, boolean validating) {
        try {
            // Create a builder factory
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            factory.setValidating(validating);

            // Create the builder and parse the file
            Document doc = factory.newDocumentBuilder().parse(
                    new File(filename));
            return doc;
        } catch (SAXException e) {
            // A parsing error occurred; the xml input is not valid
        } catch (ParserConfigurationException e) {
        } catch (IOException e) {
        }
        return null;
    }

}
