package stockmaster.marketdata;

import stockmaster.unit.StockData;
import stockmaster.util.Log;

/*
 * Generates random market data for algo testing
 */
public class MarketDataEmulatorImpl extends MarketData {

	public static final int GENERATE_NUMBER_OF_STOCKS = 1500;
	public static final int NUMBER_OF_STOCKS_PER_UPDATE = 20;
	public static final int MAX_PRICE_OF_STOCK = 25;

	// Market.BULL (70% chance of price increasing), Market.BEAR (70% chance of price decreasing), 
	// Market.NEUTRAL (50% chance of price increasing)
	public Market marketSentiment = Market.NEUTRAL;
	
	public static enum Market {
		BULL, BEAR, NEUTRAL;
	}
	
	public MarketDataEmulatorImpl(Market marketSentiment) {
		super();
		this.marketSentiment = marketSentiment;
	}
	
	@Override
	public void populateData() {
		float noOfStockUpdates = ((float) Math.random()*NUMBER_OF_STOCKS_PER_UPDATE);
		
		Log.debug(this, "Changing the prices of "+noOfStockUpdates+" stocks.");
		
		for (int i = 0; i < noOfStockUpdates; i++) {
			String stockCode = ((int)(Math.random()*GENERATE_NUMBER_OF_STOCKS))+"";
			StockData stockData = marketData.get(stockCode);
			
			float price = 0;
			
			price = (float)((Math.random()*stockData.getBuyPrice())*0.05);
			
			// 50-50 chance of price increasing/decreasing
			float weight;
			
			if (marketSentiment == Market.BULL)
				weight = 0.7f;
			else if (marketSentiment == Market.BEAR)
				weight = 0.3f;
			else // Market.NEUTRAL
				weight = 0.5f;
				
			if (Math.random() > weight)
				price *= -1;
			
			stockData.clearFieldChangedList();
			
			Log.debug(this, "Updating stock: "+stockCode+" Before (Buy/Sell): "+stockData.getBuyPrice()+"/"+stockData.getSellPrice()+" Price Change: "+price);
			stockData.setBuyPrice(stockData.getBuyPrice()+price);
			stockData.setSellPrice(stockData.getBuyPrice()+price);
			
			if (price > 0)
				stockData.setLastPrice(stockData.getBuyPrice());
			else
				stockData.setLastPrice(stockData.getSellPrice());
			
			Log.debug(this, stockData.toString());
			
			stockChange(stockData);
		}
	}

	@Override
	public void refresh() {
		populateData();
	}

	@Override
	public void init() {
		
		StockData stockData;
		
		Log.debug(this, "Generating "+GENERATE_NUMBER_OF_STOCKS+" stocks.");
		// Create dummy stockData
		for (int i = 0; i < GENERATE_NUMBER_OF_STOCKS; i++) {
			stockData = new StockData();
			marketData.put(i+"", stockData);
			
			stockData.setStockCode(i+"");
			stockData.setStockName(i+"");
						
			float buyPrice = (float)Math.random()*MAX_PRICE_OF_STOCK;
			stockData.setBuyPrice(buyPrice);
			stockData.setSellPrice(buyPrice+1);
			stockData.setLastPrice(buyPrice);
			
			stockChange(stockData);
		}
		
	}

}
