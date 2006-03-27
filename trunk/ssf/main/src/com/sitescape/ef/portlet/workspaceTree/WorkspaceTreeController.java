package com.sitescape.ef.portlet.workspaceTree;

import java.util.HashMap;
import java.util.Map;


import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import javax.portlet.PortletSession;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.NoFolderByTheIdException;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.WebHelper;

/**
 * @author Peter Hurley
 *
 */
public class WorkspaceTreeController extends SAbstractController implements DomTreeBuilder {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		Long folderId = null;
		folderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);				
		if (folderId != null) {
			//redirect handler too forum action
		    response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
		    response.setWindowState(WindowState.MAXIMIZED);
		}
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		Map model = new HashMap();
		PortletSession ses = WebHelper.getRequiredPortletSession(request);
		Document wsTree = (Document)ses.getAttribute(WebKeys.WORKSPACE_DOM_TREE);
		if (wsTree == null) {
			wsTree = getWorkspaceModule().getDomWorkspaceTree(this);
			//Save the tree for the session as a performance improvement
			ses.setAttribute(WebKeys.WORKSPACE_DOM_TREE, wsTree);
		}
		model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
			
	    return new ModelAndView("workspacetree/view", model);
	}

	public Element setupDomElement(String type, Object source, Element element) {
		Element url;
		if (type.equals(DomTreeBuilder.TYPE_WORKSPACE)) {
			Workspace ws = (Workspace)source;
			element.addAttribute("type", "workspace");
			element.addAttribute("title", ws.getTitle());
			element.addAttribute("id", ws.getId().toString());
			element.addAttribute("image", "workspace");
        	url = element.addElement("url");
	    	url.addAttribute(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
	     	url.addAttribute(WebKeys.URL_BINDER_ID, ws.getId().toString());
		} else if (type.equals(DomTreeBuilder.TYPE_FOLDER)) {
			Folder f = (Folder)source;
			element.addAttribute("type", "forum");
			element.addAttribute("title", f.getTitle());
			element.addAttribute("id", f.getId().toString());
			element.addAttribute("image", "forum");
        	url = element.addElement("url");
	    	url.addAttribute(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
	     	url.addAttribute(WebKeys.URL_BINDER_ID, f.getId().toString());
		} else return null;
		return element;
	}
}