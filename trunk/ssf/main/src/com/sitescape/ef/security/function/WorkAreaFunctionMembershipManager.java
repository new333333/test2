package com.sitescape.ef.security.function;

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
    public List findWorkAreaFunctionMemberships(Long zoneId, Long functionId);
    public List findWorkAreaFunctionMemberships(Long zoneId, Set membersToLookup, Long functionId);
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
    /**
     * Check any membersToLookup id is assigned to a function for a specific workarea
     * @param zoneId
     * @param workArea
     * @param function
     * @param membersToLookup
     * @return
     */
    public boolean checkWorkAreaFunctionMembership(Long zoneId, WorkArea workArea, 
    		Long functionId, Set membersToLookup);
 }
