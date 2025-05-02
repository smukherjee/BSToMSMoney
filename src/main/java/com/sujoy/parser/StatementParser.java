/**
 *
 */
package com.sujoy.parser;

import org.htmlparser.util.ParserException;

import java.io.IOException;
import java.text.ParseException;

/**
 * @author sujoy
 */
public interface StatementParser {

    void parse(String path, String filename, String ext)
            throws IOException, ParseException, ParserException;

}
