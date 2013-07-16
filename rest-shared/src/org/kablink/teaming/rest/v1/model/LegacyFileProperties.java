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

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Calendar;

//This annotation is necessary not only for XML but also for JSON representation.
@XmlRootElement(name="legacy_file")
public class LegacyFileProperties {

    private String id;
   	private LegacyHistoryStamp creation;
   	private LegacyHistoryStamp modification;
   	private Long length; // in bytes
   	private Integer versionNumber;
   	private Integer majorVersion;
   	private Integer minorVersion;
   	private String note; // used also for update
   	private Integer status; // used also for update
   	private String webUrl;
    private Long entryId;
    private Long binderId;
	private String name;
	private Long lockedBy;
	private Calendar lockExpiration;

	public LegacyFileProperties() {

    }

	public LegacyFileProperties(FileProperties fp) {
        id = fp.getId();
        creation = new LegacyHistoryStamp(fp.getCreation());
        modification = new LegacyHistoryStamp(fp.getModification());
        length = fp.getLength();
        versionNumber = fp.getVersionNumber();
        majorVersion = fp.getMajorVersion();
        minorVersion = fp.getMinorVersion();
        note = fp.getNote();
        status = fp.getStatus();
        if (fp.getOwningEntity()!=null) {
            entryId = fp.getOwningEntity().getId();
        }
        if (fp.getBinder()!=null) {
            binderId = fp.getBinder().getId();
        }
        name = fp.getName();
        lockedBy = fp.getLockedBy();
        lockExpiration = fp.getLockExpiration();
	}

    public LegacyHistoryStamp getCreation() {
        return creation;
    }

    public void setCreation(LegacyHistoryStamp creation) {
        this.creation = creation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public Integer getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(Integer majorVersion) {
        this.majorVersion = majorVersion;
    }

    public Integer getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(Integer minorVersion) {
        this.minorVersion = minorVersion;
    }

    public LegacyHistoryStamp getModification() {
        return modification;
    }

    public void setModification(LegacyHistoryStamp modification) {
        this.modification = modification;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }

    public Long getBinderId() {
        return binderId;
    }

    public void setBinderId(Long binderId) {
        this.binderId = binderId;
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getLockedBy() {
		return lockedBy;
	}

	public void setLockedBy(Long lockedBy) {
		this.lockedBy = lockedBy;
	}

	public Calendar getLockExpiration() {
		return lockExpiration;
	}

	public void setLockExpiration(Calendar lockExpiration) {
		this.lockExpiration = lockExpiration;
	}
}
