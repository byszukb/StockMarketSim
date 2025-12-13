package com.stockmarket.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class CommodityTest {

    private Commodity commodity;

    private final String uniqueId = "GLD";
    private final String name = "Gold";
    private final double currentMarketValue = 100.0;
    private final double storageCostRate = 0.01;

    @BeforeEach
    void setUp() {
        commodity = new Commodity(uniqueId, name, currentMarketValue, storageCostRate);
    }

    @Test
    public void shouldCalculateValueForStandardAmount() {
        int amount = 10;
        double expectedValue = (currentMarketValue * amount) - (currentMarketValue * amount * storageCostRate);
        assertEquals(expectedValue, commodity.calculateMarketValue(amount));
    }

    @Test
    public void shouldCalculateValueForNoStorageCost() {
        Commodity noStorageCostCommodity = new Commodity(uniqueId, name, currentMarketValue, 0.0);
        int amount = 2;
        double expectedValue = currentMarketValue * amount;
        assertEquals(expectedValue, noStorageCostCommodity.calculateMarketValue(amount));
    }

    @Test
    public void shouldCalculateValueForHighStorageCost() {
        double highStorageCostRate = 0.5;
        Commodity highCostCommodity = new Commodity(uniqueId, name, currentMarketValue, highStorageCostRate);
        int amount = 5;
        double expectedValue = (currentMarketValue * amount) - (currentMarketValue * amount * highStorageCostRate);
        assertEquals(expectedValue, highCostCommodity.calculateMarketValue(amount));
    }

    @Test
    public void shouldThrowExceptionWhenStorageCostIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Commodity(uniqueId, name, currentMarketValue, -0.1));
    }
}
