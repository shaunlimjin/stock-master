package stockmaster.recorder;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import stockmaster.db.MongoManager;
import stockmaster.unit.StockData;
import stockmaster.util.Log;

import java.net.UnknownHostException;
import java.util.Date;
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

    /**
     * Constructor.
     * @param market Market to record. Will be used as collection name.
     * @param startDate Recording start
     * @param endDate Recording end
     */
    public MongoRecorderImpl(String market, Date startDate, Date endDate) {
    	super(startDate, endDate);
        mongoManager.setCollection(market);
    }

    @Override
    protected void record(StockData data) {
        Log.info(this, "Writing to Mongo stock " + data.getStockName() + " | last price: " + data.getLastPrice());
        mongoManager.saveStockData(data);
    }
}
