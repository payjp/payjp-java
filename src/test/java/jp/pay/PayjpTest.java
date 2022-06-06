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
package jp.pay;


import jp.pay.exception.CardException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jp.pay.exception.InvalidRequestException;
import jp.pay.exception.PayjpException;
import jp.pay.exception.AuthenticationException;
import jp.pay.exception.APIException;
import jp.pay.model.Account;
import jp.pay.model.Card;
import jp.pay.model.Charge;
import jp.pay.model.ChargeCollection;
import jp.pay.model.Customer;
import jp.pay.model.CustomerCardCollection;
import jp.pay.model.CustomerCollection;
import jp.pay.model.CustomerSubscriptionCollection;
import jp.pay.model.DeletedCard;
import jp.pay.model.DeletedCustomer;
import jp.pay.model.DeletedPlan;
import jp.pay.model.DeletedSubscription;
import jp.pay.model.Event;
import jp.pay.model.EventCollection;
import jp.pay.model.Plan;
import jp.pay.model.PlanCollection;
import jp.pay.model.Subscription;
import jp.pay.model.SubscriptionCollection;
import jp.pay.model.Token;
import jp.pay.model.Transfer;
import jp.pay.model.TransferChargeCollection;
import jp.pay.model.TransferCollection;
import jp.pay.net.APIResource;
import jp.pay.net.LivePayjpResponseGetter;
import jp.pay.net.RequestOptions;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PayjpTest extends BasePayjpTest {
	static Map<String, Object> defaultCardParams = new HashMap<String, Object>();
	static Map<String, Object> defaultDebitCardParams = new HashMap<String, Object>();
	static Map<String, Object> defaultChargeParams = new HashMap<String, Object>();
	static Map<String, Object> defaultCustomerParams = new HashMap<String, Object>();
	static Map<String, Object> defaultPlanParams = new HashMap<String, Object>();
	static Map<String, Object> defaultCouponParams = new HashMap<String, Object>();
	static Map<String, Object> defaultTokenParams = new HashMap<String, Object>();
	static Map<String, Object> defaultDebitTokenParams = new HashMap<String, Object>();
	static RequestOptions cardSupportedRequestOptions;
	static String currency = "jpy";
	static String country = "JP";

	static String getUniquePlanId() {
		return String.format("MY-J-PLAN-%s", UUID.randomUUID().toString().substring(24));
	}

	static Map<String, Object> getUniquePlanParams() {
		Map<String, Object> uniqueParams = new HashMap<String, Object>();
		uniqueParams.putAll(defaultPlanParams);
		uniqueParams.put("id", getUniquePlanId());
		return uniqueParams;
	}

	static Map<String, Object> getSubscriptionParams() throws PayjpException {
		stubNetwork(Plan.class, "{\"id\":\"1\"}");
		Plan plan = Plan.create(getUniquePlanParams());
		stubNetwork(Customer.class, "{\"id\":\"1\"}");
		Customer customer = Customer.create(defaultCustomerParams);
		Map<String, Object> subscriptionParams = new HashMap<String, Object>();
		subscriptionParams.put("plan", plan.getId());
		subscriptionParams.put("customer", customer.getId());
		return subscriptionParams;
	}

    @Before
    public void mockPayjpResponseGetter() {
        APIResource.setPayjpResponseGetter(networkMock);
    }

    @After
    public void unmockPayjpResponseGetter() {
        /* This needs to be done because tests aren't isolated in Java */
        APIResource.setPayjpResponseGetter(new LivePayjpResponseGetter());
    }

	@Before
	public void before() {
		Payjp.apiVersion = null;
	}

	@BeforeClass
	public static void setUp() {
		Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";	// public api key for test

		cardSupportedRequestOptions = RequestOptions.builder().setPayjpVersion("2015-06-23").build();

		defaultCardParams.put("number", "4242424242424242");
		defaultCardParams.put("exp_month", "02");
		defaultCardParams.put("exp_year", "2020");
		defaultCardParams.put("cvc", "123");
		defaultCardParams.put("name", "Test Holder");
		defaultCardParams.put("address_line1", "7-4");
		defaultCardParams.put("address_line2", "203");

		defaultCardParams.put("address_city", "\u8d64\u5742");
		defaultCardParams.put("address_zip", "1500011");
		defaultCardParams.put("address_state", "\u6e2f\u533a");
		defaultCardParams.put("country", "JP");

		defaultDebitCardParams.put("number", "4000056655665556");
		defaultDebitCardParams.put("exp_month", "12");
		defaultDebitCardParams.put("exp_year", "2020");
		defaultDebitCardParams.put("cvc", "123");
		defaultDebitCardParams.put("name", "J Bindings Debitholder");
		defaultDebitCardParams.put("address_line1", "140 2nd Street");
		defaultDebitCardParams.put("address_line2", "4th Floor");
		defaultDebitCardParams.put("address_city", "\u8d64\u5742");
		defaultDebitCardParams.put("address_zip", "1500011");
		defaultDebitCardParams.put("address_state", "\u6e2f\u533a");
		defaultDebitCardParams.put("country", country);

		defaultChargeParams.put("amount", 100);
		defaultChargeParams.put("currency", currency);
		defaultChargeParams.put("card", defaultCardParams);

		defaultTokenParams.put("card", defaultCardParams);
		defaultDebitTokenParams.put("card", defaultDebitCardParams);

		defaultCustomerParams.put("card", defaultCardParams);
		defaultCustomerParams.put("description", "J Bindings Customer");

		defaultPlanParams.put("amount", 100);
		defaultPlanParams.put("currency", currency);
		defaultPlanParams.put("interval", "month");
		defaultPlanParams.put("name", "J Bindings Plan");
	}

	@Test(expected=AuthenticationException.class)
	public void testAuthenticationException() throws PayjpException {
		stubNetwork(Customer.class, 401, "{\"error\":{\"type\":\"auth_error\",\"message\":\"Invalid API Key: sk_test_***\",\"status\":401}}");
		Charge.create(defaultChargeParams);
	}

	@Test(expected=APIException.class)
	public void testAPIException() throws PayjpException {
		stubNetwork(Customer.class, 500, "{\"error\":{\"type\":\"server_error\",\"message\":\"xxx\",\"status\":500}}");
		Charge.create(defaultChargeParams);
	}

	@Test(expected=APIException.class)
	public void testAPIExceptionWithHtmlResponse() throws PayjpException {
		stubNetwork(Customer.class, 500, "<html>504 Gateway Time-out</html>");
		Charge.create(defaultChargeParams);
	}

	@Test
	public void testChargeCreate() throws PayjpException {
		stubNetwork(Charge.class, "{\"refunded\":false,\"paid\":true}");
		Charge createdCharge = Charge.create(defaultChargeParams);
		assertFalse(createdCharge.getRefunded());
		assertTrue(createdCharge.getPaid());

		Charge retrievedCharge = Charge.retrieve(createdCharge.getId());
		assertEquals(createdCharge.getId(), retrievedCharge.getId());
	}

	@Test
	public void testChargeListByCustomer() throws PayjpException {
		stubNetwork(Customer.class, "{\"cards\":{\"count\":0,\"data\":[]}}");
		Customer customer = Customer.create(defaultCustomerParams);

		Map<String, Object> createParams = new HashMap<String, Object>();
		createParams.put("customer", customer.getId());
		createParams.put("amount", 100);
		createParams.put("currency", currency);

		stubNetwork(Charge.class, "{}");
		Charge charge = Charge.create(createParams);

		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("customer", customer.getId());

		stubNetwork(ChargeCollection.class, "{\"count\":1,\"data\":[{}]}");
		ChargeCollection charges = Charge.all(listParams);
		assertEquals(1, charges.getData().size());
		assertEquals(charge.getId(), charges.getData().get(0).getId());
	}

    @Test
    public void testChargeRetrieveNullId() throws PayjpException {
        stubNetwork(Card.class, 404, "{\"error\":{\"type\":\"client_error\",\"message\":\"Unrecognized request URL: GET /v1/charges/\",\"status\":404}}");
        try {
            Charge.retrieve(null);
            assertTrue(false);
        }
        catch (InvalidRequestException e) {
            assertEquals("Unrecognized request URL: GET /v1/charges/", e.getMessage());
            assertEquals("client_error", e.getType());
            assertEquals(null, e.getCode());
            assertEquals(null, e.getParam());
        }
    }

    @Test
    public void testChargeRetrieveInvalidId() throws PayjpException {
        stubNetwork(Card.class, 404, "{\"error\":{\"type\":\"client_error\",\"code\":\"invalid_id\",\"message\":\"No such charge: hoge\",\"param\":\"id\",\"status\":404}}");
        try {
            Charge.retrieve("hoge");
            assertTrue(false);
        }
        catch (InvalidRequestException e) {
            assertEquals("No such charge: hoge", e.getMessage());
            assertEquals("client_error", e.getType());
            assertEquals("invalid_id", e.getCode());
            assertEquals("id", e.getParam());
        }
    }

	@Test
	public void testChargeUpdate() throws PayjpException {
		stubNetwork(Charge.class, "{\"id\":1}");
		Charge ch = Charge.create(defaultChargeParams);
		String id = ch.getId();

		Map<String, Object> updateParams = new HashMap<String, Object>();
		updateParams.put("description", "Updated Description");

		stubNetwork(Charge.class, "{\"id\":1,\"description\":\"Updated Description\"}");
		Charge updateCharge = ch.update(updateParams);

		assertEquals(id, updateCharge.getId());
		assertEquals("Updated Description", updateCharge.getDescription());
	}

	@Test
	public void testChargeRefund() throws PayjpException {
		stubNetwork(Charge.class, "{}");
		Charge ch = Charge.create(defaultChargeParams);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("amount", 10);
		params.put("refund_reason", "foo bar");

		stubNetwork(Charge.class, "{\"amount_refunded\":10,\"refunded\":true,\"refund_reason\":\"foo bar\"}");
		Charge ch_rf = ch.refund(params);
		Integer ar = 10;
		assertEquals(ar, ch_rf.getAmountRefunded());
		assertTrue(ch_rf.getRefunded());
		assertEquals("foo bar", ch_rf.getRefundReason());

		stubNetwork(Charge.class, "{\"amount_refunded\":10,\"refunded\":true,\"refund_reason\":\"foo bar\"}");
		Charge chr = Charge.retrieve(ch.getId());
		assertEquals(ar, chr.getAmountRefunded());
		assertTrue(chr.getRefunded());
		assertEquals("foo bar", chr.getRefundReason());

		Map<String, Object> params_2 = new HashMap<String, Object>();
		stubNetwork(Charge.class, "{\"amount_refunded\":100,\"refunded\":true,\"refund_reason\":\"foo bar\"}");
		Charge ch_rf_2 = chr.refund(params_2);

		assertEquals(defaultChargeParams.get("amount"), ch_rf_2.getAmountRefunded());
		assertTrue(ch_rf_2.getRefunded());
	}

	@Test
	public void testChargeRefundCreateApiKey() throws PayjpException {
		stubNetwork(Charge.class, "{}");
		Charge ch = Charge.create(defaultChargeParams);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("amount", 10);
		Charge ch_rf = ch.refund(params);

		stubNetwork(Charge.class, "{\"amount_refunded\":10,\"refunded\":true}");
		Charge ch_r = Charge.retrieve(ch.getId());
		assertEquals(ch_rf.getId(), ch_r.getId());

		Integer ar = 10;
		assertEquals(ar, ch_r.getAmountRefunded());
		assertTrue(ch_r.getRefunded());
	}

	@Test
	public void testChargeCapture() throws PayjpException {
		Map<String, Object> options = new HashMap<String, Object>(defaultChargeParams);
		options.put("capture", false);

		stubNetwork(Charge.class, "{\"captured\":false}");
		Charge created = Charge.create(options);
		assertFalse(created.getCaptured());

		stubNetwork(Charge.class, "{\"captured\":true}");
		Charge captured = created.capture();
		assertTrue(captured.getCaptured());
	}

	@Test
	public void testChargeList() throws PayjpException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("limit", 1);
		params.put("offset", 0);
		stubNetwork(ChargeCollection.class, "{\"count\":1,\"data\":[{}]}");
		List<Charge> charges = Charge.all(params).getData();
		assertEquals(charges.size(), 1);
	}

	@Test(expected = CardException.class)
	public void testInvalidCard() throws PayjpException {
		Map<String, Object> invalidChargeParams = new HashMap<String, Object>();
		invalidChargeParams.putAll(defaultChargeParams);
		Map<String, Object> invalidCardParams = new HashMap<String, Object>();
		invalidCardParams.put("number", "4242424242424241");
		invalidCardParams.put("exp_month", 12);
		invalidCardParams.put("exp_year", 2015);
		invalidChargeParams.put("card", invalidCardParams);
		stubNetwork(Charge.class, 402, "{\"error\":{\"type\":\"\",\"code\":\"\",\"message\":\"\",\"param\":\"\"}}");
		Charge.create(invalidChargeParams);
	}

	@Test
	public void testDeclinedCard() throws PayjpException {
		Map<String, Object> declinedChargeParams = new HashMap<String, Object>();
		declinedChargeParams.putAll(defaultChargeParams);
		Map<String, Object> declinedCardParams = new HashMap<String, Object>();
		declinedCardParams.put("number", "4000000000000002");
		declinedCardParams.put("exp_month", "12");
		declinedCardParams.put("exp_year", "2015");
		declinedChargeParams.put("card", declinedCardParams);

		try {
			Charge.create(declinedChargeParams);
		}
		catch (CardException e) {
			assertEquals("card_declined", e.getCode());
		}
	}

	@Test
	public void testExpiredNumber() throws PayjpException {
		Map<String, Object> declinedChargeParams = new HashMap<String, Object>();
		declinedChargeParams.putAll(defaultChargeParams);
		Map<String, Object> declinedCardParams = new HashMap<String, Object>();
		declinedCardParams.put("number", "4000000000000066");
		declinedCardParams.put("exp_month", "12");
		declinedCardParams.put("exp_year", "2015");
		declinedChargeParams.put("card", declinedCardParams);

		stubNetwork(Card.class, 402, "{\"error\":{\"type\":\"\",\"code\":\"expired_card\",\"message\":\"There is no card with ID: 0\",\"param\":\"\"}}");
		try {
			Charge.create(declinedChargeParams);
		}
		catch (CardException e) {
			assertEquals("expired_card", e.getCode());
		}
	}

	@Test
	public void testIncorrectCvcNumber() throws PayjpException {
		Map<String, Object> declinedChargeParams = new HashMap<String, Object>();
		declinedChargeParams.putAll(defaultChargeParams);
		Map<String, Object> declinedCardParams = new HashMap<String, Object>();
		declinedCardParams.put("number", "4000000000000890");
		declinedCardParams.put("exp_month", "12");
		declinedCardParams.put("exp_year", "2015");
		declinedChargeParams.put("card", declinedCardParams);

		try {
			Charge.create(declinedChargeParams);
		}
		catch (CardException e) {
			assertEquals("invalid_cvc", e.getCode());
		}
	}

	@Test
	public void testProceErrorNumber() throws PayjpException {
		Map<String, Object> declinedChargeParams = new HashMap<String, Object>();
		declinedChargeParams.putAll(defaultChargeParams);
		Map<String, Object> declinedCardParams = new HashMap<String, Object>();
		declinedCardParams.put("number", "4000000000000123");
		declinedCardParams.put("exp_month", "12");
		declinedCardParams.put("exp_year", "2015");
		declinedChargeParams.put("card", declinedCardParams);

		try {
			Charge.create(declinedChargeParams);
		}
		catch (CardException e) {
			assertEquals("processing_error", e.getCode());
		}
	}

	@Test
	public void testInvalidAddressZipTest() throws PayjpException {
		Map<String, Object> invalidChargeParams = new HashMap<String, Object>();
		invalidChargeParams.putAll(defaultChargeParams);
		Map<String, Object> invalidCardParams = new HashMap<String, Object>();
		invalidCardParams.put("number", "4000000000000070");
		invalidCardParams.put("exp_month", "12");
		invalidCardParams.put("exp_year", "2015");
		invalidChargeParams.put("card", invalidCardParams);
		stubNetwork(Charge.class, "{\"paid\":true,\"card\":{\"address_zip_check\":\"failed\",\"object\":\"card\"}}");
		Charge charge = Charge.create(invalidChargeParams, cardSupportedRequestOptions);
		assertEquals(charge.getPaid(), true);
		assertEquals(charge.getCard().getAddressZipCheck(), "failed");
	}

	@Test
	public void testInvalidCvcTest() throws PayjpException {
		Map<String, Object> invalidChargeParams = new HashMap<String, Object>();
		invalidChargeParams.putAll(defaultChargeParams);
		Map<String, Object> invalidCardParams = new HashMap<String, Object>();
		invalidCardParams.put("number", "4000000000000100");
		invalidCardParams.put("exp_month", "12");
		invalidCardParams.put("exp_year", "2015");
		invalidChargeParams.put("card", invalidCardParams);
		stubNetwork(Charge.class, "{\"paid\":true,\"card\":{\"cvc_check\":\"failed\",\"object\":\"card\"}}");
		Charge charge = Charge.create(invalidChargeParams, cardSupportedRequestOptions);
		assertEquals(charge.getPaid(), true);
		assertEquals(charge.getCard().getCvcCheck(), "failed");
	}

	@Test
	public void testUnavailableCvcTest() throws PayjpException {
		Map<String, Object> invalidChargeParams = new HashMap<String, Object>();
		invalidChargeParams.putAll(defaultChargeParams);
		Map<String, Object> invalidCardParams = new HashMap<String, Object>();
		invalidCardParams.put("number", "4000000000000150");
		invalidCardParams.put("exp_month", "12");
		invalidCardParams.put("exp_year", "2015");
		invalidChargeParams.put("card", invalidCardParams);
		stubNetwork(Charge.class, "{\"paid\":true,\"card\":{\"cvc_check\":\"unavailable\",\"object\":\"card\"}}");
		Charge charge = Charge.create(invalidChargeParams, cardSupportedRequestOptions);
		assertEquals(charge.getPaid(), true);
		assertEquals(charge.getCard().getCvcCheck(), "unavailable");
	}

	@Test
	public void testCustomerCreate() throws PayjpException {
		stubNetwork(Customer.class, "{\"description\":\"J Bindings Customer\",\"cards\":{\"count\":1,\"data\":[{\"address_city\":null,\"address_line1\":null,\"address_line2\":null,\"address_state\":null,\"address_zip\":null,\"address_zip_check\":null,\"brand\":null,\"country\":null,\"created\":null,\"customer\":null,\"cvc_check\":null,\"exp_month\":null,\"exp_year\":null,\"fingerprint\":null,\"id\":null,\"last4\":\"4242\",\"name\":null,\"object\": \"card\"}]}}");
		Customer customer = Customer.create(defaultCustomerParams, cardSupportedRequestOptions);
		assertEquals("J Bindings Customer", customer.getDescription());
		List<Card> customerCards = customer.getCards().getData();
		assertEquals(1, customerCards.size());
		assertEquals("4242", customerCards.get(0).getLast4());
	}

	@Test
	public void testCustomerRetrieve() throws PayjpException {
		stubNetwork(Customer.class, "{\"id\":\"1\"}");
		Customer createdCustomer = Customer.create(defaultCustomerParams);
		stubNetwork(Customer.class, "{\"id\":\"1\"}");
		Customer retrievedCustomer = Customer.retrieve(createdCustomer.getId());
		assertEquals(createdCustomer.getCreated(),
				retrievedCustomer.getCreated());
		assertEquals(createdCustomer.getId(), retrievedCustomer.getId());
	}

	@Test
	public void testCustomerUpdate() throws PayjpException {
		stubNetwork(Customer.class, "{\"cards\":{\"count\":0,\"data\":[]},\"description\":\"\"}");
		Customer createdCustomer = Customer.create(defaultCustomerParams);
		Map<String, Object> updateParams = new HashMap<String, Object>();
		updateParams.put("description", "Updated Description");
		stubNetwork(Customer.class, "{\"cards\":{\"count\":0,\"data\":[]},\"description\":\"Updated Description\"}");
		Customer updatedCustomer = createdCustomer.update(updateParams);
		assertEquals(updatedCustomer.getDescription(), "Updated Description");
	}

	@Test
	public void testCustomerDelete() throws PayjpException {
		stubNetwork(Customer.class, "{\"id\":\"1\"}");
		Customer created = Customer.create(defaultCustomerParams);
		stubNetwork(DeletedCustomer.class, "{\"id\":\"1\",\"deleted\":true}");
		DeletedCustomer deleted = created.delete();

		assertTrue(deleted.getDeleted());
		assertEquals(deleted.getId(), created.getId());

		stubNetwork(Customer.class, 404, "{\"error\":{\"type\":\"\",\"code\":\"\",\"message\":\"There is no customer with ID: 1\",\"param\":\"\"}}");
		try {
			Customer.retrieve(created.getId());
		}
		catch (InvalidRequestException e) {
			assertEquals("There is no customer with ID: "+created.getId(), e.getMessage());
		}
	}

	@Test
	public void testCustomerCardCreate() throws PayjpException {
		stubNetwork(Customer.class, "{\"cards\":{\"count\":1,\"data\":[{\"address_city\":null,\"address_line1\":null,\"address_line2\":null,\"address_state\":null,\"address_zip\":null,\"address_zip_check\":null,\"brand\":null,\"country\":null,\"created\":null,\"customer\":null,\"cvc_check\":null,\"exp_month\":null,\"exp_year\":null,\"fingerprint\":null,\"id\":null,\"last4\":null,\"name\":null,\"object\": \"card\"}]}}");
		Customer createdCustomer = Customer.create(defaultCustomerParams, cardSupportedRequestOptions);
		String originalDefaultCard = createdCustomer.getDefaultCard();

		Map<String, Object> cardPrams = new HashMap<String, Object>();
		cardPrams.put("number", "4242424242424242");
		cardPrams.put("exp_year", 2022);
		cardPrams.put("exp_month", 12);
		stubNetwork(Card.class, "{\"address_city\":null,\"address_line1\":null,\"address_line2\":null,\"address_state\":null,\"address_zip\":null,\"address_zip_check\":null,\"brand\":null,\"country\":null,\"created\":null,\"customer\":null,\"cvc_check\":null,\"exp_month\":null,\"exp_year\":null,\"fingerprint\":null,\"id\":null,\"last4\":null,\"name\":null,\"object\": \"card\"}");
		Card addedCard = createdCustomer.createCard(cardPrams);

		Map<String, Object> cardParams_2 = new HashMap<String, Object>();
		cardParams_2.put("number", "4242424242424242");
		cardParams_2.put("exp_year", 2021);
		cardParams_2.put("exp_month", 12);

		Map<String, Object> tokenParams = new HashMap<String, Object>();
		tokenParams.put("card", cardParams_2);
		stubNetwork(Token.class, "{\"id\":1}");
		Token token = Token.create(tokenParams);

		stubNetwork(Card.class, "{\"id\":1,\"object\":\"card\"}");
		createdCustomer.createCard(token.getId());

		stubNetwork(Customer.class, "{\"id\":1,\"cards\":{\"count\":3,\"data\":[{\"address_city\":null,\"address_line1\":null,\"address_line2\":null,\"address_state\":null,\"address_zip\":null,\"address_zip_check\":null,\"brand\":null,\"country\":null,\"created\":null,\"customer\":null,\"cvc_check\":null,\"exp_month\":null,\"exp_year\":null,\"fingerprint\":null,\"id\":null,\"last4\":null,\"name\":null,\"object\": \"card\"},{\"address_city\":null,\"address_line1\":null,\"address_line2\":null,\"address_state\":null,\"address_zip\":null,\"address_zip_check\":null,\"brand\":null,\"country\":null,\"created\":null,\"customer\":null,\"cvc_check\":null,\"exp_month\":null,\"exp_year\":null,\"fingerprint\":null,\"id\":null,\"last4\":null,\"name\":null,\"object\": \"card\"},{\"address_city\":null,\"address_line1\":null,\"address_line2\":null,\"address_state\":null,\"address_zip\":null,\"address_zip_check\":null,\"brand\":null,\"country\":null,\"created\":null,\"customer\":null,\"cvc_check\":null,\"exp_month\":null,\"exp_year\":null,\"fingerprint\":null,\"id\":null,\"last4\":null,\"name\":null,\"object\": \"card\"}]}}");
		Customer updatedCustomer = Customer.retrieve(createdCustomer.getId(), cardSupportedRequestOptions);
		assertEquals((Integer) updatedCustomer.getCards().getData().size(), (Integer) 3);
		assertEquals(updatedCustomer.getDefaultCard(), originalDefaultCard);

		Map<String, Object> updateParams = new HashMap<String, Object>();
		updateParams.put("default_card", addedCard.getId());
		stubNetwork(Customer.class, "{\"id\":1,\"cards\":{\"count\":3,\"data\":[{\"address_city\":null,\"address_line1\":null,\"address_line2\":null,\"address_state\":null,\"address_zip\":null,\"address_zip_check\":null,\"brand\":null,\"country\":null,\"created\":null,\"customer\":null,\"cvc_check\":null,\"exp_month\":null,\"exp_year\":null,\"fingerprint\":null,\"id\":null,\"last4\":null,\"name\":null,\"object\": \"card\"},{\"address_city\":null,\"address_line1\":null,\"address_line2\":null,\"address_state\":null,\"address_zip\":null,\"address_zip_check\":null,\"brand\":null,\"country\":null,\"created\":null,\"customer\":null,\"cvc_check\":null,\"exp_month\":null,\"exp_year\":null,\"fingerprint\":null,\"id\":null,\"last4\":null,\"name\":null,\"object\": \"card\"},{\"address_city\":null,\"address_line1\":null,\"address_line2\":null,\"address_state\":null,\"address_zip\":null,\"address_zip_check\":null,\"brand\":null,\"country\":null,\"created\":null,\"customer\":null,\"cvc_check\":null,\"exp_month\":null,\"exp_year\":null,\"fingerprint\":null,\"id\":null,\"last4\":null,\"name\":null,\"object\": \"card\"}]}}");
		Customer customerAfterDefaultCardUpdate = updatedCustomer.update(updateParams, cardSupportedRequestOptions);
		assertEquals((Integer) customerAfterDefaultCardUpdate.getCards().getData().size(), (Integer) 3);
		assertEquals(customerAfterDefaultCardUpdate.getDefaultCard(), addedCard.getId());

		stubNetwork(Card.class, "{\"address_city\":null,\"address_line1\":null,\"address_line2\":null,\"address_state\":null,\"address_zip\":null,\"address_zip_check\":null,\"brand\":null,\"country\":null,\"created\":null,\"customer\":null,\"cvc_check\":null,\"exp_month\":null,\"exp_year\":null,\"fingerprint\":null,\"id\":null,\"last4\":null,\"name\":null,\"object\": \"card\"}");
		assertEquals(customerAfterDefaultCardUpdate.getCards().retrieve(originalDefaultCard).getId(), originalDefaultCard);
		stubNetwork(Card.class, "{\"address_city\":null,\"address_line1\":null,\"address_line2\":null,\"address_state\":null,\"address_zip\":null,\"address_zip_check\":null,\"brand\":null,\"country\":null,\"created\":null,\"customer\":null,\"cvc_check\":null,\"exp_month\":null,\"exp_year\":null,\"fingerprint\":null,\"id\":null,\"last4\":null,\"name\":null,\"object\": \"card\"}");
		assertEquals(customerAfterDefaultCardUpdate.getCards().retrieve(addedCard.getId()).getId(), addedCard.getId());
	}

	@Test
	public void testCustomerCardRetrieve() throws PayjpException {
		stubNetwork(Customer.class, "{\"cards\":{\"count\":1,\"data\":[{\"address_city\":null,\"address_line1\":null,\"address_line2\":null,\"address_state\":null,\"address_zip\":null,\"address_zip_check\":null,\"brand\":null,\"country\":null,\"created\":null,\"customer\":null,\"cvc_check\":null,\"exp_month\":null,\"exp_year\":null,\"fingerprint\":null,\"id\":null,\"last4\":null,\"name\":null,\"object\": \"card\"}]}}");
		Customer customer = Customer.create(defaultCustomerParams, cardSupportedRequestOptions);
		Card originalCard = customer.getCards().getData().get(0);

		stubNetwork(Card.class, "{\"address_city\":null,\"address_line1\":null,\"address_line2\":null,\"address_state\":null,\"address_zip\":null,\"address_zip_check\":null,\"brand\":null,\"country\":null,\"created\":null,\"customer\":null,\"cvc_check\":null,\"exp_month\":null,\"exp_year\":null,\"fingerprint\":null,\"id\":null,\"last4\":null,\"name\":null,\"object\": \"card\"}");
		Card retrieveCard = customer.getCards().retrieve(originalCard.getId());
		assertEquals(originalCard.getId(), retrieveCard.getId());
	}

	@Test
	public void testCustomerCardUpdate() throws PayjpException {
		stubNetwork(Customer.class, "{\"cards\":{\"count\":1,\"data\":[{\"address_city\":null,\"address_line1\":null,\"address_line2\":null,\"address_state\":null,\"address_zip\":null,\"address_zip_check\":null,\"brand\":null,\"country\":null,\"created\":null,\"customer\":null,\"cvc_check\":null,\"exp_month\":null,\"exp_year\":null,\"fingerprint\":null,\"id\":null,\"last4\":null,\"name\":null,\"object\": \"card\"}]}}");
		Customer customer = Customer.create(defaultCustomerParams, cardSupportedRequestOptions);
		Map<String, Object> updateParams = new HashMap<String, Object>();

		updateParams.put("name", "J Bindings Cardholder, Jr.");
		stubNetwork(Card.class, "{\"address_city\":null,\"address_line1\":null,\"address_line2\":null,\"address_state\":null,\"address_zip\":null,\"address_zip_check\":null,\"brand\":null,\"country\":null,\"created\":null,\"customer\":null,\"cvc_check\":null,\"exp_month\":null,\"exp_year\":null,\"fingerprint\":null,\"id\":null,\"last4\":null,\"name\":\"J Bindings Cardholder, Jr.\",\"object\": \"card\"}");
		Card updatedCard = customer.getCards().getData().get(0).update(updateParams);
		assertEquals("J Bindings Cardholder, Jr.", updatedCard.getName());
	}

	@Test(expected=InvalidRequestException.class)
	public void testCustomerUpdateToBlank() throws PayjpException {
		stubNetwork(Customer.class, 400, "{\"error\":{\"type\":\"\",\"code\":\"\",\"message\":\"description cannot be empty?\",\"param\":\"\"}}");
		Customer createdCustomer = Customer.create(defaultCustomerParams);
		Map<String, Object> updateParams = new HashMap<String, Object>();
		updateParams.put("description", "");
		createdCustomer.update(updateParams);
	}

	@Test
	public void testCustomerUpdateToNull() throws PayjpException {
		stubNetwork(Customer.class, "{\"description\":\"description\",\"cards\":{\"count\":0,\"data\":[]}}");
		Customer createdCustomer = Customer.create(defaultCustomerParams);
		Map<String, Object> updateParams = new HashMap<String, Object>();
		updateParams.put("description", null);
		stubNetwork(Customer.class, "{\"description\":\"\",\"cards\":{\"count\":0,\"data\":[]}}");
		Customer updatedCustomer = createdCustomer.update(updateParams);
		assertEquals("",updatedCustomer.getDescription());
	}

	@Test
	public void testCustomerCardDelete() throws PayjpException {
		stubNetwork(Customer.class, "{\"cards\":{\"count\":1,\"data\":[{\"address_city\":null,\"address_line1\":null,\"address_line2\":null,\"address_state\":null,\"address_zip\":null,\"address_zip_check\":null,\"brand\":null,\"country\":null,\"created\":null,\"customer\":null,\"cvc_check\":null,\"exp_month\":null,\"exp_year\":null,\"fingerprint\":null,\"id\":0,\"last4\":null,\"name\":null,\"object\": \"card\"}]}}");
		Customer customer = Customer.create(defaultCustomerParams, cardSupportedRequestOptions);
		Map<String, Object> cardPrams = new HashMap<String, Object>();
		cardPrams.put("number", "4242424242424242");
		cardPrams.put("exp_year", 2022);
		cardPrams.put("exp_month", 12);

		stubNetwork(Card.class, "{\"address_city\":null,\"address_line1\":null,\"address_line2\":null,\"address_state\":null,\"address_zip\":null,\"address_zip_check\":null,\"brand\":null,\"country\":null,\"created\":null,\"customer\":null,\"cvc_check\":null,\"exp_month\":null,\"exp_year\":null,\"fingerprint\":null,\"id\":0,\"last4\":null,\"name\":null,\"object\": \"card\"}");
		customer.createCard(cardPrams);

		Card card = customer.getCards().getData().get(0);
		stubNetwork(DeletedCard.class, "{\"address_city\":null,\"address_line1\":null,\"address_line2\":null,\"address_state\":null,\"address_zip\":null,\"address_zip_check\":null,\"brand\":null,\"country\":null,\"created\":null,\"customer\":null,\"cvc_check\":null,\"exp_month\":null,\"exp_year\":null,\"fingerprint\":null,\"id\":0,\"last4\":null,\"name\":null,\"deleted\":true,\"object\": \"deleted_card\"}");
		DeletedCard deletedCard = card.delete();
		stubNetwork(Customer.class, "{\"cards\":{\"count\":0,\"data\":[]}}");
		Customer retrievedCustomer = Customer.retrieve(customer.getId(), cardSupportedRequestOptions);

		assertTrue(deletedCard.getDeleted());
		assertEquals(deletedCard.getId(), card.getId());

		stubNetwork(Card.class, 404, "{\"error\":{\"type\":\"\",\"code\":\"\",\"message\":\"There is no card with ID: 0\",\"param\":\"\"}}");
		try {
			retrievedCustomer.getCards().retrieve(card.getId());
		}
		catch (InvalidRequestException e) {
			assertEquals("There is no card with ID: "+card.getId(), e.getMessage());
		}
	}

	@Test
	public void testCustomerCardList() throws PayjpException {
		stubNetwork(Customer.class, "{\"cards\":{\"count\":0,\"data\":[]}}");
		Customer customer = Customer.create(defaultCustomerParams, cardSupportedRequestOptions);
		Map<String, Object> cardParams_2 = defaultCardParams;
		cardParams_2.put("exp_year", 2022);
		stubNetwork(Card.class, "{\"address_city\":null,\"address_line1\":null,\"address_line2\":null,\"address_state\":null,\"address_zip\":null,\"address_zip_check\":null,\"brand\":null,\"country\":null,\"created\":null,\"customer\":null,\"cvc_check\":null,\"exp_month\":null,\"exp_year\":null,\"fingerprint\":null,\"id\":0,\"last4\":null,\"name\":null,\"object\": \"card\"}");
		customer.createCard(cardParams_2);

		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("limit", 2);

		stubNetwork(CustomerCardCollection.class, "{\"count\":2,\"data\":[{\"object\":\"card\"},{\"object\":\"card\"}]}");
		List<Card> cards = customer.getCards().all(listParams).getData();
		assertEquals(2, cards.size());
	}

	@Test
	public void testCustomerSubscriptionList() throws PayjpException {
		stubNetwork(Customer.class, "{\"subscriptions\":{\"count\":0,\"data\":[]}}");
		Customer customer = Customer.create(defaultCustomerParams);
		stubNetwork(Plan.class, "{}");
		Plan plan = Plan.create(getUniquePlanParams());
		stubNetwork(Plan.class, "{}");
		Plan plan_2 = Plan.create(getUniquePlanParams());

		Map<String, Object> subscriptionParams = new HashMap<String, Object>();
		subscriptionParams.put("plan", plan.getId());
		subscriptionParams.put("customer", customer.getId());

		stubNetwork(Subscription.class, "{}");
		Subscription subscription = Subscription.create(subscriptionParams);

		Map<String, Object> subscriptionParams_2 = new HashMap<String, Object>();
		subscriptionParams_2.put("plan", plan_2.getId());
		subscriptionParams_2.put("customer", customer.getId());

		stubNetwork(Subscription.class, "{}");
		Subscription subscription_2 = Subscription.create(subscriptionParams_2);

		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("limit", 2);

		stubNetwork(CustomerSubscriptionCollection.class, "{\"count\":2,\"data\":[{},{}]}");
		CustomerSubscriptionCollection subscriptions = customer.getSubscriptions().all(listParams);

		assertEquals(2, subscriptions.getData().size());
		assertEquals(subscription_2.getId(), subscriptions.getData().get(0).getId());
		assertEquals(subscription.getId(), subscriptions.getData().get(1).getId());
	}

	@Test
	public void testCustomerSubscriptionRetrieve() throws PayjpException {
		stubNetwork(Customer.class, "{\"id\":\"1\",\"subscriptions\":{\"count\":0,\"data\":[]}}");
		Customer customer = Customer.create(defaultCustomerParams);
		stubNetwork(Subscription.class, "{\"id\":1,\"customer\":\"1\",\"plan\":{\"id\":1}}");
		Subscription subscription_retrieve = customer.getSubscriptions().retrieve("1");

		assertEquals("1", subscription_retrieve.getId());
		assertEquals("1", customer.getId());
		assertEquals("1", subscription_retrieve.getPlan().getId());
	}

	@Test
	public void testCustomerList() throws PayjpException {
		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("limit", 3);
		stubNetwork(CustomerCollection.class, "{\"count\":3,\"data\":[{\"id\":\"1\",\"object\":\"customer\"},{\"object\":\"customer\"},{\"object\":\"customer\"}]}");
		CustomerCollection Customers = Customer.all(listParams);
		assertEquals(Customers.getData().size(), 3);
		assertNotNull(Customers.getData().get(0).getId());
	}

	@Test
	public void testSubscriptionCreate() throws PayjpException {
		Map<String, Object> subscriptionParams = new HashMap<String, Object>();
		subscriptionParams.put("plan", "pln_1");
		subscriptionParams.put("customer", "cus_1");

		stubNetwork(Subscription.class, "{\"id\":1,\"customer\":\"cus_1\",\"plan\":{\"id\":\"pln_1\"},\"next_cycle_plan\":null}");
		Subscription subscription = Subscription.create(subscriptionParams);

		assertEquals(null, subscription.getCanceledAt());
		assertEquals(null, subscription.getPausedAt());
		assertEquals(null, subscription.getResumedAt());
		assertEquals("pln_1", subscription.getPlan().getId());
		assertEquals(null, subscription.getNextCyclePlan());
		assertEquals("cus_1", subscription.getCustomer());
	}

	@Test
	public void testSubscriptionUpdate() throws PayjpException {
		stubNetwork(Subscription.class, "{\"next_cycle_plan\":null,\"plan\":{\"id\":\"pln_1\"},\"customer\":\"cus_1\"}");
		Subscription subscription = Subscription.retrieve("1");

		Map<String, Object> subscriptionParams = new HashMap<String, Object>();
		subscriptionParams.put("next_cycle_plan", "pln_2");
		stubNetwork(Subscription.class, "{\"next_cycle_plan\":{\"id\":\"pln_2\"},\"plan\":{\"id\":\"pln_1\"},\"customer\":\"cus_1\"}");
		Subscription res = subscription.update(subscriptionParams);
		assertEquals("pln_1", res.getPlan().getId());
		assertEquals("pln_2", res.getNextCyclePlan().getId());
		assertEquals("cus_1", res.getCustomer());
	}

	@Test
	public void testSubscriptionRetrieve() throws PayjpException {
		stubNetwork(Subscription.class, "{\"status\":\"active\",\"next_cycle_plan\":null,\"plan\":{\"id\":\"pln_1\"},\"customer\":\"cus_1\"}");
		Subscription subscription = Subscription.retrieve("1");

		assertEquals(null, subscription.getCanceledAt());
		assertEquals(null, subscription.getPausedAt());
		assertEquals(null, subscription.getResumedAt());
		assertEquals("active", subscription.getStatus());
		assertEquals("pln_1", subscription.getPlan().getId());
		assertEquals(null, subscription.getNextCyclePlan());
		assertEquals("cus_1", subscription.getCustomer());
	}

	@Test
	public void testSubscriptionList() throws PayjpException {
		// Create
		Map<String, Object> params = getSubscriptionParams();
		stubNetwork(Subscription.class, "{\"plan\":{\"object\":\"plan\"},\"customer\":\"1\"}");
		Subscription.create(params);

		//list
		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("limit", 1);

		stubNetwork(SubscriptionCollection.class, "{\"count\":1,\"data\":[{\"id\":\"1\"}]}");
		List<Subscription> subs = Subscription.all(listParams).getData();
		assertEquals(1, subs.size());
	}

	@Test
	public void testSubscriptionPauseResumeCancel() throws PayjpException {
		Map<String, Object> params = getSubscriptionParams();
		stubNetwork(Subscription.class, "{\"id\":\"1\",\"status\":\"active\",\"created\":0}");
		Subscription sub = Subscription.create(params);
		assertEquals("active", sub.getStatus());

		stubNetwork(Subscription.class, "{\"status\":\"paused\"}");
		Subscription pause =  sub.pause();
		assertEquals("paused", pause.getStatus());
		stubNetwork(Subscription.class, "{\"status\":\"paused\"}");
		Subscription pause_r = Subscription.retrieve(sub.getId());
		assertEquals("paused", pause_r.getStatus());

		stubNetwork(Subscription.class, "{\"status\":\"active\"}");
		Subscription resume =  pause.resume();
		assertEquals("active", resume.getStatus());
		stubNetwork(Subscription.class, "{\"status\":\"active\"}");
		Subscription resume_r = Subscription.retrieve(sub.getId());
		assertEquals("active", resume_r.getStatus());

		stubNetwork(Subscription.class, "{\"status\":\"paused\"}");
		Subscription pause_2 =  resume.pause();
		assertEquals("paused", pause_2.getStatus());
		assertEquals(null, pause_2.getResumedAt());

		Map<String, Object> resumeParams = new HashMap<String, Object>();
		Long trialEnd = sub.getCreated()+5000;
		resumeParams.put("trial_end", trialEnd);

		stubNetwork(Subscription.class, "{\"status\":\"trial\",\"trial_end\":5000}");
		Subscription resume_2 =  pause_2.resume(resumeParams, null);
		assertEquals("trial", resume_2.getStatus());
		assertEquals(trialEnd, resume_2.getTrialEnd());

		Map<String, Object> cancelParams = new HashMap<String, Object>();
		cancelParams.put("foo", "bar");

		stubNetwork(Subscription.class, 400, "{\"error\":{\"type\":\"test_type\",\"code\":\"test_code\",\"message\":\"Parameters are not allowed: /v1/subscriptions/1/cancel\",\"param\":\"\"}}");
		try {
			resume_2.cancel(cancelParams, null);
		}
		catch (InvalidRequestException e) {
			assertEquals("Parameters are not allowed: /v1/subscriptions/"+sub.getId()+"/cancel", e.getMessage());
			assertEquals("test_type", e.getType());
			assertEquals("test_code", e.getCode());
		}

		stubNetwork(Subscription.class, "{\"status\":\"canceled\"}");
		Subscription cancel =  resume.cancel();
		assertEquals("canceled", cancel.getStatus());
		Subscription cancel_r = Subscription.retrieve(sub.getId());
		assertEquals("canceled", cancel_r.getStatus());
	}

	@Test
	public void testSubscriptionDelete() throws PayjpException {
		Map<String, Object> params = getSubscriptionParams();
		stubNetwork(Subscription.class, "{\"id\":\"1\"}");
		Subscription sub = Subscription.create(params);

		stubNetwork(DeletedSubscription.class, "{\"id\":\"1\",\"deleted\":\"true\"}");
		DeletedSubscription deleted = sub.delete();

		assertTrue(deleted.getDeleted());
		assertEquals(deleted.getId(), sub.getId());

		stubNetwork(Subscription.class, 404, "{\"error\":{\"type\":\"\",\"code\":\"\",\"message\":\"There is no subscription with ID: 1\",\"param\":\"\"}}");
		try {
			Subscription.retrieve(sub.getId());
		}
		catch (InvalidRequestException e) {
			assertEquals("There is no subscription with ID: "+sub.getId(), e.getMessage());
		}
	}

	@Test
	public void testPlanCreate() throws PayjpException {
		Map<String, Object> params = getUniquePlanParams();
		params.put("trial_days", 30);

		stubNetwork(Plan.class, "{\"interval\":\"month\",\"name\":\"J Bindings Plan\",\"trial_days\":30}");
		Plan plan = Plan.create(params);
		assertEquals("month", plan.getInterval());
		assertEquals("J Bindings Plan", plan.getName());
		assertEquals((Integer) 30, plan.getTrialDays());
	}

	@Test
	public void testPlanUpdate() throws PayjpException {
		stubNetwork(Plan.class, "{}");
		Plan createdPlan = Plan.create(getUniquePlanParams());
		Map<String, Object> updateParams = new HashMap<String, Object>();
		updateParams.put("name", "Updated Plan Name");
		stubNetwork(Plan.class, "{\"name\":\"Updated Plan Name\"}");
		Plan updatedplan = createdPlan.update(updateParams);
		assertEquals("Updated Plan Name", updatedplan.getName());
	}

	@Test
	public void testPlanRetrieve() throws PayjpException {
		stubNetwork(Plan.class, "{}");
		Plan createdPlan = Plan.create(getUniquePlanParams());
		stubNetwork(Plan.class, "{}");
		Plan retrievedPlan = Plan.retrieve(createdPlan.getId());
		assertEquals(createdPlan.getId(), retrievedPlan.getId());
	}

	@Test
	public void testPlanList() throws PayjpException {
		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("limit", 1);
		listParams.put("offset", 0);
		stubNetwork(PlanCollection.class, "{\"count\":1,\"data\":[{}]}");
		List<Plan> Plans = Plan.all(listParams).getData();
		assertEquals(Plans.size(), 1);
	}

	@Test
	public void testPlanDelete() throws PayjpException {
		stubNetwork(Plan.class, "{\"id\":\"1\"}");
		Plan createdPlan = Plan.create(getUniquePlanParams());
		stubNetwork(DeletedPlan.class, "{\"id\":\"1\",\"deleted\":true}");
		DeletedPlan deletedPlan = createdPlan.delete();
		assertTrue(deletedPlan.getDeleted());
		assertEquals(deletedPlan.getId(), createdPlan.getId());

		stubNetwork(Plan.class, 404, "{\"error\":{\"type\":\"\",\"code\":\"\",\"message\":\"There is no plan with ID: 1\",\"param\":\"\"}}");
		try {
			Plan.retrieve(createdPlan.getId());
		}
		catch (InvalidRequestException e) {
			assertEquals("There is no plan with ID: "+createdPlan.getId(), e.getMessage());
		}
	}

	@Test
	public void testTokenCreate() throws PayjpException {
		stubNetwork(Token.class, "{\"used\":false}");
		Token token = Token.create(defaultTokenParams);
		assertFalse(token.getUsed());
	}

	@Test
	public void testTokenRetrieve() throws PayjpException {
		stubNetwork(Token.class, "{}");
		Token createdToken = Token.create(defaultTokenParams);
		stubNetwork(Token.class, "{}");
		Token retrievedToken = Token.retrieve(createdToken.getId());
		assertEquals(createdToken.getId(), retrievedToken.getId());
	}

	@Test
	public void testTokenUse() throws PayjpException {
		stubNetwork(Token.class, "{}");
		Token createdToken = Token.create(defaultTokenParams);
		Map<String, Object> chargeWithTokenParams = new HashMap<String, Object>();
		chargeWithTokenParams.put("amount", 199);
		chargeWithTokenParams.put("currency", currency);
		chargeWithTokenParams.put("card", createdToken.getId());
		stubNetwork(Charge.class, "{}");
		Charge.create(chargeWithTokenParams);
		stubNetwork(Token.class, "{\"used\":true}");
		Token retrievedToken = Token.retrieve(createdToken.getId());
		assertTrue(retrievedToken.getUsed());
	}

	@Test
	public void testEventRetrieve() throws PayjpException {
		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("limit", 1);
		stubNetwork(EventCollection.class, "{\"count\":1,\"data\":[{\"id\":\"1\"}]}");
		Event event = Event.all(listParams).getData().get(0);
		stubNetwork(Event.class, "{\"id\":\"1\"}");
		Event retrievedEvent = Event.retrieve(event.getId());
		assertEquals(event.getId(), retrievedEvent.getId());
	}

	@Test
	public void testEventList() throws PayjpException {
		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("limit", 1);
		stubNetwork(EventCollection.class, "{\"count\":1,\"data\":[{}]}");
		List<Event> events = Event.all(listParams).getData();
		assertEquals(events.size(), 1);
	}

	@Test
	public void testTransferList() throws PayjpException {
		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("limit", 1);
		stubNetwork(TransferCollection.class, "{\"count\":1,\"data\":[{}]}");
		List<Transfer> transfers = Transfer.all(listParams).getData();
		assertEquals(transfers.size(), 1);
	}

	@Test
	public void testTransferRetrieve() throws PayjpException {
		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("limit", 1);
		stubNetwork(TransferCollection.class, "{\"count\":1,\"data\":[{}]}");
		List<Transfer> transfers = Transfer.all(listParams).getData();
		Transfer transfer = transfers.get(0);

		stubNetwork(Transfer.class, "{}");
		Transfer retrievedTransfer = Transfer.retrieve(transfer.getId());
		assertEquals(transfer.getDate(), retrievedTransfer.getDate());
		assertEquals(transfer.getId(), retrievedTransfer.getId());
	}

	@Test
	public void testTransferCharges() throws PayjpException {
		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("limit", 1);
		stubNetwork(TransferCollection.class, "{\"count\":1,\"data\":[{}]}");
		List<Transfer> transfers = Transfer.all(listParams).getData();
		Transfer transfer = transfers.get(0);

		Map<String, Object> listParams_2 = new HashMap<String, Object>();
		listParams_2.put("limit", 3);
		listParams_2.put("offset", 10);
		stubNetwork(Transfer.class, "{\"charges\":{\"count\":0,\"data\":[]}}");
		TransferChargeCollection charges = Transfer.retrieve(transfer.getId()).getCharges();
		stubNetwork(TransferChargeCollection.class, "{\"count\":0,\"data\":[]}");
		charges.all(listParams_2);
	}

	@Test
	public void testAccountRetrieve() throws PayjpException {
		Account.retrieve();
	}
}
