package com.stockmarket.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CurrencyTest {

    private Currency currency;
    private final String uniqueId = "USD";
    private final String name = "US Dollar";
    private final double marketValue = 4.0;
    private final double spread = 0.05;

    @BeforeEach
    void setUp() {
        currency = new Currency(uniqueId, name, marketValue, spread);
    }

    // --- Constructor Validation Tests ---

    @Test
    void constructor_NegativeSpread_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Currency(uniqueId, name, marketValue, -0.1));
    }

    // --- Calculation Tests ---

    @Test
    void calculateMarketValue_SubtractsSpread() {
        assertEquals(395.0, currency.calculateMarketValue(100), 0.001);
    }

    @Test
    void calculatePurchaseCost_IgnoresSpread() {
        assertEquals(400.0, currency.calculatePurchaseCost(100), 0.001);
    }

    // --- Getter Tests ---

    @Test
    void getType_ReturnsCurrency() {
        assertEquals(AssetType.CURRENCY, currency.getType());
    }
    
    @Test
    void getSpread_ReturnsCorrectValue() {
        assertEquals(spread, currency.getSpread());
    }
}
