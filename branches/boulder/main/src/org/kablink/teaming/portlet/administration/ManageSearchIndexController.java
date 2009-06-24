/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.portlet.administration;
import java.util.ArrayList;
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
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.IndexNode;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.search.IndexErrors;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.StatusTicket;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.tree.DomTreeBuilder;
import org.kablink.teaming.web.tree.SearchTreeHelper;
import org.kablink.teaming.web.tree.TreeHelper;
import org.kablink.teaming.web.tree.WsDomTreeBuilder;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.WebStatusTicket;
import org.kablink.util.Validator;
import org.springframework.web.portlet.ModelAndView;


public class ManageSearchIndexController extends  SAbstractController {
	private final String usersAndGroups = "zzzzzzzzzzzzzzzzzzz";
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
        User user = RequestContextHolder.getRequestContext().getUser();
		Map formData = request.getParameterMap();
		String btnClicked = PortletRequestUtils.getStringParameter(request, "btnClicked", "");
		if ((formData.containsKey("okBtn") || btnClicked.equals("okBtn")) && WebHelper.isMethodPost(request)) {
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
			IndexErrors errors = new IndexErrors();
			Collection idsIndexed = getBinderModule().indexTree(ids, statusTicket, nodeNames, errors);
			//if people selected and not yet index; index content only, not the whole ws tree
			String idChoices = TreeHelper.getSelectedIdsAsString(formData);
			if (idChoices.contains(usersAndGroups)) {
				ProfileBinder pf = getProfileModule().getProfileBinder();
				if (!idsIndexed.contains(pf.getId())) {
					errors.add(getBinderModule().indexBinder(pf.getId(), true)); 
				}
			}
    		getProfileModule().setUserProperty(user.getId(), ObjectKeys.USER_PROPERTY_UPGRADE_SEARCH_INDEX, "true");
			if (logger.isDebugEnabled()) {
				logger.debug(SimpleProfiler.toStr());
				SimpleProfiler.clearProfiler();
			}
			response.setRenderParameters(formData);
			response.setRenderParameter(WebKeys.ERROR_INDEXING_COUNT, errors.getErrorCount().toString());
			if (errors.getErrorCount() > 0) {
				List binderIds = new ArrayList();
				for (Binder b : errors.getBinders()) {
					binderIds.add(b.getId().toString());
					logger.error(NLT.get("error.indexing.binders", new String[] {b.getId().toString(), b.getTitle()}));
				}
				List entryIds = new ArrayList();
				for (Entry e : errors.getEntries()) {
					entryIds.add(e.getId().toString());
					logger.error(NLT.get("error.indexing.entries", new String[] {e.getId().toString(), e.getTitle()}));
				}
				if (binderIds.size() > 0)
					response.setRenderParameter(WebKeys.ERROR_INDEXING_BINDERS, (String[])binderIds.toArray(new String[binderIds.size()]));
				if (entryIds.size() > 0)
					response.setRenderParameter(WebKeys.ERROR_INDEXING_ENTRIES, (String[])entryIds.toArray(new String[entryIds.size()]));
			}
		} else
			response.setRenderParameters(formData);
	}

	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Map formData = request.getParameterMap();
		String btnClicked = PortletRequestUtils.getStringParameter(request, "btnClicked", "");
		if (formData.containsKey("okBtn") || btnClicked.equals("okBtn")) {
			response.setContentType("text/xml");
			model.put(WebKeys.ERROR_INDEXING_COUNT, request.getParameter(WebKeys.ERROR_INDEXING_COUNT));
			model.put(WebKeys.ERROR_INDEXING_BINDERS, request.getParameter(WebKeys.ERROR_INDEXING_BINDERS));
			model.put(WebKeys.ERROR_INDEXING_ENTRIES, request.getParameter(WebKeys.ERROR_INDEXING_ENTRIES));
			return new ModelAndView("administration/indexing_errors", model);
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
