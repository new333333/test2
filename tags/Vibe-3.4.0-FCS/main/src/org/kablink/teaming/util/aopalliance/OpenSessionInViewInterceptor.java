/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.util.aopalliance;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.kablink.teaming.InternalException;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateAccessor;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 * This class implements functionality effectively identical to 
 * org.springframework.orm.hibernate3.support.OpenSessionInViewInterceptor.
 * The primary and only difference is that this class is a general AOP 
 * interceptor while the other is a web handler interceptor specific to web
 * tier. 
 * 
 * @author jong
 *
 */
public class OpenSessionInViewInterceptor extends HibernateAccessor 
implements MethodInterceptor {

	private boolean singleSession = true;

	/**
	 * Create a new OpenSessionInViewInterceptor,
	 * turning the default flushMode to FLUSH_NEVER.
	 * @see #setFlushMode
	 */
	public OpenSessionInViewInterceptor() {
		setFlushMode(FLUSH_NEVER);
	}

	/**
	 * Set whether to use a single session for each request. Default is true.
	 * <p>If set to false, each data access operation or transaction will use
	 * its own session (like without Open Session in View). Each of those
	 * sessions will be registered for deferred close, though, actually
	 * processed at request completion.
	 * @see SessionFactoryUtils#initDeferredClose
	 * @see SessionFactoryUtils#processDeferredClose
	 */
	public void setSingleSession(boolean singleSession) {
		this.singleSession = singleSession;
	}

	/**
	 * Return whether to use a single session for each request.
	 */
	protected boolean isSingleSession() {
		return singleSession;
	}

	public Object invoke(MethodInvocation invocation) throws Throwable {
		preInvoke();
		try {
			Object obj = invocation.proceed();
			postInvoke();
			return obj;
		}
		finally {
			afterCompletion();
		}
	}

	/**
	 * Open a new Hibernate Session according to the settings of this HibernateAccessor
	 * and binds in to the thread via TransactionSynchronizationManager.
	 * @see org.springframework.orm.hibernate3.SessionFactoryUtils#getSession
	 * @see org.springframework.transaction.support.TransactionSynchronizationManager
	 */
	protected void preInvoke() throws DataAccessException {

		if(TransactionSynchronizationManager.hasResource(getSessionFactory()))
			throw new InternalException();
		
		if(SessionFactoryUtils.isDeferredCloseActive(getSessionFactory()))
			throw new InternalException();
		
		if (isSingleSession()) {
			// single session mode
			logger.debug("Opening single Hibernate Session in OpenSessionInViewInterceptor");
			Session session = SessionFactoryUtils.getSession(
					getSessionFactory(), getEntityInterceptor(), getJdbcExceptionTranslator());
			if (getFlushMode() == FLUSH_NEVER) {
				session.setFlushMode(FlushMode.NEVER);
			}
			TransactionSynchronizationManager.bindResource(getSessionFactory(), new SessionHolder(session));
		}
		else {
			// deferred close mode
			SessionFactoryUtils.initDeferredClose(getSessionFactory());
		}
	}

	/**
	 * Flush the Hibernate Session before view rendering, if necessary.
	 * Note that this just applies in single session mode!
	 * <p>The default is FLUSH_NEVER to avoid this extra flushing, assuming that
	 * middle tier transactions have flushed their changes on commit.
	 * This method is called only if the invocation of the target method was
	 * successfu, that is, returned without an exception. 
	 * @see #setFlushMode
	 */
	protected void postInvoke() throws DataAccessException {

		if (isSingleSession()) {
			// only potentially flush in single session mode
			SessionHolder sessionHolder =
					(SessionHolder) TransactionSynchronizationManager.getResource(getSessionFactory());
			logger.debug("Flushing single Hibernate Session in OpenSessionInViewInterceptor");
			try {
				flushIfNecessary(sessionHolder.getSession(), false);
			}
			catch (HibernateException ex) {
				throw convertHibernateAccessException(ex);
			}
		}
	}

	/**
	 * Unbind the Hibernate Session from the thread and closes it (in single session
	 * mode), or process deferred close for all sessions that have been opened
	 * during the current request (in deferred close mode).
	 * This method is guaranteed to be called as long as <code>preInvoke</code> 
	 * ran successfully, that is, returned without an exception.
	 * @see org.springframework.orm.hibernate3.SessionFactoryUtils#releaseSession
	 * @see org.springframework.transaction.support.TransactionSynchronizationManager
	 */
	protected void afterCompletion() throws DataAccessException {
		if (isSingleSession()) {
			// single session mode
			SessionHolder sessionHolder =
					(SessionHolder) TransactionSynchronizationManager.unbindResource(getSessionFactory());
			logger.debug("Closing single Hibernate Session in OpenSessionInViewInterceptor");
			SessionFactoryUtils.releaseSession(sessionHolder.getSession(), getSessionFactory());
		}
		else {
			// deferred close mode
			SessionFactoryUtils.processDeferredClose(getSessionFactory());
		}
		
	}

}
