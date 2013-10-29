/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.profile.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import org.kablink.teaming.ApplicationExistsException;
import org.kablink.teaming.ApplicationGroupExistsException;
import org.kablink.teaming.GroupExistsException;
import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.PasswordMismatchException;
import org.kablink.teaming.UserExistsException;
import org.kablink.teaming.comparator.PrincipalComparator;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.RequestContextUtil;
import org.kablink.teaming.context.request.SessionContext;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.dao.util.GroupSelectSpec;
import org.kablink.teaming.domain.Application;
import org.kablink.teaming.domain.ApplicationGroup;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.HistoryStampBrief;
import org.kablink.teaming.domain.IdentityInfo;
import org.kablink.teaming.domain.IndividualPrincipal;
import org.kablink.teaming.domain.NoApplicationByTheNameException;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoDefinitionByTheIdException;
import org.kablink.teaming.domain.NoGroupByTheNameException;
import org.kablink.teaming.domain.NoUserByTheIdException;
import org.kablink.teaming.domain.NoUserByTheNameException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.SharedEntity;
import org.kablink.teaming.domain.TeamInfo;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.UserPropertiesPK;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.jobs.BinderReindex;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.profile.processor.ProfileCoreProcessor;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.module.template.TemplateModule;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.search.IndexErrors;
import org.kablink.teaming.search.IndexSynchronizationManager;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.survey.Survey;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.encrypt.EncryptUtil;
import org.kablink.teaming.web.util.DateHelper;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.EventHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * ?
 * 
 * Created on Nov 16, 2004
 * 
 * @author ?
 */
@SuppressWarnings("unchecked")
public class ProfileModuleImpl extends CommonDependencyInjection implements ProfileModule {
	private static final int DEFAULT_MAX_ENTRIES = ObjectKeys.LISTING_MAX_PAGE_SIZE;
	private final static long MEGABYTES = 1024L * 1024L;
	private String[] userDocType = {Constants.ENTRY_TYPE_USER};
	private String[] groupDocType = {Constants.ENTRY_TYPE_GROUP};
	private String[] applicationDocType = {Constants.ENTRY_TYPE_APPLICATION};
	private String[] applicationGroupDocType = {Constants.ENTRY_TYPE_APPLICATION_GROUP};
	private String[] individualPrincipalDocType = {Constants.ENTRY_TYPE_USER, Constants.ENTRY_TYPE_APPLICATION};
	private String[] groupPrincipalDocType = {Constants.ENTRY_TYPE_GROUP, Constants.ENTRY_TYPE_APPLICATION_GROUP};
	
	private String[] allPrincipalDocType = {Constants.ENTRY_TYPE_USER, Constants.ENTRY_TYPE_GROUP, Constants.ENTRY_TYPE_APPLICATION, Constants.ENTRY_TYPE_APPLICATION_GROUP};
	private List<String> guestSavedProps = Arrays.asList(new String[]{ObjectKeys.USER_PROPERTY_PERMALINK_URL});
    protected DefinitionModule definitionModule;
	protected DefinitionModule getDefinitionModule() {
		return definitionModule;
	}
	/**
	 * Setup by spring
	 * @param definitionModule
	 */
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}
    
    protected TemplateModule templateModule;
    protected TemplateModule getTemplateModule() {
    	return templateModule;
    }
    public void setTemplateModule(TemplateModule templateModule) {
    	this.templateModule = templateModule;
    }

    protected AdminModule adminModule;
    protected AdminModule getAdminModule() {
    	return adminModule;
    }
    public void setAdminModule(AdminModule adminModule) {
    	this.adminModule = adminModule;
    }
    
    protected BinderModule binderModule;
    protected BinderModule getBinderModule() {
    	return binderModule;
    }
    public void setBinderModule(BinderModule binderModule) {
    	this.binderModule = binderModule;
    }
    
    protected ProfileModule profileModule;
    protected ProfileModule getProfileModule() {
    	return profileModule;
    }
    public void setProfileModule(ProfileModule profileModule) {
    	this.profileModule = profileModule;
    }

    private TransactionTemplate transactionTemplate;
    protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
	/*
	 * Check access to folder.  If operation not listed, assume read_entries needed
	 * @see org.kablink.teaming.module.binder.BinderModule#checkAccess(org.kablink.teaming.domain.Binder, java.lang.String)
	 */
    //NO transaction
    @Override
	public boolean testAccess( User user, ProfileBinder binder, ProfileOperation operation)
    {
		try
		{
			checkAccess( user, binder, operation );
			return true;
		}
		catch (AccessControlException ac)
		{
			return false;
		}
	}// end testAccess()
    
    
	/*
	 * Check access to folder.  If operation not listed, assume read_entries needed
	 * @see org.kablink.teaming.module.binder.BinderModule#checkAccess(org.kablink.teaming.domain.Binder, java.lang.String)
	 */
    //NO transaction
    @Override
	public boolean testAccess(ProfileBinder binder, ProfileOperation operation) {
		try {
			checkAccess(binder, operation);
			return true;
		} catch (AccessControlException ac) {
			return false;
		}
	}

    /**
     * 
     */
	@Override
	public void checkAccess( User user, ProfileBinder binder, ProfileOperation operation) throws AccessControlException
	{
		switch (operation)
		{
			case addEntry:
		    	getAccessControlManager().checkOperation( user, binder, WorkAreaOperation.CREATE_ENTRIES );
		    	break;
			case manageEntries:
		    	getAccessControlManager().checkOperation( user, binder, WorkAreaOperation.BINDER_ADMINISTRATION );
		    	break;
			default:
		    	throw new NotSupportedException(operation.toString(), "checkAccess");				    		
		}
	}// end checkAccess()
	
	
    @Override
	public void checkAccess(ProfileBinder binder, ProfileOperation operation) throws AccessControlException {
		switch (operation) {
			case addEntry:
		    	getAccessControlManager().checkOperation(binder, WorkAreaOperation.CREATE_ENTRIES);
		    	break;
			case manageEntries:
		    	getAccessControlManager().checkOperation(binder, WorkAreaOperation.BINDER_ADMINISTRATION);
		    	break;
			default:
		    	throw new NotSupportedException(operation.toString(), "checkAccess");				    		
		}
	}
	protected void checkReadAccess(ProfileBinder binder) {
    	try {
    		getAccessControlManager().checkOperation(binder, WorkAreaOperation.READ_ENTRIES);
    	} catch(AccessControlException ace) {
    		try {
    			getAccessControlManager().checkOperation(binder, WorkAreaOperation.VIEW_BINDER_TITLE);
    		} catch(AccessControlException ace2) {
    			throw ace;
    		}
    	}
	}
	protected void checkReadAccess(Principal principal) {
    	AccessUtils.readCheck(principal);

	}
	/*
 	 * Check access to folder.  If operation not listed, assume read_entries needed
  	 * Use method names as operation so we can keep the logic out of application
	 * and easisly change the required rights
	 * @see org.kablink.teaming.module.profile.ProfileModule#testAccess(org.kablink.teaming.domain.Principal, java.lang.String)
	 */
    //NO transaction
	@Override
	public boolean testAccess(Principal entry, ProfileOperation operation) {
		try {
			checkAccess(entry, operation);
			return true;
		} catch (Exception ac) {
			return false;
		}
	}
	@Override
	public void checkAccess(Principal entry, ProfileOperation operation) throws AccessControlException {
		switch (operation) {
			case modifyEntry:
				//give users modify access to their own entry
				if (RequestContextHolder.getRequestContext().getUser().equals(entry)) return;
				AccessUtils.modifyCheck(entry);   		
				break;
			case deleteEntry:
				AccessUtils.deleteCheck(entry);   		
				break;
			default:
		    	throw new NotSupportedException(operation.toString(), "checkAccess");				    		
		}

	}
    private ProfileCoreProcessor loadProcessor(Binder binder) {
        // This is nothing but a dispatcher to an appropriate processor. 
        // Shared logic, if exists, must be put into the corresponding method in 
        // org.kablink.teaming.module.folder.AbstractfolderCoreProcessor class, not 
        // in this method.
	    return (ProfileCoreProcessor) getProcessorManager().getProcessor(binder, ProfileCoreProcessor.PROCESSOR_KEY);
	}
	private ProfileBinder loadProfileBinder() {
	   return (ProfileBinder)getProfileDao().getProfileBinder(RequestContextHolder.getRequestContext().getZoneId());
	}

	/**
	 * Determine if the Guest user has "create entry" rights to the profile binder.
	 */
	@Override
	public boolean doesGuestUserHaveAddRightsToProfileBinder()
	{
		ProfileBinder	profileBinder;
		User			guest;
		boolean		guestUserHasAddRights;
		
		guestUserHasAddRights = false;
			
		// Get the guest user.
		guest = getGuestUser();
		
		// Does the guest user have access to the profile binder?
		profileBinder = loadProfileBinder();
		if ( profileBinder != null && testAccess( guest, profileBinder, ProfileOperation.addEntry ) )
		{
			// Yes
			guestUserHasAddRights = true;
		}
		
		return guestUserHasAddRights;
	}// end doesGuestUserHaveAddRightsToProfileBinder()
	
	
	/**
	 * Check to see if a user or a group or an application or an application group exists with the given name.
	 */
	public Principal doesPrincipalExist( String name )
	{
		Collection<Principal> principals;
		List<String> names = new ArrayList<String>();

		// Does a principal exist with the given name?
		names.add( name );
		principals = getPrincipalsByName( names );
		if ( !principals.isEmpty() )
		{
			// Yes
			return principals.iterator().next();
		}
		
		// If we get here a principal does not exist with the given name.
		return null;
	}
	
	
	/**
	 * Return the guest user object.
	 */
	@Override
	public User getGuestUser()
	{
		ProfileDao	profileDao;
		User		guest;
		Long		zoneId;
		
		zoneId = RequestContextHolder.getRequestContext().getZoneId();
		profileDao = getProfileDao();
		guest = profileDao.getReservedUser( ObjectKeys.GUEST_USER_INTERNALID, zoneId );

		return guest;
	}// end getGuestUser()
	
	
	private User getUser(Long userId, boolean modify, boolean checkActive) {
  		User currentUser = RequestContextHolder.getRequestContext().getUser();
   		User user;
		if (userId == null) user = currentUser;
		else if (userId.equals(currentUser.getId())) user = currentUser;
		else {
			if (checkActive)
			     user = getProfileDao().loadUser(           userId, currentUser.getZoneId());
			else user = getProfileDao().loadUserDeadOrAlive(userId, currentUser.getZoneId());
			if (modify) AccessUtils.modifyCheck(user);
			else AccessUtils.readCheck(user);
		}
		return user;		
	}
	private User getUser(Long userId, boolean modify) {
		return getUser(userId, modify, true);
	}
	private UserPrincipal getUserPrincipal(Long prinId, boolean modify, boolean checkActive) {
   		UserPrincipal up = getProfileDao().loadUserPrincipal(prinId, RequestContextHolder.getRequestContext().getZoneId(), checkActive);
  		User currentUser = RequestContextHolder.getRequestContext().getUser();
  		if (!(currentUser.getId().equals(up.getId()))) {
			if (modify)
			     AccessUtils.modifyCheck(up);
			else AccessUtils.readCheck(  up);
  		}
		return up;		
	}
	private UserProperties getProperties(User user, Long binderId) {
		UserProperties uProps=null;
		if (user.isShared()) { //better be the current user
			UserPropertiesPK key = new UserPropertiesPK(user.getId(), binderId);
			uProps = (UserProperties)RequestContextHolder.getRequestContext().getSessionContext().getProperty(key);
			if (uProps == null) {
				//load any saved props
				UserProperties gProps = getProfileDao().loadUserProperties(user.getId(), binderId);
				uProps = new GuestProperties(gProps);
				RequestContextHolder.getRequestContext().getSessionContext().setProperty(key, uProps);					
			}
		} else {
			uProps = getProfileDao().loadUserProperties(user.getId(), binderId);
		}
		return uProps;
	}
	private UserProperties getProperties(User user) {
		UserProperties uProps=null;
		if (user.isShared()) { //better be the current user
			UserPropertiesPK key = new UserPropertiesPK(user.getId());
			SessionContext sc = RequestContextHolder.getRequestContext().getSessionContext();
			if(sc != null) {
				uProps = (UserProperties)sc.getProperty(key);
				if (uProps == null) {
					//Start with an empty set of properties for this guest user session
					UserProperties gProps = new UserProperties(user.getId());
					uProps = new GuestProperties(gProps);
					RequestContextHolder.getRequestContext().getSessionContext().setProperty(key, uProps);				
				}
			}
			else {
				// For whatever reason, there is no session context for the user
				UserProperties gProps = new UserProperties(user.getId());
				uProps = new GuestProperties(gProps);
			}
		} else {
			uProps = getProfileDao().loadUserProperties(user.getId());
		}
		return uProps;
	}
	//RO transaction
	@Override
	public ProfileBinder getProfileBinder() {
	   ProfileBinder binder = loadProfileBinder();
		// Check if the user has "read" access to the folder.
	   checkReadAccess(binder);		
	   return binder;
    }
	//RO transaction
	@Override
	public Long getProfileBinderId() {
	   return loadProfileBinder().getId();
    }

	//RO transaction
	@Override
	public Map<String, Definition> getProfileBinderEntryDefsAsMap() {
	   ProfileBinder binder = loadProfileBinder();
	   try {
		   checkReadAccess(binder);	
	   } catch(AccessControlException ac) {
		   if (!testAccess(binder, ProfileOperation.addEntry)) return null;
	   }
	   return DefinitionHelper.getEntryDefsAsMap(binder);
    }

    @Override
	public Long getEntryWorkspaceId(Long principalId) {
        Principal p = getProfileDao().loadPrincipal(principalId, RequestContextHolder.getRequestContext().getZoneId(), false);              
        return  p.getWorkspaceId();
    }
    
	@Override
	public List reindexPersonalUserOwnedBinders(Set<Principal> userIds) {
    	List<Long> binderIds = new ArrayList<Long>();
    	if (userIds == null) return binderIds;
    	binderIds = getProfileDao().getOwnedBinders(userIds);
    	
    	//Limit this list to binders under the Profiles binder
    	@SuppressWarnings("unused")
		Long profileBinderId = getProfileBinderId();
    	//Get sub-binder list including intermediate binders that may be inaccessible
    	Set<Binder> binders = getBinderModule().getBinders(binderIds, Boolean.FALSE);
    	for (Binder b : binders) {
    		if (!Utils.isWorkareaInProfilesTree(b)) binderIds.remove(b.getId());  //Remove any binder not under the profiles binder
    	}
    	
    	// Create background job to reindex the list of binders
    	User user = RequestContextHolder.getRequestContext().getUser();
    	String className = SPropsUtil.getString("job.binder.reindex.class", "org.kablink.teaming.jobs.DefaultBinderReindex");
		BinderReindex job = (BinderReindex)ReflectHelper.getInstance(className);
		job.scheduleNonBlocking(binderIds, user, false); 
    	return binderIds;
    }

    //RW transaction
	@Override
	public UserProperties setUserProperty(Long userId, Long binderId, String property, Object value) {
   		User user = getUser(userId, true);
		UserProperties uProps=getProperties(user, binderId);
		uProps.setProperty(property, value); 
		if (user.isShared() && guestSavedProps.contains(property)) {
			//get real props and save value
			UserProperties gProps = getProfileDao().loadUserProperties(user.getId(), binderId);
			gProps.setProperty(property, value);
		}
 		return uProps;
   }
    //RW transaction
	@Override
	public UserProperties setUserProperties(Long userId, Long binderId, Map<String, Object> values) {
   		User user = getUser(userId, true);
		UserProperties uProps=getProperties(user, binderId);
		UserProperties gProps=null;
		for (Map.Entry<String, Object> me: values.entrySet()) {
 			uProps.setProperty(me.getKey(), me.getValue()); //saved in requestContext
 			if (user.isShared() && guestSavedProps.contains(me.getKey())) {
 				//get real props and save value
 				if (gProps == null) gProps = getProfileDao().loadUserProperties(user.getId(), binderId);
 				gProps.setProperty(me.getKey(), me.getValue());
 			}
		}

  		return uProps;		   
	}
	   
	//RO transaction
   @Override
