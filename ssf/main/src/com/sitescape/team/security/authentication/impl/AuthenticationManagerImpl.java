/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.security.authentication.impl;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.team.dao.CoreDao;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.NoBinderByTheNameException;
import com.sitescape.team.domain.NoUserByTheIdException;
import com.sitescape.team.domain.NoUserByTheNameException;
import com.sitescape.team.domain.NoWorkspaceByTheNameException;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.admin.AdminModule;
import com.sitescape.team.module.profile.ProfileModule;
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
			user = getProfileDao().findUserByName(username, zoneName);
		} catch (NoWorkspaceByTheNameException e) {
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
			user = getProfileDao().loadUser(userId, zoneName);
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
			User user = getProfileDao().loadUser(ws.getCreation().getPrincipal().getId(), zoneName);
			com.sitescape.team.context.request.RequestContextUtil.setThreadContext(user);
		} catch (NoWorkspaceByTheNameException nw) {
			
		} catch (NoBinderByTheNameException nb) {
					
		};

		
	}

}
