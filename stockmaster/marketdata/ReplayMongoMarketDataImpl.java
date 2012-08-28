package stockmaster.marketdata;

import com.mongodb.Mongo;
import stockmaster.db.MongoManager;
import stockmaster.unit.StockData;
import stockmaster.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: shaun
 * Date: 8/27/12
 * Time: 6:16 PM
 * Replays market data stored in MongoDB.
 */
public class ReplayMongoMarketDataImpl extends MarketData{

    private MongoManager mongoManager = MongoManager.getInstance();

    //Name of datastore to use, e.g. sgx_20120827
    private String datastoreName;


    /**
     * Sets name of datastore to use by concatenating market + date values
     * @param market String representation of market
     * @param date   String representation of date, in predetermined date format yyyymmdd
     */
    public ReplayMongoMarketDataImpl(String market, String date){
        this.datastoreName = market + "_" + date;
    }

    @Override
    public void populateData() {
        mongoManager.setDatastore(datastoreName);
        for (StockData stockData : mongoManager.getDatastore().createQuery(StockData.class)) {
            Log.info(this,"Retrieved " + stockData.getStockName());
            stockChange(stockData);
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
    public void start(){
        Log.debug(this, "Starting market data..");
        init();
        populateData();
    }
}
