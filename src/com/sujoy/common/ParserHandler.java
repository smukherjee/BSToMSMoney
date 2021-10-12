package com.sujoy.common;

import com.sujoy.parser.*;


/**
 * @author sujoy
 */
public class ParserHandler {

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
            case KOTAK: {
                return new KOTAKStmtParser();
            }
            case SBI: {
                return new SBIStmtParser();
            }
//		case SYNDICATE: {
//			return new SYNDICATEStmtParser();
//		}
            case Axis: {
                return new AxisStmtParserExcel();
            }
            case DIGIBANK: {
                return new DigiBankStmtParser();
            }

            default:
                break;
        }
        return null;
    }
}
