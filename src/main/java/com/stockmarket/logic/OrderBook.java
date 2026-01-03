package com.stockmarket.logic;

import com.stockmarket.domain.Order;

import java.util.PriorityQueue;

public class OrderBook {
    private String assetUniqueId;
    private PriorityQueue<Order> buyOrders;
    private PriorityQueue<Order> sellOrders;

    public OrderBook(String assetUniqueId) {
        this.assetUniqueId = assetUniqueId;
        this.buyOrders = new PriorityQueue<>(new BuyOrderComparator());
        this.sellOrders = new PriorityQueue<>(new SellOrderComparator());
    }

    public String getAssetUniqueId() {
        return assetUniqueId;
    }

    public PriorityQueue<Order> getBuyOrders() {
        return buyOrders;
    }

    public PriorityQueue<Order> getSellOrders() {
        return sellOrders;
    }
}
