package com.sitescape.ef.module.workflow;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.domain.WorkflowSupport;
import com.sitescape.ef.module.workflow.impl.JbpmContext;
import com.sitescape.ef.module.workflow.impl.WorkflowFactory;
import com.sitescape.util.Validator;

public class TransitionUtils {
	/**
	 * Process the conditions related to a state.  
	 * @param executionContext
	 * @param entry
	 * @param state
	 * @param isModify
	 * @return
	 */
	public static String processConditions(ExecutionContext executionContext, WorkflowSupport entry, WorkflowState state, boolean isModify) {
		List conditions = WorkflowUtils.getConditions(state.getDefinition(), state.getState());
		for (int i=0; i<conditions.size(); ++i) {
			Element condition = (Element)conditions.get(i);
//			if ((condition.getEntryDefId() != null) && (entryDef != null)) {
				//make sure condition applies to fields of this entry
//				if (!condition.getEntryDefId().equals(entryDef.getId())) continue;
//			} else if ((condition.getEntryDefId() != null) || (entryDef != null))  continue;
			//any modify triggers this
			String toState = WorkflowUtils.getProperty(condition, "toState");
			if (!Validator.isNull(toState)) {
				String type = condition.attributeValue("name", "");
				if (type.equals("conditionOnModify")) {
					if (isModify) return toState;
					
				} else if (type.equals("conditionOnResponse")) {
					
				} else if (type.equals("conditionOnValue")) {
					Object currentVal=null;
						
//						try {
//							currentVal = InvokeUtil.invokeGetter(entry, condition.getAttributeName());
//						} catch (ObjectPropertyNotFoundException pe) {
//							if (entry instanceof DefinableEntity) {
//								CustomAttribute attr = ((DefinableEntity)entry).getCustomAttribute(condition.getAttributeName());
//								if (attr != null) currentVal = attr.getValue();
//							}
							
//						}
						
				} else if (type.equals("waitForParallelThread")) {
					//	get names of threads we are waiting for
					List threads = WorkflowUtils.getPropertyList(condition, "name");
					boolean done = true;
					for (int j=0; j<threads.size(); ++j) {
						String threadName = (String)threads.get(j);
						if (!Validator.isNull(threadName)) {
							//See if child has ended
							WorkflowState child = entry.getWorkflowStateByThread(state.getDefinition(), threadName);
							//if found - still running
							if (child != null) {
								done = false;
								break;
							}
						}
					}
					if (done) return toState;
				} if (executionContext != null) { 
					if (type.equals("conditionOnVariable")) {
						String name = WorkflowUtils.getProperty(condition, "name");
						if (!Validator.isNull(name)) {
							String value = WorkflowUtils.getProperty(condition, "value");
							Object currentVal = executionContext.getVariable(name);
							if ((currentVal != null) && currentVal.equals(value)) 
								return toState;
							//Check if both null
							if (currentVal == value) return toState;
						}
					}
				}
			}
					
		}
		return null;
		
	}
	
	public static void processConditions(ExecutionContext executionContext, WorkflowSupport entry, boolean isModify) {
		processConditions(executionContext, entry, (Token)null, isModify);
	}
	public static void processConditions(ExecutionContext executionContext, WorkflowSupport entry, Token current, boolean isModify) {

		boolean found = true;
		//loop until we get through states without any changes occuring.  Each change could trigger another
		JbpmContext context=WorkflowFactory.getContext();
//JbpmContext context = executionContext.getJbpmContext();
		try {
			//not sure if it is necessary to run through the states multiple times
			while (found) {
				//assume no conditions will be met
				found = false;
				//	copy set because may change as we process each state
				Set states = new HashSet(entry.getWorkflowStates());
	
				for (Iterator iter=states.iterator(); iter.hasNext(); ) {
					WorkflowState ws = (WorkflowState)iter.next();
					if ((current != null) && (ws.getTokenId().longValue() == current.getId())) continue;
					Token t = context.loadTokenForUpdate(ws.getTokenId().longValue());
					//	make sure state hasn't been removed as the result of another thread
					if (t.hasEnded() || (ws.getOwner() == null)) continue;
					String toState = TransitionUtils.processConditions(executionContext, entry, ws, isModify);
					if (!Validator.isNull(toState)) {
						t.signal(ws.getState() + "." + toState);
						context.save(t);
						found = true;
					}
				}
				//don't trigger onModify conditions after the first time through
				isModify = false;
			}
		} finally {
			context.close();
		}
	}
		
}
