/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.util;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.Document;
import org.dom4j.Element;

import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.SingletonViolationException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.NoWorkspaceByTheNameException;
import org.kablink.teaming.module.zone.ZoneUtil;
import org.kablink.teaming.web.util.BuiltInUsersHelper;

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
 */
@SuppressWarnings({"unchecked", "unused"})
public class SZoneConfig {
	// This is a singleton class. 
	private static SZoneConfig instance; // A singleton instance
	protected DefaultMergeableXmlClassPathConfigFiles configDocs;
	private Document doc;
	private Element root;
	private String defaultZoneName;
	
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

    /*
     * Returns the zone ID of a given zone name.
     * 
     * Note that this may be called BEFORE the zone exists and hence
     * a NoWorkspaceByTheNameException would be thrown.  This method
     * gracefully handles that case.
     */
    private static Long getSafeZoneIdByZoneName(String zoneName) {
    	Long zoneId;
    	try                                          {zoneId = ZoneUtil.getZoneIdByZoneName(zoneName);}
    	catch (NoWorkspaceByTheNameException noName) {zoneId = null;                                  }
    	return zoneId;
    }
    
    
    /* * * * * * * * * * * * * * * * * * */
    /* API's for admin user name access. */
    /* * * * * * * * * * * * * * * * * * */
    
    /**
     * Returns the name to use for the 'admin' user in the given zone.
     * 
     * @param zoneName
     * 
     * @return
     */
    public static String getAdminUserName(String zoneName) {
    	return getInstance()._getAdminUserName(zoneName);
    }
    
    protected String _getAdminUserName(String zoneName) {
    	Long zoneId = getSafeZoneIdByZoneName(zoneName);
    	String reply;
    	if (null == zoneId)
    		 reply = getZoneDefaultAdminUserName(zoneName);
    	else reply = BuiltInUsersHelper.getAdminName(zoneId);
    	return reply;
    }
    
    /**
     * Returns the default name to use for the 'admin' user in the
     * given zone.
     * 
     * @param zoneName
     * 
     * @return
     */
    public static String getZoneDefaultAdminUserName(String zoneName) {
    	return getString(zoneName, "property[@name='adminUser']", ObjectKeys.ADMIN);
    }
    
    
    /* * * * * * * * * * * * * * * * * * */
    /* API's for guest user name access. */
    /* * * * * * * * * * * * * * * * * * */
    
    /**
     * Returns the name to use for the 'Guest' user in the given zone. 
     * 
     * @param zoneName
     * 
     * @return
     */
    public static String getGuestUserName(String zoneName) {
    	return getInstance()._getGuestUserName(zoneName);
    }
    
    protected String _getGuestUserName(String zoneName) {
    	Long zoneId = getSafeZoneIdByZoneName(zoneName);
    	String reply;
    	if (null == zoneId)
    		 reply = getZoneDefaultGuestUserName(zoneName);
    	else reply = BuiltInUsersHelper.getGuestUserName(zoneId);
    	return reply;

    }
    
    /**
     * Returns the default name to use for the 'Guest' user in the
     * given zone.
     * 
     * @param zoneName
     * 
     * @return
     */
    public static String getZoneDefaultGuestUserName(String zoneName) {
    	return getString(zoneName, "property[@name='guestUser']", ObjectKeys.GUEST);
    }
}
