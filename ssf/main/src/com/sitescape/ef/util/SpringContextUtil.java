package com.sitescape.ef.util;
import java.io.File;
import java.io.IOException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import com.sitescape.ef.SingletonViolationException;
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
public class SpringContextUtil implements ApplicationContextAware {
	// This is a singleton class.
	
	private static SpringContextUtil sc; // singleton instance
	protected String webRootName;
	protected ApplicationContext ac;
	
	public SpringContextUtil() {
		if(sc == null)
			sc = this;
		else
			throw new SingletonViolationException(SpringContextUtil.class);
	}
	
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        this.ac = ac;
    } 
    public void setWebRoot(Resource webRoot) throws IOException {
    	//resolve serlet context now
    	File file = webRoot.getFile(); 
    	getInstance().webRootName = file.getAbsolutePath();
    }
    public static String getWebRootName() {
    	return getInstance().webRootName;
    }
	protected static SpringContextUtil getInstance() {
		return sc;
	}	
	
    public static void applyDependencies(Object externalBean, String beanSpringName) { 
        ConfigurableListableBeanFactory configurableListableBeanFactory = 
            (ConfigurableListableBeanFactory) getInstance().ac; 
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

    /*
     * I don't want to expose this method unless it is absolutely necessary. 
    public static ApplicationContext getApplicationContext() {
    	return getInstance().ac;
    }
    */
}