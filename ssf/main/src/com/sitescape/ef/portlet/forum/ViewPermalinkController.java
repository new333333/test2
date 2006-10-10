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

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.portlet.workspaceTree.WorkspaceTreeController.WsTreeBuilder;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.BinderHelper;
import com.sitescape.ef.web.util.FindIdsHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.Tabs;
import com.sitescape.ef.web.util.Toolbar;
import com.sitescape.ef.web.util.WebHelper;
import com.sitescape.util.Validator;


/**
 * @author Peter Hurley
 *
 */
public class ViewPermalinkController  extends SAbstractController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
 		Map<String,Object> model = new HashMap<String,Object>();
 		
 		String url = BinderHelper.getBinderPermaLink(this);
		String binderId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
		String entryId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		String entityType= PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTITY_TYPE, "");
		
		if (!binderId.equals("")) url = url.replaceAll(WebKeys.URL_BINDER_ID_PLACE_HOLDER, binderId);
		if (!entryId.equals("")) url = url.replaceAll(WebKeys.URL_ENTRY_ID_PLACE_HOLDER, entryId);
		if (!entityType.equals("")) url = url.replaceAll(WebKeys.URL_ENTITY_TYPE_PLACE_HOLDER, entityType);
		
		if (entityType.equals(EntityIdentifier.EntityType.workspace.toString())) {
			url = url.replaceAll(WebKeys.URL_ACTION_PLACE_HOLDER, "view_ws_listing");
		} else if (entityType.equals(EntityIdentifier.EntityType.folder.toString())) {
			url = url.replaceAll(WebKeys.URL_ACTION_PLACE_HOLDER, "view_folder_listing");
		} else if (entityType.equals(EntityIdentifier.EntityType.folderEntry.toString())) {
			url = url.replaceAll(WebKeys.URL_ACTION_PLACE_HOLDER, "view_folder_entry");
		}
 		
		model.put(WebKeys.PERMALINK, url);
			
	    return new ModelAndView("binder/view_permalink", model);
	}

}
