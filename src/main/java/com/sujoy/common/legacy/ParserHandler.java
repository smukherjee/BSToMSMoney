package com.sujoy.common.legacy;

import com.sujoy.common.BankName;
import com.sujoy.parser.legacy.AxisStmtParserExcel;
import com.sujoy.parser.legacy.BOIStmtParserCSV;
import com.sujoy.parser.legacy.CanaraBankStmtParserCSV;
import com.sujoy.parser.legacy.ICICIStmtParserExcel;
import com.sujoy.parser.legacy.SBIStmtParser;
import com.sujoy.parser.legacy.StatementParser;
import com.sujoy.parser.legacy.UnitedBankStmtParserExcel;



/**
 * @author sujoy
 * @deprecated This handler class is deprecated along with all the legacy parsers it creates.
 * Use the new {@link com.sujoy.parser.StatementProcessor} implementations directly for
 * better transaction handling and modern domain model approach.
 */
@Deprecated
public class ParserHandler {

    /**
     * Returns a parser instance for the specified bank.
     * 
     * @param bankName The bank name to get a parser for
     * @return A new instance of the appropriate parser
     * @deprecated Use the new {@link com.sujoy.parser.StatementProcessor} implementations directly
     */
    @Deprecated
    public StatementParser getParser(BankName bankName) {

        switch (bankName) {

            case ICICI: {
                return new ICICIStmtParserExcel();
            }
            case UNITEDBANK: {
                return new UnitedBankStmtParserExcel();
            }
            case BOI: {
                return new BOIStmtParserCSV();
            }
            case SBI: {
                return new SBIStmtParser();
            }
            case CANARABANK: {
                return new CanaraBankStmtParserCSV();
            }
            case Axis: {
                return new AxisStmtParserExcel();
            }

            default:
                break;
        }
        return null;
    }
}
