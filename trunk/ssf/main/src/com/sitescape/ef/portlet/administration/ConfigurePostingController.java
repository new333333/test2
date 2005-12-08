package com.sitescape.ef.portlet.administration;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.ArrayList;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.portlet.PortletMode;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.module.admin.PostingConfig;


public class ConfigurePostingController extends  SAbstractController implements DomTreeBuilder {
	
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn")) {
			Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.FORUM_URL_FORUM_ID));
			ArrayList postings = new ArrayList();
			
			
			response.setRenderParameters(formData);
		} else if (formData.containsKey("cancelBtn")) {
			response.setRenderParameter(WebKeys.ACTION, "");
			response.setWindowState(WindowState.NORMAL);
			response.setPortletMode(PortletMode.VIEW);
		} else
			response.setRenderParameters(formData);
	
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		try {
			Map model = new HashMap();
			Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.FORUM_URL_FORUM_ID));
			Folder folder = getFolderModule().getFolder(folderId);
			Folder topFolder = folder.getTopFolder();
			if (topFolder == null) {
				model.put(WebKeys.FOLDER_DOM_TREE, getFolderModule().getDomFolderTree(folderId, this));
			} else {
				model.put(WebKeys.FOLDER_DOM_TREE, getFolderModule().getDomFolderTree(topFolder.getId(), this));			
			}
			model.put(WebKeys.FOLDER, folder);
			PostingConfig config = getAdminModule().getPostingConfig();
			model.put(WebKeys.POSTING_CONFIG, config);		
			return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_POSTING, model); 
		} catch (Exception e) {
			//assume not selected yet
			Document wsTree = getWorkspaceModule().getDomWorkspaceTree(new TreeHelper(response));
			return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_POSTING, WebKeys.WORKSPACE_DOM_TREE, wsTree);		
		}
	}
	public Element setupDomElement(String type, Object source, Element element) {
		if (type.equals(DomTreeBuilder.TYPE_FOLDER)) {
			Folder f = (Folder)source;
			element.addAttribute("type", "forum");
			element.addAttribute("title", f.getTitle());
			element.addAttribute("id", f.getId().toString());
			element.addAttribute("image", "forum");
        	Element url = element.addElement("url");
	    	url.addAttribute(WebKeys.ACTION, WebKeys.FORUM_ACTION_VIEW_FORUM);
	     	url.addAttribute(WebKeys.FORUM_URL_FORUM_ID, f.getId().toString());
		} else return null;
		return element;
	}
	private class TreeHelper implements DomTreeBuilder {
		private RenderResponse response;
		public TreeHelper(RenderResponse response) {
			this.response = response;
		}
		public Element setupDomElement(String type, Object source, Element element) {
			PortletURL url;
			if (type.equals(DomTreeBuilder.TYPE_WORKSPACE)) {
				Workspace ws = (Workspace)source;
				element.addAttribute("type", "workspace");
				element.addAttribute("title", ws.getTitle());
				element.addAttribute("id", ws.getId().toString());
				element.addAttribute("image", "workspace");
				element.addAttribute("displayOnly", "true");
				element.addAttribute("url", "");
			} else if (type.equals(DomTreeBuilder.TYPE_FOLDER)) {
				Folder f = (Folder)source;
				element.addAttribute("type", "forum");
				element.addAttribute("title", f.getTitle());
				element.addAttribute("id", f.getId().toString());
				element.addAttribute("image", "forum");
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.POSTING_ACTION_CONFIGURE);
				url.setParameter(WebKeys.FORUM_URL_FORUM_ID, f.getId().toString());
				try {
					url.setWindowState(WindowState.MAXIMIZED);
				} catch (Exception e) {};
				try {
					url.setPortletMode(PortletMode.VIEW);
				} catch (Exception e) {};
				element.addAttribute("url", url.toString());

			} else return null;
			return element;
		}
	}	
}
