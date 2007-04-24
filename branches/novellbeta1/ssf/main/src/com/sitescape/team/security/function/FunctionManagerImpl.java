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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sitescape.team.NoObjectByTheIdException;
import com.sitescape.team.NotSupportedException;
import com.sitescape.team.security.dao.SecurityDao;
import com.sitescape.team.util.NLT;
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
    	List result = getSecurityDao().findWorkAreaFunctionMemberships(function.getZoneId(), function.getId());
    	if (result.isEmpty()) getSecurityDao().delete(function);
    	else throw new NotSupportedException(NLT.get("errorcode.role.inuse", new Object[]{function.getName()}));
    }

    public void updateFunction(Function function) {
        getSecurityDao().update(function);
    }

    public List findFunctions(Long zoneId) {
        return getSecurityDao().findFunctions(zoneId);
    }
    public Function getFunction(Long zoneId, Long id)  throws NoObjectByTheIdException {
    	return getSecurityDao().loadFunction(zoneId, id);
    }

    public List findFunctions(Long zoneId, WorkAreaOperation workAreaOperation) {
        // This is implemented on top of getFunctions(Long) based on the
        // assumption that the underlying ORM effectively caches the
        // result of the query. 
        
        List functions = this.findFunctions(zoneId);
        
        List results = new ArrayList();
        
        for(Iterator i = functions.iterator(); i.hasNext();) {
            Function function = (Function) i.next();
            if(function.getOperations().contains(workAreaOperation))
                results.add(function);
        }
        
        return results;
    }
}
