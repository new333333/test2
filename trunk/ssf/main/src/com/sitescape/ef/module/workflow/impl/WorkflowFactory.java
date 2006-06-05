package com.sitescape.ef.module.workflow.impl;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jbpm.db.JbpmSession;
import org.jbpm.db.JbpmSessionFactory;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.transaction.support.ResourceHolderSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.sitescape.ef.SingletonViolationException;

public class WorkflowFactory  implements InitializingBean {

    private JbpmSessionFactory sessionFactory;

    private SessionFactory hibernateSessionFactory;

    private Configuration hibernateConfiguration;
    private static WorkflowFactory instance;
	
    public WorkflowFactory() {
		if(instance != null)
			throw new SingletonViolationException(WorkflowFactory.class);
		
		instance = this;
	}
    private static WorkflowFactory getInstance() {
    	return instance;
    }
    public JbpmSessionFactory getJbpmSessionFactory() {
    	return sessionFactory;
    }
    
    public void setHibernateSessionFactory(SessionFactory hibernateSessionFactory) {
        this.hibernateSessionFactory = hibernateSessionFactory;
    }
    public SessionFactory getHibernateSessionFactory() {
    	return hibernateSessionFactory;
    }
    public void setHibernateConfiguration(Configuration hibernateConfiguration) {
        this.hibernateConfiguration = hibernateConfiguration;
    }


	
	public void afterPropertiesSet() throws Exception {
        if (this.hibernateConfiguration == null) {
            throw new FatalBeanException("Property [hibernateConfiguration] of [" + WorkflowFactory.class + "] is required.");
        }

        if (this.hibernateSessionFactory == null) {
            throw new FatalBeanException("Property [hibernateSessionFactory] of [" + WorkflowFactory.class + "] is required.");
        }

        this.sessionFactory = new JbpmSessionFactory(this.hibernateConfiguration, this.hibernateSessionFactory);
    }

    //create session and bind to thread
    public static JbpmSession getSession() {
   	   JbpmSessionHolder jbpmSessionHolder = (JbpmSessionHolder) TransactionSynchronizationManager.getResource(getInstance().getJbpmSessionFactory());
       if (jbpmSessionHolder != null && jbpmSessionHolder.getJbpmSession() != null) {
           return jbpmSessionHolder.getJbpmSession();
       }

   	   
   	   Session hSession = SessionFactoryUtils.getSession(getInstance().getHibernateSessionFactory(), true);
       JbpmSession jSession = new JbpmSession(getInstance().getJbpmSessionFactory(), hSession);
       jbpmSessionHolder = new JbpmSessionHolder(jSession);

        TransactionSynchronizationManager.bindResource(getInstance().getJbpmSessionFactory(), jbpmSessionHolder);
        return jSession;
    }
    //release session - this will cause the hibernate session to be closed if not already
    //closed
    public static void releaseSession() {
    	if (TransactionSynchronizationManager.hasResource(getInstance().getJbpmSessionFactory())) {
    		JbpmSessionHolder jbpmSessionHolder =
    			(JbpmSessionHolder) TransactionSynchronizationManager.unbindResource(getInstance().getJbpmSessionFactory());
    		jbpmSessionHolder.getJbpmSession().close();
    		jbpmSessionHolder.clear();
    	}

    }
    public static void release() {
    	if (TransactionSynchronizationManager.hasResource(getInstance().getJbpmSessionFactory())) {
    		JbpmSessionHolder jbpmSessionHolder =
    			(JbpmSessionHolder) TransactionSynchronizationManager.unbindResource(getInstance().getJbpmSessionFactory());
    		jbpmSessionHolder.clear();
    	}

    }
    private static class JbpmSessionHolder extends ResourceHolderSupport {

        private JbpmSession jbpmSession;

        public JbpmSessionHolder(JbpmSession jbpmSession) {
            this.jbpmSession = jbpmSession;
        }

        public JbpmSession getJbpmSession() {
            return this.jbpmSession;
        }

        public void clear() {
            this.jbpmSession = null;
        }
    }
	public static JbpmContext getContext() {
		return new JbpmContext(getSession());
	}
}

/**
 * save for upgrade to jbpm3.1 
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

import com.sitescape.ef.SingletonViolationException;

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
	 *
	public static JbpmContext getContext() {
   	   
   	   	Session hSession = SessionFactoryUtils.getSession(getInstance().getHibernateSessionFactory(), false);
   	   	JbpmContext jContext = getInstance().getJbpmConfiguration().createJbpmContext();
   	   	jContext.setSession(hSession);
        return jContext;
    }
}
**/
