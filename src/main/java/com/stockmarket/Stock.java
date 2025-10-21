package com.stockmarket;

public class Stock {
    private String symbol;
    private String name;
    private double initialPrice;

    public Stock(String symbol, String name, double initialPrice) {
        this.symbol = symbol;
        this.name = name;
        this.initialPrice = initialPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getInitialPrice() {
        return initialPrice;
    }

    @Override
    public int hashCode() {
        return symbol == null ? 0 : symbol.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Stock stock = (Stock) obj;
        if (symbol == null) return stock.symbol == null;
        return symbol.equals(stock.symbol);
    }
}
