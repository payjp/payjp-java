package jp.pay.exception;

public class CardException extends PayjpException {
	private static final long serialVersionUID = 1L;

	private String code;
	private String param;

	public CardException(String message, String param, String code, Throwable e) {
		super(message, e);
		this.param = param;
		this.code = code;
	}

	public CardException(String message, String param, String code) {
		this(message, param, code, null);
	}

	public String getCode() {
		return this.code;
	}

	public String getParam() {
		return this.param;
	}
}