public UserProperties getUserProperties(Long userId, Long binderId) {
  		User user = getUser(userId, false);
		return getProperties(user, binderId);
   }

   //RW transaction
   @Override
public UserProperties setUserProperty(Long userId, String property, Object value) {
 		User user = getUser(userId, true);
		UserProperties uProps = getProperties(user);
		uProps.setProperty(property, value); 	
		if (user.isShared() && guestSavedProps.contains(property)) {
			//get real props and save value
			UserProperties gProps = getProfileDao().loadUserProperties(user.getId());
			gProps.setProperty(property, value);
		}
		return uProps;
    }
   //RW transaction
   @Override
public UserProperties setUserProperties(Long userId, Map<String, Object> values) {
		User user = getUser(userId, true);
		UserProperties uProps = getProperties(user);
		UserProperties gProps = null;
		for (Map.Entry<String, Object> me: values.entrySet()) {
 			uProps.setProperty(me.getKey(), me.getValue()); 
			if (user.isShared() && guestSavedProps.contains(me.getKey())) {
				//get real props and save value
				if (gProps == null) gProps = getProfileDao().loadUserProperties(user.getId());
 				gProps.setProperty(me.getKey(), me.getValue());
 			}
 		}
		return uProps;
	  
   }
	//RO transaction
   @Override
public UserProperties getUserProperties(Long userId) {
		User user = getUser(userId, false);
		return getProperties(user);
   }
 	//RO transaction
   @Override
public SeenMap getUserSeenMap(Long userId) {
		User user = getUser(userId, false);
		if (user.isShared()) return new SharedSeenMap(user.getId());
 		return getProfileDao().loadSeenMap(user.getId());
   }
   //RW transaction
   @Override
public void setSeen(Long userId, Entry entry) {
		User user = getUser(userId, true);
		if (user.isShared()) return;
		SeenMap seen = getProfileDao().loadSeenMap(user.getId());
		seen.setSeen(entry);
  }
   //RW transaction
   @Override
public void setSeen(Long userId, Collection<Entry> entries) {
		User user = getUser(userId, true);
		if (user.isShared()) return;
		SeenMap	seen = getProfileDao().loadSeenMap(user.getId());
		for (Entry reply:entries) {
			seen.setSeen(reply);
		}
  }  	
   @Override
public void setSeenIds(Long userId, Collection<Long> entryIds) {
		User user = getUser(userId, true);
		if (user.isShared()) return;
		SeenMap	seen = getProfileDao().loadSeenMap(user.getId());
		for (Long id:entryIds) {
			seen.setSeen(id);
		}
  }  	

   //RW transaction
   @Override
public void setUnseen(Long userId, Collection<Long> entryIds) {
		User user = getUser(userId, true);
		if (user.isShared()) return;
		SeenMap	seen = getProfileDao().loadSeenMap(user.getId());
		for (Long id:entryIds) {
			seen.setUnseen(id);
		}
   }  	

   //RW transaction
   @Override
public void setStatus(String status) {
	    User user = RequestContextHolder.getRequestContext().getUser();
	    user.setStatus(status);
   }  	
   //RW transaction
   @Override
public void setStatusDate(Date statusDate) {
	    User user = RequestContextHolder.getRequestContext().getUser();
	    user.setStatusDate(statusDate);
   }  

   //RW transaction
   @Override
public void setDiskQuota(long megabytes) {
	    User user = RequestContextHolder.getRequestContext().getUser();
	    user.setDiskQuota(megabytes);
   }  

   //RW transaction
   @Override
public void resetDiskUsage() {
	   getProfileDao().resetDiskUsage(RequestContextHolder.getRequestContext().getZoneId());
   }  
   
   //RO transaction
   @Override
public long getDiskQuota() {
	    User user = RequestContextHolder.getRequestContext().getUser();
	    return user.getDiskQuota();
   } 
   
   @Override
public long getMaxUserQuota() {
	   Long userId = RequestContextHolder.getRequestContext().getUserId();
	   return getMaxUserQuota(userId);
   }

   @Override
public long getMaxUserQuota(Long userId) {
		// first check properties to see if quotas are enabled on this system
		ZoneConfig zoneConf = getCoreDao().loadZoneConfig(
				RequestContextHolder.getRequestContext().getZoneId());

		User user = (User)getProfileDao().loadUserDeadOrAlive(userId, RequestContextHolder.getRequestContext().getZoneId());

		long userQuota = zoneConf.getDiskQuotaUserDefault();

		// User user = RequestContextHolder.getRequestContext().getUser();

		if (user.getDiskQuota() != 0L) {
			userQuota = user.getDiskQuota();
		} else {
			if (user.getMaxGroupsQuota() != 0L) {
				userQuota = user.getMaxGroupsQuota();
			}
		}
		userQuota = userQuota * MEGABYTES;
		return userQuota;
	}
   
   // RW transaction
   @Override
public void setUserDiskQuotas(Collection<Long> userIds, long megabytes) {
		//Set each users individual quota
     	for (Long id : userIds) {
			User user = (User)getProfileDao().loadUserDeadOrAlive(id, RequestContextHolder.getRequestContext().getZoneId());
			user.setDiskQuota(megabytes);
     	}
	}
   
    //RW transaction
	//Called when adding users to a group
	@Override
	public void setUserGroupDiskQuotas(Collection<Long> userIds, Group group) {
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		Long newDiskQuota = group.getDiskQuota();
		List userList = getProfileDao().loadUserPrincipals(userIds, zoneId, false);
		Iterator itUsers = userList.iterator();
		while (itUsers.hasNext()) {
			Principal p = (Principal) itUsers.next();
			if (p.getEntityType().equals(EntityIdentifier.EntityType.user)) {
				User user = (User)p;
				//See if this group's file size limit is bigger than what the user already has
				Long currentUserMaxGroupDiskQuota = user.getMaxGroupsQuota();
				
				//If the new value is bigger than what the user had, just set this as the new limit
				if (currentUserMaxGroupDiskQuota < newDiskQuota) {
					user.setMaxGroupsQuota(newDiskQuota);
				}
			}
		}
	}
	   
	//Called when deleting users from a group
    @Override
	public void deleteUserGroupDiskQuotas(Collection<Long> userIds, Group group) {
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		Long newDiskQuota = group.getDiskQuota();
		List userList = getProfileDao().loadUserPrincipals(userIds, zoneId, false);
		Iterator itUsers = userList.iterator();
		while (itUsers.hasNext()) {
			Principal p = (Principal) itUsers.next();
			if (p.getEntityType().equals(EntityIdentifier.EntityType.user)) {
				User user = (User)p;
				//See if this group's file size limit is bigger than what the user already has
				Long currentUserMaxGroupDiskQuota = user.getMaxGroupsQuota();
				
				//If the new value is less than or equal to what the user had, we must recalculate the quota
				if (currentUserMaxGroupDiskQuota <= newDiskQuota) {
					Set<Long> userGroupIds = getProfileDao().getApplicationLevelGroupMembership(user.getId(), zoneId);
					List<Group> groups = getProfileDao().loadGroups(userGroupIds, zoneId);
					Long maxGroupQuota = 0L;
					for (Group g : groups) {
						if (g.getDiskQuota() > maxGroupQuota) maxGroupQuota = g.getDiskQuota();
					}
					user.setMaxGroupsQuota(maxGroupQuota);
				}
			}
		}
	}

    @Override
	public void setGroupDiskQuotas(Collection<Long> groupIds, long newQuotaMegabytes) {
		// iterate through the members of a group - set each members max group quota to the 
	    // maximum value of all the groups they're a member of.
	   Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
	   Collection<GroupPrincipal> groupPrincipals = getProfileDao().loadGroupPrincipals(groupIds, zoneId, false);
		for (GroupPrincipal gp : groupPrincipals) {
			if (gp instanceof Group) {
				Group group = (Group)gp;
				if (group != null && group instanceof Group) {
					//We have to always check the members in case there are new members being added to the group
					Long originalGroupQuota = group.getDiskQuota();
					group.setDiskQuota(newQuotaMegabytes);
					List gIds = new ArrayList();
					gIds.add(group.getId());
					Set memberIds = getProfileDao().explodeGroups(gIds, zoneId);
					List memberList = getProfileDao().loadUserPrincipals(memberIds, zoneId, false);
					Iterator itUsers = memberList.iterator();
					while (itUsers.hasNext()) {
						Principal member = (Principal) itUsers.next();
						if (member.getEntityType().equals(EntityIdentifier.EntityType.user)) {
							User user = (User)member;
							//See if this new quota is bigger than what the user already has
							Long currentUserMaxGroupQuota = user.getMaxGroupsQuota();	//This returns 0 if no quota was set
							//If the current user max group quota didn't exist (i.e., 0) or is lower than the new quota, 
							//  then just set it to the new group quota 
							if (currentUserMaxGroupQuota < newQuotaMegabytes) {
								user.setMaxGroupsQuota(newQuotaMegabytes);
							
							//If the user was using this group's quota and that quota is being made smaller, 
							//  then we have to calculate the new maximum
							} else if (currentUserMaxGroupQuota <= originalGroupQuota &&
									newQuotaMegabytes < originalGroupQuota) {
								Set<Long> userGroupIds = getProfileDao().getApplicationLevelGroupMembership(user.getId(), zoneId);
								List<Group> groups = getProfileDao().loadGroups(userGroupIds, zoneId);
								Long maxGroupQuota = 0L;
								for (Group g : groups) {
									if (g.getDiskQuota() > maxGroupQuota) maxGroupQuota = g.getDiskQuota();
								}
								user.setMaxGroupsQuota(maxGroupQuota);
							}
						}
					}
				}
			}
		}
	}
   
   // RW transaction
   @Override
