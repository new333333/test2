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
import java.net.MalformedURLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.ldapbrowser.DirectoryServer;
import org.kablink.teaming.gwt.client.ldapbrowser.LdapObject;
import org.kablink.teaming.gwt.client.ldapbrowser.LdapSearchInfo;
import org.kablink.teaming.gwt.client.ldapbrowser.QueryOutput;
import org.kablink.teaming.gwt.client.rpc.shared.LdapServerDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.server.util.GwtLogHelper;
import org.kablink.teaming.gwt.server.util.GwtServerProfiler;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.SpringContextUtil;
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
	
	private static HashMap<String, String> DEFAULT_PROPERTIES = new HashMap<String, String>();
	static {
		DEFAULT_PROPERTIES.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		DEFAULT_PROPERTIES.put(Context.SECURITY_AUTHENTICATION, "simple"                          );
	}
	
	/**
	 * Authenticates to the given LDAP server.  If successful, returns
	 * a null string.  If unsuccessful, a localized error message is
	 * returned.
	 * 
	 * Primary usage:  To validate an LDAP connection?
	 *
	 * @param bs
	 * @param request
	 * @param ds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
    public static StringRpcResponseData authenticateUser(AllModulesInjected bs, HttpServletRequest request, DirectoryServer ds) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "LdapBrowserHelper.authenticateUser()");
		try {
			// Connection information.  Extended the LdapContextSource
			// to handle SSL, right now, we ignore the certificates.
			String  userDn    = ds.getSyncUser();
			String  password  = ds.getSyncPassword();
			boolean anonymous = ((!(MiscUtil.hasString(userDn))) && (!(MiscUtil.hasString(password))));
			SecureLdapContextSource ldapContextSource = new SecureLdapContextSource(userDn, password, ds.getSslEnabled());
			ldapContextSource.setUrl(ds.getAddress());
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
				return new StringRpcResponseData();
			}
			
			catch (Exception e) {
				// Context creation failed - authentication did not
				// succeed.
				m_logger.error("LdapBrowserHelper.authenticateUser( Login failed trying to authenticate user:  '" + (anonymous ? "*anonymous*" : userDn) + "' )", e);
				return new StringRpcResponseData(getLdapBrowserErrorMessage(e, ds, anonymous));
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

	/*
	 * Returns a CoreDao bean.
	 */
	private static CoreDao getCoreDao() {
		return ((CoreDao) SpringContextUtil.getBean("coreDao"));
	}
	
	/*
	 * Read the 'defaultNamingContext attribute from the rootDSE object
	 * in AD.
	 * 
	 * The value of the attribute will be in the format:
	 *      dc=aaa,dc=bbb,dc=ccc,dc=com
	 */
	@SuppressWarnings("unchecked")
	private static String getDomainNameFromAD(DirectoryServer ds) {
		// If it isn't active directory...
		if (!(ds.isActiveDirectory())) {
			// ...return an empty string.
			return "";
		}
		
		LdapContext ctx        = null;
        String      domainName = null;

		try {
	        SearchControls controls = new SearchControls();
	        controls.setSearchScope(SearchControls.OBJECT_SCOPE);
	        
	        ctx = getLdapContext(RequestContextHolder.getRequestContext().getZone().getId(), ds);
	        NamingEnumeration answer = ctx.search("", "(objectclass=*)", controls);
	
	        if (hasMore(answer)) {
	        	SearchResult sr = ((SearchResult) answer.next());
	        	if (null != sr) {
		        	Attributes attrs = sr.getAttributes();
	        		if (null != attrs) {
	        			Attribute attrib = attrs.get("defaultNamingContext");
	        			if (null != attrib) {
		        			Object value = attrib.get();
		        			if ((null != value) && (value instanceof String)) {
		        				domainName = ((String) value);
		        			}
	        			}
	        		}
	        	}
	        }
		}
		
		catch (Exception ex) {
			m_logger.error("LdapBrowserHelper.getDomainNameFromAD() caught exception: " + ex.toString(), ex);
		}
		
		finally {
	        if (null !=  ctx) {
				try {
					ctx.close();
					ctx = null;
				}
				catch (NamingException ex) {}	// Ignore.
			}
		}
		
		return ((null == domainName) ? "" : domainName);
	}
	
    /*
     * Maps an exception to an appropriate error message.
     */
    private static String getLdapBrowserErrorMessage(Throwable t, DirectoryServer ds, boolean anonymous) {
    	String   msgKey;
    	String[] msgPatches;
		if (t instanceof AuthenticationException) {
			msgKey     = "ldapBrowserError.authenticationException";
			msgPatches = null;
		}
		
		else {
			Throwable rootCause = getRootCause(t);
			if (rootCause instanceof MalformedURLException) {
				msgKey     = "ldapBrowserError.malformedUrlException";
				msgPatches = new String[]{ds.getAddress()};
			}
			
			else {
				if (t == rootCause) {
					msgKey     = "ldapBrowserError.otherException1";
					msgPatches = new String[]{t.getLocalizedMessage()};
				}
				else if (anonymous && isRootCauseAnonymousNotSupportedException(rootCause)) {
					if (ds.isSslEnabled())
					     msgKey = "ldapBrowserError.anonymousNotSupported.ssl";
					else msgKey = "ldapBrowserError.anonymousNotSupported";
					msgPatches  = new String[]{ds.getAddress()};
				}
				else {
					msgKey     = "ldapBrowserError.otherException2";
					msgPatches = new String[]{t.getLocalizedMessage(), rootCause.getLocalizedMessage()};
				}
			}
		}
		
		return ((null == msgPatches) ? NLT.get(msgKey) : NLT.get(msgKey, msgPatches));
    }
    
	/*
	 * Returns an LdapContext for an authentication into an LDAP
	 * server.
	 */
	@SuppressWarnings("unchecked")
	private static LdapContext getLdapContext(Long zoneId, DirectoryServer ds) throws NamingException {
		// Load user from LDAP.
		String  userDn    = ds.getSyncUser();
		String  password  = ds.getSyncPassword();
		boolean anonymous = ((!(MiscUtil.hasString(userDn))) && (!(MiscUtil.hasString(password))));

		Hashtable env      = new Hashtable();
		Workspace zone     = ((Workspace) getCoreDao().load(Workspace.class, zoneId));
		String    zoneName = zone.getName();
		env.put(Context.INITIAL_CONTEXT_FACTORY, getLdapProperty(zoneName, Context.INITIAL_CONTEXT_FACTORY));
		env.put(Context.REFERRAL,                "follow"                                                  );
		if (!anonymous) {
			env.put(Context.SECURITY_PRINCIPAL,      userDn                                                    );
			env.put(Context.SECURITY_CREDENTIALS,    password                                                  );		
			env.put(Context.SECURITY_AUTHENTICATION, getLdapProperty(zoneName, Context.SECURITY_AUTHENTICATION));
		} 

		// Specify the attributes we'll want returned as binary data.
		String guid = ds.getGuidAttribute();
		if      (null == guid)          guid  = "";
		else if (0    <  guid.length()) guid += " ";
		StringBuffer attrBuf = new StringBuffer(guid);
		attrBuf.append(                     DirectoryServer.OBJECT_SID_ATTRIBUTE     );
		attrBuf.append(" "); attrBuf.append(DirectoryServer.NDS_HOME_DIR_ATTRIBUTE   );
		attrBuf.append(" "); attrBuf.append(DirectoryServer.NETWORK_ADDRESS_ATTRIBUTE);
		if (ds.isEDirectory()) {
			attrBuf.append(" ");
			attrBuf.append(DirectoryServer.HOME_DIR_ATTRIBUTE);
		}
		env.put("java.naming.ldap.attributes.binary", attrBuf.toString());
		
		env.put(Context.PROVIDER_URL, ds.getAddress());
		String socketFactory = getLdapProperty(zoneName, "java.naming.ldap.factory.socket"); 
		if (null != socketFactory) {
			env.put("java.naming.ldap.factory.socket", socketFactory);
		}
	
		return new InitialLdapContext(env, null);
	}

    /*
     * Returns an LDAP property from the zone configuration.
     */
	private static String getLdapProperty(String zoneName, String name) {
		String val = SZoneConfig.getString(zoneName, "ldapConfiguration/property[@name='" + name + "']");
		if (!(MiscUtil.hasString(val))) {
			val = DEFAULT_PROPERTIES.get(name);
		}
		return val;
	}
	
	/**
	 * Requests a block of data from an LDAP server.
	 *  
	 * @param bs
	 * @param request
	 * @param ds
	 * @param si
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
    public static LdapServerDataRpcResponseData getLdapServerData(AllModulesInjected bs, HttpServletRequest request, DirectoryServer ds, LdapSearchInfo si) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "LdapBrowserHelper.getLdapServerData()");
		try {
			// Connection information.  Extended the LdapContextSource
			// to handle SSL, right now, we ignore the certificates.
			String  error     = null;
			String  userDn    = ds.getSyncUser();
			String  password  = ds.getSyncPassword();
			boolean anonymous = ((!(MiscUtil.hasString(userDn))) && (!(MiscUtil.hasString(password))));
			SecureLdapContextSource ldapContextSource = new SecureLdapContextSource(userDn, password, ds.getSslEnabled());
			ldapContextSource.setUrl(ds.getAddress());
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
	
			// Need to set this for Active Directory to ignore few
			// errors.
			template.setIgnoreNameNotFoundException( true);
			template.setIgnorePartialResultException(true);
	
			SearchControls sc = getSearchControls(si);
			String baseDn = ds.getUrl();
			if (ds.isActiveDirectory() && (!(MiscUtil.hasString(baseDn)))) {
				baseDn = getDomainNameFromAD(ds);
			}
			try {
				// Do the search.
				ldapObjects = template.search(
					baseDn,
					si.getSearchObjectClass(),
					sc,
					new LDAPObjectMapper());
			}
			
			catch (Exception ex) {
				// Did we get a size limit exceeded exception? 
				if (ex instanceof SizeLimitExceededException) {
					// Yes!  We special case handle that.
					returnObj.sizeExceeded(true);
					sc.setCountLimit(si.getMaximumReturnLimit());
					PagedResultsCookie pagedResultsCookie = null;
					PagedResultsDirContextProcessor control = new PagedResultsDirContextProcessor(si.getMaximumReturnLimit(), pagedResultsCookie);
		
					// Do the search.
					ldapObjects = template.search(
						baseDn,
						si.getSearchObjectClass(),
						sc,
						new LDAPObjectMapper(),
						control);
				}

				else {
					// No, we didn't get a size limit exceeded
					// exception!  Handle it as a generic error. 
					m_logger.error("LdapBrowserHelper.getLdapServerData( Failed trying to browse the tree:  '" + (anonymous ? "*anonymous*" : userDn) + "' )", ex);
					error = getLdapBrowserErrorMessage(ex, ds, anonymous);
				}
			}
	
			if (null != ldapObjects) {
				for (LdapObject obj:  ldapObjects) {
					ldapObjectList.add(obj);
				}
			}
	
			Collections.sort(ldapObjectList);
			returnObj.setResultList(ldapObjectList);
			LdapServerDataRpcResponseData reply = new LdapServerDataRpcResponseData(returnObj);
			if (null != error) {
				reply.setError(error);
			}
			return reply;
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
	 * Returns the root cause of an exception.
	 */
	private static Throwable getRootCause(Throwable t) {
		if (null == t) {
			return t;
		}

		int tries = 0;
		do {
			Throwable cause = t.getCause();
			if (null == cause) {
				return t;
			}
			t      = cause;
			tries += 1;
			if (100 < tries) {
				return t;
			}
		} while (true);
	}
	
	/*
	 * Maps an LdapSearchInfo to its corresponding SearchControls.
	 */
	private static SearchControls getSearchControls(LdapSearchInfo info) {
		SearchControls searchControls = new SearchControls();
		if (info.isSearchSubTree())
		     searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		else searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);

		// Speed up the search, we only care for these attributes.
		searchControls.setReturningAttributes(new String[] { "cn", "objectClass", "o", "ou", "dc", "c", "l" });

		// We don't want to read more than 1001 per container.  Active
		// Directory can return only a max of 1000, so set it to 1001
		// so that we can get the size limit exception
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

	/*
	 * Returns true if a root cause exception indicates that the LDAP
	 * server doesn't support anonymous authentication and false
	 * otherwise.
	 * 
	 * When the root cause of an LDAP search failure is an anonymous
	 * not supported problem, the root cause of the exception is a
	 * NamingException containing the following message:
	 * 
     *      [LDAP: error code 1 - 000004DC: LdapErr: DSID-0C090728, comment: In order to perform this operation a successful bind must be completed on the connection., data 0, v2580
	 */
	private static boolean isRootCauseAnonymousNotSupportedException(Throwable rootCause) {
		boolean reply = ((null != rootCause) && (rootCause instanceof NamingException));
		if (reply) {
			String details = rootCause.getMessage();
			reply = MiscUtil.hasString(details);
			if (reply) {
				details = details.toLowerCase();
				reply = (
					details.contains("ldap: error code 1"                 ) &&
					details.contains("ldaperr:"                           ) &&
					details.contains("a successful bind must be completed"));
			}
		}
		return true;
	}
	
	/*
	 * Return true if a NamingEnumeration has a value available and
	 * false otherwise.
	 */
	@SuppressWarnings("unchecked")
	private static boolean hasMore(NamingEnumeration namingEnumeration) {
		if (null == namingEnumeration) {
			return false;
		}
		
		boolean hasMore = false;
		try {
			// NamingEnumeration.hasMore() will throw an exception if
			// needed only after all valid objects have been returned
			// as a result of walking through the enumeration.
			hasMore = namingEnumeration.hasMore();
		}
		
		catch(Exception ex) {
			m_logger.error("LdapBrowserHelper.hasMore( NamingEnumeration ) threw exception: " + ex.toString(), ex);
		}
	
		return hasMore;
	}
}
