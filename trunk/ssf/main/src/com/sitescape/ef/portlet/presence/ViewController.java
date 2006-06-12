package com.sitescape.ef.portlet.presence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.Toolbar;
import com.sitescape.ef.web.util.WebHelper;
import com.sitescape.util.Validator;


/**
 * @author Janet McCann
 *
 */
public class ViewController  extends SAbstractController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
 		Map<String,Object> model = new HashMap<String,Object>();
 		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION);
 		//if no action in the url, assume this is an ajax update call
 		if (!Validator.isNull(action)) {
			Map statusMap = new HashMap();
			model.put(WebKeys.AJAX_STATUS, statusMap);		
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
 			//Build the toolbar and add it to the model
 			buildToolbar(model);
 			
 			//This is the portlet view; get the configured list of principals to show
 			model.put(WebKeys.USERS, getUsers(request.getPreferences().getValues(WebKeys.PRESENCE_PREF_USER_LIST, new String[0])));
 			model.put(WebKeys.GROUPS, getGroups(request.getPreferences().getValues(WebKeys.PRESENCE_PREF_GROUP_LIST, new String[0]))); 			
 			response.setProperty(RenderResponse.EXPIRATION_CACHE,"300");
 			return new ModelAndView(WebKeys.VIEW_PRESENCE, model);
 		}

			
	}

	private Collection getUsers(String [] ids) {
		List<Long> userIds = new ArrayList<Long>();
		for (int i = 0; i < ids.length; i++) {
			try {
				userIds.add(new Long(ids[i]));
			} catch (Exception ex) {};
		}
		return getProfileModule().getUsers(userIds);
		
	}
	private Collection getGroups(String [] ids) {
		List<Long> groupIds = new ArrayList<Long>();
		for (int i = 0; i < ids.length; i++) {
			try  {
				groupIds.add(new Long(ids[i]));
			} catch (Exception ex) {};
		}
		return getProfileModule().getGroups(groupIds);
		
	}
	protected void buildToolbar(Map<String,Object> model) {
		//Build the toolbar array
		Toolbar toolbar = new Toolbar();

		//The "Show unseen" menu
		String url = "javascript: ;";
		Map<String,Object> qualifiers = new HashMap<String,Object>();
		qualifiers.put("onClick", "if (ss_getPresence) {ss_getPresence()};return false;");
		toolbar.addToolbarMenu("1_showunseen", NLT.get("toolbar.presence"), url, qualifiers);

		model.put(WebKeys.FORUM_TOOLBAR, toolbar.getToolbar());
	}


}
