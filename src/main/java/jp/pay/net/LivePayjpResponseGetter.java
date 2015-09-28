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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.mail.internet.MimeUtility;

import jp.pay.Payjp;
import jp.pay.exception.APIConnectionException;
import jp.pay.exception.APIException;
import jp.pay.exception.AuthenticationException;
import jp.pay.exception.InvalidRequestException;
import jp.pay.exception.CardException;

public class LivePayjpResponseGetter implements PayjpResponseGetter {
	private static final String DNS_CACHE_TTL_PROPERTY_NAME = "networkaddress.cache.ttl";

	/*
	 * Set this property to override your environment's default
	 * URLStreamHandler; Settings the property should not be needed in most
	 * environments.
	 */
	private static final String CUSTOM_URL_STREAM_HANDLER_PROPERTY_NAME = "jp.pay.net.customURLStreamHandler";

	public <T> T request(
			APIResource.RequestMethod method,
			String url,
			Map<String, Object> params,
			Class<T> clazz,
			APIResource.RequestType type,
			RequestOptions options) throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
		return _request(method, url, params, clazz, type, options);
	}

	private static String urlEncodePair(String k, String v)
			throws UnsupportedEncodingException {
		return String.format("%s=%s", APIResource.urlEncode(k), APIResource.urlEncode(v));
	}

	static Map<String, String> getHeaders(RequestOptions options) throws UnsupportedEncodingException {
		Map<String, String> headers = new HashMap<String, String>();
		String apiVersion = options.getPayjpVersion();
		headers.put("Accept-Charset", APIResource.CHARSET);
		headers.put("Accept", "application/json");
		headers.put("User-Agent",
				String.format("Payjp/v1 JavaBindings/%s", Payjp.VERSION));
		headers.put("Authorization", String.format("Basic %s", encodeString((options.getApiKey()+':'))));

		// debug headers
		String[] propertyNames = { "os.name", "os.version", "os.arch",
				"java.version", "java.vendor", "java.vm.version",
				"java.vm.vendor" };
		Map<String, String> propertyMap = new HashMap<String, String>();
		for (String propertyName : propertyNames) {
			propertyMap.put(propertyName, System.getProperty(propertyName));
		}
		propertyMap.put("bindings.version", Payjp.VERSION);
		propertyMap.put("lang", "Java");
		propertyMap.put("publisher", "Payjp");
		headers.put("X-Payjp-Client-User-Agent", APIResource.GSON.toJson(propertyMap));
		if (apiVersion != null) {
			headers.put("Payjp-Version", apiVersion);
		}
		if (options.getIdempotencyKey() != null) {
			headers.put("Idempotency-Key", options.getIdempotencyKey());
		}
		if (options.getPayjpAccount() != null) {
			headers.put("Payjp-Account", options.getPayjpAccount());
		}
		return headers;
	}

	private static java.net.HttpURLConnection createPayjpConnection(
			String url, RequestOptions options) throws IOException {
		URL payjpURL;
		String customURLStreamHandlerClassName = System.getProperty(
				CUSTOM_URL_STREAM_HANDLER_PROPERTY_NAME, null);
		if (customURLStreamHandlerClassName != null) {
			// instantiate the custom handler provided
			try {
                @SuppressWarnings("unchecked")
				Class<URLStreamHandler> clazz = (Class<URLStreamHandler>) Class
						.forName(customURLStreamHandlerClassName);
				Constructor<URLStreamHandler> constructor = clazz
						.getConstructor();
				URLStreamHandler customHandler = constructor.newInstance();
				payjpURL = new URL(null, url, customHandler);
			} catch (ClassNotFoundException e) {
				throw new IOException(e);
			} catch (SecurityException e) {
				throw new IOException(e);
			} catch (NoSuchMethodException e) {
				throw new IOException(e);
			} catch (IllegalArgumentException e) {
				throw new IOException(e);
			} catch (InstantiationException e) {
				throw new IOException(e);
			} catch (IllegalAccessException e) {
				throw new IOException(e);
			} catch (InvocationTargetException e) {
				throw new IOException(e);
			}
		} else {
			payjpURL = new URL(url);
		}
		java.net.HttpURLConnection conn = (java.net.HttpURLConnection) payjpURL.openConnection();
		conn.setConnectTimeout(30 * 1000);
		conn.setReadTimeout(80 * 1000);
		conn.setUseCaches(false);
		for (Map.Entry<String, String> header : getHeaders(options).entrySet()) {
			conn.setRequestProperty(header.getKey(), header.getValue());
		}

		return conn;
	}

	private static String formatURL(String url, String query) {
		if (query == null || query.isEmpty()) {
			return url;
		} else {
			// In some cases, URL can already contain a question mark (eg, upcoming invoice lines)
			String separator = url.contains("?") ? "&" : "?";
			return String.format("%s%s%s", url, separator, query);
		}
	}

	private static java.net.HttpURLConnection createGetConnection(
			String url, String query, RequestOptions options) throws IOException  {
		String getURL = formatURL(url, query);
		java.net.HttpURLConnection conn = createPayjpConnection(getURL, options);
		conn.setRequestMethod("GET");

		return conn;
	}

	private static java.net.HttpURLConnection createPostConnection(
			String url, String query, RequestOptions options) throws IOException {
		java.net.HttpURLConnection conn = createPayjpConnection(url, options);

		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", String.format(
				"application/x-www-form-urlencoded;charset=%s", APIResource.CHARSET));

		OutputStream output = null;
		try {
			output = conn.getOutputStream();
			output.write(query.getBytes(APIResource.CHARSET));
		} finally {
			if (output != null) {
				output.close();
			}
		}
		return conn;
	}

	private static java.net.HttpURLConnection createDeleteConnection(
			String url, String query, RequestOptions options) throws IOException {
		String deleteUrl = formatURL(url, query);
		java.net.HttpURLConnection conn = createPayjpConnection(
				deleteUrl, options);
		conn.setRequestMethod("DELETE");

		return conn;
	}

	static String createQuery(Map<String, Object> params)
			throws UnsupportedEncodingException, InvalidRequestException {
		Map<String, String> flatParams = flattenParams(params);
		StringBuilder queryStringBuffer = new StringBuilder();
		for (Map.Entry<String, String> entry : flatParams.entrySet()) {
			if (queryStringBuffer.length() > 0) {
				queryStringBuffer.append("&");
			}
			queryStringBuffer.append(urlEncodePair(entry.getKey(),
					entry.getValue()));
		}
		return queryStringBuffer.toString();
	}


	private static Map<String, String> flattenParams(Map<String, Object> params)
			throws InvalidRequestException {
		if (params == null) {
			return new HashMap<String, String>();
		}
		Map<String, String> flatParams = new LinkedHashMap<String, String>();
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof Map<?, ?>) {
				Map<String, Object> flatNestedMap = new LinkedHashMap<String, Object>();
				Map<?, ?> nestedMap = (Map<?, ?>) value;
				for (Map.Entry<?, ?> nestedEntry : nestedMap.entrySet()) {
					flatNestedMap.put(
							String.format("%s[%s]", key, nestedEntry.getKey()),
							nestedEntry.getValue());
				}
				flatParams.putAll(flattenParams(flatNestedMap));
			} else if (value instanceof List<?>) {
				Map<String, Object> flatNestedMap = new LinkedHashMap<String, Object>();
				Iterator<?> it = ((List<?>)value).iterator();
				for (int index = 0; it.hasNext(); ++index) {
					flatNestedMap.put(String.format("%s[%s]", key, index), it.next());
				}
				flatParams.putAll(flattenParams(flatNestedMap));
			} else if ("".equals(value)) {
					throw new InvalidRequestException("You cannot set '"+key+"' to an empty string. "+
										"We interpret empty strings as null in requests. "+
										"You may set '"+key+"' to null to delete the property.",
										key, null);
			} else if (value == null) {
				flatParams.put(key, "");
			} else {
				flatParams.put(key, value.toString());
			}
		}
		return flatParams;
	}

	// represents Errors returned as JSON
	private static class ErrorContainer {
		private LivePayjpResponseGetter.Error error;
	}

	private static class Error {
		@SuppressWarnings("unused")
		String type;

		String message;

		String code;

		String param;
	}

	private static String getResponseBody(InputStream responseStream)
			throws IOException {
		//\A is the beginning of
		// the stream boundary
		String rBody = new Scanner(responseStream, APIResource.CHARSET)
												.useDelimiter("\\A")
												.next(); //

		responseStream.close();
		return rBody;
	}

	private static PayjpResponse makeURLConnectionRequest(
			APIResource.RequestMethod method, String url, String query,
			RequestOptions options) throws APIConnectionException {
		java.net.HttpURLConnection conn = null;
		try {
			switch (method) {
			case GET:
				conn = createGetConnection(url, query, options);
				break;
			case POST:
				conn = createPostConnection(url, query, options);
				break;
			case DELETE:
				conn = createDeleteConnection(url, query, options);
				break;
			default:
				throw new APIConnectionException(
						String.format(
								"Unrecognized HTTP method %s. "
										+ "This indicates a bug in the Payjp bindings. Please contact "
										+ "support@pay.jp for assistance.",
								method));
			}
			// trigger the request
			int rCode = conn.getResponseCode();
			String rBody;
			Map<String, List<String>> headers;

			if (rCode >= 200 && rCode < 300) {
				rBody = getResponseBody(conn.getInputStream());
			} else {
				rBody = getResponseBody(conn.getErrorStream());
			}
			headers = conn.getHeaderFields();
			return new PayjpResponse(rCode, rBody, headers);

		} catch (IOException e) {
			throw new APIConnectionException(
					String.format(
							"IOException during API request to Payjp (%s): %s "
									+ "Please check your internet connection and try again.",
							Payjp.getApiBase(), e.getMessage()), e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	private static <T> T _request(APIResource.RequestMethod method,
			String url, Map<String, Object> params, Class<T> clazz,
			APIResource.RequestType type, RequestOptions options)
			throws AuthenticationException, InvalidRequestException,
			CardException, APIConnectionException, APIException {
		if (options == null) {
			options = RequestOptions.getDefault();
		}
		String originalDNSCacheTTL = null;
		Boolean allowedToSetTTL = true;

		try {
			originalDNSCacheTTL = java.security.Security
					.getProperty(DNS_CACHE_TTL_PROPERTY_NAME);
			// disable DNS cache
			java.security.Security
					.setProperty(DNS_CACHE_TTL_PROPERTY_NAME, "0");
		} catch (SecurityException se) {
			allowedToSetTTL = false;
		}

		String apiKey = options.getApiKey();
		if (apiKey == null || apiKey.trim().isEmpty()) {
			throw new AuthenticationException(
					"No API key provided. (HINT: set your API key using 'Payjp.apiKey = <API-KEY>'. "
							+ "You can generate API keys from the Payjp web interface. "
							+ "See https://pay.jp/api for details or email support@pay.jp if you have questions.");
		}

		try {
			PayjpResponse response;
			switch (type) {
			case NORMAL:
				response = getPayjpResponse(method, url, params, options);
				break;
			case MULTIPART:
				response = getMultipartPayjpResponse(method, url, params,
						options);
				break;
			default:
				throw new RuntimeException(
						"Invalid APIResource request type. "
								+ "This indicates a bug in the Payjp bindings. Please contact "
								+ "support@pay.jp for assistance.");
			}
			int rCode = response.responseCode;
			String rBody = response.responseBody;
			if (rCode < 200 || rCode >= 300) {
				handleAPIError(rBody, rCode);
			}
			return APIResource.GSON.fromJson(rBody, clazz);
		} finally {
			if (allowedToSetTTL) {
				if (originalDNSCacheTTL == null) {
					// value unspecified by implementation
					// DNS_CACHE_TTL_PROPERTY_NAME of -1 = cache forever
					java.security.Security.setProperty(
							DNS_CACHE_TTL_PROPERTY_NAME, "-1");
				} else {
					java.security.Security.setProperty(
							DNS_CACHE_TTL_PROPERTY_NAME, originalDNSCacheTTL);
				}
			}
		}
	}

	private static PayjpResponse getPayjpResponse(
			APIResource.RequestMethod method, String url,
			Map<String, Object> params, RequestOptions options)
			throws InvalidRequestException, APIConnectionException,
			APIException {
		String query;
		try {
			query = createQuery(params);
		} catch (UnsupportedEncodingException e) {
			throw new InvalidRequestException("Unable to encode parameters to "
					+ APIResource.CHARSET
					+ ". Please contact support@pay.jp for assistance.",
					null, e);
		}

		try {
			// HTTPSURLConnection verifies SSL cert by default
			return makeURLConnectionRequest(method, url, query, options);
		} catch (ClassCastException ce) {
			// appengine doesn't have HTTPSConnection, use URLFetch API
			String appEngineEnv = System.getProperty(
					"com.google.appengine.runtime.environment", null);
			if (appEngineEnv != null) {
				return makeAppEngineRequest(method, url, query, options);
			} else {
				// non-appengine ClassCastException
				throw ce;
			}
		}
	}

	private static PayjpResponse getMultipartPayjpResponse(
			APIResource.RequestMethod method, String url,
			Map<String, Object> params, RequestOptions options)
			throws InvalidRequestException, APIConnectionException,
			APIException {

		if (method != APIResource.RequestMethod.POST) {
			throw new InvalidRequestException(
					"Multipart requests for HTTP methods other than POST "
							+ "are currently not supported.", null, null);
		}

		java.net.HttpURLConnection conn = null;
		try {
			conn = createPayjpConnection(url, options);

			String boundary = MultipartProcessor.getBoundary();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", String.format(
					"multipart/form-data; boundary=%s", boundary));

			MultipartProcessor multipartProcessor = null;
			try {
				multipartProcessor = new MultipartProcessor(
						conn, boundary, APIResource.CHARSET);

				for (Map.Entry<String, Object> entry : params.entrySet()) {
					String key = entry.getKey();
					Object value = entry.getValue();

					if (value instanceof File) {
						File currentFile = (File) value;
						if (!currentFile.exists()) {
							throw new InvalidRequestException("File for key "
									+ key + " must exist.", null, null);
						} else if (!currentFile.isFile()) {
							throw new InvalidRequestException("File for key "
									+ key
									+ " must be a file and not a directory.",
									null, null);
						} else if (!currentFile.canRead()) {
							throw new InvalidRequestException(
									"Must have read permissions on file for key "
									+ key + ".", null, null);
						}
						multipartProcessor.addFileField(key, currentFile);
					} else {
						// We only allow a single level of nesting for params
						// for multipart
						multipartProcessor.addFormField(key, (String) value);
					}
				}

			} finally {
				if (multipartProcessor != null) {
					multipartProcessor.finish();
				}
			}

			// trigger the request
			int rCode = conn.getResponseCode();
			String rBody;
			Map<String, List<String>> headers;

			if (rCode >= 200 && rCode < 300) {
				rBody = getResponseBody(conn.getInputStream());
			} else {
				rBody = getResponseBody(conn.getErrorStream());
			}
			headers = conn.getHeaderFields();
			return new PayjpResponse(rCode, rBody, headers);

		} catch (IOException e) {
			throw new APIConnectionException(
					String.format(
							"IOException during API request to Payjp (%s): %s "
									+ "Please check your internet connection and try again. If this problem persists,"
									+ "you should check Payjp's service status at https://twitter.com/payjpstatus,"
									+ " or let us know at support@pay.jp.",
							Payjp.getApiBase(), e.getMessage()), e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

	}

	private static void handleAPIError(String rBody, int rCode) throws InvalidRequestException, AuthenticationException, CardException, APIException {
		LivePayjpResponseGetter.Error error = APIResource.GSON.fromJson(rBody,
				LivePayjpResponseGetter.ErrorContainer.class).error;
		switch (rCode) {
		case 400:
			throw new InvalidRequestException(error.message, error.param, error.type, error.code);
		case 404:
			throw new InvalidRequestException(error.message, error.param);
		case 401:
			throw new AuthenticationException(error.message);
		case 402:
			throw new CardException(error.message, error.param, error.code);
		default:
			throw new APIException(error.message, null);
		}
	}

	/*
	 * This is slower than usual because of reflection but avoids having to
	 * maintain AppEngine-specific JAR
	 */
	private static PayjpResponse makeAppEngineRequest(APIResource.RequestMethod method,
			String url, String query, RequestOptions options) throws APIException {
		String unknownErrorMessage = "Sorry, an unknown error occurred while trying to use the "
				+ "Google App Engine runtime. Please contact support@pay.jp for assistance.";
		try {
			if (method == APIResource.RequestMethod.GET || method == APIResource.RequestMethod.DELETE) {
				url = String.format("%s?%s", url, query);
			}
			URL fetchURL = new URL(url);

			Class<?> requestMethodClass = Class
					.forName("com.google.appengine.api.urlfetch.HTTPMethod");
			Object httpMethod = requestMethodClass.getDeclaredField(
					method.name()).get(null);

			Class<?> fetchOptionsBuilderClass = Class
					.forName("com.google.appengine.api.urlfetch.FetchOptions$Builder");
			Object fetchOptions;
			try {
				fetchOptions = fetchOptionsBuilderClass.getDeclaredMethod(
						"validateCertificate").invoke(null);
			} catch (NoSuchMethodException e) {
				System.err
						.println("Warning: this App Engine SDK version does not allow verification of SSL certificates;"
								+ "this exposes you to a MITM attack. Please upgrade your App Engine SDK to >=1.5.0. "
								+ "If you have questions, contact support@pay.jp.");
				fetchOptions = fetchOptionsBuilderClass.getDeclaredMethod(
						"withDefaults").invoke(null);
			}

			Class<?> fetchOptionsClass = Class
					.forName("com.google.appengine.api.urlfetch.FetchOptions");

			// GAE requests can time out after 60 seconds, so make sure we leave
			// some time for the application to handle a slow Payjp
			fetchOptionsClass.getDeclaredMethod("setDeadline",
					java.lang.Double.class)
					.invoke(fetchOptions, new Double(55));

			Class<?> requestClass = Class
					.forName("com.google.appengine.api.urlfetch.HTTPRequest");

			Object request = requestClass.getDeclaredConstructor(URL.class,
					requestMethodClass, fetchOptionsClass).newInstance(
					fetchURL, httpMethod, fetchOptions);

			if (method == APIResource.RequestMethod.POST) {
				requestClass.getDeclaredMethod("setPayload", byte[].class)
						.invoke(request, query.getBytes());
			}

			for (Map.Entry<String, String> header : getHeaders(options)
					.entrySet()) {
				Class<?> httpHeaderClass = Class
						.forName("com.google.appengine.api.urlfetch.HTTPHeader");
				Object reqHeader = httpHeaderClass.getDeclaredConstructor(
						String.class, String.class).newInstance(
						header.getKey(), header.getValue());
				requestClass.getDeclaredMethod("setHeader", httpHeaderClass)
						.invoke(request, reqHeader);
			}

			Class<?> urlFetchFactoryClass = Class
					.forName("com.google.appengine.api.urlfetch.URLFetchServiceFactory");
			Object urlFetchService = urlFetchFactoryClass.getDeclaredMethod(
					"getURLFetchService").invoke(null);

			Method fetchMethod = urlFetchService.getClass().getDeclaredMethod(
					"fetch", requestClass);
			fetchMethod.setAccessible(true);
			Object response = fetchMethod.invoke(urlFetchService, request);

			int responseCode = (Integer) response.getClass()
					.getDeclaredMethod("getResponseCode").invoke(response);
			String body = new String((byte[]) response.getClass()
					.getDeclaredMethod("getContent").invoke(response), APIResource.CHARSET);
			return new PayjpResponse(responseCode, body);
		} catch (InvocationTargetException e) {
			throw new APIException(unknownErrorMessage, e);
		} catch (MalformedURLException e) {
			throw new APIException(unknownErrorMessage, e);
		} catch (NoSuchFieldException e) {
			throw new APIException(unknownErrorMessage, e);
		} catch (SecurityException e) {
			throw new APIException(unknownErrorMessage, e);
		} catch (NoSuchMethodException e) {
			throw new APIException(unknownErrorMessage, e);
		} catch (ClassNotFoundException e) {
			throw new APIException(unknownErrorMessage, e);
		} catch (IllegalArgumentException e) {
			throw new APIException(unknownErrorMessage, e);
		} catch (IllegalAccessException e) {
			throw new APIException(unknownErrorMessage, e);
		} catch (InstantiationException e) {
			throw new APIException(unknownErrorMessage, e);
		} catch (UnsupportedEncodingException e) {
			throw new APIException(unknownErrorMessage, e);
		}
	}

	private static String encodeString(String str) {
		String result = null;
		ByteArrayOutputStream outStreamByte = new ByteArrayOutputStream();
		OutputStream  outStream = null;

		try{
			outStream = MimeUtility.encode(outStreamByte, "base64");
			outStream.write(str.getBytes());
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if (outStream != null) {
					outStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (outStreamByte != null) {
			result = outStreamByte.toString().trim();
		}

		return result;
	}
}
