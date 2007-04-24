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