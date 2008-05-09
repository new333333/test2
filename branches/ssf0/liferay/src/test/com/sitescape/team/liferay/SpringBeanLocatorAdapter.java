/**
 * 
 */
package com.sitescape.team.liferay;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.liferay.portal.kernel.bean.BeanLocator;
import com.liferay.portal.kernel.bean.BeanLocatorException;
import com.liferay.portal.kernel.bean.BeanLocatorUtil;

/**
 * A {@link BeanLocator} implementation which uses the Spring
 * {@link ApplicationContext} for all lookups. This class also registers itself
 * with the {@link BeanLocatorUtil} on instantiation so it should <strong>only</strong>
 * be used for testing and <strong>only</strong> because of Liferay's abuses of
 * dependency injection.
 * 
 * @author dml
 * 
 */
public class SpringBeanLocatorAdapter implements BeanLocator,
		ApplicationContextAware {

	private ApplicationContext applicationContext;

	public SpringBeanLocatorAdapter() {
		BeanLocatorUtil.setBeanLocator(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.liferay.portal.kernel.bean.BeanLocator#locate(java.lang.String)
	 */
	@Override
	public Object locate(String name) throws BeanLocatorException {
		return applicationContext.getBean(name);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

}
