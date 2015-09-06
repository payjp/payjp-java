package jp.pay.model;

import java.util.Map;

public class EventData extends PayjpObject {
	Map<String, Object> previousAttributes;
	PayjpObject object;
	
	public Map<String, Object> getPreviousAttributes() {
		return previousAttributes;
	}
	
	public void setPreviousAttributes(Map<String, Object> previousAttributes) {
		this.previousAttributes = previousAttributes;
	}

	public PayjpObject getObject() {
		return object;
	}

	public void setObject(PayjpObject object) {
		this.object = object;
	}
}
