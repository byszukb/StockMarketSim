package com.stockmarket.domain;

public class Commodity extends Asset{

    private double storageCostRate;

    public Commodity(String uniqueId, String name, double currentMarketValue, double storageCostRate) {
        super(uniqueId, name, currentMarketValue);
        if (storageCostRate < 0) {
            throw new IllegalArgumentException("Storage cost rate cannot be negative");
        }
        this.storageCostRate = storageCostRate;
    }

    @Override
    public double calculateMarketValue(int amount) {
        double baseValue = getCurrentMarketValue() * amount;
        double storageCost = baseValue * storageCostRate;
        return baseValue - storageCost;
    }

    @Override
    public double calculatePurchaseCost(int amount) {
        return getCurrentMarketValue() * amount;
    }
}
