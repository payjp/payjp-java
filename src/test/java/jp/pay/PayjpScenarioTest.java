/*
 * Copyright (c) 2019 Pay, Inc. (https://pay.jp/)
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


import jp.pay.exception.PayjpException;
import jp.pay.model.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class PayjpScenarioTest extends BasePayjpTest {
	@BeforeClass
	public static void setUp() {
		Payjp.apiKey = "your_secret_key";	// public api key for test
	}

	@Ignore
	@Test
	public void testNextCyclePlanUpdate() throws PayjpException {
		Map<String, Object> ListParams = new HashMap<String, Object>();
		ListParams.put("limit", 1);
	    SubscriptionCollection subscriptions = Subscription.all(ListParams);
		assertEquals(1, (long) subscriptions.getCount());

	    Subscription su = subscriptions.getData().get(0);
		System.out.print(su.getId());
		Map<String, Object> PlanParams = new HashMap<String, Object>();
		PlanParams.put("amount", 1000);
		PlanParams.put("currency", "jpy");
		PlanParams.put("interval", "month");
        Plan plan = Plan.create(PlanParams);
        System.out.print(plan.getId());
		Map<String, Object> subscriptionParams = new HashMap<String, Object>();
		subscriptionParams.put("next_cycle_plan", plan.getId());
		su = su.update(subscriptionParams);
		assertEquals(plan.getId(), su.getNextCyclePlan().getId());

		subscriptionParams.put("next_cycle_plan", null);
		su = su.update(subscriptionParams);
		assertEquals(null, su.getNextCyclePlan());
		plan.delete();
	}
}
