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
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.LicenseStats;
import org.kablink.teaming.domain.LoginInfo;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.WorkflowStateHistory;
import org.kablink.teaming.domain.AuditTrail.AuditType;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.module.report.ReportModule.QuotaOption;

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
			Date endDate, String optionType, String sortType,
			String sortType2, Set memberIds) {
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
}
