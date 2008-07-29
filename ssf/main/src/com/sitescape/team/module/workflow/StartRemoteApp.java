package com.sitescape.team.module.workflow;
import java.util.Map;

import com.sitescape.team.module.workflow.support.AbstractWorkflowCallout;
import com.sitescape.team.module.workflow.support.WorkflowScheduledAction;
import com.sitescape.team.module.workflow.support.WorkflowStatus;
import com.sitescape.team.remoteapplication.RemoteApplicationManager;
import com.sitescape.team.security.accesstoken.AccessToken;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.util.Validator;
public class StartRemoteApp extends AbstractWorkflowCallout implements WorkflowScheduledAction {
	protected RemoteApplicationManager getRemoteApplicationManager() {
		return (RemoteApplicationManager) SpringContextUtil.getBean("remoteApplicationManager");
	}
	/**
	 * params contains 
	 * workflow.entry_id 
	 * workflow.binder_id
	 * workflow.state_id
	 * workflow.state_name
	 * workflow.thread_name
	 * workflow.application_id
	 * workflow.application_name
	 */
	public boolean execute(Long entryId, Long stateId, WorkflowStatus status) {
		Map params = status.getParams();
		String appId = (String)params.get(WorkflowScheduledAction.WORKFLOW_APPLICATION_ID);
		String binderId = (String)params.get(WorkflowScheduledAction.WORKFLOW_BINDER_ID);
		String resultVariable = (String)params.get(WorkflowScheduledAction.WORKFLOW_RESULT_NAME);
		try {
			String result = getRemoteApplicationManager().executeRequestScopedNonRenderableAction(params, Long.valueOf(appId), 
					Long.valueOf(binderId), AccessToken.BinderAccessConstraints.BINDER_ONLY);
			//scheduler will push variables back to workflow engine and check for new conditions
			if (Validator.isNotNull(resultVariable)) setVariable(resultVariable, result);
			return true;
		
		} catch (Exception ex) {
			status.setErrorMessage(ex.getLocalizedMessage());
			status.setRetrySeconds(status.getRetrySeconds());
			return false;
			
		}
	}

}
