package stockmaster.exception;

/**
 * Created with IntelliJ IDEA.
 * User: shaun
 * Date: 9/11/12
 * Time: 12:29 AM
 * Thrown to indicate that an exception related to our communication with MongoDB occurred.
 * Not to be confused with com.mongodb.MongoException which is used by the MongoDB driver.
 */
public class MongoDBException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private Error error = Error.UNEXPECTED_ERROR;

    /**
     * Constructs a MongoDBException with an Error Enum
     * @param error
     */
    public MongoDBException(Error error) {
        super(String.format("%s, [%s}", error.name(),error.toString()));
        this.error = error;
    }

    /**
     * Used to return the Error Enum
     * @return Error Enum
     */
    public Error getError() {
        return error;
    }


}
