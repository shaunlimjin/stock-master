package stockmaster.unit;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

import stockmaster.unit.*;
import stockmaster.marketdata.MarketDataSubscriber;
import stockmaster.util.Log;

public class MarketDataInfo {

	
	
	// List of stock details - stockCode, stockData
	private Hashtable<String,StockData> marketData;
		
	//Sentiment value of the entire market
	private double overallSentiment;
	
	//Sentiment values of the individual counters
	private Hashtable<String, Double> marketSentiment;
	
	// stockCode, StockUnit pair
	private Hashtable<String, StockUnit> stockList;
	
	
	public MarketDataInfo() {
		marketSentiment = new Hashtable<String, Double>();
		marketData = new Hashtable<String, StockData>();
		stockList = new Hashtable<String, StockUnit>();
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
	
	public Hashtable<String, StockUnit> getStockList() {
		return stockList;
	}

	public void setStockList(Hashtable<String, StockUnit> stockList) {
		this.stockList = stockList;
	}

	public void resetSentiment(){
		setOverallSentiment(0);
		marketSentiment.clear();
	}
	

	
		

}
