package stockmaster.unit;

import java.util.LinkedList;

import stockmaster.util.Log;

//Entity class to track the price history of a stock. we need this
		// information to calculate percentage difference
	public class StockUnit {
		
		private static final int PRICE_TIME_UNIT_QUEUE_SIZE = 10;
		
			private LinkedList<PriceTimeUnit> priceHistoryList;
			private StockData stockData;
			private double highestPrice;
			private double buyPrice;
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

			public double getBuyPrice() {
				return buyPrice;
			}

			public void setBuyPrice(double price) {
				buyPrice = price;
			}

			public void setHighestPrice(double price) {
				highestPrice = price;
			}

			public double getHighestPrice() {
				return highestPrice;
			}

			public void setPosition(int position) {
				this.position = position;
			}

			public void addInPriceList(double price, long time) {
				PriceTimeUnit unit;

				if (priceHistoryList.size() == PRICE_TIME_UNIT_QUEUE_SIZE) {
					Log.debug(this, "PriceHistoryList is full. Removing oldest entry");
					unit = priceHistoryList.removeLast();
				} else
					unit = new PriceTimeUnit();

				unit.setPrice(price);
				unit.setTime(time);

				priceHistoryList.addFirst(unit);
				Log.debug(this, "Added PriceTimeUnit to priceHistoryList (Size: " + priceHistoryList.size() + ")");
			}
			
		}


