/**
 * Copyright (c) 2000-2005 Liferay, LLC. All rights reserved.
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sitescape.util;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import javax.activation.DataSource;

/**
 * <a href="ByteArrayDataSource.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Brian Wing Shun Chan
 * @version $Revision: 1.6 $
 *
 */
public class ByteArrayDataSource implements DataSource, Serializable {

	public ByteArrayDataSource(byte[] bytes, String contentType, String name) {
		_bytes = bytes;

		if (contentType == null) {
			_contentType = "application/octet-stream";
		}
		else {
			_contentType = contentType;
			_name = name;
		}
	}

	public byte[] getBytes() {
		return _bytes;
	}

	public String getContentType() {
		return _contentType;
	}

	public InputStream getInputStream() {
		return new ByteArrayInputStream(_bytes, 0, _bytes.length - 2);
	}

	public String getName() {
		return _name;
	}

	public OutputStream getOutputStream() throws IOException {
		throw new FileNotFoundException();
	}

	private	byte[] _bytes;
	private String _contentType;
	private String _name;

}