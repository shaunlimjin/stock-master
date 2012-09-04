package stockmaster.marketdata;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import stockmaster.db.MongoManager;
import stockmaster.unit.StockData;
import stockmaster.util.Log;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: shaun
 * Date: 8/27/12
 * Time: 6:16 PM
 * Replays market data stored in MongoDB.
 */
public class ReplayMongoMarketDataImpl extends MarketData{

    private MongoManager mongoManager = MongoManager.getInstance();

    private String collectionName;
    private Date startDate;
    private Date endDate;


    /**
     * Sets name of datastore to use by concatenating market + date values
     * @param market String representation of market
     * @param startDate Start Date
     * @param endDate End Date
     */
    public ReplayMongoMarketDataImpl(String market, Date startDate, Date endDate){
        this.collectionName = market;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public void populateData() {
        mongoManager.setCollection(collectionName);

        BasicDBObject query = new BasicDBObject();
        query.put("lastUpdate", new BasicDBObject("$lt", endDate));

        DBCursor cursor;
        cursor = mongoManager.getCollection().find(query);

        try {
            while(cursor.hasNext()) {
                System.out.println(cursor.next());
            }
        } finally {
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
    public void start(){
        Log.debug(this, "Starting market data..");
        init();
        populateData();
    }
}
