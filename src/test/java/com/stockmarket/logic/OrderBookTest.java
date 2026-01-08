package com.stockmarket.logic;

import com.stockmarket.domain.Order;
import com.stockmarket.domain.Share;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.*;

public class OrderBookTest {

    private OrderBook orderBook;
    private Share share;
    private Portfolio portfolio;

    @BeforeEach
    void setUp() {
        share = new Share("AAPL", "Apple", 150.0, 1.0);
        portfolio = new Portfolio(10000.0);
        orderBook = new OrderBook("AAPL");
    }

    // --- Initialization Tests ---

    @Test
    void constructor_SetsAssetUniqueId() {
        assertEquals("AAPL", orderBook.getAssetUniqueId());
    }

    @Test
    void constructor_InitializesBuyQueue() {
        assertNotNull(orderBook.getBuyOrders());
    }

    @Test
    void constructor_InitializesSellQueue() {
        assertNotNull(orderBook.getSellOrders());
    }

    @Test
    void constructor_BuyQueueIsEmptyInitially() {
        assertTrue(orderBook.getBuyOrders().isEmpty());
    }

    @Test
    void constructor_SellQueueIsEmptyInitially() {
        assertTrue(orderBook.getSellOrders().isEmpty());
    }

    // --- Buy Orders Logic (Higher Price = Priority) ---

    @Test
    void buyOrders_HigherPriceHasPriority() {
        Order lowBid = new Order(share, 10, 100.0, Order.Type.BUY, portfolio);
        Order highBid = new Order(share, 10, 110.0, Order.Type.BUY, portfolio);

        PriorityQueue<Order> buyQueue = orderBook.getBuyOrders();
        buyQueue.add(lowBid);
        buyQueue.add(highBid);

        assertEquals(highBid, buyQueue.peek());
    }

    @Test
    void buyOrders_PriorityIndependentOfInsertionOrder() {
        Order lowBid = new Order(share, 10, 100.0, Order.Type.BUY, portfolio);
        Order highBid = new Order(share, 10, 110.0, Order.Type.BUY, portfolio);

        PriorityQueue<Order> buyQueue = orderBook.getBuyOrders();
        buyQueue.add(highBid);
        buyQueue.add(lowBid);

        assertEquals(highBid, buyQueue.peek());
    }

    @Test
    void buyOrders_CorrectOrderAfterPolling() {
        Order bid1 = new Order(share, 10, 100.0, Order.Type.BUY, portfolio);
        Order bid2 = new Order(share, 10, 120.0, Order.Type.BUY, portfolio);
        Order bid3 = new Order(share, 10, 110.0, Order.Type.BUY, portfolio);

        PriorityQueue<Order> buyQueue = orderBook.getBuyOrders();
        buyQueue.add(bid1);
        buyQueue.add(bid2);
        buyQueue.add(bid3);

        List<Order> polledOrders = new ArrayList<>();
        while(!buyQueue.isEmpty()) {
            polledOrders.add(buyQueue.poll());
        }

        List<Order> expected = Arrays.asList(bid2, bid3, bid1);
        assertEquals(expected, polledOrders);
    }

    // --- Sell Orders Logic (Lower Price = Priority) ---

    @Test
    void sellOrders_LowerPriceHasPriority() {
        Order highAsk = new Order(share, 10, 150.0, Order.Type.SELL, portfolio);
        Order lowAsk = new Order(share, 10, 140.0, Order.Type.SELL, portfolio);

        PriorityQueue<Order> sellQueue = orderBook.getSellOrders();
        sellQueue.add(highAsk);
        sellQueue.add(lowAsk);

        assertEquals(lowAsk, sellQueue.peek());
    }

    @Test
    void sellOrders_PriorityIndependentOfInsertionOrder() {
        Order highAsk = new Order(share, 10, 150.0, Order.Type.SELL, portfolio);
        Order lowAsk = new Order(share, 10, 140.0, Order.Type.SELL, portfolio);

        PriorityQueue<Order> sellQueue = orderBook.getSellOrders();
        sellQueue.add(lowAsk);
        sellQueue.add(highAsk);

        assertEquals(lowAsk, sellQueue.peek());
    }

    @Test
    void sellOrders_CorrectOrderAfterPolling() {
        Order ask1 = new Order(share, 10, 150.0, Order.Type.SELL, portfolio);
        Order ask2 = new Order(share, 10, 130.0, Order.Type.SELL, portfolio);
        Order ask3 = new Order(share, 10, 140.0, Order.Type.SELL, portfolio);

        PriorityQueue<Order> sellQueue = orderBook.getSellOrders();
        sellQueue.add(ask1);
        sellQueue.add(ask2);
        sellQueue.add(ask3);

        List<Order> polledOrders = new ArrayList<>();
        while(!sellQueue.isEmpty()) {
            polledOrders.add(sellQueue.poll());
        }

        List<Order> expected = Arrays.asList(ask2, ask3, ask1);
        assertEquals(expected, polledOrders);
    }
}
