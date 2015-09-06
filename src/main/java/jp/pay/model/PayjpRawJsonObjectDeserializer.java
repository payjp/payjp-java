package jp.pay.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class PayjpRawJsonObjectDeserializer implements JsonDeserializer<PayjpRawJsonObject> {
	public PayjpRawJsonObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		PayjpRawJsonObject object = new PayjpRawJsonObject();
		object.json = json.getAsJsonObject();
		return object;
	}

}
