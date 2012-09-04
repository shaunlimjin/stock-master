package stockmaster.recorder;

import stockmaster.unit.StockData;
import stockmaster.util.Log;

import java.io.*;
import java.util.Date;

public class CSVFileRecorderImpl extends DataRecorder {

	private String path, marketName;
	private File file;
	private FileWriter writer;
	
	public CSVFileRecorderImpl(String filePath, String market, Date startDate, Date endDate)  {
		
		super(startDate, endDate);
		
		path = filePath;
		marketName = market;
					
		if (!new File(filePath).exists())
		{
			path = "FeedData";
			Log.debug(this, "Path not found, setting to default");
			new File(path).mkdir();
			file = new File(path + "/" + Log.getCurrentDate(new Date()) + "_" + marketName + ".csv");
		}else
		{
			file = new File(path + Log.getCurrentDate(new Date()) + "_" + marketName + ".csv");
		}
		marketName = market;
		try {
			writer = new FileWriter(file, true);
		} catch (IOException e) {
			Log.error(this, e.toString());
		}
	}

	@Override
	protected void record(StockData data) {
			try{
				Log.debug(this, "Writing to csv stock " + data.getStockName());
				writer.write(data.getBuyPrice()+","+data.getBuyVolume()+","+data.getHighPrice()+","+data.getLastPrice()
						+","+data.getLowPrice()+","+data.getOpenPrice()+","+data.getPercentChange()
						+","+data.getRemarks()+","+data.getSector()+","+data.getSellPrice()+","+data.getSellVolume()
						+","+data.getStockCode()+","+data.getStockName()+","+data.getValue()+","+data.getValueChange()
						+","+data.getVolume()+","+Log.formateDateTime(data.getLastUpdate())+"\n");
				writer.flush();
			}catch(Exception e){
				Log.write(e);
			}
						
	}

}
