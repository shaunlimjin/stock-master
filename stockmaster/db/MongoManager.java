package stockmaster.db;

import com.mongodb.*;
import stockmaster.unit.StockData;
import stockmaster.util.Log;

import java.net.UnknownHostException;

/**
 * Created with IntelliJ IDEA.
 * User: shaun
 * Date: 8/27/12
 * Time: 6:37 PM
 * Singleton class that manages communication with MongoDB
 * Dependencies: - MongoDB Java driver - mongo-2.8.0.jar
 */
public class MongoManager {

    //For Singleton
    private static MongoManager instance = null;


    //DB hostname here. For now, assuming it's a mongodb instance running on your local machine
    private final String hostname = "localhost";
    //DB name
    private final String databaseName = "stockmaster";

    //This Mongo object will represent a connection pool. Only one instance is necessary.
    private Mongo mongo;
    private DB database;
    private DBCollection collection;

    /**
     * Establishes connection to MongoDB and creates instance of Morphia.
     */
    private MongoManager() {
        try {
            //Connect to Mongo
            this.mongo = new Mongo(hostname);
            //Exceptions are raised for network issues, and server errors; waits on a server for the write operation
            mongo.setWriteConcern(WriteConcern.SAFE);

            //Initialize database
            database = mongo.getDB(databaseName);

        } catch (UnknownHostException e) {
            Log.error(this, "Unable to connect to db. Check hostname.");
            Log.error(this, e.toString());
        }
    }

    /**
     * Used to get instance of MongoManger.
     * @return
     */
    public static MongoManager getInstance() {
        if(instance == null){
            instance = new MongoManager();
        }

        return instance;
    }

    /**
     * Persist StockData object to DB.
     * @param stockData StockData Object to persist
     */
    public void saveStockData(StockData stockData){

            BasicDBObject doc = new BasicDBObject();

            doc.put("valueChange", stockData.getValueChange());
            doc.put("percentChange", stockData.getPercentChange());
            doc.put("buyPrice", stockData.getBuyPrice());
            doc.put("sellPrice", stockData.getSellPrice());
            doc.put("lastPrice", stockData.getLastPrice());
            doc.put("buyVolume", stockData.getBuyVolume());
            doc.put("sellVolume", stockData.getSellVolume());
            doc.put("volume", stockData.getVolume());
            doc.put("openPrice", stockData.getOpenPrice());
            doc.put("lowPrice", stockData.getLowPrice());
            doc.put("highPrice", stockData.getHighPrice());
            doc.put("value", stockData.getValue());
            doc.put("sector", stockData.getSector());
            doc.put("stockName", stockData.getStockName());
            doc.put("stockCode", stockData.getStockCode());
            doc.put("sentiment", stockData.getSentiment());
            doc.put("sentimentWeight", stockData.getSentimentWeight());
            doc.put("lastUpdate", stockData.getLastUpdate());

            if(collection == null) {
                Log.error(this, "Collection object has not been intialized.");
            }
            else {
                collection.insert(doc);
            }
    }

    public DBCollection getCollection() {
        return collection;
    }

    /**
     * Sets which collection to use.
     * @param collectionName collection to use
     */
    public void setCollection(String collectionName) {

        if(database == null)
            Log.error(this,"Database object has not been initialized.");
        else {
            this.collection = database.getCollection(collectionName);
        }
    }

    public DB getDatabase() {
        return database;
    }

    public void setDatabase(DB database) {
        this.database = database;
    }
}
