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
import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.InternalException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.LoginInfo;
import org.kablink.teaming.domain.NoUserByTheIdException;
import org.kablink.teaming.domain.NoUserByTheNameException;
import org.kablink.teaming.domain.NoWorkspaceByTheNameException;
import org.kablink.teaming.domain.OpenIDConfig;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.modelprocessor.ProcessorManager;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.authentication.AuthenticationServiceProvider;
import org.kablink.teaming.module.ldap.LdapModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.profile.processor.ProfileCoreProcessor;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.module.zone.ZoneException;
import org.kablink.teaming.security.authentication.AuthenticationException;
import org.kablink.teaming.security.authentication.AuthenticationManager;
import org.kablink.teaming.security.authentication.DigestDoesNotMatchException;
import org.kablink.teaming.security.authentication.PasswordDoesNotMatchException;
import org.kablink.teaming.security.authentication.UserAccountNotActiveException;
import org.kablink.teaming.security.authentication.UserDoesNotExistException;
import org.kablink.teaming.security.authentication.UserMismatchException;
import org.kablink.teaming.util.EncryptUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SessionUtil;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.springframework.beans.factory.InitializingBean;

import com.liferay.util.Validator;


public class AuthenticationManagerImpl implements AuthenticationManager,InitializingBean {
	protected Log logger = LogFactory.getLog(getClass());

	private ProfileDao profileDao;
	private CoreDao coreDao;
	private AdminModule adminModule;
	private ProfileModule profileModule;
	private String[] userModify;
	private ReportModule reportModule;
	private LdapModule ldapModule;
	private ProcessorManager processorManager;

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

	protected ProcessorManager getProcessorManager() {
		return processorManager;
	}

	public void setProcessorManager(ProcessorManager processorManager) {
		this.processorManager = processorManager;
	}

	/**
     * Called after bean is initialized.  
     */
 	public void afterPropertiesSet() {
		userModify = SPropsUtil.getStringArray("portal.user.auto.synchronize", ",");
 	}
 	
