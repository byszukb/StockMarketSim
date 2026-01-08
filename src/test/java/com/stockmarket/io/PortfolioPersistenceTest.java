package com.stockmarket.io;

import com.stockmarket.domain.*;
import com.stockmarket.logic.Portfolio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

public class PortfolioPersistenceTest {

    private PortfolioPersistenceManager persistenceManager;
    private Portfolio portfolio;
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        persistenceManager = new PortfolioPersistenceManager();
        portfolio = new Portfolio(20000.0);
        
        Path path = Files.createTempFile("portfolio_test", ".txt");
        tempFile = path.toFile();
    }

    @AfterEach
    void tearDown() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    // --- Correct Save/Load Tests ---

    @Test
    void saveAndLoadPortfolio_RestoresNotNull() throws IOException {
        saveStandardPortfolio();
        Portfolio loadedPortfolio = persistenceManager.loadPortfolio(tempFile.getAbsolutePath());
        assertNotNull(loadedPortfolio);
    }

    @Test
    void saveAndLoadPortfolio_RestoresCorrectCash() throws IOException {
        saveStandardPortfolio();
        Portfolio loadedPortfolio = persistenceManager.loadPortfolio(tempFile.getAbsolutePath());
        assertEquals(portfolio.getCash(), loadedPortfolio.getCash(), 0.01);
    }

    @Test
    void saveAndLoadPortfolio_RestoresCorrectHoldingsSize() throws IOException {
        saveStandardPortfolio();
        Portfolio loadedPortfolio = persistenceManager.loadPortfolio(tempFile.getAbsolutePath());
        assertEquals(3, loadedPortfolio.getHoldings().size());
    }

    @Test
    void saveAndLoadPortfolio_RestoresShareHolding() throws IOException {
        saveStandardPortfolio();
        Portfolio loadedPortfolio = persistenceManager.loadPortfolio(tempFile.getAbsolutePath());
        assertTrue(loadedPortfolio.getHoldings().containsKey("AAPL"));
    }

    @Test
    void saveAndLoadPortfolio_RestoresCommodityHolding() throws IOException {
        saveStandardPortfolio();
        Portfolio loadedPortfolio = persistenceManager.loadPortfolio(tempFile.getAbsolutePath());
        assertTrue(loadedPortfolio.getHoldings().containsKey("GOLD"));
    }

    @Test
    void saveAndLoadPortfolio_RestoresCurrencyHolding() throws IOException {
        saveStandardPortfolio();
        Portfolio loadedPortfolio = persistenceManager.loadPortfolio(tempFile.getAbsolutePath());
        assertTrue(loadedPortfolio.getHoldings().containsKey("USD"));
    }

    @Test
    void saveAndLoadPortfolio_RestoresCorrectNumberOfPurchaseLots() throws IOException {
        saveMultiLotPortfolio();
        Portfolio loadedPortfolio = persistenceManager.loadPortfolio(tempFile.getAbsolutePath());
        
        Queue<PurchaseLot> lots = loadedPortfolio.getHoldings().get("MSFT").getPurchaseLots();
        assertEquals(2, lots.size());
    }

    @Test
    void saveAndLoadPortfolio_RestoresFirstLotQuantity() throws IOException {
        saveMultiLotPortfolio();
        Portfolio loadedPortfolio = persistenceManager.loadPortfolio(tempFile.getAbsolutePath());
        
        PurchaseLot lot1 = loadedPortfolio.getHoldings().get("MSFT").getPurchaseLots().peek();
        assertEquals(10, lot1.getQuantity());
    }

    @Test
    void saveAndLoadPortfolio_RestoresFirstLotPrice() throws IOException {
        saveMultiLotPortfolio();
        Portfolio loadedPortfolio = persistenceManager.loadPortfolio(tempFile.getAbsolutePath());
        
        PurchaseLot lot1 = loadedPortfolio.getHoldings().get("MSFT").getPurchaseLots().peek();
        assertEquals(250.0, lot1.getPurchasePrice(), 0.01);
    }
    
    // --- Error Handling & Edge Cases Tests ---
    
    @Test
    void loadPortfolio_EmptyFile_ThrowsException() throws IOException {
        // Empty file created in setUp
        assertThrows(DataIntegrityException.class, () -> {
            persistenceManager.loadPortfolio(tempFile.getAbsolutePath());
        });
    }

    @Test
    void loadPortfolio_MissingHeader_ThrowsException() throws IOException {
        String content = "ASSET,SHARE,AAPL,Apple,150.0,1.0,10\n"; 
        Files.write(tempFile.toPath(), content.getBytes());
        
        assertThrows(DataIntegrityException.class, () -> {
            persistenceManager.loadPortfolio(tempFile.getAbsolutePath());
        });
    }

    @Test
    void loadPortfolio_CorruptedFile_ThrowsException() throws IOException {
        String content = "HEADER,CASH,10000.0\n" +
                         "ASSET,SHARE,AAPL,Apple,150.0,1.0,10\n" + 
                         "LOT," + LocalDate.now() + ",150.0,5\n"; // Actual 5, Expected 10
        
        Files.write(tempFile.toPath(), content.getBytes());
        
        assertThrows(DataIntegrityException.class, () -> {
            persistenceManager.loadPortfolio(tempFile.getAbsolutePath());
        });
    }

    @Test
    void loadPortfolio_CorruptedFile_MiddleAssetMismatch_ThrowsException() throws IOException {
        String content = "HEADER,CASH,10000.0\n" +
                         "ASSET,SHARE,AAPL,Apple,150.0,1.0,10\n" + 
                         "LOT," + LocalDate.now() + ",150.0,5\n" +  
                         "ASSET,COMMODITY,GOLD,Gold,1800.0,0.05,5\n" + 
                         "LOT," + LocalDate.now() + ",1800.0,5\n";
        
        Files.write(tempFile.toPath(), content.getBytes());
        
        assertThrows(DataIntegrityException.class, () -> {
            persistenceManager.loadPortfolio(tempFile.getAbsolutePath());
        });
    }

    @Test
    void loadPortfolio_UnknownAssetType_PreservesCash() throws IOException {
        String content = "HEADER,CASH,10000.0\n" +
                         "ASSET,CRYPTO,BTC,Bitcoin,30000.0,0.0,1\n" + 
                         "LOT," + LocalDate.now() + ",30000.0,1\n";   
        
        Files.write(tempFile.toPath(), content.getBytes());
        
        Portfolio loaded = persistenceManager.loadPortfolio(tempFile.getAbsolutePath());
        assertEquals(10000.0, loaded.getCash(), 0.01);
    }

    @Test
    void loadPortfolio_UnknownAssetType_DoesNotAddUnknownAsset() throws IOException {
        String content = "HEADER,CASH,10000.0\n" +
                         "ASSET,CRYPTO,BTC,Bitcoin,30000.0,0.0,1\n" + 
                         "LOT," + LocalDate.now() + ",30000.0,1\n";   
        
        Files.write(tempFile.toPath(), content.getBytes());
        
        Portfolio loaded = persistenceManager.loadPortfolio(tempFile.getAbsolutePath());
        assertFalse(loaded.getHoldings().containsKey("BTC"));
    }

    @Test
    void loadPortfolio_UnknownHeaderTag_Ignored() throws IOException {
        String content = "HEADER,CASH,10000.0\n" +
                         "GARBAGE,DATA,IGNORED\n" +
                         "HEADER,UNKNOWN,TAG\n"; 
        
        Files.write(tempFile.toPath(), content.getBytes());
        
        Portfolio loaded = persistenceManager.loadPortfolio(tempFile.getAbsolutePath());
        
        assertEquals(10000.0, loaded.getCash(), 0.01);
    }

    @Test
    void loadPortfolio_ZeroQuantityAsset_IsAddedToHoldings() throws IOException {
        String content = "HEADER,CASH,10000.0\n" +
                         "ASSET,SHARE,EMPTY,EmptyShare,100.0,1.0,0\n"; 
        
        Files.write(tempFile.toPath(), content.getBytes());
        
        Portfolio loaded = persistenceManager.loadPortfolio(tempFile.getAbsolutePath());
        
        assertTrue(loaded.getHoldings().containsKey("EMPTY"));
    }

    @Test
    void loadPortfolio_ZeroQuantityAsset_HasZeroQuantity() throws IOException {
        String content = "HEADER,CASH,10000.0\n" +
                         "ASSET,SHARE,EMPTY,EmptyShare,100.0,1.0,0\n"; 
        
        Files.write(tempFile.toPath(), content.getBytes());
        
        Portfolio loaded = persistenceManager.loadPortfolio(tempFile.getAbsolutePath());
        
        assertEquals(0, loaded.getHoldings().get("EMPTY").getTotalQuantity());
    }

    // --- Helpers ---

    private void saveStandardPortfolio() throws IOException {
        Share share = new Share("AAPL", "Apple", 150.0, 1.0);
        Commodity commodity = new Commodity("GOLD", "Gold", 1800.0, 0.05);
        Currency currency = new Currency("USD", "US Dollar", 4.0, 0.02);
        
        portfolio.buyAsset(share, 10, 150.0);
        portfolio.buyAsset(commodity, 5, 1800.0);
        portfolio.buyAsset(currency, 100, 4.0);
        
        persistenceManager.savePortfolio(portfolio, tempFile.getAbsolutePath());
    }

    private void saveMultiLotPortfolio() throws IOException {
        Share share = new Share("MSFT", "Microsoft", 250.0, 1.0);
        
        portfolio.buyAsset(share, 10, 250.0); 
        portfolio.buyAsset(share, 5, 260.0);  
        
        persistenceManager.savePortfolio(portfolio, tempFile.getAbsolutePath());
    }
}
