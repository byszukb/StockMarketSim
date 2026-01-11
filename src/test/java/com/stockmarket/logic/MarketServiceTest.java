package com.stockmarket.logic;

import com.stockmarket.domain.Order;
import com.stockmarket.domain.Share;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MarketServiceTest {

    private MarketService marketService;
    private Share share;
    private Portfolio buyerPortfolio;
    private Portfolio sellerPortfolio;

    @BeforeEach
    void setUp() {
        share = new Share("AAPL", "Apple", 150.0, 1.0);
        buyerPortfolio = new Portfolio(20000.0);
        sellerPortfolio = new Portfolio(10000.0);
        sellerPortfolio.buyAsset(share, 50, 100.0);

        marketService = new MarketService(new HashMap<>());
    }

    // --- Basic Mechanism Tests ---

    @Test
    void getOrderBooks_ReturnsOrderBooksMap() {
        Map<String, OrderBook> orderBooksMap = new HashMap<>();
        MarketService testService = new MarketService(orderBooksMap);
        
        assertSame(orderBooksMap, testService.getOrderBooks());
    }

    @Test
    void getOrderBooks_ReturnsMapWithAddedOrderBooks() {
        Order buyOrder = new Order(share, 10, 150.0, Order.Type.BUY, buyerPortfolio);
        marketService.placeOrder(buyOrder);
        
        Map<String, OrderBook> result = marketService.getOrderBooks();
        
        assertTrue(result.containsKey("AAPL"));
    }

    @Test
    void placeOrder_AddsOrderBookForNewAsset() {
        Order buyOrder = new Order(share, 10, 150.0, Order.Type.BUY, buyerPortfolio);
        Map<String, OrderBook> orderBooksMap = new HashMap<>();
        MarketService testService = new MarketService(orderBooksMap);
        testService.placeOrder(buyOrder);
        
        assertTrue(orderBooksMap.containsKey("AAPL"));
    }

    @Test
    void placeOrder_AddsOrderToCorrectQueue() {
        Order buyOrder = new Order(share, 10, 150.0, Order.Type.BUY, buyerPortfolio);
        Map<String, OrderBook> orderBooksMap = new HashMap<>();
        MarketService testService = new MarketService(orderBooksMap);
        testService.placeOrder(buyOrder);
        
        assertEquals(1, orderBooksMap.get("AAPL").getBuyOrders().size());
    }

    // --- Full Match Scenario Tests ---
    
    @Test
    void matchTrade_FullMatch_UpdatesBuyerQuantity() {
        placeFullMatchOrders();
        assertEquals(10, buyerPortfolio.getHoldings().get("AAPL").getTotalQuantity());
    }

    @Test
    void matchTrade_FullMatch_UpdatesBuyerCash() {
        placeFullMatchOrders();
        assertEquals(18500.0, buyerPortfolio.getCash(), 0.01);
    }

    @Test
    void matchTrade_FullMatch_UpdatesSellerQuantity() {
        placeFullMatchOrders();
        assertEquals(40, sellerPortfolio.getHoldings().get("AAPL").getTotalQuantity());
    }

    // --- Partial Match Scenario Tests ---
    
    @Test
    void matchTrade_PartialMatch_BuyerQuantityUpdated() {
        placePartialMatchOrders_BuyerResidue();
        assertEquals(5, buyerPortfolio.getHoldings().get("AAPL").getTotalQuantity());
    }

    @Test
    void matchTrade_PartialMatch_BuyerResidueRemainsInBook() {
        Map<String, OrderBook> orderBooksMap = new HashMap<>();
        MarketService testService = new MarketService(orderBooksMap);

        Order sellOrder = new Order(share, 5, 150.0, Order.Type.SELL, sellerPortfolio);
        testService.placeOrder(sellOrder);

        Order buyOrder = new Order(share, 10, 150.0, Order.Type.BUY, buyerPortfolio);
        testService.placeOrder(buyOrder);

        OrderBook book = orderBooksMap.get("AAPL");
        assertEquals(5, book.getBuyOrders().peek().getAmount());
    }

    @Test
    void matchTrade_PartialMatch_SellerQuantityUpdated() {
        placePartialMatchOrders_SellerResidue();
        assertEquals(45, sellerPortfolio.getHoldings().get("AAPL").getTotalQuantity());
    }

    @Test
    void matchTrade_PartialMatch_SellerResidueRemainsInBook() {
        Map<String, OrderBook> orderBooksMap = new HashMap<>();
        MarketService testService = new MarketService(orderBooksMap);

        Order buyOrder = new Order(share, 5, 150.0, Order.Type.BUY, buyerPortfolio);
        testService.placeOrder(buyOrder);

        Order sellOrder = new Order(share, 10, 150.0, Order.Type.SELL, sellerPortfolio);
        testService.placeOrder(sellOrder);

        OrderBook book = orderBooksMap.get("AAPL");
        assertEquals(5, book.getSellOrders().peek().getAmount());
    }

    // --- No Trade / Spread Scenario Tests ---

    @Test
    void matchTrade_Spread_NoBuyerUpdate() {
        placeSpreadOrders();
        assertFalse(buyerPortfolio.getHoldings().containsKey("AAPL"));
    }

    @Test
    void matchTrade_Spread_NoSellerUpdate() {
        placeSpreadOrders();
        assertEquals(50, sellerPortfolio.getHoldings().get("AAPL").getTotalQuantity());
    }

    // --- Trade Price (Maker/Taker) Tests ---
    
    @Test
    void matchTrade_IncomingBuyOrder_TradePriceIsSellPrice() {
        Order sellOrder = new Order(share, 10, 140.0, Order.Type.SELL, sellerPortfolio);
        marketService.placeOrder(sellOrder);

        Order buyOrder = new Order(share, 10, 150.0, Order.Type.BUY, buyerPortfolio);
        marketService.placeOrder(buyOrder);

        double amountPaid = 20000.0 - buyerPortfolio.getCash();
        assertEquals(1400.0, amountPaid, 0.01);
    }

    @Test
    void matchTrade_IncomingSellOrder_TradePriceIsBuyPrice() {
        Order buyOrder = new Order(share, 10, 160.0, Order.Type.BUY, buyerPortfolio);
        marketService.placeOrder(buyOrder);

        Order sellOrder = new Order(share, 10, 150.0, Order.Type.SELL, sellerPortfolio);
        marketService.placeOrder(sellOrder);

        double amountPaid = 20000.0 - buyerPortfolio.getCash();
        assertEquals(1600.0, amountPaid, 0.01);
    }

    // --- Helpers ---

    private void placeFullMatchOrders() {
        Order sellOrder = new Order(share, 10, 150.0, Order.Type.SELL, sellerPortfolio);
        marketService.placeOrder(sellOrder);

        Order buyOrder = new Order(share, 10, 150.0, Order.Type.BUY, buyerPortfolio);
        marketService.placeOrder(buyOrder);
    }

    private void placePartialMatchOrders_BuyerResidue() {
        Order sellOrder = new Order(share, 5, 150.0, Order.Type.SELL, sellerPortfolio);
        marketService.placeOrder(sellOrder);

        Order buyOrder = new Order(share, 10, 150.0, Order.Type.BUY, buyerPortfolio);
        marketService.placeOrder(buyOrder);
    }

    private void placePartialMatchOrders_SellerResidue() {
        Order buyOrder = new Order(share, 5, 150.0, Order.Type.BUY, buyerPortfolio);
        marketService.placeOrder(buyOrder);

        Order sellOrder = new Order(share, 10, 150.0, Order.Type.SELL, sellerPortfolio);
        marketService.placeOrder(sellOrder);
    }

    private void placeSpreadOrders() {
        Order sellOrder = new Order(share, 10, 160.0, Order.Type.SELL, sellerPortfolio);
        marketService.placeOrder(sellOrder);

        Order buyOrder = new Order(share, 10, 140.0, Order.Type.BUY, buyerPortfolio);
        marketService.placeOrder(buyOrder);
    }
}
