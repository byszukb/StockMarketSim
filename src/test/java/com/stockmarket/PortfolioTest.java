package com.stockmarket;

import java.util.ArrayList;

public class PortfolioTest {
    private static class StockHolding{
        Stock stock;
        int quantity;
    }
    private double cash;
    private ArrayList<StockHolding> holdings;
    private int holdingsCount;
}
