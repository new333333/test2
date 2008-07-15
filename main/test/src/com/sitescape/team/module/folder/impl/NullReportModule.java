package com.sitescape.team.module.folder.impl;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.sitescape.team.domain.AuditTrail;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.LicenseStats;
import com.sitescape.team.domain.LoginInfo;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.WorkflowStateHistory;
import com.sitescape.team.domain.AuditTrail.AuditType;
import com.sitescape.team.module.report.ReportModule;
import com.sitescape.team.module.report.ReportModule.QuotaOption;

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
	public void addWorkflowStateHistory(
			WorkflowStateHistory workflowStateHistory) {
		// TODO Auto-generated method stub

	}

	public void addWorkflowStateHistory(WorkflowState state, HistoryStamp end,
			boolean isEnded) {
		// TODO Auto-generated method stub

	}
	
	public void addLicenseStats(LicenseStats stats)
	{
	}
	public LicenseStats getLicenseHighWaterMark(Calendar startDate, Calendar endDate)
	{
		return new LicenseStats();
	}

	public List<Map<String, Object>> generateReport(Collection ids,
			boolean byUser, Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Map<String, Object>> generateActivityReport(final Long binderId, final Long entryId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Map<String, Object>> generateLoginReport(Date startDate,
			Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Map<String, Object>> generateWorkflowStateReport(
			Collection ids, Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}

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
	public List<Map<String,Object>> getUsersActivities(Long ownerId, Long[] userIds,
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
}
