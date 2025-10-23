package com.stockmarket;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class PortfolioTest {
    
    private Portfolio portfolio;
    private Stock appleStock;
    private Stock googleStock;
    private Stock microsoftStock;
    
    @BeforeEach
    void setUp() {
        portfolio = new Portfolio(1000.0);
        appleStock = new Stock("AAPL", "Apple Inc.", 150.0);
        googleStock = new Stock("GOOGL", "Alphabet Inc.", 2500.0);
        microsoftStock = new Stock("MSFT", "Microsoft Corporation", 300.0);
    }
    
    @Test
    void testEmptyPortfolio() {
        assertEquals(1000.0, portfolio.calculateTotalValue());
        assertEquals(0.0, portfolio.calculateStockValue());
        assertEquals(0, portfolio.getHoldingsCount());
        assertEquals(0, portfolio.getStockQuantity(appleStock));
    }
    
    @Test
    void testAddStockFirstTime() {
        portfolio.addStock(appleStock, 10);
        
        assertEquals(1, portfolio.getHoldingsCount());
        assertEquals(10, portfolio.getStockQuantity(appleStock));
        assertEquals(1500.0, portfolio.calculateStockValue());
        assertEquals(2500.0, portfolio.calculateTotalValue());
    }
    
    @Test
    void testAddSameStockMultipleTimes() {
        portfolio.addStock(appleStock, 5);
        portfolio.addStock(appleStock, 3);
        portfolio.addStock(appleStock, 2);
        
        assertEquals(1, portfolio.getHoldingsCount());
        assertEquals(10, portfolio.getStockQuantity(appleStock));
        assertEquals(1500.0, portfolio.calculateStockValue());
        assertEquals(2500.0, portfolio.calculateTotalValue());
    }
    
    @Test
    void testAddDifferentStocks() {
        portfolio.addStock(appleStock, 5);
        portfolio.addStock(googleStock, 2);
        portfolio.addStock(microsoftStock, 3);
        
        assertEquals(3, portfolio.getHoldingsCount());
        assertEquals(5, portfolio.getStockQuantity(appleStock));
        assertEquals(2, portfolio.getStockQuantity(googleStock));
        assertEquals(3, portfolio.getStockQuantity(microsoftStock));
        
        double expectedStockValue = (5 * 150.0) + (2 * 2500.0) + (3 * 300.0);
        assertEquals(expectedStockValue, portfolio.calculateStockValue());
        assertEquals(1000.0 + expectedStockValue, portfolio.calculateTotalValue());
    }
    
    @Test
    void testGetStockQuantityForNonExistentStock() {
        portfolio.addStock(appleStock, 5);
        
        assertEquals(0, portfolio.getStockQuantity(googleStock));
        assertEquals(0, portfolio.getStockQuantity(microsoftStock));
    }
    
    @Test
    void testMixedStockOperations() {
        portfolio.addStock(appleStock, 10);
        assertEquals(1, portfolio.getHoldingsCount());
        assertEquals(1500.0, portfolio.calculateStockValue());
        
        portfolio.addStock(googleStock, 1);
        assertEquals(2, portfolio.getHoldingsCount());
        assertEquals(4000.0, portfolio.calculateStockValue());
        
        portfolio.addStock(appleStock, 5);
        assertEquals(2, portfolio.getHoldingsCount());
        assertEquals(15, portfolio.getStockQuantity(appleStock));
        assertEquals(1, portfolio.getStockQuantity(googleStock));
        assertEquals(4750.0, portfolio.calculateStockValue());
    }
    
    @Test
    void testZeroQuantityStock() {
        portfolio.addStock(appleStock, 0);
        
        assertEquals(1, portfolio.getHoldingsCount());
        assertEquals(0, portfolio.getStockQuantity(appleStock));
        assertEquals(0.0, portfolio.calculateStockValue());
        assertEquals(1000.0, portfolio.calculateTotalValue());
    }
    
    @Test
    void testNegativeQuantityStock() {
        portfolio.addStock(appleStock, -5);
        
        assertEquals(1, portfolio.getHoldingsCount());
        assertEquals(-5, portfolio.getStockQuantity(appleStock));
        assertEquals(-750.0, portfolio.calculateStockValue());
        assertEquals(250.0, portfolio.calculateTotalValue());
    }
    
    @Test
    void testLargeQuantities() {
        portfolio.addStock(appleStock, 1000);
        portfolio.addStock(googleStock, 100);
        
        assertEquals(2, portfolio.getHoldingsCount());
        assertEquals(1000, portfolio.getStockQuantity(appleStock));
        assertEquals(100, portfolio.getStockQuantity(googleStock));
        
        double expectedStockValue = (1000 * 150.0) + (100 * 2500.0);
        assertEquals(expectedStockValue, portfolio.calculateStockValue());
        assertEquals(1000.0 + expectedStockValue, portfolio.calculateTotalValue());
    }
    
    @Test
    void testSameStockDifferentInstances() {
        Stock appleStock2 = new Stock("AAPL", "Apple Inc.", 150.0);
        
        portfolio.addStock(appleStock, 5);
        portfolio.addStock(appleStock2, 3);
        
        assertEquals(1, portfolio.getHoldingsCount());
        assertEquals(8, portfolio.getStockQuantity(appleStock));
        assertEquals(8, portfolio.getStockQuantity(appleStock2));
        assertEquals(1200.0, portfolio.calculateStockValue());
    }
    
    @Test
    void testPortfolioWithZeroInitialCash() {
        Portfolio zeroCashPortfolio = new Portfolio(0.0);
        zeroCashPortfolio.addStock(appleStock, 2);
        
        assertEquals(1, zeroCashPortfolio.getHoldingsCount());
        assertEquals(2, zeroCashPortfolio.getStockQuantity(appleStock));
        assertEquals(300.0, zeroCashPortfolio.calculateStockValue());
        assertEquals(300.0, zeroCashPortfolio.calculateTotalValue());
    }
    
    @Test
    void testMultipleAdditionsOfSameStock() {
        for (int i = 0; i < 10; i++) {
            portfolio.addStock(appleStock, 1);
        }
        
        assertEquals(1, portfolio.getHoldingsCount());
        assertEquals(10, portfolio.getStockQuantity(appleStock));
        assertEquals(1500.0, portfolio.calculateStockValue());
        assertEquals(2500.0, portfolio.calculateTotalValue());
    }
}
