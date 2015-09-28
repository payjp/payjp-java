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
package jp.pay.net;

import java.util.List;
import java.util.Map;

public class PayjpResponse {
	
	int responseCode;
	String responseBody;
        Map<String, List<String>> responseHeaders;
	
	public PayjpResponse(int responseCode, String responseBody) {
		this.responseCode = responseCode;
		this.responseBody = responseBody;
                this.responseHeaders = null;
	}
        public PayjpResponse(int responseCode, String responseBody, Map<String, List<String>> responseHeaders) {
		this.responseCode = responseCode;
		this.responseBody = responseBody;
                this.responseHeaders = responseHeaders;
	}

	public int getResponseCode() {
		return responseCode;
	}
	
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	
	public String getResponseBody() {
		return responseBody;
	}
	
	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}
        public Map<String, List<String>> getResponseHeaders() {
		return responseHeaders;
	}
}
