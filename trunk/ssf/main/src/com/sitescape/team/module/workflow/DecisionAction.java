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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.dom4j.Element;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextUtil;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.WorkflowSupport;
import com.sitescape.team.jobs.WorkflowProcess;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.workflow.jbpm.CalloutHelper;
import com.sitescape.team.module.workflow.support.WorkflowAction;
import com.sitescape.team.module.workflow.support.WorkflowScheduledAction;
import com.sitescape.team.util.ReflectHelper;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.util.Validator;

public class DecisionAction extends AbstractActionHandler {
	protected Log logger = LogFactory.getLog(getClass());
    private static final long serialVersionUID = 1L;

	public void execute(ExecutionContext executionContext) throws Exception {
		ContextInstance ctx = executionContext.getContextInstance();
		Token current = executionContext.getToken();
		WorkflowSupport entry = loadEntry(ctx);
		WorkflowState ws = entry.getWorkflowState(new Long(current.getId()));
		if (ws != null) {
			if (debugEnabled) logger.debug("Decision begin: at state " + ws.getState() + " thread " + ws.getThreadName());
			if (TransitionUtils.isThreadEndState(ws.getDefinition(), ws.getState(), ws.getThreadName())) {
				if (debugEnabled) logger.debug("Decision: end thread");
				TransitionUtils.endWorkflow(entry, ws, false);
				return;
			}
			//Check for conditions on this threads
			String toState = TransitionUtils.processConditions(executionContext, entry, ws);
			if (toState != null) {
				if (debugEnabled) logger.debug("Decision transition("+ ws.getThreadName() + "): " + ws.getState() + "." + toState);
					executionContext.leaveNode(ws.getState() + "." + toState);
					return;
			}

			
			//wait for external event to trigger a transition
			if (debugEnabled) logger.debug("Decision wait: at state " + ws.getState() + " thread " + ws.getThreadName());
		}
	}


}
