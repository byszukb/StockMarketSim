package com.stockmarket.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CommodityTest {

    private Commodity commodity;
    private final String uniqueId = "GOLD";
    private final String name = "Gold";
    private final double marketValue = 1800.0;
    private final double storageRate = 0.01; 

    @BeforeEach
    void setUp() {
        commodity = new Commodity(uniqueId, name, marketValue, storageRate);
    }

    // --- Constructor Validation Tests ---

    @Test
    void constructor_NegativeStorageRate_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Commodity(uniqueId, name, marketValue, -0.01));
    }

    // --- Calculation Tests ---

    @Test
    void calculateMarketValue_SubtractsStorageCost() {
        assertEquals(17820.0, commodity.calculateMarketValue(10), 0.001);
    }

    @Test
    void calculatePurchaseCost_IgnoresStorageCost() {
        assertEquals(18000.0, commodity.calculatePurchaseCost(10), 0.001);
    }

    // --- Getter Tests ---

    @Test
    void getType_ReturnsCommodity() {
        assertEquals(AssetType.COMMODITY, commodity.getType());
    }
    
    @Test
    void getStorageCostRate_ReturnsCorrectValue() {
        assertEquals(storageRate, commodity.getStorageCostRate());
    }
}
