package com.sitescape.ef.module.workflow;
import java.util.List;

import org.jbpm.db.GraphSession;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

public interface WorkflowModule {
	public List getAllDefinitions();
	public List getLatestDefinitions();
	public List getNodes(Long id);
	public List getProcessInstances(Long id);
	public ProcessInstance getProcessInstance(Long id);
	public ProcessDefinition getWorkflow(Long id);
	public ProcessDefinition addWorkflow(String xmlString);
	public ProcessInstance addWorkflowInstance(Long id);
	public void deleteProcessInstance(Long processInstanceId);
	public void deleteProcessDefinition(Long id);
	public ProcessInstance setNextTransition(Long processInstanceId);
	public ProcessInstance setTransition(Long processInstanceId, String transitionId);
	public ProcessInstance setNode(Long processInstanceId, String nodeId);


}
