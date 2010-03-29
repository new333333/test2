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

import org.kablink.teaming.InternalException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Application;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.license.LicenseManager;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.AccessControlManager;
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
import org.springframework.beans.factory.InitializingBean;

/**
 *
 * @author Jong Kim
 */
public class AccessControlManagerImpl implements AccessControlManager, InitializingBean {
    
    private FunctionManager functionManager;
    private WorkAreaFunctionMembershipManager workAreaFunctionMembershipManager;
    private CoreDao coreDao;
    private ProfileDao profileDao;
    private LicenseManager licenseManager;
    private Map synchAgentRights;
    
	public void afterPropertiesSet() throws Exception {
		synchAgentRights = new HashMap();
		String[] strs = SPropsUtil.getStringArray("synchronization.agent.rights", ",");
		if(strs != null) {
			for(String str:strs)
				synchAgentRights.put(str, str);
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
    /*
    public void checkWorkAreaAccessControl(WorkArea workArea, 
            WorkAreaOperation workAreaOperation) throws AccessControlException {
        if(workArea.isFunctionMembershipInherited()) {
            WorkArea parentWorkArea = workArea.getParentWorkArea();
            if(parentWorkArea == null)
                throw new InternalException("Cannot inherit function membership when it has no parent");
            else
                checkWorkAreaAccessControl(parentWorkArea, workAreaOperation);
        }
        else {
	        Long zoneName = RequestContextHolder.getRequestContext().getZoneName();
	        
	        Set membersToLookup = RequestContextHolder.getRequestContext().getPrincipalIds();
	        
	        List functions = getFunctionManager().getFunctions(zoneName, workAreaOperation);
	        
	        boolean match = getWorkAreaFunctionMembershipManager().checkWorkAreaFunctionMembership(zoneName, workArea, membersToLookup, functions);
	        
	        if(!match)
	            throw new AccessControlException("The user is not authorized to perform the operation '" + 
	                    workAreaOperation.toString() + "' to the work area '" + workArea.getId() + "'");
        }
    }*/
    
    public boolean testOperation(WorkArea workArea, WorkAreaOperation workAreaOperation) 
    	throws AccessControlException {
        return testOperation
        	(RequestContextHolder.getRequestContext().getUser(), 
        	        workArea, workArea, workAreaOperation);
    }
	
	public boolean testOperation(User user,
			WorkArea workArea, WorkAreaOperation workAreaOperation) {
		return testOperation(user, workArea, workArea, workAreaOperation);
		
	}
	//pass the original ownerId in.  Recursive calls need the original
	private boolean testOperation(User user, WorkArea workAreaStart, WorkArea workArea, WorkAreaOperation workAreaOperation) {
		if(isAccessCheckTemporarilyDisabled())
			return true;
		
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		Application application = null;
		if(RequestContextHolder.getRequestContext() != null)
			application = RequestContextHolder.getRequestContext().getApplication();
		if ((user.isSuper() || isSynchronizationWork(user, workAreaOperation)) && 
				(application == null || application.isTrusted())) 
			return true;
		if (!workAreaOperation.equals(WorkAreaOperation.READ_ENTRIES) && 
				!workAreaOperation.equals(WorkAreaOperation.VIEW_BINDER_TITLE) && 
				!getLicenseManager().validLicense())return false;
		if (workArea.isFunctionMembershipInherited()) {
			WorkArea parentWorkArea = workArea.getParentWorkArea();
			if (parentWorkArea == null)
				throw new InternalException(
						"Cannot inherit function membership when it has no parent");
			else
				// use the original workArea owner
				return testOperation(user, workAreaStart, parentWorkArea, workAreaOperation);
		} else {
			Set membersToLookup = null;
			if(application != null && !application.isTrusted()) {
				membersToLookup = getProfileDao().getPrincipalIds(application);
				// First, test against the zone-wide maximum set by the admin
				if(!getWorkAreaFunctionMembershipManager()
						.checkWorkAreaFunctionMembership(user.getZoneId(),
								getCoreDao().loadZoneConfig(user.getZoneId()),
								workAreaOperation,
								membersToLookup)) {
					return false;
				}
				// First test passed. Now test against the specified work area.
				if(!getWorkAreaFunctionMembershipManager()
						.checkWorkAreaFunctionMembership(user.getZoneId(),
								workArea, 
								workAreaOperation, 
								membersToLookup)) {
					return false;
				}
			}
			membersToLookup = getProfileDao().getPrincipalIds(user);
			Long allUsersId = Utils.getAllUsersGroupId();
			if (allUsersId != null && !workArea.getWorkAreaType().equals(EntityIdentifier.EntityType.zone.name()) 
					&& membersToLookup.contains(allUsersId) && 
					Utils.canUserOnlySeeCommonGroupMembers(user)) {
				if (Utils.isWorkareaInProfilesTree(workArea)) {
					//If this user does not share a group with the binder owner, remove the "All Users" group.
					boolean remove = true;
					if (workArea.getWorkAreaType().equals(EntityType.workspace.name()) ||
							workArea.getWorkAreaType().equals(EntityType.folder.name())) {
						List<Group> groups = workArea.getOwner().getMemberOf();
						List gIds = new ArrayList();
						for (Group g : groups) {
							gIds.add(g.getId());
							if (membersToLookup.contains(g.getId())) {
								remove = false;
								break;
							}
						}
						if (remove) {
							//There wasn't a direct match of groups, go look in the exploded list
							Set<Long> userGroupIds = getProfileDao().getAllGroupMembership(workArea.getOwner().getId(), zoneId);
							for (Long gId : userGroupIds) {
								if (membersToLookup.contains(gId)) {
									remove = false;
									break;
								}
							}
						}
					}
					if (remove) membersToLookup.remove(allUsersId);
				}
			}
			//if current user is the workArea owner, add special Id to is membership
			if (user.getId().equals(workAreaStart.getOwnerId())) membersToLookup.add(ObjectKeys.OWNER_USER_ID);
			if (!Collections.disjoint(workAreaStart.getTeamMemberIds(), membersToLookup)) membersToLookup.add(ObjectKeys.TEAM_MEMBER_ID);
			return getWorkAreaFunctionMembershipManager()
					.checkWorkAreaFunctionMembership(user.getZoneId(),
							workArea, workAreaOperation, membersToLookup);
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
        if (!testOperation(user, workArea, workArea, workAreaOperation)) {
        	if (workArea instanceof Entry && ((Entry)workArea).hasEntryAcl() && ((Entry)workArea).isIncludeFolderAcl()) {
        		//See if the parent or the entry is allowing access
        		if (testOperation(user, ((Entry)workArea).getParentBinder(), ((Entry)workArea).getParentBinder(), workAreaOperation)) {
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
        	if (testOperation(user, workArea, workArea, WorkAreaOperation.READ_ENTRIES) ||
        			testOperation(user, workArea, workArea, WorkAreaOperation.VIEW_BINDER_TITLE)) {
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
	private boolean isSynchronizationWork(User user, WorkAreaOperation workAreaOperation) {
		return ObjectKeys.SYNCHRONIZATION_AGENT_INTERNALID.equals(user.getInternalId()) &&
			(synchAgentRights.get(workAreaOperation.getName()) != null);
	}
}
