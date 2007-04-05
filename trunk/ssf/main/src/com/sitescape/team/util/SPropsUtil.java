/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.util;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import com.sitescape.team.SingletonViolationException;

/**
 * This class provides unified access to the SSF properties loaded from the 
 * properties file(s). Normally system has one such properties file, i.e., 
 * ssf.properties. But the system allows overriding of certain properties
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
public class SPropsUtil {
	// This is a singleton class. 
	
	private static SPropsUtil instance; // A singleton instance
	
	private Properties props;
	
	public static final String DEBUG_WEB_REQUEST_ENV_PRINT = "debug.web.request.env.print";
	
	public static final String SSF_CTX ="ssf.ctx";
	public static final String SSF_HOST = "ssf.host";
	public static final String SSF_PORT = "ssf.port";
	public static final String SSF_SECURE_PORT = "ssf.secure.port";
	public static final String SSFS_CTX = "ssfs.ctx";
	public static final String WIDEN_ACCESS="entryacl.widens.folderacl";
	public SPropsUtil() {
		if(instance != null)
			throw new SingletonViolationException(SPropsUtil.class);
		
		instance = this;
	}
	
    public void setConfig(PropertiesClassPathConfigFiles config) {
        this.props = config.getProperties();
    }
	
    public static Properties getProperties() {
    	return getInstance().getProps();
    }
    
	public static boolean containsKey(String key) {
		return getInstance().getProps().containsKey(key);
	}
	
	public static String getString(String key) throws ConfigPropertyNotFoundException {
		return getRequired(key);
	}
	
	public static String getString(String key, String defValue) {
		String val = get(key);
		if(val == null)
			return defValue;
		else
			return val;		
	}
	
	public static boolean getBoolean(String key) throws ConfigPropertyNotFoundException {
		String val = getRequired(key);
		
		return Boolean.valueOf(val).booleanValue();
	}
	
	public static boolean getBoolean(String key, boolean defValue) {
		String val = get(key);
		if(val == null)
			return defValue;
		else
			return Boolean.valueOf(val).booleanValue();
	}
	
	public static int getInt(String key) throws ConfigPropertyNotFoundException {
		String val = getRequired(key);
		
		return Integer.parseInt(val);
	}
	
	public static int getInt(String key, int defValue) {
		String val = get(key);
		if(val == null)
			return defValue;
		else
			return Integer.parseInt(val);		
	}
	
	public static String getDirPath(String key) throws ConfigPropertyNotFoundException, IOException {
		String dirPath = new File(getString(key)).getAbsolutePath();
		if(!dirPath.endsWith(File.separator))
			dirPath += File.separator;
		return dirPath;
	}
	
	public static File getFile(String key) throws ConfigPropertyNotFoundException {
		return new File(getString(key));
	}
	
	public static String[] getStringArray(String key, String delim) {
		String val = get(key);
		if(val == null) {
			return new String[0];
		}

		val = val.trim();
		if(val.length() == 0)
			return new String[0];

		String[] vals = val.split(delim);
		for(int i = 0; i < vals.length; i++)
			vals[i] = vals[i].trim();
		
		return vals;
	}
	
	private static String get(String key) {
		return (String) getInstance().getProps().get(key);			
	}
    
	private static String getRequired(String key) {
		String val = get(key);
		
		if(val == null)
			throw new ConfigPropertyNotFoundException();
		else
			return val;
	}
	
    private static SPropsUtil getInstance() {
    	return instance;
    }
    
    private Properties getProps() {
    	return props;
    }
    
	//Property comma-separated lists
	//Routine to get a combined String[] array
	//  This routine combines the factory list and a user custom list (if any)
	static public String[] getCombinedPropertyList(String name, String customPrefix) {
		String[] propertyList = SPropsUtil.getStringArray(name, ",");
		String[] customPropertyList = SPropsUtil.getStringArray(customPrefix + name, ",");
		
		String[] combinedPropertyList = new String[propertyList.length + customPropertyList.length];
		int next = 0;
		for (int i = 0; i < propertyList.length; i++) {
			String propertyValue = propertyList[i].trim();
			if (!propertyValue.equals("")) {
				combinedPropertyList[next] = propertyValue;
				next++;
			}
		}
		for (int i = 0; i < customPropertyList.length; i++) {
			String propertyValue = customPropertyList[i].trim();
			if (!propertyValue.equals("")) {
				combinedPropertyList[next] = propertyValue;
				next++;
			}
		}
		return combinedPropertyList;
	}

}
