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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.asmodule.security.authentication.AuthenticationContextHolder;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.AuthenticationConfig;
import org.kablink.teaming.domain.IdentityInfo;
import org.kablink.teaming.domain.LdapConnectionConfig;
import org.kablink.teaming.domain.LoginAudit;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.module.authentication.AuthenticationServiceProvider;
import org.kablink.teaming.module.authentication.FailedAuthenticationHistory;
import org.kablink.teaming.module.authentication.FailedAuthenticationMonitor;
import org.kablink.teaming.module.authentication.IdentityInfoObtainable;
import org.kablink.teaming.module.authentication.LocalAuthentication;
import org.kablink.teaming.module.authentication.UserAccountNotProvisionedException;
import org.kablink.teaming.module.authentication.UserIdNotActiveException;
import org.kablink.teaming.module.authentication.UserIdNotUniqueException;
import org.kablink.teaming.module.ldap.LdapModule;
import org.kablink.teaming.NoObjectByTheIdException;
import org.kablink.teaming.TextVerificationException;
import org.kablink.teaming.security.authentication.AuthenticationManagerUtil;
import org.kablink.teaming.security.authentication.UserAccountNotActiveException;
import org.kablink.teaming.security.authentication.UserDoesNotExistException;
import org.kablink.teaming.spring.security.ldap.LdapAuthenticationProvider;
import org.kablink.teaming.spring.security.OpenIDAuthenticationProvider;
import org.kablink.teaming.spring.security.SsfContextMapper;
import org.kablink.teaming.spring.security.SynchNotifiableAuthentication;
import org.kablink.teaming.spring.security.ZoneAwareLocalAuthenticationProvider;
import org.kablink.teaming.spring.security.ldap.PreAuthenticatedAuthenticator;
import org.kablink.teaming.spring.security.ldap.PreAuthenticatedFilterBasedLdapUserSearch;
import org.kablink.teaming.spring.security.ldap.PreAuthenticatedLdapAuthenticationProvider;
import org.kablink.teaming.util.GangliaMonitoring;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.SessionUtil;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.WindowsUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BuiltInUsersHelper;
import org.kablink.util.Validator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * ? 
 * 
 * @author ?
 */
