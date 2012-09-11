package stockmaster.manager;

import java.util.ArrayList;

import com.sun.org.apache.bcel.internal.generic.ALOAD;

import stockmaster.algo.TradingAlgorithm;
import stockmaster.marketdata.*;
import stockmaster.recorder.DataRecorder;
import stockmaster.unit.MarketDataInfo;
import stockmaster.util.Log;

//Class to handle all MarketData interactions

public class MarketDataManager {

	private ArrayList<MarketData> marketDataList;
	private ArrayList<TradingAlgorithm> algoList;
	private ArrayList<DataRecorder>  recorderList;
	private MarketData liveMarketData;
	
	public MarketDataManager() {
	
		marketDataList = new ArrayList<>();
		algoList = new ArrayList<>();
		recorderList = new ArrayList<>();
		init();
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

	public void setLiveMarketData(MarketData liveMarketData) {
		this.liveMarketData = liveMarketData;
	}

	//Area to add all required Market Data preparation logic
	protected void init(){
		
		//MarketDataEmulatorImpl init
			//MarketDataEmulatorImpl emuData = new MarketDataEmulatorImpl(Market.NEUTRAL, new MarketDataInfo())
			//addMarketData(emuData);
				
		
		//Mongo Replayer init
			//ReplayMongoMarketDataImpl mogData = new ReplayMongoMarketDataImpl("sgx", Log.getFormattedDateTime("20120904 22:00:00"), Log.getFormattedDateTime("20120924 22:15:00"), new MarketDataInfo());
		
		// Replayer SGX Web Market Data init
				ArrayList<String> dateList = new ArrayList<String>();
				dateList.add("20120904");
				ReplayCSVMarketDataImpl rplData = new ReplayCSVMarketDataImpl("FeedData//", dateList, "SGX", new MarketDataInfo());
				//addMarketData(rplData);
				
				
		//Final executing market data should be new using liveMarketData
				setLiveMarketData(new SGXWebMarketDataImpl(rplData.getMarketDataInfo()));			
				addMarketData(getLiveMarketData());
				
				
	}
	
	
	
	public void execute(){
		loadAlgos(getLiveMarketData());
		loadRecorders(getLiveMarketData());
		for(MarketData data : marketDataList){
		data.start();	
		}
	}
}
