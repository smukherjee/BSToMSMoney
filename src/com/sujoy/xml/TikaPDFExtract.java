package com.sujoy.xml;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ContentHandlerDecorator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class TikaPDFExtract {

    public static void main(String[] args) throws Exception {

        InputStream is = null;
        try {

            is = new FileInputStream("/Users/sujoymukherjee/Downloads/1435634927582TzIk1ONiNcyqsBJO.pdf");
            ContentHandlerDecorator contenthandler = new BodyContentHandler();
            //ToXMLContentHandler contenthandler = new ToXMLContentHandler();
            Metadata metadata = new Metadata();
            PDFParser pdfparser = new PDFParser();
            pdfparser.parse(is, contenthandler, metadata, new ParseContext());


            File file = new File("/Users/sujoymukherjee/Downloads/14.txt");

            FileOutputStream fop = new FileOutputStream(file);

            // if file doesn't exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // get the content in bytes
            byte[] contentInBytes = contenthandler.toString().getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();

            System.out.println("Done");

            System.out.println(contenthandler);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) is.close();
        }
    }
}
