package com.sitescape.ef.module.workflow;
	
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

import com.sitescape.ef.domain.WorkflowSupport;
import com.sitescape.ef.domain.WorkflowState;

public class TimerAction extends AbstractActionHandler {
	protected Log logger = LogFactory.getLog(getClass());
    private static final long serialVersionUID = 1L;

	public void execute(ExecutionContext executionContext) throws Exception {
		ContextInstance ctx = executionContext.getContextInstance();
		Token current = executionContext.getToken();
		WorkflowSupport entry = loadEntry(ctx);
		WorkflowState ws = entry.getWorkflowState(new Long(current.getId()));
		if (ws != null) {
			if (infoEnabled) logger.info("Timeout begin: at state " + ws.getState() + " thread " + ws.getThreadName());
			//Check for conditions on this threads
			String toState = TransitionUtils.processConditions(executionContext, entry, ws);
			if (toState != null) {
				if (infoEnabled) logger.info("Timeout transition("+ ws.getThreadName() + "): " + ws.getState() + "." + toState);
					executionContext.leaveNode(ws.getState() + "." + toState);
					return;
			}

			
			//wait for external event to trigger a transition
			if (infoEnabled) logger.info("Timeout wait: at state " + ws.getState() + " thread " + ws.getThreadName());
		}
	}

}
