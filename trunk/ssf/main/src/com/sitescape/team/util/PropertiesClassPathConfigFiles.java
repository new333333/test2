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

import java.util.Properties;

import org.springframework.beans.factory.InitializingBean;

public class PropertiesClassPathConfigFiles extends ClassPathConfigFiles
	implements InitializingBean {
	
	private Properties props;
	
    public void afterPropertiesSet() throws Exception {
    	int size = size();
    	Properties tempProps = null;
        for(int i = 0; i < size; i++) {
        	tempProps = new Properties();
        	tempProps.load(getAsInputStream(i));
        	if(props == null)
        		props = tempProps;
        	else
        		props.putAll(tempProps);
        }
    }

    public Properties getProperties() {
    	return props;
    }
}
