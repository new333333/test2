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
package com.sitescape.team.portletadapter.taglib;

import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.web.WebKeys;
import com.sitescape.util.Validator;

import java.util.Map;

import javax.portlet.PortletConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class PortletURLTag extends ParamAncestorTagImpl {

	protected static final Log logger = LogFactory.getLog(PortletURLTag.class);

	private String _windowState;
	private String _portletMode;
	private String _var;
	private Boolean _secure;
	private String _portletName;

	public int doEndTag() throws JspTagException {
		try {
			HttpServletRequest req =
				(HttpServletRequest)pageContext.getRequest();

			String portletName = _portletName;
			if (portletName == null) {
				PortletConfig portletConfig =
					(PortletConfig)req.getAttribute(
						WebKeys.JAVAX_PORTLET_CONFIG);

				portletName = portletConfig.getPortletName();
			}

			AdaptedPortletURL portletURL = new AdaptedPortletURL(req, portletName, isAction());
			
			// Ignore window state _windowState

			// Ignore portlet mode _portletMode

			if (_secure != null) {
				portletURL.setSecure(_secure.booleanValue());
			}
			else {
				portletURL.setSecure(req.isSecure());
			}

			Map params = getParams();

			if (params != null) {
				portletURL.setParameters(params);
			}

			String portletURLToString = portletURL.toString();

			if (Validator.isNotNull(_var)) {
				pageContext.setAttribute(_var, portletURLToString);
			}
			else {
				pageContext.getOut().print(portletURLToString);
			}

			return EVAL_PAGE;
		}
		catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new JspTagException(e.getLocalizedMessage());
		}
		finally {
			clearParams();
		}
	}

	public abstract boolean isAction();

	public void setWindowState(String windowState) {
		_windowState = windowState;
	}

	public void setPortletMode(String portletMode) {
		_portletMode = portletMode;
	}

	public void setVar(String var) {
		_var = var;
	}

	public void setSecure(boolean secure) {
		_secure = new Boolean(secure);
	}

	public void setPortletName(String portletName) {
		_portletName = portletName;
	}
}