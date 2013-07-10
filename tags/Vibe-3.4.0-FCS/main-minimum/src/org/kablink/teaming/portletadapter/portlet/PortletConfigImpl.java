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

import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

public class PortletConfigImpl implements PortletConfig {

	private String portletName;
	private PortletContext portletCtx;
	private Map params;
	private String resourceBundle;
	
	public PortletConfigImpl(String portletName, PortletContext portletCtx,
							 Map params, String resourceBundle) {

		this.portletName = portletName;
		this.portletCtx = portletCtx;
		this.params = params;
		this.resourceBundle = resourceBundle;
	}

	public String getPortletName() {
		return portletName;
	}

	public PortletContext getPortletContext() {
		return portletCtx;
	}

	public ResourceBundle getResourceBundle(Locale locale) {
		
		// TODO This implementation is rather  inefficient as is the case with 
		// typical usage of ResourceBundle...
				
		return ResourceBundle.getBundle(resourceBundle, locale, 
				Thread.currentThread().getContextClassLoader());
	}

	public String getInitParameter(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		return (String)params.get(name);
	}

	public Enumeration getInitParameterNames() {
		return Collections.enumeration(params.keySet());
	}

	public Map<String, String[]> getContainerRuntimeOptions() {
		return Collections.EMPTY_MAP;
	}

	public String getDefaultNamespace() {
		return XMLConstants.NULL_NS_URI;
	}

	public Enumeration<QName> getProcessingEventQNames() {
		return new Enumeration<QName>() {
		    public boolean hasMoreElements() { return false; }
		    public QName nextElement() { return null; }
		};
	}

	public Enumeration<String> getPublicRenderParameterNames() {
		return new Enumeration<String>() {
		    public boolean hasMoreElements() { return false; }
		    public String nextElement() { return null; }
		};
	}

	public Enumeration<QName> getPublishingEventQNames() {
		return new Enumeration<QName>() {
		    public boolean hasMoreElements() { return false; }
		    public QName nextElement() { return null; }
		};
	}

	public Enumeration<Locale> getSupportedLocales() {
		return new Enumeration<Locale>() {
		    public boolean hasMoreElements() { return false; }
		    public Locale nextElement() { return null; }
		};
	}
}
