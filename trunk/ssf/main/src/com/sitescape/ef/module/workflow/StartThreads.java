package com.sitescape.ef.module.workflow;

import java.util.List;
import java.util.Map;
import java.util.Date;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.db.JbpmSession;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.domain.WorkflowControlledEntry;
import com.sitescape.ef.module.shared.WorkflowUtils;
import com.sitescape.ef.module.workflow.impl.WorkflowFactory;

/**
 * This node-enter action starts parallel threads .
 * @author Janet McCann
 *
 */
public class StartThreads extends AbstractActionHandler {
	private static final long serialVersionUID = 1L;
	  
	public void execute( ExecutionContext executionContext ) throws Exception {
		ContextInstance ctx = executionContext.getContextInstance();
		Token token = executionContext.getToken();
		Long id = new Long(token.getId());
		Node current = token.getNode();
		String stateName = current.getName();
		WorkflowControlledEntry entry = loadEntry(ctx);
		WorkflowState ws = entry.getWorkflowState(id);
		 //record event may not have happened yet
		ws.setState(stateName);
		if (infoEnabled) logger.info("Start threads begin at: " + stateName);
		if (ws != null) {
			//See if any parallel executions should be started
			//Note,we are in an intermediate state
			List parallelThreadStarts = WorkflowUtils.getParallelThreadStarts(ws.getDefinition(), stateName);
			for (int i = 0; i < parallelThreadStarts.size(); i++) {
				Map pT = (Map) parallelThreadStarts.get(i);
				String threadName = (String) pT.get(ObjectKeys.WORKFLOW_PARALLEL_THREAD_NAME);
				String startState = (String) pT.get(ObjectKeys.WORKFLOW_PARALLEL_THREAD_START_STATE);
				startParallelWorkflowThread(entry, threadName, startState, ws, token);
				if (infoEnabled) logger.info("Start threads start: " + threadName);
			}
		}
		if (infoEnabled) logger.info("Start threads end at: " + stateName);
	}
	  
	protected void startParallelWorkflowThread(WorkflowControlledEntry entry, String threadName, String startState, 
			WorkflowState currentWs, Token currentToken) {
		
		JbpmSession session = WorkflowFactory.getSession();
		
		//if thread exists, terminate it
		WorkflowState thread = entry.getWorkflowStateByThread(currentWs.getDefinition(), threadName);
		if (thread != null) {
			Token childToken = session.getGraphSession().loadToken(thread.getTokenId().longValue());
			childToken.end(false);
			entry.removeWorkflowState(thread);
		}
		ProcessInstance pI = currentToken.getProcessInstance();
        ProcessDefinition pD = pI.getProcessDefinition();
		//Now start a thread - since threads can be restarted and we don't delete old
        //tokens, each thread instance needs a unique name
        //This also implies we cannot look child tokens up by name cause we don't know it
        //the 'real' thread name is kept in WorkflowState
		Token subToken = new Token(pI.getRootToken(), threadName + "-" + new Date());
		session.getSession().save(subToken);
		//Track state of thread
		thread = (WorkflowState) new WorkflowState();
		thread.setThreadName(threadName);
		thread.setTokenId(new Long(subToken.getId()));
		thread.setState(startState);
		//Use the same workflow definition as the current workflow state
		thread.setDefinition(currentWs.getDefinition());
		//need to save explicitly - actions called by the node.enter may look it up 
		getCoreDao().save(thread);
		entry.addWorkflowState(thread);
			
		Node node = pD.findNode(startState);
		node.enter(new ExecutionContext(subToken));
	}
	  
}
