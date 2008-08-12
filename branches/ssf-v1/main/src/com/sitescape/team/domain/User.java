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

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import com.sitescape.team.NotSupportedException;
import com.sitescape.team.calendar.TimeZoneHelper;
import com.sitescape.team.util.EncryptUtil;
import com.sitescape.util.Validator;

/**
 * @hibernate.subclass discriminator-value="U" dynamic-update="true" node="User"
 *
 */
public class User extends Principal {
    protected String firstName="";//set by hibernate access="field"
    protected String middleName="";//set by hibernate access="field"
    protected String lastName="";//set by hibernate access="field"
    protected String organization="";
    protected String phone="";
    protected String zonName="";
    protected Locale locale;//set by hibernate access="field"
	protected TimeZone timeZone;//set by hibernate access="field"
    protected Date loginDate;
    protected String displayStyle;
    protected String password; //set by hibernate access="field"
    protected Long digestSeed;
    
    private Set principalIds; // set of Long; this field is computed 
    private SortedSet groupNames; // sorted set of group names; this field is computed
	public User() {
    }
	public EntityIdentifier.EntityType getEntityType() {
		return EntityIdentifier.EntityType.user;
	}
	public TimeZone getTimeZone() {
		if (timeZone != null) return timeZone;
		return TimeZoneHelper.getDefault();
	}
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = TimeZoneHelper.fixTimeZone(timeZone);
	}

   public Locale getLocale() {
       if (locale != null) return locale;
       return Locale.getDefault();
   	}
    public void setLocale(Locale locale) {
    	this.locale = locale;
    }
    public void setLocale(String localeName) {
    	locale = new Locale(localeName);
    }

	public String getTitle() {
		// title is set by hibernate access=field
		//title is only kept in the db for sql queries
		String val = super.getTitle();
    	if (Validator.isNotNull(val)) return val;
    	val = setupTitle();
    	if (Validator.isNotNull(val)) return val;
    	return getName();		
	}
	public void setTitle(String title) {
		if (!isDeleted())
			throw new NotSupportedException("errorcode.notsupported.setTitle");
		//allow title to be changed when a user is deleted.
		super.setTitle(title);
	}
    private String setupTitle() {
    	String val;
    	StringBuffer tBuf = new StringBuffer();
    	val = getFirstName();
    	if (Validator.isNotNull(val)) tBuf.append(val + " ");
    	val = getMiddleName();
    	if (Validator.isNotNull(val)) tBuf.append(val + " ");
    	val = getLastName();
    	if (Validator.isNotNull(val)) tBuf.append(val + " ");
    	return tBuf.toString().trim();   	
    }
    public String getSearchTitle() {
    	//return lastname first
       	String val;
    	StringBuffer tBuf = new StringBuffer();
    	val = getLastName();
    	if (Validator.isNotNull(val)) tBuf.append(val + ", ");
    	val = getFirstName();
    	if (Validator.isNotNull(val)) tBuf.append(val + " ");
    	val = getMiddleName();
    	if (Validator.isNotNull(val)) tBuf.append(val + " ");
    	
    	if (tBuf.toString().endsWith(", ")) {
    		tBuf.delete(tBuf.length()-2, tBuf.length()-1);
    	}
    	tBuf.append("(" + getName() + ")");
    	return tBuf.toString().trim();   	
    	
    }
	/**
	 * @hibernate.property length="32" 
	 * @return
	 */
	public String getDisplayStyle() {
		return displayStyle;
	}
	public void setDisplayStyle(String displayStyle) {
		this.displayStyle = displayStyle;
	}

	/**
	 * @hibernate.property 
	 * @return
	 */
	public Date getLoginDate() {
		return loginDate;
	}
	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

     
    /**
     * @return Returns the firstName.
     */
    public String getFirstName() {
        return firstName;
    }
    /**
     * @param firstName The firstName to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        super.setTitle(setupTitle());
    }

    
    /**
     * @return Returns the lastName.
     */
    public String getLastName() {
        return lastName;
    }
    /**
     * @param lastName The lastName to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
        super.setTitle(setupTitle());
   }
 
    /**
     * @return Returns the middleName.
     */
    public String getMiddleName() {
        return middleName;
    }
    /**
     * @param middleName The middleName to set.
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
        super.setTitle(setupTitle());
    }
    

    /**
     * @hibernate.property length="256"
     * @return Returns the organization.
     */
    public String getOrganization() {
        return organization;
    }
    /**
     * @param organization The organization to set.
     */
    public void setOrganization(String organization) {
        this.organization = organization;
    }
 
    /**
     * @hibernate.property length="128"
     * @return
     */
    public String getPhone() {
        return this.phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
 
 
    /**
     * @hibernate.property length="100"
     */
	public String getZonName() {
		return zonName;
	}
	/**
	 * @param zonName The zonName to set.
	 */
	public void setZonName(String zonName) {
		this.zonName = zonName;
	}

	/**
	 * Returns encrypted password.
	 * 
	 * @hibernate.property length="64"
	 * 
	 * @return
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Sets the password.
	 * @param clearTextPassword clear text password
	 */
	public void setPassword(String clearTextPassword) {
		this.password = EncryptUtil.encryptPassword(clearTextPassword);
	}
	
    /**
     * Returns digest seed value or <code>null</code> if it was never set.
     * 
     * @hibernate.property
     * @return
     */
	public Long getDigestSeed() {
		return digestSeed;
	}
	public void setDigestSeed(Long digestSeed) {
		this.digestSeed = digestSeed;
	}	
	/**
	 * Increment digest seed value by one. Changing digest seed value results
	 * in change to the password digest value computed from password value
	 * in conjunction with digest seed value. This mechanism is used to
	 * invalidate existing RSS urls previously created for the user. 
	 *
	 */
	public void incrementDigestSeed() {
		if(digestSeed == null) // null value is equivalent to zero
			digestSeed = new Long(1);
		else
			digestSeed = new Long(digestSeed.longValue() + 1);
	}
	
	public String getPrivateDigest(String binderId) {
		
		Long digestSeed = getDigestSeed();
		if(digestSeed == null)
			digestSeed = 0L;
		
		return EncryptUtil.encryptSHA1(getId().toString(), digestSeed.toString(), binderId);
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
     */
    public Set computePrincipalIds(Long reservedGroupId) {
    	// Each thread serving a user request has its own copy of user object.
    	// Therefore we do not have to use synchronization around principalIds.
        if (!isActive()) return new HashSet();
        
        if(principalIds == null) {
    		Set ids = new HashSet();
    		ids.add(reservedGroupId);
    		addPrincipalIds(this, ids);
    		principalIds = ids;
    	}
        return principalIds;
    }
    
    private void addPrincipalIds(Principal principal, Set ids) {
        // To prevent infinite loop resulting from possible cycle among
        // group membership, proceed only if the principal hasn't already
        // been processed. 
        if (!principal.isActive()) return;
        if(ids.add(principal.getId())) {
            List memberOf = principal.getMemberOf();
            for(Iterator i = memberOf.iterator(); i.hasNext();) {
                addPrincipalIds((Principal) i.next(), ids);
            }
        }
    }
    
    /**
     * Returns a sorted set of group names that the user is a member of
     * either directly or indirectly. 
     * 
     * @return
     */
    public SortedSet computeGroupNames() {
        if (!isActive()) return new TreeSet();
        if(groupNames == null) {
    		SortedSet names = new TreeSet();
    		addGroupNames(this, names);
    		names.add("allUsers");
    		groupNames = names;
    	}
    	return groupNames;
    }
    
    private void addGroupNames(Principal principal, SortedSet names) {
        List memberOf = principal.getMemberOf();
    	for(Iterator i = memberOf.iterator(); i.hasNext();) {
    		Group group = (Group) i.next();
            if (!group.isActive()) continue;
    		if(names.add(group.getName())) {
    			addGroupNames(group, names);
    		}
    	}
    }
}
