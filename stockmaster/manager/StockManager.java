package stockmaster.manager;

import stockmaster.algo.RideTheTideImpl;
import stockmaster.algo.TradingAlgorithm;
import stockmaster.marketdata.*;
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
	public static final LogLevel LOG_LEVEL = LogLevel.DEBUG;
	
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
		//StockManager stockManager = new StockManager(new SGXWebMarketDataImpl());

        // Starts the appplication using MarketDataEmulator
        //StockManager stockManager = new StockManager(new MarketDataEmulatorImpl());

		// Starts the application using Replayer SGX Web Market Data
		//StockManager stockManager = new StockManager(new ReplayCSVMarketDataImpl("FeedData\\","20120824","SGX"));

        // Starts the application using Mongo Replayer
        StockManager stockManager = new StockManager(new ReplayMongoMarketDataImpl("sgx", "20120827"));

		//Define recorder to use with marketData
		//stockManager.loadRecorder(new CSVFileRecorderImpl("", "Random"));
		//stockManager.loadRecorder(new MongoRecorderImpl("sgx"));
		
		
		// Define algorithm stock manager would use to monitor the market
		stockManager.loadAlgo(new RideTheTideImpl(stockManager));
		
		
	}
}
