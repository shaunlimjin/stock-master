package stockmaster.recorder;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import stockmaster.unit.StockData;
import stockmaster.util.Log;

import java.net.UnknownHostException;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: shaun
 * Date: 8/26/12
 * Time: 11:57 PM
 * Recorder implementation using MongoDB as data store.
 * Dependencies: - MongoDB Java driver - mongo-2.8.0.jar
 *               - ORM wrapper for MongoDB - morphia-0.99.jar
 */
public class MongoRecorderImpl extends DataRecorder {

    //DB hostname here. For now, assuming it's a mongodb instance running on your local machine
    private final String hostname = "localhost";

    //This Mongo object will represent a connection pool. Only one instance is necessary.
    private Mongo mongo;

    //Morphia wrapper around the Mongo java driver
    private Datastore datastore;

    public MongoRecorderImpl(String market) {
        try {
            //Connect to Mongo
            mongo = new Mongo(hostname);

            //Create a new database for the market if one does not exist. Otherwise, use existing db.
            datastore = new Morphia().createDatastore(mongo, market);
        } catch (UnknownHostException e) {
            Log.error(this, "Unable to connect to db. Check hostname.");
            e.printStackTrace();
        }
    }
    @Override
    protected void record(StockData data) {
        Log.info(this, "Writing to Mongo stock " + data.getStockName() + "last price: " + data.getLastPrice());
        datastore.save(data);
    }
}
