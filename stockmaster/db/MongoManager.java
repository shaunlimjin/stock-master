package stockmaster.db;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import stockmaster.util.Log;

import java.net.UnknownHostException;

/**
 * Created with IntelliJ IDEA.
 * User: shaun
 * Date: 8/27/12
 * Time: 6:37 PM
 * Singleton class that manages communication with MongoDB
 * Dependencies: - MongoDB Java driver - mongo-2.8.0.jar
 *               - ORM wrapper for MongoDB - morphia-0.99.jar
 */
public class MongoManager {

    //For Singleton
    private static MongoManager instance = null;

    //DB hostname here. For now, assuming it's a mongodb instance running on your local machine
    private final String hostname = "localhost";

    //This Mongo object will represent a connection pool. Only one instance is necessary.
    private Mongo mongo;

    //Morphia instance. Will be reused
    private Morphia morphia;

    //Morphia wrapper around the Mongo java driver
    private Datastore datastore;

    /**
     * Establishes connection to MongoDB and creates instance of Morphia.
     */
    private MongoManager() {
        try {
            //Connect to Mongo
            this.mongo = new Mongo(hostname);

            //Initialize Morphia instance
            this.morphia = new Morphia();

        } catch (UnknownHostException e) {
            Log.error(this, "Unable to connect to db. Check hostname.");
            e.printStackTrace();
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
     * Persist object to DB.
     * @param data Object to persist
     */
    public void save(Object data){
        if(datastore == null)
            Log.error(this,"datastore object has not been initialized.");
        else
            datastore.save(data);
    }


    /**
     * Returns previously initialized datastore object
     * @return datastore
     */
    public Datastore getDatastore() {
        if(datastore == null)
            Log.error(this,"datastore object has not been initialized.");
        return datastore;
    }

    /**
     * Creates or retrieves datastore if one with the same name already exists
     * @param name datastore's name
     */
    public void setDatastore(String name) {
        if(name == null){
            Log.error(this, "Unable to set Datastore: name is null.");
        } else {
            Log.info(this, "Created Datastore object with name: " + name);
            this.datastore = morphia.createDatastore(mongo, name);
        }
    }
}
