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
package jp.pay.net;

import jp.pay.Payjp;

public class RequestOptions {
	public static RequestOptions getDefault() {
		return new RequestOptions(Payjp.apiKey, Payjp.apiVersion, null, null);
	}

	private final String apiKey;
	private final String payjpVersion;
	private final String idempotencyKey;
	private final String payjpAccount;

	private RequestOptions(String apiKey, String payjpVersion, String idempotencyKey, String payjpAccount) {
		this.apiKey = apiKey;
		this.payjpVersion = payjpVersion;
		this.idempotencyKey = idempotencyKey;
		this.payjpAccount = payjpAccount;
	}

	public String getApiKey() {
		return apiKey;
	}

	public String getPayjpVersion() {
		return payjpVersion;
	}

	public String getIdempotencyKey() {
		return idempotencyKey;
	}

	public String getPayjpAccount() {
		return payjpAccount;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RequestOptions that = (RequestOptions) o;

		if (apiKey != null ? !apiKey.equals(that.apiKey) : that.apiKey != null) {
			return false;
		}
		if (idempotencyKey != null ? !idempotencyKey.equals(that.idempotencyKey) : that.idempotencyKey != null) {
			return false;
		}
		if (payjpVersion != null ? !payjpVersion.equals(that.payjpVersion) : that.payjpVersion != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = apiKey != null ? apiKey.hashCode() : 0;
		result = 31 * result + (payjpVersion != null ? payjpVersion.hashCode() : 0);
		result = 31 * result + (idempotencyKey != null ? idempotencyKey.hashCode() : 0);
		return result;
	}

	public static RequestOptionsBuilder builder() {
		return new RequestOptionsBuilder();
	}

	public RequestOptionsBuilder toBuilder() {
		return new RequestOptionsBuilder().setApiKey(this.apiKey).setPayjpVersion(this.payjpVersion).setPayjpAccount(this.payjpAccount);
	}

	public static final class RequestOptionsBuilder {
		private String apiKey;
		private String payjpVersion;
		private String idempotencyKey;
		private String payjpAccount;

		public RequestOptionsBuilder() {
			this.apiKey = Payjp.apiKey;
			this.payjpVersion = Payjp.apiVersion;
		}

		public String getApiKey() {
			return apiKey;
		}

		public RequestOptionsBuilder setApiKey(String apiKey) {
			this.apiKey = normalizeApiKey(apiKey);
			return this;
		}

		public RequestOptionsBuilder clearApiKey() {
			this.apiKey = null;
			return this;
		}

		public RequestOptionsBuilder setPayjpVersion(String payjpVersion) {
			this.payjpVersion = normalizePayjpVersion(payjpVersion);
			return this;
		}

		public RequestOptionsBuilder clearPayjpVersion() {
			this.payjpVersion = null;
			return this;
		}

		public RequestOptionsBuilder setIdempotencyKey(String idempotencyKey) {
			this.idempotencyKey = idempotencyKey;
			return this;
		}

		public RequestOptionsBuilder clearIdempotencyKey() {
			this.idempotencyKey = null;
			return this;
		}

		public String getIdempotencyKey() {
			return this.idempotencyKey;
		}

		public String getPayjpAccount() {
			return this.payjpAccount;
		}

		public RequestOptionsBuilder setPayjpAccount(String payjpAccount) {
			this.payjpAccount = payjpAccount;
			return this;
		}

		public RequestOptionsBuilder clearPayjpAccount() {
			return setPayjpAccount(null);
		}

		public RequestOptions build() {
			return new RequestOptions(
				normalizeApiKey(this.apiKey),
				normalizePayjpVersion(this.payjpVersion),
				normalizeIdempotencyKey(this.idempotencyKey),
				normalizePayjpAccount(this.payjpAccount));
		}
	}

	private static String normalizeApiKey(String apiKey) {
		// null apiKeys are considered "valid"
		if (apiKey == null) {
			return null;
		}
		String normalized = apiKey.trim();
		if (normalized.isEmpty()) {
			throw new InvalidRequestOptionsException("Empty API key specified!");
		}
		return normalized;
	}

	private static String normalizePayjpVersion(String payjpVersion) {
		// null payjpVersions are considered "valid" and use Payjp.apiVersion
		if (payjpVersion == null) {
			return null;
		}
		String normalized = payjpVersion.trim();
		if (normalized.isEmpty()) {
			throw new InvalidRequestOptionsException("Empty Payjp version specified!");
		}
		return normalized;
	}

	private static String normalizeIdempotencyKey(String idempotencyKey) {
		if (idempotencyKey == null) {
			return null;
		}
		String normalized = idempotencyKey.trim();
		if (normalized.isEmpty()) {
			throw new InvalidRequestOptionsException("Empty Idempotency Key Specified!");
		}
		if (normalized.length() > 255) {
			throw new InvalidRequestOptionsException(String.format("Idempotency Key length was %d, which is larger than the 255 character maximum!", normalized.length()));
		}
		return normalized;
	}

	private static String normalizePayjpAccount(String payjpAccount) {
		if (payjpAccount == null) {
			return null;
		}
		String normalized = payjpAccount.trim();
		if (normalized.isEmpty()) {
			throw new InvalidRequestOptionsException("Empty payjp account specified!");
		}
		return normalized;
	}

	public static class InvalidRequestOptionsException extends RuntimeException {
		public InvalidRequestOptionsException(String message) {
			super(message);
		}
	}
}
