package jp.pay.model;

import java.util.Map;

import jp.pay.exception.APIConnectionException;
import jp.pay.exception.APIException;
import jp.pay.exception.AuthenticationException;
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
			APIConnectionException, APIException {
		return create(params, (RequestOptions) null);
	}

	public static Plan retrieve(String id) throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			APIException {
		return retrieve(id, (RequestOptions) null);
	}

	public Plan update(Map<String, Object> params)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException {
		return update(params, (RequestOptions) null);
	}

	public static PlanCollection all(Map<String, Object> params)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException {
		return all(params, (RequestOptions) null);
	}

	public DeletedPlan delete() throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			APIException {
		return delete((RequestOptions) null);
	}

	public static Plan create(Map<String, Object> params, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException {
		return request(RequestMethod.POST, classURL(Plan.class), params, Plan.class, options);
	}

	public static Plan retrieve(String id, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException {
		return request(RequestMethod.GET, instanceURL(Plan.class, id), null, Plan.class, options);
	}

	public Plan update(Map<String, Object> params, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException {
		return request(RequestMethod.POST, instanceURL(Plan.class, this.id), params, Plan.class, options);
	}

	public static PlanCollection all(Map<String, Object> params, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException {
		return request(RequestMethod.GET, classURL(Plan.class), params, PlanCollection.class, options);
	}

	public DeletedPlan delete(RequestOptions options) throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			APIException {
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
