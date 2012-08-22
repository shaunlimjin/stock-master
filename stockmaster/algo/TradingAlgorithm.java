package stockmaster.algo;

import stockmaster.manager.StockManager;
import stockmaster.marketdata.MarketDataSubscriber;

/*
 * TradingAlgorithm
 * 
 * All algorithms should extend this class.
 */
public abstract class TradingAlgorithm implements MarketDataSubscriber {

	protected StockManager stockManager;

	// Automatically subscribes to a particular MarketData for stock changes
	public TradingAlgorithm(StockManager stockManager, int refreshTime) {
		this.stockManager = stockManager;
		stockManager.getMarketData().setRefreshTime(refreshTime);
		stockManager.getMarketData().subscribe(this);
	}
}
