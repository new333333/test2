package com.sitescape.team.spring.web.portlet.context.support;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

public class XmlPortletApplicationContext extends org.springframework.web.portlet.context.XmlPortletApplicationContext {

	private static final Log logger = LogFactory.getLog(XmlPortletApplicationContext.class);
	
	private boolean[] optional;

	public void setConfigLocations(String[] locations) {
		optional = new boolean[locations.length];
		String[] locs = new String[locations.length];
		for(int i = 0; i < locations.length; i++) {
			if(locations[i].startsWith("optional:")) {
				locs[i] = locations[i].substring(9);
				optional[i] = true;
			}
			else {
				locs[i] = locations[i];
				optional[i] = false;
			}
		}
		super.setConfigLocations(locs);
	}

	protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws BeansException, IOException {
		String[] configLocations = getConfigLocations();
		if (configLocations != null) {
			for (int i = 0; i < configLocations.length; i++) {
				try {
					reader.loadBeanDefinitions(getResources(configLocations[i]));
				}
				catch(BeansException e) {
					if(optional[i])
						logger.debug("Cannot load optional config file " + configLocations[i]);
					else
						throw e;
				}
			}
		}
	}

}
