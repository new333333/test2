package com.sitescape.ef.module.workflow;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.def.Node;

import com.sitescape.ef.domain.AclControlledEntry;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.util.Validator;
import com.sitescape.ef.domain.WfWaits;

public class DecisionAction extends AbstractActionHandler {
	protected Log logger = LogFactory.getLog(getClass());
    private static final long serialVersionUID = 1L;

	public void execute(ExecutionContext executionContext) throws Exception {
		ContextInstance ctx = executionContext.getContextInstance();
		Token token = executionContext.getToken();
		Long id = new Long(token.getId());
		AclControlledEntry entry = loadEntry(ctx);
		WorkflowState ws = entry.getWorkflowState(id);
		if (ws != null) {
			logger.info("Workflow decision start:" + ws.getState() + ws.getThreadName());
			if (ws.isThreadEndState()) {
				//terminate - record event will kick off waiting threads
				executionContext.getToken().end(false);	    
				entry.removeWorkflowState(ws);
				checkForWaits(token, entry);
				return;
			}
			//see if threads I am waiting for are done
			List waitingFor = ws.getWfWaits();
			Token root = token.getProcessInstance().getRootToken();
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
								//The token we are waiting for is a child of the root.
								//see if it has ended
								Token child = root.getChild(threadName);
								//If nulll, hasn't stated yet
								if ((child == null) || !child.hasEnded()) {
									done = false;
									break;
								}
							}							
						}
						if (done) {
							logger.info("Workflow decision transition: " + ws.getState() + "." + toState);
							token.getNode().leave(executionContext, ws.getState() + "." + toState);
							return;
						}
 
					}
				}
			}
			logger.info("Workflow decision end:" + ws.getState() + ws.getThreadName());
		}
	}

}
