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
package org.kablink.teaming.security.impl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.InternalException;
import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Application;
import org.kablink.teaming.domain.AuthenticationConfig;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.fi.connection.acl.AclResourceDriver;
import org.kablink.teaming.license.LicenseManager;
import org.kablink.teaming.module.authentication.AuthenticationModule;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.accesstoken.AccessToken;
import org.kablink.teaming.security.function.FunctionManager;
import org.kablink.teaming.security.function.OperationAccessControlException;
import org.kablink.teaming.security.function.OperationAccessControlExceptionNoName;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.kablink.teaming.security.function.WorkAreaFunctionMembershipManager;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.util.search.Constants;
import org.springframework.beans.factory.InitializingBean;

/**
 *
 * @author Jong Kim
 */
public class AccessControlManagerImpl implements AccessControlManager, InitializingBean {
    
	private Log logger = LogFactory.getLog(getClass());
	
    private FunctionManager functionManager;
    private WorkAreaFunctionMembershipManager workAreaFunctionMembershipManager;
    private CoreDao coreDao;
    private ProfileDao profileDao;
    private LicenseManager licenseManager;
    private AuthenticationModule authenticationModule;
    private Map synchAgentRights;
    private Map synchAgentTokenBoostRights;
    private Map fileSyncAgentRights;
    
	public void afterPropertiesSet() throws Exception {
		synchAgentRights = new HashMap();
		String[] strs = SPropsUtil.getStringArray("synchronization.agent.rights", ",");
		if(strs != null) {
			for(String str:strs)
				synchAgentRights.put(str, str);
		}
		synchAgentTokenBoostRights = new HashMap();
		String[] strs2 = SPropsUtil.getStringArray("synchronization.agent.token.boost.rights", ",");
		if(strs2 != null) {
			for(String str:strs2)
				synchAgentTokenBoostRights.put(str, str);
		}
		fileSyncAgentRights = new HashMap();
		String[] strs3 = SPropsUtil.getStringArray("file.sync.agent.rights", ",");
		if(strs3 != null) {
			for(String str:strs3)
				fileSyncAgentRights.put(str, str);
		}
	}
	
    public FunctionManager getFunctionManager() {
        return functionManager;
    }
    public void setFunctionManager(FunctionManager functionManager) {
        this.functionManager = functionManager;
    }
    public WorkAreaFunctionMembershipManager getWorkAreaFunctionMembershipManager() {
        return workAreaFunctionMembershipManager;
    }
    public void setWorkAreaFunctionMembershipManager(
            WorkAreaFunctionMembershipManager workAreaFunctionMembershipManager) {
        this.workAreaFunctionMembershipManager = workAreaFunctionMembershipManager;
    }
	public void setProfileDao(ProfileDao profileDao) {
		this.profileDao = profileDao;
	}
	protected ProfileDao getProfileDao() {
		return profileDao;
	}
	protected CoreDao getCoreDao() {
		return coreDao;
	}
	public void setCoreDao(CoreDao coreDao) {
		this.coreDao = coreDao;
	}
	public void setLicenseManager(LicenseManager licenseManager) {
		this.licenseManager = licenseManager;
	}
	protected LicenseManager getLicenseManager() {
		return licenseManager;
	}
	public void setAuthenticationModule(AuthenticationModule authenticationModule) {
		this.authenticationModule = authenticationModule;
	}
	protected AuthenticationModule getAuthenticationModule() {
		authenticationModule = (AuthenticationModule) SpringContextUtil.getBean("authenticationModule");
		return authenticationModule;
	}
    public Set getWorkAreaAccessControl(WorkArea workArea, WorkAreaOperation workAreaOperation) {
         if(workArea.isFunctionMembershipInherited()) {
            WorkArea parentWorkArea = workArea.getParentWorkArea();
            if(parentWorkArea == null)
                return new HashSet();  //possible for templates
            else
                return getWorkAreaAccessControl(parentWorkArea, workAreaOperation);
        }
        else {
	        Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
        	List<WorkAreaFunctionMembership>wfms = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(zoneId, workArea, workAreaOperation);
          	Set ids = new HashSet();
            for (WorkAreaFunctionMembership wfm:wfms) {
            	ids.addAll(wfm.getMemberIds());
        	}
	        return ids;
	        
        }    	
    }
    
