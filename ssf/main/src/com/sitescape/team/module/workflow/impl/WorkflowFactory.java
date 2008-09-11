/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
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

