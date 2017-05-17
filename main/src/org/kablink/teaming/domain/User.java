/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
/*
 * Created on Nov 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.kablink.teaming.domain;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.calendar.TimeZoneHelper;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.encrypt.EncryptUtil;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.util.Validator;

/**
 * ?
 *  
 * @author ?
 */
@SuppressWarnings("unchecked")
public class User extends UserPrincipal implements IndividualPrincipal {
	public enum ExtProvState {
		/**
		 * The external user account was created as result of a sharing activity
		 * performed by another user and an invitation has gone out.
		 * 
		 */
		initial((short)1),
		/**
		 * The invited external user responded, and through self-provisioning interface
		 * successfully supplied his credential to use with local Filr authentication
		 * in the future. The account still needs to be confirmed/verified before
		 * the user can actually log into Fir using the specified credential and
		 * start accessing data. 
		 */
		credentialed((short)2),
		/**
		 * The user has been successfully verified and is ready to use the system.
		 * Verification is needed/performed only once for each user account.
		 */
		verified((short)3),
		
		/**
		 * The user has requested to reset their password.
		 */
		pwdResetRequested( (short) 4 ),
		
		/**
		 * The user has reset their password but have not verified the reset
		 */
		pwdResetWaitingForVerification( (short) 5 );
		
		short value;
		ExtProvState(short value) {
			this.value = value;
		}
		public short getValue() {
			return value;
		}
		public static ExtProvState valueOf(short value) {
			switch(value) {
			case 1: return ExtProvState.initial;
			case 2: return ExtProvState.credentialed;
			case 3: return ExtProvState.verified;
			case 4: return ExtProvState.pwdResetRequested;
			case 5: return ExtProvState.pwdResetWaitingForVerification;
			default: throw new IllegalArgumentException("Invalid db value " + value + " for enum ExtProvState");
			}
		}
	}

	private final static int	WORK_DAY_START_DEFAULT	= 8;	// Original default was 6 in ss_calendar.js.
	
    protected String firstName="";//set by hibernate access="field"
    protected String middleName="";//set by hibernate access="field"
    protected String lastName="";//set by hibernate access="field"
    protected String organization="";
    protected String phone="";
    protected String zonName="";
    protected Locale locale;//set by hibernate access="field"
	protected TimeZone timeZone;//set by hibernate access="field"
    protected Date firstLoginDate; // This indicates the date/time at which the user logged into Vibe for the first time
    protected Date lastPasswordChange; // This indicates the date/time at which the user last changed their password
    protected String displayStyle;
    protected String password; //set by hibernate access="field"
    protected String pwdenc; // set by hibernate access="field"
    protected Long digestSeed;
    protected String skypeId="";
    protected String twitterId="";
    protected String status="";
    protected Date statusDate;
    protected Long miniBlogId;
    protected Long diskSpaceUsed;
    protected Long maxGroupsQuota;
    protected Long maxGroupsFileSizeLimit;
    private SortedSet groupNames; // sorted set of group names; this field is computed
    
    protected Short extProvState; // applicable only to external users
    protected Long extProvSeed; // applicable only to external users 
    
	protected Boolean workspacePreDeleted;
	
    private static Random random = new Random(System.currentTimeMillis());
    
    // For use by Hibernate only
	protected User() {
    }
	
	// For use by application
	public User(IdentityInfo identityInfo) {
		super(identityInfo);
	}
	
	@Override
	public EntityIdentifier.EntityType getEntityType() {
		return EntityIdentifier.EntityType.user;
	}
	
