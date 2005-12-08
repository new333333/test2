package com.sitescape.ef.security.dao;

import java.util.List;
import java.util.Set;

/**
 *
 * @author Jong Kim
 */
public interface SecurityDao {
    
    public void save(Object obj);
    
    public void update(Object obj);
    
    public void delete(Object obj);
    
    public List findFunctions(String zoneId);
    
    public List findWorkAreaFunctionMemberships(String zoneId, Long workAreaId, String workAreaType);
    
    /**
     * 
     * @param zoneId
     * @param workAreaId
     * @param workAreaType
     * @param workAreaOperationName
     * @param membersToLookup	a set of Long
     * @return
     */
    public boolean checkWorkAreaFunctionMembership(String zoneId, Long workAreaId, 
            String workAreaType, String workAreaOperationName, Set membersToLookup);
}
