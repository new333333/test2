package com.sitescape.ef.security.authentication.impl;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.ef.context.request.RequestContext;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.context.request.RequestContextUtil;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.dao.ProfileDao;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.NoBinderByTheNameException;
import com.sitescape.ef.domain.NoUserByTheIdException;
import com.sitescape.ef.domain.NoUserByTheNameException;
import com.sitescape.ef.domain.NoWorkspaceByTheNameException;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.modelprocessor.ProcessorManager;
import com.sitescape.ef.module.admin.AdminModule;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.profile.ProfileCoreProcessor;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.module.shared.EntryBuilder;
import com.sitescape.ef.search.IndexSynchronizationManager;
import com.sitescape.ef.security.authentication.AuthenticationManager;
import com.sitescape.ef.security.authentication.PasswordDoesNotMatchException;
import com.sitescape.ef.security.authentication.UserDoesNotExistException;
import com.sitescape.ef.util.PasswordEncryptor;
import com.sitescape.ef.util.SPropsUtil;

public class AuthenticationManagerImpl implements AuthenticationManager {
	protected Log logger = LogFactory.getLog(getClass());

	private ProfileDao profileDao;
	private CoreDao coreDao;
	private AdminModule adminModule;
	private DefinitionModule definitionModule;
	private ProfileModule profileModule;
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
	protected DefinitionModule getDefinitionModule() {
		return definitionModule;
	}
	protected ProcessorManager getProcessorManager() {
		return processorManager;
	}
	public void setProcessorManager(ProcessorManager processorManager) {
		this.processorManager = processorManager;
	}
	protected ProfileModule getProfileModule() {
		return profileModule;
	}
	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}

	/**
	 * Setup by spring
	 * @param definitionModule
	 */
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
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
		}
    	catch(NoUserByTheNameException e) {
    		user = checkZone(zoneName, username);
    		if (user == null) 
    				throw new UserDoesNotExistException("Authentication failed: Unrecognized user [" 
     						+ zoneName + "," + username + "]", e);
    	}
    	
    	if(!PasswordEncryptor.encrypt(password).equals(user.getPassword())) {
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
			return null;
		} catch (NoWorkspaceByTheNameException nw) {
		} catch (NoBinderByTheNameException nb) {};
		try {
			getAdminModule().addZone(zoneName);
			return getProfileDao().findUserByNameOnlyIfEnabled(userName, zoneName);
		} catch (Exception ex) {
			return null;
		}
		
	}
}
