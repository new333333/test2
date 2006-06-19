package com.sitescape.ef.module.workflow;
import java.util.List;

import org.dom4j.Element;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.domain.WorkflowSupport;
/**
 * Handle setting variables when a node is exitted.
 * This is done as part on one action so we can maintain the ordering
 * specified in the definition and reduce the amount of synchronization needed between the
 * JBPM definition and the Sitescape definition.
 * @author Janet McCann
 *
 */
public class ExitEvent extends AbstractActionHandler {
	private static final long serialVersionUID = 1L;
	  
	//Indexing the entry is handled by the code that initiates a transition/nodeEnter
	//Because mutiple states can be effected, we don't want to re-index
	//each time.  Only need one at the end of the transaction
	public void execute( ExecutionContext executionContext ) throws Exception {
		ContextInstance ctx = executionContext.getContextInstance();
		Token token = executionContext.getToken();
		Long id = new Long(token.getId());
		String state = token.getNode().getName();
		WorkflowSupport entry = loadEntry(ctx);
		WorkflowState ws = entry.getWorkflowState(id);
		if (ws != null) {
			if (infoEnabled) logger.info("Workflow event (" + executionContext.getEvent().getEventType() + ")");
			//cancel timers associated with this state.
			token.getProcessInstance().getSchedulerInstance().cancel("onDataValue", token);
			List items  = WorkflowUtils.getItems(ws.getDefinition(), state);
			boolean check = false;
			for (int i=0; i<items.size(); ++i) {
				Element item = (Element)items.get(i);
	   			String name = item.attributeValue("name","");
	   			if ("onExit".equals(name)) {
	   				if (TransitionUtils.setVariables(item, executionContext, entry, ws)) {
	   					check = true;
	   				}
	   			} 
			}
			//See if other threads conditions are now met.
			if (check) TransitionUtils.processConditions(entry, token);
			
		}
	}
}

