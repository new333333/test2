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
package org.kablink.teaming.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kablink.teaming.util.CollectionUtil;
import org.kablink.util.StringUtil;


/**
 * @author Jong Kim
 */
public class NotificationDef  {
   
	protected List distribution;//initialized by hibernate access=field
    protected String emailAddress;//initialized by hibernate access=field
    protected boolean teamOn=false;
    protected String from,subject;
    private int style=Subscription.DIGEST_STYLE_EMAIL_NOTIFICATION;
    
    public NotificationDef() {
    }
 

    /**
     * @hibernate.property column="teamOn"
     * @return
     */
    public boolean isTeamOn() {
        return this.teamOn;
    }
    public void setTeamOn(boolean teamOn) {
        this.teamOn  = teamOn;
    }

    /**
     * 
     * @return
     */
    public List getDistribution() {
    	if (distribution == null) distribution = new ArrayList();
    	return distribution;
    }	
    public void setDistribution(Collection newDistribution) {
    	distribution = CollectionUtil.mergeAsSet(getDistribution(), newDistribution);
     }

    /**
     * @hibernate.property
     * @return
     */
    public int getStyle() {
        return style;
    }
    public void setStyle(int style) {
        this.style = style;
    }

    /**
     * @hibernate.property
     * @return
     */
    public String getEmailAddress() {
    	return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
    	this.emailAddress = emailAddress;
    }
    /**
     * @hibernate.property length="128" column="fromAddress"
     * @return
     */
    public String getFromAddress() {
    	return from;
    }
    public void setFromAddress(String from) {
    	this.from = from;
    }
    /**
     * @hibernate.property length="128" column="subject"
     * @return
     */
    public String getSubject() {
    	return subject;
    }
    public void setSubject(String subject) {
    	this.subject = subject;
    }    
}
