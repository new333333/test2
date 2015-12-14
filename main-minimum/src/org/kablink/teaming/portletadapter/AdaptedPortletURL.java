/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.portletadapter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSecurityException;
import javax.servlet.http.HttpServletRequest;

import org.kablink.teaming.util.SpringContextUtil;

public class AdaptedPortletURL {

	 HttpServletRequest sreq;
	 PortletRequest preq;
	 String portletName;
	 boolean action;
	 Map params;
	 Boolean secure;
	 String adapterUrlString=null;
	 boolean crawler = false;
	
	// Normally, hostname and port are taken either from the runtime context 
	// or from the configuration. The following two fields are used only for
	// the special situations where the application wishes to suppress the
	// default behavior with supplied values.
	 String hostname;
	 Integer port;
	
	 final String ACTION_FALSE = "0";
	 final String ACTION_TRUE = "1";
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

	public AdaptedPortletURL(PortletRequest req, String portletName, boolean action, boolean crawler) {
		this(req, portletName, action);
		this.crawler = crawler;
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
		this.params = new HashMap();
	}
	
	private AdaptedPortletURL(String portletName, boolean action, boolean isSecure, String hostname, int port) {
		this(portletName, action);
		this.secure = Boolean.valueOf(isSecure);
		this.hostname = hostname;
		this.port = port;
	}
	
	/**
	 * Return the value of the given parameter.
	 */
	public String getParameterSingleValue( String paramName )
	{
		String value = null;
		
		if ( params != null )
		{
		   String[] values;
		   
		   values = (String[])params.get( paramName );
		   if ( values != null && values.length > 0 )
		   {
		      value = (String) values[0];
		   }
      }
		
		return value;
	}// end getParameterSingleValue()
	
	
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

	public Map getParameterMap() {
		return params;
	}
	
	public void setSecure(boolean sec) throws PortletSecurityException {
		this.secure = Boolean.valueOf(sec);
	}
	
	public void setCrawler(boolean crawler) {
		this.crawler = crawler;
	}
		
	public String toString() {
		PortletUrlToStringHelperInterface helper = (PortletUrlToStringHelperInterface) SpringContextUtil.getBean("portletUrlToStringHelper");
		return helper.toString(this);
	}

}
