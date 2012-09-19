package stockmaster.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.sun.org.apache.bcel.internal.generic.ALOAD;

import stockmaster.algo.TradingAlgorithm;
import stockmaster.marketdata.*;
import stockmaster.recorder.DataRecorder;
import stockmaster.unit.MarketDataInfo;
import stockmaster.util.Log;

//Class to handle all MarketData interactions
//All MarketDataManager should extend this class

public abstract class MarketDataManager {

	private ArrayList<MarketData> marketDataList;
	private ArrayList<TradingAlgorithm> algoList;
	private ArrayList<DataRecorder>  recorderList;
	private MarketData liveMarketData;
	protected Calendar startDate;
	protected Calendar endDate;
	
	public MarketDataManager(Calendar startDate, Calendar endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
		marketDataList = new ArrayList<>();
		algoList = new ArrayList<>();
		recorderList = new ArrayList<>();
		init();
	}
	
	public MarketDataManager(){
		this(null, null);
	}
	
	
	protected void addMarketData(MarketData marketData){
		marketDataList.add(marketData);
	}
	
	protected void removeMarketData(MarketData marketData){
		marketDataList.remove(marketData);
	}
	
	public void loadAlgo(TradingAlgorithm algo) {
		algoList.add(algo);
		Log.info(this, "Algorithm loaded: "+algo.getClass().getName());
	}
	
	public void loadRecorder(DataRecorder recorder) {
		recorderList.add(recorder);
		Log.info(this, "Recorder loaded: "+ recorder.getClass().getName());
		
	}
	
	//Loads all recorders into MarketData
	protected void loadRecorders(MarketData marketData){
		
		for(DataRecorder recorder: recorderList){
			marketData.subscribe(recorder);
		}
	}
	
	//Loads all algos into MarketData
	protected void loadAlgos(MarketData marketData){
		
		for(TradingAlgorithm algo: algoList){
			marketData.subscribe(algo);
		}
	}
	
	public MarketData getLiveMarketData() {
		return liveMarketData;
	}

	protected void setLiveMarketData(MarketData liveMarketData) {
		this.liveMarketData = liveMarketData;
	}

	//Area to add all required Market Data preparation logic
	protected abstract void init();
	
	
	
	public void execute(){
		loadAlgos(getLiveMarketData());
		loadRecorders(getLiveMarketData());
		for(MarketData data : marketDataList){
		data.start();	
		}
	}
}
