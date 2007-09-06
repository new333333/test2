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
import org.springframework.beans.factory.InitializingBean;

import com.sitescape.team.dao.CoreDao;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.LoginInfo;
import com.sitescape.team.domain.NoUserByTheIdException;
import com.sitescape.team.domain.NoUserByTheNameException;
import com.sitescape.team.domain.NoWorkspaceByTheNameException;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.admin.AdminModule;
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.module.report.ReportModule;
import com.sitescape.team.security.authentication.AuthenticationManager;
import com.sitescape.team.security.authentication.DigestDoesNotMatchException;
import com.sitescape.team.security.authentication.PasswordDoesNotMatchException;
import com.sitescape.team.security.authentication.UserDoesNotExistException;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SessionUtil;
import com.sitescape.util.PasswordEncryptor;

public class AuthenticationManagerImpl implements AuthenticationManager,InitializingBean {
	protected Log logger = LogFactory.getLog(getClass());

	private ProfileDao profileDao;
	private CoreDao coreDao;
	private AdminModule adminModule;
	private ProfileModule profileModule;
	private String[] userModify;
	private ReportModule reportModule;

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
	protected ReportModule getReportModule() {
		return reportModule;
	}
	public void setReportModule(ReportModule reportModule) {
		this.reportModule = reportModule;
	}

	/**
     * Called after bean is initialized.  
     */
 	public void afterPropertiesSet() {
		userModify = SPropsUtil.getStringArray("portal.user.auto.synchronize", ",");
 	}
 	
	public User authenticate(String zoneName, String userName, String password,
			boolean createUser, boolean passwordAutoSynch, boolean ignorePassword, 
			Map updates, String authenticatorName) 
		throws PasswordDoesNotMatchException, UserDoesNotExistException {
		User user=null;
		boolean hadSession = SessionUtil.sessionActive();
		try {
			if (!hadSession) SessionUtil.sessionStartup();	
			user = doAuthenticate(zoneName, userName, password, passwordAutoSynch, ignorePassword);
			if (updates != null && !updates.isEmpty()) {
				Map mods = new HashMap();
				for (int i = 0; i<userModify.length; ++i) {
					Object val = updates.get(userModify[i]);
					if (val != null) {
						mods.put(userModify[i], val);
					}					
				}
				if (!mods.isEmpty()) getProfileModule().modifyUserFromPortal(user, mods);
			}
			if(authenticatorName != null)
				getReportModule().addLoginInfo(new LoginInfo(authenticatorName, user.getId()));
		} 
		catch (UserDoesNotExistException nu) {
 			if (createUser) {
 				if(ignorePassword) {
 					// This password should be ignored. Use username as password instead.
 					password = userName;
 				}
 				user=getProfileModule().addUserFromPortal(zoneName, userName, password, updates);
 				if(authenticatorName != null)
 					getReportModule().addLoginInfo(new LoginInfo(authenticatorName, user.getId()));
 			} 
 			else throw nu;
		} finally {
			if (!hadSession) SessionUtil.sessionStop();			
		}
		return user;
	}

	public User authenticate(String zoneName, String username, String password,
			boolean passwordAutoSynch, boolean ignorePassword, String authenticatorName)
		throws PasswordDoesNotMatchException, UserDoesNotExistException {
		User user=null;
		boolean hadSession = SessionUtil.sessionActive();
		try {
			if (!hadSession) SessionUtil.sessionStartup();	
			user = doAuthenticate(zoneName, username, password, passwordAutoSynch, ignorePassword);
			if(authenticatorName != null)
				getReportModule().addLoginInfo(new LoginInfo(authenticatorName, user.getId()));
		} finally {
			if (!hadSession) SessionUtil.sessionStop();			
		}
		return user;
	}
	
	protected User doAuthenticate(String zoneName, String username, String password,
				boolean passwordAutoSynch, boolean ignorePassword)
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
    	if(!ignorePassword) {
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
    	}
		return user;
	}

	public User authenticate(String zoneName, Long userId, String binderId, String privateDigest, String authenticatorName) 
		throws PasswordDoesNotMatchException, UserDoesNotExistException {
		User user = null;
		boolean hadSession = SessionUtil.sessionActive();
		try {
			if (!hadSession) SessionUtil.sessionStartup();	
			user = getProfileDao().loadUser(userId, zoneName);
			if(!privateDigest.equals(user.getPrivateDigest(binderId))) {
				throw new DigestDoesNotMatchException("Authentication failed: digest does not match");
			}
			
			if(authenticatorName != null)
				getReportModule().addLoginInfo(new LoginInfo(authenticatorName, user.getId()));

		}
		catch(NoUserByTheIdException e) {
			throw new UserDoesNotExistException("Authentication failed: Unrecognized user ["
					+ zoneName + "," + userId + "]", e);
		} finally {
			if (!hadSession) SessionUtil.sessionStop();			
		}
		
		return user;
	}
		
}
