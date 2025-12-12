package com.stockmarket.domain;

public class Currency extends Asset{
    private double spread;

    public Currency(String uniqueId, String name, double currentMarketValue, double spread) {
        super(uniqueId, name, currentMarketValue);
        if (spread < 0) {
            throw new IllegalArgumentException("Spread cannot be negative");
        }
        this.spread = spread;
    }

    @Override
    public double calculateMarketValue(int amount) {
        double singleUnitValue = getCurrentMarketValue() - spread;
        return singleUnitValue * amount;
    }

    @Override
    public double calculatePurchaseCost(int amount) {
        return getCurrentMarketValue() * amount;
    }
}
