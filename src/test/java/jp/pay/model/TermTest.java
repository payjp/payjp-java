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

public class TermTest extends BasePayjpTest {
	@Test
	public void testDeserialize() throws PayjpException, IOException {
		String json = resource("term.json");
		Term term = APIResource.GSON.fromJson(json, Term.class);
		assertEquals(Long.valueOf("1438354812"), term.getCreated());
		assertEquals(Boolean.FALSE, term.getLivemode());
		assertEquals("tm_b92b879e60f62b532d6756ae12aa", term.getId());
		assertEquals(Long.valueOf("1438354802"), term.getStartAt());
		assertEquals(Long.valueOf("1439650803"), term.getEndAt());
		assertEquals((Integer)987, term.getChargeCount());
		assertEquals((Integer)58, term.getRefundCount());
		assertEquals((Integer)5, term.getDisputeCount());
	}

	@Test
	public void testRetrieve() throws PayjpException {
		stubNetwork(Term.class, "{\"id\":\"term1\"}");
		Term term = Term.retrieve("term1");
		verifyGet(Term.class, "https://api.pay.jp/v1/terms/term1");
		assertEquals(term.getId(), "term1");
	}

	@Test
	public void testList() throws PayjpException {
		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("limit", 2);
		stubNetwork(TermCollection.class, "{\"count\":2,\"data\":[{\"id\":\"term1\"},{\"id\":\"term2\"}]}");
		List<Term> terms = Term.all(listParams).getData();
		verifyGet(TermCollection.class, "https://api.pay.jp/v1/terms", listParams);
		assertEquals(terms.size(), 2);
		assertEquals(terms.get(0).getId(), "term1");
		assertEquals(terms.get(1).getId(), "term2");
	}

}
