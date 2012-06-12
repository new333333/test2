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
package org.kablink.teaming.liferay.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.CompanyThreadLocal;
import com.liferay.portal.security.auth.PrincipalThreadLocal;
import com.liferay.portal.security.permission.PermissionCheckerFactory;
import com.liferay.portal.security.permission.PermissionCheckerImpl;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

public class LiferayProxyServer {
	
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
	 * @return object
	 * @throws Exception
	 */
	public static Object invoke(String contextCompanyWebId, String contextUserName, String className, 
			String methodName, Class[] methodArgTypes, Object[] methodArgs)
	throws Exception {
		Class classObj = Class.forName(className);
		
		Method methodObj = classObj.getMethod(methodName, methodArgTypes);
		
		Object obj = null;
		
		if(!Modifier.isStatic(methodObj.getModifiers()))
			obj = classObj.newInstance();
		
		Company company = CompanyLocalServiceUtil.getCompanyByWebId(contextCompanyWebId);
		
		User user = UserLocalServiceUtil.getUserByScreenName(company.getCompanyId(), contextUserName);
		
		PermissionCheckerImpl permissionChecker = null;
		
		if(PermissionThreadLocal.getPermissionChecker() == null ||
				CompanyThreadLocal.getCompanyId() == 0)
			permissionChecker = PermissionCheckerFactory.create(user, true, true);

		try {
			if(permissionChecker != null) {
				CompanyThreadLocal.setCompanyId(user.getCompanyId());
				PrincipalThreadLocal.setName(String.valueOf(user.getUserId()));
				PermissionThreadLocal.setPermissionChecker(permissionChecker);
			}
			
			return methodObj.invoke(obj, methodArgs);	
		}
		finally {
			if(permissionChecker != null) {
				CompanyThreadLocal.setCompanyId(0);
				PrincipalThreadLocal.setName(null);
				PermissionThreadLocal.setPermissionChecker(null);
				PermissionCheckerFactory.recycle(permissionChecker);
			}
		}
	}
}
