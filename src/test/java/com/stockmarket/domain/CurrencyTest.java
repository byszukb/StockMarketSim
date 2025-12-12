package com.stockmarket.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class CurrencyTest {
    private Currency currency;
    private final String uniqueId = "PLN";
    private final String name = "Polish Zloty";
    private final double currentMarketValue = 4.5;
    private final double spread = 0.05;

    @BeforeEach
    void setUp() {
        currency = new Currency(uniqueId, name, currentMarketValue, spread);
    }

    @Test
    public void shouldCalculateValueForStandardSpread() {
        int amount = 100;
        double expectedValue = (currentMarketValue - spread) * amount;
        assertEquals(expectedValue, currency.calculateMarketValue(amount));
    }

    @Test
    public void shouldCalculateValueForHighSpread() {
        double highSpread = 2;
        double lowCurrentMarketValue = 1.5;
        Currency highSpreadCurrency = new Currency(uniqueId, name, lowCurrentMarketValue, highSpread);
        int amount = 10;
        double expectedValue = (lowCurrentMarketValue - highSpread) * amount;
        assertEquals(expectedValue, highSpreadCurrency.calculateMarketValue(amount));
    }

    @Test
    public void shouldCalculateValueForNoSpread() {
        Currency noSpreadCurrency = new Currency(uniqueId, name, currentMarketValue, 0.0);
        int amount = 50;
        double expectedValue = currentMarketValue * amount;
        assertEquals(expectedValue, noSpreadCurrency.calculateMarketValue(amount));
    }
}
