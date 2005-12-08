/*
 * Created on Oct 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.domain;

import com.sitescape.util.Validator;

/**
 * @hibernate.subclass discriminator-value="U" dynamic-update="true"
 * @author janet
 */
public class URLAttachment extends Attachment{
    private String url;
    
    public URLAttachment() {
    }
    
    /**
     * Share column with bookmark
     * @hibernate.property length="256" 
     * @hibernate.column name="title"
     */
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
    	if (Validator.isNull(url)) throw new IllegalArgumentException("null url");
        this.url = url;
    }
    public boolean equals(Object obj) {
 
        URLAttachment o = (URLAttachment) obj;
        //Don't use id - may not be saved yet
        if (url.equals(o.getUrl()))  return true;

        return false;
    }
    public int hashCode() {
    	return  31*super.hashCode() + url.hashCode();
    }   
}
