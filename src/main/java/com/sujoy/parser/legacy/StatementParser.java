/**
 *
 */
package com.sujoy.parser.legacy;

import java.io.IOException;
import java.text.ParseException;

import org.htmlparser.util.ParserException;

/**
 * @author sujoy
 * @deprecated This interface is deprecated in favor of {@link StatementProcessor}.
 * All implementations should be migrated to implement StatementProcessor instead.
 */
@Deprecated
public interface StatementParser {

    void parse(String path, String filename, String ext)
            throws IOException, ParseException, ParserException;

}
