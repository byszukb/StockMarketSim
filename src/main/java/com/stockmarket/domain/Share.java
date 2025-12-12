package com.stockmarket.domain;

public class Share extends Asset{

    private double handlingFee;

    public Share(String uniqueId, String name, double currentMarketValue, double handlingFee) {
        super(uniqueId, name, currentMarketValue);
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
