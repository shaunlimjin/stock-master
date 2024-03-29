package stockmaster.marketdata;

import java.io.Console;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import stockmaster.unit.StockData;
import stockmaster.util.Log;
import stockmaster.unit.MarketDataInfo;

/*
 * MarketData
 * 
 * All market data feed should extend this class.
 */
public abstract class MarketData {

	// Value in ms
	public static final int DEFAULT_REFRESH_TIME = 30000;
	
	// Value in ms. Set as -1 to never timeout.
	public static final int DEFAULT_EVENT_TIMEOUT = 30000; 
	
	/// Worker thread to continuously perform events defined by its child classes
	protected WorkerThread workerThread;
	
	//Entity to hold market data info like sentiment and price history
	private MarketDataInfo marketDataInfo;
	
	
	// List of subscribers (typically algorithms) that are interested in being informed of stock changes.
	private ArrayList<MarketDataSubscriber> subscriptionList;
		
	public MarketData() {
		this(DEFAULT_REFRESH_TIME, DEFAULT_EVENT_TIMEOUT, new MarketDataInfo());
	
	}
	
	public MarketData(int refreshTime, int eventTimeout, MarketDataInfo marketDataInfo) {
		subscriptionList = new ArrayList<MarketDataSubscriber>();
		workerThread = new WorkerThread(refreshTime, eventTimeout);	
		this.setMarketDataInfo(marketDataInfo);
	}
	
	public MarketDataInfo getMarketDataInfo() {
		return marketDataInfo;
	}

	public void setMarketDataInfo(MarketDataInfo marketDataInfo) {
		this.marketDataInfo = marketDataInfo;
	}

	// Populate hashtable of stocks with latest prices
	public abstract void populateData();

	
	public void calculateSentiment(StockData data){
		
			if(getMarketDataInfo().getMarketSentiment().containsKey(data.getStockCode())){
				double value = getMarketDataInfo().getMarketSentiment().get(data.getStockCode());
					Log.debug(this, "Old Price :" + value);
					Log.debug(this, "New Price :" + data.getLastPrice());
					
				if(data.getLastPrice()>value){
					getMarketDataInfo().setOverallSentiment(getMarketDataInfo().getOverallSentiment()+data.getSentimentWeight());
				    data.setSentiment(data.getSentiment() + 1);
				}else if(data.getLastPrice()<value){
					getMarketDataInfo().setOverallSentiment(getMarketDataInfo().getOverallSentiment()-data.getSentimentWeight());
					data.setSentiment(data.getSentiment() - 1);
				}
											
				getMarketDataInfo().getMarketSentiment().put(data.getStockCode(), data.getLastPrice());
				Log.debug(this, data.getStockCode() + " Sentiment : " + data.getSentiment());
			}
			else{
				getMarketDataInfo().getMarketSentiment().put(data.getStockCode(),data.getLastPrice());
			}
	
	}

	// Logic to retrieve latest prices
	public abstract void refresh();	
	
	// This method is called by a thread every x seconds. 
	// Override this method to perform routine work / tap into the thread
	public void event() {
		refresh();
		Log.info(this, "Current market sentiment is : " + getMarketDataInfo().getOverallSentiment());
	}
	
	// This method is called to initialize the object upon creation.
	public abstract void init();
	
	// Inform subscribers that a stock has updated values (e.g. price changes, volume changes etc.) 
	// Subscribers are typically Algorithms which are interested in being informed on price changes.
	public void stockChange(StockData stock) {
		calculateSentiment(stock);		
		for (MarketDataSubscriber obj : subscriptionList)
			obj.stockChange(stock);
	}
	
	public void subscribe(MarketDataSubscriber obj) {
		subscriptionList.add(obj);
	}
	 
	public void unsubscribe(MarketDataSubscriber obj) {
		subscriptionList.remove(obj);
	}
	
	// Sets the refresh timing of market data
	public void setRefreshTime(int refreshTime) {
		workerThread.sleepTime = refreshTime;
	}

	public int getRefreshTime(int refreshTime) {
		return workerThread.sleepTime;
	}
	
	public void start() {
		Log.debug(this, "Starting market data..");
		init();
		workerThread.start();
	}
	
	public void stop() {
		Log.debug(this, "Stopping market data..");
		workerThread.stopWorker();
	}
	
	// Worker thread to continuously perform events defined by its child classes
	class WorkerThread extends Thread {
		
		private boolean isRunning;
		
		// Create an ExecutorService to manage timeout of event tasks.
		private ExecutorService executor;
		
		// Create a class for managing thread timeout
		private Runnable marketDataEvent = new Runnable() {
			public void run() {
				MarketData.this.event();
			}
		};
		
		int sleepTime;
		int eventTimeout;
		
		public WorkerThread(int sleepTime, int eventTimeout) {
			executor = Executors.newCachedThreadPool();
			
			this.sleepTime = sleepTime;
			this.eventTimeout = eventTimeout;
		}
		
		public void run() {
			isRunning = true;
			
			Log.debug(this, "Thread started.");
			
			while (isRunning) {
				
				doWorkWithTimeout(eventTimeout);
				try {
					Log.info(this, "Going to sleep for "+sleepTime+"ms");
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					Log.error(this, e.toString());
				}
			}
			
			Log.debug(this, "MarketData event stopped.");
		}
		
		public void stopWorker() {
			isRunning = false;
		}
		
		public void doWorkWithTimeout(int timeout) {
			Future<?> future = null;
			
			try {
				future = executor.submit(marketDataEvent);
				
				if (timeout < 0) // wait indefinitely for marketDataEvent to complete
					future.get();
				else // wait for x seconds for marketDataEvent to complete
					future.get(timeout, TimeUnit.MILLISECONDS);
			}
			catch (TimeoutException e) {
				if (future != null)
					future.cancel(true); // clean up thread
			
				Log.error(this, "event() method timeout ("+timeout+"ms). Relaunching thread.");
				doWorkWithTimeout(timeout);
			}
			catch (Exception e) {
				Log.error(this, "Unknown exception.");
				Log.error(this, e.toString());
			}
			finally {
				if (future != null)
					future.cancel(true); // clean up thread
			}
		}
	}
}
