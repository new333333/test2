package com.sitescape.team.module.workflow;
import java.util.Map;

import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.WorkflowSupport;
import com.sitescape.team.module.workflow.support.WorkflowScheduledAction;
import com.sitescape.team.module.workflow.support.WorkflowStatus;
import com.sitescape.team.remoteapplication.RemoteApplicationManager;
import com.sitescape.team.util.SpringContextUtil;
public class StartRemoteApp implements WorkflowScheduledAction {
	protected RemoteApplicationManager getRemoteApplicationManager() {
		return (RemoteApplicationManager) SpringContextUtil.getBean("remoteApplicationManager");
	}
	public boolean execute(WorkflowSupport entry, WorkflowState state, WorkflowStatus status) {
		Map params = status.getParams();
		String appId = (String)params.get("workflow.application_id");
		try {
			String result = getRemoteApplicationManager().executeRequestScopedNonRenderableAction(params, Long.valueOf(appId));
			return true;
		
		} catch (Exception ex) {
			status.setErrorMessage(ex.getLocalizedMessage());
			status.setRetrySeconds(5);
			return false;
			
		}
	}

}
