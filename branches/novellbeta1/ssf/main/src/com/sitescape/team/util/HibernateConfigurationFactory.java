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
