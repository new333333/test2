package com.sitescape.ef.security.authentication.impl;

import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.NoUserByTheNameException;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.security.authentication.AuthenticationManager;
import com.sitescape.ef.security.authentication.PasswordDoesNotMatchException;
import com.sitescape.ef.security.authentication.UserDoesNotExistException;

public class AuthenticationManagerImpl implements AuthenticationManager {
	
	private CoreDao coreDao;
	
	protected CoreDao getCoreDao() {
		return coreDao;
	}

	public void setCoreDao(CoreDao coreDao) {
		this.coreDao = coreDao;
	}

	public void authenticate(String zoneId, String username, String password) throws PasswordDoesNotMatchException, UserDoesNotExistException {
		User user = null;
		try {
			user = getCoreDao().findUserByName(username, zoneId);
		}
    	catch(NoUserByTheNameException e) {
    		throw new UserDoesNotExistException(e);
    	}

    	if(!user.getPassword().equals(password))
    		throw new PasswordDoesNotMatchException();
	}
}
