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
package org.kablink.teaming.portletadapter.portlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.servlet.http.HttpServletRequest;

import org.kablink.teaming.portletadapter.support.PortletInfo;
import org.kablink.teaming.web.WebKeys;


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
