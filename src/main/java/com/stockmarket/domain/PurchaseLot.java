package com.stockmarket.domain;

import java.time.LocalDate;

public class PurchaseLot {
    private LocalDate purchaseDate;
    private double purchasePrice;
    private int quantity;

    public PurchaseLot(LocalDate purchaseDate, double purchasePrice, int quantity) {
        if (purchaseDate == null) {
            throw new IllegalArgumentException("Purchase date cannot be null");
        }
        if (purchasePrice < 0) {
            throw new IllegalArgumentException("Purchase price cannot be negative");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.purchaseDate = purchaseDate;
        this.purchasePrice = purchasePrice;
        this.quantity = quantity;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void partialSale(int soldQuantity) {
        if (soldQuantity <= 0 || soldQuantity > this.quantity) {
            throw new IllegalArgumentException("Invalid sold quantity");
        }
        this.quantity -= soldQuantity;
    }
}
