package stockmaster.algo;

import java.util.Hashtable;
import java.util.LinkedList;

import stockmaster.StockMaster;
import stockmaster.unit.PriceTimeUnit;
import stockmaster.unit.StockData;
import stockmaster.util.Log;
import stockmaster.unit.StockUnit;

/*
 * Ride The Tide Algorithm
 * 
 * Trading idea is to follow market sentiments on a particular stock. 
 * E.g. If a stock rises by x% in y seconds, try to get in the market to ride with the market.
 */
public class RideTheTideImpl extends TradingAlgorithm {

	private static final int PRICE_TIME_UNIT_QUEUE_SIZE = 10;

	// Time to trigger algorithm - in millis
	private static double TIME_PERIOD = 2 * 60 * 1000;
	// Percentage price increase to trigger BUY
	private static double PRICE_INCREASE = 5;
	// Percentage price decrease from highest price to trigger SELL
	private static double PRICE_DECREASE_FROM_HIGHEST_PRICE = 2;
	// Minimum price we should monitor
	private static double MINIMUM_PRICE = 5;

	public RideTheTideImpl(StockMaster stockManager) {
		super(stockManager, 20000);

	}
	
	private void analyze(StockUnit stockUnit) {
		long startTime = stockUnit.getStockData().getLastUpdate().getTime() - (long)TIME_PERIOD;

		// Retrieve price history of a stock
		LinkedList<PriceTimeUnit> priceHistoryList = stockUnit.getPriceHistoryList();
		Log.info(this, "Analyzing StockCode: " + stockUnit.getStockData().getStockCode() + " Price History Size: " + priceHistoryList.size());

		double lastPrice = stockUnit.getStockData().getLastPrice();
		double highestPrice = stockUnit.getHighestPrice();

		// Check if we need to SELL
		if ((stockUnit.getPosition() > 0) && (highestPrice * ((100 - PRICE_DECREASE_FROM_HIGHEST_PRICE) / 100) > lastPrice)) {
			Log.info(this, "#### SELL: " + stockUnit.getStockData().getStockCode() + " (" + stockUnit.getStockData().getStockName() + ") @ $" + lastPrice + " (BUY Price: " + stockUnit.getBuyPrice() + ", Profit: "+(lastPrice-stockUnit.getBuyPrice())+")");
			stockUnit.setPosition(0);
			
			if (isAlgoTesting) {
				if (lastPrice-stockUnit.getBuyPrice() > 0) {
					noOfProfit++;
					Log.algoTesting(this, "PROFIT: #### SELL: " + stockUnit.getStockData().getStockCode() + " (" + stockUnit.getStockData().getStockName() + ") @ $" + lastPrice + " (BUY Price: " + stockUnit.getBuyPrice() + ", Profit: "+(lastPrice-stockUnit.getBuyPrice())+")");
				}
				else {
					noOfLoss++;
					Log.algoTesting(this, "LOSS: #### SELL: " + stockUnit.getStockData().getStockCode() + " (" + stockUnit.getStockData().getStockName() + ") @ $" + lastPrice + " (BUY Price: " + stockUnit.getBuyPrice() + ", Profit: "+(lastPrice-stockUnit.getBuyPrice())+")");
				}	
			}
		} // Check if we hit a new high
		else if ((stockUnit.getPosition() > 0) && (lastPrice > highestPrice)) {
			stockUnit.setHighestPrice(lastPrice);
		} // Check if we need to BUY
		else {
			// Check if price has increased by x% in y time
			for (PriceTimeUnit unit : priceHistoryList) {
				Log.debug(this, "[Price History] Time/Price: " + unit.getTime() + "/" + unit.getPrice());
				
				// Check what is the current time vs the last recorded time
				if (startTime < unit.getTime()) {
					// compare current price with startTime price - check if it
					// exceeds x%
					double percentageChange = 100 * (priceHistoryList.getFirst().getPrice() - unit.getPrice()) / unit.getPrice();
					Log.info(this, "[Price History] Percentage Change: " + percentageChange + " (Current Price: " + priceHistoryList.getFirst().getPrice() + " Start Price: " + unit.getPrice() + ")");
				
					// decide whether or not to BUY - stock must increase by x%
					// over specified time
					if ((percentageChange >= PRICE_INCREASE) && (stockUnit.getPosition() == 0)) {
						Log.info(this, "#### BUY: " + stockUnit.getStockData().getStockCode() + " (" + stockUnit.getStockData().getStockName() + ") @ $" + lastPrice + "(Start Price: " + unit.getPrice() + ")");
						stockUnit.setBuyPrice(lastPrice);
						stockUnit.setHighestPrice(lastPrice);
						stockUnit.setPosition(1);
						
	
					}
				}
			}
		}
	}
	
