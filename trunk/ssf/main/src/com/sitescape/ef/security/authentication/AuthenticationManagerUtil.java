package com.sitescape.ef.security.authentication;

import com.sitescape.ef.domain.User;
import com.sitescape.ef.util.SpringContextUtil;

public class AuthenticationManagerUtil {

	public static User authenticate(String zoneName, String username, String password,
			boolean passwordAutoSynch)
		throws UserDoesNotExistException, PasswordDoesNotMatchException {
		AuthenticationManager am = (AuthenticationManager) SpringContextUtil.getBean("authenticationManager");
		return am.authenticate(zoneName, username, password, passwordAutoSynch);
	}

	public static User authenticate(String zoneName, Long userId, String passwordDigest)
	throws UserDoesNotExistException, PasswordDoesNotMatchException {
		AuthenticationManager am = (AuthenticationManager) SpringContextUtil.getBean("authenticationManager");
		return am.authenticate(zoneName, userId, passwordDigest);
	}
}
