package com.sitescape.ef.module.workflow;
import java.util.List;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.domain.WorkflowSupport;

public interface WorkflowModule {
	public List getAllDefinitions();
	public List getAllDefinitions(String name);
	public List getLatestDefinitions();
	public List getNodes(Long id);
	public List getProcessInstances(Long id);
	public ProcessInstance getProcessInstance(Long id);
	public ProcessDefinition getWorkflow(Long id);
	public ProcessDefinition addWorkflow(String xmlString);
	public ProcessInstance addWorkflowInstance(Long id);
	public void deleteProcessInstance(Long processInstanceId);
	public void deleteProcessDefinition(Long id);
	public void deleteProcessDefinition(String name);
	public ProcessInstance setNextTransition(Long processInstanceId);
	public ProcessInstance setTransition(Long processInstanceId, String transitionId);
	public ProcessInstance setNode(Long processInstanceId, String nodeId);
	public void modifyProcessDefinition(String definitionName, Definition def);
	public void modifyProcessDefinition(ProcessDefinition pD, Definition def);
	public void addEntryWorkflow(WorkflowSupport entry, EntityIdentifier id, Definition workflowDef);
	public void deleteEntryWorkflow(WorkflowSupport entry);
	public void modifyWorkflowState(WorkflowSupport entry, WorkflowState state, String toState);
	public void modifyWorkflowStateOnReply(WorkflowSupport entry);
	public void modifyWorkflowStateOnUpdate(WorkflowSupport entry);
	public void modifyWorkflowStateOnTimeout(Long timerId);
}
