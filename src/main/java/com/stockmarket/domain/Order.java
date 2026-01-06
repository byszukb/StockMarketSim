package com.stockmarket.domain;

import com.stockmarket.logic.Portfolio;

public class Order {
    private Asset asset;
    private int amount;
    private double price;
    private Type typeOfOrder;
    private Portfolio portfolio;

    public Order(Asset asset, int amount, double price, Type typeOfOrder, Portfolio portfolio) {
        if(asset == null)
            throw new IllegalArgumentException("Asset cannot be null");
        if(amount <= 0)
            throw new IllegalArgumentException("Amount must be positive");
        if(price <= 0)
            throw new IllegalArgumentException("Price must be positive");
        if(typeOfOrder == null)
            throw new IllegalArgumentException("Type of order cannot be null");
        if(portfolio == null)
            throw new IllegalArgumentException("Portfolio cannot be null");

        this.asset = asset;
        this.amount = amount;
        this.price = price;
        this.typeOfOrder = typeOfOrder;
        this.portfolio = portfolio;
    }

    public Asset getAsset() {
        return asset;
    }

    public int getAmount() {
        return amount;
    }

    public double getPrice() {
        return price;
    }

    public Type getType() {
        return typeOfOrder;
    }
    public Portfolio getPortfolio() {
        return portfolio;
    }

    public enum Type {
        BUY,
        SELL
    }
}
