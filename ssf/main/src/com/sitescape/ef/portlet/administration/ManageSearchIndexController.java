package com.sitescape.ef.portlet.administration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.util.Validator;
public class ManageSearchIndexController extends  SAbstractController {
	
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn")) {
			//Get the list of binders to be indexed
			List binderIdList = new ArrayList();
			
			//Get the binders to be indexed
			Iterator itFormData = formData.entrySet().iterator();
			while (itFormData.hasNext()) {
				Map.Entry me = (Map.Entry) itFormData.next();
				if (((String)me.getKey()).startsWith("id_")) {
					String binderId = ((String)me.getKey()).substring(3);
					binderIdList.add(binderId);
				}
			}
			
			//Now, index the binders
			Iterator itBinders = binderIdList.iterator();
			while (itBinders.hasNext()) {
				Long folderId = new Long((String)itBinders.next());
				getBinderModule().indexTree(folderId);
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
    	Document wsTree = DocumentHelper.createDocument();
    	Element rootElement = wsTree.addElement(DomTreeBuilder.NODE_ROOT);
    	Element users = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
    	ProfileBinder p = getProfileModule().getProfileBinder();
    	users.addAttribute("type", "people");
    	users.addAttribute("title", p.getTitle());
    	users.addAttribute("id", p.getId().toString());
		String icon = p.getIconName();
		if (Validator.isNull(icon)) {
	    	users.addAttribute("image", "people");
		} else {
			users.addAttribute("image", icon);
			users.addAttribute("imageClass", "ss_twIcon");
		}
		users.addAttribute("url", "");
    	Document result = getWorkspaceModule().getDomWorkspaceTree(new WSTreeHelper());
    	rootElement.appendAttributes(result.getRootElement());
    	rootElement.appendContent(result.getRootElement());
		model.put(WebKeys.DOM_TREE, wsTree);
			
		return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_SEARCH_INDEX, model);
	}
	private class WSTreeHelper implements DomTreeBuilder {
		public WSTreeHelper() {
		}

		public Element setupDomElement(String type, Object source, Element element) {
			if (type.equals(DomTreeBuilder.TYPE_WORKSPACE)) {
				Workspace ws = (Workspace)source;
				String icon = ws.getIconName();
				String imageClass = "ss_twIcon";
				if (icon == null || icon.equals("")) {
					icon = "/icons/workspace.gif";
					imageClass = "ss_twImg";
				}
				element.addAttribute("type", "workspace");
				element.addAttribute("title", ws.getTitle());
				element.addAttribute("id", "");
				element.addAttribute("image", icon);
				element.addAttribute("imageClass", imageClass);
				element.addAttribute("displayOnly", "true");
				element.addAttribute("url", "");
			} else if (type.equals(DomTreeBuilder.TYPE_FOLDER)) {
				Folder f = (Folder)source;
				String icon = f.getIconName();
				if (icon == null || icon.equals("")) icon = "/icons/folder.png";
				element.addAttribute("type", "folder");
				element.addAttribute("title", f.getTitle());
				element.addAttribute("id", f.getId().toString());
				element.addAttribute("image", icon);
				element.addAttribute("imageClass", "ss_twIcon");
				element.addAttribute("url", "");
			} else return null;
			return element;
		}
	}
}
