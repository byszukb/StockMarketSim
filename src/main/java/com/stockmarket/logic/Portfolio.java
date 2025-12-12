package com.stockmarket.logic;

import com.stockmarket.domain.Asset;

import java.util.ArrayList;

public class Portfolio {
    static class AssetHolding{
        Asset asset;
        int amount;
    }

    private double cash;
    private ArrayList<AssetHolding> holdings;

    public Portfolio(double initialCash) {
        if (initialCash < 0) {
            throw new IllegalArgumentException("Initial cash cannot be negative");
        }
        this.cash = initialCash;
        this.holdings = new ArrayList<AssetHolding>();
    }

    public void buyAsset(Asset asset, int amount) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset cannot be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        double cost = asset.calculatePurchaseCost(amount);
        if(cash < cost) throw new InsufficientFundsException("Insufficient Funds");
        cash -= cost;

        for (AssetHolding holding : holdings) {
            if (holding.asset.equals(asset)) {
                holding.amount += amount;
                return;
            }
        }
        AssetHolding newHolding = new AssetHolding();
        newHolding.asset = asset;
        newHolding.amount = amount;
        holdings.add(newHolding);
    }

    public double calculateAssetsValue() {
        double totalValue = 0.0;
        for (AssetHolding holding : holdings) {
            totalValue += holding.asset.calculateMarketValue(holding.amount);
        }
        return totalValue;
    }

    public double calculateTotalValue() {
        return calculateAssetsValue() + cash;
    }

    public double getCash() {
        return cash;
    }
}
