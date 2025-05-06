package com.sujoy.common;

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
                + ((ext.length() > 0) ? "." + ext : "")));
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

    public BufferedWriter getWriter() {
        return writer;
    }

    public void closeResources() throws IOException {
        if (reader != null) {
            reader.close();
        }
        if (writer != null) {
            writer.close();
        }
    }
}