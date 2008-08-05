package com.sitescape.team.module.workflow.jbpm;

import java.util.Map;

import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ExecutionContext;

import com.sitescape.team.module.workflow.support.WorkflowCallout;
public class CalloutHelper implements WorkflowCallout {
	private ExecutionContext executionContext;
	public CalloutHelper(ExecutionContext executionContext) {
		this.executionContext = executionContext;
	}
	public Object getVariable(String name) {
		return executionContext.getContextInstance().getVariable(name);
	}
	public Map getVariables() {
		return executionContext.getContextInstance().getVariables();
	}
	public void setVariable(String name, Object value) {
		ContextInstance cI = executionContext.getContextInstance();
		cI.setVariable(name, value);
	}
}
