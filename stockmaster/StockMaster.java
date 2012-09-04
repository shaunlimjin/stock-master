package stockmaster;

import java.util.ArrayList;
import java.util.Date;

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
public class StockMaster {
	
	// set debug level
	public static LogLevel LOG_LEVEL = LogLevel.INFO;
	
	private MarketData marketData;
	private ArrayList<TradingAlgorithm> algoList;
	private ArrayList<DataRecorder>  recorderList;
	
	public StockMaster(MarketData marketData) {
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
		//StockMaster stockMaster = new StockMaster(new SGXWebMarketDataImpl());

        // Starts the appplication using MarketDataEmulator
        //StockMaster stockMaster = new StockMaster(new MarketDataEmulatorImpl(Market.NEUTRAL));

		// Starts the application using Replayer SGX Web Market Data
		ArrayList<String> dateList = new ArrayList<String>();
		dateList.add("20120903");
		//dateList.add("20120904");
        StockMaster stockMaster = new StockMaster(new ReplayCSVMarketDataImpl("FeedData/",dateList,"SGX"));

        // Starts the application using Mongo Replayer
        //StockMaster stockMaster = new StockMaster(new ReplayMongoMarketDataImpl("sgx", "20120902"));

		//Define recorder to use with marketData
		//Datetime format yyyyMMdd hh:mm:ss 
		//stockMaster.loadRecorder(new CSVFileRecorderImpl("", "SGX", Log.getFormattedDateTime("20120904 08:58:00"), Log.getFormattedDateTime("20120904 17:05:00")));
		//stockMaster.loadRecorder(new MongoRecorderImpl("sgx", Log.getFormattedDateTime("20120904 08:58:00"), Log.getFormattedDateTime("20120904 17:05:00")));
		
		// Define algorithm stock manager would use to monitor the market
        //stockMaster.loadAlgo(new RideTheTideImpl(stockMaster));	
        stockMaster.start();
	}
}
