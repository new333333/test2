package com.sitescape.ef.security.function;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sitescape.ef.security.dao.SecurityDao;
import com.sitescape.ef.NoObjectByTheIdException;
import com.sitescape.ef.NotSupportedException;
import com.sitescape.ef.util.NLT;
/**
 *
 * @author Jong Kim
 */
public class FunctionManagerImpl implements FunctionManager {

    private SecurityDao securityDao;
    
    public SecurityDao getSecurityDao() {
        return securityDao;
    }
    public void setSecurityDao(SecurityDao securityDao) {
        this.securityDao = securityDao;
    }
    
    public void addFunction(Function function) {
        getSecurityDao().save(function);
    }

    public void deleteFunction(Function function) throws NotSupportedException {
    	if (function.isReserved()) throw new NotSupportedException(NLT.get("errorcode.role.reserved", new Object[]{function.getName()}));
    	List result = getSecurityDao().findWorkAreaFunctionMemberships(function.getZoneName(), function.getId());
    	if (result.isEmpty()) getSecurityDao().delete(function);
    	else throw new NotSupportedException(NLT.get("errorcode.role.inuse", new Object[]{function.getName()}));
    }

    public void updateFunction(Function function) {
        getSecurityDao().update(function);
    }

    public List findFunctions(String zoneName) {
        return getSecurityDao().findFunctions(zoneName);
    }
    public Function getFunction(String zoneName, Long id)  throws NoObjectByTheIdException {
    	return getSecurityDao().loadFunction(zoneName, id);
    }
    public Function getReservedFunction(String zoneName, String id)  throws NoObjectByTheIdException {
    	return getSecurityDao().loadReservedFunction(zoneName, id);
    }

    public List findFunctions(String zoneName, WorkAreaOperation workAreaOperation) {
        // This is implemented on top of getFunctions(Long) based on the
        // assumption that the underlying ORM effectively caches the
        // result of the query. 
        
        List functions = this.findFunctions(zoneName);
        
        List results = new ArrayList();
        
        for(Iterator i = functions.iterator(); i.hasNext();) {
            Function function = (Function) i.next();
            if(function.getOperations().contains(workAreaOperation))
                results.add(function);
        }
        
        return results;
    }
}
