/*
 * Copyright (c) 2022 Pay, Inc. (https://pay.co.jp/)
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

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RetryDelayTest {

	@Test
	public void testRetryDelay() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Method getRetryDelay = LivePayjpResponseGetter.class.getDeclaredMethod("getRetryDelay", int.class);
		getRetryDelay.setAccessible(true);
		assertBetween((Long) getRetryDelay.invoke(null, 0), 1, 2);
		assertBetween((Long) getRetryDelay.invoke(null, 1), 2, 4);
		assertBetween((Long) getRetryDelay.invoke(null, 2), 3, 9);
		assertBetween((Long) getRetryDelay.invoke(null, 3), 4, 16);
	}

	private void assertBetween(Long value, int start, int end) {
		assertTrue(value.compareTo((long) (start * 1000)) >= 0);
		assertTrue(value.compareTo((long) (end * 1000)) <= 0);
	}
}
