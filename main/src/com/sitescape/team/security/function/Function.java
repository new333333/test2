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
package com.sitescape.team.security.function;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.sitescape.team.domain.ZonedObject;
import com.sitescape.team.util.CollectionUtil;

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
