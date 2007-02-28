package com.sitescape.team.portlet.profile;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.Toolbar;

public class ViewEntryController extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		response.setRenderParameters(formData);
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
			
		Map model = new HashMap();	
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		Map formData = request.getParameterMap();
		String viewPath = BinderHelper.getViewListingJsp(this, getViewType(binderId.toString()));
		if (formData.containsKey(WebKeys.RELOAD_URL_FORCED)) {
			PortletURL reloadUrl = response.createRenderURL();
			reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			reloadUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
			reloadUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_VIEW_ENTRY);
			reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
			model = new HashMap();
			model.put(WebKeys.RELOAD_URL_FORCED, reloadUrl.toString());			
			return new ModelAndView(viewPath, model);
		}
		model.put(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_ENTRY);
		Principal entry = getProfileModule().getEntry(binderId, entryId);
		
		model.put(WebKeys.ENTRY, entry);
		model.put(WebKeys.DEFINITION_ENTRY, entry);
		model.put(WebKeys.FOLDER, entry.getParentBinder());
		model.put(WebKeys.BINDER, entry.getParentBinder());
		model.put(WebKeys.CONFIG_JSP_STYLE, "view");
		model.put(WebKeys.USER_PROPERTIES, getProfileModule().getUserProperties(null).getProperties());
		//Get the definition used to view this entry
		Definition entryDef = entry.getEntryDef();
		if (entryDef == null) {
			DefinitionHelper.getDefaultEntryView(entry, model);
		} else {
			//Set up the definition used to show this profile entry
			if (!DefinitionHelper.getDefinition(entryDef, model, "//item[@name='profileEntryView']")) {
				DefinitionHelper.getDefaultEntryView(entry, model);
			}
		}
		//	Build the toolbar array
		Toolbar toolbar = new Toolbar();
		PortletURL url;
		if (getProfileModule().testAccess(entry, "modifyEntry")) {
			//	The "Modify" menu
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_PROFILE_ENTRY);
			url.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			url.setParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
			toolbar.addToolbarMenu("2_modify", NLT.get("toolbar.modify"), url);
		}
	
    
		//	The "Delete" menu
		if (getProfileModule().testAccess(entry, "deleteEntry")) {
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_PROFILE_ENTRY);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_DELETE);
			url.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			url.setParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
			toolbar.addToolbarMenu("3_delete", NLT.get("toolbar.delete"), url);
		}
    
		model.put(WebKeys.FOLDER_ENTRY_TOOLBAR, toolbar.getToolbar());
//		if (operation.equals("buddy")) 
//			return new ModelAndView("presence/view_entry", model);
		return new ModelAndView(BinderHelper.getViewListingJsp(this, getViewType(binderId.toString())), model);
	}	

	public String getViewType(String folderId) {

		User user = RequestContextHolder.getRequestContext().getUser();
		Binder binder = getBinderModule().getBinder(Long.valueOf(folderId));
		
		String viewType = "";
		
		//Check the access rights of the user
		if (getBinderModule().testAccess(binder, "setProperty")) {
			UserProperties userProperties = getProfileModule().getUserProperties(user.getId(), Long.valueOf(folderId)); 
			String displayDefId = (String) userProperties.getProperty(ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION);
			Definition displayDef = binder.getDefaultViewDef();
			if (displayDefId != null && !displayDefId.equals("")) {
				displayDef = DefinitionHelper.getDefinition(displayDefId);
			}
			Document defDoc = displayDef.getDefinition();
			
			if (defDoc != null) {
				Element rootElement = defDoc.getRootElement();
				Element elementView = (Element) rootElement.selectSingleNode("//item[@name='forumView' or @name='profileView' or @name='workspaceView' or @name='userWorkspaceView']");
				if (elementView != null) {
					Element viewElement = (Element)elementView.selectSingleNode("./properties/property[@name='type']");
					if (viewElement != null) viewType = viewElement.attributeValue("value", "");
				}
			}
		}
		return viewType;
	}		
	
} 