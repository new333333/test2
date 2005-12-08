
package com.sitescape.ef.portlet.forum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.WindowState;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.portlet.PortletKeys;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.util.Toolbar;
import com.sitescape.ef.web.portlet.SAbstractController;
import org.springframework.web.servlet.ModelAndView;
import com.sitescape.ef.domain.Folder;

/**
 * @author Janet McCann
 *
 */
public class SAbstractForumController extends SAbstractController {
	public ModelAndView returnToViewForum(RenderRequest request, Map formData, Long folderId) {
		request.setAttribute(PortletKeys.ACTION, PortletKeys.FORUM_OPERATION_VIEW_FORUM);
		Map model = getForumActionModule().getShowFolder(formData, request, folderId);
	    //Build the toolbar array
		Toolbar toolbar = new Toolbar();
	    String forumId = folderId.toString();
	    //The "Add" menu
		Folder folder = (Folder)model.get(PortletKeys.FOLDER);
		List defaultEntryDefinitions = folder.getEntryDefs();
	    if (!defaultEntryDefinitions.isEmpty()) {
	    	toolbar.addToolbarMenu("1_add", NLT.get("toolbar.add"));
	       	for (int i=0; i<defaultEntryDefinitions.size(); ++i) {
	       		Definition def = (Definition) defaultEntryDefinitions.get(i);
	       		Map urlParams = new HashMap();
	       		urlParams.put(PortletKeys.ACTION, PortletKeys.FORUM_OPERATION_ADD_ENTRY);
	       		urlParams.put(PortletKeys.FORUM_URL_FORUM_ID, forumId);
	       		urlParams.put(PortletKeys.FORUM_URL_ENTRY_TYPE, def.getId());
	    		urlParams.put(PortletKeys.IS_ACTION_URL, Boolean.toString(false));
		        toolbar.addToolbarMenuItem("1_add", "entries", def.getTitle(), urlParams);
	       	}
	    }
	    
	    //The "Administration" menu
	    Map urlParams = null;
	    toolbar.addToolbarMenu("2_administration", NLT.get("toolbar.administration"));
		//Configuration
		urlParams = new HashMap();
		urlParams.put(PortletKeys.ACTION, PortletKeys.FORUM_OPERATION_CONFIGURE_FORUM);
		urlParams.put(PortletKeys.FORUM_URL_FORUM_ID, forumId);
		urlParams.put(PortletKeys.IS_ACTION_URL, Boolean.toString(false));
		toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.configuration"), urlParams);
	    //Definition builder
		urlParams = new HashMap();
		urlParams.put(PortletKeys.ACTION, PortletKeys.FORUM_OPERATION_DEFINITION_BUILDER);
		urlParams.put(PortletKeys.FORUM_URL_FORUM_ID, forumId);
		urlParams.put(PortletKeys.IS_ACTION_URL, Boolean.toString(false));
        toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.definition_builder"), urlParams);
    	//The "Display styles" menu
    	urlParams = null;
    	toolbar.addToolbarMenu("3_display_styles", NLT.get("toolbar.display_styles"));
		//vertical
		urlParams = new HashMap();
		urlParams.put(PortletKeys.ACTION, PortletKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		urlParams.put(PortletKeys.FORUM_URL_FORUM_ID, forumId);
		urlParams.put(PortletKeys.FORUM_URL_VALUE, ObjectKeys.USER_PROPERTY_DISPLAY_STYLE_HORIZONTAL);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_horizontal"), urlParams);
		//horizontal
		urlParams = new HashMap();
		urlParams.put(PortletKeys.ACTION, PortletKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		urlParams.put(PortletKeys.FORUM_URL_FORUM_ID, forumId);
		urlParams.put(PortletKeys.FORUM_URL_VALUE, ObjectKeys.USER_PROPERTY_DISPLAY_STYLE_VERTICAL);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_vertical"), urlParams);
		//accessible
		urlParams = new HashMap();
		urlParams.put(PortletKeys.ACTION, PortletKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		urlParams.put(PortletKeys.FORUM_URL_FORUM_ID, forumId);
		urlParams.put(PortletKeys.FORUM_URL_VALUE, ObjectKeys.USER_PROPERTY_DISPLAY_STYLE_ACCESSIBLE);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_accessible"), urlParams);
		//iframe
		urlParams = new HashMap();
		urlParams.put(PortletKeys.ACTION, PortletKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		urlParams.put(PortletKeys.FORUM_URL_FORUM_ID, forumId);
		urlParams.put(PortletKeys.FORUM_URL_VALUE, ObjectKeys.USER_PROPERTY_DISPLAY_STYLE_IFRAME);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_iframe"), urlParams);
		model.put(PortletKeys.FOLDER_TOOLBAR, toolbar.getToolbar());

		Object obj = model.get(PortletKeys.CONFIG_ELEMENT);
		if ((obj == null) || (obj.equals(""))) 
			return new ModelAndView(PortletKeys.VIEW_NO_DEFINITION, model);
		obj = model.get(PortletKeys.CONFIG_DEFINITION);
		if ((obj == null) || (obj.equals(""))) 
			return new ModelAndView(PortletKeys.VIEW_NO_DEFINITION, model);
		return new ModelAndView(PortletKeys.VIEW_FORUM, model);
	}
/*	public ModelAndView returnToWorkspace(RenderRequest req) {
		Map model = new HashMap();
		model.put(ObjectKeys.WORKSPACE_DOM_TREE, getWorkspaceModule().getDomWorkspaceTree());
		
		//Make the tree available to the jsp
		ModelUtil.processModel(req,model);
		if (req.getWindowState().equals(WindowState.NORMAL)) {
		    return mapping.findForward("portlet.forum.view_workspacetree");
		} else {
			//Show the workspace tree maximized
		    return mapping.findForward("portlet.forum.view_workspacetree_maximized");
		}
	}
	public ModelAndView returnToWorkspace(HttpServletRequest req) {
		//There is no forum specified, show the workspace tree
		Map model = new HashMap();
		model.put(ObjectKeys.WORKSPACE_DOM_TREE, getWorkspaceModule().getDomWorkspaceTree());
		
		//Make the tree available to the jsp
		ModelUtil.processModel(req,model);
		return mapping.findForward("portlet.forum.view_workspacetree_maximized");				

	}
*/
}
