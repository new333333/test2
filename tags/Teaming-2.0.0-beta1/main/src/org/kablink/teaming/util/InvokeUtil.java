/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package org.kablink.teaming.util;

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
    	throws org.kablink.teaming.util.ObjectPropertyNotFoundException, InvokeException {
        String key = makeKey(target, propertyName);
        Getter getter = (Getter) getters.get(key);
        
        if(getter == null) {
            try {
                getter = propertyAccessor.getGetter(target.getClass(), propertyName);
            } catch (PropertyNotFoundException e) {
                throw new org.kablink.teaming.util.ObjectPropertyNotFoundException(e);
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
		throws org.kablink.teaming.util.ObjectPropertyNotFoundException, InvokeException {
    	String key = makeKey(target, propertyName);
    	Setter setter = (Setter) setters.get(key);
    
    	if (setter == null) {
    		try {
    			setter = propertyAccessor.getSetter(target.getClass(), propertyName);
    		} catch (PropertyNotFoundException e) {
    			throw new org.kablink.teaming.util.ObjectPropertyNotFoundException(e);
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
