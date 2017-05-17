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
package org.kablink.teaming.portletadapter.portlet;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

public class ActionResponseImpl extends PortletResponseImpl implements ActionResponse {

	private ActionRequestImpl req;
	// Suppress the use of following flag to check JSR-168 adherence. 
	// Spring portlet MVC does not honor that part of the spec.
	//private boolean calledSetRenderParameter;
	private String redirectLocation;
	private Map params;
	
	public ActionResponseImpl(ActionRequestImpl req, HttpServletResponse res,
			  String portletName)
	throws PortletModeException, WindowStateException {
		super(res, portletName);
		this.req = req;
		//this.calledSetRenderParameter = false;
		this.params = new HashMap();
	}

	public void setWindowState(WindowState windowState) throws WindowStateException {
		throw new UnsupportedOperationException();
	}

	public void setPortletMode(PortletMode portletMode) throws PortletModeException {
		throw new UnsupportedOperationException();
	}

	public void sendRedirect(String location) throws IOException {
		if ((location == null)
				|| (!location.startsWith("/") && (location.indexOf("://") == -1))) {

			throw new IllegalArgumentException(location);
		}

		//if (calledSetRenderParameter) {
		//	throw new IllegalStateException();
		//}

		redirectLocation = location;
	}

	public void setRenderParameters(Map params) {
		if (redirectLocation != null) {
			throw new IllegalStateException();
		}

		if (params == null) {
			throw new IllegalArgumentException();
		}
		else {
			Map newParams = new LinkedHashMap();

			Iterator itr = params.entrySet().iterator();

			while (itr.hasNext()) {
				Map.Entry entry = (Map.Entry)itr.next();

				Object key = entry.getKey();
				Object value = entry.getValue();

				if (key == null) {
					throw new IllegalArgumentException();
				}
				else if (value == null) {
					throw new IllegalArgumentException();
				}

				if (value instanceof String[]) {
					newParams.put(key, value);
				}
				else {
					throw new IllegalArgumentException();
				}
			}

			this.params = newParams;
		}

		//calledSetRenderParameter = true;
	}

	public void setRenderParameter(String key, String value) {
		if (redirectLocation != null) {
			throw new IllegalStateException();
		}

		if ((key == null) || (value == null)) {
			throw new IllegalArgumentException();
		}

		setRenderParameter(key, new String[] {value});
	}

	public void setRenderParameter(String key, String[] values) {
		if (redirectLocation != null) {
			throw new IllegalStateException();
		}

		if ((key == null) || (values == null)) {
			throw new IllegalArgumentException();
		}

		for (int i = 0; i < values.length; i++) {
			if (values[i] == null) {
				throw new IllegalArgumentException();
			}
		}

		params.put(key, values);

		//calledSetRenderParameter = true;
	}

	// This method is specific to ActionResponseImpl.
	public Map getRenderParameterMap() {
		return params;
	}
	
	public String getRedirectLocation() {
		return redirectLocation;
	}

	public void sendRedirect(String location, String renderUrlParamName) throws IOException {
		throw new UnsupportedOperationException();
	}

	public PortletMode getPortletMode() {
		return null;
	}

	public WindowState getWindowState() {
		return null;
	}

	public void removePublicRenderParameter(String arg0) {
		throw new UnsupportedOperationException();
	}

	public void setEvent(QName arg0, Serializable arg1) {
		throw new UnsupportedOperationException();
	}

	public void setEvent(String arg0, Serializable arg1) {
		throw new UnsupportedOperationException();
	}

}
