package jp.pay;


import jp.pay.exception.CardException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jp.pay.Payjp;
import jp.pay.exception.InvalidRequestException;
import jp.pay.exception.PayjpException;
import jp.pay.model.Account;
import jp.pay.model.Card;
import jp.pay.model.Charge;
import jp.pay.model.ChargeCollection;
import jp.pay.model.Customer;
import jp.pay.model.CustomerCollection;
import jp.pay.model.CustomerSubscriptionCollection;
import jp.pay.model.DeletedCard;
import jp.pay.model.DeletedCustomer;
import jp.pay.model.DeletedPlan;
import jp.pay.model.DeletedSubscription;
import jp.pay.model.Event;
import jp.pay.model.Plan;
import jp.pay.model.Subscription;
import jp.pay.model.Token;
import jp.pay.model.Transfer;
import jp.pay.net.RequestOptions;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PayjpTest {
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

	static Customer createDefaultCustomerWithPlan(Plan plan)
			throws PayjpException {
		Map<String, Object> customerWithPlanParams = new HashMap<String, Object>();
		customerWithPlanParams.putAll(defaultCustomerParams);
		customerWithPlanParams.put("plan", plan.getId());
		return Customer.create(customerWithPlanParams);
	}

	static Map<String, Object> getSubscriptionParams() throws PayjpException {
		Plan plan = Plan.create(getUniquePlanParams());
		Customer customer = Customer.create(defaultCustomerParams);
		Map<String, Object> subscriptionParams = new HashMap<String, Object>();
		subscriptionParams.put("plan", plan.getId());
		subscriptionParams.put("customer", customer.getId());
		return subscriptionParams;
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

	@Test
	public void testChargeCreate() throws PayjpException {
		Charge createdCharge = Charge.create(defaultChargeParams);
		assertFalse(createdCharge.getRefunded());
		assertTrue(createdCharge.getPaid());

		Charge retrievedCharge = Charge.retrieve(createdCharge.getId());
		assertEquals(createdCharge.getId(), retrievedCharge.getId());
	}

	@Test
	public void testChargeListByCustomer() throws PayjpException {
		Customer customer = Customer.create(defaultCustomerParams);

		Map<String, Object> createParams = new HashMap<String, Object>();
		createParams.put("customer", customer.getId());
		createParams.put("amount", 100);
		createParams.put("currency", currency);

		Charge charge = Charge.create(createParams);

		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("customer", customer.getId());

		ChargeCollection charges = Charge.all(listParams);
		assertEquals(1, charges.getData().size());
		assertEquals(charge.getId(), charges.getData().get(0).getId());
	}

	@Test
	public void testChargeRetrieveNullId() throws PayjpException {
		try {
			Charge.retrieve(null);
			assertTrue(false);
		}
		catch (InvalidRequestException e) {
			// Expected
		}
	}

	@Test
	public void testChargeUpdate() throws PayjpException {
		Charge ch = Charge.create(defaultChargeParams);
		String id = ch.getId();

		Map<String, Object> updateParams = new HashMap<String, Object>();
		updateParams.put("description", "Updated Description");

		Charge updateCharge = ch.update(updateParams);

		assertEquals(id, updateCharge.getId());
		assertEquals("Updated Description", updateCharge.getDescription());
	}

	@Test
	public void testChargeRefund() throws PayjpException {
		Charge ch = Charge.create(defaultChargeParams);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("amount", 10);
		params.put("refund_reason", "foo bar");

		Charge ch_rf = ch.refund(params);
		Integer ar = 10;
		assertEquals(ar, ch_rf.getAmountRefunded());
		assertTrue(ch_rf.getRefunded());
		assertEquals("foo bar", ch_rf.getRefundReason());

		Charge chr = Charge.retrieve(ch.getId());
		assertEquals(ar, chr.getAmountRefunded());
		assertTrue(chr.getRefunded());
		assertEquals("foo bar", chr.getRefundReason());

		Map<String, Object> params_2 = new HashMap<String, Object>();
		Charge ch_rf_2 = chr.refund(params_2);

		assertEquals(defaultChargeParams.get("amount"), ch_rf_2.getAmountRefunded());
		assertTrue(ch_rf_2.getRefunded());
	}

	@Test
	public void testChargeRefundCreateApiKey() throws PayjpException {
		Charge ch = Charge.create(defaultChargeParams);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("amount", 10);
		Charge ch_rf = ch.refund(params);

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

		Charge created = Charge.create(options);
		assertFalse(created.getCaptured());

		Charge captured = created.capture();
		assertTrue(captured.getCaptured());
	}

	@Test
	public void testChargeList() throws PayjpException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("limit", 1);
		params.put("offset", 0);
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
		Charge charge = Charge.create(invalidChargeParams, cardSupportedRequestOptions);
		assertEquals(charge.getPaid(), true);
		assertEquals(charge.getCard().getCvcCheck(), "unavailable");
	}

	@Test
	public void testCustomerCreate() throws PayjpException {
		Customer customer = Customer.create(defaultCustomerParams, cardSupportedRequestOptions);
		assertEquals(customer.getDescription(), "J Bindings Customer");
		List<Card> customerCards = customer.getCards().getData();
		assertEquals(1, customerCards.size());
		assertEquals("4242", customerCards.get(0).getLast4());
	}

	@Test
	public void testCustomerRetrieve() throws PayjpException {
		Customer createdCustomer = Customer.create(defaultCustomerParams);
		Customer retrievedCustomer = Customer.retrieve(createdCustomer.getId());
		assertEquals(createdCustomer.getCreated(),
				retrievedCustomer.getCreated());
		assertEquals(createdCustomer.getId(), retrievedCustomer.getId());
	}

	@Test
	public void testCustomerUpdate() throws PayjpException {
		Customer createdCustomer = Customer.create(defaultCustomerParams);
		Map<String, Object> updateParams = new HashMap<String, Object>();
		updateParams.put("description", "Updated Description");
		Customer updatedCustomer = createdCustomer.update(updateParams);
		assertEquals(updatedCustomer.getDescription(), "Updated Description");
	}

	@Test
	public void testCustomerDelete() throws PayjpException {
		Customer created = Customer.create(defaultCustomerParams);
		DeletedCustomer deleted = created.delete();

		assertTrue(deleted.getDeleted());
		assertEquals(deleted.getId(), created.getId());

		try {
			Customer.retrieve(created.getId());
		}
		catch (InvalidRequestException e) {
			assertEquals("There is no customer with ID: "+created.getId(), e.getMessage());
		}
	}

	@Test
	public void testCustomerCardCreate() throws PayjpException {
		Customer createdCustomer = Customer.create(defaultCustomerParams, cardSupportedRequestOptions);
		String originalDefaultCard = createdCustomer.getDefaultCard();

		Map<String, Object> cardPrams = new HashMap<String, Object>();
		cardPrams.put("number", "4242424242424242");
		cardPrams.put("exp_year", 2022);
		cardPrams.put("exp_month", 12);
		Card addedCard = createdCustomer.createCard(cardPrams);

		Map<String, Object> cardPrams_2 = new HashMap<String, Object>();
		cardPrams_2.put("number", "4242424242424242");
		cardPrams_2.put("exp_year", 2021);
		cardPrams_2.put("exp_month", 12);

		Map<String, Object> tokenPrams = new HashMap<String, Object>();
		tokenPrams.put("card", cardPrams_2);
		Token token = Token.create(tokenPrams);

		createdCustomer.createCard(token.getId());

		Customer updatedCustomer = Customer.retrieve(createdCustomer.getId(), cardSupportedRequestOptions);
		assertEquals((Integer) updatedCustomer.getCards().getData().size(), (Integer) 3);
		assertEquals(updatedCustomer.getDefaultCard(), originalDefaultCard);

		Map<String, Object> updateParams = new HashMap<String, Object>();
		updateParams.put("default_card", addedCard.getId());
		Customer customerAfterDefaultCardUpdate = updatedCustomer.update(updateParams, cardSupportedRequestOptions);
		assertEquals((Integer) customerAfterDefaultCardUpdate.getCards().getData().size(), (Integer) 3);
		assertEquals(customerAfterDefaultCardUpdate.getDefaultCard(), addedCard.getId());

		assertEquals(customerAfterDefaultCardUpdate.getCards().retrieve(originalDefaultCard).getId(), originalDefaultCard);
		assertEquals(customerAfterDefaultCardUpdate.getCards().retrieve(addedCard.getId()).getId(), addedCard.getId());
	}

	@Test
	public void testCustomerCardRetrieve() throws PayjpException {
		Customer customer = Customer.create(defaultCustomerParams, cardSupportedRequestOptions);
		Card originalCard = customer.getCards().getData().get(0);

		Card retrieveCard = customer.getCards().retrieve(originalCard.getId());
		assertEquals(originalCard.getId(), retrieveCard.getId());
	}

	@Test
	public void testCustomerCardUpdate() throws PayjpException {
		Customer customer = Customer.create(defaultCustomerParams, cardSupportedRequestOptions);
		Map<String, Object> updateParams = new HashMap<String, Object>();

		updateParams.put("name", "J Bindings Cardholder, Jr.");
		Card updatedCard = customer.getCards().getData().get(0).update(updateParams);
		assertEquals(updatedCard.getName(), "J Bindings Cardholder, Jr.");
	}

	@Test(expected=InvalidRequestException.class)
	public void testCustomerUpdateToBlank() throws PayjpException {
		Customer createdCustomer = Customer.create(defaultCustomerParams);
		Map<String, Object> updateParams = new HashMap<String, Object>();
		updateParams.put("description", "");
		createdCustomer.update(updateParams);
	}

	@Test
	public void testCustomerUpdateToNull() throws PayjpException {
		Customer createdCustomer = Customer.create(defaultCustomerParams);
		Map<String, Object> updateParams = new HashMap<String, Object>();
		updateParams.put("description", null);
		Customer updatedCustomer = createdCustomer.update(updateParams);
		assertEquals("",updatedCustomer.getDescription());
	}

	@Test
	public void testCustomerCardDelete() throws PayjpException {
		Customer customer = Customer.create(defaultCustomerParams, cardSupportedRequestOptions);
		Map<String, Object> cardPrams = new HashMap<String, Object>();
		cardPrams.put("number", "4242424242424242");
		cardPrams.put("exp_year", 2022);
		cardPrams.put("exp_month", 12);

		customer.createCard(cardPrams);

		Card card = customer.getCards().getData().get(0);
		DeletedCard deletedCard = card.delete();
		Customer retrievedCustomer = Customer.retrieve(customer.getId(), cardSupportedRequestOptions);

		assertTrue(deletedCard.getDeleted());
		assertEquals(deletedCard.getId(), card.getId());

		try {
			retrievedCustomer.getCards().retrieve(card.getId());
		}
		catch (InvalidRequestException e) {
			assertEquals("There is no card with ID: "+card.getId(), e.getMessage());
		}
	}

	@Test
	public void testCustomerCardList() throws PayjpException {
		Customer customer = Customer.create(defaultCustomerParams, cardSupportedRequestOptions);
		Map<String, Object> cardPrams_2 = defaultCardParams;
		cardPrams_2.put("exp_year", 2022);
		customer.createCard(cardPrams_2);

		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("limit", 2);

		List<Card> cards = customer.getCards().all(listParams).getData();
		assertEquals(2, cards.size());
	}

	@Test
	public void testCustomerSubscriptionList() throws PayjpException {
		Customer customer = Customer.create(defaultCustomerParams);
		Plan plan = Plan.create(getUniquePlanParams());
		Plan plan_2 = Plan.create(getUniquePlanParams());

		Map<String, Object> subscriptionParams = new HashMap<String, Object>();
		subscriptionParams.put("plan", plan.getId());
		subscriptionParams.put("customer", customer.getId());

		Subscription subscription = Subscription.create(subscriptionParams);

		Map<String, Object> subscriptionParams_2 = new HashMap<String, Object>();
		subscriptionParams_2.put("plan", plan_2.getId());
		subscriptionParams_2.put("customer", customer.getId());

		Subscription subscription_2 = Subscription.create(subscriptionParams_2);

		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("limit", 2);

		CustomerSubscriptionCollection subscriptions = customer.getSubscriptions().all(listParams);

		assertEquals(2, subscriptions.getData().size());
		assertEquals(subscription_2.getId(), subscriptions.getData().get(0).getId());
		assertEquals(subscription.getId(), subscriptions.getData().get(1).getId());
	}

	@Test
	public void testCustomerSubscriptionRetrieve() throws PayjpException {
		Customer customer = Customer.create(defaultCustomerParams);
		Plan plan = Plan.create(getUniquePlanParams());

		Map<String, Object> subscriptionParams = new HashMap<String, Object>();
		subscriptionParams.put("plan", plan.getId());
		subscriptionParams.put("customer", customer.getId());

		Subscription subscription = Subscription.create(subscriptionParams);

		Subscription subscription_retrieve = customer.getSubscriptions().retrieve(subscription.getId());

		assertEquals(subscription.getId(), subscription_retrieve.getId());
		assertEquals(subscription.getCustomer(), customer.getId());
		assertEquals(subscription.getPlan().getId(), plan.getId());
	}

	@Test
	public void testCustomerList() throws PayjpException {
		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("limit", 3);
		CustomerCollection Customers = Customer.all(listParams);
		assertEquals(Customers.getData().size(), 3);
		assertNotNull(Customers.getData().get(0).getId());
	}

	@Test
	public void testSubscriptionCreate() throws PayjpException {
		Plan plan = Plan.create(getUniquePlanParams());
		Customer customer = Customer.create(defaultCustomerParams);
		Map<String, Object> subscriptionParams = new HashMap<String, Object>();
		subscriptionParams.put("plan", plan.getId());
		subscriptionParams.put("customer", customer.getId());

		Subscription subscription = Subscription.create(subscriptionParams);

		assertEquals(null, subscription.getCanceledAt());
		assertEquals(null, subscription.getPausedAt());
		assertEquals(null, subscription.getResumedAt());
		assertEquals("active", subscription.getStatus());
		assertEquals(plan.getId(), subscription.getPlan().getId());
		assertEquals(customer.getId(), subscription.getCustomer());
	}

	@Test
	public void testSubscriptionRetrieve() throws PayjpException {
		Plan plan = Plan.create(getUniquePlanParams());
		Customer customer = Customer.create(defaultCustomerParams);
		Map<String, Object> subscriptionParams = new HashMap<String, Object>();
		subscriptionParams.put("plan", plan.getId());
		subscriptionParams.put("customer", customer.getId());

		Subscription createdSubscription = Subscription.create(subscriptionParams);

		Subscription subscription = Subscription.retrieve(createdSubscription.getId());
		assertEquals(null, subscription.getCanceledAt());
		assertEquals(null, subscription.getPausedAt());
		assertEquals(null, subscription.getResumedAt());
		assertEquals("active", subscription.getStatus());
		assertEquals(plan.getId(), subscription.getPlan().getId());
		assertEquals(customer.getId(), subscription.getCustomer());
	}

	@Test
	public void testSubscriptionList() throws PayjpException {
		// Create
		Subscription.create(getSubscriptionParams());

		//list
		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("limit", 1);

		List<Subscription> subs = Subscription.all(listParams).getData();
		assertEquals(1, subs.size());
	}

	@Test
	public void testSubscriptionPauseResumeCancel() throws PayjpException {
		Subscription sub = Subscription.create(getSubscriptionParams());
		assertEquals("active", sub.getStatus());

		Subscription pause =  sub.pause();
		assertEquals("paused", pause.getStatus());
		Subscription pause_r = Subscription.retrieve(sub.getId());
		assertEquals("paused", pause_r.getStatus());

		Subscription resume =  pause.resume();
		assertEquals("active", resume.getStatus());
		Subscription resume_r = Subscription.retrieve(sub.getId());
		assertEquals("active", resume_r.getStatus());

		Subscription pause_2 =  resume.pause();
		assertEquals("paused", pause_2.getStatus());
		assertEquals(null, pause_2.getResumedAt());

		Map<String, Object> resumeParams = new HashMap<String, Object>();
		Long trialEnd = sub.getCreated()+5000;
		resumeParams.put("trial_end", trialEnd);

		Subscription resume_2 =  pause_2.resume(resumeParams, null);
		assertEquals("trial", resume_2.getStatus());
		assertEquals(trialEnd, resume_2.getTrialEnd());

		Map<String, Object> cancelParams = new HashMap<String, Object>();
		cancelParams.put("foo", "bar");

		try {
			resume_2.cancel(cancelParams, null);
		}
		catch (InvalidRequestException e) {
			assertEquals("Parameters are not allowed: /v1/subscriptions/"+sub.getId()+"/cancel", e.getMessage());
		}

		Subscription cancel =  resume.cancel();
		assertEquals("canceled", cancel.getStatus());
		Subscription cancel_r = Subscription.retrieve(sub.getId());
		assertEquals("canceled", cancel_r.getStatus());
	}

	@Test
	public void testSubscriptionDelete() throws PayjpException {
		Subscription sub = Subscription.create(getSubscriptionParams());

		DeletedSubscription deleted = sub.delete();

		assertTrue(deleted.getDeleted());
		assertEquals(deleted.getId(), sub.getId());

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

		Plan plan = Plan.create(params);
		assertEquals(plan.getInterval(), "month");
		assertEquals(plan.getName(), "J Bindings Plan");
		assertEquals(plan.getTrialDays(), (Integer) 30);
	}

	@Test
	public void testPlanUpdate() throws PayjpException {
		Plan createdPlan = Plan.create(getUniquePlanParams());
		Map<String, Object> updateParams = new HashMap<String, Object>();
		updateParams.put("name", "Updated Plan Name");
		Plan updatedplan = createdPlan.update(updateParams);
		assertEquals(updatedplan.getName(), "Updated Plan Name");
	}

	@Test
	public void testPlanRetrieve() throws PayjpException {
		Plan createdPlan = Plan.create(getUniquePlanParams());
		Plan retrievedPlan = Plan.retrieve(createdPlan.getId());
		assertEquals(createdPlan.getId(), retrievedPlan.getId());
	}

	@Test
	public void testPlanList() throws PayjpException {
		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("limit", 1);
		listParams.put("offset", 0);
		List<Plan> Plans = Plan.all(listParams).getData();
		assertEquals(Plans.size(), 1);
	}

	@Test
	public void testPlanDelete() throws PayjpException {
		Plan createdPlan = Plan.create(getUniquePlanParams());
		DeletedPlan deletedPlan = createdPlan.delete();
		assertTrue(deletedPlan.getDeleted());
		assertEquals(deletedPlan.getId(), createdPlan.getId());

		try {
			Plan.retrieve(createdPlan.getId());
		}
		catch (InvalidRequestException e) {
			assertEquals("There is no plan with ID: "+createdPlan.getId(), e.getMessage());
		}
	}

	@Test
	public void testTokenCreate() throws PayjpException {
		Token token = Token.create(defaultTokenParams);
		assertFalse(token.getUsed());
	}

	@Test
	public void testTokenRetrieve() throws PayjpException {
		Token createdToken = Token.create(defaultTokenParams);
		Token retrievedToken = Token.retrieve(createdToken.getId());
		assertEquals(createdToken.getId(), retrievedToken.getId());
	}

	@Test
	public void testTokenUse() throws PayjpException {
		Token createdToken = Token.create(defaultTokenParams);
		Map<String, Object> chargeWithTokenParams = new HashMap<String, Object>();
		chargeWithTokenParams.put("amount", 199);
		chargeWithTokenParams.put("currency", currency);
		chargeWithTokenParams.put("card", createdToken.getId());
		Charge.create(chargeWithTokenParams);
		Token retrievedToken = Token.retrieve(createdToken.getId());
		assertTrue(retrievedToken.getUsed());
	}

	@Test
	public void testEventRetrieve() throws PayjpException {
		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("limit", 1);
		Event event = Event.all(listParams).getData().get(0);
		Event retrievedEvent = Event.retrieve(event.getId());
		assertEquals(event.getId(), retrievedEvent.getId());
	}

	@Test
	public void testEventList() throws PayjpException {
		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("limit", 1);
		List<Event> events = Event.all(listParams).getData();
		assertEquals(events.size(), 1);
	}

	@Test
	public void testTransferList() throws PayjpException {
		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("limit", 1);
		List<Transfer> transfers = Transfer.all(listParams).getData();
		assertEquals(transfers.size(), 1);
	}

	@Test
	public void testTransferRetrieve() throws PayjpException {
		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("limit", 1);
		List<Transfer> transfers = Transfer.all(listParams).getData();
		Transfer transfer = transfers.get(0);

		Transfer retrievedTransfer = Transfer.retrieve(transfer.getId());
		assertEquals(transfer.getDate(), retrievedTransfer.getDate());
		assertEquals(transfer.getId(), retrievedTransfer.getId());
	}

	@Test
	public void testTransferCharges() throws PayjpException {
		Map<String, Object> listParams = new HashMap<String, Object>();
		listParams.put("limit", 1);
		List<Transfer> transfers = Transfer.all(listParams).getData();
		Transfer transfer = transfers.get(0);

		Map<String, Object> listParams_2 = new HashMap<String, Object>();
		listParams_2.put("limit", 3);
		listParams_2.put("offset", 10);
		Transfer.retrieve(transfer.getId()).getCharges().all(listParams_2);
	}

	@Test
	public void testAccountRetrieve() throws PayjpException {
		Account.retrieve();
	}
}
