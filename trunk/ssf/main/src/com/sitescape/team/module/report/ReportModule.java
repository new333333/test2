package com.sitescape.team.module.report;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Collection;

import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.AuditTrail;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.LoginInfo;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.WorkflowStateHistory;
public interface ReportModule {

	public static final String BINDER_ID = "binder_id";
	public static final String BINDER_NAME = "binder_name";
	
	public void addAuditTrail(AuditTrail auditTrail);
	public void addAuditTrail(AuditTrail.AuditType type, User user, DefinableEntity entity);
	public void addAuditTrail(AuditTrail.AuditType type, DefinableEntity entity);
	public void addLoginInfo(LoginInfo loginInfo);
	public void addWorkflowStateHistory(WorkflowStateHistory workflowStateHistory);
	public void addWorkflowStateHistory(WorkflowState state, HistoryStamp end, boolean isEnded);
	
	public List<Map<String, Object>> generateReport(Collection ids);
	public List<Map<String,Object>> generateReport(Long binderId, boolean includeChildren);
	public List<Map<String,Object>> generateLoginReport(Date after, Date before);
	public List<Map<String,Object>> generateWorkflowStateReport(String definitionId, String state);

    public boolean testAccess(String operation);	
}
