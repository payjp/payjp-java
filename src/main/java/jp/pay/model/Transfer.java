package jp.pay.model;

import java.util.Map;

import jp.pay.exception.APIConnectionException;
import jp.pay.exception.APIException;
import jp.pay.exception.AuthenticationException;
import jp.pay.exception.CardException;
import jp.pay.exception.InvalidRequestException;
import jp.pay.net.APIResource;
import jp.pay.net.RequestOptions;

public class Transfer extends APIResource {
	Integer amount;
	TransferChargeCollection charges;
	Long created;
	String currency;
	Long date;
	String description;
	String id;
	Boolean livemode;
	String status;
	Summary summary;

	public TransferChargeCollection getCharges() {
		return charges;
	}

	public Long getCreated() {
		return created;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getLivemode() {
		return livemode;
	}

	public void setLivemode(Boolean livemode) {
		this.livemode = livemode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public Summary getSummary() {
		return summary;
	}

	public void setSummary(Summary summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public static Transfer create(Map<String, Object> params)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return create(params, (RequestOptions) null);
	}

	public static Transfer retrieve(String id) throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			CardException, APIException {
		return retrieve(id, (RequestOptions) null);
	}

	public static TransferCollection all(Map<String, Object> params)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return all(params, (RequestOptions) null);
	}

	public static Transfer create(Map<String, Object> params, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return request(RequestMethod.POST, classURL(Transfer.class), params, Transfer.class, options);
	}

	public static Transfer retrieve(String id, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return request(RequestMethod.GET, instanceURL(Transfer.class, id), null, Transfer.class, options);
	}

	public static TransferCollection all(Map<String, Object> params,
			RequestOptions options) throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			CardException, APIException {
		return request(RequestMethod.GET, classURL(Transfer.class), params, TransferCollection.class, options);
	}
}
