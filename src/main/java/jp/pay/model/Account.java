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
	List<String> accountsEnabled;
	Long created;
	Customer customer;
	String email;
	Merchant merchant;

	public String getId() {
		return id;
	}

	public List<String> getAccountsEnabled() {
		return accountsEnabled;
	}

	public Long getCreated() {
		return created;
	}

	public Customer getCustomer() {
		return customer;
	}

	public String getEmail() {
		return email;
	}

	public Merchant getMerchant() {
		return merchant;
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
		String chargeType;
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
		String productType;
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

		public String getChargeType() {
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

		public String getProductType() {
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
