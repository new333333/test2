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

import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.kablink.teaming.portletadapter.support.PortletInfo;

public class RenderRequestImpl extends PortletRequestImpl implements RenderRequest {

	private Map params;

	public RenderRequestImpl(HttpServletRequest req, PortletInfo portletInfo, PortletContext portletContext) {
		super(req, portletInfo, portletContext);
	}

	public String getParameter(String name) {
		if(name == null)
			throw new IllegalArgumentException();
		
		if(params == null)
			return req.getParameter(name);
		else {
			String[] values = (String[]) params.get(name);
			if(values != null)
				return values[0];
			else
				return null;
		}
	}

	public Enumeration getParameterNames() {
		if(params == null) {
			return req.getParameterNames();
		}
		else {
			return new Vector(params.keySet()).elements();
		}
	}

	public String[] getParameterValues(String name) {
		if(name == null)
			throw new IllegalArgumentException();
		
		if(params == null)
			return req.getParameterValues(name);
		else
			return (String[]) params.get(name);
	}

	public Map getParameterMap() {
		if(params == null)
			return req.getParameterMap();
		else
			return params;
	}

	// Non-standard
	public void setRenderParameters(Map params) {
		this.params = params;
	}
	
	public void defineObjects(PortletConfig portletConfig, RenderResponse res) {
		setAttribute(JAVAX_PORTLET_CONFIG, portletConfig);
		setAttribute(JAVAX_PORTLET_REQUEST, this);
		setAttribute(JAVAX_PORTLET_RESPONSE, res);
	}

	public String getETag() {
		return null;
	}
}
