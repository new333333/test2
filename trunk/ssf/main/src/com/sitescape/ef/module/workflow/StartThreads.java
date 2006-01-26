package com.sitescape.ef.module.workflow;

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
import com.sitescape.ef.domain.AclControlledEntry;
import com.sitescape.ef.module.shared.WorkflowUtils;

/**
 * This node-enter action starts parallel threads .
 * @author Janet McCann
 *
 */
public class StartThreads extends AbstractActionHandler {
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
		logger.info("Begin start threads: " + stateName);
		if (ws != null) {
			//See if any parallel executions should be started
			//Note,we are in an intermediate state
			List parallelThreadStarts = WorkflowUtils.getParallelThreadStarts(ws.getDefinition(), stateName);
			try {
				ctx.setTransientVariable("ignoreChecks", "");
				for (int i = 0; i < parallelThreadStarts.size(); i++) {
					Map pT = (Map) parallelThreadStarts.get(i);
					String threadName = (String) pT.get(ObjectKeys.WORKFLOW_PARALLEL_THREAD_NAME);
					String startState = (String) pT.get(ObjectKeys.WORKFLOW_PARALLEL_THREAD_START_STATE);
					startParallelWorkflowThread(entry, threadName, startState, ws, token);
				}
			} finally {
				ctx.setTransientVariable("ignoreChecks", null);				
			}
		}
		logger.info("End start threads: " + stateName);
	}
	  
	protected void startParallelWorkflowThread(AclControlledEntry entry, String threadName, String startState, 
			WorkflowState currentWs, Token currentToken) {
		
		WorkflowState ws;
		JbpmSession session = getWorkflowFactory().getSession();
		Token root = currentToken.getProcessInstance().getRootToken();
		Token child = root.getChild(threadName);
		if (child != null) {
			ws = entry.getWorkflowState(new Long(child.getId()));
			if (ws != null) entry.removeWorkflowState(ws);
			child.end(false);
			//explictly delete it.
			session.getSession().delete(child);
		}
		ProcessInstance pI = currentToken.getProcessInstance();
        ProcessDefinition pD = pI.getProcessDefinition();
		//Now start a thread
		Token subToken = new Token(pI.getRootToken(), threadName);
		session.getSession().save(subToken);
		//Track state of thread
		ws = (WorkflowState) new WorkflowState();
		ws.setThreadName(threadName);
		ws.setTokenId(new Long(subToken.getId()));
		ws.setState(startState);
		//Use the same workflow definition as the current workflow state
		ws.setDefinition(currentWs.getDefinition());
		//need to save explicitly - actions called by the node.enter may look it up 
		getCoreDao().save(ws);
		entry.addWorkflowState(ws);
		
		Node node = pD.findNode(startState);
		node.enter(new ExecutionContext(subToken));
	}
	  
}
