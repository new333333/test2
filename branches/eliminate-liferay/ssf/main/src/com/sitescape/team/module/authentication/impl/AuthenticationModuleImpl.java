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

import com.sitescape.team.asmodule.zonecontext.ZoneContextHolder;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.dao.util.OrderBy;
import com.sitescape.team.domain.AuthenticationConfig;
import com.sitescape.team.domain.ZoneInfo;
import com.sitescape.team.module.authentication.AuthenticationModule;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.zone.ZoneModule;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.authentication.AuthenticationManagerUtil;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.spring.security.SpringAuthenticationBeans;
import com.sitescape.team.spring.security.SsfAnonymousAuthenticationProvider;
import com.sitescape.team.spring.security.SsfContextMapper;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.util.Validator;

public class AuthenticationModuleImpl extends CommonDependencyInjection implements AuthenticationModule, AuthenticationProvider, InitializingBean {
	protected Log logger = LogFactory.getLog(getClass());

	private ZoneModule zoneModule;
	public ZoneModule getZoneModule() { return zoneModule; }
	public void setZoneModule(ZoneModule zoneModule) { this.zoneModule = zoneModule; }
	
	private ProviderManager providerManager;
	public ProviderManager getProviderManager() { return providerManager; }
	public void setProviderManager(ProviderManager providerManager) { this.providerManager = providerManager; }

	protected Map<Long, ProviderManager> authenticators = null;
	protected Map<Long, SsfAnonymousAuthenticationProvider> anonymousProviders = null;
	
	public AuthenticationModuleImpl()
	{
		authenticators = new HashMap<Long, ProviderManager>();
		anonymousProviders = new HashMap<Long, SsfAnonymousAuthenticationProvider>();
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
		getAccessControlManager().checkOperation(RequestContextHolder.getRequestContext().getZone(), WorkAreaOperation.SITE_ADMINISTRATION);
	}

	public void afterPropertiesSet() throws Exception
	{
		for(ZoneInfo zoneInfo : getZoneModule().getZoneInfos()) {
			logger.debug("Setting authentication info for zone " + zoneInfo.getZoneName() + ", host " + zoneInfo.getVirtualHost());
			ProviderManager pm = new ProviderManager();
			SsfAnonymousAuthenticationProvider anonymousProvider = new SsfAnonymousAuthenticationProvider();
			anonymousProvider.setKey(getKeyForZone(zoneInfo));
			anonymousProvider.setZoneModule(getZoneModule());
			anonymousProvider.afterPropertiesSet();
			anonymousProviders.put(zoneInfo.getZoneId(), anonymousProvider);
			authenticators.put(zoneInfo.getZoneId(), pm);

			rebuildProvidersForZone(zoneInfo.getZoneId());
		}
		getProviderManager().getProviders().add(this);
	}

	protected void rebuildProvidersForZone(Long zoneId) throws Exception
	{
		ProviderManager pm = authenticators.get(zoneId);
		List<AuthenticationProvider> providers = createProvidersForZone(zoneId);
		providers.add(anonymousProviders.get(zoneId));

		pm.setProviders(providers);
	}

	protected String getKeyForZone(ZoneInfo zoneInfo)
	{
		return SZoneConfig.getGuestUserName(zoneInfo.getZoneName());
	}

	protected List<AuthenticationProvider> createProvidersForZone(Long zoneId) throws Exception
	{
		List<AuthenticationProvider> providers = new LinkedList<AuthenticationProvider>();
		for(AuthenticationConfig config : getAuthenticationConfigs(zoneId)) {
			String search = "(" + config.getUserIdAttribute() + "={0})";
			if(config.getUserSearches().size() > 0) {
				DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(config.getUrl());
				if(Validator.isNotNull(config.getPrincipal())) {
					contextSource.setUserDn(config.getPrincipal());
					contextSource.setPassword(config.getCredentials());
				} else {
					contextSource.setAnonymousReadOnly(true);
				}
				contextSource.afterPropertiesSet();
				
				SsfContextMapper contextMapper = new SsfContextMapper(getZoneModule(), config.getMappings());
	
				for(AuthenticationConfig.SearchInfo us : config.getUserSearches()) {
					BindAuthenticator authenticator = new BindAuthenticator(contextSource);
					String filter = search;
					if(us.getFilter()!= "") {
						filter = "(&"+search+us.getFilter()+")";
					}
					FilterBasedLdapUserSearch userSearch = new FilterBasedLdapUserSearch(us.getBaseDn(), filter, contextSource);
					if(!us.isSearchSubtree()) {
						userSearch.setSearchSubtree(false);
					}
					authenticator.setUserSearch(userSearch);
					LdapAuthenticationProvider ldap = new LdapAuthenticationProvider(authenticator);
					ldap.setUseAuthenticationRequestCredentials(true);
					ldap.setUserDetailsContextMapper(contextMapper);
					providers.add(ldap);
				}
			}
		}
		
/*
 * Don't forget to allow for custom authenticators.  This isn't how to do it, but it reminds you that
 * the custom authenticator beans are registered with SpringAuthenticationBeans
 *

		if(zoneInfo.getZoneName().equals("monkeyco")) {
			providers.add(SpringAuthenticationBeans.getInstance().findProvider(zoneInfo.getZoneId(), "ldapTwo"));
		} else {
			providers.add(SpringAuthenticationBeans.getInstance().findProvider(zoneInfo.getZoneId(), "dynamicLdap"));			
		}
*/
		if(providers.size() == 0) {
			providers.add(SpringAuthenticationBeans.getInstance().getDefaultProvider());
		}
		return providers;
	}

