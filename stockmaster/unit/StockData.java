package stockmaster.unit;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Transient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import stockmaster.util.Log;

/*
 * Entity class to store Stock details.
 * Annotations used are Morphia's for persistence/retrieval from MongoDB.
 */

//This entity will be stored in a collection in MongoDB named "stocks", and the class name will not be stored
@Entity(value="stocks", noClassnameStored=true)
public class StockData {
	private float valueChange;
	private float percentChange;
	private float buyPrice;
	private float sellPrice;
	private float lastPrice;
	private float buyVolume;
	private float sellVolume;
	private float volume;
	private float openPrice;
	private float lowPrice;
	private float highPrice;
	private float value;
	private String sector;
	private String stockName;
	private String stockCode;
	private float sentiment;
	private float sentimentWeight;
	private Calendar lastUpdate;
	
	// indicates which field was last updated
    @Transient //don't persist
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
				+ ", value=" + value + ", sector=" + sector + ", date="+ Log.formateDateTime(lastUpdate.getTime())+"]";
	}

	private String remarks;
	
	public float getValueChange() {
		return valueChange;
	}

	public void setValueChange(float valueChange) {
		this.valueChange = valueChange;
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public float getPercentChange() {
		return percentChange;
	}

	public void setPercentChange(float percentChange) {
		this.percentChange = percentChange;
		fieldChangedList.add(FieldChanged.PERCENT_CHANGE);
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public float getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(float buyPrice) {
		this.buyPrice = buyPrice;
		fieldChangedList.add(FieldChanged.BUY_PRICE);
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public float getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(float sellPrice) {
		this.sellPrice = sellPrice;
		fieldChangedList.add(FieldChanged.SELL_PRICE);
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public float getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(float lastPrice) {
		this.lastPrice = lastPrice;
		fieldChangedList.add(FieldChanged.LAST_PRICE);
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public float getBuyVolume() {
		return buyVolume;
	}

	public void setBuyVolume(float buyVolume) {
		this.buyVolume = buyVolume;
		fieldChangedList.add(FieldChanged.BUY_VOLUME);
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public float getSellVolume() {
		return sellVolume;
	}

	public void setSellVolume(float sellVolume) {
		this.sellVolume = sellVolume;
		fieldChangedList.add(FieldChanged.SELL_VOLUME);
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public float getVolume() {
		return volume;
	}

	public void setVolume(float volume) {
		this.volume = volume;
		fieldChangedList.add(FieldChanged.VOLUME);
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public float getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(float openPrice) {
		this.openPrice = openPrice;
		fieldChangedList.add(FieldChanged.OPEN_PRICE);
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public float getLowPrice() {
		return lowPrice;
	}

	public void setLowPrice(float lowPrice) {
		this.lowPrice = lowPrice;
		fieldChangedList.add(FieldChanged.LOW_PRICE);
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public float getHighPrice() {
		return highPrice;
	}

	public void setHighPrice(float highPrice) {
		this.highPrice = highPrice;
		fieldChangedList.add(FieldChanged.HIGH_PRICE);
		lastUpdate.setTimeInMillis(System.currentTimeMillis());
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
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
	
	public float getSentiment() {
		return sentiment;
	}


	public void setSentiment(float sentiment) {
		this.sentiment = sentiment;
	}


	public float getSentimentWeight() {
		return sentimentWeight;
	}


	public void setSentimentWeight(float sentimentWeight) {
		this.sentimentWeight = sentimentWeight;
	}

	public void setLastUpdate(long time) {
		lastUpdate.setTimeInMillis(time);
	}
	
	public Date getLastUpdate() {
		return lastUpdate.getTime();
	}
	
	public void displayFieldChanges() {
		Log.debug(this, stockCode+" Field Changes: ");
		for (FieldChanged changed : fieldChangedList)
			Log.debug(this, changed.toString());
	}
}
