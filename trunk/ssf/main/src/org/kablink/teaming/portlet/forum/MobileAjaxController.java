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
package org.kablink.teaming.portlet.forum;

import static org.kablink.util.search.Restrictions.in;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.regex.Pattern;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.TextVerificationException;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.calendar.EventsViewHelper;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.HomePageConfig;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.portletadapter.portlet.HttpServletRequestReachable;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.search.filter.SearchFilterRequestParser;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.ssfs.util.SsfsUtil;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.feed.TeamingFeedCache;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractControllerRetry;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.DashboardHelper;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.Favorites;
import org.kablink.teaming.web.util.ListFolderHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.Tabs;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.Http;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Order;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.web.portlet.ModelAndView;

/**
 * ?
 * 
 * @author Peter Hurley
 */
@SuppressWarnings({"deprecation", "unchecked", "unused"})
public class MobileAjaxController  extends SAbstractControllerRetry {
	static Pattern replacePtrn = Pattern.compile("([\\p{Punct}&&[^\\*]])");	
	
	//caller will retry on OptimisiticLockExceptions
	@Override
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
			} else if (op.equals(WebKeys.OPERATION_MOBILE_DELETE_ENTRY)) {
				ajaxMobileDoDeleteEntry(request, response);
			} else if (op.equals(WebKeys.OPERATION_MOBILE_ADD_USER_GROUP_TEAM) ||
					op.equals(WebKeys.OPERATION_MOBILE_FIND_USER_GROUP_TEAM)) {
				ajaxMobileDoAddUserGroupTeam(request, response);
			} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_FRONT_PAGE)) {
				ajaxMobileDoFrontPage(this, request, response);
			} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_FULL_UI)) {
				ajaxMobileDoFullUi(this, request, response);
			} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_MOBILE_UI)) {
				ajaxMobileDoMobileUi(this, request, response);
			} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_ENTRY)) {
				ajaxMobileDoShowEntry(this, request, response);
			} else if (op.equals(WebKeys.OPERATION_VIEW_TEAMING_LIVE)) {
				ajaxDoTeamingLive(this, request, response);
			} else if (op.equals(WebKeys.OPERATION_MOBILE_TRACK_THIS)) {
				ajaxMobileDoTrackThis(this, request, response);
			}
		}
	}
	
	@Override
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
			} else if (op.equals(WebKeys.OPERATION_TEAMING_LIVE_CHECK_FOR_ACTIVITY)) {
				return ajaxMobileLoginCheckForActivity(this, request, response, "ss_forum", WebKeys.OPERATION_VIEW_TEAMING_LIVE);
			} else if (op.equals(WebKeys.OPERATION_MOBILE_APP_LOGIN)) {
				return ajaxMobileAppLogin(this, request, response);
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
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_DELETE_ENTRY)) {
			return ajaxMobileDeleteEntry(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_ADD_USER_GROUP_TEAM)) {
			return ajaxMobileAddUserGroupTeam(this, request, response, null);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_FIND_USER_GROUP_TEAM)) {
			return ajaxMobileFindUserGroupTeam(this, request, response);
			
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
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_APP_LOGIN)) {
			return ajaxMobileAppLogin(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_VIEW_TEAMING_LIVE)) {
			return ajaxShowTeamingLive(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_VIEW_TEAMING_LIVE_UPDATE)) {
			return ajaxShowTeamingLiveUpdate(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_TEAMING_LIVE_CHECK_FOR_ACTIVITY)) {
			return ajaxTeamingLiveCheckForActivity(this, request, response);
		} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_FOLLOWING)) {
			return ajaxMobileShowFollowing(this, request, response);
		}
		
		if (!WebHelper.isUserLoggedIn(request) || ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			return ajaxMobileLogin(this, request, response, "ss_mobile", WebKeys.OPERATION_MOBILE_SHOW_FRONT_PAGE);
		} else {
			//There is no operation specified. See if this should go to the default home page
			Long zoneId = getZoneModule().getZoneIdByVirtualHost(ZoneContextHolder.getServerName());
			HomePageConfig homePageConfig = getZoneModule().getZoneConfig(zoneId).getHomePageConfig();
			if (homePageConfig != null) {
				Long binderId = homePageConfig.getDefaultHomePageId();
				if (WebHelper.isGuestLoggedIn(request) && homePageConfig != null) {
					binderId = homePageConfig.getDefaultGuestHomePageId();
					if (binderId == null) binderId = homePageConfig.getDefaultHomePageId();
				}
				if (binderId != null) {
					return ajaxMobileShowWorkspace(this, request, response, binderId, null);
				} else {
					return ajaxMobileFrontPage(this, request, response);
				}
			} else {
				return ajaxMobileFrontPage(this, request, response);
			}
		}
	} 

	private void ajaxMobileDoAddEntry(ActionRequest request, ActionResponse response) 
	throws Exception {
		//Add an entry
        User user = RequestContextHolder.getRequestContext().getUser();
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		//See if the add entry form was submitted
		Long entryId=null;
		if ((formData.containsKey("okBtn") || formData.containsKey("addUGTBtn")) && WebHelper.isMethodPost(request)) {
			//The form was submitted. Go process it
			String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
			Map options = new HashMap();
			String delayWorkflow = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_DELAY_WORKFLOW, "");
			String entryOperationType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_OPERATION_TYPE, "");
			if (formData.containsKey("addUGTBtn")) {
				//This request is about to add a user or group. Delay starting the workflow if any
				options.put(ObjectKeys.INPUT_OPTION_DELAY_WORKFLOW, "true");
				response.setRenderParameter(WebKeys.URL_ENTRY_DELAY_WORKFLOW, "true");
			}
			Map fileMap = new HashMap();
			MapInputData inputData = new MapInputData(formData);
			entryId = getFolderModule().addEntry(folderId, entryType, inputData, fileMap, options).getId();
			response.setRenderParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
			
			//If we just added a MiniBlog entry, update the user's status
			BinderHelper.updateUserStatus(folderId, entryId, user);
			
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
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		String entryOperationType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_OPERATION_TYPE, "");
		String delayWorkflow = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_DELAY_WORKFLOW, "");
		//See if the modify entry form was submitted
		if ((formData.containsKey("okBtn") || formData.containsKey("addUGTBtn")) && WebHelper.isMethodPost(request)) {
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
			FolderEntry entry = getFolderModule().getEntry(null, entryId);
			Long folderId = entry.getParentFolder().getId();
			Map options = new HashMap();
			if (delayWorkflow.equals("true") && formData.containsKey("okBtn")) {
				//If delaying workflow starts and we are not going to add a new user, group or team, then start the delayed workflow
				options.put(ObjectKeys.INPUT_OPTION_DO_WORKFLOW, "true");
			}
			getFolderModule().modifyEntry(folderId, entryId, 
					new MapInputData(formData), fileMap, deleteAtts, null, options);
			
			//See if the user wants to subscribe to this entry
			BinderHelper.subscribeToThisEntry(this, request, folderId, entryId);
		} else {
			String sUrl = PortletRequestUtils.getStringParameter(request, WebKeys.URL_MOBILE_URL, "");
			if (!sUrl.equals("")) response.sendRedirect(sUrl);
		}
	}

	private void ajaxMobileDoDeleteEntry(ActionRequest request, ActionResponse response) 
			throws Exception {
		//Delete an entry
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		//See if the delete entry form was submitted
		if (formData.containsKey("okBtn") && WebHelper.isMethodPost(request)) {
			//The form was submitted. Go process it
			getFolderModule().deleteEntry(folderId, entryId);
		} else {
			String sUrl = PortletRequestUtils.getStringParameter(request, WebKeys.URL_MOBILE_URL, "");
			if (!sUrl.equals("")) response.sendRedirect(sUrl);
		}
	}

	private void ajaxMobileDoAddUserGroupTeam(ActionRequest request, ActionResponse response) 
	throws Exception {
		//Add a user, group, or team to an element in an entry
		Map formData = request.getParameterMap();
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		Long id = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ID);				
		String type = new String(PortletRequestUtils.getStringParameter(request, WebKeys.URL_TYPE, "user"));		
		String entryOperationType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_OPERATION_TYPE, "");
		String delayWorkflow = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_DELAY_WORKFLOW, "");
		String elementName = new String(PortletRequestUtils.getStringParameter(request, WebKeys.URL_ELEMENT, ""));		
		FolderEntry entry = getFolderModule().getEntry(null, entryId);
		//See if the modify entry form was submitted
		if (formData.containsKey("addUserGroupTeamBtn") && WebHelper.isMethodPost(request)) {
			//The form was submitted. Go process it
			Map data = new HashMap();
			CustomAttribute ca = entry.getCustomAttribute(elementName);
			Set values = new HashSet();
			if (ca != null) {
				values = ca.getValueSet();
			}
			if (id != null) values.add(id);
			String ids = "";
			for (Object thisId : values) {
				if (!ids.equals("")) ids += " ";
				ids += String.valueOf(thisId);
			}
			data.put(elementName, ids);
			getFolderModule().modifyEntry(entry.getParentBinder().getId(), entryId, 
					new MapInputData(data), null, null, null, null);
			
		} else if (formData.containsKey("cancelBtn")) {
			AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_mobile", false);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
			adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_MODIFY_ENTRY);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, String.valueOf(entry.getParentBinder().getId()));
			adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, String.valueOf(entryId));
			adapterUrl.setParameter(WebKeys.URL_ENTRY_DELAY_WORKFLOW, delayWorkflow);
			adapterUrl.setParameter(WebKeys.URL_ENTRY_OPERATION_TYPE, entryOperationType);
			response.sendRedirect(adapterUrl.toString());
			
		} else {
			String sUrl = PortletRequestUtils.getStringParameter(request, WebKeys.URL_MOBILE_URL, "");
			if (!sUrl.equals("")) response.sendRedirect(sUrl);
		}
	}

	private void ajaxMobileDoAddReply(ActionRequest request, ActionResponse response) 
			throws Exception {
		//Add a reply
		Map formData = request.getParameterMap();
		//See if the add entry form was submitted
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		if (formData.containsKey("okBtn") && WebHelper.isMethodPost(request)) {
			//The form was submitted. Go process it
			String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
			Map fileMap = new HashMap();
			MapInputData inputData = new MapInputData(formData);
			FolderEntry entry = getFolderModule().getEntry(null, entryId);
			Long replyId = getFolderModule().addReply(entry.getParentFolder().getId(), entryId, entryType, inputData, fileMap, null).getId();

			//See if the user wants to subscribe to this entry
			BinderHelper.subscribeToThisEntry(this, request, entry.getParentFolder().getId(), replyId);
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
			String text = PortletRequestUtils.getStringParameter(request, "miniblogText", "", false);
			BinderHelper.addMiniBlogEntry(bs, text);
		} else if (formData.containsKey("acceptBtn") && WebHelper.isMethodPost(request)) {
			//User clicked "I Accept"
			getProfileModule().setUserProperty( null, "acceptedMobileDisclaimer", true );
		}
		//See if the user clicked on a What's New option
		String type = PortletRequestUtils.getStringParameter(request, "whats_new", "");
		if (!type.equals("")) {
			UserProperties userProperties = bs.getProfileModule().getUserProperties(user.getId());
			String savedType = (String)userProperties.getProperty(ObjectKeys.USER_PROPERTY_MOBILE_WHATS_NEW_TYPE);
			if (savedType == null) savedType = "";
			if (!type.equals("") && !type.equals(savedType)) {
				//Remember the last type of results
				bs.getProfileModule().setUserProperty(user.getId(), ObjectKeys.USER_PROPERTY_MOBILE_WHATS_NEW_TYPE, type);
			}
		}
	}

	private void ajaxMobileDoFullUi(AllModulesInjected bs, ActionRequest request, ActionResponse response) 
			throws Exception {
		//Go to the full ui
		HttpServletRequest req = WebHelper.getHttpServletRequest(request);
		HttpSession session = WebHelper.getRequiredSession(req);
		session.setAttribute(WebKeys.MOBILE_FULL_UI, Boolean.TRUE);
		response.sendRedirect(WebUrlUtil.getSimpleURLContextRootURL(request).toString());
	}

	private void ajaxMobileDoMobileUi(AllModulesInjected bs, ActionRequest request, ActionResponse response) 
			throws Exception {
		//Go to the mobile ui
		HttpServletRequest req = WebHelper.getHttpServletRequest(request);
		HttpSession session = WebHelper.getRequiredSession(req);
		session.setAttribute(WebKeys.MOBILE_FULL_UI, Boolean.FALSE);
		response.sendRedirect(WebUrlUtil.getSimpleURLContextRootURL(request).toString());
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
		Long folderId =PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);				
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
				//Check if this user is allowed to do this manual transition
				if (getFolderModule().checkIfManualTransitionAllowed(folderId, replyId, tokenId, toState)) {
					getFolderModule().modifyWorkflowState(folderId, replyId, tokenId, toState);
				}
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
						(binder.getDefinitionType().equals(Definition.USER_WORKSPACE_VIEW) ||
						 binder.getDefinitionType().equals(Definition.EXTERNAL_USER_WORKSPACE_VIEW))) 
					//Also stop tracking the user if this is a user workspace
					BinderHelper.trackThisBinder(bs, binder.getOwnerId(), "deletePerson");
			}
		} catch(Exception e) {}
	}

	private ModelAndView ajaxMobileLogin(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, String portletName, String operation) throws Exception {
		Map model = new HashMap();
		String operation2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
        HttpSession session = ((HttpServletRequestReachable) request).getHttpServletRequest().getSession();
    	AuthenticationException ex = (AuthenticationException) session.getAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY);
    	if(ex != null) {
    		model.put(WebKeys.LOGIN_ERROR, ex.getMessage());
    		session.removeAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY);

			if ( ex instanceof TextVerificationException )
    		{
				// Either the user entered an invalid captcha response or we have detected
				// a brute-force authentication attack.  Either way require captcha on the login dialog.
				model.put( "ssDoTextVerification", "true" );
    		}
    	}
		String refererUrl = PortletRequestUtils.getStringParameter(request, WebKeys.URL_REFERER_URL);
		if (Validator.isNotNull(refererUrl)) {
			model.put(WebKeys.URL, refererUrl);
		} else {
			BinderHelper.setupStandardBeans(bs, request, response, model, null, portletName);
			BinderHelper.setupMobileSearchBeans(bs, request, response, model);
			refererUrl = Http.getCompleteURL(((HttpServletRequestReachable) request).getHttpServletRequest());
			refererUrl = StringCheckUtil.checkForQuotes(refererUrl, false);		//Prevent XSS attacks
			if (Validator.isNotNull(refererUrl) && !refererUrl.contains("operation="+WebKeys.OPERATION_MOBILE_LOGIN)) {
				model.put(WebKeys.URL, refererUrl);
			} else {
				AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, portletName, true);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
				adapterUrl.setParameter(WebKeys.URL_OPERATION, operation);
				adapterUrl.setParameter(WebKeys.URL_OPERATION2, operation2);
				model.put(WebKeys.URL, adapterUrl);
			}
		}
			
		String view = "mobile/show_login_form";
		if(MiscUtil.isNativeMobileApp(request)) {
			view = "mobile/app_login";
		}
		
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for(Cookie cookie:cookies) {
				if(cookie.getName().equals(WebKeys.URL_NATIVE_MOBILE_APP_COOKIE)) {
					String value = cookie.getValue();
					model.put(WebKeys.URL_OPERATION2, value);
					view = "mobile/app_login";
					break;
				}
			}
		}
		
		return new ModelAndView(view, model);
	}
	
	private ModelAndView ajaxMobileLoginCheckForActivity(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, String portletName, String operation) throws Exception {
        HttpSession session = ((HttpServletRequestReachable) request).getHttpServletRequest().getSession();
		Map model = new HashMap();
    	AuthenticationException ex = (AuthenticationException) session.getAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY);
    	if(ex != null) {
    		model.put(WebKeys.LOGIN_ERROR, ex.getMessage());
    		session.removeAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY);
    	}
		model.put("ss_teamingLiveStatus", "reload");
        session.setAttribute(WebKeys.TEAMING_LIVE_UPDATE_DATE, new Date());
		model.put(WebKeys.TEAMING_LIVE_UPDATE_DATE, session.getAttribute(WebKeys.TEAMING_LIVE_UPDATE_DATE));

		response.setContentType("text/xml");
		return new ModelAndView("mobile/teaming_live_check_for_activity", model);
	}
	
	
	private ModelAndView ajaxMobileFrontPage(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String op2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
		model.put(WebKeys.URL_OPERATION2, op2);
		SeenMap seen = getProfileModule().getUserSeenMap(null);
		model.put(WebKeys.SEEN_MAP, seen);
		String view = BinderHelper.setupMobileFrontPageBeans(bs, request, response, model, "mobile/show_front_page");

		return new ModelAndView(view, model);
	}

	private ModelAndView ajaxMobileAppLogin(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser(); 
		String op2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
		Map model = new HashMap();
		model.put(WebKeys.URL_OPERATION2, op2);
		model.put(WebKeys.MOBILE_URL, SsfsUtil.getMobileUrl(request));
		model.put(WebKeys.USER_PRINCIPAL, user);
		if (user.isShared()) {
			model.put(WebKeys.MOBILE_IS_LOGGED_IN, Boolean.FALSE);
		} else {
			model.put(WebKeys.MOBILE_IS_LOGGED_IN, Boolean.TRUE);
		}
		AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_mobile", false);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
		adapterUrl.setParameter(WebKeys.URL_OPERATION, "mobile_show_front_page");
		adapterUrl.setParameter(WebKeys.URL_OPERATION2, "whatsnew");
		model.put(WebKeys.MOBILE_WHATSNEW_URL, adapterUrl);

		HttpSession session = ((HttpServletRequestReachable) request).getHttpServletRequest().getSession();
		BinderHelper.setupMobileCookie(session, request, response, model);
		
		String view = "mobile/app_login";
		return new ModelAndView(view, model);
	}

	private ModelAndView ajaxShowTeamingLive(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		SeenMap seen = getProfileModule().getUserSeenMap(null);
		model.put(WebKeys.SEEN_MAP, seen);
        HttpSession session = ((HttpServletRequestReachable) request).getHttpServletRequest().getSession();
		Date lastUpdate = (Date)session.getAttribute(WebKeys.TEAMING_LIVE_UPDATE_DATE);
		if (lastUpdate == null) {
			lastUpdate = new Date();
			lastUpdate.setTime(lastUpdate.getTime() - TeamingFeedCache.feedClientUpdateInterval*60*1000 - 1);
		}
		model.put(WebKeys.TEAMING_LIVE_PREVIOUS_UPDATE_DATE, lastUpdate);
		String view = "mobile/show_teaming_live";
		try {
			view = BinderHelper.setupTeamingLiveBeans(bs, request, response, model, "mobile/show_teaming_live");
		} catch(AccessControlException e) {
			view = "mobile/show_teaming_live_not_available";
		}

		//Reset the date used to see what is new
        Date updateDate = new Date();
        session.setAttribute(WebKeys.TEAMING_LIVE_UPDATE_DATE, updateDate);
        model.put(WebKeys.TEAMING_LIVE_UPDATE_DATE, updateDate);
        
		return new ModelAndView(view, model);
	}

	private ModelAndView ajaxShowTeamingLiveUpdate(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
        HttpSession session = ((HttpServletRequestReachable) request).getHttpServletRequest().getSession();
		Map model = new HashMap();
		Date lastUpdate = (Date)session.getAttribute(WebKeys.TEAMING_LIVE_UPDATE_DATE);
		if (lastUpdate == null) {
			lastUpdate = new Date();
			lastUpdate.setTime(lastUpdate.getTime() - TeamingFeedCache.feedClientUpdateInterval*60*1000 - 1);
		}
		model.put(WebKeys.TEAMING_LIVE_PREVIOUS_UPDATE_DATE, lastUpdate);
		String view = BinderHelper.setupTeamingLiveBeans(bs, request, response, model, "mobile/teaming_live_update");
        session.setAttribute(WebKeys.TEAMING_LIVE_UPDATE_DATE, new Date());
		model.put(WebKeys.TEAMING_LIVE_UPDATE_DATE, session.getAttribute(WebKeys.TEAMING_LIVE_UPDATE_DATE));
		return new ModelAndView(view, model);
	}

	private ModelAndView ajaxTeamingLiveCheckForActivity(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
        HttpSession session = ((HttpServletRequestReachable) request).getHttpServletRequest().getSession();
		Map model = new HashMap();
		String view = "mobile/teaming_live_check_for_activity";

        Date lastUpdateDate = (Date) session.getAttribute(WebKeys.TEAMING_LIVE_UPDATE_DATE);
        if (lastUpdateDate == null) {
        	lastUpdateDate = new Date();
        	lastUpdateDate.setTime(lastUpdateDate.getTime() - TeamingFeedCache.feedClientUpdateInterval - 1);
        }

		//Check if the client needs to do an update. 
		//An empty status indicates no update needed
		TeamingFeedCache.updateMap(bs);
		String status = "";
		Set<Long> trackedBinderIds = (Set<Long>)session.getAttribute(ObjectKeys.SESSION_TEAMING_LIVE_TRACKED_BINDER_IDS);
		if (trackedBinderIds == null) trackedBinderIds = new HashSet<Long>();
		if (TeamingFeedCache.checkBindersForNewEntries(bs, trackedBinderIds, lastUpdateDate)) {
			status = "update";
		}
		model.put("ss_teamingLiveStatus", status);

		response.setContentType("text/xml");
		return new ModelAndView(view, model);
	}

	private ModelAndView ajaxMobileSearchResults(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser(); 
		Map model = new HashMap();
		SeenMap seen = getProfileModule().getUserSeenMap(null);
		model.put(WebKeys.SEEN_MAP, seen);
		String queryName = PortletRequestUtils.getStringParameter(request, WebKeys.URL_SEARCH_QUERY_NAME, "");
		Integer tabId = PortletRequestUtils.getIntParameter(request, WebKeys.URL_TAB_ID, -1);
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
		BinderHelper.setupMobileSearchBeans(bs, request, response, model);

		String pageNumber = PortletRequestUtils.getStringParameter(request, "pageNumber", "1");
      	int pageSize = Integer.valueOf(WebKeys.MOBILE_PAGE_SIZE).intValue();
      	int pageStart = (Integer.parseInt(pageNumber) -1) * pageSize;
      	int pageEnd = pageStart + pageSize;
		Map formData = request.getParameterMap();
	    Tabs tabs = Tabs.getTabs(request);
		model.put(WebKeys.TABS, tabs);		
		String searchText = PortletRequestUtils.getStringParameter(request, WebKeys.SEARCH_TEXT_FIELD, "", false);
		model.put(WebKeys.SEARCH_TEXT, searchText);
	    if (formData.containsKey("searchBtn") || formData.containsKey("quickSearch")) {
	    	SearchFilterRequestParser requestParser = new SearchFilterRequestParser(request, getDefinitionModule());
			Document searchQuery = requestParser.getSearchQuery();
			Map options = BinderHelper.prepareSearchOptions(bs, request);
			options.put(ObjectKeys.SEARCH_OFFSET, new Integer(pageStart));
			options.put(ObjectKeys.SEARCH_USER_MAX_HITS, new Integer(pageSize));
			if (scope.equals("local")) options.put(ObjectKeys.SEARCH_ANCESTRY, binderId.toString());;
			Map results =  bs.getBinderModule().executeSearchQuery(searchQuery, Constants.SEARCH_MODE_NORMAL, options);
			
			//Set the title of the tab
			DateFormat fmt = DateFormat.getTimeInstance(DateFormat.SHORT, user.getLocale());
			fmt.setTimeZone(user.getTimeZone());
			options.put(Tabs.TITLE, NLT.get("searchForm.button.label") + " " + fmt.format(new Date()));
			Tabs.TabEntry tab = tabs.addTab(searchQuery, options);
			
			BinderHelper.prepareSearchResultPage(bs, results, model, searchQuery, options, tab);
	    } else if (!queryName.equals("")){
			
			// get query and options from tab		
			Document searchQuery = BinderHelper.getSavedQuery(bs, queryName, bs.getProfileModule().getUserProperties(user.getId()));
			if (searchQuery == null) {
				return ajaxMobileFrontPage(bs, request, response);
			}
			
			// get page no and actualize options
			// execute query
			// actualize tabs info
			Map options = BinderHelper.prepareSearchOptions(bs, request);
			options.put(ObjectKeys.SEARCH_OFFSET, new Integer(pageStart));
			options.put(ObjectKeys.SEARCH_USER_MAX_HITS, new Integer(pageSize));

			options.put(Tabs.TITLE, queryName);
			Map results =  bs.getBinderModule().executeSearchQuery(searchQuery, Constants.SEARCH_MODE_NORMAL, options);
			
			Tabs.TabEntry tab = tabs.addTab(searchQuery, options);
			
			BinderHelper.prepareSearchResultPage(bs, results, model, searchQuery, options, tab);
	    } else if (tabId != -1) {
	    	model.putAll(BinderHelper.prepareSearchResultPage(this, request, tabs));
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
		BinderHelper.addActionsLogout(request, actions);
		model.put("ss_actions", actions);
		
		return new ModelAndView("mobile/show_search_results", model);
	}

	private ModelAndView ajaxMobileShowFolder(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser(); 
		Map model = new HashMap();
		//Setup the actions menu list
		List actions = new ArrayList();
		List new_actions = new ArrayList();
		
		SeenMap seen = getProfileModule().getUserSeenMap(null);
		model.put(WebKeys.SEEN_MAP, seen);
		
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		Binder binder = null;
		try {
			binder = getBinderModule().getBinder(binderId);
		} catch(NoBinderByTheIdException e) {
			BinderHelper.addActionsRecentPlaces(request, actions, null);
			BinderHelper.addActionsLogout(request, actions);
			model.put("ss_actions", actions);
			return new ModelAndView("mobile/binder_does_not_exist", model);
		} catch(Exception e) {
			BinderHelper.addActionsRecentPlaces(request, actions, null);
			BinderHelper.addActionsLogout(request, actions);
			model.put("ss_actions", actions);
			return new ModelAndView("mobile/access_denied", model);
		}
		BinderHelper.setupStandardBeans(bs, request, response, model, binderId, "ss_mobile");
		BinderHelper.setupMobileSearchBeans(bs, request, response, model);
		UserProperties userProperties = (UserProperties)model.get(WebKeys.USER_PROPERTIES_OBJ);
		UserProperties userFolderProperties = (UserProperties)model.get(WebKeys.USER_FOLDER_PROPERTIES_OBJ);

		//If this is a calendar, look for a new event type setting
		String eventType = PortletRequestUtils.getStringParameter(request, "eventType", "");
		if (!"".equals(eventType)) {
			EventsViewHelper.setCalendarDisplayEventType(this, user.getId(), binderId, eventType);
		}
		eventType = EventsViewHelper.getCalendarDisplayEventType(bs, user.getId(), binderId);
		model.put(WebKeys.CALENDAR_EVENT_TYPE, eventType);

		Map options = new HashMap();		
		
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

      	List entryList = new ArrayList();
      	if (family.equals(Constants.FAMILY_FIELD_CALENDAR)) {
      		entryList = ListFolderHelper.findCalendarEvents(bs, request, response, binder, model);
      		model.put(WebKeys.FOLDER_ENTRIES, entryList);
      	} else {
      		Map folderEntries = getFolderModule().getEntries(binderId, options);
	      	model.put(WebKeys.SEARCH_TOTAL_HITS, folderEntries.get(ObjectKeys.SEARCH_COUNT_TOTAL));
			if (folderEntries != null) {
				entryList = (List) folderEntries.get(ObjectKeys.SEARCH_ENTRIES);
			}
			model.put(WebKeys.FOLDER_ENTRIES, entryList);
      	}
  		Integer entryCount = entryList.size();
      	
		
      	if (pageNumber.intValue() > 0) prevPage = String.valueOf(pageNumber - 1);
      	if (entryCount == pageSize && entryCount > ((pageNumber.intValue() + 1) * pageSize)) {
      		nextPage = String.valueOf(pageNumber + 1);
      	}
		model.put(WebKeys.PAGE_NUMBER, pageNumber.toString());
		model.put(WebKeys.NEXT_PAGE, nextPage);
		model.put(WebKeys.PREV_PAGE, prevPage);

		model.put(WebKeys.PAGE_ENTRIES_PER_PAGE, (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS));

		if (binder instanceof Folder && getFolderModule().testAccess((Folder)binder, FolderOperation.addEntry)) {				
			List defaultEntryDefinitions = binder.getEntryDefinitions();
			List<Map> defTitleUrlList = new ArrayList();
			for (int i=0; i<defaultEntryDefinitions.size(); ++i) {
				Definition def = (Definition) defaultEntryDefinitions.get(i);
				if (validateMobileDef(def)) {
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
					String[] ta = new String[1];
					ta[0] = title;
					title = NLT.get("mobile.addSomething", ta);
					BinderHelper.addActionsGeneral(request, new_actions, title, adapterUrl.toString(), "");
				}
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
		Map binderMap = bs.getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, 0, ObjectKeys.SEARCH_MAX_HITS_SUB_BINDERS,
				org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.DOCID_FIELD));

		List binderMapList = (List)binderMap.get(ObjectKeys.SEARCH_ENTRIES); 
		List binderIdList = new ArrayList();

      	for (Iterator iter=binderMapList.iterator(); iter.hasNext();) {
      		Map entryMap = (Map) iter.next();
      		binderIdList.add(new Long((String)entryMap.get(Constants.DOCID_FIELD)));
      	}
      	//Get the sub-folder list including itermediate folders that may be inaccessible
      	SortedSet binderList = bs.getBinderModule().getBinders(binderIdList, Boolean.FALSE);
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

		Map<String, Map> cacheEntryDef = new HashMap();
    	List items = entryList;
    	if (items != null) {
	    	Iterator it = items.iterator();
	    	while (it.hasNext()) {
	    		Map entry = (Map)it.next();
	    		String entryDefId = (String)entry.get(Constants.COMMAND_DEFINITION_FIELD);
	    		if (cacheEntryDef.get(entryDefId) == null) {
	    			cacheEntryDef.put(entryDefId, bs.getDefinitionModule().getEntryDefinitionElements(entryDefId));
	    		}
	    		entry.put(WebKeys.ENTRY_DEFINTION_ELEMENT_DATA, cacheEntryDef.get(entryDefId));
	    	}
    	}

		//BinderHelper.addActionsHome(request, actions);
		BinderHelper.addActionsWhatsNew(request, actions, binder);
		BinderHelper.addActionsWhatsUnseen(request, actions, binder);
		BinderHelper.addActionsRecentPlaces(request, actions, binder.getId());
		BinderHelper.addActionsTrackThisBinder(bs, request, actions, binder);
		//BinderHelper.addActionsFullView(bs, request, actions, binder.getId(), null);
		BinderHelper.addActionsLogout(request, actions);
		BinderHelper.addActionsFullView(bs, request, actions, binder.getId(), null);
		model.put("ss_actions", actions);
		model.put("ss_new_actions", new_actions);
		
		return new ModelAndView("mobile/show_folder", model);
	}
	
	private boolean validateMobileDef(Definition def) {
		Document doc = def.getDefinition();
		Element root = doc.getRootElement();
		
		//See if this is a file or photo entry
		Element familyEle = (Element) root.selectSingleNode("./properties/property[@name='family']");
		if (familyEle != null) {
			String family = familyEle.attributeValue("value", "");
			if (family.equals("file") || family.equals("photo")) return false;
		}
		
		//See if the title field is coming from a file upload
		Element formEle = (Element) root.selectSingleNode("//item[@type='form']");
		Element titleItem = (Element) formEle.selectSingleNode(".//item[@name='title']");
		if (titleItem != null) {
			Element itemSourceEle = (Element) titleItem.selectSingleNode("./properties/property[@name='itemSource']");
			if (itemSourceEle != null) {
				String itemSource = itemSourceEle.attributeValue("value", "");
				Element titleSourceProperty = (Element) formEle.selectSingleNode(".//item/properties/property[@name='name' and @value='"+itemSource+"']");
				if (titleSourceProperty != null) {
					Element titleSourceItem = (Element) titleSourceProperty.getParent().getParent();
					String titleSourceItemName = titleSourceItem.attributeValue("name", "");
					if (titleSourceItemName.equals("file") || titleSourceItemName.equals("graphic")) {
						//This is a file or graphic entry. Skip it in the mobile UI
						return false;
					}
				}
			}
		}
		
		//See if there is a survey element on the form
		formEle = (Element) root.selectSingleNode("//item[@type='form']");
		Element surveyItem = (Element) formEle.selectSingleNode(".//item[@name='survey']");
		if (surveyItem != null) return false;
		
		return true;
	}

	private ModelAndView ajaxMobileWhatsNew(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser(); 
		Map model = new HashMap();

		SeenMap seen = getProfileModule().getUserSeenMap(null);
		model.put(WebKeys.SEEN_MAP, seen);

		String type = PortletRequestUtils.getStringParameter(request, WebKeys.TYPE, WebKeys.URL_WHATS_NEW);

		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		Binder top = getWorkspaceModule().getTopWorkspace();
		Binder binder = null;
		if (binderId == null || binderId.equals(top.getId())) {
			binder = top;
			binderId = binder.getId();
			model.put(WebKeys.MOBILE_WHATS_NEW_SITE, true);
			UserProperties userProperties = bs.getProfileModule().getUserProperties(user.getId());
			String savedType = (String)userProperties.getProperty(ObjectKeys.USER_PROPERTY_MOBILE_WHATS_NEW_TYPE);
			if (savedType == null || savedType.equals("")) savedType = ObjectKeys.MOBILE_WHATS_NEW_VIEW_SITE;
			type = PortletRequestUtils.getStringParameter(request, WebKeys.TYPE, savedType);
			if (!type.equals("") && !type.equals(savedType)) {
				//Remember the last type of results
				bs.getProfileModule().setUserProperty(user.getId(), ObjectKeys.USER_PROPERTY_MOBILE_WHATS_NEW_TYPE, type);
			}
		} else {
			binder = getBinderModule().getBinder(binderId);
		}
		
		BinderHelper.setupStandardBeans(bs, request, response, model, binderId, "ss_mobile");
		BinderHelper.setupMobileSearchBeans(bs, request, response, model);

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

      	if (type.equals(ObjectKeys.MOBILE_WHATS_NEW_VIEW_TRACKED) || 
				type.equals(ObjectKeys.MOBILE_WHATS_NEW_VIEW_FAVORITES) ||
				type.equals(ObjectKeys.MOBILE_WHATS_NEW_VIEW_TEAMS) ||
				type.equals(ObjectKeys.MOBILE_WHATS_NEW_VIEW_SITE)) {
      		BinderHelper.setupWhatsNewBinderBeans(bs, bs.getBinderModule().getBinder(user.getWorkspaceId()), binderId, model, 
					String.valueOf(pageNumber), Integer.valueOf(pageSize), type);
      	} else if (type.equals(WebKeys.URL_WHATS_NEW)) {
			BinderHelper.setupWhatsNewBinderBeans(bs, binder, model, String.valueOf(pageNumber), Integer.valueOf(pageSize), type);
		} else if (type.equals(WebKeys.URL_WHATS_NEW_TRACKED)|| type.equals(WebKeys.URL_WHATS_NEW_TEAMS)) {
			BinderHelper.setupWhatsNewBinderBeans(bs, binder, model, String.valueOf(pageNumber), Integer.valueOf(pageSize), type);
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
		BinderHelper.addActionsLogout(request, actions);
		model.put("ss_actions", actions);
		
		return new ModelAndView("mobile/show_whats_new", model);
	}

	private ModelAndView ajaxMobileShowWorkspace(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
		return ajaxMobileShowWorkspace(bs, request, response, binderId, entryId);
	}
	private ModelAndView ajaxMobileShowWorkspace(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Long binderId, Long entryId) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser(); 
		Map model = new HashMap();
		//Setup the actions menu list
		List actions = new ArrayList();
		
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
					BinderHelper.setupMobileSearchBeans(bs, request, response, model);

					model.put(WebKeys.DEFINITION_ENTRY, u);
					model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_MOBILE);
					if (DefinitionHelper.getDefinition(u.getEntryDefDoc(), model, "//item[@name='profileEntryStandardView']") == false) {
						DefinitionHelper.getDefaultEntryView(u, model);
					}
					
					//BinderHelper.addActionsHome(request, actions);
					BinderHelper.addActionsRecentPlaces(request, actions, binderId);
					BinderHelper.addActionsLogout(request, actions);
					BinderHelper.addActionsFullView(bs, request, actions, binderId, null);
					model.put("ss_actions", actions);
					
					return new ModelAndView("mobile/show_user", model);
				}
			}
		}
		BinderHelper.setupStandardBeans(bs, request, response, model, binderId, "ss_mobile");
		BinderHelper.setupMobileSearchBeans(bs, request, response, model);
		UserProperties userFolderProperties = (UserProperties)model.get(WebKeys.USER_FOLDER_PROPERTIES_OBJ);
		if (userFolderProperties == null) userFolderProperties = new UserProperties(user.getId(), binderId);
		Workspace binder;
		List wsList = new ArrayList();
		List workspaces = new ArrayList();
		List folders = new ArrayList();
		try {
			binder = getWorkspaceModule().getWorkspace(Long.valueOf(binderId));
		} catch(NoBinderByTheIdException e) {
			BinderHelper.addActionsRecentPlaces(request, actions, null);
			BinderHelper.addActionsLogout(request, actions);
			model.put("ss_actions", actions);
			return new ModelAndView("mobile/binder_does_not_exist", model);
		} catch(Exception e) {
			BinderHelper.addActionsRecentPlaces(request, actions, null);
			BinderHelper.addActionsLogout(request, actions);
			model.put("ss_actions", actions);
			return new ModelAndView("mobile/access_denied", model);
		}
		if (binder.isDeleted() || binder.isPreDeleted()) {
			BinderHelper.addActionsRecentPlaces(request, actions, null);
			BinderHelper.addActionsLogout(request, actions);
			model.put("ss_actions", actions);
			return new ModelAndView("mobile/binder_does_not_exist", model);
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
				(Definition.USER_WORKSPACE_VIEW == binder.getDefinitionType() ||
				 Definition.EXTERNAL_USER_WORKSPACE_VIEW == binder.getDefinitionType())) {
			Set wsUsers = new HashSet();
			Long userId = binder.getCreation().getPrincipal().getId();
			if (userId != null) wsUsers.add(userId);
			try {
				SortedSet wsUsers2 = getProfileModule().getUsers(wsUsers);
				if (wsUsers2.size() > 0) model.put(WebKeys.WORKSPACE_CREATOR, wsUsers2.first());
			} catch(Exception e) {}
		}
		if (binder.getId().equals(getProfileModule().getProfileBinderId())) {
			//This is the profiles binder.
			Map users = null;
			Map options = new HashMap();
			
			options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(pageSize*(pageNumber + 1) + 3));
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
	        wsList = workspaces;
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
	      	//Get sub-binder list including intermediate binders that may be inaccessible
	      	SortedSet binderList = getBinderModule().getBinders(binderIdList, Boolean.FALSE);
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
	      	wsList =workspaces;
		}

		model.put(WebKeys.WORKSPACES, wsList);
		model.put(WebKeys.PAGE_NUMBER, pageNumber.toString());
		model.put(WebKeys.NEXT_PAGE, nextPage);
		model.put(WebKeys.PREV_PAGE, prevPage);
		model.put(WebKeys.FOLDERS, folders);

		//BinderHelper.addActionsHome(request, actions);
		BinderHelper.addActionsWhatsNew(request, actions, binder);
		BinderHelper.addActionsWhatsUnseen(request, actions, binder);
		BinderHelper.addActionsRecentPlaces(request, actions, binder.getId());
		if (binder != null && !binder.getEntityType().equals(EntityIdentifier.EntityType.profiles))
			BinderHelper.addActionsTrackThisBinder(bs, request, actions, binder);
		//BinderHelper.addActionsFullView(bs, request, actions, binder.getId(), entryId);
		BinderHelper.addActionsLogout(request, actions);
		BinderHelper.addActionsFullView(bs, request, actions, binder.getId(), null);
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
		BinderHelper.setupMobileSearchBeans(bs, request, response, model);

		//Setup the actions menu list
		List actions = new ArrayList();
		//BinderHelper.addActionsHome(request, actions);
		BinderHelper.addActionsRecentPlaces(request, actions, binderId);
		//BinderHelper.addActionsFullView(bs, request, actions, binderId, entryId);
		BinderHelper.addActionsLogout(request, actions);
		BinderHelper.addActionsFullView(bs, request, actions, binderId, entryId);
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
		BinderHelper.setupMobileSearchBeans(bs, request, response, model);

		//Setup the actions menu list
		List actions = new ArrayList();
		//BinderHelper.addActionsHome(request, actions);
		BinderHelper.addActionsRecentPlaces(request, actions, binderId);
		//BinderHelper.addActionsFullView(bs, request, actions, binderId, entryId);
		BinderHelper.addActionsLogout(request, actions);
		BinderHelper.addActionsFullView(bs, request, actions, binderId, entryId);
		model.put("ss_actions", actions);
		
		return new ModelAndView("mobile/show_no_entry", model);
	}
	private ModelAndView ajaxMobileShowEntry(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		if (entryId != null && binderId == null) {
			FolderEntry entry = getFolderModule().getEntry(binderId, entryId);
			binderId = entry.getParentBinder().getId();
		}
		if (entryId == null && binderId != null) {
			String zoneUUID = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ZONE_UUID, "");
			String title = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TITLE, "", false);
			Set entries = getFolderModule().getFolderEntryByNormalizedTitle(binderId, title, zoneUUID);
			if (entries.size() == 1) {
				FolderEntry entry = (FolderEntry)entries.iterator().next();
				entryId = entry.getId();
			} else if (entries.size() > 1) {
				//There are multiple matches
				entryId = ((FolderEntry)entries.iterator().next()).getId();
			}
		}
		if (entryId == null) {
			//No entry exists by this title
			Map model = new HashMap();
			Binder binder = getBinderModule().getBinder(binderId);
			BinderHelper.setupStandardBeans(bs, request, response, model, binderId, "ss_mobile");
			model.put(WebKeys.BINDER, binder);
			model.put(WebKeys.TABS, Tabs.getTabs(request));
			BinderHelper.setupMobileSearchBeans(bs, request, response, model);
			return new ModelAndView("mobile/entry_does_not_exist", model);
		}
		return ajaxMobileShowEntry(bs, request, response, entryId);
	}
	private ModelAndView ajaxMobileShowEntry(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Long entryId) throws Exception {
		Map model = new HashMap();
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);		
		if (entryId != null && binderId == null) {
			FolderEntry entry = getFolderModule().getEntry(binderId, entryId);
			binderId = entry.getParentBinder().getId();
		}
		Binder binder = getBinderModule().getBinder(binderId);
		BinderHelper.setupStandardBeans(bs, request, response, model, binderId, "ss_mobile");
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.TABS, Tabs.getTabs(request));
		BinderHelper.setupMobileSearchBeans(bs, request, response, model);

		User user = RequestContextHolder.getRequestContext().getUser();
		//Setup the actions menu list
		List actions = new ArrayList();
		List new_actions = new ArrayList();	
		List modify_actions = new ArrayList();	
		List delete_actions = new ArrayList();	
		
		FolderEntry entry = null;
		Map folderEntries = null;
		folderEntries  = getFolderModule().getEntryTree(binderId, entryId);
		if (folderEntries != null) {
			entry = (FolderEntry)folderEntries.get(ObjectKeys.FOLDER_ENTRY);
			if (entry != null && (entry.isPreDeleted() || entry.isDeleted())) {
				BinderHelper.addActionsRecentPlaces(request, actions, binderId);
				BinderHelper.addActionsLogout(request, actions);
				model.put("ss_actions", actions);
				return new ModelAndView("mobile/entry_deleted", model);
			}
			model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_MOBILE);
			if (DefinitionHelper.getDefinition(entry.getEntryDefDoc(), model, "//item[@name='entryView']") == false) {
				DefinitionHelper.getDefaultEntryView(entry, model);
			}
			BinderHelper.setAccessControlForAttachmentList(this, model, entry, user);
			Map accessControlMap = (Map) model.get(WebKeys.ACCESS_CONTROL_MAP);
			HashMap entryAccessMap = BinderHelper.getEntryAccessMap(this, model, entry);
			model.put(WebKeys.ENTRY, entry);
			model.put(WebKeys.DEFINITION_ENTRY, entry);
			model.put(WebKeys.FOLDER_ENTRY_DESCENDANTS, folderEntries.get(ObjectKeys.FOLDER_ENTRY_DESCENDANTS));
			model.put(WebKeys.FOLDER_ENTRY_ANCESTORS, folderEntries.get(ObjectKeys.FOLDER_ENTRY_ANCESTORS));
			if (DefinitionHelper.getDefinition(entry.getEntryDefDoc(), model, "//item[@name='entryView']") == false) {
				DefinitionHelper.getDefaultEntryView(entry, model);
			}
			SeenMap seen = getProfileModule().getUserSeenMap(null);
			Map seenEntries = new HashMap();
			
			model.put(WebKeys.SEEN_MAP, seen);
			model.put(WebKeys.SEEN_ENTRIES, seenEntries);
			List replies = new ArrayList((List)model.get(WebKeys.FOLDER_ENTRY_DESCENDANTS));
			if (replies != null)  {
				accessControlMap.put(entry.getId(), entryAccessMap);
				for (int i=0; i<replies.size(); i++) {
					FolderEntry reply = (FolderEntry)replies.get(i);
					accessControlMap.put(reply.getId(), entryAccessMap);
					if (!seen.checkIfSeen(reply)) {  
						seenEntries.put(reply.getId(), false);
						getProfileModule().setSeen(null, reply);
					} else {
						seenEntries.put(reply.getId(), true);
					}
				}
			}
			if (!seen.checkIfSeen(entry)) {  
				seenEntries.put(entry.getId(), true);	//always show main entry as having been seen
				getProfileModule().setSeen(null, entry);
			} else {
				seenEntries.put(entry.getId(), true);
			}
			Document defDoc = entry.getEntryDefDoc(); //cannot be null here
			Element familyEle = (Element) defDoc.getRootElement().selectSingleNode("./properties/property[@name='family']");
			String replyText = NLT.get("toolbar.comment");
			if (familyEle != null && familyEle.attributeValue("value", "").equals("discussion")) {
				replyText = NLT.get("toolbar.reply");
				model.put(WebKeys.DEFINITION_FAMILY, familyEle.attributeValue("value", ""));
			}
			model.put(WebKeys.ADD_REPLY_TITLE, replyText);
			
			if (getFolderModule().testAccess(entry, FolderOperation.addReply)) {
				List replyStyles = DefinitionUtils.getPropertyValueList(defDoc.getRootElement(), "replyStyle");
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
						String[] ta = new String[1];
						ta[0] = title;
						title = NLT.get("mobile.addSomething", ta);
						BinderHelper.addActionsGeneral(request, new_actions, title, adapterUrl.toString(), "");
					}
				}
			}
			
			if (getFolderModule().testAccess(entry, FolderOperation.modifyEntry)) {
				AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_mobile", true);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
				adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
				adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entry.getId().toString());
				adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_MODIFY_ENTRY);
				model.put(WebKeys.MOBILE_ENTRY_MODIFY_URL, adapterUrl);
				BinderHelper.addActionsGeneral(request, modify_actions, NLT.get("mobile.modifyEntry"), adapterUrl.toString(), "");
			}
	
			if (getFolderModule().testAccess(entry, FolderOperation.deleteEntry)) {
				AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_mobile", true);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
				adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
				adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entry.getId().toString());
				adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_DELETE_ENTRY);
				model.put(WebKeys.MOBILE_ENTRY_MODIFY_URL, adapterUrl);
				BinderHelper.addActionsGeneral(request, delete_actions, NLT.get("mobile.deleteEntry"), adapterUrl.toString(), "");
			}
	
			List entries = new ArrayList();
			entries.add(entry);
			BinderHelper.buildWorkflowSupportBeans(this, entries, model);
		}
	
		//BinderHelper.addActionsHome(request, actions);
		BinderHelper.addActionsRecentPlaces(request, actions, binderId);
		//BinderHelper.addActionsFullView(bs, request, actions, binderId, entryId);
		BinderHelper.addActionsLogout(request, actions);
		BinderHelper.addActionsFullView(bs, request, actions, binderId, entryId);
		BinderHelper.setRepliesAccessControl(bs, model, entry);

		model.put("ss_actions", actions);
		model.put("ss_new_actions", new_actions);
		model.put("ss_modify_actions", modify_actions);
		model.put("ss_delete_actions", delete_actions);
		
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
		BinderHelper.setupMobileSearchBeans(bs, request, response, model);

		User user = RequestContextHolder.getRequestContext().getUser();
		
		Principal entry = getProfileModule().getEntry(entryId);
		model.put(WebKeys.ENTRY, entry);
		model.put(WebKeys.DEFINITION_ENTRY, entry);

		model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_MOBILE);
		if (DefinitionHelper.getDefinition(entry.getEntryDefDoc(), model, "//item[@name='profileEntryStandardView']") == false) {
			DefinitionHelper.getDefaultEntryView(entry, model);
		}
		
		//Setup the actions menu list
		List actions = new ArrayList();
		//BinderHelper.addActionsHome(request, actions);
		BinderHelper.addActionsRecentPlaces(request, actions, binder.getId());
		if (entry instanceof User && ((User)entry).getWorkspaceId() != null) {
			//BinderHelper.addActionsFullView(bs, request, actions, ((User)entry).getWorkspaceId(), null);
		}
		BinderHelper.addActionsLogout(request, actions);
		BinderHelper.addActionsFullView(bs, request, actions, binder.getId(), entryId);
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
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		BinderHelper.setupStandardBeans(bs, request, response, model, null, "ss_mobile");
		if (binderId != null) {
			try {
				Binder binder = getBinderModule().getBinder(binderId);
				model.put(WebKeys.BINDER, binder);
			} catch(Exception e) {}
		}
		BinderHelper.setupMobileSearchBeans(bs, request, response, model);
	    Tabs tabs = Tabs.getTabs(request);
		model.put(WebKeys.TABS, tabs);		

		//Setup the actions menu list
		List actions = new ArrayList();
		//BinderHelper.addActionsHome(request, actions);
		BinderHelper.addActionsRecentPlaces(request, actions, binderId);
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
		BinderHelper.setupMobileSearchBeans(bs, request, response, model);

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
		BinderHelper.setupMobileSearchBeans(bs, request, response, model);

		List<Long> teamIds = new ArrayList<Long>();
		Collection myTeams = bs.getBinderModule().getTeamMemberships(user.getId(), null);
		model.put(WebKeys.MOBILE_TEAMS_LIST, myTeams);
		
		//Setup the actions menu list
		List actions = new ArrayList();
		//BinderHelper.addActionsHome(request, actions);
		BinderHelper.addActionsRecentPlaces(request, actions, binderId);
		BinderHelper.addActionsLogout(request, actions);
		model.put("ss_actions", actions);
		
		return new ModelAndView("mobile/show_teams", model);
	}
	
	private ModelAndView ajaxMobileShowFollowing(AllModulesInjected bs, RenderRequest request, 
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
		BinderHelper.setupMobileSearchBeans(bs, request, response, model);

		List<String> trackedPeople = new ArrayList<String>();
		Long userWsId = user.getWorkspaceId();
		if (userWsId != null) {
			Binder userWs = bs.getBinderModule().getBinder(userWsId);
			trackedPeople = SearchUtils.getTrackedPeopleIds(bs, userWs);
		}
		
		List<Long> trackedPeopleIds = new ArrayList<Long>();
		for (String strId : trackedPeople) { trackedPeopleIds.add(Long.parseLong(strId));	}
		Set<User> trackedUsers = getProfileModule().getUsers(trackedPeopleIds);
		
		List<Map<String, String>> trackedPeopleMaps = new ArrayList<Map<String, String>>();
		for (User tracked : trackedUsers) {
			Map<String, String> properties = new HashMap<String, String>();
			properties.put("id", tracked.getId().toString());
			properties.put("name", tracked.getTitle());
			trackedPeopleMaps.add(properties);
		}
		model.put(WebKeys.MOBILE_TRACKED_PEOPLE, trackedPeopleMaps);
		
		//Setup the actions menu list
		List actions = new ArrayList();
		//BinderHelper.addActionsHome(request, actions);
		BinderHelper.addActionsRecentPlaces(request, actions, binderId);
		BinderHelper.addActionsLogout(request, actions);
		model.put("ss_actions", actions);
		
		return new ModelAndView("mobile/show_following", model);
	}

	private ModelAndView ajaxMobileAddEntry(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));		
		Folder folder = getFolderModule().getFolder(folderId);
		//Adding an entry; get the specific definition
		Map folderEntryDefs = DefinitionHelper.getEntryDefsAsMap(folder);
		String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
		String delayWorkflow = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_DELAY_WORKFLOW, "");
    	request.setAttribute(WebKeys.URL_ENTRY_TYPE, entryType);
		model.put(WebKeys.FOLDER, folder);
		model.put(WebKeys.BINDER, folder);
		model.put(WebKeys.ENTRY_DEFINITION_MAP, folderEntryDefs);
		model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_MOBILE);
		model.put(WebKeys.DEFINITION_ID, entryType);
		model.put(WebKeys.OPERATION_TYPE, WebKeys.OPERATION_MOBILE_ADD_ENTRY);
		//Make sure the requested definition is legal
		if (folderEntryDefs.containsKey(entryType)) {
			DefinitionHelper.getDefinition((Definition)folderEntryDefs.get(entryType), model, "//item[@type='form']");
		} else {
			DefinitionHelper.getDefinition((Document) null, model, "//item[@name='entryForm']");
		}
		Map formData = request.getParameterMap();
		if (entryType.equals("") || formData.containsKey("okBtn") || formData.containsKey("addUGTBtn") || formData.containsKey("cancelBtn")) {
			if (formData.containsKey("addUGTBtn")) {
				//This is a request to get a user, group or team name
				model.put(WebKeys.ENTRY_DELAY_WORKFLOW, delayWorkflow);
				return ajaxMobileAddUserGroupTeam(bs, request, response, model);
			} else {
				return ajaxMobileShowFolder(bs, request, response);
			}
		} else {
			return new ModelAndView("mobile/add_entry", model);
		}
	}	

	private ModelAndView ajaxMobileModifyEntry(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));		
		String entryOperationType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_OPERATION_TYPE, "");
		String delayWorkflow = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_DELAY_WORKFLOW, "");
		FolderEntry entry = getFolderModule().getEntry(null, entryId);
		//Adding an entry; get the specific definition
		model.put(WebKeys.FOLDER, entry.getParentFolder());
		model.put(WebKeys.BINDER, entry.getParentFolder());
		model.put(WebKeys.ENTRY, entry);
		model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_MOBILE);
		model.put(WebKeys.OPERATION_TYPE, entryOperationType);
		model.put(WebKeys.ENTRY_DELAY_WORKFLOW, delayWorkflow);
		DefinitionHelper.getDefinition(entry.getEntryDefDoc(), model, "//item[@type='form']");
		
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn") || formData.containsKey("addUGTBtn") || formData.containsKey("cancelBtn")) {
			if (formData.containsKey("addUGTBtn")) {
				//This is a request to get a user, group or team name
				return ajaxMobileAddUserGroupTeam(bs, request, response, model);
			}
			return ajaxMobileShowEntry(bs, request, response);
		} else {
			return new ModelAndView("mobile/modify_entry", model);
		}
	}	

	private ModelAndView ajaxMobileDeleteEntry(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));		
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));		
		Folder folder = getFolderModule().getFolder(folderId);
		Map formData = request.getParameterMap();
		if (!formData.containsKey("okBtn")) {
			FolderEntry entry = getFolderModule().getEntry(folderId, entryId);
			model.put(WebKeys.ENTRY, entry);
			DefinitionHelper.getDefinition(entry.getEntryDefDoc(), model, "//item[@type='form']");
		}
		//Deleting an entry; get the specific definition
		model.put(WebKeys.FOLDER, folder);
		model.put(WebKeys.BINDER, folder);
		model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_MOBILE);
		
		if (formData.containsKey("okBtn")) {
			return ajaxMobileShowFolder(bs, request, response);
		} else if (formData.containsKey("cancelBtn")) {
			return ajaxMobileShowEntry(bs, request, response);
		} else {
			return new ModelAndView("mobile/delete_entry", model);
		}
	}	

	private ModelAndView ajaxMobileAddUserGroupTeam(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Map model) throws Exception {
		if (model == null) {
			model = new HashMap();
		}
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));		
		String type = new String(PortletRequestUtils.getStringParameter(request, WebKeys.URL_TYPE, "user"));		
		String elementName = new String(PortletRequestUtils.getStringParameter(request, WebKeys.URL_ELEMENT, ""));		
		String addUGT = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_UGT, "");
		String entryOperationType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_OPERATION_TYPE, "");
		String delayWorkflow = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_DELAY_WORKFLOW, "");
		if (!addUGT.equals("")) {
			//This is a request from the add or modify form
			String[] ugtData = addUGT.split(",");
			type = ugtData[0];
			elementName = ugtData[1];
		}
		FolderEntry entry = getFolderModule().getEntry(null, entryId);
		//Adding an entry; get the specific definition
		model.put(WebKeys.FOLDER, entry.getParentBinder());
		model.put(WebKeys.BINDER, entry.getParentBinder());
		model.put(WebKeys.ENTRY, entry);
		model.put(WebKeys.TYPE, type);
		model.put(WebKeys.ELEMENT_NAME, elementName);
		model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_MOBILE);
		model.put(WebKeys.ENTRY_DELAY_WORKFLOW, delayWorkflow);
		model.put(WebKeys.OPERATION_TYPE, entryOperationType);
		DefinitionHelper.getDefinition(entry.getEntryDefDoc(), model, "//item[@type='form']");
		
		Map formData = request.getParameterMap();
		if (formData.containsKey("addUserGroupTeamBtn") || formData.containsKey("closeBtn")) {
			return ajaxMobileModifyEntry(bs, request, response);
		} else {
			return new ModelAndView("mobile/add_user_group_team", model);
		}
	}	

	private ModelAndView ajaxMobileFindUserGroupTeam(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));		
		String type = new String(PortletRequestUtils.getStringParameter(request, WebKeys.URL_TYPE, "user"));		
		String elementName = new String(PortletRequestUtils.getStringParameter(request, WebKeys.URL_ELEMENT, ""));		
		String entryOperationType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_OPERATION_TYPE, "");
		String delayWorkflow = new String(PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_DELAY_WORKFLOW, ""));		
		FolderEntry entry = getFolderModule().getEntry(null, entryId);
		//Adding an entry; get the specific definition
		model.put(WebKeys.FOLDER, entry.getParentBinder());
		model.put(WebKeys.BINDER, entry.getParentBinder());
		model.put(WebKeys.ENTRY, entry);
		model.put(WebKeys.TYPE, type);
		model.put(WebKeys.ELEMENT_NAME, elementName);
		model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_MOBILE);
		model.put(WebKeys.ENTRY_DELAY_WORKFLOW, delayWorkflow);
		model.put(WebKeys.ENTRY_OPERATION_TYPE, entryOperationType);
		DefinitionHelper.getDefinition(entry.getEntryDefDoc(), model, "//item[@type='form']");
		
		Map formData = request.getParameterMap();
		String searchText = PortletRequestUtils.getStringParameter(request, "searchText", "", false);
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
	    if (!searchText.equals("")) {
	    	searchText = searchText.trim() + "*";
	    }
			    
		if (type.equals("group")) {
			searchTermFilter.addGroupNameFilter(searchText);
			// 1/8/2016 JK (bug 960775)
			searchTermFilter.addTitleFilter(searchText);
		} else if (type.equals("team")) {
			searchTermFilter.addTeamFilter(searchText);
		} else {
			searchTermFilter.addLoginNameFilter(searchText);
			// 1/8/2016 JK (bug 960775)
			searchTermFilter.addTitleFilter(searchText);
		}
			   	
		//Do a search to find the first few items that match the search text
		options.put(ObjectKeys.SEARCH_SEARCH_FILTER, searchTermFilter.getFilter());
		Map entries = null;
		if (type.equals("user")) {
			entries = getProfileModule().getUsers(options);
		} else if (type.equals("group")) {
			entries = getProfileModule().getGroups(options);
		} else if (type.equals("team")) {
			entries = getBinderModule().executeSearchQuery(searchTermFilter.getFilter(), Constants.SEARCH_MODE_NORMAL, options);
		}
		model.put(WebKeys.USERS, entries.get(ObjectKeys.SEARCH_ENTRIES));
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

		return new ModelAndView("mobile/add_user_group_team", model);
	}	

	private ModelAndView ajaxMobileAddReply(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();

    	Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));
    	request.setAttribute(WebKeys.URL_ENTRY_ID, entryId.toString());
    	FolderEntry entry = getFolderModule().getEntry(null, entryId);
    	Folder folder = entry.getParentFolder();
    	model.put(WebKeys.FOLDER, folder); 
    	model.put(WebKeys.BINDER, folder); 
    	model.put(WebKeys.ENTRY, entry); 
			
    	//Get the legal reply types from the parent entry definition
		Document entryView = null;
		if (entry.getEntryDefId() != null) {
			entryView = entry.getEntryDefDoc();
		}
		List replyStyles = null;
		if (entryView != null) {
			//See if there is a reply style for this entry definition
			replyStyles = DefinitionUtils.getPropertyValueList(entryView.getRootElement(), "replyStyle");
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
			DefinitionHelper.getDefinition((Document) null, model, "//item[@name='entryForm']");
		}
		
		Map formData = request.getParameterMap();
		if (entryType.equals("") || formData.containsKey("okBtn") || formData.containsKey("addUGTBtn") || formData.containsKey("cancelBtn")) {
			if (formData.containsKey("addUGTBtn")) {
				//This is a request to get a user, group or team name
				return ajaxMobileAddUserGroupTeam(bs, request, response, null);
			} else {
				return ajaxMobileShowEntry(bs, request, response);
			}
		} else {
			return new ModelAndView("mobile/add_reply", model);
		}
	}	

	private ModelAndView ajaxMobileFindPeople(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);		
		Map model = new HashMap();
		String scope = PortletRequestUtils.getStringParameter(request, WebKeys.URL_SEARCH_SCOPE, "site");
		model.put(WebKeys.SEARCH_SCOPE, scope);
		BinderHelper.setupStandardBeans(bs, request, response, model, null, "ss_mobile");
		if (binderId != null) {
			try {
				Binder binder = getBinderModule().getBinder(binderId);
				model.put(WebKeys.BINDER, binder);
			} catch(Exception e) {}
		}
		BinderHelper.setupMobileSearchBeans(bs, request, response, model);
		Map formData = request.getParameterMap();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if (op.equals(WebKeys.OPERATION_MOBILE_FIND_PEOPLE) || op.equals(WebKeys.OPERATION_MOBILE_FIND_PLACES)) {
			String searchText = PortletRequestUtils.getStringParameter(request, "searchText", "", false);
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
				if (scope.equals("local")) options.put(ObjectKeys.SEARCH_ANCESTRY, binderId.toString());;
				
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
					entries = getBinderModule().executeSearchQuery( searchTermFilter.getFilter(), Constants.SEARCH_MODE_NORMAL, options);
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

				//Setup the actions menu list
				List actions = new ArrayList();
				//BinderHelper.addActionsHome(request, actions);
				BinderHelper.addActionsRecentPlaces(request, actions, binderId);
				BinderHelper.addActionsLogout(request, actions);
				model.put("ss_actions", actions);

				return new ModelAndView(view, model);
				
			}
		}

		//Setup the actions menu list
		List actions = new ArrayList();
		//BinderHelper.addActionsHome(request, actions);
		BinderHelper.addActionsRecentPlaces(request, actions, binderId);
		BinderHelper.addActionsLogout(request, actions);
		model.put("ss_actions", actions);

		String view = "mobile/find_people";
		if (op.equals(WebKeys.OPERATION_MOBILE_FIND_PLACES)) view = "mobile/find_places";

		return new ModelAndView(view, model);
	}
}
