package xyz.roosterseatyou.elections.database;

public class ElectionNotFoundError extends Exception {
    public ElectionNotFoundError(String message) {
        super(message);
    }

    public ElectionNotFoundError(String message, Throwable cause) {
        super(message, cause);
    }
}
