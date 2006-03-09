package com.sitescape.ef.portlet.profile;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.DefinitionUtils;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.Toolbar;

public class ViewEntryController extends SAbstractProfileController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		
		//See if the user asked to change state
		if (formData.containsKey("changeStateBtn")) {
			//Change the state
			//Get the workflow process to change and the name of the new state
	        Long tokenId = new Long(PortletRequestUtils.getRequiredLongParameter(request, "tokenId"));	
			String toState = PortletRequestUtils.getRequiredStringParameter(request, "toState");
			getProfileModule().modifyWorkflowState(folderId, entryId, tokenId, toState);
		}
		response.setRenderParameters(formData);
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
			
		request.setAttribute(WebKeys.ACTION, WebKeys.ACTION_VIEW_ENTRY);
		Map model = new HashMap();	
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		Principal entry = getProfileModule().getEntry(binderId, entryId);
		
		model.put(WebKeys.ENTRY_ID, entryId);
		model.put(WebKeys.ENTRY, entry);
		model.put(WebKeys.DEFINITION_ENTRY, entry);
		model.put(WebKeys.FOLDER, entry.getParentBinder());
		model.put(WebKeys.BINDER, entry.getParentBinder());
		model.put(WebKeys.CONFIG_JSP_STYLE, "view");
		model.put(WebKeys.USER_PROPERTIES, getProfileModule().getUserProperties(null).getProperties());
		//Get the definition used to view this entry
		Definition entryDef = entry.getEntryDef();
		if (entryDef == null) {
			DefinitionUtils.getDefaultEntryView(entry, model);
		} else {
			//Set up the definition used to show this profile entry
			if (!DefinitionUtils.getDefinition(entryDef, model, "//item[@name='profileEntryView']")) {
				DefinitionUtils.getDefaultEntryView(entry, model);
			}
		}
		//	Build the toolbar array
		Toolbar toolbar = new Toolbar();
		PortletURL url;
		try {
			getProfileModule().checkModifyEntryAllowed(entry);
			//	The "Modify" menu
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_ENTRY);
			url.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			url.setParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
			toolbar.addToolbarMenu("2_modify", NLT.get("toolbar.modify"), url);
		} catch (AccessControlException ac) {};
	
    
		//	The "Delete" menu
		try {
			getProfileModule().checkDeleteEntryAllowed(entry);
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DELETE_ENTRY);
			url.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			url.setParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
			toolbar.addToolbarMenu("3_delete", NLT.get("toolbar.delete"), url);
		} catch (AccessControlException ac) {};
    
		model.put(WebKeys.FOLDER_ENTRY_TOOLBAR, toolbar.getToolbar());
			return new ModelAndView(WebKeys.VIEW_LISTING, model);
	}	
	
} 