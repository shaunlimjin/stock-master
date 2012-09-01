package stockmaster.recorder;

import stockmaster.util.Log;
import stockmaster.marketdata.MarketDataSubscriber;
import stockmaster.unit.StockData;
import java.util.Date;
import java.text.SimpleDateFormat;

//Recorder classes should extend this class

public abstract class DataRecorder implements MarketDataSubscriber {

	
	public void stockChange(StockData stockCode){
		this.record(stockCode);
	}
	
	//Future implementation can be to a database, xml etc etc...
	protected abstract void record(StockData data);
}
