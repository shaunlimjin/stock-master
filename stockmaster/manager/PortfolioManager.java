package stockmaster.manager;

import stockmaster.db.MongoManager;
import stockmaster.unit.PortfolioStock;
import stockmaster.unit.StockData;

/**
 * Created with IntelliJ IDEA.
 * User: shaun
 * Date: 9/10/12
 * Time: 11:05 PM
 * Portfolio Manager - Singleton
 * Responsible for managing the portfolio
 */
public class PortfolioManager {

    private static PortfolioManager instance;

    private PortfolioManager() {

    }

    public static PortfolioManager getInstance() {
        if (instance == null)
            instance = new PortfolioManager();

        return instance;
    }

    private MongoManager mongoManager = MongoManager.getInstance();

    /**
     * Add stock to Portfolio
     *
     * @param portfolioStock stock to add to portfolio
     * @param portfolioName  name of portfolio to add stock to
     */
    public void addStockToPortfolio(PortfolioStock portfolioStock, String portfolioName) {
        mongoManager.saveStockToPortfolio(portfolioStock, portfolioName);
    }
}
