/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.security.function.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.NoObjectByTheIdException;
import org.kablink.teaming.security.dao.SecurityDao;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.FunctionManager;
import org.kablink.teaming.security.function.WorkAreaOperation;

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
    public void deleteFunctions(Long zoneId) {
    	List functions = findFunctions(zoneId);
    	for (int i=0; i<functions.size(); ++i) {
    		deleteFunction((Function)functions.get(i));
    	}
    }
    public List deleteFunction(Function function) {
    	List result = getSecurityDao().findWorkAreaFunctionMemberships(function.getZoneId(), function.getId());
    	if (result.isEmpty()) {
    		getSecurityDao().delete(function);
    		return null;
    	}
    	else {
    		return result;
    	}
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
