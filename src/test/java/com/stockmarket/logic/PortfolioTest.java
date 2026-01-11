package com.stockmarket.logic;

import com.stockmarket.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

public class PortfolioTest {

    private Portfolio portfolio;
    private Share share;
    private Commodity commodity;
    private Currency currency;

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio(10000.0);
        share = new Share("KRU", "Kruk", 100.0, 5.0);
        commodity = new Commodity("GOLD", "Gold", 100.0, 0.1);
        currency = new Currency("CNY", "Chinese Yuan", 100.0, 2.0);
    }

    // --- Validation Tests ---
    
    @Test
    void constructor_ZeroInitialCash_IsAllowed() {
        Portfolio poor = new Portfolio(0.0);
        assertEquals(0.0, poor.getCash(), 0.01);
    }

    @Test
    void buyAsset_NullAsset_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> portfolio.buyAsset(null, 10, 100.0));
    }

    @Test
    void buyAsset_NegativeAmount_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> portfolio.buyAsset(share, -5, 100.0));
    }

    @Test
    void buyAsset_ZeroAmount_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> portfolio.buyAsset(share, 0, 100.0));
    }
    
    @Test
    void buyAsset_NegativePrice_ThrowsException() {
         assertThrows(IllegalArgumentException.class, () -> portfolio.buyAsset(share, 10, -50.0));
    }

    @Test
    void constructor_NegativeInitialCash_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Portfolio(-100.0));
    }

    @Test
    void sellAsset_NullAsset_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> portfolio.sellAsset(null, 10, 100.0));
    }

    @Test
    void sellAsset_NegativeAmount_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> portfolio.sellAsset(share, -5, 100.0));
    }

    @Test
    void sellAsset_AssetNotOwned_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> portfolio.sellAsset(share, 5, 100.0));
    }

    @Test
    void sellAsset_NotEnoughQuantity_ThrowsException() {
        portfolio.buyAsset(share, 10, 100.0);
        assertThrows(IllegalArgumentException.class, () -> portfolio.sellAsset(share, 15, 110.0));
    }

    @Test
    void buyAsset_InsufficientFunds_ThrowsException() {
        Portfolio poorPortfolio = new Portfolio(50.0);
        assertThrows(InsufficientFundsException.class, () -> poorPortfolio.buyAsset(share, 1, 100.0));
    }

    // --- Buying Logic Tests ---

    @Test
    void buyAsset_SinglePurchase_UpdatesCash() {
        portfolio.buyAsset(share, 10, 100.0); 
        assertEquals(9000.0, portfolio.getCash(), 0.01);
    }

    @Test
    void buyAsset_SinglePurchase_UpdatesHoldingsQuantity() {
        portfolio.buyAsset(share, 10, 100.0);
        assertEquals(10, portfolio.getHoldings().get(share.getUniqueId()).getTotalQuantity());
    }

    @Test
    void buyAsset_MultiplePurchasesSameAsset_IncreasesTotalQuantity() {
        portfolio.buyAsset(share, 10, 100.0);
        portfolio.buyAsset(share, 5, 110.0);
        assertEquals(15, portfolio.getHoldings().get(share.getUniqueId()).getTotalQuantity());
    }
    
    @Test
    void buyAsset_MultiplePurchasesSameAsset_CreatesNewLot() {
        portfolio.buyAsset(share, 10, 100.0);
        portfolio.buyAsset(share, 5, 110.0);
        assertEquals(2, portfolio.getHoldings().get(share.getUniqueId()).getPurchaseLots().size());
    }
    
    // --- AssetHolding Tests ---
    
    @Test
    void assetHolding_getAsset_ReturnsCorrectAsset() {
        portfolio.buyAsset(share, 10, 100.0);
        Portfolio.AssetHolding holding = portfolio.getHoldings().get(share.getUniqueId());
        
        assertEquals(share, holding.getAsset());
    }

    // --- FIFO & Selling Logic Tests ---

    @Test
    void sellAsset_FullSaleSingleLot_UpdatesCash() {
        portfolio.buyAsset(share, 10, 100.0); 
        portfolio.sellAsset(share, 10, 150.0); 
        
        assertEquals(10500.0, portfolio.getCash(), 0.01);
    }

    @Test
    void sellAsset_FullSaleSingleLot_RemovesAssetFromHoldings() {
        portfolio.buyAsset(share, 10, 100.0);
        portfolio.sellAsset(share, 10, 150.0);
        
        assertFalse(portfolio.getHoldings().containsKey(share.getUniqueId()));
    }

    @Test
    void sellAsset_PartialSaleSingleLot_UpdatesQuantity() {
        portfolio.buyAsset(share, 10, 100.0);
        portfolio.sellAsset(share, 4, 150.0);
        
        assertEquals(6, portfolio.getHoldings().get(share.getUniqueId()).getTotalQuantity());
    }

    @Test
    void sellAsset_PartialSaleSingleLot_UpdatesLotQuantity() {
        portfolio.buyAsset(share, 10, 100.0);
        portfolio.sellAsset(share, 4, 150.0);
        
        PurchaseLot remainingLot = portfolio.getHoldings().get(share.getUniqueId()).getPurchaseLots().peek();
        assertEquals(6, remainingLot.getQuantity());
    }

    @Test
    void sellAsset_FIFOMultipleLots_FirstLotConsumed() {
        portfolio.buyAsset(share, 10, 100.0); 
        portfolio.buyAsset(share, 10, 120.0); // Lot 2: 10 units @ 120
        
        portfolio.sellAsset(share, 15, 150.0); // Sell 15 units
        
        Queue<PurchaseLot> lots = portfolio.getHoldings().get(share.getUniqueId()).getPurchaseLots();
        assertEquals(1, lots.size()); // Only Lot 2 remains
    }
    
    @Test
    void sellAsset_FIFOMultipleLots_RemainingLotHasCorrectQuantity() {
        portfolio.buyAsset(share, 10, 100.0);
        portfolio.buyAsset(share, 10, 120.0);
        
        portfolio.sellAsset(share, 15, 150.0);
        
        Queue<PurchaseLot> lots = portfolio.getHoldings().get(share.getUniqueId()).getPurchaseLots();
        assertEquals(5, lots.peek().getQuantity());
    }
    
    @Test
    void sellAsset_FIFOMultipleLots_RemainingLotHasCorrectPrice() {
        portfolio.buyAsset(share, 10, 100.0);
        portfolio.buyAsset(share, 10, 120.0);
        
        portfolio.sellAsset(share, 15, 150.0);
        
        Queue<PurchaseLot> lots = portfolio.getHoldings().get(share.getUniqueId()).getPurchaseLots();
        assertEquals(120.0, lots.peek().getPurchasePrice(), 0.01);
    }

    // --- Profit & Loss Tests ---

    @Test
    void sellAsset_GeneratesSaleReport() {
        portfolio.buyAsset(share, 10, 100.0);
        portfolio.sellAsset(share, 10, 150.0);
        
        assertEquals(1, portfolio.getSalesHistory().size());
    }

    @Test
    void sellAsset_ReportHasCorrectRevenue() {
        portfolio.buyAsset(share, 10, 100.0);
        portfolio.sellAsset(share, 10, 150.0);
        
        SaleReport report = portfolio.getSalesHistory().get(0);
        assertEquals(1500.0, report.getTotalRevenue(), 0.01);
    }

    @Test
    void sellAsset_ReportHasCorrectProfit_SingleLot() {
        portfolio.buyAsset(share, 10, 100.0); 
        portfolio.sellAsset(share, 10, 150.0); 
        
        SaleReport report = portfolio.getSalesHistory().get(0);
        assertEquals(500.0, report.getTotalProfitOrLoss(), 0.01);
    }

    @Test
    void sellAsset_ReportHasCorrectProfit_MultipleLotsMixed() {
        portfolio.buyAsset(share, 10, 100.0);
        portfolio.buyAsset(share, 10, 120.0);
        
        portfolio.sellAsset(share, 15, 150.0);
        
        SaleReport report = portfolio.getSalesHistory().get(0);
        assertEquals(650.0, report.getTotalProfitOrLoss(), 0.01);
    }

    // --- Polymorphism & Value Calculation Tests ---
    
    @Test
    void calculateAssetsValue_ShareIncludesHandlingFee() {
        portfolio.buyAsset(share, 10, 100.0); 
        assertEquals(995.0, portfolio.calculateAssetsValue(), 0.01);
    }
    
    @Test
    void calculateAssetsValue_CommodityIncludesStorageCost() {
        portfolio.buyAsset(commodity, 10, 100.0);
        assertEquals(900.0, portfolio.calculateAssetsValue(), 0.01);
    }
    
    @Test
    void calculateAssetsValue_CurrencyIncludesSpread() {
        portfolio.buyAsset(currency, 10, 100.0);
        assertEquals(980.0, portfolio.calculateAssetsValue(), 0.01);
    }
    
    @Test
    void calculateTotalValue_SumOfCashAndAssets() {
        portfolio.buyAsset(share, 10, 100.0);
        assertEquals(9995.0, portfolio.calculateTotalValue(), 0.01);
    }
}
