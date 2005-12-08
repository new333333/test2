package com.sitescape.ef.util;

/**
 * 
 * @author jong
 *
 */
public class JndiObjectFactoryBean extends org.springframework.jndi.JndiObjectFactoryBean {
	
	public void setJndiName(String jndiName) {
		super.setJndiName(PortabilityUtil.getJndiName(jndiName));
	}
}
