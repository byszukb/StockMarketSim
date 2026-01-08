package com.stockmarket.domain;

import com.stockmarket.logic.Portfolio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    private Share share;
    private Portfolio portfolio;
    private Order order;

    @BeforeEach
    void setUp() {
        share = new Share("AAPL", "Apple", 150.0, 1.0);
        portfolio = new Portfolio(10000.0);
        order = new Order(share, 10, 100.0, Order.Type.BUY, portfolio);
    }

    // --- Constructor Validation Tests ---

    @Test
    void constructor_NullAsset_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Order(null, 10, 100.0, Order.Type.BUY, portfolio));
    }

    @Test
    void constructor_ZeroAmount_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Order(share, 0, 100.0, Order.Type.BUY, portfolio));
    }

    @Test
    void constructor_NegativeAmount_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Order(share, -1, 100.0, Order.Type.BUY, portfolio));
    }

    @Test
    void constructor_ZeroPrice_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Order(share, 10, 0.0, Order.Type.BUY, portfolio));
    }

    @Test
    void constructor_NegativePrice_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Order(share, 10, -50.0, Order.Type.BUY, portfolio));
    }

    @Test
    void constructor_NullType_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Order(share, 10, 100.0, null, portfolio));
    }

    @Test
    void constructor_NullPortfolio_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Order(share, 10, 100.0, Order.Type.BUY, null));
    }

    // --- Getter Tests ---

    @Test
    void getAsset_ReturnsCorrectAsset() {
        assertEquals(share, order.getAsset());
    }

    @Test
    void getAmount_ReturnsCorrectAmount() {
        assertEquals(10, order.getAmount());
    }

    @Test
    void getPrice_ReturnsCorrectPrice() {
        assertEquals(100.0, order.getPrice());
    }

    @Test
    void getType_ReturnsCorrectType() {
        assertEquals(Order.Type.BUY, order.getType());
    }

    @Test
    void getPortfolio_ReturnsCorrectPortfolio() {
        assertEquals(portfolio, order.getPortfolio());
    }
}
