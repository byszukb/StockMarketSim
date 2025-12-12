package com.stockmarket.domain;

public abstract class Asset {
    private String uniqueId;
    private String name;
    private double currentMarketValue;

    public Asset(String uniqueId, String name, double currentMarketValue) {
        if (uniqueId == null || uniqueId.trim().isEmpty()) {
            throw new IllegalArgumentException("Unique ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (currentMarketValue < 0) {
            throw new IllegalArgumentException("Current market value cannot be negative");
        }
        this.uniqueId = uniqueId;
        this.name = name;
        this.currentMarketValue = currentMarketValue;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public int hashCode() {
        return uniqueId == null ? 0 : uniqueId.hashCode();
    }

    // Aktualnie jest to odrobine niebezpieczne - rzutujemy bez sprawdzania typu,
    // ale opis zadania zabrania uzywania instanceof
    @Override
    public boolean equals(Object obj) {
        Asset asset = (Asset) obj;
        return uniqueId.equals(asset.uniqueId);
    }

    public String getName() {
        return name;
    }

    public double getCurrentMarketValue() {
        return currentMarketValue;
    }

    public abstract double calculateMarketValue(int amount);

    public abstract double calculatePurchaseCost(int amount);
}
