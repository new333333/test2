/*
 * Created on Dec 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.domain;


/**
 * @author Janet McCann
 * @hibernate.class table="SS_Notifications" dynamic-update="true" lazy="false" discriminator-value="N"
 * @hibernate.discriminator type="string" length="1" column="type"
 * 
 */
public class Notification extends PersistentObject {
    private Binder owningForum;
    private Principal principal;
    public Notification() {
    	
    }
    public Notification(Binder f, Principal p) {
    	owningForum=f;
    	principal=p;
    }
    /**
     * @hibernate.many-to-one 
     * @return
     */
    public Binder getOwningForum() {
        return owningForum;
    }
    public void setOwningForum(Binder owner)  {
        this.owningForum = owner;
    }
    /**
     * @hibernate.many-to-one
     * @return
     */
    public Principal getSendTo() {
        return principal;
    }
    public void setSendTo(Principal principal) {
        this.principal = principal;
    }
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        if ((obj == null) || (obj.getClass() != getClass()))
            return false;
        
 
        Notification o = (Notification) obj;
        //Don't use id - may not be saved yet
        if (principal.equals(o.getSendTo()) && owningForum.equals(o.getOwningForum()))
            return true;
                
        return false;
    }
    public int hashCode() {
    	int hash = 7;
    	hash = 31*hash + principal.hashCode();
    	hash = 31*hash + owningForum.hashCode();
    	return hash;
    }
  
}