package com.stockmarket.logic;

import com.stockmarket.domain.Order;
import com.stockmarket.domain.Share;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderComparatorTest {

    private BuyOrderComparator buyComparator;
    private SellOrderComparator sellComparator;
    private Share share;
    private Portfolio portfolio;

    @BeforeEach
    void setUp() {
        buyComparator = new BuyOrderComparator();
        sellComparator = new SellOrderComparator();
        share = new Share("AAPL", "Apple", 150.0, 1.0);
        portfolio = new Portfolio(1000.0);
    }

    // --- Buy Order Comparator Tests ---

    @Test
    void buyComparator_Compare_HigherPrice_ReturnsPositive() {
        Order o1 = new Order(share, 10, 100.0, Order.Type.BUY, portfolio);
        Order o2 = new Order(share, 10, 110.0, Order.Type.BUY, portfolio);

        assertEquals(1, buyComparator.compare(o1, o2));
    }

    @Test
    void buyComparator_Compare_LowerPrice_ReturnsNegative() {
        Order o1 = new Order(share, 10, 100.0, Order.Type.BUY, portfolio);
        Order o2 = new Order(share, 10, 110.0, Order.Type.BUY, portfolio);

        assertEquals(-1, buyComparator.compare(o2, o1));
    }

    @Test
    void buyComparator_EqualPriceReturnsZero() {
        Order o1 = new Order(share, 10, 100.0, Order.Type.BUY, portfolio);
        Order o2 = new Order(share, 5, 100.0, Order.Type.BUY, portfolio);

        assertEquals(0, buyComparator.compare(o1, o2));
    }

    // --- Sell Order Comparator Tests ---

    @Test
    void sellComparator_Compare_LowerPrice_ReturnsNegative() {
        Order o1 = new Order(share, 10, 100.0, Order.Type.SELL, portfolio);
        Order o2 = new Order(share, 10, 110.0, Order.Type.SELL, portfolio);

        assertEquals(-1, sellComparator.compare(o1, o2));
    }

    @Test
    void sellComparator_Compare_HigherPrice_ReturnsPositive() {
        Order o1 = new Order(share, 10, 100.0, Order.Type.SELL, portfolio);
        Order o2 = new Order(share, 10, 110.0, Order.Type.SELL, portfolio);

        assertEquals(1, sellComparator.compare(o2, o1));
    }

    @Test
    void sellComparator_EqualPriceReturnsZero() {
        Order o1 = new Order(share, 10, 100.0, Order.Type.SELL, portfolio);
        Order o2 = new Order(share, 5, 100.0, Order.Type.SELL, portfolio);

        assertEquals(0, sellComparator.compare(o1, o2));
    }
}
