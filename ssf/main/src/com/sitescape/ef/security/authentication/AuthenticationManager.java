package com.sitescape.ef.security.authentication;

import com.sitescape.ef.domain.User;

public interface AuthenticationManager {

	public User authenticate(String zoneName, String username, String password,
			boolean passwordAutoSynch)
		throws PasswordDoesNotMatchException, UserDoesNotExistException;
	
	public User authenticate(String zoneName, Long userId, String passwordDigest)
		throws PasswordDoesNotMatchException, UserDoesNotExistException;
}
