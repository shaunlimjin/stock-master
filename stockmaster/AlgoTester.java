package stockmaster;

import java.util.ArrayList;

import stockmaster.algo.RideTheTideImpl;
import stockmaster.algo.TradingAlgorithm;
import stockmaster.manager.MarketDataManager;
import stockmaster.marketdata.*;
import stockmaster.recorder.CSVFileRecorderImpl;
import stockmaster.recorder.DataRecorder;
import stockmaster.util.Log;
import stockmaster.util.Log.LogLevel;

/* AlgoTester
 * Starts up the application with predefined MarketData source and Algorithm.
 */
public class AlgoTester extends StockMaster {
	
	public AlgoTester(MarketDataManager marketData) {
		super(marketData);
	}

	public static void main(String[] args) {
		Log.logLevel = LogLevel.ALGO_TESTING;
		System.out.println("Starting StockManager in "+Log.logLevel+" mode.");
		
		ArrayList<String> dateList = new ArrayList<String>();
		dateList.add("20120904");
	
		// Starts the application using Replayer SGX Web Market Data
        StockMaster stockMaster = new StockMaster(new MarketDataManager());

		// Define algorithm stock manager would use to monitor the market
        RideTheTideImpl rideTheTideAlgo = new RideTheTideImpl(stockMaster);
        stockMaster.getMarketDataManager().loadAlgo(rideTheTideAlgo);	
        
        
        rideTheTideAlgo.initAlgoTestParameters();
        rideTheTideAlgo.startAlgoTest();
	}
}
