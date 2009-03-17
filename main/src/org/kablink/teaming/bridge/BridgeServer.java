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
package org.kablink.teaming.bridge;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.SessionUtil;
import org.kablink.teaming.util.SpringContextUtil;


public class BridgeServer {

	/**
	 * Invoke ICEcore service.
	 * <p>
	 * This mechanism allows an invocation to be <i>initiated</i> from outside
	 * of ICEcore's ssf webapp, yet <i>executed</i> within the environment
	 * of ICEcore's context.
	 * 
	 * @param contextZoneName Name of the zone in whose context the operation is
	 * to be executed; if null, default zone is assumed
	 * @param contextUserName Name of the user in whose context the operation is
	 * to be executed; if null, admin account is assumed (so be careful)
	 * @param className class name of the ICEcore service 
	 * @param methodName method name of the ICEcore service
	 * @param methodArgTypes method argument types
	 * @param methodArgs method arguments to the invocation
	 * @return object
	 * @throws Exception
	 */
	public static Object invoke(String contextZoneName, String contextUserName, 
			String className, String methodName, Class[] methodArgTypes, 
			final Object[] methodArgs) throws Exception {
		Class classObj = Class.forName(className);
		
		Method methodObj = classObj.getMethod(methodName, methodArgTypes);
		
		Object obj = null;
		
		if(!Modifier.isStatic(methodObj.getModifiers()))
			obj = classObj.newInstance();

		return invokeInternal(contextZoneName, contextUserName, methodObj, obj, methodArgs);
	}
	
	public static Object invokeBean(String contextZoneName, String contextUserName, 
			String beanName, String methodName, Class[] methodArgTypes, 
			final Object[] methodArgs) throws Exception {
		Object bean = SpringContextUtil.getBean(beanName);
		
		Class classObj = bean.getClass();

		Method methodObj = classObj.getMethod(methodName, methodArgTypes);

		return invokeInternal(contextZoneName, contextUserName, methodObj, bean, methodArgs);		
	}
	
	private static Object invokeInternal(String contextZoneName, String contextUserName,
			final Method methodObj, final Object obj, final Object[] methodArgs) 
	throws Exception {		
		if(contextZoneName == null)
			contextZoneName = SZoneConfig.getDefaultZoneName();
		
		if(contextUserName == null)
			contextUserName = SZoneConfig.getAdminUserName(contextZoneName);
		
		SessionUtil.sessionStartup();	
		
		try {
			return RunasTemplate.runas(new RunasCallback() {
				public Object doAs() {
					try {
						return methodObj.invoke(obj, methodArgs);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					} catch (InvocationTargetException e) {
						throw new RuntimeException(e);
					}
				}
			}, contextZoneName, contextUserName);	
		}
		finally {
			SessionUtil.sessionStop();
		}
	}
}
