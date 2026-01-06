package com.stockmarket.logic;

public class SaleReport {
    private double totalProfitOrLoss;
    private double totalRevenue;
    private int quantitySold;
    private String assetId;

    public SaleReport(String assetId, int quantitySold, double totalRevenue, double totalProfitOrLoss) {
        this.assetId = assetId;
        this.quantitySold = quantitySold;
        this.totalRevenue = totalRevenue;
        this.totalProfitOrLoss = totalProfitOrLoss;
    }

    public double getTotalProfitOrLoss() { return totalProfitOrLoss; }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public String getAssetId() {
        return assetId;
    }
}