package com.sitescape.ef.module.workflow;

import java.util.List;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.context.exe.ContextInstance;

import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.domain.WorkflowSupport;
import com.sitescape.ef.module.shared.WorkflowUtils;
import com.sitescape.util.Validator;
import com.sitescape.ef.module.workflow.impl.WorkflowFactory;

/**
 * This node-enter action stops parallel threads and checks to see if anyone is waiting
 * @author Janet McCann
 *
 */
public class StopThreads extends AbstractActionHandler {
	private static final long serialVersionUID = 1L;
	  
	public void execute( ExecutionContext executionContext ) throws Exception {
		ContextInstance ctx = executionContext.getContextInstance();
		Token token = executionContext.getToken();
		Long id = new Long(token.getId());
		Node current = token.getNode();
		String stateName = current.getName();
		WorkflowSupport entry = loadEntry(ctx);
		WorkflowState ws = entry.getWorkflowState(id);
		 //record event may not have happened yet
		ws.setState(stateName);
		if (infoEnabled) logger.info("Stop threads begin at: " + stateName);
		if (ws != null) {
			//See if any parallel executions should be terminated
			//Note,we are in an intermediate state
			List parallelThreadStops = WorkflowUtils.getParallelThreadStops(ws.getDefinition(), stateName);
			boolean found = false;
			for (int i = 0; i < parallelThreadStops.size(); i++) {
				String threadName = (String) parallelThreadStops.get(i);
				if (!Validator.isNull(threadName)) {
					//See if child has ended
					WorkflowState thread = entry.getWorkflowStateByThread(ws.getDefinition(), threadName);
					if (thread != null) {
						//child is active, end it
						Token childToken = WorkflowFactory.getSession().getGraphSession().loadToken(thread.getTokenId().longValue());
						if (childToken != null)	childToken.end();
						found = true;
						entry.removeWorkflowState(thread);
						if (infoEnabled) logger.info("Stop threads: end thread " + threadName);
					}
				}
			}
			if (found) checkForWaits(token, entry);
		}
		if (infoEnabled) logger.info("Stop threads end at: " + stateName);
	}
}
