/**
 * Copyright (c) 2000-2005 Liferay, LLC. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.kablink.util.dao.hibernate;


import java.sql.Connection;
import java.sql.SQLException;

import java.util.LinkedList;
import java.util.Properties;

import javax.naming.InitialContext;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Environment;
import org.hibernate.connection.ConnectionProvider;
import org.kablink.util.JNDIUtil;

/**
 * <a href="DSConnectionProvider.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Brian Wing Shun Chan
 * @version $Revision: 1.5 $
 *
 */
public class DSConnectionProvider implements ConnectionProvider {

	private Log logger = LogFactory.getLog(getClass());
	
	private static LinkedList<WrappedConnection> borrowedConnections = null;
	
	public void configure(Properties props) throws HibernateException {
		String location = props.getProperty(Environment.DATASOURCE);

		try {
			_ds = (DataSource)JNDIUtil.lookup(new InitialContext(), location);
		}
		catch (Exception e) {
			throw new HibernateException(e.getLocalizedMessage());
		}
		
		String str = System.getProperty("ds.connection.debug.enabled");
		if("true".equals(str))
			borrowedConnections = new LinkedList<WrappedConnection>();
	}

	public Connection getConnection() throws SQLException {
		if(logger.isDebugEnabled())
			logger.debug("Borrowing connection");
		Connection conn = _ds.getConnection();
		if(logger.isDebugEnabled())
			logger.debug("Connection borrowed");
		return addWrappedConnection(conn);
	}

	public void closeConnection(Connection con) throws SQLException {
		if(logger.isDebugEnabled())
			logger.debug("Returning connection");
		con.close();
		if(logger.isDebugEnabled())
			logger.debug("Connection returned");
		removeWrappedConnection(con);
	}

	public boolean isStatementCache() {
		return false;
	}

	public void close() {
	}
	public boolean supportsAggressiveRelease() {
		return false;
	}
	private DataSource _ds;

	private Connection addWrappedConnection(Connection conn) {
		if(borrowedConnections != null) {
			WrappedConnection wc = new WrappedConnection(conn);
			synchronized(borrowedConnections) {
				borrowedConnections.add(wc);
			}
			return wc;
		}
		else {
			return conn;
		}
	}
	
	private void removeWrappedConnection(Connection conn) {
		if(borrowedConnections != null) {		
			synchronized(borrowedConnections) {
				borrowedConnections.remove(conn);
			}
		}
	}
	
	public static String debugInfoAsString() {
		if(borrowedConnections != null) {
			StringBuilder sb = new StringBuilder();
			synchronized(borrowedConnections) {
				sb.append("Number of borrowed connections: " + borrowedConnections.size()).append("\n\n");
				int i = 1;
				for(WrappedConnection wc:borrowedConnections) {
					sb.append("[Connection ").append(i++).append("]\n");
					wc.asString(sb);
					sb.append("\n");
				}
			}
			return sb.toString();
		}
		else {
			return "To enable DS connection debug info, add ds.connection.debug.enabled=true in system-ext.properties.\nThis adds SERIOUS overhead, so should not be used in a production mode.";
		}
	}
	
}