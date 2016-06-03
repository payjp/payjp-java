/*
 * Copyright (c) 2016 Yusuke Yamamoto
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
import jp.pay.exception.PayjpException;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class HeaderTest {

    @Test
    public void testLongerApiKey() throws PayjpException, IOException {
        Payjp.apiKey = "sk_live_fffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";

        URL payjpURL = new URL("https://pay.jp/api/dummy");
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) payjpURL.openConnection();
        conn.setConnectTimeout(30 * 1000);
        conn.setReadTimeout(80 * 1000);
        conn.setUseCaches(false);
        // ensure that we can pass longer apiKey value as Basic 
        for (Map.Entry<String, String> header : LivePayjpResponseGetter.getHeaders(RequestOptions.getDefault()).entrySet()) {
            conn.setRequestProperty(header.getKey(), header.getValue());
        }
    }

}
