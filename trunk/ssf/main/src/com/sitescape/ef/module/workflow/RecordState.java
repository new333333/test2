package com.sitescape.ef.module.workflow;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.context.exe.ContextInstance;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.MultipleWorkflowSupport;
import com.sitescape.ef.module.shared.WorkflowUtils;


public class RecordState extends AbstractActionHandler {
	  private static final long serialVersionUID = 1L;
	  public void execute( ExecutionContext executionContext ) throws Exception {
		  Token token = executionContext.getToken();
		  String state = token.getNode().getName();
		  ContextInstance ctx = executionContext.getContextInstance();
		  Long id = new Long(token.getId());
		  Long entryId = (Long)ctx.getVariable(WorkflowUtils.ENTRY_ID);
		  String entryType = (String)ctx.getVariable(WorkflowUtils.ENTRY_TYPE);
		  Entry entry = loadEntry(entryType, entryId);
		  WorkflowState ws = (WorkflowState)getCoreDao().load(WorkflowState.class, id);
		  if (ws != null) {
			  ws.setState(state);
		  } else {
			  //doesn't exist, add a new one
			  ws = new WorkflowState();
			  ws.setTokenId(id);
			  ws.setState(state);
			  ws.setOwner(entry);
			  getCoreDao().save(ws);
			  entry.addWorkflowState(ws);
		  }
		  System.out.println("Set state to " + state);
	  }

}
