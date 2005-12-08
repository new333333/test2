package com.sitescape.ef.util;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import com.sitescape.ef.SingletonViolationException;

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
	
	public SPropsUtil() {
		if(instance != null)
			throw new SingletonViolationException(SPropsUtil.class);
		
		instance = this;
	}
	
    public void setConfig(PropertiesClassPathConfigFiles config) {
        this.props = config.getProperties();
    }
	
	public static boolean containsKey(String key) {
		return getInstance().getProperties().containsKey(key);
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
	
	private static String get(String key) {
		return (String) getInstance().getProperties().get(key);			
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
    
    private Properties getProperties() {
    	return props;
    }
}
