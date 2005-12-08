package com.sitescape.ef.module.workflow;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.context.exe.ContextInstance;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.domain.WorkflowStateObject;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.MultipleWorkflowSupport;
import com.sitescape.ef.domain.SingletonWorkflowSupport;


public class RecordEvent extends AbstractActionHandler {
	  private static final long serialVersionUID = 1L;
	  private String state;
	  private String eventType;
	  
	  public void setWfState(String state) {
		  this.state = state;
	  }
	  public void execute( ExecutionContext executionContext ) throws Exception {
		  Token token = executionContext.getToken();
		  state = token.getNode().getName();
		  ContextInstance ctx = executionContext.getContextInstance();
		  eventType = executionContext.getEvent().getEventType();
		  Long id = new Long(token.getId());
		  Long entryId = (Long)ctx.getVariable("entryId");
		  String entryType = (String)ctx.getVariable("entryType");
		  Entry entry = loadEntry(entryType, entryId);
		  if (entry instanceof MultipleWorkflowSupport) {
			  MultipleWorkflowSupport mEntry = (MultipleWorkflowSupport)entry;
			  WorkflowStateObject ws = (WorkflowStateObject)getCoreDao().load(WorkflowStateObject.class, id);
			  if (ws != null) {
				  ws.setState(state);
			  } else {
				  //doesn't exist, add a new one
				  ws = new WorkflowStateObject();
				  ws.setTokenId(id);
				  ws.setState(state);
				  ws.setOwner(entry);
				  getCoreDao().save(ws);
				  mEntry.addWorkflowState(ws);
			  }
		  } else if (entry instanceof SingletonWorkflowSupport) {
			  SingletonWorkflowSupport sEntry = (SingletonWorkflowSupport)entry;
			  WorkflowState ws = sEntry.getWorkflowState();
			  if (ws == null) {
				  ws = new WorkflowState();
				  sEntry.setWorkflowState(ws);
			  }
			  ws.setState(state);
			  ws.setTokenId(id);
		  }	   
		  System.out.println("Workflow event (" + eventType + ") recorded: " + state);
	  }

}
