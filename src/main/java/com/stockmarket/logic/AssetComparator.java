package com.stockmarket.logic;

import java.util.Comparator;

public class AssetComparator implements Comparator<Portfolio.AssetHolding> {
    @Override
    public int compare(Portfolio.AssetHolding o1, Portfolio.AssetHolding o2) {
        int compareResult = o1.getAsset().getType().name().compareTo(o2.getAsset().getType().name());
        if (compareResult != 0) {
            return compareResult;
        } else {
            return Double.compare(o2.getAsset().calculateMarketValue(o2.getTotalQuantity()),
                    o1.getAsset().calculateMarketValue(o1.getTotalQuantity()));
        }
    }
}
