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
import org.kablink.teaming.NoObjectByTheNameException;
import org.kablink.teaming.security.dao.SecurityDao;
import org.kablink.teaming.security.function.Condition;
import org.kablink.teaming.security.function.ConditionEvaluationResult;
import org.kablink.teaming.security.function.ConditionalClause;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.FunctionManager;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
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
    	return deleteFunction(function, Boolean.FALSE);
    }
    public List deleteFunction(Function function, boolean force) {
    	List<WorkAreaFunctionMembership> result = getSecurityDao().findWorkAreaFunctionMemberships(function.getZoneId(), function.getId());
    	if (force && !result.isEmpty()) {
    		//Forcably remove all of the memberships, too
    		for (WorkAreaFunctionMembership wfm : result) {
    			getSecurityDao().delete(wfm);
    		}
    		result = getSecurityDao().findWorkAreaFunctionMemberships(function.getZoneId(), function.getId());
    	}
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
	/* (non-Javadoc)
	 * @see org.kablink.teaming.security.function.FunctionManager#evaluateConditionalClause(org.kablink.teaming.security.function.Function)
	 */
	@Override
	public ConditionEvaluationResult evaluateConditionalClauses(
			Function function) {
		ConditionEvaluationResult.Status status = ConditionEvaluationResult.Status.NOT_CONDITIONAL;
		List<Long> metConditionIds = new ArrayList<Long>();
		
		List<ConditionalClause> mustConditions = function.getConditionalClauses(ConditionalClause.Meet.MUST);
		if(mustConditions.size() > 0) {
			// There are one or more conditions of MUST type.
    		for(ConditionalClause conditionalClause : mustConditions) {
    			Condition condition = conditionalClause.getCondition();
				if(condition.evaluate())
					metConditionIds.add(condition.getId());
				else
					break; // skip the rest of the conditions of MUST type
    		}
    		if(mustConditions.size() == metConditionIds.size()) {
    			// All of the MUST conditions were met.
    			status = ConditionEvaluationResult.Status.ALL_MUST_MET;
    		}
    		else {
    			// Not all of the MUST conditions were met.
    			status = ConditionEvaluationResult.Status.NOT_ALL_MUST_MET;
    			metConditionIds.clear();
    		}
		}
		else {
			// There is no condition of MUST type. Check SHOULD conditions.
    		List<ConditionalClause> shouldConditions = function.getConditionalClauses(ConditionalClause.Meet.SHOULD);
    		if(shouldConditions.size() > 0) {
    			// There are one or more conditions of SHOULD type.
    			for(ConditionalClause conditionalClause : shouldConditions) {
        			Condition condition = conditionalClause.getCondition();
    				if(condition.evaluate())
    					metConditionIds.add(condition.getId());
    				else
    					continue;	// continue on to the next condition of SHOULD type
    			}
    			if(metConditionIds.size() > 0)
    				status = ConditionEvaluationResult.Status.SOME_SHOULD_MET;
    			else
    				status = ConditionEvaluationResult.Status.NO_SHOULD_MET;
    		}
    		else {
    			// There is no condition of SHOULD type. This means that the function is not conditional.
    		}
		}
		return new ConditionEvaluationResult(status, metConditionIds);
	}
	
    public Function findFunctionByName(Long zoneId, String name) throws NoObjectByTheNameException {
        // This is implemented on top of getFunctions(Long) based on the
        // assumption that the underlying ORM effectively caches the
        // result of the query. 
        
        List functions = this.findFunctions(zoneId);
        
        for(Iterator i = functions.iterator(); i.hasNext();) {
            Function function = (Function) i.next();
            if(function.getName().equals(name))
                 return function;
        }
        
        return null;
    }
}
