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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class BaseAuthenticationModule extends CommonDependencyInjection
		implements AuthenticationModule {
	protected Log logger = LogFactory.getLog(getClass());

	private ZoneModule zoneModule;
    private TransactionTemplate transactionTemplate;

	public ZoneModule getZoneModule() {
		return zoneModule;
	}

	public void setZoneModule(ZoneModule zoneModule) {
		this.zoneModule = zoneModule;
	}

    protected TransactionTemplate getTransactionTemplate() {
        return transactionTemplate;
    }
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
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

	public LdapConnectionConfig getLdapConnectionConfig(String id) {
		return getCoreDao().loadLdapConnectionConfig(id, RequestContextHolder.getRequestContext().getZoneId());
	}

	public void saveLdapConnectionConfig(LdapConnectionConfig config) {
        checkAccess(AuthenticationOperation.manageAuthentication);
        if (config.getId() != null) {
            LdapConnectionConfig existing = getLdapConnectionConfig(config.getId());
            config.setPosition(existing.getPosition());
        } else {
            int position = getCoreDao().getMaxLdapConnectionConfigPosition(RequestContextHolder.getRequestContext().getZoneId());
            config.setPosition(position + 10);
        }
        _saveLdapConnectionConfig(config);
        ZoneConfig zoneConfig = getZoneModule().getZoneConfig(config.getZoneId());
        zoneConfig.getAuthenticationConfig().markAsUpdated();
    }

	public void _saveLdapConnectionConfig(final LdapConnectionConfig config) {

        getTransactionTemplate().execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                if (config.getId() != null) {
                    getCoreDao().merge(config);
                } else {
                    getCoreDao().save(config);
                }
                return null;
            }
        });
    }

	public void setLdapConnectionConfigs(List<LdapConnectionConfig> configs) {
		checkAccess(AuthenticationOperation.manageAuthentication);
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		int nextPosition = 10;
		for (LdapConnectionConfig config : configs) {
			config.setZoneId(zoneId);
			config.setPosition(nextPosition);
			if (config.getId() != null) {
				getCoreDao().merge(config);
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
		zoneConfig.getAuthenticationConfig().setAnonymousReadOnly(authConfig.isAnonymousReadOnly());
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