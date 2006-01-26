package com.sitescape.ef.module.workflow;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.context.exe.ContextInstance;

import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.domain.AclControlledEntry;
import com.sitescape.ef.module.shared.WorkflowUtils;
import com.sitescape.util.Validator;

/**
 * This node-enter action stops parallel threads and checks to see if anyone is waiting
 * @author Janet McCann
 *
 */
public class StopThreads extends AbstractActionHandler {
	protected Log logger = LogFactory.getLog(getClass());
	private static final long serialVersionUID = 1L;
	  
	public void execute( ExecutionContext executionContext ) throws Exception {
		ContextInstance ctx = executionContext.getContextInstance();
		Token token = executionContext.getToken();
		Long id = new Long(token.getId());
		Node current = token.getNode();
		String stateName = current.getName();
		AclControlledEntry entry = loadEntry(ctx);
		WorkflowState ws = entry.getWorkflowState(id);
		 //record event may not have happened yet
		ws.setState(stateName);
		logger.info("Begin stop threads: " + stateName);
		if (ws != null) {
			//See if any parallel executions should be terminated
			//Note,we are in an intermediate state
			List parallelThreadStops = WorkflowUtils.getParallelThreadStops(ws.getDefinition(), stateName);
			Token root = token.getProcessInstance().getRootToken();
			boolean found = false;
			for (int i = 0; i < parallelThreadStops.size(); i++) {
				String threadName = (String) parallelThreadStops.get(i);
				if (!Validator.isNull(threadName)) {
					//All thread tokens are children of the root
					Token child = root.getChild(threadName);
					//If null, hasn't stated yet
					if ((child != null) && !child.hasEnded()) {
						child.end();
						found = true;
						WorkflowState state = entry.getWorkflowStateByThread(ws.getDefinition(), threadName);
						if (state != null) entry.removeWorkflowState(state);
						logger.info("End thread: " + threadName);
					}
				}
			}
			if (found) checkForWaits(token, entry);
		}
		logger.info("End stop threads: " + stateName);
	}
}
