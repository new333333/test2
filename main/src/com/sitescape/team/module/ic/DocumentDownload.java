/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.module.ic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DocumentDownload {
	String meetingId;

	String operation; // "add", "addAndDelete", null

	RecordType type; // null if this is document (not record)

	String id;

	String url;
	
	String orginalUrl;

	protected DocumentDownload(String meetingId, String operation, String id,
			RecordType type, String url) {
		this.meetingId = meetingId;
		this.operation = operation;
		this.type = type;
		this.id = id;
		this.url = url;
		this.orginalUrl = url;
	}
	
	static public DocumentDownload fromDocument(String meetingId, 
			List document) {
		if (document == null) {
			return null;
		}
		return new DocumentDownload(meetingId, null, (String)document.get(0), null, (String)document.get(1));
	}

	static public DocumentDownload fromRecord(String meetingId, String operation,
			List record) {
		if (record == null) {
			return null;
		}
		return new DocumentDownload(meetingId, operation, (String) record
				.get(0), RecordType.getByNumber(Integer.parseInt((String)record.get(1))), (String) record.get(2));
	}
	
	static public List<DocumentDownload> fromRecordsList(String meetingId, String operation, List records) {
		List<DocumentDownload> result = new ArrayList();
		
		if (records == null) {
			return result;
		}
		
		Iterator<List> it = records.iterator();
		while (it.hasNext()) {
			result.add(fromRecord(meetingId, operation, it.next()));
		}
		
		return result;
	}

	public String getId() {
		return id;
	}

	public String getMeetingId() {
		return meetingId;
	}

	public String getOperation() {
		return operation;
	}

	public RecordType getType() {
		return type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getOrginalUrl() {
		return orginalUrl;
	}
}
