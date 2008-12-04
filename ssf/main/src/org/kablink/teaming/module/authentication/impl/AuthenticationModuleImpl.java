/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */

package org.kablink.teaming.module.authentication.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.NoObjectByTheIdException;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.domain.AuthenticationConfig;
import org.kablink.teaming.domain.LdapConnectionConfig;
import org.kablink.teaming.domain.LoginInfo;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.security.authentication.AuthenticationManagerUtil;
import org.kablink.teaming.spring.security.SsfAuthenticationProvider;
import org.kablink.teaming.spring.security.SsfContextMapper;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.util.Validator;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.AuthenticationServiceException;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.providers.AuthenticationProvider;
import org.springframework.security.providers.ProviderManager;
import org.springframework.security.providers.ldap.LdapAuthenticationProvider;
import org.springframework.security.providers.ldap.authenticator.BindAuthenticator;
import org.springframework.security.userdetails.UsernameNotFoundException;

public class AuthenticationModuleImpl extends BaseAuthenticationModule
		implements AuthenticationProvider {
	protected Log logger = LogFactory.getLog(getClass());

	protected Map<Long, ProviderManager> authenticators = null;

	protected Map<Long, SsfAuthenticationProvider> localProviders = null;
	protected Map<Long, Long> lastUpdates = null;
	
	public AuthenticationModuleImpl() {
		authenticators = new HashMap<Long, ProviderManager>();
		localProviders = new HashMap<Long, SsfAuthenticationProvider>();
		lastUpdates = new HashMap<Long, Long>();
	}

	protected void addZone(ZoneConfig zoneConfig) throws Exception
	{
		String zoneName = getZoneModule().getZoneInfo(zoneConfig.getZoneId()).getZoneName();
		if(authenticators.containsKey(zoneConfig.getZoneId())) {
			logger.error("Duplicate zone added to AuthenticationModule: " + zoneConfig.getZoneId() + " " + zoneName);
			throw new Exception("Duplicate zone added to AuthenticationModule");
		}
		logger.debug("Setting authentication info for zone " + zoneName);
		ProviderManager pm = new ProviderManager();
		
		SsfAuthenticationProvider localProvider = new SsfAuthenticationProvider(zoneName);
		localProviders.put(zoneConfig.getZoneId(), localProvider);
		
		authenticators.put(zoneConfig.getZoneId(), pm);
		
		rebuildProvidersForZone(zoneConfig);
	}
	
	protected void removeZone(Long zoneId)
	{
		if(authenticators.containsKey(zoneId)) {
			authenticators.remove(zoneId);
			localProviders.remove(zoneId);
			lastUpdates.remove(zoneId);
		}
	}

	protected void ensureZoneIsConfigured(Long zoneId) throws Exception
	{
		try {
			ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(zoneId);
			if(! authenticators.containsKey(zoneId)) {
				addZone(zoneConfig);
			}
			AuthenticationConfig authConfig = zoneConfig.getAuthenticationConfig();
			if(authConfig.getLastUpdate().compareTo(lastUpdates.get(zoneId)) > 0) {
				try {
					rebuildProvidersForZone(zoneConfig);
				} catch(Exception e) {
					logger.error("Unable to rebuild providers for zone " + zoneId);
				}
			}
		}catch (NoObjectByTheIdException no) {
			removeZone(zoneId);
		} 
	}
	
	protected void rebuildProvidersForZone(ZoneConfig zoneConfig) throws Exception {
		ProviderManager pm = authenticators.get(zoneConfig.getZoneId());
		List<AuthenticationProvider> providers = createProvidersForZone(zoneConfig.getZoneId());
		if(zoneConfig.getAuthenticationConfig().isAllowLocalLogin()) {
			providers.add(localProviders.get(zoneConfig.getZoneId()));
		}

		pm.setProviders(providers);
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
					contextSource = new DefaultSpringSecurityContextSource(
						config.getUrl());
				} catch(Exception e) {
					logger.debug("Unable to create LDAP context for url: " + config.getUrl());
					continue;
				}
				if (Validator.isNotNull(config.getPrincipal())) {
					contextSource.setUserDn(config.getPrincipal());
					contextSource.setPassword(config.getCredentials());
				} else {
					contextSource.setAnonymousReadOnly(true);
				}
				contextSource.afterPropertiesSet();

				SsfContextMapper contextMapper = new SsfContextMapper(
						getZoneModule(), config.getMappings());

				for (LdapConnectionConfig.SearchInfo us : config
						.getUserSearches()) {
					BindAuthenticator authenticator = new BindAuthenticator(
							contextSource);
					String filter = search;
					if (us.getFilter() != "") {
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
					providers.add(ldap);
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
    	Long zone = getZoneModule().getZoneIdByVirtualHost(ZoneContextHolder.getServerName());
    	try {
    		ensureZoneIsConfigured(zone);
    	} catch(Exception e) {
    		logger.error("Unable to configure authentication for zone " + zone);
    		throw new AuthenticationServiceException("Unable to configure authentication for zone " + zone, e);
    	}
    	if(authenticators.containsKey(zone)) {
       		Authentication result = null;
    		try {
     			result = authenticators.get(zone).authenticate(authentication);
    			AuthenticationManagerUtil.authenticate(getZoneModule().getZoneNameByVirtualHost(ZoneContextHolder.getServerName()),
    					(String) result.getName(), (String) result.getCredentials(),
    					(Map) result.getPrincipal(), LoginInfo.AUTHENTICATOR_PORTAL);
    			return result;
    		} catch(UsernameNotFoundException e) {
    		}
    		if(SZoneConfig.getAdminUserName(getZoneModule().getZoneNameByVirtualHost(ZoneContextHolder.getServerName())).equals(authentication.getName())) {
    			return localProviders.get(zone).authenticate(authentication);
    		}
    	}
    	throw new UsernameNotFoundException("No such user");
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
    		logger.error("Unable to configure authentication for zone " + zone);
    		throw new AuthenticationServiceException("Unable to configure authentication for zone " + zone, e);
    	}
		if (authenticators.containsKey(zone)) {
			for (Object o : authenticators.get(zone).getProviders()) {
				AuthenticationProvider p = (AuthenticationProvider) o;
				if (p.supports(authentication)) {
					return true;
				}
			}
			return (localProviders.get(zone).supports(authentication));
		}
		return false;
	}
}