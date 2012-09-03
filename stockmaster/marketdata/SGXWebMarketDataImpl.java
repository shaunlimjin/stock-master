package stockmaster.marketdata;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import stockmaster.unit.StockData;
import stockmaster.util.Log;

/*
 * Logic to scrape SGX Website for market data
 */
public class SGXWebMarketDataImpl extends MarketData {

	public static final int EVENT_TIMEOUT = 10000;
	public static final int REFRESH_TIME = 30000;
	
	private URL jsonURL;
	private JsonFactory jsonFactory;
	private JsonParser jsonParser;

	public SGXWebMarketDataImpl() {
		super(REFRESH_TIME, EVENT_TIMEOUT);
	}

	public void init() {
		try {
			jsonURL = new URL(
					"http://sgx.com/JsonRead/JsonData?qryId=RStock&timeout=30&%20noCache=1345409928980.862806.0060484405");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		jsonFactory = new JsonFactory();
	}

	public void populateData() {
		try {
			Log.info(this, "Repopulating data...");
			
			jsonParser = jsonFactory.createJsonParser(jsonURL);
			jsonParser.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
			jsonParser.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);

			// Ignore all fields till 'items'
			while (true) {
				try {
					jsonParser.nextToken();
					// System.out.println(jsonParser.getCurrentName()+" - "+jsonParser.getText());
				} catch (Exception e) {
				}

				if (jsonParser.getText().equals("items")) {
					jsonParser.nextToken(); // start of object
					break;
				}
			}

			// found items list. start by traversing each item in the list
			while (jsonParser.nextToken() != JsonToken.END_OBJECT && !jsonParser.getText().equals("]")) {
				moveToNextValue(jsonParser); // move to ID
				moveToNextValue(jsonParser); // move to stockName

				String stockName = jsonParser.getText();

				moveToNextValue(jsonParser); // move to SIP
				moveToNextValue(jsonParser); // move to StockCode

				String stockCode = jsonParser.getText();

				StockData stock;

				// if stock already exist, update prices only
				if (marketData.containsKey(stockCode)) {
					Log.debug(this, stockCode
							+ " already exist. Retrieving record..");
					stock = marketData.get(stockCode);
					stock.clearFieldChangedList(); // we will be re-populating
													// updated field list
				} else { // create new stock object and populate all fields
					Log.debug(this, "Creating new object: " + stockCode + " ("
							+ stockName + ")");
					stock = new StockData();
					stock.setStockName(stockName);
					stock.setStockCode(stockCode);
					marketData.put(stockCode, stock);
				}

				// traverse an item
				do {
					String value;
					
					moveToNextValue(jsonParser); // move to remarks
					value = jsonParser.getText();
					if (!value.equals(stock.getRemarks())) {
						stock.setRemarks(value);
					}

					moveToNextValue(jsonParser); // move to I
					moveToNextValue(jsonParser); // move to M

					moveToNextValue(jsonParser); // move to Last Traded;
					value = jsonParser.getText();
			
					float floatValue;
					
					if (value.equals(""))
						floatValue = 0;
					else
						floatValue = Float.parseFloat(value);
					
					if (stock.getLastPrice() != floatValue) {
						stock.setLastPrice(floatValue);
					}

					moveToNextValue(jsonParser); // move to Change
					value = jsonParser.getText();
					
					if (value.equals(""))
						floatValue = 0;
					else
						floatValue = Float.parseFloat(value);
					
					if (stock.getValueChange() != floatValue) {
						stock.setValueChange(floatValue);
					}

					moveToNextValue(jsonParser); // move to volume
					value = jsonParser.getText();
					
					if (value.equals(""))
						floatValue = 0;
					else
						floatValue = Float.parseFloat(value);
					
					if (stock.getVolume() != floatValue) {
						stock.setVolume(floatValue);
					}

					moveToNextValue(jsonParser); // move to buyVolume
					value = jsonParser.getText();
					
					if (value.equals(""))
						floatValue = 0;
					else
						floatValue = Float.parseFloat(value);
					
					if (stock.getBuyVolume() != floatValue) {
						stock.setBuyVolume(floatValue);
					}

					moveToNextValue(jsonParser); // move to buy
					
					value = jsonParser.getText();
					
					if (value.equals(""))
						floatValue = 0;
					else
						floatValue = Float.parseFloat(value);
			
					if (stock.getBuyPrice() != floatValue) {
						stock.setBuyPrice(floatValue);
					}

					moveToNextValue(jsonParser); // move to sell
					value = jsonParser.getText();
					
					if (value.equals(""))
						floatValue = 0;
					else
						floatValue = Float.parseFloat(value);
					
					if (stock.getSellPrice() != floatValue) {
						stock.setSellPrice(floatValue);
					}

					moveToNextValue(jsonParser); // move to sell volume
					value = jsonParser.getText();
					
					if (value.equals(""))
						floatValue = 0;
					else
						floatValue = Float.parseFloat(value);
					
					if (stock.getSellVolume() != floatValue) {
						stock.setSellVolume(floatValue);
					}

					moveToNextValue(jsonParser); // move to open
					value = jsonParser.getText();
					
					if (value.equals(""))
						floatValue = 0;
					else
						floatValue = Float.parseFloat(value);
					
					if (!value.equals("-")
							&& stock.getOpenPrice() != floatValue) {
						stock.setOpenPrice(floatValue);
					}

					moveToNextValue(jsonParser); // move to high
					value = jsonParser.getText();
					
					if (value.equals(""))
						floatValue = 0;
					else
						floatValue = Float.parseFloat(value);
					
					if (!value.equals("-")
							&& stock.getHighPrice() != floatValue) {
						stock.setHighPrice(floatValue);
					}

					moveToNextValue(jsonParser); // move to low
					value = jsonParser.getText();
					
					if (value.equals(""))
						floatValue = 0;
					else
						floatValue = Float.parseFloat(value);
					
					if (!value.equals("-")
							&& stock.getLowPrice() != floatValue) {
						stock.setLowPrice(floatValue);
					}

					moveToNextValue(jsonParser); // move to value
					value = jsonParser.getText();
					
					if (value.equals(""))
						floatValue = 0;
					else
						floatValue = Float.parseFloat(value);
					
					if (!value.equals("-")
							&& stock.getValue() != floatValue) {
						stock.setValue(floatValue);
					}

					moveToNextValue(jsonParser); // move to sector
					value = jsonParser.getText();
					if (!value.equals(stock.getSector())) {
						stock.setSector(value);
					}

					moveToNextValue(jsonParser); // move to PV?
					moveToNextValue(jsonParser); // move to PTDate?

					moveToNextValue(jsonParser); // move to percentageChange
					value = jsonParser.getText();
					
					if (value.equals(""))
						floatValue = 0;
					else
						floatValue = Float.parseFloat(value);
					
					if (stock.getPercentChange() != floatValue) {
						stock.setPercentChange(floatValue);
					}

					moveToNextValue(jsonParser); // move to P_?
					moveToNextValue(jsonParser); // move to V_?

					Log.debug(this, stock.toString());

					if (stock.wasUpdated()) { // inform subscribers that stock
												// has updated fields
						Log.info(this, "Stock updated "+stock.getStockName()+" ("+stock.getStockCode()+")! Notifying subscribers.");
						stockChange(stock);
					}
				} while (jsonParser.nextToken() != JsonToken.END_OBJECT);
			}

		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.debug(this, "Stock list size: "+marketData.size());
	}

	// Utility method to move to value instead of field followed by value.
	private void moveToNextValue(JsonParser jsonParser)
			throws JsonParseException, IOException {
		jsonParser.nextToken(); // move to field name
		jsonParser.nextToken(); // move to value
	}

	@Override
	public void refresh() {
		populateData();
	}
}