 	@Override
	public User authenticate(Integer identitySource, AuthenticationServiceProvider authenticationServiceProvider, 
			String zoneName, String userName, String password,
			boolean createUser, boolean passwordAutoSynch, boolean ignorePassword, 
			Map updates, String authenticatorName) 
		throws PasswordDoesNotMatchException, UserDoesNotExistException, UserAccountNotActiveException, UserMismatchException
	{
		validateZone(zoneName);
		User user=null;
		boolean hadSession = SessionUtil.sessionActive();
		boolean syncUser;
		LdapModule ldapModule;

		SimpleProfiler.start( "3x-AuthenticationManagerImpl.authenticate()" );
		
		ldapModule = getLdapModule();
		syncUser = false;
		
		Long zoneId = getCoreDao().findTopWorkspace(zoneName).getZoneId();
		
		try
		{
			if (!hadSession)
				SessionUtil.sessionStartup();	
			
			if(isExternalUser(identitySource)) { // OpenID
				user = doAuthenticateExternalUser(zoneName, userName);
				// If you're still here, it means that the corresponding user object was found in the database.
				if(AuthenticationServiceProvider.OPENID == authenticationServiceProvider) {
					int syncMode = getCoreDao().loadZoneConfig(zoneId).getOpenIDConfig().getProfileSynchronizationMode();
					if(syncMode == OpenIDConfig.PROFILE_SYNCHRONIZATION_ON_FIRST_LOGIN_ONLY) {
						if(user.getFirstLoginDate() != null)
							updates.clear(); // This is not the first time logging in. Should not sync. Clear all attributes.
					}
					else if(syncMode == OpenIDConfig.PROFILE_SYNCHRONIZATION_ON_EVERY_LOGIN) {
						// Should sync.
					}
					else {
						updates.clear(); // Should not sync.
					}
				}
				else {
					throw new InternalException("Encountered auth service provider " + authenticationServiceProvider.name() + " when expecting OPENID");
				}
			}
			else { // LOCAL and LDAP
				user = doAuthenticateInternalUser(zoneName, userName, password, passwordAutoSynch, ignorePassword);
			}
			//Make sure this user account hasn't been disabled
			if (!user.isActive() && !MiscUtil.isSystemUserAccount( userName )) {
				//This account is not active
				throw new UserAccountNotActiveException(NLT.get("error.accountNotActive"));
			}
			if (updates != null && !updates.isEmpty()) { // There are some update data
				if(identitySource != null) {
					if(identitySource.intValue() != User.IDENTITY_SOURCE_LOCAL)
						syncUser = true;
				}
				else { // This means either LDAP or LOCAL. We have to fall back to the old mechanism for making determination.
	   				// We don't want to sync ldap attributes if the user is one of the 5
	   				// system user accounts, "admin", "guest", "_postingAgent", "_jobProcessingAgent" and "_synchronizationAgent".
	   				// Is the user a system user account?
	   				if ( !MiscUtil.isSystemUserAccount( userName ) )
	   				{
	   					// No
	   					syncUser = true;
	   				}					
				}
			}
			if (user.getWorkspaceId() == null)
				getProfileModule().addUserWorkspace(user, null);
			
			if(authenticatorName != null)
				getReportModule().addLoginInfo(new LoginInfo(authenticatorName, user.getId()));
		} 
		catch (UserDoesNotExistException nu) {
 			if (createUser) {
 				if(isExternalUser(identitySource)) { // e.g. OpenID
 					if(AuthenticationServiceProvider.OPENID == authenticationServiceProvider) {
 						int syncMode = getCoreDao().loadZoneConfig(zoneId).getOpenIDConfig().getProfileSynchronizationMode();
 						if(syncMode == OpenIDConfig.PROFILE_SYNCHRONIZATION_NEVER) {
	 						// Don't allow profile sync as we create new user account. We will only store email address.
	 						removeEverythingButEmailAddress(updates); 							
 						}
	 			 		// Make sure foreign name is identical to name.
	 					updates.put(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, userName);
	 					// For external users, there's no need for separate step for synchronizing profile info
	 					// because all the information is already ready and presented to the method that creates the user.
	 					syncUser = false;
 					}
 					else {
 						throw new InternalException("Encountered auth service provider " + authenticationServiceProvider.name() + " when expecting OPENID");	
 					}
 				}
 				else { // LDAP
 					syncUser = true;
 				}
 				
 				user=getProfileModule().addUserFromPortal(
 						// It is NOT possible for system to authenticate a local user against Vibe database 
 						// unless the user record already exists in the database. So, if the authentication
 						// has already succeeded without the corresponding user record being found in the database,
 						// it means that the identity source is anything but local. So, if identitySource is
 						// unspecified (i.e., null), then we can safely conclude that the identity source
 						// must be LDAP.
 						(identitySource != null)? identitySource.intValue():User.IDENTITY_SOURCE_LDAP,
 						userName, password, updates, null);
 				if(user == null)
 					throw nu;
 				
 				if(authenticatorName != null)
 					getReportModule().addLoginInfo(new LoginInfo(authenticatorName, user.getId()));
 			} 
 			else throw nu;
		} 
		catch (UserAccountNotActiveException nu) {
 			throw new UserAccountNotActiveException(NLT.get("error.accountNotActive"));
		} finally {
			if (!hadSession) SessionUtil.sessionStop();			
		}

		// Do we need to sync attributes from the ldap directory into Teaming for this user?
		if ( syncUser && user != null )
		{
			if(identitySource != null && identitySource.intValue() == User.IDENTITY_SOURCE_EXTERNAL) {
		 		ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
		            	user.getParentBinder(), ProfileCoreProcessor.PROCESSOR_KEY);
		 		// Make sure foreign name is identical to name.
				updates.put(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, user.getName());
		 		processor.syncEntry(user, new MapInputData(StringCheckUtil.check(updates)), null);
			}
			else {
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
		}
		
		// If still here, it means that authentication was successful.
		// If this is the user's first time logging in, let's capture the date/time.
		if(user.getFirstLoginDate() == null)
			getProfileModule().setFirstLoginDate(user.getId());
		
		SimpleProfiler.stop( "3x-AuthenticationManagerImpl.authenticate()" );
		return user;
	}

 	private boolean isExternalUser(Integer identitySource) {
 		if(identitySource != null) {
 			if(identitySource.intValue() == User.IDENTITY_SOURCE_EXTERNAL)
 				return true;
 			else
 				return false;
 		}
 		else { // This means either LDAP or LOCAL
 			return false;
 		}
 	}
 		
	private void removeEverythingButEmailAddress(Map<String,String> updates) {
		if(updates == null) return;
		String emailAddress = updates.get("emailAddress");
		updates.clear();
		if(Validator.isNotNull(emailAddress))
			updates.put("emailAddress", emailAddress);
	}
	
