package stockmaster.unit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import stockmaster.util.Log;

/*
 * Entity class to store Stock details.
 */

public class StockData {
	private double valueChange;
	private double percentChange;
	private double buyPrice;
	private double sellPrice;
	private double lastPrice;
	private double buyVolume;
	private double sellVolume;
	private double volume;
	private double openPrice;
	private double lowPrice;
	private double highPrice;
	private double value;
	private String sector;
	private String stockName;
	private String stockCode;
	private double sentiment;
	private double sentimentWeight;
	private Calendar lastUpdate;
	private boolean hasInvalidData; // in case there were errors parsing marketdata, we will flag this stock as untradable.
	
	// indicates which field was last updated
	private ArrayList<FieldChanged> fieldChangedList;

	public static enum FieldChanged {
		BUY_PRICE,
		SELL_PRICE,
		LAST_PRICE,
		BUY_VOLUME,
		SELL_VOLUME,
		VOLUME,
		OPEN_PRICE,
		HIGH_PRICE,
		LOW_PRICE,
		VALUE,
		SECTOR,
		STOCK_NAME,
		STOCK_CODE,
		PERCENT_CHANGE,
		VALUE_CHANGE,
		REMARKS
	}
	
	public StockData() {
		fieldChangedList = new ArrayList<FieldChanged>();
		setSentiment(0);
		setSentimentWeight(1);
		
		lastUpdate = Calendar.getInstance();
	}
	
	
	public void clearFieldChangedList() {
		fieldChangedList.clear();
	}
	
	public void addFieldChanged(FieldChanged fieldChanged) {
		fieldChangedList.add(fieldChanged);
	}
	
	public boolean fieldChanged(FieldChanged fieldChanged) {
		return fieldChangedList.contains(fieldChanged);
	}
	
	public boolean wasUpdated() {
		return (fieldChangedList.size() != 0);
	}
	
	@Override
	public String toString() {
		return "StockData [stockName=" + stockName + ", stockCode=" + stockCode
				+ ", remarks=" + remarks + ", valueChange=" + valueChange
				+ ", percentChange=" + percentChange + ", buyPrice=" + buyPrice
				+ ", sellPrice=" + sellPrice + ", lastPrice=" + lastPrice
				+ ", buyVolume=" + buyVolume + ", sellVolume=" + sellVolume
				+ ", volume=" + volume + ", openPrice=" + openPrice
				+ ", lowPrice=" + lowPrice + ", highPrice=" + highPrice
				+ ", value=" + value + ", sector=" + sector + ", date="+ Log.formatDateTime(lastUpdate.getTime())+"]";
	}

	private String remarks;
	
	public double getValueChange() {
		return valueChange;
	}

	public void setValueChange(double valueChange) {
		this.valueChange = valueChange;
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public double getPercentChange() {
		return percentChange;
	}

	public void setPercentChange(double percentChange) {
		this.percentChange = percentChange;
		fieldChangedList.add(FieldChanged.PERCENT_CHANGE);
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public double getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
		fieldChangedList.add(FieldChanged.BUY_PRICE);
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public double getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
		fieldChangedList.add(FieldChanged.SELL_PRICE);
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public double getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(double lastPrice) {
		this.lastPrice = lastPrice;
		fieldChangedList.add(FieldChanged.LAST_PRICE);
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public double getBuyVolume() {
		return buyVolume;
	}

	public void setBuyVolume(double buyVolume) {
		this.buyVolume = buyVolume;
		fieldChangedList.add(FieldChanged.BUY_VOLUME);
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public double getSellVolume() {
		return sellVolume;
	}

	public void setSellVolume(double sellVolume) {
		this.sellVolume = sellVolume;
		fieldChangedList.add(FieldChanged.SELL_VOLUME);
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
		fieldChangedList.add(FieldChanged.VOLUME);
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public double getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(double openPrice) {
		this.openPrice = openPrice;
		fieldChangedList.add(FieldChanged.OPEN_PRICE);
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public double getLowPrice() {
		return lowPrice;
	}

	public void setLowPrice(double lowPrice) {
		this.lowPrice = lowPrice;
		fieldChangedList.add(FieldChanged.LOW_PRICE);
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public double getHighPrice() {
		return highPrice;
	}

	public void setHighPrice(double highPrice) {
		this.highPrice = highPrice;
		fieldChangedList.add(FieldChanged.HIGH_PRICE);
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
		fieldChangedList.add(FieldChanged.VALUE);
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
		fieldChangedList.add(FieldChanged.SECTOR);
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
		fieldChangedList.add(FieldChanged.STOCK_NAME);
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
		fieldChangedList.add(FieldChanged.STOCK_CODE);
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
		fieldChangedList.add(FieldChanged.REMARKS);
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}
	
	public double getSentiment() {
		return sentiment;
	}


	public void setSentiment(double sentiment) {
		this.sentiment = sentiment;
	}


	public double getSentimentWeight() {
		return sentimentWeight;
	}


	public void setSentimentWeight(double sentimentWeight) {
		this.sentimentWeight = sentimentWeight;
	}

	public void setLastUpdate(long time) {
		lastUpdate.setTimeInMillis(time);
	}
	
	public void setLastUpdate(Date dateTime) {
		lastUpdate.setTime(dateTime);
	}
	
	public Date getLastUpdate() {
		return lastUpdate.getTime();
	}
	
	public boolean hasInvalidData() {
		return hasInvalidData();
	}
	
	public void setHasInvalidData(boolean hasInvalidData) {
		this.hasInvalidData = hasInvalidData;
	}
	
	public void displayFieldChanges() {
		Log.debug(this, stockCode+" Field Changes: ");
		for (FieldChanged changed : fieldChangedList)
			Log.debug(this, changed.toString());
	}
}
