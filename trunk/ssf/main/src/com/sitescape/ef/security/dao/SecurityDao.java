package com.sitescape.ef.security.dao;

import java.util.List;
import java.util.Set;

import com.sitescape.ef.NoObjectByTheIdException;
import com.sitescape.ef.security.function.Function;
import com.sitescape.ef.security.function.WorkAreaFunctionMembership;

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
