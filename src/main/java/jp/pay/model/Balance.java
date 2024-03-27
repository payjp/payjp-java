package jp.pay.model;

import jp.pay.exception.*;
import jp.pay.net.APIResource;
import jp.pay.net.RequestOptions;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;


public class Balance extends APIResource {
	Long created;
	String id;
	Boolean livemode;
	BigInteger net;
	StatementCollection statements;
	String type;
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

	public StatementCollection getStatements() {
		return statements;
	}

	public String getType() {
		return type;
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
		return request(RequestMethod.GET, classURL(Statement.class), params, BalanceCollection.class, options);
	}
}
