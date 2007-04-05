/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.portlet.administration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.tree.DomTreeBuilder;
import com.sitescape.team.web.tree.SearchTreeHelper;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.util.Validator;
public class ManageSearchIndexController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn") || formData.containsKey("applyBtn")) {
			//Get the list of binders to be indexed
			List<Long> folderIdList = new ArrayList();
			List<Long> wsIdList = new ArrayList();
			Long profileId = null;
			//Get the binders to be indexed
			Iterator itFormData = formData.entrySet().iterator();
			while (itFormData.hasNext()) {
				Map.Entry me = (Map.Entry) itFormData.next();
				String key = (String)me.getKey();
				if (key.startsWith(DomTreeBuilder.NODE_TYPE_FOLDER)) {
					String binderId = key.replaceFirst(DomTreeBuilder.NODE_TYPE_FOLDER + "_", "");
					folderIdList.add(Long.valueOf(binderId));
				} else if (key.startsWith(DomTreeBuilder.NODE_TYPE_WORKSPACE)) {
					String binderId = key.replaceFirst(DomTreeBuilder.NODE_TYPE_WORKSPACE + "_", "");
					wsIdList.add(Long.valueOf(binderId));
				} else if (key.startsWith(DomTreeBuilder.NODE_TYPE_PEOPLE)) {
					String binderId = key.replaceFirst(DomTreeBuilder.NODE_TYPE_PEOPLE + "_", "");
					profileId = Long.valueOf(binderId);
				}
			}
			TreeSet exclude = new TreeSet();
			for (Long id:wsIdList) {
				exclude.addAll(getBinderModule().indexTree(id, exclude));
			}
			for (Long id:folderIdList) {
				exclude.addAll(getBinderModule().indexTree(id, exclude));
			}
			//if people selected and not yet index; index content only, not the whole ws tree
			if ((profileId != null) && !exclude.contains(profileId))
				getBinderModule().indexBinder(profileId, true);
			
			response.setRenderParameter("redirect", "true");
			
		} else if (formData.containsKey("closeBtn") || formData.containsKey("cancelBtn")) {
			response.setRenderParameter("redirect", "true");
		} else
			response.setRenderParameters(formData);
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		if (!Validator.isNull(request.getParameter("redirect"))) {
			return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT);
		}

		Map model = new HashMap();
		Document pTree = DocumentHelper.createDocument();
    	Element rootElement = pTree.addElement(DomTreeBuilder.NODE_ROOT);
    	Element users = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
    	ProfileBinder p = getProfileModule().getProfileBinder();
    	users.addAttribute("type", DomTreeBuilder.NODE_TYPE_PEOPLE);
    	users.addAttribute("title", NLT.get("administration.profile.content"));
    	users.addAttribute("id", p.getId().toString());
		String icon = p.getIconName();
		if (Validator.isNull(icon)) {
	    	users.addAttribute("image", "people");
		} else {
			users.addAttribute("image", icon);
			users.addAttribute("imageClass", "ss_twIcon");
		}
		users.addAttribute("url", "");
    	Document wsTree = getWorkspaceModule().getDomWorkspaceTree(RequestContextHolder.getRequestContext().getZoneId(), 
				new WsDomTreeBuilder(null, true, this, new SearchTreeHelper()),1);
    	//merge the trees
    	rootElement.appendAttributes(wsTree.getRootElement());
    	rootElement.appendContent(wsTree.getRootElement());
 		model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, RequestContextHolder.getRequestContext().getZoneId().toString());
		model.put(WebKeys.WORKSPACE_DOM_TREE, pTree);		
			
		return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_SEARCH_INDEX, model);
	}

}
