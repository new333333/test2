/*
 * Created on Nov 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.Set;

import com.sitescape.util.Validator;
import com.sitescape.ef.util.CollectionUtil;
import com.sitescape.ef.NotSupportedException;

/**
* @hibernate.class table="SS_Principals" dynamic-update="true" node="Principal"
* @hibernate.discriminator type="string" length="1" column="type"
* @hibernate.query name="find-User-Company" query="from com.sitescape.ef.domain.User user where user.name=:userName and user.zoneName=:zoneName"
* @hibernate.mapping auto-import="false"
* @hibernate.cache usage="read-write"
* need auto-import = false so names don't collide with jbpm
* @author Jong Kim
*
*/
public abstract class Principal extends Entry  {
	protected boolean disabled=false;
    protected String name;
    protected String lcName;
    protected String foreignName="";
    protected List memberOf;//initialized by hiberate access=field
    protected String signature="";    
    protected String zoneName;
    protected Long preferredWorkspaceId;
    protected boolean reserved;
    protected List iMemberOf;
    
    /**
     * @hibernate.property
     * Reserverd principals cannot be deleted or disabled
     * @return
     */
    public boolean isReserved() {
    	return reserved;
    }
    public void setReserved(boolean reserved) {
    	this.reserved = reserved;
    }
    /**
     * @hibernate.property
     * @hibernate.column name="preferredWorkspace"
     * Load ourselves - cause not always needed and don't want to proxy
     * @return
     */
    public Long getPreferredWorkspaceId() {
      	return preferredWorkspaceId;
    }
    public void setPreferredWorkspaceId(Long ws) {
       	this.preferredWorkspaceId = ws;         	
    }
 
    
    /**
     * @hibernate.property length="100" not-null="true"
     */
    public String getZoneName() {
    	return this.zoneName;
    }
    public void setZoneName(String id) {
    	this.zoneName = id;
    }

	/**
     * @hibernate.property length="256"
	 * @return
	 */	
	public String getSignature() {
	    return this.signature;
	}
	public void setSignature(String signature) {
	    this.signature = signature;
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
    /**
     * Along with the zone this is used to force case-insensitive 
     * names to be unique in db
     * 
     * @hibernate.property length="82"
     * @return Returns the lowercase version of loginName
     */
    protected String getLcName() {
        return this.lcName;
    }

    protected void setLcName(String lcName) {
        this.lcName = lcName;
    }

    /**
     * @hibernate.property length="82"
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
    	throw new NotSupportedException();
    }
    //overload 
    public Long getPopularity() {
   	 	return null;
    }
    public void setPopularity(Long popularity) {
       	throw new NotSupportedException();
    }
 
    public String toString() {
    	return zoneName + ":" + name;
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

