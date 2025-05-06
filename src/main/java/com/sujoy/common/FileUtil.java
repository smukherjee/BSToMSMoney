/**
 *
 */
package com.sujoy.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

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
     * Closes BufferedReader and BufferedWriter safely, handling any exceptions
     * 
     * @param reader BufferedReader to close
     * @param writer BufferedWriter to close
     */
    @SuppressWarnings("ConvertToTryWithResources")
    public static void closeReaderWriter(BufferedReader reader,
                                         BufferedWriter writer) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException ex) {
                ErrorHandler.logWarning("Error closing reader", ex);
            }
        }

        if (writer != null) {
            try {
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                ErrorHandler.logWarning("Error closing writer", ex);
            }
        }
    }
}
