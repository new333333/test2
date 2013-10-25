/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
package org.kablink.teaming.gwt.server.LdapBrowser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.ldapbrowser.DirectoryServer;
import org.kablink.teaming.gwt.client.ldapbrowser.LdapObject;
import org.kablink.teaming.gwt.client.ldapbrowser.LdapSearchInfo;
import org.kablink.teaming.gwt.client.ldapbrowser.QueryOutput;
import org.kablink.teaming.gwt.client.rpc.shared.LdapServerDataRpcResponseData;
import org.kablink.teaming.gwt.server.util.GwtLogHelper;
import org.kablink.teaming.gwt.server.util.GwtServerProfiler;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.web.util.MiscUtil;

import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.SizeLimitExceededException;
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapUtils;

/**
 * ?
 * 
 * @author rvasudevan
 */
public final class LdapBrowserHelper {
	protected static Log m_logger = LogFactory.getLog(LdapBrowserHelper.class);

	/**
	 * ?
	 *
	 * @param bs
	 * @param request
	 * @param syncServer
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
    public static String authenticateUser(AllModulesInjected bs, HttpServletRequest request, DirectoryServer syncServer) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "LdapBrowserHelper.authenticateUser()");
		try {
			// Connection information.  Extended the LdapContextSource
			// to handle SSL, right now, we ignore the certificates.
			String  userDn    = syncServer.getSyncUser();
			String  password  = syncServer.getSyncPassword();
			boolean anonymous = ((!(MiscUtil.hasString(userDn))) && (!(MiscUtil.hasString(password))));
			SecureLdapContextSource ldapContextSource = new SecureLdapContextSource(userDn, password, syncServer.getSslEnabled());
			ldapContextSource.setUrl(syncServer.getAddress());
			if (anonymous) {
				ldapContextSource.setAnonymousReadOnly(true);
			}
			else {
				ldapContextSource.setUserDn(userDn);
				ldapContextSource.setPassword(password);
			}
	
			ldapContextSource.afterPropertiesSet();
	
			DirContext ctx = null;
			try {
				if (anonymous)
				     ctx = ldapContextSource.getReadOnlyContext();
				else ctx = ldapContextSource.getContext(userDn, password);
				return null;
			}
			
			catch (Exception e) {
				// Context creation failed - authentication did not succeed
				m_logger.error("LdapBrowserHelper.authenticateUser( Login failed trying to authenticate user:  '" + (anonymous ? "*anonymous*" : userDn) + "' )", e);
				return e.getMessage();
			}
			
			finally {
				if (null != ctx) {
					// It is imperative that the created DirContext
					// instance is always closed.
					LdapUtils.closeContext(ctx);
				}
			}
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"LdapBrowserHelper.authenticateUser( SOURCE EXCEPTION ):  ");
		}
		
		finally {
			gsp.stop();
		}

	}
    
	/**
	 * ?
	 *  
	 * @param bs
	 * @param request
	 * @param directoryServer
	 * @param ldapSearchInfo
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
    public static LdapServerDataRpcResponseData getLdapServerData(AllModulesInjected bs, HttpServletRequest request, DirectoryServer directoryServer, LdapSearchInfo ldapSearchInfo) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "LdapBrowserHelper.getLdapServerData()");
		try {
			// Connection information.  Extended the LdapContextSource
			// to handle SSL, right now, we ignore the certificates.
			String  userDn    = directoryServer.getSyncUser();
			String  password  = directoryServer.getSyncPassword();
			boolean anonymous = ((!(MiscUtil.hasString(userDn))) && (!(MiscUtil.hasString(password))));
			SecureLdapContextSource ldapContextSource = new SecureLdapContextSource(userDn, password, directoryServer.getSslEnabled());
			ldapContextSource.setUrl(directoryServer.getAddress());
			if (anonymous) {
				ldapContextSource.setAnonymousReadOnly(true);
			}
			else {
				ldapContextSource.setUserDn(userDn);
				ldapContextSource.setPassword(password);
			}
	
			ldapContextSource.afterPropertiesSet();
	
			QueryOutput<LdapObject> returnObj = new QueryOutput<LdapObject>();
			List<LdapObject> ldapObjectList = new ArrayList<LdapObject>();
			List<LdapObject> ldapObjects = null;
	
			LdapTemplate template = new LdapTemplate(ldapContextSource);
	
			// Need to set this for Active Directory to ignore few errors
			template.setIgnoreNameNotFoundException( true);
			template.setIgnorePartialResultException(true);
	
			SearchControls searchControls = getSearchControls(ldapSearchInfo); 
			try {
				// Do the search.
				ldapObjects = template.search(
					directoryServer.getUrl(),
					ldapSearchInfo.getSearchObjectClass(),
					searchControls,
					new LDAPObjectMapper());
			}
			
			catch (SizeLimitExceededException exception) {
				returnObj.sizeExceeded(true);
				searchControls.setCountLimit(ldapSearchInfo.getMaximumReturnLimit());
				PagedResultsCookie pagedResultsCookie = null;
				PagedResultsDirContextProcessor control = new PagedResultsDirContextProcessor(ldapSearchInfo.getMaximumReturnLimit(), pagedResultsCookie);
	
				// Do the search
				ldapObjects = template.search(
					directoryServer.getUrl(),
					ldapSearchInfo.getSearchObjectClass(),
					searchControls,
					new LDAPObjectMapper(),
					control);
			}
			
			catch (AuthenticationException ee) {
				// Throw exception.
	            ee.getCause();
			}
	
			if (null != ldapObjects) {
				for (LdapObject obj:  ldapObjects) {
					ldapObjectList.add(obj);
				}
			}
	
			Collections.sort(ldapObjectList);
			returnObj.setResultList(ldapObjectList);
			return new LdapServerDataRpcResponseData(returnObj);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"LdapBrowserHelper.getLdapServerData( SOURCE EXCEPTION ):  ");
		}
		
		finally {
			gsp.stop();
		}
	}

	/*
	 */
	private static SearchControls getSearchControls(LdapSearchInfo info) {
		SearchControls searchControls = new SearchControls();
		if (info.isSearchSubTree())
		     searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		else searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);

		// Speed up the search, we only care for these attributes.
		searchControls.setReturningAttributes(new String[] { "cn", "objectClass", "o", "ou", "dc", "c", "l" });

		// We don't want to read more than 1001 per container
		// Active Directory can return only a max of 1000, so set it to 1001 so that we can get
		// the size limit exception
		searchControls.setCountLimit(info.getMaximumReturnLimit());
		searchControls.setReturningObjFlag(true);

		return searchControls;
	}

	/**
	 * ?
	 * 
	 * @param sslCertDer
	 * 
	 * @return
	 */
	public static X509Certificate getX509Certificate(byte[] sslCertDer) {
		X509Certificate cert = null;
		InputStream     strm = null;

		if (null != sslCertDer) {
			strm = new ByteArrayInputStream(sslCertDer);
		}
		
		if (null != strm) {
			try {
				CertificateFactory factory = CertificateFactory.getInstance("X.509");
				cert = ((X509Certificate) factory.generateCertificate(strm));
			}
			
			catch (CertificateException e) {
				m_logger.debug("LdapBrowserHelper.getX509Certificate( Unable to access certificate byte stream ):  ", e);
			}
			
			finally {
				try {
					strm.close();
				}
				
				catch (IOException ignored) {
				}
			}
		}

		return cert;
	}
}
