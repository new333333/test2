package com.sitescape.team.spring.security;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.AuthenticationServiceException;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.SpringSecurityContextSource;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.providers.AuthenticationProvider;
import org.springframework.security.providers.ProviderManager;
import org.springframework.security.providers.anonymous.AnonymousAuthenticationProvider;
import org.springframework.security.providers.ldap.LdapAuthenticationProvider;
import org.springframework.security.providers.ldap.LdapAuthenticator;
import org.springframework.security.providers.ldap.authenticator.BindAuthenticator;

import com.sitescape.team.asmodule.zonecontext.ZoneContextHolder;
import com.sitescape.team.domain.AuthenticationConfig;
import com.sitescape.team.domain.ZoneInfo;
import com.sitescape.team.module.ldap.LdapModule;
import com.sitescape.team.module.zone.ZoneModule;
import com.sitescape.team.util.SZoneConfig;

public class ZoneAwareAuthenticationProvider implements AuthenticationProvider, InitializingBean {
	protected Log logger = LogFactory.getLog(getClass());

	private ZoneModule zoneModule;
	public ZoneModule getZoneModule() { return zoneModule; }
	public void setZoneModule(ZoneModule zoneModule) { this.zoneModule = zoneModule; }
	
	private LdapModule ldapModule;
	public LdapModule getLdapModule() { return ldapModule; }
	public void setLdapModule(LdapModule ldapModule) { this.ldapModule = ldapModule; }
	
	protected Map<Long, ProviderManager> authenticators = null;
	
	public ZoneAwareAuthenticationProvider()
	{
		authenticators = new HashMap<Long, ProviderManager>();
	}
	
	public void afterPropertiesSet() throws Exception
	{
		for(ZoneInfo zoneInfo : getZoneModule().getZoneInfos()) {
			logger.debug("Setting authentication info for zone " + zoneInfo.getZoneName() + ", host " + zoneInfo.getVirtualHost());
			ProviderManager pm = new ProviderManager();
			List<AuthenticationProvider> providers = createProvidersForZone(zoneInfo);

			SsfAnonymousAuthenticationProvider anonymousProvider = new SsfAnonymousAuthenticationProvider();
			anonymousProvider.setKey(getKeyForZone(zoneInfo));
			anonymousProvider.setZoneModule(getZoneModule());
			anonymousProvider.afterPropertiesSet();
			providers.add(anonymousProvider);

			pm.setProviders(providers);
			authenticators.put(zoneInfo.getZoneId(), pm);
		}
	}

	protected String getKeyForZone(ZoneInfo zoneInfo)
	{
		return SZoneConfig.getGuestUserName(zoneInfo.getZoneName());
	}

	protected List<AuthenticationProvider> createProvidersForZone(ZoneInfo zoneInfo) throws Exception
	{
		List<AuthenticationProvider> providers = new LinkedList<AuthenticationProvider>();
		for(AuthenticationConfig config : getLdapModule().getAuthenticationConfigs(zoneInfo.getZoneId())) {
			String search = "(" + config.getUserIdAttribute() + "={0})";
			if(config.getUserSearches().size() > 0) {
				DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(config.getUrl());
				contextSource.setAnonymousReadOnly(true);
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
					ldap.setUserDetailsContextMapper(contextMapper);
					providers.add(ldap);
				}
			}
		}
/*
 * This is how to programmatically create a provider.  You'll need this when you write
 * the UI for managing LDAP
 * 
		DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource("ldap://192.168.3.197:389/");
		contextSource.setAnonymousReadOnly(true);
		contextSource.afterPropertiesSet();
		
		BindAuthenticator authenticator = new BindAuthenticator(contextSource);
		FilterBasedLdapUserSearch userSearch = new FilterBasedLdapUserSearch("ou=aspendemocouid,o=novell","(uid={0})",contextSource);
		authenticator.setUserSearch(userSearch);
		LdapAuthenticationProvider ldap = new LdapAuthenticationProvider(authenticator);
		ldap.setUserDetailsContextMapper(new SsfContextMapper());
		providers.add(ldap);

		authenticator = new BindAuthenticator(contextSource);
		userSearch = new FilterBasedLdapUserSearch("o=novell","(uid={0})",contextSource);
		userSearch.setSearchSubtree(false);
		authenticator.setUserSearch(userSearch);
		ldap = new LdapAuthenticationProvider(authenticator);
		ldap.setUserDetailsContextMapper(new SsfContextMapper());
		providers.add(ldap);
*/
		
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
    		return authenticators.get(zone).authenticate(authentication);
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
}
