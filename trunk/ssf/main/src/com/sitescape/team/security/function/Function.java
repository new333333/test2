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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.sitescape.team.domain.Principal;
import com.sitescape.team.util.CollectionUtil;
import com.sitescape.team.util.NLT;
import com.sitescape.util.Validator;
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
public class Function {
    
    private Long id;
    private Long zoneId;
    private String name;
    private Set operations; // A set of WorkSpaceOperation - this is not persistent
    private Set operationNames; // Used for persistence only
    private long lockVersion; // Used for optimistic locking support
    
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
     * @hibernate.version type="long" 
     */
    private long getLockVersion() {
        return this.lockVersion;
    }
    private void setLockVersion(long lockVersion) {
        this.lockVersion = lockVersion;
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
    }
    
    public void removeOperation(WorkAreaOperation operation) {
        getOperations().remove(operation);
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
}
