package stockmaster.recorder;

import stockmaster.util.Log;
import stockmaster.marketdata.MarketDataSubscriber;
import stockmaster.unit.StockData;
import java.util.Date;
import java.text.SimpleDateFormat;

//Recorder classes should extend this class

public abstract class DataRecorder implements MarketDataSubscriber {
	
	private Date startDate;
	private Date endDate;
	
	protected DataRecorder(Date startDate, Date endDate){
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public void stockChange(StockData stockCode){
		Date now = new Date();
		if(now.after(startDate) && now.before(endDate)){
			this.record(stockCode);
		}else
			Log.info(this, "Current DateTime:  " + now  + " is out of recording time range");
	}
	
	//Future implementation can be to a database, xml etc etc...
	protected abstract void record(StockData data);
}
