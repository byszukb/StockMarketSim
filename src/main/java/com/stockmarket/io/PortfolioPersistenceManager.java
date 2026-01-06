package com.stockmarket.io;

import com.stockmarket.domain.*;
import com.stockmarket.logic.Portfolio;

import java.io.*;
import java.time.LocalDate;
import java.util.Map;

public class PortfolioPersistenceManager {

    public void savePortfolio(Portfolio portfolio, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {

            writer.write("HEADER,CASH," + portfolio.getCash());
            writer.newLine();

            Map<String, Portfolio.AssetHolding> holdings = portfolio.getHoldings();

            for (Portfolio.AssetHolding holding : holdings.values()) {
                Asset asset = holding.getAsset();
                double specificValue = 0.0;
                String assetType;

                if (asset.getType() == AssetType.SHARE) {
                    Share share = (Share) asset;
                    assetType = AssetType.SHARE.name();
                    specificValue = share.getHandlingFee();
                }
                else if (asset.getType() == AssetType.CURRENCY) {
                    Currency currency = (Currency) asset;
                    assetType = AssetType.CURRENCY.name();
                    specificValue = currency.getSpread();
                }
                else if (asset.getType() == AssetType.COMMODITY) {
                    Commodity commodity = (Commodity) asset;
                    assetType = AssetType.COMMODITY.name();
                    specificValue = commodity.getStorageCostRate();
                }
                else {
                    assetType = "UNKNOWN";
                }

                writer.write("ASSET," + assetType + "," + asset.getUniqueId() + "," + asset.getName() + "," + asset.getCurrentMarketValue() + "," + specificValue + "," + holding.getTotalQuantity());
                writer.newLine();

                for (PurchaseLot lot : holding.getPurchaseLots()) {
                    writer.write("LOT," +
                            lot.getPurchaseDate() + "," +
                            lot.getPurchasePrice() + "," +
                            lot.getQuantity());
                    writer.newLine();
                }
            }
        }
    }

    public Portfolio loadPortfolio(String filename) throws IOException {
        Portfolio portfolio = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            Asset currentAsset = null;
            int expectedQuantity = 0;
            while ((line = reader.readLine()) != null){
                String [] parts = line.split(",");
                switch (parts[0]){
                    case "HEADER":
                        if(parts[1].equals("CASH")){
                            double cash = Double.parseDouble(parts[2]);
                            portfolio = new Portfolio(cash);
                        }
                        break;
                    case "ASSET":
                        if(currentAsset != null && expectedQuantity > 0){
                            Portfolio.AssetHolding holding = portfolio.getHoldings().get(currentAsset.getUniqueId());
                            if(holding.getTotalQuantity() != expectedQuantity){
                                throw new DataIntegrityException("Not enough quantity for asset: " + currentAsset.getUniqueId());
                            }
                        }
                        String assetTypeStr = parts[1];
                        String uniqueId = parts[2];
                        String name = parts[3];
                        double currentMarketValue = Double.parseDouble(parts[4]);
                        double specificValue = Double.parseDouble(parts[5]);
                        expectedQuantity = Integer.parseInt(parts[6]);
                        
                        AssetType typeEnum;
                        try {
                            typeEnum = AssetType.valueOf(assetTypeStr);
                        } catch (IllegalArgumentException e) {
                            // Handle unknown types or backward compatibility if needed
                            // For strict compliance, we might want to throw or log
                            typeEnum = null;
                        }

                        if (typeEnum != null) {
                            switch (typeEnum) {
                                case SHARE:
                                    currentAsset = new Share(uniqueId, name, currentMarketValue, specificValue);
                                    break;
                                case CURRENCY:
                                    currentAsset = new Currency(uniqueId, name, currentMarketValue, specificValue);
                                    break;
                                case COMMODITY:
                                    currentAsset = new Commodity(uniqueId, name, currentMarketValue, specificValue);
                                    break;
                            }
                        } else {
                            currentAsset = null;
                        }

                        if(currentAsset != null){
                           Portfolio.AssetHolding holding = new Portfolio.AssetHolding(currentAsset);
                           portfolio.getHoldings().put(uniqueId, holding);
                        }
                        break;
                    case "LOT":
                        LocalDate date = LocalDate.parse(parts[1]);
                        double price = Double.parseDouble(parts[2]);
                        int quantity = Integer.parseInt(parts[3]);
                        PurchaseLot lot = new PurchaseLot(date, price, quantity);
                        if(currentAsset != null){
                            Portfolio.AssetHolding holding = portfolio.getHoldings().get(currentAsset.getUniqueId());
                            holding.getPurchaseLots().add(lot);
                        }
                }
            }
            if(currentAsset != null && expectedQuantity > 0){
                Portfolio.AssetHolding holding = portfolio.getHoldings().get(currentAsset.getUniqueId());
                if(holding.getTotalQuantity() != expectedQuantity){
                    throw new DataIntegrityException("Not enough quantity for asset: " + currentAsset.getUniqueId());
                }
            }
        }
        return portfolio;
    }
}
