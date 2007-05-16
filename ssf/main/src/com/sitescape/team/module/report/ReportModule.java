package com.sitescape.team.module.report;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Collection;

import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.AuditTrail;
import com.sitescape.team.domain.FolderEntry;
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
	public static final String LAST_LOGIN = "last_login";
	public static final String COUNT = "count";
	public static final String STATE = "state";
	public static final String START_DATE = "start_date";
	public static final String END_DATE = "end_date";
	public static final String START_BY = "start_by";
	public static final String END_BY = "end_by";
	
	public void addAuditTrail(AuditTrail auditTrail);
	public void addAuditTrail(AuditTrail.AuditType type, User user, DefinableEntity entity);
	public void addAuditTrail(AuditTrail.AuditType type, DefinableEntity entity);
	public void addLoginInfo(LoginInfo loginInfo);
	public void addWorkflowStateHistory(WorkflowStateHistory workflowStateHistory);
	public void addWorkflowStateHistory(WorkflowState state, HistoryStamp end, boolean isEnded);
	
	public List<Map<String, Object>> generateReport(Collection ids, boolean byUser, Date startDate, Date endDate);
	public List<Map<String,Object>> generateLoginReport(Date startDate, Date endDate);
	public List<Map<String,Object>> generateWorkflowHistoryReport(Long binderId, Long entryId);
	public List<Map<String,Object>> generateWorkflowStateReport(String definitionId, String state);

    public boolean testAccess(FolderEntry entry, String operation);	
    public boolean testAccess(String operation);	
}
