/*
 * Created on Nov 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import com.sitescape.ef.domain.SSClobString;
import com.sitescape.util.Validator;


/**
 * @hibernate.subclass discriminator-value="U" dynamic-update="true" node="User"
 *
 */
public class User extends Principal {
    protected String languageId="en";
    protected String country="US";
    protected String firstName="";
    protected String middleName="";
    protected String lastName="";
    protected String emailAddress="";
    protected String homepage="";
    protected String webPubDir="";
    protected String organization="";
    protected String phone="";
    protected Folder calendar;
    protected Workspace preferredWorkspace;
    protected String zonName="";
    protected Locale locale;
	protected TimeZone timeZone;
	protected String timeZoneName;
    protected Date loginDate;
    protected String displayStyle;
	public User() {
    }
	public TimeZone getTimeZone() {
		if (timeZone != null) return timeZone;
		if ((timeZoneName == null) || timeZoneName.length() ==0) {
			timeZone = TimeZone.getDefault();
		} else {
			timeZone = TimeZone.getTimeZone(timeZoneName);
		}
		return timeZone;
	}
    public String getTitle() {
    	String title = super.getTitle();
    	if (!Validator.isNull(title)) return title;
    	StringBuffer tBuf = new StringBuffer();
    	title = getFirstName();
    	if (!Validator.isNull(title)) tBuf.append(title + " ");
    	title = getMiddleName();
    	if (!Validator.isNull(title)) tBuf.append(title + " ");
    	title = getLastName();
    	if (!Validator.isNull(title)) tBuf.append(title + " ");
    	title = tBuf.toString().trim();
    	if (!Validator.isNull(title)) return title;
    	return getName();
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
	 * @hibernate.property length="10"
	 * @param timeZoneName
	 */
	public String getTimeZoneName() {
		return timeZoneName;
	} 

	public void setTimeZoneName(String timeZoneName) {
		this.timeZoneName = timeZoneName;
		timeZone = null;
	}

    /**
     * @hibernate.property length="2" 
     * @return
     */
    public String getLanguageId() {
        return this.languageId;
    }
    public void setLanguageId(String languageId) {
        this.languageId = languageId;
        locale = null;
    }
    /**
     * @hibernate.property length="2"
     * @return
     */
    public String getCountry() {
        return this.country;
    }
    public void setCountry(String country) {
        this.country = country;
        locale = null;
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
    }

    
    /**
     * @hibernate.property length="64"
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
    }
 
    /**
     * @hibernate.property length="64"
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
    }
    
    /**
     * @hibernate.property length="256"
     * @return
     */
    public String getHomepage() {
        return this.homepage;
    }
    public void setHomepage(String homepage) {
        this.homepage = homepage;
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
     * @hibernate.property length="256"
     * @return
     */
    public String getWebPubDir() {
        return this.webPubDir;
    }
    public void setWebPubDir(String webPubDir) {
        this.webPubDir = webPubDir;
    }
 
    /**
     * @hibernate.many-to-one class="com.sitescape.ef.domain.Folder" node="Calendar" embed-xml="false"
     * @hibernate.column name="calender"
     * @return
     */
    public Folder getCalendar() {
        return this.calendar;
    }
    public void setCalendar(Folder calendar) {
        this.calendar = calendar;
    }

    /**
     * @hibernate.property length="100" not-null="true"
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

    public Locale getLocale() {
        if (locale != null) return locale;
        if(languageId == null) {
        	locale = Locale.getDefault();
        	return locale;
        }
        else {
            if(country == null)
                return new Locale(languageId);
            else
                return new Locale(languageId, country);
        }
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
     * change any time.    
     * 
     * Note: This method may load associated groups lazily, which means that
     * this method is expected to be executed in a valid transactional context.
     * Otherwise, a data-access runtime exception may be thrown. 
     * 
     * @return
     */
    public Set computePrincipalIds() {
        Set ids = new HashSet();
        addPrincipalIds(this, ids);
        return ids;
    }
    
    private void addPrincipalIds(Principal principal, Set ids) {
        // To prevent infinite loop resulting from possible cycle among
        // group membership, proceed only if the principal hasn't already
        // been processed. 
        
        if(ids.add(principal.getId())) {
            List memberOf = principal.getMemberOf();
            for(Iterator i = memberOf.iterator(); i.hasNext();) {
                addPrincipalIds((Principal) i.next(), ids);
            }
        }
    }
}
