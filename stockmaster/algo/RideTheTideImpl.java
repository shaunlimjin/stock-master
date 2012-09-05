package stockmaster.algo;

import java.util.Hashtable;
import java.util.LinkedList;

import stockmaster.StockMaster;
import stockmaster.unit.StockData;
import stockmaster.util.Log;

/*
 * Ride The Tide Algorithm
 * 
 * Trading idea is to follow market sentiments on a particular stock. 
 * E.g. If a stock rises by x% in y seconds, try to get in the market to ride with the market.
 */
public class RideTheTideImpl extends TradingAlgorithm {

	private static final int PRICE_TIME_UNIT_QUEUE_SIZE = 10;

	// Time to trigger algorithm - in millis
	private static float TIME_PERIOD = 2 * 60 * 1000;
	// Percentage price increase to trigger BUY
	private static float PRICE_INCREASE = 5;
	// Percentage price decrease from highest price to trigger SELL
	private static float PRICE_DECREASE_FROM_HIGHEST_PRICE = 2;
	// Minimum price we should monitor
	private static float MINIMUM_PRICE = 5;

	// stockCode, StockUnit pair
	private Hashtable<String, StockUnit> stockList;

	public RideTheTideImpl(StockMaster stockManager) {
		super(stockManager, 20000);
		stockList = new Hashtable<String, StockUnit>();
	}
	
	private void analyze(StockUnit stockUnit) {
		long startTime = stockUnit.getStockData().getLastUpdate().getTime() - (long)TIME_PERIOD;

		// Retrieve price history of a stock
		LinkedList<PriceTimeUnit> priceHistoryList = stockUnit.getPriceHistoryList();
		Log.info(this, "Analyzing StockCode: " + stockUnit.getStockData().getStockCode() + " Price History Size: " + priceHistoryList.size());

		float lastPrice = stockUnit.getStockData().getLastPrice();
		float highestPrice = stockUnit.getHighestPrice();

		// Check if we need to SELL
		if ((stockUnit.position > 0) && (highestPrice * ((100 - PRICE_DECREASE_FROM_HIGHEST_PRICE) / 100) > lastPrice)) {
			Log.info(this, "#### SELL: " + stockUnit.getStockData().getStockCode() + " (" + stockUnit.getStockData().getStockName() + ") @ $" + lastPrice + " (BUY Price: " + stockUnit.getBuyPrice() + ", Profit: "+(lastPrice-stockUnit.getBuyPrice())+")");
			stockUnit.position = 0;
			
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
		else if ((stockUnit.position > 0) && (lastPrice > highestPrice)) {
			stockUnit.setHighestPrice(lastPrice);
		} // Check if we need to BUY
		else {
			// Check if price has increased by x% in y time
			for (PriceTimeUnit unit : priceHistoryList) {
				Log.debug(this, "[Price History] Time/Price: " + unit.time + "/" + unit.price);
				
				// Check what is the current time vs the last recorded time
				if (startTime < unit.time) {
					// compare current price with startTime price - check if it
					// exceeds x%
					float percentageChange = 100 * (priceHistoryList.getFirst().price - unit.price) / unit.price;
					Log.info(this, "[Price History] Percentage Change: " + percentageChange + " (Current Price: " + priceHistoryList.getFirst().price + " Start Price: " + unit.price + ")");
				
					// decide whether or not to BUY - stock must increase by x%
					// over specified time
					if ((percentageChange >= PRICE_INCREASE) && (stockUnit.position == 0)) {
						Log.info(this, "#### BUY: " + stockUnit.getStockData().getStockCode() + " (" + stockUnit.getStockData().getStockName() + ") @ $" + lastPrice + "(Start Price: " + unit.price + ")");
						stockUnit.setBuyPrice(lastPrice);
						stockUnit.setHighestPrice(lastPrice);
						stockUnit.position = 1;
						
	
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
	
	// Entity class to track the price history of a stock. we need this
	// information to calculate percentage difference
	class StockUnit {
		private LinkedList<PriceTimeUnit> priceHistoryList;
		private StockData stockData;
		private float highestPrice;
		private float buyPrice;
		private int position;

		public StockUnit(StockData stockData) {
			priceHistoryList = new LinkedList<PriceTimeUnit>();
			this.stockData = stockData;
		}

		public LinkedList<PriceTimeUnit> getPriceHistoryList() {
			return priceHistoryList;
		}

		public void setPriceHistoryList(LinkedList<PriceTimeUnit> priceHistoryList) {
			this.priceHistoryList = priceHistoryList;
		}

		public StockData getStockData() {
			return stockData;
		}

		public void setStockData(StockData stockData) {
			this.stockData = stockData;
		}

		public int getPosition() {
			return position;
		}

		public float getBuyPrice() {
			return buyPrice;
		}

		public void setBuyPrice(float price) {
			buyPrice = price;
		}

		public void setHighestPrice(float price) {
			highestPrice = price;
		}

		public float getHighestPrice() {
			return highestPrice;
		}

		public void setPosition(int position) {
			this.position = position;
		}

		public void addInPriceList(float price, long time) {
			PriceTimeUnit unit;

			if (priceHistoryList.size() == PRICE_TIME_UNIT_QUEUE_SIZE) {
				Log.debug(this, "PriceHistoryList is full. Removing oldest entry");
				unit = priceHistoryList.removeLast();
			} else
				unit = new PriceTimeUnit();

			unit.price = price;
			unit.time = time;

			priceHistoryList.addFirst(unit);
			Log.debug(this, "Added PriceTimeUnit to priceHistoryList (Size: " + priceHistoryList.size() + ")");
		}
	}

	class PriceTimeUnit {
		float price;
		long time;
	}

	@Override
	public void reset() {
		
		int openAtProfit = 0;
		int openAtLoss = 0;
		int openAtNeutral = 0;
		
		for (StockUnit stockUnit : stockList.values()) {
			if (stockUnit.position > 0) {
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
