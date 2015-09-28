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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;

import com.google.gson.reflect.TypeToken;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ExternalAccountTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!ExternalAccount.class.isAssignableFrom(type.getRawType())) {
            return null; // this class only serializes 'ExternalAccount' and its subtypes
        }

        final String SOURCE_OBJECT_PROP = "object";

        final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
        final TypeAdapter<ExternalAccount> externalAccountAdapter = gson.getDelegateAdapter(this, TypeToken.get(ExternalAccount.class));

        final TypeAdapter<Card> cardAdapter = gson.getDelegateAdapter(this, TypeToken.get(Card.class));

        TypeAdapter<ExternalAccount> result = new TypeAdapter<ExternalAccount>() {
            public void write(JsonWriter out, ExternalAccount value) throws IOException {
                // TODO: check instance of for correct writer
                externalAccountAdapter.write(out, value);
            }

            public ExternalAccount read(JsonReader in) throws IOException {
                JsonObject object = elementAdapter.read(in).getAsJsonObject();
                String sourceObject = object.getAsJsonPrimitive(SOURCE_OBJECT_PROP).getAsString();

                if (sourceObject.equals("card")) {
                    return cardAdapter.fromJsonTree(object);
                } else {
                    return externalAccountAdapter.fromJsonTree(object);
                }
            }
        }.nullSafe();

        return (TypeAdapter<T>) result;
    }
}
