package stockmaster.marketdata;

import com.mongodb.*;
import stockmaster.db.MongoManager;
import stockmaster.unit.MarketDataInfo;
import stockmaster.unit.StockData;
import stockmaster.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: shaun
 * Date: 8/27/12
 * Time: 6:16 PM
 * Replays market data stored in MongoDB.
 */
public class ReplayMongoMarketDataImpl extends MarketData {

    private MongoManager mongoManager = MongoManager.getInstance();

    private String collectionName;
    private Date startDate;
    private Date endDate;


    /**
     * Sets name of datastore to use by concatenating market + date values
     *
     * @param market    String representation of market
     * @param startDate Start Date
     * @param endDate   End Date
     */
    public ReplayMongoMarketDataImpl(String market, Date startDate, Date endDate, MarketDataInfo marketDataInfo) {
        this.collectionName = market;
        this.startDate = startDate;
        this.endDate = endDate;
        this.setMarketDataInfo(marketDataInfo);
    }
    
    public ReplayMongoMarketDataImpl(){
    	super();
    }

    @Override
    public void populateData() {
        //Specifying that records returned should be greater than startDate and less than endDate
        BasicDBObject query = new BasicDBObject();
        query.put("lastUpdate", new BasicDBObject("$gte", startDate).append("$lte", endDate));
        Log.info(this, "Start date: " + startDate + " End Date: " + endDate);
        Log.info(this, "MongoDB Query String: " + query.toString());

        DBCursor cursor;
        cursor = mongoManager.getDatabase().getCollection(collectionName).find(query);

        try {
            while (cursor.hasNext()) {
                //retrieve current record
                DBObject object = cursor.next();

                StockData stockData;
                String stockCode = (String) object.get("stockCode");

                //if stock does not already exist in marketData's hashtable, put it there.
                if (!getMarketDataInfo().getMarketData().containsKey(stockCode)) {
                    stockData = new StockData();
                    stockData.setStockCode(stockCode);
                    stockData.setStockName((String) object.get("stockName"));

                    getMarketDataInfo().getMarketData().put(stockData.getStockCode(), stockData);
                }

                //if stock exists, retrieve from hashtable
                stockData = getMarketDataInfo().getMarketData().get(stockCode);
                stockData.clearFieldChangedList();

                stockData.setValueChange((Double) object.get("valueChange"));
                stockData.setPercentChange((Double) object.get("percentChange"));
                stockData.setBuyPrice((Double) object.get("buyPrice"));
                stockData.setSellPrice((Double) object.get("sellPrice"));
                stockData.setLastPrice((Double) object.get("lastPrice"));
                stockData.setBuyVolume((Double) object.get("buyVolume"));
                stockData.setSellVolume((Double) object.get("sellVolume"));
                stockData.setVolume((Double) object.get("volume"));
                stockData.setOpenPrice((Double) object.get("openPrice"));
                stockData.setLowPrice((Double) object.get("lowPrice"));
                stockData.setHighPrice((Double) object.get("highPrice"));
                stockData.setValue((Double) object.get("value"));
                stockData.setSector((String) object.get("sector"));
                stockData.setSentiment((Double) object.get("sentiment"));
                stockData.setSentimentWeight((Double) object.get("sentimentWeight"));
                stockData.setLastUpdate((Date) object.get("lastUpdate"));

                Log.debug(this, "Stock Code: " + stockData.getStockCode() + " Last Update: " + String.valueOf(stockData.getLastUpdate()));

                if(stockData.wasUpdated()) { // inform subscribers that stock
                    // has updated fields
                    Log.debug(this, "Stock updated " + stockData.getStockName() + " (" + stockData.getStockCode() + ")! Notifying subscribers.");
                    stockChange(stockData);
                }


            }
        } finally {
            Log.debug(this, "Total records retrieved by query: " + String.valueOf(cursor.count()));
            cursor.close();
        }
    }

    @Override
    public void refresh() {
        populateData();
    }

    @Override
    public void init() {
        Log.info(this, "init() called.");
    }

    /**
     * Similar to ReplayCSVMarketDataImpl, overwrite this method so worker thread doesn't get started
     */
    @Override
    public void start() {
        Log.debug(this, "Starting market data..");
        init();
        populateData();
    }
}