	// When there is any stock changes, analyze whether stock meets our triggers
	@Override
	public void stockChange(StockData stockData) {
		String stockCode = stockData.getStockCode();

		Log.debug(this, stockCode + ": Stock data changed!");

		if (Log.logLevel == Log.LogLevel.DEBUG)
			stockData.displayFieldChanges();

		// we are only interested in price changes. we ignore all other changes
		// (e.g. volume etc)
		if (stockData.fieldChanged(StockData.FieldChanged.LAST_PRICE) && (stockData.getVolume() != 0) && (stockData.getLastPrice() > MINIMUM_PRICE)) {
			StockUnit stockUnit;

			if (stockList.containsKey(stockCode))
				stockUnit = stockList.get(stockCode);
			else {
				stockUnit = new StockUnit(stockData);
				stockList.put(stockCode, stockUnit);
			}

			stockUnit.addInPriceList(stockData.getLastPrice(), stockData.getLastUpdate().getTime());

			// analyze whether stock meets our triggers
			analyze(stockUnit);
		}
	}
	
	// Prepare algo for test run
	public void initAlgoTestParameters() {
		//algoTestParameters.put("TIME_PERIOD", new AlgoTestUnit(30000, 6*60000, 30000));
		//algoTestParameters.put("PRICE_INCREASE", new AlgoTestUnit(0.5f, 20, 0.5f));
		//algoTestParameters.put("PRICE_DECREASE_FROM_HIGHEST_PRICE", new AlgoTestUnit(1, 4, 0.5f));
		
		algoTestParameters.put("TIME_PERIOD", new AlgoTestUnit(60000, 3*60000, 30000));
		algoTestParameters.put("PRICE_INCREASE", new AlgoTestUnit(2, 15, 1));
		algoTestParameters.put("PRICE_DECREASE_FROM_HIGHEST_PRICE", new AlgoTestUnit(1, 5, 1));
		algoTestParameters.put("MINIMUM_PRICE", new AlgoTestUnit(0.1f, 1, 0.1f));
	}
	

	@Override
	public void reset() {
		
		int openAtProfit = 0;
		int openAtLoss = 0;
		int openAtNeutral = 0;
		
		for (StockUnit stockUnit : stockList.values()) {
			if (stockUnit.getPosition() > 0) {
				Log.algoTesting(this, "### OPEN: " + stockUnit.getStockData().getStockCode() + " (" + stockUnit.getStockData().getStockName() + ") (BUY Price: " + stockUnit.getBuyPrice() + ", Closed at: "+(stockUnit.getStockData().getLastPrice())+", Profit: "+(stockUnit.getStockData().getLastPrice()-stockUnit.getBuyPrice())+")");
		
				if (stockUnit.getStockData().getLastPrice()-stockUnit.getBuyPrice() > 0)
					openAtProfit++;
				else if (stockUnit.getStockData().getLastPrice()-stockUnit.getBuyPrice() == 0)
					openAtNeutral++;
				else 
					openAtLoss++;
					
			}
		}
		
		Log.algoTesting(this, "### OPEN AND PROFIT: "+openAtProfit+", OPEN AND NEUTRAL: "+openAtNeutral+", OPEN AND LOSS: "+openAtLoss);
		
		stockList.clear();
	}
}
