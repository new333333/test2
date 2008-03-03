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
package com.sitescape.team.module.report;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Collection;

import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.AuditTrail;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.LicenseStats;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.LoginInfo;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.WorkflowStateHistory;
public interface ReportModule {

	public static final String BINDER_ID = "binder_id";
	public static final String BINDER_TITLE = "binder_title";
	public static final String BINDER_PARENT = "binder_parent";
	public static final String USER_ID = "user_id";
	public static final String USER_TITLE = "user_title";
	public static final String LAST_LOGIN = "last_login";
	public static final String LOGIN_COUNT = "login_count";
	public static final String STATE = "state";
	public static final String DEFINITION_ID = "definition_id";
	public static final String AVERAGE = "average";
	public static final String AVERAGE_TI = "average_ti";
	public static final String COUNT = "count";
	public static final String SIZE = "size";
	
	public static class VisitInfo
	{
		Entry whoOrWhat;
		int visitCount;
		Date lastVisit;
		
		public VisitInfo(Entry whoOrWhat, int visitCount, Date lastVisit)
		{
			this.whoOrWhat = whoOrWhat;
			this.visitCount = visitCount;
			this.lastVisit = lastVisit;
		}
		public Entry getWhoOrWhat() { return whoOrWhat; }
		public int getVisitCount() { return visitCount; }
		public Date getLastVisit() { return lastVisit; }
	};
	
	public enum QuotaOption { UsersOnly, WorkspacesOnly, UsersAndWorkspaces };
	
	public void addAuditTrail(AuditTrail auditTrail);
	public void addAuditTrail(AuditTrail.AuditType type, User user, DefinableEntity entity);
	public void addAuditTrail(AuditTrail.AuditType type, DefinableEntity entity);
	public void addLoginInfo(LoginInfo loginInfo);
	public void addFileInfo(AuditTrail.AuditType type, FileAttachment attachment);
	public void addWorkflowStateHistory(WorkflowStateHistory workflowStateHistory);
	public void addWorkflowStateHistory(WorkflowState state, HistoryStamp end, boolean isEnded);

	public void addLicenseStats(LicenseStats stats);
	public LicenseStats getLicenseHighWaterMark(Calendar startDate, Calendar endDate);
	
	public List<Map<String, Object>> generateReport(Collection ids, boolean byUser, Date startDate, Date endDate);
	public List<Map<String, Object>> generateActivityReport(final Long binderId, final Long entryId);
	public List<Map<String,Object>> generateLoginReport(Date startDate, Date endDate);
	public List<Map<String,Object>> generateWorkflowStateReport(Collection ids, Date startDate, Date endDate);
	public List<Map<String,Object>> generateWorkflowStateCountReport(Collection ids);
	public List<Map<String, Object>> generateQuotaReport(QuotaOption option, Long threshold);

	public List<LicenseStats> generateLicenseReport(Date startDate, Date endDate);
	public Collection<User> getUsersActivity(DefinableEntity entity, AuditTrail.AuditType type, Date startDate, Date endDate);
	public Collection<VisitInfo> culaEsCaliente(Date startDate, Date endDate);

}
