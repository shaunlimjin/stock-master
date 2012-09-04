package stockmaster.marketdata;

import stockmaster.unit.StockData;
import stockmaster.util.Log;

import java.io.*;

public class ReplayCSVMarketDataImpl extends MarketData {

	File dataFile;
	private String selectedDate,selectedMarket,selectedFolder,line;
	private BufferedReader reader;
	private FileReader freader;
	private String[] itemList;
	
	public ReplayCSVMarketDataImpl(String folder, String date, String market) {
		selectedDate = date;
		selectedMarket = market;
		selectedFolder = folder;
	}

	public ReplayCSVMarketDataImpl() {
		super();
	}

	@Override
	public void populateData() {
		int i=0;
		StockData stockData;
		float floatValue;
		try {
			while((line = reader.readLine())!=null){			
				itemList = line.split(",");
                //if stock does not already exist in marketData's hashtable, put it there.
				if(!marketData.containsKey(itemList[11])){
					stockData = new StockData();
					stockData.setStockCode(itemList[11]);
					stockData.setStockName(itemList[12]);
			
					//Log.debug(this, stockData.toString());
					marketData.put(stockData.getStockCode(), stockData);															
				}
				
				
					stockData = marketData.get(itemList[11]);
					stockData.clearFieldChangedList();
					
					floatValue = Float.parseFloat(itemList[0]);

					if(stockData.getBuyPrice()!=floatValue){
						stockData.setBuyPrice(floatValue);
					}
					
					floatValue = Float.parseFloat(itemList[1]);
					
					if(stockData.getBuyVolume()!=floatValue){
						stockData.setBuyVolume(floatValue);
					}
					

					floatValue = Float.parseFloat(itemList[2]);
					
					if(stockData.getHighPrice()!=floatValue){
						stockData.setHighPrice(floatValue);
					}
					

					floatValue = Float.parseFloat(itemList[3]);
					
					if(stockData.getLastPrice()!=floatValue){
						stockData.setLastPrice(floatValue);
					}
					

					floatValue = Float.parseFloat(itemList[4]);
					
					if(stockData.getLowPrice()!=floatValue){
						stockData.setLowPrice(floatValue);
					}
					
					floatValue = Float.parseFloat(itemList[5]);
					
					if(stockData.getOpenPrice()!=floatValue){
						stockData.setOpenPrice(floatValue);
					}
					
	
					floatValue = Float.parseFloat(itemList[6]);
					
					if(stockData.getPercentChange()!=floatValue){
						stockData.setPercentChange(floatValue);
					}
					
					
					if(stockData.getRemarks()!= itemList[7]){
						stockData.setRemarks(itemList[7]);
					}
					
					
					if(stockData.getSector()!= itemList[8]){
						stockData.setSector(itemList[8]);
					}
					

					floatValue = Float.parseFloat(itemList[9]);
					
					if(stockData.getSellPrice()!=floatValue){
						stockData.setSellPrice(floatValue);
					}
					

					floatValue = Float.parseFloat(itemList[10]);
					
					if(stockData.getSellVolume()!=floatValue){
						stockData.setSellVolume(floatValue);
					}
					
					

					floatValue = Float.parseFloat(itemList[13]);
					
					if(stockData.getValue()!=floatValue){
						stockData.setValue(floatValue);
					}
					

					floatValue = Float.parseFloat(itemList[14]);
					
					if(stockData.getValueChange()!=floatValue){
						stockData.setValueChange(floatValue);
					}
					

					floatValue = Float.parseFloat(itemList[15]);
					
					if(stockData.getVolume()!=floatValue){
						stockData.setVolume(floatValue);
					}
					
					if (stockData.wasUpdated()) { // inform subscribers that stock
						// has updated fields
						Log.debug(this, "Stock updated "+stockData.getStockName()+" ("+stockData.getStockCode()+")! Notifying subscribers.");
						stockChange(stockData);
					}
				
				
				
				i++;
			}
			Log.debug(this, "Total stock parsed : " + Integer.toString(i));
		} catch (Exception e) {
			Log.error(this, e.toString());
		}
		finally {
			try {
				reader.close();
			} catch (IOException e) {
			}
			
			try {
				freader.close();
			} catch (IOException e) {
			}
		}

	}

	@Override
	public void refresh() {
		populateData();

	}

	@Override
	public void init() {
		try{
			dataFile = new File(selectedFolder + "/" + selectedDate + "_" + selectedMarket + ".csv");
			freader = new FileReader(dataFile);
			reader = new BufferedReader(freader);
			
			marketData.clear();
		}catch (Exception e)
		{
			Log.error(this, e.toString());
		}

	}
	
	@Override
	public void start(){
		Log.debug(this, "Starting market data..");
		init();
		populateData();
	}
}
