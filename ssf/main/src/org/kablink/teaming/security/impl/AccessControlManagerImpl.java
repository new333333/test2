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
import org.kablink.teaming.ObjectKeys;
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
import org.kablink.teaming.license.LicenseManager;
import org.kablink.teaming.module.authentication.AuthenticationModule;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.module.shared.SearchUtils;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.accesstoken.AccessToken;
import org.kablink.teaming.security.function.Function;
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
	
	public boolean testOperation(User user, WorkArea workArea, WorkAreaOperation workAreaOperation) {
		return testOperation(user, workArea, workAreaOperation, true);
	}
	
	public boolean testOperation(User user, WorkArea workArea, WorkAreaOperation workAreaOperation, boolean checkSharing) {
		long begin = System.nanoTime();
		
		boolean result = testOperationRecursive(user, workArea, workArea, workAreaOperation, checkSharing);

		if(logger.isDebugEnabled()) {
			double diff = (System.nanoTime() - begin)/1000000.0; // millisecond
			logger.debug("testOperation: result=" + result + 
					" user=" + user.getName() +
					" operation=" + workAreaOperation.getName() +
					" wa-type=" + workArea.getWorkAreaType() +
					" wa-id=" + workArea.getWorkAreaId() + 
					" checkSharing=" + checkSharing +
					" time=" + diff); 
		}
		
		return result;
	}
	
	private boolean checkRootFolderAccess(User user, WorkArea workArea, WorkAreaOperation workAreaOperation) {
		//See if this is a net folder
		if (workArea.isAclExternallyControlled() && !WorkAreaOperation.ALLOW_ACCESS_NET_FOLDER.equals(workAreaOperation)) {
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
		return true;
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
	private boolean testOperationRecursive(User user, WorkArea workAreaStart, WorkArea workArea, 
			WorkAreaOperation workAreaOperation, boolean checkSharing) {
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
						!WorkAreaOperation.VIEW_BINDER_TITLE.equals(workAreaOperation) &&
						!WorkAreaOperation.ALLOW_ACCESS_NET_FOLDER.equals(workAreaOperation)) {
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
		if ((!isExternalAclControlledOperation && 
					(workArea.isFunctionMembershipInherited() || 
					(workArea instanceof FolderEntry && !((FolderEntry)workArea).hasEntryAcl()))) || 
				(isExternalAclControlledOperation && workArea.isExtFunctionMembershipInherited())) {
			WorkArea parentWorkArea = workArea.getParentWorkArea();
			if (workArea instanceof FolderEntry) {
				//For folder entries, get the parent folder instead of the top entry
				parentWorkArea = ((FolderEntry)workArea).getParentBinder();
			}
			if (parentWorkArea == null) {
				throw new InternalException(
						"Cannot inherit function membership when it has no parent");
			} else {
				// use the original workArea owner
				if (testOperationRecursive(user, workAreaStart, parentWorkArea, workAreaOperation, checkSharing)) {
					// (20150423:DRF:Bugzilla 928059)
					//    Added the following if statement:
					//    Are we checking for a sharing right to
					//    something the user doesn't have access too
					//    outside of sharing?
					if (checkSharing &&
							WorkAreaOperation.isAllowSharingRight(workAreaOperation) &&
							(!(testOperationRecursive(user, workAreaStart, workAreaStart, WorkAreaOperation.READ_ENTRIES, false)))) {	// false -> We don't want to check sharing for whether the user has access to the item.
						// Yes!  Then all we need to do is check
						// whether that right was granted by the
						// share.
						return testRightGrantedBySharing(user, workAreaStart, workArea, workAreaOperation, null);
					}
					
					if (checkRootFolderAccess(user, workAreaStart, workAreaOperation)) {
						//OK, this is accessible by this user
						return true;
					} else {
						//See if this was shared. If so, we can ignore the rootFolderAccess check
						if (checkSharing) {
							return testRightGrantedBySharing(user, workAreaStart, workArea, workAreaOperation, null);
						} else {
							return false;
						}
					}
				} else {
					if (checkSharing) {
						return testRightGrantedBySharing(user, workAreaStart, workArea, workAreaOperation, null);
					} else {
						return false;
					}
				}
			}
		} else {
			//The workspaceStart is either an acl controlled folder or it is not inheriting from its parent
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
			// If current user is the workArea or workAreaStart owner,
			// add special ID to is membership.
			Long userId = user.getId();
			boolean addOwnerUserId = userId.equals(workArea.getOwnerId());
			if (!addOwnerUserId) {
				// Bugzilla 939041:  We don't want to expand the
				//    members to lookup to include owner rights for
				//    FolderEntry's.  To do so would allow extra
				//    capabilities on it that would otherwise not be
				//    allowed.
				boolean widenEntryOwnerAccess = SPropsUtil.getBoolean(SPropsUtil.WIDEN_ENTRY_OWNER_ACCESS, false);
				addOwnerUserId = ((widenEntryOwnerAccess || (!(workAreaStart instanceof FolderEntry))) && userId.equals(workAreaStart.getOwnerId()));
				if (logger.isDebugEnabled()) {
					logger.debug("testOperationRecursive( 1:user:  '" + user.getTitle() + "' ):  addOwnerUserId:  " + addOwnerUserId);
					logger.debug("\tworkAreaOperation:  " + workAreaOperation.getName());
					logger.debug("\tworkArea ID:  " + workArea.getWorkAreaId());
					logger.debug("\tworkAreaStart ID:  " + workAreaStart.getWorkAreaId());
					logger.debug("\tworkAreaStart instanceof FolderEntry:  " + (workAreaStart instanceof FolderEntry));
					logger.debug("\t\tuser is workArea's owner:  false");
					logger.debug("\t\tuser is workAreaStart's owner:  " + userId.equals(workAreaStart.getOwnerId()));
				}
			}
			else {
				if (logger.isDebugEnabled()) {
					logger.debug("testOperationRecursive( 2:user:  '" + user.getTitle() + "' ):  addOwnerUserId:  true");
					logger.debug("\tworkAreaOperation:  " + workAreaOperation.getName());
					logger.debug("\tworkArea ID:  " + workArea.getWorkAreaId());
					logger.debug("\tworkAreaStart ID:  " + workAreaStart.getWorkAreaId());
					logger.debug("\t\tuser is workArea's owner:  true");
					logger.debug("\t\tuser is workAreaStart's owner:  " + userId.equals(workAreaStart.getOwnerId()));
				}
			}
			if (addOwnerUserId) {
				userApplicationLevelMembersToLookup.add(ObjectKeys.OWNER_USER_ID);
			}
			Set<Long> teamMembers = null;
			if (workAreaStart instanceof FolderEntry) {
				teamMembers = getBinderModule().getTeamMemberIds( ((FolderEntry)workAreaStart).getParentBinder() );
			} else if ( workAreaStart instanceof Binder ){
				teamMembers = getBinderModule().getTeamMemberIds( (Binder)workAreaStart );
			}
			if (teamMembers != null && !Collections.disjoint(teamMembers, userApplicationLevelMembersToLookup)) {
				userApplicationLevelMembersToLookup.add(ObjectKeys.TEAM_MEMBER_ID);
			}
			// Take container groups into consideration
			List<Long> containerGroupsToLookup = getProfileDao().getMemberOfLdapContainerGroupIds(user.getId(), zoneId);
			Set<Long> userAllMembersToLookup = null;
			if (containerGroupsToLookup.isEmpty()) {
				userAllMembersToLookup = userApplicationLevelMembersToLookup;
			} else {	
				userAllMembersToLookup = new HashSet<Long>(userApplicationLevelMembersToLookup);
				userAllMembersToLookup.addAll(containerGroupsToLookup);
			}
			// Regular ACL checking must take container groups into consideration. 
			// However, sharing-granted ACL checking must not because sharing can never take
			// place against a container group.
			if (workArea.isAclExternallyControlled() && 
					workArea instanceof FolderEntry && 
					((FolderEntry)workArea).noAclDredged()) {
				//See if this is an operation controlled externally
				if (isExternalAclControlledOperation) {
					//This entry has no ACL set up, so it has to get it from the file system 
					if (testRightGrantedByDredgedAcl(user, (FolderEntry)workArea, workAreaOperation)) {
						return true;
					}
				}
			} else if (checkWorkAreaFunctionMembership(user.getZoneId(),
							workArea, workAreaOperation, userAllMembersToLookup)) {
				if (checkRootFolderAccess(user, workAreaStart, workAreaOperation)) {
					//OK, this is accessible by this user
					return true;
				} else {
					if (checkSharing) {
						//See if this was shared. If so, we can ignore the rootFolderAccess check
						return testRightGrantedBySharing(user, workAreaStart, workArea, workAreaOperation, userApplicationLevelMembersToLookup);
					} else {
						return false;
					}
				}
			}
			
			//It isn't available by normal ACLs, so check if shared
			if (checkSharing) {
				return testRightGrantedBySharing(user, workAreaStart, workArea, workAreaOperation, userApplicationLevelMembersToLookup);
			} else {
				return false;
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
		if (WorkAreaOperation.ZONE_ADMINISTRATION.equals(workAreaOperation)) {
			if (user.isDisabled() || user.isDeleted() || user.isShared() || !user.getIdentityInfo().isInternal()) {
   				//External users the guest user or disabled and deleted accounts are not allowed to do zone admistration functions
   				throw OperationAccessControlExceptionNoName.newInstance(user.getName(), 
   						workAreaOperation.toString(), workArea);
   			}
		}
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
        		throw OperationAccessControlExceptionNoName.newInstance(user.getName(), 
            			workAreaOperation.toString(), workArea);
        	}
        	if (testOperation(user, workArea, WorkAreaOperation.READ_ENTRIES) ||
        			testOperation(user, workArea, WorkAreaOperation.VIEW_BINDER_TITLE)) {
        		throw new OperationAccessControlException(user.getName(), 
        			workAreaOperation.toString(), workArea.toString());
        	} else {
        		//This user shouldn't see anything about this workarea
        		throw OperationAccessControlExceptionNoName.newInstance(user.getName(), 
            			workAreaOperation.toString(), workArea);
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
		FolderEntry topEntry = null;
		if (workAreaStart instanceof FolderEntry) {
			topEntry = ((FolderEntry)workAreaStart).getTopEntry();
		}

		//Check for this being a reply. We allow recursion of replies up to the parent entry.
    	if (workAreaStart != workArea && topEntry == null) {
    		//This is not a reply to an entry, so recursion is not being used
    		return false;
    	} else if (workAreaStart == workArea && topEntry != null && topEntry != workAreaStart) {
			//This is a reply. So we must check the top entry instead
			return testRightGrantedBySharing(user, workAreaStart, topEntry, workAreaOperation, userMembers);
    	}
    	
    	if (testRightGrantedBySharing(user, workArea, workAreaOperation, userMembers)) {
    		return true;
    	} else {
    		//Is this the guest user? If so, we are done.
    		if (ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
    			return false;
    		}
    		if (WorkAreaOperation.ONLY_SEE_GROUP_MEMBERS.equals(workAreaOperation) || 
    				WorkAreaOperation.OVERRIDE_ONLY_SEE_GROUP_MEMBERS.equals(workAreaOperation)) {
    			//Don't try to check further on these rights. It will loop. These operations can't be shared.
    			return false;
    		}
    		//The user doesn't have direct access, see if guest has access (if guest is allowed in at all)
    		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
			AuthenticationConfig config = getAuthenticationModule().getAuthenticationConfigForZone(zoneId);
			if (!config.isAllowAnonymousAccess()) {
				//Guest access is not enabled, disallow access to everything
				return false;
			} else {
				User guest = getProfileDao().getReservedUser(ObjectKeys.GUEST_USER_INTERNALID, zoneId);
				return testOperation(guest, workArea, workAreaOperation);
			}
    	}
    }
    
    private boolean testRightGrantedByDredgedAcl(User user, final FolderEntry workArea, final WorkAreaOperation workAreaOperation) {
    	// This entry has to get its ACL role from the file system
    	final long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	if (user.equals(RequestContextHolder.getRequestContext().getUser())) {
    		//We can only do this for the current user
	    	Long roleId = AccessUtils.askExternalSystemForRoleId(workArea);
	    	if (roleId != null) {
		    	Function f = getFunctionManager().getFunction(zoneId, roleId);
		    	for (WorkAreaOperation wao : (Set<WorkAreaOperation>)f.getOperations()) {
		    		if (wao.equals(workAreaOperation)) {
		    			//This function includes the desired operation.
		    			return true;
		    		}
		    	}
	    	}
    	} else {
    		//Since the current user is not the same as the user being checked, we must do this in a "runas"
			try {
				boolean result = (boolean)RunasTemplate.runas(new RunasCallback() {
					public Object doAs() {
				    	Long roleId = AccessUtils.askExternalSystemForRoleId(workArea);
				    	if (roleId != null) {
					    	Function f = getFunctionManager().getFunction(zoneId, roleId);
					    	for (WorkAreaOperation wao : (Set<WorkAreaOperation>)f.getOperations()) {
					    		if (wao.equals(workAreaOperation)) {
					    			//This function includes the desired operation.
					    			return true;
					    		}
					    	}
				    	}
				    	return false;
					}
				}, zoneId, user.getId());
				return result;
			} catch (Exception ex) {}
    	}
    	return false;
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
    		List<Map> myTeams = getBinderModule().getTeamMemberships(user.getId(), SearchUtils.fieldNamesList(Constants.DOCID_FIELD));
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
    			teamMemberIds = getBinderModule().getTeamMemberIds( binder );
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
