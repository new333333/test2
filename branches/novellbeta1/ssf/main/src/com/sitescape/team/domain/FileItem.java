/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.domain;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.util.Validator;

/**
 * This is used as a component class of FileAttachment
 * May also be used independently, but does not have
 * any persistent characteristics in that case.
 * @author Jong Kim
 *
 */
public class FileItem  {
    private String name;
    private long length=0;
   
    /**
     * This method name might be a bit misleading because it returns not just
     * name portion of the file but its pathname as well if it exists. 
     * (eg. /abc/xyz/foo.txt).  
     * 
     * Implementation Note: Given the fact that the path could be deep,
     * is 256 characters enough to store them?
     * 
     * @hibernate.property length="256" column="fileName"
     * @return
     */
    public String getName() {
        return name;
    }
    public void setName(String name) {
    	if (Validator.isNull(name)) throw new IllegalArgumentException("null name");
       this.name = name;
    }
    /**
     * @hibernate.property column="fileLength"
     * @return size in bytes
     */
    public long getLength() {
        return this.length;
    }
    public void setLength(long length) {
        this.length = length;
    }
    
    /*
     * Convience routines
     * @return
     */
    public long getLengthKB() {
        return (this.length + 999)/1000;
    }
    public boolean equals(Object obj) {
   	 
    	if (obj == null) return false;
    	if (obj instanceof FileItem) {
    		FileItem o = (FileItem) obj;
    		if (name.equals(o.getName()))  return true;
    	}
    	return false;
    }
    public int hashCode() {
       	return  name.hashCode();
    }
    public String toString() {
    	return name;
    }
 
}
