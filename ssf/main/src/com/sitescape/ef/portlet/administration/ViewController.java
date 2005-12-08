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

import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.portlet.PortletKeys;


public class ViewController extends  SAbstractController {
	
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		PortletURL url;
		Map model = new HashMap();
		//Build the tree
		Document wsTree = DocumentHelper.createDocument();
		Element rootElement = wsTree.addElement("root");
		rootElement.addAttribute("title", "Sitescape Administration");
		rootElement.addAttribute("image", "root");
		rootElement.addAttribute("displayOnly", "true");
		rootElement.addAttribute("id", "");
		//Definition builder
		Element element = rootElement.addElement("child");
		element.addAttribute("title", "Definition Builder");
		element.addAttribute("image", "page");
		element.addAttribute("id", "");
		url = response.createActionURL();
		url.setParameter(PortletKeys.ACTION, PortletKeys.FORUM_ACTION_DEFINITION_BUILDER);
		url.setWindowState(WindowState.MAXIMIZED);
		url.setPortletMode(PortletMode.VIEW);
		element.addAttribute("url", url.toString());
		//Ldap configuration
		element = rootElement.addElement("child");
		element.addAttribute("title", "LDAP");
		element.addAttribute("image", "page");
		element.addAttribute("id", "");
		url = response.createRenderURL();
		url.setParameter(PortletKeys.ACTION, PortletKeys.LDAP_ACTION_CONFIGURE);
		url.setWindowState(WindowState.MAXIMIZED);
		url.setPortletMode(PortletMode.VIEW);
		element.addAttribute("url", url.toString());

		model.put("wsTree", wsTree);
		return new ModelAndView("administration/view", model);
		
	}
}
