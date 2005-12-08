/*
 * Created on Nov 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.domain;

/**
 * @author janet
 *
 * Persistent compenent class
 */
public class Description {
    public static final int FORMAT_HTML = 1;
    public static final int FORMAT_NONE = 2;
    public static final int FORMAT_AUTOMATIC = 3;

    private SSClobString description;
    private Integer format;
    public Description() {
    }
    public Description(String text) {
        setText(text);
        setFormat(FORMAT_NONE);
    }
    public Description(String text, int format) {
        setText(text);
        setFormat(format);
    }
    
    
    /**
     * @hibernate.property type="com.sitescape.ef.dao.util.SSClobStringType" column="text"
     */
    protected SSClobString getHdescription() {
        if (description == null) description = new SSClobString("");
        return description;
    }
 
    protected void setHdescription(SSClobString description) {
        this.description = description;
    }

    public String getText() {
       if (description == null) return "";
       return description.getText();
    }
    public void setText(String description) {
       this.description = new SSClobString(description);      
    }
    // Internal routines to deal with null. Since description is an optional component, it 
    // may not be allocated so we cannot default the format value.  Hibernate will not
    // behave if a primitive (int) is null in the database.
    /**
     * @hibernate.property column="format"
     */
    protected Integer getHformat() {
        return this.format;
    }
    protected void setHformat(Integer format) {
        this.format = format;
    }
    public int getFormat() {
    	if (format == null) return FORMAT_NONE;
        return this.format.intValue();
    }
    public void setFormat(int format) {
        this.format = new Integer(format);
    }
}
