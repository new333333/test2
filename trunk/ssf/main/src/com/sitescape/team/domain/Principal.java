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
/*
 * Created on Nov 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.team.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.Set;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.util.CollectionUtil;
import com.sitescape.util.Validator;
import com.sitescape.team.NotSupportedException;

/**
* @hibernate.class table="SS_Principals" dynamic-update="true" node="Principal"
* @hibernate.discriminator type="string" length="16" column="type"
* @hibernate.mapping auto-import="false"
* @hibernate.cache usage="read-write"
* need auto-import = false so names don't collide with jbpm
* @author Jong Kim
*
*/
public abstract class Principal extends Entry  {
	protected boolean disabled=false;
    protected String name;
    protected String foreignName="";
    protected List memberOf;//initialized by hiberate access=field
    protected Long zoneId;
    protected Long workspaceId;
    protected List iMemberOf;
    protected String internalId;
    protected String type;
    protected String emailAddress="";
    
     public EntityIdentifier.EntityType getEntityType() {
    	return EntityIdentifier.EntityType.valueOf(getType());
    }
    /**
     * @hibernate.property insert="false" update="false"
     *
     */
    protected String getType() {
    	return type;
    }
    protected void setType(String type) {
    	this.type = type;
    }
    /**
     * Internal id used to identify default principals.  This id plus
     * the zoneId are used to locate default principals.  If we just used the primary key id
     * the zones would need the same default and that may not be desirable.
     * @hibernate.property length="32"
     */
    public String getInternalId() {
    	return this.internalId;
    }
    public void setInternalId(String internalId) {
    	this.internalId = internalId;
    }
    public boolean isReserved() {
    	return Validator.isNotNull(internalId);
    }
    public boolean isSuper() {
    	if (!isReserved()) return false;
    	if (ObjectKeys.SUPER_USER_INTERNALID.equals(internalId)) return true;
    	if (ObjectKeys.JOB_PROCESSOR_INTERNALID.equals(internalId)) return true;
    	return false;
    }
	/**
     * @hibernate.property 
     * @return Return disabled
     */
    public boolean isDisabled() {
        return this.disabled;
    }
    public void setDisabled(boolean disabled) {
       this.disabled = disabled;
    }
    public boolean isActive() {
    	return !(disabled || deleted);
    }
    /**
     * @hibernate.property
     * Load ourselves - cause not always needed and don't want to proxy
     * @return
     */
    public Long getWorkspaceId() {
      	return workspaceId;
    }
    public void setWorkspaceId(Long workspaceId) {
       	this.workspaceId = workspaceId;         	
    }
     
    /**
     * @hibernate.property 
     */
    public Long getZoneId() {
    	return this.zoneId;
    }
    public void setZoneId(Long zoneId) {
    	this.zoneId = zoneId;
    }

 
    /**
     * @hibernate.property length="256"
     * @return Returns the emailAddress.
     */
    public String getEmailAddress() {
        return emailAddress;
    }
    /**
     * @param emailAddress The emailAddress to set.
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    /**
     * @hibernate.property length="64"
     * @return Returns the loginName.
     */
    public String getName() {
        return this.name;
    }
    /**
     * @param loginName The loginName to set.
     */
    public void setName(String name) {
    	if (Validator.isNull(name)) throw new IllegalArgumentException("null name");
        this.name = name;
    }
 
    public String getNormalTitle() {
        String val = super.getNormalTitle();
       	if (Validator.isNotNull(val)) return val;
       	return getName();		
    }
    /**
     * @hibernate.property length="256"
     * @return
     */
    public String getForeignName() {
    	return foreignName;
    }
    public void setForeignName(String foreignName) {
    	this.foreignName = foreignName;
    }
     
    public List getMemberOf() {
    	if (iMemberOf != null) return iMemberOf;  //must be indexing
    	if (memberOf == null) memberOf = new ArrayList();
    	return memberOf;
    }
   	/**
	 * Remove the current groups from the user and add new groups.
	 * This method will also add/remove the user from each group as needed.
	 * @param groups
	 */
    public void setMemberOf(Collection groups) {
   		if (memberOf == null) memberOf = new ArrayList();
		Set newM = CollectionUtil.differences(groups, memberOf);
		Set remM = CollectionUtil.differences(memberOf, groups);
		for (Iterator iter=newM.iterator(); iter.hasNext();) {
			Group g = (Group)iter.next();
			g.getMembers().add(this);
		}
		for (Iterator iter=remM.iterator(); iter.hasNext();) {
			Group g = (Group)iter.next();
			g.getMembers().remove(this);
		}
     } 	

    //overload 
    public AverageRating getAverageRating() {
   	 	return null;
    }
    public void setAverageRating(AverageRating rating) {
    	throw new NotSupportedException("setAverageRating", "principal");
    }
    //overload 
    public Long getPopularity() {
   	 	return null;
    }
    public void setPopularity(Long popularity) {
       	throw new NotSupportedException("setPopularity", "principal");
    }
 
    public String toString() {
    	return name;
    }
    /*
     * The following methods are used for performance optimization during indexing.
     * The values of each collection are loaded and built by hand.  
     * They are not persisted.  This allows us to load greater than the 
     * hibernate "batch-size" number of collections at once.
     */
    public void setIndexMemberOf(List iMemberOf) {
    	this.iMemberOf = iMemberOf;
    }
    
 }