    public boolean testOperation(WorkArea workArea, WorkAreaOperation workAreaOperation) 
    	throws AccessControlException {
        return testOperation
        	(RequestContextHolder.getRequestContext().getUser(), 
        	        workArea, workAreaOperation);
    }
	
	public boolean testOperation(User user,
			WorkArea workArea, WorkAreaOperation workAreaOperation) {
		long begin = System.nanoTime();
		//See if this is a net folder
		if (workArea.isAclExternallyControlled() && !workAreaOperation.equals(WorkAreaOperation.ALLOW_ACCESS_NET_FOLDER)) {
			//We must also check that the user has the right to access this net folder
			Binder topFolder = null;
			if (workArea instanceof FolderEntry) {
				topFolder = ((FolderEntry)workArea).getParentBinder();
			} else if (workArea instanceof Folder) {
				topFolder = (Folder)workArea;
			}
			while (topFolder != null) {
				if (topFolder.getParentBinder() != null &&
						!topFolder.getParentBinder().getEntityType().name().equals(EntityType.folder.name())) {
					//We have found the top folder (i.e., the net folder root)
					break;
				}
				//Go up a level
				topFolder = topFolder.getParentBinder();
			}
			//See if the top folder is inheriting from its parent
			while (topFolder != null && topFolder.isFunctionMembershipInherited()) {
				topFolder = topFolder.getParentBinder();
			}
			//Now check if the root folder allows access
			if (topFolder != null) {
				if (!testOperation(user, topFolder, WorkAreaOperation.ALLOW_ACCESS_NET_FOLDER)) {
					return false;
				}
			} else {
				return false;
			}
		}
		
		boolean result = testOperationRecursive(user, workArea, workArea, workAreaOperation);

		if(logger.isDebugEnabled()) {
			double diff = (System.nanoTime() - begin)/1000000.0; // millisecond
			logger.debug("testOperation: result=" + result + 
					" operation=" + workAreaOperation.getName() +
					" time=" + diff + 
					" user=" + user.getName() +
					" wa-type=" + workArea.getClass().getSimpleName() +
					" wa-id=" + workArea.getWorkAreaId());
		}
		
		return result;
	}
	
	private boolean applicationPlaysNoRoleInAccessControl(Application application) {
		return (application == null || application.isTrusted());
	}
	
	private boolean userAccessGrantedViaSpecialMeans(User user, WorkAreaOperation workAreaOperation) {
		return (user.isSuper() || 
				isDirectSynchronizationWork(user, workAreaOperation) ||
				isIndirectSynchronizationWork(workAreaOperation) ||
				isFileSyncWork(user, workAreaOperation));
	}
	
