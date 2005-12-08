package com.sitescape.ef.util;
import java.util.Locale;
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
	
	private Locale getLocale() {
		User user = RequestContextHolder.getRequestContext().getUser();
		if(user != null)
			return user.getLocale();
		else
			return Locale.getDefault();
	}
	
	private String getMessageWithTagAsDefault(String tag) {
		return getMessageWithTagAsDefault(tag, null, getLocale());
	}
	
	private String getMessageWithTagAsDefault(String tag, Object[] args) {
		return getMessageWithTagAsDefault(tag, args, getLocale());		
	}
	
	private String getMessageWithTagAsDefault(String tag, Locale locale) {
 		return getMessageWithTagAsDefault(tag, null, locale);
	}
	
	private String getMessageWithTagAsDefault(String tag, Object[] args, Locale locale) {
		return getMessageWithDefault(tag, args, tag, locale);
	}
	
	private String getMessageWithTextAsDefault(String tag, String text) {
		return getMessageWithTextAsDefault(tag, null, text, getLocale());
	}
	
	private String getMessageWithTextAsDefault(String tag, Object[] args, String text) {
		return getMessageWithTextAsDefault(tag, args, text, getLocale());
	}
	
	private String getMessageWithTextAsDefault(String tag, String text, Locale locale) {
		return getMessageWithTextAsDefault(tag, null, text, locale);
	}
	
	private String getMessageWithTextAsDefault(String tag, Object[] args, String text, Locale locale) {
		return getMessageWithDefault(tag, args, text, locale);
	}
	
	private String getMessageWithDefault(String tag, Object[] args, String defaultMessage, Locale locale) {
    	String translation = "";
    	try {
    		translation = getApplicationContext().getMessage(tag, args, locale);
    	} catch (NoSuchMessageException e) {
    	    logger.warn(e);
    		translation = defaultMessage;
    	}
    	return translation;				
	}
	
	public static String getDef(String tag) {
		if (tag.startsWith("__")) {
			//If the tag starts with "__" it is a tag to be translated
			return get(tag);
		}
		else {
			//Otherwise, this is just a string, so return it unchanged.
			return tag;
		}
	}

	public static String get(String tag) {
		return getInstance().getMessageWithTagAsDefault(tag);
	}
	
	public static String get(String tag, Object[] args) {
		return getInstance().getMessageWithTagAsDefault(tag, args);
	}

	public static String get(String tag, Locale locale) {
		return getInstance().getMessageWithTagAsDefault(tag, locale);
	}

	public static String get(String tag, Object[] args, Locale locale) {
		return getInstance().getMessageWithTagAsDefault(tag, args, locale);
	}

	public static String get(String tag, String text) {
		return getInstance().getMessageWithTextAsDefault(tag, text);
	}
	
	public static String get(String tag, Object[] args,  String text) {
		return getInstance().getMessageWithTextAsDefault(tag, args, text);
	}
	
	public static String get(String tag, String text, Locale locale) {
		return getInstance().getMessageWithTextAsDefault(tag, text, locale);
	}
	
	public static String get(String tag, Object[] args, String text, Locale locale) {
		return getInstance().getMessageWithTextAsDefault(tag, args, text, locale);
	}
}
