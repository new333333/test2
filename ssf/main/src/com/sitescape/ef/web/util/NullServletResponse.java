package com.sitescape.ef.web.util;

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