public void setUserFileSizeLimits(Collection<Long> userIds, Long fileSizeLimit) {
		//Set each users individual quota
     	for (Long id : userIds) {
			User user = (User)getProfileDao().loadUserDeadOrAlive(id, RequestContextHolder.getRequestContext().getZoneId());
			user.setFileSizeLimit(fileSizeLimit);
     	}
	}
   
	//RW transaction
	//Called when adding users to a group
	@Override
	public void setUserGroupFileSizeLimits(Collection<Long> userIds, Group group) {
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		Long newFileSizeLimit = group.getFileSizeLimit();	//can be null
		List userList = getProfileDao().loadUserPrincipals(userIds, zoneId, false);
		Iterator itUsers = userList.iterator();
		while (itUsers.hasNext()) {
			Principal p = (Principal) itUsers.next();
			if (p.getEntityType().equals(EntityIdentifier.EntityType.user)) {
				User user = (User)p;
				//See if this group's file size limit is bigger than what the user already has
				Long currentUserMaxGroupFileSizeLimit = user.getMaxGroupsFileSizeLimit();	//Can be null
				
				//If the new value is bigger than what the user had, just set this as the new limit
				if (newFileSizeLimit != null && 
						(currentUserMaxGroupFileSizeLimit == null || 
						currentUserMaxGroupFileSizeLimit < newFileSizeLimit)) {
					user.setMaxGroupsFileSizeLimit(newFileSizeLimit);
				}
			}
		}
	}
	
	//Called when removing users from a group
	@Override
	public void deleteUserGroupFileSizeLimits(Collection<Long> userIds, Group group) {
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		Long newFileSizeLimit = group.getFileSizeLimit();	//can be null
		List userList = getProfileDao().loadUserPrincipals(userIds, zoneId, false);
		Iterator itUsers = userList.iterator();
		while (itUsers.hasNext()) {
			Principal p = (Principal) itUsers.next();
			if (p.getEntityType().equals(EntityIdentifier.EntityType.user)) {
				User user = (User)p;
				//See if this group's file size limit is bigger than what the user already has
				Long currentUserMaxGroupFileSizeLimit = user.getMaxGroupsFileSizeLimit();	//Can be null
				
				//If the new value is the same as (or greater than) what the user had, 
				//  then we must recalculate the limit
				if (newFileSizeLimit != null && currentUserMaxGroupFileSizeLimit != null && 
						newFileSizeLimit >= currentUserMaxGroupFileSizeLimit) {
					Set<Long> userGroupIds = getProfileDao().getApplicationLevelGroupMembership(user.getId(), zoneId);
					List<Group> groups = getProfileDao().loadGroups(userGroupIds, zoneId);
					Long maxGroupFileSizeLimit = 0L;
					for (Group g : groups) {
						if (g.getFileSizeLimit() == null) {
							//One of the groups has no limit, so we are done
							maxGroupFileSizeLimit = null;
							break;
						} else if (g.getFileSizeLimit() > maxGroupFileSizeLimit) {
							maxGroupFileSizeLimit = g.getFileSizeLimit();
						}
					}
					user.setMaxGroupsFileSizeLimit(maxGroupFileSizeLimit);
				}
			}
		}
	}
	
	@Override
	public void setGroupFileSizeLimits(Collection<Long> groupIds, Long newFileSizeLimit) {
		// iterate through the members of a group - set each member's max file size limit to the 
	    // maximum value of all the groups they're a member of.
	   Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
	   Collection<GroupPrincipal> groupPrincipals = getProfileDao().loadGroupPrincipals(groupIds, zoneId, false);
		for (GroupPrincipal gp : groupPrincipals) {
			if (gp instanceof Group) {
				Group group = (Group)gp;
				//We always have to check each user in case there are new users being added to the group
				Long originalFileSizeLimit = group.getFileSizeLimit();	//Can be null
				group.setFileSizeLimit(newFileSizeLimit);
				List gIds = new ArrayList();
				gIds.add(group.getId());
				Set memberIds = getProfileDao().explodeGroups(gIds, zoneId);
				List memberList = getProfileDao().loadUserPrincipals(memberIds, zoneId, false);
				Iterator itUsers = memberList.iterator();
				while (itUsers.hasNext()) {
					Principal member = (Principal) itUsers.next();
					if (member.getEntityType().equals(EntityIdentifier.EntityType.user)) {
						User user = (User)member;
						//See if this new file size limit is bigger than what the user already has
						Long currentUserMaxGroupFileSizeLimit = user.getMaxGroupsFileSizeLimit();	//Can be null
						
						//If the new value is bigger than what the user had, just set this as the new limit
						if (currentUserMaxGroupFileSizeLimit == null || (newFileSizeLimit != null && 
								currentUserMaxGroupFileSizeLimit < newFileSizeLimit)) {
							user.setMaxGroupsFileSizeLimit(newFileSizeLimit);
							
						//If the user limit was equal to (or less than) the original group file size limit and
						//  the group file size limit is being made smaller (or being removed), 
						//  then we have to look for the new maximum file size limit for the user
						} else if (originalFileSizeLimit != null && 
								currentUserMaxGroupFileSizeLimit <= originalFileSizeLimit &&
								(newFileSizeLimit == null || newFileSizeLimit < originalFileSizeLimit)) {
							Set<Long> userGroupIds = getProfileDao().getApplicationLevelGroupMembership(user.getId(), zoneId);
							List<Group> groups = getProfileDao().loadGroups(userGroupIds, zoneId);
							Long maxGroupFileSizeLimit = 0L;
							for (Group g : groups) {
								if (g.getFileSizeLimit() == null) {
									//One of the groups has no limit, so we are done
									maxGroupFileSizeLimit = null;
									break;
								} else if (g.getFileSizeLimit() > maxGroupFileSizeLimit) {
									maxGroupFileSizeLimit = g.getFileSizeLimit();
								}
							}
							user.setMaxGroupsFileSizeLimit(maxGroupFileSizeLimit);
						}
					}
				}
			}
		}
	}
   
   // This returns a list of all disabled user account ids 
   @Override
public List<Long> getDisabledUserAccounts() {
	   return getProfileDao().getDisabledUserAccounts(RequestContextHolder.getRequestContext().getZoneId());
   }
   
   // this returns non-zero quotas (any quota which has been set by the admin)
   @Override
public List getNonDefaultQuotas(String type) {
	   return getProfileDao().getNonDefaultQuotas(type,
				RequestContextHolder.getRequestContext().getZoneId());
   }
   
   // this returns non-zero file size limits (any limit which has been set by the admin)
   @Override
public List getNonDefaultFileSizeLimits(String type) {
	   return getProfileDao().getNonDefaultFileSizeLimits(type,
				RequestContextHolder.getRequestContext().getZoneId());
   }
   
   // Walk through the user's group memberships, determine the max quota of all the groups
   public void resetMaxGroupsDiskQuota(Long userId) {
		User user = (User) getProfileDao().loadUserDeadOrAlive(userId,
				RequestContextHolder.getRequestContext().getZoneId());
		Long maxGroupsQuota = 0L;
		List groups = user.getMemberOf();
		for (Iterator i = groups.iterator(); i.hasNext();) {
			Group group = (Group) i.next();
			maxGroupsQuota = Math.max(maxGroupsQuota, group.getDiskQuota());
		}
		user.setMaxGroupsQuota(maxGroupsQuota);
	}

   @Override
public boolean isDiskQuotaExceeded() {
	   if (checkDiskQuota() == ObjectKeys.DISKQUOTA_EXCEEDED) return true;
	   else return false;
   }
   
   @Override
public boolean isDiskQuotaHighWaterMarkExceeded() {
	   if (checkDiskQuota() == ObjectKeys.DISKQUOTA_HIGHWATERMARK_EXCEEDED) return true;
	   else return false;
   }
   
   protected int checkDiskQuota() {
		// first check properties to see if quotas are enabled on this system
		ZoneConfig zoneConf = getCoreDao().loadZoneConfig(
				RequestContextHolder.getRequestContext().getZoneId());
		if (zoneConf.isDiskQuotaEnabled()) {
			User user = RequestContextHolder.getRequestContext().getUser();

			long userQuota = zoneConf.getDiskQuotaUserDefault();

			if (user.getDiskQuota() != 0L) {
				userQuota = user.getDiskQuota();
			} else {
				if (user.getMaxGroupsQuota() != 0L) {
					userQuota = user.getMaxGroupsQuota();
				}
			}
			if (userQuota == -1L) return ObjectKeys.DISKQUOTA_OK;  // -1 = unlimited
			
			userQuota = userQuota * MEGABYTES;

			long highWaterMark = zoneConf.getDiskQuotasHighwaterPercentage();
			double waterMark = (highWaterMark * userQuota) / 100.0;

			if (user.getDiskSpaceUsed() > userQuota) {
				return ObjectKeys.DISKQUOTA_EXCEEDED;
			} else if (user.getDiskSpaceUsed()*1.0 > waterMark) {
				return ObjectKeys.DISKQUOTA_HIGHWATERMARK_EXCEEDED;
			}
		}
		return ObjectKeys.DISKQUOTA_OK;
   }
   
   // RO transaction
   @Override
public Group getGroup(String name) {
	  Principal p = getProfileDao().findPrincipalByName(name, RequestContextHolder.getRequestContext().getZoneId());
	  if (!(p instanceof Group)) throw new NoGroupByTheNameException(name);
	  checkReadAccess(p);			
	  return (Group)p;
   }
	//RO transaction
   @Override
public Map getGroups() {
	   Map options = new HashMap();
	   options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(DEFAULT_MAX_ENTRIES));
	   return getGroups(options);
   }

   /**
	 * Return a list of groups that meet the given filter
	*/
   //RO transaction
   @Override
   public List<Group> getGroups( GroupSelectSpec groupSelectSpec )
   {
	   List<Group> listOfGroups;

	   listOfGroups = getProfileDao().findGroups( groupSelectSpec );
	   
	   return listOfGroups;
   }
   
   //RO transaction
   @Override
   public List<Group> getLdapContainerGroups() {
	   return getProfileDao().loadLdapContainerGroups(RequestContextHolder.getRequestContext().getZoneId());
   }

	//RO transaction
   @Override
public Map getGroups(Map options) {
		//does read access check
		ProfileBinder binder = getProfileBinder();
		options.put(ObjectKeys.SEARCH_MODE, Integer.valueOf(Constants.SEARCH_MODE_SELF_CONTAINED_ONLY));
        return loadProcessor(binder).getBinderEntries(binder, groupDocType, options);        
    }
	//RO transaction
	@Override
	public SortedSet<Group> getGroups(Collection<Long> entryIds) {
		//does read access check
		@SuppressWarnings("unused")
		ProfileBinder binder = getProfileBinder();
	    User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new PrincipalComparator(user.getLocale());
       	TreeSet<Group> result = new TreeSet(c);
       	result.addAll(getProfileDao().loadGroups(entryIds, user.getZoneId()));
		return result;
	}
	
	//RO transaction
	@Override
	public Principal getEntry(String name) {
		return getProfileDao().findPrincipalByName(name, RequestContextHolder.getRequestContext().getZoneId());
	}
	//RO transaction
	@Override
	public Principal getEntry(Long principalId) {
        Principal p = getProfileDao().loadPrincipal(principalId, RequestContextHolder.getRequestContext().getZoneId(), false);              
		//give users read access to their own entry
		if (!RequestContextHolder.getRequestContext().getUser().equals(p))
    		checkReadAccess(p);			
        return p;
    }

	@Override
	public SortedSet<Principal> getPrincipals(Collection<Long> ids) {
		//does read access check
		@SuppressWarnings("unused")
		ProfileBinder binder = getProfileBinder();
 	    User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new PrincipalComparator(user.getLocale());
       	TreeSet<Principal> result = new TreeSet(c);
 		result.addAll(getProfileDao().loadPrincipals(ids, user.getZoneId(), false));
 		return result;
	}
    
    //***********************************************************************************************************	
	@Override
	public IndexErrors indexEntry(Principal entry) {
        ProfileCoreProcessor processor=loadProcessor((ProfileBinder)entry.getParentBinder());
        return processor.indexEntry(entry);
	}

	/**
	 * Index all of the entries found in the given Collection
	 * @param entries
	 */
	@Override
	public IndexErrors indexEntries( Collection<Principal> entries )
	{
        ProfileCoreProcessor processor;

        processor = loadProcessor( getProfileBinder() );
        
        return processor.indexEntries( entries );
	}

    //NO transaction
    @Override
	public void modifyEntry(Long id, InputDataAccessor inputData) 
	throws AccessControlException, WriteFilesException, WriteEntryDataException {
    	modifyEntry(id, inputData, null, null, null, null);
    }
    //NO transaction
   @Override
