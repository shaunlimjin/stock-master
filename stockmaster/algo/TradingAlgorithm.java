package stockmaster.algo;

import java.lang.reflect.Field;
import java.util.Hashtable;

import stockmaster.StockMaster;
import stockmaster.manager.RiskManager;
import stockmaster.marketdata.MarketData;
import stockmaster.marketdata.MarketDataSubscriber;
import stockmaster.util.Log;

/*
 * TradingAlgorithm
 * 
 * All algorithms should extend this class.
 */
public abstract class TradingAlgorithm implements MarketDataSubscriber {

	protected StockMaster stockManager;
	protected RiskManager riskManager;

	// ### AUTOMATED ALGO TESTING VARIABLES ###
	protected boolean isAlgoTesting;
	protected int noOfProfit;
	protected int noOfLoss;
	protected Hashtable<String, AlgoTestUnit> algoTestParameters;

	// Automatically subscribes to a particular MarketData for stock changes
	public TradingAlgorithm(StockMaster stockManager, int refreshTime) {
		this.stockManager = stockManager;
		algoTestParameters = new Hashtable<String, AlgoTestUnit>();

		riskManager = RiskManager.getInstance();
	//	stockManager.getMarketDataManager().setRefreshTime(refreshTime);
		stockManager.getMarketDataManager().loadAlgo(this);
	}

	public void executeBuy(String stockCode, double price) {
		riskManager.executeSell(stockCode, price);
	}
	
	public void executeSell(String stockCode, double price) {
		riskManager.executeSell(stockCode, price);
	}
	
	// Method to clear all data in Algo. This is required for automated algo testing.
	public abstract void reset();
	
	
	// #### AUTOMATED ALGO TESTING METHODS ###
	public abstract void initAlgoTestParameters();

	public void startAlgoTest() {
		Log.algoTesting(this, "Starting algo testing for "+this.getClass().getName());
		
		isAlgoTesting = true;
		
		try {
			initAlgoTestParameters();
			
			Class c = this.getClass();

			Log.algoTesting(this, "AlgoParameter: MIN, MAX, INCREMENT");
			
			for (String fieldName : algoTestParameters.keySet()) {
				Field field = c.getDeclaredField(fieldName);
				field.setAccessible(true);
				field.setDouble(this, algoTestParameters.get(fieldName).MIN_VALUE);
				algoTestParameters.get(fieldName).ALGO_FIELD = field;
				
				Log.algoTesting(this, fieldName+": "+algoTestParameters.get(fieldName).MIN_VALUE+", "+algoTestParameters.get(fieldName).MAX_VALUE+", "+algoTestParameters.get(fieldName).INCREMENT);
			}	
			
			do {
				Log.algoTesting(this, "[Testing Parameters]");
				
				for (String fieldName : algoTestParameters.keySet()) {
					Log.algoTesting(this, fieldName+":"+algoTestParameters.get(fieldName).ALGO_FIELD.getDouble(this));
				}	
				stockManager.getMarketDataManager().execute();
				
				Log.algoTesting(this, "Profit: "+noOfProfit+" Loss: "+noOfLoss);
				
				reset();
				noOfProfit = 0;
				noOfLoss = 0;
			}
			while (!cycleParameters());
			
		} catch (SecurityException e) {
			Log.error(this, e.toString());
		} catch (NoSuchFieldException e) {
			Log.error(this, e.toString());
		} catch (IllegalArgumentException e) {
			Log.error(this, e.toString());
		} catch (IllegalAccessException e) {
			Log.error(this, e.toString());
		}
		finally {
			isAlgoTesting = false;
		}
		
		Log.algoTesting(this, "Algo testing completed.");
	}

	// responsibility to traverse all parameters
	private boolean cycleParameters() {
		try {
			// terminating condition
			boolean overallCondition = true;
			for (AlgoTestUnit algoUnit : algoTestParameters.values()) {
				if (algoUnit.ALGO_FIELD.getDouble(this) >= algoUnit.MAX_VALUE);
				else {
					overallCondition = false;
					break;
				}
			}
			
			// terminate since we have traversed all parameters
			if (overallCondition) 
				return true;
			
			Object[] algoUnitArray = algoTestParameters.values().toArray();
			
			// move first algo unit to the next increment
			AlgoTestUnit algoUnit = (AlgoTestUnit) algoUnitArray[0];
			algoUnit.ALGO_FIELD.setDouble(this, algoUnit.ALGO_FIELD.getDouble(this) + algoUnit.INCREMENT);
			
			// logic to reset algo_field to min value
			for (int i = 0; i < algoUnitArray.length; i++) {
				algoUnit = (AlgoTestUnit) algoUnitArray[i];
				
				if (algoUnit.ALGO_FIELD.getDouble(this) > algoUnit.MAX_VALUE) {
					algoUnit.ALGO_FIELD.setDouble(this, algoUnit.MIN_VALUE);
					
					if ((i+1) < algoUnitArray.length) {
						algoUnit = (AlgoTestUnit) algoUnitArray[i+1]; // get next
						algoUnit.ALGO_FIELD.setDouble(this, algoUnit.ALGO_FIELD.getDouble(this) + algoUnit.INCREMENT);
					}
				}
			}
		} catch (IllegalArgumentException e) {
			Log.error(this, e.toString());
		} catch (IllegalAccessException e) {
			Log.error(this, e.toString());
		}

		return false;
	}

	// Class used for automated algo testing
	class AlgoTestUnit {
		double MIN_VALUE;
		double MAX_VALUE;
		double INCREMENT;

		// corresponding field of algo parameter
		Field ALGO_FIELD;

		public AlgoTestUnit(double minValue, double maxValue, double increment) {
			MIN_VALUE = minValue;
			MAX_VALUE = maxValue;
			INCREMENT = increment;
		}
	}
}
