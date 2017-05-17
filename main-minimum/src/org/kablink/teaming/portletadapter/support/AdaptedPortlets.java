/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.portletadapter.support;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.portlet.PortletContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import org.kablink.teaming.SingletonViolationException;
import org.kablink.teaming.portletadapter.portlet.PortletConfigImpl;
import org.kablink.teaming.portletadapter.portlet.PortletContextImpl;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;

import org.xml.sax.SAXException;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings("unchecked")
public class AdaptedPortlets implements ServletContextAware, InitializingBean, DisposableBean {
	protected static final Log logger = LogFactory.getLog(AdaptedPortlets.class);

	private static AdaptedPortlets instance; // singleton instance
	
	private ServletContext servletContext;
	
	private Map portlets = new HashMap();
	private PortletContext portletContext;
	
	private String[] portletNames; // names of adapted portlets 
	
	public AdaptedPortlets() {
		if(instance == null)
			instance = this;
		else
			throw new SingletonViolationException(AdaptedPortlets.class);
	}
	
	protected static AdaptedPortlets getInstance() {
		return instance;
	}
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;		
	}
	protected ServletContext getServletContext() {
		return servletContext;
	}

	public void setPortletNames(String[] portletNames) {
		this.portletNames = portletNames;
	}
	
	private boolean isAdaptedPortlet(String portletName) {
		if(portletNames == null)
			return false;
		
		for(int i = 0; i < portletNames.length; i++) {
			if(portletNames[i].equals(portletName))
				return true; // match found
		}
		
		return false;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		portletContext = new PortletContextImpl(getServletContext());

		// Since portlet.xml is processed by the portlet container, it will be
    	// validated any way. So we will not bother with validation here. 
        SAXReader reader = fixSAXReaderSecurity(new SAXReader(false));
        Document doc = null;
        try {
			doc = reader.read(getServletContext().getResourceAsStream("/WEB-INF/portlet.xml"));
		} catch (DocumentException e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new ServletException(e);
		}

		Element root = doc.getRootElement();
		for(Iterator it = root.elementIterator("portlet"); it.hasNext();) {
			Element portletElem = (Element) it.next();
			String portletName = portletElem.elementText("portlet-name");
			
			if(isAdaptedPortlet(portletName)) {			
				String portletClassName = portletElem.elementText("portlet-class");
				Map params = new HashMap();
				for(Iterator it2 = portletElem.elementIterator("init-param"); it2.hasNext();) {
					Element initParamElem = (Element) it2.next();
					String initParamName = initParamElem.elementText("name");
					String initParamValue = initParamElem.elementText("value");
					params.put(initParamName, initParamValue);
				}
				
				Vector mimeTypes = new Vector();
				for(Iterator it3 = portletElem.element("supports").elementIterator("mime-type"); it3.hasNext();) {
					mimeTypes.add(((Element) it3.next()).getText());
				}
				
				String resourceBundle = portletElem.elementText("resource-bundle");
				PortletConfigImpl portletConfig = new PortletConfigImpl(portletName, portletContext, params, resourceBundle);
				try {
					portlets.put(portletName, new PortletInfo(portletName, portletClassName, portletConfig, mimeTypes));
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
					throw new ServletException(e);
				}
			}
		}		
	}

	public static PortletInfo getPortletInfo(String portletName) {
		return (PortletInfo) getInstance().portlets.get(portletName); 
	}
	
	public static PortletContext getPortletContext() {
		return getInstance().portletContext;
	}

	@Override
	public void destroy() throws Exception {
		for(Iterator i = portlets.values().iterator(); i.hasNext();) {
			PortletInfo portletInfo = (PortletInfo) i.next();
			portletInfo.getPortlet().destroy();
		}
	}
	
	/*
	 * Implements a fix for bug#901787 on a newly constructed
	 * SAXReader.
	 */
	private static SAXReader fixSAXReaderSecurity(SAXReader saxReader) {
		try {
			saxReader.setFeature("http://xml.org/sax/features/external-general-entities",   false);
			saxReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		}
		catch (SAXException e) {
			logger.error("fixSAXReaderSecurity( SAXException ):  ", e);
			saxReader = null;
		}
		return saxReader;
	}
}
