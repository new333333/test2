/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.InternalException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.asmodule.security.authentication.AuthenticationContextHolder;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.IdentityInfo;
import org.kablink.teaming.domain.LdapConnectionConfig;
import org.kablink.teaming.domain.LoginAudit;
import org.kablink.teaming.domain.NoUserByTheIdException;
import org.kablink.teaming.domain.NoUserByTheNameException;
import org.kablink.teaming.domain.NoWorkspaceByTheNameException;
import org.kablink.teaming.domain.OpenIDConfig;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.modelprocessor.ProcessorManager;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.authentication.AuthenticationServiceProvider;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.ldap.LdapModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.profile.processor.ProfileCoreProcessor;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.module.resourcedriver.ResourceDriverModule;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.module.template.TemplateModule;
import org.kablink.teaming.module.zone.ZoneException;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.module.zone.ZoneUtil;
import org.kablink.teaming.runasync.RunAsyncManager;
import org.kablink.teaming.security.authentication.AuthenticationManager;
import org.kablink.teaming.security.authentication.DigestDoesNotMatchException;
import org.kablink.teaming.security.authentication.PasswordDoesNotMatchException;
import org.kablink.teaming.security.authentication.UserAccountNotActiveException;
import org.kablink.teaming.security.authentication.UserDoesNotExistException;
import org.kablink.teaming.security.authentication.UserMismatchException;
import org.kablink.teaming.util.LocaleUtils;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SessionUtil;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.encrypt.EncryptUtil;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.kablink.teaming.web.util.AdminHelper;
import org.kablink.teaming.web.util.BuiltInUsersHelper;
import org.kablink.teaming.web.util.PasswordPolicyHelper;
import org.kablink.teaming.web.util.PasswordPolicyHelper.PasswordStatus;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.encrypt.ExtendedPBEStringEncryptor;

import org.springframework.beans.factory.InitializingBean;

import com.liferay.util.Validator;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings({"unchecked","unused"})
public class AuthenticationManagerImpl implements AuthenticationManager,InitializingBean {
	protected Log logger = LogFactory.getLog(getClass());

	private ProfileDao profileDao;
	private CoreDao coreDao;
	private AdminModule adminModule;
	private ProfileModule profileModule;
	private String[] userModify;
	private ReportModule reportModule;
	private LdapModule ldapModule;
	private TemplateModule templateModule;
	private BinderModule binderModule;
	private FolderModule folderModule;
	private ResourceDriverModule resourceDriverModule;
    private ZoneModule zoneModule;
	private ProcessorManager processorManager;
	private RunAsyncManager runAsyncManager;

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

    public ZoneModule getZoneModule() {
        return zoneModule;
    }

    public void setZoneModule(ZoneModule zoneModule) {
        this.zoneModule = zoneModule;
    }

    /**
	 * 
	 * @return
	 */
	protected BinderModule getBinderModule()
	{
		return binderModule;
	}
	
	/**
	 * 
	 * @param binderModule
	 */
	public void setBinderModule( BinderModule binderModule )
	{
		this.binderModule = binderModule;
	}

	/**
	 * 
	 * @return
	 */
	protected FolderModule getFolderModule()
	{
		return folderModule;
	}
	
