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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.security.dao.SecurityDao;
import org.kablink.teaming.security.function.ConditionalClause;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.kablink.teaming.security.function.WorkAreaFunctionMembershipManager;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.util.Validator;


/**
 *
 * @author Jong Kim
 */
public class WorkAreaFunctionMembershipManagerImpl implements WorkAreaFunctionMembershipManager {

    private SecurityDao securityDao;
    
    public SecurityDao getSecurityDao() {
        return securityDao;
    }
    public void setSecurityDao(SecurityDao securityDao) {
        this.securityDao = securityDao;
    }

    public void addWorkAreaFunctionMembership(WorkAreaFunctionMembership functionMembership) {
        getSecurityDao().save(functionMembership);
    }
    public void copyWorkAreaFunctionMemberships(Long zoneId, WorkArea source, WorkArea destination) {
    	copyWorkAreaFunctionMemberships(zoneId, source, source, destination, Boolean.FALSE, ObjectKeys.ROLE_TYPE_FILR);
    }
    public void copyWorkAreaFunctionMemberships(Long zoneId, WorkArea source, WorkArea extSource, WorkArea destination,
    		boolean justThisScope, String scope) {
    	List<WorkAreaOperation> extWaos = destination.getExternallyControlledRights();
		List<WorkAreaFunctionMembership> wfms = findWorkAreaFunctionMemberships(zoneId, source);
		for (WorkAreaFunctionMembership fm: wfms) {
			Function f = getFunction(zoneId, fm.getFunctionId());
			if (Validator.isNotNull(scope)) {
	        	if (f != null) {
					if ((scope.equals(f.getScope()) && justThisScope) || 
							(!scope.equals(f.getScope()) && !justThisScope)) {
						//See if this function has externally defined operations
						boolean copyThisFunction = true;
						//If source and extSource are the same, then copy all function memberships
						if (source != null && extSource != null && !source.equals(extSource)) {
							Set<WorkAreaOperation> functionWaos = f.getOperations();
							for (WorkAreaOperation wao : functionWaos) {
								if (extWaos.contains(wao)) {
									//Don't copy any function that has externally controlled rights.
									//This will be done later
									copyThisFunction = false;
									break;
								}
							}
						}
						if (copyThisFunction) {
							WorkAreaFunctionMembership membership = new WorkAreaFunctionMembership();
							membership.setZoneId(zoneId);
							membership.setWorkAreaId(destination.getWorkAreaId());
							membership.setWorkAreaType(destination.getWorkAreaType());
							membership.setFunctionId(fm.getFunctionId());
							membership.setMemberIds(new HashSet(fm.getMemberIds()));
							addWorkAreaFunctionMembership(membership);	
						}
					}
	        	}
			}
		}
		//Now do the functions for the external source (if different)
		if (source != null && extSource != null && !source.equals(extSource)) {
			wfms = findWorkAreaFunctionMemberships(zoneId, extSource);
			for (WorkAreaFunctionMembership fm: wfms) {
				Function f = getFunction(zoneId, fm.getFunctionId());
				if (Validator.isNotNull(scope)) {
		        	if (f != null) {
						if ((scope.equals(f.getScope()) && justThisScope) || 
								(!scope.equals(f.getScope()) && !justThisScope)) {
							//See if this function has externally defined operations
							Set<WorkAreaOperation> functionWaos = f.getOperations();
							boolean copyThisFunction = false;
							for (WorkAreaOperation wao : functionWaos) {
								if (extWaos.contains(wao)) {
									copyThisFunction = true;
									break;
								}
							}
							if (copyThisFunction) {
								//This fumction was skiped above, so do it now
								WorkAreaFunctionMembership membership = new WorkAreaFunctionMembership();
								membership.setZoneId(zoneId);
								membership.setWorkAreaId(destination.getWorkAreaId());
								membership.setWorkAreaType(destination.getWorkAreaType());
								membership.setFunctionId(fm.getFunctionId());
								membership.setMemberIds(new HashSet(fm.getMemberIds()));
								addWorkAreaFunctionMembership(membership);	
							}
						}
		        	}
				}
			}
		}
}
    public void deleteWorkAreaFunctionMemberships(Long zoneId, WorkArea workArea) {
    	getSecurityDao().deleteWorkAreaFunctionMemberships(zoneId, workArea.getWorkAreaId(), workArea.getWorkAreaType());
    }
    public void deleteWorkAreaFunctionMembership(WorkAreaFunctionMembership functionMembership) {
        getSecurityDao().delete(functionMembership);
    }

    public void updateWorkAreaFunctionMembership(WorkAreaFunctionMembership functionMembership) {
        getSecurityDao().update(functionMembership);
    }

    public WorkAreaFunctionMembership getWorkAreaFunctionMembership(Long zoneId, WorkArea workArea, Long functionId) {
        return getSecurityDao().getWorkAreaFunctionMembership
    	(zoneId, workArea.getWorkAreaId(), workArea.getWorkAreaType(), functionId);
    }

