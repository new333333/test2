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
    private FolderEntry bookmarkEntry;
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

    /**
     * @hibernate.many-to-one	
	 */ 
    public FolderEntry getBookmarkEntry() {
        return bookmarkEntry;
    }
    public void setBookmarkEntry(FolderEntry bookmarkEntry) {
        this.bookmarkEntry = bookmarkEntry;
    }

    public void update(Object newVal) {
    	super.update(newVal);
    	Bookmark nb = (Bookmark)newVal;
    	setActive(nb.isActive());
    	setTitle(nb.getTitle());
    }
}
