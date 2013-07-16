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
package org.kablink.teaming.security.function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.ZonedObject;
import org.kablink.teaming.util.CollectionUtil;
import org.kablink.util.Validator;


/**
 * @hibernate.class table="SS_Functions" dynamic-update="true" lazy="false"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * @hibernate.cache usage="nonstrict-read-write"
 * 
 * <code>Function</code> is a role defined at the zone level.
 * 
 * @author Jong Kim
 */
public class Function extends ZonedObject {
    
    private Long id;
    private String name;
    private Set operations; // A set of WorkSpaceOperation - this is not persistent
    private Set operationNames; // Used for persistence only
    private long lockVersion; // Used for optimistic locking support
    private String internalId;
    private String scope; //Used to segment roles between zone, binder and entry
    private boolean zoneWide=false;
    private List<ConditionalClause> conditionalClauses;
	/**
	 * @hibernate.id generator-class="native" type="long"  unsaved-value="null"
	 */    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    /**
     * Internal id used to identify reserved functions.  
     * @hibernate.property length="32"
     */
    public String getInternalId() {
    	return this.internalId;
    }
    public void setInternalId(String internalId) {
    	this.internalId = internalId;
    }
    /**
     * Return true if the function is a 'system function'
     * @return
     */
    public boolean isReserved() {
    	return Validator.isNotNull(internalId);
    }
    public boolean isZoneWide() {
    	return zoneWide;
    }
    public void setZoneWide(boolean zoneWide) {
    	this.zoneWide = zoneWide;
    }
	/**
	 * @hibernate.property not-null="true"
	 */    
    public Long getZoneId() {
        return zoneId;
    }
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * @hibernate.property length="128" not-null="true" 
     */
    public String getName() {
        return name;
    }
    public void setName(String name) {
        if(name == null)
            throw new IllegalArgumentException("Name must not be null");
        
        if(!StringUtils.isAlphanumericSpace(name.replaceAll("_", "").replaceAll("\\.", "")))
            throw new IllegalArgumentException("Role must contain only alphanumeric characters." +
				" They include letters, numbers, spaces, underscores, and periods.");
        
        this.name = name;
    }

    /**
     * @hibernate.property length="16" not-null="false" 
     */
    public String getScope() {
    	if (scope == null) {
    		if (this.isZoneWide()) {
    			scope = ObjectKeys.ROLE_TYPE_ZONE;
    		} else {
    			scope = ObjectKeys.ROLE_TYPE_BINDER;
    		}
    	}
        return scope;
    }
    public void setScope(String scope) {
        if (!scope.equals(ObjectKeys.ROLE_TYPE_ZONE) &&
        		!scope.equals(ObjectKeys.ROLE_TYPE_BINDER) && 
        		!scope.equals(ObjectKeys.ROLE_TYPE_ENTRY) && 
        		!scope.equals(ObjectKeys.ROLE_TYPE_FILR))
            throw new IllegalArgumentException("Type must be 'zone', 'binder', 'entry' or 'filr'.");
        
        this.scope = scope;
    }

    public Set getOperations() {
        if(operations == null)
            computeOperations();
        return operations;
    }
    
    public void setOperations(Set operations) {
        if(operations == null)
            throw new IllegalArgumentException("Operations must not be null");
        
        this.operations = operations;
        computeOperationNames();
    }
    
    public void addOperation(WorkAreaOperation operation) {
        getOperations().add(operation);
        computeOperationNames();
    }
    
    public void removeOperation(WorkAreaOperation operation) {
        getOperations().remove(operation);
        computeOperationNames();
   }
    
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        //objects can be proxied so don't compare classes.
        if (obj == null)
            return false;
      
