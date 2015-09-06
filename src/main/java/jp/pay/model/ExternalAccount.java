package jp.pay.model;

import java.util.Map;

import jp.pay.exception.APIConnectionException;
import jp.pay.exception.APIException;
import jp.pay.exception.AuthenticationException;
import jp.pay.exception.InvalidRequestException;
import jp.pay.net.APIResource;
import jp.pay.net.RequestOptions;

public class ExternalAccount extends APIResource {
    String id;
    String object;
    String customer;
    String account;

    public String getId() {
        return id;
    }

    public String getObject() {
        return object;
    }

    public String getCustomer() {
        return customer;
    }

    // For testing
    public void setCustomer(String customer) {
    	this.customer = customer;
    }

    public String getAccount() {
        return account;
    }

    // For testing
    public void setAccount(String account) {
    	this.account = account;
    }

    public String getInstanceURL() {
        if (this.getCustomer() != null) {
            return String.format("%s/%s/cards/%s", classURL(Customer.class), this.getCustomer(), this.getId());
        } else {
            return null;
        }
    }

    public ExternalAccount update(Map<String, Object> params) throws
            AuthenticationException, InvalidRequestException,
            APIConnectionException, APIException {
        return update(params, null);
    }

    public ExternalAccount update(Map<String, Object> params, RequestOptions options)
            throws AuthenticationException, InvalidRequestException,
            APIConnectionException, APIException {
        return request(RequestMethod.POST, this.getInstanceURL(), params, ExternalAccount.class, options);
    }

    public DeletedExternalAccount delete() throws AuthenticationException,
            InvalidRequestException, APIConnectionException,
            APIException {
        return delete(null);
    }

    public DeletedExternalAccount delete(RequestOptions options) throws
            AuthenticationException, InvalidRequestException,
            APIConnectionException, APIException {
        return request(RequestMethod.DELETE, this.getInstanceURL(), null, DeletedExternalAccount.class, options);
    }
}
