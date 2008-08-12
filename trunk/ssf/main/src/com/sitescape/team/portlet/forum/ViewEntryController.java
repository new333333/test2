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
package com.sitescape.team.portlet.forum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.springframework.web.portlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.AuditTrail;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.NoDefinitionByTheIdException;
import com.sitescape.team.domain.NoFolderEntryByTheIdException;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.SeenMap;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.ical.util.UrlUtil;
import com.sitescape.team.module.binder.BinderModule.BinderOperation;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.folder.FolderModule.FolderOperation;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.module.workflow.WorkflowUtils;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.portletadapter.support.PortletAdapterUtil;
import com.sitescape.team.security.function.OperationAccessControlExceptionNoName;
import com.sitescape.team.ssfs.util.SsfsUtil;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.TagUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.Tabs;
import com.sitescape.team.web.util.Toolbar;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.Validator;


public class ViewEntryController extends  SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		try {response.setWindowState(request.getWindowState());} catch(Exception e){};
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);				
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		
		if (entryId != null) {
			//See if the user asked to change state
			if (formData.containsKey("changeStateBtn")) {
				//Change the state
				//Get the workflow process to change and the name of the new state
				Long replyId = new Long(PortletRequestUtils.getLongParameter(request, "replyId"));
				if (replyId == null) replyId = entryId;
		        Long tokenId = new Long(PortletRequestUtils.getRequiredLongParameter(request, "tokenId"));	
				String toState = PortletRequestUtils.getRequiredStringParameter(request, "toState");
				getFolderModule().modifyWorkflowState(folderId, replyId, tokenId, toState);
				response.setRenderParameter(WebKeys.IS_REFRESH, "1");
			} else if (formData.containsKey("changeRatingBtn")) {
				Long replyId = new Long(PortletRequestUtils.getLongParameter(request, "replyId"));
				if (replyId == null) replyId = entryId;
				long rating = PortletRequestUtils.getRequiredLongParameter(request, "rating");
				getFolderModule().setUserRating(folderId, replyId, rating);
				response.setRenderParameter(WebKeys.IS_REFRESH, "1");
			} else if (formData.containsKey("changeTags")) {
				boolean community = true;
				Long replyId = new Long(PortletRequestUtils.getLongParameter(request, "replyId"));
				if (replyId == null) replyId = entryId;
				String tag = PortletRequestUtils.getRequiredStringParameter(request, "tag");
				String scope = PortletRequestUtils.getRequiredStringParameter(request,"scope");
				if (scope.equalsIgnoreCase("Personal")) community = false;
				getFolderModule().setTag(folderId, replyId, tag, community);
				response.setRenderParameter(WebKeys.IS_REFRESH, "1");
			} else if (formData.containsKey("respondBtn")) {
				Long replyId = new Long(PortletRequestUtils.getLongParameter(request, "replyId"));
				if (replyId == null) replyId = entryId;
		        Long tokenId = new Long(PortletRequestUtils.getRequiredLongParameter(request, "tokenId"));	
		        getFolderModule().setWorkflowResponse(folderId, replyId, tokenId, new MapInputData(formData));
		        //force reload of listing for state change
				response.setRenderParameter(WebKeys.IS_REFRESH, "1");
			} else if (op.equals(WebKeys.OPERATION_SET_WIKI_HOMEPAGE)) {
				Binder binder = getBinderModule().getBinder(folderId);
				//Check the access rights of the user
				if (getBinderModule().testAccess(binder, BinderOperation.setProperty)) {
					getBinderModule().setProperty(folderId, ObjectKeys.BINDER_PROPERTY_WIKI_HOMEPAGE, entryId.toString());
					response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());		
					response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
					response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_RELOAD_LISTING);
				}
			} 
		}
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		String displayStyle = user.getDisplayStyle();
		if (request.getWindowState().equals(WindowState.NORMAL)) 
			return BinderHelper.CommonPortletDispatch(this, request, response);
		
		Map<String,Object> model = new HashMap();
		
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String entryId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		String entryViewType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_VIEW_TYPE, "entryView");
		String entryViewStyle = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_VIEW_STYLE, "");
		String entryViewStyle2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_VIEW_STYLE2, "");
		String displayType = BinderHelper.getDisplayType(request);
		if (entryViewStyle.equals("")) {
			if (ObjectKeys.USER_DISPLAY_STYLE_NEWPAGE.equals(displayStyle) &&
					!ViewController.WIKI_PORTLET.equals(displayType)) entryViewStyle = "full";
		}
		
		Map formData = request.getParameterMap();
		Map userProperties = getProfileModule().getUserProperties(null).getProperties();
		
		//Let the jsp know what style to show the entry in 
		//  (popup has no navbar header, inline has no navbar and no script tags, full has a navbar header)
		model.put(WebKeys.ENTRY_VIEW_STYLE, entryViewStyle);
		model.put(WebKeys.ENTRY_VIEW_STYLE2, entryViewStyle2);
		
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if (!operation.equals("")) {
			model.put(WebKeys.URL_OPERATION, operation);
		}
		
		//Set up the standard beans
		BinderHelper.setupStandardBeans(this, request, response, model);

		model.put(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
		
 		//BinderHelper.getViewType requires read access to the binder.  
 		//This causes access errors when have access to an entry but not the binder which happens
 		//when you are following an email permalink link
 		//setup default value for reload case = viewType shouldn't matter
 		String viewPath=WebKeys.VIEW_LISTING_IFRAME;
 		model.put(WebKeys.TABS, Tabs.getTabs(request));
 		FolderEntry fe = null;
		try {
			if (Validator.isNull(entryId)) {
				entryId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TITLE, "");
				model.put(WebKeys.ENTRY_TITLE, PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_PAGE_TITLE, ""));
				Set entries = getFolderModule().getFolderEntryByNormalizedTitle(folderId, entryId);
				if (entries.size() == 1) {
					FolderEntry entry = (FolderEntry)entries.iterator().next();
					entryId = entry.getId().toString();
					fe = getShowEntry(entryId, formData, request, response, folderId, model, entryViewType);
				} else if (entries.size() == 0) {
					//There are no entries by this title
					Folder folder = getFolderModule().getFolder(folderId);
					buildNoEntryBeans(request, response, folder, entryId, model);
					return new ModelAndView(WebKeys.VIEW_NO_TITLE_ENTRY, model);
				} else {
					//There are multiple matches
					model.put(WebKeys.FOLDER_ENTRIES, entries);
					return new ModelAndView(WebKeys.VIEW_MULTIPLE_TITLE_ENTRIES, model);
				}
			} else {
				try {
					fe = getShowEntry(entryId, formData, request, response, folderId, model, entryViewType);
				} catch (NoFolderEntryByTheIdException nf) {
					Folder newFolder = getFolderModule().locateEntry(Long.valueOf(entryId));
					if (newFolder == null) throw nf;
					model.put("entryMoved", newFolder);
					throw nf;
				} catch(OperationAccessControlExceptionNoName e) {
					//Access is not allowed
					if (WebHelper.isUserLoggedIn(request) && 
							!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
						//Access is not allowed
						return new ModelAndView(WebKeys.VIEW_ACCESS_DENIED, model);
					} else {
						//Please log in
						String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
						model.put(WebKeys.URL, refererUrl);
						return new ModelAndView(WebKeys.VIEW_LOGIN_PLEASE_SNIPPET, model);
					}
				}
			}

			//Set up the rest of the standard beans
			//These have been documented, so don't delete any
			model.put(WebKeys.DEFINITION_ENTRY, fe);
			model.put(WebKeys.ENTRY, fe);

			//Build the reload url (after getting the entryId from the title if necessary)
			if (PortletAdapterUtil.isRunByAdapter((PortletRequest) request)) {
				AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", false);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
				adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folderId.toString());
				adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
				adapterUrl.setParameter(WebKeys.URL_RANDOM, WebKeys.URL_RANDOM_PLACEHOLDER);
				model.put(WebKeys.RELOAD_URL, adapterUrl.toString());
				if (formData.containsKey(WebKeys.RELOAD_URL_FORCED)) {
					model.clear();
					model.put(WebKeys.RELOAD_URL_FORCED, adapterUrl.toString());			
					return new ModelAndView(WebKeys.VIEW_LISTING_IFRAME, model);
				} 
			} else {
				PortletURL reloadUrl = response.createRenderURL();
				reloadUrl.setParameter(WebKeys.URL_BINDER_ID, folderId.toString());
				reloadUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
				reloadUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_VIEW_ENTRY);
				reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
				reloadUrl.setParameter(WebKeys.URL_RANDOM, WebKeys.URL_RANDOM_PLACEHOLDER);
				model.put(WebKeys.RELOAD_URL, reloadUrl.toString());
				if (formData.containsKey(WebKeys.RELOAD_URL_FORCED)) {
					model.clear();
					model.put(WebKeys.RELOAD_URL_FORCED, reloadUrl.toString());			
					return new ModelAndView(WebKeys.VIEW_LISTING_IFRAME, model);
				} 
			}

			String viewType = BinderHelper.getViewType(this, fe.getParentBinder());			
			viewPath = BinderHelper.getViewListingJsp(this, viewType);
			buildEntryToolbar(request, response, model, fe, viewType, userProperties);
			setRepliesAccessControl(model, fe);

			//Build the navigation beans
			BinderHelper.buildNavigationLinkBeans(this, fe.getParentBinder(), model);
			BinderHelper.buildWorkspaceTreeBean(this, fe.getParentBinder(), model, null);
				
			//only want to update visits when first enter.  Don't want cancels on modifies
			//to increment count
			if (!PortletRequestUtils.getStringParameter(request, WebKeys.IS_REFRESH, "0").equals("1")) { 
				//doesn't make sense on replies unless we update the visits when replies are show with the entry
				//that seems wasteful, so don't bother at all
				if (fe.isTop()) getFolderModule().setUserVisit(fe);  
		       	getReportModule().addAuditTrail(AuditTrail.AuditType.view, fe);

			}
		} catch(NoFolderEntryByTheIdException e) {
		} catch(OperationAccessControlExceptionNoName e) {
			//Access is not allowed
			if (WebHelper.isUserLoggedIn(request) && 
					!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
				//Access is not allowed
				return new ModelAndView(WebKeys.VIEW_ACCESS_DENIED, model);
			} else {
				//Please log in
				String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
				model.put(WebKeys.URL, refererUrl);
				return new ModelAndView(WebKeys.VIEW_LOGIN_PLEASE_SNIPPET, model);
			}
		}
		
		if(fe == null) {
			return new ModelAndView("entry/deleted_entry", model);		
		} else {
			if (entryViewType.equals("entryBlogView") && PortletAdapterUtil.isRunByAdapter(request)) {
				model.put(WebKeys.SNIPPET, true);
				viewPath = "entry/view_entry_snippet";
			}
			return new ModelAndView(viewPath, model);
		}
	} 


	protected void setRepliesAccessControl(Map model, FolderEntry entry) {

		Map accessControlMap = (Map) model.get(WebKeys.ACCESS_CONTROL_MAP);
		HashMap entryAccessMap = new HashMap();
		if (accessControlMap.containsKey(entry.getId())) {
			entryAccessMap = (HashMap) accessControlMap.get(entry.getId());
		}
		
		List replies = new ArrayList((List)model.get(WebKeys.FOLDER_ENTRY_DESCENDANTS));
		if (replies != null)  {
			for (int i=0; i<replies.size(); i++) {
				FolderEntry reply = (FolderEntry)replies.get(i);
				accessControlMap.put(reply.getId(), entryAccessMap);
			}
		}
		
	}
	
	protected Toolbar buildEntryToolbar(RenderRequest request, RenderResponse response, 
			Map model, FolderEntry entry, String viewType, Map userProperties) {

		PortletURL url;
		
		User user = RequestContextHolder.getRequestContext().getUser();
		
		//Initialize the acl bean
		Map accessControlEntryMap = BinderHelper.getAccessControlEntityMapBean(model, entry);

		Map disabledQual = new HashMap();
		disabledQual.put("disabled", new Boolean(true));
		PortletURL nullPortletUrl = null;
		boolean reserveAccessCheck = false;
		boolean isUserBinderAdministrator = false;
		boolean isEntryReserved = false;
		boolean isLockedByAndLoginUserSame = false;

		if (getFolderModule().testAccess(entry, FolderOperation.reserveEntry)) {
			reserveAccessCheck = true;
		}
		if (getFolderModule().testAccess(entry, FolderOperation.overrideReserveEntry)) {
			isUserBinderAdministrator = true;
		}
		
		HistoryStamp historyStamp = entry.getReservation();
		if (historyStamp != null) isEntryReserved = true;

		if (isEntryReserved) {
			Principal lockedByUser = historyStamp.getPrincipal();
			if (lockedByUser.getId().equals(user.getId())) {
				isLockedByAndLoginUserSame = true;
			}
		}
		Definition def = entry.getEntryDef(); //cannot be null here
	    //The "Reply" menu
		//strings for urls
		String entryDefId=def.getId().toString();
		String entryId = entry.getId().toString();
		String folderId = entry.getParentFolder().getId().toString();
				
	    //Build the toolbar array
		Toolbar toolbar = new Toolbar();
		if (getFolderModule().testAccess(entry, FolderOperation.addReply)) {
			accessControlEntryMap.put("addReply", new Boolean(true));
			List replyStyles = DefinitionUtils.getPropertyValueList(def.getDefinition().getRootElement(), "replyStyle");
			model.put(WebKeys.ENTRY_REPLY_STYLES, replyStyles);
			if (!replyStyles.isEmpty()) {
				if (replyStyles.size() == 1) {
					//There is only one reply style, so show it not as a drop down menu
					String replyStyleId = (String)replyStyles.get(0);
					
					if (Validator.isNotNull(replyStyleId)) {
						AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
						adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_REPLY);
						adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folderId);
						adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, replyStyleId);
						adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);

						Map qualifiers = new HashMap();
						qualifiers.put("popup", new Boolean(true));
						toolbar.addToolbarMenu("1_reply", NLT.get("toolbar.reply"), 
								adapterUrl.toString(), qualifiers);
					}
				} else {
					toolbar.addToolbarMenu("1_reply", NLT.get("toolbar.reply"));
					Map qualifiers = new HashMap();
					qualifiers.put("popup", new Boolean(true));
					for (int i = 0; i < replyStyles.size(); i++) {
						String replyStyleId = (String)replyStyles.get(i);
						try {
							Definition replyDef = getDefinitionModule().getDefinition(replyStyleId);
							AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
							adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_REPLY);
							adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folderId);
							adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, replyStyleId);
							adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
							toolbar.addToolbarMenuItem("1_reply", "replies", NLT.getDef(replyDef.getTitle()), 
									adapterUrl.toString(), qualifiers);
						} catch (NoDefinitionByTheIdException e) {
							continue;
						}
					}
				} 
			}
		}
	    
		if (getFolderModule().testAccess(entry, FolderOperation.modifyEntry)) {
			if (isEntryReserved && !isLockedByAndLoginUserSame ) {
				toolbar.addToolbarMenu("2_modify", NLT.get("toolbar.modify"), nullPortletUrl, disabledQual);
				if (entry.isTop()) {
					toolbar.addToolbarMenu("4_move", NLT.get("toolbar.move"), nullPortletUrl, disabledQual);
					toolbar.addToolbarMenu("5_copy", NLT.get("toolbar.copy"), nullPortletUrl, disabledQual);
				}
			}
			else {
				accessControlEntryMap.put("modifyEntry", new Boolean(true));
				//The "Modify" menu
				Map qualifiers = new HashMap();
				qualifiers.put("popup", new Boolean(true));
				AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_FOLDER_ENTRY);
				adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folderId);
				adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, entryDefId);
				adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
				toolbar.addToolbarMenu("2_modify", NLT.get("toolbar.modify"), adapterUrl.toString(), qualifiers);
				if (entry.isTop()) {
					//The "Move" menu
					url = response.createActionURL();
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_FOLDER_ENTRY);
					url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOVE);
					url.setParameter(WebKeys.URL_BINDER_ID, folderId);
					url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
					toolbar.addToolbarMenu("4_move", NLT.get("toolbar.move"), url);
					//The "Copy" menu
					url = response.createActionURL();
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_FOLDER_ENTRY);
					url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_COPY);
					url.setParameter(WebKeys.URL_BINDER_ID, folderId);
					url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
					toolbar.addToolbarMenu("5_copy", NLT.get("toolbar.copy"), url);
				}
			}
			Iterator itWorkflows = entry.getParentBinder().getWorkflowDefinitions().iterator();
			if (itWorkflows.hasNext()) {
				//The "Workflow" menu
				Map qualifiers = new HashMap();
				qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.entryWorkflowMenu");
				toolbar.addToolbarMenu("6_workflow", NLT.get("toolbar.entryWorkflow"), "", qualifiers);
				
				//See if there are workflows running
				Map runningWorkflowDefs = new HashMap();
				Iterator itWorkflowStates = entry.getWorkflowStates().iterator();
				while (itWorkflowStates.hasNext()) {
					WorkflowState state = (WorkflowState) itWorkflowStates.next();
					Definition workflowDef = state.getDefinition();
					if (!runningWorkflowDefs.containsKey(workflowDef.getId())) {
						String wfTitle = NLT.getDef(workflowDef.getTitle());
						String wfTitle1 = wfTitle.replaceAll("'", "\\\\'");
						qualifiers = new HashMap();
						qualifiers.put("onClick", "if (ss_confirm) {return ss_confirm('" + NLT.get("entry.confirmStopWorkflow") + "', '"+wfTitle1+"')} else {return false}");
						url = response.createActionURL();
						url.setParameter(WebKeys.ACTION, WebKeys.ACTION_STOP_WORKFLOW);
						url.setParameter(WebKeys.URL_BINDER_ID, folderId);
						url.setParameter(WebKeys.URL_ENTRY_ID, entryId); 
						url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_STOP_WORKFLOW);
						url.setParameter(WebKeys.URL_WORKFLOW_TYPE, workflowDef.getId());
						toolbar.addToolbarMenuItem("6_workflow", "", 
								NLT.get("toolbar.menu.stopWorkflow", new String[] {wfTitle}), url, qualifiers);
						runningWorkflowDefs.put(workflowDef.getId(), "1");
					}
				}
				
				while (itWorkflows.hasNext()) {
					Definition workflowDef = (Definition) itWorkflows.next();
					if (!runningWorkflowDefs.containsKey(workflowDef.getId())) {
						String wfTitle = NLT.getDef(workflowDef.getTitle());
						String wfTitle1 = wfTitle.replaceAll("'", "\\\\'");
						qualifiers = new HashMap();
						qualifiers.put("onClick", "if (ss_confirm) {return ss_confirm('" + NLT.get("entry.confirmStartWorkflow") + "', '"+wfTitle1+"')} else {return false}");
						url = response.createActionURL();
						url.setParameter(WebKeys.ACTION, WebKeys.ACTION_START_WORKFLOW);
						url.setParameter(WebKeys.URL_BINDER_ID, folderId);
						url.setParameter(WebKeys.URL_ENTRY_ID, entryId); 
						url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_START_WORKFLOW);
						url.setParameter(WebKeys.URL_WORKFLOW_TYPE, workflowDef.getId());
						toolbar.addToolbarMenuItem("6_workflow", "", 
								NLT.get("toolbar.menu.startWorkflow", new String[] {wfTitle}), url, qualifiers);
					}
				}
			}
					}

		//Does the user have access to reserve the entry
		if (reserveAccessCheck) {
			//If no one has reserved the entry, it can be locked
			if (!isEntryReserved) {
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_LOCK_FOLDER_ENTRY);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_LOCK);
				url.setParameter(WebKeys.URL_BINDER_ID, folderId);
				url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
				toolbar.addToolbarMenu("3_lock", NLT.get("toolbar.lock"), url);
			} else {
			    //If some one has reserved the entry	
				//If the person who has locked the entry and the logged in user are the same we allow access to unlock
				//If the person who has logged in is the binder administrator we allow access to unlock
				if (isLockedByAndLoginUserSame || isUserBinderAdministrator) {
		   			url = response.createActionURL();
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_LOCK_FOLDER_ENTRY);
					url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_UNLOCK);
					url.setParameter(WebKeys.URL_BINDER_ID, folderId);
					url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
					if(!isLockedByAndLoginUserSame) {
						Map qualifiers = new HashMap();
						qualifiers.put("onClick", "return ss_confirmUnlockEntry();");
						toolbar.addToolbarMenu("3_lock", NLT.get("toolbar.unlock"), url, qualifiers);
					} else {
						toolbar.addToolbarMenu("3_lock", NLT.get("toolbar.unlock"), url);
					}
				} else {
					toolbar.addToolbarMenu("3_lock", NLT.get("toolbar.unlock"), nullPortletUrl, disabledQual);
				}
			}
		}
		
		if (getFolderModule().testAccess(entry, FolderOperation.deleteEntry)) {
			//The "Delete" menu
			if (isEntryReserved && !isLockedByAndLoginUserSame ) {
				toolbar.addToolbarMenu("5_delete", NLT.get("toolbar.delete"), nullPortletUrl, disabledQual);
			}
			else {
				accessControlEntryMap.put("deleteEntry", new Boolean(true));
				Map qualifiers = new HashMap();
				qualifiers.put("onClick", "return ss_confirmDeleteEntry();");
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_FOLDER_ENTRY);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_DELETE);
				url.setParameter(WebKeys.URL_BINDER_ID, folderId);
				url.setParameter(WebKeys.URL_ENTRY_TYPE, entryDefId);
				url.setParameter(WebKeys.URL_ENTRY_ID, entryId); 
				toolbar.addToolbarMenu("5_delete", NLT.get("toolbar.delete"), url, qualifiers);
			}
		}
		
		if (getFolderModule().testAccess(entry, FolderOperation.report)) {
			accessControlEntryMap.put("report", new Boolean(true));
			Map qualifiers = new HashMap();
			toolbar.addToolbarMenu("8_reports", NLT.get("toolbar.reports"), "", qualifiers);

			String servletUrl = WebUrlUtil.getServletRootURL(request) + WebKeys.SERVLET_DOWNLOAD_REPORT + "?" +
			WebKeys.URL_BINDER_ID + "=" + folderId + "&" + WebKeys.URL_ENTRY_ID + "=" + entryId + "&" +
			WebKeys.URL_REPORT_TYPE + "=entry&forumOkBtn=OK"; 
			toolbar.addToolbarMenuItem("8_reports", "", NLT.get("toolbar.reports.activity"), servletUrl, qualifiers);

			qualifiers.put("popup", Boolean.TRUE);
			AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_EDITABLE_HISTORY);
			adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MODIFY_ENTRY);
			adapterUrl.setParameter(WebKeys.URL_ENTITY_ID, entryId);
			toolbar.addToolbarMenuItem("8_reports", "", NLT.get("toolbar.reports.editHistory"), adapterUrl.toString(), qualifiers);
			
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WORKFLOW_HISTORY);
			adapterUrl.setParameter(WebKeys.URL_ENTITY_ID, entryId);
			adapterUrl.setParameter(WebKeys.URL_FOLDER_ID, folderId);
			toolbar.addToolbarMenuItem("8_reports", "", NLT.get("toolbar.reports.workflowHistory"), adapterUrl.toString(), qualifiers);
		}


		if (viewType.equals(Definition.VIEW_STYLE_WIKI)) {
			if (getBinderModule().testAccess(entry.getParentBinder(), BinderOperation.setProperty)) {
				Map qualifiers = new HashMap();
				qualifiers.put("onClick", "if (parent.ss_confirmSetWikiHomepage) {return parent.ss_confirmSetWikiHomepage()} else {return false}");
				qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.setWikiHomepage");
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_WIKI_HOMEPAGE);
				url.setParameter(WebKeys.URL_BINDER_ID, folderId);
				url.setParameter(WebKeys.URL_ENTRY_TYPE, entryDefId);
				url.setParameter(WebKeys.URL_ENTRY_ID, entryId); 
				toolbar.addToolbarMenu("7_setHomepage", NLT.get("toolbar.setWikiHomepage"), url, qualifiers);
			}
		}	
		
		Map qualifiers = new HashMap();
		qualifiers.put("popup", new Boolean(true));
		qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.shareThis");
		AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, "__ajax_relevance");
		adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SHARE_THIS_BINDER);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folderId);
		adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId); 
		toolbar.addToolbarMenu("8_shareThis", NLT.get("toolbar.shareThis"), adapterUrl.toString(), qualifiers);


		//The "Footer" menu
		Toolbar footerToolbar = new Toolbar();
		qualifiers = new HashMap();
		adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folderId);
		adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
		adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, entry.getEntityType().toString());
		qualifiers.put("onClick", "ss_showPermalink(this);return false;");
		footerToolbar.addToolbarMenu("permalink", NLT.get("toolbar.menu.entryPermalink"), adapterUrl.toString(), qualifiers);

		model.put(WebKeys.PERMALINK, adapterUrl.toString());

		if (entry.isTop() && !user.getEmailAddresses().isEmpty() && 
				!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			AdaptedPortletURL adapterSubscriptionUrl = new AdaptedPortletURL(request, "ss_forum", false);
			adapterSubscriptionUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_AJAX_REQUEST);
			adapterSubscriptionUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SUBSCRIBE);
			adapterSubscriptionUrl.setParameter(WebKeys.URL_BINDER_ID, folderId);
			adapterSubscriptionUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
			adapterSubscriptionUrl.setParameter("rn", "ss_randomNumberPlaceholder");			
			
			qualifiers = new HashMap();		
			qualifiers.put("onClick", "ss_createPopupDiv(this, 'ss_subscription_entry"+entryId+"'); return false;");
			footerToolbar.addToolbarMenu("subscribe", NLT.get("toolbar.menu.subscribeToEntry"), adapterSubscriptionUrl.toString(), qualifiers);
		}
		
		String[] contributorIds = collectContributorIds(entry);
		
		if (!user.getEmailAddresses().isEmpty() && 
				!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_SEND_ENTRY_EMAIL);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folderId);
			adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
			qualifiers = new HashMap();
			qualifiers.put("popup", Boolean.TRUE);
			qualifiers.put("post", Boolean.TRUE);
			qualifiers.put("postParams", Collections.singletonMap(WebKeys.USER_IDS_TO_ADD, contributorIds));			
			footerToolbar.addToolbarMenu("sendMail", NLT.get("toolbar.menu.sendMail"), adapterUrl.toString(), qualifiers);
		}

		if (getIcBrokerModule().isEnabled() && 
				!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_MEETING);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folderId);
			adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
			qualifiers = new HashMap();
			qualifiers.put("popup", Boolean.TRUE);
			qualifiers.put("post", Boolean.TRUE);
			qualifiers.put("postParams", Collections.singletonMap(WebKeys.USER_IDS_TO_ADD, contributorIds));
			footerToolbar.addToolbarMenu("addMeeting", NLT.get("toolbar.menu.addMeeting"), adapterUrl.toString(), qualifiers);
		}
		
		if (entry.getEvents() != null && !entry.getEvents().isEmpty()) {
			qualifiers = new HashMap();
			qualifiers.put("onClick", "ss_showPermalink(this);return false;");
			footerToolbar.addToolbarMenu("iCalendar", NLT.get("toolbar.menu.iCalendar"), UrlUtil.getICalURL(request, folderId, entryId), qualifiers);
		}
		
		model.put(WebKeys.FOLDER_ENTRY_TOOLBAR,  toolbar.getToolbar());
		model.put(WebKeys.FOOTER_TOOLBAR,  footerToolbar.getToolbar());

		return toolbar;
	}


	protected void buildNoEntryBeans(RenderRequest request, 
			RenderResponse response, Folder folder, String entryTitle, Map model) {
		//Initialize the acl bean
		Map accessControlFolderMap = BinderHelper.getAccessControlEntityMapBean(model, folder);

		//Build the "add entry" beans
		List defaultEntryDefinitions = folder.getEntryDefinitions();
		Map urls = new HashMap();
		Map titles = new TreeMap();
		model.put(WebKeys.ADD_ENTRY_DEFINITIONS,  defaultEntryDefinitions);
		model.put(WebKeys.ADD_ENTRY_URLS,  urls);
		model.put(WebKeys.ADD_ENTRY_TITLES,  titles);
		model.put(WebKeys.ADD_ENTRY_TITLE,  entryTitle);
		if (getFolderModule().testAccess(folder, FolderOperation.addEntry)) {				
			accessControlFolderMap.put("addEntry", new Boolean(true));
			if (!defaultEntryDefinitions.isEmpty()) {
				for (int i=0; i<defaultEntryDefinitions.size(); ++i) {
					Definition def = (Definition) defaultEntryDefinitions.get(i);
					AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
					adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_ENTRY);
					adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folder.getId().toString());
					adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, def.getId());
					urls.put(def.getId(), adapterUrl.toString());
					titles.put(NLT.getDef(def.getTitle()), def.getId());
					if (i == 0) {
						adapterUrl.setParameter(WebKeys.URL_NAMESPACE, response.getNamespace());
						adapterUrl.setParameter(WebKeys.URL_ADD_DEFAULT_ENTRY_FROM_INFRAME, "1");
						model.put(WebKeys.URL_ADD_DEFAULT_ENTRY, adapterUrl.toString());
					}
				}
			}
		}
	}

	private String[] collectContributorIds(FolderEntry entry) {		
		Set principals = new HashSet();
		collectCreatorAndMoficationIdsRecursive(entry, principals);
		String[] as = new String[principals.size()];
		principals.toArray(as);
		return as;
	}
		
	private void collectCreatorAndMoficationIdsRecursive(FolderEntry entry, Set principals) {		
		principals.add(entry.getCreation().getPrincipal().getId().toString());
		principals.add(entry.getModification().getPrincipal().getId().toString());
		Iterator repliesIt = entry.getReplies().iterator();
		while (repliesIt.hasNext()) {
			collectCreatorAndMoficationIdsRecursive((FolderEntry)repliesIt.next(), principals);
		}
	}
	
	protected FolderEntry getShowEntry(String entryId, Map formData, RenderRequest req, RenderResponse response, 
			Long folderId, Map model, String viewType)  {
		Folder folder = null;
		FolderEntry entry = null;
		Map folderEntries = null;
		folderEntries  = getFolderModule().getEntryTree(folderId, Long.valueOf(entryId));
		entry = (FolderEntry)folderEntries.get(ObjectKeys.FOLDER_ENTRY);
		folder = entry.getParentFolder();
		model.put(WebKeys.FOLDER_ENTRY_DESCENDANTS, folderEntries.get(ObjectKeys.FOLDER_ENTRY_DESCENDANTS));
		model.put(WebKeys.FOLDER_ENTRY_ANCESTORS, folderEntries.get(ObjectKeys.FOLDER_ENTRY_ANCESTORS));
		
		boolean isAppletSupported = SsfsUtil.supportApplets();
		
		String strEntryURL = DefinitionHelper.getWebDAVURL(req, folder, entry);
		//String strEntryURL = SsfsUtil.getEntryUrl(folder, entry, strRepositoryName);
		//String strWebDavURL = SsfsUtil.getLibraryBinderUrl(folder);
		
		SeenMap seen = getProfileModule().getUserSeenMap(null);
		
		model.put(WebKeys.IS_APPLET_SUPPORTED, isAppletSupported);
		model.put(WebKeys.SEEN_MAP, seen);
		model.put(WebKeys.ENTRY, entry);
		model.put(WebKeys.DEFINITION_ENTRY, entry);
		model.put(WebKeys.FOLDER, folder);
		model.put(WebKeys.BINDER, folder);
		model.put(WebKeys.BINDER_WEBDAV_URL, strEntryURL);
		//model.put(WebKeys.BINDER_WEBDAV_URL, strWebDavURL);
		Map tagResults = TagUtil.uniqueTags(getFolderModule().getTags(entry));
		model.put(WebKeys.COMMUNITY_TAGS, tagResults.get(ObjectKeys.COMMUNITY_ENTITY_TAGS));
		model.put(WebKeys.PERSONAL_TAGS, tagResults.get(ObjectKeys.PERSONAL_ENTITY_TAGS));
		model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_VIEW);
		if (DefinitionHelper.getDefinition(entry.getEntryDef(), model, "//item[@name='"+viewType+"']") == false) {
			DefinitionHelper.getDefaultEntryView(entry, model, "//item[@name='"+viewType+"']");
		}

		//only start transaction if necessary
		List replies = new ArrayList((List)model.get(WebKeys.FOLDER_ENTRY_DESCENDANTS));
		if (replies != null)  {
			replies.add(entry);
		}
		if (!seen.checkIfSeen(entry)) {  
			getProfileModule().setSeen(null, entry);
		}
		Map captionMap = new HashMap();
		Map questionsMap = new HashMap();
		Map transitionMap = new HashMap();
		for (int i=0; i<replies.size(); i++) {
			FolderEntry reply = (FolderEntry)replies.get(i);
			Set states = reply.getWorkflowStates();
			for (Iterator iter=states.iterator(); iter.hasNext();) {
				WorkflowState ws = (WorkflowState)iter.next();
				//store the UI caption for each state
				captionMap.put(ws.getTokenId(), WorkflowUtils.getStateCaption(ws.getDefinition(), ws.getState()));
				//See if user can transition out of this state
				if (getFolderModule().testTransitionOutStateAllowed(reply, ws.getTokenId())) {
					//get all manual transitions
					Map trans = getFolderModule().getManualTransitions(reply, ws.getTokenId());
					transitionMap.put(ws.getTokenId(), trans);
				} 
					
				Map qMap = getFolderModule().getWorkflowQuestions(reply, ws.getTokenId());
				questionsMap.put(ws.getTokenId(), qMap);
			}
		}
		
		Definition entryDefinition = entry.getEntryDef();
		Map fieldsData = getDefinitionModule().getEntryDefinitionElements(entryDefinition.getId());
		model.put(WebKeys.ENTRY_DEFINTION_ELEMENT_DATA, fieldsData);
		
		model.put(WebKeys.WORKFLOW_CAPTIONS, captionMap);
		model.put(WebKeys.WORKFLOW_QUESTIONS, questionsMap);
		model.put(WebKeys.WORKFLOW_TRANSITIONS, transitionMap);
		
		return entry;
	}
}