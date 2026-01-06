package com.stockmarket.logic;

import com.stockmarket.domain.Asset;
import java.util.HashSet;
import java.util.Set;

public class Watchlist {
    private Set<String> watchedAssetIds;

    public Watchlist() {
        this.watchedAssetIds = new HashSet<>();
    }

    public void add(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Cannot watch null asset");
        }
        watchedAssetIds.add(asset.getUniqueId());
    }

    public void remove(String assetId) {
        if (assetId == null) {
            throw new IllegalArgumentException("Asset ID cannot be null");
        }
        watchedAssetIds.remove(assetId);
    }

    public boolean contains(String assetId) {
        return watchedAssetIds.contains(assetId);
    }

    public Set<String> getWatchedAssetIds() {
        return new HashSet<>(watchedAssetIds);
    }
}