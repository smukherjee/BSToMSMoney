package com.sujoy.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class BasicDom {
    public static void main(String[] args) {
        Document doc = parseXmlFile("c:\\temp\\moneytest.xml", false);
        NodeList nl = doc.getElementsByTagName("Bank");
        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            System.out.println(e.getFirstChild());
            System.out.println(e.getAttribute("filepath"));
            System.out.println(e.getAttribute("filename"));

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
