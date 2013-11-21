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
package org.kablink.teaming.module.interceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.spring.web.context.ContextLoaderListener;
import org.kablink.teaming.util.Constants;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SPropsUtil;
import org.springframework.beans.factory.InitializingBean;

public class EventListenerManagerInterceptor implements MethodInterceptor, InitializingBean {

	private static final String PROPERTY_PREFIX = "module.event.listeners.";
	
	protected Map<String, Object[]> moduleEventListeners;
	
    protected final Log logger = LogFactory.getLog(getClass());

	public Object invoke(MethodInvocation invocation) throws Throwable {
		String moduleInterfaceName = invocation.getMethod().getDeclaringClass().getName();
		Object[] listeners = moduleEventListeners.get(moduleInterfaceName);
		if(listeners == null) {
			// No listener registered for this module
			return invocation.proceed();
		}
		else {
			return doInvoke(invocation, listeners);
		}
	}

	public void afterPropertiesSet() throws Exception {
		moduleEventListeners = new HashMap();
		for(Enumeration e = SPropsUtil.getProperties().propertyNames(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			if(key.startsWith(PROPERTY_PREFIX)) {
				String moduleInterfaceName = key.substring(PROPERTY_PREFIX.length());
				String[] listenerClassNames = SPropsUtil.getStringArray(key, Constants.COMMA);
				if(listenerClassNames != null && listenerClassNames.length > 0)
					registerListeners(moduleInterfaceName, listenerClassNames);
			}
		}
	}
	
	protected Object doInvoke(MethodInvocation invocation, Object[] listeners) throws Throwable {
		if(ContextLoaderListener.isInitializationInProgress()) {
			// Spring context is in the process of being initialized. 
			// If we call module listeners in this case, we might encounter a problem if/when
			// some module listener is designed to depend on the Spring context having already
			// been initialized. To avoid such circular dependency, we refrain from invoking 
			// module listeners until the context initialization will have completed.
			// See Bug #634830 for related info.
			
			return invocation.proceed(); // Simply proceed with the actual module method.
		}
		else { // Let's invoke module listeners
			int listenerIndex = -1;
			
			try {
				// Apply pre-event methods of registered listeners.
				for(int i = 0; i < listeners.length; i++) {
					Object listener = listeners[i];
					Method preMethod = getPreMethod(invocation, listener);
					if(preMethod != null) {
						Object returnValueFromPreMethod = preMethod.invoke(listener, invocation.getArguments());
						if(Boolean.FALSE.equals(returnValueFromPreMethod)) {
							runAfterCompletion(listeners, listenerIndex, invocation, null);
							return null;
						}
					}
					listenerIndex = i;
				}
				
				// Invoke the actual module method
				Object returnValue = invocation.proceed();
				
				// Apply post-event methods of registered listeners.
				// These execute in the reverse order.
				for(int i = listeners.length - 1; i >= 0; i--) {
					runPostMethod(listeners[i], invocation, returnValue);
				}
				
				// Apply after-completion methods for successful outcome.
				runAfterCompletion(listeners, listenerIndex, invocation, null);
				
				return returnValue;
			}
			catch(Throwable t) {
				// Apply after-completion methods for thrown exception.
				runAfterCompletion(listeners, listenerIndex, invocation, t);
				if(t instanceof InvocationTargetException)
					throw ((InvocationTargetException) t).getTargetException();
				else
					throw t;
			}
		}
	}
	
	private String getPreMethodName(Method eventMethod) {
		String eventMethodName = eventMethod.getName();
		return "pre" + Character.toUpperCase(eventMethodName.charAt(0)) + eventMethodName.substring(1);
	}
	
	private Method getPreMethod(MethodInvocation invocation, Object listener) {
		Method eventMethod = invocation.getMethod();
		String preMethodName = getPreMethodName(eventMethod);
		Class listenerClass = listener.getClass();
		try {
			return listenerClass.getMethod(preMethodName, eventMethod.getParameterTypes());
		} catch (NoSuchMethodException e) {
			return null;
		}
	}
	
	private String getPostMethodName(Method eventMethod) {
		String eventMethodName = eventMethod.getName();
		return "post" + Character.toUpperCase(eventMethodName.charAt(0)) + eventMethodName.substring(1);
	}
	
	private String getAfterCompletionMethodName(Method eventMethod) {
		String eventMethodName = eventMethod.getName();
		return "afterCompletion" + Character.toUpperCase(eventMethodName.charAt(0)) + eventMethodName.substring(1);
	}
	
	private Method getAfterCompletionMethod(MethodInvocation invocation, Object listener) {
		Method eventMethod = invocation.getMethod();
		String afterCompletionMethodName = getAfterCompletionMethodName(eventMethod);
		Class listenerClass = listener.getClass();
		try {
			return listenerClass.getMethod(afterCompletionMethodName, appendClass(eventMethod.getParameterTypes(), Throwable.class));
		} catch (NoSuchMethodException e) {
			return null;
		}
	}
	
	private Class[] appendClass(Class[] classes, Class clas) {
		Class[] newClasses = new Class[classes.length + 1];
		System.arraycopy(classes, 0, newClasses, 0, classes.length);
		newClasses[classes.length] = clas;
		return newClasses;
	}
	
	private Object[] appendObject(Object[] objects, Object object) {
		Object[] newObjects = new Object[objects.length + 1];
		System.arraycopy(objects, 0, newObjects, 0, objects.length);
		newObjects[objects.length] = object;
		return newObjects;
	}
	
	private void runAfterCompletion(Object[] listeners, int listenerIndex, MethodInvocation invocation, Throwable t) {
		// Apply after-completion methods of registered interceptors.
		for(int i = listenerIndex; i >= 0; i--) {
			Object listener = listeners[i];
			Method afterCompletionMethod = getAfterCompletionMethod(invocation, listener);
			if(afterCompletionMethod != null) {
				try {
					afterCompletionMethod.invoke(listener, appendObject(invocation.getArguments(), t));
				}
				catch(Throwable t2) {
					logger.error(afterCompletionMethod.toString() + " threw exception", t2);
				}
			}
		}
	}
	
	private void runPostMethod(Object listener, MethodInvocation invocation, Object moduleMethodReturnValue) 
	throws Exception {
		Method eventMethod = invocation.getMethod();
		String postMethodName = getPostMethodName(eventMethod);
		Class listenerClass = listener.getClass();
		Class eventMethodReturnType = eventMethod.getReturnType();
		Class[] postMethodParameterTypes;
		if(eventMethodReturnType.getName().equals("void"))
			postMethodParameterTypes = eventMethod.getParameterTypes();
		else
			postMethodParameterTypes = appendClass(eventMethod.getParameterTypes(), eventMethodReturnType);
		Method postMethod;
		try {
			postMethod = listenerClass.getMethod(postMethodName, postMethodParameterTypes);
		} catch (NoSuchMethodException e) {
			postMethod = null;
		}
		if(postMethod != null) {
			Object[] postMethodArguments;
			if(eventMethodReturnType.getName().equals("void"))
				postMethodArguments = invocation.getArguments();
			else
				postMethodArguments = appendObject(invocation.getArguments(), moduleMethodReturnValue);
				
			postMethod.invoke(listener, postMethodArguments);
		}
	}
	
	private void registerListeners(String moduleInterfaceName, String[] listenerClassNames) {
		Object[] listeners = new Object[listenerClassNames.length];
		for(int i = 0; i < listenerClassNames.length; i++) {
			if(logger.isDebugEnabled())
				logger.debug("Registering event listener " + listenerClassNames[i] + " for module " + moduleInterfaceName);
			listeners[i] = ReflectHelper.getInstance(listenerClassNames[i]);
		}
		moduleEventListeners.put(moduleInterfaceName, listeners);
	}
	
}
