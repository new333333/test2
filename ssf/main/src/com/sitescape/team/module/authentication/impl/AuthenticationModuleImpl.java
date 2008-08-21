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

package com.sitescape.team.module.authentication.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
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

import com.sitescape.team.asmodule.zonecontext.ZoneContextHolder;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.dao.util.OrderBy;
import com.sitescape.team.domain.AuthenticationConfig;
import com.sitescape.team.domain.LdapConnectionConfig;
import com.sitescape.team.domain.ZoneInfo;
import com.sitescape.team.module.authentication.AuthenticationModule;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.zone.ZoneModule;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.authentication.AuthenticationManagerUtil;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.spring.security.SpringAuthenticationBeans;
import com.sitescape.team.spring.security.SsfAuthenticationProvider;
import com.sitescape.team.spring.security.SsfContextMapper;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.util.Validator;

public class AuthenticationModuleImpl extends CommonDependencyInjection
		implements AuthenticationModule, AuthenticationProvider,
		InitializingBean {
	protected Log logger = LogFactory.getLog(getClass());

	private ZoneModule zoneModule;

	public ZoneModule getZoneModule() {
		return zoneModule;
	}

	public void setZoneModule(ZoneModule zoneModule) {
		this.zoneModule = zoneModule;
	}

	private ProviderManager providerManager;

	public ProviderManager getProviderManager() {
		return providerManager;
	}

	public void setProviderManager(ProviderManager providerManager) {
		this.providerManager = providerManager;
	}

	protected Map<Long, ProviderManager> authenticators = null;

	protected Map<Long, SsfAuthenticationProvider> localProviders = null;
	protected Map<Long, Long> lastUpdates = null;

	public AuthenticationModuleImpl() {
		authenticators = new HashMap<Long, ProviderManager>();
		localProviders = new HashMap<Long, SsfAuthenticationProvider>();
		lastUpdates = new HashMap<Long, Long>();
	}

	public boolean testAccess(AuthenticationOperation operation) {
		try {
			checkAccess(operation);
			return true;
		} catch (AccessControlException ac) {
			return false;
		}
	}

	protected void checkAccess(AuthenticationOperation operation) {
		getAccessControlManager().checkOperation(
				RequestContextHolder.getRequestContext().getZone(),
				WorkAreaOperation.SITE_ADMINISTRATION);
	}

	public void afterPropertiesSet() throws Exception {
		getProviderManager().getProviders().add(this);
	}

	protected void addZone(ZoneInfo zoneInfo) throws Exception
	{
		if(authenticators.containsKey(zoneInfo.getId())) {
			logger.error("Duplicate zone added to AuthenticationModule: " + zoneInfo.getId() + " " + zoneInfo.getZoneName());
			throw new Exception("Duplicate zone added to AuthenticationModule");
		}
		logger.debug("Setting authentication info for zone "
				+ zoneInfo.getZoneName() + ", host "
				+ zoneInfo.getVirtualHost());
		ProviderManager pm = new ProviderManager();
		
		SsfAuthenticationProvider localProvider = new SsfAuthenticationProvider(zoneInfo.getZoneName());
		localProviders.put(zoneInfo.getZoneId(), localProvider);
		
		authenticators.put(zoneInfo.getZoneId(), pm);
		
		rebuildProvidersForZone(zoneInfo.getZoneId());
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
		ZoneInfo zoneInfo = getZoneModule().getZoneInfo(zoneId);
		if(zoneInfo == null) {
			removeZone(zoneId);
		} else {
			if(! authenticators.containsKey(zoneId)) {
				addZone(zoneInfo);
			}
			AuthenticationConfig authConfig = getAuthenticationConfigForZone(zoneId);
			if(authConfig.getLastUpdate().compareTo(lastUpdates.get(zoneId)) > 0) {
				try {
					rebuildProvidersForZone(zoneId);
				} catch(Exception e) {
					logger.error("Unable to rebuild providers for zone " + zoneId);
				}
			}
		}
	}
	
	protected void rebuildProvidersForZone(Long zoneId) throws Exception {
		AuthenticationConfig authConfig = getAuthenticationConfigForZone(zoneId);
		
		ProviderManager pm = authenticators.get(zoneId);
		List<AuthenticationProvider> providers = createProvidersForZone(zoneId);
		if(authConfig.isAllowLocalLogin()) {
			providers.add(localProviders.get(zoneId));
		}

		pm.setProviders(providers);
		lastUpdates.put(zoneId, authConfig.getLastUpdate());
	}

	protected String getKeyForZone(ZoneInfo zoneInfo) {
		return SZoneConfig.getGuestUserName(zoneInfo.getZoneName());
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
    					true, true, true, (Map) result.getPrincipal(), null);
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

	public List<LdapConnectionConfig> getLdapConnectionConfigs() {
		return getLdapConnectionConfigs(RequestContextHolder
				.getRequestContext().getZoneId());
	}

	public List<LdapConnectionConfig> getLdapConnectionConfigs(Long zoneId) {
		FilterControls filter = new FilterControls();
		OrderBy order = new OrderBy();
		order.addColumn("position");
		filter.setOrderBy(order);

		return (List<LdapConnectionConfig>) getCoreDao().loadObjects(
				LdapConnectionConfig.class, filter, zoneId);
	}

	public void setLdapConnectionConfigs(List<LdapConnectionConfig> configs) {
		checkAccess(AuthenticationOperation.manageAuthentication);
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();

		int nextPosition = 10;
		for (LdapConnectionConfig config : configs) {
			config.setZoneId(zoneId);
			config.setPosition(nextPosition);
			if (config.getId() != null) {
				getCoreDao().update(config);
			} else {
				getCoreDao().save(config);
			}
			nextPosition += 10;
		}

		HashMap<String, LdapConnectionConfig> notFound = new HashMap<String, LdapConnectionConfig>();
		for (LdapConnectionConfig config : getLdapConnectionConfigs(zoneId)) {
			notFound.put(config.getId(), config);
		}

		for (LdapConnectionConfig config : configs) {
			notFound.remove(config.getId());
		}

		for (LdapConnectionConfig config : notFound.values()) {
			getCoreDao().delete(config);
		}
		AuthenticationConfig authConfig = getAuthenticationConfigForZone(zoneId);
		authConfig.markAsUpdated();
		getCoreDao().update(authConfig);
	}
	
	public AuthenticationConfig getAuthenticationConfig()
	{
		return getAuthenticationConfigForZone(RequestContextHolder.getRequestContext().getZoneId());
	}
	
	public AuthenticationConfig getAuthenticationConfigForZone(Long zoneId)
	{
		return (AuthenticationConfig) getCoreDao().load(AuthenticationConfig.class, zoneId);
	}
}