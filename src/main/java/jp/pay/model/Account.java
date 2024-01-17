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

import java.util.List;
import java.util.Map;

import jp.pay.exception.APIConnectionException;
import jp.pay.exception.APIException;
import jp.pay.exception.AuthenticationException;
import jp.pay.exception.CardException;
import jp.pay.exception.InvalidRequestException;
import jp.pay.net.APIResource;
import jp.pay.net.RequestOptions;

public class Account extends APIResource {
	String id;
	Long created;
	String email;
	Merchant merchant;
	String teamId;

	public String getId() {
		return id;
	}

	public Long getCreated() {
		return created;
	}

	public String getEmail() {
		return email;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public String getTeamId() {
		return teamId;
	}

	public static Account retrieve()
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {
		return request(
			RequestMethod.GET,
			classURL(Account.class),
			null,
			Account.class,
			null);
	}

	public static class Merchant extends PayjpObject {
		Boolean bankEnabled;
		List<String> brandsAccepted;
		String businessType;
		List<String> chargeType;
		String contactPhone;
		String country;
		Long created;
		List<String> currenciesSupported;
		String defaultCurrency;
		Boolean detailsSubmitted;
		String id;
		Long livemodeActivatedAt;
		Boolean livemodeEnabled;
		String productDetail;
		String productName;
		List<String> productType;
		Boolean sitePublished;
		String url;

		public Boolean getBankEnabled() {
			return bankEnabled;
		}

		public List<String> getBrandsAccepted() {
			return brandsAccepted;
		}

		public String getBusinessType() {
			return businessType;
		}

		public List<String> getChargeType() {
			return chargeType;
		}

		public String getContactPhone() {
			return contactPhone;
		}

		public String getCountry() {
			return country;
		}

		public Long getCreated() {
			return created;
		}

		public List<String> getCurrenciesSupported() {
			return currenciesSupported;
		}

		public String getDefaultCurrency() {
			return defaultCurrency;
		}

		public Boolean getDetailsSubmitted() {
			return detailsSubmitted;
		}

		public String getId() {
			return id;
		}

		public Long getLivemodeActivatedAt() {
			return livemodeActivatedAt;
		}

		public Boolean getLivemodeEnabled() {
			return livemodeEnabled;
		}

		public String getProductDetail() {
			return productDetail;
		}

		public String getProductName() {
			return productName;
		}

		public List<String> getProductType() {
			return productType;
		}

		public Boolean getSitePublished() {
			return sitePublished;
		}

		public String getUrl() {
			return url;
		}
	}
}
