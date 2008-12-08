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
package com.sitescape.team.domain;
import java.util.HashSet;
import java.util.Set;
/**
 * Helper class to encapsalate state notifications
 * @author Janet McCann
 *
 */
public class WfNotify {
	private String subject="";
	private String body="";
	private boolean appendTitle;
	private boolean appendBody;
	private Set ids;
	private Set ccIds;//added post 1.0.3
	private Set bccIds;//added post 1.0.3
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
	public void addCCPrincipalIds(Set<Long> ids) {
		if (this.ccIds == null) this.ccIds = new HashSet();
		this.ccIds.addAll(ids);
	}
	public void addCCPrincipalId(Long id) {
		if (this.ccIds == null) this.ccIds = new HashSet();
		this.ccIds.add(id);
	}
	public Set<Long> getCCPrincipalIds() {
		if (ccIds == null) ccIds = new HashSet();
		return ccIds;
	}	
	public void addBCCPrincipalIds(Set<Long> ids) {
		if (this.bccIds == null) this.bccIds = new HashSet();
		this.bccIds.addAll(ids);
	}
	public void addBCCPrincipalId(Long id) {
		if (this.bccIds == null) this.bccIds = new HashSet();
		this.bccIds.add(id);
	}
	public Set<Long> getBCCPrincipalIds() {
		if (bccIds == null) bccIds = new HashSet();
		return bccIds;
	}
}
