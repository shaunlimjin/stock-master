package stockmaster.unit;

import stockmaster.algo.TradingAlgorithm;

import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: shaun
 * Date: 9/10/12
 * Time: 11:07 PM
 * Entity class for a stock in Portfolio
 */
public class PortfolioStock {
    private String stockCode;
    private String stockName;
    private double purchasedPrice;
    private int quantity;
    private Calendar acquiredDate;

    private TradingAlgorithm tradingAlgorithm;

    public PortfolioStock(String stockCode, String stockName, double purchasedPrice, int quantity, TradingAlgorithm tradingAlgorithm){
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.purchasedPrice = purchasedPrice;
        this.quantity = quantity;

        this.tradingAlgorithm = tradingAlgorithm;

        this.acquiredDate = Calendar.getInstance();
    }
    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public double getPurchasedPrice() {
        return purchasedPrice;
    }

    public void setPurchasedPrice(double purchasedPrice) {
        this.purchasedPrice = purchasedPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getAcquiredDate() {
        return acquiredDate.getTime();
    }

    public void setAcquiredDate(Calendar acquiredDate) {
        this.acquiredDate = acquiredDate;
    }

    public TradingAlgorithm getTradingAlgorithm() {
        return tradingAlgorithm;
    }

    public void setTradingAlgorithm(TradingAlgorithm tradingAlgorithm) {
        this.tradingAlgorithm = tradingAlgorithm;
    }
}
