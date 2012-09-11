package stockmaster.unit;

import java.util.ArrayList;
import java.util.Hashtable;

import stockmaster.marketdata.MarketDataSubscriber;

public class MarketDataInfo {

	// List of stock details - stockCode, stockData
	private Hashtable<String,StockData> marketData;
		
	//Sentiment value of the entire market
	private double overallSentiment;
	
	//Sentiment values of the individual counters
	private Hashtable<String, Double> marketSentiment;
	
	
	public MarketDataInfo() {
		marketSentiment = new Hashtable<String, Double>();
		marketData = new Hashtable<String, StockData>();
	}
	
	public Hashtable<String,StockData> getMarketData() {
		return marketData;
	}
	
	public Hashtable<String, Double> getMarketSentiment() {
		return marketSentiment;
	}

	// retrieve market sentiment
	public double getOverallSentiment() {
		return overallSentiment;
	}

	public void setOverallSentiment(double f) {
		this.overallSentiment = f;
	}
	
	public void resetSentiment(){
		setOverallSentiment(0);
		marketSentiment.clear();
	}

}
