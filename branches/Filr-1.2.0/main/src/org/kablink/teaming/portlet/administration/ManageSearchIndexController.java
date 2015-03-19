/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
import java.util.HashSet;
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
import org.kablink.teaming.module.admin.IndexOptimizationSchedule;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.search.IndexErrors;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.StatusTicket;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.tree.DomTreeBuilder;
import org.kablink.teaming.web.tree.SearchTreeHelper;
import org.kablink.teaming.web.tree.TreeHelper;
import org.kablink.teaming.web.tree.WsDomTreeBuilder;
import org.kablink.teaming.web.util.BuiltInUsersHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.ScheduleHelper;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.WebStatusTicket;
import org.kablink.util.Validator;
import org.springframework.web.portlet.ModelAndView;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings("unchecked")
public class ManageSearchIndexController extends  SAbstractController {
	private final String usersAndGroups = "zzzzzzzzzzzzzzzzzzz";
	
	// The following controls whether users given zone administration
	// rights will be able to re-index the site.  If true, they can and
	// the indexing operations are 'run as' the built-in admin user.
	// If false, they can't and the indexing operation does not appear
	// for them.
	public static final boolean INDEX_AS_BUILT_IN_ADMIN	= SPropsUtil.getBoolean("index.as.built.in.admin", true);
	
	@Override
	public void handleActionRequestAfterValidation(final ActionRequest request, final ActionResponse response) throws Exception {
        final User user = RequestContextHolder.getRequestContext().getUser();
		final Map formData = request.getParameterMap();
		final String btnClicked = PortletRequestUtils.getStringParameter(request, "btnClicked", "");
		final String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		final Boolean indexAll = PortletRequestUtils.getBooleanParameter(request, "indexAll", false);
		final Binder topBinder = getWorkspaceModule().getTopWorkspace();
		if ((formData.containsKey("okBtn") || btnClicked.equals("okBtn")) && WebHelper.isMethodPost(request)) {
			// If we're already the built-in admin...
			if (user.isAdmin()) {
				// ...simply perform the index operation.
				runIndexOperation(
					request,
					response,
					topBinder,
					user,
					operation,
					indexAll,
					formData);
			}

			// ...otherwise, if we're supposed to perform index
			// ...operations as the built-in admin...
			else if (INDEX_AS_BUILT_IN_ADMIN) {
				// ...perform the index operation as that user.
				logger.info("User '" + user.getTitle() + "' is re-indexing as the built-in admin user.");
				RunasTemplate.runasAdmin(
					new RunasCallback() {
						@Override
						public Object doAs() {
							runIndexOperation(
								request,
								response,
								topBinder,
								BuiltInUsersHelper.getZoneSuperUser(),
								operation,
								indexAll,
								formData);
							return null;
						}
					},
					RequestContextHolder.getRequestContext().getZoneName());
			}
		}
		
		else {
			response.setRenderParameters(formData);
		}
	}
	
