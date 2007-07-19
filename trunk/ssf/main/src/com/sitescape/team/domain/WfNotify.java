/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.domain;
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
	private Set ids;
	
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
	public void addPrincipalId(Long id) {
		if (this.ids == null) this.ids = new HashSet();
		this.ids.add(id);
	}
	public void addPrincipalIds(Set<Long> ids) {
		if (this.ids == null) this.ids = new HashSet();
		this.ids.addAll(ids);
	}
	public Set<Long> getPrincipalIds() {
		if (ids == null) ids = new HashSet();
		return ids;
	}

}
