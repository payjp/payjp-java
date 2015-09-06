package jp.pay.exception;

public abstract class PayjpException extends Exception {

	public PayjpException(String message) {
		super(message, null);
	}

	public PayjpException(String message, Throwable e) {
		super(message, e);
	}

	private static final long serialVersionUID = 1L;

}
