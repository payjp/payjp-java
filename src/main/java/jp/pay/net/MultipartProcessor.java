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

import java.io.File;
import java.io.FileInputStream;
import java.net.URLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Random;

public class MultipartProcessor {
	private final String boundary;
	private static final String LINE_BREAK = "\r\n";
	private OutputStream outputStream;
	private PrintWriter writer;
	private String charset;
	private java.net.HttpURLConnection conn;

	public static String getBoundary() {
		Random random = new Random();
		Long positiveRandomLong = Math.abs(random.nextLong());
		return String.valueOf(positiveRandomLong);
	}
	
	public MultipartProcessor(java.net.HttpURLConnection conn, String boundary, String charset)
			throws IOException {
		this.boundary = boundary;
		this.charset = charset;
		this.conn = conn;

		this.outputStream = conn.getOutputStream();
		this.writer = new PrintWriter(new OutputStreamWriter(outputStream,
				charset), true);
	}

	public void addFormField(String name, String value) {
		writer.append("--" + boundary).append(LINE_BREAK);
		writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
				.append(LINE_BREAK);
		writer.append(LINE_BREAK);
		writer.append(value).append(LINE_BREAK);
		writer.flush();
	}

	public void addFileField(String name, File file) throws IOException {
		String fileName = file.getName();
		writer.append("--" + boundary).append(LINE_BREAK);
		writer.append(
				"Content-Disposition: form-data; name=\"" + name
						+ "\"; filename=\"" + fileName + "\"").append(
				LINE_BREAK);

		String probableContentType = URLConnection
				.guessContentTypeFromName(fileName);
		writer.append("Content-Type: " + probableContentType)
				.append(LINE_BREAK);
		writer.append("Content-Transfer-Encoding: binary").append(LINE_BREAK);
		writer.append(LINE_BREAK);
		writer.flush();

		FileInputStream inputStream = new FileInputStream(file);
		try {
			byte[] buffer = new byte[4096];
			int bytesRead = -1;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			outputStream.flush();
		} finally {
			inputStream.close();
		}

		writer.append(LINE_BREAK);
		writer.flush();
	}

	public void finish() throws IOException {
		writer.append("--" + boundary + "--").append(LINE_BREAK);
		writer.flush();
		writer.close();
		outputStream.flush();
		outputStream.close();
	}

}
