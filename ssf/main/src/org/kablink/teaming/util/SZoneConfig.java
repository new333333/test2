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
package org.kablink.teaming.util;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.Document;
import org.dom4j.Element;
import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.SingletonViolationException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.util.Validator;


/**
 * This class provides unified access to the SSF zone properties loaded from the 
 * zone xml file(s). Normally system has one such config file, i.e., 
 * zone.cfg.xml. But the system allows overriding of certain properties
 * without modifying the factory-shipped config file by creating additional
 * properties file and adding it to the appropriate list in the application
 * context config file (i.e., applicationContext.xml). This makes it easy
 * to upgrade system when it is expected that there will be large amount of
 * customizations performed. In fact, the system allows for any number of
 * such properties files to be chained.
 * 
 * @author jong
 *
 */
public class SZoneConfig {
	// This is a singleton class. 
	private static SZoneConfig instance; // A singleton instance
	protected DefaultMergeableXmlClassPathConfigFiles configDocs;
	private Document doc;
	private Element root;
	private String defaultZoneName;
	// Caches super user names for each zone for faster access
	private ConcurrentHashMap<String,String> adminUsers = new ConcurrentHashMap<String,String>();
	// Caches default user names for each zone for faster access
	private ConcurrentHashMap<String,String> guestUsers = new ConcurrentHashMap<String,String>();
	
	public SZoneConfig() {
		if(instance != null)
			throw new SingletonViolationException(SZoneConfig.class);
		
		instance = this;
	}
	
	public void setConfigDocs(DefaultMergeableXmlClassPathConfigFiles configDocs) {
		this.configDocs = configDocs;
		this.doc = configDocs.getAsMergedDom4jDocument();
		this.root = doc.getRootElement();
		
		// For performance reason, we cache the following value.
		Element elm = (Element)root.selectSingleNode("/zoneConfiguration/defaultZone");	

		defaultZoneName = elm.attributeValue("name");
	}
	public DefaultMergeableXmlClassPathConfigFiles getConfigDocs() {
		return configDocs;
	}
	
	
	public static List<Element> getElements(String key) {
		
		return getElements(RequestContextHolder.getRequestContext().getZoneName(), key);
	}
	public static Element getElement(String key) {
		return getElement(RequestContextHolder.getRequestContext().getZoneName(), key);
	}

	public static String getString(String key) {
		return get(RequestContextHolder.getRequestContext().getZoneName(), key);
	}
	public static String getString(String zoneName, String key) {
		return get(zoneName, key);
	}
	public static String getString(String zoneName, String key, String defValue) {
		String val = get(zoneName, key);
		if(val == null)
			return defValue;
		else
			return val;		
	}
	
	private static String get(String zoneName, String key) {
		Element root = getInstance().root;			
		Element element;
		element = (Element)root.selectSingleNode("/zoneConfiguration/zone[@name='" + zoneName + "']/" + key);
		if (element == null)
			element = (Element)root.selectSingleNode("/zoneConfiguration/" + key);
		if (element == null) return null;
		return element.getTextTrim();
		
	}
	public static List<Element> getAllElements(String key) {
		List<Element> results = getInstance().root.selectNodes(key);
		if (results == null) results = new ArrayList();
		return results;
	}
	public static List<Element> getElements(String zoneName, String key) {
		Element root = getInstance().root;			
		List<Element> results;
		results = root.selectNodes("/zoneConfiguration/zone[@name='" + zoneName + "']/" + key);
		if ((results == null) || results.isEmpty()) {
			results = root.selectNodes("/zoneConfiguration/" + key);	
		}
		if (results == null) results = new ArrayList();
		return results;
	}
	public static Element getElement(String zoneName, String key) {
		Element root = getInstance().root;			
		Element result;
		result = (Element)root.selectSingleNode("/zoneConfiguration/zone[@name='" + zoneName + "']/" + key);
		if (result == null) {
			result = (Element)root.selectSingleNode("/zoneConfiguration/" + key);	
		}
		return result;
	}
	public static String getDefaultZoneName() {
		return getInstance().defaultZoneName.toLowerCase();
	}
	private static String getRequired(String zoneName, String key) {
		String val = get(zoneName, key);
		
		if(val == null)
			throw new ConfigurationException("Property " + key + " is not found for zone " + zoneName);
		else
			return val;
	}
	
    private static SZoneConfig getInstance() {
    	return instance;
    }
    
    public static String getAdminUserName(String zoneName) {
    	return getInstance()._getAdminUserName(zoneName);
    }
    
    public static String getGuestUserName(String zoneName) {
    	return getInstance()._getGuestUserName(zoneName);
    }
    public static Object getObject(String zoneName, String key, Class defaultClass) {
		String className = getString(RequestContextHolder.getRequestContext().getZoneName(), key);
		try {
			Class processorClass = defaultClass;
			if (Validator.isNotNull(className)) processorClass = ReflectHelper.classForName(className);
			return processorClass.newInstance();
		} catch (Exception e) {
			   throw new ConfigurationException("Cannot instantiate class name '" + className + "'",
					e);
		} 

    }
    protected String _getAdminUserName(String zoneName) {
    	String adminUser = adminUsers.get(zoneName);
    	if(adminUser == null) {
    		adminUser = SZoneConfig.getString(zoneName, "property[@name='adminUser']", "admin");
    		adminUsers.put(zoneName, adminUser);
    	}
    	return adminUser;
    }
    
    protected String _getGuestUserName(String zoneName) {
    	String guestUser = guestUsers.get(zoneName);
    	if(guestUser == null) {
    		guestUser = SZoneConfig.getString(zoneName, "property[@name='guestUser']", "guest");
    		guestUsers.put(zoneName, guestUser);
    	}
    	return guestUser;
    }
}
