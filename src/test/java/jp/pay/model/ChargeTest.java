/*
 * Copyright (c) 2020 Pay, Inc. (https://pay.co.jp/)
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

import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import jp.pay.BasePayjpTest;
import jp.pay.exception.PayjpException;
import jp.pay.model.Charge;
import jp.pay.model.Card;
import jp.pay.net.APIResource;
import jp.pay.net.LivePayjpResponseGetter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class ChargeTest extends BasePayjpTest {
	@Before
	public void mockPayjpResponseGetter() {
		APIResource.setPayjpResponseGetter(networkMock);
	}

	@After
	public void unmockPayjpResponseGetter() {
		/* This needs to be done because tests aren't isolated in Java */
		APIResource.setPayjpResponseGetter(new LivePayjpResponseGetter());
	}

	@Test
	public void testDeserialize() throws PayjpException, IOException {
		String json = resource("charge.json");

		Charge ch = APIResource.GSON.fromJson(json, Charge.class);

		assertEquals("ch_fa990a4c10672a93053a774730b0a", ch.getId());
		assertEquals(3500, (int) ch.getAmount());

		Long created = 1578372537l;

		assertEquals(created, ch.getCreated());
		assertEquals("jpy", ch.getCurrency());
		assertFalse(ch.getLivemode());
		assertTrue(ch.getPaid());
		assertFalse(ch.getRefunded());
		assertTrue(ch.getCaptured());
		assertEquals("test charge", ch.getDescription());
		assertNull(ch.getFailureMessage());
		assertNull(ch.getFailureCode());
		assertEquals(0, (int) ch.getAmountRefunded());
		assertNull(ch.getCustomer());
		assertNull(ch.getExpiredAt());
		assertNull(ch.getRefundReason());
		assertNull(ch.getSubscription());
		assertNull(ch.getMetadata());
		assertEquals("3.00", ch.getFeeRate());
		assertEquals("attempt", ch.getThreeDSecureStatus());
		assertEquals("tm_b92b879e60f62b532d6756ae12cc", ch.getTermId());

		Card ca = ch.getCard();
		assertEquals("car_d0e44730f83b0a19ba6caee04160", ca.getId());
		assertEquals("Visa", ca.getBrand());
		assertEquals(created, ca.getCreated());
		assertNull(ca.getLivemode());
		assertEquals(2, (int) ca.getExpMonth());
		assertEquals(2020, (int) ca.getExpYear());
		assertEquals("4242", ca.getLast4());
		assertNull(ca.getCountry());
		assertEquals("PAY TARO", ca.getName());
		assertNull(ca.getAddressLine1());
		assertNull(ca.getAddressLine2());
		assertNull(ca.getAddressCity());
		assertNull(ca.getAddressZip());
		assertNull(ca.getAddressState());
		assertEquals("unchecked", ca.getAddressZipCheck());
		assertEquals("unchecked", ca.getCvcCheck());
		assertEquals("e1d8225886e3a7211127df751c86787f", ca.getFingerprint());
		assertEquals("liveaccount@example.com", ca.getEmail());
		assertEquals("+81301234567", ca.getPhone());
		assertNull(ca.getThreeDSecureStatus());

		assertEquals("Visa", ca.getBrand());
	}
}
