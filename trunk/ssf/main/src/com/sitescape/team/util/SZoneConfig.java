package com.sitescape.team.util;

import java.util.List;
import java.util.ArrayList;

import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.ef.SingletonViolationException;
import com.sitescape.team.context.request.RequestContextHolder;

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
	
	
	public static List getElements(String key) {
		
		return getElements(RequestContextHolder.getRequestContext().getZoneName(), key);
	}
	public static Element getElement(String key) {
		return getElement(RequestContextHolder.getRequestContext().getZoneName(), key);
	}

	public static String getString(String key) throws ConfigPropertyNotFoundException {
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
		String value = null;
		Element element = (Element)root.selectSingleNode("/zoneConfiguration/zone[@name='" + zoneName + "']/" + key);
		if (element == null)
			element = (Element)root.selectSingleNode("/zoneConfiguration/" + key);
		if (element == null) return null;
		return element.getTextTrim();
		
	}
	public static List getElements(String zoneName, String key) {
		Element root = getInstance().root;			
		List results = root.selectNodes("/zoneConfiguration/zone[@name='" + zoneName + "']/" + key);
		if ((results == null) || results.isEmpty()) {
			results = root.selectNodes("/zoneConfiguration/" + key);	
		}
		if (results == null) results = new ArrayList();
		return results;
	}
	public static Element getElement(String zoneName, String key) {
		Element root = getInstance().root;			
		Element result = (Element)root.selectSingleNode("/zoneConfiguration/zone[@name='" + zoneName + "']/" + key);
		if (result == null) {
			result = (Element)root.selectSingleNode("/zoneConfiguration/" + key);	
		}
		return result;
	}
	public static String getDefaultZoneName() {
		return getInstance().defaultZoneName;
	}
	private static String getRequired(String zoneName, String key) {
		String val = get(zoneName, key);
		
		if(val == null)
			throw new ConfigPropertyNotFoundException();
		else
			return val;
	}
	
    private static SZoneConfig getInstance() {
    	return instance;
    }
    
}
