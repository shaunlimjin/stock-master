package stockmaster.marketdata;

import java.util.ArrayList;
import java.util.Hashtable;

import stockmaster.unit.StockData;
import stockmaster.util.Log;

/*
 * MarketData
 * 
 * All market data feed should extend this class.
 */
public abstract class MarketData {

	public static final int DEFAULT_REFRESH_TIME = 30000;
	
	/// Worker thread to continuously perform events defined by its child classes
	protected WorkerThread workerThread;
	
	// List of stock details - stockCode, stockData
	protected Hashtable<String,StockData> marketData;
	
	// List of subscribers (typically algorithms) that are interested in being informed of stock changes.
	private ArrayList<MarketDataSubscriber> subscriptionList;
	
	public MarketData() {
		this(DEFAULT_REFRESH_TIME);
	}
	
	public MarketData(int refreshTime) {
		subscriptionList = new ArrayList<MarketDataSubscriber>();
		marketData = new Hashtable<String, StockData>();
		workerThread = new WorkerThread(refreshTime);	
	}
	
	// Populate hashtable of stocks with latest prices
	public abstract void populateData();

	public Hashtable<String,StockData> getMarketData() {
		return marketData;
	}

	// Logic to retrieve latest prices
	public abstract void refresh();	
	
	// This method is called by a thread every x seconds. 
	// Use this method to perform routine work (e.g. call refresh())
	public abstract void event();
	
	// This method is called to initialize the object upon creation.
	public abstract void init();
	
	// Inform subscribers that a stock has updated values (e.g. price changes, volume changes etc.) 
	// Subscribers are typically Algorithms which are interested in being informed on price changes.
	public void stockChange(StockData stock) {
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
		int sleepTime;
		
		public WorkerThread(int sleepTime) {
			this.sleepTime = sleepTime;
		}
		
		public void run() {
			isRunning = true;
			
			Log.debug(this, "Thread started.");
			
			while (isRunning) {
				MarketData.this.event();
				
				try {
					Log.debug(this, "Going to sleep for "+sleepTime+"ms");
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
	}
}
