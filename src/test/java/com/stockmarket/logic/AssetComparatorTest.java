package com.stockmarket.logic;

import com.stockmarket.domain.Commodity;
import com.stockmarket.domain.Share;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AssetComparatorTest {

    private List<Portfolio.AssetHolding> holdings;
    private Share shareHighValue;
    private Share shareLowValue;
    private Commodity commodityHighValue;

    @BeforeEach
    void setUp() {
        shareHighValue = new Share("S1", "Share1", 100.0, 0.0);
        Portfolio.AssetHolding holdingShareHigh = createHolding(shareHighValue, 10); // 1000

        shareLowValue = new Share("S2", "Share2", 50.0, 0.0);
        Portfolio.AssetHolding holdingShareLow = createHolding(shareLowValue, 10); // 500

        commodityHighValue = new Commodity("C1", "Comm1", 200.0, 0.0);
        Portfolio.AssetHolding holdingCommHigh = createHolding(commodityHighValue, 10); // 2000

        holdings = new ArrayList<>();
        holdings.add(holdingShareLow);
        holdings.add(holdingCommHigh);
        holdings.add(holdingShareHigh);

        Collections.sort(holdings, new AssetComparator());
    }

    // --- Comparator Logic Tests ---

    @Test
    void compare_PutsCommodityFirst() {
        assertEquals(commodityHighValue, holdings.get(0).getAsset()); 
    }

    @Test
    void compare_PutsHighValueShareSecond() {
        assertEquals(shareHighValue, holdings.get(1).getAsset());     
    }

    @Test
    void compare_PutsLowValueShareLast() {
        assertEquals(shareLowValue, holdings.get(2).getAsset());      
    }

    // --- Helpers ---

    private Portfolio.AssetHolding createHolding(com.stockmarket.domain.Asset asset, int quantity) {
        Portfolio.AssetHolding holding = new Portfolio.AssetHolding(asset);
        holding.getPurchaseLots().add(new com.stockmarket.domain.PurchaseLot(java.time.LocalDate.now(), 100.0, quantity));
        return holding;
    }
}
