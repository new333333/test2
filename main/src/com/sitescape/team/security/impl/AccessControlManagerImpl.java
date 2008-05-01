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
package com.sitescape.team.security.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.web.servlet.support.RequestContextUtils;

import com.sitescape.team.InternalException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.Application;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.User;
import com.sitescape.team.license.LicenseManager;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.AccessControlManager;
import com.sitescape.team.security.acl.AccessType;
import com.sitescape.team.security.acl.AclAccessControlException;
import com.sitescape.team.security.acl.AclContainer;
import com.sitescape.team.security.acl.AclControlled;
import com.sitescape.team.security.function.FunctionManager;
import com.sitescape.team.security.function.OperationAccessControlException;
import com.sitescape.team.security.function.OperationAccessControlExceptionNoName;
import com.sitescape.team.security.function.WorkArea;
import com.sitescape.team.security.function.WorkAreaFunctionMembership;
import com.sitescape.team.security.function.WorkAreaFunctionMembershipManager;
import com.sitescape.team.security.function.WorkAreaOperation;

/**
 *
 * @author Jong Kim
 */
public class AccessControlManagerImpl implements AccessControlManager {
    
    private FunctionManager functionManager;
    private WorkAreaFunctionMembershipManager workAreaFunctionMembershipManager;
    private ProfileDao profileDao;
    private LicenseManager licenseManager;
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
		Application application = null;
		if(RequestContextHolder.getRequestContext() != null)
			application = RequestContextHolder.getRequestContext().getApplication();
		if (user.isSuper() && application == null) return true;
		if (!workAreaOperation.equals(WorkAreaOperation.READ_ENTRIES) && !getLicenseManager().validLicense())return false;
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
			if(application != null) {
				membersToLookup = getProfileDao().getPrincipalIds(application);
				if(!getWorkAreaFunctionMembershipManager()
						.checkWorkAreaFunctionMembership(user.getZoneId(),
								workArea, workAreaOperation, membersToLookup)) {
					return false;
				}
			}
			membersToLookup = getProfileDao().getPrincipalIds(user);
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
        	//Make sure the user is allowed to see the workarea at all
        	if (!WorkAreaOperation.READ_ENTRIES.equals(workAreaOperation)) {
        		throw new OperationAccessControlException(user.getName(), 
        			workAreaOperation.toString(), workArea.toString());
        	} else {
        		//This user shouldn't see anything about this workarea
        		throw new OperationAccessControlExceptionNoName(user.getName(), 
            			workAreaOperation.toString());
        	}
        }
    }
}
