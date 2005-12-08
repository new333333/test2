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
import org.springframework.web.servlet.ModelAndView;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.util.PortletRequestUtils;
public class WorkflowController extends SAbstractController {

	  public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
     throws Exception {
	    Map formData = request.getParameterMap();
		response.setRenderParameters(formData);
		String operation=PortletRequestUtils.getStringParameter(request,WebKeys.FORUM_URL_OPERATION);
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
				String id=PortletRequestUtils.getRequiredStringParameter(request,"workflowId");
				ProcessInstance processInstance = getWorkflowModule().addWorkflowInstance(Long.valueOf(id));
		    
			    Token token = processInstance.getRootToken(); 
			    response.setRenderParameter("processId", String.valueOf(processInstance.getId()));
			    response.setRenderParameter("workflowState", token.getNode().getName());

	 } else if (operation.equals("proceed")) {
		    
		    // Now we can query the database for the process definition that we 
		    // deployed above. 
			String id=PortletRequestUtils.getRequiredStringParameter(request,"processId");
			ProcessInstance processInstance = getWorkflowModule().setNextTransition(Long.valueOf(id));
		    
		    Token token = processInstance.getRootToken(); 
	        response.setRenderParameter("processId", String.valueOf(processInstance.getId()));
		    response.setRenderParameter("workflowState", token.getNode().getName());

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
