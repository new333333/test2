package com.sitescape.ef.util;

import java.io.IOException;
import java.util.Properties;

public class PropertyPlaceholderConfigurer 
	extends org.springframework.beans.factory.config.PropertyPlaceholderConfigurer {

	/**
	 * Overrides super class's method so that it obtains properties from
	 * the ssf properties file(s) accessed through SPropsUtil class.
	 * This effectively means that the properties files (if any) specified by
	 * <code>setLocation</code> or <code>setLocations</code> method is ignored.
	 */
	protected Properties mergeProperties() throws IOException {
		return SPropsUtil.getProperties();
	}
}
