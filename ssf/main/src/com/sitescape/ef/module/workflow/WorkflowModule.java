package com.sitescape.ef.module.workflow;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

public interface WorkflowModule {
	public ProcessDefinition getWorkflow(Long id);
	public ProcessDefinition addWorkflow(String xmlString);
	public ProcessInstance addWorkflowInstance(Long id);
	public ProcessInstance setNextTransition(Long processInstanceId);
	public ProcessInstance setTransition(Long processInstanceId, String transitionId);
	public ProcessInstance setNode(Long processInstanceId, String nodeId);


}
