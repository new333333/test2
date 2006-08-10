package com.sitescape.ef.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Interceptor;
import org.hibernate.engine.SessionImplementor;

import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
/**
 * Helper class to setup hibernate.  Simulates openSessionInView for
 * non-web users, ie) scheduled jobs.
 * @author Janet McCann
 *
 */
public class SessionUtil {
	private static SessionFactory sessionFactory=null;
	private static SessionFactory getSessionFactory() {
		if (sessionFactory == null) sessionFactory = (SessionFactory)SpringContextUtil.getBean("sessionFactory");
		return sessionFactory;
	}
	public static Session getSession() {
		return SessionFactoryUtils.getSession(getSessionFactory(), false);
	}
	public static void sessionStartup() {
		//open shared session
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), true);
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));		
	}
	public static void sessionStartup(Interceptor interceptor) {
		//open shared session
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), interceptor, null);
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));		
	}
	public static Interceptor getInterceptor() {
		Session session = getSession();
		if (session == null) return null;
		if (session instanceof SessionImplementor) {
			SessionImplementor sI = (SessionImplementor) session;
			return sI.getInterceptor();
		}
		return null;
	}
	public static void sessionStop() {
	   	if (TransactionSynchronizationManager.hasResource(getSessionFactory())) {
	   	 		SessionHolder sessionHolder =
			(SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
	   	 		SessionFactoryUtils.releaseSession(sessionHolder.getSession(), sessionFactory);
	   	}
	}
	public static boolean sessionActive() {
	   	if (TransactionSynchronizationManager.hasResource(getSessionFactory())) return true;
	   	return false;
		
	}
}
