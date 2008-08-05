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
package com.sitescape.team.module.workflow.impl;



import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

import com.sitescape.team.SingletonViolationException;

public class WorkflowFactory  implements InitializingBean {

    private JbpmConfiguration jbpmConfiguration;

    private SessionFactory hibernateSessionFactory;
    
    private static WorkflowFactory instance;
	
    public WorkflowFactory() {
		if(instance != null)
			throw new SingletonViolationException(WorkflowFactory.class);
		
		instance = this;
	}
    private static WorkflowFactory getInstance() {
    	return instance;
    }
    public JbpmConfiguration getJbpmConfiguration() {
    	return jbpmConfiguration;
    }
    
    public void setHibernateSessionFactory(SessionFactory hibernateSessionFactory) {
        this.hibernateSessionFactory = hibernateSessionFactory;
    }
    public SessionFactory getHibernateSessionFactory() {
    	return hibernateSessionFactory;
    }
	public void afterPropertiesSet() throws Exception {
       if (this.hibernateSessionFactory == null) {
            throw new FatalBeanException("Property [hibernateSessionFactory] of [" + WorkflowFactory.class + "] is required.");
        }

//        jbpmConfiguration = JbpmConfiguration.getInstance(configFile);
       //the config file is in the classpath.  This is needed because other jbpm classes load the configuration before
       //we can get to it.  BusinessCalendar is one.  They expect the config file on the class path.
       jbpmConfiguration = JbpmConfiguration.getInstance();
    }

	/**
	 * Use the existing session in all JBPM operations.
	 * @return
	 */
	public static JbpmContext getContext() {
   	   
   	   	Session hSession = SessionFactoryUtils.getSession(getInstance().getHibernateSessionFactory(), false);
   	   	JbpmContext jContext = getInstance().getJbpmConfiguration().createJbpmContext();
   	   	jContext.setSession(hSession);
        return jContext;
    }
}

