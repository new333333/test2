package com.sitescape.ef.portlet.widget_test;


import java.util.Map;
import java.util.HashMap;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.jbpm.graph.def.ProcessDefinition;
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
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
public class WorkflowController extends SAbstractController {

	  public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
     throws Exception {
	    Map formData = request.getParameterMap();
	    PlatformTransactionManager txManager = (PlatformTransactionManager)SpringContextUtil.getBean("transactionManager");
		response.setRenderParameters(formData);
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
		    response.setRenderParameter("workflowId", String.valueOf(processDefinition.getId()));
		 } else if (operation.equals("new")) {
			    TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
			    try {
			    	String id=PortletRequestUtils.getRequiredStringParameter(request,"workflowId");
			    	ProcessInstance processInstance = getWorkflowModule().addWorkflowInstance(Long.valueOf(id));
		    
			    	Token token = processInstance.getRootToken(); 
			    	ContextInstance ctx = processInstance.getContextInstance();
			    	ctx.createVariable("entryType", AnyOwner.FOLDERENTRY);
			    	ctx.createVariable("entryId", new Long(1));
			    	
				    txManager.commit(status);
			    	response.setRenderParameter("processId", String.valueOf(processInstance.getId()));
			    	response.setRenderParameter("workflowState", token.getNode().getName());
				    response.setRenderParameter("workflowId", id);
			    } catch (Exception e) {
			    	txManager.rollback(status);
			    } 

		 } else if (operation.equals("proceed")) {
			    TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
			    try {
			    	// Now we can query the database for the process definition that we 
			    	// deployed above. 
			    	String id=PortletRequestUtils.getRequiredStringParameter(request,"processId");
			    	ProcessInstance processInstance = getWorkflowModule().setNextTransition(Long.valueOf(id));
			    	TaskInstance taskInstance = processInstance.getTaskMgmtInstance().createStartTaskInstance();
		    
			    	Token token = processInstance.getRootToken(); 
				    txManager.commit(status);
				    response.setRenderParameter("processId", String.valueOf(processInstance.getId()));
				    response.setRenderParameter("workflowState", token.getNode().getName());
				    response.setRenderParameter("workflowId", String.valueOf(processInstance.getProcessDefinition().getId()));
			    } catch (Exception e) {
			    	txManager.rollback(status);
			    } 

		 } else if (operation.equals("orphan")) {
			    TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
			    try {
			    	// Now we can query the database for the process definition that we 
			    	// deployed above. 
			    	String id=PortletRequestUtils.getRequiredStringParameter(request,"processId");
			    	ProcessInstance processInstance = getWorkflowModule().setNode(Long.valueOf(id), "orphan");
			    	//TaskInstance taskInstance = processInstance.getTaskMgmtInstance().createStartTaskInstance();
		    
			    	Token token = processInstance.getRootToken(); 
				    txManager.commit(status);
				    response.setRenderParameter("processId", String.valueOf(processInstance.getId()));
				    response.setRenderParameter("workflowState", token.getNode().getName());
				    response.setRenderParameter("workflowId", String.valueOf(processInstance.getProcessDefinition().getId()));
			    } catch (Exception e) {
			    	txManager.rollback(status);
			    } 

		 }
			
	 }

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
		RenderResponse response) throws Exception {
		Map results = new HashMap();
		
		results.put("workflowId", PortletRequestUtils.getStringParameter(request,"workflowId", ""));
		results.put("processId", PortletRequestUtils.getStringParameter(request,"processId", ""));
		results.put("workflowState",PortletRequestUtils.getStringParameter(request,"workflowState"));
		String path = "widget_test/view_workflow";
		return new ModelAndView(path, results);

    
	}
}
