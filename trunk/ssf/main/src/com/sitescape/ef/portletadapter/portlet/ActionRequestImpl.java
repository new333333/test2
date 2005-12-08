package com.sitescape.ef.portletadapter.portlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.servlet.http.HttpServletRequest;

import com.sitescape.ef.portletadapter.support.PortletInfo;
import com.sitescape.ef.web.WebKeys;

public class ActionRequestImpl extends PortletRequestImpl implements ActionRequest {

	private boolean calledGetReader;
	
	public ActionRequestImpl(HttpServletRequest req, PortletInfo portletInfo, PortletContext portletContext) {
		super(req, portletInfo, portletContext);
	}

	public InputStream getPortletInputStream() throws IOException {
		return getHttpServletRequest().getInputStream();
	}

	public void setCharacterEncoding(String enc) throws UnsupportedEncodingException {
		if (calledGetReader) {
			throw new IllegalStateException();
		}

		getHttpServletRequest().setCharacterEncoding(enc);
	}

	public BufferedReader getReader() throws UnsupportedEncodingException, IOException {
		calledGetReader = true;

		return getHttpServletRequest().getReader();
	}

	public String getCharacterEncoding() {
		return getHttpServletRequest().getCharacterEncoding();
	}

	public String getContentType() {
		return getHttpServletRequest().getContentType();
	}

	public int getContentLength() {
		return getHttpServletRequest().getContentLength();
	}
	
	public void defineObjects(PortletConfig portletConfig, ActionResponse res) {
		setAttribute(WebKeys.JAVAX_PORTLET_CONFIG, portletConfig);
		setAttribute(WebKeys.JAVAX_PORTLET_REQUEST, this);
		setAttribute(WebKeys.JAVAX_PORTLET_RESPONSE, res);
	}
}
