package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Collection;

import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Workspace;

/**
 * @author Peter Hurley
 *
 */
public class EditController extends SAbstractController implements DomTreeBuilder {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response)
	throws Exception {

		PortletPreferences prefs = request.getPreferences();
		Map formData = request.getParameterMap();
		List forumPrefIdList = new ArrayList();
		
		//Get the forums to be displayed
		Iterator itFormData = formData.entrySet().iterator();
		while (itFormData.hasNext()) {
			Map.Entry me = (Map.Entry) itFormData.next();
			if (((String)me.getKey()).startsWith("id_")) {
				String forumId = ((String)me.getKey()).substring(3);
				forumPrefIdList.add(forumId);
			}
		}
		
		prefs.setValues(WebKeys.FORUM_PREF_FORUM_ID_LIST, (String[]) forumPrefIdList.toArray(new String[forumPrefIdList.size()]));

		prefs.store();
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {


        //Make the prefs available to the jsp
        Map model = new HashMap();
		
		Document wsTree = getWorkspaceModule().getDomWorkspaceTree(this);
		model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
		
		PortletPreferences prefsPP = request.getPreferences();
		String[] forumPrefIdList = prefsPP.getValues(WebKeys.FORUM_PREF_FORUM_ID_LIST, new String[0]);
		
		//Build the jsp bean (sorted by folder title)
		List forumIdList = new ArrayList();
		List folderIds = new ArrayList();
		for (int i = 0; i < forumPrefIdList.length; i++) {
			forumIdList.add(forumPrefIdList[i]);
			folderIds.add(new Long(forumPrefIdList[i]));
		}
		Collection folders = getFolderModule().getFolders(folderIds);
		
		model.put(WebKeys.FOLDER_LIST, folders);
		model.put(WebKeys.FOLDER_ID_LIST, folderIds);
		model.put(WebKeys.FORUM_ID_LIST, forumIdList);
			
		return new ModelAndView(WebKeys.VIEW_EDIT, model);
	}
	public Element setupDomElement(String type, Object source, Element element) {
		if (type.equals(DomTreeBuilder.TYPE_WORKSPACE)) {
			Workspace ws = (Workspace)source;
			element.addAttribute("type", "workspace");
			element.addAttribute("title", ws.getTitle());
			element.addAttribute("id", "");
			element.addAttribute("image", "workspace");
			element.addAttribute("displayOnly", "true");
			element.addAttribute("url", "");
		} else if (type.equals(DomTreeBuilder.TYPE_FOLDER)) {
			Folder f = (Folder)source;
			element.addAttribute("type", "forum");
			element.addAttribute("title", f.getTitle());
			element.addAttribute("id", f.getId().toString());
			element.addAttribute("image", "forum");
			element.addAttribute("url", "");
		} else return null;
		return element;
	}
}
