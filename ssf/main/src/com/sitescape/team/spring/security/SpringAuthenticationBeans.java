package com.sitescape.team.spring.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.DescriptiveResource;
import org.springframework.security.providers.AuthenticationProvider;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.SingletonViolationException;
import com.sitescape.team.domain.ZoneInfo;
import com.sitescape.team.module.zone.ZoneModule;
import com.sitescape.team.util.SpringContextUtil;

public class SpringAuthenticationBeans implements InitializingBean, ApplicationContextAware {
	private static SpringAuthenticationBeans instance; // A singleton instance

	protected Log logger = LogFactory.getLog(getClass());

	protected Map<Long, Map<String, AuthenticationProvider>> providersByZone;
	protected AuthenticationProvider defaultProvider = null;
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
	
	public AuthenticationProvider getDefaultProvider() {
		return defaultProvider;
	}

	public void setDefaultProvider(AuthenticationProvider defaultProvider) {
		this.defaultProvider = defaultProvider;
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
		if(defaultProvider == null) {
			throw new ConfigurationException("defaultProvider must be supplied for SpringAuthenticationBeans");
		}
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
				Long zoneId = getZoneModule().getZoneIdByZoneName(zoneName);
				Map m = (Map) zoneSpecificProviders.get(o);
				for(Object o2 : m.keySet()) {
					String name = (String) o2;
					AuthenticationProvider provider = (AuthenticationProvider) m.get(o2);
					addProviderToZone(zoneId, name, provider);
				}
			}
		}

		DefaultListableBeanFactory factory = new DefaultListableBeanFactory(ac);
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
		Document doc = DocumentHelper.parseText("<beans xmlns=\"http://www.springframework.org/schema/beans\"     xmlns:s=\"http://www.springframework.org/schema/security\"     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"     xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.0.xsd     http://www.springframework.org/schema/security http://www.springframework.org/schema/security/spring-security-2.0.1.xsd\">          <bean id=\"contextSourceC\" class=\"org.springframework.security.ldap.DefaultSpringSecurityContextSource\">         <constructor-arg value=\"ldap://192.168.3.197:389/\"/>     </bean>       <bean id=\"dynamicLdap\" class=\"org.springframework.security.providers.ldap.LdapAuthenticationProvider\"> 		<constructor-arg> 			<bean class=\"org.springframework.security.providers.ldap.authenticator.BindAuthenticator\"> 				<constructor-arg ref=\"contextSourceC\" /> 				<property name=\"userSearch\"> 					<bean id=\"userSearchOne\" class=\"org.springframework.security.ldap.search.FilterBasedLdapUserSearch\"> 					  <constructor-arg index=\"0\" value=\"ou=Field Service,ou=aspendemocouid,o=novell\"/> 					  <constructor-arg index=\"1\" value=\"(uid={0})\"/> 					  <constructor-arg index=\"2\" ref=\"contextSourceC\" /> 					  <property name=\"searchSubtree\" value=\"false\"/> 					</bean> 				</property> 			</bean> 		</constructor-arg> 		<property name=\"userDetailsContextMapper\"><ref bean=\"ssfContextMapper\"/></property>     </bean>  </beans>");
		org.dom4j.io.DOMWriter d4Writer = new org.dom4j.io.DOMWriter();
		org.w3c.dom.Document w3doc = d4Writer.write(doc);
		reader.registerBeanDefinitions(w3doc, new DescriptiveResource("foo"));
		
		addProviderToZone(getZoneModule().getZoneIdByZoneName("liferay.com"), "dynamicLdap", (AuthenticationProvider) factory.getBean("dynamicLdap"));
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
