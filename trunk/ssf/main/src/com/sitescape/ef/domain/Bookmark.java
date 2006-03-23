/*
 * Created on Dec 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.domain;


/**
 * @hibernate.subclass discriminator-value="B" dynamic-update="true"  
 * to a user.
 * @author janet
 */
public class Bookmark extends Attachment {
    private boolean active=true;
    private String title="";
    public Bookmark () {
    }
    /**
     * @hibernate.property
     */
    public boolean isActive() {
    	return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
   
    /**
     * @hibernate.property length="256"
     */
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public boolean update(Object newVal) {
    	boolean changed = super.update(newVal);
    	Bookmark nb = (Bookmark)newVal;
    	if (isActive() != nb.isActive()) {
        	setActive(nb.isActive());
        	changed = true;
    	}
    	if (!getTitle().equals(nb.getTitle())) {
    		setTitle(nb.getTitle());
    		changed=true;
    	}
    	return changed;
    }
}
