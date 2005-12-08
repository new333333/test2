package com.sitescape.ef.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.sitescape.ef.module.workflow.impl.WorkflowFactory;

public class SessionUtil {
	private static SessionFactory sessionFactory=null;
	private static WorkflowFactory workflowFactory=null;
	private static SessionFactory getSessionFactory() {
		if (sessionFactory == null) sessionFactory = (SessionFactory)SpringContextUtil.getBean("sessionFactory");
		return sessionFactory;
	}
	private static WorkflowFactory getWorkflowFactory() {
		if (workflowFactory == null) workflowFactory = (WorkflowFactory)SpringContextUtil.getBean("workflowFactory");
		return workflowFactory;
		
	}
	public static void sessionStartup() {
		//open shared session
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), true);
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));		
	}
	public static void sessionStop() {
	   	if (TransactionSynchronizationManager.hasResource(getSessionFactory())) {
	   	 		SessionHolder sessionHolder =
			(SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
	   	 		SessionFactoryUtils.releaseSession(sessionHolder.getSession(), sessionFactory);
	   	}
	   	getWorkflowFactory().releaseSession();
	}
	public static boolean sessionActive() {
	   	if (TransactionSynchronizationManager.hasResource(getSessionFactory())) return true;
	   	return false;
		
	}
}
