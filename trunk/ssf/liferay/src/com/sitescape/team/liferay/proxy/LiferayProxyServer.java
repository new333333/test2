package com.sitescape.team.liferay.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.CompanyThreadLocal;
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
	 * @param companyWebId ID of the company whose context the service
	 * is to be invoked in
	 * @param userName screen name of the portal user in whose context the 
	 * service is to be invoked
	 * @param className class name of the Liferay service
	 * @param methodName method name of the Liferay service
	 * @param methodArgTypes method argument types
	 * @param methodArgs method arguments to the invocation
	 * @throws Exception
	 */
	public static void invoke(String companyWebId, String userName, String className, 
			String methodName, Class[] methodArgTypes, Object[] methodArgs)
	throws Exception {
		Class classObj = Class.forName(className);
		
		Method methodObj = classObj.getMethod(methodName, methodArgTypes);
		
		Object obj = null;
		
		if(!Modifier.isStatic(methodObj.getModifiers()))
			obj = classObj.newInstance();
		
		Company company = CompanyLocalServiceUtil.getCompanyByWebId(companyWebId);
		
		User user = UserLocalServiceUtil.getUserByScreenName(company.getCompanyId(), userName);
		
		PermissionCheckerImpl permissionChecker = PermissionCheckerFactory.create(user, true, true);

		try {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
			CompanyThreadLocal.setCompanyId(user.getCompanyId());

			methodObj.invoke(obj, methodArgs);	
		}
		finally {
			PermissionCheckerFactory.recycle(permissionChecker);
		}
	}
}