	/*
	 * Performs the requested indexing operation.
	 */
	private void runIndexOperation(ActionRequest request, ActionResponse response, Binder topBinder, User user, String operation, Boolean indexAll, Map formData) {
		if (operation.equals("index")) {
			//Get the binders to be indexed
			Collection<Long> ids = TreeHelper.getSelectedIds(formData);
			if (indexAll) {
				ids = new HashSet();
				ids.add(topBinder.getId());
			}
			
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
			IndexErrors errors = new IndexErrors();
			String idChoices = TreeHelper.getSelectedIdsAsString(formData);
			boolean includeUsersAndGroups = false;
			if (idChoices.contains(usersAndGroups))
				includeUsersAndGroups = true;
			try {
				getAdminModule().reindexDestructive(ids, statusTicket, nodeNames, errors, includeUsersAndGroups);
			} catch(Exception e) {
				errors.addError(NLT.get("error.indexing.string", new String[] {e.getMessage()}));
			}
			
    		getProfileModule().setUserProperty(user.getId(), ObjectKeys.USER_PROPERTY_UPGRADE_SEARCH_INDEX, "true");
			//SimpleProfiler.done(logger);
			response.setRenderParameters(formData);
			response.setRenderParameter(WebKeys.ERROR_INDEXING_COUNT, String.valueOf(errors.getErrorCount()));
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
				List errorStrings = new ArrayList();
				for (String s : errors.getGeneralErrors()) {
					errorStrings.add(s);
				}
				if (binderIds.size() > 0)
					response.setRenderParameter(WebKeys.ERROR_INDEXING_BINDERS, (String[])binderIds.toArray(new String[binderIds.size()]));
				if (entryIds.size() > 0)
					response.setRenderParameter(WebKeys.ERROR_INDEXING_ENTRIES, (String[])entryIds.toArray(new String[entryIds.size()]));
				if (errorStrings.size() > 0)
					response.setRenderParameter(WebKeys.ERROR_INDEXING_STRINGS, (String[])errorStrings.toArray(new String[errorStrings.size()]));
			}
		
		} else if (operation.equals("optimize")) {
			response.setRenderParameters(formData);
			IndexOptimizationSchedule schedule = getAdminModule().getIndexOptimizationSchedule();
			schedule.getScheduleInfo().setSchedule(ScheduleHelper.getSchedule(request, null));
			boolean runNow = PortletRequestUtils.getBooleanParameter(request, "runnow", false);
			boolean scheduleEnabled = PortletRequestUtils.getBooleanParameter(request, "enabled", false);
			String searchNodesPresent = PortletRequestUtils.getStringParameter(request, "searchNodesPresent", "");
			if(searchNodesPresent.equals("1")) { // H/A environment
				String[] nodeNames = (String[])formData.get(WebKeys.URL_SEARCH_NODE_NAME);
				if(nodeNames == null || nodeNames.length == 0) {
					// The user selected no node, probably by mistake.
					// In this case, we must not enable the schedule since it won't have any work to do 
					// when wake up and have no good way of differentiating H/A situation with no node
					// selected from non-H/A situation (hence ambiguous).
					schedule.getScheduleInfo().setEnabled(false);
					schedule.setNodeNames(null);
					getAdminModule().setIndexOptimizationSchedule(schedule);			
					// Forget about "run now". Even if it was checked, there's nothing to run.
				}
				else { // At least one node is selected.
					schedule.getScheduleInfo().setEnabled(scheduleEnabled);
					schedule.setNodeNames(nodeNames);
					getAdminModule().setIndexOptimizationSchedule(schedule);			
					if(runNow)
						getAdminModule().optimizeIndex(nodeNames);						
				}
			}
			else { // non-HA environment
				schedule.getScheduleInfo().setEnabled(scheduleEnabled);
				schedule.setNodeNames(null);
				getAdminModule().setIndexOptimizationSchedule(schedule);			
				if(runNow)
					getAdminModule().optimizeIndex(null);						
			}
		}
	}

	@Override
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Map formData = request.getParameterMap();
		String btnClicked = PortletRequestUtils.getStringParameter(request, "btnClicked", "");
		if (formData.containsKey("okBtn") || btnClicked.equals("okBtn")) {
			response.setContentType("text/xml");
			model.put(WebKeys.ERROR_INDEXING_COUNT, request.getParameter(WebKeys.ERROR_INDEXING_COUNT));
			model.put(WebKeys.ERROR_INDEXING_BINDERS, formData.get(WebKeys.ERROR_INDEXING_BINDERS));
			model.put(WebKeys.ERROR_INDEXING_ENTRIES, formData.get(WebKeys.ERROR_INDEXING_ENTRIES));
			model.put(WebKeys.ERROR_INDEXING_STRINGS, formData.get(WebKeys.ERROR_INDEXING_STRINGS));
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
		if (Validator.isNull(icon)) {
	    	users.addAttribute("image", Utils.getIconNameTranslated("/icons/profiles.gif"));
			users.addAttribute("imageClass", "ss_twImg");
		} else {
			users.addAttribute("image", Utils.getIconNameTranslated(icon));
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
		
		List<IndexNode> nodes = getAdminModule().retrieveIndexNodesHA();
		
		if (nodes != null) {
			model.put(WebKeys.SEARCH_NODES, nodes);
		} else {
			model.put(WebKeys.SEARCH_NODE, getAdminModule().loadNonHAIndexNode());
		}
		model.put(WebKeys.SEARCH_SAFE_TO_INDEX, !(getAdminModule().isUnsafeReindexingInProgress()));
			
		IndexOptimizationSchedule schedule = getAdminModule().getIndexOptimizationSchedule();
		model.put(WebKeys.SCHEDULE_INFO, schedule);
		
		return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_SEARCH_INDEX, model);
	}
}
