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

import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import jp.pay.BasePayjpTest;
import jp.pay.exception.PayjpException;
import jp.pay.model.Account;
import jp.pay.model.Card;
import jp.pay.net.APIResource;
import jp.pay.net.LivePayjpResponseGetter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class AccountTest extends BasePayjpTest {
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
        String json = resource("account.json");
        Account acc = APIResource.GSON.fromJson(json, Account.class);

        assertEquals("acct_8a27db83a7bf11a0c12b0c2833f", acc.getId());

        LinkedList<String> ae = new LinkedList<String>();
        ae.add("merchant");
        ae.add("customer");
        assertEquals(ae, acc.getAccountsEnabled());

        Long created = 1432965397l;
        assertEquals(created, acc.getCreated());

        Integer customer_count = 1;
        assertEquals(customer_count, acc.getCustomer().getCards().count);

		List<Card> customerCards = acc.getCustomer().getCards().getData();
		assertEquals(1, customerCards.size());
		assertEquals("\u8d64\u5742", customerCards.get(0).getAddressCity());
		assertEquals("7-4", customerCards.get(0).getAddressLine1());
		assertEquals("203", customerCards.get(0).getAddressLine2());
		assertEquals("\u6e2f\u533a", customerCards.get(0).getAddressState());
		assertEquals("1070050", customerCards.get(0).getAddressZip());
		assertEquals("passed", customerCards.get(0).getAddressZipCheck());
		assertEquals("Visa", customerCards.get(0).getBrand());

		Long card_created = 1432965397l;
		assertEquals(card_created, customerCards.get(0).getCreated());

		assertEquals("passed", customerCards.get(0).getCvcCheck());

		Integer ex_m = 12;
		assertEquals(ex_m, customerCards.get(0).getExpMonth());

		Integer ex_y = 2016;
		assertEquals(ex_y, customerCards.get(0).getExpYear());

		assertEquals("e1d8225886e3a7211127df751c86787f", customerCards.get(0).getFingerprint());
		assertEquals("car_99abf74cb5527ff68233a8b836dd", customerCards.get(0).getId());
		assertEquals("4242", customerCards.get(0).getLast4());
		assertEquals(true, customerCards.get(0).getLivemode());
		assertEquals("Test Holder", customerCards.get(0).getName());

		assertEquals("/v1/accounts/cards", acc.getCustomer().getCards().getURL());

		Long customer_created = 1432965397l;
		assertEquals(customer_created, acc.getCustomer().getCreated());

		assertEquals(null, acc.getCustomer().getDefaultCard());
		assertEquals("user customer", acc.getCustomer().getDescription());
		assertEquals(null, acc.getCustomer().getEmail());
		assertEquals("acct_cus_38153121efdb7964dd1e147", acc.getCustomer().getId());

        assertEquals(false, acc.getMerchant().getBankEnabled());

        LinkedList<String> mba = new LinkedList<String>();
        mba.add("Visa");
        mba.add("American Express");
        mba.add("MasterCard");
        mba.add("JCB");
        mba.add("Diners Club");
        assertEquals(mba, acc.getMerchant().getBrandsAccepted());

        assertEquals("personal", acc.getMerchant().getBusinessType());
        assertEquals(null, acc.getMerchant().getChargeType());
        assertEquals(null, acc.getMerchant().getContactPhone());
        assertEquals("JP", acc.getMerchant().getCountry());

        Long mc = 1432965397l;
        assertEquals(mc, acc.getMerchant().getCreated());

        LinkedList<String> mcs = new LinkedList<String>();
        mcs.add("jpy");
        assertEquals(mcs, acc.getMerchant().getCurrenciesSupported());

        assertEquals("jpy", acc.getMerchant().getDefaultCurrency());
        assertEquals(false, acc.getMerchant().getDetailsSubmitted());
        assertEquals("acct_mch_002418151ef82e49f6edee1", acc.getMerchant().getId());

        Long mla = 1432965401l;
        assertEquals(mla, acc.getMerchant().getLivemodeActivatedAt());

        assertEquals(true, acc.getMerchant().getLivemodeEnabled());
        assertEquals(null, acc.getMerchant().getProductDetail());
        assertEquals(null, acc.getMerchant().getProductName());
        assertEquals(null, acc.getMerchant().getProductType());
        assertEquals(false, acc.getMerchant().getSitePublished());
        assertEquals(null, acc.getMerchant().getUrl());
    }

    @Test
    public void testRetrieve() throws PayjpException {
        Account.retrieve();

        verifyGet(Account.class, "https://api.pay.jp/v1/accounts");
        verifyNoMoreInteractions(networkMock);
    }
}
