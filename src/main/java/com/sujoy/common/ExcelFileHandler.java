package com.sujoy.common;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class ExcelFileHandler extends BaseFileHandler {
    private FileInputStream excelFile;
    private Workbook workbook;
    private Sheet datatypeSheet;
    private Iterator<Row> iterator;
    private BufferedWriter writer;

    public void openFile(String path, String filename, String ext) throws IOException {
        try {
            excelFile = new FileInputStream(new File(path + File.separator + filename + "." + ext));
            workbook = new HSSFWorkbook(excelFile);
            datatypeSheet = workbook.getSheetAt(0);
            iterator = datatypeSheet.iterator();
            writer = createOutputWriter(path, filename);
            writeHeader(writer);
        } catch (IOException e) {
            closeResources();
            throw e;
        }
    }

    public Iterator<Row> getRowIterator() {
        return iterator;
    }

    public BufferedWriter getWriter() {
        return writer;
    }

    public void closeResources() throws IOException {
        if (writer != null) {
            writer.close();
        }
        if (workbook != null) {
            workbook.close();
        }
        if (excelFile != null) {
            excelFile.close();
        }
    }
}