	//pass the original ownerId in.  Recursive calls need the original
	private boolean testOperationRecursive(User user, WorkArea workAreaStart, WorkArea workArea, WorkAreaOperation workAreaOperation) {
		if(isAccessCheckTemporarilyDisabled())
			return true;
		
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		Application application = null;
		if(RequestContextHolder.getRequestContext() != null)
			application = RequestContextHolder.getRequestContext().getApplication();		
		
		if(userAccessGrantedViaSpecialMeans(user, workAreaOperation) &&
				applicationPlaysNoRoleInAccessControl(application)) {
			return true;
		}
		if (user.isDisabled() || user.isDeleted()) {
			//Whatever the operation, deny it if the user account is disabled or deleted
			return false;
		}
		if (user.isShared()) {
			//This is the "guest" account. Make sure guest access is enabled
			AuthenticationConfig config = getAuthenticationModule().getAuthenticationConfigForZone(zoneId);
			if (!config.isAllowAnonymousAccess()) {
				//Guest access is not enabled, disallow access to everything
				return false;
			}
			//See if the user is only allowed "read only" rights
			if (config.isAnonymousReadOnly()) {
				//This is the guest account and it is read only. Only allow checks for read rights
				if (!WorkAreaOperation.READ_ENTRIES.equals(workAreaOperation) &&
						!WorkAreaOperation.VIEW_BINDER_TITLE.equals(workAreaOperation)) {
					//Rights other than "read" rights are not permitted by read only guests
					return false;
				}
			}
		}
		if (!workAreaOperation.equals(WorkAreaOperation.READ_ENTRIES) && 
				!workAreaOperation.equals(WorkAreaOperation.VIEW_BINDER_TITLE) && 
				!getLicenseManager().validLicense())return false;
		if (workAreaOperation.equals(WorkAreaOperation.VIEW_BINDER_TITLE)) {
			if (!SPropsUtil.getBoolean("accessControl.viewBinderTitle.enabled", false)) {
				return false;
			}
		}
		boolean isExternalAclControlledOperation = isExternalAclControlledOperation(workArea, workAreaOperation);
		if ((!isExternalAclControlledOperation && workArea.isFunctionMembershipInherited()) || 
				(isExternalAclControlledOperation && workArea.isExtFunctionMembershipInherited())) {
			WorkArea parentWorkArea = workArea.getParentWorkArea();
			if (parentWorkArea == null) {
				throw new InternalException(
						"Cannot inherit function membership when it has no parent");
			} else {
				// use the original workArea owner
				if (testOperationRecursive(user, workAreaStart, parentWorkArea, workAreaOperation)) {
					return true;
				} else {
					return testRightGrantedBySharing(user, workAreaStart, workArea, workAreaOperation, null);
				}
			}
		} else {
			Set<Long> applicationMembersToLookup = null;
			if(application != null && !application.isTrusted()) {
				applicationMembersToLookup = getProfileDao().getApplicationLevelPrincipalIds(application);
				// First, test against the zone-wide maximum set by the admin
				if(!checkWorkAreaFunctionMembership(user.getZoneId(),
								getCoreDao().loadZoneConfig(user.getZoneId()),
								workAreaOperation,
								applicationMembersToLookup)) {
					return false;
				}
				// First test passed. Now test against the specified work area.
				if(!checkWorkAreaFunctionMembership(user.getZoneId(),
								workArea, 
								workAreaOperation, 
								applicationMembersToLookup)) {
					return false;
				}
			}
			Set<Long> userApplicationLevelMembersToLookup = getProfileDao().getApplicationLevelPrincipalIds(user);
			Long allUsersId = Utils.getAllUsersGroupId();
			Long allExtUsersId = Utils.getAllExtUsersGroupId();
			if (allUsersId != null && !workArea.getWorkAreaType().equals(ZoneConfig.WORKAREA_TYPE) 
					&& userApplicationLevelMembersToLookup.contains(allUsersId) && 
					Utils.canUserOnlySeeCommonGroupMembers(user)) {
				if (Utils.isWorkareaInProfilesTree(workAreaStart) && !user.getId().equals(workAreaStart.getOwnerId())) {
					//If this user does not share a group with the binder owner, remove the "All Users" group.
					boolean remove = true;
					if (workArea.getWorkAreaType().equals(EntityType.workspace.name()) ||
							workArea.getWorkAreaType().equals(EntityType.folder.name())) {
						List<Group> groups = workArea.getOwner().getMemberOf();
						for (Group g : groups) {
							//See if this group is not the allExtUsers group and is shared with the user
							//Being in the allExtUsers group does not count as a "common" group
							if (!g.getId().equals(allExtUsersId) && userApplicationLevelMembersToLookup.contains(g.getId())) {
								remove = false;
								break;
							}
						}
						if (remove) {
							//There wasn't a direct match of groups, go look in the exploded list
							Set<Long> userGroupIds = getProfileDao().getApplicationLevelGroupMembership(workArea.getOwner().getId(), zoneId);
							for (Long gId : userGroupIds) {
								if (!gId.equals(allExtUsersId) && userApplicationLevelMembersToLookup.contains(gId)) {
									remove = false;
									break;
								}
							}
						}
					}
					if (remove) {
						//The user is only allowed to see users in a common group, and the user does not share a common group.
						//So, we remove the All Users and All Ext Users groups to force using just the real groups and users in the ACL check
						userApplicationLevelMembersToLookup.remove(allUsersId);
						userApplicationLevelMembersToLookup.remove(allExtUsersId);
					}
				}
			}
			//if current user is the workArea owner, add special Id to is membership
			if (user.getId().equals(workAreaStart.getOwnerId())) userApplicationLevelMembersToLookup.add(ObjectKeys.OWNER_USER_ID);
			Set<Long> teamMembers = null;
			if (workAreaStart instanceof FolderEntry) {
				teamMembers = ((FolderEntry)workAreaStart).getParentBinder().getTeamMemberIds();
			} else {
				teamMembers = workAreaStart.getTeamMemberIds();
			}
			if (teamMembers != null && !Collections.disjoint(teamMembers, userApplicationLevelMembersToLookup)) {
				userApplicationLevelMembersToLookup.add(ObjectKeys.TEAM_MEMBER_ID);
			}
			// Take container groups into consideration
			List<Long> containerGroupsToLookup = getProfileDao().getMemberOfLdapContainerGroupIds(user.getId(), zoneId);
			Set<Long> userAllMembersToLookup = null;
			if(containerGroupsToLookup.isEmpty()) {
				userAllMembersToLookup = userApplicationLevelMembersToLookup;
			}
			else {	
				userAllMembersToLookup = new HashSet<Long>(userApplicationLevelMembersToLookup);
				userAllMembersToLookup.addAll(containerGroupsToLookup);
			}
			// Regular ACL checking must take container groups into consideration. 
			// However, sharing-granted ACL checking must not because sharing can never take
			// place against a container group.
			if(checkWorkAreaFunctionMembership(user.getZoneId(),
							workArea, workAreaOperation, userAllMembersToLookup)) {
				return true;
			}
			else {
				return testRightGrantedBySharing(user, workAreaStart, workArea, workAreaOperation, userApplicationLevelMembersToLookup);
			}
		}
	}
	