public void modifyEntry(Long entryId, InputDataAccessor inputData, 
		   Map fileItems, Collection<String> deleteAttachments, Map<FileAttachment,String> fileRenamesTo, Map options) 
   		throws AccessControlException, WriteFilesException, WriteEntryDataException {
	   Principal entry = getProfileDao().loadPrincipal(entryId, RequestContextHolder.getRequestContext().getZoneId(), false);              
       Binder binder = entry.getParentBinder();
       ProfileCoreProcessor processor=loadProcessor(binder);
       //user can set their own display style and theme
       int noCheckCount = 0;
       if (inputData.exists(ObjectKeys.FIELD_USER_DISPLAYSTYLE)) ++noCheckCount;
       if (inputData.exists(ObjectKeys.FIELD_PRINCIPAL_THEME)) ++noCheckCount;
   	        
       if (!RequestContextHolder.getRequestContext().getUserId().equals(entryId) ||
    		   (inputData.getCount() == -1) ||
    		   (inputData.getCount() > noCheckCount)) {
    	   checkAccess(entry, ProfileOperation.modifyEntry);
       }
       List atts = new ArrayList();
       if (deleteAttachments != null) {
    	   for (Iterator iter=deleteAttachments.iterator(); iter.hasNext();) {
    		   String id = (String)iter.next();
    		   Attachment a = entry.getAttachment(id);
    		   if (a != null) atts.add(a);
    	   }
       }
       processor.modifyEntry(binder, entry, inputData, fileItems, atts, fileRenamesTo, options);
      }

   //NO transaction
    @Override
	public void addEntries(Document doc, Map options) {
       ProfileBinder binder = loadProfileBinder();
       checkAccess(binder, ProfileOperation.manageEntries);
       //process the document
       Element root = doc.getRootElement();
       List defList = root.selectNodes("/profiles/user");
       Map userLists = new HashMap();
       List<String> deleteNames = new ArrayList();
	   Definition defaultUserDef = binder.getDefaultEntryDef();		
	   if (defaultUserDef == null) {
		   // This user object is only used temporarily, so it doesn't really matter which identity source we set it to.
		   User temp = new User(new IdentityInfo());
		   getDefinitionModule().setDefaultEntryDefinition(temp);
		   defaultUserDef = getDefinitionModule().getDefinition(temp.getEntryDefId());
	   }
	   List defaultUserList = new ArrayList();
	   userLists.put(defaultUserDef, defaultUserList);
       //group users by defintion
       for (int i=0; i<defList.size(); ++i) {
    	   //get default definition to use
    	   Element user = (Element)defList.get(i);
    	   if ("delete".equals(user.attributeValue("operation"))) {
    		   Element nameEle = (Element)user.selectSingleNode("./attribute[@name='" + ObjectKeys.XTAG_PRINCIPAL_NAME + "']");
    		   if (nameEle == null) continue;
    		   String name = nameEle.getTextTrim();
    		   if (Validator.isNotNull(name)) deleteNames.add(name.toLowerCase());
    		   continue;
    	   }
    	   String defId = user.attributeValue("entryDef");
    	   Definition userDef=null;
    	   if (!Validator.isNull(defId)) {
    		   try {
    			   userDef = getDefinitionModule().getDefinition(defId);
    		   } catch (NoDefinitionByTheIdException nd) {
    			   logger.debug("ProfileModuleImpl.addEntries(NoDefinitionByTheIdException):  Ignored");
    		   };
    		   
    	   }
    	   if (userDef == null) defaultUserList.add(user);
    	   else {
    		   //see if it exists
    		   List userL = (List)userLists.get(userDef);
    		   if (userL == null) {
    			   userL = new ArrayList();
    			   userLists.put(userDef, userL);
    		   }
    		   userL.add(user);
    	   }
       }
       //delete users first
       if (!deleteNames.isEmpty()) {
    	   Map params = new HashMap();
    	   params.put("plist", deleteNames);
    	   params.put("zoneId", binder.getZoneId());
    	   List<Principal> deleteUsers = getCoreDao().loadObjects("from org.kablink.teaming.domain.User where zoneId=:zoneId and name in (:plist)", params);
    	   if (!deleteUsers.isEmpty()) {
    		   deleteEntries(binder, deleteUsers, options);
    	   }
       }
    		   //add entries grouped by definitionId
       for (Iterator iter=userLists.entrySet().iterator(); iter.hasNext();) {
    	   Map.Entry me = (Map.Entry)iter.next();
    	   List users = (List)me.getValue();
    	   Definition userDef = (Definition)me.getKey();
    	   addEntries(users, User.class, binder, userDef, options); 
    	}
       
       defList = root.selectNodes("/profiles/group");
       deleteNames.clear();
       List groupList = new ArrayList();
       for (int i=0; i<defList.size(); ++i) {
    	   //get default definition to use
    	   Element group = (Element)defList.get(i);
    	   if ("delete".equals(group.attributeValue("operation"))) {
    		   Element nameEle = (Element)group.selectSingleNode("./attribute[@name='" + ObjectKeys.XTAG_PRINCIPAL_NAME + "']");
    		   if (nameEle == null) continue;
    		   String name = nameEle.getTextTrim();
    		   if (Validator.isNotNull(name)) deleteNames.add(name.toLowerCase());
    		   continue;
    	   }
    	   groupList.add(group);
        }
       //delete groups first
       if (!deleteNames.isEmpty()) {
    	   Map params = new HashMap();
    	   params.put("plist", deleteNames);
    	   params.put("zoneId", binder.getZoneId());
    	   List<Principal> deleteGroups = getCoreDao().loadObjects("from org.kablink.teaming.domain.Group where zoneId=:zoneId and name in (:plist)", params);
    	   if (!deleteGroups.isEmpty()) {
    		   deleteEntries(binder, deleteGroups, options);
    	   }
       }

	   Group temp = new Group(new IdentityInfo());
	   getDefinitionModule().setDefaultEntryDefinition(temp);
	   Definition defaultGroupDef = getDefinitionModule().getDefinition(temp.getEntryDefId());
   	   addEntries(groupList, Group.class, binder, defaultGroupDef, options);  	   
    }
  	//no transaction
    private void deleteEntries(final ProfileBinder binder, final Collection<Principal> entries, final Map options) {
		getTransactionTemplate().execute(new TransactionCallback() {
        	@Override
			public Object doInTransaction(TransactionStatus status) {
        		   try {
        			   for (Principal p:entries) {
        				   deleteEntry(p.getId(), options);
        			   }
        		   } catch  (AccessControlException ac) {
        			   //can't do one, can't do any
        			   logger.error(ac.getLocalizedMessage());
        		   }
			return null;
	     }});

    }
    private void addEntries(List elements, Class clazz, ProfileBinder binder, Definition def, Map options) {
       ProfileCoreProcessor processor=loadProcessor(binder);
   	   Map newEntries = new TreeMap(String.CASE_INSENSITIVE_ORDER);
   	   Map oldEntries = new HashMap();
   	   Map foundNames = new TreeMap(String.CASE_INSENSITIVE_ORDER);
   	   for (int j=0; j<elements.size();) {
   		   newEntries.clear();
   		   for (int k=j; k < elements.size() && k-j<100; ++k) {
				ElementInputData e = new ElementInputData((Element)elements.get(k)); 
				String n = e.getSingleValue(ObjectKeys.XTAG_PRINCIPAL_NAME);
				if (Validator.isNull(n)) {
					logger.error("Name attribute missing: " + e.toString());
					continue;
				}
				if (foundNames.containsKey(n)) {
					logger.error("Duplicate name found: " + n);
				} else {
					newEntries.put(n, e);
					foundNames.put(n, Boolean.TRUE); //save as processed
				}
			}
			j+= 100;
			if (newEntries.isEmpty()) continue;
			//make sure don't exist
			Map params = new HashMap();
			params.put("plist", newEntries.keySet());
			params.put("zoneId", binder.getZoneId());
			List<Principal> exists = getCoreDao().loadObjects("from org.kablink.teaming.domain.Principal where zoneId=:zoneId and name in (:plist)", params);
			
			for (int x=0;x<exists.size(); ++x) {
				Principal p = (Principal)exists.get(x);
				ElementInputData data = (ElementInputData)newEntries.get(p.getName());
				if (data != null && !p.isDeleted()) {
					newEntries.remove(p.getName());
					oldEntries.put(p,data);
				}
				if(logger.isDebugEnabled())
					logger.debug("Principal exists: " + p.getName());
			}
			if (!newEntries.isEmpty() || !oldEntries.isEmpty()) {
				try {
					if (logger.isInfoEnabled()) {
						logger.info("Updating principals:");
						for (Iterator iter=oldEntries.keySet().iterator(); iter.hasNext();) {
							logger.info("'" + iter.next() + "'");
						}
					}
					processor.syncEntries(oldEntries, options, null );
					//processor commits entries - so update indexnow
					IndexSynchronizationManager.applyChanges();
				} catch (Exception ex) {
					IndexSynchronizationManager.discardChanges();
					logger.error("Error updating principals:", ex);
				}
				//flush from cache
				getCoreDao().evict(exists);
				//returns list of user objects
				try {
					if (logger.isInfoEnabled()) {
						logger.info("Creating principals:");
						for (Iterator iter=newEntries.keySet().iterator(); iter.hasNext();) {
							logger.info("'" + iter.next() + "'");
						}
					}
					// In this call context, we're simply assuming that we're creating internal local principals (is it safe?) 
					List addedEntries = processor.syncNewEntries(binder, def, clazz, new ArrayList(newEntries.values()), options, null, new IdentityInfo());
					//processor commits entries - so update indexnow
					IndexSynchronizationManager.applyChanges();
					//flush from cache
					getCoreDao().evict(addedEntries);			
				} catch (Exception ex) {
					IndexSynchronizationManager.discardChanges();
					logger.error("Error creating principals:", ex);
				}
			}
  	   }
    }
    //NO transaction
    @Override
	public Workspace addUserWorkspace(User entry, Map options) throws AccessControlException {
        if (entry.getWorkspaceId() != null) {
        	try {
        		return (Workspace)getCoreDao().loadBinder(entry.getWorkspaceId(), entry.getZoneId()); 
        	} catch (Exception ex) {
				logger.debug("ProfileModuleImpl.addUserWorkspace(Exception:  '" + MiscUtil.exToString(ex) + "'):  1:  Ignored");
        	};
        }
		Workspace ws = null;
		String wsTitle = entry.getWSTitle();
        RequestContext oldCtx = RequestContextHolder.getRequestContext();
        //want the user to be the creator
        RequestContextUtil.setThreadContext(entry).resolve();
 		try {	
  			if (!entry.isReserved() || (!ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID.equals(entry.getInternalId()) &&
  					!ObjectKeys.SYNCHRONIZATION_AGENT_INTERNALID.equals(entry.getInternalId()) &&
  					!ObjectKeys.FILE_SYNC_AGENT_INTERNALID.equals(entry.getInternalId()) &&
 					!ObjectKeys.JOB_PROCESSOR_INTERNALID.equals(entry.getInternalId()))) {
  				List templates;
  				if (!entry.getIdentityInfo().isInternal()) {
  					templates = getCoreDao().loadTemplates(entry.getZoneId(), Definition.EXTERNAL_USER_WORKSPACE_VIEW);
  				} else {
  					templates = getCoreDao().loadTemplates(entry.getZoneId(), Definition.USER_WORKSPACE_VIEW);
  				}

  				if (!templates.isEmpty()) {
  					//	pick the first
  					TemplateBinder template = (TemplateBinder)templates.get(0);
  					Long wsId = getTemplateModule().addBinder(template.getId(), entry.getParentBinder().getId(), wsTitle, entry.getName()).getId();
  					ws = (Workspace)getCoreDao().loadBinder(wsId, entry.getZoneId());
  				}
  			}
  			if (ws == null) {
  				//just load a workspace without all the stuff underneath
  				//processor handles transaction
  				Definition userDef;
  				if (!entry.getIdentityInfo().isInternal()) {
  					userDef = getDefinitionModule().addDefaultDefinition(Definition.EXTERNAL_USER_WORKSPACE_VIEW);
  				} else {
  					userDef = getDefinitionModule().addDefaultDefinition(Definition.USER_WORKSPACE_VIEW);
  				}
  				ProfileCoreProcessor processor=loadProcessor((ProfileBinder)entry.getParentBinder());
  				Map updates = new HashMap();
  				updates.put(ObjectKeys.FIELD_BINDER_NAME, entry.getName());
  				updates.put(ObjectKeys.FIELD_ENTITY_TITLE, wsTitle);
        		updates.put(ObjectKeys.INPUT_OPTION_FORCE_LOCK, Boolean.TRUE);
  				ws = (Workspace)processor.addBinder(entry.getParentBinder(), userDef, Workspace.class, new MapInputData(updates), null, options);
  				// the processor committed transaction, so make sure to get the index changes committed here as well
  				IndexSynchronizationManager.applyChanges();
  			}
  		} catch (WriteFilesException wf) {
   			logger.error("Error create user workspace: ", wf);
   			
   		} catch (WriteEntryDataException e) {
   			logger.error("Error create user workspace: ", e);
		} finally {
   			//	leave new context for indexing
   			RequestContextHolder.setRequestContext(oldCtx);				
   		}
  		
        return ws;
   }


    //RW transaction
    @Override
	public Folder addUserMiniBlog(User entry) throws AccessControlException {
        if (entry.getMiniBlogId() != null) {
        	try {
        		Folder miniblog = (Folder)getCoreDao().loadBinder(entry.getMiniBlogId(), entry.getZoneId()); 
        		if (!miniblog.isDeleted()) return miniblog;
			} catch(AccessControlException e) {
				return null;
        	} catch (Exception ex) {
        	}
        	
    		// Clear the old ID since we can't access it or it refers
        	// to a deleted folder.
    		entry.setMiniBlogId(null);
        }

		Folder miniBlog = null;
 		try {	
  			if (!entry.isReserved() || (!ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID.equals(entry.getInternalId()) &&
  					!ObjectKeys.SYNCHRONIZATION_AGENT_INTERNALID.equals(entry.getInternalId()) &&
  					!ObjectKeys.FILE_SYNC_AGENT_INTERNALID.equals(entry.getInternalId()) &&
 					!ObjectKeys.JOB_PROCESSOR_INTERNALID.equals(entry.getInternalId()))) {
				TemplateBinder miniblogTemplate = getTemplateModule().getTemplateByName(ObjectKeys.DEFAULT_TEMPLATE_NAME_MINIBLOG);
				if (miniblogTemplate != null) {
					// Can we find an existing MiniBlog Folder to use?
				    String defaultMiniBlogTitle = NLT.getDef(miniblogTemplate.getTitle());
				    if (Validator.isNull(defaultMiniBlogTitle)) {
				    	defaultMiniBlogTitle = NLT.getDef(miniblogTemplate.getTemplateTitle());
				    }
				    Long miniBlogId = findExistingMiniBlogFolderId(entry, defaultMiniBlogTitle);
				    if (null == miniBlogId) {
				    	// No!  Create one.
						miniBlogId = getTemplateModule().addBinder(miniblogTemplate.getId(), 
								entry.getWorkspaceId(), null, null).getId();
				    }
  					entry.setMiniBlogId(miniBlogId);
					if (miniBlogId != null) 
  						miniBlog = (Folder)getCoreDao().loadBinder(miniBlogId, entry.getZoneId());
  				}
  			}
   		} catch (Exception e) {
   			logger.error("Error create user MiniBlog: ", e);
   		}
  		
        return miniBlog;
   }
   private Long findExistingMiniBlogFolderId(User user, String defaultMiniBlogTitle)
   {
		// Scan the user's Binders.
	    Boolean checkTitle = (!(Validator.isNull(defaultMiniBlogTitle)));
		Binder userWorkspace = getCoreDao().loadBinder(user.getWorkspaceId(), user.getZoneId());
		List<Binder>binders = userWorkspace.getBinders();
		Long otherMiniBlogId = null;
	   	for (Binder binder:binders) {
	   		// Skip deleted Binders
	   		if (binder.isDeleted()) {
	   			continue;
	   		}
	   		
	   		// Does this Binder have a default view defined?
	   		Definition defaultBinderView = binder.getDefaultViewDef();
	   		if (null != defaultBinderView) {
	   			// Yes!  Is the default view a MiniBlog Folder?
	   			if (defaultBinderView.getName().equals("_miniBlogFolder")) {
	   				// Yes!  Does it use the default MiniBlog Folder
	   				// name?
	   				if ((!checkTitle) || binder.getTitle().equals(defaultMiniBlogTitle)) {
	   					// Yes!  Return its ID.
	   					otherMiniBlogId = binder.getId();
	   					break;
	   				}
	   				
	   				// No, it doesn't use the default MiniBlog Folder
	   				// name!  If we're not tracking another MiniBlog
	   				// Folder...
	   				else if (null == otherMiniBlogId) {
	   					// ...track this one.
	   					otherMiniBlogId = binder.getId();
	   				}
	   			}
	   		}
	   	}
	   		
	   	// If we get here, otherMiniBlogId is the ID of a MiniBlog
	   	// Folder to use or is null if we couldn't find one.  Return
	   	// it.
	   	return otherMiniBlogId;
   }

   //RW transaction
   @Override
