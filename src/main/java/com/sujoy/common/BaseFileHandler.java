package com.sujoy.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class BaseFileHandler {
    protected static void writeHeader(BufferedWriter writer) throws IOException {
        writer.write("!Type:Bank");
        writer.newLine();
    }

    protected BufferedWriter createOutputWriter(String path, String filename) throws IOException {
        return new BufferedWriter(new FileWriter(path + File.separator + "Converted" + filename + ".qif"));
    }
}