    public void checkOperation(WorkArea workArea, 
            WorkAreaOperation workAreaOperation) throws AccessControlException {
        checkOperation(RequestContextHolder.getRequestContext().getUser(),
                workArea, workAreaOperation);
    }

	public void checkOperation(User user, WorkArea workArea, 
			WorkAreaOperation workAreaOperation) 
    	throws AccessControlException {
        if (!testOperation(user, workArea, workAreaOperation)) {
        	if (workArea instanceof Entry && ((Entry)workArea).hasEntryAcl() && ((Entry)workArea).isIncludeFolderAcl()) {
        		//See if the parent or the entry is allowing access
        		if (testOperation(user, ((Entry)workArea).getParentBinder(), workAreaOperation)) {
        			return;
        		}
        	}
        	// Are we dealing with the Guest user?
        	if ( ObjectKeys.GUEST_USER_INTERNALID.equals( user.getInternalId() ) )
        	{
        		Object[]	errorArgs;
        		
        		// Yes
        		// Throw an exception that indicates the user is not logged in.
        		errorArgs = new Object[] { user.getName(), workAreaOperation.toString(), workArea.toString() };
        		throw new AccessControlException( "errorcode.operation.denied.sessionTimeout", errorArgs );
        	}
        	
        	//Make sure the user is allowed to see the workarea at all
        	if (WorkAreaOperation.READ_ENTRIES.equals(workAreaOperation) || 
        			WorkAreaOperation.VIEW_BINDER_TITLE.equals(workAreaOperation)) {
           		//This user shouldn't see anything about this workarea
        		throw new OperationAccessControlExceptionNoName(user.getName(), 
            			workAreaOperation.toString());
        	}
        	if (testOperation(user, workArea, WorkAreaOperation.READ_ENTRIES) ||
        			testOperation(user, workArea, WorkAreaOperation.VIEW_BINDER_TITLE)) {
        		throw new OperationAccessControlException(user.getName(), 
        			workAreaOperation.toString(), workArea.toString());
        	} else {
        		//This user shouldn't see anything about this workarea
        		throw new OperationAccessControlExceptionNoName(user.getName(), 
            			workAreaOperation.toString());
        	}
        }
    }
	
