package stockmaster.marketdata;

import stockmaster.unit.MarketDataInfo;
import stockmaster.unit.StockData;
import stockmaster.util.Log;
import java.util.ArrayList;
import java.io.*;
import java.util.Date;

public class ReplayCSVMarketDataImpl extends MarketData {

	File dataFile;
	private String selectedDate,selectedMarket,selectedFolder,line;
	private BufferedReader reader;
	private FileReader freader;
	private String[] itemList;
	private ArrayList<String> fileList;
	
	public ReplayCSVMarketDataImpl(String folder, ArrayList<String> date, String market, MarketDataInfo marketDataInfo) {
		fileList = date;
		selectedMarket = market;
		selectedFolder = folder;
		this.setMarketDataInfo(marketDataInfo);
	}

	public ReplayCSVMarketDataImpl() {
		super();
	}

	@Override
	public void populateData() {
		int i=0;
		StockData stockData;
		double doubleValue;
		Date updated;
		try {
			while((line = reader.readLine())!=null){			
				itemList = line.split(",");
                //if stock does not already exist in marketData's hashtable, put it there.
				if(!getMarketDataInfo().getMarketData().containsKey(itemList[11])){
					stockData = new StockData();
					stockData.setStockCode(itemList[11]);
					stockData.setStockName(itemList[12]);
			
					//Log.debug(this, stockData.toString());
					getMarketDataInfo().getMarketData().put(stockData.getStockCode(), stockData);															
				}
				
				
					stockData = getMarketDataInfo().getMarketData().get(itemList[11]);
					stockData.clearFieldChangedList();
					
					doubleValue = Double.parseDouble(itemList[0]);

					if(stockData.getBuyPrice()!=doubleValue){
						stockData.setBuyPrice(doubleValue);
					}
					
					doubleValue = Double.parseDouble(itemList[1]);
					
					if(stockData.getBuyVolume()!=doubleValue){
						stockData.setBuyVolume(doubleValue);
					}
					

					doubleValue = Double.parseDouble(itemList[2]);
					
					if(stockData.getHighPrice()!=doubleValue){
						stockData.setHighPrice(doubleValue);
					}
					

					doubleValue = Double.parseDouble(itemList[3]);
					
					if(stockData.getLastPrice()!=doubleValue){
						stockData.setLastPrice(doubleValue);
					}
					

					doubleValue = Double.parseDouble(itemList[4]);
					
					if(stockData.getLowPrice()!=doubleValue){
						stockData.setLowPrice(doubleValue);
					}
					
					doubleValue = Double.parseDouble(itemList[5]);
					
					if(stockData.getOpenPrice()!=doubleValue){
						stockData.setOpenPrice(doubleValue);
					}
					
	
					doubleValue = Double.parseDouble(itemList[6]);
					
					if(stockData.getPercentChange()!=doubleValue){
						stockData.setPercentChange(doubleValue);
					}
					
					
					if(stockData.getRemarks()!= itemList[7]){
						stockData.setRemarks(itemList[7]);
					}
					
					
					if(stockData.getSector()!= itemList[8]){
						stockData.setSector(itemList[8]);
					}
					

					doubleValue = Double.parseDouble(itemList[9]);
					
					if(stockData.getSellPrice()!=doubleValue){
						stockData.setSellPrice(doubleValue);
					}
					

					doubleValue = Double.parseDouble(itemList[10]);
					
					if(stockData.getSellVolume()!=doubleValue){
						stockData.setSellVolume(doubleValue);
					}
					
					

					doubleValue = Double.parseDouble(itemList[13]);
					
					if(stockData.getValue()!=doubleValue){
						stockData.setValue(doubleValue);
					}
					

					doubleValue = Double.parseDouble(itemList[14]);
					
					if(stockData.getValueChange()!=doubleValue){
						stockData.setValueChange(doubleValue);
					}
					

					doubleValue = Double.parseDouble(itemList[15]);
					
					if(stockData.getVolume()!=doubleValue){
						stockData.setVolume(doubleValue);
					}

					updated = Log.getFormattedDateTime(itemList[16]);
					stockData.setLastUpdate(updated);
					
					
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

	}
	
	@Override
	public void start(){
		Log.debug(this, "Starting market data..");
		init();
		
		for(String selectedDate: fileList){
			
			try{
				dataFile = new File(selectedFolder + "/" + selectedDate + "_" + selectedMarket + ".csv");
				freader = new FileReader(dataFile);
				reader = new BufferedReader(freader);
				
				//getMarketDataInfo().getMarketData().clear();
			}catch (Exception e)
			{
				e.printStackTrace();
			}
			populateData();
			
		}
		
		
	}
}
