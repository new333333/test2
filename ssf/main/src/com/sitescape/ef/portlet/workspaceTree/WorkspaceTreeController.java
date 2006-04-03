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

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.domain.EntityIdentifier.EntityType;
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
		
		Map<String,Object> model = new HashMap<String,Object>();
		Long binderId= PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);				
		if ((binderId == null)) {
			binderId = getWorkspaceModule().getWorkspace().getId();
		}
		Binder binder = getBinderModule().getBinder(binderId);

		PortletSession ses = WebHelper.getRequiredPortletSession(request);
		Document wsTree = (Document)ses.getAttribute(WebKeys.WORKSPACE_DOM_TREE);
		Long wsTreeId = (Long)ses.getAttribute(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID);
		if (wsTree == null || wsTreeId == null || !binderId.equals(wsTreeId)) {
			wsTree = getWorkspaceModule().getDomWorkspaceTree(binderId, new WsTreeBuilder((Workspace)binder, true), 1);
			//Save the tree for the session as a performance improvement
			ses.setAttribute(WebKeys.WORKSPACE_DOM_TREE, wsTree);
			ses.setAttribute(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, binderId);
		}
		model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
		model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, binderId.toString());
			
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
			element.addAttribute("type", "folder");
			element.addAttribute("title", f.getTitle());
			element.addAttribute("id", f.getId().toString());
			element.addAttribute("image", "folder");
        	url = element.addElement("url");
	    	url.addAttribute(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
	     	url.addAttribute(WebKeys.URL_BINDER_ID, f.getId().toString());
		} else return null;
		return element;
	}

	protected class WsTreeBuilder implements DomTreeBuilder {
		Workspace bottom;
		boolean check;
		public WsTreeBuilder(Workspace ws, boolean checkChildren) {
			this.bottom = ws;
			this.check = checkChildren;
		}
		public Element setupDomElement(String type, Object source, Element element) {
			Element url;
			Binder binder = (Binder) source;
			element.addAttribute("title", binder.getTitle());
			element.addAttribute("id", binder.getId().toString());

			//only need this information if this is the bottom of the tree
			if (check && bottom.equals(binder.getParentBinder())) {
				if (getBinderModule().hasBinders(binder)) {
					element.addAttribute("hasChildren", "true");
				} else {	
					element.addAttribute("hasChildren", "false");
				}
			}
			if (type.equals(DomTreeBuilder.TYPE_WORKSPACE)) {
				Workspace ws = (Workspace)source;
				element.addAttribute("type", "workspace");
				element.addAttribute("image", "workspace");
			} else if (type.equals(DomTreeBuilder.TYPE_FOLDER)) {
				Folder f = (Folder)source;
				element.addAttribute("type", "folder");
				element.addAttribute("image", "folder");
			} else return null;
			return element;
		}
	}
}