package stockmaster.algo;

import stockmaster.StockMaster;
import stockmaster.marketdata.MarketDataSubscriber;

/*
 * TradingAlgorithm
 * 
 * All algorithms should extend this class.
 */
public abstract class TradingAlgorithm implements MarketDataSubscriber {

	protected StockMaster stockManager;

	// Automatically subscribes to a particular MarketData for stock changes
	public TradingAlgorithm(StockMaster stockManager, int refreshTime) {
		this.stockManager = stockManager;
		stockManager.getMarketData().setRefreshTime(refreshTime);
		stockManager.getMarketData().subscribe(this);
	}
}
