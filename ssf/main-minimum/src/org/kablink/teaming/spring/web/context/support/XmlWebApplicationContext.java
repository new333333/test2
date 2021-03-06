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
package org.kablink.teaming.spring.web.context.support;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.util.BootstrapProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.util.StringUtils;

public class XmlWebApplicationContext extends org.springframework.web.context.support.XmlWebApplicationContext {

	private static final Log logger = LogFactory.getLog(XmlWebApplicationContext.class);
	
	private boolean[] optional;
	
	public void setConfigLocations(String[] locations) {
		String bsp = BootstrapProperties.getProperty("spring.context.config.location");
		if(bsp != null)
			locations = StringUtils.tokenizeToStringArray(bsp, CONFIG_LOCATION_DELIMITERS);
		
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
					reader.loadBeanDefinitions(configLocations[i]);
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
