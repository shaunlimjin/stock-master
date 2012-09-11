package stockmaster.db;

import com.mongodb.*;
import stockmaster.exception.MongoDBException;
import stockmaster.unit.PortfolioStock;
import stockmaster.unit.StockData;
import stockmaster.util.Log;

import stockmaster.exception.Error;

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

    /**
     * Establishes connection to MongoDB.
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
            Log.error(this, e.toString());
            throw new MongoDBException(Error.MONGODB_UNABLE_TO_CONNECT);
        }
    }

    /**
     * Used to get instance of MongoManger.
     *
     * @return
     */
    public static MongoManager getInstance() {
        if (instance == null) {
            instance = new MongoManager();
        }

        return instance;
    }

    public DB getDatabase() {
        return database;
    }

    public void setDatabase(DB database) {
        this.database = database;
    }

    /**
     * Generic method to persist an object to the database
     * @param doc populated object containing the data to persist
     * @param collectionName name of the collection to persist object to
     */
    private void persistObject(BasicDBObject doc, String collectionName) {

        DBCollection collection = null;

        if (database == null) {
            Log.error(this, "Database object has not been initialized.");
            throw new MongoDBException(Error.MONGODB_INVALID_DATABASE);
        } else {
            //collections are cached by the mongodb driver, so no performance hit here.
            collection = database.getCollection(collectionName);
        }

        if (collection == null) {
            Log.error(this, "Collection object has not been intialized.");
            throw new MongoDBException(Error.MONGODB_INVALID_COLLECTION);
        } else {
            //If connection to mongodb fails, MongoException.Network is thrown,
            //but is descriptive enough so don't need to catch?
            collection.insert(doc);
        }
    }

    /**
     * Persist StockData object to DB.
     *
     * @param stockData StockData Object to persist
     */
    public void saveStockData(StockData stockData, String market) {

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

        this.persistObject(doc, market);
    }

    /**
     * Persist stock to Portfolio
     * @param portfolioStock stock to add to portfolio
     * @param portfolioName name of portfolio to add stock to
     */
    public void saveStockToPortfolio(PortfolioStock portfolioStock, String portfolioName) {

        BasicDBObject doc = new BasicDBObject();

        doc.put("stockCode", portfolioStock.getStockCode());
        doc.put("stockName", portfolioStock.getStockName());
        doc.put("purchasedPrice", portfolioStock.getPurchasedPrice());
        doc.put("quantity", portfolioStock.getQuantity());
        doc.put("acquiredDate", portfolioStock.getAcquiredDate());
        doc.put("tradingAlorithm", portfolioStock.getTradingAlgorithm().getClass().toString());

        this.persistObject(doc, portfolioName);
    }

}
