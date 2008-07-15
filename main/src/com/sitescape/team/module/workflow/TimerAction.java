/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.module.workflow;
	
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.WorkflowSupport;

public class TimerAction extends AbstractActionHandler {
	protected Log logger = LogFactory.getLog(getClass());
    private static final long serialVersionUID = 1L;

	public void execute(ExecutionContext executionContext) throws Exception {
		ContextInstance ctx = executionContext.getContextInstance();
		Token current = executionContext.getToken();
		WorkflowSupport entry = loadEntry(ctx);
		WorkflowState ws = entry.getWorkflowState(new Long(current.getId()));
		if (ws != null) {
			if (debugEnabled) logger.debug("Timeout begin: at state " + ws.getState() + " thread " + ws.getThreadName());
			//Check for conditions on this threads
			String toState = TransitionUtils.processConditions(executionContext, entry, ws);
			if (toState != null) {
				if (debugEnabled) logger.debug("Timeout transition("+ ws.getThreadName() + "): " + ws.getState() + "." + toState);
					executionContext.leaveNode(ws.getState() + "." + toState);
					return;
			}

			
			//wait for external event to trigger a transition
			if (debugEnabled) logger.debug("Timeout wait: at state " + ws.getState() + " thread " + ws.getThreadName());
		}
	}

}
