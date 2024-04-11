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
package jp.pay;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import jp.pay.Payjp;
import jp.pay.exception.APIException;
import jp.pay.exception.AuthenticationException;
import jp.pay.exception.CardException;
import jp.pay.exception.InvalidRequestException;
import jp.pay.exception.PayjpException;
import jp.pay.net.APIResource;
import jp.pay.net.RequestOptions;
import jp.pay.net.PayjpResponseGetter;
import jp.pay.net.LivePayjpResponseGetter;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class BasePayjpTest {
	public static PayjpResponseGetter networkMock;

	public static <T> void verifyGet(
			Class<T> clazz,
			String url) throws PayjpException {
		verifyRequest(APIResource.RequestMethod.GET, clazz, url, null,
				APIResource.RequestType.NORMAL, RequestOptions.getDefault());
	}

	public static <T> void verifyGet(
			Class<T> clazz,
			String url,
			Map<String, Object> params) throws PayjpException {
		verifyRequest(APIResource.RequestMethod.GET, clazz, url, params,
				APIResource.RequestType.NORMAL, RequestOptions.getDefault());
	}

	public static <T> void verifyPost(
			Class<T> clazz,
			String url) throws PayjpException {
		verifyRequest(APIResource.RequestMethod.POST, clazz, url, null,
				APIResource.RequestType.NORMAL, RequestOptions.getDefault());
	}

	public static <T> void verifyPost(
			Class<T> clazz,
			String url,
			Map<String, Object> params) throws PayjpException {
		verifyRequest(APIResource.RequestMethod.POST, clazz, url, params,
				APIResource.RequestType.NORMAL, RequestOptions.getDefault());
	}

	public static <T> void verifyRequest(
			APIResource.RequestMethod method,
			Class<T> clazz,
			String url,
			Map<String, Object> params,
			APIResource.RequestType requestType,
			RequestOptions options) throws PayjpException {
		verify(networkMock).request(
				eq(method),
				eq(url),
				argThat(new ParamMapMatcher(params)),
				eq(clazz),
				eq(requestType),
				argThat(new RequestOptionsMatcher(options)));
	}

	public static <T> void stubNetwork(Class<T> clazz, String response) throws PayjpException {
		Mockito.reset(networkMock);
		when(networkMock.request(
					Mockito.any(APIResource.RequestMethod.class),
					Mockito.anyString(),
					Mockito.<Map<String, Object>>any(),
					Mockito.<Class<T>>any(),
					Mockito.any(APIResource.RequestType.class),
					Mockito.any(RequestOptions.class))).thenReturn(APIResource.GSON.fromJson(response, clazz));
	}

    public static <T> void stubNetwork(Class<T> clazz, int status, String response) throws PayjpException {
        try {
            Method handleAPIError = LivePayjpResponseGetter.class.getDeclaredMethod("handleAPIError", String.class, int.class);
            handleAPIError.setAccessible(true);
            handleAPIError.invoke(null, response, status);
        } catch (InvocationTargetException e) {
            Throwable exc = e.getCause();
            // included in java.lang.NoSuchMethodException
            Mockito.reset(networkMock);
            when(networkMock.request(
                Mockito.any(APIResource.RequestMethod.class),
                Mockito.anyString(),
                Mockito.<Map<String, Object>>any(),
                Mockito.<Class<T>>any(),
                Mockito.any(APIResource.RequestType.class),
                Mockito.any(RequestOptions.class))
            ).thenThrow(exc);
            return;
        } catch (Exception e) {
        }
        stubNetwork(clazz, response);
        return;
    }

	public static class ParamMapMatcher extends ArgumentMatcher<Map<String, Object>> {
		private Map<String, Object> other;

		public ParamMapMatcher(Map<String, Object> other) {
			this.other = other;
		}

		/* Treat null references as equal to empty maps */
		public boolean matches(Object obj) {
			if (obj == null) {
				return this.other == null || this.other.isEmpty();
			} else if (obj instanceof Map) {
				Map<String, Object> paramMap = (Map<String, Object>) obj;
				if (this.other == null) {
					return paramMap.isEmpty();
				} else {
					return this.other.equals(paramMap);
				}
			} else {
				return false;
			}
		}
	}

	public static class RequestOptionsMatcher extends ArgumentMatcher<RequestOptions> {
		private RequestOptions other;

		public RequestOptionsMatcher(RequestOptions other) {
			this.other = other;
		}

		/* Treat null reference as RequestOptions.getDefault() */
		public boolean matches(Object obj) {
			RequestOptions defaultOptions = RequestOptions.getDefault();
			if (obj == null) {
				return this.other == null || this.other.equals(defaultOptions);
			} else if (obj instanceof RequestOptions) {
				RequestOptions requestOptions = (RequestOptions) obj;
				if (this.other == null) {
					return requestOptions.equals(defaultOptions);
				} else {
					return this.other.equals(requestOptions);
				}
			} else {
				return false;
			}
		}
	}

	@BeforeClass
	public static void setUp() {
		Payjp.apiKey = "foobar";
	}

	@Before
	public void setUpMock() {
		networkMock = mock(PayjpResponseGetter.class);
	}

	protected String resource(String path) throws IOException {
		InputStream resource = getClass().getResourceAsStream(path);

		ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
		byte[] buf = new byte [1024];

		for( int i = resource.read(buf); i > 0; i = resource.read(buf)) {
			os.write(buf,0,i);
		}

		return os.toString("utf8");

	}

	@Before
    public void mockPayjpResponseGetter() {
        APIResource.setPayjpResponseGetter(networkMock);
    }

    @After
    public void unmockPayjpResponseGetter() {
        /* This needs to be done because tests aren't isolated in Java */
        APIResource.setPayjpResponseGetter(new LivePayjpResponseGetter());
    }

	@BeforeClass
	public static void setApiKey() {
		Payjp.apiKey = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";	// public api key for test
	}
}
