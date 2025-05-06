package com.sujoy.common;

import java.util.function.Supplier;

import com.sujoy.parser.AxisStatementProcessor;
import com.sujoy.parser.BOIStatementProcessor;
import com.sujoy.parser.CanaraBankStatementProcessor;
import com.sujoy.parser.ICICIStatementProcessor;
import com.sujoy.parser.SBIStatementProcessor;
import com.sujoy.parser.StatementProcessor;
import com.sujoy.parser.UnitedBankStatementProcessor;

/**
 * Enum representing supported bank types with associated processor factories.
 * 
 * @author sujoy
 */
public enum BankName {
    ICICI("ICICI", ICICIStatementProcessor::new),
    UNITEDBANK("UNITEDBANK", UnitedBankStatementProcessor::new),
    BOI("BOI", BOIStatementProcessor::new), 
    SBI("SBI", SBIStatementProcessor::new),
    Axis("Axis", AxisStatementProcessor::new),
    CANARABANK("CANARABANK", CanaraBankStatementProcessor::new);

    private final String displayName;
    private final Supplier<StatementProcessor> processorSupplier;

    BankName(String displayName, Supplier<StatementProcessor> processorSupplier) {
        this.displayName = displayName;
        this.processorSupplier = processorSupplier;
    }
    
    /**
     * Get the display name for the bank
     * 
     * @return The bank's display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Create a new instance of the appropriate statement processor for this bank
     * 
     * @return A new StatementProcessor instance
     */
    public StatementProcessor createProcessor() {
        return processorSupplier.get();
    }
    
    /**
     * Find a BankName enum value by its string name (case-insensitive)
     * 
     * @param name The bank name to find
     * @return The matching BankName enum value
     * @throws IllegalArgumentException if no match is found
     */
    public static BankName fromString(String name) {
        for (BankName bank : values()) {
            if (bank.displayName.equalsIgnoreCase(name)) {
                return bank;
            }
        }
        throw new IllegalArgumentException("Unknown bank name: " + name);
    }
}
