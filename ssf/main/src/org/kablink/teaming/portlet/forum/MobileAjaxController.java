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
package org.kablink.teaming.portlet.forum;

import static org.kablink.util.search.Restrictions.in;
import static org.kablink.util.search.Restrictions.eq;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import org.apache.commons.lang.Validate;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.calendar.AbstractIntervalView;
import org.kablink.teaming.calendar.EventsViewHelper;
import org.kablink.teaming.calendar.OneDayView;
import org.kablink.teaming.calendar.OneMonthView;
import org.kablink.teaming.comparator.BinderComparator;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NoDefinitionByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.portletadapter.MultipartFileSupport;
import org.kablink.teaming.portletadapter.portlet.HttpServletRequestReachable;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.search.filter.SearchFilterKeys;
import org.kablink.teaming.search.filter.SearchFilterRequestParser;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.CalendarHelper;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractControllerRetry;
import org.kablink.teaming.web.tree.DomTreeBuilder;
import org.kablink.teaming.web.tree.FolderConfigHelper;
import org.kablink.teaming.web.tree.WsDomTreeBuilder;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.Clipboard;
import org.kablink.teaming.web.util.DashboardHelper;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.Favorites;
import org.kablink.teaming.web.util.ListFolderHelper;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.PortletPreferencesUtil;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.ProfilesBinderHelper;
import org.kablink.teaming.web.util.RelevanceDashboardHelper;
import org.kablink.teaming.web.util.Tabs;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.WebStatusTicket;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.teaming.web.util.WorkspaceTreeHelper;
import org.kablink.util.Http;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Order;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.PortletRequestBindingException;

/**
 * @author Peter Hurley
 *
 */
public class MobileAjaxController  extends SAbstractControllerRetry {
	static Pattern replacePtrn = Pattern.compile("([\\p{Punct}&&[^\\*]])");	
	
