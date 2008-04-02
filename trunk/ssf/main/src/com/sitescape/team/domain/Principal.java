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
/*
 * Created on Nov 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.team.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
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
public abstract class Principal extends Entry implements IPrincipal {
	//use _ to reserve names, perhaps allow customized additions later
	public static final String PRIMARY_EMAIL="_primary";
	public static final String TEXT_EMAIL="_text";
	public static final String MOBILE_EMAIL="_mobile";
	protected boolean disabled=false;
    protected String name;
    protected String foreignName="";
    protected List memberOf;//initialized by hiberate access=field
    protected Long workspaceId;
    protected List iMemberOf;
    protected String internalId;
    protected String type;
    protected String theme="";
    protected String emailAddress=null;
    private Set principalIds; // set of Long; this field is computed 
    protected Map<String,EmailAddress> emailAddresses;//initialized by hibernate access=field
    // these collections are loaded for quicker indexing, hibernate will not persist them
    protected Map iEmailAddresses;

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
     * @hibernate.property
     */
    public String getTheme() {
    	return theme;
    }
    public void setTheme(String theme) {
    	this.theme = theme;
    }
 
    /**
     * @return Returns the primary email address
     * Left here for historical purposes
     */
    public String getEmailAddress() {
        return getEmailAddress(PRIMARY_EMAIL);
    }
    public void setEmailAddress(String address) {
    	setEmailAddress(PRIMARY_EMAIL, address);
   }
    /**
     * @hibernate.element map for email address
     * @return
     */
    public Map<String, EmailAddress> getEmailAddresses() {
    	if (iEmailAddresses != null) return iEmailAddresses; //must be indexing
    	return emailAddresses;
    }
 
    public String getEmailAddress(String type) {
       	if (emailAddresses == null) return null;
       	EmailAddress a = emailAddresses.get(type);
       	return (a==null ? "":a.getAddress());
    }
    public void setEmailAddress(String type, String address) {
    	if (emailAddresses == null) emailAddresses = new HashMap();
    	if (Validator.isNull(address)) {
    		emailAddresses.remove(type);
    	} else {
    		EmailAddress a = emailAddresses.get(type);
    		if (a == null) emailAddresses.put(type, new EmailAddress(this, type, address));
    		else a.setAddress(address);
    	}
    }
     /**
     * @return Returns the mobileEmailAddress.
     */
    public String getMobileEmailAddress() {
        return getEmailAddress(MOBILE_EMAIL);
    }
    public void setMobileEmailAddress(String address) {
        setEmailAddress(MOBILE_EMAIL, address);
    }
   /**
     * @return Returns the txtEmailAddress.
     */
    public String getTxtEmailAddress() {
        return getEmailAddress(TEXT_EMAIL);
    }
    public void setTxtEmailAddress(String address) {
        setEmailAddress(TEXT_EMAIL, address);
    }
    /**
     * @hibernate.property length="128"
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
     * @hibernate.property length="128"
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
    /**
     * This method computes a complete graph of membership starting bottom up
     * from this user object, and returns their ids as a set of 
     * <code>Long</code>.
     * 
     * TODO I believe it would be more efficient to augment the DAO service to
     * fetch the entire graph eargerly rather than relying on lazy loading. 
     * This lazy loading can prove to be unacceptably costly, especially when
     * the depth of graph is large. But for now...
     * 
     * Note: This does not cache the result of the computation, since it can
     * change any time (TRUE IN THEORY BUT NOT IN PRACTICE: see below)   
     * 
     * Note: Our system architecture is such that new persistence session is
     * created and the user object is re-loaded from the database for each
     * and every request (ie, interaction between the user and the system).
     * In other words, we do not cache user object across multiple requests
     * within a user session. Consequently it is safe to cache the result
     * of this computation within the user object.
     * 
     * Note: This method may load associated groups lazily, which means that
     * this method is expected to be executed in a valid transactional context.
     * Otherwise, a data-access runtime exception may be thrown. 
     * 
     * @return
     */	public Set computePrincipalIds(GroupPrincipal reservedGroup) {
    	// Each thread serving a user request has its own copy of user object.
    	// Therefore we do not have to use synchronization around principalIds.
        if (!isActive()) return new HashSet();
        
        if(principalIds == null) {
    		Set ids = new HashSet();
    		if (reservedGroup != null) {
    			//this handles the case where all users is part of another group
    			addPrincipalIds(reservedGroup, ids);
    		}
    		addPrincipalIds(this, ids);
    		principalIds = ids;
    	}
        return principalIds;
    }
	private void addPrincipalIds(IPrincipal principal, Set ids) {
		// To prevent infinite loop resulting from possible cycle among
		// group membership, proceed only if the principal hasn't already
		// been processed. 
		if (!principal.isActive()) return;
		if(ids.add(principal.getId())) {
			List memberOf = principal.getMemberOf();
			for(Iterator i = memberOf.iterator(); i.hasNext();) {
				addPrincipalIds((IPrincipal) i.next(), ids);
	           }
	    }
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
    public void setIndexEmailAddresses(Map iEmailAddresses) {
    	this.iEmailAddresses = iEmailAddresses;
    }

 }

