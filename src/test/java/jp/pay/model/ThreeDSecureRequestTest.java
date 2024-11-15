/*
 * Copyright (c) 2010-2011 Stripe (http://stripe.com)
 * Copyright (c) 2024 PAY, Inc. (http://pay.co.jp/)
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


import jp.pay.net.APIResource;

import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.pay.exception.PayjpException;
import jp.pay.BasePayjpTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ThreeDSecureRequestTest extends BasePayjpTest {
	@Test
	public void testDeserialize() throws PayjpException, IOException {
		String json = resource("three_d_secure_request.json");
		ThreeDSecureRequest threeDSecureRequest = APIResource.GSON.fromJson(json, ThreeDSecureRequest.class);
		assertEquals(threeDSecureRequest.getId(), "tdsr_125192559c91c4011c1ff56f50a");
		assertEquals(threeDSecureRequest.getResourceId(), "car_4ec110e0700daf893160424fe03c");
		assertEquals(threeDSecureRequest.getLivemode(), true);
		assertEquals(threeDSecureRequest.getCreated().longValue(), 1730084767);
		assertEquals(threeDSecureRequest.getState(), "created");
		assertEquals(threeDSecureRequest.getThreeDSecureStatus(), "unverified");
		assertEquals(threeDSecureRequest.getStartedAt(), null);
		assertEquals(threeDSecureRequest.getResultReceivedAt(), null);
		assertEquals(threeDSecureRequest.getFinishedAt(), null);
		assertEquals(threeDSecureRequest.getExpiredAt(), null);
		assertEquals(threeDSecureRequest.getTenantId(), null);
	}

	@Test
	public void testRetrieve() throws PayjpException {
		stubNetwork(ThreeDSecureRequest.class, "{\"id\":\"tdsr_xxxxx\"}");
		ThreeDSecureRequest threeDSecureRequest = ThreeDSecureRequest.retrieve("tdsr_xxxxx");
		verifyGet(ThreeDSecureRequest.class, "https://api.pay.jp/v1/three_d_secure_requests/tdsr_xxxxx");
		assertEquals(threeDSecureRequest.getId(), "tdsr_xxxxx");
	}

	@Test
	public void testList() throws PayjpException {
		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("limit", 2);
		stubNetwork(ThreeDSecureRequestCollection.class,
				"{\"count\":2,\"data\":[{\"id\":\"tdsr_xxxxx\"},{\"id\":\"tdsr_yyyyy\"}]}");
		List<ThreeDSecureRequest> threeDSecureRequests = ThreeDSecureRequest.all(listParams).getData();
		verifyGet(ThreeDSecureRequestCollection.class, "https://api.pay.jp/v1/three_d_secure_requests", listParams);
		assertEquals(threeDSecureRequests.size(), 2);
		assertEquals(threeDSecureRequests.get(0).getId(), "tdsr_xxxxx");
		assertEquals(threeDSecureRequests.get(1).getId(), "tdsr_yyyyy");
	}

	@Test
	public void testCreate() throws PayjpException {
		stubNetwork(ThreeDSecureRequest.class, "{\"id\":tdsr_xxxxx,\"resource_id\":car_xxxxx}");
		Map<String, Object> createThreeDSecureRequestParams = new HashMap<String, Object>();
		createThreeDSecureRequestParams.put("resource_id", "car_xxxxx");
		ThreeDSecureRequest threeDSecureRequest = ThreeDSecureRequest.create(createThreeDSecureRequestParams);
		assertEquals(threeDSecureRequest.getId(), "tdsr_xxxxx");
		assertEquals(threeDSecureRequest.getResourceId(), "car_xxxxx");
		verifyPost(ThreeDSecureRequest.class, "https://api.pay.jp/v1/three_d_secure_requests", createThreeDSecureRequestParams);
	}
}
