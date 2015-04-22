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

import java.util.Set;

import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.ZonedObject;
import org.kablink.teaming.module.shared.XmlUtils;
import org.kablink.teaming.util.LongIdUtil;


/**
 * @hibernate.class table="SS_WorkAreaFunctionMemberships" lazy="false" dynamic-update="true"
 * @hibernate.cache usage="nonstrict-read-write"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * 
 * <code>FunctionMembership</code> defines the members of a function for 
 * a work area.
 * 
 * @author Jong Kim
 */
public class WorkAreaFunctionMembership extends ZonedObject {
       
    private Long id;
    private Long workAreaId;
    private String workAreaType;
    private Long functionId;
    private Set<Long> memberIds; // A set of Long
    
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
     * @hibernate.version type="long"
     */
    private long getLockVersion() {
        return this.lockVersion;
    }
    private void setLockVersion(long lockVersion) {
        this.lockVersion = lockVersion;
    }
    
    /**
     * @hibernate.set lazy="false" table="SS_WorkAreaFunctionMembers" cascade="all"
     * @hibernate.key column="workAreaFunctionMembershipId"
     * @hibernate.element type="long" column="memberId" not-null="true"
     * @hibernate.cache usage="nonstrict-read-write"
     */
    public Set<Long> getMemberIds() {
        return memberIds;
    }
    public void setMemberIds(Set<Long> memberIds) {
        this.memberIds = memberIds;
    }
    
	/**
	 * @hibernate.property column="workAreaId" type="long" not-null="true"
	 */    
    public Long getWorkAreaId() {
        return workAreaId;
    }
    public void setWorkAreaId(Long workAreaId) {
        this.workAreaId = workAreaId;
    }
    
    /**
     * @hibernate.property column="workAreaType" length="16" not-null="true"
     * 
     * @return
     */
    public String getWorkAreaType() {
        return workAreaType;
    }
    public void setWorkAreaType(String workAreaType) {
        this.workAreaType = workAreaType;
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
     * @hibernate.property not-null="true"
     * 
     * @return
     */
    public Long getFunctionId() {
        return functionId;
    }
    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }
    
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        //objects can be proxied so don't compare classes.
        if (obj == null)
            return false;
      
        WorkAreaFunctionMembership o = (WorkAreaFunctionMembership) obj;
        // Don't bring surrogate key value (id) into consideration.
        // Use business key only (which is a combination of zone name + 
        // work area id + work area type + function id). 
        if (!o.getZoneId().equals(zoneId)) return false;               
        if (!o.getWorkAreaId().equals(workAreaId)) return false;               
        if (!o.getWorkAreaType().equals(workAreaType)) return false;               
        if (!o.getFunctionId().equals(functionId)) return false;               
        
        return true;
    }
    
    public int hashCode() {
       	int hash = 7;
    	hash = 31*hash + zoneId.hashCode();
    	hash = 31*hash + workAreaId.hashCode();
    	hash = 31*hash + workAreaType.hashCode();
    	hash = 31*hash + functionId.hashCode();
    	return hash;
    }
    public Element addChangeLog(Element parent) {
		Element element = parent.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_FUNCTION_MEMBERSHIP);
		element.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_DATABASEID, getId().toString());
		
		XmlUtils.addProperty(element, ObjectKeys.XTAG_WA_FUNCTION, getFunctionId());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_WA_MEMBERS, LongIdUtil.getIdsAsString(getMemberIds()));
		return element;
    	
    }
    public void toXml(Element parent) {
     	addChangeLog(parent);
    }
}
