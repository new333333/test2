/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.web.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * An implementation of HttpServletResponse that doesn't do anything useful.
 * 
 * IMPORTANT: Do NOT make this class dependent upon any other class in the
 * system. In other word, do NOT import any class other than java or
 * javax classes.
 * 
 * @author jong
 *
 */
public class NullServletResponse implements HttpServletResponse {

	@Override
	public void addCookie(Cookie cookie) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsHeader(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String encodeURL(String url) {
		throw new UnsupportedOperationException();

	}

	@Override
	public String encodeRedirectURL(String url) {
		throw new UnsupportedOperationException();

	}

	@Override
	public String encodeUrl(String url) {
		throw new UnsupportedOperationException();

	}

	@Override
	public String encodeRedirectUrl(String url) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		throw new UnsupportedOperationException();

	}

	@Override
	public void sendError(int sc) throws IOException {
		throw new UnsupportedOperationException();

	}

	@Override
	public void sendRedirect(String location) throws IOException {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setDateHeader(String name, long date) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void addDateHeader(String name, long date) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setHeader(String name, String value) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void addHeader(String name, String value) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setIntHeader(String name, int value) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void addIntHeader(String name, int value) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setStatus(int sc) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setStatus(int sc, String sm) {
		throw new UnsupportedOperationException();

	}

	@Override
	public String getCharacterEncoding() {
		throw new UnsupportedOperationException();

	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		throw new UnsupportedOperationException();

	}

	@Override
	public PrintWriter getWriter() throws IOException {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setContentLength(int len) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setContentType(String type) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setBufferSize(int size) {
		throw new UnsupportedOperationException();

	}

	@Override
	public int getBufferSize() {
		throw new UnsupportedOperationException();

	}

	@Override
	public void flushBuffer() throws IOException {
		throw new UnsupportedOperationException();

	}

	@Override
	public void resetBuffer() {
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean isCommitted() {
		throw new UnsupportedOperationException();

	}

	@Override
	public void reset() {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setLocale(Locale loc) {
		throw new UnsupportedOperationException();

	}

	@Override
	public Locale getLocale() {
		throw new UnsupportedOperationException();

	}

	@Override
	public String getContentType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCharacterEncoding(String arg0) {
		throw new UnsupportedOperationException();
	}

	public String getHeader(String arg0) {
		throw new UnsupportedOperationException();
	}

	public Collection<String> getHeaderNames() {
		throw new UnsupportedOperationException();
	}

	public Collection<String> getHeaders(String arg0) {
		throw new UnsupportedOperationException();
	}

	public int getStatus() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setContentLengthLong(long length) {
		throw new UnsupportedOperationException();
	}

}
