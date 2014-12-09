/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.dao;

import java.lang.invoke.MethodHandles;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.kablink.teaming.util.SpringContextUtil;

/**
 * Helper class that aids with implementing data access code using short-lived and plain 
 * Hibernate session or stateless session.
 * 
 * When executed in this manner, it does NOT use the pre-bound (thread-bound) Hibernate
 * session, and its ramification must be clearly understood prior to using this class. 
 * If unsure about the distinction, use existing Dao classes such as CoreDao.
 * 
 * @author Jong
 *
 */
public class PlainHibernateTemplate {

	private static Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	public static <T> T execute(SessionCallback<T> action) throws HibernateException, SQLException {
		Session session = getSessionFactory().openSession();
		try {
			return action.doInHibernate(session);
		}
		finally {
			closeSession(session);
		}
	}
	
	public static <T> T execute(StatelessSessionCallback<T> action) throws HibernateException, SQLException {
		StatelessSession session = getSessionFactory().openStatelessSession();
		try {
			return action.doInHibernate(session);
		}
		finally {
			closeSession(session);
		}		
	}
	
	private static void closeSession(Session session) {
		if (session != null) {
			logger.debug("Closing Hibernate session");
			try {
				session.close();
			}
			catch (HibernateException ex) {
				logger.warn("Could not close Hibernate session", ex);
			}
			catch (Throwable ex) {
				logger.warn("Unexpected exception on closing Hibernate session", ex);
			}
		}
	}
	
	private static void closeSession(StatelessSession session) {
		if (session != null) {
			logger.debug("Closing Hibernate stateless session");
			try {
				session.close();
			}
			catch (HibernateException ex) {
				logger.warn("Could not close Hibernate stateless session", ex);
			}
			catch (Throwable ex) {
				logger.warn("Unexpected exception on closing Hibernate stateless session", ex);
			}
		}
	}
	
	private static SessionFactory getSessionFactory() {
		return (SessionFactory) SpringContextUtil.getBean("sessionFactory");
	}
}
