package com.stockmarket;

import java.util.ArrayList;

public class Portfolio {
    static class StockHolding{
        Stock stock;
        int quantity;
    }

    private double cash;
    private ArrayList<StockHolding> holdings;
    private int holdingsCount;

    public Portfolio(double initialCash) {
        this.cash = initialCash;
        this.holdings = new ArrayList<>(10);
        this.holdingsCount = 0;
    }

    public void addStock(Stock stock, int quantity) {
        for (StockHolding holding : holdings) {
            if (holding.stock.equals(stock)) {
                holding.quantity += quantity;
                return;
            }
        }
        StockHolding newHolding = new StockHolding();
        newHolding.stock = stock;
        newHolding.quantity = quantity;
        holdings.add(newHolding);
        holdingsCount++;
    }

    public double calculateStockValue() {
        double totalValue = 0.0;
        for (int i = 0; i < holdingsCount; i++) {
            StockHolding holding = holdings.get(i);
            totalValue += holding.quantity * holding.stock.getInitialPrice();
        }
        return totalValue;
    }

    public double calculateTotalValue() {
        return calculateStockValue() + cash;
    }

    public int getHoldingsCount() {
        return holdingsCount;
    }

    public int getStockQuantity(Stock stock) {
        for (int i = 0; i < holdingsCount; i++) {
            StockHolding holding = holdings.get(i);
            if (holding.stock.equals(stock)) {
                return holding.quantity;
            }
        }
        return 0;
    }
}
