package com.sitescape.ef.util;

import com.sitescape.util.ServerDetector;

/**
 * 
 * @author jong
 *
 */
public class JndiObjectFactoryBean extends org.springframework.jndi.JndiObjectFactoryBean {
	
	public void setJndiName(String jndiName) {

		if(ServerDetector.isJBoss()) {
			jndiName = jndiName.replaceFirst("java:comp/env/jdbc", "java:jdbc");
			jndiName = jndiName.replaceFirst("java:comp/env/", "");
		}
		
		super.setJndiName(jndiName);
	}
}
