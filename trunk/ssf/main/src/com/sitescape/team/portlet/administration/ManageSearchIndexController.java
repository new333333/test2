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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
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
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.search.filter.SearchFilterKeys;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SimpleProfiler;
import com.sitescape.team.util.StatusTicket;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.tree.DomTreeBuilder;
import com.sitescape.team.web.tree.SearchTreeHelper;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.WebStatusTicket;
import com.sitescape.util.Validator;
public class ManageSearchIndexController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		String btnClicked = PortletRequestUtils.getStringParameter(request, "btnClicked", "");
		if (formData.containsKey("okBtn") || btnClicked.equals("okBtn")) {
			//Get the list of binders to be indexed
			List<Long> ids = new ArrayList();
			Long profileId = null;
			//Get the binders to be indexed
			String[] values = (String[])formData.get(WebKeys.URL_ID_CHOICES);
			for (int i = 0; i < values.length; i++) {
				String[] valueSplited = values[i].split("\\s");
				for (int j = 0; j < valueSplited.length; j++) {
					if (valueSplited[j] != null && !"".equals(valueSplited[j])) {
						if (valueSplited[j].startsWith(DomTreeBuilder.NODE_TYPE_FOLDER)) {
							String binderId = valueSplited[j].replaceFirst(DomTreeBuilder.NODE_TYPE_FOLDER, "");
							ids.add(Long.valueOf(binderId));
						} else if (valueSplited[j].startsWith(DomTreeBuilder.NODE_TYPE_WORKSPACE)) {
							String binderId = valueSplited[j].replaceFirst(DomTreeBuilder.NODE_TYPE_WORKSPACE, "");
							ids.add(Long.valueOf(binderId));
						} else if (valueSplited[j].startsWith(DomTreeBuilder.NODE_TYPE_PEOPLE)) {
							//people are handled separetly, so we can reindex users without reindex their
							//the entire workspace tree.
							String binderId = valueSplited[j].replaceFirst(DomTreeBuilder.NODE_TYPE_PEOPLE, "");
							profileId = Long.valueOf(binderId);
						}								
					}
				}
			}

			
			// Create a new status ticket
			StatusTicket statusTicket = WebStatusTicket.newStatusTicket(PortletRequestUtils.getStringParameter(request, WebKeys.URL_STATUS_TICKET_ID, "none"), request);
			SimpleProfiler profiler = null; 
			if (logger.isDebugEnabled()) {
				profiler = new SimpleProfiler("manageSearchIndex");
				SimpleProfiler.setProfiler(profiler);
			}
			Collection idsIndexed = getBinderModule().indexTree(ids, statusTicket);
			//if people selected and not yet index; index content only, not the whole ws tree
			if ((profileId != null) && !idsIndexed.contains(profileId))
				getBinderModule().indexBinder(profileId, true);
			
			if (logger.isDebugEnabled()) {
				logger.debug(SimpleProfiler.toStr());
				SimpleProfiler.clearProfiler();
			}
			response.setRenderParameters(formData);
		} else if (formData.containsKey("closeBtn") || formData.containsKey("cancelBtn") ||
				 btnClicked.equals("closeBtn")) {
			response.setRenderParameter("redirect", "true");
		} else
			response.setRenderParameters(formData);
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		if (!Validator.isNull(request.getParameter("redirect"))) {
			return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT);
		}
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
    	users.addAttribute("type", DomTreeBuilder.NODE_TYPE_PEOPLE);
    	users.addAttribute("title", NLT.get("administration.profile.content"));
    	users.addAttribute("id", p.getId().toString());
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
			
		return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_SEARCH_INDEX, model);
	}

}
