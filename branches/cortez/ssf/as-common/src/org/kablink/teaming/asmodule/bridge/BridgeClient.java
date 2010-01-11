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
package org.kablink.teaming.asmodule.bridge;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class BridgeClient {

	private static final String BRIDGE_SERVER_CLASS_NAME = 
		"org.kablink.teaming.bridge.BridgeServer";
		
	private static Method bridgeServerInvokeMethod;
	
	private static Method bridgeServerInvokeBeanMethod;
	
	private static Method bridgeServerInvokeBeanWithoutContextMethod;
	
	private static Object bridgeServer;
	
	static {
		try {
			Class classObj = Class.forName(BRIDGE_SERVER_CLASS_NAME, 
					true, BridgeUtil.getClassLoader());
			
			bridgeServerInvokeMethod = classObj.getMethod("invoke", 
					new Class[] {String.class, String.class, String.class, String.class, Class[].class, Object[].class});
			
			bridgeServerInvokeBeanMethod = classObj.getMethod("invokeBean", 
					new Class[] {String.class, String.class, String.class, String.class, Class[].class, Object[].class});
			
			bridgeServerInvokeBeanWithoutContextMethod = classObj.getMethod("invokeBeanWithoutContext", 
					new Class[] {String.class, String.class, Class[].class, Object[].class});
			
			bridgeServer = classObj.newInstance();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static Object invoke(String contextZoneName, String contextUserName, String className, 
			String methodName, Class[] methodArgTypes, Object[] methodArgs)
	throws Exception {
		ClassLoader clSave = Thread.currentThread().getContextClassLoader();
		
		try {
			Thread.currentThread().setContextClassLoader(BridgeUtil.getClassLoader());
							
			return bridgeServerInvokeMethod.invoke(bridgeServer,
					new Object[] {contextZoneName, contextUserName, className, methodName, methodArgTypes, methodArgs});
		}
		finally {
			Thread.currentThread().setContextClassLoader(clSave);
		}
	}
	
	public static Object invokeBean(String contextZoneName, String contextUserName, String beanName, 
			String methodName, Class[] methodArgTypes, Object[] methodArgs)
	throws Exception {
		ClassLoader clSave = Thread.currentThread().getContextClassLoader();
		
		try {
			Thread.currentThread().setContextClassLoader(BridgeUtil.getClassLoader());
							
			return bridgeServerInvokeBeanMethod.invoke(bridgeServer,
					new Object[] {contextZoneName, contextUserName, beanName, methodName, methodArgTypes, methodArgs});
		}
		finally {
			Thread.currentThread().setContextClassLoader(clSave);
		}
	}
		
	public static Object invokeBeanWithoutContext(String beanName, 
			String methodName, Class[] methodArgTypes, Object[] methodArgs)
	throws Exception {
		ClassLoader clSave = Thread.currentThread().getContextClassLoader();
		
		try {
			Thread.currentThread().setContextClassLoader(BridgeUtil.getClassLoader());
							
			return bridgeServerInvokeBeanWithoutContextMethod.invoke(bridgeServer,
					new Object[] {beanName, methodName, methodArgTypes, methodArgs});
		}
		finally {
			Thread.currentThread().setContextClassLoader(clSave);
		}
	}
		
	/*
	 * Includes the content of a resource (servlet, JSP page, HTML file) 
	 * in the response.
	 */
	public static void include(ServletRequest req, ServletResponse res) 
	throws ServletException, IOException {
		BridgeUtil.getCCDispatcher().include(req, res);
	}
	
}
