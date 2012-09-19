package stockmaster.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import stockmaster.marketdata.ReplayCSVMarketDataImpl;
import stockmaster.marketdata.SGXWebMarketDataImpl;
import stockmaster.unit.MarketDataInfo;
import stockmaster.util.Log;

public class CSVSGXMarketDataManagerImpl extends MarketDataManager {

	public CSVSGXMarketDataManagerImpl(Calendar startDate, Calendar endDate) {
		super(startDate, endDate);
	
	}

	public CSVSGXMarketDataManagerImpl() {
		super();
	}

	//Uses CSV replayer to preload data to fill up the MarketDataInfo
	//If dates are null, it will just use Sgx marketdata with a new instance of MarketDataInfo
	
	@Override
	protected void init() {
			
		if(startDate != null && endDate != null){
			
			if(endDate.after(startDate)){
		
		ArrayList<String> dateList = calulateDates(startDate, endDate);	
		ReplayCSVMarketDataImpl rplData = new ReplayCSVMarketDataImpl("FeedData//", dateList, "SGX", new MarketDataInfo());
		addMarketData(rplData);
		setLiveMarketData(new SGXWebMarketDataImpl(rplData.getMarketDataInfo()));			
		addMarketData(getLiveMarketData());
		}else{
			throw new IllegalArgumentException("Error in input dates");
			}
		}

		else{
			setLiveMarketData(new SGXWebMarketDataImpl(new MarketDataInfo()));
			addMarketData(getLiveMarketData());
		}
			

	}
	
	private ArrayList<String> calulateDates(Calendar startDate, Calendar endDate){
	
		ArrayList<String> dateList = new ArrayList<String>();
		while(!startDate.after(endDate)){
			if(!(startDate.DAY_OF_WEEK==Calendar.SUNDAY) && (startDate.DAY_OF_WEEK==Calendar.SATURDAY)){
			dateList.add(Log.formatDateTime(startDate));
			}
			startDate.add(Calendar.DAY_OF_MONTH, 1);	
		}
		
		return dateList;
	}

}
