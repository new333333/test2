package com.sitescape.ef.domain;
import java.util.HashSet;
import java.util.Set;

import com.sitescape.util.StringUtil;
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
	private Set principalIds;
	
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
	public Set getPrincipalIds() {
		if (principalIds == null) principalIds = new HashSet();
		return principalIds;
	}
	public void setPrincipalIds(Set principalIds) {
		this.principalIds = principalIds;
	}
	public void setPrincipalIds(String stringIds) {
		String [] result = StringUtil.split(stringIds);
		this.principalIds = new HashSet();
		for (int i=0; i<result.length; ++i) {
			try {
				this.principalIds.add(Long.valueOf(result[i]));
			} catch (Exception e) {}

		}
	}

}
