package jp.pay.model;

import java.util.Map;

import jp.pay.exception.APIConnectionException;
import jp.pay.exception.APIException;
import jp.pay.exception.AuthenticationException;
import jp.pay.exception.InvalidRequestException;
import jp.pay.net.APIResource;
import jp.pay.net.RequestOptions;

public class Charge extends APIResource {
	Integer amount;
	Integer amountRefunded;
	Boolean captured;
	Card card;
	Long created;
	String currency;
	String customer;
	String description;
	String expiredAt;
	String failureCode;
	String failureMessage;
	String id;
	Boolean livemode;
	Boolean paid;
	String refundReason;
	Boolean refunded;
	String subscription;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Long getCreated() {
		return created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Boolean getLivemode() {
		return livemode;
	}

	public void setLivemode(Boolean livemode) {
		this.livemode = livemode;
	}

	public Boolean getPaid() {
		return paid;
	}

	public void setPaid(Boolean paid) {
		this.paid = paid;
	}

	public Boolean getRefunded() {
		return refunded;
	}

	public void setRefunded(Boolean refunded) {
		this.refunded = refunded;
	}

	public Boolean getCaptured() {
		return captured;
	}

	public void setCaptured(Boolean captured) {
		this.captured = captured;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFailureMessage() {
		return failureMessage;
	}

	public void setFailureMessage(String failureMessage) {
		this.failureMessage = failureMessage;
	}

	public String getFailureCode() {
		return failureCode;
	}

	public void setFailureCode(String failureCode) {
		this.failureCode = failureCode;
	}

	public Integer getAmountRefunded() {
		return amountRefunded;
	}

	public void setAmountRefunded(Integer amountRefunded) {
		this.amountRefunded = amountRefunded;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	public String getExpiredAt() {
		return expiredAt;
	}

	public void setExpiredAt(String expiredAt) {
		this.expiredAt = expiredAt;
	}

	public String getRefundReason() {
		return refundReason;
	}

	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}

	public String getSubscription() {
		return subscription;
	}

	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}
	
	public static Charge create(Map<String, Object> params)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException {
		return create(params, (RequestOptions) null);
	}

	public static Charge retrieve(String id) throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			APIException {
		return retrieve(id, (RequestOptions) null);
	}

	public Charge update(Map<String, Object> params)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException {
		return update(params, (RequestOptions) null);
	}

	public static ChargeCollection all(Map<String, Object> params)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException {
		return all(params, (RequestOptions) null);
	}

	public Charge refund() throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			APIException {
		return this.refund(null, (RequestOptions) null);
	}

	public Charge capture() throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			APIException {
		return this.capture(null, (RequestOptions) null);
	}

	public Charge refund(Map<String, Object> params)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException {
		return this.refund(params, (RequestOptions) null);
	}

	public Charge capture(Map<String, Object> params)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException {
		return this.capture(params, (RequestOptions) null);
	}

	public static Charge create(Map<String, Object> params, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException {
		return request(RequestMethod.POST, classURL(Charge.class), params, Charge.class, options);
	}

	public static Charge retrieve(String id, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException {
		return request(RequestMethod.GET, instanceURL(Charge.class, id), null, Charge.class, options);
	}

	public Charge update(Map<String, Object> params, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException {
		return request(RequestMethod.POST, instanceURL(Charge.class, id), params, Charge.class, options);
	}

	public static ChargeCollection all(Map<String, Object> params, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException {
		return request(RequestMethod.GET, classURL(Charge.class), params, ChargeCollection.class, options);
	}

	public Charge refund(RequestOptions options) throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			APIException {
		return this.refund(null, options); // full refund
	}

	public Charge refund(Map<String, Object> params, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException {
		return request(RequestMethod.POST, String.format("%s/refund",
				instanceURL(Charge.class, this.getId())), params, Charge.class, options);
	}

	public Charge capture(RequestOptions options) throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			APIException {
		return this.capture(null, options);
	}

	public Charge capture(Map<String, Object> params, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException {
		return request(RequestMethod.POST, String.format("%s/capture",
						instanceURL(Charge.class, this.getId())), params, Charge.class, options);
	}
}
