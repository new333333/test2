package com.sitescape.ef.portlet.administration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.portlet.PortletMode;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.security.function.Function;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.util.NLT;
import com.sitescape.util.Validator;
public class ManageSearchIndexController extends  SAbstractController {
	
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn")) {
			//Get the list of forums to be indexed
			List forumIdList = new ArrayList();
			
			//Get the forums to be indexed
			Iterator itFormData = formData.entrySet().iterator();
			while (itFormData.hasNext()) {
				Map.Entry me = (Map.Entry) itFormData.next();
				if (((String)me.getKey()).startsWith("id_")) {
					String forumId = ((String)me.getKey()).substring(3);
					forumIdList.add(forumId);
				}
			}
			
			//Now, index the forums
			Iterator itForums = forumIdList.iterator();
			while (itForums.hasNext()) {
				Long folderId = new Long((String)itForums.next());
				getFolderModule().indexFolderTree(folderId);
			}
			
			response.setRenderParameter("redirect", "true");
			
		} else if (formData.containsKey("cancelBtn")) {
			response.setRenderParameter("redirect", "true");
		} else
			response.setRenderParameters(formData);
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		if (!Validator.isNull(request.getParameter("redirect"))) {
			return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT);
		}

		Map model = new HashMap();
		
		Document wsTree = getWorkspaceModule().getDomWorkspaceTree(new WSTreeHelper(response));
		model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
			
		return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_SEARCH_INDEX, model);
	}
	private class WSTreeHelper implements DomTreeBuilder {
		private RenderResponse response;
		public WSTreeHelper(RenderResponse response) {
			this.response = response;
		}

		public Element setupDomElement(String type, Object source, Element element) {
			if (type.equals(DomTreeBuilder.TYPE_WORKSPACE)) {
				Workspace ws = (Workspace)source;
				element.addAttribute("type", "workspace");
				element.addAttribute("title", ws.getTitle());
				element.addAttribute("id", "");
				element.addAttribute("image", "workspace");
				element.addAttribute("displayOnly", "true");
				element.addAttribute("url", "");
			} else if (type.equals(DomTreeBuilder.TYPE_FOLDER)) {
				Folder f = (Folder)source;
				element.addAttribute("type", "folder");
				element.addAttribute("title", f.getTitle());
				element.addAttribute("id", f.getId().toString());
				element.addAttribute("image", "folder");
				element.addAttribute("url", "");
			} else return null;
			return element;
		}
	}
}
