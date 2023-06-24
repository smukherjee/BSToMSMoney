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

/**
 * @author sujoy
 */
public class RunParser {


    public static void main(String[] args) {

        ParserHandler handler;
        StatementParser parser;
        Document doc = parseXmlFile("msmoney.xml", false);

        assert doc != null;
        NodeList nl = doc.getElementsByTagName("Bank");
        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            handler = new ParserHandler();
            try {
                parser = handler.getParser(BankName.valueOf(e.getAttribute("name")));
                parser.parse(e.getAttribute("filepath"), e.getAttribute("filename"), e.getAttribute("ext"));
            } catch (Exception ex) {
                ex.printStackTrace();
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
            return factory.newDocumentBuilder().parse(
                    new File(filename));
        } catch (SAXException e) {
            // A parsing error occurred; the xml input is not valid
        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
