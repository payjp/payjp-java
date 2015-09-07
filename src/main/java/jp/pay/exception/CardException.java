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

	public CardException(String message, String code, String param) {
        this(message, code, param, null);
	}

	public String getCode() {
		return code;
	}
	public String getParam() {
		return param;
	}
}
