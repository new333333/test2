package com.sitescape.ef.security.authentication.impl;

import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.dao.ProfileDao;
import com.sitescape.ef.domain.NoUserByTheIdException;
import com.sitescape.ef.domain.NoUserByTheNameException;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.domain.NoWorkspaceByTheNameException;
import com.sitescape.ef.module.admin.AdminModule;
import com.sitescape.ef.security.authentication.AuthenticationManager;
import com.sitescape.ef.security.authentication.PasswordDoesNotMatchException;
import com.sitescape.ef.security.authentication.UserDoesNotExistException;

public class AuthenticationManagerImpl implements AuthenticationManager {
	
	private ProfileDao profileDao;
	private CoreDao coreDao;
	private AdminModule adminModule;
	
	protected CoreDao getCoreDao() {
		return coreDao;
	}

	public void setCoreDao(CoreDao coreDao) {
		this.coreDao = coreDao;
	}

	protected ProfileDao getProfileDao() {
		return profileDao;
	}

	public void setProfileDao(ProfileDao profileDao) {
		this.profileDao = profileDao;
	}
	protected AdminModule getAdminModule() {
		return adminModule;
	}

	public void setAdminModule(AdminModule adminModule) {
		this.adminModule = adminModule;
	}

	public User authenticate(String zoneName, String username, String password,
			boolean passwordAutoSynch)
		throws PasswordDoesNotMatchException, UserDoesNotExistException {
		User user = null;
		try {
			user = getProfileDao().findUserByNameOnlyIfEnabled(username, zoneName);
		}
    	catch(NoUserByTheNameException e) {
    		user = checkZone(zoneName, username);
    		if (user == null)
    		throw new UserDoesNotExistException("Authentication failed: Unrecognized user [" 
    				+ zoneName + "," + username + "]", e);
    	}
    	
    	if(!password.equals(user.getPassword())) {
    		// Passwords do not match
    		if(passwordAutoSynch) {
    			// Change the user's password to the value passed in. 
    			user.setPassword(password);
    		}
    		else {
    			throw new PasswordDoesNotMatchException("Authentication failed: password does not match");
    		}
    	}
		
		return user;
	}

	public User authenticate(String zoneName, Long userId, String passwordDigest) 
		throws PasswordDoesNotMatchException, UserDoesNotExistException {
		User user = null;
		try {
			user = getProfileDao().loadUserOnlyIfEnabled(userId, zoneName);
		}
		catch(NoUserByTheIdException e) {
			throw new UserDoesNotExistException("Authentication failed: Unrecognized user ["
					+ zoneName + "," + userId + "]", e);
		}
		
		if(!passwordDigest.equals(user.getPasswordDigest())) {
			throw new PasswordDoesNotMatchException("Authentication failed: password does not match");
		}
		
		return user;
	}
	private User checkZone(String zoneName, String userName) {
		//make sure zone exists
		try {
			Workspace ws = getCoreDao().findTopWorkspace(zoneName);
		} catch (NoWorkspaceByTheNameException nw) {
			try {
				getAdminModule().addZone(zoneName);
				return getProfileDao().findUserByNameOnlyIfEnabled(userName, zoneName);
			} catch (Exception ex) {
				return null;
			}

		}
		return null;
		
		
		
	}

}
