package jp.pay.exception;

public class InvalidRequestException extends PayjpException {

	private static final long serialVersionUID = 1L;

	private String param;
	private String type;
	private String code;

	public InvalidRequestException(String message, String param, Throwable e) {
		super(message, e);
		this.param = param;
	}
	
	public InvalidRequestException(String message, String param, String type, String code, Throwable e) {
		super(message, e);
		this.param = param;
		this.type = type;
		this.code = code;
		
	}

	public String getParam() {
		return param;
	}

	public String getType() {
		return type;
	}

	public String getCode() {
		return code;
	}
}
