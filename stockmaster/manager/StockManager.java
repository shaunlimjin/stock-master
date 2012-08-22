package stockmaster.manager;

import stockmaster.algo.RideTheTideImpl;
import stockmaster.algo.TradingAlgorithm;
import stockmaster.marketdata.MarketData;
import stockmaster.marketdata.SGXWebMarketDataImpl;
import stockmaster.util.Log;
import stockmaster.util.Log.LogLevel;

/* Main Application
 * Starts up the application with predefined MarketData source and Algorithm.
 */
public class StockManager {
	
	private MarketData marketData;
	private TradingAlgorithm algo;
	
	public StockManager(MarketData marketData) {
		this.marketData = marketData;
	}
	
	public void loadAlgo(TradingAlgorithm algo) {
		this.algo = algo;
		Log.info(this, "Algorithm loaded: "+algo.getClass().getName());
		marketData.start();
	}
	
	public MarketData getMarketData() {
		return marketData;
	}
	
	public static void main(String[] args) {
		Log.logLevel = LogLevel.DEBUG;
		
		// Starts the application using SGX Web Market Data 
		StockManager stockManager = new StockManager(new SGXWebMarketDataImpl());
		
		// Define algorithm stock manager would use to monitor the market
		stockManager.loadAlgo(new RideTheTideImpl(stockManager));
	}
}
