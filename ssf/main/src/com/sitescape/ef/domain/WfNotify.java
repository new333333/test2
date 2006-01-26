package com.sitescape.ef.domain;
import java.util.List;
import java.util.ArrayList;
/**
 * Helper class to encapsalate state notifications
 * @author Janet McCann
 *
 */
public class WfNotify {
	private boolean creator;
	private String subject="";
	private String body="";
	private boolean appendTitle;
	private boolean appendBody;
	private List principalIds;
	
	public boolean isCreatorEnabled() {
		return creator;
	}
	public void setCreatorEnabled(boolean creator) {
		this.creator = creator;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public boolean isAppendTitle() {
		return appendTitle;
	}
	public void setAppendTitle(boolean appendTitle) {
		this.appendTitle = appendTitle;
	}
	
	public boolean isAppendBody() {
		return appendBody;
	}
	public void setAppendBody(boolean appendBody) {
		this.appendBody = appendBody;
	}
	public List getPrincipalIds() {
		if (principalIds == null) principalIds = new ArrayList();
		return principalIds;
	}
	public void setPrincipalIds(List principalIds) {
		this.principalIds = principalIds;
	}
	
}
