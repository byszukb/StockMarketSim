package com.stockmarket;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class StockTest {
    
    private Stock stock;
    private final String SYMBOL = "AAPL";
    private final String NAME = "Apple Inc.";
    private final double INITIAL_PRICE = 150.0;
    
    @BeforeEach
    void setUp() {
        stock = new Stock(SYMBOL, NAME, INITIAL_PRICE);
    }
    
    @Test
    void testStockCreation() {
        Stock newStock = new Stock("GOOGL", "Alphabet Inc.", 2500.0);
    
        assertNotNull(newStock);
        assertEquals("GOOGL", newStock.getSymbol());
        assertEquals("Alphabet Inc.", newStock.getName());
        assertEquals(2500.0, newStock.getInitialPrice());
    }
    
    @Test
    void testGetSymbol() {
        assertEquals(SYMBOL, stock.getSymbol());
    }
    
    @Test
    void testGetName() {
        assertEquals(NAME, stock.getName());
    }
    
    @Test
    void testGetInitialPrice() {
        assertEquals(INITIAL_PRICE, stock.getInitialPrice());
    }
    
    @Test
    void testEqualsWithSameObject() {
        assertTrue(stock.equals(stock));
    }
    
    @Test
    void testEqualsWithNull() {
        assertFalse(stock.equals(null));
    }
    
    @Test
    void testEqualsWithDifferentClass() {
        String differentObject = "not a stock";
        assertFalse(stock.equals(differentObject));
    }
    
    @Test
    void testEqualsWithSameSymbol() {
        Stock stockWithSameSymbol = new Stock(SYMBOL, "Different Name", 200.0);
        assertTrue(stock.equals(stockWithSameSymbol));
    }
    
    @Test
    void testEqualsWithDifferentSymbol() {
        Stock stockWithDifferentSymbol = new Stock("GOOGL", NAME, INITIAL_PRICE);
        assertFalse(stock.equals(stockWithDifferentSymbol));
    }
    
    @Test
    void testHashCodeConsistency() {
        int hashCode1 = stock.hashCode();
        int hashCode2 = stock.hashCode();
        assertEquals(hashCode1, hashCode2);
    }
    
    @Test
    void testHashCodeForSameSymbol() {
        Stock stockWithSameSymbol = new Stock(SYMBOL, "Different Name", 200.0);
        int hashCode1 = stock.hashCode();
        int hashCode2 = stockWithSameSymbol.hashCode();
        assertEquals(hashCode1, hashCode2);
    }
    
    @Test
    void testHashCodeForDifferentSymbol() {
        Stock stockWithDifferentSymbol = new Stock("GOOGL", NAME, INITIAL_PRICE);
        int hashCode1 = stock.hashCode();
        int hashCode2 = stockWithDifferentSymbol.hashCode();
        assertNotEquals(hashCode1, hashCode2);
    }
    
    @Test
    void testEqualsAndHashCodeContract() {
        Stock stock1 = new Stock("MSFT", "Microsoft Corporation", 300.0);
        Stock stock2 = new Stock("MSFT", "Microsoft Corp", 350.0);
        // If two objects are equal, they must have the same hash code
        assertTrue(stock1.equals(stock2));
        assertEquals(stock1.hashCode(), stock2.hashCode());
    }
    
    @Test
    void testStockWithNullSymbol() {
        Stock stockWithNullSymbol = new Stock(null, "Test Company", 100.0);
        assertNull(stockWithNullSymbol.getSymbol());
        assertEquals("Test Company", stockWithNullSymbol.getName());
        assertEquals(100.0, stockWithNullSymbol.getInitialPrice());
    }
    
    @Test
    void testEqualsWithNullSymbol() {
        Stock stockWithNullSymbol1 = new Stock(null, "Company 1", 100.0);
        Stock stockWithNullSymbol2 = new Stock(null, "Company 2", 200.0);
        Stock stockWithSymbol = new Stock("TEST", "Test Company", 100.0);
        assertTrue(stockWithNullSymbol1.equals(stockWithNullSymbol2));
        assertFalse(stockWithNullSymbol1.equals(stockWithSymbol));
        assertFalse(stockWithSymbol.equals(stockWithNullSymbol1));
    }
}
