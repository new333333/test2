package com.sitescape.ef.portletadapter.taglib;

import com.sitescape.ef.portletadapter.AdaptedPortletURL;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.util.Validator;

import java.util.Map;

import javax.portlet.PortletConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;

public abstract class PortletURLTag extends ParamAncestorTagImpl {

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
			e.printStackTrace();
			throw new JspTagException(e.getMessage());
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