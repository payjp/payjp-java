package jp.pay.model;

import java.util.Map;

import jp.pay.Payjp;
import jp.pay.exception.APIConnectionException;
import jp.pay.exception.APIException;
import jp.pay.exception.AuthenticationException;
import jp.pay.exception.CardException;
import jp.pay.exception.InvalidRequestException;
import jp.pay.net.RequestOptions;

public class CustomerSubscriptionCollection extends PayjpCollectionAPIResource<Subscription> {
	  public CustomerSubscriptionCollection all(Map<String, Object> params)
	      throws AuthenticationException, InvalidRequestException,
	      APIConnectionException, CardException, APIException {
	    return all(params, (RequestOptions) null);
	  }

	  public CustomerSubscriptionCollection all(Map<String, Object> params,
	      RequestOptions options) throws AuthenticationException,
	      InvalidRequestException, APIConnectionException,
	      CardException, APIException {
	    String url = String.format("%s%s", Payjp.getApiBase(), this.getURL());
		  return request(RequestMethod.GET, url, params, CustomerSubscriptionCollection.class, options);
	  }

	  public Subscription retrieve(String id)
	      throws AuthenticationException, InvalidRequestException,
	      APIConnectionException, CardException, APIException {
	    return retrieve(id, (RequestOptions) null);
	  }

	  public Subscription retrieve(String id, RequestOptions options) throws AuthenticationException,
	      InvalidRequestException, APIConnectionException,
	      CardException, APIException {
	    String url = String.format("%s%s/%s", Payjp.getApiBase(), this.getURL(), id);
		  return request(RequestMethod.GET, url, null, Subscription.class, options);
	  }
}