public Folder setUserMiniBlog(User entry, Long folderId) throws AccessControlException {
		Folder miniBlog = null;
		
		entry.setMiniBlogId(folderId);
		if (folderId != null) 
			miniBlog = (Folder)getCoreDao().loadBinder(folderId, entry.getZoneId());
		
		return miniBlog;
   }

   //RW transaction
   @Override
public void disableEntry(Long principalId, boolean disabled) {
       Principal entry = getProfileDao().loadPrincipal(principalId, RequestContextHolder.getRequestContext().getZoneId(), false);
       checkAccess(entry, ProfileOperation.deleteEntry);
       if (entry.isReserved()) {
    	   throw new NotSupportedException("errorcode.principal.reserved", new Object[]{entry.getName()});
       }
       ProfileCoreProcessor processor=loadProcessor(entry.getParentBinder());
	   processor.disableEntry(entry, disabled);
   }
   //RW transaction
   @Override
public void deleteEntry(Long principalId, Map options) {
	   deleteEntry(principalId, options, false);
   }
   //RW transaction
   @Override
public void deleteEntry(Long principalId, Map options, boolean phase1Only) {
	   boolean delMirroredFolderSource = (!(Utils.checkIfFilr()));	// Only delete mirrored source by default if not Filr.
	   
        Principal entry = getProfileDao().loadPrincipal(principalId, RequestContextHolder.getRequestContext().getZoneId(), false);
        checkAccess(entry, ProfileOperation.deleteEntry);
       	if (entry.isReserved()) 
    		throw new NotSupportedException("errorcode.principal.reserved", new Object[]{entry.getName()});       	
        Binder binder = entry.getParentBinder();
        ProfileCoreProcessor processor=loadProcessor(binder);
       	processor.deleteEntry(binder, entry, true, options); 
       	boolean delWs = Boolean.FALSE;
       	if (options != null )
       	{
       		if ( options.containsKey(ObjectKeys.INPUT_OPTION_DELETE_USER_WORKSPACE) )
       			delWs = (Boolean)options.get(ObjectKeys.INPUT_OPTION_DELETE_USER_WORKSPACE);
       		
       		if ( options.containsKey( ObjectKeys.INPUT_OPTION_DELETE_MIRRORED_FOLDER_SOURCE ) )
       			delMirroredFolderSource = (Boolean) options.get( ObjectKeys.INPUT_OPTION_DELETE_MIRRORED_FOLDER_SOURCE );
       	}
       	
       	if (Boolean.TRUE.equals(delWs) && (entry instanceof User)) {
        	//delete workspace
        	User u = (User)entry;
        	Long wsId = u.getWorkspaceId();
        	if (wsId != null) {
        		try {
        			getBinderModule().deleteBinder(wsId, delMirroredFolderSource, options, phase1Only);
        			u.setWorkspaceId(null);       		
        		} catch (Exception ue) {}    
        	}
        }
     }
   // no transaction
	@Override
	public void deleteEntryFinish() {
		getBinderModule().deleteBinderFinish();
	}
    //RO transaction
    @Override
	public User getUser(String name) {
 	  Principal p = getProfileDao().findPrincipalByName(name, RequestContextHolder.getRequestContext().getZoneId());
 	  if (!(p instanceof User)) throw new NoUserByTheNameException(name);
 	  return (User)p;
    }

    
    /**
     * Get user by name even deleted or disabled users.
     * @param name
     * @return
     */
    //RO transaction
    @Override
	public User getUserDeadOrAlive( String name )
    {
 	  Principal principal;
 	  
 	  principal = getProfileDao().findUserByNameDeadOrAlive( name, RequestContextHolder.getRequestContext().getZoneName() );
 	  if ( !(principal instanceof User) )
 		  throw new NoUserByTheNameException( name );
 	  
 	  return (User)principal;
    }
    
    
    /**
     * Get user by id even deleted or disabled users.
     * @param name
     * @return
     */
    //RO transaction
    @Override
	public User getUserDeadOrAlive( Long userId )
    {
 	  Principal principal;

 	  principal = getProfileDao().loadUserPrincipal(
 			  									userId,
 			  									RequestContextHolder.getRequestContext().getZoneId(),
 			  									false );

 	  if ( !(principal instanceof User) )
 		  throw new NoUserByTheIdException( userId );
 	  
 	  return (User) principal;
    }
    
    

    //RO transaction
   @Override
