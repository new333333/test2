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

package org.kablink.teaming.module.authentication.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.NoObjectByTheIdException;
import org.kablink.teaming.asmodule.security.authentication.AuthenticationContextHolder;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.AuthenticationConfig;
import org.kablink.teaming.domain.LdapConnectionConfig;
import org.kablink.teaming.domain.LoginInfo;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.security.authentication.AuthenticationManagerUtil;
import org.kablink.teaming.security.authentication.UserAccountNotActiveException;
import org.kablink.teaming.spring.security.SsfContextMapper;
import org.kablink.teaming.spring.security.SynchNotifiableAuthentication;
import org.kablink.teaming.spring.security.ZoneAwareLocalAuthenticationProvider;
import org.kablink.teaming.spring.security.ldap.PreAuthenticatedAuthenticator;
import org.kablink.teaming.spring.security.ldap.PreAuthenticatedFilterBasedLdapUserSearch;
import org.kablink.teaming.spring.security.ldap.PreAuthenticatedLdapAuthenticationProvider;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.SessionUtil;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.WindowsUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.Validator;
import org.kablink.teaming.module.authentication.UserIdNotActiveException;
import org.kablink.teaming.module.authentication.UserIdNotUniqueException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.AuthenticationServiceException;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.providers.AuthenticationProvider;
import org.springframework.security.providers.ProviderManager;
import org.springframework.security.providers.ldap.LdapAuthenticationProvider;
import org.springframework.security.providers.ldap.authenticator.BindAuthenticator;
import org.springframework.security.providers.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.userdetails.UsernameNotFoundException;

