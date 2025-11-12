package com.stockmarket;

public class Portfolio {
    static class StockHolding{
        Stock stock;
        int quantity;
    }

    private double cash;
    private StockHolding[] holdings;
    private int holdingsCount;

    public Portfolio(double initialCash) {
        this.cash = initialCash;
        this.holdings = new StockHolding[10];
        this.holdingsCount = 0;
    }

    public void addStock(Stock stock, int quantity) {
        for (int i = 0; i < holdingsCount; i++) {
            if (holdings[i].stock.equals(stock)) {
                holdings[i].quantity += quantity;
                return;
            }
        }
        StockHolding newHolding = new StockHolding();
        newHolding.stock = stock;
        newHolding.quantity = quantity;
        holdings[holdingsCount] = newHolding;
        holdingsCount++;
    }

    public double calculateStockValue() {
        double totalValue = 0.0;
        for (int i = 0; i < holdingsCount; i++) {
            StockHolding holding = holdings[i];
            totalValue += holding.quantity * holding.stock.getInitialPrice();
        }
        return totalValue;
    }

    public double calculateTotalValue() {
        return calculateStockValue() + cash;
    }

    public double getCash() {
        return cash;
    }

    public int getHoldingsCount() {
        return holdingsCount;
    }

    public int getStockQuantity(Stock stock) {
        for (int i = 0; i < holdingsCount; i++) {
            StockHolding holding = holdings[i];
            if (holding.stock.equals(stock)) {
                return holding.quantity;
            }
        }
        return 0;
    }
}
