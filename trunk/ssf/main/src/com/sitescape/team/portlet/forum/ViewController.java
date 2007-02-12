package com.sitescape.team.portlet.forum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.dom4j.Document;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.NoObjectByTheIdException;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DashboardPortlet;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.shared.WsDomTreeBuilder;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.DashboardHelper;
import com.sitescape.team.web.util.FindIdsHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.Toolbar;
import com.sitescape.util.Validator;



/**
 * @author Peter Hurley
 *
 */
public class ViewController  extends SAbstractController {
	public static final String FORUM_PORTLET="ss_forum";
	public static final String PRESENCE_PORTLET="ss_presence";
	public static final String WORKSPACE_PORTLET="ss_workspacetree";
	public static final String TOOLBAR_PORTLET="ss_toolbar";
	public static final String BLOG_SUMMARY_PORTLET="ss_blog";
	public static final String GUESTBOOK_SUMMARY_PORTLET="ss_guestbook";
	public static final String WIKI_PORTLET="ss_wiki";
	public static final String SEARCH_PORTLET="ss_search";
	public static final String PORTLET_COMPONENT_ID =DashboardHelper.Portlet+"_0";

	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
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

		String displayType = (String)prefs.getValue(WebKeys.PORTLET_PREF_TYPE, null);
		if (Validator.isNull(displayType)) {
			PortletConfig pConfig = (PortletConfig)request.getAttribute(WebKeys.JAVAX_PORTLET_CONFIG);
			String pName = pConfig.getPortletName();
			//For liferay we use instances and the name will be changed slightly
			//That is why we check for the name with contains
			if (pName.contains(FORUM_PORTLET))
				displayType=FORUM_PORTLET;
			else if (pName.contains(WORKSPACE_PORTLET))
				displayType=WORKSPACE_PORTLET;
			else if (pName.contains(PRESENCE_PORTLET))
				displayType=PRESENCE_PORTLET;
			else if (pName.contains(TOOLBAR_PORTLET)) 
				displayType=TOOLBAR_PORTLET;
			else if (pName.contains(BLOG_SUMMARY_PORTLET)) 
					displayType=BLOG_SUMMARY_PORTLET;
			else if (pName.contains(GUESTBOOK_SUMMARY_PORTLET)) 
				displayType=GUESTBOOK_SUMMARY_PORTLET;
			else if (pName.contains(SEARCH_PORTLET)) 
				displayType=SEARCH_PORTLET;
			else if (pName.contains(WIKI_PORTLET)) 
				displayType=WIKI_PORTLET;
		}
			
        User user = RequestContextHolder.getRequestContext().getUser();

		if (FORUM_PORTLET.equals(displayType)) {
			//Build the toolbar and add it to the model
			buildForumToolbar(response.getNamespace(), model);
		
			//This is the portlet view; get the configured list of folders to show
			String[] preferredBinderIds = prefs.getValues(WebKeys.FORUM_PREF_FORUM_ID_LIST, new String[0]);

			//Build the jsp bean (sorted by folder title)
			List<Long> binderIds = new ArrayList<Long>();
			for (int i = 0; i < preferredBinderIds.length; i++) {
				binderIds.add(new Long(preferredBinderIds[i]));
			}
			model.put(WebKeys.FOLDER_LIST, getFolderModule().getFolders(binderIds));
			response.setProperty(RenderResponse.EXPIRATION_CACHE,"300");
			return new ModelAndView(WebKeys.VIEW_FORUM, model);
		} else if (WORKSPACE_PORTLET.equals(displayType)) {
			String id = prefs.getValue(WebKeys.WORKSPACE_PREF_ID, null);
			Workspace binder;
			try {
				binder = getWorkspaceModule().getWorkspace(Long.valueOf(id));
			} catch (Exception ex) {
				binder = getWorkspaceModule().getWorkspace();				
			}
			Document wsTree;
			//when at the top, don't expand
			if (request.getWindowState().equals(WindowState.NORMAL)) {
				wsTree = getWorkspaceModule().getDomWorkspaceTree(binder.getId(), new WsDomTreeBuilder(null, true, this), 0);
			} else {
				wsTree = getWorkspaceModule().getDomWorkspaceTree(binder.getId(), new WsDomTreeBuilder((Workspace)binder, true, this), 1);									
			}
			model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
			model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, binder.getId().toString());
				
