package CLI.exceptions;

public class OverdraftException extends Exception {

	public OverdraftException() {
		super();
	}

	public OverdraftException(String message) {
		super(message);
	}
}
