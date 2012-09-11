package stockmaster.exception;

/**
 * Created with IntelliJ IDEA.
 * User: shaun
 * Date: 9/11/12
 * Time: 12:23 AM
 * Enum construct containing error codes used throughout the system.
 * Each error should have an error code and description.
 */
public enum Error {
    MONGODB_UNABLE_TO_CONNECT(0, "Unable to connect to MongoDB"),
    MONGODB_INVALID_DATABASE(1, "Unable to open database"),
    MONGODB_INVALID_COLLECTION(2, "Unable to open collection"),
    UNEXPECTED_ERROR(999, "Unexpected error.");

    private final int code;
    private final String description;

    private Error(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return code + ": " + description;
    }
}
