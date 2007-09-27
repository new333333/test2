/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.web.util;

import java.io.IOException;
import java.io.PrintWriter;
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

	public void addCookie(Cookie cookie) {
		throw new UnsupportedOperationException();
	}

	public boolean containsHeader(String name) {
		throw new UnsupportedOperationException();
	}

	public String encodeURL(String url) {
		throw new UnsupportedOperationException();

	}

	public String encodeRedirectURL(String url) {
		throw new UnsupportedOperationException();

	}

	public String encodeUrl(String url) {
		throw new UnsupportedOperationException();

	}

	public String encodeRedirectUrl(String url) {
		throw new UnsupportedOperationException();

	}

	public void sendError(int sc, String msg) throws IOException {
		throw new UnsupportedOperationException();

	}

	public void sendError(int sc) throws IOException {
		throw new UnsupportedOperationException();

	}

	public void sendRedirect(String location) throws IOException {
		throw new UnsupportedOperationException();

	}

	public void setDateHeader(String name, long date) {
		throw new UnsupportedOperationException();

	}

	public void addDateHeader(String name, long date) {
		throw new UnsupportedOperationException();

	}

	public void setHeader(String name, String value) {
		throw new UnsupportedOperationException();

	}

	public void addHeader(String name, String value) {
		throw new UnsupportedOperationException();

	}

	public void setIntHeader(String name, int value) {
		throw new UnsupportedOperationException();

	}

	public void addIntHeader(String name, int value) {
		throw new UnsupportedOperationException();

	}

	public void setStatus(int sc) {
		throw new UnsupportedOperationException();

	}

	public void setStatus(int sc, String sm) {
		throw new UnsupportedOperationException();

	}

	public String getCharacterEncoding() {
		throw new UnsupportedOperationException();

	}

	public ServletOutputStream getOutputStream() throws IOException {
		throw new UnsupportedOperationException();

	}

	public PrintWriter getWriter() throws IOException {
		throw new UnsupportedOperationException();

	}

	public void setContentLength(int len) {
		throw new UnsupportedOperationException();

	}

	public void setContentType(String type) {
		throw new UnsupportedOperationException();

	}

	public void setBufferSize(int size) {
		throw new UnsupportedOperationException();

	}

	public int getBufferSize() {
		throw new UnsupportedOperationException();

	}

	public void flushBuffer() throws IOException {
		throw new UnsupportedOperationException();

	}

	public void resetBuffer() {
		throw new UnsupportedOperationException();

	}

	public boolean isCommitted() {
		throw new UnsupportedOperationException();

	}

	public void reset() {
		throw new UnsupportedOperationException();

	}

	public void setLocale(Locale loc) {
		throw new UnsupportedOperationException();

	}

	public Locale getLocale() {
		throw new UnsupportedOperationException();

	}

}
