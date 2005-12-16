
package com.sitescape.ef.portlet.profile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.DefinitionUtils;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.Toolbar;

import org.springframework.web.servlet.ModelAndView;

/**
 * @author Janet McCann
 *
 */
public class SAbstractProfileController extends SAbstractController {
	public ModelAndView returnToView(RenderRequest request, RenderResponse response) throws Exception {
		HashMap model = new HashMap();
	   	User user = RequestContextHolder.getRequestContext().getUser();
		request.setAttribute(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);

//	temp to bootstrap
	getProfileModule().addProfileBinder();
		Map users = getProfileModule().getUsers();
		ProfileBinder binder = (ProfileBinder)users.get(ObjectKeys.BINDER);
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.FOLDER, binder);
		model.put(WebKeys.ENTRIES, users.get(ObjectKeys.ENTRIES));
		DefinitionUtils.getDefinitions(binder, model);
		Object obj = model.get(WebKeys.CONFIG_ELEMENT);
		if ((obj == null) || (obj.equals(""))) 
			return new ModelAndView(WebKeys.VIEW_NO_DEFINITION, model);
		obj = model.get(WebKeys.CONFIG_DEFINITION);
		if ((obj == null) || (obj.equals(""))) 
			return new ModelAndView(WebKeys.VIEW_NO_DEFINITION, model);
		model.put(WebKeys.USER_PROPERTIES, getProfileModule().getUserProperties(user.getId()).getProperties());
		model.put(WebKeys.FOLDER_TOOLBAR, buildViewToolbar(response, binder).getToolbar());
		return new ModelAndView(WebKeys.VIEW_LISTING, model);
	}
	public ModelAndView returnToViewEntry(RenderRequest request, RenderResponse response) throws Exception {
		
		Map model = new HashMap();	
		request.setAttribute(WebKeys.ACTION, WebKeys.ACTION_VIEW_ENTRY);
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		ProfileBinder binder = getProfileModule().getProfileBinder();
		Principal entry = getProfileModule().getPrincipal(entryId);
	
		model.put(WebKeys.ENTRY_ID, entryId);
		model.put(WebKeys.ENTRY, entry);
		model.put(WebKeys.DEFINITION_ENTRY, entry);
		model.put(WebKeys.FOLDER, binder);
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.CONFIG_JSP_STYLE, "view");
		model.put(WebKeys.USER_PROPERTIES, getProfileModule().getUserProperties(null).getProperties());
		//Get the definition used to view this entry
		Definition entryDef = entry.getEntryDef();
		if (entryDef == null) {
			//There is no definition for this entry; get the default for the binder
			List profileDefinitions = binder.getEntryDefs();
			for (int i = 0; i < profileDefinitions.size(); i++) {
				//Look for the first profile entry definition
				Definition def = (Definition) profileDefinitions.get(i);
				if (def.getType() == Definition.PROFILE_ENTRY_VIEW) {
					//Found the first profile entry definition
					entryDef = def;
					break;
				}
			}
		}
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
		//	The "Modify" menu
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_ENTRY);
		url.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		url.setParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
		toolbar.addToolbarMenu("2_modify", NLT.get("toolbar.modify"), url);
	
    
		//	The "Delete" menu
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DELETE_ENTRY);
		url.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		url.setParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
		toolbar.addToolbarMenu("3_delete", NLT.get("toolbar.delete"), url);
    
		model.put(WebKeys.FOLDER_ENTRY_TOOLBAR, toolbar.getToolbar());

		return new ModelAndView(WebKeys.VIEW_LISTING, model);
	}	
	protected Toolbar buildViewToolbar(RenderResponse response, Binder binder) {
		//Build the toolbar array
		Toolbar toolbar = new Toolbar();
		//	The "Add" menu
		String binderId = binder.getId().toString();
		List defaultEntryDefinitions = binder.getEntryDefs();
		PortletURL url;
		if (!defaultEntryDefinitions.isEmpty()) {
			toolbar.addToolbarMenu("1_add", NLT.get("toolbar.add"));
			for (int i=0; i<defaultEntryDefinitions.size(); ++i) {
				Definition def = (Definition) defaultEntryDefinitions.get(i);
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_ENTRY);
				url.setParameter(WebKeys.URL_BINDER_ID, binderId);
				url.setParameter(WebKeys.URL_ENTRY_TYPE, def.getId());
				toolbar.addToolbarMenuItem("1_add", "entries", def.getTitle(), url);
			}
		}
    
		//The "Administration" menu
		toolbar.addToolbarMenu("2_administration", NLT.get("toolbar.administration"));
		//Configuration
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_CONFIGURE_FORUM);
		url.setParameter(WebKeys.URL_BINDER_ID, binderId);
		toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.configuration"), url);
		//Definition builder
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_DEFINITION_BUILDER);
		url.setParameter(WebKeys.URL_BINDER_ID, binderId);
		toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.definition_builder"), url);
		
		//	The "Display styles" menu
		toolbar.addToolbarMenu("3_display_styles", NLT.get("toolbar.display_styles"));
		/**
		//horizontal
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.URL_BINDER_ID, forumId);
		url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_HORIZONTAL);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_horizontal"), url);
		*/
		//vertical
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.URL_BINDER_ID, binderId);
		url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_VERTICAL);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_vertical"), url);
		//accessible
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.URL_BINDER_ID, binderId);
		url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_accessible"), url);
		//iframe
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.URL_BINDER_ID, binderId);
		url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_IFRAME);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_iframe"), url);
		//popup
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.URL_BINDER_ID, binderId);
		url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_POPUP);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_popup"), url);
		return toolbar;
		
	}

}
