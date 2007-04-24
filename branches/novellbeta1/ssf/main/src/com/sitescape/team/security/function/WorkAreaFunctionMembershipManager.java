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
package com.sitescape.team.security.function;

import java.util.List;
import java.util.Set;

/**
 *
 * @author Jong Kim
 */
public interface WorkAreaFunctionMembershipManager {
    
    public void addWorkAreaFunctionMembership(WorkAreaFunctionMembership functionMembership);
    
    public void deleteWorkAreaFunctionMemberships(Long zoneId, WorkArea workArea);
    public void deleteWorkAreaFunctionMembership(WorkAreaFunctionMembership functionMembership);
    
    public void updateWorkAreaFunctionMembership(WorkAreaFunctionMembership functionMembership);
    
    /**
     * Returns <code>WorkAreaFunctionMembership</code> for
     * @param zoneId
     * @param workArea
     * @param functionId
     * @return
     */
    public WorkAreaFunctionMembership getWorkAreaFunctionMembership(Long zoneId, WorkArea workArea, Long functionId);
    
    /**
     * Returns a list of <code>WorkAreaFunctionMembership</code>
     * @param zoneId
     * @param workArea
     * @return
     */
    public List findWorkAreaFunctionMemberships(Long zoneId, WorkArea workArea);
    public List findWorkAreaFunctionMembershipsByOperation(Long zoneId, WorkArea workArea, WorkAreaOperation workAreaOperation);
    public List findWorkAreaFunctionMembershipsByOperation(Long zoneId, WorkAreaOperation workAreaOperation, Set membersToLookup);
    /**
     * 
     * @param zoneId
     * @param workArea
     * @param workAreaOperation
     * @param membersToLookup a set of <code>Long</code>
     * @return
     */
    public boolean checkWorkAreaFunctionMembership(Long zoneId, WorkArea workArea, 
            WorkAreaOperation workAreaOperation, Set membersToLookup);   
}
