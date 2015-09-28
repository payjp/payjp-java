package jp.pay.exception;

public class APIException extends PayjpException {

	private static final long serialVersionUID = 1L;

	public APIException(String message, Throwable e) {
		super(message, e);
	}
}
