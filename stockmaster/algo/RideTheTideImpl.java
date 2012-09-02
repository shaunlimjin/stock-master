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
			
	public static final int PRICE_TIME_UNIT_QUEUE_SIZE = 10;
	
	// Time to trigger algorithm - in millis
	public static final long TIME_PERIOD = 7*60*1000;
	// Price increase to trigger algorithm - in percentage
	public static final float PRICE_INCREASE = 2;
	// Price decrease to trigger algorithm - in percentage
	public static final float PRICE_DECREASE = -2;
			
	
	// stockCode, StockUnit pair
	private Hashtable<String, StockUnit> stockList;
	
	public RideTheTideImpl(StockMaster stockManager) {
		super(stockManager, 30000);
		stockList = new Hashtable<String, StockUnit>();
	}
	
	
	private void analyze(StockUnit stockUnit) {
			
			long startTime = System.currentTimeMillis() - TIME_PERIOD;
			
			// Retrieve price history of a stock
			LinkedList<PriceTimeUnit> priceHistoryList = stockUnit.getPriceHistoryList();
			Log.info(this, "Analyzing StockCode: "+stockUnit.getStockData().getStockCode()+" Size: "+priceHistoryList.size()+" ("+System.currentTimeMillis()+" - "+startTime+")");
			
			// Check if price has increased by x% in y time
			for (PriceTimeUnit unit : priceHistoryList) {
				Log.info(this, "[Price History] Time/Price: "+unit.time+"/"+unit.price);
				
				// Check what is the current time vs the last recorded time
				if (startTime < unit.time) {
					// compare current price with startTime price - check if it exceeds x%
					float percentageChange = 100*(priceHistoryList.getFirst().price-unit.price) / unit.price;	
					Log.info(this, "[Price History] Percentage Change: "+percentageChange +" (Current Price: "+priceHistoryList.getFirst().price+ " Start Price: "+unit.price+")");	
					
					// decide whether or not to BUY - stock must increase by x% over specified time	
					if ((percentageChange >= PRICE_INCREASE) && (stockUnit.position == 0)) {
						Log.write("#### BUY: "+stockUnit.getStockData().getStockCode()+" ("+stockUnit.getStockData().getStockName()+") @ $"+priceHistoryList.getFirst().price+ "(Start Price: "+unit.price+")");
						Log.info(this, "#### BUY: "+stockUnit.getStockData().getStockCode()+" ("+stockUnit.getStockData().getStockName()+") @ $"+priceHistoryList.getFirst().price+ "(Start Price: "+unit.price+")");
						stockUnit.position = 1;
						
					} // decide whether or not to SELL - stock must decrease by x% over specified time
					else if ((percentageChange <= PRICE_DECREASE) && (stockUnit.position != 0)) {
						Log.write("#### SELL: "+stockUnit.getStockData().getStockCode()+" ("+stockUnit.getStockData().getStockName()+") @ $"+priceHistoryList.getFirst().price+ "(Start Price: "+unit.price+")");
						Log.info(this, "#### SELL: "+stockUnit.getStockData().getStockCode()+" ("+stockUnit.getStockData().getStockName()+") @ $"+priceHistoryList.getFirst().price+ "(Start Price: "+unit.price+")");
						stockUnit.position = 0;
					}
				}
			}
	}

	// When there is any stock changes, analyze whether stock meets our triggers
	@Override
	public void stockChange(StockData stockData) {
		String stockCode = stockData.getStockCode();
		
		Log.info(this, stockCode+": Stock data changed!");
		
		if (Log.logLevel == Log.LogLevel.DEBUG)
			stockData.displayFieldChanges();
		
		// we are only interested in price changes. we ignore all other changes (e.g. volume etc)
		if (stockData.fieldChanged(StockData.FieldChanged.LAST_PRICE)) {
			StockUnit stockUnit;
		
			if (stockList.containsKey(stockCode))
				stockUnit = stockList.get(stockCode);
			else {
				stockUnit = new StockUnit(stockData);
				stockList.put(stockCode, stockUnit);
			}
		
			stockUnit.addInPriceList(stockData.getLastPrice());

			// analyze whether stock meets our triggers
			analyze(stockUnit);
		}
	}
	
	// Entity class to track the price history of a stock. we need this information to calculate percentage difference
	class StockUnit {
		private LinkedList<PriceTimeUnit> priceHistoryList;
		private StockData stockData;
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

		public void setPosition(int position) {
			this.position = position;
		}
		
		public void addInPriceList(float price) {
			PriceTimeUnit unit;
			
			if (priceHistoryList.size() == PRICE_TIME_UNIT_QUEUE_SIZE) {
				Log.debug(this, "PriceHistoryList is full. Removing oldest entry");
				unit = priceHistoryList.removeLast();
			}
			else 
				unit = new PriceTimeUnit();
			
			unit.price = price;
			unit.time = System.currentTimeMillis();
			
			priceHistoryList.addFirst(unit);
			Log.debug(this, "Added PriceTimeUnit to priceHistoryList (Size: "+priceHistoryList.size()+")");
		}
	}
	
	class PriceTimeUnit {
		float price;
		long time;
	}
}
