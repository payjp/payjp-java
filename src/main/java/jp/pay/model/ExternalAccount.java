/*
 * Copyright (c) 2010-2011 Stripe (http://stripe.com)
 * Copyright (c) 2015 Base, Inc. (http://binc.jp/)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
package jp.pay.model;

import java.util.Map;

import jp.pay.exception.APIConnectionException;
import jp.pay.exception.APIException;
import jp.pay.exception.AuthenticationException;
import jp.pay.exception.CardException;
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
            APIConnectionException, CardException, APIException {
        return update(params, null);
    }

    public ExternalAccount update(Map<String, Object> params, RequestOptions options)
            throws AuthenticationException, InvalidRequestException,
            APIConnectionException, CardException, APIException {
        return request(RequestMethod.POST, this.getInstanceURL(), params, ExternalAccount.class, options);
    }

    public DeletedExternalAccount delete() throws AuthenticationException,
            InvalidRequestException, APIConnectionException,
            CardException, APIException {
        return delete(null);
    }

    public DeletedExternalAccount delete(RequestOptions options) throws
            AuthenticationException, InvalidRequestException,
            APIConnectionException, CardException, APIException {
        return request(RequestMethod.DELETE, this.getInstanceURL(), null, DeletedExternalAccount.class, options);
    }
}
