/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
/*
 * Created on Nov 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.kablink.teaming.domain;
import org.kablink.util.Html;
import org.kablink.util.HtmlToTextParser;
import org.kablink.util.Validator;
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
     * @hibernate.property type="org.kablink.teaming.dao.util.SSClobStringType" column="text"
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
    	if (format == null) return FORMAT_HTML;
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
        return getStrippedText(false);
    }

    public String getStrippedText(boolean withNewLines) {
    	if (getFormat() != FORMAT_HTML) return getText();
        if (withNewLines) {
    	    return HtmlToTextParser.htmlToText(getText());
        } else {
            return Html.stripHtml(getText());
        }
    }

    public String getHtmlText() {
        if (getFormat() != FORMAT_NONE) return getText();
        return Html.plainTextToHTML2(getText());
    }

    public String toString() {
    	return getText();
    }
}
