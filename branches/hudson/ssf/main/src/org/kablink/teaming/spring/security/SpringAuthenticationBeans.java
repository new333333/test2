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
package org.kablink.teaming.spring.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.SingletonViolationException;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.module.zone.ZoneUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.DescriptiveResource;
import org.springframework.security.authentication.AuthenticationProvider;


public class SpringAuthenticationBeans implements InitializingBean, ApplicationContextAware {
	private static SpringAuthenticationBeans instance; // A singleton instance

	protected Log logger = LogFactory.getLog(getClass());

	protected Map<Long, Map<String, AuthenticationProvider>> providersByZone;
	protected Map globalProviders = null;
	protected Map zoneSpecificProviders = null;
	protected ApplicationContext ac;
	
	private ZoneModule zoneModule;
	public ZoneModule getZoneModule() {
		return zoneModule;
	}
	public void setZoneModule(ZoneModule zoneModule) {
		this.zoneModule = zoneModule;
	}

	public void setApplicationContext(ApplicationContext ac) throws BeansException {
        this.ac = ac;
    } 

	public SpringAuthenticationBeans() {
		if(instance != null)
			throw new SingletonViolationException(SpringAuthenticationBeans.class);
		
		instance = this;
	}

	protected void addProviderToZone(Long zoneId, String name, AuthenticationProvider provider)
	{
		Map<String, AuthenticationProvider> authenticationBeans = providersByZone.get(zoneId);
		if(authenticationBeans.containsKey(name)) {
			throw new ConfigurationException("Duplicate authentication bean (" + name + ") in zone " + zoneId);
		}
		authenticationBeans.put(name, provider);
	}
	
	public static SpringAuthenticationBeans getInstance()
	{
		return instance;
	}
	
	public Map getGlobalProviders() {
		return globalProviders;
	}
	public void setGlobalProviders(Map globalProviders) {
		this.globalProviders = globalProviders;
		for(Object k : globalProviders.keySet()) {
			if(! (k instanceof String)) {
				throw new ConfigurationException("Provider names must be strings in configuration of SpringAuthenticationBeans");
			}
			if(! (globalProviders.get(k) instanceof AuthenticationProvider)) {
				throw new ConfigurationException("Providers must be instances of AuthenticationProvider in configuration of SpringAuthenticationBeans");
			}
		}
	}
	
	public Map getZoneSpecificProviders() {
		return zoneSpecificProviders;
	}
	public void setZoneSpecificProviders(Map zoneSpecificProviders) {
		this.zoneSpecificProviders = zoneSpecificProviders;
		for(Object o : zoneSpecificProviders.keySet()) {
			if(! (o instanceof String)) {
				throw new ConfigurationException("Zone names must be strings in configuration of SpringAuthenticationBeans");
			}
			if(! (zoneSpecificProviders.get(o) instanceof Map)) {
				throw new ConfigurationException("Zone names must have maps as their values in configuration of SpringAuthenticationBeans");
			}
			Map m = (Map) zoneSpecificProviders.get(o);
			for(Object k : m.keySet()) {
				if(! (k instanceof String)) {
					throw new ConfigurationException("Provider names must be strings in configuration of SpringAuthenticationBeans");
				}
				if(! (m.get(k) instanceof AuthenticationProvider)) {
					throw new ConfigurationException("Providers must be instances of AuthenticationProvider in configuration of SpringAuthenticationBeans");
				}
			}
		}
	}
	
	public void afterPropertiesSet() throws Exception
	{
		providersByZone = new HashMap<Long, Map<String, AuthenticationProvider>>();
		for(ZoneInfo zoneInfo : getZoneModule().getZoneInfos()) {
			Map<String, AuthenticationProvider> authenticationBeans = new HashMap<String, AuthenticationProvider>();
			providersByZone.put(zoneInfo.getZoneId(), authenticationBeans);
			if(globalProviders != null) {
				for(Object o : globalProviders.keySet()) {
					String name = (String) o;
					AuthenticationProvider provider = (AuthenticationProvider) globalProviders.get(o);
					addProviderToZone(zoneInfo.getZoneId(), name, provider);
				}
			}
		}

		if(zoneSpecificProviders != null) {
			for(Object o : zoneSpecificProviders.keySet()) {
				String zoneName = (String) o;
				if(! getZoneModule().zoneExists(zoneName)) {
					throw new ConfigurationException("No zone named " + zoneName + " exists while configuring SpringAuthenticationBeans");
				}
				Long zoneId = ZoneUtil.getZoneIdByZoneName(zoneName);
				Map m = (Map) zoneSpecificProviders.get(o);
				for(Object o2 : m.keySet()) {
					String name = (String) o2;
					AuthenticationProvider provider = (AuthenticationProvider) m.get(o2);
					addProviderToZone(zoneId, name, provider);
				}
			}
		}
	}
	
	public AuthenticationProvider findProvider(Long zoneId, String name)
	{
		if(! providersByZone.containsKey(zoneId)) {
			return null;
		}
		Map<String, AuthenticationProvider> authenticationBeans = providersByZone.get(zoneId);
		if(authenticationBeans.containsKey(name)) {
			return authenticationBeans.get(name);
		}
		return null;
	}
}
