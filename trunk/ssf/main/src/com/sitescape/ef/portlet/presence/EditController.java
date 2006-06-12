package com.sitescape.ef.portlet.presence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;

/**
 * @author Peter Hurley
 *
 */
public class EditController extends SAbstractController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response)
	throws Exception {

		PortletPreferences prefs = request.getPreferences();
		Map formData = request.getParameterMap();
		
		if (formData.containsKey("applyBtn")) {
			Set userIds = new HashSet();
			Set groupIds = new HashSet();
			if (formData.containsKey("users")) {
				String ids[] = (String[])formData.get("users");
				if (ids != null) {
					for (int i = 0; i < ids.length; i++) {
						String[] uIds = ids[i].split(" ");
						for (int j = 0; j < uIds.length; j++) {
							if (uIds[j].length() > 0) userIds.add(uIds[j].trim());
						}
					}
					
				}
			}
			if (formData.containsKey("groups")) {
				String ids[] = (String[])formData.get("groups");
				if (ids != null) {
					for (int i = 0; i < ids.length; i++) {
						String[] uIds = ids[i].split(" ");
						for (int j = 0; j < uIds.length; j++) {
							if (uIds[j].length() > 0) groupIds.add(uIds[j].trim());
						}
					}
					
				}
			}
	
			prefs.setValues(WebKeys.PRESENCE_PREF_USER_LIST, (String[]) userIds.toArray(new String[userIds.size()]));
			prefs.setValues(WebKeys.PRESENCE_PREF_GROUP_LIST, (String[]) groupIds.toArray(new String[groupIds.size()]));
			prefs.store();
		} 
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {


        //Make the prefs available to the jsp
        Map model = new HashMap();
		
		//This is the portlet view; get the configured list of principals to show
		String[] uIds = request.getPreferences().getValues(WebKeys.PRESENCE_PREF_USER_LIST, new String[0]);
		String[] gIds = request.getPreferences().getValues(WebKeys.PRESENCE_PREF_GROUP_LIST, new String[0]);

		//Build the jsp bean (sorted by folder title)
		List<Long> userIds = new ArrayList<Long>();
		for (int i = 0; i < uIds.length; i++) {
			userIds.add(new Long(uIds[i]));
		}
		List<Long> groupIds = new ArrayList<Long>();
		for (int i = 0; i < gIds.length; i++) {
			groupIds.add(new Long(gIds[i]));
		}
		model.put(WebKeys.USERS, getProfileModule().getUsers(userIds));
		model.put(WebKeys.GROUPS, getProfileModule().getGroups(groupIds));
		
		return new ModelAndView(WebKeys.VIEW_PRESENCE_EDIT, model);
	}

}
