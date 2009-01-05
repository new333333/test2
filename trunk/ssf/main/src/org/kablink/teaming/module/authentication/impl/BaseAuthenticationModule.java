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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.AuthenticationConfig;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.LdapConnectionConfig;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.module.authentication.AuthenticationModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.util.Validator;

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
				getZoneModule().getZoneConfig(RequestContextHolder
						.getRequestContext().getZoneId()),
				WorkAreaOperation.ZONE_ADMINISTRATION);
	}

	public List<LdapConnectionConfig> getLdapConnectionConfigs() {
		return getLdapConnectionConfigs(RequestContextHolder
				.getRequestContext().getZoneId());
	}

	public List<LdapConnectionConfig> getLdapConnectionConfigs(Long zoneId) {
		return getCoreDao().loadLdapConnectionConfigs(zoneId);
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
		ZoneConfig zoneConfig = getZoneModule().getZoneConfig(zoneId);
		zoneConfig.getAuthenticationConfig().markAsUpdated();
	}
	
	public AuthenticationConfig getAuthenticationConfig()
	{
		return getAuthenticationConfigForZone(RequestContextHolder.getRequestContext().getZoneId());
	}
	
	public AuthenticationConfig getAuthenticationConfigForZone(Long zoneId)
	{
		ZoneConfig config =(ZoneConfig) getCoreDao().load(ZoneConfig.class, zoneId);
		return config.getAuthenticationConfig();
	}
	public void setAuthenticationConfig(AuthenticationConfig authConfig) {
		setAuthenticationConfigForZone(RequestContextHolder.getRequestContext().getZoneId(), authConfig);
	}
	public void setAuthenticationConfigForZone(Long zoneId, AuthenticationConfig authConfig) {
		ZoneConfig zoneConfig = getZoneModule().getZoneConfig(zoneId);
		zoneConfig.getAuthenticationConfig().setAllowAnonymousAccess(authConfig.isAllowAnonymousAccess());
		zoneConfig.getAuthenticationConfig().setAllowLocalLogin(authConfig.isAllowLocalLogin());
		zoneConfig.getAuthenticationConfig().setAllowSelfRegistration(authConfig.isAllowSelfRegistration());
	}

	public Set<String>getMappedAttributes(Principal principal) {
		Set<String>attributes = new HashSet();
		
		if (principal.getEntityType().equals(EntityIdentifier.EntityType.user)) {
			getPortalAttributes(attributes);
			if (principal.getName().equalsIgnoreCase(principal.getForeignName())) { //not synched with ldap
				return attributes;
			}
			for (LdapConnectionConfig config : getLdapConnectionConfigs(principal.getZoneId())) {
				attributes.addAll(config.getMappings().values());
			}
		} else if (principal.getEntityType().equals(EntityIdentifier.EntityType.group)) {
			// for groups the name and foreignname are the same, so look for signs of ldap
			if (principal.getName().contains("=")) {
				List mappings  = SZoneConfig.getElements("ldapConfiguration/groupMapping/mapping");
				for(int i=0; i < mappings.size(); i++) {
					Element next = (Element) mappings.get(i);
					attributes.add(next.attributeValue("to"));
				}
			}
		} 
		return attributes;
	}
	private void getPortalAttributes(Set<String>attributes) {
		if (!"standalone".equals(SPropsUtil.getString("deployment.portal"))) {
			String[] props = SPropsUtil.getStringArray("portal.user.auto.synchronize", ",");
			attributes.addAll(Arrays.asList(props));
		}
	}

}