        Function o = (Function) obj;
        //assume object not persisted yet
        if (!o.getName().equals(name)) return false;
        if (!o.getZoneId().equals(zoneId)) return false;               
        return true;
    }
    public int hashCode() {
       	int hash = 7;
    	hash = 31*hash + name.hashCode();
    	hash = 31*hash + zoneId.hashCode();
    	return hash;
    }
    
    /*
     * Returns a list of <code>ConditionalClause</code> ordered by condition ids.
     */
	public List<ConditionalClause> getConditionalClauses() {
		// We have no requirement that this list be stored sorted in the database. 
		// We only need to make sure that the caller always gets this list sorted.
		if(conditionalClauses == null)
			return new ArrayList<ConditionalClause>();
		// Return a copy of the list to keep the caller from making direct changes to the original list.
		// The returned copy must have the members sorted in ascending order of the IDs of the conditions.
		return sortByConditionId(new ArrayList<ConditionalClause>(conditionalClauses));
	}
	
	public void setConditionalClauses(List<ConditionalClause> conditionalClauses) {
		this.conditionalClauses = conditionalClauses;
	}
	
	/*
	 * Returns a list of <code>ConditionalClause</code> of specified type ordered by condition ids.
	 */
	public List<ConditionalClause> getConditionalClauses(ConditionalClause.Meet meet) {
		if(conditionalClauses == null)
			return new ArrayList<ConditionalClause>();
		List<ConditionalClause> list = new ArrayList<ConditionalClause>();
		for(ConditionalClause cc : conditionalClauses) {
			if(cc.getMeet() == meet)
				list.add(cc);
		}
		return sortByConditionId(list);
	}

	/*
	 * Returns a sorted list of IDs of the conditions of specified type.
	 */
	public List<Long> getConditionIds(ConditionalClause.Meet meet) {
		if(conditionalClauses == null)
			return new ArrayList<Long>();
		List<Long> list = new ArrayList<Long>();
		for(ConditionalClause cc : conditionalClauses) {
			if(cc.getMeet() == meet)
				list.add(cc.getCondition().getId());
		}
		Collections.sort(list);
		return list;
	}

	
	public boolean isConditional() {
		if(conditionalClauses != null && conditionalClauses.size() > 0)
			return true;
		else
			return false;
	}

    /**
     * @hibernate.version type="long" 
     */
    private long getLockVersion() {
        return this.lockVersion;
    }
    private void setLockVersion(long lockVersion) {
        this.lockVersion = lockVersion;
    }

    /**
     * @hibernate.set lazy="false" table="SS_FunctionOperations" cascade="all"
     * @hibernate.key column="functionId"
     * @hibernate.element type="string" column="operationName" length="128" not-null="true"
     * @hibernate.cache usage="nonstrict-read-write"
     * 
     */
    private Set getOperationNames() {
        if(operationNames == null)
            computeOperationNames();
        
        return operationNames;
    }
    
    private void setOperationNames(Set operationNames) {
        if(operationNames == null)
            throw new IllegalArgumentException("Operation names must not be null");

        this.operationNames = operationNames;
        this.operations = null;
    }
    
    private void computeOperations() {
        operations = new HashSet();
        
        if(operationNames != null) {
            for(Iterator it = operationNames.iterator(); it.hasNext();) {
                String operationName = (String) it.next();
                operations.add(WorkAreaOperation.getInstance(operationName));
            }
        }
    }
    
    private void computeOperationNames() {
    	//If just replace the set, hibernate does a delete and re-add
    	// So only change the members that we need to
    	if (operationNames == null) operationNames = new HashSet();
        if (operations != null) {
        	Set newNames = new HashSet();
            for(Iterator it = operations.iterator(); it.hasNext();) {
                WorkAreaOperation operation = (WorkAreaOperation) it.next();
                newNames.add(operation.getName());
            }
      
            Set newM = CollectionUtil.differences(newNames, operationNames);
            Set remM = CollectionUtil.differences(operationNames, newNames);
            this.operationNames.addAll(newM);
            this.operationNames.removeAll(remM);
        }
        
    }

	private List<ConditionalClause> sortByConditionId(List<ConditionalClause> list) {
		Collections.sort(list, 
				new Comparator<ConditionalClause>() {
			public int compare(ConditionalClause cc1, ConditionalClause cc2) {
				Long id1 = cc1.getCondition().getId();
				Long id2 = cc2.getCondition().getId();
				if(id1 == null)
					return -1;
				if(id2 == null)
					return 1;
				return id1.compareTo(id2);
			}
		});
		return list;
	}

}
