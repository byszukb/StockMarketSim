package com.stockmarket.logic;

import com.stockmarket.logic.Portfolio.AssetHolding;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReportGenerator {

    public String generatePortfolioReport(Portfolio portfolio) {
        StringBuilder sb = new StringBuilder();

        sb.append("=== RAPORT PORTFELA ===\n");

        List<AssetHolding> holdingList = new ArrayList<>(portfolio.getHoldings().values());

        holdingList.sort(new AssetComparator());

        for (AssetHolding holding : holdingList) {
            String type = holding.getAsset().getClass().getSimpleName();
            String name = holding.getAsset().getUniqueId();
            int quantity = holding.getTotalQuantity();

            double value = holding.getAsset().calculateMarketValue(quantity);

            sb.append(String.format("[%s] %s: %d szt. | %.2f PLN\n",
                    type, name, quantity, value));
        }

        sb.append("-----------------------\n");
        sb.append(String.format("Gotówka: %.2f PLN\n", portfolio.getCash()));
        sb.append(String.format("Wartość Całkowita: %.2f PLN\n", portfolio.calculateTotalValue()));
        sb.append("=======================\n");

        return sb.toString();
    }

    public String generateSalesReport(Portfolio portfolio) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RAPORT SPRZEDAŻY (HISTORIA) ===\n");

        List<SaleReport> history = portfolio.getSalesHistory();

        if (history.isEmpty()) {
            sb.append("Brak transakcji sprzedaży.\n");
        } else {
            double totalPnL = 0.0;

            for (SaleReport report : history) {
                sb.append(String.format("Aktywo: %s | Sprzedano: %d szt. | Przychód: %.2f | Wynik (P&L): %.2f\n",
                        report.getAssetId(),
                        report.getQuantitySold(),
                        report.getTotalRevenue(),
                        report.getTotalProfitOrLoss()));

                totalPnL += report.getTotalProfitOrLoss();
            }
            sb.append("-----------------------------------\n");
            sb.append(String.format("Całkowity Zysk/Strata ze wszystkich sprzedaży: %.2f\n", totalPnL));
        }

        sb.append("===================================\n");
        return sb.toString();
    }

    public void saveReportToFile(String reportContent, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(reportContent);
        }
    }
}