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
package org.kablink.teaming.portal.liferay;

import java.lang.reflect.Method;

import com.liferay.portal.kernel.util.PortalClassLoaderUtil;

public class LiferayProxyClient {

	private static final String LIFERAY_PROXY_SERVER_CLASS_NAME = 
		"org.kablink.teaming.liferay.proxy.LiferayProxyServer";
	
	private static final String LIFERAY_PROXY_SERVER_INVOKE_METHOD_NAME = "invoke";
	
	private static Method proxyServerInvokeMethod;
	
	private static Object proxyServer;
	
	static {
		try {
			Class classObj = Class.forName(LIFERAY_PROXY_SERVER_CLASS_NAME, 
					true, PortalClassLoaderUtil.getClassLoader());
			
			proxyServerInvokeMethod = classObj.getMethod(LIFERAY_PROXY_SERVER_INVOKE_METHOD_NAME, 
					new Class[] {String.class, String.class, String.class, String.class, Class[].class, Object[].class});
			
			proxyServer = classObj.newInstance();
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
	
	/**
	 * Invoke a Liferay service.
	 * <p>
	 * This mechanism allows an invocation to be <i>initiated</i> from ICEcore
	 * side, yet <i>executed</i> within the environment of Liferay context. 
	 * 
	 * @param contextCompanyWebId Web ID of the company whose context the 
	 * service is to be invoked in
	 * @param contextUserName screen name of the portal user in whose context
	 * the service is to be invoked
	 * @param className class name of the Liferay service
	 * @param methodName method name of the Liferay service
	 * @param methodArgTypes method argument types
	 * @param methodArgs method arguments to the invocation
	 * @throws Exception
	 */
	public static Object invoke(String contextCompanyWebId, String contextUserName, String className, 
			String methodName, Class[] methodArgTypes, Object[] methodArgs)
	throws Exception {
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		
		ClassLoader portalClassLoader = PortalClassLoaderUtil.getClassLoader();
		
		Thread.currentThread().setContextClassLoader(portalClassLoader);

		try {
			return proxyServerInvokeMethod.invoke(proxyServer, 
					new Object[] {contextCompanyWebId, contextUserName, className, methodName, methodArgTypes, methodArgs});
		}
		finally {
			Thread.currentThread().setContextClassLoader(contextClassLoader);
		}
	}
}
