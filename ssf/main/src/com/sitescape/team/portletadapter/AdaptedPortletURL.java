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
package com.sitescape.team.portletadapter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSecurityException;
import javax.servlet.http.HttpServletRequest;

import com.sitescape.team.portletadapter.support.KeyNames;
import com.sitescape.team.util.Constants;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.Http;
import com.sitescape.util.Validator;

public class AdaptedPortletURL {

	private HttpServletRequest sreq;
	private PortletRequest preq;
	private String portletName;
	private boolean action;
	private Map params;
	private Boolean secure;
	private String adapterUrlString=null;
	private boolean crawler = false;
	
	// Normally, hostname and port are taken either from the runtime context 
	// or from the configuration. The following two fields are used only for
	// the special situations where the application wishes to suppress the
	// default behavior with supplied values.
	private String hostname;
	private Integer port;
	
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
		if(req != null)
			this.secure = req.isSecure();
		this.params = new HashMap();
	}
	
	public AdaptedPortletURL(HttpServletRequest req, String portletName, boolean action, boolean crawler) {
		this(req, portletName, action);
		this.crawler = crawler;
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
		if(req != null)
			this.secure = req.isSecure();
		this.params = new HashMap();
	}
	
	/**
	 * Add to a preconstructed url
	 */
	public AdaptedPortletURL(String urlString) {
		this.adapterUrlString = urlString;
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
		(String portletName, boolean action, boolean isSecure, String hostname, int port) {
		return new AdaptedPortletURL(portletName, action, isSecure, hostname, port);
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
		this.secure = Boolean.FALSE;
		this.params = new HashMap();
	}
	
	private AdaptedPortletURL(String portletName, boolean action, boolean isSecure, String hostname, int port) {
		this(portletName, action);
		this.secure = Boolean.valueOf(isSecure);
		this.hostname = hostname;
		this.port = port;
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

	public void setSecure(boolean sec) throws PortletSecurityException {
		this.secure = Boolean.valueOf(sec);
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		if (adapterUrlString == null) {		
			String adapterRootURL;
			if(secure != null && hostname != null && port != null)
				adapterRootURL = WebUrlUtil.getAdapterRootURL(secure, hostname, port);
			else if(sreq != null)
				adapterRootURL = WebUrlUtil.getAdapterRootURL(sreq, secure);
			else
				adapterRootURL = WebUrlUtil.getAdapterRootURL(preq, secure);
		
			sb.append(adapterRootURL);
					
			if(crawler) {
				sb.append("c/");
				
				sb.append(KeyNames.PORTLET_URL_PORTLET_NAME);
				sb.append(Constants.SLASH);
				sb.append(Http.encodeURL(portletName));
				sb.append(Constants.SLASH);
			
				sb.append(KeyNames.PORTLET_URL_ACTION);
				sb.append(Constants.SLASH);
				sb.append(action? Http.encodeURL(ACTION_TRUE) : Http.encodeURL(ACTION_FALSE));				
			}
			else {
				sb.append("do?");
			
				sb.append(KeyNames.PORTLET_URL_PORTLET_NAME);
				sb.append(Constants.EQUAL);
				sb.append(Http.encodeURL(portletName));
				sb.append(Constants.AMPERSAND);
			
				sb.append(KeyNames.PORTLET_URL_ACTION);
				sb.append(Constants.EQUAL);
				sb.append(action? Http.encodeURL(ACTION_TRUE) : Http.encodeURL(ACTION_FALSE));
			}
		} else {
			sb.append(adapterUrlString);
		}
		Iterator itr = params.entrySet().iterator();

		while (itr.hasNext()) {
			Map.Entry entry = (Map.Entry)itr.next();

			String name = (String)entry.getKey();
			String[] values = (String[])entry.getValue();

			for (int i = 0; i < values.length; i++) {
				if(crawler) {
					if(Validator.isNull(values[i]))
							continue;
					sb.append(Constants.SLASH);
				}
				else {
					sb.append(Constants.AMPERSAND);
				}
				sb.append(name);
				if(crawler)
					sb.append(Constants.SLASH);
				else
					sb.append(Constants.EQUAL);
				sb.append(Http.encodeURL(values[i]));

			}
		}
		
		return sb.toString();
	}

}
