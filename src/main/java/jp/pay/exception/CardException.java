package jp.pay.exception;

public class CardException extends PayjpException {
	private static final long serialVersionUID = 1L;

	private String code;
	private String param;

	public CardException(String message, String code, String param, Throwable e) {
		super(message, e);
		this.code = code;
		this.param = param;
	}

	public CardException(String message, String param, String code) {
        this(message, code, param, null);
	}

	public String getCode() {
		return this.code;
	}

	public String getParam() {
		return this.param;
	}
}
