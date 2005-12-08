package com.sitescape.ef.portlet.widget_test;


import java.util.Map;
import java.util.HashMap;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.jbpm.db.GraphSession;
import org.jbpm.db.JbpmSession;
import org.jbpm.db.JbpmSessionFactory;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.springframework.web.servlet.ModelAndView;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.portlet.forum.ActionUtil;
import com.sitescape.ef.web.WebKeys;

public class WorkflowController extends SAbstractController {
	//  static JbpmSessionFactory jbpmSessionFactory = 
	 //     JbpmSessionFactory.buildJbpmSessionFactory();

	  public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
     throws Exception {
	    Map formData = request.getParameterMap();
		response.setRenderParameters(formData);
		String operation=ActionUtil.getStringValue(formData,WebKeys.FORUM_URL_OPERATION);
		if (operation.equals("create")) {
		    ProcessDefinition processDefinition = getWorkflowModule().addWorkflow(
		    	      "<process-definition name='hello world'>" +
		    	      "  <start-state name='start'>" +
		    	      "    <transition to='s' />" +
		    	      "  </start-state>" +
		    	      "  <state name='s'>" +
		    	      "    <transition to='end' />" +
		    	      "  </state>" +
		    	      "  <end-state name='end' />" +
		    	      "</process-definition>"
		    	    );
		    response.setRenderParameter("workflowId", String.valueOf(processDefinition.getId()));
		 } else if (operation.equals("new")) {
				String id=ActionUtil.getStringValue(formData,"workflowId");
				ProcessInstance processInstance = getWorkflowModule().addWorkflowInstance(Long.valueOf(id));
		    
			    Token token = processInstance.getRootToken(); 
			    response.setRenderParameter("processId", String.valueOf(processInstance.getId()));
			    response.setRenderParameter("workflowState", token.getNode().getName());

	 } else if (operation.equals("proceed")) {
		    
		    // Now we can query the database for the process definition that we 
		    // deployed above. 
			String id=ActionUtil.getStringValue(formData,"processId");
			ProcessInstance processInstance = getWorkflowModule().setNextTransition(Long.valueOf(id));
		    
		    Token token = processInstance.getRootToken(); 
	    response.setRenderParameter("processId", String.valueOf(processInstance.getId()));
		    response.setRenderParameter("workflowState", token.getNode().getName());

	 }

			
	 }

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
		RenderResponse response) throws Exception {
		Map formData = request.getParameterMap();    
		Map results = new HashMap();
		
		results.put("workflowId", ActionUtil.getStringValue(formData,"workflowId"));
		results.put("processId", ActionUtil.getStringValue(formData,"processId"));
		results.put("workflowState",ActionUtil.getStringValue(formData,"workflowState"));
		String path = "widget_test/view_workflow";
		return new ModelAndView(path, results);

    
	}
}
