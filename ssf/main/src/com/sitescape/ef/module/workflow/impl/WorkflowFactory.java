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

public class WorkflowFactory  implements InitializingBean {

    private JbpmSessionFactory sessionFactory;

    private SessionFactory hibernateSessionFactory;

    private Configuration hibernateConfiguration;

    public void setHibernateSessionFactory(SessionFactory hibernateSessionFactory) {
        this.hibernateSessionFactory = hibernateSessionFactory;
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
    public JbpmSession getSession() {
   	   JbpmSessionHolder jbpmSessionHolder = (JbpmSessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
       if (jbpmSessionHolder != null && jbpmSessionHolder.getJbpmSession() != null) {
           return jbpmSessionHolder.getJbpmSession();
       }

   	   
   	   Session hSession = SessionFactoryUtils.getSession(this.hibernateSessionFactory, true);
       JbpmSession jSession = new JbpmSession(sessionFactory, hSession);
       jbpmSessionHolder = new JbpmSessionHolder(jSession);

        TransactionSynchronizationManager.bindResource(sessionFactory, jbpmSessionHolder);
        return jSession;
    }
    //release session - this will cause the hibernate session to be closed if not already
    //closed
    public void releaseSession() {
    	if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
    		JbpmSessionHolder jbpmSessionHolder =
    			(JbpmSessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
    		jbpmSessionHolder.getJbpmSession().close();
    		jbpmSessionHolder.clear();
    	}

    }
    private class JbpmSessionHolder extends ResourceHolderSupport {

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
}

