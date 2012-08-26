package stockmaster.marketdata;

import stockmaster.unit.StockData;
import stockmaster.util.Log;
import sun.util.logging.resources.logging;

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
		try {
			while((line = reader.readLine())!=null){
				stockData = new StockData();
				itemList = line.split(",");
		
					stockData.setBuyPrice(Float.parseFloat(itemList[0]));
					stockData.setBuyVolume(Float.parseFloat(itemList[1]));
					stockData.setHighPrice(Float.parseFloat(itemList[2]));
					stockData.setLastPrice(Float.parseFloat(itemList[3]));
					stockData.setLowPrice(Float.parseFloat(itemList[4]));
					stockData.setOpenPrice(Float.parseFloat(itemList[5]));
					stockData.setPercentChange(Float.parseFloat(itemList[6]));
					stockData.setRemarks(itemList[7]);
					stockData.setSector(itemList[8]);
					stockData.setSellPrice(Float.parseFloat(itemList[9]));
					stockData.setSellVolume(Float.parseFloat(itemList[10]));
					stockData.setStockCode(itemList[11]);
					stockData.setStockName(itemList[12]);
					stockData.setValue(Float.parseFloat(itemList[13]));
					stockData.setValueChange(Float.parseFloat(itemList[14]));
					stockData.setVolume(Float.parseFloat(itemList[15]));
					
					Log.debug(this, stockData.toString());
					stockChange(stockData);													
			
				i++;
				
			}
			Log.debug(this, "Total stock parsed : " + Integer.toString(i));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.write(e);
		}

	}

	@Override
	public void refresh() {
		populateData();

	}

	@Override
	public void init() {
		try{
			dataFile = new File(selectedFolder + "\\" + selectedDate + "_" + selectedMarket + ".csv");
			freader = new FileReader(dataFile);
			reader = new BufferedReader(freader);
		}catch (Exception e)
		{
			Log.write(e);
		}

	}
	
	@Override
	public void start(){
		Log.debug(this, "Starting market data..");
		init();
		populateData();
	}

}
