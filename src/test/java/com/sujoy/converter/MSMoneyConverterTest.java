package com.sujoy.converter;

import com.sujoy.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MSMoneyConverterTest {

    private MSMoneyConverter converter;
    private StringWriter writer;

    @BeforeEach
    void setUp() {
        converter = new MSMoneyConverter();
        writer = new StringWriter();
    }

    @Test
    void testWriteTransaction_Credit() throws IOException {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setDate(LocalDate.of(2025, 5, 6));
        transaction.setAmount(new BigDecimal("1000.50"));
        transaction.setType(Transaction.TransactionType.CREDIT);
        transaction.setPayee("ABC Company");
        transaction.setDescription("Salary payment");
        transaction.setChequeNumber("12345");

        // Act
        converter.writeTransaction(transaction, writer);
        String result = writer.toString();

        // Assert
        String expected = "D05/06/2025\n" +
                "T1000.50\n" +
                "N12345\n" +
                "PABC Company\n" +
                "MSalary payment\n" +
                "^\n";
        assertEquals(expected, result);
    }

    @Test
    void testWriteTransaction_Debit() throws IOException {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setDate(LocalDate.of(2025, 5, 6));
        transaction.setAmount(new BigDecimal("500.25"));
        transaction.setType(Transaction.TransactionType.DEBIT);
        transaction.setPayee("XYZ Store");
        transaction.setDescription("Groceries");

        // Act
        converter.writeTransaction(transaction, writer);
        String result = writer.toString();

        // Assert
        String expected = "D05/06/2025\n" +
                "T-500.25\n" +
                "N\n" +
                "PXYZ Store\n" +
                "MGroceries\n" +
                "^\n";
        assertEquals(expected, result);
    }

    @Test
    void testWriteTransactions() throws IOException {
        // Arrange
        Transaction t1 = new Transaction(
                LocalDate.of(2025, 5, 1),
                new BigDecimal("100.00"),
                Transaction.TransactionType.CREDIT
        );
        t1.setPayee("Company A");

        Transaction t2 = new Transaction(
                LocalDate.of(2025, 5, 2),
                new BigDecimal("50.00"),
                Transaction.TransactionType.DEBIT
        );
        t2.setPayee("Company B");

        List<Transaction> transactions = Arrays.asList(t1, t2);

        // Act
        converter.writeTransactions(transactions, writer);
        String result = writer.toString();

        // Assert
        assertTrue(result.contains("!Type:Bank"));
        assertTrue(result.contains("D05/01/2025"));
        assertTrue(result.contains("T100.00"));
        assertTrue(result.contains("PCompany A"));
        assertTrue(result.contains("D05/02/2025"));
        assertTrue(result.contains("T-50.00"));
        assertTrue(result.contains("PCompany B"));
    }

    @Test
    void testWriteTransactions_EmptyList() throws IOException {
        // Act
        converter.writeTransactions(List.of(), writer);

        // Assert
        assertEquals("", writer.toString());
    }
}