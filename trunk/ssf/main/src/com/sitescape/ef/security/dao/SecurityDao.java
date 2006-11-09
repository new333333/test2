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
    public void deleteWorkAreaFunctionMemberships(String zoneName, Long workAreaId, String workAreaType);
    
    public Function loadFunction(String zoneName, Long id)  throws NoObjectByTheIdException;
    public Function loadReservedFunction(String zoneName, String id)  throws NoObjectByTheIdException;
	public WorkAreaFunctionMembership loadWorkAreaFunctionMembership(String zoneName, Long id)  throws NoObjectByTheIdException;
    	    	     
    public List findFunctions(String zoneName);

    /**
     * Returns specified <code>WorkAreaFunctionMembership</code>.
     * Returns <code>null</code> if not exists.
     * 
     * @param zoneName
     * @param workAreaId
     * @param workAreaType
     * @param functionId
     * @return
     */
    public WorkAreaFunctionMembership getWorkAreaFunctionMembership(String zoneName, 
    		Long workAreaId, String workAreaType, Long functionId);

    public List findWorkAreaFunctionMemberships(String zoneName, Long workAreaId, String workAreaType);
	public List findWorkAreaFunctionMemberships(String zoneName, Long functionId);
	public List findWorkAreaFunctionMemberships(String zoneName, Long functionId, Set membersToLookup);

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
