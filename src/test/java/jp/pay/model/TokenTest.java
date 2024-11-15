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

public class TokenTest extends BasePayjpTest {
	@Test
	public void testDeserialize() throws PayjpException, IOException {
		String json = resource("token.json");
		Token token = APIResource.GSON.fromJson(json, Token.class);
		assertEquals(Long.valueOf("1442290383"), token.getCreated());
		assertEquals("tok_5ca06b51685e001723a2c3b4aeb4", token.getId());
		assertEquals(Boolean.FALSE, token.getLivemode());
		assertEquals(Boolean.FALSE, token.getUsed());
		assertEquals("card", token.getCard().getObject());
	}

	@Test
	public void testRetrieve() throws PayjpException {
		stubNetwork(Token.class, "{\"id\":\"token1\"}");
		Token token = Token.retrieve("token1");
		verifyGet(Token.class, "https://api.pay.jp/v1/tokens/token1");
		assertEquals(token.getId(), "token1");
	}
}