@SuppressWarnings({"unchecked", "unused", "deprecation"})
public abstract class AbstractAuthenticationProviderModule extends BaseAuthenticationModule
		implements AuthenticationProvider, FailedAuthenticationMonitor, InitializingBean {
	protected Log logger = LogFactory.getLog(getClass());

	protected Map<Long, ProviderManager> nonLocalAuthenticators = null;

	protected Map<Long, ZoneAwareLocalAuthenticationProvider> localProviders = null;
	protected ConcurrentHashMap<Long, Long> lastUpdates = null;
	
	protected Class localAuthenticationProviderClass;
	protected boolean authenticateLdapMatchingUsersUsingLdapOnly = true;
	
	protected Set<String> cacheUsingAuthenticators;
	protected int cacheUsingAuthenticatorTimeout; // in seconds
	protected ConcurrentHashMap<String, Long> lastRegularAuthenticationTimes;

	protected boolean m_recordFailedAuthentications = false;
	protected FailedAuthenticationHistory m_failedAuthenticationHistory = null;
	
	public AbstractAuthenticationProviderModule() throws ClassNotFoundException {
		nonLocalAuthenticators = new HashMap<Long, ProviderManager>();
		localProviders = new HashMap<Long, ZoneAwareLocalAuthenticationProvider>();
		lastUpdates = new ConcurrentHashMap<Long, Long>();
		m_recordFailedAuthentications = SPropsUtil.getBoolean( "failed.user.authentication.history", false ); 
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		localAuthenticationProviderClass = ReflectHelper.classForName(SPropsUtil.getString("local.authentication.provider.class", "org.kablink.teaming.spring.security.ZoneAwareLocalAuthenticationProviderImpl"));
		authenticateLdapMatchingUsersUsingLdapOnly = SPropsUtil.getBoolean("authenticate.ldap.matching.users.using.ldap.only", true);
		String strs[] = SPropsUtil.getStringArray("cache.using.authenticators", ",");
		cacheUsingAuthenticators = new HashSet<String>();
		for(String str:strs)
			cacheUsingAuthenticators.add(str);
		cacheUsingAuthenticatorTimeout = SPropsUtil.getInt("cache.using.authenticator.timeout", 60);
		lastRegularAuthenticationTimes = new ConcurrentHashMap<String, Long>();
	}

	protected void addZone(ZoneConfig zoneConfig) throws Exception
	{
		String zoneName = getZoneModule().getZoneInfo(zoneConfig.getZoneId()).getZoneName();
		if(nonLocalAuthenticators.containsKey(zoneConfig.getZoneId())) {
			logger.error("Duplicate zone added to AuthenticationModule: " + zoneConfig.getZoneId() + " " + zoneName);
			throw new Exception("Duplicate zone added to AuthenticationModule");
		}
		logger.debug("Setting authentication info for zone " + zoneName);
		
		ZoneAwareLocalAuthenticationProvider localProvider = newZoneAwareLocalAuthenticationProviderInstance(zoneName);
		localProviders.put(zoneConfig.getZoneId(), localProvider);
		
		nonLocalAuthenticators.put(zoneConfig.getZoneId(), null);
		
		rebuildProvidersForZone(zoneConfig);
	}
	
	protected ZoneAwareLocalAuthenticationProvider newZoneAwareLocalAuthenticationProviderInstance(String zoneName) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		ZoneAwareLocalAuthenticationProvider provider = (ZoneAwareLocalAuthenticationProvider) localAuthenticationProviderClass.newInstance();
		provider.setZoneName(zoneName);
		return provider;
	}
	
	protected void removeZone(Long zoneId)
	{
		if(nonLocalAuthenticators.containsKey(zoneId)) {
			nonLocalAuthenticators.remove(zoneId);
			localProviders.remove(zoneId);
			lastUpdates.remove(zoneId);
		}
	}

	protected void ensureZoneIsConfigured(Long zoneId) throws Exception
	{
		try {
			ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(zoneId);
			synchronized(this) {
				if(!nonLocalAuthenticators.containsKey(zoneId)) {
					addZone(zoneConfig);
				}
			}
			AuthenticationConfig authConfig = zoneConfig.getAuthenticationConfig();
			Long lastUpdateInDb = authConfig.getLastUpdate();
			Long lastUpdateInMem = lastUpdates.get(zoneId);
			// If the date in the db is different from the in memory date, rebuild the providers.
			if((lastUpdateInDb != null) &&
					((lastUpdateInMem == null) || 
							(lastUpdateInDb.compareTo(lastUpdateInMem) != 0))) {
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
		ProviderManager pm = nonLocalAuthenticators.get(zoneConfig.getZoneId());
		List<AuthenticationProvider> providers = createProvidersForZone(zoneConfig.getZoneId());
		if(providers.size() > 0) { // we've got external authenticators such as LDAP
			if(pm != null) {
				pm.setProviders(providers);
			}
			else {
				pm = new ProviderManager();
				pm.setProviders(providers);
				nonLocalAuthenticators.put(zoneConfig.getZoneId(), pm);
			}
		}
		else { // no external authenticators
			if(pm != null)
				nonLocalAuthenticators.put(zoneConfig.getZoneId(), null);
		}
		lastUpdates.put(zoneConfig.getZoneId(), zoneConfig.getAuthenticationConfig().getLastUpdate());
	}


	protected List<AuthenticationProvider> createProvidersForZone(Long zoneId)
			throws Exception {
		List<AuthenticationProvider> providers = new LinkedList<AuthenticationProvider>();
		
		// Build LDAP authentication providers.
		// Get the latest state of the ldap connection config objects from the database. Do NOT read it from the cache.
		List<LdapConnectionConfig> configs = getLdapConnectionConfigs(zoneId);
		// Disconnect the objects from the current session so that it can be used across many different sessions
		// without the fear of application layer inadvertantly making modification to the objects and flush the
		// changes to the database accidently.
		getCoreDao().evict(configs);
		// Update the cache with the latest state of the objects.
		getLdapModule().setConfigsReadOnlyCache(zoneId, configs);
		for (LdapConnectionConfig config : configs) {
			String search = "(" + config.getUserIdAttribute() + "={0})";
			if (config.getUserSearches().size() > 0) {
				DefaultSpringSecurityContextSource contextSource = null;
				try {
					String url;
					String timeout;
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
					baseEnvironmentProperties = new HashMap<String, String>();
					baseEnvironmentProperties.put( "java.naming.ldap.derefAliases", SPropsUtil.getString( "java.naming.ldap.derefAliases", "never" ) );

					// Part of fix for bug 875689
					timeout = SPropsUtil.getString( "com.sun.jndi.ldap.read.timeout", "10000" );
					baseEnvironmentProperties.put( "com.sun.jndi.ldap.read.timeout", timeout );
					
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
																config.getMappings());

				for (LdapConnectionConfig.SearchInfo us : config
						.getUserSearches()) {
					providers.add(createLdapAuthenticationProvider(contextSource, contextMapper, zoneId, config.getId(), us, search));
					providers.add(createPreAuthenticatedLdapAuthenticationProvider(contextSource, contextMapper, zoneId, config.getId(), us, search));
				}
			}
		}
		
		// Build OpenID authentication provider
		OpenIDAuthenticationProvider openIdAuthProvider = createOpenIDAuthenticationProvider(zoneId);
		if(openIdAuthProvider != null)
			providers.add(openIdAuthProvider);
		
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
	
	abstract protected OpenIDAuthenticationProvider createOpenIDAuthenticationProvider(Long zoneId) throws Exception;

	protected AuthenticationProvider createLdapAuthenticationProvider
		(DefaultSpringSecurityContextSource contextSource, 
			SsfContextMapper contextMapper, 
			Long zoneId,
			String configId,
			LdapConnectionConfig.SearchInfo us, 
			String search) {
		BindAuthenticator authenticator = new BindAuthenticator(
				contextSource);
		String filter = search;
		if(!us.getFilter().equals("")) {
			filter = "(&" + search + us.getFilter() + ")";
		}
		
		if ( filter != null && filter.length() > 0 )
		{
			// Carriage returns and line feeds in the filter cause authentication to fail.
			filter = filter.replaceAll( "\r", "" );
			filter = filter.replaceAll( "\n", "" );
		}
		
		FilterBasedLdapUserSearch userSearch = new FilterBasedLdapUserSearch(
				us.getBaseDn(), filter, contextSource);
		if (!us.isSearchSubtree()) {
			userSearch.setSearchSubtree(false);
		}
		authenticator.setUserSearch(userSearch);
		LdapAuthenticationProvider ldap = new LdapAuthenticationProvider(
				zoneId,
				configId,
				authenticator);
		ldap.setUseAuthenticationRequestCredentials(true);
		ldap.setUserDetailsContextMapper(contextMapper);
		return ldap;
	}
	
	protected AuthenticationProvider createPreAuthenticatedLdapAuthenticationProvider
	(DefaultSpringSecurityContextSource contextSource,
		SsfContextMapper contextMapper, 
		Long zoneId,
		String configId,
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
				zoneId,
				configId,
				authenticator);
		ldap.setUseAuthenticationRequestCredentials(true);
		ldap.setUserDetailsContextMapper(contextMapper);
		return ldap;
	}
	
	/**
	 * Performs authentication with the same contract as {@link
	 * org.springframework.security.core.AuthenticationManager#authenticate(Authentication)}.
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
	@Override
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

					// Does authentication require captcha?
					// If we have detected a brute-force attack we will require captcha for
					// web-based authentication.
					if ( doesAuthenticationRequireCaptcha( getAuthenticator(), authentication.getName() ) )
					{
						// Yes
						if ( isCaptchaValid( authentication ) == false )
						{
							logger.warn( "Authentication attempt failed because a captcha response was invalid.  Name: " + authentication.getName() );
							throw new TextVerificationException();
						}
					}
					
					SimpleProfiler.start( "1-AuthenticationModuleImpl.doAuthenticate()");
					Authentication retVal = doAuthenticate(authentication);
					SimpleProfiler.stop( "1-AuthenticationModuleImpl.doAuthenticate()");
					
					// If we get here the user successfull authenticated.
					{
						String authenticatorName;
						
						authenticatorName = getAuthenticator();
						if ( authenticatorName != null && authenticatorName.equalsIgnoreCase( LoginAudit.AUTHENTICATOR_WEB ) )
						{
							clearFailedAuthenticationHistory( authentication.getName() );
						}
					}
					
					return retVal;
				}
				finally {
					if (!hadSession) SessionUtil.sessionStop();
				}
			}
			catch(AuthenticationServiceException e) {
				unsuccessfulAuthentication(authentication);
				Throwable t = e.getCause();
				logger.error(e.getMessage() + ((t != null)? ": " + t.toString() : ""));
				throw e;
			}
			catch(AuthenticationException e) {
				String ipAddr;
				
				if ( (e instanceof TextVerificationException) == false )
					unsuccessfulAuthentication(authentication);
				
				String exDesc;				
				Long zone = getZoneModule().getZoneIdByVirtualHost(ZoneContextHolder.getServerName());
				if ( e.getCause() != null )
					exDesc = e.getCause().toString();
				else
					exDesc = e.toString();
				
				ipAddr = ZoneContextHolder.getClientAddr();
				if ( ipAddr == null || ipAddr.length() == 0 )
					ipAddr = "unknown";
				
				logger.warn( "[client " + ipAddr + "] user " + authentication.getName() + ": authentication failure: " + exDesc );
				throw e;
			}
			catch(RuntimeException e) {
				unsuccessfulAuthentication(authentication);
				Long zone = getZoneModule().getZoneIdByVirtualHost(ZoneContextHolder.getServerName());
				logger.error("Authentication failure for [" + authentication.getName() + "]", e);
				throw e;	
			}
		}
		finally {
			if(logger.isDebugEnabled())
				logger.debug("Authenticating '" + authentication.getName() + "' - " +
					((System.nanoTime() - begin)/1000000.0) + " ms");
		}
    }

	private void updateLastRegularAuthenticationTimeIfApplicable(Authentication authentication) {
		if(cacheUsingAuthenticators.contains(getAuthenticator()) && !(authentication instanceof PreAuthenticatedAuthenticationToken)) {
			lastRegularAuthenticationTimes.put(authentication.getName(), System.currentTimeMillis());
		}
	}
	
	/*
	 * Return true if authenticating against local database (e.g. local or external users whose 
	 * credentials are stored in the database, or LDAP users using their locally cached credential, etc.)
	 * Return false if authenticating against external identity source such as LDAP server or
	 * the user is already preauthenticated via IDP/SP SSO service in which case validation is
	 * performed rather than authentication.
	 */
	private boolean useLocalAuthentication(Authentication authentication) {
		if(authentication instanceof PreAuthenticatedAuthenticationToken)
			return false; // Since preauthentication doesn't make password available, local authentication will fail no matter what
		
    	// Are we dealing with one of Teaming's system accounts such as "admin" or "guest"?
		if(BuiltInUsersHelper.isSystemUserAccount(authentication.getName()))
			return true; // must use local authentication
		
		if(!cacheUsingAuthenticators.contains(getAuthenticator()))
			return false; // must use regular authentication
		
		Long lastRegularAuthenticationTime = lastRegularAuthenticationTimes.get(authentication.getName());
		if(lastRegularAuthenticationTime == null) {
			if(logger.isDebugEnabled())
				logger.debug("user='" + authentication.getName() + "' authenticator='" + getAuthenticator() + "' - Use regular auth because first time");
			return false; // First time logging in through this server since the server started - must use regular authentication
		}
		
		if(System.currentTimeMillis() - lastRegularAuthenticationTime > cacheUsingAuthenticatorTimeout * 1000) {
			if(logger.isDebugEnabled())
				logger.debug("user='" + authentication.getName() + "' authenticator='" + getAuthenticator() + "' - Use regular auth because cache time limit has passed");
			return false; // The specified time limit has passed since the last time the user used regular authentication - must use regular authentication again
		}
		else {
			if(logger.isTraceEnabled())
				logger.trace("user='" + authentication.getName() + "' authenticator='" + getAuthenticator() + "' - Use local cache");
			return true; // Still within time limit. Can use locally cached credential.
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

    	if(nonLocalAuthenticators.containsKey(zone)) {
    		checkPasswordRequirement(authentication);
    		
       		Authentication result = null;
       		Object credentials = authentication.getCredentials();
       		
       		if(useLocalAuthentication(authentication)) {
       			// Skip non-local authentication. Authenticate only against local database.
     			SimpleProfiler.start( "3a-system account: localProviders.get(zone).authenticate(authentication)" );
    			result = localProviders.get(zone).authenticate(authentication);
     			SimpleProfiler.stop( "3a-system account: localProviders.get(zone).authenticate(authentication)" );

    			// This is not used for authentication or profile synchronization but merely to log the authenticator.
     			SimpleProfiler.start( "4a-system account: AuthenticationManagerUtil.authenticate()" );
    			AuthenticationManagerUtil.authenticate(AuthenticationServiceProvider.LOCAL,
    					getZoneModule().getZoneNameByVirtualHost(ZoneContextHolder.getServerName()),
    					(String) result.getName(), 
    					(String) credentials,
    					false, 
    					false,
    					false,
                        false,
    					true, 
    					(Map) result.getPrincipal(), getAuthenticator());			
     			SimpleProfiler.stop( "4a-system account: AuthenticationManagerUtil.authenticate()" );

    			return successfulAuthentication(result);
       		}
       		else {
        		AuthenticationServiceProvider authenticationServiceProvider = AuthenticationServiceProvider.UNKNOWN;
        		
        		// Try to do an ldap authentication.
        		// This will also try local authentication as fallback, if configured to do so.
	    		try {
	    			// Perform authentication
	    			SimpleProfiler.start( "3-authenticators.get(zone).authenticate(authentication)" );
	     			result = performAuthentication(zone, authentication);
	     			SimpleProfiler.stop( "3-authenticators.get(zone).authenticate(authentication)" );
	     			
	     			// If still here, the authentication was successful.
	     			updateLastRegularAuthenticationTimeIfApplicable(authentication);
	     			
	     			String loginName = getLoginName(result);
	     			
	     			authenticationServiceProvider = getAuthenticationServiceProvider(result);
	     			
	     			// Get default settings first
	     			boolean passwordAutoSynch = SPropsUtil.getBoolean("portal.password.auto.synchronize", true);
	     			boolean ignorePassword = SPropsUtil.getBoolean("portal.password.ignore", true);
	     			
	     			boolean createUser = SPropsUtil.getBoolean("authenticator.create.user." + getAuthenticator(), false);
                    boolean updateUser = SPropsUtil.getBoolean("authenticator.update.user." + getAuthenticator(), false);
                    boolean updateHomeFolder = SPropsUtil.getBoolean("authenticator.update.homefolder." + getAuthenticator(), false);
                    
	     			if(cacheUsingAuthenticators.contains(getAuthenticator())) {
	     				// This authenticator is set up to utilize cached credential. In this case,
	     				// we must allow caching of password (i.e, password sync). Otherwise,
	     				// the authenticator will fail very soon when it tries to utilize cached
	     				// credential in subsequent requests.
	     				passwordAutoSynch = true;
	     			}
	     			
	     			if(!createUser) {
	     				// If this authenticator is one that doesn't allow creation of user (e.g. rss, ical),
	     				// then don't allow password synchronization either.
	     				passwordAutoSynch = false;
	     			}
                    	     			
	     			if(AuthenticationServiceProvider.OPENID == authenticationServiceProvider) {
	     				// If OpenID, override the default settings. OpenID is applicable only with web authenticator.
	     				// Don't allow OpenID user to self provision himself.
	     				createUser = false;
	     				passwordAutoSynch = false;
	     				ignorePassword = true;
	     			}

	     			if(AuthenticationServiceProvider.PRE == authenticationServiceProvider) {
	     				// If preauthentication, override the default settings.	    	     				
	     				createUser = false; // Don't allow preauthenticated user to self provision himself.
	     				passwordAutoSynch = false; // Preauthentication doesn't make password available.
	     				ignorePassword = true; // Authentication has already been done. The only thing left is validation.
	     			}

	     			SimpleProfiler.start( "4-AuthenticationManagerUtil.authenticate" );
	    			AuthenticationManagerUtil.authenticate(authenticationServiceProvider,
	    					getZoneModule().getZoneNameByVirtualHost(ZoneContextHolder.getServerName()),
	    					loginName, 
	    					(String) credentials,
	    					createUser,
	    					updateUser,
                            updateHomeFolder,
                            passwordAutoSynch,
	    					ignorePassword,
	    					(Map) result.getPrincipal(), 
	    					getAuthenticator());
	     			SimpleProfiler.stop( "4-AuthenticationManagerUtil.authenticate" );

	     			if(result instanceof SynchNotifiableAuthentication)
	    				((SynchNotifiableAuthentication)result).synchDone();
	    			return successfulAuthentication(result);
	    		} catch(UserAccountNotActiveException e) {
	    			UserIdNotActiveException unaEx = new UserIdNotActiveException(e.getMessage());
	    			unaEx.setApiErrorCode(e.getApiErrorCode());
	    			unaEx.setUserId(e.getUserId());
	    			exc = unaEx;
	    		} catch(UserDoesNotExistException e) {
	    			if(authenticationServiceProvider == AuthenticationServiceProvider.OPENID)
	    				exc = new UserAccountNotProvisionedException(e.getMessage());
				} catch(AuthenticationException e) {
					exc = e;
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
    	}
    	
    	if(exc != null)
    		throw exc;
    	else
    		throw new UsernameNotFoundException("No such user " + authentication.getName());
	}
	
	private Authentication successfulAuthentication(Authentication result) {
		if(LoginAudit.AUTHENTICATOR_WEB.equals(getAuthenticator()))
			GangliaMonitoring.addLoggedInUser(getLoginName(result)); // This metric is applicable only with web client (browser)
		return result;
	}
	
	private Authentication unsuccessfulAuthentication(Authentication result) {
		if(LoginAudit.AUTHENTICATOR_WEB.equals(getAuthenticator()))
		{
			GangliaMonitoring.incrementFailedLogins(); // This metric is applicable only with web client (browser)
		}
		
		recordFailedAuthentication( result );

		return result;
	}
	
	private String getAuthenticator() {
		String authenticator = AuthenticationContextHolder.getAuthenticator();
		if(authenticator == null)
			authenticator = LoginAudit.AUTHENTICATOR_UNKNOWN;
		return authenticator;
	}
	
	private boolean isAuthenticatedUserInternal(Authentication authentication) {
		if(authentication instanceof IdentityInfoObtainable) {
			// With local authentication, authentication is made against Vibe database, and therefore
			// identity info is always obtainable (which could represent either internal or external user).
			// With LDAP authentication, identity info is directly obtainable only if the authentication
			// was made against Vibe database which can happen when the LDAP server is down.
			return ((IdentityInfoObtainable)authentication).isInternal();
		}
		else {
			// If here, it implies that the authentication service was provided by anything but local.
			
			if (getAuthenticationServiceProvider(authentication) == AuthenticationServiceProvider.OPENID)
				return false;
			else
				return true;
		}
	}
	
	private AuthenticationServiceProvider getAuthenticationServiceProvider(Authentication authentication) {
		if(authentication instanceof LocalAuthentication)
			return AuthenticationServiceProvider.LOCAL; // identity source is either local or LDAP
		else if(authentication instanceof OpenIDAuthenticationToken)
			return AuthenticationServiceProvider.OPENID; // identity source id OpenID
		else if(authentication instanceof PreAuthenticatedAuthenticationToken) // identity source is most likely LDAP
			return AuthenticationServiceProvider.PRE;
		else
			return AuthenticationServiceProvider.LDAP; // identity source is LDAP
	}
	
	private String getLoginName(Authentication result) {
		String loginName;
		if(result instanceof PreAuthenticatedAuthenticationToken) {
			PreAuthenticatedAuthenticationToken preAuth = (PreAuthenticatedAuthenticationToken) result;
			// To take care of Integrated Windows Authentication through integration with IIS
			loginName = WindowsUtil.getSamaccountname((String) result.getName());
		}
		else {
			loginName = (String) result.getName();
		}
		return loginName;
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
	@Override
	public boolean supports(Class authentication) {
		if(SPropsUtil.getBoolean("authentication.supports.compute.dynamically", false)) {
			// Compute the result dynamically.
			Long zone = getZoneModule().getZoneIdByVirtualHost(
					ZoneContextHolder.getServerName());
	    	try {
	    		ensureZoneIsConfigured(zone);
	    	} catch(Exception e) {
	    		logger.error("Unable to configure authentication for zone " + zone, e);
	    		throw new AuthenticationServiceException("Unable to configure authentication for zone " + zone, e);
	    	}
			if (nonLocalAuthenticators.containsKey(zone)) {
				ProviderManager pm = nonLocalAuthenticators.get(zone);
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
		else {
			// Do not compute the result dynamically. Instead, just return true based on the observation
			// that this call never returns false in the context of our application. The reason for this
			// shortcut is to avoid the cost of repeating the same computation (which we repeat again 
			// in the doAuthenticate() method).
			return true;
		}
	}

	private Authentication performAuthentication(Long zoneId, Authentication authentication) throws AuthenticationException {
		AuthenticationException exc = null;
		ProviderManager pm = nonLocalAuthenticators.get(zoneId);
		boolean mustSkipLocalAuthentication = false;
		if(pm != null) {
			try {
				return pm.authenticate(authentication);
			}
			catch(AuthenticationException e) {
				if(logger.isDebugEnabled())	
					logger.debug("External authentication failed: " + e.toString());
				exc = e;

				// Did we find the user in the ldap directory?
				if ( authenticateLdapMatchingUsersUsingLdapOnly && e instanceof UsernameNotFoundException )
				{
					User user = null;
					
					// No
					// Does this user exist in the db?
					try
					{
						user = getProfileDao().findUserByName( authentication.getName(), zoneId );
					}
					catch ( Exception ex )
					{
						// Nothing to do.
					}
					
					if ( user != null )
					{
						IdentityInfo identityInfo;
						
						// Yes
						// Did this user come from ldap?
						identityInfo = user.getIdentityInfo();
						if ( identityInfo != null && identityInfo.isFromLdap() )
						{
							// Yes
							// Because this user came from ldap and we can find them in ldap
							// don't do a local authentication
							mustSkipLocalAuthentication = true;
						}
					}
				}
				
				if ( mustSkipLocalAuthentication == false )
				{
					if ( (authenticateLdapMatchingUsersUsingLdapOnly && (e instanceof BadCredentialsException) && !(e instanceof UsernameNotFoundException))
						|| (authentication instanceof OpenIDAuthenticationToken)
						|| (e instanceof AccountStatusException) )
					{
						mustSkipLocalAuthentication = true;
					}
				}
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
	
	private void checkPasswordRequirement(Authentication authentication) {
		if(authentication instanceof OpenIDAuthenticationToken || authentication instanceof PreAuthenticatedAuthenticationToken)
			return;
		
		// Don't allow anyone to log in without specifying a password (to prevent successful
		// authentication on an account that has no password).
		if(authentication.getCredentials() == null || authentication.getCredentials().equals(""))
			throw new BadCredentialsException("Password is required");
	}
	
	private LdapModule getLdapModule() {
		return (LdapModule) SpringContextUtil.getBean("ldapModule");
	}
	
	/**
	 * Record the failed authentication attempt
	 */
	private void recordFailedAuthentication( Authentication authentication )
	{
		String ipAddr;
		Date now;
		
		if ( m_recordFailedAuthentications == false )
			return;
		
		if ( m_failedAuthenticationHistory == null )
			m_failedAuthenticationHistory = FailedAuthenticationHistory.getFailedAuthenticationHistory();

		ipAddr = ZoneContextHolder.getClientAddr();
		if ( ipAddr == null || ipAddr.length() == 0 )
			ipAddr = "unknown";

		now = new Date();
		m_failedAuthenticationHistory.addFailure( authentication, ipAddr, now.getTime() );
	}
	
	/**
	 * 
	 */
	@Override
	public boolean isBruteForceAttackInProgress( String userId )
	{
		if ( m_failedAuthenticationHistory != null )
			return m_failedAuthenticationHistory.isBruteForceAttackInProgress( userId );
		
		return false;
	}
	
	/**
	 * 
	 */
	@Override
	public void clearFailedAuthenticationHistory( String userId )
	{
		if ( m_failedAuthenticationHistory != null )
			m_failedAuthenticationHistory.clearHistory( userId );
	}
	
	/**
	 * 
	 */
	@Override
	public boolean doesAuthenticationRequireCaptcha( String authenticatorName, String userId )
	{
		if ( authenticatorName != null && authenticatorName.equalsIgnoreCase( LoginAudit.AUTHENTICATOR_WEB ) )
		{
			return isBruteForceAttackInProgress( userId );
		}
			
		return false;
		
	}
	
	/**
	 * 
	 */
	private boolean isCaptchaValid( Authentication authentication )
	{
		HttpServletRequest httpServletRequest;
		String text = null;
		
		httpServletRequest = ZoneContextHolder.getHttpServletRequest();
		if ( httpServletRequest != null )
			text = httpServletRequest.getParameter( WebKeys.TEXT_VERIFICATION_RESPONSE );
		
		return Utils.isCaptchaValid( httpServletRequest, text );
	}
}
