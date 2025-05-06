package com.sujoy.common;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

import com.sujoy.parser.AxisStmtParserExcel;
import com.sujoy.parser.BOIStmtParserCSV;
import com.sujoy.parser.CanaraBankStmtParserCSV;
import com.sujoy.parser.ICICIStmtParserExcel;
import com.sujoy.parser.SBIStmtParser;
import com.sujoy.parser.StatementParser;
import com.sujoy.parser.UnitedBankStmtParserExcel;

/**
 * Factory class for creating bank statement parsers.
 * Uses a map-based approach for improved maintainability and extensibility.
 * 
 * @author sujoy
 */
public class ParserFactory {
    
    private static final Map<BankName, Supplier<StatementParser>> PARSER_MAP = new EnumMap<>(BankName.class);
    
    // Static initialization of the parser map
    static {
        PARSER_MAP.put(BankName.ICICI, ICICIStmtParserExcel::new);
        PARSER_MAP.put(BankName.UNITEDBANK, UnitedBankStmtParserExcel::new);
        PARSER_MAP.put(BankName.BOI, BOIStmtParserCSV::new);
        PARSER_MAP.put(BankName.SBI, SBIStmtParser::new);
        PARSER_MAP.put(BankName.CANARABANK, CanaraBankStmtParserCSV::new);
        PARSER_MAP.put(BankName.Axis, AxisStmtParserExcel::new);
    }
    
    /**
     * Returns a parser instance for the specified bank.
     * 
     * @param bankName The bank name to get a parser for
     * @return A new instance of the appropriate parser
     * @throws IllegalArgumentException if the bank name is not supported
     */
    public static StatementParser getParser(BankName bankName) {
        Supplier<StatementParser> supplier = PARSER_MAP.get(bankName);
        if (supplier == null) {
            throw new IllegalArgumentException("Unsupported bank type: " + bankName);
        }
        return supplier.get();
    }
    
    /**
     * Register a new parser for a bank.
     * This method allows for dynamic registration of new parsers.
     * 
     * @param bankName The bank name
     * @param parserSupplier The supplier function that creates parser instances
     */
    public static void registerParser(BankName bankName, Supplier<StatementParser> parserSupplier) {
        PARSER_MAP.put(bankName, parserSupplier);
    }
}