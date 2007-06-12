package com.sitescape.util;

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
