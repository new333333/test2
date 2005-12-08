/*
 * Created on Sep 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.domain;


/**
 * @hibernate.class table="SS_Attachments" discriminator-value="N" dynamic-update="true" lazy="false"
 * @hibernate.discriminator column="type" type="char"  
 * @author janet
 */
public abstract class Attachment extends PersistentTimestampObject 
	implements UpdateAttributeSupport {
 
    protected AnyOwner owner;
    protected String usage="";
    protected String name;
    public Attachment() {
    }
    /**
     * @hibernate.component class="com.sitescape.ef.domain.AnyOwner"
     * @return
     */
    public AnyOwner getOwner() {
    	return owner;
    }
    public void setOwner(AnyOwner owner) {
    	this.owner = owner;
    } 
 	public void setOwner(Entry entry) {
  		owner = new AnyOwner(entry);
  	}
    /**
     * @hibernate.property length="64"
     * Used to tie event to command fields
     * @return
     */
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    } 	

    public void update(Object obj) {
    	
    }
}
