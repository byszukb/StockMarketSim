package com.stockmarket.logic;

import com.stockmarket.domain.Asset;
import com.stockmarket.domain.PurchaseLot;

import java.util.*;

public class Portfolio {
    public static class AssetHolding{
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

        public Asset getAsset() {
            return asset;
        }

        public Queue<PurchaseLot> getPurchaseLots() {
            return purchaseLots;
        }
    }

    private double cash;
    private Map<String, AssetHolding> holdings;
    private List<SaleReport> salesHistory;

    public Portfolio(double initialCash) {
        if (initialCash < 0) {
            throw new IllegalArgumentException("Initial cash cannot be negative");
        }
        this.cash = initialCash;
        this.holdings = new HashMap<>();
        this.salesHistory = new ArrayList<>();
    }

    public void buyAsset(Asset asset, int amount, double price) {
        if(asset == null) {
            throw new IllegalArgumentException("Asset cannot be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        double cost = amount * price;
        if(cash < cost) throw new InsufficientFundsException("Insufficient Funds");
        cash -= cost;

        if(!holdings.containsKey(asset.getUniqueId())) {
            holdings.put(asset.getUniqueId(), new AssetHolding(asset));
        }

        holdings.get(asset.getUniqueId()).purchaseLots.add(
                new PurchaseLot(java.time.LocalDate.now(), price, amount));
    }

    public void sellAsset(Asset asset, int amountToSell, double price){
        if(asset == null)
            throw new IllegalArgumentException("Asset cannot be null");
        if (amountToSell <= 0)
            throw new IllegalArgumentException("Amount to sell must be positive");
        if(!holdings.containsKey(asset.getUniqueId()))
            throw new IllegalArgumentException("Asset not found in portfolio");

        int totalQuantity = holdings.get(asset.getUniqueId()).getTotalQuantity();
        if(amountToSell > totalQuantity)
            throw new IllegalArgumentException("Not enough asset quantity");

        double totalRevenue = amountToSell * price;
        cash += totalRevenue;

        int initialAmount = amountToSell;
        double totalProfit = 0.0;

        Queue<PurchaseLot> lots = holdings.get(asset.getUniqueId()).purchaseLots;

        while(amountToSell > 0) {
            PurchaseLot lot = lots.peek();
            int quantityFromBatch = Math.min(lot.getQuantity(), amountToSell);
            totalProfit += quantityFromBatch * (price - lot.getPurchasePrice());
            if(lot.getQuantity() <= amountToSell) {
                amountToSell -= lot.getQuantity();
                lots.poll();
            } else {
                lot.partialSale(amountToSell);
                amountToSell = 0;
            }
        }

        if (holdings.get(asset.getUniqueId()).purchaseLots.isEmpty()) {
            holdings.remove(asset.getUniqueId());
        }
        salesHistory.add(new SaleReport(asset.getUniqueId(), initialAmount, totalRevenue, totalProfit));
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

    public Map<String, AssetHolding> getHoldings() {
        return holdings;
    }

    public List<SaleReport> getSalesHistory() {
        return salesHistory;
    }
}