public Map getUsers() {
    	Map options = new HashMap();
    	options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(DEFAULT_MAX_ENTRIES));
    	return getUsers(options);
    }
	//RO transaction
    @Override
	public Map getUsers(Map options) {
		//does read check
		ProfileBinder binder = getProfileBinder();
		options.put(ObjectKeys.SEARCH_MODE, Integer.valueOf(Constants.SEARCH_MODE_SELF_CONTAINED_ONLY));
        return loadProcessor(binder).getBinderEntries(binder, userDocType, options);
        
   }
	//RO transaction 
	@Override
	public SortedSet<User> getUsers(Collection<Long> entryIds) {
		//does read check on Profiles binder
        User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new PrincipalComparator(user.getLocale());
       	TreeSet<User> result = new TreeSet(c);
       	try {
			ProfileBinder profile = getProfileBinder();
	       	result.addAll(getProfileDao().loadUsers(entryIds, profile.getZoneId()));
       	} catch(AccessControlException ace) {}
 		return result;
	}
	  
	//RO transaction
	@Override
	public SortedSet<User> getUsersFromPrincipals(Collection<Long> principalIds) {
		//does read check
		ProfileBinder profile = getProfileBinder();
		Set ids = getProfileDao().explodeGroups(principalIds, profile.getZoneId());
		return getUsers(ids);
	}

	@Override
	public User findUserByName(String username)  throws NoUserByTheNameException {
		return getProfileDao().findUserByName(username, RequestContextHolder.getRequestContext().getZoneId());
	}
	
	@Override
	public User getReservedUser(String internalId) throws NoUserByTheNameException {
		return getProfileDao().getReservedUser(internalId, RequestContextHolder.getRequestContext().getZoneId());
	}
	
	/**
	 * Find the User with the given ldap guid
	 */
	@Override
	public User findUserByLdapGuid( String ldapGuid )  throws NoUserByTheNameException
	{
		return getProfileDao().findUserByLdapGuid( ldapGuid, RequestContextHolder.getRequestContext().getZoneId() );
	}// end findUserByLdapGuid()
	
	@Override
	public Collection<Principal> getPrincipalsByName(Collection<String> names) throws AccessControlException {
		Map params = new HashMap();
		params.put("zoneId", RequestContextHolder.getRequestContext().getZoneId());
		params.put("name", names);
		return getCoreDao().loadObjects("from org.kablink.teaming.domain.Principal where zoneId=:zoneId and name in (:name)", params);

	}
	public static class ElementInputData implements InputDataAccessor {
		private Element source;
		private Boolean fieldsOnly;

		public ElementInputData(Element source) {
			this.source = source;
			this.fieldsOnly = false;
		}
		@Override
		public String getSingleValue(String key) {
			Element result = (Element)source.selectSingleNode("./attribute[@name='" + key + "'] | ./property[@name='" + key + "']");
			if (result == null) return null;
			else return result.getTextTrim();
		}

		@Override
		public String[] getValues(String key) {
			List<Element> result = source.selectNodes("./attribute[@name='" + key + "'] | ./property[@name='" + key + "']");
			if ((result == null) || result.isEmpty()) return null;
			String [] resultVals = new String[result.size()];
			for (int i=0; i<resultVals.length; ++i) {
				resultVals[i] = result.get(i).getTextTrim();
			}
			return resultVals;
		}

		@Override
		public Date getDateValue(String key) {
			return DateHelper.getDateFromInput(this, key);
		}

		@Override
		public Event getEventValue(String key, boolean hasDuration, boolean hasRecurrence)
		{
			return EventHelper.getEventFromMap(this, key, hasDuration, hasRecurrence);
		}

		@Override
		public Survey getSurveyValue(String key)
		{
			return new Survey(key);
		}
		
		@Override
		public Description getDescriptionValue(String key) {
			return new Description(getSingleValue(key));
		}

		@Override
		public boolean exists(String key) {
			Element result = (Element)source.selectSingleNode("./attribute[@name='" + key + "'] | ./property[@name='" + key + "']");
			if (result == null) return false;
			return true;
		}

		@Override
		public Object getSingleObject(String key) {
			return (Element)source.selectSingleNode("./attribute[@name='" + key + "'] | ./property[@name='" + key + "']");
		}
		@Override
		public int getCount() {
			return source.nodeCount();
		}

		@Override
		public void setFieldsOnly(Boolean fieldsOnly) {
			this.fieldsOnly = fieldsOnly;
		}
		@Override
		public boolean isFieldsOnly() {
			return this.fieldsOnly;
		}
	}
	
    //NO transaction
	@Override
	public User addUserFromPortal(IdentityInfo identityInfo, String userName, String password, Map updates, Map options) {
		if(updates == null)
			updates = new HashMap();
		
		// The minimum we require is the last name. If it isn't available,
		// we use the user's login name as the last name just for now.
		// User can change it later if desired.
		if(updates.get("lastName") == null)
			updates.put("lastName", userName);
		
		// build user
		RequestContext oldCtx = RequestContextHolder.getRequestContext();
		Binder top = getCoreDao().findTopWorkspace(RequestContextHolder.getRequestContext().getZoneName());
		RequestContextUtil.setThreadContext(getProfileDao().getReservedUser(ObjectKeys.JOB_PROCESSOR_INTERNALID, top.getZoneId())).resolve();
		try {
			ProfileBinder profiles = getProfileDao().getProfileBinder(top.getZoneId());
			// indexing needs the user
			ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager()
					.getProcessor(profiles, profiles.getProcessorKey(ProfileCoreProcessor.PROCESSOR_KEY));
			Map newUpdates = new HashMap(updates);
			newUpdates.put(ObjectKeys.FIELD_PRINCIPAL_NAME, userName);
			if (Validator.isNotNull(password)) newUpdates.put(ObjectKeys.FIELD_USER_PASSWORD, password);
			//get default definition to use
			Definition userDef = profiles.getDefaultEntryDef();		
			if (userDef == null) userDef = getDefinitionModule().addDefaultDefinition(Definition.PROFILE_ENTRY_VIEW);
			List<InputDataAccessor>accessors = new ArrayList();
			accessors.add(new MapInputData(newUpdates));
		
			User user = (User)processor.syncNewEntries(profiles, userDef, User.class, accessors, options, null, identityInfo).get(0);
			// flush user before adding workspace
			IndexSynchronizationManager.applyChanges();
			
			addUserWorkspace(user, options);
			//do now, with request context set
			IndexSynchronizationManager.applyChanges();
			return user;
		} finally {
			// leave new context for indexing
			RequestContextHolder.setRequestContext(oldCtx);
		}
	}

    //No transaction
	@Override
	public void modifyUserFromPortal(Long userId, Map updates, Map options) {
		if (updates == null)
			return; // nothing to update with
		User user = getProfileDao().loadUser(userId, (Long) null);
		RequestContext oldCtx = RequestContextHolder.getRequestContext();
		RequestContextUtil.setThreadContext(user).resolve();
		try {
			//transaction handled in processor
			//use processor to handle title changes
			ProfileCoreProcessor processor = (ProfileCoreProcessor)getProcessorManager().getProcessor(user.getParentBinder(), 
					user.getParentBinder().getProcessorKey(ProfileCoreProcessor.PROCESSOR_KEY));
			processor.syncEntry(user, new MapInputData(updates), options);
			// flush user before adding workspace
			IndexSynchronizationManager.applyChanges();
			if (user.getWorkspaceId() == null) addUserWorkspace(user, options);

			//do now, with request context set
			IndexSynchronizationManager.applyChanges();
		} finally {
			//leave new context for indexing
			RequestContextHolder.setRequestContext(oldCtx);				
		};
	}

    @Override
	public User findOrAddExternalUser(final String emailAddress) {
        User user;
        try
        {
            // Does a Vibe account exist with the given name?
            user = getUser( emailAddress );
        }
        catch ( Exception ex )
        {
            RunasCallback callback;

            // If we get here a Vibe account does not exist for the given external user.
            // Create one.
            callback = new RunasCallback()
            {
                @Override
                public Object doAs()
                {
                    HashMap updates;
                    User user;

                    updates = new HashMap();
                    updates.put( ObjectKeys.FIELD_USER_EMAIL, emailAddress );
                    updates.put( ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, emailAddress );
                    updates.put( ObjectKeys.FIELD_USER_EXT_ACCOUNT_STATE, User.ExtProvState.initial );
                    // Do NOT set the "fromOpenid" bit on initially. We will set it when the user actually
                    // logs in and binds a valid OpenID account with the email address specified during sharing.
                    user = addUserFromPortal(
                            new IdentityInfo(false, false, false, false),
                            emailAddress,
                            null,
                            updates,
                            null );

                    return user;
                }
            };

            user = (User) RunasTemplate.runasAdmin(
                    callback,
                    RequestContextHolder.getRequestContext().getZoneName());
        }
        return user;
    }

	protected class SharedSeenMap extends SeenMap {
		public SharedSeenMap(Long principalId) {
			super(principalId);
		}
	    @Override
		public void setSeen(Entry entry) {	    	
	    }
	    @Override
		public void setSeen(FolderEntry entry) {
	    }
	    @Override
		public boolean checkIfSeen(FolderEntry entry) {
	    	return true;
	    }
		@Override
		protected boolean checkAndSetSeen(FolderEntry entry, boolean setIt) {
			return true;
		}
		@Override
		public boolean checkAndSetSeen(Map entry, boolean setIt) {
			return true;
		}	
	    @Override
		public boolean checkIfSeen(Map entry) {
	    	return true;
	    }   
	    
		@Override
		public boolean checkAndSetSeen(Long id, Date modDate, boolean setIt) {
			return true;
		}
    }

    //RW transaction
	@Override
	public void deleteUserByName(String userName,  Map options) {
		try {
			User user = getProfileDao().findUserByName(userName, 
					RequestContextHolder.getRequestContext().getZoneName());
			deleteEntry(user.getId(),options);
		}
		catch(NoUserByTheNameException thisIsOk) {}
	}
	
	//NO transaction
	@Override
	public User addUser(String definitionId, InputDataAccessor inputData, Map fileItems, Map options) 
	throws AccessControlException, WriteFilesException, WriteEntryDataException, PasswordMismatchException {
		if (inputData.getSingleValue("password") == null || inputData.getSingleValue("password").equals(""))
			throw new PasswordMismatchException("errorcode.password.cannotBeNull");
        ProfileBinder binder = loadProfileBinder();
        checkAccess(binder, ProfileOperation.addEntry);
		return (User) addIndividualPrincipal(definitionId, inputData, fileItems, options, User.class);
	}
	//NO transaction
	@Override
	public User addUser(InputDataAccessor inputData)
				throws AccessControlException, WriteFilesException, WriteEntryDataException {
		ProfileBinder binder = this.getProfileBinder();
		List defaultEntryDefinitions = binder.getEntryDefinitions();
		String definitionId = null;
		if (!defaultEntryDefinitions.isEmpty()) {
			Definition def = (Definition) defaultEntryDefinitions.get(0);
			definitionId = def.getId();
		}
		return this.addUser(definitionId, inputData, new HashMap(), null);
	}
	//NO transaction
	@Override
	public Application addApplication(String definitionId, 
			InputDataAccessor inputData, Map fileItems, Map options) 
	throws AccessControlException, WriteFilesException, WriteEntryDataException {
        ProfileBinder binder = loadProfileBinder();
        checkAccess(binder, ProfileOperation.manageEntries);
    	return (Application) addIndividualPrincipal(definitionId, inputData, fileItems, options, Application.class);
	}
    //NO transaction
    @Override
	public Group addGroup(String definitionId, InputDataAccessor inputData, Map fileItems, Map options) 
    	throws AccessControlException, WriteFilesException, WriteEntryDataException {
        ProfileBinder binder = loadProfileBinder();
        checkAccess(binder, ProfileOperation.manageEntries);
    	return (Group) addGroupPrincipal(definitionId, inputData, fileItems, options, Group.class);
    }
	
    //NO transaction
	@Override
	public ApplicationGroup addApplicationGroup(String definitionId, 
			InputDataAccessor inputData, Map fileItems, Map options) 
	throws AccessControlException, WriteFilesException, WriteEntryDataException {
        ProfileBinder binder = loadProfileBinder();
        checkAccess(binder, ProfileOperation.manageEntries);
    	return (ApplicationGroup) addGroupPrincipal(definitionId, inputData, fileItems, options, ApplicationGroup.class);
	}
	
	protected Entry addIndividualPrincipal(String definitionId, 
			InputDataAccessor inputData, Map fileItems, Map options, Class clazz) 
	throws AccessControlException, WriteFilesException, WriteEntryDataException {
        //Stricter access checking must be done by the caller
		ProfileBinder binder = loadProfileBinder();
        checkAccess(binder, ProfileOperation.addEntry);
        Definition definition;
        if (!Validator.isNull(definitionId))
        	definition = getCoreDao().loadDefinition(definitionId, binder.getZoneId());
        else {
        	// get the default
           	if(clazz.equals(User.class))
        		definition = getDefinitionModule().addDefaultDefinition(Definition.PROFILE_ENTRY_VIEW);
           	else
           		definition = getDefinitionModule().addDefaultDefinition(Definition.PROFILE_APPLICATION_VIEW);
        }
        try {
        	String name;
        	Principal principal;
        	
        	// Does a user or group or application or application group already exist with this name?
        	name = inputData.getSingleValue( "name" );
        	principal = doesPrincipalExist( name );
        	if ( principal != null )
        	{
        		EntityType entityType;
        		
        		// Yes
        		entityType = principal.getEntityType();
        		switch ( entityType )
        		{
        		case application:
        			throw new ApplicationExistsException();

        		case applicationGroup:
        			throw new ApplicationGroupExistsException();
        		
        		case group:
        			throw new GroupExistsException();
        			
        		case user:
        			throw new UserExistsException();
        			
        		default:
        			throw new UserExistsException();
        		}

        	}
        	
        	Entry newEntry = loadProcessor(binder).addEntry(binder, definition, clazz, inputData, fileItems, options);

            //Added to allow default groups to be defined for users in ssf.properties file
            if (clazz.equals(User.class))  //only do this for users not applications (maybe later;-)
            {
                List<Element> groups  = SZoneConfig.getElements("defaultGroupsOnAcctCreation/group");
                if (!groups.isEmpty()) {
                    for (Element elem: groups) {  //loop through all returned group names from properties file
                        try {
                            Group group = this.getGroup(elem.attributeValue("name"));
                            if (group!=null){ //make sure it finds the group.  what to do if it doesn't?
                                Map updates = new HashMap();
                                List members = new ArrayList(group.getMembers());
                                members.add((UserPrincipal)newEntry);
                                updates.put(ObjectKeys.FIELD_GROUP_PRINCIPAL_MEMBERS, members);
                                this.modifyEntry(group.getId(), new MapInputData(updates));
                            }
                        }catch (Exception e) {
                            //Do nothing.  User just won't exist in a group that was wrong anyway.
                            //won't completely abandon creation though.  Some may be defined correctly.
                            //Should log it.
                            logger.warn("Warning: User could not be added to default group.  Please check that " +
                            "the defined group in the properties file matches the one in the running system: ", e);
                        }
                    }
                }
                groups  = SZoneConfig.getElements("defaultGroupsOnExtAcctCreation/group");
                if (!groups.isEmpty()) {
                    for (Element elem: groups) {  //loop through all returned group names from properties file
                        try {
                            Group group = this.getGroup(elem.attributeValue("name"));
                            if (group!=null){ //make sure it finds the group.  what to do if it doesn't?
                                Map updates = new HashMap();
                                List members = new ArrayList(group.getMembers());
                                members.add((UserPrincipal)newEntry);
                                updates.put(ObjectKeys.FIELD_GROUP_PRINCIPAL_MEMBERS, members);
                                this.modifyEntry(group.getId(), new MapInputData(updates));
                            }
                        }catch (Exception e) {
                            //Do nothing.  User just won't exist in a group that was wrong anyway.
                            //won't completely abandon creation though.  Some may be defined correctly.
                            //Should log it.
                            logger.warn("Warning: User could not be added to default group.  Please check that " +
                            "the defined group in the properties file matches the one in the running system: ", e);
                        }
                    }
                }
            }
            return newEntry;
        } catch (DataIntegrityViolationException de) {
        	if(clazz.equals(User.class))
        		throw new UserExistsException(de);
        	else
            	throw new ApplicationExistsException(de);
        }
	}

	protected Entry addGroupPrincipal(String definitionId, 
			InputDataAccessor inputData, Map fileItems, Map options, Class clazz) 
	throws AccessControlException, WriteFilesException, WriteEntryDataException {
        ProfileBinder binder = loadProfileBinder();
        checkAccess(binder, ProfileOperation.manageEntries);
        Definition definition;
        if (!Validator.isNull(definitionId))
        	definition = getCoreDao().loadDefinition(definitionId, binder.getZoneId());
        else {
        	//get the default
        	if(clazz.equals(Group.class))
        		definition = getDefinitionModule().addDefaultDefinition(Definition.PROFILE_GROUP_VIEW);
        	else
        		definition = getDefinitionModule().addDefaultDefinition(Definition.PROFILE_APPLICATION_GROUP_VIEW);        		
        }
        try {
        	String name;
        	Principal principal;
        	
        	// Does a user or group or application or application group already exist with this name?
        	name = inputData.getSingleValue( "name" );
        	principal = doesPrincipalExist( name );
        	if ( principal != null )
        	{
        		EntityType entityType;
        		
        		// Yes
        		entityType = principal.getEntityType();
        		switch ( entityType )
        		{
        		case application:
        			throw new ApplicationExistsException();

        		case applicationGroup:
        			throw new ApplicationGroupExistsException();
        		
        		case group:
        			throw new GroupExistsException();
        			
        		case user:
        			throw new UserExistsException();
        			
        		default:
        			throw new UserExistsException();
        		}

        	}
        	
        	return loadProcessor(binder).addEntry(binder, definition, clazz, inputData, fileItems, options);
        } catch (DataIntegrityViolationException de) {
        	if(clazz.equals(Group.class))
        		throw new GroupExistsException(de);
        	else
        		throw new ApplicationGroupExistsException(de);        		
        }
	}
    //RO transaction
    @Override
	public ApplicationGroup getApplicationGroup(String name) {
 	  Principal p = getProfileDao().findPrincipalByName(name, RequestContextHolder.getRequestContext().getZoneId());
 	  if (!(p instanceof ApplicationGroup)) throw new NoGroupByTheNameException(name);
 	  return (ApplicationGroup)p;
    }

	//RO transaction
	@Override
	public Map getApplicationGroups() throws AccessControlException {
		   Map options = new HashMap();
		   options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(DEFAULT_MAX_ENTRIES));
		   return getApplicationGroups(options);
	}
	
	//RO transaction
	@Override
	public Map getApplicationGroups(Map searchOptions) throws AccessControlException {
		//does read access check
		ProfileBinder binder = getProfileBinder();
		searchOptions.put(ObjectKeys.SEARCH_MODE, Integer.valueOf(Constants.SEARCH_MODE_SELF_CONTAINED_ONLY));
        return loadProcessor(binder).getBinderEntries(binder, applicationGroupDocType, searchOptions);        
	}
	
	//RO transaction
	@Override
	public SortedSet<ApplicationGroup> getApplicationGroups(Collection<Long> groupIds) throws AccessControlException {
		//does read access check
		@SuppressWarnings("unused")
		ProfileBinder binder = getProfileBinder();
	    User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new PrincipalComparator(user.getLocale());
       	TreeSet<ApplicationGroup> result = new TreeSet(c);
       	result.addAll(getProfileDao().loadApplicationGroups(groupIds, user.getZoneId()));
		return result;	
	}
	
	//RO transaction
	@Override
	public Map getGroupPrincipals() throws AccessControlException {
		   Map options = new HashMap();
		   options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(DEFAULT_MAX_ENTRIES));
		   return getGroupPrincipals( options);	
	}
	
	//RO transaction
	@Override
	public Map getGroupPrincipals(Map searchOptions) throws AccessControlException {
		//does read access check
		ProfileBinder binder = getProfileBinder();
		searchOptions.put(ObjectKeys.SEARCH_MODE, Integer.valueOf(Constants.SEARCH_MODE_SELF_CONTAINED_ONLY));
        return loadProcessor(binder).getBinderEntries(binder, groupPrincipalDocType, searchOptions);        
	}
	
	//RO transaction
	@Override
	public SortedSet<GroupPrincipal> getGroupPrincipals(Collection<Long> groupIds) throws AccessControlException {
		//does read access check
		@SuppressWarnings("unused")
		ProfileBinder binder = getProfileBinder();
	    User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new PrincipalComparator(user.getLocale());
       	TreeSet<GroupPrincipal> result = new TreeSet(c);
       	result.addAll(getProfileDao().loadGroupPrincipals(groupIds, user.getZoneId(), true));
		return result;	
	}
	
    //RO transaction
    @Override
	public Application getApplication(String name) {
 	  Principal p = getProfileDao().findPrincipalByName(name, RequestContextHolder.getRequestContext().getZoneId());
 	  if (!(p instanceof Application)) throw new NoApplicationByTheNameException(name);
 	  return (Application)p;
    }
	//RO transaction
	@Override
	public Map getApplications() {
    	Map options = new HashMap();
    	options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(DEFAULT_MAX_ENTRIES));
    	return getApplications( options);
	}
	//RO transaction
	@Override
	public Map getApplications(Map searchOptions) {
		Map result = new HashMap();
		//does read access check
		try {
			ProfileBinder binder = getProfileBinder();
			searchOptions.put(ObjectKeys.SEARCH_MODE, Integer.valueOf(Constants.SEARCH_MODE_SELF_CONTAINED_ONLY));
	        result = loadProcessor(binder).getBinderEntries(binder, applicationDocType, searchOptions);
		} catch(AccessControlException e) {}
		return result;
	}
	//RO transaction
	@Override
	public SortedSet<Application> getApplications(Collection<Long> applicationIds) {
        User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new PrincipalComparator(user.getLocale());
       	TreeSet<Application> result = new TreeSet(c);
       	//does read check
		try {
			ProfileBinder profile = getProfileBinder();
	       	result.addAll(getProfileDao().loadApplications(applicationIds, profile.getZoneId()));
		} catch(AccessControlException e) {
			return result;
		}
 		return result;
	}
	
	//RO transaction
	@Override
	public Map getIndividualPrincipals() {
		   Map options = new HashMap();
		   options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(DEFAULT_MAX_ENTRIES));
		   return getIndividualPrincipals(options);	
	}
	//RO transaction
	@Override
	public Map getIndividualPrincipals( Map searchOptions) {
		//does read access check
		ProfileBinder binder = getProfileBinder();
		searchOptions.put(ObjectKeys.SEARCH_MODE, Integer.valueOf(Constants.SEARCH_MODE_SELF_CONTAINED_ONLY));
        return loadProcessor(binder).getBinderEntries(binder, individualPrincipalDocType, searchOptions);        
	}
	//RO transaction
	@Override
	public SortedSet<IndividualPrincipal> getIndividualPrincipals(Collection<Long> individualIds) {
		//does read access check
		@SuppressWarnings("unused")
		ProfileBinder binder = getProfileBinder();
	    User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new PrincipalComparator(user.getLocale());
       	TreeSet<IndividualPrincipal> result = new TreeSet(c);
       	result.addAll(getProfileDao().loadIndividualPrincipals(individualIds, user.getZoneId(), true));
		return result;	
	}
	//RO transaction
	@Override
	public Map getPrincipals( Map searchOptions) {
		//does read access check
		ProfileBinder binder = getProfileBinder();
		searchOptions.put(ObjectKeys.SEARCH_MODE, Integer.valueOf(Constants.SEARCH_MODE_SELF_CONTAINED_ONLY));
        return loadProcessor(binder).getBinderEntries(binder, allPrincipalDocType, searchOptions);        
	}
    //RO transaction
    @Override
	public List<SharedEntity> getShares(Long userId, Date after) {
	    User user = getUser(userId, false);
	    //get list of all groups user is a member of.
	    Set<Long> accessIds = getProfileDao().getApplicationLevelPrincipalIds(user);
		List<Map> myTeams = getBinderModule().getTeamMemberships(user.getId());
		Set<Long>binderIds = new HashSet();
		for(Map binder : myTeams) {
			try {
				binderIds.add(Long.valueOf((String)binder.get(Constants.DOCID_FIELD)));
			} catch (Exception ignore) {};
		}

	  List<SharedEntity> shares = getProfileDao().loadSharedEntities(accessIds, binderIds, after, user.getZoneId());
	  //need to check access
	  for (int i=0; i<shares.size();) {
		  SharedEntity se = shares.get(i);
		  try {
			  if (se.getEntity().isDeleted()) {
				  shares.remove(i);
				  continue;
			  }
		  } catch(Exception skipThis) {
			  shares.remove(i);
			  continue;
		  }
		  if (se.getEntity() instanceof Binder) {
				if (!getAccessControlManager().testOperation(user, (Binder)se.getEntity(), WorkAreaOperation.READ_ENTRIES) && 
						!getAccessControlManager().testOperation(user, (Binder)se.getEntity(), WorkAreaOperation.VIEW_BINDER_TITLE)) {
					shares.remove(i);
				} else {
					++i;
				}
		  } else {
			  try {
				  AccessUtils.readCheck((Entry)se.getEntity());
				  ++i;
			  } catch (Exception ex) {
					shares.remove(i);
			  }
		  }
		  //See if this one is the same as the previous one; filter out duplicates
		  if (i >= 2) {
			  SharedEntity seM1 = shares.get(i-1);
			  SharedEntity seM2 = shares.get(i-2);
			  if (seM1.getReferer().equals(seM2.getReferer()) && 
					  seM1.getSharedDate().getTime() == seM2.getSharedDate().getTime() &&
					  seM1.getEntity().equals(seM2.getEntity())) {
				  //These are the same, so remove one of them
				  shares.remove(i-1);
				  --i;
			  }
		  }
	  }
	  return shares;
    }
    //RW transaction
    @Override
	public void setShares(DefinableEntity entity, Collection<Long> principalIds, Collection<Long> binderIds) {
	    User user = RequestContextHolder.getRequestContext().getUser();
    	if (principalIds != null) {
    		for (Long p: principalIds) {
    			SharedEntity shared = new SharedEntity(user, entity, p, SharedEntity.ACCESS_TYPE_PRINCIPAL);
   	   			getCoreDao().save(shared);
      		}
    	}
    	if (binderIds != null) {
    		for (Long p: binderIds) {
    			SharedEntity shared = new SharedEntity(user, entity, p, SharedEntity.ACCESS_TYPE_TEAM);
    			getCoreDao().save(shared);
    		}
    	}
    }
    /* HOLD OFF - need better implementation */
    public boolean checkUserSeeCommunity() {
  	  return false;
//   	return getAccessControlManager().testOperation(this.getProfileBinder(), WorkAreaOperation.USER_SEE_COMMUNITY);        
  }

  public boolean checkUserSeeAll() {
  	return true;
//  	return getAccessControlManager().testOperation(this.getProfileBinder(), WorkAreaOperation.USER_SEE_ALL);        
  }

  @Override
