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

	// private static final String url =
	// "http://www.sgx.com/wps/portal/sgxweb/home/marketinfo/securities/stocks";
	private URL jsonURL;
	// private static final int TIMEOUT = 18;
	// private WebDriver driver;
	private long timestamp;
	// private WebDriverWait webDriverWait;

	// private ExpectedCondition<WebElement> staticViewCondition;
	// private ExpectedCondition<WebElement> gridHeaderCondition;

	// private ArrayList<PopulateFieldThread> populateFieldThreadList;

	private JsonFactory jsonFactory;
	private JsonParser jsonParser;

	public SGXWebMarketDataImpl() {
		super();
	}

	public void init() {
		try {
			jsonURL = new URL(
					"http://sgx.com/JsonRead/JsonData?qryId=RStock&timeout=30&%20noCache=1345409928980.862806.0060484405");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		jsonFactory = new JsonFactory();
	}

	/*
	 * public void init() { try { //populateFieldThreadList = new
	 * ArrayList<PopulateFieldThread>();
	 * 
	 * // Starts FireFox driver driver = new FirefoxDriver();
	 * driver.manage().timeouts().pageLoadTimeout(TIMEOUT, TimeUnit.SECONDS);
	 * driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
	 * driver.manage().timeouts().setScriptTimeout(TIMEOUT, TimeUnit.SECONDS);
	 * 
	 * // Create a condition to wait for staticview ID in HTML before continuing
	 * execution staticViewCondition = new ExpectedCondition<WebElement>() {
	 * 
	 * @Override public WebElement apply(WebDriver d) { return
	 * d.findElement(By.id("staticview")); } };
	 * 
	 * webDriverWait = new WebDriverWait(driver, TIMEOUT);
	 * 
	 * // Condition to wait for 'gridheader' ID in HTML before continuing
	 * execution gridHeaderCondition = new ExpectedCondition<WebElement>(){
	 * 
	 * @Override public WebElement apply(WebDriver d) { return
	 * d.findElement(By.id("gridheader")); } };
	 * 
	 * // Retrieves SGX website driver.get(url);
	 * 
	 * // Wait for 'gridHeader' ID to load
	 * webDriverWait.until(gridHeaderCondition);
	 * 
	 * // After page is fully loaded, execute java script to retrieve top 20
	 * volume ((JavascriptExecutor)
	 * driver).executeScript("doTops('VL', true, 7, 'Top 20 Vol')");
	 * populateData(); } catch (TimeoutException e) {
	 * Log.info(this,"Timeout. Reloading "+url); driver.quit(); init(); } }
	 */

	public void populateData() {
		try {
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
						Log.debug(this, "Stock updated! Notifying subscribers.");
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

	/*
	 * public void populateData() { try { timestamp =
	 * System.currentTimeMillis(); //((JavascriptExecutor)
	 * driver).executeScript("doAll()"); //((JavascriptExecutor)
	 * driver).executeScript("doTops('P', true, 6, 'Top 20 %Gainers')");
	 * 
	 * 
	 * Log.debug(this, "Starting populateData()");
	 * 
	 * //for (int i = 1; i < 7; i++) { // wait for it to load
	 * 
	 * //webDriverWait.until(staticViewCondition);
	 * 
	 * Log.debug( this, "Starting findElements - " + (System.currentTimeMillis()
	 * - timestamp) + "ms"); timestamp = System.currentTimeMillis();
	 * 
	 * //List<WebElement> table = driver.findElements(By //
	 * .xpath("//table[@class='sgxTableGrid']//tr//td"));
	 * 
	 * // Retrieve the entire SGX table of stocks into a WebElement object
	 * //WebElement tableElement =
	 * driver.findElement(By.className("sgxTableGrid"));
	 * 
	 * Log.debug(this,"Starting populatingHashtable - " +
	 * (System.currentTimeMillis() - timestamp) + "ms"); timestamp =
	 * System.currentTimeMillis();
	 * 
	 * int noOfColumns = 19;
	 * 
	 * StockData stock;
	 * 
	 * List<WebElement> table = tableElement.findElements(By.tagName("td"));
	 * 
	 * // Parse the HTML and create StockData object with stock details
	 * Log.debug(this,"Parsing webdata... table size: "+table.size()); for (int
	 * i = 0; i < (table.size() / noOfColumns); i++) { String stockCode =
	 * table.get((i * noOfColumns) + 4).getText();
	 * 
	 * // if stock already exist, update prices only if
	 * (marketData.containsKey(stockCode)) { Log.debug(this, stockCode
	 * +" already exist. Retrieving record.."); stock =
	 * marketData.get(stockCode); stock.clearFieldChangedList(); // we will be
	 * re-populating updated field list } else { // create new stock object and
	 * populate all fields Log.debug(this, "Creating new object: "+stockCode);
	 * stock = new StockData(); stock.setStockCode(stockCode);
	 * marketData.put(stockCode, stock); }
	 * 
	 * WebElement element = table.get((i * noOfColumns) + 2); String value =
	 * element.getText(); if (!value.equals("-") &&
	 * !value.equals(stock.getStockName())) { stock.setStockName(value); }
	 * 
	 * element = table.get((i * noOfColumns) + 5); value = element.getText(); if
	 * (!value.equals("-") && !value.equals(stock.getRemarks())) {
	 * stock.setRemarks(value); }
	 * 
	 * element = table.get((i * noOfColumns) + 6); value =
	 * element.getText().replaceAll(",",""); if (!value.equals("-") &&
	 * stock.getLastPrice() != Float.parseFloat(value)) {
	 * stock.setLastPrice(Float.parseFloat(value)); }
	 * 
	 * element = table.get((i * noOfColumns) + 7); value =
	 * element.getText().replaceAll(",",""); if (!value.equals("-") &&
	 * stock.getValueChange() != Float.parseFloat(value)) {
	 * stock.setValueChange(Float.parseFloat(value)); }
	 * 
	 * element = table.get((i * noOfColumns) + 8); value =
	 * element.getText().replaceAll(",",""); if (!value.equals("-") &&
	 * stock.getPercentChange() != Float.parseFloat(value)) {
	 * stock.setPercentChange(Float.parseFloat(value)); }
	 * 
	 * element = table.get((i * noOfColumns) + 9); value =
	 * element.getText().replaceAll(",",""); if (!value.equals("-") &&
	 * stock.getVolume() != Float.parseFloat(value)) {
	 * stock.setVolume(Float.parseFloat(value)); }
	 * 
	 * element = table.get((i * noOfColumns) + 10); value =
	 * element.getText().replaceAll(",",""); if (!value.equals("-") &&
	 * stock.getBuyVolume() != Float.parseFloat(value)) {
	 * stock.setBuyVolume(Float.parseFloat(value)); }
	 * 
	 * element = table.get((i * noOfColumns) + 11); value =
	 * element.getText().replaceAll(",",""); if (!value.equals("-") &&
	 * stock.getBuyPrice() != Float.parseFloat(value)) {
	 * stock.setBuyPrice(Float.parseFloat(value)); }
	 * 
	 * element = table.get((i * noOfColumns) + 12); value =
	 * element.getText().replaceAll(",",""); if (!value.equals("-") &&
	 * stock.getSellPrice() != Float.parseFloat(value)) {
	 * stock.setSellPrice(Float.parseFloat(value)); }
	 * 
	 * element = table.get((i * noOfColumns) + 13); value =
	 * element.getText().replaceAll(",",""); if (!value.equals("-") &&
	 * stock.getSellVolume() != Float.parseFloat(value)) {
	 * stock.setSellVolume(Float.parseFloat(value)); }
	 * 
	 * element = table.get((i * noOfColumns) + 14); value =
	 * element.getText().replaceAll(",",""); if (!value.equals("-") &&
	 * stock.getOpenPrice() != Float.parseFloat(value)) {
	 * stock.setOpenPrice(Float.parseFloat(value)); }
	 * 
	 * element = table.get((i * noOfColumns) + 15); value =
	 * element.getText().replaceAll(",",""); if (!value.equals("-") &&
	 * stock.getHighPrice() != Float.parseFloat(value)) {
	 * stock.setHighPrice(Float.parseFloat(value)); }
	 * 
	 * element = table.get((i * noOfColumns) + 16); value =
	 * element.getText().replaceAll(",",""); if (!value.equals("-") &&
	 * stock.getLowPrice() != Float.parseFloat(value)) {
	 * stock.setLowPrice(Float.parseFloat(value)); }
	 * 
	 * element = table.get((i * noOfColumns) + 17); value =
	 * element.getText().replaceAll(",",""); if (!value.equals("-") &&
	 * stock.getValue() != Float.parseFloat(value)) {
	 * stock.setValue(Float.parseFloat(value)); }
	 * 
	 * element = table.get((i * noOfColumns) + 18); value =
	 * element.getText().replaceAll(",",""); if (!value.equals("-") &&
	 * !value.equals(stock.getSector())) { stock.setSector(value); }
	 * 
	 * if (stock.wasUpdated()) { // inform subscribers that stock has updated
	 * fields Log.debug(this, "Stock updated! Notifying subscribers.");
	 * stockChange(stock); }
	 * 
	 * Log.debug(this, stock.toString()); }
	 * 
	 * /*PopulateFieldThread populateFieldThread = new PopulateFieldThread(
	 * tableElement, "All Stocks - Page: 1"); populateFieldThread.start();
	 * 
	 * populateFieldThreadList.add(populateFieldThread);
	 */

	// Load next page
	// ((JavascriptExecutor)
	// driver).executeScript("gotoPage({on_success:\""+(i+1)+"\",render_top:\"paging\",render_bottom:\"null\",goto_page:\"gotoPage\",record_count:\"1024\"})");
	// }

	// Wait for all threads to complete.
	/*
	 * for (PopulateFieldThread thread : populateFieldThreadList) { try {
	 * thread.join(); } catch (InterruptedException e) { e.printStackTrace(); }
	 * }
	 * 
	 * populateFieldThreadList.clear();
	 * 
	 * Log.debug(this, "Done parsing. - " + (System.currentTimeMillis() -
	 * timestamp) + "ms"); timestamp = System.currentTimeMillis(); } catch
	 * (TimeoutException e) { Log.info(this,"Timeout. Repopulating data..");
	 * populateData(); }
	 * 
	 * }
	 */

	@Override
	public void refresh() {
		// ((JavascriptExecutor) driver).executeScript("doRefresh()");
		// ((JavascriptExecutor)
		// driver).executeScript("doTops('P', true, 6, 'Top 20 %Gainers')");
		populateData();
	}
}
