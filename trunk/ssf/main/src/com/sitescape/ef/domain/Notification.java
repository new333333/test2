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
    private Binder binder;
    private Principal principal;
    private String type;
    public Notification() {
    	
    }
    public Notification(Binder b, Principal p) {
    	binder=b;
    	principal=p;
    }
    /**
     * @hibernate.property length="1" insert="false" update="false"
     *
     */
    public String getType() {
    	return type;
    }
    public void setType(String type) {
    	this.type = type;
    }
   /**
     * @hibernate.many-to-one 
     * @return
     */
    public Binder getBinder() {
        return binder;
    }
    public void setBinder(Binder owner)  {
        this.binder = owner;
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
        if (principal.equals(o.getSendTo()) && binder.equals(o.getBinder()))
            return true;
                
        return false;
    }
    public int hashCode() {
    	int hash = 7;
    	hash = 31*hash + principal.hashCode();
    	hash = 31*hash + binder.hashCode();
    	return hash;
    }
  
}