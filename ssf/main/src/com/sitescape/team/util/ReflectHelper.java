/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.util;

import java.beans.Introspector;
import java.lang.reflect.Method;

/**
 * @author Jong Kim
 *
 */
public class ReflectHelper {
	public static Class classForName(String name) throws ClassNotFoundException {
		try {
			return Thread.currentThread().getContextClassLoader().loadClass(name);
		}
		catch (Exception e) {
			return Class.forName(name);
		}
	} 
	
	public static Method getterMethod(Class theClass, String propertyName, boolean includeInherited) {
		Method[] methods = null;
	    if(includeInherited)
	        methods = theClass.getMethods();
	    else
	        methods = theClass.getDeclaredMethods();
		for (int i=0; i<methods.length; i++) {
			// only carry on if the method has no parameters
			if ( methods[i].getParameterTypes().length==0 ) {
				String methodName = methods[i].getName();
				
				// Try "get"
				if( methodName.startsWith("get") ) {
					String testStdMethod = Introspector.decapitalize( methodName.substring(3) );
					if( testStdMethod.equals(propertyName)) 
					    return methods[i];
					
				}
				
				// If not "get" then try "is"
				if( methodName.startsWith("is") ) {
					String testStdMethod = Introspector.decapitalize( methodName.substring(2) );
					if( testStdMethod.equals(propertyName)) 
					    return methods[i];
				}
			}
		}
		return null;
	}
}
