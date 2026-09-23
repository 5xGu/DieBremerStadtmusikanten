package bauernhof.player;

/**
 * Custom exception for when an illegal move was made.
 * @author Tobias Kai Lorenz Plattner
 */
public class IllegalMoveException extends Exception {
	/**
	 * Constructs an IllegalMoveException with no detail message. Calls {@link java.lang.Exception Exception() }
	 */
    public IllegalMoveException() {
		super();
	}
	/**
	 * Constructs a new exception with the specified detail message. Calls {@link java.lang.Exception Exception(String s)}
	 */
    public IllegalMoveException(String message) {
        super(message);
    }
	/**
	 * Constructs a new exception with the specified detail message and cause. Calls {@link java.lang.Exception Exception(String message, Throwable cause)}
	 */
	public IllegalMoveException(String message, Throwable cause) {
		super(message, cause);
	}
	/**
	 * Constructs a new exception with the specified cause and a detail message of (cause==null ? null : cause.toString()). Calls {@link java.lang.Exception Exception(Throwable cause)}
	 */
	public IllegalMoveException(Throwable cause) {
		super(cause);
	}
}
