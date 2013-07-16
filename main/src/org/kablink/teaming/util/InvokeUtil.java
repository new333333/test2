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
    		
    		// Bug 740536  - Can't think of any situation where preserving leading/trailing spaces
    		// in the original input value is desirable. So we're adding this check at this low level.
    		if(value instanceof String)
    			value = ((String) value).trim();
    		
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
