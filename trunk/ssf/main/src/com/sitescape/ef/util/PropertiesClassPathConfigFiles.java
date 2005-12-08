package com.sitescape.ef.util;

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
