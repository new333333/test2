package com.sitescape.ef.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.NoSuchMessageException;

import com.sitescape.ef.SingletonViolationException;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.User;

/**
 * @author hurley
 *
 */
public class NLT implements ApplicationContextAware {
	
	protected static Log logger = LogFactory.getLog(NLT.class);
	
	private static NLT nlt; // Singleton instance

	private ApplicationContext ac;
	
	public NLT() {
		if(nlt == null)
			nlt = this;
		else
			throw new SingletonViolationException(NLT.class);
	}
	
	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		this.ac = ac;
	}
	
	protected ApplicationContext getApplicationContext() {
		return ac;
	}
	
	protected static NLT getInstance() {
		return nlt;
	}
	
	public String getMessage(String tag) {
		User user = RequestContextHolder.getRequestContext().getUser();
    	String translation = "";
    	try {
    		translation = getApplicationContext().getMessage(tag, null, user.getLocale());
    	} catch (NoSuchMessageException e) {
    	    logger.warn(e);
    		translation = tag;
    	}
    	return translation;		
	}
	
	public static String get(String tag) {
		return getInstance().getMessage(tag);
	}

	public String getMessage(String tag, String text) {
		User user = RequestContextHolder.getRequestContext().getUser();
    	String translation = "";
    	try {
    		String notfound = "___notfound___";
    		translation = getApplicationContext().getMessage(tag, null, notfound, user.getLocale());
    		if (translation.equals(notfound)) {
    			logger.warn("Translation not found: " + tag + " = " + text);
    			translation = text;
    		}
    	} catch (NoSuchMessageException e) {
    	    logger.warn(e);
    		translation = text;
    	}
    	return translation;
	}
	
	public static String get(String tag, String text) {
		return getInstance().getMessage(tag, text);
	}
}
