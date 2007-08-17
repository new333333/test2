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
