package com.sitescape.ef.module.workflow;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
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
	protected Log logger = LogFactory.getLog(getClass());
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
		//Get the WorkflowState object associated with this token
		ws = (WorkflowState)getCoreDao().load(WorkflowState.class, id);
		if (ws != null) {
			ws.setState(state);
			
			//See if any parallel executions should be started
			List parallelThreadStarts = WorkflowUtils.getParallelThreadStarts(ws.getDefinition(), state);
			for (int i = 0; i < parallelThreadStarts.size(); i++) {
				Map pT = (Map) parallelThreadStarts.get(i);
				String threadName = (String) pT.get(ObjectKeys.WORKFLOW_PARALLEL_THREAD_NAME);
				String startState = (String) pT.get(ObjectKeys.WORKFLOW_PARALLEL_THREAD_START_STATE);
				startParallelWorkflowThread(entry, threadName, startState, ws, token);
			}
			//Re-index the entry after changing its state
			//TODO add code to re-index the entry
			  
			logger.info("Workflow event (" + eventType + ") recorded: " + state);
		}
	}
	  
	protected void startParallelWorkflowThread(Entry entry, String threadName, String startState, 
			WorkflowState currentWs, Token currentToken) {
		//See if there is a thread by this name already running
		Iterator itWorkflowStates = entry.getWorkflowStates().iterator();
		WorkflowState ws = null;
		while (itWorkflowStates.hasNext()) {
			WorkflowState ws1 = (WorkflowState) itWorkflowStates.next();
			if (threadName.equals(ws1.getThreadName())) {
				ws = ws1;
				break;
			}
		}
		JbpmSession session = getWorkflowFactory().getSession();
		ProcessInstance pI = currentToken.getProcessInstance();
        ProcessDefinition pD = pI.getProcessDefinition();
		//Now start a thread
		Token subToken = new Token(pI.getRootToken(), threadName);
		session.getSession().save(subToken);
		if (ws != null) {
			//There is a workflow state already running by this name. Use it
			Token t = session.getGraphSession().loadToken(ws.getTokenId().longValue());
			t.end();
		    ws.setThreadName(threadName);
		    ws.setTokenId(new Long(subToken.getId()));
			ws.setState(startState);
		} else {
			ws = (WorkflowState) new WorkflowState();
		    ws.setThreadName(threadName);
		    ws.setTokenId(new Long(subToken.getId()));
			ws.setState(startState);
			//Use the same workflow definition as the current workflow state
			ws.setDefinition(currentWs.getDefinition());
			//need to save explicitly - actions called by the node.enter may look it up 
			getCoreDao().save(ws);
			entry.addWorkflowState(ws);
		}
		Node node = pD.findNode(startState);
		node.enter(new ExecutionContext(subToken));
	}
	  
}
