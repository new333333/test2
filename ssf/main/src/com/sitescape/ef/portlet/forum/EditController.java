package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import com.sitescape.ef.portlet.PortletKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.domain.NoFolderByTheIdException;


/**
 * @author Peter Hurley
 *
 */
public class EditController extends SAbstractController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response)
	throws Exception {

		PortletPreferences prefs = request.getPreferences();

		String forumId = ActionUtil.getStringValue(request.getParameterMap(), PortletKeys.FORUM_URL_FORUM_ID);

		//Get the name of the forum to be displayed
		prefs.setValue(PortletKeys.FORUM_URL_FORUM_ID, forumId);

		prefs.store();
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {


        //Make the prefs available to the jsp
        Map model = new HashMap();
		
		model.put(PortletKeys.WORKSPACE_DOM_TREE, getWorkspaceModule().getDomWorkspaceTree());
		
		PortletPreferences prefsPP = request.getPreferences();
		String forumPref = prefsPP.getValue(PortletKeys.FORUM_URL_FORUM_ID, "");
    	if (!forumPref.equals("")) {		
			//Build the jsp beans
			try {
				model.put(PortletKeys.FOLDER, getFolderModule().getFolder(new Long(forumPref)));    
			} catch (NoFolderByTheIdException nf) {
				//fall thru
			}
    	}
			
		return new ModelAndView(PortletKeys.VIEW_EDIT, model);
	}
}