		    return new ModelAndView("workspacetree/view", model);
		    
		} else if (PRESENCE_PORTLET.equals(displayType)) {
 			Set ids = new HashSet();		
 			ids.addAll(FindIdsHelper.getIdsAsLongSet(prefs.getValue(WebKeys.PRESENCE_PREF_USER_LIST, "")));
 			ids.addAll(FindIdsHelper.getIdsAsLongSet(prefs.getValue(WebKeys.PRESENCE_PREF_GROUP_LIST, "")));
 			//This is the portlet view; get the configured list of principals to show
 			model.put(WebKeys.USERS, getProfileModule().getUsersFromPrincipals(ids));
 			//if we list groups, then we have issues when a user appears in multiple groups??
 			//how do we update the correct divs??
 			//so, explode the groups and just show members
 			//TODO: either deal with groups correctly or remove
  			response.setProperty(RenderResponse.EXPIRATION_CACHE,"300");
  			Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
  			Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
  			if ((binderId != null) && (entryId != null)) {
  				model.put(WebKeys.URL_BINDER_ID, binderId);
  				model.put(WebKeys.URL_ENTRY_ID, entryId);
  			}
 			return new ModelAndView(WebKeys.VIEW_PRESENCE, model);				
		} else if (TOOLBAR_PORTLET.equals(displayType)) {
 			return new ModelAndView(WebKeys.VIEW_TOOLBAR, model);		
		} else if (BLOG_SUMMARY_PORTLET.equals(displayType)) {
			return setupSummaryPortlets(request, prefs, model, WebKeys.VIEW_BLOG_SUMMARY, true);		
		} else if (WIKI_PORTLET.equals(displayType)) {
			return setupSummaryPortlets(request, prefs, model, WebKeys.VIEW_WIKI, true);		
		} else if (GUESTBOOK_SUMMARY_PORTLET.equals(displayType)) {
			return setupSummaryPortlets(request, prefs, model, WebKeys.VIEW_GUESTBOOK_SUMMARY, true);		
		} else if (SEARCH_PORTLET.equals(displayType)) {
			return setupSummaryPortlets(request, prefs, model, WebKeys.VIEW_SEARCH, false);		
		}
		return null;
	}
	protected ModelAndView setupSummaryPortlets(RenderRequest request, PortletPreferences prefs, Map model, String view, boolean hasBinders) {
		String gId = prefs.getValue(WebKeys.PORTLET_PREF_DASHBOARD, null);
		if (gId != null) {
			try {
				DashboardPortlet d = (DashboardPortlet)getDashboardModule().getDashboard(gId);
				model.put(WebKeys.DASHBOARD_PORTLET, d);
				Map userProperties = (Map) getProfileModule().getUserProperties(RequestContextHolder.getRequestContext().getUserId()).getProperties();
				model.put(WebKeys.USER_PROPERTIES, userProperties);
				if (request.getWindowState().equals(WindowState.MAXIMIZED))
					model.put(WebKeys.PAGE_SIZE, "20");
				else
					model.put(WebKeys.PAGE_SIZE, "5");						
				model.put(WebKeys.DASHBOARD_COMPONENT_ID, PORTLET_COMPONENT_ID);
				DashboardHelper.getDashboardMap(d, userProperties, model, PORTLET_COMPONENT_ID);
				if (!hasBinders) return new ModelAndView(view, model);
				Map dataMap = DashboardHelper.getComponentData(d, PORTLET_COMPONENT_ID);
				if (dataMap != null) {
					List savedFolderIds = (List)dataMap.get(DashboardHelper.SearchFormSavedFolderIdList);
					//	Build the jsp bean (sorted by folder title)
					Long folderId;
					if (savedFolderIds != null && savedFolderIds.size() > 0) {
						for (int i = 0; i < savedFolderIds.size(); i++) {
							folderId = Long.valueOf((String)savedFolderIds.get(i));
							Binder folder = getFolderModule().getFolder(folderId);
							model.put(WebKeys.BINDER, folder);
							break;
						}
						return new ModelAndView(view, model);		
					} 
				}
			} catch (NoObjectByTheIdException no) {}
		}
		return new ModelAndView(WebKeys.VIEW_NOT_CONFIGURED);
		
	}
	protected void buildForumToolbar(String prefix, Map<String,Object> model) {
		//Build the toolbar array
		Toolbar toolbar = new Toolbar();

		//The "Show unseen" menu
		String url = "javascript: ;";
		Map<String,Object> qualifiers = new HashMap<String,Object>();
		String name= prefix + "_getUnseenCounts";
		qualifiers.put("onClick", "if (" + name+ ") {" + name + "()};return false;");
		toolbar.addToolbarMenu("1_showunseen", NLT.get("toolbar.showUnseen"), url, qualifiers);

		model.put(WebKeys.FORUM_TOOLBAR, toolbar.getToolbar());
	}


}
