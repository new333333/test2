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
package com.sitescape.team.module.workflow;
import java.util.Map;

import com.sitescape.team.module.workflow.support.AbstractWorkflowCallout;
import com.sitescape.team.module.workflow.support.WorkflowScheduledAction;
import com.sitescape.team.module.workflow.support.WorkflowStatus;
import com.sitescape.team.remoteapplication.RemoteApplicationManager;
import com.sitescape.team.security.accesstoken.AccessToken;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.util.Validator;
import com.sitescape.team.NoObjectByTheIdException;

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
					Long.valueOf(binderId), AccessToken.BinderAccessConstraints.NONE);
			//scheduler will push variables back to workflow engine and check for new conditions
			if (Validator.isNotNull(resultVariable)) setVariable(resultVariable, result);
			return true;
		
		} catch (NoObjectByTheIdException ex) {
			throw ex; //this will remove the job
		} catch (Exception ex) {
			status.setErrorMessage(ex.getLocalizedMessage());
			status.setRetrySeconds(status.getRetrySeconds());
			return false;
			
		}
	}

}
