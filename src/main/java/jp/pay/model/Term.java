package jp.pay.model;

import jp.pay.exception.*;
import jp.pay.net.APIResource;
import jp.pay.net.RequestOptions;

import java.util.Map;


public class Term extends APIResource {
	String id;
	Boolean livemode;
	Long startAt;
	Long endAt;
	Integer chargeCount;
	Integer refundCount;
	Integer disputeCount;

	public String getId() {
		return id;
	}

	public Boolean getLivemode() {
		return livemode;
	}

	public Long getStartAt() {
		return startAt;
	}

	public Long getEndAt() {
		return endAt;
	}

	public Integer getChargeCount() {
		return chargeCount;
	}

	public Integer getRefundCount() {
		return refundCount;
	}

	public Integer getDisputeCount() {
		return disputeCount;
	}


	public static Term retrieve(String id) throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			CardException, APIException {
		return retrieve(id, (RequestOptions) null);
	}

	public static TermCollection all(Map<String, Object> params)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return all(params, (RequestOptions) null);
	}

	public static Term retrieve(String id, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return request(RequestMethod.GET, instanceURL(Term.class, id), null, Term.class, options);
	}

	public static TermCollection all(Map<String, Object> params,
			RequestOptions options) throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			CardException, APIException {
		return request(RequestMethod.GET, classURL(Term.class), params, TermCollection.class, options);
	}
}
