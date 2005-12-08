package com.sitescape.ef.security.authentication;

public interface AuthenticationManager {

	public void authenticate(String zoneId, String username, String password)
		throws PasswordDoesNotMatchException, UserDoesNotExistException;
}
