package com.sitescape.ef.portlet.forum;

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
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.portlet.workspaceTree.WorkspaceTreeController.WsTreeBuilder;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.FindIdsHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.Toolbar;
import com.sitescape.ef.web.util.WebHelper;
import com.sitescape.util.Validator;


/**
 * @author Peter Hurley
 *
 */
public class ViewController  extends SAbstractController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
 		Map<String,Object> model = new HashMap<String,Object>();
 		
		PortletPreferences prefs = request.getPreferences();
		String displayType = (String)prefs.getValue(WebKeys.PORTLET_PREF_TYPE, null);
		if (Validator.isNull(displayType)) {
			//select type of porlet
			return new ModelAndView(WebKeys.VIEW_ASPEN_TYPE);
		}
		String title = (String)prefs.getValue(WebKeys.PORTLET_PREF_TITLE, null);
		if (!Validator.isNull(title)) response.setTitle(title);
		if ("ss_forum".equals(displayType)) {
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
		} else if ("ss_profile".equals(displayType)) {
			//Get the profile binder
			//If first time here, add a profile folder to the top workspace
			ProfileBinder binder = getProfileModule().getProfileBinder();
				
			model.put(WebKeys.BINDER, binder);
			Toolbar toolbar = new Toolbar();
			PortletURL url;
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
			url.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
			url.setWindowState(WindowState.MAXIMIZED);
			toolbar.addToolbarMenu("listing", NLT.get("profile.list", "List users"), url);
			model.put(WebKeys.TOOLBAR, toolbar.getToolbar());
			return new ModelAndView(WebKeys.VIEW_PROFILE, model);
		} else if ("ss_workspace".equals(displayType)) {
			PortletSession ses = WebHelper.getRequiredPortletSession(request);
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
				wsTree = getWorkspaceModule().getDomWorkspaceTree(binder.getId(), new WsTopOnly(), 0);
			} else {
				wsTree = getWorkspaceModule().getDomWorkspaceTree(binder.getId(), new WsTreeBuilder((Workspace)binder, true, getBinderModule()), 1);									
			}
			model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
			model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, binder.getId().toString());
				
		    return new ModelAndView("workspacetree/view", model);			
		} else if ("ss_presence".equals(displayType)) {
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
		}
		return null;
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
	protected class WsTopOnly implements DomTreeBuilder {
		public Element setupDomElement(String type, Object source, Element element) {
			Binder binder = (Binder) source;
			String icon = binder.getIconName();
			String imageClass = "ss_twIcon";
			if (icon == null || icon.equals("")) {
				icon = "/icons/workspace.gif";
				imageClass = "ss_twImg";
			}
			element.addAttribute("title", binder.getTitle());
			element.addAttribute("id", binder.getId().toString());
			element.addAttribute("image", icon);
			element.addAttribute("imageClass", imageClass);
			element.addAttribute("action", WebKeys.ACTION_VIEW_WS_LISTING);
			if (getBinderModule().hasBinders(binder)) {
				element.addAttribute("hasChildren", "true");
			} else {	
				element.addAttribute("hasChildren", "false");
			}
			return element;
		}
	}

}
