package com.stockmarket.domain;

public class Share extends Asset{

    private double handlingFee;

    public Share(String uniqueId, String name, double currentMarketValue, double handlingFee) {
        super(uniqueId, name, currentMarketValue);
        if (handlingFee < 0) {
            throw new IllegalArgumentException("Handling fee cannot be negative");
        }
        this.handlingFee = handlingFee;
    }

    @Override
    public double calculateMarketValue(int amount) {
        return (getCurrentMarketValue() * amount) - handlingFee;
    }

    @Override
    public double calculatePurchaseCost(int amount) {
        return (getCurrentMarketValue() * amount) + handlingFee;
    }
}
