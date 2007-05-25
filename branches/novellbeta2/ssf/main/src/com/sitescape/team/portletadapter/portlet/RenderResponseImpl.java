/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
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
