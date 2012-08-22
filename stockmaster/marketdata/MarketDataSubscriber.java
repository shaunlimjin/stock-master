package stockmaster.marketdata;

import stockmaster.unit.StockData;

public interface MarketDataSubscriber {
	
	// Notify those who subscribes to marketData about stockChanges
	public void stockChange(StockData stockCode);
}
