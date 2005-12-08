package com.sitescape.ef.portletadapter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.portlet.PortletSecurityException;
import javax.servlet.http.HttpServletRequest;

import com.sitescape.ef.portletadapter.support.KeyNames;
import com.sitescape.ef.util.Constants;
import com.sitescape.ef.util.WebUrlUtil;
import com.sitescape.util.Http;

public class AdaptedPortletURL {

	private HttpServletRequest req;
	private String portletName;
	private boolean action;
	private Map params;
	private boolean secure;
	
	private final String ACTION_FALSE = "0";
	private final String ACTION_TRUE = "1";

	public AdaptedPortletURL(HttpServletRequest req, String portletName, boolean action) {
		this.req = req;
		this.portletName = portletName;
		this.action = action;
		this.secure = req.isSecure();
		this.params = new HashMap();
	}
	
	public void setParameter(String name, String value) {
		if ((name == null) || (value == null)) {
			throw new IllegalArgumentException();
		}

		setParameter(name, new String[] {value});
	}

	public void setParameter(String name, String[] values) {
		if ((name == null) || (values == null)) {
			throw new IllegalArgumentException();
		}

		for (int i = 0; i < values.length; i++) {
			if (values[i] == null) {
				throw new IllegalArgumentException();
			}
		}

		params.put(name, values);
	}

	public void setParameters(Map params) {
		if (params == null) {
			throw new IllegalArgumentException();
		}

		Map newParams = new LinkedHashMap();

		Iterator itr = params.entrySet().iterator();

		while (itr.hasNext()) {
			Map.Entry entry = (Map.Entry) itr.next();

			Object key = entry.getKey();
			Object value = entry.getValue();

			if (key == null) {
				throw new IllegalArgumentException();
			} else if (value == null) {
				throw new IllegalArgumentException();
			}

			if (value instanceof String[]) {
				newParams.put(key, value);
			} else {
				throw new IllegalArgumentException();
			}
		}

		this.params = newParams;
	}

	public void setSecure(boolean secure) throws PortletSecurityException {
		this.secure = secure;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append(WebUrlUtil.getAdapterRootURL(req, secure));
		
		sb.append("do?");
		
		sb.append(KeyNames.PORTLET_URL_PORTLET_NAME);
		sb.append(Constants.EQUAL);
		sb.append(Http.encodeURL(portletName));
		sb.append(Constants.AMPERSAND);
		
		sb.append(KeyNames.PORTLET_URL_ACTION);
		sb.append(Constants.EQUAL);
		sb.append(action? Http.encodeURL(ACTION_TRUE) : Http.encodeURL(ACTION_FALSE));
		sb.append(Constants.AMPERSAND);
		
		Iterator itr = params.entrySet().iterator();

		while (itr.hasNext()) {
			Map.Entry entry = (Map.Entry)itr.next();

			String name = (String)entry.getKey();
			String[] values = (String[])entry.getValue();

			for (int i = 0; i < values.length; i++) {
				sb.append(name);
				sb.append(Constants.EQUAL);
				sb.append(Http.encodeURL(values[i]));

				if ((i + 1 < values.length) || itr.hasNext()) {
					sb.append(Constants.AMPERSAND);
				}
			}
		}
		
		return sb.toString();
	}

}
