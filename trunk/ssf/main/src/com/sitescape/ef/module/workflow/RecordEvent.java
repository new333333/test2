package com.sitescape.ef.module.workflow;

import java.util.List;
import java.util.Map;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.context.exe.ContextInstance;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.domain.WorkflowStateObject;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.MultipleWorkflowSupport;
import com.sitescape.ef.domain.SingletonWorkflowSupport;
import com.sitescape.ef.module.shared.WorkflowUtils;
import com.sitescape.ef.ObjectKeys;


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
		  Long entryId = (Long)ctx.getVariable(WorkflowUtils.ENTRY_ID);
		  String entryType = (String)ctx.getVariable(WorkflowUtils.ENTRY_TYPE);
		  Entry entry = loadEntry(entryType, entryId);
		  WorkflowState ws = null;
		  if (entry instanceof MultipleWorkflowSupport) {
			  MultipleWorkflowSupport mEntry = (MultipleWorkflowSupport)entry;
			  ws = (WorkflowState)getCoreDao().load(WorkflowStateObject.class, id);
			  if (ws != null) {
				  ws.setState(state);
			  } else {
				  //doesn't exist, add a new one
				  ws = (WorkflowState) new WorkflowStateObject();
				  ws.setTokenId(id);
				  ws.setState(state);
				  ((WorkflowStateObject)ws).setOwner(entry);
				  getCoreDao().save(ws);
				  mEntry.addWorkflowState(ws);
			  }
		  } else if (entry instanceof SingletonWorkflowSupport) {
			  SingletonWorkflowSupport sEntry = (SingletonWorkflowSupport)entry;
			  ws = sEntry.getWorkflowState();
			  if (ws == null) {
				  ws = new WorkflowState();
				  sEntry.setWorkflowState(ws);
			  }
			  ws.setState(state);
			  ws.setTokenId(id);
		  }
		  //See if any parallel executions should be started
		  if (ws != null) {
			  List parallelStarts = WorkflowUtils.getParallelExecutions(ws.getDefinition(), state);
			  for (int i = 0; i < parallelStarts.size(); i++) {
				  Map pT = (Map) parallelStarts.get(i);
				  pT.get(ObjectKeys.WORKFLOW_PARALLEL_THREAD_NAME);
				  pT.get(ObjectKeys.WORKFLOW_PARALLEL_THREAD_START_STATE);
			  }
		  }
		  //Re-index the entry after changing its state
		  //TODO add code to re-index the entry
		  
		  System.out.println("Workflow event (" + eventType + ") recorded: " + state);
	  }

}
