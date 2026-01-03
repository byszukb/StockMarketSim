package com.stockmarket.logic;

import com.stockmarket.domain.Asset;
import com.stockmarket.domain.PurchaseLot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Portfolio {
    static class AssetHolding{
        Asset asset;
        Queue<PurchaseLot> purchaseLots = new LinkedList<>();

        public AssetHolding(Asset asset) {
            this.asset = asset;
        }

        public int getTotalQuantity(){
            int total = 0;
            for(PurchaseLot lot : purchaseLots){
                total += lot.getQuantity();
            }
            return total;
        }
    }

    private double cash;
    private Map<String, AssetHolding> holdings;

    public Portfolio(double initialCash) {
        if (initialCash < 0) {
            throw new IllegalArgumentException("Initial cash cannot be negative");
        }
        this.cash = initialCash;
        this.holdings = new HashMap<>();
    }

    public void buyAsset(Asset asset, int amount) {
        if(asset == null) {
            throw new IllegalArgumentException("Asset cannot be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        double cost = asset.calculatePurchaseCost(amount);
        if(cash < cost) throw new InsufficientFundsException("Insufficient Funds");
        cash -= cost;

        if(!holdings.containsKey(asset.getUniqueId())) {
            holdings.put(asset.getUniqueId(), new AssetHolding(asset));
        }

        holdings.get(asset.getUniqueId()).purchaseLots.add(
                new PurchaseLot(java.time.LocalDate.now(), asset.getCurrentMarketValue(), amount));
    }

    public void sellAsset(Asset asset, int amountToSell){
        if(asset == null)
            throw new IllegalArgumentException("Asset cannot be null");
        if (amountToSell <= 0)
            throw new IllegalArgumentException("Amount to sell must be positive");
        if(!holdings.containsKey(asset.getUniqueId()))
            throw new IllegalArgumentException("Asset not found in portfolio");

        int totalQuantity = holdings.get(asset.getUniqueId()).getTotalQuantity();
        if(amountToSell > totalQuantity)
            throw new IllegalArgumentException("Not enough asset quantity");

        Queue<PurchaseLot> lots = holdings.get(asset.getUniqueId()).purchaseLots;

        while(amountToSell > 0) {
            PurchaseLot lot = lots.peek();
            if(lot.getQuantity() <= amountToSell) {
                amountToSell -= lot.getQuantity();
                lots.remove(lot);
            } else {
                lot.partialSale(amountToSell);
                amountToSell = 0;
            }
        }
    }

    public double calculateAssetsValue() {
        double totalValue = 0.0;
        for(AssetHolding holding : holdings.values()) {
            totalValue += holding.asset.calculateMarketValue(holding.getTotalQuantity());
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
