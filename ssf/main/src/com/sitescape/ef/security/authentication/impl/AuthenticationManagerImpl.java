package com.sitescape.ef.security.authentication.impl;

import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.NoUserByTheNameException;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.security.authentication.AuthenticationManager;
import com.sitescape.ef.security.authentication.PasswordDoesNotMatchException;
import com.sitescape.ef.security.authentication.UserDoesNotExistException;
import com.sitescape.ef.util.UserIdMapping;

public class AuthenticationManagerImpl implements AuthenticationManager {
	
	private CoreDao coreDao;
	
	protected CoreDao getCoreDao() {
		return coreDao;
	}

	public void setCoreDao(CoreDao coreDao) {
		this.coreDao = coreDao;
	}

	public User authenticate(String zoneName, String username/*, String password*/) 
		throws /*PasswordDoesNotMatchException,*/ UserDoesNotExistException {
		User user = null;
		try {
			user = getCoreDao().findUserByName(username, zoneName);
		}
    	catch(NoUserByTheNameException e) {
    		throw new UserDoesNotExistException(e);
    	}

    	/* Do NOT check password. As a matter of fact, there's no reason to
    	 * store password in SSF database since by the time this method is
    	 * called it is expected that the user has been successfully 
    	 * authenticated against the portal user database or against some
    	 * sort of external user database (eg. LDAP) configured to work
    	 * with the portal.   
    	 
    	if(!user.getPassword().equals(password))
    		throw new PasswordDoesNotMatchException();
    		
    	*/
    	
		// If you're here, the authentication was successful.
    	// TODO $$$
		UserIdMapping.addEntry(zoneName, username, user.getId());
		
		return user;
	}
}
