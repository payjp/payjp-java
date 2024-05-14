package jp.pay.model;

import jp.pay.exception.*;
import jp.pay.net.APIResource;
import jp.pay.net.RequestOptions;

import java.math.BigInteger;
import java.util.Map;
import java.util.List;


public class Balance extends APIResource {
	Long created;
	String id;
	Boolean livemode;
	BigInteger net;
	String tenantId;
	List<Statement> statements;
	String state;
	Boolean closed;
	String dueDate;
	BankInfo bankInfo;

	public Long getCreated() {
		return created;
	}

	public String getId() {
		return id;
	}

	public Boolean getLivemode() {
		return livemode;
	}

	public BigInteger getNet() {
		return net;
	}

	public String getTenantId() {
		return tenantId;
	}

	public List<Statement> getStatements() {
		return statements;
	}

	public String getState() {
		return state;
	}

	public Boolean getClosed() {
		return closed;
	}

	public String getDueDate() {
		return dueDate;
	}

	public BankInfo getBankInfo() {
		return bankInfo;
	}

	public static Balance retrieve(String id) throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			CardException, APIException {
		return retrieve(id, (RequestOptions) null);
	}

	public static BalanceCollection all(Map<String, Object> params)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return all(params, (RequestOptions) null);
	}

	public static Balance retrieve(String id, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return request(RequestMethod.GET, instanceURL(Balance.class, id), null, Balance.class, options);
	}

	public static BalanceCollection all(Map<String, Object> params,
			RequestOptions options) throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			CardException, APIException {
		return request(RequestMethod.GET, classURL(Balance.class), params, BalanceCollection.class, options);
	}

	public StatementUrl statementUrls(Map<String, Object> params)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return this.statementUrls(params, (RequestOptions) null);
	}

	public StatementUrl statementUrls(Map<String, Object> params, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return request(RequestMethod.POST, String.format("%s/statement_urls",
				instanceURL(Balance.class, id)), params, StatementUrl.class, options);
	}
}
