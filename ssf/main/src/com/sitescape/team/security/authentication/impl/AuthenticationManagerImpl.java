/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.security.authentication.impl;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import com.sitescape.team.context.request.RequestContextHolder;
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
import com.sitescape.team.module.zone.ZoneException;
import com.sitescape.team.security.authentication.AuthenticationManager;
import com.sitescape.team.security.authentication.DigestDoesNotMatchException;
import com.sitescape.team.security.authentication.PasswordDoesNotMatchException;
import com.sitescape.team.security.authentication.UserDoesNotExistException;
import com.sitescape.team.util.EncryptUtil;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SessionUtil;

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
		validateZone(zoneName);
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
				if (!mods.isEmpty()) getProfileModule().modifyUserFromPortal(user, mods, null);
			}
			if (user.getWorkspaceId() == null) getProfileModule().addUserWorkspace(user, null);
			if(authenticatorName != null)
				getReportModule().addLoginInfo(new LoginInfo(authenticatorName, user.getId()));
		} 
		catch (UserDoesNotExistException nu) {
 			if (createUser) {
 				user=getProfileModule().addUserFromPortal(userName, password, updates, null);
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
		validateZone(zoneName);
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
    	
   		if(!EncryptUtil.encryptPassword(password).equals(user.getPassword())) {
   			// Password does not match.
   			if(passwordAutoSynch) {
   				// Change the user's password to the value passed in. 
   				Map updates = new HashMap();
   				updates.put("password", password);
   				getProfileModule().modifyUserFromPortal(user, updates, null);  				
   			}
   			else {
   				// We're not allowed to change the user's password to the value passed in.
   				// ignorePassword flag indicates whether to ignore the password "for the 
   				// purpose of authentication" or not. In other word, the value of true does 
   				// not mean that the password value should be discarded or treated unworthy. 
   				// It simply means that the password value must not be used for the
   				// authentication purpose. Therefore, if we require password-based
   				// authentication AND do not allow automatic synchronization of password,
   				// then mismatched password results in authentication failure.
   				if(!ignorePassword) {
	   				throw new PasswordDoesNotMatchException("Authentication failed: password does not match");  					
   				}
   			}
   		}
   		
		return user;
	}

	public User authenticate(String zoneName, Long userId, String binderId, String privateDigest, String authenticatorName) 
		throws PasswordDoesNotMatchException, UserDoesNotExistException {
		validateZone(zoneName);
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
		
	private void validateZone(String zoneName) throws ZoneException {
		if(!zoneName.equals(RequestContextHolder.getRequestContext().getZoneName()))
			throw new ZoneException("Authentication is permitted only against the context zone"); 
	}
}
