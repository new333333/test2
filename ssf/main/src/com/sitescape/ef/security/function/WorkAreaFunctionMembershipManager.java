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
     * Returns <code>WorkAreaFunctionMembership</code> for
     * @param zoneName
     * @param workArea
     * @param functionId
     * @return
     */
    public WorkAreaFunctionMembership getWorkAreaFunctionMembership(String zoneName, WorkArea workArea, Long functionId);
    
    /**
     * Returns a list of <code>WorkAreaFunctionMembership</code>
     * @param zoneName
     * @param workArea
     * @return
     */
    public List findWorkAreaFunctionMemberships(String zoneName, WorkArea workArea);
    
    /**
     * 
     * @param zoneName
     * @param workArea
     * @param workAreaOperation
     * @param membersToLookup a set of <code>Long</code>
     * @return
     */
    public boolean checkWorkAreaFunctionMembership(String zoneName, WorkArea workArea, 
            WorkAreaOperation workAreaOperation, Set membersToLookup);   
}
