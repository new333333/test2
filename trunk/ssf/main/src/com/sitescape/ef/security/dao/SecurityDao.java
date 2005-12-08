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
    
    public List findFunctions(String zoneName);
    
    public List findWorkAreaFunctionMemberships(String zoneName, Long workAreaId, String workAreaType);
    
    /**
     * 
     * @param zoneName
     * @param workAreaId
     * @param workAreaType
     * @param workAreaOperationName
     * @param membersToLookup	a set of Long
     * @return
     */
    public boolean checkWorkAreaFunctionMembership(String zoneName, Long workAreaId, 
            String workAreaType, String workAreaOperationName, Set membersToLookup);
}