public void changePassword(Long userId, String oldPassword, String newPassword) {
	  if(newPassword == null || newPassword.equals(""))
		  throw new PasswordMismatchException("errorcode.password.cannotBeNull");
	  User user = getUser(userId, true);

      if (mustSupplyOldPasswordToSetNewPassword(userId)) {
    	  // The user making the call does not have the right to manage profile entries. 
    	  // In this case, we require that the old password be specified. 
    	  // Note: This code needs to be kept in synch with the similar check in ModifyEntryController.java.
    	  // We require users changing their own password to know the old one
    	  // We also require that anyone changing the admin password know the old one
    	  if(oldPassword == null || 
    			  oldPassword.equals("") || 
    			  !EncryptUtil.checkPassword(oldPassword, user))
    		  // The user didn't enter the old/current password or they entered it incorrectly.
    		  throw new PasswordMismatchException("errorcode.password.invalid");
      }
      
      user.setPassword(newPassword);
  }
  
  @Override
public boolean mustSupplyOldPasswordToSetNewPassword(Long userId) {
	  User currentUser = RequestContextHolder.getRequestContext().getUser();
	  User user = getUser(userId, true);
	  ProfileBinder profileBinder = loadProfileBinder();

      return (!testAccess(profileBinder, ProfileOperation.manageEntries) || currentUser.getName().equals(user.getName()) ||
    		  user.isSuper() );
  }

  //RO transaction
  @Override