    public List<WorkAreaFunctionMembership> findWorkAreaFunctionMemberships(Long zoneId, WorkArea workArea) {
    	if (workArea == null) return new ArrayList<WorkAreaFunctionMembership>();
        return getSecurityDao().findWorkAreaFunctionMemberships
        	(zoneId, workArea.getWorkAreaId(), workArea.getWorkAreaType());
    }
    //Find workareas assigning this user to a specific operation.  
    public List<WorkAreaFunctionMembership> findWorkAreaFunctionMembershipsByOperation(Long zoneId, WorkAreaOperation workAreaOperation, Set membersToLookup) {
        return getSecurityDao().findWorkAreaByOperation(zoneId, workAreaOperation.getName(), membersToLookup);
    }
    //Find memberhships for a specific workarea that have a specified operation.
    public List<WorkAreaFunctionMembership> findWorkAreaFunctionMembershipsByOperation(Long zoneId, WorkArea workArea, WorkAreaOperation workAreaOperation) {
    	return getSecurityDao().findWorkAreaFunctionMembershipsByOperation(zoneId, workArea.getWorkAreaId(), workArea.getWorkAreaType(), workAreaOperation.getName());
    }

    public List<WorkAreaFunctionMembership> findWorkAreaFunctionMemberships(Long zoneId, WorkArea workArea, String functionScope) {
    	 List<WorkAreaFunctionMembership> membership = getSecurityDao().findWorkAreaFunctionMemberships(zoneId, workArea.getWorkAreaId(), workArea.getWorkAreaType());
    	 if(functionScope == null) 
    		 return membership;
    	 if(membership.size() == 0)
    		 return membership;
		 List<Function> functions = getSecurityDao().findFunctions(zoneId);
		 List<WorkAreaFunctionMembership> result = new ArrayList<WorkAreaFunctionMembership>();
		 for(WorkAreaFunctionMembership m:membership) {
			 for(Function f:functions) {
				 if(m.getFunctionId().equals(f.getId()) && functionScope.equals(f.getScope())) {
					 result.add(m);
					 break;
				 }
			 }
		 }
		 return result;
    }
    
    /*
    public boolean checkWorkAreaFunctionMembership(Long zoneName, WorkArea workArea, 
            Set membersToLookup, List functions) {
        // There are a number of different approaches we can take in implementing 
        // this method. This specific implementation relies on the query cache
        // and the observation that the number of different combinations of the
        // paramaters is relatively small and the most heavily sought-after 
        // results will remain in the cache. We will have to measure the 
        // efficiency of this approach to see if it is acceptable. 
        
        
        // TODO To be rewritten...
        
        List waFunctionMemberships = 
            getSecurityDao().findWorkAreaFunctionMembership
            (zoneName, workArea.getId(), functions);
        int size = waFunctionMemberships.size();
        for(int i = 0; i < size; i++) {
            Set memberIds = ((WorkAreaFunctionMembership) waFunctionMemberships.get(i)).getMemberIds();
            for(Iterator it = membersToLookup.iterator(); it.hasNext();) {
                if(memberIds.contains(it.next()))
                    return true;
            }
        }
        return false;
    }*/
    
    public boolean checkWorkAreaFunctionMembership(Long zoneId, WorkArea workArea, 
            WorkAreaOperation workAreaOperation, Set membersToLookup) {
        List<Long> functionIds = getSecurityDao().checkWorkAreaFunctionMembership
        	(zoneId, workArea.getWorkAreaId(), workArea.getWorkAreaType(), 
        	        workAreaOperation.getName(), membersToLookup);
        // If a function has conditional clauses associated with it, the conditions must
        // evaluate to true in order for the role to be effective. As long as at least
        // one of the roles meet the requirement, the access check is successful.
        for(Long functionId : functionIds) {
        	Function function = getSecurityDao().loadFunction(zoneId, functionId);
        	if (evaluateFunctionClauses(function))
        		return true;
        }
        return false;
    }

    private boolean evaluateFunctionClauses(Function function) {
    	if(!function.isConditional()) {
    		// This function is not conditional. So, this always passes.
    		return true;
    	}
    	else {
    		int metMustClauses = 0;
    		// Pass 1 - Evaluate all clauses that MUST be met.
    		for(ConditionalClause conditionalClause : function.getConditionalClauses(ConditionalClause.Meet.MUST)) {
				if(conditionalClause.getCondition().evaluate())
					metMustClauses++;
				else
					return false;
    		}
    		if(metMustClauses > 0) {
    			// There were at least one clause of MUST type, and all of the clauses of MUST type were met. 
    			// In this case, it doesn't matter whether the clauses of SHOULD type meet the condition or not.
    			// This evaluates to true.
    			return true;
    		}
    		else {
    			// There were no clause of MUST type. In this case, at least one of the clauses of SHOULD type
    			// must be met. 
    			for(ConditionalClause conditionalClause : function.getConditionalClauses(ConditionalClause.Meet.SHOULD)) {
    				if(conditionalClause.getCondition().evaluate())
    					return true;
    			}
        		// If still here, it means that none of the clauses of SHOULD type was met.
    			return false;
    		}
    	}
    }
    
    public Function getFunction(Long zoneId, Long functionId) {
    	return getSecurityDao().loadFunction(zoneId, functionId);
    }
}