package com.sitescape.ef.module.workflow;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
	
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

import com.sitescape.ef.domain.WorkflowSupport;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.util.Validator;
import com.sitescape.ef.domain.WfWaits;

public class DecisionAction extends AbstractActionHandler {
	protected Log logger = LogFactory.getLog(getClass());
    private static final long serialVersionUID = 1L;

	public void execute(ExecutionContext executionContext) throws Exception {
		ContextInstance ctx = executionContext.getContextInstance();
		Token current = executionContext.getToken();
		WorkflowSupport entry = loadEntry(ctx);
		WorkflowState ws = entry.getWorkflowState(new Long(current.getId()));
		if (ws != null) {
			if (infoEnabled) logger.info("Decision begin: at state " + ws.getState() + " thread " + ws.getThreadName());
			if (ws.isThreadEndState()) {
				if (infoEnabled) logger.info("Decision: end thread");
				if (!current.isRoot()) {
					current.end(false);
				} else {
					executionContext.getProcessInstance().end();
				}
				// cleanup any children - should only have children if token is root
				Map children = current.getChildren();
				for (Iterator iter=children.values().iterator();iter.hasNext();) {
					Token child = (Token)iter.next();
					WorkflowState w = entry.getWorkflowState(new Long(child.getId()));
					if (w != null) {
						entry.removeWorkflowState(w);
					}
				}
				if (!current.isRoot()) {
					entry.removeWorkflowState(ws);
					checkForWaits(executionContext, current, entry);
				} 
				return;
			}
			//see if threads I am waiting for are done
			List waitingFor = ws.getWfWaits();
			for (int i=0; i<waitingFor.size(); ++i) {
				WfWaits wait = (WfWaits)waitingFor.get(i);
				List result = wait.getThreads();
				if (!result.isEmpty()) {
					String toState = wait.getToStateName();
					if (!Validator.isNull(toState)) {
						boolean done = true;
						for (int j=0; j<result.size(); ++j) {
							String threadName = (String)result.get(j);
							if (!Validator.isNull(threadName)) {
								//See if child has ended
								WorkflowState child = entry.getWorkflowStateByThread(ws.getDefinition(), threadName);
								//if found - still running
								if (child != null) {
									done = false;
									break;
								}
							}							
						}
						if (done) {
							if (infoEnabled) logger.info("Decision transition("+ ws.getThreadName() + "): " + ws.getState() + "." + toState);
							current.signal(ws.getState() + "." + toState);
							return;
						}
 
					}
				}
			}
			if (infoEnabled) logger.info("Decision end: at state " + ws.getState() + " thread " + ws.getThreadName());
		}
	}

}