	/**
     * Performs authentication with the same contract as {@link
     * org.springframework.security.AuthenticationManager#authenticate(Authentication)}.
     * Delegates the authentication to the AuthenticationManager configured for the zone
     * to which the request was directed.
     *
     * @param authentication the authentication request object.
     *
     * @return a fully authenticated object including credentials. May return <code>null</code> if the
     *         <code>AuthenticationProvider</code> is unable to support authentication of the passed
     *         <code>Authentication</code> object. In such a case, the next <code>AuthenticationProvider</code> that
     *         supports the presented <code>Authentication</code> class will be tried.
     *
     * @throws AuthenticationException if authentication fails.
     */
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
    	Long zone = getZoneModule().getZoneIdByVirtualHost(ZoneContextHolder.getServerName());
    	if(authenticators.containsKey(zone)) {
    		Authentication result = authenticators.get(zone).authenticate(authentication);
    		if(result != null) {
    			AuthenticationManagerUtil.authenticate(getZoneModule().getZoneNameByVirtualHost(ZoneContextHolder.getServerName()),
    													(String) result.getName(), (String) result.getCredentials(),
    													true, true, true, (Map) result.getPrincipal(), null);
    		}
    		return result;
    	}
    	throw new AuthenticationServiceException("No authenticator configured for zone: " + zone);
    }

    /**
     * Returns <code>true</code> if this <Code>AuthenticationProvider</code> supports the indicated
     * <Code>Authentication</code> object.
     * Delegates the decision to the providers of the AuthenticationManager configured for the zone
     * to which the request was directed.
     * <p>
     * Returning <code>true</code> does not guarantee an <code>AuthenticationProvider</code> will be able to
     * authenticate the presented instance of the <code>Authentication</code> class. It simply indicates it can support
     * closer evaluation of it. An <code>AuthenticationProvider</code> can still return <code>null</code> from the
     * {@link #authenticate(Authentication)} method to indicate another <code>AuthenticationProvider</code> should be
     * tried.
     * </p>
     * <p>Selection of an <code>AuthenticationProvider</code> capable of performing authentication is
     * conducted at runtime the <code>ProviderManager</code>.</p>
     *
     * @param authentication DOCUMENT ME!
     *
     * @return <code>true</code> if the implementation can more closely evaluate the <code>Authentication</code> class
     *         presented
     */
    public boolean supports(Class authentication)
    {
    	Long zone = getZoneModule().getZoneIdByVirtualHost(ZoneContextHolder.getServerName());
    	if(authenticators.containsKey(zone)) {
    		for(Object o : authenticators.get(zone).getProviders()) {
    			AuthenticationProvider p = (AuthenticationProvider) o;
    			if(p.supports(authentication)) {
    				return true;
    			}
    		}
    	}
    	return SpringAuthenticationBeans.getInstance().getDefaultProvider().supports(authentication);
    }

    public List<AuthenticationConfig> getAuthenticationConfigs()
	{
		return getAuthenticationConfigs(RequestContextHolder.getRequestContext().getZoneId());
	}
	public List<AuthenticationConfig> getAuthenticationConfigs(Long zoneId)
	{
		FilterControls filter = new FilterControls(); 
		OrderBy order = new OrderBy();
		order.addColumn("position");	   
		filter.setOrderBy(order);

		return (List<AuthenticationConfig>) getCoreDao().loadObjects(AuthenticationConfig.class, filter, zoneId);
	}

	public void setAuthenticationConfigs(List<AuthenticationConfig> configs)
	{
		checkAccess(AuthenticationOperation.manageAuthentication);
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();

		int nextPosition = 10;
		for(AuthenticationConfig config : configs) {
			config.setZoneId(zoneId);
			config.setPosition(nextPosition);
			if(config.getId() != null) {
				getCoreDao().update(config);
			} else {
				getCoreDao().save(config);
			}
			nextPosition += 10;
		}
		
		HashMap<String, AuthenticationConfig> notFound = new HashMap<String, AuthenticationConfig>();
		for(AuthenticationConfig config : getAuthenticationConfigs(zoneId)) {
			notFound.put(config.getId(), config);
		}

		for(AuthenticationConfig config : configs) {
			notFound.remove(config.getId());
		}

		for(AuthenticationConfig config : notFound.values()) {
			getCoreDao().delete(config);
		}
		try {
			rebuildProvidersForZone(zoneId);
		} catch(Exception e) {
			logger.error("Unable to update authentication providers for zone " + zoneId, e);
		}
	}
}