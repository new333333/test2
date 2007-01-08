/*
 * Created on Nov 17, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.domain;

import java.util.Date;

import org.dom4j.Element;

import com.sitescape.ef.ObjectKeys;


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
     * @hibernate.many-to-one class="com.sitescape.ef.domain.Principal"
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
		element.addAttribute(ObjectKeys.XTAG_NAME, name);
		return element;
	}
	public Element addChangeLog(Element parent) {
		Element element = parent.addElement("historyStamp");
		element.addAttribute(ObjectKeys.XTAG_HISTORY_BY, getPrincipal().getId().toString());
		element.addAttribute(ObjectKeys.XTAG_HISTORY_WHEN, getDate().toString());
		return element;
		
	}
}
