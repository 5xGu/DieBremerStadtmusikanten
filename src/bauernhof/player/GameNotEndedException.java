package bauernhof.player;

/**
 * Custom exception thrown when the game is not over but should be.
 * @author Tobias Kai Lorenz Plattner
 */
public class GameNotEndedException extends IllegalStateException {
	/**
	 * Constructs an GameNotEndedException with no detail message. Calls {@link java.lang.IllegalStateException IllegalStateException() }
	 */
    public GameNotEndedException() {
		super();
	}
	/**
	 * Constructs a new exception with the specified detail message. Calls {@link java.lang.IllegalStateException IllegalStateException(String s)}
	 */
    public GameNotEndedException(String message) {
        super(message);
    }
	/**
	 * Constructs a new exception with the specified detail message and cause. Calls {@link java.lang.IllegalStateException IllegalStateException(String message, Throwable cause)}
	 */
	public GameNotEndedException(String message, Throwable cause) {
		super(message, cause);
	}
	/**
	 * Constructs a new exception with the specified cause and a detail message of (cause==null ? null : cause.toString()). Calls {@link java.lang.IllegalStateException IllegalStateException(Throwable cause)}
	 */
	public GameNotEndedException(Throwable cause) {
		super(cause);
	}
}
