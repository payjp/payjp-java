package jp.pay.model;

import jp.pay.exception.*;
import jp.pay.net.APIResource;
import jp.pay.net.RequestOptions;

import java.util.List;
import java.util.Map;


public class Statement extends APIResource {
	Long created;
	String id;
	List<StatementItem> items;
	Boolean livemode;
	String title;
	Long updated;
	Term term;
	String balanceId;
	String tenantId;
	String type;

	public class StatementItem {
		Integer amount;
		String subject;
		String name;
		String taxRate;

		public Integer getAmount() {
			return amount;
		}

		public String getSubject() {
			return subject;
		}

		public String getName() {
			return name;
		}

		public String getTaxRate() {
			return taxRate;
		}
	}

	public List<StatementItem> getItems() {
		return items;
	}

	public Long getCreated() {
		return created;
	}

	public String getId() {
		return id;
	}

	public Boolean getLivemode() {
		return livemode;
	}

	public void setLivemode(Boolean livemode) {
		this.livemode = livemode;
	}

	public Term getTerm() {
		return term;
	}

	public String getBalanceId() {
		return balanceId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public String getType() {
		return type;
	}

	public static Statement retrieve(String id) throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			CardException, APIException {
		return retrieve(id, (RequestOptions) null);
	}

	public static StatementCollection all(Map<String, Object> params)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return all(params, (RequestOptions) null);
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
				instanceURL(Statement.class, id)), params, StatementUrl.class, options);
	}

	public static Statement retrieve(String id, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return request(RequestMethod.GET, instanceURL(Statement.class, id), null, Statement.class, options);
	}

	public static StatementCollection all(Map<String, Object> params,
			RequestOptions options) throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			CardException, APIException {
		return request(RequestMethod.GET, classURL(Statement.class), params, StatementCollection.class, options);
	}
}