public SortedSet<User> getUsersByEmail(String emailAddress, String emailType) {
	  List<Principal> principals = getProfileDao().loadPrincipalByEmail(emailAddress, emailType, RequestContextHolder.getRequestContext().getZoneId());
      Comparator c = new PrincipalComparator(RequestContextHolder.getRequestContext().getUser().getLocale());
      TreeSet<User> result = new TreeSet(c);
	  for(Principal p:principals) {
		  //Make sure it is a user
		  try {
			  User user = (User)getProfileDao().loadUser(p.getId(), RequestContextHolder.getRequestContext().getZoneId());
			  result.add(user);
		  }
		  catch(Exception ignore) {}
	  }
	  return result;
  }
  
  @Override
public String[] getUsernameAndDecryptedPassword(String username) {
	  String[] result = new String[2];
	  try {
		  User user = findUserByName(username);
		  result[0] = user.getName();
		  result[1] = EncryptUtil.decryptPasswordForMatching(user);
	  }
	  catch(NoUserByTheNameException e) {
	  }
	  return result;
  }
  
	@Override
	public List<Group> getUserGroups(Long userId) throws AccessControlException {
		//does read access check
		@SuppressWarnings("unused")
		ProfileBinder binder = getProfileBinder();
		Set<Long> groupIds = getProfileDao().getApplicationLevelGroupMembership(userId, RequestContextHolder.getRequestContext().getZoneId());
		return getProfileDao().loadGroups(groupIds, RequestContextHolder.getRequestContext().getZoneId());
	}

    @Override
	public List<Binder> getUserFavorites(Long userId) {
        List<Binder> binders = new ArrayList<Binder>();
        Document favorites = null;
        UserProperties userProperties = getUserProperties(userId);
        Object obj = userProperties.getProperty(ObjectKeys.USER_PROPERTY_FAVORITES);

        if(obj != null) {
            if(obj instanceof Document) {
                favorites = (Document)obj;
            } else {
                try {
                    favorites = DocumentHelper.parseText((String) obj);
                } catch (DocumentException e) {}
            }
        }

        if(favorites != null) {
            java.util.Iterator it = favorites.getRootElement().selectNodes("favorite[@type=\"binder\"]").iterator();
            while(it.hasNext()) {
                Element e = (Element)it.next();
                Binder binder = getBinderIfAccessible(Long.valueOf(e.attributeValue("value")));
                if(binder != null){
                    binders.add(binder);
                }
            }
        }
        return binders;
    }

    @Override
	public List<TeamInfo> getUserTeams(Long userId) {
        List<Map> myTeams = getBinderModule().getTeamMemberships(userId);

        List<TeamInfo> teamList = new ArrayList<TeamInfo>();
        for(Map binder : myTeams) {
            String binderIdStr = (String) binder.get(Constants.DOCID_FIELD);
            Long binderId = (binderIdStr != null)? Long.valueOf(binderIdStr) : null;
            Boolean library = null;
            String libraryStr = (String) binder.get(Constants.IS_LIBRARY_FIELD);
            if(Constants.TRUE.equals(libraryStr))
                library = Boolean.TRUE;
            else if(Constants.FALSE.equals(libraryStr))
                library = Boolean.FALSE;

            Boolean mirrored = null;
            String mirroredStr = (String) binder.get(Constants.IS_MIRRORED_FIELD);
            if(Constants.TRUE.equals(mirroredStr))
                mirrored = Boolean.TRUE;
            else if(Constants.FALSE.equals(mirroredStr))
                mirrored = Boolean.FALSE;

            Boolean homeDir = null;
            String homeDirStr = (String) binder.get(Constants.IS_HOME_DIR_FIELD);
            if(Constants.TRUE.equals(homeDirStr))
                homeDir = Boolean.TRUE;
            else if(Constants.FALSE.equals(homeDirStr))
                homeDir = Boolean.FALSE;

            Boolean myFilesDir = null;
            String myFilesDirStr = (String) binder.get(Constants.IS_MYFILES_DIR_FIELD);
            if(Constants.TRUE.equals(myFilesDirStr))
                myFilesDir = Boolean.TRUE;
            else if(Constants.FALSE.equals(myFilesDirStr))
                myFilesDir = Boolean.FALSE;

            UserPrincipal creator = Utils.redactUserPrincipalIfNecessary(Long.valueOf((String) binder.get(Constants.CREATORID_FIELD)));
            UserPrincipal modifier = Utils.redactUserPrincipalIfNecessary(Long.valueOf((String) binder.get(Constants.MODIFICATIONID_FIELD)));

            Long parentBinderId = null;
            String parentBinderIdStr = (String) binder.get(Constants.BINDERS_PARENT_ID_FIELD);
            if(Validator.isNotNull(parentBinderIdStr))
                parentBinderId = Long.valueOf(parentBinderIdStr);

            TeamInfo info = new TeamInfo();
            info.setId(binderId);
            info.setTitle((String) binder.get(Constants.TITLE_FIELD));
            info.setEntityType((String)binder.get(Constants.ENTITY_FIELD));
            info.setFamily((String) binder.get(Constants.FAMILY_FIELD));
            info.setLibrary(library);
            info.setDefinitionType(Integer.valueOf((String)binder.get(Constants.DEFINITION_TYPE_FIELD)));
            info.setPath((String) binder.get(Constants.ENTITY_PATH));
            info.setCreation(
                    new HistoryStampBrief(((creator != null)? creator.getName() : (String) binder.get(Constants.CREATOR_NAME_FIELD)),
                            Long.valueOf((String)binder.get(Constants.CREATORID_FIELD)),
                            (Date) binder.get(Constants.CREATION_DATE_FIELD)));
            info.setModification(
                    new HistoryStampBrief(((modifier != null)? modifier.getName() : (String) binder.get(Constants.MODIFICATION_NAME_FIELD)),
                            Long.valueOf((String)binder.get(Constants.MODIFICATIONID_FIELD)),
                            (Date) binder.get(Constants.MODIFICATION_DATE_FIELD)));
            info.setPermaLink(PermaLinkUtil.getPermalink(binder));
            info.setMirrored(mirrored);
            info.setHomeDir(homeDir);
            info.setMyFilesDir(myFilesDir);
            info.setParentBinderId(parentBinderId);
            teamList.add(info);
        }
        return teamList;
    }

    private org.kablink.teaming.domain.Binder getBinderIfAccessible(Long binderId) {
   		try {
   			return getBinderModule().getBinder(binderId);
   		}
   		catch(NoBinderByTheIdException e) {
   			return null;
   		}
   		catch(AccessControlException e) {
   			return null;
   		}
   	}

    @Override
	public void setFirstLoginDate(Long userId) {
		User user = getUser(userId, true);
		if(user.getFirstLoginDate() != null)
			return; // This user already logged in before. Shouldn't update it (or shall we throw an exception?)
		user.setFirstLoginDate(new Date()); // Set it to current date/time.
    }
    

    /**
     * Returns a User's workspace pre-deleted flag.
     * 
     * @param userId
     */
    //RO transaction
    @Override
    public Boolean getUserWorkspacePreDeleted(Long userId) {
   		User user = getUser(userId, true, false);
		return user.isWorkspacePreDeleted();
    }
    
    /**
     * Sets a User's workspace pre-deleted flag.
     * 
     * @param userId
     * @param userWorkspacePreDeleted
     */
    //RW transaction
    @Override
    public void setUserWorkspacePreDeleted(Long userId, boolean userWorkspacePreDeleted) {
   		User user = getUser(userId, true, false);
		user.setWorkspacePreDeleted(userWorkspacePreDeleted);
    }

    /**
     * Returns a user or group's download enabled flag.
     * 
     * @param upId
     */
    //RO transaction
    @Override
    public Boolean getDownloadEnabled(Long upId) {
   		UserPrincipal up = getUserPrincipal(upId, false, false);
		return up.isDownloadEnabled();
    }
    
    /**
     * Sets a user or group's downloadEnabled flag.
     * 
     * @param upId
     * @param downloadEnabled
     */
    //RW transaction
    @Override
    public void setDownloadEnabled(Long upId, Boolean downloadEnabled) {
   		UserPrincipal up = getUserPrincipal(upId, true, false);
		up.setDownloadEnabled(downloadEnabled);
    }

    /**
     * Returns a user or group's web access enabled flag.
     * 
     * @param upId
     */
    //RO transaction
    @Override
    public Boolean getWebAccessEnabled(Long upId) {
   		UserPrincipal up = getUserPrincipal(upId, false, false);
		return up.isWebAccessEnabled();
    }
    
    /**
     * Sets a user or group's web access enabled flag.
     * 
     * @param upId
     * @param webAccessEnabled
     */
    //RW transaction
    @Override
    public void setWebAccessEnabled(Long upId, Boolean webAccessEnabled) {
   		UserPrincipal up = getUserPrincipal(upId, true, false);
		up.setWebAccessEnabled(webAccessEnabled);
    }

    /**
     * Returns a user or group's adHoc folders flag.
     * 
     * @param upId
     */
    //RO transaction
    @Override
    public Boolean getAdHocFoldersEnabled(Long upId) {
   		UserPrincipal up = getUserPrincipal(upId, false, false);
		return up.isAdHocFoldersEnabled();
    }
    
    /**
     * Sets a user or group's web access enabled flag.
     * 
     * @param upId
     * @param aAdHocFoldersEnabled
     */
    //RW transaction
    @Override
    public void setAdHocFoldersEnabled(Long upId, Boolean adHocFoldersEnabled) {
   		UserPrincipal up = getUserPrincipal(upId, true, false);
		up.setAdHocFoldersEnabled(adHocFoldersEnabled);
    }

    /**
     * Returns a Collection<User> of all the external user's the
     * current user has rights to see.
     * 
     * @return
     */
    @Override
    public Collection<User> getAllExternalUsers() {
    	// Allocate a collection we can return.
		List<User> reply = new ArrayList<User>();

		// Can we access the ID of the all external users group?
    	ProfileDao pd            = getProfileDao();
    	Long       zoneId        = RequestContextHolder.getRequestContext().getZoneId();
    	Long       allExtUsersId = pd.getReservedGroupId(ObjectKeys.ALL_EXT_USERS_GROUP_INTERNALID, zoneId);
    	if (null == allExtUsersId) {
    		// No!  Bail.
    		return reply;
    	}
    	
		// Can we get the members of the group?
    	List<Long> allExtUsersIds = new ArrayList<Long>();
    	allExtUsersIds.add(allExtUsersId);
    	Set<Long> extIds = pd.explodeGroups(allExtUsersIds, zoneId);
    	if (!(MiscUtil.hasItems(extIds))) {
    		// No!  Bail.
    		return reply;
    	}
    	
		// Resolve the members.  We call ResolveIDs.getPrincipals()
		// because it handles deleted users and users the logged-in
    	// user has rights to see.  Does the groups members resolve
    	// to any users?
		List<Principal> extUsers = ResolveIds.getPrincipals(extIds);
		if (!(MiscUtil.hasItems(extUsers))) {
			// No!  Bail.
			return reply;
		}

		// Scan the group members.
		for (Principal p:  extUsers) {
			// Is this member a non reserved User?
			if ((p instanceof UserPrincipal) && (!(p.isReserved()))) {
				// Yes!  Add it to the reply collection.
				reply.add((User) p);
			}
		}

		// If we get here, reply refers to a Collection<User> of the
		// external users.  Return it.
    	return reply;
    }
}
