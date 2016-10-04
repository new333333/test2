/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.kablink.teaming.rest.v1.annotations.Undocumented;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Base class for file objects.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public abstract class BaseFileProperties extends SearchableObject {

	private String id;
	private HistoryStamp creation;
	private HistoryStamp modification;
	private Long length; // in bytes
    private String md5;
	private Integer versionNumber;
	private Integer majorVersion;
	private Integer minorVersion;
	private String note; // used also for update
	private Integer status; // used also for update

	protected BaseFileProperties() {
        setDocType("file");
    }

    protected BaseFileProperties(BaseFileProperties orig) {
        super(orig);
        this.id = orig.id;
        this.creation = orig.creation;
        this.modification = orig.modification;
        this.length = orig.length;
        this.md5 = orig.md5;
        this.versionNumber = orig.versionNumber;
        this.majorVersion = orig.majorVersion;
        this.minorVersion = orig.minorVersion;
        this.note = orig.note;
        this.status = orig.status;
    }

	/**
	 * The ID of the file.
     */
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * The date and time when the file was created.
     */
	public HistoryStamp getCreation() {
		return creation;
	}

	public void setCreation(HistoryStamp creation) {
		this.creation = creation;
	}

	/**
	 * The datae and time when the file was last modified.
     */
	public HistoryStamp getModification() {
		return modification;
	}

    @XmlTransient
    public Date getModificationDate() {
        return this.modification==null ? null : this.modification.getDate().getTime();
    }

    public void setModification(HistoryStamp modification) {
		this.modification = modification;
	}

	/**
	 * The file size in bytes.
     */
	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	/**
	 * MD5 checksum.
     */
    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

	/**
	 * File version number.  This is incremented each time the file is modified.
     */
    @XmlElement(name="version_number")
	public Integer getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(Integer versionNumber) {
		this.versionNumber = versionNumber;
	}

	@Undocumented
    @XmlElement(name="major_version")
	public Integer getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(Integer majorVersion) {
		this.majorVersion = majorVersion;
	}

	@Undocumented
    @XmlElement(name="minor_version")
	public Integer getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(Integer minorVersion) {
		this.minorVersion = minorVersion;
	}

	@Undocumented
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Undocumented
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public Calendar getCreateDate() {
		HistoryStamp stamp = getCreation();
		if (stamp!=null) {
			return stamp.getDate();
		} else {
			return new GregorianCalendar(1970, 0, 0);
		}
	}
}
