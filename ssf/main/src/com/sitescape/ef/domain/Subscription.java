/*
 * Created on Dec 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.domain;


/**
 * @author Janet McCann
 * @hibernate.class table="SS_Subscriptions" dynamic-update="true" lazy="false" 
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * 
 * A subscription is similiar to a notification, except it is managed by a user.  
 * An individual chooses to be notified.  A notification is managed by the folder 
 * administrator.
 */
public class Subscription  {
     public static final int DIGEST_STYLE_EMAIL_NOTIFICATION = 1;
    public static final int MESSAGE_STYLE_EMAIL_NOTIFICATION = 2;
   
    private int style=DIGEST_STYLE_EMAIL_NOTIFICATION;
    private boolean disabled=false;
    private UserEntityPK id;
	public Subscription() {
    	
    }
    public Subscription(Long userId, EntityIdentifier entityId) {
    	setId(new UserEntityPK(userId, entityId));
    }
    public Subscription(UserEntityPK key) {
       	setId(key);
     }
    /**
	* @hibernate.composite-id
	**/
	public UserEntityPK getId() {
		return id;
	}
	public void setId(UserEntityPK id) {
		this.id = id;
	}    

    /**
     * @hibernate.property
     * @return
     */
    public int getStyle() {
        return style;
    }
    public void setStyle(int style) {
        this.style = style;
    }
    /**
     * This allows users to explicity turn off notification, which would be needed
     * if they are enabled by an admin.
     * @hibernate.property
     * @return
     */
    public boolean isDisabled() {
        return this.disabled;
    }
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        if ((obj == null) || (!(obj instanceof Subscription)))
            return false;
        
        Subscription o = (Subscription) obj;
        if (getId().equals(o.getId())) return true;
                
        return false;
    }
    public int hashCode() {
    	return getId().hashCode();
    }
  
}