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
 * Created on Nov 17, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.kablink.teaming.domain;

import java.util.Date;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;



/**
 * Component class
 * This class establishes the foreign key mapping between an object and the principals
 * Its main purpose is to hide the real user object from the UI
 */
public class HistoryStamp {
    protected Date date;
    protected UserPrincipal principal;
   
    public HistoryStamp() {
        
    }
    public HistoryStamp(UserPrincipal principal, Date date) {
        setPrincipal(principal);
        setDate(date);
    }
    public HistoryStamp(UserPrincipal principal) {
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
     * @hibernate.many-to-one class="org.kablink.teaming.domain.Principal"
     * @hibernate.column name="principal"
     * @return
     */
    public UserPrincipal getPrincipal() {
        return principal;
    }
    public void setPrincipal(UserPrincipal principal) {
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
