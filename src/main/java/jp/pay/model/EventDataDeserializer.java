/*
 * Copyright (c) 2010-2011 Stripe (http://stripe.com)
 * Copyright (c) 2015 Base, Inc. (http://binc.jp/)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
package jp.pay.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jp.pay.net.APIResource;

public class EventDataDeserializer implements JsonDeserializer<PayjpObject> {

	@SuppressWarnings("rawtypes")
	static final Map<String, Class> objectMap = new HashMap<String, Class>();
    static {
        objectMap.put("account", Account.class);
        objectMap.put("charge", Charge.class);
        objectMap.put("customer", Customer.class);
        objectMap.put("plan", Plan.class);
        objectMap.put("subscription", Subscription.class);
        objectMap.put("token", Token.class);
        objectMap.put("transfer", Transfer.class);
        objectMap.put("summary", Summary.class);
        objectMap.put("card", Card.class);
    }

    private Object deserializeJsonPrimitive(JsonPrimitive element) {
    	if (element.isBoolean()) {
    		return element.getAsBoolean();
    	} else if (element.isNumber()) {
    		return element.getAsNumber();
    	} else {
    		return element.getAsString();
    	}
    }

    private Object[] deserializeJsonArray(JsonArray arr) {
    	Object[] elems = new Object[arr.size()];
    	Iterator<JsonElement> elemIter = arr.iterator();
    	int i = 0;
    	while (elemIter.hasNext()) {
    		JsonElement elem = elemIter.next();
    		elems[i++] = deserializeJsonElement(elem);
    	}
    	return elems;
    }

    private Object deserializeJsonElement(JsonElement element) {
    	if (element.isJsonNull()) {
    		return null;
    	} else if (element.isJsonObject()) {
			Map<String, Object> valueMap = new HashMap<String, Object>();
			populateMapFromJSONObject(valueMap, element.getAsJsonObject());
			return valueMap;
		} else if (element.isJsonPrimitive()) {
			return deserializeJsonPrimitive(element.getAsJsonPrimitive());
		} else if (element.isJsonArray()) {
			return deserializeJsonArray(element.getAsJsonArray());
		} else {
			System.err.println("Unknown JSON element type for element " + element + ". " +
					"If you're seeing this messaage, it's probably a bug in the Payjp Java " +
					"library. Please contact us by email at support@pay.jp.");
			return null;
		}
	}

    private void populateMapFromJSONObject(Map<String, Object> objMap, JsonObject jsonObject) {
		for(Map.Entry<String, JsonElement> entry: jsonObject.entrySet()) {
			String key = entry.getKey();
			JsonElement element = entry.getValue();
			objMap.put(key, deserializeJsonElement(element));
		}
    }

	@SuppressWarnings("unchecked")
	public PayjpObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		PayjpObject object = null;
		JsonObject jsonObject = json.getAsJsonObject();
		
		for(Map.Entry<String, JsonElement> entry: jsonObject.entrySet()) {
			String key = entry.getKey();
			JsonElement element = entry.getValue();
			System.out.println(key);
			
			if ("object".equals(key)) {
				String type = element.getAsString();
				Class<PayjpObject> cl = objectMap.get(type);

				object = APIResource.GSON.fromJson(json, cl != null ? cl : PayjpRawJsonObject.class);
			}
		}
		return object;
	}
}
