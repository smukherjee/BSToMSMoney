package com.sujoy.parser;

import com.sujoy.common.MSMoney;
import com.sujoy.common.Util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ICICIStmtParserExcelTest {

    private ICICIStmtParserExcel parser;
    private MSMoney mockMSMoney;
    private Row mockRow;
    private Cell mockCell;

    @BeforeEach
    void setUp() {
        parser = new ICICIStmtParserExcel();
        mockMSMoney = mock(MSMoney.class);
        mockRow = mock(Row.class);
        mockCell = mock(Cell.class);
    }

    @Test
    void testProcessRow_InvalidCellType() {
        // Negative scenario: CellType is not STRING
        when(mockCell.getCellType()).thenReturn(CellType.NUMERIC); // Simulating a numeric cell
        when(mockCell.getColumnIndex()).thenReturn(4); // Cheque column
// Mocking the iterator to return the mocked cell
    Iterator<Cell> mockIterator = mock(Iterator.class);
    when(mockIterator.hasNext()).thenReturn(true, false); // First call returns true, second call returns false
    when(mockIterator.next()).thenReturn(mockCell);
    when(mockRow.iterator()).thenReturn(mockIterator);

        boolean result = parser.processRow(mockRow, mockMSMoney);

        assertFalse(result, "Row processing should fail for invalid cell type.");
    }

    @Test
    void testProcessRow_InvalidWithdrawalAmount() {
        // Negative scenario: Withdrawal amount is not a valid double
        String invalidAmount = "INVALID_AMOUNT";
        when(mockCell.getCellType()).thenReturn(CellType.STRING);
        when(mockCell.getColumnIndex()).thenReturn(6); // Withdrawal column
        when(mockCell.getStringCellValue()).thenReturn(invalidAmount);
// Mocking the iterator to return the mocked cell
    Iterator<Cell> mockIterator = mock(Iterator.class);
    when(mockIterator.hasNext()).thenReturn(true, false); // First call returns true, second call returns false
    when(mockIterator.next()).thenReturn(mockCell);
    when(mockRow.iterator()).thenReturn(mockIterator);

        doThrow(NumberFormatException.class).when(Util.class);
        Util.processTransactionAmount(invalidAmount, mockMSMoney, true);

        boolean result = parser.processRow(mockRow, mockMSMoney);

        assertFalse(result, "Row processing should fail for invalid withdrawal amount.");
    }

    @Test
    void testProcessRow_InvalidDepositAmount() {
        // Negative scenario: Deposit amount is not a valid double
        String invalidAmount = "INVALID_AMOUNT";
        when(mockCell.getCellType()).thenReturn(CellType.STRING);
        when(mockCell.getColumnIndex()).thenReturn(7); // Deposit column
        when(mockCell.getStringCellValue()).thenReturn(invalidAmount);
// Mocking the iterator to return the mocked cell
    Iterator<Cell> mockIterator = mock(Iterator.class);
    when(mockIterator.hasNext()).thenReturn(true, false); // First call returns true, second call returns false
    when(mockIterator.next()).thenReturn(mockCell);
    when(mockRow.iterator()).thenReturn(mockIterator);

        doThrow(NumberFormatException.class).when(Util.class);
        Util.processTransactionAmount(invalidAmount, mockMSMoney, false);

        boolean result = parser.processRow(mockRow, mockMSMoney);

        assertFalse(result, "Row processing should fail for invalid deposit amount.");
    }
}