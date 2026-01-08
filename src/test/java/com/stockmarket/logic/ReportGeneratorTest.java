package com.stockmarket.logic;

import com.stockmarket.domain.Share;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ReportGeneratorTest {

    private ReportGenerator reportGenerator;
    private Portfolio portfolio;
    private Share share;

    @BeforeEach
    void setUp() {
        reportGenerator = new ReportGenerator();
        portfolio = new Portfolio(10000.0);
        share = new Share("AAPL", "Apple", 150.0, 1.0);
    }

    // --- Portfolio Report Tests ---

    @Test
    void generatePortfolioReport_ContainsHeader() {
        portfolio.buyAsset(share, 10, 100.0);
        String report = reportGenerator.generatePortfolioReport(portfolio);
        assertTrue(report.contains("=== RAPORT PORTFELA ==="));
    }

    @Test
    void generatePortfolioReport_ContainsAssetDetails() {
        portfolio.buyAsset(share, 10, 100.0);
        String report = reportGenerator.generatePortfolioReport(portfolio);
        assertTrue(report.contains("[Share] AAPL: 10 szt."));
    }

    @Test
    void generatePortfolioReport_ContainsCorrectAssetValue() {
        portfolio.buyAsset(share, 10, 100.0);
        String report = reportGenerator.generatePortfolioReport(portfolio);
        assertTrue(report.contains(String.format("%.2f PLN", 1499.00)));
    }

    @Test
    void generatePortfolioReport_ContainsCashInfo() {
        portfolio.buyAsset(share, 10, 100.0);
        String report = reportGenerator.generatePortfolioReport(portfolio);
        assertTrue(report.contains(String.format("Gotówka: %.2f PLN", 9000.00)));
    }

    @Test
    void generatePortfolioReport_EmptyPortfolio_ContainsCash() {
        String report = reportGenerator.generatePortfolioReport(portfolio);
        assertTrue(report.contains(String.format("Gotówka: %.2f PLN", 10000.00)));
    }

    @Test
    void generatePortfolioReport_EmptyPortfolio_ContainsTotalValue() {
        String report = reportGenerator.generatePortfolioReport(portfolio);
        assertTrue(report.contains(String.format("Wartość Całkowita: %.2f PLN", 10000.00)));
    }

    // --- Sales Report Tests ---

    @Test
    void generateSalesReport_ContainsHeader() {
        prepareSale();
        String report = reportGenerator.generateSalesReport(portfolio);
        assertTrue(report.contains("=== RAPORT SPRZEDAŻY"));
    }

    @Test
    void generateSalesReport_ContainsAssetName() {
        prepareSale();
        String report = reportGenerator.generateSalesReport(portfolio);
        assertTrue(report.contains("Aktywo: AAPL"));
    }

    @Test
    void generateSalesReport_ContainsSoldQuantity() {
        prepareSale();
        String report = reportGenerator.generateSalesReport(portfolio);
        assertTrue(report.contains("Sprzedano: 5 szt."));
    }

    @Test
    void generateSalesReport_ContainsRevenue() {
        prepareSale();
        String report = reportGenerator.generateSalesReport(portfolio);
        assertTrue(report.contains(String.format("Przychód: %.2f", 600.00)));
    }

    @Test
    void generateSalesReport_ContainsProfit() {
        prepareSale();
        String report = reportGenerator.generateSalesReport(portfolio);
        assertTrue(report.contains(String.format("Wynik (P&L): %.2f", 100.00)));
    }

    @Test
    void generateSalesReport_ContainsTotalPnL() {
        prepareSale();
        String report = reportGenerator.generateSalesReport(portfolio);
        assertTrue(report.contains(String.format("Całkowity Zysk/Strata ze wszystkich sprzedaży: %.2f", 100.00)));
    }

    @Test
    void generateSalesReport_NoSales_ShowsEmptyMessage() {
        String report = reportGenerator.generateSalesReport(portfolio);
        assertTrue(report.contains("Brak transakcji sprzedaży."));
    }

    // --- File Saving Tests ---

    @Test
    void saveReportToFile_FileIsCreated() throws IOException {
        Path tempFile = Files.createTempFile("test_report", ".txt");
        File file = tempFile.toFile();
        try {
            reportGenerator.saveReportToFile("content", file.getAbsolutePath());
            assertTrue(file.exists());
        } finally {
            file.delete();
        }
    }

    @Test
    void saveReportToFile_ContentIsCorrect() throws IOException {
        String content = "Test Content Report";
        Path tempFile = Files.createTempFile("test_report", ".txt");
        File file = tempFile.toFile();
        try {
            reportGenerator.saveReportToFile(content, file.getAbsolutePath());
            String fileContent = new String(Files.readAllBytes(tempFile));
            assertEquals(content, fileContent);
        } finally {
            file.delete();
        }
    }

    private void prepareSale() {
        portfolio.buyAsset(share, 10, 100.0);
        portfolio.sellAsset(share, 5, 120.0);
    }
}
