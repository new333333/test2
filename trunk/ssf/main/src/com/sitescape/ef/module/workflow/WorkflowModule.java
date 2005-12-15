package com.sitescape.ef.module.workflow;
import java.util.List;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Binder;

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
	public Token addWorkflowSubToken(Long processInstanceId, String name);
	public void deleteProcessInstance(Long processInstanceId);
	public void deleteProcessDefinition(Long id);
	public ProcessInstance setNextTransition(Long processInstanceId);
	public ProcessInstance setTransition(Long processInstanceId, String transitionId);
	public ProcessInstance setNode(Long processInstanceId, String nodeId);
	public void buildProcessDefinition(String definitionName, Definition def);
	public void updateProcessDefinition(ProcessDefinition pD, Definition def);
	public void startWorkflow(Entry entry, Definition workflowDef);
	public void modifyWorkflowState(Long tokenId, String fromState, String toState);
	public void deleteEntryWorkflow(Binder parent, Entry entry);

}
