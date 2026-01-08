package com.stockmarket.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class PurchaseLotTest {

    private PurchaseLot lot;
    private final LocalDate date = LocalDate.now();

    @BeforeEach
    void setUp() {
        lot = new PurchaseLot(date, 100.0, 10);
    }

    // --- Constructor Validation Tests ---

    @Test
    void constructor_NullDate_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new PurchaseLot(null, 100.0, 10));
    }

    @Test
    void constructor_NegativePrice_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new PurchaseLot(LocalDate.now(), -1.0, 10));
    }

    @Test
    void constructor_ZeroQuantity_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new PurchaseLot(LocalDate.now(), 100.0, 0));
    }

    @Test
    void constructor_NegativeQuantity_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new PurchaseLot(LocalDate.now(), 100.0, -5));
    }

    // --- Logic Tests ---

    @Test
    void partialSale_ValidAmount_UpdatesQuantity() {
        lot.partialSale(4);
        assertEquals(6, lot.getQuantity());
    }

    @Test
    void partialSale_ZeroAmount_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> lot.partialSale(0));
    }

    @Test
    void partialSale_NegativeAmount_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> lot.partialSale(-1));
    }

    @Test
    void partialSale_AmountExceedingQuantity_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> lot.partialSale(11));
    }
    
    // --- Getter Tests ---

    @Test
    void getPurchaseDate_ReturnsCorrectDate() {
        assertEquals(date, lot.getPurchaseDate());
    }

    @Test
    void getPurchasePrice_ReturnsCorrectPrice() {
        assertEquals(100.0, lot.getPurchasePrice());
    }

    @Test
    void getQuantity_ReturnsCorrectQuantity() {
        assertEquals(10, lot.getQuantity());
    }
}
