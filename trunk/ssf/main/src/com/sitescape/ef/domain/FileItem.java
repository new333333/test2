package com.sitescape.ef.domain;

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
}
