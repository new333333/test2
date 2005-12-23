package com.sitescape.ef.module.workflow;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.db.JbpmSession;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.MultipleWorkflowSupport;
import com.sitescape.ef.module.shared.WorkflowUtils;
import com.sitescape.ef.module.workflow.impl.WorkflowFactory;
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
		ws = (WorkflowState)getCoreDao().load(WorkflowState.class, id);
		if (ws != null) {
			ws.setState(state);
		} else {
			//doesn't exist, add a new one
			ws = (WorkflowState) new WorkflowState();
			ws.setTokenId(id);
			ws.setState(state);
			((WorkflowState)ws).setOwner(entry);
			getCoreDao().save(ws);
			entry.addWorkflowState(ws);
		}
		//See if any parallel executions should be started
		if (ws != null) {
			List parallelThreadStarts = WorkflowUtils.getParallelThreadStarts(ws.getDefinition(), state);
			for (int i = 0; i < parallelThreadStarts.size(); i++) {
				Map pT = (Map) parallelThreadStarts.get(i);
				String threadName = (String) pT.get(ObjectKeys.WORKFLOW_PARALLEL_THREAD_NAME);
				String startState = (String) pT.get(ObjectKeys.WORKFLOW_PARALLEL_THREAD_START_STATE);
				startParallelWorkflowThread(entry, threadName, startState);
			}
		}
		//Re-index the entry after changing its state
		//TODO add code to re-index the entry
		  
		System.out.println("Workflow event (" + eventType + ") recorded: " + state);
	}
	  
	protected void startParallelWorkflowThread(Entry entry, String threadName, String startState) {
		//See if there is a thread by this name already running
		Iterator itWorkflowStates = entry.getWorkflowStates().iterator();
		WorkflowState ws = null;
		while (itWorkflowStates.hasNext()) {
			WorkflowState ws1 = (WorkflowState) itWorkflowStates.next();
			if (ws1.getThreadName().equals(threadName)) {
				//There is a workflow state already running by this name. Use it
				//JbpmSession session = workflowFactory.getSession();
				//Token t = session.getGraphSession().loadToken(ws.getTokenId().longValue());
				//t.end();
				  
				ws = ws1;
			}
		}
		//Now start a new thread 
	}
	  
}
