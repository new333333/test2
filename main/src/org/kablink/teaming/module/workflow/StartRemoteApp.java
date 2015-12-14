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
package org.kablink.teaming.module.workflow;
import java.util.Map;

import org.kablink.teaming.NoObjectByTheIdException;
import org.kablink.teaming.module.workflow.support.AbstractWorkflowCallout;
import org.kablink.teaming.module.workflow.support.WorkflowScheduledAction;
import org.kablink.teaming.module.workflow.support.WorkflowStatus;
import org.kablink.teaming.remoteapplication.RemoteApplicationManager;
import org.kablink.teaming.security.accesstoken.AccessToken;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.util.Validator;


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
