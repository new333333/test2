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
package com.sitescape.team.security.function.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sitescape.team.security.dao.SecurityDao;
import com.sitescape.team.security.function.WorkArea;
import com.sitescape.team.security.function.WorkAreaFunctionMembership;
import com.sitescape.team.security.function.WorkAreaFunctionMembershipManager;
import com.sitescape.team.security.function.WorkAreaOperation;

/**
 *
 * @author Jong Kim
 */
public class WorkAreaFunctionMembershipManagerImpl implements WorkAreaFunctionMembershipManager {

    private SecurityDao securityDao;
    
    public SecurityDao getSecurityDao() {
        return securityDao;
    }
    public void setSecurityDao(SecurityDao securityDao) {
        this.securityDao = securityDao;
    }

    public void addWorkAreaFunctionMembership(WorkAreaFunctionMembership functionMembership) {
        getSecurityDao().save(functionMembership);
    }
    public void copyWorkAreaFunctionMemberships(Long zoneId, WorkArea source, WorkArea destination) {
		List<WorkAreaFunctionMembership> wfms = findWorkAreaFunctionMemberships(zoneId, source);
		for (WorkAreaFunctionMembership fm: wfms) {
			WorkAreaFunctionMembership membership = new WorkAreaFunctionMembership();
			membership.setZoneId(zoneId);
			membership.setWorkAreaId(destination.getWorkAreaId());
			membership.setWorkAreaType(destination.getWorkAreaType());
			membership.setFunctionId(fm.getFunctionId());
			membership.setMemberIds(new HashSet(fm.getMemberIds()));
			addWorkAreaFunctionMembership(membership);	
		}
	}
    public void deleteWorkAreaFunctionMemberships(Long zoneId, WorkArea workArea) {
    	getSecurityDao().deleteWorkAreaFunctionMemberships(zoneId, workArea.getWorkAreaId(), workArea.getWorkAreaType());
    }
    public void deleteWorkAreaFunctionMembership(WorkAreaFunctionMembership functionMembership) {
        getSecurityDao().delete(functionMembership);
    }

    public void updateWorkAreaFunctionMembership(WorkAreaFunctionMembership functionMembership) {
        getSecurityDao().update(functionMembership);
    }

    public WorkAreaFunctionMembership getWorkAreaFunctionMembership(Long zoneId, WorkArea workArea, Long functionId) {
        return getSecurityDao().getWorkAreaFunctionMembership
    	(zoneId, workArea.getWorkAreaId(), workArea.getWorkAreaType(), functionId);
    }

    public List findWorkAreaFunctionMemberships(Long zoneId, WorkArea workArea) {
        return getSecurityDao().findWorkAreaFunctionMemberships
        	(zoneId, workArea.getWorkAreaId(), workArea.getWorkAreaType());
    }
    //Find workareas assigning this user to a specific operation.  
    public List findWorkAreaFunctionMembershipsByOperation(Long zoneId, WorkAreaOperation workAreaOperation, Set membersToLookup) {
        return getSecurityDao().findWorkAreaByOperation(zoneId, workAreaOperation.getName(), membersToLookup);
    }
    //Find memberhships for a specific workarea that have a specified operation.
    public List findWorkAreaFunctionMembershipsByOperation(Long zoneId, WorkArea workArea, WorkAreaOperation workAreaOperation) {
    	return getSecurityDao().findWorkAreaFunctionMembershipsByOperation(zoneId, workArea.getWorkAreaId(), workArea.getWorkAreaType(), workAreaOperation.getName());
    }

    /*
    public boolean checkWorkAreaFunctionMembership(Long zoneName, WorkArea workArea, 
            Set membersToLookup, List functions) {
        // There are a number of different approaches we can take in implementing 
        // this method. This specific implementation relies on the query cache
        // and the observation that the number of different combinations of the
        // paramaters is relatively small and the most heavily sought-after 
        // results will remain in the cache. We will have to measure the 
        // efficiency of this approach to see if it is acceptable. 
        
        
        // TODO To be rewritten...
        
        List waFunctionMemberships = 
            getSecurityDao().findWorkAreaFunctionMembership
            (zoneName, workArea.getId(), functions);
        int size = waFunctionMemberships.size();
        for(int i = 0; i < size; i++) {
            Set memberIds = ((WorkAreaFunctionMembership) waFunctionMemberships.get(i)).getMemberIds();
            for(Iterator it = membersToLookup.iterator(); it.hasNext();) {
                if(memberIds.contains(it.next()))
                    return true;
            }
        }
        return false;
    }*/
    
    public boolean checkWorkAreaFunctionMembership(Long zoneId, WorkArea workArea, 
            WorkAreaOperation workAreaOperation, Set membersToLookup) {
        return getSecurityDao().checkWorkAreaFunctionMembership
        	(zoneId, workArea.getWorkAreaId(), workArea.getWorkAreaType(), 
        	        workAreaOperation.getName(), membersToLookup);
    }

}