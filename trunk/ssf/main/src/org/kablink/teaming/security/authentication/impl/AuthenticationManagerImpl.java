/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.security.authentication.impl;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.LoginInfo;
import org.kablink.teaming.domain.NoUserByTheIdException;
import org.kablink.teaming.domain.NoUserByTheNameException;
import org.kablink.teaming.domain.NoWorkspaceByTheNameException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.ldap.LdapModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.module.zone.ZoneException;
import org.kablink.teaming.security.authentication.AuthenticationManager;
import org.kablink.teaming.security.authentication.DigestDoesNotMatchException;
import org.kablink.teaming.security.authentication.PasswordDoesNotMatchException;
import org.kablink.teaming.security.authentication.UserDoesNotExistException;
import org.kablink.teaming.util.EncryptUtil;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SessionUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.springframework.beans.factory.InitializingBean;


public class AuthenticationManagerImpl implements AuthenticationManager,InitializingBean {
	protected Log logger = LogFactory.getLog(getClass());

	private ProfileDao profileDao;
	private CoreDao coreDao;
	private AdminModule adminModule;
	private ProfileModule profileModule;
	private String[] userModify;
	private ReportModule reportModule;
	private LdapModule ldapModule;

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
	 * 
	 * @return
	 */
	protected LdapModule getLdapModule()
	{
		return ldapModule;
	}
	
	/**
	 * 
	 * @param ldapModule
	 */
	public void setLdapModule( LdapModule ldapModule )
	{
		this.ldapModule = ldapModule;
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
		throws PasswordDoesNotMatchException, UserDoesNotExistException
	{
		validateZone(zoneName);
		User user=null;
		boolean hadSession = SessionUtil.sessionActive();
		boolean syncUser;
		LdapModule ldapModule;
	
		ldapModule = getLdapModule();
		syncUser = false;
		
		try
		{
			if (!hadSession)
				SessionUtil.sessionStartup();	
			
			user = doAuthenticate(zoneName, userName, password, passwordAutoSynch, ignorePassword);
			if (updates != null && !updates.isEmpty()) {
   				// We don't want to sync ldap attributes if the user is one of the 5
   				// system user accounts, "admin", "guest", "_postingAgent", "_jobProcessingAgent" and "_synchronizationAgent".
   				// Is the user a system user account?
   				if ( !MiscUtil.isSystemUserAccount( userName ) )
   				{
   					// No
   					syncUser = true;
   				}
			}
			if (user.getWorkspaceId() == null)
				getProfileModule().addUserWorkspace(user, null);
			
			if(authenticatorName != null)
				getReportModule().addLoginInfo(new LoginInfo(authenticatorName, user.getId()));
		} 
		catch (UserDoesNotExistException nu) {
 			if (createUser) {
 				user=getProfileModule().addUserFromPortal(userName, password, updates, null);
 				if(user == null)
 					throw nu;

 				syncUser = true;
 				
 				if(authenticatorName != null)
 					getReportModule().addLoginInfo(new LoginInfo(authenticatorName, user.getId()));
 			} 
 			else throw nu;
		} finally {
			if (!hadSession) SessionUtil.sessionStop();			
		}

		// Do we need to sync attributes from the ldap directory into Teaming for this user?
		if ( syncUser && user != null )
		{
			// Yes
			try
			{
				// The Teaming user name and the ldap user name may not be the same name.
				ldapModule.syncUser( user.getName(), userName );
			}
			catch (NamingException ex)
			{
				// Nothing to do.
			}
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
	
	/**
	 * 
	 * @param zoneName
	 * @param username
	 * @param password
	 * @param passwordAutoSynch
	 * @param ignorePassword
	 * @return
	 * @throws PasswordDoesNotMatchException
	 * @throws UserDoesNotExistException
	 */
	protected User doAuthenticate(String zoneName, String username, String password,
				boolean passwordAutoSynch, boolean ignorePassword)
			throws PasswordDoesNotMatchException, UserDoesNotExistException {
		User user = null;

		try {
			String ldapGuid;
			LdapModule ldapModule;
			
			// Read this user's ldap guid from the ldap directory.
			ldapModule = getLdapModule();
			ldapGuid = ldapModule.readLdapGuidFromDirectory( username );
			
			// Did we find an ldap guid for this user?
			if ( ldapGuid != null && ldapGuid.length() > 0 )
			{
				// Yes
				try
				{
					ProfileModule profileModule;
					
					// Try to find the user in Teaming by their ldap guid.
					profileModule = getProfileModule();
					user = profileModule.findUserByLdapGuid( ldapGuid );
				}
				catch (NoUserByTheNameException ex)
				{
					// Nothing to do
				}
			}
			
			// Did we find the user by their ldap guid?
			if ( user == null )
			{
				// No, try to find the user by their name.
				user = getProfileDao().findUserByName(username, zoneName);
			}
		} catch (NoWorkspaceByTheNameException e) {
     		if (user == null) {
    			throw new UserDoesNotExistException("Unrecognized user [" 
     						+ zoneName + "," + username + "]", e);
    		}
    	} catch (NoUserByTheNameException e) {
			throw new UserDoesNotExistException("Unrecognized user [" 
						+ zoneName + "," + username + "]", e);
    	}
    	
    	if(password != null) {
	   		if(!EncryptUtil.encryptPassword(password).equals(user.getPassword())) {
	   			// Password does not match.
	   			if(passwordAutoSynch) {
	   				// We don't want to sync the password if the user is one of the 5
	   				// system user accounts, "admin", "guest", "_postingAgent", "_jobProcessingAgent" and "_synchronizationAgent".
	   				// Is the user a system user account?
	   				if ( !MiscUtil.isSystemUserAccount( username ) )
	   				{
	   					// No
		   				// Change the user's password to the value passed in. 
		   				Map updates = new HashMap();
		   				updates.put("password", password);
		   				getProfileModule().modifyUserFromPortal(user, updates, null);
	   				}
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
		   				throw new PasswordDoesNotMatchException("Password does not match for user [" + zoneName + "," + username + "]");				
	   				}
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
				throw new DigestDoesNotMatchException("Digest does not match for user [" + zoneName + "," + userId + "]");
			}
			
			if(authenticatorName != null)
				getReportModule().addLoginInfo(new LoginInfo(authenticatorName, user.getId()));

		}
		catch(NoUserByTheIdException e) {
			throw new UserDoesNotExistException("Unrecognized user ["
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
