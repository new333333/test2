package com.sitescape.ef.security.authentication.impl;

import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.NoUserByTheNameException;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.security.authentication.AuthenticationManager;
import com.sitescape.ef.security.authentication.PasswordDoesNotMatchException;
import com.sitescape.ef.security.authentication.UserDoesNotExistException;
import com.sitescape.ef.util.SPropsUtil;

public class AuthenticationManagerImpl implements AuthenticationManager {
	
	private CoreDao coreDao;
	
	protected CoreDao getCoreDao() {
		return coreDao;
	}

	public void setCoreDao(CoreDao coreDao) {
		this.coreDao = coreDao;
	}

	public User authenticate(String zoneName, String username, String password) 
		throws PasswordDoesNotMatchException, UserDoesNotExistException {
		User user = null;
		try {
			user = getCoreDao().findUserByName(username, zoneName);
		}
    	catch(NoUserByTheNameException e) {
    		throw new UserDoesNotExistException("Authentication failed: Unrecognized user [" 
    				+ zoneName + "," + username + "]", e);
    	}
    	
    	if(!password.equals(user.getPassword())) {
    		// Passwords do not match
    		if(SPropsUtil.getBoolean("portal.password.auto.synchronize", false)) {
    			// Change the user's password to the value passed in. 
    			user.setPassword(password);
    		}
    		else {
    			throw new PasswordDoesNotMatchException("Authentication failed: password does not match");
    		}
    	}
		
		return user;
	}
}
