package com.sujoy.common.handlers;

import au.com.bytecode.opencsv.CSVReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CSVFileHandler extends BaseFileHandler {
    private CSVReader reader;
    private BufferedWriter writer;

    public void openFile(String path, String filename, String ext) throws IOException {
        try {
            reader = new CSVReader(new FileReader(path + File.separator + filename
                    + ((!ext.isEmpty()) ? "." + ext : "")));
            writer = createOutputWriter(path, filename);
            writeHeader(writer);
        } catch (IOException e) {
            closeResources();
            throw e;
        }
    }

    public String[] readNext() throws IOException {
        return reader.readNext();
    }

// --Commented out by Inspection START (5/6/2025 6:09 PM):
//    public BufferedWriter getWriter() {
//        return writer;
//    }
// --Commented out by Inspection STOP (5/6/2025 6:09 PM)

    public void closeResources() throws IOException {
        if (reader != null) {
            reader.close();
        }
        if (writer != null) {
            writer.close();
        }
    }
}