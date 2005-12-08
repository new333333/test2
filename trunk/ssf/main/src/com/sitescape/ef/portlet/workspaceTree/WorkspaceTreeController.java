package com.sitescape.ef.portlet.workspaceTree;

import java.util.HashMap;
import java.util.Map;


import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.portlet.PortletSession;

import org.dom4j.Document;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.web.portlet.SAbstractController;

/**
 * @author Peter Hurley
 *
 */
public class WorkspaceTreeController extends SAbstractController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		PortletSession ses = request.getPortletSession();
		

		Map model = new HashMap();
		Document wsTree = (Document)ses.getAttribute(ObjectKeys.WORKSPACE_DOM_TREE);
		if (wsTree == null) {
			wsTree = getWorkspaceModule().getDomWorkspaceTree();
			//Save the tree for the session as a performance improvement
			ses.setAttribute(ObjectKeys.WORKSPACE_DOM_TREE, wsTree);
		}
		model.put(ObjectKeys.WORKSPACE_DOM_TREE, wsTree);
			
	    return new ModelAndView("workspacetree/view", model);
	}
}