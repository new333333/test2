package com.sitescape.team.portlet.administration;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.util.Validator;


public class ViewController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
//		getAdminModule().setZone();
		response.setRenderParameters(request.getParameterMap());
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
 		Map<String,Object> model = new HashMap<String,Object>();
 		PortletPreferences prefs = request.getPreferences();
		String ss_initialized = (String)prefs.getValue(WebKeys.PORTLET_PREF_INITIALIZED, null);
		if (Validator.isNull(ss_initialized)) {
			prefs.setValue(WebKeys.PORTLET_PREF_INITIALIZED, "true");
			//Signal that this is the initialization step
			model.put(WebKeys.PORTLET_INITIALIZATION, "1");
			
			PortletURL url;
			url = response.createRenderURL();
			model.put(WebKeys.PORTLET_INITIALIZATION_URL, url);
			prefs.store();
		}
		
		PortletURL url;
		//Build the tree
		int nextId = 0;

		Document adminTree = DocumentHelper.createDocument();
		Element rootElement = adminTree.addElement("root");
		rootElement.addAttribute("title", NLT.get("administration.title"));
		rootElement.addAttribute("image", "admin_tools");
		rootElement.addAttribute("displayOnly", "true");
		rootElement.addAttribute("id", String.valueOf(nextId++));

		
		//Definition builders
		Element designerElement = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
		designerElement.addAttribute("title", NLT.get("administration.definition_builder_designers"));
		designerElement.addAttribute("image", "bullet");
		designerElement.addAttribute("displayOnly", "true");
		designerElement.addAttribute("id", String.valueOf(nextId++));
		
		//Definition builder - Entry form designer
		Element element = designerElement.addElement(DomTreeBuilder.NODE_CHILD);
		element.addAttribute("title", NLT.get("administration.definition_builder_entry_form_designer"));
		element.addAttribute("image", "bullet");
		element.addAttribute("id", String.valueOf(nextId++));
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
		url.setParameter(WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, String.valueOf(Definition.FOLDER_ENTRY));
		url.setWindowState(WindowState.MAXIMIZED);
		url.setPortletMode(PortletMode.VIEW);
		element.addAttribute("url", url.toString());
				
		//Definition builder - Folder view designer
		element = designerElement.addElement(DomTreeBuilder.NODE_CHILD);
		element.addAttribute("title", NLT.get("administration.definition_builder_folder_view_designer"));
		element.addAttribute("image", "bullet");
		element.addAttribute("id", String.valueOf(nextId++));
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
		url.setParameter(WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, String.valueOf(Definition.FOLDER_VIEW));
		url.setWindowState(WindowState.MAXIMIZED);
		url.setPortletMode(PortletMode.VIEW);
		element.addAttribute("url", url.toString());
				
		//Definition builder - Workflow designer
		element = designerElement.addElement(DomTreeBuilder.NODE_CHILD);
		element.addAttribute("title", NLT.get("administration.definition_builder_workflow_designer"));
		element.addAttribute("image", "bullet");
		element.addAttribute("id", String.valueOf(nextId++));
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
		url.setParameter(WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, String.valueOf(Definition.WORKFLOW));
		url.setWindowState(WindowState.MAXIMIZED);
		url.setPortletMode(PortletMode.VIEW);
		element.addAttribute("url", url.toString());
		
		//Definition builder - Profile listing designer
		element = designerElement.addElement(DomTreeBuilder.NODE_CHILD);
		element.addAttribute("title", NLT.get("administration.definition_builder_profile_listing_designer"));
		element.addAttribute("image", "bullet");
		element.addAttribute("id", String.valueOf(nextId++));
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
		url.setParameter(WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, String.valueOf(Definition.PROFILE_VIEW));
		url.setWindowState(WindowState.MAXIMIZED);
		url.setPortletMode(PortletMode.VIEW);
		element.addAttribute("url", url.toString());
		
		//Definition builder - Profile designer
		element = designerElement.addElement(DomTreeBuilder.NODE_CHILD);
		element.addAttribute("title", NLT.get("administration.definition_builder_profile_designer"));
		element.addAttribute("image", "bullet");
		element.addAttribute("id", String.valueOf(nextId++));
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
		url.setParameter(WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, String.valueOf(Definition.PROFILE_ENTRY_VIEW));
		url.setWindowState(WindowState.MAXIMIZED);
		url.setPortletMode(PortletMode.VIEW);
		element.addAttribute("url", url.toString());
		
		//Definition builder - Workspace designer
		element = designerElement.addElement(DomTreeBuilder.NODE_CHILD);
		element.addAttribute("title", NLT.get("administration.definition_builder_workspace_designer"));
		element.addAttribute("image", "bullet");
		element.addAttribute("id", String.valueOf(nextId++));
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
		url.setParameter(WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, String.valueOf(Definition.WORKSPACE_VIEW));
		url.setWindowState(WindowState.MAXIMIZED);
		url.setPortletMode(PortletMode.VIEW);
		element.addAttribute("url", url.toString());
		
		//Definition builder - User workspace designer
		element = designerElement.addElement(DomTreeBuilder.NODE_CHILD);
		element.addAttribute("title", NLT.get("administration.definition_builder_user_workspace_designer"));
		element.addAttribute("image", "bullet");
		element.addAttribute("id", String.valueOf(nextId++));
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
		url.setParameter(WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, String.valueOf(Definition.USER_WORKSPACE_VIEW));
		url.setWindowState(WindowState.MAXIMIZED);
		url.setPortletMode(PortletMode.VIEW);
		element.addAttribute("url", url.toString());
		
		//Ldap configuration
		element = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
		element.addAttribute("title", NLT.get("administration.configure_ldap"));
		element.addAttribute("image", "bullet");
		element.addAttribute("id", String.valueOf(nextId++));
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_LDAP_CONFIGURE);
		url.setWindowState(WindowState.MAXIMIZED);
		url.setPortletMode(PortletMode.VIEW);
		element.addAttribute("url", url.toString());

		//Roles configuration
		element = rootElement.addElement("child");
		element.addAttribute("title", NLT.get("administration.configure_roles"));
		element.addAttribute("image", "bullet");
		element.addAttribute("id", String.valueOf(nextId++));
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ADMIN_ACTION_CONFIGURE_ROLES);
		url.setWindowState(WindowState.MAXIMIZED);
		url.setPortletMode(PortletMode.VIEW);
		element.addAttribute("url", url.toString());

		//Notification configuration
		element = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
		element.addAttribute("title", NLT.get("administration.configure_email"));
		element.addAttribute("image", "bullet");
		element.addAttribute("id", String.valueOf(nextId++));
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIG_EMAIL);
		url.setWindowState(WindowState.MAXIMIZED);
		url.setPortletMode(PortletMode.VIEW);
		element.addAttribute("url", url.toString());
		
		//Posting schedule
		element = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
		element.addAttribute("title", NLT.get("administration.configure_posting_job"));
		element.addAttribute("image", "bullet");
		element.addAttribute("id", String.valueOf(nextId++));
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_POSTINGJOB_CONFIGURE);
		url.setWindowState(WindowState.MAXIMIZED);
		url.setPortletMode(PortletMode.VIEW);
		element.addAttribute("url", url.toString());
		
		//Search index
		element = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
		element.addAttribute("title", NLT.get("administration.configure_search_index"));
		element.addAttribute("image", "bullet");
		element.addAttribute("id", String.valueOf(nextId++));
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_FOLDER_INDEX_CONFIGURE);
		url.setWindowState(WindowState.MAXIMIZED);
		url.setPortletMode(PortletMode.VIEW);
		element.addAttribute("url", url.toString());

		//Definition profiles
		element = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
		element.addAttribute("title", NLT.get("administration.import.profiles"));
		element.addAttribute("image", "bullet");
		element.addAttribute("id", String.valueOf(nextId++));
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_PROFILES_IMPORT);
		url.setWindowState(WindowState.MAXIMIZED);
		url.setPortletMode(PortletMode.VIEW);
		element.addAttribute("url", url.toString());
	
		//Definition import
		element = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
		element.addAttribute("title", NLT.get("administration.import.definitions"));
		element.addAttribute("image", "bullet");
		element.addAttribute("id", String.valueOf(nextId++));
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_IMPORT);
		url.setWindowState(WindowState.MAXIMIZED);
		url.setPortletMode(PortletMode.VIEW);
		element.addAttribute("url", url.toString());

		element = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
		element.addAttribute("title", NLT.get("administration.export.definitions"));
		element.addAttribute("image", "bullet");
		element.addAttribute("id", String.valueOf(nextId++));
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_EXPORT);
		url.setWindowState(WindowState.MAXIMIZED);
		url.setPortletMode(PortletMode.VIEW);
		element.addAttribute("url", url.toString());

		element = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
		element.addAttribute("title", NLT.get("administration.configure_configurations"));
		element.addAttribute("image", "bullet");
		element.addAttribute("id", String.valueOf(nextId++));
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
		url.setWindowState(WindowState.MAXIMIZED);
		url.setPortletMode(PortletMode.VIEW);
		element.addAttribute("url", url.toString());

		element = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
		element.addAttribute("title", NLT.get("administration.view_change_log"));
		element.addAttribute("image", "bullet");
		element.addAttribute("id", String.valueOf(nextId++));
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_CHANGELOG);
		url.setWindowState(WindowState.MAXIMIZED);
		url.setPortletMode(PortletMode.VIEW);
		element.addAttribute("url", url.toString());
		
		model.put(WebKeys.ADMIN_TREE, adminTree);
		model.put(WebKeys.USER_PRINCIPAL, RequestContextHolder.getRequestContext().getUser());
		return new ModelAndView("administration/view", model);
	}
}
