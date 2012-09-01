package stockmaster.manager;

import java.util.ArrayList;

import stockmaster.algo.RideTheTideImpl;
import stockmaster.algo.TradingAlgorithm;
import stockmaster.marketdata.*;
import stockmaster.marketdata.MarketDataEmulatorImpl.Market;
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
	private ArrayList<TradingAlgorithm> algoList;
	private ArrayList<DataRecorder>  recorderList;
	
	public StockManager(MarketData marketData) {
		this.marketData = marketData;
		
		algoList = new ArrayList<TradingAlgorithm>();
		recorderList = new ArrayList<DataRecorder>();
	}
	
	public void loadAlgo(TradingAlgorithm algo) {
		algoList.add(algo);
		Log.info(this, "Algorithm loaded: "+algo.getClass().getName());
	}
	
	public void loadRecorder(DataRecorder recorder) {
		recorderList.add(recorder);
		Log.info(this, "Recorder loaded: "+ recorder.getClass().getName());
		marketData.subscribe(recorder);

	}
	
	public void start() {
		marketData.start();
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
        StockManager stockManager = new StockManager(new MarketDataEmulatorImpl(Market.NEUTRAL));

		// Starts the application using Replayer SGX Web Market Data
		//StockManager stockManager = new StockManager(new ReplayCSVMarketDataImpl("FeedData/","20120824","SGX"));

        // Starts the application using Mongo Replayer
        //StockManager stockManager = new StockManager(new ReplayMongoMarketDataImpl("sgx", "20120827"));

		//Define recorder to use with marketData
		stockManager.loadRecorder(new CSVFileRecorderImpl("", "Random"));
		//stockManager.loadRecorder(new MongoRecorderImpl("sgx"));
		
		
		// Define algorithm stock manager would use to monitor the market
		stockManager.loadAlgo(new RideTheTideImpl(stockManager));
		
		stockManager.start();
	}
}
