package com.sitescape.ef.module.workflow;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
	
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

import com.sitescape.ef.domain.AclControlledEntry;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.util.Validator;
import com.sitescape.ef.domain.WfWaits;

public class DecisionAction extends AbstractActionHandler {
	protected Log logger = LogFactory.getLog(getClass());
    private static final long serialVersionUID = 1L;

	public void execute(ExecutionContext executionContext) throws Exception {
		ContextInstance ctx = executionContext.getContextInstance();
		Token current = executionContext.getToken();
		AclControlledEntry entry = loadEntry(ctx);
		WorkflowState ws = entry.getWorkflowState(new Long(current.getId()));
		if (ws != null) {
			logger.info("Begin decision :" + ws.getState() + ":" + ws.getThreadName());
			if (ws.isThreadEndState()) {
				logger.info("ThreadEnd");
				if (!current.isRoot()) {
					current.end(false);
				} else {
					executionContext.getProcessInstance().end();
				}
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
					checkForWaits(current, entry);
				} 
				return;
			}
			//see if threads I am waiting for are done
			List waitingFor = ws.getWfWaits();
			Token root = current.getProcessInstance().getRootToken();
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
							logger.info("Decision transition("+ ws.getThreadName() + "): " + ws.getState() + "." + toState);
							current.signal(ws.getState() + "." + toState);
							return;
						}
 
					}
				}
			}
			logger.info("End decision:" + ws.getState() + ":" + ws.getThreadName());
		}
	}

}