	/**
	 * 
	 * @param folderModule
	 */
	public void setFolderModule( FolderModule folderModule )
	{
		this.folderModule = folderModule;
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
	 * 
	 * @return
	 */
	protected ResourceDriverModule getResourceDriverModule()
	{
		return resourceDriverModule;
	}
	
	/**
	 * 
	 * @param resourceDriverModule
	 */
	public void setResourceDriverModule( ResourceDriverModule resourceDriverModule )
	{
		this.resourceDriverModule = resourceDriverModule;
	}

	/**
	 * 
	 * @return
	 */
	protected TemplateModule getTemplateModule()
	{
		return templateModule;
	}
	
	/**
	 * 
	 * @param templateModule
	 */
	public void setTemplateModule( TemplateModule templateModule )
	{
		this.templateModule = templateModule;
	}

	protected ProcessorManager getProcessorManager() {
		return processorManager;
	}

	public void setProcessorManager(ProcessorManager processorManager) {
		this.processorManager = processorManager;
	}

	/**
	 * 
	 */
	protected RunAsyncManager getRunAsyncManager()
	{
		return runAsyncManager;
	}

	/**
	 * 
	 */
	public void setRunAsyncManager( RunAsyncManager runAsyncManager )
	{
		this.runAsyncManager = runAsyncManager;
	}

	/**
     * Called after bean is initialized.  
     */
 	@Override
	public void afterPropertiesSet() {
		userModify = SPropsUtil.getStringArray("portal.user.auto.synchronize", ",");
 	}
 	
	@Override
	public User authenticate(AuthenticationServiceProvider authenticationServiceProvider, 
			String zoneName, String userName, String password,
			boolean createUser, boolean updateUser, boolean updateHomeFolder, boolean passwordAutoSynch, boolean ignorePassword,
			Map updates, String authenticatorName) 
		throws PasswordDoesNotMatchException, UserDoesNotExistException, UserAccountNotActiveException, UserMismatchException
	{
		validateZone(zoneName);
		User user=null;
		String userPasswordEncryptionAlgorithmAtTheBeginning = null;
		boolean hadSession = SessionUtil.sessionActive();
		boolean syncUser;
		LdapModule ldapModule;

		SimpleProfiler.start( "3x-AuthenticationManagerImpl.authenticate()" );
		
		ldapModule = getLdapModule();
		syncUser = false;
		
		Long zoneId = getZoneModule().getZoneIdByVirtualHost(zoneName);
		boolean webClient = ( authenticatorName != null && authenticatorName.equalsIgnoreCase( "web" ) );
		
		try
		{
			if (!hadSession)
				SessionUtil.sessionStartup();	
			
			if(AuthenticationServiceProvider.OPENID == authenticationServiceProvider) {
				user = fetchOpenidUser(zoneName, userName);
				
				int syncMode = getCoreDao().loadZoneConfig(zoneId).getOpenIDConfig().getProfileSynchronizationMode();
				if(syncMode == OpenIDConfig.PROFILE_SYNCHRONIZATION_ON_FIRST_LOGIN_ONLY) {
					if(user.getFirstLoginDate() != null)
						updates.clear(); // This is not the first time logging in. Should not sync. Clear all attributes.
				}
				else if(syncMode == OpenIDConfig.PROFILE_SYNCHRONIZATION_ON_EVERY_LOGIN) {
					// Should sync.
				}
				else { // never
					updates.clear(); // Should not sync.
				}
				
				if(User.ExtProvState.credentialed == user.getExtProvState() || User.ExtProvState.verified == user.getExtProvState()) {
					// This external user has already gone through the self-provisioning step where
					// he at least supplied credential information (along with first and last name).
					// In this case, we should not allow the first/last names from OpenID provider
					// to overwrite the values that the user supplied explicitly through self-provisioning.
					updates.remove(ObjectKeys.FIELD_USER_FIRSTNAME);
					updates.remove(ObjectKeys.FIELD_USER_LASTNAME);
				}
			}
			else {
				Map result = doAuthenticateUser(authenticationServiceProvider, zoneName, userName, password, passwordAutoSynch, ignorePassword);
				user = (User) result.get("user");
				userPasswordEncryptionAlgorithmAtTheBeginning = (String) result.get("userPasswordEncryptionAlgorithmAtTheBeginning");
			}
			
			// If still here, a matching user account has been found.
			
			//Make sure this user account hasn't been disabled
			if (!user.isActive() && !BuiltInUsersHelper.isSystemUserAccount( userName )) {
				//This account is not active
				throw new UserAccountNotActiveException(NLT.get("error.accountNotActive"));
			}

			// If this authentication is from the web client for a user
			// that's restricted from using the web client...
			if (webClient && (!(AdminHelper.getEffectiveWebAccessSetting(adminModule, profileModule, user)))) {
				// ...don't allow things to proceed.
				UserAccountNotActiveException uaEx = new UserAccountNotActiveException(NLT.get("error.webAccessRestricted"));
				uaEx.setApiErrorCode(ApiErrorCode.USERACCOUNT_WEBACCESS_BLOCKED);
				throw uaEx;
			}

			// What's the status of the user's password?
			PasswordStatus ps = PasswordPolicyHelper.getUsersPasswordStatus(user); 
			switch (ps) {
			case EXPIRED:
				// It's expired!  Don't allow things to proceed.
				UserAccountNotActiveException uaEx = new UserAccountNotActiveException(NLT.get("error.passwordExpired"));
				uaEx.setApiErrorCode(ApiErrorCode.PASSWORD_EXPIRED);
				uaEx.setUserId(user.getId());
				throw uaEx;
				
			default:
				// Unexpected value!  Log it as an error...
                logger.error("AuthenticationManagerImpl.authenticate( BOGUS PASSWORD STATUS ):  " + ps.name());
                
				// ...and fall through and handle it as having been
                // ...valid.
				
			case VALID:
				break;
			}
			
			// Again, the user account already exists, so we can safely rule out creation situation.
			if (updateUser & updates != null && !updates.isEmpty()) { 
				// Updating user profile is permitted and there are some update data
				if(authenticationServiceProvider != AuthenticationServiceProvider.LOCAL) {
	   				// We don't want to sync ldap attributes if the user is one of the 5
	   				// system user accounts, "admin", "guest", "_postingAgent", "_jobProcessingAgent", "_synchronizationAgent", and "_fileSyncAgent.
	   				// Is the user a system user account?
	   				if ( !BuiltInUsersHelper.isSystemUserAccount( userName ) )
	   				{
	   					// No
	   					syncUser = true;
	   				}					
				}
				else {
					// This means one of the three.
					// 1. internal local user
					// 2. LDAP user authenticated against cached local data due to unreachable LDAP server
					// 3. external user registered with Filr
					// In all three cases, there's nothing to sync.
					syncUser = false;
				}
			}
			if (user.getWorkspaceId() == null)
				getProfileModule().addUserWorkspace(user, null);
			
			if(authenticatorName != null)
				getReportModule().addLoginInfo(new LoginAudit(authenticatorName, ZoneContextHolder.getClientAddr(), user.getId()));
		} 
		catch (UserDoesNotExistException nu) {
			// Matching user account doesn't exist in the database yet
 			if (createUser) {
				if(AuthenticationServiceProvider.OPENID == authenticationServiceProvider) {
					int syncMode = getCoreDao().loadZoneConfig(zoneId).getOpenIDConfig().getProfileSynchronizationMode();
					if(syncMode == OpenIDConfig.PROFILE_SYNCHRONIZATION_NEVER) {
 						// Don't allow profile sync as we create new user account. We will only store email address.
 						removeEverythingButEmailAddress(updates); 							
					}
 			 		// Make sure foreign name is identical to name.
 					updates.put(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, userName.toLowerCase());
 					// For external users, there's no need for separate step for synchronizing profile info
 					// because all the information is already ready and presented to the method that creates the user.
 					syncUser = false;
 					// Initialize time zone and locale as the default for external users.
                    updates.put(ObjectKeys.FIELD_USER_TIMEZONE, LocaleUtils.getDefaultTimeZoneIdExt());
                    updates.put(ObjectKeys.FIELD_USER_LOCALE, LocaleUtils.getDefaultLocaleExt().toString());
 					// Note: This code never gets executed, because we do not allow self provisioning for OpenID user.
 					// That is createUser will never be set to true under this situation.
 	 				user=getProfileModule().addUserFromPortal(
 	 						new IdentityInfo(false, false, false, true),
 	 						userName, password, updates, null);
				}
				else if(AuthenticationServiceProvider.LDAP == authenticationServiceProvider ||
							AuthenticationServiceProvider.PRE == authenticationServiceProvider) {
 					syncUser = true;
 	 				user=getProfileModule().addUserFromPortal(
 	 						new IdentityInfo(true, true, false, false),
 	 						userName, password, updates, null);
					}
				else {
					throw new InternalException("Cannot create a new user account when auth service provider is " + authenticationServiceProvider.name());	
				}
				
 				if(user == null)
 					throw nu;
 				
 				if(authenticatorName != null)
 					getReportModule().addLoginInfo(new LoginAudit(authenticatorName, ZoneContextHolder.getClientAddr(), user.getId()));
 			}
 			else {
 				throw nu;
 			}
		} 
		catch (UserAccountNotActiveException nu) {
 			throw nu;
		} finally {
			if (!hadSession) SessionUtil.sessionStop();			
		}

		// Do we need to sync attributes from the ldap directory into Teaming for this user?
		if ( syncUser && user != null )
		{
			if(AuthenticationServiceProvider.OPENID == authenticationServiceProvider) {
		 		ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
		            	user.getParentBinder(), ProfileCoreProcessor.PROCESSOR_KEY);
		 		// Make sure foreign name is identical to name.
				updates.put(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, user.getName().toLowerCase());
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
		
		// Are we running Filr and are we dealing with a user imported from ldap?
		if ( updateHomeFolder && Utils.checkIfFilr() && user.getIdentityInfo().isFromLdap() )
		{
			// Yes
			try
			{
                ldapModule.updateHomeDirectoryIfNecessary(user, userName, webClient);
			}
			catch ( Exception ex )
            {
                logger.error( "Unable to create, update or delete home directory net folder for user " + userName + ": " + ex.toString() );
                logger.debug(ex);
            }
		}

		// If still here, it means that authentication was successful.
		// Has the user logged in before?
		if( user.getFirstLoginDate() == null )
		{
			boolean setFirstLoginDate = true;
			
			// No
			// Are we dealing with the admin user?
			if ( ObjectKeys.SUPER_USER_INTERNALID.equals( user.getInternalId() ) )
			{
				// Yes
				// Are we running Vibe?
				if ( Utils.checkIfVibe() )
				{
					// Yes
					// Are we upgrading from Vibe 3.x?
					if( ExtendedPBEStringEncryptor.SYMMETRIC_ENCRYPTION_ALGORITHM_SECOND_GEN.equals(userPasswordEncryptionAlgorithmAtTheBeginning) )
					{
						// No
						setFirstLoginDate = false;
					}
				}
				else
				{
					// No, running Filr
					setFirstLoginDate = false;
				}
			}
			
			// If setFirstLoginDate is false, don't set the first login date.  We will set it after the admin has
			// changed the default password.  This enables us to prompt the admin to change their password.
			if ( setFirstLoginDate )
				getProfileModule().setFirstLoginDate(user.getId());
		}
		
		SimpleProfiler.stop( "3x-AuthenticationManagerImpl.authenticate()" );
		return user;
	}

	private void removeEverythingButEmailAddress(Map<String,String> updates) {
		if(updates == null) return;
		String emailAddress = updates.get("emailAddress");
		updates.clear();
		if(Validator.isNotNull(emailAddress))
			updates.put("emailAddress", emailAddress);
	}
	
	/*
	 * Handle authentication for all requests that didn't use OpenID. 
	 */
	protected Map doAuthenticateUser(AuthenticationServiceProvider authenticationServiceProvider, String zoneName, String username, String password,
				boolean passwordAutoSynch, boolean ignorePassword)
			throws PasswordDoesNotMatchException, UserDoesNotExistException, UserAccountNotActiveException, UserMismatchException {
		User user = null;

		SimpleProfiler.start( "3x-AuthenticationManagerImpl.doAuthenticate()" );

		try {
			String ldapGuid;
            // Get the zone id from the zone name.
            Long zoneId = ZoneUtil.getZoneIdByZoneName(zoneName);

            ldapGuid = null;
			
			if(AuthenticationServiceProvider.LOCAL != authenticationServiceProvider)
			{
                // No
		    	// Read this user's ldap guid from the ldap directory.
				ldapGuid = readLdapGuidFromDirectory( username, zoneId );
				
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
				user = getProfileDao().findUserByName(username, zoneId);

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
							throw new UserMismatchException( "The user account for the name, " + username + ", does not belong to the authenticated user" );
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

		Map result = new HashMap();
		if(user != null) {
			result.put("user", user);
			result.put("userPasswordEncryptionAlgorithmAtTheBeginning", user.getPwdenc());
		}
		
    	if(password != null) {
    		if(!EncryptUtil.checkPassword(password, user)) {
	   			// Password does not match.
	   			if(passwordAutoSynch) {
	   				// We don't want to sync the password if the user is one of the 5
	   				// system user accounts, "admin", "guest", "_postingAgent", "_jobProcessingAgent", "_synchronizationAgent", and "_fileSyncAgent.
	   				// Is the user a system user account?
	   				if ( !BuiltInUsersHelper.isSystemUserAccount( username ) )
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
		
		return result;
	}

	protected User fetchOpenidUser(String zoneName, String username) {
		User user = null;
		try {
			user = getProfileDao().findUserByName(username, ZoneUtil.getZoneIdByZoneName(zoneName));
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
    	// Only external users can use OpenID.
		if(user.getIdentityInfo().isInternal()) {
			// This shouldn't happen
			logger.warn("External user with username '" + username + "' matched an internal user account with id=" + user.getId());
			throw new UserDoesNotExistException("Unauthorized user [" 
						+ zoneName + "," + username + "]");
		}
		return user;
	}

	void modifyPassword(User user, String password) {
		Map updates = new HashMap();
		updates.put("password", password);
		getProfileModule().modifyUserFromPortal(user.getId(), updates, null);
        EncryptUtil.clearCachedPassword(user.getId());
	}
	
	@Override
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
				getReportModule().addLoginInfo(new LoginAudit(authenticatorName, ZoneContextHolder.getClientAddr(), user.getId()));

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
	
	private String readLdapGuidFromDirectory(String username, Long zoneId) {
		String ldapGuid;
		
		LdapConnectionConfig ldapConnectionConfig = null;
		
		String ldapConnectionConfigId = (String) AuthenticationContextHolder.getLdapAuthenticationInfo("ldapConnectionConfigId");
		
		if(ldapConnectionConfigId != null) {
			try {
				ldapConnectionConfig = getLdapModule().getConfigReadOnlyCache(zoneId, ldapConnectionConfigId);
			}
			catch(Exception e) {
				logger.warn("Error loading LDAP connection config object by ID [" + ldapConnectionConfigId + "]");
			}
		}
		
		if(ldapConnectionConfig != null) {
			// Limit search in LDAP only to the specific LDAP source identified by this configuration object.
			ldapGuid = ldapModule.readLdapGuidFromDirectory( username, zoneId, ldapConnectionConfig);
		}
		else {
			ldapGuid = ldapModule.readLdapGuidFromDirectory( username, zoneId );
		}
		
		return ldapGuid;
	}
}
