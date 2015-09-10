package jp.pay.model;

import java.util.Map;

import jp.pay.Payjp;
import jp.pay.exception.APIConnectionException;
import jp.pay.exception.APIException;
import jp.pay.exception.AuthenticationException;
import jp.pay.exception.CardException;
import jp.pay.exception.InvalidRequestException;
import jp.pay.net.RequestOptions;

public class TransferChargeCollection extends PayjpCollectionAPIResource<Charge> {
	public TransferChargeCollection all(Map<String, Object> params)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return all(params, (RequestOptions) null);
	}

	public TransferChargeCollection all(Map<String, Object> params,
			RequestOptions options) throws AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		String url = String.format("%s%s", Payjp.getApiBase(), this.getURL());
		return request(RequestMethod.GET, url, params,
				TransferChargeCollection.class, options);
	}
}
