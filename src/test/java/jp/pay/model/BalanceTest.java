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
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.pay.exception.PayjpException;
import jp.pay.BasePayjpTest;
import static org.junit.Assert.assertEquals;

public class BalanceTest extends BasePayjpTest {
	@Test
	public void testDeserialize() throws PayjpException, IOException {
		String json = resource("model/balance.json");
		Balance balance = APIResource.GSON.fromJson(json, Balance.class);
		assertEquals("ba_b92b879e60f62b532d6756ae56af", balance.getId());
		assertEquals(Long.valueOf("1438354824"), balance.getCreated());
		assertEquals(BigInteger.valueOf(Long.valueOf("12300000000")), balance.getNet());
		StatementCollection statements = balance.getStatements();
		assertEquals("st_178fd25dc7ab7b75906f1c3c4b0e6", statements.getData().get(0).getId());
		assertEquals("st_b4a569b0122a7d08b358f198cf263", statements.getData().get(1).getId());
	}

	@Test
	public void testRetrieve() throws PayjpException {
		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("limit", 1);
		stubNetwork(Balance.class, "{\"id\":\"balance1\"}");
		Balance balance = Balance.retrieve("balance1");
		verifyGet(Balance.class, "https://api.pay.jp/v1/balances/balance1");
		String id = balance.getId();
		assertEquals(id, "balance1");
	}

	@Test
	public void testList() throws PayjpException {
		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("limit", 1);
		stubNetwork(BalanceCollection.class, "{\"count\":1,\"data\":[{\"id\":\"balance1\"}]}");
		List<Balance> balances = Balance.all(listParams).getData();
		Balance balance = balances.get(0);
		String id = balance.getId();
		assertEquals(id, "balance1");
	}

}
