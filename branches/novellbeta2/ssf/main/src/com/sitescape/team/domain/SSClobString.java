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
 * Created on Nov 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.team.domain;
import java.sql.Clob;
import java.sql.SQLException;
import java.io.Serializable;
/**
 * Hide a clob string and lazy load the value.
 * This is an immutable object.  Its value cannot be changed.  This allows
 * lazy value loading to work.  If not, the deepcopy method of a user type, would have
 * to read the value for later comparision.
 * 
 * Create new instancs to change values 
 */
public class SSClobString implements Serializable {
    transient Clob clob;
    String value=null;
    
    public SSClobString(Clob clob) {
        this.clob = clob;
    }
    public SSClobString(String value) {
        this.value = value;
    }
    
    public String getText() {
        try {
            if (value != null) return value;
            if (clob == null) {return "";}
 
            value = clob.getSubString(1, (int) clob.length());
            return value;
        } catch (SQLException ex) {
            return "";
        }

    }
    public String toString() {
        return getText();
    }
}

