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

import org.junit.Test;

import java.io.IOException;

import jp.pay.BasePayjpTest;
import jp.pay.model.Event;
import jp.pay.net.APIResource;
import static org.junit.Assert.assertEquals;

public class DeserializerTest extends BasePayjpTest {

	private static Gson gson = APIResource.GSON;

	@Test
	public void deserializeEventDataAccountEvent() throws IOException {
		String json = resource("account_event.json");
		Event e = gson.fromJson(json, Event.class);

		assertEquals(Subscription.class, e.getData().getClass());

		Subscription sub = (Subscription)e.getData();
		assertEquals("sub_dca9de79e5240009adb994d52974", sub.getId());
	}

	@Test
	public void deserializeTermEvent() throws IOException {
		String json = resource("term_event.json");
		Event e = gson.fromJson(json, Event.class);

		assertEquals(Term.class, e.getData().getClass());

		Term term = (Term)e.getData();
		assertEquals("tm_b92b879e60f62b532d6756ae12bb", term.getId());
	}

	@Test
	public void deserializeStatementEvent() throws IOException {
		String json = resource("statement_event.json");
		Event e = gson.fromJson(json, Event.class);

		assertEquals(Statement.class, e.getData().getClass());

		Statement obj = (Statement)e.getData();
		assertEquals("st_178fd25dc7ab7b75906f5d4c4b0e6", obj.getId());
		assertEquals("ba_b92b879e60f62b532d6756ae90af", obj.getBalanceId());

		Term term = obj.getTerm();
		assertEquals("tm_b92b879e60f62b532d6756ae12dd", term.getId());
	}

	@Test
	public void deserializeBalanceEvent() throws IOException {
		String json = resource("balance_event.json");
		Event e = gson.fromJson(json, Event.class);

		assertEquals(Balance.class, e.getData().getClass());

		Balance obj = (Balance)e.getData();
		assertEquals("ba_b92b879e60f62b532d6756ae78af", obj.getId());
	}
}
