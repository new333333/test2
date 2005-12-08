package com.sitescape.ef.util;

import java.util.HashMap;
import java.util.Map;


import org.hibernate.HibernateException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.property.BasicPropertyAccessor;
import org.hibernate.property.Getter;
import org.hibernate.property.Setter;

/**
 *
 * @author Jong Kim
 */
public class InvokeUtil {
    
    private static final String DELIM = "|";
    private static BasicPropertyAccessor propertyAccessor = new BasicPropertyAccessor();
    private static Map getters = new HashMap(); // Cache of getters
    private static Map setters = new HashMap(); // Cache of setters
    private static SessionFactoryImplementor factory;
    private static boolean factorySpecified = false;
    
    public static Object invokeGetter(Object target, String propertyName) 
    	throws com.sitescape.ef.util.ObjectPropertyNotFoundException, InvokeException {
        String key = makeKey(target, propertyName);
        Getter getter = (Getter) getters.get(key);
        
        if(getter == null) {
            try {
                getter = propertyAccessor.getGetter(target.getClass(), propertyName);
            } catch (PropertyNotFoundException e) {
                throw new com.sitescape.ef.util.ObjectPropertyNotFoundException(e);
            }
            getters.put(key, getter);
        }
        
        try {
            return getter.get(target);
        } catch (HibernateException e) {
            throw new InvokeException(e);
        }
    }
    public static void invokeSetter(Object target, String propertyName, Object value) 
		throws com.sitescape.ef.util.ObjectPropertyNotFoundException, InvokeException {
    	String key = makeKey(target, propertyName);
    	Setter setter = (Setter) setters.get(key);
    
    	if (setter == null) {
    		try {
    			setter = propertyAccessor.getSetter(target.getClass(), propertyName);
    		} catch (PropertyNotFoundException e) {
    			throw new com.sitescape.ef.util.ObjectPropertyNotFoundException(e);
    		}
    		setters.put(key, setter);
    	}
    	
    	try {
    		if(!factorySpecified)
    			setSessionFactoryImplementor((SessionFactoryImplementor)SpringContextUtil.getBean("sessionFactory"));
    		
    		setter.set(target, value, factory);

    	} catch (HibernateException e) {
    		throw new InvokeException(e);
    	}
    }    
    private static String makeKey(Object target, String propertyName) {
        return target.getClass().getName() + DELIM + propertyName;
    }
    static void setSessionFactoryImplementor(SessionFactoryImplementor f) {
    	factory = f;
    	factorySpecified = true;
    }
}