	public User authenticate(String zoneName, String username, String password,
			boolean passwordAutoSynch, boolean ignorePassword, String authenticatorName)
		throws PasswordDoesNotMatchException, UserDoesNotExistException, UserAccountNotActiveException, UserMismatchException {
		validateZone(zoneName);
		User user=null;
		boolean hadSession = SessionUtil.sessionActive();
		try {
			if (!hadSession) SessionUtil.sessionStartup();	
			user = doAuthenticateInternalUser(zoneName, username, password, passwordAutoSynch, ignorePassword);
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
	 * @throws UserAccountNotActiveException
	 */
	protected User doAuthenticateInternalUser(String zoneName, String username, String password,
				boolean passwordAutoSynch, boolean ignorePassword)
			throws PasswordDoesNotMatchException, UserDoesNotExistException, UserAccountNotActiveException, UserMismatchException {
		User user = null;

		SimpleProfiler.start( "3x-AuthenticationManagerImpl.doAuthenticate()" );

		try {
			String ldapGuid;

			ldapGuid = null;
			
			// Are we dealing with one of the system accounts? ie admin
			if ( !MiscUtil.isSystemUserAccount( username ) )
			{
				LdapModule ldapModule;
				Binder top;
				Long zoneId;

				// No
				// Get the zone id from the zone name.
		    	top = getCoreDao().findTopWorkspace( zoneName );
		    	zoneId = top.getZoneId();

		    	// Read this user's ldap guid from the ldap directory.
				ldapModule = getLdapModule();
				ldapGuid = ldapModule.readLdapGuidFromDirectory( username, zoneId );
				
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
					catch (UserAccountNotActiveException ex)
					{
						// Nothing to do
					}
				}
			}

			// Did we find the user in the Vibe db by their ldap guid?
			if ( user == null )
			{
				// No, try to find the user by their name.
				user = getProfileDao().findUserByName(username, zoneName);

				// Did we find the user by name?
				if ( user != null )
				{
					// Yes
					// Do we have an ldap guid we read from the directory for this user?
					if ( ldapGuid != null )
					{
						String vibeLdapGuid;
						
						// Yes
						// Is the user we found really the user we are looking for?
						// Check to see that the ldap guid we read from the directory matches
						// the ldap guid stored in the Vibe db for this user.
						vibeLdapGuid = user.getLdapGuid();
						
						// Do the ldap guids match?
						if ( vibeLdapGuid != null && ldapGuid.equalsIgnoreCase( vibeLdapGuid ) == false )
						{
							// No
							// The user we found by name is not the user we are looking for.
							user = null;
							
							// Throw an exception that indicates that the user that tried
							// to log in successfully authenticated to the ldap directory
							// but can't use Vibe because there is another user in Vibe with
							// the same name.
							throw new UserMismatchException( "The Vibe user account for the name, " + username + ", does not belong to the authenticated user" );
						}
					}
				}
			}
		} catch (NoWorkspaceByTheNameException e) {
     		if (user == null) {
    			throw new UserDoesNotExistException("Unrecognized user [" 
     						+ zoneName + "," + username + "]", e);
    		}
    	} catch (NoUserByTheNameException e) {
			try {
				// Try to find the user even if disabled or deleted
				user = getProfileDao().findUserByNameDeadOrAlive( username, zoneName );
				throw new UserAccountNotActiveException("User account disabled or deleted [" 
						+ zoneName + "," + username + "]", e);

			} catch (NoUserByTheNameException ex) {
				throw new UserDoesNotExistException("Unrecognized user [" 
						+ zoneName + "," + username + "]", e);
			}
    	} catch (UserAccountNotActiveException e) {
			throw new UserAccountNotActiveException("User account disabled or deleted [" 
						+ zoneName + "," + username + "]", e);
    	}
    	
    	if(password != null) {
    		if(!EncryptUtil.checkPassword(password, user)) {
	   			// Password does not match.
	   			if(passwordAutoSynch) {
	   				// We don't want to sync the password if the user is one of the 5
	   				// system user accounts, "admin", "guest", "_postingAgent", "_jobProcessingAgent" and "_synchronizationAgent".
	   				// Is the user a system user account?
	   				if ( !MiscUtil.isSystemUserAccount( username ) )
	   				{
	   					// No
		   				// Change the user's password to the value passed in. 
	   					modifyPassword(user, password);
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
	   		else { // The user-entered credential and the stored password do match.
	   			if(!EncryptUtil.passwordEncryptionAlgorithmForMatching(user).equals(EncryptUtil.passwordEncryptionAlgorithmForStorage(user))) {
	   				// The password encryption algorithm has changed since the last time the password was encrypted for this user.
	   				// This is the opportunity to migrate the password to the latest encryption.
	   				modifyPassword(user, password);
	   			}
	   		}
    	}
   		
		SimpleProfiler.stop( "3x-AuthenticationManagerImpl.doAuthenticate()" );
		return user;
	}

	protected User doAuthenticateExternalUser(String zoneName, String username) {
		User user = null;
		try {
			user = getProfileDao().findUserByName(username, zoneName);
		} catch (NoWorkspaceByTheNameException e) {
     		if (user == null) {
    			throw new UserDoesNotExistException("Unrecognized user [" 
     						+ zoneName + "," + username + "]", e);
    		}
    	} catch (NoUserByTheNameException e) {
			try {
				// Try to find the user even if disabled or deleted
				user = getProfileDao().findUserByNameDeadOrAlive( username, zoneName );
				throw new UserAccountNotActiveException("User account disabled or deleted [" 
						+ zoneName + "," + username + "]", e);

			} catch (NoUserByTheNameException ex) {
				throw new UserDoesNotExistException("Unrecognized user [" 
						+ zoneName + "," + username + "]", e);
			}
    	} catch (UserAccountNotActiveException e) {
			throw new UserAccountNotActiveException("User account disabled or deleted [" 
						+ zoneName + "," + username + "]", e);
    	}
		return user;
	}

	void modifyPassword(User user, String password) {
		Map updates = new HashMap();
		updates.put("password", password);
		getProfileModule().modifyUserFromPortal(user, updates, null);
	}
	
	public User authenticate(String zoneName, Long userId, String binderId, String privateDigest, String authenticatorName) 
		throws PasswordDoesNotMatchException, UserDoesNotExistException, UserAccountNotActiveException {
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
		}
		catch(UserAccountNotActiveException e) {
			throw new UserDoesNotExistException("User account disabled or deleted ["
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
