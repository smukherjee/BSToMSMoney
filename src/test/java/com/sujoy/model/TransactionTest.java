package com.sujoy.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;

public class TransactionTest {

    @Test
    public void testConstructor() {
        // Arrange
        LocalDate date = LocalDate.of(2025, 5, 6);
        BigDecimal amount = new BigDecimal("1000.50");
        Transaction.TransactionType type = Transaction.TransactionType.CREDIT;
        
        // Act
        Transaction transaction = new Transaction(date, amount, type);
        
        // Assert
        assertEquals(date, transaction.getDate());
        assertEquals(amount, transaction.getAmount());
        assertEquals(type, transaction.getType());
        assertEquals("", transaction.getPayee());
        assertEquals("", transaction.getDescription());
        assertEquals("", transaction.getChequeNumber());
    }
    
    @Test
    public void testGetSignedAmount_Credit() {
        // Arrange
        BigDecimal amount = new BigDecimal("1000.50");
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(Transaction.TransactionType.CREDIT);
        
        // Act
        BigDecimal signedAmount = transaction.getSignedAmount();
        
        // Assert
        assertEquals(amount, signedAmount); // Credit should be positive
    }
    
    @Test
    public void testGetSignedAmount_Debit() {
        // Arrange
        BigDecimal amount = new BigDecimal("1000.50");
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(Transaction.TransactionType.DEBIT);
        
        // Act
        BigDecimal signedAmount = transaction.getSignedAmount();
        
        // Assert
        assertEquals(amount.negate(), signedAmount); // Debit should be negative
    }
    
    @Test
    public void testGetFormattedDate() {
        // Arrange
        LocalDate date = LocalDate.of(2025, 5, 6);
        Transaction transaction = new Transaction();
        transaction.setDate(date);
        
        // Act
        String formattedDate = transaction.getFormattedDate("MM/dd/yyyy");
        
        // Assert
        assertEquals("05/06/2025", formattedDate);
    }
    
    @Test
    public void testEquals_SameTransaction() {
        // Arrange
        LocalDate date = LocalDate.of(2025, 5, 6);
        BigDecimal amount = new BigDecimal("1000.50");
        Transaction t1 = new Transaction(date, amount, Transaction.TransactionType.CREDIT);
        t1.setPayee("Test Payee");
        t1.setChequeNumber("12345");
        
        Transaction t2 = new Transaction(date, amount, Transaction.TransactionType.CREDIT);
        t2.setPayee("Test Payee");
        t2.setChequeNumber("12345");
        
        // Act & Assert
        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }
    
    @Test
    public void testEquals_DifferentTransaction() {
        // Arrange
        Transaction t1 = new Transaction(LocalDate.of(2025, 5, 6), 
                                         new BigDecimal("1000.50"),
                                         Transaction.TransactionType.CREDIT);
                                         
        Transaction t2 = new Transaction(LocalDate.of(2025, 5, 7), 
                                         new BigDecimal("1000.50"),
                                         Transaction.TransactionType.CREDIT);
        
        // Act & Assert
        assertNotEquals(t1, t2);
    }
}