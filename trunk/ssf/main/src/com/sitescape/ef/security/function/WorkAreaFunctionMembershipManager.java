package com.sitescape.ef.security.function;

import java.util.List;
import java.util.Set;

/**
 *
 * @author Jong Kim
 */
public interface WorkAreaFunctionMembershipManager {
    
    public void addWorkAreaFunctionMembership(WorkAreaFunctionMembership functionMembership);
    
    public void deleteWorkAreaFunctionMembership(WorkAreaFunctionMembership functionMembership);
    
    public void updateWorkAreaFunctionMembership(WorkAreaFunctionMembership functionMembership);
    
    /**
     * Returns a list of <code>WorkAreaFunctionMembership</code>
     * @param zoneId
     * @param workArea
     * @return
     */
    public List findWorkAreaFunctionMemberships(String zoneId, WorkArea workArea);
    
    /**
     * 
     * @param zoneId
     * @param workArea
     * @param workAreaOperation
     * @param membersToLookup a set of <code>Long</code>
     * @return
     */
    public boolean checkWorkAreaFunctionMembership(String zoneId, WorkArea workArea, 
            WorkAreaOperation workAreaOperation, Set membersToLookup);   
}
