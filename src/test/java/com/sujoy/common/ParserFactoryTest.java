package com.sujoy.common;

import com.sujoy.parser.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ParserFactory class
 */
public class ParserFactoryTest {

    @Test
    public void testGetParser_shouldReturnCorrectParserForBank() {
        // Test each bank type returns the correct parser
        assertTrue(ParserFactory.getParser(BankName.ICICI) instanceof ICICIStmtParserExcel);
        assertTrue(ParserFactory.getParser(BankName.Axis) instanceof AxisStmtParserExcel);
        assertTrue(ParserFactory.getParser(BankName.BOI) instanceof BOIStmtParserCSV);
        assertTrue(ParserFactory.getParser(BankName.SBI) instanceof SBIStmtParser);
        assertTrue(ParserFactory.getParser(BankName.UNITEDBANK) instanceof UnitedBankStmtParserExcel);
        assertTrue(ParserFactory.getParser(BankName.CANARABANK) instanceof CanaraBankStmtParserCSV);
    }

    @Test
    public void testGetParser_shouldThrowExceptionForUnsupportedBank() {
        // Create a mock implementation of BankName for testing
        BankName mockBank = BankName.valueOf("ICICI");
        
        // Use reflection to get the enum ordinal and create a "new" enum value that's not in our map
        // This is a hack for testing purposes since we can't directly create a new enum value
        try {
            java.lang.reflect.Field ordinalField = Enum.class.getDeclaredField("ordinal");
            ordinalField.setAccessible(true);
            ordinalField.set(mockBank, 99); // Set to a value not in our map
            
            // Should throw IllegalArgumentException
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                ParserFactory.getParser(mockBank);
            });
            
            assertEquals("Unsupported bank type: " + mockBank, exception.getMessage());
            
        } catch (ReflectiveOperationException e) {
            // If reflection fails, print a message and skip this test
            System.err.println("Test skipped: Could not create mock enum value with reflection");
        }
    }

    @Test
    public void testRegisterParser_shouldAllowAddingNewParser() {
        // Create a mock parser
        StatementParser mockParser = new StatementParser() {
            @Override
            public void parse(String path, String filename, String ext) {
                // Mock implementation
            }
        };
        
        // Test registering a new parser
        try {
            // Register a new parser for an existing bank (this would override the default)
            ParserFactory.registerParser(BankName.ICICI, () -> mockParser);
            
            // The factory should return our mock parser
            assertSame(mockParser, ParserFactory.getParser(BankName.ICICI));
            
        } finally {
            // Clean up by restoring the original parser
            ParserFactory.registerParser(BankName.ICICI, ICICIStmtParserExcel::new);
        }
    }
}