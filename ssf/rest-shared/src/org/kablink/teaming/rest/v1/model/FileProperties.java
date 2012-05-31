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
package org.kablink.teaming.rest.v1.model;

import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//This annotation is necessary not only for XML but also for JSON representation.
@XmlRootElement(name="file")
public class FileProperties extends BaseFileProperties {

    private EntityId owningEntity;
    private LongIdLinkPair binder;
	private String name;
	private Long lockedBy;
	private Calendar lockExpiration;
	
	public FileProperties() {
		super();
	}
	
	public FileProperties(String id, String name, HistoryStamp creation, HistoryStamp modification, 
			Long length, Integer versionNumber, Integer majorVersion, Integer minorVersion, 
			String note, Integer status, Long lockedBy, Calendar lockExpiration) {
		super(id, creation, modification, length, versionNumber, majorVersion, minorVersion, note, status);
		this.name = name;
		this.lockedBy = lockedBy;
		this.lockExpiration = lockExpiration;
	}

	public FileProperties(String id, String name, HistoryStamp creation, HistoryStamp modification, 
			Long length, Integer versionNumber, Integer majorVersion, Integer minorVersion, 
			String note, Integer status, Long lockedBy, Date lockExpiration) {
		super(id, creation, modification, length, versionNumber, majorVersion, minorVersion, note, status);
		this.name = name;
		this.lockedBy = lockedBy;
		if(lockExpiration != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(lockExpiration);
			this.lockExpiration = cal;
		}
	}

    @XmlElement(name="owning_entity")
    public EntityId getOwningEntity() {
        return owningEntity;
    }

    public void setOwningEntity(EntityId owningEntity) {
        this.owningEntity = owningEntity;
    }

    @XmlElement(name="parent_binder")
    public LongIdLinkPair getBinder() {
        return binder;
    }

    public void setBinder(LongIdLinkPair binder) {
        this.binder = binder;
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    @XmlElement(name="locked_by")
	public Long getLockedBy() {
		return lockedBy;
	}

	public void setLockedBy(Long lockedBy) {
		this.lockedBy = lockedBy;
	}

    @XmlElement(name="lock_expiration")
	public Calendar getLockExpiration() {
		return lockExpiration;
	}

	public void setLockExpiration(Calendar lockExpiration) {
		this.lockExpiration = lockExpiration;
	}
}
