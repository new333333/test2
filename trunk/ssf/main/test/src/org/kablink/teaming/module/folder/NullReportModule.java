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
package org.kablink.teaming.module.folder;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kablink.teaming.domain.AuditTrail;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.LicenseStats;
import org.kablink.teaming.domain.LoginInfo;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.AuditTrail.AuditType;
import org.kablink.teaming.module.report.ReportModule;

public class NullReportModule implements ReportModule {

	public void addAuditTrail(AuditTrail auditTrail) {
		// TODO Auto-generated method stub

	}

	public void addAuditTrail(AuditType type, User user, DefinableEntity entity) {
		// TODO Auto-generated method stub

	}

	public void addAuditTrail(AuditType type, DefinableEntity entity) {
		// TODO Auto-generated method stub

	}

	public void addLoginInfo(LoginInfo loginInfo) {
		// TODO Auto-generated method stub

	}

	public void addFileInfo(AuditTrail.AuditType type, FileAttachment attachment) {
		// TODO Auto-generated method stub
		
	}
	
	public void addLicenseStats(LicenseStats stats)
	{
	}
	public LicenseStats getLicenseHighWaterMark(Calendar startDate, Calendar endDate)
	{
		return new LicenseStats();
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> generateReport(Collection ids,
			boolean byUser, Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Map<String, Object>> generateActivityReport(final Long binderId, final Long entryId) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> generateLoginReport(Date startDate,
			Date endDate, String optionType, String sortType,
			String sortType2, Set memberIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> generateWorkflowStateReport(
			Collection ids, Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> generateWorkflowStateCountReport(
			Collection ids) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<LicenseStats> generateLicenseReport(Date startDate, Date endDate)
	{
		return null;
	}

	public List<Map<String,Object>> generateQuotaReport(QuotaOption option, Long threshold) {
		return null;
	}
	public boolean testAccess(FolderEntry entry, String operation) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean testAccess(String operation) {
		// TODO Auto-generated method stub
		return false;
	}
	public List<User> getUsersActivity(DefinableEntity entity, AuditTrail.AuditType type, Date startDate, Date endDate) {
		return null;
	}

	public Collection<ActivityInfo> culaEsCaliente(AuditType type, Date startDate, Date endDate, Binder binder) {
		// TODO Auto-generated method stub
		return null;
	}
	public List<Map<String,Object>> getEntriesViewed(Long ownerId, 
			Date startDate, Date endDate, Integer returnCount) {
		return null;
	}
	public List<Map<String,Object>> getUsersStatuses(Long[] userIds,
			Date startDate, Date endDate, Integer returnCount) {
		return null;
	}
	public void addStatusInfo(User user) {
	}

	public Collection<ActivityInfo> culaEsCaliente(AuditType limitType, Date startDate, Date endDate, Object[] entityTypes, Integer returnCount) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<ActivityInfo> culaEsCaliente(AuditType limitType, Date startDate, Date endDate, Object[] entityTypes, Integer returnCount,
													Binder binder) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Map<String, Object>> generateActivityReportByUser(Set<Long> userIds, Date startDate, Date endDate, String reportType) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<ActivityInfo> getActivity(AuditType limitType, Date startDate, Date endDate, Binder binder) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<ActivityInfo> getActivity(AuditType limitType, Date startDate, Date endDate, Object[] entityTypes, Integer returnCount) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<ActivityInfo> getActivity(AuditType limitType, Date startDate, Date endDate, Object[] entityTypes, Integer returnCount, Binder binder) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.report.ReportModule#addTokenInfo(org.kablink.teaming.domain.User, org.kablink.teaming.domain.User, java.lang.Long)
	 */
	public void addTokenInfo(User requester, User requestee, Long applicationId) {
		// TODO Auto-generated method stub
		
	}

	public List<Map<String, Object>> generateAccessReportByUser(Long userId,
			Date startDate, Date endDate, String reportType) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addAuditTrail(AuditType type, DefinableEntity entity,
			Date startDate) {
		// TODO Auto-generated method stub
		
	}
}
