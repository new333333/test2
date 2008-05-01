/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.security.function.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sitescape.team.NoObjectByTheIdException;
import com.sitescape.team.security.dao.SecurityDao;
import com.sitescape.team.security.function.Function;
import com.sitescape.team.security.function.FunctionManager;
import com.sitescape.team.security.function.WorkAreaOperation;
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
