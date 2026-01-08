package com.stockmarket.logic;

import com.stockmarket.domain.Share;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class WatchlistTest {

    private Watchlist watchlist;
    private Share share;

    @BeforeEach
    void setUp() {
        watchlist = new Watchlist();
        share = new Share("AAPL", "Apple", 150.0, 1.0);
    }

    // --- Add Operations Tests ---

    @Test
    void add_AddsAssetToWatchlist() {
        watchlist.add(share);
        assertTrue(watchlist.contains(share.getUniqueId()));
    }

    @Test
    void add_NullAsset_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> watchlist.add(null));
    }

    // --- Remove Operations Tests ---

    @Test
    void remove_RemovesAssetFromWatchlist() {
        watchlist.add(share);
        watchlist.remove(share.getUniqueId());
        assertFalse(watchlist.contains(share.getUniqueId()));
    }

    @Test
    void remove_NullId_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> watchlist.remove(null));
    }

    // --- Query Operations Tests ---

    @Test
    void contains_ReturnsTrueIfPresent() {
        watchlist.add(share);
        assertTrue(watchlist.contains("AAPL"));
    }

    @Test
    void contains_ReturnsFalseIfAbsent() {
        assertFalse(watchlist.contains("MSFT"));
    }

    @Test
    void getWatchedAssetIds_ReturnsSetWithCorrectSize() {
        watchlist.add(share);
        Set<String> ids = watchlist.getWatchedAssetIds();
        assertEquals(1, ids.size());
    }

    @Test
    void getWatchedAssetIds_ReturnsSetWithCorrectContent() {
        watchlist.add(share);
        Set<String> ids = watchlist.getWatchedAssetIds();
        assertTrue(ids.contains("AAPL"));
    }

    @Test
    void getWatchedAssetIds_ReturnsIndependentCopy() {
        watchlist.add(share);
        Set<String> ids = watchlist.getWatchedAssetIds();
        ids.clear();
        assertTrue(watchlist.contains("AAPL"));
    }
}