	private static final ThreadLocal temporarilyDisableAccessCheckForThisThreadTL = new ThreadLocal();
	public static void temporarilyDisableAccessCheckForThisThread() {
		temporarilyDisableAccessCheckForThisThreadTL.set(Boolean.TRUE);
	}
	public static void bringAccessCheckBackToNormalForThisThread() {
		temporarilyDisableAccessCheckForThisThreadTL.set(null);	
	}
	public static boolean isAccessCheckTemporarilyDisabled() {
		Boolean b = (Boolean) temporarilyDisableAccessCheckForThisThreadTL.get();
		return (b != null && b.equals(Boolean.TRUE));
	}
	private boolean isDirectSynchronizationWork(User user, WorkAreaOperation workAreaOperation) {
		return ObjectKeys.SYNCHRONIZATION_AGENT_INTERNALID.equals(user.getInternalId()) &&
			(synchAgentRights.get(workAreaOperation.getName()) != null);
	}
	private boolean isIndirectSynchronizationWork(WorkAreaOperation workAreaOperation) {
		AccessToken accessToken = RequestContextHolder.getRequestContext().getAccessToken();
		User requester = null;
		if(accessToken != null) {
			Long requesterId = accessToken.getRequesterId();
			if(requesterId != null) {
				try {
					// Don't use regular loadUser() method here. If you do, you will get infinite loop.
					requester = getProfileDao().loadUserDeadOrAlive(requesterId, RequestContextHolder.getRequestContext().getZoneId());
				}
				catch(Exception e) {
					// This means either the requester is no longer in the system or something else is wrong.
					// Either way, the test fails.
					return false;
				}
			}
		}
		if(requester != null)
			return ObjectKeys.SYNCHRONIZATION_AGENT_INTERNALID.equals(requester.getInternalId()) &&
				(synchAgentTokenBoostRights.get(workAreaOperation.getName()) != null);
		else
			return false;
	}
	
	private boolean isFileSyncWork(User user, WorkAreaOperation workAreaOperation) {
		return ObjectKeys.FILE_SYNC_AGENT_INTERNALID.equals(user.getInternalId()) &&
			(fileSyncAgentRights.get(workAreaOperation.getName()) != null);
	}

    private boolean checkWorkAreaFunctionMembership(Long zoneId, WorkArea workArea, 
            WorkAreaOperation workAreaOperation, Set membersToLookup) {
    	List<WorkAreaOperation> increaseByRights = RequestContextHolder.getRequestContext().getIncreaseByRights();
    	List<WorkAreaOperation> decreaseByRights = RequestContextHolder.getRequestContext().getDecreaseByRights();
    	
    	if(increaseByRights != null && increaseByRights.contains(workAreaOperation))
    		return true; // granted due to temporary boost
    	
    	if(decreaseByRights != null && decreaseByRights.contains(workAreaOperation))
    		return false; // denied due to temporary setback
    	
    	// normal check
    	return getWorkAreaFunctionMembershipManager().checkWorkAreaFunctionMembership
    	(zoneId, workArea, workAreaOperation, membersToLookup);
    }

    @Override
    public boolean testRightGrantedBySharing(User user, WorkArea workArea, WorkAreaOperation workAreaOperation) {
    	return testRightGrantedBySharing(user, workArea, workAreaOperation, null);
    }

    private boolean testRightGrantedBySharing(User user, WorkArea workAreaStart, WorkArea workArea, WorkAreaOperation workAreaOperation, Set<Long> userMembers) {
    	// Unlike regular ACL checking, share right checking is not implemented using recursive invocation.
    	if(workAreaStart != workArea) {
    		//Check for this being a reply. We allow recursion of replies up to the parent entry.
    		if (!(workAreaStart instanceof FolderEntry) || ((FolderEntry)workAreaStart).getTopEntry() != workArea) {
    			//This is not a reply to an entry, so recursion is not being used
    			return false;
    		}
    	}
    	
    	return testRightGrantedBySharing(user, workArea, workAreaOperation, userMembers);
    }
    
