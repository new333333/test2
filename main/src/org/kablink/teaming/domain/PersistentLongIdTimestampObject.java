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

import org.kablink.teaming.domain.HistoryStamp;

import javax.persistence.Transient;
import java.util.Date;

/**
 * @author janet
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class PersistentLongIdTimestampObject extends PersistentLongIdObject 
	implements PersistentLongIdTimestamp, Comparable {
    protected HistoryStamp creation;
    protected HistoryStamp modification;
    
    public PersistentLongIdTimestampObject() {
    }
    public PersistentLongIdTimestampObject(PersistentLongIdTimestampObject source) {
		 creation = new HistoryStamp(source.getCreation().getPrincipal(), source.getCreation().getDate());
		 modification = new HistoryStamp(source.getModification().getPrincipal(), source.getModification().getDate());
    }
    /**
     * @hibernate.component class="org.kablink.teaming.domain.HistoryStamp" prefix="creation_" 
     */
    public HistoryStamp getCreation() {
        return this.creation;
    }
    public void setCreation(HistoryStamp stamp) {
        this.creation = stamp;
    }
    /**
     * @hibernate.component class="org.kablink.teaming.domain.HistoryStamp" prefix="modification_"
     */
    public HistoryStamp getModification() {
        return this.modification;
    }
    public void setModification(HistoryStamp stamp) {
        this.modification = stamp;
    }

    @Transient
    public Date getModificationDate() {
        return this.modification==null ? null : this.modification.getDate();
    }
    public int compareTo(Object o) {
    	int result;
    	if (o == null) throw new NullPointerException();
    	if (!(o instanceof PersistentLongIdTimestamp)) return -1;
    	PersistentLongIdTimestamp obj = (PersistentLongIdTimestamp)o;
    	result = getCreation().compareDate(obj.getCreation());
    	if (result != 0) return result;
    	return this.getId().compareTo(obj.getId());	 
    }
 }
