package com.sitescape.team.module.workflow.support;

import java.util.Map;

public interface WorkflowCalloutHelper {
	public Object getVariable(String name);
	public Map getVariables();
	public void setVariable(String name, Object value);
}
