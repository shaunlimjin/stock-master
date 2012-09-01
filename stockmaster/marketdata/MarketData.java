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
	
	// List of stock details - stockCode, stockData
	protected Hashtable<String,StockData> marketData;
	
	// List of subscribers (typically algorithms) that are interested in being informed of stock changes.
	private ArrayList<MarketDataSubscriber> subscriptionList;
	
	//Sentiment value of the entire market
	private float overallSentiment;
	
	//Sentiment values of the individual counters
	private Hashtable<String, Float> marketSentiment;
	
	public MarketData() {
		this(DEFAULT_REFRESH_TIME, DEFAULT_EVENT_TIMEOUT);
	}
	
	public MarketData(int refreshTime, int eventTimeout) {
		subscriptionList = new ArrayList<MarketDataSubscriber>();
		marketData = new Hashtable<String, StockData>();
		workerThread = new WorkerThread(refreshTime, eventTimeout);	
		marketSentiment = new Hashtable<String, Float>();
		setOverallSentiment(0);
		marketSentiment.clear();
		
	}
	
	// Populate hashtable of stocks with latest prices
	public abstract void populateData();

	public Hashtable<String,StockData> getMarketData() {
		return marketData;
	}
	
	public Hashtable<String, Float> getMarketSentiment() {
		return marketSentiment;
	}

	// retrieve market sentiment
	public float getOverallSentiment() {
		return overallSentiment;
	}

	public void setOverallSentiment(float f) {
		this.overallSentiment = f;
	}
	
	public void resetSentiment(){
		setOverallSentiment(0);
		marketSentiment.clear();
	}
	
	public void calculateSentiment(StockData data){
		
			if(marketSentiment.containsKey(data.getStockCode())){
				float value = marketSentiment.get(data.getStockCode());
					Log.debug(this, "Old Price :" + value);
					Log.debug(this, "New Price :" + data.getLastPrice());
					
				if(data.getLastPrice()>value){
				    setOverallSentiment(getOverallSentiment()+data.getSentimentWeight());
				    data.setSentiment(data.getSentiment() + 1);
				}else if(data.getLastPrice()<value){
					setOverallSentiment(getOverallSentiment()-data.getSentimentWeight());
					data.setSentiment(data.getSentiment() - 1);
				}
											
				marketSentiment.put(data.getStockCode(), data.getLastPrice());
				Log.debug(this, data.getStockCode() + " Sentiment : " + data.getSentiment());
			}
			else{
				marketSentiment.put(data.getStockCode(),data.getLastPrice());
			}
	
	}

	// Logic to retrieve latest prices
	public abstract void refresh();	
	
	// This method is called by a thread every x seconds. 
	// Override this method to perform routine work / tap into the thread
	public void event() {
		refresh();
		Log.info(this, "Current market sentiment is : " + getOverallSentiment());
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
					e.printStackTrace();
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
				e.printStackTrace();
			}
			finally {
				if (future != null)
					future.cancel(true); // clean up thread
			}
		}
	}
}
