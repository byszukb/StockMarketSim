package com.stockmarket.domain;

public class Order {
    private Asset asset;
    private int amount;
    private double price;
    private TypeOfOrder typeOfOrder;

    public Order(Asset asset, int amount, double price, TypeOfOrder typeOfOrder) {
        if(asset == null)
            throw new IllegalArgumentException("Asset cannot be null");
        if(amount <= 0)
            throw new IllegalArgumentException("Amount must be positive");
        if(price <= 0)
            throw new IllegalArgumentException("Price must be positive");
        if(typeOfOrder == null)
            throw new IllegalArgumentException("Type of order cannot be null");

        this.asset = asset;
        this.amount = amount;
        this.price = price;
        this.typeOfOrder = typeOfOrder;
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

    public TypeOfOrder getTypeOfOrder() {
        return typeOfOrder;
    }
}
