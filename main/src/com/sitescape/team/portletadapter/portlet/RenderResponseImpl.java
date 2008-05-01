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
package com.sitescape.team.portletadapter.portlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Locale;

import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletResponse;

import com.sitescape.team.portletadapter.support.PortletAdapterUtil;

public class RenderResponseImpl extends PortletResponseImpl implements RenderResponse {

	private RenderRequestImpl req;
	private String contentType;
	private boolean calledGetPortletOutputStream;
 	private boolean calledGetWriter;
	
	public RenderResponseImpl(RenderRequestImpl req, HttpServletResponse res,
			String portletName) {
		super(res, portletName);
		this.req = req;
		this.calledGetPortletOutputStream = false;
		this.calledGetWriter = false;
	}
	
	public String getContentType() {
		return contentType;
	}

	public PortletURL createRenderURL() {
		PortletURL portletURL = createPortletURL(false);

		/* We don't support window state.
		try {
			portletURL.setWindowState(req.getWindowState());
		}
		catch (WindowStateException wse) {
		}*/

		/* We don't support portlet mode.
		try {
			portletURL.setPortletMode(req.getPortletMode());
		}
		catch (PortletModeException pme) {
		}*/

		return portletURL;
	}

	public PortletURL createActionURL() {
		PortletURL portletURL = createPortletURL(true);

		/* We don't support window state.
		try {
			portletURL.setWindowState(req.getWindowState());
		}
		catch (WindowStateException wse) {
		}*/

		/* We don't support portlet mode.
		try {
			portletURL.setPortletMode(req.getPortletMode());
		}
		catch (PortletModeException pme) {
		}*/

		return portletURL;

	}

	public String getNamespace() {
		return PortletAdapterUtil.getPortletNamespace(portletName);
	}

	public void setTitle(String title) {
		// GenericServlet calls this. Simply ignore. 
	}

	public void setContentType(String contentType) {
		// Due to the bug described in # 59 in Aspen issue tracking database,
		// we are removing any content type checking. 
		//  
		//Enumeration enu = req.getResponseContentTypes();

		//boolean valid = false;

		//while (enu.hasMoreElements()) {
			//String resContentType = (String)enu.nextElement();

			//if(resContentType.equals("*") ||
				//	resContentType.equals("*/*")) {
				//valid = true;
			//}
			//else if (resContentType.endsWith("/*") && contentType.startsWith(resContentType.substring(0, resContentType.indexOf("/")))) {
				//valid = true;
			//}
			//else if (contentType.startsWith(resContentType)) {
				//valid = true;
			//}
		//}

		//if (!valid) {
			//throw new IllegalArgumentException();
		//}

		getHttpServletResponse().setContentType(contentType);
		
		this.contentType = contentType;
	}

	public String getCharacterEncoding() {
		return res.getCharacterEncoding();
	}

	public PrintWriter getWriter() throws IOException {
		if (calledGetPortletOutputStream) {
			throw new IllegalStateException();
		}

		if (contentType == null) {
			throw new IllegalStateException();
		}

		calledGetWriter = true;

		return res.getWriter();
	}

	public Locale getLocale() {
		return req.getLocale();
	}

	public void setBufferSize(int size) {
		res.setBufferSize(size);
	}

	public int getBufferSize() {
		return res.getBufferSize();
	}

	public void flushBuffer() throws IOException {
		res.flushBuffer();
	}

	public void resetBuffer() {
		res.resetBuffer();
	}

	public boolean isCommitted() {
		return res.isCommitted();
	}

	public void reset() {
		res.reset();
	}

	public OutputStream getPortletOutputStream() throws IOException {
		if (calledGetWriter) {
			throw new IllegalStateException();
		}

		if (contentType == null) {
			throw new IllegalStateException();
		}

		calledGetPortletOutputStream = true;

		return res.getOutputStream();
	}
	
	private PortletURL createPortletURL(boolean action) {
		return new PortletURLImpl(req.getHttpServletRequest(), portletName, action);
	}

}
