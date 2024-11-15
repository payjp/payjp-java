package jp.pay.model;

import jp.pay.exception.*;
import jp.pay.net.APIResource;
import jp.pay.net.APIResource.RequestMethod;
import jp.pay.net.RequestOptions;

import java.util.Map;

public class ThreeDSecureRequest extends APIResource {
	String id;
	String resourceId;
	Boolean livemode;
	Long created;
	String state;
	Long startedAt;
	Long resultReceivedAt;
	Long finishedAt;
	Long expiredAt;
	String tenantId;
	String threeDSecureStatus;

	public String getId() {
		return id;
	}

	public String getResourceId() {
		return resourceId;
	}

	public Boolean getLivemode() {
		return livemode;
	}

	public Long getCreated() {
		return created;
	}

	public String getState() {
		return state;
	}

	public Long getStartedAt() {
		return startedAt;
	}

	public Long getResultReceivedAt() {
		return resultReceivedAt;
	}

	public Long getFinishedAt() {
		return finishedAt;
	}

	public Long getExpiredAt() {
		return expiredAt;
	}

	public String getTenantId() {
		return tenantId;
	}

	public String getThreeDSecureStatus() {
		return threeDSecureStatus;
	}

	public static ThreeDSecureRequest retrieve(String id) throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			CardException, APIException {
		return retrieve(id, (RequestOptions) null);
	}

	public static ThreeDSecureRequestCollection all(Map<String, Object> params)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return all(params, (RequestOptions) null);
	}

	public static ThreeDSecureRequest create(Map<String, Object> params)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return create(params, (RequestOptions) null);
	}

	public static ThreeDSecureRequest retrieve(String id, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return request(RequestMethod.GET, instanceURL(
				ThreeDSecureRequest.class, id), null, ThreeDSecureRequest.class, options);
	}

	public static ThreeDSecureRequestCollection all(Map<String, Object> params,
			RequestOptions options) throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			CardException, APIException {
		return request(RequestMethod.GET, classURL(
				ThreeDSecureRequest.class), params, ThreeDSecureRequestCollection.class, options);
	}

	public static ThreeDSecureRequest create(Map<String, Object> params, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return request(RequestMethod.POST, classURL(ThreeDSecureRequest.class), params,
				ThreeDSecureRequest.class, options);
	}
}
