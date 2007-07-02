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
