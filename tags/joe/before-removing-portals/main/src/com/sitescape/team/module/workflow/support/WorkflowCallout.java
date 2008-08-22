package com.sitescape.team.module.workflow.support;

import java.util.Map;

/**
 * Workflow engine neutral access to variables
 * 
 * @author Janet
 *
 */
public interface WorkflowCallout {
	public Object getVariable(String name);
	public Map getVariables();
	public void setVariable(String name, Object value);

}
