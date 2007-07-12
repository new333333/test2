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
/*
 * Created on Nov 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.team.domain;
import com.sitescape.util.Validator;
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
    private Integer format=FORMAT_HTML;
    public Description() {
    }
    public Description(String text) {
        setText(text);
    }
    public Description(String text, int format) {
        setText(text);
        setFormat(format);
    }
    public Description(Description source) {
    	super();
    	if (source != null) {
    		setText(source.getText());
    		setFormat(source.getFormat());
    	}
    }
    
    
    /**
     * @hibernate.property type="com.sitescape.team.dao.util.SSClobStringType" column="text"
     */
    protected SSClobString getHDescription() {
//        if (description == null) description = new SSClobString("");
        return description;
    }
 
    protected void setHDescription(SSClobString description) {
        this.description = description;
    }

    public String getText() {
       if (description == null) return "";
       return description.getText();
    }
    public void setText(String description) {
       this.description = new SSClobString(description);      
    }
    public boolean isTag() {
    	String val = getText();
    	if (Validator.isNull(val)) return false;
    	if (val.startsWith("__")) return true;
    	return false;
    }
    // Internal routines to deal with null. Since description is an optional component, it 
    // may not be allocated so we cannot default the format value.  Hibernate will not
    // behave if a primitive (int) is null in the database.
    /**
     * @hibernate.property column="format"
     */
    protected Integer getHFormat() {
        return this.format;
    }
    protected void setHFormat(Integer format) {
        this.format = format;
    }
    public int getFormat() {
    	if (format == null) return FORMAT_NONE;
        return this.format.intValue();
    }
    public void setFormat(int format) {
        this.format = new Integer(format);
    }
    public boolean equals(Object obj1) {
    	if (obj1 == null) return false;
    	if (obj1 instanceof Description) {
    		Description desc = (Description)obj1;
    		if (getFormat() != desc.getFormat()) return false;
    		if (!getText().equals(desc.getText())) return false;
    		return true;
    	}
    	return false;
    }
    public int hashCode() {
       	int hash = getFormat();
    	hash = 31*hash + getText().hashCode();
    	return hash;
    }    
    public String getStrippedText() {
    	if (getFormat() != FORMAT_HTML) return getText();
    	return getText().replaceAll("\\<.*?\\>","");

    }
    public String toString() {
    	return getText();
    }
}
