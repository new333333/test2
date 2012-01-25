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

/**
 * @author jong
 *
 */
public abstract class FileCommonProperties {

	private String id;
	private HistoryStamp creation;
	private HistoryStamp modification;
	private Long length; // in bytes
	private Integer versionNumber;
	private Integer majorVersion;
	private Integer minorVersion;
	private String note; // used also for update
	private Integer status; // used also for update
	private String webUrl;

	protected FileCommonProperties() {}
	
	public FileCommonProperties(String id, HistoryStamp creation, HistoryStamp modification, 
			Long length, Integer versionNumber, Integer majorVersion, Integer minorVersion, 
			String note, Integer status, String webUrl) {
		this.id = id;
		this.creation = creation;
		this.modification = modification;
		this.length = length;
		this.versionNumber = versionNumber;
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
		this.note = note;
		this.status = status;
		this.webUrl = webUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public HistoryStamp getCreation() {
		return creation;
	}

	public void setCreation(HistoryStamp creation) {
		this.creation = creation;
	}

	public HistoryStamp getModification() {
		return modification;
	}

	public void setModification(HistoryStamp modification) {
		this.modification = modification;
	}

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public Integer getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(Integer versionNumber) {
		this.versionNumber = versionNumber;
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

	public String getWebUrl() {
		return webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}


}
