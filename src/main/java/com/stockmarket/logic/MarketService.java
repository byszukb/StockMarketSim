package com.stockmarket.logic;

import com.stockmarket.domain.Order;

import java.util.HashMap;
import java.util.Map;

public class MarketService {
    private Map<String, OrderBook> orderBooks = new HashMap<>();

    public MarketService(Map<String, OrderBook> orderBooks) {
        this.orderBooks = orderBooks;
    }

    public void placeOrder(Order order) {
        String assetId = order.getAsset().getUniqueId();
        OrderBook orderBook = orderBooks.get(assetId);
        if (orderBook == null) {
            orderBook = new OrderBook(assetId);
            orderBooks.put(assetId, orderBook);
        }

        if (order.getType() == Order.Type.BUY) {
            orderBook.getBuyOrders().add(order);
        } else {
            orderBook.getSellOrders().add(order);
        }

        matchTrade(order);
    }

    private void matchTrade(Order incomingOrder) {
        String assetId = incomingOrder.getAsset().getUniqueId();
        OrderBook orderBook = orderBooks.get(assetId);

        while(!orderBook.getBuyOrders().isEmpty() && !orderBook.getSellOrders().isEmpty()) {
            Order highestBuy = orderBook.getBuyOrders().peek();
            Order lowestSell = orderBook.getSellOrders().peek();

            if(highestBuy.getPrice() >= lowestSell.getPrice()) {

                double tradePrice;
                if (incomingOrder.getType() == Order.Type.BUY) {
                    tradePrice = lowestSell.getPrice();
                } else {
                    tradePrice = highestBuy.getPrice();
                }

                int tradeAmount = Math.min(highestBuy.getAmount(), lowestSell.getAmount());
                Portfolio buyerPortfolio = highestBuy.getPortfolio();
                Portfolio sellerPortfolio = lowestSell.getPortfolio();

                buyerPortfolio.buyAsset(highestBuy.getAsset(), tradeAmount, tradePrice);
                sellerPortfolio.sellAsset(lowestSell.getAsset(), tradeAmount, tradePrice);

                if(highestBuy.getAmount() > tradeAmount) {
                    orderBook.getBuyOrders().poll();
                    orderBook.getBuyOrders().add(new Order(highestBuy.getAsset(), highestBuy.getAmount() - tradeAmount, highestBuy.getPrice(), Order.Type.BUY, highestBuy.getPortfolio()));
                } else {
                    orderBook.getBuyOrders().poll();
                }

                if(lowestSell.getAmount() > tradeAmount) {
                    orderBook.getSellOrders().poll();
                    orderBook.getSellOrders().add(new Order(lowestSell.getAsset(), lowestSell.getAmount() - tradeAmount, lowestSell.getPrice(), Order.Type.SELL, lowestSell.getPortfolio()));
                } else {
                    orderBook.getSellOrders().poll();
                }

            } else {
                break;
            }
        }
    }
}
