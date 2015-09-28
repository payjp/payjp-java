package jp.pay.net;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import jp.pay.Payjp;
import jp.pay.exception.APIConnectionException;
import jp.pay.exception.APIException;
import jp.pay.exception.AuthenticationException;
import jp.pay.exception.CardException;
import jp.pay.exception.InvalidRequestException;
import jp.pay.model.ExternalAccountTypeAdapterFactory;
import jp.pay.model.PayjpObject;
import jp.pay.model.PayjpObjectDeserializer;
import jp.pay.model.PayjpRawJsonObject;
import jp.pay.model.PayjpRawJsonObjectDeserializer;

public abstract class APIResource extends PayjpObject {
	private static PayjpResponseGetter payjpResponseGetter = new LivePayjpResponseGetter();

	public static void setPayjpResponseGetter(PayjpResponseGetter srg) {
		APIResource.payjpResponseGetter = srg;
	}

	public static final Gson GSON = new GsonBuilder()
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.registerTypeAdapter(PayjpObject.class, new PayjpObjectDeserializer())
			.registerTypeAdapter(PayjpRawJsonObject.class, new PayjpRawJsonObjectDeserializer())
			.registerTypeAdapterFactory(new ExternalAccountTypeAdapterFactory())
			.create();

	private static String className(Class<?> clazz) {
		String className = clazz.getSimpleName().toLowerCase().replace("$", " ");

		// TODO: Delurk this, with invoiceitem being a valid url, we can't get too
		// fancy yet.
		if (className.equals("applicationfee")) {
			return "application_fee";
		} else if (className.equals("fileupload")) {
			return "file";
		} else if (className.equals("bitcoinreceiver")) {
			return "bitcoin_receiver";
		} else {
			return className;
		}
	}

	protected static String singleClassURL(Class<?> clazz) {
		return singleClassURL(clazz, Payjp.getApiBase());
	}

	protected static String singleClassURL(Class<?> clazz, String apiBase) {
		return String.format("%s/v1/%s", apiBase, className(clazz));
	}

	protected static String classURL(Class<?> clazz) {
		return classURL(clazz, Payjp.getApiBase());
	}

	protected static String classURL(Class<?> clazz, String apiBase) {
		return String.format("%ss", singleClassURL(clazz, apiBase));
	}

	protected static String instanceURL(Class<?> clazz, String id)
			throws InvalidRequestException {
		return instanceURL(clazz, id, Payjp.getApiBase());
	}

	protected static String instanceURL(Class<?> clazz, String id, String apiBase)
			throws InvalidRequestException {
		try {
			return String.format("%s/%s", classURL(clazz, apiBase), urlEncode(id));
		} catch (UnsupportedEncodingException e) {
			throw new InvalidRequestException("Unable to encode parameters to "
					+ CHARSET
					+ ". Please contact support@pay.jp for assistance.",
					null, e);
		}
	}

	public static final String CHARSET = "UTF-8";

	public enum RequestMethod {
		GET, POST, DELETE
	}

	public enum RequestType {
		NORMAL, MULTIPART
	}

	public static String urlEncode(String str) throws UnsupportedEncodingException {
		// Preserve original behavior that passing null for an object id will lead
		// to us actually making a request to /v1/foo/null
		if (str == null) {
			return null;
		}
		else {
			return URLEncoder.encode(str, CHARSET);
		}
	}

	protected static <T> T multipartRequest(APIResource.RequestMethod method,
			String url, Map<String, Object> params, Class<T> clazz,
			RequestOptions options) throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
            CardException, APIException {
		return APIResource.payjpResponseGetter.request(method, url, params, clazz,
				APIResource.RequestType.MULTIPART, options);
	}

	protected static <T> T request(APIResource.RequestMethod method,
			String url, Map<String, Object> params, Class<T> clazz,
			RequestOptions options) throws AuthenticationException,
			InvalidRequestException, APIConnectionException,
			CardException, APIException {
		return APIResource.payjpResponseGetter.request(method, url, params, clazz,
				APIResource.RequestType.NORMAL, options);
	}
}
