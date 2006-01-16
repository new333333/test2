package com.sitescape.ef.security.authentication;

import com.sitescape.ef.domain.User;
import com.sitescape.ef.util.SpringContextUtil;

public class AuthenticationManagerUtil {

	public static User authenticate(String zoneName, String username, String password)
		throws UserDoesNotExistException, PasswordDoesNotMatchException {
		AuthenticationManager am = (AuthenticationManager) SpringContextUtil.getBean("authenticationManager");
		return am.authenticate(zoneName, username, password);
	}

}
