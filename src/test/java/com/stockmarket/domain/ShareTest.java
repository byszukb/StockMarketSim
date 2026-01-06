package com.stockmarket.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ShareTest {
    private Share share;

    private final String uniqueId = "KRUK";
    private final String name = "Kruk S.A.";
    private final double currentMarketValue = 473.0;
    private final double handlingFee = 5.0;

    @BeforeEach
    public void setUp() {
        share = new Share(uniqueId, name, currentMarketValue, handlingFee);
    }

    // --- Calculation Tests ---

    @Test
    public void shouldCalculateValueForStandardAmount(){
        assertEquals(share.calculateMarketValue(10), (currentMarketValue * 10) - handlingFee);
    }

    @Test
    public void shouldCalculateValueWhereFeeIsSignificant(){
        assertEquals(share.calculateMarketValue(1), (currentMarketValue * 1) - handlingFee);
    }

    @Test
    public void shouldReturnNegativeValueWhenFeeExceedsMarketPrice(){
        double highHandlingFee = 500.0;
        Share expensiveShare = new Share(uniqueId, name, currentMarketValue, highHandlingFee);
        assertEquals(-27.0, expensiveShare.calculateMarketValue(1));
    }
    
    @Test
    public void shouldCalculatePurchaseCost_AddsHandlingFee() {
        assertEquals(4735.0, share.calculatePurchaseCost(10), 0.001);
    }
    
    // --- Validation Tests (Asset & Share) ---

    @Test
    public void shouldThrowExceptionWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Share("ID", null, 100.0, 5.0));
    }

    @Test
    public void shouldThrowExceptionWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Share(null, "Name", 100.0, 5.0));
    }

    @Test
    public void shouldThrowExceptionWhenIdIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Share("   ", "Name", 100.0, 5.0));
    }

    @Test
    public void shouldThrowExceptionWhenNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Share("ID", "   ", 100.0, 5.0));
    }

    @Test
    public void shouldThrowExceptionWhenHandlingFeeIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Share(uniqueId, name, currentMarketValue, -5.0));
    }

    @Test
    public void shouldThrowExceptionWhenMarketValueIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Share(uniqueId, name, -100.0, 5.0));
    }

    // --- Getter & HashCode Tests ---

    @Test
    public void shouldReturnCorrectUniqueId() {
        assertEquals(uniqueId, share.getUniqueId());
    }

    @Test
    public void shouldReturnCorrectName() {
        assertEquals(name, share.getName());
    }

    @Test
    public void shouldReturnCorrectHandlingFee() {
        assertEquals(handlingFee, share.getHandlingFee());
    }

    @Test
    public void shouldReturnCorrectAssetType() {
        assertEquals(AssetType.SHARE, share.getType());
    }

    @Test
    public void shouldReturnCorrectHashCode() {
        assertEquals(uniqueId.hashCode(), share.hashCode());
    }

    // --- Equals Tests (Asset Coverage) ---

    @Test
    public void equals_ReturnsTrueForSameObject() {
        assertTrue(share.equals(share));
    }

    @Test
    public void equals_ReturnsTrueForEqualId() {
        Share otherShare = new Share(uniqueId, "Other Name", 100.0, 1.0);
        assertTrue(share.equals(otherShare));
    }

    @Test
    public void equals_ReturnsFalseForDifferentId() {
        Share otherShare = new Share("OTHER", name, currentMarketValue, handlingFee);
        assertFalse(share.equals(otherShare));
    }

    @Test
    public void equals_ReturnsFalseForNull() {
        assertFalse(share.equals(null));
    }

    @Test
    public void equals_ReturnsFalseForDifferentClass() {
        assertFalse(share.equals("Some String"));
    }
}
