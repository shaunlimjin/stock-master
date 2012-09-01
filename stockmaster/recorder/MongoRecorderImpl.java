package stockmaster.recorder;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import stockmaster.db.MongoManager;
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
 */
public class MongoRecorderImpl extends DataRecorder {

    private MongoManager mongoManager = MongoManager.getInstance();

    public MongoRecorderImpl(String market) {
        mongoManager.setDatastore(market + "_" + Log.getCurrentDate());
    }

    @Override
    protected void record(StockData data) {
        Log.info(this, "Writing to Mongo stock " + data.getStockName() + " | last price: " + data.getLastPrice());
        mongoManager.save(data);
    }
}
