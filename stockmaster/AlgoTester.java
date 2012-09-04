package stockmaster;

import java.util.ArrayList;

import stockmaster.algo.RideTheTideImpl;
import stockmaster.algo.TradingAlgorithm;
import stockmaster.marketdata.*;
import stockmaster.recorder.DataRecorder;
import stockmaster.util.Log;
import stockmaster.util.Log.LogLevel;

/* AlgoTester
 * Starts up the application with predefined MarketData source and Algorithm.
 */
public class AlgoTester extends StockMaster {
	
	public AlgoTester(MarketData marketData) {
		super(marketData);
	}

	public static void main(String[] args) {
		Log.logLevel = LogLevel.ALGO_TESTING;
		System.out.println("Starting StockManager in "+Log.logLevel+" mode.");
	
		// Starts the application using Replayer SGX Web Market Data
        StockMaster stockMaster = new StockMaster(new ReplayCSVMarketDataImpl("FeedData/","20120903","SGX"));

		// Define algorithm stock manager would use to monitor the market
        RideTheTideImpl rideTheTideAlgo = new RideTheTideImpl(stockMaster);
        stockMaster.loadAlgo(rideTheTideAlgo);	
        
        
        rideTheTideAlgo.initAlgoTestParameters();
        rideTheTideAlgo.startAlgoTest();
	}
}
