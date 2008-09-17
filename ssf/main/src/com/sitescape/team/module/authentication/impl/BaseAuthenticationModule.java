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

public class BaseAuthenticationModule extends CommonDependencyInjection
		implements AuthenticationModule {
	protected Log logger = LogFactory.getLog(getClass());

	private ZoneModule zoneModule;

	public ZoneModule getZoneModule() {
		return zoneModule;
	}

	public void setZoneModule(ZoneModule zoneModule) {
		this.zoneModule = zoneModule;
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
		AuthenticationConfig config =(AuthenticationConfig) getCoreDao().load(AuthenticationConfig.class, zoneId);
		if(config == null) {
			config = new AuthenticationConfig();
			config.setZoneId(zoneId);
		}
		return config;
	}
}