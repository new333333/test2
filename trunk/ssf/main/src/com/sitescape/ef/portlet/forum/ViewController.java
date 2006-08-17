package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.portlet.PortletConfig;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.portlet.workspaceTree.WorkspaceTreeController.WsTreeBuilder;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.Toolbar;
import com.sitescape.ef.web.util.WebHelper;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.util.Validator;
import com.sitescape.ef.web.portlet.SAbstractController;


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
 		
		//TODO: need to make the display type a config option
		PortletConfig pConfig = (PortletConfig)request.getAttribute("javax.portlet.config");
		String pName = pConfig.getPortletName();
		if ("ss_forum".equals(pName) || Validator.isNull(pName)) {
			//Build the toolbar and add it to the model
			buildForumToolbar(model);
		
			//This is the portlet view; get the configured list of folders to show
			String[] preferredBinderIds = request.getPreferences().getValues(WebKeys.FORUM_PREF_FORUM_ID_LIST, new String[0]);

			//Build the jsp bean (sorted by folder title)
			List<Long> binderIds = new ArrayList<Long>();
			for (int i = 0; i < preferredBinderIds.length; i++) {
				binderIds.add(new Long(preferredBinderIds[i]));
			}
			model.put(WebKeys.FOLDER_LIST, getFolderModule().getFolders(binderIds));
			response.setProperty(RenderResponse.EXPIRATION_CACHE,"300");
			return new ModelAndView(WebKeys.VIEW_FORUM, model);
		} else if ("ss_profile".equals(pName)) {
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
		} else if ("ss_workspacetree".equals(pName)) {
			PortletSession ses = WebHelper.getRequiredPortletSession(request);
			Workspace binder = getWorkspaceModule().getWorkspace();
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
		} else if ("ss_presence".equals(pName)) {
	 		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION);
	 		//if action in the url, assume this is an ajax update call
	 		if (!Validator.isNull(action)) {
				Map statusMap = new HashMap();
				model.put(WebKeys.AJAX_STATUS, statusMap);	
				model.put(WebKeys.NAMING_PREFIX, PortletRequestUtils.getStringParameter(request, WebKeys.NAMING_PREFIX, ""));
				model.put(WebKeys.DASHBOARD_ID, PortletRequestUtils.getStringParameter(request, WebKeys.DASHBOARD_ID, ""));
				response.setContentType("text/xml");
	 			if (!WebHelper.isUserLoggedIn(request)) {
	 				
	 				//Signal that the user is not logged in. 
	 				//  The code on the calling page will output the proper translated message.
	 				statusMap.put(WebKeys.AJAX_STATUS_NOT_LOGGED_IN, new Boolean(true));
	 				return new ModelAndView(WebKeys.VIEW_PRESENCE_AJAX, model);
	 			} else {
	 				model.put(WebKeys.USERS, getUsers(PortletRequestUtils.getStringParameter(request, "userList", "").split(" ")));
					model.put(WebKeys.GROUPS, getGroups(PortletRequestUtils.getStringParameter(request, "groupList", "").split(" ")));
					return new ModelAndView(WebKeys.VIEW_PRESENCE_AJAX, model);
	 			}
			} else {
	 			Set ids = new HashSet();		
	 			ids.addAll(getIds(request.getPreferences().getValue(WebKeys.PRESENCE_PREF_USER_LIST, "")));
	 			ids.addAll(getIds(request.getPreferences().getValue(WebKeys.PRESENCE_PREF_GROUP_LIST, "")));
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

			
		}
		return null;
	}

	private Collection getUsers(String [] ids) {
		Set<Long> userIds = new HashSet<Long>();
		for (int i = 0; i < ids.length; i++) {
			try {
				userIds.add(new Long(ids[i]));
			} catch (Exception ex) {};
		}
		return getProfileModule().getUsers(userIds);
		
	}
	private Collection getGroups(String [] ids) {
		Set<Long> groupIds = new HashSet<Long>();
		for (int i = 0; i < ids.length; i++) {
			try  {
				groupIds.add(new Long(ids[i]));
			} catch (Exception ex) {};
		}
		return getProfileModule().getGroups(groupIds);
		
	}
	private Set getIds(String ids) {
		String [] sIds = ids.split(",");
		Set<Long> idSet = new HashSet<Long>();
		for (int i = 0; i < sIds.length; i++) {
			try  {
				idSet.add(new Long(sIds[i]));
			} catch (Exception ex) {};
		}
		return idSet;
		
	}
	
	protected void buildForumToolbar(Map<String,Object> model) {
		//Build the toolbar array
		Toolbar toolbar = new Toolbar();

		//The "Show unseen" menu
		String url = "javascript: ;";
		Map<String,Object> qualifiers = new HashMap<String,Object>();
		qualifiers.put("onClick", "if (ss_getUnseenCounts) {ss_getUnseenCounts()};return false;");
		toolbar.addToolbarMenu("1_showunseen", NLT.get("toolbar.showUnseen"), url, qualifiers);

		model.put(WebKeys.FORUM_TOOLBAR, toolbar.getToolbar());
	}
	protected class WsTopOnly implements DomTreeBuilder {
		public Element setupDomElement(String type, Object source, Element element) {
			Element url;
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
