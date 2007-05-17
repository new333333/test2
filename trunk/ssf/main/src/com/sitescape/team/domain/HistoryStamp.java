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
 * Created on Nov 17, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.team.domain;

import java.util.Date;
import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;


/**
 * Component class
 * This class establishes the foreign key mapping between an object and the principals
 * Its main purpose is to hide the real user object from the UI
 */
public class HistoryStamp {
    protected Date date;
    protected Principal principal;
   
    public HistoryStamp() {
        
    }
    public HistoryStamp(Principal principal, Date date) {
        setPrincipal(principal);
        setDate(date);
    }
    public HistoryStamp(Principal principal) {
        setPrincipal(principal);
        setDate(new Date());
    }
    /**
     * @hibernate.property type="timestamp" column="date"
     * @return
     */
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    /**
     * @hibernate.many-to-one class="com.sitescape.team.domain.Principal"
     * @hibernate.column name="principal"
     * @return
     */
    public Principal getPrincipal() {
        return principal;
    }
    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }
    
	public Element addChangeLog(Element parent, String name) {
		Element element = addChangeLog(parent);
		element.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, name);
		return element;
	}
	public Element addChangeLog(Element parent) {
		Element element = parent.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_HISTORYSTAMP);
		element.addAttribute(ObjectKeys.XTAG_HISTORY_BY, getPrincipal().getId().toString());
		element.addAttribute(ObjectKeys.XTAG_HISTORY_WHEN, getDate().toGMTString());
		return element;
		
	}
	public int compareDate(HistoryStamp stamp) {
		if (date == null) return -1;
		if ((stamp == null) || (stamp.getDate() == null)) return 1;
		//have to handle ourselves, cause hibernate uses 
    	//java.sql.TimeStamp and doesn't compare with Date
		if (date.getTime() < stamp.getDate().getTime()) return -1;
		if (date.getTime() > stamp.getDate().getTime()) return 1;
		return 0;
	}
}
