package stockmaster.manager;

import stockmaster.algo.RideTheTideImpl;
import stockmaster.algo.TradingAlgorithm;
import stockmaster.marketdata.MarketData;
import stockmaster.marketdata.SGXWebMarketDataImpl;
import stockmaster.marketdata.ReplayCSVMarketDataImpl;
import stockmaster.recorder.DataRecorder;
import stockmaster.recorder.CSVFileRecorderImpl;
import stockmaster.recorder.MongoRecorderImpl;
import stockmaster.util.Log;
import stockmaster.util.Log.LogLevel;

/* Main Application
 * Starts up the application with predefined MarketData source and Algorithm.
 */
public class StockManager {
	
	// set debug level
	public static final LogLevel LOG_LEVEL = LogLevel.INFO;
	
	private MarketData marketData;
	private TradingAlgorithm algo;
	private DataRecorder recorder;
	
	public StockManager(MarketData marketData) {
		this.marketData = marketData;
	}
	
	public void loadAlgo(TradingAlgorithm algo) {
		this.algo = algo;
		Log.info(this, "Algorithm loaded: "+algo.getClass().getName());
		marketData.start();
	}
	
	public void loadRecorder(DataRecorder recorder) {
		this.recorder = recorder;
		Log.info(this, "Recorder loaded: "+ recorder.getClass().getName());
		marketData.subscribe(recorder);
	}
	
	public MarketData getMarketData() {
		return marketData;
	}
	
	public static void main(String[] args) {
		Log.logLevel = LOG_LEVEL;
		System.out.println("Starting StockManager in "+Log.logLevel+" mode.");
		
		// Starts the application using SGX Web Market Data 
		StockManager stockManager = new StockManager(new SGXWebMarketDataImpl());
		
		// Starts the application using Replayer SGX Web Market Data 
		//StockManager stockManager = new StockManager(new ReplayCSVMarketDataImpl("FeedData\\","20120824","SGX"));
		
		// Define algorithm stock manager would use to monitor the market
		stockManager.loadAlgo(new RideTheTideImpl(stockManager));
		
		//Define recorder to use with marketData
		//stockManager.loadRecorder(new CSVFileRecorderImpl("", "SGX"));
        stockManager.loadRecorder(new MongoRecorderImpl("sgx"));
	}
}
