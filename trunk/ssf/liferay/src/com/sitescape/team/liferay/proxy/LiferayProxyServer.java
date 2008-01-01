package com.sitescape.team.liferay.proxy;

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
		
		if(PermissionThreadLocal.getPermissionChecker() == null)
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
				PermissionCheckerFactory.recycle(permissionChecker);
			}
		}
	}
}