public class AuthenticationModuleImpl extends BaseAuthenticationModule
		implements AuthenticationProvider, InitializingBean {
	protected Log logger = LogFactory.getLog(getClass());

	protected Map<Long, ProviderManager> externalAuthenticators = null;

	protected Map<Long, ZoneAwareLocalAuthenticationProvider> localProviders = null;
	protected ConcurrentHashMap<Long, Long> lastUpdates = null;
	
	protected Class localAuthenticationProviderClass;
	protected boolean authenticateLdapMatchingUsersUsingLdapOnly = true;
	
	public AuthenticationModuleImpl() throws ClassNotFoundException {
		externalAuthenticators = new HashMap<Long, ProviderManager>();
		localProviders = new HashMap<Long, ZoneAwareLocalAuthenticationProvider>();
		lastUpdates = new ConcurrentHashMap<Long, Long>();
	}

	public void afterPropertiesSet() throws Exception {
		localAuthenticationProviderClass = ReflectHelper.classForName(SPropsUtil.getString("local.authentication.provider.class", "org.kablink.teaming.spring.security.ZoneAwareLocalAuthenticationProviderImpl"));
		authenticateLdapMatchingUsersUsingLdapOnly = SPropsUtil.getBoolean("authenticate.ldap.matching.users.using.ldap.only", true);
	}

	protected void addZone(ZoneConfig zoneConfig) throws Exception
	{
		String zoneName = getZoneModule().getZoneInfo(zoneConfig.getZoneId()).getZoneName();
		if(externalAuthenticators.containsKey(zoneConfig.getZoneId())) {
			logger.error("Duplicate zone added to AuthenticationModule: " + zoneConfig.getZoneId() + " " + zoneName);
			throw new Exception("Duplicate zone added to AuthenticationModule");
		}
		logger.debug("Setting authentication info for zone " + zoneName);
		
		ZoneAwareLocalAuthenticationProvider localProvider = newZoneAwareLocalAuthenticationProviderInstance(zoneName);
		localProviders.put(zoneConfig.getZoneId(), localProvider);
		
		externalAuthenticators.put(zoneConfig.getZoneId(), null);
		
		rebuildProvidersForZone(zoneConfig);
	}
	
	protected ZoneAwareLocalAuthenticationProvider newZoneAwareLocalAuthenticationProviderInstance(String zoneName) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		ZoneAwareLocalAuthenticationProvider provider = (ZoneAwareLocalAuthenticationProvider) localAuthenticationProviderClass.newInstance();
		provider.setZoneName(zoneName);
		return provider;
	}
	
	protected void removeZone(Long zoneId)
	{
		if(externalAuthenticators.containsKey(zoneId)) {
			externalAuthenticators.remove(zoneId);
			localProviders.remove(zoneId);
			lastUpdates.remove(zoneId);
		}
	}

	protected void ensureZoneIsConfigured(Long zoneId) throws Exception
	{
		try {
			ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(zoneId);
			synchronized(this) {
				if(!externalAuthenticators.containsKey(zoneId)) {
					addZone(zoneConfig);
				}
			}
			AuthenticationConfig authConfig = zoneConfig.getAuthenticationConfig();
			Long lastUpdateInDb = authConfig.getLastUpdate();
			Long lastUpdateInMem = lastUpdates.get(zoneId);
			if((lastUpdateInDb != null) &&
					((lastUpdateInMem == null) || 
							(lastUpdateInDb.compareTo(lastUpdateInMem) > 0))) {
				try {
					rebuildProvidersForZone(zoneConfig);
				} catch(Exception e) {
					logger.error("Unable to rebuild providers for zone " + zoneId + ": " + e.toString());
				}
			}
		}catch (NoObjectByTheIdException no) {
			removeZone(zoneId);
		} 
	}
	
	protected void rebuildProvidersForZone(ZoneConfig zoneConfig) throws Exception {
		ProviderManager pm = externalAuthenticators.get(zoneConfig.getZoneId());
		List<AuthenticationProvider> providers = createProvidersForZone(zoneConfig.getZoneId());
		if(providers.size() > 0) { // we've got external authenticators such as LDAP
			if(pm != null) {
				pm.setProviders(providers);
			}
			else {
				pm = new ProviderManager();
				pm.setProviders(providers);
				externalAuthenticators.put(zoneConfig.getZoneId(), pm);
			}
		}
		else { // no external authenticators
			if(pm != null)
				externalAuthenticators.put(zoneConfig.getZoneId(), null);
		}
		lastUpdates.put(zoneConfig.getZoneId(), zoneConfig.getAuthenticationConfig().getLastUpdate());
	}


	protected List<AuthenticationProvider> createProvidersForZone(Long zoneId)
			throws Exception {
		List<AuthenticationProvider> providers = new LinkedList<AuthenticationProvider>();
		for (LdapConnectionConfig config : getLdapConnectionConfigs(zoneId)) {
			String search = "(" + config.getUserIdAttribute() + "={0})";
			if (config.getUserSearches().size() > 0) {
				DefaultSpringSecurityContextSource contextSource = null;
				try {
					String url;
					String derefAliases;
					Map<String,String> baseEnvironmentProperties; 
					
					// The call to new DefaultSpringSecurityContextSource() will fail if
					// the word "ldap" is not all lower case.
					url = config.getUrl();
					if ( url != null )
					{
						String protocol;
						
						// ldap authentication fails if the word "ldap" in the url is not all lower case.  Bug 553190
						// See if the url starts with "ldap:"
						protocol = url.substring( 0, 5 );
						if ( protocol.equalsIgnoreCase( "ldap:" ) )
						{
							url = "ldap:" + url.substring( 5 );
						}
					}
					
					contextSource = new DefaultSpringSecurityContextSource( url );
					
					// Set the property that tells ldap whether or not to dereference aliases.
					derefAliases = SPropsUtil.getString( "java.naming.ldap.derefAliases", "never" );
					baseEnvironmentProperties = new HashMap<String, String>();
					baseEnvironmentProperties.put( "java.naming.ldap.derefAliases", "never" );
					contextSource.setBaseEnvironmentProperties( baseEnvironmentProperties );
					
				} catch(Exception e) {
					logger.warn("Unable to create LDAP context for url " + config.getUrl() + ": " + e.toString());
					continue;
				}
				if (Validator.isNotNull(config.getPrincipal())) {
					contextSource.setUserDn(config.getPrincipal());
					contextSource.setPassword(config.getCredentials()==null?"":config.getCredentials());
				} else {
					contextSource.setAnonymousReadOnly(true);
				}
				contextSource.afterPropertiesSet();

				SsfContextMapper contextMapper = new SsfContextMapper(
																getZoneModule(),
																config.getMappings() );

				for (LdapConnectionConfig.SearchInfo us : config
						.getUserSearches()) {
					providers.add(createLdapAuthenticationProvider(contextSource, contextMapper, us, search));
					providers.add(createPreAuthenticatedLdapAuthenticationProvider(contextSource, contextMapper, us, search));
				}
			}
		}

		/*
		 * Don't forget to allow for custom authenticators. This isn't how to do
		 * it, but it reminds you that the custom authenticator beans are
		 * registered with SpringAuthenticationBeans
		 * 
		 * 
		 * if(zoneInfo.getZoneName().equals("monkeyco")) {
		 * providers.add(SpringAuthenticationBeans.getInstance().findProvider(zoneInfo.getZoneId(),
		 * "ldapTwo")); } else {
		 * providers.add(SpringAuthenticationBeans.getInstance().findProvider(zoneInfo.getZoneId(),
		 * "dynamicLdap")); }
		 */
		return providers;
	}

	protected AuthenticationProvider createLdapAuthenticationProvider
		(DefaultSpringSecurityContextSource contextSource, 
			SsfContextMapper contextMapper, 
			LdapConnectionConfig.SearchInfo us, 
			String search) {
		BindAuthenticator authenticator = new BindAuthenticator(
				contextSource);
		String filter = search;
		if(!us.getFilter().equals("")) {
			filter = "(&" + search + us.getFilter() + ")";
		}
		FilterBasedLdapUserSearch userSearch = new FilterBasedLdapUserSearch(
				us.getBaseDn(), filter, contextSource);
		if (!us.isSearchSubtree()) {
			userSearch.setSearchSubtree(false);
		}
		authenticator.setUserSearch(userSearch);
		LdapAuthenticationProvider ldap = new LdapAuthenticationProvider(
				authenticator);
		ldap.setUseAuthenticationRequestCredentials(true);
		ldap.setUserDetailsContextMapper(contextMapper);
		return ldap;
	}
	
	protected AuthenticationProvider createPreAuthenticatedLdapAuthenticationProvider
	(DefaultSpringSecurityContextSource contextSource,
		SsfContextMapper contextMapper, 
		LdapConnectionConfig.SearchInfo us, 
		String search) {
		PreAuthenticatedAuthenticator authenticator = new PreAuthenticatedAuthenticator(
				contextSource);
		String filter = search;
		if(!us.getFilter().equals("")) {
			filter = "(&" + search + us.getFilter() + ")";
		}
		PreAuthenticatedFilterBasedLdapUserSearch userSearch = new PreAuthenticatedFilterBasedLdapUserSearch(
				us.getBaseDn(), filter, contextSource);
		if (!us.isSearchSubtree()) {
			userSearch.setSearchSubtree(false);
		}
		authenticator.setUserSearch(userSearch);
		PreAuthenticatedLdapAuthenticationProvider ldap = new PreAuthenticatedLdapAuthenticationProvider(
				authenticator);
		ldap.setUseAuthenticationRequestCredentials(true);
		ldap.setUserDetailsContextMapper(contextMapper);
		return ldap;
	}
	
	/**
	 * Performs authentication with the same contract as {@link
	 * org.springframework.security.AuthenticationManager#authenticate(Authentication)}.
	 * Delegates the authentication to the AuthenticationManager configured for
	 * the zone to which the request was directed.
	 * 
	 * @param authentication
	 *            the authentication request object.
	 * 
	 * @return a fully authenticated object including credentials. May return
	 *         <code>null</code> if the <code>AuthenticationProvider</code>
	 *         is unable to support authentication of the passed
	 *         <code>Authentication</code> object. In such a case, the next
	 *         <code>AuthenticationProvider</code> that supports the presented
	 *         <code>Authentication</code> class will be tried.
	 * 
	 * @throws AuthenticationException
	 *             if authentication fails.
	 */
	public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
		long begin = System.nanoTime();
		
		try {
			// The following hack(?) is necessary to handle the pre-authentication situation where
			// initial authentication request is made in the context of no user, yet at least one
			// of the interceptors associated with this method invocation relies on such context
			// being there. To work around that, we simply set up guest context and leave it until
			// after returning from this method.
			if(RequestContextHolder.getRequestContext() == null) {
				String zoneName = getZoneModule().getZoneNameByVirtualHost(ZoneContextHolder.getServerName());
				String guestUserName = SZoneConfig.getGuestUserName(zoneName);
				RequestContextHolder.setRequestContext(new RequestContext(zoneName, guestUserName, null).resolve());
			}
	 				
			String enableKey = AuthenticationContextHolder.getEnableKey();
			boolean enable = true;
			if(Validator.isNotNull(enableKey)) {
				enable = SPropsUtil.getBoolean(enableKey, true);
			}
			if(!enable) {
				if(logger.isDebugEnabled())
					logger.debug("Rejecting " + getAuthenticator() + " authentication request from " + authentication.getName() + ": It is disabled");
				throw new AuthenticationServiceException("The service is disabled");
			}
			
			try {
				boolean hadSession = SessionUtil.sessionActive();
				try {
					if (!hadSession) SessionUtil.sessionStartup();
					
					SimpleProfiler.start( "1-AuthenticationModuleImpl.doAuthenticate()");
					Authentication retVal = doAuthenticate(authentication);
					SimpleProfiler.stop( "1-AuthenticationModuleImpl.doAuthenticate()");
					
					return retVal;
				}
				finally {
					if (!hadSession) SessionUtil.sessionStop();
				}
			}
			catch(AuthenticationServiceException e) {
				Throwable t = e.getCause();
				logger.error(e.getMessage() + ((t != null)? ": " + t.toString() : ""));
				throw e;
			}
			catch(AuthenticationException e) {
				Long zone = getZoneModule().getZoneIdByVirtualHost(ZoneContextHolder.getServerName());
				logger.warn("Authentication failure for zone " + zone + ": " + e.toString());
				throw e;
			}
			catch(RuntimeException e) {
				Long zone = getZoneModule().getZoneIdByVirtualHost(ZoneContextHolder.getServerName());
				logger.error("Authentication failure for zone " + zone, e);
				throw e;	
			}
		}
		finally {
			if(logger.isDebugEnabled())
				logger.debug("Authenticating '" + authentication.getName() + "' - " +
					((System.nanoTime() - begin)/1000000.0) + " ms");
		}
    }

	protected Authentication doAuthenticate(Authentication authentication) throws AuthenticationException {
		AuthenticationException exc = null;
    	Long zone = getZoneModule().getZoneIdByVirtualHost(ZoneContextHolder.getServerName());
    	try {
    		SimpleProfiler.start( "2-AuthenticationModuleImpl.ensureZoneIsConfigured()" );
    		ensureZoneIsConfigured(zone);
    		SimpleProfiler.stop( "2-AuthenticationModuleImpl.ensureZoneIsConfigured()" );
    	} catch(Exception e) {
    		logger.error("Unable to configure authentication for zone " + zone, e);
    		throw new AuthenticationServiceException("Unable to configure authentication for zone " + zone, e);
    	}

    	if(externalAuthenticators.containsKey(zone)) {
    		// Don't allow anyone to log in without specifying a password (to prevent successful
    		// authentication on an account that has no password).
    		if(authentication.getCredentials() == null || authentication.getCredentials().equals(""))
    			throw new BadCredentialsException("Password is required");
    		
       		Authentication result = null;
       		
        	// Are we dealing with one of Teaming's system accounts such as "admin" or "guest"?
        	if ( !MiscUtil.isSystemUserAccount( authentication.getName() ) )
        	{
        		// No, try to do an ldap authentication.
        		// This will also try local authentication as fallback, if configured to do so.
	    		try {
	    			// Perform authentication
	    			SimpleProfiler.start( "3-authenticators.get(zone).authenticate(authentication)" );
	     			result = performAuthentication(zone, authentication);
	     			SimpleProfiler.stop( "3-authenticators.get(zone).authenticate(authentication)" );
	     			
	     			String loginName;
	     			
	     			if(result instanceof PreAuthenticatedAuthenticationToken) {
	     				PreAuthenticatedAuthenticationToken preAuth = (PreAuthenticatedAuthenticationToken) result;
	     				loginName = WindowsUtil.getSamaccountname((String) result.getName());
	     			}
	     			else {
	     				loginName = (String) result.getName();
	     			}
	     			
	     			if(SPropsUtil.getBoolean("authenticator.synch." + getAuthenticator(), false)) {
		     			// This is not used for authentication but for synchronization.
		     			SimpleProfiler.start( "4-AuthenticationManagerUtil.authenticate1" );
		    			AuthenticationManagerUtil.authenticate(getZoneModule().getZoneNameByVirtualHost(ZoneContextHolder.getServerName()),
		    					loginName, (String) result.getCredentials(),
		    					(Map) result.getPrincipal(), getAuthenticator());
		     			SimpleProfiler.stop( "4-AuthenticationManagerUtil.authenticate1" );
	     			}
	     			else {
	        			// This is not used for authentication or synchronization but merely to log the authenticator.
		     			SimpleProfiler.start( "4-AuthenticationManagerUtil.authenticate2" );
	        			AuthenticationManagerUtil.authenticate(getZoneModule().getZoneNameByVirtualHost(ZoneContextHolder.getServerName()),
	        					loginName, (String) result.getCredentials(),
	        					false, false, true, 
	        					(Map) result.getPrincipal(), getAuthenticator());			
		     			SimpleProfiler.stop( "4-AuthenticationManagerUtil.authenticate2" );
	     			}
	     			
	    			if(result instanceof SynchNotifiableAuthentication)
	    				((SynchNotifiableAuthentication)result).synchDone();
	    			return result;
	    		} catch(AuthenticationException e) {
	    			exc = e;
	    		} catch(UserAccountNotActiveException e) {
	    			exc = new UserIdNotActiveException(e.getMessage());
	    		}
	    		catch ( IncorrectResultSizeDataAccessException irsdaEx )
	    		{
	    			String errDesc;
	    			String[] args = {authentication.getName()};
	    			
	    			// This exception means that there are multiple users in the ldap directory
	    			// that have the same name as the user id supplied by the user.
	        		errDesc = NLT.get( "errorcode.login.failed.loginNameNotUnique", args );
	        		
	    			exc = new UserIdNotUniqueException( errDesc );
	    		}
        	}
        	else { // Yes, system account
     			// Do not try ldap authentication. Authenticate against Teaming db.
     			SimpleProfiler.start( "3a-system account: localProviders.get(zone).authenticate(authentication)" );
    			result = localProviders.get(zone).authenticate(authentication);
     			SimpleProfiler.stop( "3a-system account: localProviders.get(zone).authenticate(authentication)" );

    			// This is not used for authentication or synchronization but merely to log the authenticator.
     			SimpleProfiler.start( "4a-system account: AuthenticationManagerUtil.authenticate()" );
    			AuthenticationManagerUtil.authenticate(getZoneModule().getZoneNameByVirtualHost(ZoneContextHolder.getServerName()),
    					(String) result.getName(), (String) result.getCredentials(),
    					false, false, true, 
    					(Map) result.getPrincipal(), getAuthenticator());			
     			SimpleProfiler.stop( "4a-system account: AuthenticationManagerUtil.authenticate()" );

    			return result;
    		}
    	}
    	
    	if(exc != null)
    		throw exc;
    	else
    		throw new UsernameNotFoundException("No such user " + authentication.getName());
	}
	
	private String getAuthenticator() {
		String authenticator = AuthenticationContextHolder.getAuthenticator();
		if(authenticator == null)
			authenticator = LoginInfo.AUTHENTICATOR_UNKNOWN;
		return authenticator;
	}
	
	/**
	 * Returns <code>true</code> if this <Code>AuthenticationProvider</code>
	 * supports the indicated <Code>Authentication</code> object. Delegates
	 * the decision to the providers of the AuthenticationManager configured for
	 * the zone to which the request was directed.
	 * <p>
	 * Returning <code>true</code> does not guarantee an <code>AuthenticationProvider</code>
	 * will be able to authenticate the presented instance of the <code>Authentication</code>
	 * class. It simply indicates it can support closer evaluation of it. An
	 * <code>AuthenticationProvider</code> can still return <code>null</code>
	 * from the {@link #authenticate(Authentication)} method to indicate another
	 * <code>AuthenticationProvider</code> should be tried.
	 * </p>
	 * <p>
	 * Selection of an <code>AuthenticationProvider</code> capable of
	 * performing authentication is conducted at runtime the <code>ProviderManager</code>.
	 * </p>
	 * 
	 * @param authentication
	 *            DOCUMENT ME!
	 * 
	 * @return <code>true</code> if the implementation can more closely
	 *         evaluate the <code>Authentication</code> class presented
	 */
	public boolean supports(Class authentication) {
		Long zone = getZoneModule().getZoneIdByVirtualHost(
				ZoneContextHolder.getServerName());
    	try {
    		ensureZoneIsConfigured(zone);
    	} catch(Exception e) {
    		logger.error("Unable to configure authentication for zone " + zone, e);
    		throw new AuthenticationServiceException("Unable to configure authentication for zone " + zone, e);
    	}
		if (externalAuthenticators.containsKey(zone)) {
			ProviderManager pm = externalAuthenticators.get(zone);
			if(pm != null) {
				for (Object o : pm.getProviders()) {
					AuthenticationProvider p = (AuthenticationProvider) o;
					if (p.supports(authentication)) {
						return true;
					}
				}
			}
			return (localProviders.get(zone).supports(authentication));
		}
		return false;
	}

	private Authentication performAuthentication(Long zoneId, Authentication authentication) throws AuthenticationException {
		AuthenticationException exc = null;
		ProviderManager pm = externalAuthenticators.get(zoneId);
		boolean mustSkipLocalAuthentication = false;
		if(pm != null) {
			try {
				return pm.authenticate(authentication);
			}
			catch(AuthenticationException e) {
				if(logger.isDebugEnabled())	
					logger.debug("External authentication failed: " + e.toString());
				exc = e;
				if(authenticateLdapMatchingUsersUsingLdapOnly && (e instanceof BadCredentialsException) && !(e instanceof UsernameNotFoundException)) 
					mustSkipLocalAuthentication = true;
			}
		}
		if(!mustSkipLocalAuthentication) {
			ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(zoneId);
			if(zoneConfig.getAuthenticationConfig().isAllowLocalLogin()) {
				return localProviders.get(zoneId).authenticate(authentication);
			}
		}
		if(exc != null) {
			throw exc;
		}
		else {
			// This means that we don't have any external authenticators while local login is at the same time disallowed.
			// Clearly a major configuration error which should not (and can not) occur.
			throw new UsernameNotFoundException("No such account " + authentication.getName());
		}
	}
}