package com.sitescape.ef.module.workflow;
import java.util.Date;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.context.exe.ContextInstance;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.domain.WorkflowControlledEntry;


public class RecordEvent extends AbstractActionHandler {
	private static final long serialVersionUID = 1L;
	  
	//Indexing the entry is handled by the code that initiates a transition/nodeEnter
	//Because mutiple states can be effected, we don't want to re-index
	//each time.  Only need one at the end of the transaction
	public void execute( ExecutionContext executionContext ) throws Exception {
		ContextInstance ctx = executionContext.getContextInstance();
		Token token = executionContext.getToken();
		Long id = new Long(token.getId());
		String state = token.getNode().getName();
		WorkflowControlledEntry entry = loadEntry(ctx);
		WorkflowState ws = entry.getWorkflowState(id);
		if (ws != null) {
			HistoryStamp stamp = (HistoryStamp)ctx.getTransientVariable("historyStamp");
			if (stamp == null) {
				stamp = new HistoryStamp(RequestContextHolder.getRequestContext().getUser(), new Date());
			}
			entry.setWorkflowChange(stamp);
			ws.setState(state);
			if (infoEnabled) logger.info("Workflow event (" + executionContext.getEvent().getEventType() + ") recorded: " + state);
		}
//Don't think you can wait for arbitrary state changes, don't have control over ordering.
//		if (ctx.getTransientVariable("ignoreChecks") == null)
//			checkForWaits(token, entry);
	}
	  
 
}
