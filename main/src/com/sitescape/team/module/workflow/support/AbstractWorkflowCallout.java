package com.sitescape.team.module.workflow.support;
import java.util.Map;
import java.util.HashMap;

/**
 * Abstact class to kick off customization in workflow processing
 * @author Janet
 *
 */
public abstract class AbstractWorkflowCallout implements WorkflowCallout {
	protected WorkflowCalloutHelper helper;
	public void setHelper(WorkflowCalloutHelper helper) {
		this.helper = helper;		
	}
	public Object getVariable(String name) {
		if (helper == null) return null;
		return helper.getVariable(name);
	}
	public Map getVariables() {
		if (helper == null) return new HashMap();
		return helper.getVariables();
	}
	public void setVariable(String name, Object value) {
		if (helper == null) return;
		helper.setVariable(name, value);
	}
}
