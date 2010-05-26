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
/*
 * Created on Nov 16, 2004
 *
 */
package org.kablink.teaming.module.profile.impl;
import java.io.StringReader;
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
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.ObjectExistsException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.PasswordMismatchException;
import org.kablink.teaming.comparator.PrincipalComparator;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.RequestContextUtil;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Application;
import org.kablink.teaming.domain.ApplicationGroup;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.IndividualPrincipal;
import org.kablink.teaming.domain.NoApplicationByTheNameException;
import org.kablink.teaming.domain.NoDefinitionByTheIdException;
import org.kablink.teaming.domain.NoGroupByTheNameException;
import org.kablink.teaming.domain.NoUserByTheNameException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.SharedEntity;
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
import org.kablink.teaming.module.profile.ProfileModule.ProfileOperation;
import org.kablink.teaming.module.profile.processor.ProfileCoreProcessor;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.module.template.TemplateModule;
import org.kablink.teaming.repository.RepositoryServiceException;
import org.kablink.teaming.search.IndexErrors;
import org.kablink.teaming.search.IndexSynchronizationManager;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.survey.Survey;
import org.kablink.teaming.util.EncryptUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.util.DateHelper;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.EventHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;


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
    		getAccessControlManager().checkOperation(binder, WorkAreaOperation.VIEW_BINDER_TITLE);
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
	public boolean testAccess(Principal entry, ProfileOperation operation) {
		try {
			checkAccess(entry, operation);
			return true;
		} catch (AccessControlException ac) {
			return false;
		}
	}
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
	 * Return the guest user object.
	 */
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
	
	
	private User getUser(Long userId, boolean modify) {
  		User currentUser = RequestContextHolder.getRequestContext().getUser();
   		User user;
		if (userId == null) user = currentUser;
		else if (userId.equals(currentUser.getId())) user = currentUser;
		else {
			user = getProfileDao().loadUser(userId, currentUser.getZoneId());
			if (modify) AccessUtils.modifyCheck(user);
			else AccessUtils.readCheck(user);
		}
		return user;		
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
			uProps = (UserProperties)RequestContextHolder.getRequestContext().getSessionContext().getProperty(key);
			if (uProps == null) {
				//load any saved props
				UserProperties gProps = getProfileDao().loadUserProperties(user.getId());
				uProps = new GuestProperties(gProps);
				RequestContextHolder.getRequestContext().getSessionContext().setProperty(key, uProps);				
			}
		} else {
			uProps = getProfileDao().loadUserProperties(user.getId());
		}
		return uProps;
	}
	//RO transaction
	public ProfileBinder getProfileBinder() {
	   ProfileBinder binder = loadProfileBinder();
		// Check if the user has "read" access to the folder.
	   checkReadAccess(binder);		
	   return binder;
    }
	//RO transaction
	public Long getProfileBinderId() {
	   return loadProfileBinder().getId();
    }

	//RO transaction
	public Map<String, Definition> getProfileBinderEntryDefsAsMap() {
	   ProfileBinder binder = loadProfileBinder();
	   try {
		   checkReadAccess(binder);	
	   } catch(AccessControlException ac) {
		   if (!testAccess(binder, ProfileOperation.addEntry)) return null;
	   }
	   return DefinitionHelper.getEntryDefsAsMap(binder);
    }

    public Long getEntryWorkspaceId(Long principalId) {
        Principal p = getProfileDao().loadPrincipal(principalId, RequestContextHolder.getRequestContext().getZoneId(), false);              
        return  p.getWorkspaceId();
    }
    
    public List reindexPersonalUserOwnedBinders(Set<Principal> userIds) {
    	List<Long> binderIds = new ArrayList<Long>();
    	if (userIds == null) return binderIds;
    	binderIds = getProfileDao().getOwnedBinders(userIds);
    	
    	//Limit this list to binders under the Profiles binder
    	Long profileBinderId = getProfileBinderId();
    	Set<Binder> binders = getBinderModule().getBinders(binderIds);
    	for (Binder b : binders) {
    		if (!Utils.isWorkareaInProfilesTree(b)) binderIds.remove(b.getId());  //Remove any binder not under the profiles binder
    	}
    	
    	// Create background job to reindex the list of binders
    	User user = RequestContextHolder.getRequestContext().getUser();
		BinderReindex job=null;
		if (job == null) job = (BinderReindex)ReflectHelper.getInstance(org.kablink.teaming.jobs.DefaultBinderReindex.class);
		job.schedule(binderIds, user); 
    	return binderIds;
    }

    //RW transaction
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
   public UserProperties getUserProperties(Long userId, Long binderId) {
  		User user = getUser(userId, false);
		return getProperties(user, binderId);
   }

   //RW transaction
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
   public UserProperties getUserProperties(Long userId) {
		User user = getUser(userId, false);
		return getProperties(user);
   }
 	//RO transaction
   public SeenMap getUserSeenMap(Long userId) {
		User user = getUser(userId, false);
		if (user.isShared()) return new SharedSeenMap(user.getId());
 		return getProfileDao().loadSeenMap(user.getId());
   }
   //RW transaction
   public void setSeen(Long userId, Entry entry) {
		User user = getUser(userId, true);
		if (user.isShared()) return;
		SeenMap seen = getProfileDao().loadSeenMap(user.getId());
		seen.setSeen(entry);
  }
   //RW transaction
   public void setSeen(Long userId, Collection<Entry> entries) {
		User user = getUser(userId, true);
		if (user.isShared()) return;
		SeenMap	seen = getProfileDao().loadSeenMap(user.getId());
		for (Entry reply:entries) {
			seen.setSeen(reply);
		}
  }  	
   public void setSeenIds(Long userId, Collection<Long> entryIds) {
		User user = getUser(userId, true);
		if (user.isShared()) return;
		SeenMap	seen = getProfileDao().loadSeenMap(user.getId());
		for (Long id:entryIds) {
			seen.setSeen(id);
		}
  }  	

   //RW transaction
   public void setUnseen(Long userId, Collection<Long> entryIds) {
		User user = getUser(userId, true);
		if (user.isShared()) return;
		SeenMap	seen = getProfileDao().loadSeenMap(user.getId());
		for (Long id:entryIds) {
			seen.setUnseen(id);
		}
   }  	

   //RW transaction
   public void setStatus(String status) {
	    User user = RequestContextHolder.getRequestContext().getUser();
	    user.setStatus(status);
   }  	
   //RW transaction
   public void setStatusDate(Date statusDate) {
	    User user = RequestContextHolder.getRequestContext().getUser();
	    user.setStatusDate(statusDate);
   }  

   //RW transaction
   public void setDiskQuota(long megabytes) {
	    User user = RequestContextHolder.getRequestContext().getUser();
	    user.setDiskQuota(megabytes);
   }  

   //RW transaction
   public void resetDiskUsage() {
	   getProfileDao().resetDiskUsage(RequestContextHolder.getRequestContext().getZoneId());
   }  
   
   //RO transaction
   public long getDiskQuota() {
	    User user = RequestContextHolder.getRequestContext().getUser();
	    return user.getDiskQuota();
   } 
   
   public long getMaxUserQuota() {
	   Long userId = RequestContextHolder.getRequestContext().getUserId();
	   return getMaxUserQuota(userId);
   }

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
   public void setUserDiskQuotas(Collection<Long> userIds, long megabytes) {
		//Set each users individual quota
     	for (Long id : userIds) {
			User user = (User)getProfileDao().loadUserDeadOrAlive(id, RequestContextHolder.getRequestContext().getZoneId());
			user.setDiskQuota(megabytes);
     	}
	}
   
   //RW transaction
   public void setGroupDiskQuotas(Collection<Long> groupIds, long megabytes) {
		// iterate through the members of a group - set each members max group quota to the 
	    // maximum value of all the groups they're a member of.
	   Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
	   Collection<GroupPrincipal> groupPrincipals = getProfileDao().loadGroupPrincipals(groupIds, zoneId, false);
		for (GroupPrincipal gp : groupPrincipals) {
			if (gp instanceof Group) {
				Group group = (Group)gp;
				if (group != null && group instanceof Group) {
					group.setDiskQuota(megabytes);
					List gIds = new ArrayList();
					gIds.add(group.getId());
					Set memberIds = getProfileDao().explodeGroups(gIds, zoneId);
					List memberList = getProfileDao().loadUserPrincipals(memberIds, zoneId, false);
					Iterator itUsers = memberList.iterator();
					while (itUsers.hasNext()) {
						Principal member = (Principal) itUsers.next();
						if (member.getEntityType().equals(EntityIdentifier.EntityType.user)) {
							User user = (User)member;
							Set<Long> userGroupIds = getProfileDao().getAllGroupMembership(user.getId(), zoneId);
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
   
   // this returns non-zero quotas (any quota which has been set by the admin)
   public List getNonDefaultQuotas(String type) {
	   return getProfileDao().getNonDefaultQuotas(type,
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

   public boolean isDiskQuotaExceeded() {
	   if (checkDiskQuota() == ObjectKeys.DISKQUOTA_EXCEEDED) return true;
	   else return false;
   }
   
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
   public Group getGroup(String name) {
	  Principal p = getProfileDao().findPrincipalByName(name, RequestContextHolder.getRequestContext().getZoneId());
	  if (!(p instanceof Group)) throw new NoGroupByTheNameException(name);
	  checkReadAccess(p);			
	  return (Group)p;
   }
	//RO transaction
   public Map getGroups() {
	   Map options = new HashMap();
	   options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(DEFAULT_MAX_ENTRIES));
	   return getGroups(options);
   }
 
	//RO transaction
   public Map getGroups(Map options) {
		//does read access check
		ProfileBinder binder = getProfileBinder();
        return loadProcessor(binder).getBinderEntries(binder, groupDocType, options);        
    }
	//RO transaction
	public SortedSet<Group> getGroups(Collection<Long> entryIds) {
		//does read access check
		ProfileBinder binder = getProfileBinder();
	    User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new PrincipalComparator(user.getLocale());
       	TreeSet<Group> result = new TreeSet(c);
       	result.addAll(getProfileDao().loadGroups(entryIds, user.getZoneId()));
		return result;
	}
	
	//RO transaction
	public Principal getEntry(String name) {
		return getProfileDao().findPrincipalByName(name, RequestContextHolder.getRequestContext().getZoneId());
	}
	//RO transaction
	public Principal getEntry(Long principalId) {
        Principal p = getProfileDao().loadPrincipal(principalId, RequestContextHolder.getRequestContext().getZoneId(), false);              
    	checkReadAccess(p);			
        return p;
    }

	public SortedSet<Principal> getPrincipals(Collection<Long> ids) {
		//does read access check
		ProfileBinder binder = getProfileBinder();
 	    User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new PrincipalComparator(user.getLocale());
       	TreeSet<Principal> result = new TreeSet(c);
 		result.addAll(getProfileDao().loadPrincipals(ids, user.getZoneId(), false));
 		return result;
	}
    
    //***********************************************************************************************************	
	public IndexErrors indexEntry(Principal entry) {
        ProfileCoreProcessor processor=loadProcessor((ProfileBinder)entry.getParentBinder());
        return processor.indexEntry(entry);
	}


    //NO transaction
    public void modifyEntry(Long id, InputDataAccessor inputData) 
	throws AccessControlException, WriteFilesException, WriteEntryDataException {
    	modifyEntry(id, inputData, null, null, null, null);
    }
    //NO transaction
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
		   User temp = new User();
		   getDefinitionModule().setDefaultEntryDefinition(temp);
		   defaultUserDef = temp.getEntryDef();
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

	   Group temp = new Group();
	   getDefinitionModule().setDefaultEntryDefinition(temp);
	   Definition defaultGroupDef = temp.getEntryDef();
   	   addEntries(groupList, Group.class, binder, defaultGroupDef, options);  	   
    }
  	//no transaction
    private void deleteEntries(final ProfileBinder binder, final Collection<Principal> entries, final Map options) {
		getTransactionTemplate().execute(new TransactionCallback() {
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
					List addedEntries = processor.syncNewEntries(binder, def, clazz, new ArrayList(newEntries.values()), options, null);
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
 					!ObjectKeys.JOB_PROCESSOR_INTERNALID.equals(entry.getInternalId()))) {
  				List templates = getCoreDao().loadTemplates(entry.getZoneId(), Definition.USER_WORKSPACE_VIEW);

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
  				Definition userDef = getDefinitionModule().addDefaultDefinition(Definition.USER_WORKSPACE_VIEW);
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
   public Folder setUserMiniBlog(User entry, Long folderId) throws AccessControlException {
		Folder miniBlog = null;
		
		entry.setMiniBlogId(folderId);
		if (folderId != null) 
			miniBlog = (Folder)getCoreDao().loadBinder(folderId, entry.getZoneId());
		
		return miniBlog;
   }

   //RW transaction
   public void deleteEntry(Long principalId, Map options) {
	   deleteEntry(principalId, options, false);
   }
   //RW transaction
   public void deleteEntry(Long principalId, Map options, boolean phase1Only) {
        Principal entry = getProfileDao().loadPrincipal(principalId, RequestContextHolder.getRequestContext().getZoneId(), true);
        checkAccess(entry, ProfileOperation.deleteEntry);
       	if (entry.isReserved()) 
    		throw new NotSupportedException("errorcode.principal.reserved", new Object[]{entry.getName()});       	
        Binder binder = entry.getParentBinder();
        ProfileCoreProcessor processor=loadProcessor(binder);
       	processor.deleteEntry(binder, entry, true, options); 
       	boolean delWs = Boolean.FALSE;
       	if (options != null) delWs = (Boolean)options.get(ObjectKeys.INPUT_OPTION_DELETE_USER_WORKSPACE);
       	if (Boolean.TRUE.equals(delWs) && (entry instanceof User)) {
        	//delete workspace
        	User u = (User)entry;
        	Long wsId = u.getWorkspaceId();
        	if (wsId != null) {
        		try {
        			getBinderModule().deleteBinder(wsId, true, options, phase1Only);
        			u.setWorkspaceId(null);       		
        		} catch (Exception ue) {}    
        	}
        }
     }
   // no transaction
	public void deleteEntryFinish() {
		getBinderModule().deleteBinderFinish();
	}
    //RO transaction
    public User getUser(String name) {
 	  Principal p = getProfileDao().findPrincipalByName(name, RequestContextHolder.getRequestContext().getZoneId());
 	  if (!(p instanceof User)) throw new NoUserByTheNameException(name);
 	  return (User)p;
    }
    
    //RO transaction
   public Map getUsers() {
    	Map options = new HashMap();
    	options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(DEFAULT_MAX_ENTRIES));
    	return getUsers(options);
    }
	//RO transaction
    public Map getUsers(Map options) {
		//does read check
		ProfileBinder binder = getProfileBinder();
        return loadProcessor(binder).getBinderEntries(binder, userDocType, options);
        
   }
	//RO transaction 
	public SortedSet<User> getUsers(Collection<Long> entryIds) {
		//does read check
		ProfileBinder profile = getProfileBinder();
        User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new PrincipalComparator(user.getLocale());
       	TreeSet<User> result = new TreeSet(c);
       	result.addAll(getProfileDao().loadUsers(entryIds, profile.getZoneId()));
 		return result;
	}
	  
	//RO transaction
	public SortedSet<User> getUsersFromPrincipals(Collection<Long> principalIds) {
		//does read check
		ProfileBinder profile = getProfileBinder();
		Set ids = getProfileDao().explodeGroups(principalIds, profile.getZoneId());
		return getUsers(ids);
	}

	public User findUserByName(String username)  throws NoUserByTheNameException {
		return getProfileDao().findUserByName(username, RequestContextHolder.getRequestContext().getZoneId());
	}
	
	/**
	 * Find the User with the given ldap guid
	 */
	public User findUserByLdapGuid( String ldapGuid )  throws NoUserByTheNameException
	{
		return getProfileDao().findUserByLdapGuid( ldapGuid, RequestContextHolder.getRequestContext().getZoneId() );
	}// end findUserByLdapGuid()
	
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
		public String getSingleValue(String key) {
			Element result = (Element)source.selectSingleNode("./attribute[@name='" + key + "'] | ./property[@name='" + key + "']");
			if (result == null) return null;
			else return result.getTextTrim();
		}

		public String[] getValues(String key) {
			List<Element> result = source.selectNodes("./attribute[@name='" + key + "'] | ./property[@name='" + key + "']");
			if ((result == null) || result.isEmpty()) return null;
			String [] resultVals = new String[result.size()];
			for (int i=0; i<resultVals.length; ++i) {
				resultVals[i] = result.get(i).getTextTrim();
			}
			return resultVals;
		}

		public Date getDateValue(String key) {
			return DateHelper.getDateFromInput(this, key);
		}

		public Event getEventValue(String key, boolean hasDuration, boolean hasRecurrence)
		{
			return EventHelper.getEventFromMap(this, key, hasDuration, hasRecurrence);
		}

		public Survey getSurveyValue(String key)
		{
			return new Survey(key);
		}
		
		public boolean exists(String key) {
			Element result = (Element)source.selectSingleNode("./attribute[@name='" + key + "'] | ./property[@name='" + key + "']");
			if (result == null) return false;
			return true;
		}

		public Object getSingleObject(String key) {
			return (Element)source.selectSingleNode("./attribute[@name='" + key + "'] | ./property[@name='" + key + "']");
		}
		public int getCount() {
			return source.nodeCount();
		}

		public void setFieldsOnly(Boolean fieldsOnly) {
			this.fieldsOnly = fieldsOnly;
		}
		public boolean isFieldsOnly() {
			return this.fieldsOnly;
		}
		public Set<String> keySetForPotentialStringValues() {
			Set<String> set = new HashSet<String>();
			List<Element> result = source.selectNodes("./attribute");
			if(result != null) {
				for(Element elem:result) {
					String nameValue = elem.attributeValue("name");
					if(nameValue != null)
						set.add(nameValue);
				}
			}
			result = source.selectNodes("./property");
			if(result != null) {
				for(Element elem:result) {
					String nameValue = elem.attributeValue("name");
					if(nameValue != null)
						set.add(nameValue);
				}
			}
			return set;
		}
	}
	
    //NO transaction
	public User addUserFromPortal(String userName, String password, Map updates, Map options) {
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
			User user = (User)processor.syncNewEntries(profiles, userDef, User.class, accessors, options, null).get(0);
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
	public void modifyUserFromPortal(User user, Map updates, Map options) {
		if (updates == null)
			return; // nothing to update with
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
	protected class SharedSeenMap extends SeenMap {
		public SharedSeenMap(Long principalId) {
			super(principalId);
		}
	    public void setSeen(Entry entry) {	    	
	    }
	    public void setSeen(FolderEntry entry) {
	    }
	    public boolean checkIfSeen(FolderEntry entry) {
	    	return true;
	    }
		protected boolean checkAndSetSeen(FolderEntry entry, boolean setIt) {
			return true;
		}
		public boolean checkAndSetSeen(Map entry, boolean setIt) {
			return true;
		}	
	    public boolean checkIfSeen(Map entry) {
	    	return true;
	    }   
	    
		public boolean checkAndSetSeen(Long id, Date modDate, boolean setIt) {
			return true;
		}
    }

    //RW transaction
	public void deleteUserByName(String userName,  Map options) {
		try {
			User user = getProfileDao().findUserByName(userName, 
					RequestContextHolder.getRequestContext().getZoneName());
			deleteEntry(user.getId(),options);
		}
		catch(NoUserByTheNameException thisIsOk) {}
	}
	
	//NO transaction
	public User addUser(String definitionId, InputDataAccessor inputData, Map fileItems, Map options) 
	throws AccessControlException, WriteFilesException, WriteEntryDataException, PasswordMismatchException {
		if (inputData.getSingleValue("password") == null || inputData.getSingleValue("password").equals(""))
			throw new PasswordMismatchException("errorcode.password.cannotBeNull");
        ProfileBinder binder = loadProfileBinder();
        checkAccess(binder, ProfileOperation.addEntry);
		return (User) addIndividualPrincipal(definitionId, inputData, fileItems, options, User.class);
	}
	//NO transaction
	public Application addApplication(String definitionId, 
			InputDataAccessor inputData, Map fileItems, Map options) 
	throws AccessControlException, WriteFilesException, WriteEntryDataException {
        ProfileBinder binder = loadProfileBinder();
        checkAccess(binder, ProfileOperation.manageEntries);
    	return (Application) addIndividualPrincipal(definitionId, inputData, fileItems, options, Application.class);
	}
    //NO transaction
    public Group addGroup(String definitionId, InputDataAccessor inputData, Map fileItems, Map options) 
    	throws AccessControlException, WriteFilesException, WriteEntryDataException {
        ProfileBinder binder = loadProfileBinder();
        checkAccess(binder, ProfileOperation.manageEntries);
    	return (Group) addGroupPrincipal(definitionId, inputData, fileItems, options, Group.class);
    }
	
    //NO transaction
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
            }
            return newEntry;
        } catch (DataIntegrityViolationException de) {
        	if(clazz.equals(User.class))
        		throw new ObjectExistsException("errorcode.user.exists", (Object[])null, de);
        	else
            	throw new ObjectExistsException("errorcode.application.exists", (Object[])null, de);
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
        	return loadProcessor(binder).addEntry(binder, definition, clazz, inputData, fileItems, options);
        } catch (DataIntegrityViolationException de) {
        	if(clazz.equals(Group.class))
        		throw new ObjectExistsException("errorcode.group.exists", (Object[])null, de);
        	else
        		throw new ObjectExistsException("errorcode.applicationgroup.exists", (Object[])null, de);        		
        }
	}
    //RO transaction
    public ApplicationGroup getApplicationGroup(String name) {
 	  Principal p = getProfileDao().findPrincipalByName(name, RequestContextHolder.getRequestContext().getZoneId());
 	  if (!(p instanceof ApplicationGroup)) throw new NoGroupByTheNameException(name);
 	  return (ApplicationGroup)p;
    }

	//RO transaction
	public Map getApplicationGroups() throws AccessControlException {
		   Map options = new HashMap();
		   options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(DEFAULT_MAX_ENTRIES));
		   return getApplicationGroups(options);
	}
	
	//RO transaction
	public Map getApplicationGroups(Map searchOptions) throws AccessControlException {
		//does read access check
		ProfileBinder binder = getProfileBinder();
        return loadProcessor(binder).getBinderEntries(binder, applicationGroupDocType, searchOptions);        
	}
	
	//RO transaction
	public SortedSet<ApplicationGroup> getApplicationGroups(Collection<Long> groupIds) throws AccessControlException {
		//does read access check
		ProfileBinder binder = getProfileBinder();
	    User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new PrincipalComparator(user.getLocale());
       	TreeSet<ApplicationGroup> result = new TreeSet(c);
       	result.addAll(getProfileDao().loadApplicationGroups(groupIds, user.getZoneId()));
		return result;	
	}
	
	//RO transaction
	public Map getGroupPrincipals() throws AccessControlException {
		   Map options = new HashMap();
		   options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(DEFAULT_MAX_ENTRIES));
		   return getGroupPrincipals( options);	
	}
	
	//RO transaction
	public Map getGroupPrincipals(Map searchOptions) throws AccessControlException {
		//does read access check
		ProfileBinder binder = getProfileBinder();
        return loadProcessor(binder).getBinderEntries(binder, groupPrincipalDocType, searchOptions);        
	}
	
	//RO transaction
	public SortedSet<GroupPrincipal> getGroupPrincipals(Collection<Long> groupIds) throws AccessControlException {
		//does read access check
		ProfileBinder binder = getProfileBinder();
	    User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new PrincipalComparator(user.getLocale());
       	TreeSet<GroupPrincipal> result = new TreeSet(c);
       	result.addAll(getProfileDao().loadGroupPrincipals(groupIds, user.getZoneId(), true));
		return result;	
	}
	
    //RO transaction
    public Application getApplication(String name) {
 	  Principal p = getProfileDao().findPrincipalByName(name, RequestContextHolder.getRequestContext().getZoneId());
 	  if (!(p instanceof Application)) throw new NoApplicationByTheNameException(name);
 	  return (Application)p;
    }
	//RO transaction
	public Map getApplications() {
    	Map options = new HashMap();
    	options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(DEFAULT_MAX_ENTRIES));
    	return getApplications( options);
	}
	//RO transaction
	public Map getApplications(Map searchOptions) {
		Map result = new HashMap();
		//does read access check
		try {
			ProfileBinder binder = getProfileBinder();
	        result = loadProcessor(binder).getBinderEntries(binder, applicationDocType, searchOptions);
		} catch(AccessControlException e) {}
		return result;
	}
	//RO transaction
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
	public Map getIndividualPrincipals() {
		   Map options = new HashMap();
		   options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(DEFAULT_MAX_ENTRIES));
		   return getIndividualPrincipals(options);	
	}
	//RO transaction
	public Map getIndividualPrincipals( Map searchOptions) {
		//does read access check
		ProfileBinder binder = getProfileBinder();
        return loadProcessor(binder).getBinderEntries(binder, individualPrincipalDocType, searchOptions);        
	}
	//RO transaction
	public SortedSet<IndividualPrincipal> getIndividualPrincipals(Collection<Long> individualIds) {
		//does read access check
		ProfileBinder binder = getProfileBinder();
	    User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new PrincipalComparator(user.getLocale());
       	TreeSet<IndividualPrincipal> result = new TreeSet(c);
       	result.addAll(getProfileDao().loadIndividualPrincipals(individualIds, user.getZoneId(), true));
		return result;	
	}
	//RO transaction
	public Map getPrincipals( Map searchOptions) {
		//does read access check
		ProfileBinder binder = getProfileBinder();
        return loadProcessor(binder).getBinderEntries(binder, allPrincipalDocType, searchOptions);        
	}
    //RO transaction
    public List<SharedEntity> getShares(Long userId, Date after) {
	    User user = getUser(userId, false);
	    //get list of all groups user is a member of.
	    Set<Long> accessIds = getProfileDao().getPrincipalIds(user);
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

  public void changePassword(Long userId, String oldPassword, String newPassword) {
	  if(newPassword == null || newPassword.equals(""))
		  throw new PasswordMismatchException("errorcode.password.cannotBeNull");
	  
	  User user = getUser(userId, true);
	  ProfileBinder profileBinder = loadProfileBinder();
	  
      if (!testAccess(profileBinder, ProfileOperation.manageEntries)) {
    	  // The user making the call does not have the right to manage profile entries. 
    	  // In this case, we require that the old password be specified. 
    	  // Note: This code needs to be kept in synch with the similar check in ModifyEntryController.java.
    	  if(oldPassword == null || oldPassword.equals("") || !EncryptUtil.encryptPasswordForMatching(oldPassword, user).equals(user.getPassword()))
    		  // The user didn't enter the old/current password or they entered it incorrectly.
    		  throw new PasswordMismatchException("errorcode.password.invalid");
      }
      
      user.setPassword(newPassword);
  }
  
  //RO transaction
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
  public static void main(String[] args) throws Exception {
	  String xmlStr = "<mydata><elem1 attr1=\"hello\"><attribute name=\"A1\"/><property name=\"P1\"/></elem1><attribute name=\"A2\"/><attribute name=\"A3\"/><property name=\"P2\"/><elem2 attr2=\"hi\"><attribute name=\"A4\"/><property name=\"P3\"/></elem2></mydata>";
	  
	   SAXReader reader = new SAXReader();
       Document document = reader.read(new StringReader(xmlStr));
       Element elem = document.getRootElement();
       ElementInputData eid = new ElementInputData(elem);
       Set<String> set = eid.keySetForPotentialStringValues();
       for(String key:set) {
    	   System.out.println(key);
       }
  }
  
}

