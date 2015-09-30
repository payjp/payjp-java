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

import java.util.Map;

import jp.pay.exception.APIConnectionException;
import jp.pay.exception.APIException;
import jp.pay.exception.AuthenticationException;
import jp.pay.exception.CardException;
import jp.pay.exception.InvalidRequestException;
import jp.pay.net.APIResource;
import jp.pay.net.RequestOptions;

public class Plan extends APIResource {
	Integer amount;
	Long created;
	String currency;
	String id;
	String interval;
	Boolean livemode;
	String name;
	Integer trialDays;

	public static Plan create(Map<String, Object> params)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return create(params, (RequestOptions) null);
	}

	public static Plan retrieve(String id) throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			CardException, APIException {
		return retrieve(id, (RequestOptions) null);
	}

	public Plan update(Map<String, Object> params)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return update(params, (RequestOptions) null);
	}

	public static PlanCollection all(Map<String, Object> params)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return all(params, (RequestOptions) null);
	}

	public DeletedPlan delete() throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			CardException, APIException {
		return delete((RequestOptions) null);
	}

	public static Plan create(Map<String, Object> params, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return request(RequestMethod.POST, classURL(Plan.class), params, Plan.class, options);
	}

	public static Plan retrieve(String id, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return request(RequestMethod.GET, instanceURL(Plan.class, id), null, Plan.class, options);
	}

	public Plan update(Map<String, Object> params, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return request(RequestMethod.POST, instanceURL(Plan.class, this.id), params, Plan.class, options);
	}

	public static PlanCollection all(Map<String, Object> params, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return request(RequestMethod.GET, classURL(Plan.class), params, PlanCollection.class, options);
	}

	public DeletedPlan delete(RequestOptions options) throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			CardException, APIException {
		return request(RequestMethod.DELETE, instanceURL(Plan.class, this.id), null, DeletedPlan.class, options);
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getLivemode() {
		return livemode;
	}

	public void setLivemode(Boolean livemode) {
		this.livemode = livemode;
	}

	public Long getCreated() {
		return created;
	}

	public Integer getTrialDays() {
		return trialDays;
	}

	public void setTrialDays(Integer trialDays) {
		this.trialDays = trialDays;
	}
}