    private boolean isExternalAclControlledOperation(WorkArea workArea, WorkAreaOperation workAreaOperation) {
		boolean isExternalAclControlledOperation = false;
		if (workArea.isAclExternallyControlled()) {
			//This is a workarea with external ACLs
			List<WorkAreaOperation> ardWaos = workArea.getExternallyControlledRights();
			if (ardWaos.contains(workAreaOperation)) {
				//This right is controlled externally
				isExternalAclControlledOperation = true;
			}
		}
		return isExternalAclControlledOperation;
    }
    
    private boolean testRightGrantedBySharing(User user, WorkArea workArea, WorkAreaOperation workAreaOperation, Set<Long> userMembers) {
    	// Share-granted access rights can be defined only on DefinableEntity
    	if(!(workArea instanceof DefinableEntity))
    		return false;
    	
    	// Whether a sharing on a folder should apply recursively on the member entries and sub-folders or not
    	// is controlled strictly by the regular Vibe-side inheritance setting. The inheritance setting associated
    	// with external ACLs has NO effect on the scope of sharing. In other word, it doesn't matter whether
    	// the entity inherits its external ACLs from their parents or not.
		List<EntityIdentifier> chain = new ArrayList<EntityIdentifier>();
    	chain.add(((DefinableEntity) workArea).getEntityIdentifier());
    	if(workArea instanceof FolderEntry) {
			FolderEntry entry = (FolderEntry) workArea;
			if(!entry.hasEntryAcl() || (entry.hasEntryAcl() && entry.isIncludeFolderAcl())) {
				// This entry inherits the parent's ACLs.
				chain.add(entry.getParentFolder().getEntityIdentifier());
				workArea = entry.getParentFolder();
			}
    	}
    	while(workArea.isFunctionMembershipInherited()) {
    		workArea = workArea.getParentWorkArea();
    		if(workArea instanceof DefinableEntity)
    			chain.add(((DefinableEntity)workArea).getEntityIdentifier());
    	}
    	Map<ShareItem.RecipientType, Set<Long>> shareMembers = getProfileDao().getRecipientIdsWithGrantedRightToSharedEntities(chain, workAreaOperation.getName());
    	
    	// Check if at least one entity in the ACL inheritance parentage chain grants the specified access to the user directly.
    	if(shareMembers.get(ShareItem.RecipientType.user).contains(user.getId()))
    		return true;
    	
    	// Check if at least one entity in the ACL inheritance parentage chain grants the specified access to the user through group membership.
    	if(userMembers == null)
    		userMembers = getProfileDao().getApplicationLevelPrincipalIds(user);
    	if(!Collections.disjoint(shareMembers.get(ShareItem.RecipientType.group), userMembers))
    		return true;
    	
    	// Check if at least one entity in the ACL inheritance parentage chain grants the specified access to the user through team membership.
    	if(SPropsUtil.getBoolean("share.based.access.check.use.search.index.for.team.membership", false)) {
    		// Note: This implementation is used for testing/comparison purpose only.
    		List<Map> myTeams = getBinderModule().getTeamMemberships(user.getId());
    		Set<Long> teamBinderIds = new HashSet<Long>();
    		for(Map binder : myTeams) {
    			try {
    				teamBinderIds.add(Long.valueOf((String)binder.get(Constants.DOCID_FIELD)));
    			} catch (Exception ignore) {};
    		}
        	return (!Collections.disjoint(shareMembers.get(ShareItem.RecipientType.team), teamBinderIds));
    	}
    	else {
    		// Note: This implementation is used in production system.
    		Set<Long> teamBinderIds = shareMembers.get(ShareItem.RecipientType.team);
    		Binder binder;
    		Set<Long> teamMemberIds;
    		for(Long teamBinderId:teamBinderIds) {
    			binder = getBinderModule().getBinder(teamBinderId);
    			teamMemberIds = binder.getTeamMemberIds();
    	    	if(!Collections.disjoint(teamMemberIds, userMembers))
    	    		return true;
    		}
    		return false;
    	}
    }
    
    private BinderModule getBinderModule() {
    	return (BinderModule) SpringContextUtil.getBean("binderModule");
    }
}
