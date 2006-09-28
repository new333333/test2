package com.sitescape.ef.portlet.profile;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.module.shared.EntityIndexUtils;
import com.sitescape.ef.module.shared.MapInputData;
import com.sitescape.ef.portletadapter.AdaptedPortletURL;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.BinderHelper;
import com.sitescape.ef.web.util.DashboardHelper;
import com.sitescape.ef.web.util.DefinitionHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.Toolbar;
import com.sitescape.util.Validator;

public class ListProfilesController extends   SAbstractController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
        User user = RequestContextHolder.getRequestContext().getUser();
		if (op.equals(WebKeys.OPERATION_SET_DISPLAY_STYLE)) {
			Map updates = new HashMap();
			updates.put("displayStyle", PortletRequestUtils.getStringParameter(request,WebKeys.URL_VALUE,""));
			getProfileModule().modifyEntry(user.getParentBinder().getId(), user.getId(), new MapInputData(updates));
		} else if (op.equals(WebKeys.OPERATION_SELECT_FILTER)) {
				getProfileModule().setUserProperty(user.getId(), binderId, ObjectKeys.USER_PROPERTY_USER_FILTER, 
						PortletRequestUtils.getStringParameter(request, WebKeys.OPERATION_SELECT_FILTER,""));
		}
		response.setRenderParameters(request.getParameterMap());
		
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
 		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		HashMap model = new HashMap();

		if (op.equals(WebKeys.OPERATION_RELOAD_LISTING)) {
			//An action is asking us to build the url to reload the parent page
			PortletURL reloadUrl = response.createRenderURL();
			reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
			model.put("ssReloadUrl", reloadUrl.toString());
			return new ModelAndView(BinderHelper.getViewListingJsp(), model);
		} else if (op.equals(WebKeys.OPERATION_VIEW_ENTRY)) {
			String entryId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
			if (!entryId.equals("")) {
				AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_ENTRY);
				adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
				adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
				request.setAttribute("ssLoadEntryUrl", adapterUrl.toString());			
				request.setAttribute("ssLoadEntryId", entryId);			
			}
		}

	   	User user = RequestContextHolder.getRequestContext().getUser();
		model.put(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
		//Build a reload url
		PortletURL reloadUrl = response.createRenderURL();
		reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
		model.put(WebKeys.RELOAD_URL, reloadUrl.toString());
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
		String searchFilterName = (String)userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_USER_FILTER);
		Map users = null;
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(ObjectKeys.LISTING_MAX_PAGE_SIZE));
		options.put(ObjectKeys.SEARCH_SORT_BY, EntityIndexUtils.TITLE1_FIELD);
		options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.FALSE);
		if (!Validator.isNull(searchFilterName)) {
			Map searchFilters = (Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS);
			options.put(ObjectKeys.SEARCH_SEARCH_FILTER, (Document)searchFilters.get(searchFilterName));
			users = getProfileModule().getUsers(binderId, options);
		} else {
			users = getProfileModule().getUsers(binderId, options);
		}
		ProfileBinder binder = (ProfileBinder)users.get(ObjectKeys.BINDER);
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.FOLDER, binder);
		model.put(WebKeys.ENTRIES, users.get(ObjectKeys.SEARCH_ENTRIES));
		model.put(WebKeys.USER_FOLDER_PROPERTIES, userFolderProperties);
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		model.put(WebKeys.USER_PROPERTIES, userProperties);
		model.put(WebKeys.SEEN_MAP,getProfileModule().getUserSeenMap(user.getId()));
		DashboardHelper.getDashboardMap(binder, userFolderProperties, 
				userProperties, model);
		DefinitionHelper.getDefinitions(binder, model);
		Object obj = model.get(WebKeys.CONFIG_ELEMENT);
		if ((obj == null) || (obj.equals(""))) 
			return new ModelAndView(WebKeys.VIEW_NO_DEFINITION, model);
		obj = model.get(WebKeys.CONFIG_DEFINITION);
		if ((obj == null) || (obj.equals(""))) 
			return new ModelAndView(WebKeys.VIEW_NO_DEFINITION, model);
		model.put(WebKeys.FOLDER_TOOLBAR, buildViewToolbar(request, response, binder).getToolbar());
		return new ModelAndView(BinderHelper.getViewListingJsp(), model);
	}

	protected Toolbar buildViewToolbar(RenderRequest request, RenderResponse response, ProfileBinder binder) {
		PortletURL url;
		String binderId = binder.getId().toString();
		//Build the toolbar array
		Toolbar toolbar = new Toolbar();
		//	The "Add" menu (Turned off because adding users must be done in the portal)
/*		List defaultEntryDefinitions = binder.getEntryDefinitions();
		if (!defaultEntryDefinitions.isEmpty()) {
			try {
				getProfileModule().checkAddEntryAllowed(binder);
				int count = 1;
				toolbar.addToolbarMenu("1_add", NLT.get("toolbar.add"));
				Map qualifiers = new HashMap();
				String onClickPhrase = "if (self.ss_addEntry) {return(self.ss_addEntry(this))} else {return true;}";
				qualifiers.put(ObjectKeys.TOOLBAR_QUALIFIER_ONCLICK, onClickPhrase);
				for (int i=0; i<defaultEntryDefinitions.size(); ++i) {
					Definition def = (Definition) defaultEntryDefinitions.get(i);
					AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
					adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_PROFILE_ENTRY);
					adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId);
					adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, def.getId());
					String title = NLT.getDef(def.getTitle());
					if (toolbar.checkToolbarMenuItem("1_add", "entries", title)) {
						title = title + " (" + String.valueOf(count++) + ")";
					}
					toolbar.addToolbarMenuItem("1_add", "entries", title, adapterUrl.toString(), qualifiers);
				}
			} catch (AccessControlException ac) {};
		}
*/
		
		//The "Administration" menu
		toolbar.addToolbarMenu("2_administration", NLT.get("toolbar.administration"));
		//Access control
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACCESS_CONTROL);
		url.setParameter(WebKeys.URL_BINDER_ID, binderId);
		url.setParameter(WebKeys.URL_BINDER_TYPE, binder.getEntityIdentifier().getEntityType().name());
		toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.accessControl"), url);
		//Configuration
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_FORUM);
		url.setParameter(WebKeys.URL_BINDER_ID, binderId);
		url.setParameter(WebKeys.URL_BINDER_TYPE, binder.getEntityIdentifier().getEntityType().name());
		toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.configuration"), url);
		//Definition builder
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
		url.setParameter(WebKeys.URL_BINDER_ID, binderId);
		url.setParameter(WebKeys.URL_BINDER_TYPE, binder.getEntityIdentifier().getEntityType().name());
		toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.definition_builder"), url);
		
		//	The "Display styles" menu
		toolbar.addToolbarMenu("3_display_styles", NLT.get("toolbar.display_styles"));
		//vertical
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.URL_BINDER_ID, binderId);
		url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_VERTICAL);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_vertical"), url);
		//accessible
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.URL_BINDER_ID, binderId);
		url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_accessible"), url);
		//iframe
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.URL_BINDER_ID, binderId);
		url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_IFRAME);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_iframe"), url);
		//popup
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.URL_BINDER_ID, binderId);
		url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_POPUP);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_popup"), url);
		return toolbar;
		
	}

}
