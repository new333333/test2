package com.sitescape.ef.util.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

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
public class SpringContextUtil implements BeanFactoryAware {
	// This is a singleton class.
	
	private static SpringContextUtil sc; // singleton instance
	
	private BeanFactory beanFactory;
	
	public SpringContextUtil() {
		if(sc == null)
			sc = this;
		else
			throw new SingletonViolationException(SpringContextUtil.class);
	}
	
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    } 
    
    protected BeanFactory getBeanFactory() {
        return beanFactory;
    } 

	protected static SpringContextUtil getInstance() {
		return sc;
	}	
	
    public static void applyDependencies(Object externalBean, String beanSpringName) { 
        ConfigurableListableBeanFactory configurableListableBeanFactory = 
            (ConfigurableListableBeanFactory) getInstance().getBeanFactory(); 
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
        return getInstance().getBeanFactory().getBean(name);
    }

}