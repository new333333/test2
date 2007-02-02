package com.sitescape.team.security.authentication.impl;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.dao.ProfileDao;
import com.sitescape.ef.module.admin.AdminModule;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.team.domain.NoBinderByTheNameException;
import com.sitescape.team.domain.NoUserByTheIdException;
import com.sitescape.team.domain.NoUserByTheNameException;
import com.sitescape.team.domain.NoWorkspaceByTheNameException;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.security.authentication.AuthenticationManager;
import com.sitescape.team.security.authentication.PasswordDoesNotMatchException;
import com.sitescape.team.security.authentication.UserDoesNotExistException;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SessionUtil;
import com.sitescape.util.PasswordEncryptor;

public class AuthenticationManagerImpl implements AuthenticationManager {
	protected Log logger = LogFactory.getLog(getClass());

	private ProfileDao profileDao;
	private CoreDao coreDao;
	private AdminModule adminModule;
	private ProfileModule profileModule;

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
	protected ProfileModule getProfileModule() {
		return profileModule;
	}
	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}

	
	public User authenticate(String zoneName, String userName, String password,
			boolean passwordAutoSynch, Map updates) 
		throws PasswordDoesNotMatchException, UserDoesNotExistException {
		User user=null;
		try {
			user = authenticate(zoneName, userName, password, passwordAutoSynch);
			
			boolean userModify = 
				SPropsUtil.getBoolean("portal.user.auto.synchronize", false);
			
			if (userModify && updates != null && !updates.isEmpty())
				getProfileModule().modifyUserFromPortal(user, updates);
		} 
		catch (UserDoesNotExistException nu) {
			boolean userCreate = 
				SPropsUtil.getBoolean("portal.user.auto.create", false);
 			if (userCreate) {
 				getProfileModule().addUserFromPortal(zoneName, userName, password, updates);
 			} 
 			else throw nu;
		} 
		return user;
	}
	public User authenticate(String zoneName, String username, String password,
			boolean passwordAutoSynch)
		throws PasswordDoesNotMatchException, UserDoesNotExistException {
		User user = null;

		try {
			user = getProfileDao().findUserByNameOnlyIfEnabled(username, zoneName);
		} catch (NoBinderByTheNameException e) {
    		//zone not setup?
    		user = addZone(zoneName, username);
    		if (user == null) {
    			throw new UserDoesNotExistException("Authentication failed: Unrecognized user [" 
     						+ zoneName + "," + username + "]", e);
    		}
    	} catch (NoUserByTheNameException e) {
			throw new UserDoesNotExistException("Authentication failed: Unrecognized user [" 
						+ zoneName + "," + username + "]", e);
    	}
   		if(!PasswordEncryptor.encrypt(password).equals(user.getPassword())) {
   			// 	Passwords do not match
   			if(passwordAutoSynch) {
   				// 	Change the user's password to the value passed in. 
   				Map updates = new HashMap();
   				updates.put("password", password);
   				getProfileModule().modifyUserFromPortal(user, updates);
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
	public void checkZone(String zoneName) {
		//make sure zone exists
		try {
			Workspace ws = getCoreDao().findTopWorkspace(zoneName);
			User user = getProfileDao().loadUserOnlyIfEnabled(ws.getCreation().getPrincipal().getId(), zoneName);
			com.sitescape.ef.context.request.RequestContextUtil.setThreadContext(user);
			//TODO: temporary to fixup zones
			getAdminModule().setZone1(zoneName);
			getAdminModule().setZone2(zoneName);
		} catch (NoWorkspaceByTheNameException nw) {
			
		} catch (NoBinderByTheNameException nb) {
					
		};

		
	}
	private User addZone(String zoneName, String userName) {
		//make sure zone exists
		try {
			Workspace ws = getCoreDao().findTopWorkspace(zoneName);
		} catch (NoWorkspaceByTheNameException nw) {
			getAdminModule().addZone(zoneName);
		} catch (NoBinderByTheNameException nb) {
			getAdminModule().addZone(zoneName);			
		};
		try {
			return getProfileDao().findUserByNameOnlyIfEnabled(userName, zoneName);
		} catch (Exception ex) {
			return null;
		}
		
	}
}
