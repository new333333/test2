/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.portlet.administration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collection;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.portlet.ModelAndView;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.IndexNode;
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SimpleProfiler;
import com.sitescape.team.util.StatusTicket;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.tree.DomTreeBuilder;
import com.sitescape.team.web.tree.SearchTreeHelper;
import com.sitescape.team.web.tree.TreeHelper;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.WebStatusTicket;
import com.sitescape.util.Validator;

public class ManageSearchIndexController extends  SAbstractController {
	private final String usersAndGroups = "zzzzzzzzzzzzzzzzzzz";
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		String btnClicked = PortletRequestUtils.getStringParameter(request, "btnClicked", "");
		if (formData.containsKey("okBtn") || btnClicked.equals("okBtn")) {
			//Get the binders to be indexed
			Collection<Long> ids = TreeHelper.getSelectedIds(formData);
			
			String[] nodeNames = null;
			String searchNodesPresent = PortletRequestUtils.getStringParameter(request, "searchNodesPresent", "");
			if(searchNodesPresent.equals("1")) { // H/A environment
				nodeNames = (String[])formData.get(WebKeys.URL_SEARCH_NODE_NAME);
				if(nodeNames == null || nodeNames.length == 0) {
					// The user selected no node, probably by mistake.
					// In this case, there's no work to perform.
					response.setRenderParameters(formData);
					return;
				}
			}
			
			// Create a new status ticket
			StatusTicket statusTicket = WebStatusTicket.newStatusTicket(PortletRequestUtils.getStringParameter(request, WebKeys.URL_STATUS_TICKET_ID, "none"), request);
			SimpleProfiler profiler = null; 
			if (logger.isDebugEnabled()) {
				profiler = new SimpleProfiler("manageSearchIndex");
				SimpleProfiler.setProfiler(profiler);
			}
			Collection idsIndexed = getBinderModule().indexTree(ids, statusTicket, nodeNames);
			//if people selected and not yet index; index content only, not the whole ws tree
			String idChoices = TreeHelper.getSelectedIdsAsString(formData);
			if (idChoices.contains(usersAndGroups)) {
				ProfileBinder pf = getProfileModule().getProfileBinder();
				if (!idsIndexed.contains(pf.getId()))
					getBinderModule().indexBinder(pf.getId(), true);
			}
			if (logger.isDebugEnabled()) {
				logger.debug(SimpleProfiler.toStr());
				SimpleProfiler.clearProfiler();
			}
			response.setRenderParameters(formData);
		} else
			response.setRenderParameters(formData);
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Map formData = request.getParameterMap();
		String btnClicked = PortletRequestUtils.getStringParameter(request, "btnClicked", "");
		if (formData.containsKey("okBtn") || btnClicked.equals("okBtn")) {
			response.setContentType("text/xml");
			return new ModelAndView("forum/ajax_return", model);
		}

		Document pTree = DocumentHelper.createDocument();
    	Element rootElement = pTree.addElement(DomTreeBuilder.NODE_ROOT);
    	Element users = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
    	ProfileBinder p = getProfileModule().getProfileBinder();
       	users.addAttribute("action", "search");
       	users.addAttribute("title", NLT.get("administration.profile.content"));
    	users.addAttribute("id", usersAndGroups);
		String icon = p.getIconName();
		String imageBrand = SPropsUtil.getString("branding.prefix");
		if (Validator.isNull(icon)) {
	    	users.addAttribute("image", "/" + imageBrand + "/icons/profiles.gif");
			users.addAttribute("imageClass", "ss_twImg");
		} else {
			users.addAttribute("image", "/" + imageBrand + icon);
			users.addAttribute("imageClass", "ss_twIcon");
		}
		users.addAttribute("url", "");
    	Document wsTree = getBinderModule().getDomBinderTree(RequestContextHolder.getRequestContext().getZoneId(), 
				new WsDomTreeBuilder(null, true, this, new SearchTreeHelper()),1);
    	//merge the trees
    	rootElement.appendAttributes(wsTree.getRootElement());
    	rootElement.appendContent(wsTree.getRootElement());
 		model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, RequestContextHolder.getRequestContext().getZoneId().toString());
		model.put(WebKeys.WORKSPACE_DOM_TREE, pTree);		
		
		List<IndexNode> nodes = getAdminModule().retrieveIndexNodes();
		
		if(nodes != null) {
			model.put(WebKeys.SEARCH_NODES, nodes);
		}
			
		return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_SEARCH_INDEX, model);
	}

}
