package com.sitescape.ef.portlet.widget_test;


import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.context.exe.ContextInstance;

import org.springframework.web.servlet.ModelAndView;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.util.PortletRequestUtils;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.domain.AnyOwner;
import com.sitescape.util.Validator;
import com.sitescape.ef.module.workflow.impl.WorkflowFactory;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
public class WorkflowController extends SAbstractController {

	public void handleActionRequestInternal(ActionRequest request, ActionResponse response)
	throws Exception {
		//There is no action. Just go to the render phase
		response.setRenderParameters(request.getParameterMap());
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		Map results = new HashMap();
		String pId=PortletRequestUtils.getStringParameter(request,"processId", "");
		String wId=PortletRequestUtils.getStringParameter(request,"workflowId", "");
		String state = "";
		PlatformTransactionManager txManager = (PlatformTransactionManager)SpringContextUtil.getBean("transactionManager");
		String operation=PortletRequestUtils.getStringParameter(request,WebKeys.FORUM_URL_OPERATION);
		if (operation.equals("create")) {
		    ProcessDefinition processDefinition = getWorkflowModule().addWorkflow(
		    	      "<process-definition name='hello world'>" +
		    	      " <action name='recordState' class='com.sitescape.ef.module.workflow.RecordState' config-type='bean'/>" +
		    	      "  <start-state name='start'>" +
		    	      "    <transition to='notify' />" +
		    	      "  </start-state>" +
		    	      "  <state name='notify'>" +
		    	      " <event type='node-enter'>" +
		    	      " <action ref-name='recordState'> " +
		    	      " <wfState>test notify</wfState></action>" +
		    	      " <action class='com.sitescape.ef.module.workflow.Notify' config-type='bean'>" +
		    	      " <principals>10,25,100</principals>" +
		    	      " <subject>State change</subject>" +
		    	      " <body>Blah, blah, blah...</body>" +
		    	      " </action></event>" +
		    	      "    <transition to='end' />" +
		    	      "  </state>" +
		    	      "  <state name='orphan'>" +
		    	      "    <transition to='orphan2' />" +
		    	      "  </state>" +
		    	      "  <state name='orphan2'>" +
		    	      "    <transition to='orphan' />" +
		    	      "  </state>" +
		    	      "  <end-state name='end' />" +
		    	      "</process-definition>"
		    	    );
		    wId = String.valueOf(processDefinition.getId());
		 } else if (operation.equals("new")) {
		    	//pick the first
		    	if (Validator.isNull(wId)) {
		    		List defs = getWorkflowModule().getLatestDefinitions();
		    		wId = String.valueOf(((ProcessDefinition)defs.get(0)).getId());
		    	}
		    	ProcessInstance processInstance = getWorkflowModule().addWorkflowInstance(Long.valueOf(wId));
		    	pId = String.valueOf(processInstance.getId());
		    	Token token = processInstance.getRootToken(); 
			    	
			    state= token.getNode().getName();

		 } else if (operation.equals("proceed")) {
		    	// Now we can query the database for the process definition that we 
		    	// deployed above. 
		    	pId=PortletRequestUtils.getRequiredStringParameter(request,"processId");
		    	ProcessInstance processInstance = getWorkflowModule().setNextTransition(Long.valueOf(pId));
		    
		    	Token token = processInstance.getRootToken(); 
			    state= token.getNode().getName();
			    wId = String.valueOf(processInstance.getProcessDefinition().getId());

		 } else if (operation.equals("orphan")) {
		    	// Now we can query the database for the process definition that we 
		    	// deployed above. 
			 	pId=PortletRequestUtils.getStringParameter(request,"processId", "");
			 	ProcessInstance processInstance = null;
			 	if (!pId.equals("")) {
		    		processInstance = getWorkflowModule().setNode(Long.valueOf(pId), "orphan");
		    	}
		    	if (processInstance == null) {
		    		state = "not found";
		    	} else {
		    		Token token = processInstance.getRootToken(); 
		    		Node currentNode = token.getNode();
		    		if (currentNode != null) {
			    		state= currentNode.getName();
			    		wId = String.valueOf(processInstance.getProcessDefinition().getId());
		    		} else {
			    		state = "not found";
		    		}
		    	}
		 } else if (operation.equals("listDef")) {
		    	// Now we can query the database for the list of process definitions 
		    	List definitions = getWorkflowModule().getAllDefinitions();
		    	results.put("definitions", definitions);

		    	// Now we can query the database for the process definition that we 
		    	// deployed above. 
		    	if (!Validator.isNull(pId)) {
		    		ProcessInstance processInstance = getWorkflowModule().getProcessInstance(Long.valueOf(pId));
		    		
		    		Token token = processInstance.getRootToken(); 
				    state= token.getNode().getName();
				    wId = String.valueOf(processInstance.getProcessDefinition().getId());
		    	} 

		 } else if (operation.equals("listInst")) {
		    	// Now we can query the database for the process definition that we 
		    	// deployed above. 
		    	wId=PortletRequestUtils.getRequiredStringParameter(request,"workflowId");
		    	List processInstances = getWorkflowModule().getProcessInstances(Long.valueOf(wId));
		    	results.put("instances", processInstances);
		    	
		    	if (!Validator.isNull(pId)) {
		    		ProcessInstance processInstance = getWorkflowModule().getProcessInstance(Long.valueOf(pId));
		    
		    		Token token = processInstance.getRootToken(); 
				    state= token.getNode().getName();
				    wId = String.valueOf(processInstance.getProcessDefinition().getId());
		    	} 
		 } else if (operation.equals("modifyNodeName")) {
		    	wId=PortletRequestUtils.getRequiredStringParameter(request,"workflowId");
			    TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
			    try {
			    	ProcessDefinition pD = getWorkflowModule().getWorkflow(Long.valueOf(wId));
			    	if (pD.hasNode("orphan")) pD.getNode("orphan").setName("orphan3");
			    	else if (pD.hasNode("orphan3")) pD.getNode("orphan3").setName("orphan");
			    	txManager.commit(status);
			    } catch (Exception e) {
			    	txManager.rollback(status);
			    } 
			    pId=PortletRequestUtils.getStringParameter(request,"processId", "");
			    if (!Validator.isNull(pId)) {
			    	ProcessInstance processInstance = getWorkflowModule().getProcessInstance(Long.valueOf(pId));
			    	Token token = processInstance.getRootToken(); 
				    state= token.getNode().getName();
				    wId = String.valueOf(processInstance.getProcessDefinition().getId());
			    } 
			    		
		 } else if (operation.equals("addNode")) {
		    	wId=PortletRequestUtils.getRequiredStringParameter(request,"workflowId");
			    TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
			    try {
			    	ProcessDefinition pD = getWorkflowModule().getWorkflow(Long.valueOf(wId));
			    	if (!pD.hasNode("orphan") && !pD.hasNode("orphan3")) {
			    		Node node = new Node("orphan");
			    		node.setProcessDefinition(pD);
			    		WorkflowFactory wf = (WorkflowFactory)SpringContextUtil.getBean("workflowFactory");
			    		wf.getSession().getSession().save(node);
			    		
			    		Transition transition = new Transition();
			    		wf.getSession().getSession().save(transition);
			    		transition.setProcessDefinition(pD);
			    		transition.setTo(pD.getNode("orphan2"));
			    		node.addLeavingTransition(transition);
			    		pD.addNode(node);
			    		
			    		Transition transition2 = new Transition();
			    		wf.getSession().getSession().save(transition2);
			    		transition2.setProcessDefinition(pD);
			    		transition2.setTo(pD.getNode("orphan"));
			    		Iterator itTransitions = pD.getNode("orphan2").getLeavingTransitions().iterator();
			    		while (itTransitions.hasNext()) {
			    			pD.getNode("orphan2").removeLeavingTransition((Transition)itTransitions.next());
			    		}
			    		pD.getNode("orphan2").addLeavingTransition(transition2);
			    	}
			    	txManager.commit(status);
			    } catch (Exception e) {
			    	txManager.rollback(status);
			    } 
			    pId=PortletRequestUtils.getStringParameter(request,"processId", "");
			    if (!Validator.isNull(pId)) {
			    	ProcessInstance processInstance = getWorkflowModule().getProcessInstance(Long.valueOf(pId));
			    	Token token = processInstance.getRootToken(); 
				    state= token.getNode().getName();
				    wId = String.valueOf(processInstance.getProcessDefinition().getId());
			    } 
			    		
		 } else if (operation.equals("deleteNode")) {
		    	wId=PortletRequestUtils.getRequiredStringParameter(request,"workflowId");
			    TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
			    try {
			    	ProcessDefinition pD = getWorkflowModule().getWorkflow(Long.valueOf(wId));
			    	if (pD.hasNode("orphan")) pD.removeNode(pD.getNode("orphan"));
			    	else if (pD.hasNode("orphan3")) pD.removeNode(pD.getNode("orphan3"));
			    	txManager.commit(status);
			    } catch (Exception e) {
			    	txManager.rollback(status);
			    } 
			    pId=PortletRequestUtils.getStringParameter(request,"processId", "");
			    if (!Validator.isNull(pId)) {
			    	ProcessInstance processInstance = getWorkflowModule().getProcessInstance(Long.valueOf(pId));
			    	Token token = processInstance.getRootToken(); 
				    state= token.getNode().getName();
				    wId = String.valueOf(processInstance.getProcessDefinition().getId());
			    } 
			    		
		 } else if (operation.equals("deleteAll")) {
			 	//clear out the junk
		    	List definitions = getWorkflowModule().getAllDefinitions();
		    	for (int i=0; i<definitions.size(); ++i) {
		    		ProcessDefinition pD = (ProcessDefinition)definitions.get(i);
			    	Iterator processInstances = getWorkflowModule().getProcessInstances(new Long(pD.getId())).listIterator();
			    	while (processInstances.hasNext()) {
			    		ProcessInstance pI = (ProcessInstance) processInstances.next();
			    		getWorkflowModule().deleteProcessInstance(new Long(pI.getId()));
			    		
			    	}
		    		getWorkflowModule().deleteProcessDefinition(new Long(pD.getId()));
		    	}
		 }
	    results.put("processId", pId);
	    results.put("workflowState", state);
	    results.put("workflowId", wId);
		
		String path = "widget_test/view_workflow";
		return new ModelAndView(path, results);
	 }

}
