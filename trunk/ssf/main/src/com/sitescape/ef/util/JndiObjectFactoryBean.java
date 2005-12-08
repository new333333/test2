package com.sitescape.ef.util;

import com.sitescape.util.ServerDetector;

public class JndiObjectFactoryBean extends org.springframework.jndi.JndiObjectFactoryBean {
	
	public void setJndiName(String jndiName) {

		if(ServerDetector.isJBoss()) {
			jndiName = jndiName.replaceFirst("comp/env/", "");
		}
		
		super.setJndiName(jndiName);
	}
}
