package com.sujoy.xml;

import com.sujoy.common.ErrorHandler;
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

    /**
     * Extract text from a PDF file and save it to a text file
     * 
     * @param args Command line arguments (not used)
     * @throws Exception If an error occurs
     */
    public static void main(String[] args) throws Exception {
        InputStream is = null;
        FileOutputStream fop = null;

        try {
            // Open the PDF file
            String pdfPath = "/Users/sujoymukherjee/Downloads/1435634927582TzIk1ONiNcyqsBJO.pdf";
            is = new FileInputStream(pdfPath);

            // Parse the PDF
            ContentHandlerDecorator contenthandler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            PDFParser pdfparser = new PDFParser();
            pdfparser.parse(is, contenthandler, metadata, new ParseContext());

            // Create the output text file
            String outputPath = "/Users/sujoymukherjee/Downloads/14.txt";
            File file = new File(outputPath);

            // Create parent directories if they don't exist
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            // Create the file if it doesn't exist
            if (!file.exists()) {
                file.createNewFile();
            }

            // Write the content to the file
            fop = new FileOutputStream(file);
            byte[] contentInBytes = contenthandler.toString().getBytes();
            fop.write(contentInBytes);
            fop.flush();

            System.out.println("Done");
            System.out.println(contenthandler);

        } catch (Exception e) {
            ErrorHandler.logError("Error processing PDF", e);
        } finally {
            // Close resources in finally block
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    ErrorHandler.logWarning("Error closing input stream", e);
                }
            }
            if (fop != null) {
                try {
                    fop.close();
                } catch (Exception e) {
                    ErrorHandler.logWarning("Error closing output stream", e);
                }
            }
        }
    }
}