	public TimeZone getTimeZone() {
		if (timeZone != null) return TimeZoneHelper.fixTimeZone(timeZone);
		return TimeZoneHelper.getDefault();
	}
	
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = TimeZoneHelper.fixTimeZone(timeZone);
	}

   public Locale getLocale() {
       if (locale != null) return locale;
       return NLT.getTeamingLocale();
   	}
   
    public void setLocale(Locale locale) {
    	this.locale = locale;
    }
    
    public int getWeekFirstDayDefault() {
    	return new GregorianCalendar(getTimeZone(), getLocale()).getFirstDayOfWeek();
    }
    
    public int getWorkDayStartDefault() {
    	String	wdsS = NLT.get("calendar.settings.workDayStartsAt.Default",String.valueOf(WORK_DAY_START_DEFAULT));
    	int wds;
    	try {
    		wds = Integer.parseInt(wdsS);
    	}
    	catch (NumberFormatException e) {
    		wds = WORK_DAY_START_DEFAULT;
    	}
    	return wds;
    }

	@Override
	public String getTitle() {
		// title is set by hibernate access=field
		//title is only kept in the db for sql queries
		String val = super.getTitle();
    	if (Validator.isNotNull(val)) return val;
    	val = setupTitle();
    	if (Validator.isNotNull(val)) return val;
    	return getName();		
	}
	
	@Override
	public void setTitle(String title) {
		if (!isDeleted())
			throw new NotSupportedException("errorcode.notsupported.setTitle");
		//allow title to be changed when a user is deleted.
		super.setTitle(title);
	}
	
	public String getWSTitle() {
		String val = super.getTitle();
    	if (Validator.isNotNull(val)) return val + " (" + getName() + ")";
    	val = setupTitle();
    	if (Validator.isNotNull(val)) return val + " (" + getName() + ")";
    	return getName()+ " (" + getName() + ")";
 
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
	 * Returns the current display style for the user.
	 * 
	 * Note that getDisplayStyle() doesn't return this directly as it's
	 * used during object loading and if it returns something other
	 * that what's in the object, that value can get written to the
	 * database.
	 * 
	 * @return
	 */
	public String getCurrentDisplayStyle() {
		String reply = getDisplayStyle();
		if ((null == reply) || (0 == reply.length())) {
			reply = BinderHelper.getDefaultViewDisplayStyle();
		}
		return reply;
	}

	/**
	 * @hibernate.property 
	 * @return
	 */
	public Date getFirstLoginDate() {
		return firstLoginDate;
	}
	
	public void setFirstLoginDate(Date firstLoginDate) {
		this.firstLoginDate = firstLoginDate;
	}
     
	/**
	 * @hibernate.property 
	 * @return
	 */
	public Date getLastPasswordChange() {
		return lastPasswordChange;
	}
	
	public void setLastPasswordChange(Date lastPasswordChange) {
		this.lastPasswordChange = lastPasswordChange;
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
 
    public static String getNormalizedConferencingName(String zonName) {
    	if (Validator.isNull(zonName)) return zonName;
    	return zonName.replaceAll(" ", "+").toLowerCase();
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
     * @hibernate.property length="64"
     */
	public String getSkypeId() {
		return skypeId;
	}
	
	/**
	 * @param skypeId The skypeId to set.
	 */
	public void setSkypeId(String skypeId) {
		this.skypeId = skypeId;
	}

	/**
     * @hibernate.property length="64"
     */
	public String getTwitterId() {
		return twitterId;
	}
	
	/**
	 * @param twitterId The twitterId to set.
	 */
	public void setTwitterId(String twitterId) {
		this.twitterId = twitterId;
	}

	/**
     * @hibernate.property
     */
	public Long getMiniBlogId() {
		return miniBlogId;
	}
	
	/**
	 * @param miniBlogId The miniBlogId to set.
	 */
	public void setMiniBlogId(Long miniBlogId) {
		this.miniBlogId = miniBlogId;
	}
	
	/**
     * @hibernate.property
     */
	public Long getDiskSpaceUsed() {
		if (diskSpaceUsed == null) return new Long(0);
		return diskSpaceUsed;
	}
	/**
	 * @param diskUsed to set.
	 */

	public void setDiskSpaceUsed(Long diskSpaceUsed) {
		this.diskSpaceUsed = diskSpaceUsed;
	}
	
	public Long getMaxGroupsQuota() {
		if (maxGroupsQuota == null) return new Long(0);
		return maxGroupsQuota;
	}
	
	public void setMaxGroupsQuota(Long maxGroupsQuota) {
		this.maxGroupsQuota = maxGroupsQuota;
	}
	
	public Long getMaxGroupsFileSizeLimit() {
		return maxGroupsFileSizeLimit;
	}
	
	public void setMaxGroupsFileSizeLimit(Long maxGroupsFileSizeLimit) {
		this.maxGroupsFileSizeLimit = maxGroupsFileSizeLimit;
	}

	/**
	 * @param diskSpace to increment.
	 */
	public void incrementDiskSpaceUsed(Long diskSpace) {
		if(diskSpace == null || diskSpace.longValue() == 0L)
			return;
		setDiskSpaceUsed(getDiskSpaceUsed().longValue() + diskSpace.longValue());
	}
	
	/**
	 * @param diskSpace to decrement.
	 */
	public void decrementDiskSpaceUsed(Long diskSpace) {
		if(diskSpace == null || diskSpace.longValue() == 0L)
			return;
		long newValue = getDiskSpaceUsed().longValue() - diskSpace.longValue();
		// I don't know if this can ever happen, but we'd better be safe and ensure that the usage can't never go below zero.
		if(newValue < 0L) 
			newValue = 0L; 
		setDiskSpaceUsed(newValue);
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
		if(clearTextPassword != null) {
			this.password = EncryptUtil.encryptPasswordForStorage(clearTextPassword, this);
			this.pwdenc = EncryptUtil.passwordEncryptionAlgorithmForStorage(this);
		}
		else {
			this.password = null;
			this.pwdenc = null;
		}
	}
	
	public String getPwdenc() {
		return pwdenc;
	}
	
	public void setPwdenc(String pwdenc) {
		this.pwdenc = pwdenc;
	}
	
    /**
     * Returns status value or <code>null</code> if it was never set.
     * 
     * @hibernate.property length="256"
     * @return
     */
	public String getStatus() {
		return status;
	}
	
	/**
	 * @param status The status to set.
	 */
	public void setStatus(String status) {
		if (status != null && status.length() > ObjectKeys.USER_STATUS_DATABASE_FIELD_LENGTH) {
			this.status = status.substring(0, ObjectKeys.USER_STATUS_DATABASE_FIELD_LENGTH -3) + "...";
		} else {
			this.status = status;
		}
	}	
	/**
	 * @hibernate.property 
	 * @return
	 */
	public Date getStatusDate() {
		return statusDate;
	}
	
	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
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
	public Long incrementDigestSeed() {
		if(digestSeed == null) // null value is equivalent to zero
			digestSeed = new Long(1);
		else
			digestSeed = new Long(digestSeed.longValue() + 1);
		return digestSeed;
	}
	
	public Long getExtProvSeed() {
		return extProvSeed;
	}

	public void setExtProvSeed(Long extProvSeed) {
		this.extProvSeed = extProvSeed;
	}

	public Long reseedExtProvSeed() {
		setExtProvSeed(random.nextLong());
		return getExtProvSeed();
	}
	
	public String computeExtProvHash() {
		Long seed = getExtProvSeed();
		if(seed == null)
			seed = 0L;
		return EncryptUtil.encryptSHA256(getId(), getName(), seed);
	}
	
	public String getPrivateDigest(String binderId) {
		Long seed = getDigestSeed();
		if(seed == null)
			seed = 0L;
		if(binderId != null)
			return EncryptUtil.encryptSHA1(getId().toString(), seed.toString(), binderId);
		else
			return EncryptUtil.encryptSHA1(getId().toString(), seed.toString());
	}
 
    @Override
	public boolean isAllIndividualMember() {
    	if (!isReserved()) return true;
		if (ObjectKeys.GUEST_USER_INTERNALID.equals(getInternalId())) return false;
    	if (ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID.equals(getInternalId())) return false;
    	if (ObjectKeys.JOB_PROCESSOR_INTERNALID.equals(getInternalId())) return false;
    	if (ObjectKeys.SYNCHRONIZATION_AGENT_INTERNALID.equals(getInternalId())) return false;
    	if (ObjectKeys.FILE_SYNC_AGENT_INTERNALID.equals(getInternalId())) return false;
    	return true;
    }
 

    /**
     * Returns a sorted set of group names that the user is a member of
     * either directly or indirectly. 
     * 
     * @return
     */
    public SortedSet computeApplicationLevelGroupNames() {
        if (!isActive()) return new TreeSet();
        if(groupNames == null) {
    		SortedSet names = new TreeSet();
    		addApplicationLevelGroupNames(this, names);
    		if (!isShared()) {
    			if(!getIdentityInfo().isInternal())
    				names.add("allExtUsers");
    			else
    				names.add("allUsers");
    		}
    		groupNames = names;
    	}
    	return groupNames;
    }
    
    private void addApplicationLevelGroupNames(UserPrincipal principal, SortedSet names) {
        List memberOf = principal.getMemberOf();
    	for(Iterator i = memberOf.iterator(); i.hasNext();) {
    		Group group = (Group) i.next();
            if (!group.isActive()) continue;
    		if(names.add(group.getName())) {
    			addApplicationLevelGroupNames(group, names);
    		}
    	}
    }
    
    public boolean isShared() {
    	if (ObjectKeys.GUEST_USER_INTERNALID.equals(internalId)) return true;
    	return false;
    }
    
    public boolean isSuper() {
    	if (!isReserved()) return false;
    	if (ObjectKeys.SUPER_USER_INTERNALID.equals(internalId)) return true;
    	if (ObjectKeys.JOB_PROCESSOR_INTERNALID.equals(internalId)) return true;
    	return false;
    }
    
    public boolean isAdmin() {
    	if (ObjectKeys.SUPER_USER_INTERNALID.equals(internalId)) return true;
    	return false;
    }
    
	/**
	 * Returns true if user is a person and false otherwise.
	 * 
	 * @return
	 */
    public boolean isPerson() {
    	boolean reply;
    	
		// Is this a reserved user:
		if (isReserved()) {
			// Yes!  If it's guest or admin...
			String internalId = getInternalId();
			if (ObjectKeys.GUEST_USER_INTERNALID.equals(internalId) ||
				ObjectKeys.SUPER_USER_INTERNALID.equals( internalId)) {
				// ...we consider it a person...
				reply = true;
			}
			else {
				// ...otherwise, we don't.
				reply = false;
			}
		}
		else {
			// No, it's not a reserved user!  These are always
			// considered a person.
			reply = true;
		}
		
		return reply;
    }

	public ExtProvState getExtProvState() {
		if(extProvState == null)
			return null;
		else 
			return ExtProvState.valueOf(extProvState.shortValue());
	}

	public void setExtProvState(ExtProvState extProvState) {
		this.extProvState = (extProvState == null)? null : extProvState.getValue();
	}
    
    /**
     * @hibernate.property
     * @return
     */
    public Boolean isWorkspacePreDeleted() {
    	return ((null != workspacePreDeleted) && workspacePreDeleted);
    }
    
    public void setWorkspacePreDeleted(Boolean workspacePreDeleted) {
    	this.workspacePreDeleted = workspacePreDeleted;
    }
}
