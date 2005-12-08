package com.sitescape.ef.util;

import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
/**
 * Wrapper class to allow creation of configuration by spring.
 * @author Janet McCann
 *
 */
public class HibernateConfigurationFactory implements FactoryBean {
	private LocalSessionFactoryBean sessionFactoryBean;
	public void setSessionFactoryBean(LocalSessionFactoryBean sessionFactoryBean) {
		this.sessionFactoryBean = sessionFactoryBean;
	}
	/**
	 * Return the singleton SessionFactory.
	 */
	public Object getObject() {
		return this.sessionFactoryBean.getConfiguration();
	}

	public Class getObjectType() {
		return (this.sessionFactoryBean != null) ? this.sessionFactoryBean.getClass() : Configuration.class;
	}

	public boolean isSingleton() {
		return true;
	}

}
