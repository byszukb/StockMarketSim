package com.stockmarket.logic;

import com.stockmarket.domain.Asset;
import com.stockmarket.domain.Commodity;
import com.stockmarket.domain.Currency;
import com.stockmarket.domain.Share;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    // --- Testy Walidacji / Null Check ---

    @Test
    void testValidation_NullAssetThrowsException() {
        // ewentualnie jak sprawdzam nulla to tez podpisz typ testu
        assertThrows(IllegalArgumentException.class, () -> portfolio.buyAsset(null, 10));
    }

    @Test
    void testValidation_NegativeAmountThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> portfolio.buyAsset(share, -5));
    }

    @Test
    void testValidation_ZeroAmountThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> portfolio.buyAsset(share, 0));
    }

    @Test
    void testValidation_NegativeInitialCashThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Portfolio(-100.0));
    }

    // --- Testy Polimorfizmu ---

    @Test
    void testPolymorphism_DifferentAssetsReturnDifferentValues_List() {
        int amount = 10;
        
        List<Asset> assets = Arrays.asList(share, commodity, currency);
        Set<Double> values = new HashSet<>(); // Przechowuje unikalne wartości

        for (Asset asset : assets) {
            values.add(asset.calculateMarketValue(amount));
        }

        assertEquals(3, values.size());
    }

    @Test
    void testPolymorphism_ShareValueDifferentFromCommodity() {
        int amount = 10;
        assertNotEquals(share.calculateMarketValue(amount), commodity.calculateMarketValue(amount));
    }

    @Test
    void testPolymorphism_ShareValueDifferentFromCurrency() {
        int amount = 10;
        assertNotEquals(share.calculateMarketValue(amount), currency.calculateMarketValue(amount));
    }

    @Test
    void testPolymorphism_CommodityValueDifferentFromCurrency() {
        int amount = 10;
        assertNotEquals(commodity.calculateMarketValue(amount), currency.calculateMarketValue(amount));
    }

    @Test
    void testPolymorphism_PortfolioTotalValueCorrectWithMixedAssets() {
        int amount = 10;
        portfolio.buyAsset(share, amount);
        portfolio.buyAsset(commodity, amount);
        portfolio.buyAsset(currency, amount);

        assertEquals(2875.0, portfolio.calculateAssetsValue(), 0.01);
    }

    // --- Testy Wyjątków ---

    @Test
    void testPurchaseMechanism_InsufficientFundsThrowsException() {
        Portfolio poorPortfolio = new Portfolio(50.0);
        Share expensiveShare = new Share("LPP", "LPP.SA", 100.0, 5.0);
        
        assertThrows(InsufficientFundsException.class, () -> {
            poorPortfolio.buyAsset(expensiveShare, 1);
        });
    }

    @Test
    void testPurchaseMechanism_HiddenCostsPreventPurchase() {
        Portfolio exactPortfolio = new Portfolio(100.0);
        Share deceptiveShare = new Share("GPEC", "GPEC.DE", 98.0, 5.0);

        assertThrows(InsufficientFundsException.class, () -> {
            exactPortfolio.buyAsset(deceptiveShare, 1);
        });
    }

    // --- Testy Logiki Biznesowej ---

    @Test
    void testCommodityDepreciation() {
        int amount = 100;
        portfolio.buyAsset(commodity, amount);
        
        double expectedValue = 9000.0; 
        assertEquals(expectedValue, portfolio.calculateAssetsValue(), 0.01);
    }

    @Test
    void testCurrencySpread() {
        int amount = 100;
        portfolio.buyAsset(currency, amount);
        
        double expectedValue = 9800.0; 
        assertEquals(expectedValue, portfolio.calculateAssetsValue(), 0.01);
    }
    
    @Test
    void testBuyingSameAssetIncreasesAmount() {
        portfolio.buyAsset(share, 10);
        portfolio.buyAsset(share, 5);
        
        double expectedValue = 1495.0;
        assertEquals(expectedValue, portfolio.calculateAssetsValue(), 0.01);
    }

    @Test
    void testPortfolioAudit_AssetsValueCorrect() {
        portfolio.buyAsset(share, 10);
        
        double assetsValue = portfolio.calculateAssetsValue();
        
        assertEquals(995.0, assetsValue, 0.01);
    }

    @Test
    void testPortfolioAudit_CashCorrect() {
        portfolio.buyAsset(share, 10);
        
        assertEquals(8995.0, portfolio.getCash(), 0.01);
    }

    @Test
    void testPortfolioAudit_TotalValueCorrect() {
        portfolio.buyAsset(share, 10);
        
        double totalValue = portfolio.calculateTotalValue();
        
        assertEquals(9990.0, totalValue, 0.01);
    }
}
