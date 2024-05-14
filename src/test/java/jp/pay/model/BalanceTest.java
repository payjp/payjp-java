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
		String json = resource("balance.json");
		Balance balance = APIResource.GSON.fromJson(json, Balance.class);

		assertEquals("ba_b92b879e60f62b532d6756ae56af", balance.getId());
		assertEquals(Long.valueOf("1438354824"), balance.getCreated());
		assertEquals(Boolean.FALSE, balance.getLivemode());
		assertEquals(BigInteger.valueOf(Long.valueOf("12300000000")), balance.getNet());
		assertEquals("ten_4f2a6b6b8f4b0f1e8b0fa8bca8b0", balance.getTenantId());
		assertEquals("collecting", balance.getState());
		assertEquals(Boolean.FALSE, balance.getClosed());
		assertEquals(null, balance.getDueDate());

		BankInfo bankInfo = balance.getBankInfo();
		assertEquals("0000", bankInfo.getBankCode());
		assertEquals("123", bankInfo.getBankBranchCode());
		assertEquals("普通", bankInfo.getBankAccountType());
		assertEquals("1234567", bankInfo.getBankAccountNumber());
		assertEquals("ペイ　タロウ", bankInfo.getBankAccountHolderName());
		assertEquals("pending", bankInfo.getBankAccountStatus());

		List<Statement> statements = balance.getStatements();
		assertEquals("st_178fd25dc7ab7b75906f1c3c4b0e6", statements.get(0).getId());
		assertEquals("st_b4a569b0122a7d08b358f198cf263", statements.get(1).getId());
	}

	@Test
	public void testRetrieve() throws PayjpException {
		stubNetwork(Balance.class, "{\"id\":\"balance1\"}");
		Balance balance = Balance.retrieve("balance1");
		verifyGet(Balance.class, "https://api.pay.jp/v1/balances/balance1");
		assertEquals(balance.getId(), "balance1");
	}

	@Test
	public void testList() throws PayjpException {
		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("limit", 2);
		stubNetwork(BalanceCollection.class, "{\"count\":2,\"data\":[{\"id\":\"balance1\"},{\"id\":\"balance2\"}]}");
		List<Balance> balances = Balance.all(listParams).getData();
		verifyGet(BalanceCollection.class, "https://api.pay.jp/v1/balances", listParams);
		assertEquals(balances.size(), 2);
		assertEquals(balances.get(0).getId(), "balance1");
		assertEquals(balances.get(1).getId(), "balance2");
	}

	@Test
	public void testStatementUrls() throws PayjpException {
		stubNetwork(Balance.class, "{\"id\":\"balance1\"}");
		Balance balance = Balance.retrieve("balance1");
		stubNetwork(StatementUrl.class, "{\"expires\": 1695903280,\"object\": \"statement_url\",\"url\": \"https://pay.jp/_/statements/bd84d2d680b8xxxxxxxxxxxxxxxxxxxx\"}");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("platformer", true);
		StatementUrl url = balance.statementUrls(params);
		verifyPost(StatementUrl.class, "https://api.pay.jp/v1/balances/balance1/statement_urls", params);
		assertEquals(url.getUrl(), "https://pay.jp/_/statements/bd84d2d680b8xxxxxxxxxxxxxxxxxxxx");
		assertEquals(url.getExpires().intValue(), 1695903280);
	}
}
