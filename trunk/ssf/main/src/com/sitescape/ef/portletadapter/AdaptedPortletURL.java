package com.sitescape.ef.portletadapter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSecurityException;
import javax.servlet.http.HttpServletRequest;

import com.sitescape.ef.portletadapter.support.KeyNames;
import com.sitescape.ef.util.Constants;
import com.sitescape.ef.web.util.WebUrlUtil;
import com.sitescape.util.Http;

public class AdaptedPortletURL {

	private HttpServletRequest sreq;
	private PortletRequest preq;
	private String portletName;
	private boolean action;
	private Map params;
	private boolean secure;
	
	private final String ACTION_FALSE = "0";
	private final String ACTION_TRUE = "1";

	/**
	 * Construct an adapted portlet URL from the information passed in.
	 * 
	 * @param req
	 * @param portletName
	 * @param action
	 */
	public AdaptedPortletURL(HttpServletRequest req, String portletName, boolean action) {
		this.sreq = req;
		this.portletName = portletName;
		this.action = action;
		this.secure = req.isSecure();
		this.params = new HashMap();
	}
	
	/**
	 * Construct an adapted portlet URL from the information passed in.
	 * 
	 * @param req
	 * @param portletName
	 * @param action
	 */
	public AdaptedPortletURL(PortletRequest req, String portletName, boolean action) {
		this.preq = req;
		this.portletName = portletName;
		this.action = action;
		this.secure = req.isSecure();
		this.params = new HashMap();
	}
	
	/**
	 * Note: The web/portlet controllers serving browser-based client must 
	 *       *never* use this method. This method is reserved exclusive
	 *       for other types of clients (eg. email notification, rss, and
	 *       web services) that need to create valid adapted porlet urls
	 *       initiated from non web-based interactions. 
	 * 
	 * @param portletName
	 * @param action
	 * @return
	 */
	public static AdaptedPortletURL createAdaptedPortletURLOutOfWebContext
		(String portletName, boolean action) {
		return new AdaptedPortletURL(portletName, action);
	}
	
	/**
	 * Construct an adapted portlet URL without using <code>HttpServletRequest</code>
	 * or <code>PortletRequest</code>.
	 * The necessary information such as hostname, port number, etc., are read in
	 * from the system configuration file (i.e., they are statically configured)
	 * as opposed to being read from the <code>HttpServletRequest</code>.
	 * This should be only used by those interactions that do not come from the
	 * browser client.  
	 *  
	 * @param portletName
	 * @param action
	 */
	private AdaptedPortletURL(String portletName, boolean action) {
		this.sreq = null;
		this.preq = null;
		this.portletName = portletName;
		this.action = action;
		this.secure = false;
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
		
		String adapterRootURL = null;
		
		if(sreq != null)
			adapterRootURL = WebUrlUtil.getAdapterRootURL(sreq, secure);
		else
			adapterRootURL = WebUrlUtil.getAdapterRootURL(preq, secure);
		
		sb.append(adapterRootURL);
		
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
