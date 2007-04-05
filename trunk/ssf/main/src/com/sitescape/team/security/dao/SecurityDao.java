/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.security.dao;

import java.util.List;
import java.util.Set;

import com.sitescape.team.NoObjectByTheIdException;
import com.sitescape.team.security.function.Function;
import com.sitescape.team.security.function.WorkAreaFunctionMembership;

/**
 *
 * @author Jong Kim
 */
public interface SecurityDao {
    
    public void save(Object obj);
    
    public void update(Object obj);
    
    public void delete(Object obj);
    public void deleteWorkAreaFunctionMemberships(Long zoneId, Long workAreaId, String workAreaType);
    
    public Function loadFunction(Long zoneId, Long id)  throws NoObjectByTheIdException;
    	    	     
    public List findFunctions(Long zoneId);

    /**
     * Returns specified <code>WorkAreaFunctionMembership</code>.
     * Returns <code>null</code> if not exists.
     * 
     * @param zoneId
     * @param workAreaId
     * @param workAreaType
     * @param functionId
     * @return
     */
    public WorkAreaFunctionMembership getWorkAreaFunctionMembership(Long zoneId, 
    		Long workAreaId, String workAreaType, Long functionId);

    public List findWorkAreaFunctionMemberships(Long zoneId, Long functionId);
	public List findWorkAreaFunctionMemberships(Long zoneId, Long functionId, String workAreaType);

    /**
     * 
     * @param zoneId
     * @param workAreaId
     * @param workAreaType
     * @param workAreaOperationName
     * @param membersToLookup	a set of Long
     * @return
     */
    public boolean checkWorkAreaFunctionMembership(Long zoneId, Long workAreaId, 
            String workAreaType, String workAreaOperationName, Set membersToLookup);
    public List findWorkAreaFunctionMembershipsByOperation(Long zoneId,
            Long workAreaId, String workAreaType, 
            String workAreaOperationName);
    public List findWorkAreaByOperation( Long zoneId,
             String workAreaOperationName,  Set membersToLookup);

}
