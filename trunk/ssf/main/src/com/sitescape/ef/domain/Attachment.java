/*
 * Created on Sep 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.domain;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;


/**
 * @hibernate.class table="SS_Attachments" discriminator-value="N" dynamic-update="true" lazy="false"
 * @hibernate.discriminator column="type" type="char"  
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * @author janet
 */
public abstract class Attachment extends PersistentTimestampObject 
	implements UpdateAttributeSupport {
 
    protected AnyOwner owner;
    protected String name;
    protected char type;
    public Attachment() {
    }
    public Attachment(String name) {
    	this.name = name;
    }
    /**
     * @hibernate.property insert="false" update="false"
     * use in queries
     *
     */
    protected char getType() {
    	return type;
    }
    protected void setType(char type) {
    	this.type = type;
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
 	public void setOwner(DefinableEntity entity) {
  		owner = new AnyOwner(entity);
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

    public boolean update(Object obj) {
    	return true;
    	
    }
    public abstract Element addChangeLog(Element parent); 
}
