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
package org.kablink.util;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class PropsUtil {
	// This is a singleton class. 
	
	private static PropsUtil instance; // A singleton instance
	
	private Properties props;
	
	public PropsUtil() {
		if(instance != null)
			throw new IllegalStateException("PropsUtil is a singleton class");
		
		instance = this;
	}
	
    public void setProperties(Properties props) {
    	this.props = props;
    }
    
    public static Properties getProperties() {
    	return getInstance().getProps();
    }
    
	public static boolean containsKey(String key) {
		return getInstance().getProps().containsKey(key);
	}
	
	public static String getString(String key) throws PropertyNotFoundException {
		return getRequired(key);
	}
	
	public static String getString(String key, String defValue) {
		String val = get(key);
		if(val == null)
			return defValue;
		else
			return val;		
	}
	
	public static boolean getBoolean(String key) throws PropertyNotFoundException {
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
	
	public static int getInt(String key) throws PropertyNotFoundException {
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
	
	public static long getLong(String key) throws PropertyNotFoundException {
		String val = getRequired(key);
		
		return Long.parseLong(val);
	}
	
	public static long getLong(String key, long defValue) {
		String val = get(key);
		if(val == null)
			return defValue;
		else
			return Long.parseLong(val);		
	}
	
	public static String getDirPath(String key) throws PropertyNotFoundException, IOException {
		String dirPath = new File(getString(key)).getAbsolutePath();
		if(!dirPath.endsWith(File.separator))
			dirPath += File.separator;
		return dirPath;
	}
	
	public static File getFile(String key) throws PropertyNotFoundException {
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
			throw new PropertyNotFoundException("Required key '" + key + "' is not found");
		else
			return val;
	}
	
    private static PropsUtil getInstance() {
    	return instance;
    }
    
    private Properties getProps() {
    	return props;
    }
    
	//Property comma-separated lists
	//Routine to get a combined String[] array
	//  This routine combines the factory list and a user custom list (if any)
	static public String[] getCombinedPropertyList(String name, String customPrefix) {
		String[] propertyList = PropsUtil.getStringArray(name, ",");
		String[] customPropertyList = PropsUtil.getStringArray(customPrefix + name, ",");
		
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