	//caller will retry on OptimisiticLockExceptions
	public void handleActionRequestWithRetry(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");

		User user = RequestContextHolder.getRequestContext().getUser();
		if (WebHelper.isUserLoggedIn(request) && !ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
		
			//The user is logged in
			if (op.equals(WebKeys.OPERATION_MOBILE_ADD_ENTRY)) {
				ajaxMobileDoAddEntry(request, response);
			} else if (op.equals(WebKeys.OPERATION_MOBILE_ADD_REPLY)) {
				ajaxMobileDoAddReply(request, response);
			} else if (op.equals(WebKeys.OPERATION_MOBILE_MODIFY_ENTRY)) {
				ajaxMobileDoModifyEntry(request, response);
			} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_FRONT_PAGE)) {
				ajaxMobileDoFrontPage(this, request, response);
			} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_ENTRY)) {
				ajaxMobileDoShowEntry(this, request, response);
			} else if (op.equals(WebKeys.OPERATION_VIEW_TEAMING_LIVE)) {
				ajaxDoTeamingLive(this, request, response);
			} else if (op.equals(WebKeys.OPERATION_MOBILE_TRACK_THIS)) {
				ajaxMobileDoTrackThis(this, request, response);
			}
		}
	}
	
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if (!getAdminModule().isMobileAccessEnabled()) {
			Map model = new HashMap();
			return new ModelAndView("mobile/not_supported", model);
		}

		User user = RequestContextHolder.getRequestContext().getUser();
		if (!WebHelper.isUserLoggedIn(request) || ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			if (op.equals(WebKeys.OPERATION_VIEW_TEAMING_LIVE)) {
				return ajaxMobileLogin(this, request, response, "ss_forum", WebKeys.OPERATION_VIEW_TEAMING_LIVE);
			} else {
				return ajaxMobileLogin(this, request, response, "ss_mobile", WebKeys.OPERATION_MOBILE_SHOW_FRONT_PAGE);
			}
		}
		
		//The user is logged in
		if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_FOLDER)) {
			return ajaxMobileShowFolder(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_WORKSPACE)) {
			return ajaxMobileShowWorkspace(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_ENTRY)) {
			return ajaxMobileShowEntry(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_USER)) {
			return ajaxMobileShowUser(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_RECENT_PLACES)) {
			return ajaxMobileShowRecentPlaces(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_NEXT_ENTRY)) {
			return ajaxMobileShowNextEntry(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_PREV_ENTRY)) {
			return ajaxMobileShowPrevEntry(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_ADD_ENTRY)) {
			return ajaxMobileAddEntry(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_ADD_REPLY)) {
			return ajaxMobileAddReply(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_MODIFY_ENTRY)) {
			return ajaxMobileModifyEntry(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_WHATS_NEW)) {
			return ajaxMobileWhatsNew(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_TRACK_THIS)) {
			return ajaxMobileTrackThis(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_LOGIN)) {
			return ajaxMobileLogin(this, request, response, "ss_mobile", WebKeys.OPERATION_MOBILE_SHOW_FRONT_PAGE);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_FRONT_PAGE)) {
			return ajaxMobileFrontPage(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_SEARCH_RESULTS)) {
			return ajaxMobileSearchResults(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_FIND_PEOPLE) || 
				op.equals(WebKeys.OPERATION_MOBILE_FIND_PLACES)) {
			return ajaxMobileFindPeople(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_FAVORITES)) {
			return ajaxMobileShowFavorites(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_TEAMS)) {
			return ajaxMobileShowTeams(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_VIEW_TEAMING_LIVE)) {
			return ajaxShowTeamingLive(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_VIEW_TEAMING_LIVE_UPDATE)) {
			return ajaxShowTeamingLiveUpdate(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_TEAMING_LIVE_CHECK_FOR_ACTIVITY)) {
			return ajaxTeamingLiveCheckForActivity(this, request, response);
		}
		
		if (!WebHelper.isUserLoggedIn(request) || ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			return ajaxMobileLogin(this, request, response, "ss_mobile", WebKeys.OPERATION_MOBILE_SHOW_FRONT_PAGE);
		} else {
			return ajaxMobileFrontPage(this, request, response);
		}
	} 

	private void ajaxMobileDoAddEntry(ActionRequest request, ActionResponse response) 
	throws Exception {
		//Add an entry
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		//See if the add entry form was submitted
		Long entryId=null;
		if (formData.containsKey("okBtn") && WebHelper.isMethodPost(request)) {
			//The form was submitted. Go process it
			String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
			Map fileMap = new HashMap();
			MapInputData inputData = new MapInputData(formData);
			entryId= getFolderModule().addEntry(folderId, entryType, inputData, fileMap, null).getId();
			response.setRenderParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
			
			//See if the user wants to subscribe to this entry
			BinderHelper.subscribeToThisEntry(this, request, folderId, entryId);
		} else {
			String sUrl = PortletRequestUtils.getStringParameter(request, WebKeys.URL_MOBILE_URL, "");
			if (!sUrl.equals("")) response.sendRedirect(sUrl);
		}
	}

	private void ajaxMobileDoModifyEntry(ActionRequest request, ActionResponse response) 
	throws Exception {
		//Modify an entry
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		//See if the modify entry form was submitted
		if (formData.containsKey("okBtn") && WebHelper.isMethodPost(request)) {
			//The form was submitted. Go process it
			Map fileMap = new HashMap();
			Set deleteAtts = new HashSet();
			for (Iterator iter=formData.entrySet().iterator(); iter.hasNext();) {
				Map.Entry e = (Map.Entry)iter.next();
				String key = (String)e.getKey();
				if (key.startsWith("_delete_")) {
					deleteAtts.add(key.substring(8));
				}
			}
			getFolderModule().modifyEntry(folderId, entryId, 
					new MapInputData(formData), fileMap, deleteAtts, null, null);
			
			//See if the user wants to subscribe to this entry
			BinderHelper.subscribeToThisEntry(this, request, folderId, entryId);
		} else {
			String sUrl = PortletRequestUtils.getStringParameter(request, WebKeys.URL_MOBILE_URL, "");
			if (!sUrl.equals("")) response.sendRedirect(sUrl);
		}
	}

	private void ajaxMobileDoAddReply(ActionRequest request, ActionResponse response) 
			throws Exception {
		//Add an entry
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		//See if the add entry form was submitted
		Long entryId=null;
		if (formData.containsKey("okBtn") && WebHelper.isMethodPost(request)) {
			//The form was submitted. Go process it
			String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
			Map fileMap = new HashMap();
			MapInputData inputData = new MapInputData(formData);
			Long id = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
			entryId = getFolderModule().addReply(folderId, id, entryType, inputData, fileMap, null).getId();

			//See if the user wants to subscribe to this entry
			BinderHelper.subscribeToThisEntry(this, request, folderId, entryId);
		} else {
			String sUrl = PortletRequestUtils.getStringParameter(request, WebKeys.URL_MOBILE_URL, "");
			if (!sUrl.equals("")) response.sendRedirect(sUrl);
		}
	}

	private void ajaxMobileDoFrontPage(AllModulesInjected bs, ActionRequest request, ActionResponse response) 
			throws Exception {
		//Do front page stuff
		User user = RequestContextHolder.getRequestContext().getUser();
		Map formData = request.getParameterMap();
		//See if the add entry form was submitted
		if (formData.containsKey("miniblogBtn") && WebHelper.isMethodPost(request)) {
			//The miniblog form was submitted. Go process it
			String text = PortletRequestUtils.getStringParameter(request, "miniblogText", "");
			BinderHelper.addMiniBlogEntry(bs, text);
		} else if (formData.containsKey("whatsNewBtn") && WebHelper.isMethodPost(request)) {
			//User clicked on a Whats New option
			String type = PortletRequestUtils.getStringParameter(request, "whats_new", "");
			UserProperties userProperties = bs.getProfileModule().getUserProperties(user.getId());
			String savedType = (String)userProperties.getProperty(ObjectKeys.USER_PROPERTY_MOBILE_WHATS_NEW_TYPE);
			if (savedType == null) savedType = "";
			if (!type.equals("") && !type.equals(savedType)) {
				//Remember the last type of results
				bs.getProfileModule().setUserProperty(user.getId(), ObjectKeys.USER_PROPERTY_MOBILE_WHATS_NEW_TYPE, type);
			}
		} else if (formData.containsKey("acceptBtn") && WebHelper.isMethodPost(request)) {
			//User clicked "I Accept"
			getProfileModule().setUserProperty( null, "acceptedMobileDisclaimer", true );
		}
	}

	private void ajaxDoTeamingLive(AllModulesInjected bs, ActionRequest request, ActionResponse response) 
			throws Exception {
		//Do front page stuff
		User user = RequestContextHolder.getRequestContext().getUser();
		Map formData = request.getParameterMap();
		//See if the add entry form was submitted
		if (formData.containsKey("miniblogBtn") && WebHelper.isMethodPost(request)) {
			//The miniblog form was submitted. Go process it
			String text = PortletRequestUtils.getStringParameter(request, "miniblogText", "");
			BinderHelper.addMiniBlogEntry(bs, text);
		} else if (formData.containsKey("whatsNewBtn") && WebHelper.isMethodPost(request)) {
			//User clicked on a Whats New option
			String type = PortletRequestUtils.getStringParameter(request, "whats_new", "");
			UserProperties userProperties = bs.getProfileModule().getUserProperties(user.getId());
			String savedType = (String)userProperties.getProperty(ObjectKeys.USER_PROPERTY_TEAMING_LIVE_WHATS_NEW_TYPE);
			if (savedType == null) savedType = "";
			if (!type.equals("") && !type.equals(savedType)) {
				//Remember the last type of results
				bs.getProfileModule().setUserProperty(user.getId(), ObjectKeys.USER_PROPERTY_TEAMING_LIVE_WHATS_NEW_TYPE, type);
			}
		} else if (formData.containsKey("acceptBtn") && WebHelper.isMethodPost(request)) {
			//User clicked "I Accept"
			getProfileModule().setUserProperty( null, "acceptedMobileDisclaimer", true );
		}
	}

	private void ajaxMobileDoShowEntry(AllModulesInjected bs, ActionRequest request, ActionResponse response) 
			throws Exception {
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);				
		
		if (entryId != null) {
			//See if the user asked to change state
			if (formData.containsKey("changeStateBtn") && WebHelper.isMethodPost(request)) {
				//Change the state
				//Get the workflow process to change and the name of the new state
				Long replyId = new Long(PortletRequestUtils.getLongParameter(request, "replyId"));
				if (replyId == null) replyId = entryId;
		        Long tokenId = new Long(PortletRequestUtils.getRequiredLongParameter(request, "tokenId"));	
				String toState = PortletRequestUtils.getRequiredStringParameter(request, "toState");
				getFolderModule().modifyWorkflowState(folderId, replyId, tokenId, toState);
			}
		}
	}

	private void ajaxMobileDoTrackThis(AllModulesInjected bs, ActionRequest request, ActionResponse response) 
			throws Exception {
		Map formData = request.getParameterMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String type = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TYPE, "add");		
		BinderHelper.trackThisBinder(bs, binderId, type);
		try {
			if (binderId != null) {
				Binder binder = getBinderModule().getBinder(binderId);
				if (type.equals("delete") && 
						binder.getEntityType().equals(EntityType.workspace) && 
						binder.getDefinitionType().equals(Definition.USER_WORKSPACE_VIEW)) 
					//Also stop tracking the user if this is a user workspace
					BinderHelper.trackThisBinder(bs, binder.getOwnerId(), "deletePerson");
			}
		} catch(Exception e) {}
	}

	private ModelAndView ajaxMobileLogin(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, String portletName, String operation) throws Exception {
		Map model = new HashMap();
		String refererUrl = PortletRequestUtils.getStringParameter(request, WebKeys.URL_REFERER_URL);
		if (Validator.isNotNull(refererUrl)) {
			model.put(WebKeys.URL, refererUrl);
		} else {
			BinderHelper.setupStandardBeans(bs, request, response, model, null, portletName);
			refererUrl = Http.getCompleteURL(((HttpServletRequestReachable) request).getHttpServletRequest());
			if (Validator.isNotNull(refererUrl) && !refererUrl.contains("operation="+WebKeys.OPERATION_MOBILE_LOGIN)) {
				model.put(WebKeys.URL, refererUrl);
			} else {
				AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, portletName, true);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
				adapterUrl.setParameter(WebKeys.URL_OPERATION, operation);
				model.put(WebKeys.URL, adapterUrl);
			}
		}
		return new ModelAndView("mobile/show_login_form", model);
	}
	
	
	private ModelAndView ajaxMobileFrontPage(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String view = BinderHelper.setupMobileFrontPageBeans(bs, request, response, model, "mobile/show_front_page");

		return new ModelAndView(view, model);
	}

	private ModelAndView ajaxShowTeamingLive(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String view = BinderHelper.setupTeamingLiveBeans(bs, request, response, model, "mobile/show_teaming_live");

		//Reset the date used to see what is new
        HttpSession session = ((HttpServletRequestReachable) request).getHttpServletRequest().getSession();
        Date updateDate = new Date();
        session.setAttribute(WebKeys.TEAMING_LIVE_UPDATE_DATE, updateDate);
        model.put(WebKeys.TEAMING_LIVE_UPDATE_DATE, updateDate);
        
		return new ModelAndView(view, model);
	}

	private ModelAndView ajaxShowTeamingLiveUpdate(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String view = BinderHelper.setupTeamingLiveBeans(bs, request, response, model, "mobile/teaming_live_update");
        HttpSession session = ((HttpServletRequestReachable) request).getHttpServletRequest().getSession();
        Date updateDate = (Date) session.getAttribute(WebKeys.TEAMING_LIVE_UPDATE_DATE);
        if (updateDate == null) updateDate = new Date();
		model.put(WebKeys.TEAMING_LIVE_UPDATE_DATE, updateDate);
		return new ModelAndView(view, model);
	}

	private ModelAndView ajaxTeamingLiveCheckForActivity(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String view = "mobile/teaming_live_check_for_activity";
		
		//Check if the client needs to do an update. 
		//An empty status indicates no update needed
		// todo For now always do an update
		String status = "update";
		model.put("ss_teamingLiveStatus", status);

		response.setContentType("text/xml");
		return new ModelAndView(view, model);
	}

	private ModelAndView ajaxMobileSearchResults(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser(); 
		Map model = new HashMap();
		String queryName = PortletRequestUtils.getStringParameter(request, WebKeys.URL_SEARCH_QUERY_NAME, "");
		String scope = PortletRequestUtils.getStringParameter(request, WebKeys.URL_SEARCH_SCOPE, "site");
		model.put(WebKeys.SEARCH_SCOPE, scope);
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		model.put("ss_queryName", queryName);
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		try {
			if (binderId != null) {
				Binder binder = getBinderModule().getBinder(binderId);
				model.put(WebKeys.BINDER, binder);
			} else {
				Binder binder = getWorkspaceModule().getTopWorkspace();
				model.put(WebKeys.BINDER, binder);
				binderId = binder.getId();
			}
		} catch(Exception e) {}
		BinderHelper.setupStandardBeans(bs, request, response, model, binderId, "ss_mobile");

		String pageNumber = PortletRequestUtils.getStringParameter(request, "pageNumber", "1");
      	int pageSize = Integer.valueOf(WebKeys.MOBILE_PAGE_SIZE).intValue();
      	int pageStart = (Integer.parseInt(pageNumber) -1) * pageSize;
      	int pageEnd = pageStart + pageSize;
		Map formData = request.getParameterMap();
	    Tabs tabs = Tabs.getTabs(request);
		model.put(WebKeys.TABS, tabs);		
		String searchText = PortletRequestUtils.getStringParameter(request, "searchText", "");
		model.put(WebKeys.SEARCH_TEXT, searchText);
	    if (formData.containsKey("searchBtn") || formData.containsKey("quickSearch")) {
	    	SearchFilterRequestParser requestParser = new SearchFilterRequestParser(request, getDefinitionModule());
			Document searchQuery = requestParser.getSearchQuery();
			Map options = BinderHelper.prepareSearchOptions(bs, request);
			options.put(ObjectKeys.SEARCH_OFFSET, new Integer(pageStart));
			options.put(ObjectKeys.SEARCH_USER_MAX_HITS, new Integer(pageSize));
			if (scope.equals("local")) options.put(ObjectKeys.SEARCH_ANCESTRY, binderId.toString());;
			Map results =  bs.getBinderModule().executeSearchQuery(searchQuery, options);
			
			Tabs.TabEntry tab = tabs.addTab(searchQuery, options);
			
			BinderHelper.prepareSearchResultPage(bs, results, model, searchQuery, options, tab);
	    } else if (!queryName.equals("")){
			
			// get query and options from tab		
			Document searchQuery = BinderHelper.getSavedQuery(bs, queryName, bs.getProfileModule().getUserProperties(user.getId()));
			
			// get page no and actualize options
			// execute query
			// actualize tabs info
			Map options = BinderHelper.prepareSearchOptions(bs, request);
			options.put(ObjectKeys.SEARCH_OFFSET, new Integer(pageStart));
			options.put(ObjectKeys.SEARCH_USER_MAX_HITS, new Integer(pageSize));

			options.put(Tabs.TITLE, queryName);
			Map results =  bs.getBinderModule().executeSearchQuery(searchQuery, options);
			
			Tabs.TabEntry tab = tabs.addTab(searchQuery, options);
			
			BinderHelper.prepareSearchResultPage(bs, results, model, searchQuery, options, tab);
	    }
		Map userQueries = new HashMap();
		if (userProperties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
			userQueries = (Map)userProperties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES);
		}

      	//Get the total records found by the search
      	Integer totalRecords = (Integer)model.get(WebKeys.PAGE_TOTAL_RECORDS);
      	if (totalRecords == null) totalRecords = 0;
      	//Get the records returned (which may be more than the page size)
      	List results = (List)model.get(WebKeys.FOLDER_ENTRIES);
      	String nextPage = "";
      	String prevPage = "";
      	if (totalRecords.intValue() < pageStart) {
      		if (Integer.parseInt(pageNumber) > 1) prevPage = String.valueOf(Integer.parseInt(pageNumber) - 1);
      	} else if (totalRecords.intValue() >= pageEnd) {
      		nextPage = String.valueOf(Integer.parseInt(pageNumber) + 1);
      		if (Integer.parseInt(pageNumber) > 1) prevPage = String.valueOf(Integer.parseInt(pageNumber) - 1);
      	} else {
      		if (Integer.parseInt(pageNumber) > 1) prevPage = String.valueOf(Integer.parseInt(pageNumber) - 1);
      	}
		model.put(WebKeys.TAB_ID, String.valueOf(model.get(WebKeys.URL_TAB_ID)));
		model.put(WebKeys.PAGE_NUMBER, pageNumber);
		model.put(WebKeys.NEXT_PAGE, nextPage);
		model.put(WebKeys.PREV_PAGE, prevPage);

		//Setup the actions menu list
		List actions = new ArrayList();
		//BinderHelper.addActionsHome(request, actions);
		BinderHelper.addActionsRecentPlaces(request, actions, binderId);
		BinderHelper.addActionsSpacer(request, actions);
		BinderHelper.addActionsLogout(request, actions);
		model.put("ss_actions", actions);
		
		return new ModelAndView("mobile/show_search_results", model);
	}

	private ModelAndView ajaxMobileShowFolder(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		Binder binder = getBinderModule().getBinder(binderId);
		BinderHelper.setupStandardBeans(bs, request, response, model, binderId, "ss_mobile");
		UserProperties userProperties = (UserProperties)model.get(WebKeys.USER_PROPERTIES_OBJ);
		UserProperties userFolderProperties = (UserProperties)model.get(WebKeys.USER_FOLDER_PROPERTIES_OBJ);
		Map options = new HashMap();		
		Map folderEntries = null;
		
		if (binder== null) {
			return ajaxMobileFrontPage(this, request, response);
		} 
		if (binder.getEntityType().name().equals(EntityIdentifier.EntityType.workspace.name())) {
			return ajaxMobileShowWorkspace(this, request, response);
		} else if (binder.getEntityType().name().equals(EntityIdentifier.EntityType.profiles.name())) {
			return ajaxMobileShowWorkspace(this, request, response);
		}
		model.put(WebKeys.BINDER, binder);

		Tabs.TabEntry tab= BinderHelper.initTabs(request, binder);
		model.put(WebKeys.TABS, tab.getTabs());		

		if (binder != null) {
			//See if the user has selected a specific view to use
			DefinitionHelper.getDefinitions(binder, model);
		}
		Document configDocument = (Document)model.get(WebKeys.CONFIG_DEFINITION);
		String family = null;
		if (configDocument != null) family = DefinitionUtils.getFamily(configDocument);
		if (family == null) family = "";
		model.put(WebKeys.DEFINITION_FAMILY, family);
		
		//Build the mashup beans
		DefinitionHelper.buildMashupBeans(bs, binder, configDocument, model, request );

		Integer pageNumber = PortletRequestUtils.getIntParameter(request, WebKeys.URL_PAGE_NUMBER);
      	if (pageNumber == null || pageNumber < 0) pageNumber = 0;
      	int pageSize = Integer.valueOf(WebKeys.MOBILE_PAGE_SIZE).intValue();
      	int pageStart = pageNumber.intValue() * pageSize;
      	String nextPage = "";
      	String prevPage = "";
      	options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.valueOf(pageSize));
      	options.put(ObjectKeys.SEARCH_OFFSET, Integer.valueOf(pageStart));

      	if (family.equals(Constants.FAMILY_FIELD_CALENDAR)) {
      		folderEntries = ListFolderHelper.findCalendarEvents(bs, request, response, binder, model);
      	} else {
      		folderEntries = getFolderModule().getEntries(binderId, options);
      	}
      	
      	model.put(WebKeys.SEARCH_TOTAL_HITS, folderEntries.get(ObjectKeys.SEARCH_COUNT_TOTAL));
		if (folderEntries != null) {
			model.put(WebKeys.FOLDER_ENTRIES, (List) folderEntries.get(ObjectKeys.SEARCH_ENTRIES));
		}
		
      	if (pageNumber.intValue() > 0) prevPage = String.valueOf(pageNumber - 1);
      	if (((List) folderEntries.get(ObjectKeys.SEARCH_ENTRIES)).size() == pageSize && 
      			((Integer)folderEntries.get(ObjectKeys.SEARCH_COUNT_TOTAL)).intValue() > ((pageNumber.intValue() + 1) * pageSize)) 
      		nextPage = String.valueOf(pageNumber + 1);
		model.put(WebKeys.PAGE_NUMBER, pageNumber.toString());
		model.put(WebKeys.NEXT_PAGE, nextPage);
		model.put(WebKeys.PREV_PAGE, prevPage);

		model.put(WebKeys.PAGE_ENTRIES_PER_PAGE, (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS));

		if (binder instanceof Folder && getFolderModule().testAccess((Folder)binder, FolderOperation.addEntry)) {				
			List defaultEntryDefinitions = binder.getEntryDefinitions();
			List<Map> defTitleUrlList = new ArrayList();
			for (int i=0; i<defaultEntryDefinitions.size(); ++i) {
				Definition def = (Definition) defaultEntryDefinitions.get(i);
				String title = NLT.getDef(def.getTitle());
				AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_mobile", true);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
				adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
				adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, def.getId());
				adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_ADD_ENTRY);
				Map defTitle = new HashMap();
				defTitle.put("title", title);
				defTitle.put("def", def);
				defTitle.put("url", adapterUrl.toString());
				defTitleUrlList.add(defTitle);
			}
			model.put(WebKeys.MOBILE_BINDER_DEF_URL_LIST, defTitleUrlList);
		}

		if (binder != null) {
			DashboardHelper.getDashboardMap(binder, userProperties.getProperties(), model);
			//See if the user has selected a specific view to use
			DefinitionHelper.getDefinitions(binder, model, 
					(String)userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION));
		}
		List folders = new ArrayList();
		List folderIds = new ArrayList();
		folderIds.add(binderId.toString());
		Criteria crit = new Criteria();
		crit.add(in(Constants.DOC_TYPE_FIELD, new String[] {Constants.DOC_TYPE_BINDER}))
			.add(in(Constants.BINDERS_PARENT_ID_FIELD, folderIds));
		crit.addOrder(Order.asc(Constants.SORT_TITLE_FIELD));
		Map binderMap = bs.getBinderModule().executeSearchQuery(crit, 0, ObjectKeys.SEARCH_MAX_HITS_SUB_BINDERS);

		List binderMapList = (List)binderMap.get(ObjectKeys.SEARCH_ENTRIES); 
		List binderIdList = new ArrayList();

      	for (Iterator iter=binderMapList.iterator(); iter.hasNext();) {
      		Map entryMap = (Map) iter.next();
      		binderIdList.add(new Long((String)entryMap.get("_docId")));
      	}
      	SortedSet binderList = bs.getBinderModule().getBinders(binderIdList);
        for (Iterator iter=binderList.iterator(); iter.hasNext();) {
     		Binder b = (Binder)iter.next();
      		if (b.isDeleted()) continue;
      		if (b.getEntityType().equals(EntityIdentifier.EntityType.folder)) {
      			folders.add(b);
      		}
		}
		if (!folders.isEmpty()) {
			model.put(WebKeys.FOLDERS, folders);
		}

		//Setup the actions menu list
		List actions = new ArrayList();
		//BinderHelper.addActionsHome(request, actions);
		BinderHelper.addActionsWhatsNew(request, actions, binder);
		BinderHelper.addActionsWhatsUnseen(request, actions, binder);
		BinderHelper.addActionsRecentPlaces(request, actions, binder.getId());
		BinderHelper.addActionsTrackThisBinder(bs, request, actions, binder);
		BinderHelper.addActionsSpacer(request, actions);
		BinderHelper.addActionsLogout(request, actions);
		model.put("ss_actions", actions);
		
		return new ModelAndView("mobile/show_folder", model);
	}

	private ModelAndView ajaxMobileWhatsNew(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser(); 
		Map model = new HashMap();
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		Binder binder = null;
		if (binderId == null) {
			binder = getWorkspaceModule().getTopWorkspace();
			binderId = binder.getId();
			model.put(WebKeys.MOBILE_WHATS_NEW_SITE, true);
		} else {
			binder = getBinderModule().getBinder(binderId);
		}
		String type = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TYPE, WebKeys.URL_WHATS_NEW);
		BinderHelper.setupStandardBeans(bs, request, response, model, binderId, "ss_mobile");

		Map options = new HashMap();		
		if (binder== null) {
			return ajaxMobileFrontPage(bs, request, response);
		} 
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.TYPE, type);

      	Integer pageNumber = PortletRequestUtils.getIntParameter(request, WebKeys.URL_PAGE_NUMBER);
      	if (pageNumber == null || pageNumber < 0) pageNumber = 0;
      	int pageSize = SPropsUtil.getInt("relevance.mobile.whatsNewPageSize");
      	int pageStart = pageNumber.intValue() * pageSize;
      	String nextPage = "";
      	String prevPage = "";
      	options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.valueOf(pageSize));
      	options.put(ObjectKeys.SEARCH_OFFSET, Integer.valueOf(pageStart));

		if (type.equals(WebKeys.URL_WHATS_NEW)) {
			BinderHelper.setupWhatsNewBinderBeans(bs, binder, model, String.valueOf(pageNumber));
		} else if (type.equals(WebKeys.URL_WHATS_NEW_TRACKED)|| type.equals(WebKeys.URL_WHATS_NEW_TEAMS)) {
			BinderHelper.setupWhatsNewBinderBeans(bs, binder, model, String.valueOf(pageNumber), type);
		} else if (type.equals(WebKeys.URL_UNSEEN)) {
			BinderHelper.setupUnseenBinderBeans(bs, binder, model, String.valueOf(pageNumber));
		}

		model.put(WebKeys.PAGE_NUMBER, pageNumber.toString());
		model.put(WebKeys.NEXT_PAGE, nextPage);
		model.put(WebKeys.PREV_PAGE, prevPage);

		model.put(WebKeys.PAGE_ENTRIES_PER_PAGE, (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS));

		//Setup the actions menu list
		List actions = new ArrayList();
		//BinderHelper.addActionsHome(request, actions);
		BinderHelper.addActionsRecentPlaces(request, actions, binderId);
		BinderHelper.addActionsSpacer(request, actions);
		BinderHelper.addActionsLogout(request, actions);
		model.put("ss_actions", actions);
		
		return new ModelAndView("mobile/show_whats_new", model);
	}

	private ModelAndView ajaxMobileShowWorkspace(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser(); 
		Map model = new HashMap();
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
		if (entryId != null && getProfileModule().getProfileBinderId().equals(binderId)) {
			//This is a request to show a user. Check if there is a workspace for this user
			Principal u = getProfileModule().getEntry(entryId);
			if (u != null && u instanceof User) {
				binderId = u.getWorkspaceId();
				if (binderId == null) {
					//There is no workspace for this user, show the user profile instead
					BinderHelper.setupStandardBeans(bs, request, response, model, null, "ss_mobile");
					model.put(WebKeys.ENTRY, u);
					model.put(WebKeys.BINDER, getProfileModule().getProfileBinder());
					
					//Setup the actions menu list
					List actions = new ArrayList();
					//BinderHelper.addActionsHome(request, actions);
					BinderHelper.addActionsRecentPlaces(request, actions, binderId);
					BinderHelper.addActionsSpacer(request, actions);
					BinderHelper.addActionsLogout(request, actions);
					model.put("ss_actions", actions);
					
					return new ModelAndView("mobile/show_user", model);
				}
			}
		}
		BinderHelper.setupStandardBeans(bs, request, response, model, binderId, "ss_mobile");
		UserProperties userProperties = (UserProperties)model.get(WebKeys.USER_PROPERTIES_OBJ);
		UserProperties userFolderProperties = (UserProperties)model.get(WebKeys.USER_FOLDER_PROPERTIES_OBJ);
		if (userFolderProperties == null) userFolderProperties = new UserProperties(user.getId(), binderId);
		Workspace binder;
		List wsList = new ArrayList();
		List workspaces = new ArrayList();
		List folders = new ArrayList();
		try {
			binder = getWorkspaceModule().getWorkspace(Long.valueOf(binderId));
		} catch (Exception ex) {
			binder = getWorkspaceModule().getTopWorkspace();				
		}
		model.put(WebKeys.BINDER, binder);
		if (binder == null) {
			return new ModelAndView("mobile/show_workspace", model);
		}

      	Integer pageNumber = PortletRequestUtils.getIntParameter(request, WebKeys.URL_PAGE_NUMBER);
      	if (pageNumber == null) pageNumber = 0;
      	int pageSize = Integer.valueOf(WebKeys.MOBILE_PAGE_SIZE).intValue();
      	int pageStart = pageNumber.intValue() * pageSize;
      	int pageEnd = pageStart + pageSize;
      	String nextPage = "";
      	String prevPage = "";

      	//See if the user has selected a specific view to use
		String userDefaultDef = (String)userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION);
		DefinitionHelper.getDefinitions(binder, model, userDefaultDef);

		//Build the mashup beans
		Document configDocument = (Document)model.get(WebKeys.CONFIG_DEFINITION);
		DefinitionHelper.buildMashupBeans(bs, binder, configDocument, model, request );

		Tabs.TabEntry tab= BinderHelper.initTabs(request, binder);
		model.put(WebKeys.TABS, tab.getTabs());		

		//See if this is a user workspace
		if (binder != null && binder.getDefinitionType() != null && 
				Definition.USER_WORKSPACE_VIEW == binder.getDefinitionType()) {
			Set wsUsers = new HashSet();
			Long userId = binder.getCreation().getPrincipal().getId();
			if (userId != null) wsUsers.add(userId);
			SortedSet wsUsers2 = getProfileModule().getUsers(wsUsers);
			if (wsUsers2.size() > 0) model.put(WebKeys.WORKSPACE_CREATOR, wsUsers2.first());
		}
		if (binder.getId().equals(getProfileModule().getProfileBinderId())) {
			//This is the profiles binder.
			Map users = null;
			Map options = new HashMap();
			
			options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(pageSize*(pageNumber + 1) + 1));
			options.put(ObjectKeys.SEARCH_OFFSET, new Integer(pageNumber*pageSize));
			options.put(ObjectKeys.SEARCH_SORT_BY, Constants.SORT_TITLE_FIELD);
			options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.FALSE);

			options.put(ObjectKeys.SEARCH_SEARCH_FILTER, BinderHelper.getSearchFilter(bs, binder, userFolderProperties));
			users = bs.getProfileModule().getUsers(options);
			List userMapList = (List)users.get(ObjectKeys.SEARCH_ENTRIES); 
			List userIdList = new ArrayList();
	
	      	for (Iterator iter=userMapList.iterator(); iter.hasNext();) {
	      		Map entryMap = (Map) iter.next();
	      		userIdList.add(new Long((String)entryMap.get("_docId")));
	      	}
	      	SortedSet userList = getProfileModule().getUsers(userIdList);
	        for (Iterator iter=userList.iterator(); iter.hasNext();) {
	     		User u = (User)iter.next();
	      		workspaces.add(u);
			}
	        if (workspaces.size() >= pageSize) {
	      		wsList = workspaces.subList(0, pageSize);
	      		nextPage = String.valueOf(pageNumber.intValue() + 1);
	        }
      		if (pageNumber.intValue() > 0) prevPage = String.valueOf(pageNumber.intValue() - 1);

		} else {
			Map options = new HashMap();
			options.put(ObjectKeys.SEARCH_MAX_HITS, SPropsUtil.getInt("mobile.max.binders", 200));
			Map binderMap = getBinderModule().getBinders(binder, options);
			List binderMapList = (List)binderMap.get(ObjectKeys.SEARCH_ENTRIES); 
			List binderIdList = new ArrayList();
	
	      	for (Iterator iter=binderMapList.iterator(); iter.hasNext();) {
	      		Map entryMap = (Map) iter.next();
	      		binderIdList.add(new Long((String)entryMap.get("_docId")));
	      	}
	      	SortedSet binderList = getBinderModule().getBinders(binderIdList);
	        for (Iterator iter=binderList.iterator(); iter.hasNext();) {
	     		Binder b = (Binder)iter.next();
	      		if (b.isDeleted()) continue;
	      		if (b.getEntityType().equals(EntityIdentifier.EntityType.workspace) || 
	      				b.getEntityType().equals(EntityIdentifier.EntityType.profiles)) {
	      			workspaces.add(b);
	      		} else if (b.getEntityType().equals(EntityIdentifier.EntityType.folder)) {
	      			folders.add(b);
	      		}
			}
	      	if (workspaces.size() < pageStart) {
	      		wsList = new ArrayList();
	      		if (pageNumber.intValue() > 0) prevPage = String.valueOf(pageNumber.intValue() - 1);
	      	} else if (workspaces.size() >= pageEnd) {
	      		wsList = workspaces.subList(pageStart, pageEnd);
	      		nextPage = String.valueOf(pageNumber.intValue() + 1);
	      		if (pageNumber.intValue() > 0) prevPage = String.valueOf(pageNumber.intValue() - 1);
	      	} else {
	      		wsList = workspaces.subList(pageStart, workspaces.size());
	      		if (pageNumber.intValue() > 0) prevPage = String.valueOf(pageNumber.intValue() - 1);
	      	}
		}

		model.put(WebKeys.WORKSPACES, wsList);
		model.put(WebKeys.PAGE_NUMBER, pageNumber.toString());
		model.put(WebKeys.NEXT_PAGE, nextPage);
		model.put(WebKeys.PREV_PAGE, prevPage);
		model.put(WebKeys.FOLDERS, folders);

		//Setup the actions menu list
		List actions = new ArrayList();
		//BinderHelper.addActionsHome(request, actions);
		BinderHelper.addActionsWhatsNew(request, actions, binder);
		BinderHelper.addActionsWhatsUnseen(request, actions, binder);
		BinderHelper.addActionsRecentPlaces(request, actions, binder.getId());
		if (!binder.getEntityType().equals(EntityIdentifier.EntityType.profiles))
			BinderHelper.addActionsTrackThisBinder(bs, request, actions, binder);
		BinderHelper.addActionsSpacer(request, actions);
		BinderHelper.addActionsLogout(request, actions);
		model.put("ss_actions", actions);
		
		return new ModelAndView("mobile/show_workspace", model);
	}

	private ModelAndView ajaxMobileShowNextEntry(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);		
		Binder binder = getBinderModule().getBinder(binderId);
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		Long nextEntryId = BinderHelper.getNextPrevEntry(bs, (Folder) binder, entryId, true);	
		if (nextEntryId != null) return ajaxMobileShowEntry(bs, request, response, nextEntryId);
		
		Map model = new HashMap();
		BinderHelper.setupStandardBeans(bs, request, response, model, binderId, "ss_mobile");
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.ENTRY_ID, entryId);

		//Setup the actions menu list
		List actions = new ArrayList();
		//BinderHelper.addActionsHome(request, actions);
		BinderHelper.addActionsRecentPlaces(request, actions, binderId);
		BinderHelper.addActionsSpacer(request, actions);
		BinderHelper.addActionsLogout(request, actions);
		model.put("ss_actions", actions);
		
		return new ModelAndView("mobile/show_no_entry", model);
	}
	private ModelAndView ajaxMobileShowPrevEntry(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);		
		Binder binder = getBinderModule().getBinder(binderId);
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		Long nextEntryId = BinderHelper.getNextPrevEntry(bs, (Folder) binder, entryId, false);	
		if (nextEntryId != null) return ajaxMobileShowEntry(bs, request, response, nextEntryId);
		
		Map model = new HashMap();
		BinderHelper.setupStandardBeans(bs, request, response, model, binderId, "ss_mobile");
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.ENTRY_ID, entryId);

		//Setup the actions menu list
		List actions = new ArrayList();
		//BinderHelper.addActionsHome(request, actions);
		BinderHelper.addActionsRecentPlaces(request, actions, binderId);
		BinderHelper.addActionsSpacer(request, actions);
		BinderHelper.addActionsLogout(request, actions);
		model.put("ss_actions", actions);
		
		return new ModelAndView("mobile/show_no_entry", model);
	}
	private ModelAndView ajaxMobileShowEntry(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		return ajaxMobileShowEntry(bs, request, response, entryId);
	}
	private ModelAndView ajaxMobileShowEntry(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Long entryId) throws Exception {
	Map model = new HashMap();
	Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);		
	Binder binder = getBinderModule().getBinder(binderId);
	BinderHelper.setupStandardBeans(bs, request, response, model, binderId, "ss_mobile");
	model.put(WebKeys.BINDER, binder);
	model.put(WebKeys.TABS, Tabs.getTabs(request));
	User user = RequestContextHolder.getRequestContext().getUser();
	
	FolderEntry entry = null;
	Map folderEntries = null;
	folderEntries  = getFolderModule().getEntryTree(binderId, entryId);
	if (folderEntries != null) {
		entry = (FolderEntry)folderEntries.get(ObjectKeys.FOLDER_ENTRY);
		model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_MOBILE);
		if (DefinitionHelper.getDefinition(entry.getEntryDef(), model, "//item[@name='entryView']") == false) {
			DefinitionHelper.getDefaultEntryView(entry, model);
		}
		BinderHelper.setAccessControlForAttachmentList(this, model, entry, user);
		Map accessControlMap = (Map) model.get(WebKeys.ACCESS_CONTROL_MAP);
		HashMap entryAccessMap = BinderHelper.getEntryAccessMap(this, model, entry);
		model.put(WebKeys.ENTRY, entry);
		model.put(WebKeys.DEFINITION_ENTRY, entry);
		model.put(WebKeys.FOLDER_ENTRY_DESCENDANTS, folderEntries.get(ObjectKeys.FOLDER_ENTRY_DESCENDANTS));
		model.put(WebKeys.FOLDER_ENTRY_ANCESTORS, folderEntries.get(ObjectKeys.FOLDER_ENTRY_ANCESTORS));
		if (DefinitionHelper.getDefinition(entry.getEntryDef(), model, "//item[@name='entryView']") == false) {
			DefinitionHelper.getDefaultEntryView(entry, model);
		}
		SeenMap seen = getProfileModule().getUserSeenMap(null);
		model.put(WebKeys.SEEN_MAP, seen);
		List replies = new ArrayList((List)model.get(WebKeys.FOLDER_ENTRY_DESCENDANTS));
		if (replies != null)  {
			accessControlMap.put(entry.getId(), entryAccessMap);
			for (int i=0; i<replies.size(); i++) {
				FolderEntry reply = (FolderEntry)replies.get(i);
				accessControlMap.put(reply.getId(), entryAccessMap);
			}
		}
		if (!seen.checkIfSeen(entry)) { 
			//only mark top entries as seen
			getProfileModule().setSeen(null, entry);
		}
		
		if (getFolderModule().testAccess(entry, FolderOperation.modifyEntry)) {
			AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_mobile", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
			adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entry.getId().toString());
			adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_MODIFY_ENTRY);
			model.put(WebKeys.MOBILE_ENTRY_MODIFY_URL, adapterUrl);
		}

		if (getFolderModule().testAccess(entry, FolderOperation.addReply)) {
			Definition def = entry.getEntryDef(); //cannot be null here
			List replyStyles = DefinitionUtils.getPropertyValueList(def.getDefinition().getRootElement(), "replyStyle");
			model.put(WebKeys.ENTRY_REPLY_STYLES, replyStyles);
			List<Map> defTitleUrlList = new ArrayList();
			model.put(WebKeys.MOBILE_BINDER_DEF_URL_LIST, defTitleUrlList);
			if (!replyStyles.isEmpty()) {
				for (int i = 0; i < replyStyles.size(); i++) {
					String replyStyleId = (String)replyStyles.get(i);
					Definition replyDef = getDefinitionModule().getDefinition(replyStyleId);
					String title = NLT.getDef(replyDef.getTitle());
					AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_mobile", true);
					adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
					adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
					adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entry.getId().toString());
					adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, replyDef.getId());
					adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_ADD_REPLY);
					Map defTitle = new HashMap();
					defTitle.put("title", title);
					defTitle.put("def", replyDef);
					defTitle.put("url", adapterUrl.toString());
					defTitleUrlList.add(defTitle);
				}
			}
		}
		List entries = new ArrayList();
		entries.add(entry);
		BinderHelper.buildWorkflowSupportBeans(this, entries, model);
	}

	//Setup the actions menu list
	List actions = new ArrayList();
	//BinderHelper.addActionsHome(request, actions);
	BinderHelper.addActionsRecentPlaces(request, actions, binderId);
	BinderHelper.addActionsSpacer(request, actions);
	BinderHelper.addActionsLogout(request, actions);
	model.put("ss_actions", actions);
	
	return new ModelAndView("mobile/show_entry", model);
}	

	private ModelAndView ajaxMobileShowUser(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		return ajaxMobileShowUser(bs, request, response, entryId);
	}
	private ModelAndView ajaxMobileShowUser(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Long entryId) throws Exception {
	Map model = new HashMap();
	Binder binder = getProfileModule().getProfileBinder();
	BinderHelper.setupStandardBeans(bs, request, response, model, binder.getId(), "ss_mobile");
	model.put(WebKeys.BINDER, binder);
	model.put(WebKeys.TABS, Tabs.getTabs(request));
	User user = RequestContextHolder.getRequestContext().getUser();
	
	Principal entry = getProfileModule().getEntry(entryId);
	model.put(WebKeys.ENTRY, entry);
	
	//Setup the actions menu list
	List actions = new ArrayList();
	//BinderHelper.addActionsHome(request, actions);
	BinderHelper.addActionsRecentPlaces(request, actions, binder.getId());
	BinderHelper.addActionsSpacer(request, actions);
	BinderHelper.addActionsLogout(request, actions);
	model.put("ss_actions", actions);
	
	return new ModelAndView("mobile/show_user", model);
}	

	private ModelAndView ajaxMobileTrackThis(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);		
		Map model = new HashMap();
		if (binderId == null) return ajaxMobileFrontPage(bs, request, response);
		try {
			Binder binder = getBinderModule().getBinder(binderId);
			model.put(WebKeys.BINDER, binder);
			if (binder.getEntityType().equals(EntityType.folder)) {
				return ajaxMobileShowFolder(bs, request, response);
			} else {
				return ajaxMobileShowWorkspace(bs, request, response);
			}
		} catch(Exception e) {
			return ajaxMobileFrontPage(bs, request, response);
		}
	}
	
	private ModelAndView ajaxMobileShowRecentPlaces(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);		
		Map model = new HashMap();
		if (binderId != null) {
			try {
				Binder binder = getBinderModule().getBinder(binderId);
				model.put(WebKeys.BINDER, binder);
			} catch(Exception e) {}
		}
	    Tabs tabs = Tabs.getTabs(request);
		model.put(WebKeys.TABS, tabs);		

		//Setup the actions menu list
		List actions = new ArrayList();
		//BinderHelper.addActionsHome(request, actions);
		BinderHelper.addActionsRecentPlaces(request, actions, binderId);
		BinderHelper.addActionsSpacer(request, actions);
		BinderHelper.addActionsLogout(request, actions);
		model.put("ss_actions", actions);
		
		return new ModelAndView("mobile/show_recent_places", model);
	}
	private ModelAndView ajaxMobileShowFavorites(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);		
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		Map model = new HashMap();
		BinderHelper.setupStandardBeans(bs, request, response, model, null, "ss_mobile");
		if (binderId != null) {
			try {
				Binder binder = getBinderModule().getBinder(binderId);
				model.put(WebKeys.BINDER, binder);
			} catch(Exception e) {}
		}

		Object obj = userProperties.get(ObjectKeys.USER_PROPERTY_FAVORITES);
		Favorites f;
		if (obj != null && obj instanceof Document) {
			f = new Favorites((Document)obj);
			//fixup - have to store as string cause hibernate equals fails
			getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_FAVORITES, f.toString());
		} else {		
			f = new Favorites((String)obj);
		}
		List<Map> favList = f.getFavoritesList();
		model.put(WebKeys.MOBILE_FAVORITES_LIST, favList);

		//Setup the actions menu list
		List actions = new ArrayList();
		//BinderHelper.addActionsHome(request, actions);
		BinderHelper.addActionsRecentPlaces(request, actions, binderId);
		BinderHelper.addActionsSpacer(request, actions);
		BinderHelper.addActionsLogout(request, actions);
		model.put("ss_actions", actions);
		
		return new ModelAndView("mobile/show_favorites", model);
	}
	
	private ModelAndView ajaxMobileShowTeams(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);		
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		Map model = new HashMap();
		BinderHelper.setupStandardBeans(bs, request, response, model, null, "ss_mobile");
		if (binderId != null) {
			try {
				Binder binder = getBinderModule().getBinder(binderId);
				model.put(WebKeys.BINDER, binder);
			} catch(Exception e) {}
		}

		List<Long> teamIds = new ArrayList<Long>();
		Collection myTeams = bs.getBinderModule().getTeamMemberships(user.getId());
		model.put(WebKeys.MOBILE_TEAMS_LIST, myTeams);
		
		//Setup the actions menu list
		List actions = new ArrayList();
		//BinderHelper.addActionsHome(request, actions);
		BinderHelper.addActionsRecentPlaces(request, actions, binderId);
		BinderHelper.addActionsSpacer(request, actions);
		BinderHelper.addActionsLogout(request, actions);
		model.put("ss_actions", actions);
		
		return new ModelAndView("mobile/show_teams", model);
	}
	
	private ModelAndView ajaxMobileAddEntry(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));		
		Folder folder = getFolderModule().getFolder(folderId);
		//Adding an entry; get the specific definition
		Map folderEntryDefs = DefinitionHelper.getEntryDefsAsMap(folder);
		String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
    	request.setAttribute(WebKeys.URL_ENTRY_TYPE, entryType);
		model.put(WebKeys.FOLDER, folder);
		model.put(WebKeys.BINDER, folder);
		model.put(WebKeys.ENTRY_DEFINITION_MAP, folderEntryDefs);
		model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_MOBILE);
		model.put(WebKeys.DEFINITION_ID, entryType);
		//Make sure the requested definition is legal
		if (folderEntryDefs.containsKey(entryType)) {
			DefinitionHelper.getDefinition((Definition)folderEntryDefs.get(entryType), model, "//item[@type='form']");
		} else {
			DefinitionHelper.getDefinition(null, model, "//item[@name='entryForm']");
		}
		Map formData = request.getParameterMap();
		if (entryType.equals("") || formData.containsKey("okBtn") || formData.containsKey("cancelBtn")) {
			return ajaxMobileShowFolder(bs, request, response);
		} else {
			return new ModelAndView("mobile/add_entry", model);
		}
	}	

	private ModelAndView ajaxMobileModifyEntry(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));		
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));		
		Folder folder = getFolderModule().getFolder(folderId);
		FolderEntry entry = getFolderModule().getEntry(folderId, entryId);
		//Adding an entry; get the specific definition
		model.put(WebKeys.FOLDER, folder);
		model.put(WebKeys.BINDER, folder);
		model.put(WebKeys.ENTRY, entry);
		model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_MOBILE);
		DefinitionHelper.getDefinition(entry.getEntryDef(), model, "//item[@type='form']");
		
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn") || formData.containsKey("cancelBtn")) {
			return ajaxMobileShowEntry(bs, request, response);
		} else {
			return new ModelAndView("mobile/modify_entry", model);
		}
	}	

	private ModelAndView ajaxMobileAddReply(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();

		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));		
    	Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));
    	request.setAttribute(WebKeys.URL_ENTRY_ID, entryId.toString());
    	FolderEntry entry = getFolderModule().getEntry(folderId, entryId);
    	Folder folder = entry.getParentFolder();
    	model.put(WebKeys.FOLDER, folder); 
    	model.put(WebKeys.ENTRY, entry); 
			
    	//Get the legal reply types from the parent entry definition
		Document entryView = null;
		Definition entryDefinition = entry.getEntryDef();
		if (entryDefinition != null) {
			entryView = entryDefinition.getDefinition();
		}
		List replyStyles = null;
		if (entryView != null) {
			//See if there is a reply style for this entry definition
			replyStyles = DefinitionUtils.getPropertyValueList(entryDefinition.getDefinition().getRootElement(), "replyStyle");
		}
   	
    	//Adding an entry; get the specific definition
		Map folderEntryDefs = DefinitionHelper.getEntryDefsAsMap(folder);
    	String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
    	request.setAttribute(WebKeys.URL_ENTRY_TYPE, entryType);
		model.put(WebKeys.DEFINITION_ID, entryType);
    	model.put(WebKeys.ENTRY_DEFINITION_MAP, folderEntryDefs);
    	model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_MOBILE);
    	
        //Make sure the requested reply definition is legal
    	boolean replyStyleIsGood = false;
    	Iterator itReplyStyles = replyStyles.iterator();
    	while (itReplyStyles.hasNext()) {
    		if (((String)itReplyStyles.next()).equals(entryType)) {
    			replyStyleIsGood = true;
    			break;
    		}
    	}
	    	
		if (replyStyleIsGood) {
			DefinitionHelper.getDefinition(getDefinitionModule().getDefinition(entryType), model, "//item[@type='form']");
		} else {
			DefinitionHelper.getDefinition(null, model, "//item[@name='entryForm']");
		}
		
		Map formData = request.getParameterMap();
		if (entryType.equals("") || formData.containsKey("okBtn") || formData.containsKey("cancelBtn")) {
			return ajaxMobileShowEntry(bs, request, response);
		} else {
			return new ModelAndView("mobile/add_reply", model);
		}
	}	

	private ModelAndView ajaxMobileFindPeople(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);		
		Map model = new HashMap();
		BinderHelper.setupStandardBeans(bs, request, response, model, null, "ss_mobile");
		if (binderId != null) {
			try {
				Binder binder = getBinderModule().getBinder(binderId);
				model.put(WebKeys.BINDER, binder);
			} catch(Exception e) {}
		}
		Map formData = request.getParameterMap();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if (op.equals(WebKeys.OPERATION_MOBILE_FIND_PEOPLE) || op.equals(WebKeys.OPERATION_MOBILE_FIND_PLACES)) {
			String searchText = PortletRequestUtils.getStringParameter(request, "searchText", "");
			if (formData.containsKey("okBtn") || !searchText.equals("")) {
				model.put(WebKeys.SEARCH_TEXT, searchText);
				String maxEntries = PortletRequestUtils.getStringParameter(request, "maxEntries", "10");
				String pageNumber = PortletRequestUtils.getStringParameter(request, "pageNumber", "0");
				Integer startingCount = Integer.parseInt(pageNumber) * Integer.parseInt(maxEntries);

				User user = RequestContextHolder.getRequestContext().getUser();
				Map options = new HashMap();
				String view;
				options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.parseInt(maxEntries));
				options.put(ObjectKeys.SEARCH_OFFSET, startingCount);
				options.put(ObjectKeys.SEARCH_SORT_BY, Constants.SORT_TITLE_FIELD);
				options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(false));
				
				//Build the search query
				SearchFilter searchTermFilter = new SearchFilter();
				
				searchText = searchText.replaceAll(" \\*", "\\*");
			    searchText = searchText.trim() + "*";
			    
				//Add the login name term
				if (searchText.length()>0) {
					searchTermFilter.addTitleFilter(searchText);
					if (op.equals(WebKeys.OPERATION_MOBILE_FIND_PEOPLE)) {
						searchTermFilter.addLoginNameFilter(searchText);
					}
					if (op.equals(WebKeys.OPERATION_MOBILE_FIND_PLACES)) {
						searchTermFilter.addPlacesFilter(searchText, false);
					}
				}
			   	
				//Do a search to find the first few items that match the search text
				options.put(ObjectKeys.SEARCH_SEARCH_FILTER, searchTermFilter.getFilter());
				Map entries = null;
				view = "mobile/find_people";
				if (op.equals(WebKeys.OPERATION_MOBILE_FIND_PEOPLE)) {
					entries = getProfileModule().getUsers(options);
					model.put(WebKeys.USERS, entries.get(ObjectKeys.SEARCH_ENTRIES));
				} else if (op.equals(WebKeys.OPERATION_MOBILE_FIND_PLACES)) {
					entries = getBinderModule().executeSearchQuery( searchTermFilter.getFilter(), options);
					model.put(WebKeys.ENTRIES, entries.get(ObjectKeys.SEARCH_ENTRIES));
					view = "mobile/find_places";
				}
				model.put(WebKeys.SEARCH_TOTAL_HITS, entries.get(ObjectKeys.SEARCH_COUNT_TOTAL));
				model.put(WebKeys.PAGE_SIZE, maxEntries);
				model.put(WebKeys.PAGE_NUMBER, pageNumber);

		      	Integer searchCountTotal = (Integer) entries.get(ObjectKeys.SEARCH_COUNT_TOTAL);
		      	if (searchCountTotal == null) searchCountTotal = 0;
		      	int pageSize = Integer.valueOf(WebKeys.MOBILE_PAGE_SIZE).intValue();
		      	int pageStart = Integer.parseInt(pageNumber) * pageSize;
		      	int pageEnd = pageStart + pageSize;
		      	String nextPage = "";
		      	String prevPage = "";
		      	if (searchCountTotal.intValue() < pageStart) {
		      		if (Integer.parseInt(pageNumber) > 0) prevPage = String.valueOf(Integer.parseInt(pageNumber) - 1);
		      	} else if (searchCountTotal.intValue() >= pageEnd) {
		      		nextPage = String.valueOf(Integer.parseInt(pageNumber) + 1);
		      		if (Integer.parseInt(pageNumber) > 0) prevPage = String.valueOf(Integer.parseInt(pageNumber) - 1);
		      	} else {
		      		if (Integer.parseInt(pageNumber) > 0) prevPage = String.valueOf(Integer.parseInt(pageNumber) - 1);
		      	}
				model.put(WebKeys.NEXT_PAGE, nextPage);
				model.put(WebKeys.PREV_PAGE, prevPage);

				return new ModelAndView(view, model);
				
			}
		}

		//Setup the actions menu list
		List actions = new ArrayList();
		//BinderHelper.addActionsHome(request, actions);
		BinderHelper.addActionsRecentPlaces(request, actions, binderId);
		BinderHelper.addActionsSpacer(request, actions);
		BinderHelper.addActionsLogout(request, actions);
		model.put("ss_actions", actions);
		
		return new ModelAndView("mobile/find_people", model);
	}
	
}
