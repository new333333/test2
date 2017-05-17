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

import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.StatelessSession;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateAccessor;

/**
 * Helper class that simplifies Hibernate data access code that uses Hibernate 
 * stateless session. 
 * 
 * We provide this template ourselves because Spring's HibernateTemplate class
 * does not support stateless session.
 * 
 * In addition, this template is designed to be used directly by the application
 * code rather than used indirectly by a DAO implementation. This is based on
 * the observation that only the application code performing bulk operations
 * or extended operations should consider using this template and their data 
 * access logic tends to require much greater control over the various aspects
 * of the interactions with the database (such as transactions) than typical
 * DAO interface/implementation can provide.
 * 
 * In most cases, application code must NOT use this template since careless
 * use of this template can result in incorrect or unpredictable behavior 
 * due to vulnerability to data aliasing effects.
 * 
 * @author Jong
 *
 */
public class StatelessSessionTemplate extends HibernateAccessor {

	protected Log logger = LogFactory.getLog(getClass());

	public interface Callback<T> {
		T doInHibernate(StatelessSession session) throws HibernateException, SQLException;
	}
	
	public <T> T execute(Callback<T> action) throws DataAccessException {
		StatelessSession session = getSessionFactory().openStatelessSession();
		try {
			return action.doInHibernate(session);
		}
		catch (HibernateException ex) {
			throw convertHibernateAccessException(ex);
		}
		catch (SQLException ex) {
			throw convertJdbcAccessException(ex);
		}
		catch (RuntimeException ex) {
			// Callback code threw application exception...
			throw ex;
		}
		finally {
			closeSession(session);
		}		
	}
	
	private void closeSession(StatelessSession session) {
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
	
}
