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
package org.kablink.teaming.util;
import java.io.File;

import javax.servlet.ServletContext;

import org.kablink.teaming.SingletonViolationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.web.context.ServletContextAware;

/**
 * It is strongly suggested to use Spring's regular dependency injection
 * capability for obtaining references to other beans. 
 * Use <code>SpringContextUtil</code> only under the rare circumstances
 * where it is extremely difficult or impossible to use regular dependency
 * injection, for example, for integration with legacy system, etc.
 *  
 * @author Jong Kim
 *
 */
public class SpringContextUtil implements ApplicationContextAware, ServletContextAware, InitializingBean {
	// This is a singleton class.
	
	private static SpringContextUtil sc; // singleton instance
	protected String webRootName;
	protected String webappRootDir;
	protected ApplicationContext ac;
	protected ServletContext servletContext;
	
	public SpringContextUtil() {
		if(sc == null)
			sc = this;
		else
			throw new SingletonViolationException(SpringContextUtil.class);
	}
	
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        this.ac = ac;
    } 

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
    
	public void afterPropertiesSet() throws Exception {
        File file = ac.getResource("").getFile();

        this.webappRootDir = file.getAbsolutePath();
	}

    public static String getWebappRootDirPath() {
    	return getInstance().webappRootDir;
    }
    
	protected static SpringContextUtil getInstance() {
		return sc;
	}	
	protected static ConfigurableListableBeanFactory getBeanFactory() {
		return ((AbstractApplicationContext) getInstance().ac).getBeanFactory();
	}
	
    public static void applyDependencies(Object externalBean, String beanSpringName) { 
        ConfigurableListableBeanFactory configurableListableBeanFactory = 
        	getBeanFactory();
        AbstractBeanDefinition bd = (AbstractBeanDefinition) 
        	configurableListableBeanFactory.getBeanDefinition(beanSpringName); 
        int autowireMode = bd.getAutowireMode(); 
        if(autowireMode == AutowireCapableBeanFactory.AUTOWIRE_BY_NAME || 
                autowireMode == AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE) { 
            configurableListableBeanFactory.autowireBeanProperties
            	(externalBean, autowireMode ,true); 
        } else { 
            configurableListableBeanFactory.applyBeanPropertyValues
            	(externalBean, beanSpringName); 
        } 
    }
    
    public static Object getBean(String name) {
        return getInstance().ac.getBean(name);
    }

	public static ServletContext getServletContext() {
		return getInstance().servletContext;
	}
	
    /*
     * I don't want to expose this method unless it is absolutely necessary. 
    public static ApplicationContext getApplicationContext() {
    	return getInstance().ac;
    }
    */
}