package com.sitescape.team.module.report;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Collection;

import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.AuditTrail;
import com.sitescape.team.domain.LicenseStats;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.HistoryStamp;
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
	
	public enum QuotaOption { UsersOnly, WorkspacesOnly, UsersAndWorkspaces };
	
	public void addAuditTrail(AuditTrail auditTrail);
	public void addAuditTrail(AuditTrail.AuditType type, User user, DefinableEntity entity);
	public void addAuditTrail(AuditTrail.AuditType type, DefinableEntity entity);
	public void addLoginInfo(LoginInfo loginInfo);
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
}
