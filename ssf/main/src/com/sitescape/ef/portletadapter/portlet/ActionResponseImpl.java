package com.sitescape.ef.portletadapter.portlet;

import java.io.IOException;
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

public class ActionResponseImpl extends PortletResponseImpl implements ActionResponse {

	private ActionRequestImpl req;
	private boolean calledSetRenderParameter;
	private String redirectLocation;
	private Map params;
	
	public ActionResponseImpl(ActionRequestImpl req, HttpServletResponse res,
			  String portletName)
	throws PortletModeException, WindowStateException {
		super(res, portletName);
		this.req = req;
		this.calledSetRenderParameter = false;
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

		if (calledSetRenderParameter) {
			throw new IllegalStateException();
		}

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

		calledSetRenderParameter = true;
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

		calledSetRenderParameter = true;
	}

	// This method is specific to ActionResponseImpl.
	public Map getRenderParameters() {
		return params;
	}
}
