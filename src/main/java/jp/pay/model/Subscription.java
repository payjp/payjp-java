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


public class Subscription extends APIResource{
	Long canceledAt;
	Long created;
	Long currentPeriodEnd;
	Long currentPeriodStart;
	String customer;
	String id;
	Boolean livemode;
	Long pausedAt;
	Plan plan;
	Long resumedAt;
	Long start;
	String status;
	Long trialEnd;
	Long trialStart;

	public static Subscription create(Map<String, Object> params)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return create(params, (RequestOptions) null);
	}
	
	public static Subscription create(Map<String, Object> params, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return request(RequestMethod.POST, classURL(Subscription.class), params, Subscription.class, options);
	}
	
	public static Subscription retrieve(String id) throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			CardException, APIException {
		return retrieve(id, (RequestOptions) null);
	}
	
	public static Subscription retrieve(String id, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return request(RequestMethod.GET, instanceURL(Subscription.class, id), null, Subscription.class, options);
	}
	
	public Subscription update(Map<String, Object> params)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return update(params, (RequestOptions) null);
	}

	public Subscription update(Map<String, Object> params, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return request(RequestMethod.POST, instanceURL(Subscription.class, id), params, Subscription.class, options);
	}
	
	public static SubscriptionCollection all(Map<String, Object> params)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return all(params, (RequestOptions) null);
	}
	
	public static SubscriptionCollection all(Map<String, Object> params, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return request(RequestMethod.GET, classURL(Subscription.class), params, SubscriptionCollection.class, options);
	}

	public Subscription pause() throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			CardException, APIException {
		return this.pause(null, (RequestOptions) null);
	}
	
	public Subscription pause(RequestOptions options) throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			CardException, APIException {
		return this.pause(null, options); 
	}
	
	public Subscription pause(Map<String, Object> params, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return request(RequestMethod.POST, String.format("%s/pause",
			instanceURL(Subscription.class, this.getId())), params, Subscription.class, options);
	}

	public Subscription resume() throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			CardException, APIException {
		return this.resume(null, (RequestOptions) null);
	}
	
	public Subscription resume(RequestOptions options) throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			CardException, APIException {
		return this.resume(null, options); 
	}

	public Subscription resume(Map<String, Object> params, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return request(RequestMethod.POST, String.format("%s/resume",
				instanceURL(Subscription.class, this.getId())), params, Subscription.class, options);
	}
	
	public Subscription cancel() throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			CardException, APIException {
		return this.cancel(null, (RequestOptions) null);
	}
	
	public Subscription cancel(RequestOptions options) throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			CardException, APIException {
		return this.cancel(null, options); 
	}

	public Subscription cancel(Map<String, Object> params, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return request(RequestMethod.POST, String.format("%s/cancel",
				instanceURL(Subscription.class, this.getId())), params, Subscription.class, options);
	}
	
	public DeletedSubscription delete() throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			CardException, APIException {
		return this.delete(null, (RequestOptions) null);
	}
	
	public DeletedSubscription delete(Map<String, Object> params) throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			CardException, APIException {
		return delete(params, (RequestOptions) null);
	}

	public DeletedSubscription delete(Map<String, Object> params, RequestOptions options) throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			CardException, APIException {
		return request(RequestMethod.DELETE, instanceURL(Subscription.class, id), params, DeletedSubscription.class, options);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getCurrentPeriodEnd() {
		return currentPeriodEnd;
	}
	public void setCurrentPeriodEnd(Long currentPeriodEnd) {
		this.currentPeriodEnd = currentPeriodEnd;
	}
	public Long getCurrentPeriodStart() {
		return currentPeriodStart;
	}
	public void setCurrentPeriodStart(Long currentPeriodStart) {
		this.currentPeriodStart = currentPeriodStart;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public Long getStart() {
		return start;
	}
	public void setStart(Long start) {
		this.start = start;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getTrialStart() {
		return trialStart;
	}
	public void setTrialStart(Long trialStart) {
		this.trialStart = trialStart;
	}
	public Long getTrialEnd() {
		return trialEnd;
	}
	public void setTrialEnd(Long trialEnd) {
		this.trialEnd = trialEnd;
	}
	public Plan getPlan() {
		return plan;
	}
	public void setPlan(Plan plan) {
		this.plan = plan;
	}
	public Long getCanceledAt() {
		return canceledAt;
	}
	public void setCanceledAt(Long canceledAt) {
		this.canceledAt = canceledAt;
	}
	
	public Long getCreated() {
		return created;
	}

	public Boolean getLivemode() {
		return livemode;
	}

	public Long getPausedAt() {
		return pausedAt;
	}

	public Long getResumedAt() {
		return resumedAt;
	}

	public void setLivemode(Boolean livemode) {
		this.livemode = livemode;
	}

	public void setPausedAt(Long pausedAt) {
		this.pausedAt = pausedAt;
	}

	public void setResumedAt(Long resumedAt) {
		this.resumedAt = resumedAt;
	}
}
