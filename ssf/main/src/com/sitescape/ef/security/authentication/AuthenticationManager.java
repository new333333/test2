package com.sitescape.ef.security.authentication;

import com.sitescape.ef.domain.User;

public interface AuthenticationManager {

	public User authenticate(String zoneName, String username/*, String password*/)
		throws /*PasswordDoesNotMatchException,*/ UserDoesNotExistException;
}
