package com.sitescape.ef.portlet.administration;
import java.util.Map;
import java.util.HashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletURL;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;


public class ViewController extends  SAbstractController {
	
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		PortletURL url;
		//Build the tree
		Document adminTree = DocumentHelper.createDocument();
		Element rootElement = adminTree.addElement("root");
		rootElement.addAttribute("title", NLT.get("administration.title"));
		rootElement.addAttribute("image", "root");
		rootElement.addAttribute("displayOnly", "true");
		//Definition builder
		Element element = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
		element.addAttribute("title", NLT.get("administration.definition_builder"));
		element.addAttribute("image", "page");
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_DEFINITION_BUILDER);
		url.setWindowState(WindowState.MAXIMIZED);
		url.setPortletMode(PortletMode.VIEW);
		element.addAttribute("url", url.toString());
		
		//Ldap configuration
		element = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
		element.addAttribute("title", NLT.get("administration.configure_ldap"));
		element.addAttribute("image", "page");
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.LDAP_ACTION_CONFIGURE);
		url.setWindowState(WindowState.MAXIMIZED);
		url.setPortletMode(PortletMode.VIEW);
		element.addAttribute("url", url.toString());

		//Roles configuration
		element = rootElement.addElement("child");
		element.addAttribute("title", NLT.get("administration.configure_roles"));
		element.addAttribute("image", "page");
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ADMIN_ACTION_CONFIGURE_ROLES);
		url.setWindowState(WindowState.MAXIMIZED);
		url.setPortletMode(PortletMode.VIEW);
		element.addAttribute("url", url.toString());

		//Ldap configuration
		element = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
		element.addAttribute("title", NLT.get("administration.configure_notify"));
		element.addAttribute("image", "page");
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.NOTIFY_ACTION_CONFIGURE);
		url.setWindowState(WindowState.MAXIMIZED);
		url.setPortletMode(PortletMode.VIEW);
		element.addAttribute("url", url.toString());
		
		return new ModelAndView("administration/view", WebKeys.ADMIN_TREE, adminTree);
	}
}
