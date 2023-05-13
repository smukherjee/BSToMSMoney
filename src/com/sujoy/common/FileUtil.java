/**
 *
 */
package com.sujoy.common;

import java.io.*;

/**
 * @author sujoy
 */
public class FileUtil {

    /**
     * Method to read a file from file system as FileInputStream
     *
     * @param path
     * @return finputStream
     * @throws FileNotFoundException
     */
//    public static FileInputStream readFile(String path)
//            throws FileNotFoundException {
//        File file = new File(path);
//        FileInputStream finputStream;
//        finputStream = new FileInputStream(file);
//
//        return finputStream;
//    }

    /**
     * Closes BufferedReader and BufferedWriter
     *
     * @param reader
     * @param writer
     */
    public static void closeReaderWriter(BufferedReader reader,
                                         BufferedWriter writer) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
