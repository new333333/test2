/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

import static org.kablink.util.search.Constants.DOC_TYPE_FIELD;
import static org.kablink.util.search.Constants.ENTRY_TYPE_FIELD;
import static org.kablink.util.search.Constants.MODIFICATION_DATE_FIELD;
import static org.kablink.util.search.Restrictions.between;
import static org.kablink.util.search.Restrictions.in;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
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
import java.util.TreeMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import net.sf.json.util.JSONUtils;

import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.lucene.document.DateTools;
import org.dom4j.Document;
import org.dom4j.Element;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.kablink.teaming.web.util.BinderHelper;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.PortletRequestBindingException;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.calendar.AbstractIntervalView;
import org.kablink.teaming.calendar.EventsViewHelper;
import org.kablink.teaming.calendar.OneMonthView;
import org.kablink.teaming.calendar.StartEndDatesView;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.ApplicationGroup;
import org.kablink.teaming.domain.ApplicationPrincipal;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.NoBinderByTheNameException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ReservedByAnotherUserException;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.WorkflowControlledEntry;
import org.kablink.teaming.domain.WorkflowSupport;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.FileAttachment.FileStatus;
import org.kablink.teaming.lucene.util.SearchFieldResult;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.conferencing.ConferencingException;
import org.kablink.teaming.module.conferencing.ConferencingModule;
import org.kablink.teaming.module.conferencing.MeetingInfo;
import org.kablink.teaming.module.conferencing.MeetingInfo.MeetingRecurrance;
import org.kablink.teaming.module.conferencing.MeetingInfo.MeetingType;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.ical.AttendedEntries;
import org.kablink.teaming.module.ldap.LdapModule;
import org.kablink.teaming.module.ldap.LdapModule.LdapSyncMode;
import org.kablink.teaming.module.ldap.LdapSyncResults;
import org.kablink.teaming.module.ldap.LdapSyncThread;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.module.shared.SearchUtils;
import org.kablink.teaming.portlet.binder.AccessControlController;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.relevance.Relevance;
import org.kablink.teaming.relevance.util.RelevanceUtils;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.search.filter.SearchFiltersBuilder;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.OperationAccessControlException;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.ssfs.util.SsfsUtil;
import org.kablink.teaming.survey.Question;
import org.kablink.teaming.survey.Survey;
import org.kablink.teaming.survey.SurveyModel;
import org.kablink.teaming.task.TaskHelper;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.CalendarHelper;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.StatusTicket;
import org.kablink.teaming.util.TagUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractControllerRetry;
import org.kablink.teaming.web.tree.DomTreeBuilder;
import org.kablink.teaming.web.tree.TreeHelper;
import org.kablink.teaming.web.tree.WsDomTreeBuilder;
import org.kablink.teaming.web.upload.FileUploadProgressListener;
import org.kablink.teaming.web.upload.ProgressListenerSessionResolver;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.EventHelper;
import org.kablink.teaming.web.util.FixupFolderDefsThread;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.teaming.web.util.UserAppConfig;
import org.kablink.teaming.web.util.Favorites;
import org.kablink.teaming.web.util.ListFolderHelper;
import org.kablink.teaming.web.util.ListFolderHelper.ModeType;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.Tabs;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.WebStatusTicket;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.Http;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Order;

/**
 * ?
 * 
 * @author Peter Hurley
 */
@SuppressWarnings({"unchecked", "unused"})
public class AjaxController  extends SAbstractControllerRetry {
	
	//caller will retry on OptimisiticLockExceptions
	@Override
	public void handleActionRequestWithRetry(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if (WebHelper.isUserLoggedIn(request)) {
			if (op.equals(WebKeys.OPERATION_SHOW_FOLDER_PAGE) || 
					op.equals(WebKeys.OPERATION_SHOW_WIKI_FOLDER_PAGE)) {
				ajaxSaveFolderPage(request, response);
			} else if (op.equals(WebKeys.OPERATION_SAVE_COLUMN_POSITIONS)) {
				ajaxSaveColumnPositions(request, response);
			} else if (op.equals(WebKeys.OPERATION_ADD_FAVORITE_BINDER)) {
				ajaxAddFavoriteBinder(request, response);
			} else if (op.equals(WebKeys.OPERATION_ADD_FAVORITES_CATEGORY)) {
				ajaxAddFavoritesCategory(request, response);
			} else if (op.equals(WebKeys.OPERATION_MODIFY_TAGS)) {
				if (WebHelper.isMethodPost(request)) ajaxModifyTags(request, response);
			} else if (op.equals(WebKeys.OPERATION_SAVE_FAVORITES)) {
				ajaxSaveFavorites(request, response);
			} else if (op.equals(WebKeys.OPERATION_SAVE_USER_APPCONFIG)) {
				ajaxSaveUserAppConfig(request, response);
			} else if ( op.equals( WebKeys.OPERATION_SAVE_USER_TUTORIAL_PANEL_STATE ) )
			{
				// Save the state of the tutorial panel.  It is either collapsed, expanded or hidden.
				ajaxSaveUserTutorialPanelState( request, response );
			} else if (op.equals(WebKeys.OPERATION_SAVE_RATING)) {
				if (WebHelper.isMethodPost(request)) ajaxSaveRating(request, response);
			} else if (op.equals(WebKeys.OPERATION_SHOW_HELP_CPANEL) || 
					op.equals(WebKeys.OPERATION_HIDE_HELP_CPANEL)) {
				ajaxShowHideHelpControlPanel(request, response);
			} else if (op.equals(WebKeys.OPERATION_SHOW_SIDEBAR_PANEL) || 
					op.equals(WebKeys.OPERATION_HIDE_SIDEBAR_PANEL)) {
				ajaxShowHideSidebarPanel(request, response);
			} else if (op.equals(WebKeys.OPERATION_SHOW_BUSINESS_CARD) || 
					op.equals(WebKeys.OPERATION_HIDE_BUSINESS_CARD)) {
				ajaxShowHideBusinessCard(request, response);
			} else if (op.equals(WebKeys.OPERATION_SET_UI_THEME)) {
				ajaxSetUiTheme(request, response);
			} else if (op.equals(WebKeys.OPERATION_UPLOAD_IMAGE_FILE)) {
				if (WebHelper.isMethodPost(request)) ajaxUploadImageFile(request, response); 
			} else if (op.equals(WebKeys.OPERATION_UPLOAD_ICALENDAR_FILE)) {
				if (WebHelper.isMethodPost(request)) ajaxUploadICalendarFile(request, response, false);
			} else if (op.equals(WebKeys.OPERATION_UPLOAD_ICALENDAR_FILE_GWT)) {
				if (WebHelper.isMethodPost(request)) ajaxUploadICalendarFile(request, response, true);
			} else if (op.equals(WebKeys.OPERATION_UPLOAD_DESKTOP_BRANDING_MAC)) {
				if (WebHelper.isMethodPost(request)) ajaxDesktopBrandingMac(request, response);
			} else if (op.equals(WebKeys.OPERATION_UPLOAD_DESKTOP_BRANDING_WINDOWS)) {
				if (WebHelper.isMethodPost(request)) ajaxDesktopBrandingWindows(request, response);
			} else if (op.equals(WebKeys.OPERATION_UPLOAD_MOBILE_BRANDING_ANDROID)) {
				if (WebHelper.isMethodPost(request)) ajaxMobileBrandingAndroid(request, response);
			} else if (op.equals(WebKeys.OPERATION_UPLOAD_MOBILE_BRANDING_IOS)) {
				if (WebHelper.isMethodPost(request)) ajaxMobileBrandingIos(request, response);
			} else if (op.equals(WebKeys.OPERATION_UPLOAD_MOBILE_BRANDING_WINDOWS)) {
				if (WebHelper.isMethodPost(request)) ajaxMobileBrandingWindows(request, response);
			} else if (op.equals(WebKeys.OPERATION_LOAD_ICALENDAR_BY_URL)) {
				ajaxLoadICalendarByURL(request, response);				
			} else if (op.equals(WebKeys.OPERATION_SAVE_CALENDAR_CONFIGURATION)) {
				ajaxSaveCalendarConfiguration(request, response);				
			} else if (op.equals(WebKeys.OPERATION_SET_BINDER_OWNER_ID)) {
				ajaxSetBinderOwnerId(request, response);
			} else if (op.equals(WebKeys.OPERATION_MODIFY_GROUP)) {
				if (WebHelper.isMethodPost(request)) ajaxModifyGroup(request, response);
			} else if (op.equals(WebKeys.OPERATION_STICKY_CALENDAR_DISPLAY_SETTINGS)) {
				ajaxStickyCalendarDisplaySettings(request, response);
			} else if (op.equals(WebKeys.OPERATION_SAVE_SEARCH_QUERY)) {
				ajaxSaveSearchQuery(request, response);
			} else if (op.equals(WebKeys.OPERATION_REMOVE_SEARCH_QUERY)) {
				ajaxRemoveSearchQuery(request, response);
			} else if (op.equals(WebKeys.OPERATION_VOTE_SURVEY_REMOVE)) {
				if (WebHelper.isMethodPost(request)) ajaxVoteSurveyRemove(request, response);				
			} else if (op.equals(WebKeys.OPERATION_SUBSCRIBE)) {
				Map formData = request.getParameterMap();
				if (formData.containsKey("okBtn") && WebHelper.isMethodPost(request)) ajaxDoSubscription(request, response);
			} else if (op.equals(WebKeys.OPERATION_SAVE_UESR_STATUS)) {
				if (WebHelper.isMethodPost(request)) ajaxSaveUserStatus(this, request, response);
			} else if (op.equals(WebKeys.OPERATION_SET_SIDEBAR_VISIBILITY)) {
				ajaxSetSidebarVisibility(request, response);
			} else if (op.equals(WebKeys.OPERATION_SET_SUNBURST_VISIBILITY)) {
				ajaxSetSunburstVisibility(request, response);
			} else if (op.equals(WebKeys.OPERATION_UNSET_SUNBURST_VISIBILITY)) {
				ajaxUnsetSunburstVisibility(request, response);
			} else if (op.equals(WebKeys.OPERATION_PIN_ENTRY)) {
				if (WebHelper.isMethodPost(request)) ajaxPinEntry(this, request, response);
			} else if (op.equals( WebKeys.OPERATION_SET_FILE_STATUS )) {
				if (WebHelper.isMethodPost(request)) ajaxSetFileStatus(this, request, response);
			}
		}
	}
	
	@Override
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		Map model = new HashMap();
		Map statusMap = new HashMap();
		
		if (op.equals(WebKeys.OPERATION_SAVE_WINDOW_HEIGHT)) {
			return ajaxSetWindowHeight(request, response);
		} else if (op.equals(WebKeys.OPERATION_GET_WINDOW_HEIGHT)) {
			return ajaxGetWindowHeight(request, response);
		} else if (op.equals(WebKeys.OPERATION_SET_PORTAL_SIGNAL_URL)) {
			return ajaxSetPortalUrl(request, response);
		} else if (op.equals(WebKeys.OPERATION_VIEW_ERROR_MESSAGE)) {
			return ajaxViewErrorMessage(request, response);
		}
		if (!WebHelper.isUserLoggedIn(request) || (!WebHelper.isUserLoggedIn(request) && op.equals(""))) {
			//Signal that the user is not logged in. 
			//  The code on the calling page will output the proper translated message.
			statusMap.put(WebKeys.AJAX_STATUS_NOT_LOGGED_IN, new Boolean(true));
			model.put(WebKeys.AJAX_STATUS, statusMap);

			//Check for calls from "ss_fetch_url" (which return 
			if (op.equals(WebKeys.OPERATION_SHOW_FOLDER_PAGE) || 
					op.equals(WebKeys.OPERATION_SHOW_WIKI_FOLDER_PAGE)) {
				return new ModelAndView("forum/fetch_url_return", model);
			} else if (op.equals(WebKeys.OPERATION_SHOW_BLOG_REPLIES)) {
				return new ModelAndView("forum/fetch_url_return", model);
			} else if (op.equals(WebKeys.OPERATION_CONFIGURE_FOLDER_COLUMNS)) {
				return new ModelAndView("forum/fetch_url_return", model);
			} else if (op.equals(WebKeys.OPERATION_UPLOAD_IMAGE_FILE)) {
				return new ModelAndView("forum/fetch_url_return", model);
			} else if (op.equals(WebKeys.OPERATION_MODIFY_GROUP)) {
				return new ModelAndView("forum/fetch_url_return", model);
			} else if (op.equals(WebKeys.OPERATION_SET_LAST_VIEWED_BINDER)) {
			} else if (op.equals(WebKeys.OPERATION_SHOW_MY_TEAMS)) {
				return new ModelAndView("forum/fetch_url_return", model);
			} else if (op.equals(WebKeys.OPERATION_SHOW_HELP_CPANEL) || 
						op.equals(WebKeys.OPERATION_HIDE_HELP_CPANEL) ||
						op.equals(WebKeys.OPERATION_SHOW_HELP_PANEL) ||
						op.equals(WebKeys.OPERATION_SHOW_BUSINESS_CARD) ||
						op.equals(WebKeys.OPERATION_HIDE_BUSINESS_CARD) ||
						op.equals(WebKeys.OPERATION_SHOW_SIDEBAR_PANEL) || 
						op.equals(WebKeys.OPERATION_HIDE_SIDEBAR_PANEL)	) {
				return new ModelAndView("forum/fetch_url_return", model);			
			}
			if (op.equals(WebKeys.OPERATION_SAVE_SEARCH_QUERY) ||
					op.equals(WebKeys.OPERATION_REMOVE_SEARCH_QUERY) ||
					op.equals(WebKeys.OPERATION_SAVE_ENTRY_WIDTH) ||
					op.equals(WebKeys.OPERATION_SUBSCRIBE) ||
					op.equals(WebKeys.OPERATION_ADD_FAVORITE_BINDER) || 
					op.equals(WebKeys.OPERATION_ADD_FAVORITES_CATEGORY) || 
					op.equals(WebKeys.OPERATION_GET_FAVORITES_TREE) ||
					op.equals(WebKeys.OPERATION_SAVE_FAVORITES) ||
					op.equals(WebKeys.OPERATION_SAVE_USER_APPCONFIG) ||
					op.equals( WebKeys.OPERATION_SAVE_USER_TUTORIAL_PANEL_STATE ) ||
					op.equals( WebKeys.OPERATION_START_LDAP_SYNC ) ||
					op.equals( WebKeys.OPERATION_GET_LDAP_SYNC_RESULTS ) ||
					op.equals( WebKeys.OPERATION_REMOVE_LDAP_SYNC_RESULTS ) ||
					op.equals( WebKeys.OPERATION_STOP_COLLECTING_LDAP_SYNC_RESULTS ) ) {
				model.put(WebKeys.AJAX_ERROR_MESSAGE, "general.notLoggedIn");	
				response.setContentType("text/json");
				return new ModelAndView("common/json_ajax_return", model);
			}
			
			response.setContentType("text/xml");			
			if (op.equals(WebKeys.OPERATION_UNSEEN_COUNTS)) {
				return new ModelAndView("forum/unseen_counts", model);
			} else if (op.equals(WebKeys.OPERATION_CHECK_IF_LOGGED_IN)) {
				if (RequestContextHolder.getRequestContext() != null) {
		        	model.put(WebKeys.USER_PRINCIPAL, RequestContextHolder.getRequestContext().getUser());
				}
				return new ModelAndView("forum/check_if_logged_in_return", model);
			} else if (op.equals(WebKeys.OPERATION_SAVE_COLUMN_POSITIONS)) {
				return new ModelAndView("forum/save_column_positions_return", model);
			} else if (op.equals(WebKeys.OPERATION_SAVE_ENTRY_HEIGHT)) {
				return new ModelAndView("forum/save_entry_height_return", model);
			} else if (op.equals(WebKeys.OPERATION_GET_FILTER_TYPE) || 
					op.equals(WebKeys.OPERATION_GET_ENTRY_ELEMENTS) || 
					op.equals(WebKeys.OPERATION_GET_ELEMENT_VALUES) || 
					op.equals(WebKeys.OPERATION_GET_ELEMENT_VALUE_DATA) ||
					op.equals(WebKeys.OPERATION_GET_WORKFLOW_STATES)) {
				return new ModelAndView("binder/get_entry_elements", model);
			} else if (op.equals(WebKeys.OPERATION_GET_CONDITION_ENTRY_ELEMENTS) || 
					op.equals(WebKeys.OPERATION_GET_CONDITION_ENTRY_VALUE_LIST) ||
					op.equals(WebKeys.OPERATION_GET_CONDITION_ENTRY_DATA_VALUE_LIST)) {
				return new ModelAndView("definition_builder/get_condition_element", model);
			} else if (op.equals(WebKeys.OPERATION_GET_ENTRY_WORKFLOW_STATES)) {
				return new ModelAndView("definition_builder/get_entry_workflow_states", model);
			} else if (op.equals(WebKeys.OPERATION_WORKSPACE_TREE)) {
				return new ModelAndView("tag_jsps/tree/get_tree_div", model);
			} else if (op.equals(WebKeys.OPERATION_GET_ACCESS_CONTROL_TABLE)) {
				return new ModelAndView("binder/access_control_table", model);
			} else if (op.equals(WebKeys.OPERATION_START_MEETING)) {
				return new ModelAndView("forum/meeting_return", model);	
			} else if (op.equals(WebKeys.OPERATION_SCHEDULE_MEETING)) {
				return new ModelAndView("forum/meeting_return", model);	
			} else if (op.equals(WebKeys.OPERATION_VOTE_SURVEY)) {
				return new ModelAndView("forum/json/vote_survey", model);	
			} else if (op.equals(WebKeys.OPERATION_VOTE_SURVEY_REMOVE)) {
				return new ModelAndView("forum/json/vote_survey", model);				
			} else if (op.equals(WebKeys.OPERATION_UPLOAD_ICALENDAR_FILE)) {
				return new ModelAndView("forum/json/icalendar_upload", model);
			} else if (op.equals(WebKeys.OPERATION_UPLOAD_ICALENDAR_FILE_GWT)) {
				return new ModelAndView("forum/json/icalendar_upload_gwt", model);
			} else if (op.equals(WebKeys.OPERATION_UPDATE_TASK)) {
				return  new ModelAndView("forum/json/update_task", model);
			}

			return new ModelAndView("forum/ajax_return", model);
		}
		
		//The user is logged in
		if (op.equals(WebKeys.OPERATION_SHOW_FOLDER_PAGE)) {
			return ajaxGetFolderPage(this, request, response);
		} else if (op.equals(WebKeys.OPERATION_SHOW_WIKI_FOLDER_PAGE)) {
			return ajaxGetWikiFolderPage(this, request, response);
		} else if (op.equals(WebKeys.OPERATION_UNSEEN_COUNTS)) {
			return ajaxGetUnseenCounts(request, response);
		} else if (op.equals(WebKeys.OPERATION_CHECK_IF_LOGGED_IN)) {
			return ajaxCheckIfLoggedIn(request, response);

		} else if (op.equals(WebKeys.OPERATION_ADD_FAVORITE_BINDER) || 
				op.equals(WebKeys.OPERATION_ADD_FAVORITES_CATEGORY) ||
				op.equals(WebKeys.OPERATION_GET_FAVORITES_TREE) ||
				op.equals(WebKeys.OPERATION_SAVE_FAVORITES)) {
			return ajaxGetFavoritesTree(request, response);
		} else if (op.equals(WebKeys.OPERATION_SET_LAST_VIEWED_BINDER)) {
			return ajaxSetLastViewedBinder(request, response);
		} else if (op.equals(WebKeys.OPERATION_SAVE_COLUMN_POSITIONS)) {
			return new ModelAndView("forum/save_column_positions_return");
			
		} else if (op.equals(WebKeys.OPERATION_CONFIGURE_FOLDER_COLUMNS)) {
			return ajaxConfigureFolderColumns(this, request, response);
		} else if (op.equals(WebKeys.OPERATION_SUBSCRIBE)) {
			return ajaxSubscribe(request, response); 
		} else if (op.equals(WebKeys.OPERATION_SAVE_ENTRY_WIDTH)) {
			return ajaxSaveEntryWidth(request, response);
			
		} else if (op.equals(WebKeys.OPERATION_SAVE_ENTRY_HEIGHT)) {
			return ajaxSaveEntryHeight(request, response);
			
		} else if (op.equals(WebKeys.OPERATION_SAVE_REGION_VIEW)) {
			return ajaxSaveRegionView(request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MODIFY_TAGS)) {
			return ajaxShowTags(request, response);
		} else if (op.equals(WebKeys.OPERATION_WORKSPACE_TREE)) {
			return ajaxGetWorkspaceTree(request, response);
		} else if (op.equals(WebKeys.OPERATION_SHOW_MY_TEAMS)) {
			return ajaxGetMyTeams(request, response);
		} else if(op.equals(WebKeys.OPERATION_SHOW_BLOG_REPLIES)) {
			return ajaxGetBlogReplies(request, response);
		} else if (op.equals(WebKeys.OPERATION_SAVE_RATING)) {
			return ajaxGetEntryRating(request, response);
		
		} else if (op.equals(WebKeys.OPERATION_SHOW_HELP_PANEL)) {
			return ajaxShowHelpPanel(request, response);
		
		} else if (op.equals(WebKeys.OPERATION_GET_ACCESS_CONTROL_TABLE)) {
			return ajaxGetAccessControlTable(request, response);
		} else if (op.equals(WebKeys.OPERATION_UPLOAD_IMAGE_FILE)) {
			return ajaxGetUploadImageFile(request, response);
		} else if (op.equals(WebKeys.OPERATION_ADD_ATTACHMENT_OPTIONS)) {
			return addAttachmentOptions(request, response); 
		} else if (op.equals(WebKeys.OPERATION_RELOAD_ENTRY_ATTACHMENTS)) {
			return reloadEntryAttachment(request, response); 
		} else if (op.equals(WebKeys.OPERATION_OPEN_WEBDAV_FILE)) {
			return openWebDAVFile(request, response); 
		} else if (op.equals(WebKeys.OPERATION_OPEN_WEBDAV_FILE_BY_FILEID)) {
			return openWebDAVFileUsingFileId(request, response); 
		} else if (op.equals(WebKeys.OPERATION_ADD_FOLDER_ATTACHMENT_OPTIONS)) {
			return addFolderAttachmentOptions(request, response); 
		} else if (op.equals(WebKeys.OPERATION_START_MEETING)) {
			return ajaxStartMeeting(request, response, MeetingType.Adhoc);
		} else if (op.equals(WebKeys.OPERATION_SCHEDULE_MEETING)) {
			return ajaxStartMeeting(request, response, MeetingType.Scheduled);
		} else if (op.equals(WebKeys.OPERATION_GET_TEAM_MEMBERS)) {
			return ajaxGetTeamMembers(request, response);
		} else if (op.equals(WebKeys.OPERATION_SET_BINDER_OWNER_ID)) {
			return ajaxGetBinderOwner(request, response);
		} else if (op.equals(WebKeys.OPERATION_MODIFY_GROUP)) {
			return ajaxGetGroup(request, response);			
		} else if (op.equals(WebKeys.OPERATION_FIND_CALENDAR_EVENTS)) {
			return ajaxFindCalendarEvents(request, response);
		} else if (op.equals(WebKeys.OPERATION_GET_CALENDAR_FREE_INFO)) {
			return ajaxGetCalendarFreeInfo(request, response);			
		} else if (op.equals(WebKeys.OPERATION_FIND_ENTRY_FOR_FILE)) {
			return ajaxFindEntryForFile(request, response);
		} else if (op.equals(WebKeys.OPERATION_CHECK_BINDER_TITLE)) {
			return ajaxCheckBinderTitle(request, response);
		} else if (op.equals(WebKeys.OPERATION_CHECK_TEMPLATE_NAME)) {
			return ajaxCheckTemplateName(request, response);
		} else if (op.equals(WebKeys.OPERATION_SAVE_SEARCH_QUERY)) {
			return ajaxGetSearchQueryName(request, response);
		} else if (op.equals(WebKeys.OPERATION_REMOVE_SEARCH_QUERY)) {
			return ajaxGetRemovedQueryName(request, response);
		} else if (op.equals(WebKeys.OPERATION_UPDATE_TASK)) {
			return ajaxUpdateTask(request, response);
		} else if (op.equals(WebKeys.OPERATION_LIST_SAVED_QUERIES)) {
			return ajaxListSavedQueries(request, response);
		} else if (op.equals(WebKeys.OPERATION_VOTE_SURVEY)) {
			return ajaxVoteSurvey(request, response);	
		} else if (op.equals(WebKeys.OPERATION_VOTE_SURVEY_REMOVE)) {
			return ajaxSurveyRemoveVoteStatus(request, response);				
		} else if (op.equals(WebKeys.OPERATION_CHECK_STATUS)) {
			return ajaxCheckStatus(request, response);
		} else if (op.equals(WebKeys.OPERATION_WIKILINK_FORM)) {
			return ajaxWikiLinkForm(request, response);
		} else if (op.equals(WebKeys.OPERATION_FIND_PLACE_FORM)) {
			return ajaxFindPlaceForm(request, response);
		} else if (op.equals(WebKeys.OPERATION_UPLOAD_ICALENDAR_FILE)) {
			return ajaxUploadICalendarFileStatus(request, response, false);
		} else if (op.equals(WebKeys.OPERATION_UPLOAD_ICALENDAR_FILE_GWT)) {
			return ajaxUploadICalendarFileStatus(request, response, true);
		} else if (op.equals(WebKeys.OPERATION_LOAD_ICALENDAR_BY_URL)) {
			return ajaxLoadICalendarByURL(request, response);			
		} else if (op.equals(WebKeys.OPERATION_SAVE_CALENDAR_CONFIGURATION)) {
			return ajaxSaveCalendarConfigurationStatus(request, response);			
		} else if (op.equals(WebKeys.OPERATION_GET_CHANGE_LOG_ENTRY_FORM)) {
			return ajaxGetChangeLogEntryForm(request, response);
		} else if (op.equals(WebKeys.OPERATION_GET_FILTER_TYPE) || 
				op.equals(WebKeys.OPERATION_GET_ENTRY_ELEMENTS) || 
				op.equals(WebKeys.OPERATION_GET_ELEMENT_VALUES) || 
				op.equals(WebKeys.OPERATION_GET_ELEMENT_VALUE_DATA) || 
				op.equals(WebKeys.OPERATION_GET_WORKFLOW_STATES)) {
			return ajaxGetFilterData(request, response);
		} else if (op.equals(WebKeys.OPERATION_GET_CONDITION_ENTRY_ELEMENTS) || 
				op.equals(WebKeys.OPERATION_GET_CONDITION_ENTRY_DATA_ELEMENTS) || 
				op.equals(WebKeys.OPERATION_GET_CONDITION_ENTRY_USER_LIST_ELEMENTS) || 
				op.equals(WebKeys.OPERATION_GET_CONDITION_ENTRY_OPERATIONS) || 
				op.equals(WebKeys.OPERATION_GET_CONDITION_ENTRY_DATA_OPERATIONS) || 
				op.equals(WebKeys.OPERATION_GET_CONDITION_ENTRY_DATA_VALUE_LIST) || 
				op.equals(WebKeys.OPERATION_GET_CONDITION_ENTRY_VALUE_LIST)) {
			return ajaxGetConditionData(request, response);
		} else if (op.equals(WebKeys.OPERATION_GET_ENTRY_WORKFLOW_STATES)) {
			return ajaxGetEntryWorkflowStates(request, response);
		} else if (op.equals(WebKeys.OPERATION_SHOW_HELP_CPANEL) || 
					op.equals(WebKeys.OPERATION_HIDE_HELP_CPANEL) ||
					op.equals(WebKeys.OPERATION_SHOW_BUSINESS_CARD) ||
					op.equals(WebKeys.OPERATION_HIDE_BUSINESS_CARD) ||
					op.equals(WebKeys.OPERATION_SHOW_SIDEBAR_PANEL) || 
					op.equals(WebKeys.OPERATION_HIDE_SIDEBAR_PANEL)) {
			return new ModelAndView("forum/fetch_url_return");			
		} else if (op.equals(WebKeys.OPERATION_SAVE_UESR_STATUS)) {
			return ajaxGetUserStatus(request, response);
		} else if (op.equals(WebKeys.OPERATION_GET_USER_APPCONFIG) ||
				   op.equals(WebKeys.OPERATION_SAVE_USER_APPCONFIG)) {
			return ajaxGetUserAppConfig(request, response);
		} else if (op.equals(WebKeys.OPERATION_GET_GROUP_LIST)) {
			return ajaxGetGroupList(this, request, response);
		} else if (op.equals(WebKeys.OPERATION_VIEW_MINIBLOG)) {
			return ajaxViewMiniBlog(request, response);
		} else if (op.equals(WebKeys.OPERATION_GET_UPLOAD_PROGRESS_STATUS)) {
			return ajaxGetUploadProgressStatus(request, response);
		} else if (op.equals(WebKeys.OPERATION_GET_WORKFLOW_APPLET)) {
			return ajaxGetWorkflowApplet(request, response);
		} else if (op.equals( WebKeys.OPERATION_START_FIXUP_FOLDER_DEFS )) {
			return ajaxStartFixupFolderDefs(request, response);
		} else if (op.equals( WebKeys.OPERATION_SET_FILE_STATUS )) {
			return ajaxSetFileStatusReturn(request, response);
		}
		else if ( op.equals( WebKeys.OPERATION_SAVE_USER_TUTORIAL_PANEL_STATE ) )
		{
			// There is no data that needs to be passed back.
			response.setContentType( "text/json" );
			return new ModelAndView( "common/json_ajax_return", model );
		}
		else if ( op.equals( WebKeys.OPERATION_START_LDAP_SYNC ) )
		{
			// Start an ldap sync.
			return ajaxStartLdapSync( request, response );
		}
		else if ( op.equals( WebKeys.OPERATION_GET_LDAP_SYNC_RESULTS ) )
		{
			// Get the latest results of an ldap sync results.
			return ajaxGetLdapSyncResults( request, response );
		}
		else if ( op.equals( WebKeys.OPERATION_STOP_COLLECTING_LDAP_SYNC_RESULTS ) )
		{
			// Tell the ldap sync process to stop collecting results.
			return ajaxStopCollectingLdapSyncResults( request, response );
		}
		else if ( op.equals( WebKeys.OPERATION_REMOVE_LDAP_SYNC_RESULTS ) )
		{
			// Remove the LdapSyncThread object we stored in the session.
			return ajaxRemoveLdapSyncResults( request, response );
		}
		else if ( op.equals( WebKeys.OPERATION_GET_USER_ACCESS_REPORT ) )
		{
			// Get a user access report.
			return ajaxGetUserAccessReport( request, response );
		}
		else if ( op.equals( WebKeys.OPERATION_GET_EMAIL_REPORT ) )
		{
			// Get email report.
			return ajaxGetEmailReport( request, response );
		}
		else if ( op.equals( WebKeys.OPERATION_GET_XSS_REPORT ) )
		{
			// Get the XSS report.
			return ajaxGetXssReport( request, response );
		}
		else if (op.equals(WebKeys.OPERATION_TRASH_PURGE)     ||
				   op.equals(WebKeys.OPERATION_TRASH_PURGE_ALL) ||
				   op.equals(WebKeys.OPERATION_TRASH_RESTORE)   ||
				   op.equals(WebKeys.OPERATION_TRASH_RESTORE_ALL)) {
			return TrashHelper.ajaxTrashRequest(op, this, request, response);
		}
		else if (op.equals(WebKeys.OPERATION_GET_FILE_RELATIONSHIPS_BY_ENTRY)) {
			return ajaxGetFileRelationshipsByEntry(request, response);
		}
		else if (op.equals(WebKeys.OPERATION_VALIDATE_BINDER_QUOTAS)) {
			return ajaxValidateBinderQuotas(request, response);
			
		} else if (op.equals(WebKeys.OPERATION_CHECK_EXISTS_FILES_FROM_APPLET)) {
			return ajaxCheckIfFilesExist(request, response);
		}
		return ajaxReturn(request, response);
	} 

	private void ajaxSaveFolderPage(ActionRequest request, ActionResponse response) throws Exception {
		Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
		String pageStartIndex = PortletRequestUtils.getStringParameter(request, WebKeys.PAGE_START_INDEX, "0");
		if (pageStartIndex.equals("")) pageStartIndex = "0";
		Tabs.TabEntry tab = Tabs.getTabs(request).getTab(binderId);
		if (tab != null) {
			Map tabData = tab.getData();
			tabData.put(Tabs.PAGE, new Integer(pageStartIndex));			
			tab.setData(tabData);
		}
		response.setRenderParameter(WebKeys.URL_NEW_TAB, "0");
	}
	
	private void ajaxSaveColumnPositions(ActionRequest request, ActionResponse response) throws Exception {
		Long binderId = null;
		try {
			binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);				
		} catch(PortletRequestBindingException ex) {}
		
		//Save the user's placement of columns in this folder
		String columnPositions = PortletRequestUtils.getStringParameter(request, "column_positions", "");
		if (Validator.isNotNull(columnPositions)) {
			//Save the column positions
		   	if (binderId == null) {
		   		getProfileModule().setUserProperty(null, WebKeys.FOLDER_COLUMN_POSITIONS, columnPositions);
		   	} else {
		   		getProfileModule().setUserProperty(null, binderId, WebKeys.FOLDER_COLUMN_POSITIONS, columnPositions);
		   	}
		}
	}
	
	private void ajaxAddFavoriteBinder(ActionRequest request, ActionResponse response) throws Exception {
		//Add a binder to the favorites list
		Long binderId = new Long(PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID));				
		if (binderId != null) {
			Binder binder = getBinderModule().getBinder(binderId);
			UserProperties userProperties = getProfileModule().getUserProperties(null);
			Favorites f = new Favorites((String)userProperties.getProperty(ObjectKeys.USER_PROPERTY_FAVORITES));
			String title = binder.getTitle();
			if (binder instanceof Folder) title += " (" + ((Folder)binder).getParentBinder().getTitle() + ")";
			f.addFavorite(title, binder.getPathName(), Favorites.FAVORITE_BINDER, binderId.toString(), PortletRequestUtils.getStringParameter(request, "viewAction", ""), "");
			getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_FAVORITES, f.toString());
		}
	}
	
	private void ajaxAddFavoritesCategory(ActionRequest request, ActionResponse response) throws Exception {
		//Add a category to the favorites list
		String category = PortletRequestUtils.getStringParameter(request, "category", "");
		UserProperties userProperties = getProfileModule().getUserProperties(null);
		Favorites f = new Favorites((String)userProperties.getProperty(ObjectKeys.USER_PROPERTY_FAVORITES));
		f.addCategory(category, "");
		getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_FAVORITES, f.toString());
	}
	
	private void ajaxModifyTags(ActionRequest request, ActionResponse response) throws Exception {
		//Add or delete tags
		Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
		Long entityId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTITY_ID);
		String type = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTITY_TYPE, "");
		String operation2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
		String communityTag = PortletRequestUtils.getStringParameter(request, "communityTag", "");
		String personalTag = PortletRequestUtils.getStringParameter(request, "personalTag", "");
		String tagToDelete = PortletRequestUtils.getStringParameter(request, "tagToDelete", "");
		EntityType entityType = EntityType.valueOf(type);
		if (EntityIdentifier.EntityType.folder.equals(entityType) || 
				EntityIdentifier.EntityType.workspace.equals(entityType) ||
				EntityIdentifier.EntityType.profiles.equals(entityType)) {
			if (operation2.equals("delete")) {
				getBinderModule().deleteTag(binderId, tagToDelete);
			} else if (operation2.equals("add")) {
				if (!communityTag.equals("")) getBinderModule().setTag(binderId, communityTag, true);
				if (!personalTag.equals("")) getBinderModule().setTag(binderId, personalTag, false);
			}
		} else {
			if (operation2.equals("delete")) {
				getFolderModule().deleteTag(binderId, entityId, tagToDelete);
			} else if (operation2.equals("add")) {
				if (!communityTag.equals("")) getFolderModule().setTag(binderId, entityId, communityTag, true);
				if (!personalTag.equals("")) getFolderModule().setTag(binderId, entityId, personalTag, false);
			}
		}
	}
	
	
	private void ajaxSaveFavorites(ActionRequest request, ActionResponse response) throws Exception {
		//Save the order of the favorites list
		String deletedIds = PortletRequestUtils.getStringParameter(request, "deletedIds", "");
		String favoritesList = PortletRequestUtils.getStringParameter(request, "favorites", "");
		UserProperties userProperties = getProfileModule().getUserProperties(null);
		Favorites f = new Favorites((String)userProperties.getProperty(ObjectKeys.USER_PROPERTY_FAVORITES));
		f.saveOrder(deletedIds, favoritesList);
		getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_FAVORITES, f.toString());
	}
	
	private void ajaxSaveUserAppConfig(ActionRequest request, ActionResponse response) throws Exception {
		String appConfigs = PortletRequestUtils.getStringParameter(request, "appConfigs", "");
		UserAppConfig uac = UserAppConfig.createFromBrowserData(appConfigs);
		getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_APPCONFIGS, uac.toString());
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private void ajaxSaveUserTutorialPanelState( ActionRequest request, ActionResponse response ) throws Exception
	{
		String tutorialState;
		
		// Get the state of the tutorial panel from the request.
		tutorialState = PortletRequestUtils.getStringParameter( request, "tutorialPanelState", "" );

		// Save the state of the tutorial panel to the db.
		if ( tutorialState != null && tutorialState.length() > 0 )
		{
			getProfileModule().setUserProperty( null, ObjectKeys.USER_PROPERTY_TUTORIAL_PANEL_STATE, tutorialState );
		}
	}// end ajaxSaveUserTutorialPanelState()
	
	
	private void ajaxSaveRating(ActionRequest request, ActionResponse response) throws Exception {
		//Save the order of the favorites list
		Long rating = new Long(PortletRequestUtils.getRequiredLongParameter(request, "rating"));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, "entryId"));				
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, "binderId"));				
		FolderEntry entry = getFolderModule().getEntry(binderId, entryId);
		getFolderModule().setUserRating(binderId, entryId, rating);
		getFolderModule().indexEntry(entry, false);
	}
	

	private void ajaxSetUiTheme(ActionRequest request,
			ActionResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		String uiTheme = PortletRequestUtils.getStringParameter(request, "theme", "");
		if (uiTheme.equals(ObjectKeys.USER_THEME_DEFAULT)) uiTheme = "";
		if (uiTheme.length() > 50) {
			uiTheme = uiTheme.substring(0,50);
		} 
		
		Map updates = new HashMap();
		updates.put(ObjectKeys.FIELD_PRINCIPAL_THEME, uiTheme);
		getProfileModule().modifyEntry(user.getId(), new MapInputData(updates));
	}
	
	private void ajaxShowHideHelpControlPanel(ActionRequest request,
			ActionResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		Boolean showHelpCPanel = Boolean.TRUE;
		if (op.equals(WebKeys.OPERATION_HIDE_HELP_CPANEL)) showHelpCPanel = Boolean.FALSE;
		getProfileModule().setUserProperty(user.getId(), 
					ObjectKeys.USER_PROPERTY_HELP_CPANEL_SHOW, showHelpCPanel);
	}


	private void ajaxShowHideSidebarPanel(ActionRequest request,
			ActionResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		String panelId = PortletRequestUtils.getStringParameter(request, "id", "");
		if (panelId.length() > 50) {
			panelId = panelId.substring(0,50);
		} 
		Boolean showPanel = Boolean.TRUE;
		if (op.equals(WebKeys.OPERATION_HIDE_SIDEBAR_PANEL)) showPanel = Boolean.FALSE;
		getProfileModule().setUserProperty(user.getId(), 
					ObjectKeys.USER_PROPERTY_SIDEBAR_PANEL_PREFIX + panelId, showPanel);
	}

	
	private void ajaxShowHideBusinessCard(ActionRequest request,
			ActionResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		String opScope = PortletRequestUtils.getStringParameter(request, "scope", "");
		String scope = "mine";
		if (opScope.equals("other")) {
			scope = "other";
		}
		Boolean showBC = Boolean.TRUE;
		if (op.equals(WebKeys.OPERATION_HIDE_BUSINESS_CARD)) showBC = Boolean.FALSE;
		getProfileModule().setUserProperty(user.getId(), 
					ObjectKeys.USER_PROPERTY_BUSINESS_CARD_PREFIX + scope, showBC);
	}

	/**
	 * Returns the relationships to the files attached to the given
	 * entry.
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private ModelAndView ajaxGetFileRelationshipsByEntry(RenderRequest request, RenderResponse response) throws Exception {
		Map model = new HashMap();
		String relationshipErrorKey = null;
		
		// Is relevance integration enabled?
		Set<Attachment> attSet;
		Set<User>       userSet;
		Set<Workspace>  wsSet;
		Relevance re = RelevanceUtils.getRelevanceEngine();
		if (re.isRelevanceEnabled()) {
			// Yes!  Access the Binder...
			String binderIdStr = PortletRequestUtils.getStringParameter( request, "binderId", "" );
			Long binderId = Long.valueOf( binderIdStr );
			
			// ...FolderEntry...
			String entryIdStr = PortletRequestUtils.getStringParameter( request, "entryId", "" );
			Long entryId = Long.valueOf( entryIdStr );
			FolderEntry fe = getFolderModule().getEntry(binderId, entryId);
			
			// ...the Set<Attachment> of the Attachment's that are
			// ...related to any of the Attachment's on that
			// ...FolderEntry...
			try {
				attSet = RelevanceUtils.getRelatedAttachments(this, fe);
			}
			catch (Exception e) {
				attSet = new HashSet<Attachment>();
				if ((e instanceof IOException) && e.getMessage().equalsIgnoreCase("In Process")) {
					// As of the 20100128 (401) build of the relevance
					// engine, we get this IOException if we call this
					// too soon after adding files.  It seems that
					// while the relevance engine is busy processing
					// files, it can't handle requests for file
					// relationships.
					re.getRelevanceLogger().debug("AjaxController.ajaxGetFileRelationshipsByEntry( EXCEPTION:  'In Process' ):  FolderEntry:  '" + fe.getTitle() + "'");				
					relationshipErrorKey = "entry.relevanceEngineBusy";
				}
				else {
					re.getRelevanceLogger().error("AjaxController.ajaxGetFileRelationshipsByEntry( EXCEPTION ):  FolderEntry:  '" + fe.getTitle() + "'", e);				
				}
			}

			// ...and map the Set<Attachment> to their User's and
			// ...Workspace's.
			userSet = RelevanceUtils.getAttachmentUsers(     attSet);
			wsSet   = RelevanceUtils.getAttachmentWorkspaces(attSet);
		}
		else {
			attSet  = null;
			userSet = null;
			wsSet   = null;
		}
		
		buildRelationshipModel(model, attSet, userSet, wsSet, relationshipErrorKey);
		response.setContentType("text/json");
		return new ModelAndView("forum/json/file_relationships", model);
	}

	/*
	 * Adds information about Attachment relationships to mode.
	 */
	private static void buildRelationshipModel(Map model, Set<Attachment> attSet, Set<User> userSet, Set<Workspace> wsSet, String relationshipErrorKey) {
		model.put("relationshipErrorKey", ((null == relationshipErrorKey) ? ""                        : relationshipErrorKey));
		model.put("relatedAttachments",   ((null == attSet)               ? new HashSet<Attachment>() : attSet));
		model.put("relatedUsers",         ((null == userSet)              ? new HashSet<User>()       : userSet));
		model.put("relatedWorkspaces",    ((null == wsSet)                ? new HashSet<Workspace>()  : wsSet));
	}
	
	/**
	 * ?
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private ModelAndView ajaxValidateBinderQuotas(RenderRequest request, RenderResponse response) throws Exception {
		Map model = new HashMap();
		getAdminModule().checkAccess(AdminOperation.manageFunction);
		Binder topBinder = getWorkspaceModule().getTopWorkspace();

		// Create a new status ticket
		String ticketId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_STATUS_TICKET_ID, "none");
		StatusTicket statusTicket = WebStatusTicket.newStatusTicket(ticketId, request);
		statusTicket.setStatus("<span class='ss_bold'>" + NLT.get("validate.binderQuota.starting") + "</span>");
		List<Long> errors = new ArrayList<Long>();
		Collection idsIndexed = getBinderModule().validateBinderQuotaTree(topBinder, statusTicket, errors);
		String msg = "<span class='ss_bold'>" + NLT.get("validate.binderQuota.completedScanned") + " " + String.valueOf(idsIndexed.size()) + "</span><br/>";
		msg += "<span class='ss_bold'>" + NLT.get("validate.binderQuota.completedCorrections") + " " + String.valueOf(errors.size()) + "</span><br/>";
		statusTicket.setStatus(msg);
		statusTicket.done();
		//SimpleProfiler.done(logger);
		if (!getAdminModule().isBinderQuotaInitialized()) {
			getAdminModule().setBinderQuotasInitialized(Boolean.TRUE);
		}
		model.put(WebKeys.IDS_COUNT, String.valueOf(idsIndexed.size()));
		model.put(WebKeys.ERROR_COUNT, String.valueOf(errors.size()));
		response.setContentType("text/json");
		return new ModelAndView("forum/json/validate_binder_quotas", model);
	}

	/**
	 * ?
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private ModelAndView ajaxCheckIfFilesExist(RenderRequest request, RenderResponse response) throws Exception {
		Map model = new HashMap();
		Binder binder = null;
		Entry entry = null;
		Long binderId = null;
		try {
			binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);				
		} catch(Exception ex) {}
		if (binderId != null) {
			try {
				binder = getBinderModule().getBinder(binderId);
			} catch(Exception ex) {}
		}
		Long entryId = null;
		try {
			entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);				
		} catch(Exception ex) {}
		if (entryId != null) {
			try {
				entry = getFolderModule().getEntry(binderId, entryId);
			} catch(Exception ex) {}
		}
		String fileNames = PortletRequestUtils.getStringParameter(request, "fileNames", "", false);
		List fileNameList = new ArrayList<String>();
		String[] fns = fileNames.split(",");
		for (int i = 0; i < fns.length; i++) {
			if (!fns[i].trim().equals("")) {
				//See if this file exists
				if (entry != null) {
					//This is an entry. Look at the entry's attached files for a match
					for (FileAttachment fa : entry.getFileAttachments()) {
						if (fa.getFileExists() && fa.getFileItem().getName().equals(fns[i])) {
							fileNameList.add(fns[i]);
							break;
						}
					}
				} else if (binder!= null) {
					//Now look for an entry in the binder with this name
					if (binder instanceof Folder) {
						FolderEntry fe = getFolderModule().getLibraryFolderEntryByFileName((Folder) binder, fns[i]);
						if (fe != null) {
							fileNameList.add(fns[i]);
							continue;
						}
					}
					//This is a binder. Look at the binder's attached files for a match
					for (FileAttachment fa : binder.getFileAttachments()) {
						if (fa.getFileExists() && fa.getFileItem().getName().equals(fns[i])) {
							fileNameList.add(fns[i]);
							break;
						}
					}
				}
			}
		}
		model.put("fileNames", fileNameList);

		response.setContentType("text/json");
		return new ModelAndView("forum/json/return_file_names", model);
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private ModelAndView ajaxGetUserAccessReport( RenderRequest request, RenderResponse response ) throws Exception
	{
		Map		model;
		String	userIdStr;
		Long	userId;
		List<Map<String, Object>> report = null;
		
		model = new HashMap();
		
		// Get the id of the user we should get an access report for.
		userIdStr = PortletRequestUtils.getStringParameter( request, "userId", "" );
		userId = Long.valueOf( userIdStr );
		
		// Get a report of what items the given user has access to.
        report = getReportModule().generateAccessReportByUser( userId, null, null, "summary" );

        // Add the access report to the response.
        model.put( "userAccessReport", report );

		response.setContentType( "text/json" );
		return new ModelAndView("forum/json/user_access_report", model);
	}// end ajaxGetUserAccessReport()

	/**
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private ModelAndView ajaxGetEmailReport( RenderRequest request, RenderResponse response ) throws Exception
	{
		Map model;
		List<Map<String, Object>> report = null;
		
		String reportType = PortletRequestUtils.getStringParameter(request, "reportType", 
				ReportModule.EMAIL_REPORT_TYPE_SEND);
		
		model = new HashMap();
		
		Map formData = request.getParameterMap();
		MapInputData inputData = new MapInputData(formData);
		GregorianCalendar cal = new GregorianCalendar();
		Date startDate = inputData.getDateValue(WebKeys.URL_START_DATE);
		Date endDate = inputData.getDateValue(WebKeys.URL_END_DATE);
		
		if(endDate != null) {
			cal.setTime(endDate);
			cal.add(Calendar.DATE, 1);
			endDate = cal.getTime();
		}
        report = getReportModule().generateEmailReport( startDate, endDate, reportType );

        // Add the access report to the response.
        model.put( "emailReport", report );

		response.setContentType( "text/json" );
		return new ModelAndView("forum/json/email_report_json", model);
	}// end ajaxGetXssReport()
	
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private ModelAndView ajaxGetXssReport( RenderRequest request, RenderResponse response ) throws Exception
	{
		Map		model;
		List<Map<String, Object>> report = null;
		
		model = new HashMap();
		
		//Get the binders to be indexed
		Map formData = request.getParameterMap();
		Collection<Long> ids = TreeHelper.getSelectedIds(formData);

		// Get a report of items that have XSS problems.
		List binderIds = new ArrayList();
		for (Long id : ids) {
			binderIds.add(String.valueOf(id));
		}
		if (binderIds.isEmpty()) binderIds.add(getWorkspaceModule().getTopWorkspace().getId().toString());
        report = getReportModule().generateXssReport( binderIds, null, null, "summary" );

        // Add the access report to the response.
        model.put( "xssReport", report );

		response.setContentType( "text/json" );
		return new ModelAndView("forum/json/xss_report_json", model);
	}// end ajaxGetXssReport()
	
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private ModelAndView ajaxGetLdapSyncResults( RenderRequest request, RenderResponse response ) throws Exception
	{
		Map				model;
		String			syncId;
		LdapSyncResults	syncResults;
		
		model = new HashMap();
		
		// Get the id of the sync results we are looking for.
		syncId = PortletRequestUtils.getStringParameter( request, "ldapSyncResultsId", "" );
		
		// Get the ldap sync results object we are looking for.
		syncResults = LdapSyncThread.getLdapSyncResults( request, syncId );
		
		// Gather up any partial results we have and return them.
		model.put( "ldapSyncResults", syncResults );

		response.setContentType( "text/json" );
		return new ModelAndView("forum/json/ldap_sync_results", model);
	}// end ajaxGetLdapSyncResults()
	
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private ModelAndView ajaxRemoveLdapSyncResults( RenderRequest request, RenderResponse response ) throws Exception
	{
		Map				model;
		String			syncId;
		LdapSyncThread	syncThread;
		
		model = new HashMap();
		
		// Get the id of the sync thread we are looking for.
		syncId = PortletRequestUtils.getStringParameter( request, "ldapSyncResultsId", "" );
		
		// Get the ldap sync thread object we are looking for.
		syncThread = LdapSyncThread.getLdapSyncThread( request, syncId );
		
		// Remove the ldap sync thread from the session.  This won't stop the ldap sync.  Just cleaning up.
		if ( syncThread != null )
			syncThread.removeFromSession();

		// We don't need to return any data.
		response.setContentType( "text/json" );
		return new ModelAndView( "common/json_ajax_return", model );
	}// end ajaxRemoveLdapSyncResults()
	
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private ModelAndView ajaxStartLdapSync( RenderRequest request, RenderResponse response ) throws Exception
	{
		Map				model;
		String			syncId;
		LdapSyncThread	ldapSyncThread;
		LdapModule		ldapModule;
		Boolean syncUsersAndGroups;
		String[] listOfLdapConfigsToSyncGuid;
		
		model = new HashMap();
		
		ldapModule = getLdapModule();

		// Get the id of the sync results we are looking for.
		syncId = PortletRequestUtils.getStringParameter( request, "ldapSyncResultsId", "" );
		
		// Get the list of ldap configs that we need to sync the guid.
		listOfLdapConfigsToSyncGuid = PortletRequestUtils.getStringParameters( request, "listOfLdapConfigsToSyncGuid", false );
		
		// Get the flag that tells us whether we should sync all users and groups.
		syncUsersAndGroups = PortletRequestUtils.getBooleanParameter( request, "syncUsersAndGroups", false );
		
		// Create an LdapSyncThread object that will do the sync work.
		// Currently doing the sync on a separate thread does not work.  When doing work on a separate thread
		// works, replace the call to doLdapSync() with start().
		ldapSyncThread = LdapSyncThread.createLdapSyncThread(
														request,
														syncId,
														ldapModule,
														syncUsersAndGroups.booleanValue(),
														listOfLdapConfigsToSyncGuid,
														LdapSyncMode.PERFORM_SYNC );
		if ( ldapSyncThread != null )
		{
			ldapSyncThread.doLdapSync();
		}

		// We don't need to return any data for this request.
		response.setContentType( "text/json" );
		return new ModelAndView( "common/json_ajax_return", model );
	}// end ajaxStartLdapSync()
	
	
	/**
	 * Handles a request start a fixup folder definitions thread.
	 *  
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private ModelAndView ajaxStartFixupFolderDefs( RenderRequest request, RenderResponse response ) throws Exception
	{
		// Do we have a fixup thread ready to start?
		FixupFolderDefsThread fixFolderDefsThread = FixupFolderDefsThread.getFixupFolderDefsThread(request);
		if ((null != fixFolderDefsThread) && fixFolderDefsThread.isFolderFixupReady()) {
			// Yes!  Start it.
			fixFolderDefsThread.startFixups(this);
		}

		// We don't need to return any data for this request.
		response.setContentType("text/json" );
		return new ModelAndView("common/json_ajax_return", new HashMap());
	}
	
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private ModelAndView ajaxStopCollectingLdapSyncResults( RenderRequest request, RenderResponse response ) throws Exception
	{
		Map				model;
		String			syncId;
		LdapSyncResults	syncResults;
		
		model = new HashMap();
		
		// Get the id of the sync results we are looking for.
		syncId = PortletRequestUtils.getStringParameter( request, "ldapSyncResultsId", "" );
		
		// Get the ldap sync results object we are looking for.
		syncResults = LdapSyncThread.getLdapSyncResults( request, syncId );
		
		// Tell the ldap sync process to not collect any more results.
		if ( syncResults != null )
			syncResults.stopCollectingResults();

		response.setContentType( "text/json" );
		return new ModelAndView( "common/json_ajax_return", model );
	}// end ajaxStopCollectingLdapSyncResults()
	
	
	private ModelAndView ajaxGetWindowHeight(RenderRequest request, 
			RenderResponse response) throws Exception {
		PortletSession portletSession = WebHelper.getRequiredPortletSession(request);
		String windowHeight = (String)portletSession.getAttribute(WebKeys.WINDOW_HEIGHT, PortletSession.APPLICATION_SCOPE);
		Map model = new HashMap();
		model.put(WebKeys.WINDOW_HEIGHT, windowHeight);
		response.setContentType("text/javascript");
		return new ModelAndView("forum/window_height", model);
	}
	
	private ModelAndView ajaxSetWindowHeight(RenderRequest request, 
			RenderResponse response) throws Exception {
		String height = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
		PortletSession portletSession = WebHelper.getRequiredPortletSession(request);
		String windowHeight = (String) portletSession.getAttribute(WebKeys.WINDOW_HEIGHT, PortletSession.APPLICATION_SCOPE);
		portletSession.setAttribute(WebKeys.WINDOW_HEIGHT, height, PortletSession.APPLICATION_SCOPE);
		Map model = new HashMap();
		model.put(WebKeys.WINDOW_HEIGHT, windowHeight);
		response.setContentType("text/html");
		return new ModelAndView("forum/blank_return", model);
	}
	
	private ModelAndView ajaxSetPortalUrl(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String value = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
		PortletSession portletSession = WebHelper.getRequiredPortletSession(request);
		portletSession.setAttribute(WebKeys.PORTAL_SIGNAL_URL, value, PortletSession.APPLICATION_SCOPE);
		model.put(WebKeys.PORTAL_SIGNAL_URL, value);
		response.setContentType("text/html");
		return new ModelAndView("forum/signal_portal_resize", model);
	}
	
	private ModelAndView ajaxViewErrorMessage(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String errorMsg = PortletRequestUtils.getStringParameter(request, WebKeys.URL_VALUE, "");
		model.put(WebKeys.ERROR_MESSAGE, NLT.get("general.error.anErrorOccurred") + ": " + errorMsg);
		response.setContentType("text/html");
		return new ModelAndView("forum/error_return", model);
	}
	
	private ModelAndView ajaxSetLastViewedBinder(RenderRequest request, 
			RenderResponse response) throws Exception {
		String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		String entityType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTITY_TYPE, "");
		if (binderId != null && !entityType.equals("") && !namespace.equals("")) {
			PortletSession portletSession = WebHelper.getRequiredPortletSession(request);
			portletSession.setAttribute(WebKeys.LAST_BINDER_VIEWED + namespace, binderId, PortletSession.APPLICATION_SCOPE);
			portletSession.setAttribute(WebKeys.LAST_BINDER_ENTITY_TYPE + namespace, entityType, PortletSession.APPLICATION_SCOPE);
		}
		Map model = new HashMap();
		return new ModelAndView("forum/blank_return", model);
	}
	
	private ModelAndView ajaxGetFavoritesTree(RenderRequest request, 
							RenderResponse response) throws Exception {
		Map model = new HashMap();
		UserProperties userProperties = getProfileModule().getUserProperties(null);
		Object obj = userProperties.getProperty(ObjectKeys.USER_PROPERTY_FAVORITES);
		String namespace = PortletRequestUtils.getStringParameter(request, "namespace", "");
		Favorites f;
		if (obj instanceof Document) {
			f = new Favorites((Document)obj);
			//fixup - have to store as string cause hibernate equals fails
			getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_FAVORITES, f.toString());
		} else {		
			f = new Favorites((String)obj);
		}
		Document favTree = f.getFavoritesTree();
		String favJson = (String)f.getFavoritesTreeJson().toString();
		model.put(WebKeys.FAVORITES_TREE, favTree);
		model.put("TreeJSON",favJson);
		Document favTreeDelete = f.getFavoritesTreeDelete();
		model.put(WebKeys.FAVORITES_TREE_DELETE, favTreeDelete);
		model.put(WebKeys.NAMESPACE, namespace);

		response.setContentType("text/json");
		return new ModelAndView("forum/favorites_tree", model);
	}
	
	private ModelAndView ajaxGetFolderPage(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
		String zoneUUID = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ZONE_UUID, "");
		boolean showTrash = PortletRequestUtils.getBooleanParameter(request, WebKeys.URL_SHOW_TRASH, false);
		ModelAndView modelAndView = ListFolderHelper.BuildFolderBeans(bs, request, response, binderId, zoneUUID, showTrash);
		modelAndView.setView("definition_elements/folder_view_common_page");
		return modelAndView;
	}

	private ModelAndView ajaxGetWikiFolderPage(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
		String zoneUUID = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ZONE_UUID, "");
		boolean showTrash = PortletRequestUtils.getBooleanParameter(request, WebKeys.URL_SHOW_TRASH, false);
		ModelAndView modelAndView = ListFolderHelper.BuildFolderBeans(bs, request, response, binderId, zoneUUID, showTrash);
		modelAndView.setView("definition_elements/wiki/wiki_folder_page_ajax");
		return modelAndView;
	}

	private ModelAndView ajaxGetUnseenCounts(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String[] forumList = new String[0];
		if (PortletRequestUtils.getStringParameter(request, "forumList") != null) {
			forumList = PortletRequestUtils.getStringParameter(request, "forumList").split(" ");
		}
		List folderIds = new ArrayList();
		for (int i = 0; i < forumList.length; i++) {
			folderIds.add(new Long(forumList[i]));
		}
		Map unseenCounts = new HashMap();
		unseenCounts = getFolderModule().getUnseenCounts(folderIds);
	
		response.setContentType("text/xml");
		
		model.put(WebKeys.LIST_UNSEEN_COUNTS_BINDER_IDS, folderIds);
		model.put(WebKeys.LIST_UNSEEN_COUNTS, unseenCounts);
		model.put(WebKeys.NAMING_PREFIX, PortletRequestUtils.getStringParameter(request, WebKeys.NAMING_PREFIX, ""));
		return new ModelAndView("forum/unseen_counts", model);
}

	private ModelAndView ajaxReturn(RenderRequest request, 
				RenderResponse response) throws Exception {
		response.setContentType("text/xml");
		return new ModelAndView("forum/ajax_return");
	}
		
	private ModelAndView ajaxConfigureFolderColumns(AllModulesInjected bs, RenderRequest request, 
				RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long binderId = null;
		try {
			binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);				
		} catch(PortletRequestBindingException ex) {}
		
		BinderHelper.setupStandardBeans(bs, request, response, model, binderId);
		
		Map accessControlMap = BinderHelper.getAccessControlMapBean(model);
		Map binderAccessMap = new HashMap();
		if (accessControlMap.containsKey(binderId)) {
			binderAccessMap = (Map) accessControlMap.get(binderId);
		}
		Binder binder = null;
		if (binderId != null) binder = getBinderModule().getBinder(binderId);
		model.put(WebKeys.BINDER, binder);
		if (binder != null && getBinderModule().testAccess(binder, BinderOperation.modifyBinder)) {
			binderAccessMap.put(BinderOperation.modifyBinder.toString(), true);
		}
		accessControlMap.put(binderId, binderAccessMap);
		if (binder instanceof TemplateBinder) model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_TEMPLATE);
		UserProperties userProperties;
		Map columns = null;
		if (binderId == null) {
			userProperties = getProfileModule().getUserProperties(null);
			columns = (Map) userProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_RESULTS_FOLDER_COLUMNS);
		} else {
			userProperties = getProfileModule().getUserProperties(null, binderId);
			if (!(binder instanceof TemplateBinder)) columns = (Map) userProperties.getProperty(ObjectKeys.USER_PROPERTY_FOLDER_COLUMNS);
			if (columns == null || columns.isEmpty()) 
				columns = (Map) binder.getProperty(ObjectKeys.BINDER_PROPERTY_FOLDER_COLUMNS);
			Map entryDefs = DefinitionHelper.getEntryDefsAsMap(binder);
			Map entryElements = new HashMap();
			Iterator itDefs = entryDefs.entrySet().iterator();
			while (itDefs.hasNext()) {
				Map.Entry me = (Map.Entry) itDefs.next();
				String defId = (String) me.getKey();
				Map elementData = getDefinitionModule().getEntryDefinitionElements(defId);
				entryElements.put(defId, elementData);
			}
			model.put(WebKeys.ENTRY_DEFINITION_MAP, entryDefs);
			model.put(WebKeys.ENTRY_DEFINTION_ELEMENT_DATA_MAP, entryElements);
		}
		model.put(WebKeys.FOLDER_COLUMNS, columns);
		String op2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
		model.put(WebKeys.FOLDER_TYPE, op2);
		
		String viewType = BinderHelper.getViewType(bs, binder);
		model.put(WebKeys.FOLDER_VIEW_TYPE, viewType);

		return new ModelAndView("forum/configure_folder_columns_return", model);
	}
	
	private ModelAndView ajaxSubscribe(RenderRequest request, RenderResponse response) throws Exception {
		Map formData = request.getParameterMap();
		//if just finished a request, nothing to return
		if (formData.containsKey("okBtn")) {
			response.setContentType("text/json");
			return new ModelAndView("common/json_ajax_return");
		}
		//request for forms is by fetch_url
		Map model = new HashMap();
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);				
		Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
		String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
		model.put(WebKeys.NAMESPACE, namespace);
		if (entryId==null) {
			Binder binder = getBinderModule().getBinder(binderId);
			Subscription sub = getBinderModule().getSubscription(binder);
			model.put(WebKeys.SUBSCRIPTION, sub);
			model.put(WebKeys.SCHEDULE_INFO, getAdminModule().getNotificationSchedule());
			model.put(WebKeys.BINDER, binder);
			return new ModelAndView("forum/subscribe_return", model);			
		} else {
			FolderEntry entry = getFolderModule().getEntry(binderId, entryId);
			Subscription sub = getFolderModule().getSubscription(entry);			
			model.put(WebKeys.SUBSCRIPTION, sub);
			model.put(WebKeys.ENTRY, entry);
			return new ModelAndView("forum/subscribe_entry_return", model);
			
		}
	}

	private ModelAndView ajaxDoSubscription(ActionRequest request, 
			ActionResponse response) throws Exception {
		//this call is the json ajax part of ajaxSubscription, made by ss_post
		Long binderId= PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
		Long entryId= PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
		Map<Integer, String[]> styles = new HashMap();
		Boolean disable = PortletRequestUtils.getBooleanParameter(request, "disable", false);
		if (Boolean.TRUE.equals(disable)) styles.put(Subscription.DISABLE_ALL_NOTIFICATIONS, null);
		for (int i=1; i<6; ++i) {
			String[] address = PortletRequestUtils.getStringParameters(request, "_subscribe"+i, false);
			if (address == null || address.length ==0) continue;
			else styles.put(Integer.valueOf(i), address);
		}
		if (entryId == null) {
			getBinderModule().setSubscription(binderId, styles);
		} else {
			getFolderModule().setSubscription(binderId, entryId, styles);
		}

		return new ModelAndView("common/json_ajax_return");

	}

	private ModelAndView ajaxSaveEntryWidth(RenderRequest request, 
				RenderResponse response) throws Exception {
		//Save the user's selected entry width, etc.
		String entryWidth = PortletRequestUtils.getStringParameter(request, "entry_width");
		String entryHeight = PortletRequestUtils.getStringParameter(request, "entry_height");
		String entryTop = PortletRequestUtils.getStringParameter(request, "entry_top");
		String entryLeft = PortletRequestUtils.getStringParameter(request, "entry_left");

		Map values = new HashMap();
		if (Validator.isNumber(entryWidth)) values.put(WebKeys.FOLDER_ENTRY_WIDTH, entryWidth);
		if (Validator.isNumber(entryHeight)) values.put(WebKeys.FOLDER_ENTRY_HEIGHT, entryHeight);
		if (Validator.isNumber(entryTop)) values.put(WebKeys.FOLDER_ENTRY_TOP, entryTop);
		if (Validator.isNumber(entryLeft)) values.put(WebKeys.FOLDER_ENTRY_LEFT, entryLeft);
		getProfileModule().setUserProperties(null, values);
		
		response.setContentType("text/json");
		return new ModelAndView("common/json_ajax_return");
	}
	
	private ModelAndView ajaxSaveEntryHeight(RenderRequest request, 
			RenderResponse response) throws Exception {
		Long binderId = null;
		try {
			binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);				
		} catch(PortletRequestBindingException ex) {}
		
		Map model = new HashMap();
		String entryHeight = PortletRequestUtils.getStringParameter(request, "entry_height");
		if (Validator.isNotNull(entryHeight)) {
			//Save the entry width
		   	if (binderId == null) {
		   		getProfileModule().setUserProperty(null, WebKeys.FOLDER_ENTRY_HEIGHT, entryHeight);
		   	} else {
		   		getProfileModule().setUserProperty(null, binderId, WebKeys.FOLDER_ENTRY_HEIGHT, entryHeight);
		   	}
		}
		response.setContentType("text/xml");
		return new ModelAndView("forum/save_entry_height_return", model);
	}

	private ModelAndView ajaxSaveRegionView(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String regionId = PortletRequestUtils.getStringParameter(request, "id");
		String regionState = PortletRequestUtils.getStringParameter(request, "state", "expanded");
		getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_REGION_VIEW + "." + regionId, regionState);

		response.setContentType("text/xml");
		return new ModelAndView("forum/ajax_return", model);
	}

	private ModelAndView ajaxSetFileStatusReturn(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String fileAttId = PortletRequestUtils.getStringParameter(request, "fileAttId");
		String fileStatusId = PortletRequestUtils.getStringParameter(request, "fileStatus");
		if (Validator.isNotNull(fileStatusId)) {
			if (fileStatusId.equals("0")) {
				model.put("fileStatus", NLT.get("file.statusNone"));
			} else {
				model.put("fileStatus", NLT.get("file.status" + fileStatusId));
			}
		}
		return new ModelAndView("forum/set_file_status_return", model);
	}

	private ModelAndView ajaxShowTags(RenderRequest request, 
			RenderResponse response) throws Exception {
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		Long entityId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTITY_ID);
		String type = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTITY_TYPE, "");
		String namespace = PortletRequestUtils.getStringParameter(request, "namespace", "");
		String tagDivNumber = PortletRequestUtils.getStringParameter(request, "tagDivNumber", "");
		EntityType entityType = EntityType.valueOf(type);
		Map model = new HashMap();
		
		if (EntityIdentifier.EntityType.folder.equals(entityType) || 
				EntityIdentifier.EntityType.workspace.equals(entityType) ||
				EntityIdentifier.EntityType.profiles.equals(entityType)) {
			Binder binder = getBinderModule().getBinder(binderId);
			Map tagResults = TagUtil.uniqueTags(getBinderModule().getTags(binder));
			model.put(WebKeys.COMMUNITY_TAGS, tagResults.get(ObjectKeys.COMMUNITY_ENTITY_TAGS));
			model.put(WebKeys.PERSONAL_TAGS, tagResults.get(ObjectKeys.PERSONAL_ENTITY_TAGS));
			model.put(WebKeys.ENTRY, binder);
			model.put(WebKeys.BINDER, binder);
		} else {
			FolderEntry entry = getFolderModule().getEntry(binderId, entityId);
			Binder binder = getBinderModule().getBinder(binderId);
			Map tagResults = TagUtil.uniqueTags(getFolderModule().getTags(entry));
			model.put(WebKeys.COMMUNITY_TAGS, tagResults.get(ObjectKeys.COMMUNITY_ENTITY_TAGS));
			model.put(WebKeys.PERSONAL_TAGS, tagResults.get(ObjectKeys.PERSONAL_ENTITY_TAGS));
			model.put(WebKeys.ENTRY, entry);
			model.put(WebKeys.BINDER, binder);
		}
		model.put(WebKeys.NAMESPACE, namespace);
		model.put(WebKeys.TAG_DIV_NUMBER, tagDivNumber);
		response.setContentType("text/xml");
		return new ModelAndView("definition_elements/tag_view_ajax", model);
	}



	private ModelAndView ajaxGetFilterData(RenderRequest request, 
				RenderResponse response) throws Exception {
		Map model = new HashMap();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		String op2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
			
		String filterTermNumber = PortletRequestUtils.getStringParameter(request, WebKeys.FILTER_ENTRY_FILTER_TERM_NUMBER, "");
		model.put(WebKeys.FILTER_ENTRY_FILTER_TERM_NUMBER, filterTermNumber);
		String filterTermNumberMax = PortletRequestUtils.getStringParameter(request, WebKeys.FILTER_ENTRY_FILTER_TERM_NUMBER_MAX, "");
		model.put(WebKeys.FILTER_ENTRY_FILTER_TERM_NUMBER_MAX, filterTermNumberMax);
		
		//Get the definition id (if present)
		if (op.equals(WebKeys.OPERATION_GET_ENTRY_ELEMENTS) || 
				op.equals(WebKeys.OPERATION_GET_ELEMENT_VALUES) || 
				op.equals(WebKeys.OPERATION_GET_ELEMENT_VALUE_DATA)) {
			String defId = PortletRequestUtils.getStringParameter(request,WebKeys.FILTER_ENTRY_DEF_ID+filterTermNumber);
			if (Validator.isNotNull(defId)) {
				if (defId.equals("_common")) {
					model.put(WebKeys.FILTER_ENTRY_DEF_ID, "");
					Map elementData = BinderHelper.getCommonEntryElements();
					model.put(WebKeys.ENTRY_DEFINTION_ELEMENT_DATA, elementData);
				} else {
					model.put(WebKeys.FILTER_ENTRY_DEF_ID, defId);
					Map elementData = getDefinitionModule().getEntryDefinitionElements(defId);
					model.put(WebKeys.ENTRY_DEFINTION_ELEMENT_DATA, elementData);
				}
			}
		} else if (op.equals(WebKeys.OPERATION_GET_WORKFLOW_STATES)) {
			String defId = PortletRequestUtils.getStringParameter(request,WebKeys.FILTER_WORKFLOW_DEF_ID+filterTermNumber);
			if (Validator.isNotNull(defId)) {
				model.put(WebKeys.FILTER_WORKFLOW_DEF_ID, defId);
				Map stateData = getDefinitionModule().getWorkflowDefinitionStates(defId);
				model.put(WebKeys.WORKFLOW_DEFINITION_STATE_DATA, stateData);
			}
		}
		
		String elementName = PortletRequestUtils.getStringParameter(request, "elementName" + filterTermNumber, "");
		if (Validator.isNotNull(elementName)) {
			model.put(WebKeys.FILTER_ENTRY_ELEMENT_NAME, elementName);
		}

		
		response.setContentType("text/xml");
		if (op.equals(WebKeys.OPERATION_GET_FILTER_TYPE)) {
			//don't always have a binder
			Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
			Binder binder = getBinderModule().getBinder(binderId);
			model.put(WebKeys.BINDER, binder);
			model.put(WebKeys.FILTER_TYPE, op2);
			Map defaultEntryDefinitions = DefinitionHelper.getEntryDefsAsMap(binder);
			model.put(WebKeys.ENTRY_DEFINITION_MAP, defaultEntryDefinitions);
			List<Definition> wfs = getDefinitionModule().getDefinitions(binderId, Boolean.TRUE, Definition.WORKFLOW);
			Map definitions = new HashMap();
			for (Definition def:wfs) {
				definitions.put(def.getId(), def);
			}
			model.put(WebKeys.PUBLIC_WORKFLOW_DEFINITIONS, definitions);
			model.put(WebKeys.WORKFLOW_DEFINITION_MAP, model.get(WebKeys.PUBLIC_WORKFLOW_DEFINITIONS));
			return new ModelAndView("binder/get_filter_type", model);
		} else if (op.equals(WebKeys.OPERATION_GET_ENTRY_ELEMENTS)) {
			model.put(WebKeys.FILTER_TYPE, "entry");
			return new ModelAndView("binder/get_entry_elements", model);
		} else if (op.equals(WebKeys.OPERATION_GET_ELEMENT_VALUES)) {
			model.put(WebKeys.FILTER_TYPE, "entry");
			return new ModelAndView("binder/get_element_value", model);
		} else if (op.equals(WebKeys.OPERATION_GET_WORKFLOW_STATES)) {
			model.put(WebKeys.FILTER_TYPE, "workflow");
			return new ModelAndView("binder/get_entry_elements", model);
		} else {
			model.put(WebKeys.FILTER_VALUE_TYPE, PortletRequestUtils.getStringParameter(request, 
					"elementValueDateType" + filterTermNumber, ""));
			return new ModelAndView("binder/get_element_value_data", model);
		}
	}
	
	
	private ModelAndView ajaxGetConditionData(RenderRequest request, 
				RenderResponse response) throws Exception {
		Map model = new HashMap();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		//Get the definition id (if present)
		String defId = PortletRequestUtils.getStringParameter(request,WebKeys.CONDITION_ENTRY_DEF_ID, "");
		if (Validator.isNotNull(defId)) {
			model.put(WebKeys.CONDITION_ENTRY_DEF_ID, defId);
			Map elementData = getDefinitionModule().getEntryDefinitionElements(defId);
			model.put(WebKeys.ENTRY_DEFINTION_ELEMENT_DATA, elementData);
		}
		
		String name = PortletRequestUtils.getStringParameter(request, WebKeys.CONDITION_ELEMENT_NAME, "");
		if (Validator.isNotNull(name)) {
			model.put(WebKeys.CONDITION_ELEMENT_NAME, name);
		}
		
		String value = PortletRequestUtils.getStringParameter(request, WebKeys.CONDITION_ELEMENT_OPERATION, "");
		if (Validator.isNotNull(value)) {
			model.put(WebKeys.CONDITION_ELEMENT_OPERATION, value);
		}
		
		value = PortletRequestUtils.getStringParameter(request, WebKeys.CONDITION_ELEMENT_VALUE, null);
		if (Validator.isNotNull(value)) {
			model.put(WebKeys.CONDITION_ELEMENT_VALUE, value);
		}
		
		response.setContentType("text/xml");
		if (op.equals(WebKeys.OPERATION_GET_CONDITION_ENTRY_ELEMENTS)) {
			return new ModelAndView("definition_builder/get_condition_entry_element", model);
		} else if (op.equals(WebKeys.OPERATION_GET_CONDITION_ENTRY_DATA_ELEMENTS)) {
			return new ModelAndView("definition_builder/get_condition_entry_data_element", model);
		} else if (op.equals(WebKeys.OPERATION_GET_CONDITION_ENTRY_USER_LIST_ELEMENTS)) {
			return new ModelAndView("definition_builder/get_condition_entry_user_list_element", model);
		} else if (op.equals(WebKeys.OPERATION_GET_CONDITION_ENTRY_DATA_OPERATIONS)) {
			return new ModelAndView("definition_builder/get_condition_entry_data_element_operations", model);
		} else if (op.equals(WebKeys.OPERATION_GET_CONDITION_ENTRY_OPERATIONS)) {
			return new ModelAndView("definition_builder/get_condition_entry_element_operations", model);
		} else if (op.equals(WebKeys.OPERATION_GET_CONDITION_ENTRY_DATA_VALUE_LIST)) {
			return new ModelAndView("definition_builder/get_condition_entry_data_element_value", model);
		} else {
			return new ModelAndView("definition_builder/get_condition_entry_element_value", model);
		}
	}
	
	private ModelAndView ajaxGetEntryWorkflowStates(RenderRequest request, 
				RenderResponse response) throws Exception {
		Map model = new HashMap();
		String propertyId = PortletRequestUtils.getStringParameter(request,WebKeys.PROPERTY_ID, "");
		if (Validator.isNotNull(propertyId)) {
			String defId = PortletRequestUtils.getStringParameter(request, "propertyId_" + propertyId, "");
			if (Validator.isNotNull(defId)) {
				model.put(WebKeys.WORKFLOW_DEFINITION_ID, defId);
				model.put(WebKeys.PROPERTY_ID, propertyId);
				Map stateData = getDefinitionModule().getWorkflowDefinitionStates(defId);
				model.put(WebKeys.WORKFLOW_DEFINTION_STATE_DATA, stateData);
				model.put("previous_workflowDefinitionId", PortletRequestUtils.getStringParameter(request,"previous_workflowDefinitionId", ""));
				String[] previousStates = PortletRequestUtils.getStringParameters(request, "previous_workflowState");
				Map previousStatesList = new HashMap();
				if (previousStates != null) {
					for (int i = 0; i < previousStates.length; i++) {
						previousStatesList.put(previousStates[i], true);
					}
				}
				model.put("previous_workflowStates", previousStatesList);
			}
		}
		
		response.setContentType("text/xml");
		return new ModelAndView("definition_builder/get_entry_workflow_states", model);
	}
	
	private ModelAndView ajaxGetWorkspaceTree(RenderRequest request, 
				RenderResponse response) throws Exception {
		Map model = new HashMap();
		String op2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
		String selectType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TREE_SELECT_TYPE, "0");
		String selectId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TREE_SELECT_ID, "");
		String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
		model.put(WebKeys.NAMESPACE, namespace);
		String binderIdText = PortletRequestUtils.getStringParameter(request, "binderId", "");
		Long binderId = null;
		if (!binderIdText.equals("")) {
			try {
				int i = binderIdText.indexOf(".");
				if (i >= 0) {
					binderId = Long.valueOf(binderIdText.substring(0, i));
				} else {
					binderId = Long.valueOf(binderIdText);
				}
			} catch (NumberFormatException e) {
				// it's not id
			}
		}
		
		String indentKey = PortletRequestUtils.getStringParameter(request, "indentKey", "");
		String page = PortletRequestUtils.getStringParameter(request, "page", "");
		String pageNumber = "";
		int i = page.indexOf(DomTreeBuilder.PAGE_DELIMITER);
    	if (!page.equals("") && i >= 0) {
    		pageNumber = "." + page.substring(0, i);
    	}
		String treeName = PortletRequestUtils.getStringParameter(request, "treeName", "");
		String treeKey = PortletRequestUtils.getStringParameter(request, "treeKey", "");
		
		if (binderId != null) {
			model.put("ss_tree_binderId", binderId.toString());
			model.put("ss_tree_id", binderId.toString() + pageNumber);
		}
		
		model.put("ss_tree_treeName", treeName);
		model.put("ss_tree_showIdRoutine", PortletRequestUtils.getStringParameter(request, "showIdRoutine", ""));
		model.put("ss_tree_parentId", PortletRequestUtils.getStringParameter(request, "parentId", ""));
		model.put("ss_tree_bottom", PortletRequestUtils.getStringParameter(request, "bottom", ""));
		model.put("ss_tree_type", PortletRequestUtils.getStringParameter(request, "type", ""));
		model.put("ss_tree_showFullLineOnHover", PortletRequestUtils.getStringParameter(request, "showFullLineOnHover", "false"));

		model.put("ss_tree_indentKey", indentKey);
		model.put("ss_tree_topId", op2);
		model.put("ss_tree_select_id", "");
		model.put("ss_tree_select_type", selectType);
		if (selectType.equals("2")) {
			//multi select
			String[] joinedMultiSelect = PortletRequestUtils.getStringParameters(request, WebKeys.URL_TREE_MULTI_SELECT);
			
			List multiSelect = new ArrayList();
			
			if (joinedMultiSelect != null) {
				for (int j = 0; j < joinedMultiSelect.length; j++) {
					if (joinedMultiSelect[j] != null && !joinedMultiSelect[j].equals("")) {
						multiSelect.addAll(Arrays.asList(joinedMultiSelect[j].split(",")));
					}
				}
			}
			model.put("ss_tree_select", multiSelect);
			model.put("ss_tree_select_id", selectId);
			
		} else if (selectType.equals("1")) {
			//single select, get name and selectedId
			model.put("ss_tree_select_id", selectId);				
			model.put("ss_tree_select", PortletRequestUtils.getStringParameter(request, "select", ""));
		} 
		
		if (binderId != null) {
			Binder binder = getBinderModule().getBinder(binderId);
			Document tree;
			tree = getBinderModule().getDomBinderTree(binder.getId(), 
						new WsDomTreeBuilder(binder, true, this, treeKey, page),1);
			model.put(WebKeys.WORKSPACE_DOM_TREE, tree);
		}
		
		String view = "tag_jsps/tree/get_tree_div";
		response.setContentType("text/xml");
		return new ModelAndView(view, model);
	}
	
	
	private void ajaxUploadImageFile(ActionRequest request, 
			ActionResponse response) throws Exception {
		// Get a handle on the uploaded file
		String fileHandle = WebHelper.getFileHandleOnUploadedFile(request);
		if (fileHandle != null) {
			// Create a URL containing the handle
			String url = WebUrlUtil.getServletRootURL(request) + WebKeys.SERVLET_VIEW_FILE + "?" +
			WebKeys.URL_FILE_VIEW_TYPE + "=" + WebKeys.FILE_VIEW_TYPE_UPLOAD_FILE + 
			"&" + WebKeys.URL_FILE_ID + "=" + Http.encodeURL(fileHandle); 
			response.setRenderParameter(WebKeys.IMAGE_FILE_URL, url);
		}
	
		// And then, here's what you need to do at the time you create an entry.
		
		// You can use WebHelper.wrapFileHandleInMultipartFile(fileHandle) method
		// to create a MultipartFile instance from the file handle and then put
		// it into a map. Then you can pass it (along with other stuff) to 
		// addEntry method to create an entry with file attachment, etc. 
		
		// When you're done creating an entry, make sure to call 
		// WebHelper.releaseFileHandle(fileHandle) method to release system 
		// resources associated with the file handle. 
	}
	
	private void ajaxUploadICalendarFile(ActionRequest request, 
			ActionResponse response, boolean gwtRequest) throws Exception {
		// Get a handle on the uploaded file
		AttendedEntries attendedEntries = new AttendedEntries();
		String fileHandle = WebHelper.getFileHandleOnUploadedCalendarFile(request);
		if (fileHandle != null) {
			MultipartFile file = WebHelper.wrapFileHandleInMultipartFile(fileHandle);
			Long folderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_FOLDER_ID, -1);
			if (folderId != -1) {
				try {
					attendedEntries = getIcalModule().parseToEntries(folderId, file.getInputStream());
				} catch (net.fortuna.ical4j.data.ParserException e) {
					response.setRenderParameter("ssICalendarException", (gwtRequest ? e.getLocalizedMessage() : "parseException"));
				}
			}
			WebHelper.releaseFileHandle(fileHandle);
		}
		response.setRenderParameter("ssICalendarEntryAddedIdsSize", Integer.toString(attendedEntries.added.size()));
		response.setRenderParameter("ssICalendarEntryModifiedIdsSize", Integer.toString(attendedEntries.modified.size()));
		
		Set addedEntriesIdsAsStrings = LongIdUtil.getIdsAsStringSet(attendedEntries.added);
		String[] ids = new String[addedEntriesIdsAsStrings.size()];
		ids = (String[])addedEntriesIdsAsStrings.toArray(ids);
		response.setRenderParameter("ssICalendarAddedEntryIds", ids);
		
		Set modifiedEntriesIdsAsStrings = LongIdUtil.getIdsAsStringSet(attendedEntries.modified);
		ids = new String[modifiedEntriesIdsAsStrings.size()];
		ids = (String[])modifiedEntriesIdsAsStrings.toArray(ids);
		response.setRenderParameter("ssICalendarModifiedEntryIds", ids);
		
	}
	
	private void ajaxDesktopBrandingMac(ActionRequest request, ActionResponse response) throws Exception {
		String fileHandle = WebHelper.getFileHandleOnUploadedSiteBrandingFile(request);
		if (fileHandle != null) {
			MultipartFile file = WebHelper.wrapFileHandleInMultipartFile(fileHandle);
//!			...this needs to be implemented...
			WebHelper.releaseFileHandle(fileHandle);
		}
	}
	
	private void ajaxDesktopBrandingWindows(ActionRequest request, ActionResponse response) throws Exception {
		String fileHandle = WebHelper.getFileHandleOnUploadedSiteBrandingFile(request);
		if (fileHandle != null) {
			MultipartFile file = WebHelper.wrapFileHandleInMultipartFile(fileHandle);
//!			...this needs to be implemented...
			WebHelper.releaseFileHandle(fileHandle);
		}
	}
	
	private void ajaxMobileBrandingAndroid(ActionRequest request, ActionResponse response) throws Exception {
		String fileHandle = WebHelper.getFileHandleOnUploadedSiteBrandingFile(request);
		if (fileHandle != null) {
			MultipartFile file = WebHelper.wrapFileHandleInMultipartFile(fileHandle);
//!			...this needs to be implemented...
			WebHelper.releaseFileHandle(fileHandle);
		}
	}
	
	private void ajaxMobileBrandingIos(ActionRequest request, ActionResponse response) throws Exception {
		String fileHandle = WebHelper.getFileHandleOnUploadedSiteBrandingFile(request);
		if (fileHandle != null) {
			MultipartFile file = WebHelper.wrapFileHandleInMultipartFile(fileHandle);
//!			...this needs to be implemented...
			WebHelper.releaseFileHandle(fileHandle);
		}
	}
	
	private void ajaxMobileBrandingWindows(ActionRequest request, ActionResponse response) throws Exception {
		String fileHandle = WebHelper.getFileHandleOnUploadedSiteBrandingFile(request);
		if (fileHandle != null) {
			MultipartFile file = WebHelper.wrapFileHandleInMultipartFile(fileHandle);
//!			...this needs to be implemented...
			WebHelper.releaseFileHandle(fileHandle);
		}
	}
	
	private void ajaxLoadICalendarByURL(ActionRequest request, 
			ActionResponse response) throws Exception {
		AttendedEntries attendedEntries = new AttendedEntries();
		
		Long folderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_FOLDER_ID, -1);
		String iCalURL = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ICAL_URL, null);
		
		if (folderId != -1) {
			GetMethod getMethod = null;
			try {
				HttpURL hrl = getHttpURL(iCalURL);
				HttpClient httpClient = getHttpClient(hrl);
				getMethod = new GetMethod(hrl.getPathQuery());
				int statusCode = httpClient.executeMethod(getMethod);
				if (statusCode == 200) {
					// Get the response as an InputStream.
					InputStream icalInputStream = getMethod.getResponseBodyAsStream();
					try {
						attendedEntries = getIcalModule().parseToEntries(folderId, icalInputStream);
					} catch (net.fortuna.ical4j.data.ParserException e) {
						response.setRenderParameter("ssICalendarException", "parseException");
					}
					icalInputStream.close();
				} else {
					response.setRenderParameter("ssICalendarException", "wrongURL");
				}
			} catch(Exception e){
				logger.error("AjaxController.ajaxLoadICalendarByURL():  Get iCalendar by URL [" + iCalURL + "].", e);
				response.setRenderParameter("ssICalendarException", "wrongURL");
			} finally{
				//release the connection
				if (null != getMethod) {
					getMethod.releaseConnection();
				}
			}
		}
			
		response.setRenderParameter("ssICalendarEntryAddedIdsSize", Integer.toString(attendedEntries.added.size()));
		response.setRenderParameter("ssICalendarEntryModifiedIdsSize", Integer.toString(attendedEntries.modified.size()));
		
		Set addedEntriesIdsAsStrings = LongIdUtil.getIdsAsStringSet(attendedEntries.added);
		String[] ids = new String[addedEntriesIdsAsStrings.size()];
		ids = (String[])addedEntriesIdsAsStrings.toArray(ids);
		response.setRenderParameter("ssICalendarAddedEntryIds", ids);
		
		Set modifiedEntriesIdsAsStrings = LongIdUtil.getIdsAsStringSet(attendedEntries.modified);
		ids = new String[modifiedEntriesIdsAsStrings.size()];
		ids = (String[])modifiedEntriesIdsAsStrings.toArray(ids);
		response.setRenderParameter("ssICalendarModifiedEntryIds", ids);
		
	}
	
	private ModelAndView ajaxUploadICalendarFileStatus(RenderRequest request, RenderResponse response, boolean gwtRequest) {
		int entriesAddedAmount = PortletRequestUtils.getIntParameter(request, "ssICalendarEntryAddedIdsSize", 0);
		int entriesModifiedAmount = PortletRequestUtils.getIntParameter(request, "ssICalendarEntryModifiedIdsSize", 0);
		long[] entryAddedIds = PortletRequestUtils.getLongParameters(request, "ssICalendarAddedEntryIds");
		long[] entryModifiedIds = PortletRequestUtils.getLongParameters(request, "ssICalendarModifiedEntryIds");
		String exception = PortletRequestUtils.getStringParameter(request, "ssICalendarException", "");
		
		
		Map model = new HashMap();
		model.put("entriesAddedAmount", entriesAddedAmount);
		model.put("entriesModifiedAmount", entriesModifiedAmount);
		model.put("entryAddedIds", entryAddedIds);
		model.put("entryModifiedIds", entryModifiedIds);
		model.put("exception", exception);
		
		response.setContentType("text/html");
		String jsp = gwtRequest ? "forum/json/icalendar_upload_gwt" : "forum/json/icalendar_upload";
		return new ModelAndView(jsp, model);
	}
	
	private ModelAndView ajaxLoadICalendarByURL(RenderRequest request, RenderResponse response) {
		int entriesAddedAmount = PortletRequestUtils.getIntParameter(request, "ssICalendarEntryAddedIdsSize", 0);
		int entriesModifiedAmount = PortletRequestUtils.getIntParameter(request, "ssICalendarEntryModifiedIdsSize", 0);
		long[] entryAddedIds = PortletRequestUtils.getLongParameters(request, "ssICalendarAddedEntryIds");
		long[] entryModifiedIds = PortletRequestUtils.getLongParameters(request, "ssICalendarModifiedEntryIds");
		String exception = PortletRequestUtils.getStringParameter(request, "ssICalendarException", "");
		
		
		Map model = new HashMap();
		model.put("entriesAddedAmount", entriesAddedAmount);
		model.put("entriesModifiedAmount", entriesModifiedAmount);
		model.put("entryAddedIds", entryAddedIds);
		model.put("entryModifiedIds", entryModifiedIds);
		model.put("exception", exception);
		
		response.setContentType("text/json");
		return new ModelAndView("forum/json/icalendar_import", model);
	}
	
	private void ajaxSaveCalendarConfiguration(ActionRequest request, 
			ActionResponse response) throws Exception {
		int weekFirstDay = PortletRequestUtils.getIntParameter(request, "weekFirstDay", CalendarHelper.getFirstDayOfWeek());
		int workDayStart = PortletRequestUtils.getIntParameter(request, "workDayStart", 8);
				
		if (weekFirstDay < 1 || weekFirstDay > 7) {
			weekFirstDay = CalendarHelper.getFirstDayOfWeek();
		}
		
		if (workDayStart < 0 && workDayStart > 12) {
			workDayStart = 6;
		}
		
		User user = RequestContextHolder.getRequestContext().getUser();
		UserProperties userProperties = getProfileModule().getUserProperties(user.getId());

		Integer weekFirstDayOld = (Integer)userProperties.getProperty(ObjectKeys.USER_PROPERTY_CALENDAR_FIRST_DAY_OF_WEEK);
		if (weekFirstDayOld == null || weekFirstDay != weekFirstDayOld) {
			getProfileModule().setUserProperty(user.getId(), ObjectKeys.USER_PROPERTY_CALENDAR_FIRST_DAY_OF_WEEK, weekFirstDay);
		}

		Integer workDayStartOld = (Integer)userProperties.getProperty(ObjectKeys.USER_PROPERTY_CALENDAR_WORK_DAY_START);
		if (workDayStartOld == null || workDayStart != workDayStartOld) {
			getProfileModule().setUserProperty(user.getId(), ObjectKeys.USER_PROPERTY_CALENDAR_WORK_DAY_START, workDayStart);
		}
	}

	private ModelAndView ajaxSaveCalendarConfigurationStatus(RenderRequest request, RenderResponse response) {
		Map model = new HashMap();
		return new ModelAndView("forum/json/calendar_config", model);
	}	
	
	private ModelAndView ajaxGetChangeLogEntryForm(RenderRequest request, RenderResponse response) {
		Map model = new HashMap();
		String binderId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
		model.put(WebKeys.BINDER_ID, binderId);
		model.put(WebKeys.PROFILES_BINDER_ID, getProfileModule().getProfileBinderId().toString());
		return new ModelAndView("administration/get_change_log_entry_form", model);
	}
	
	private void ajaxSetBinderOwnerId(ActionRequest request, 
			ActionResponse response) throws Exception {
		String ownerId = PortletRequestUtils.getStringParameter(request, "ownerId", "");
		if (Validator.isNull(ownerId)) return;
		WorkArea workArea = null;
		Long workAreaId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_WORKAREA_ID));				
		String type = PortletRequestUtils.getStringParameter(request, WebKeys.URL_WORKAREA_TYPE);	
		if (EntityIdentifier.EntityType.valueOf(type).isBinder()) {
			workArea = getBinderModule().getBinder(workAreaId);
		} else {
			workArea = getZoneModule().getZoneConfig(workAreaId);

		}
		String sPropagate = PortletRequestUtils.getStringParameter(request, "propagate", "");
		boolean bPropagate = false;
		if (sPropagate.equals("on") || sPropagate.equals("true")) bPropagate = true;
		getAdminModule().setWorkAreaOwner(workArea, Long.valueOf(ownerId), bPropagate);
	}
	
	private void ajaxModifyGroup(ActionRequest request, 
			ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("applyBtn") || formData.containsKey("okBtn")) {
			Long groupId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID);
			String title = PortletRequestUtils.getStringParameter(request, "title", "", false);
			String description = PortletRequestUtils.getStringParameter(request, "description", "", false);
			Set ids = LongIdUtil.getIdsAsLongSet(request.getParameterValues("users"));
			ids.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("groups")));
			SortedSet principals = getProfileModule().getPrincipals(ids);
			Map updates = new HashMap();
			updates.put(ObjectKeys.FIELD_ENTITY_TITLE, title);
			updates.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION, description);
			updates.put(ObjectKeys.FIELD_GROUP_PRINCIPAL_MEMBERS, principals);
			getProfileModule().modifyEntry(groupId, new MapInputData(updates));
		}
	}
	
	private void ajaxStickyCalendarDisplaySettings(ActionRequest request, 
			ActionResponse response) throws PortletRequestBindingException {
		PortletSession portletSession = WebHelper.getRequiredPortletSession(request);
		User user = RequestContextHolder.getRequestContext().getUser();
		Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
		String calendarStickyId = PortletRequestUtils.getStringParameter(request, WebKeys.CALENDAR_STICKY_ID, String.valueOf(binderId) + "_");
		
		String eventType = PortletRequestUtils.getStringParameter(request, "eventType", "");
		if (!"".equals(eventType)) {
			EventsViewHelper.setCalendarDisplayEventType(this, user.getId(), binderId, eventType);
		}
		
		String gridType = PortletRequestUtils.getStringParameter(request, WebKeys.CALENDAR_GRID_TYPE, "");
		if (!"".equals(gridType)) {
			int gridSize = PortletRequestUtils.getIntParameter(request, WebKeys.CALENDAR_GRID_SIZE, -1);
			
			UserProperties userProperties = getProfileModule().getUserProperties(user.getId());
			
			Map grids = EventsViewHelper.setCalendarGrid(portletSession, userProperties, calendarStickyId, gridType, gridSize);
			
			getProfileModule().setUserProperty(user.getId(), WebKeys.CALENDAR_GRID_TYPE, ((EventsViewHelper.Grid)grids.get(calendarStickyId)).type);
			getProfileModule().setUserProperty(user.getId(), WebKeys.CALENDAR_GRID_SIZE, ((EventsViewHelper.Grid)grids.get(calendarStickyId)).size);
		}
		
		String dayViewType = PortletRequestUtils.getStringParameter(request, "dayViewType", "");
		if (!"".equals(dayViewType)) {
			EventsViewHelper.setCalendarDayViewType(portletSession, dayViewType);
		}
		
		int year = PortletRequestUtils.getIntParameter(request, WebKeys.URL_DATE_YEAR, -1);
		int month = PortletRequestUtils.getIntParameter(request, WebKeys.URL_DATE_MONTH, -1);
		int dayOfMonth = PortletRequestUtils.getIntParameter(request, WebKeys.URL_DATE_DAY_OF_MONTH, -1);
		Date currentDate = EventsViewHelper.getCalendarCurrentDate(portletSession);
		currentDate = EventsViewHelper.getDate(year, month, dayOfMonth, currentDate);
		EventsViewHelper.setCalendarCurrentDate(portletSession, currentDate);
	}
	

	private ModelAndView ajaxGetBlogReplies(RenderRequest request, 
				RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		User user = RequestContextHolder.getRequestContext().getUser();
		
		String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
		model.put(WebKeys.NAMESPACE, namespace);
		
		FolderEntry entry = null;
		Map folderEntries = null;
		folderEntries  = getFolderModule().getEntryTree(binderId, entryId);
		if (folderEntries != null) {
			entry = (FolderEntry)folderEntries.get(ObjectKeys.FOLDER_ENTRY);
			BinderHelper.setAccessControlForAttachmentList(this, model, entry, user);
			Map accessControlMap = (Map) model.get(WebKeys.ACCESS_CONTROL_MAP);
			HashMap entryAccessMap = BinderHelper.getEntryAccessMap(this, model, entry);
			model.put(WebKeys.ENTRY, entry);
			model.put(WebKeys.FOLDER_ENTRY_DESCENDANTS, folderEntries.get(ObjectKeys.FOLDER_ENTRY_DESCENDANTS));
			model.put(WebKeys.FOLDER_ENTRY_ANCESTORS, folderEntries.get(ObjectKeys.FOLDER_ENTRY_ANCESTORS));
			if (DefinitionHelper.getDefinition(entry.getEntryDefDoc(), model, "//item[@name='entryBlogView']") == false) {
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
			if (!seen.checkIfSeen(entry)) { //only mark top entries as seen
				getProfileModule().setSeen(null, entry);
			}
		}
		return new ModelAndView("definition_elements/blog/view_blog_replies_content", model);
	}
	
	private ModelAndView ajaxGetMyTeams(RenderRequest request, 
			RenderResponse response) throws Exception {
	Map model = new HashMap();
	User user = RequestContextHolder.getRequestContext().getUser();
	Collection myTeams = getBinderModule().getTeamMemberships(user.getId(), null);
	model.put(WebKeys.MY_TEAMS, myTeams);
	String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
	model.put(WebKeys.NAMESPACE, namespace);
	return new ModelAndView("forum/my_teams", model);
}

	private ModelAndView ajaxGetEntryRating(RenderRequest request, 
			RenderResponse response) throws Exception {
	Map model = new HashMap();
	String ratingDivId = PortletRequestUtils.getStringParameter(request, "ratingDivId");
	model.put(WebKeys.RATING_DIV_ID, ratingDivId);
	Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
	Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
	Entry entry = getFolderModule().getEntry(binderId, entryId);
	if (entry != null) {
		model.put(WebKeys.DEFINITION_ENTRY, entry);
	}
	String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
	model.put(WebKeys.NAMESPACE, namespace);
	response.setContentType("text/xml");
	return new ModelAndView("forum/rating_return", model);
}

	private ModelAndView ajaxShowHelpPanel(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		User user = RequestContextHolder.getRequestContext().getUser();
		model.put(WebKeys.USER_PRINCIPAL, user);
		String helpPanelId = PortletRequestUtils.getStringParameter(request, 
				WebKeys.HELP_PANEL_ID, "ss_help_panel_id");
		model.put(WebKeys.HELP_PANEL_ID, helpPanelId);
		UserProperties folderProps = getProfileModule().getUserProperties(null);
		Boolean showHelpCPanel = (Boolean) folderProps.getProperty(ObjectKeys.USER_PROPERTY_HELP_CPANEL_SHOW);
		if (showHelpCPanel == null) showHelpCPanel = Boolean.TRUE;
		model.put(WebKeys.HELP_CPANEL_SHOW, showHelpCPanel);
		
		
		String op2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
		String tagId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TAG_ID, "");
		String jsp = "";
		if (op2.equals("")) {
			jsp = "/WEB-INF/jsp/tag_jsps/inline_help/tag_ajax.jsp";
			if (tagId.equals("")) tagId = "help.globalStrings.noHelp";
		} else {
			//See if the site has overridden the mapping for this help page
			jsp = SPropsUtil.getString("help_system." + op2, "");
			if (jsp.equals("")) {
				//There is no override; use the id as the jsp name directly
				jsp = "/WEB-INF/jsp/help/" + op2 + ".jsp";
			}
		}
		model.put(WebKeys.HELP_PANEL_JSP, jsp);
		model.put(WebKeys.HELP_PANEL_TAG, tagId);

		//Put in the product name
		model.put(WebKeys.PRODUCT_NAME, SPropsUtil.getString("product.name", ObjectKeys.PRODUCT_NAME_DEFAULT));
		model.put(WebKeys.PRODUCT_TITLE, SPropsUtil.getString("product.title", ObjectKeys.PRODUCT_TITLE_DEFAULT));
		model.put(WebKeys.PRODUCT_CONFERENCING_NAME, SPropsUtil.getString("product.conferencing.name", ObjectKeys.PRODUCT_CONFERENCING_NAME_DEFAULT));
		model.put(WebKeys.PRODUCT_CONFERENCING_TITLE, SPropsUtil.getString("product.conferencing.title", ObjectKeys.PRODUCT_CONFERENCING_TITLE_DEFAULT));
		
		return new ModelAndView("forum/help_panel", model);
	}

	
	
	private ModelAndView ajaxGetAccessControlTable(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		WorkArea workArea = null;
		Long workAreaId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_WORKAREA_ID));				
		String type = PortletRequestUtils.getStringParameter(request, WebKeys.URL_WORKAREA_TYPE);	
		if (EntityIdentifier.EntityType.valueOf(type).isBinder()) {
			workArea = getBinderModule().getBinder(workAreaId);
			model.put(WebKeys.ACCESS_CONTROL_CONFIGURE_ALLOWED, 
					getAdminModule().testAccess(workArea, AdminOperation.manageFunctionMembership));
		} else if (EntityIdentifier.EntityType.folderEntry.name().equals(type)) {
			FolderEntry entry = getFolderModule().getEntry(null, workAreaId);
			workArea = entry;
			model.put(WebKeys.ENTRY_HAS_ENTRY_ACL, entry.hasEntryAcl());
			model.put(WebKeys.ACCESS_SUPER_USER, AccessUtils.getZoneSuperUser(entry.getZoneId()));
			if (entry.hasEntryAcl()) {
				model.put(WebKeys.ACCESS_CONTROL_CONFIGURE_ALLOWED, getAdminModule().testAccess(entry, AdminOperation.manageFunctionMembership));
						
			} else {
				model.put(WebKeys.ACCESS_CONTROL_CONFIGURE_ALLOWED, 
						getAdminModule().testAccess(entry.getParentBinder(), AdminOperation.manageFunctionMembership));
			}
		} else {
			workArea = getZoneModule().getZoneConfig(workAreaId);
			model.put(WebKeys.ACCESS_CONTROL_CONFIGURE_ALLOWED, 
					getAdminModule().testAccess(workArea, AdminOperation.manageFunctionMembership));

		}
		AccessControlController.setupAccess(this, request, response, workArea, model);
		
		// User context
		User user = RequestContextHolder.getRequestContext().getUser();
		model.put(WebKeys.USER_PRINCIPAL, user);
		String namespace = PortletRequestUtils.getStringParameter(request, "namespace", "");
		model.put(WebKeys.NAMESPACE, namespace);
		response.setContentType("text/xml");
		return new ModelAndView("binder/access_control_ajax", model);
	}

	private ModelAndView ajaxGetUploadImageFile(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String url = PortletRequestUtils.getStringParameter(request, WebKeys.IMAGE_FILE_URL, "");
		model.put(WebKeys.UPLOAD_FILE_URL, url);

		return new ModelAndView("binder/upload_image_file_ajax_return", model);
	}
	
	private ModelAndView addAttachmentOptions(RenderRequest request, 
			RenderResponse response) throws Exception {
	
		String namespace = PortletRequestUtils.getStringParameter(request, "namespace", "");
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		Binder binder = null;
		if (binderId != null) {
			binder = getBinderModule().getBinder(binderId);
		}
		
		AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", Boolean.parseBoolean("true"));
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_ENTRY_ATTACHMENT);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
		adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_FILES_FROM_APPLET);

		//This replace has been done AJAX does not allow "&"
		String strURL = adapterUrl.toString();
		strURL = strURL.replaceAll("&", "&amp;");
		
		AdaptedPortletURL adapterUrl2 = new AdaptedPortletURL(request, "ss_forum", Boolean.parseBoolean("true"));
		adapterUrl2.setParameter(WebKeys.ACTION, WebKeys.ACTION_AJAX_REQUEST);
		adapterUrl2.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		adapterUrl2.setParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
		adapterUrl2.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_CHECK_EXISTS_FILES_FROM_APPLET);

		//This replace has been done AJAX does not allow "&"
		String strURL2 = adapterUrl2.toString();
		strURL2 = strURL2.replaceAll("&", "&amp;");
		
		Map model = new HashMap();
		model.put(WebKeys.NAMESPACE, namespace);
		model.put(WebKeys.BINDER_ID, binderId);
		model.put(WebKeys.ENTRY_ID, entryId);
		model.put(WebKeys.ENTRY_ATTACHMENT_FILE_RECEIVER_URL, strURL);
		model.put(WebKeys.ENTRY_ATTACHMENT_FILE_CHECK_EXISTS_URL, strURL2);
		model.put(WebKeys.IS_APPLET_CHUNKED_STREAMING_MODE_SUPPORTED, 
				SPropsUtil.getString("applet.is.chunked.streaming.mode.supported", "yes"));
		if (SPropsUtil.getBoolean("preauthentication.by.iis.enable", false)) {
			//If running in IIS, always turn off chunked mode.
			model.put(WebKeys.IS_APPLET_CHUNKED_STREAMING_MODE_SUPPORTED, "no");
		}

		if (binder != null) {
			Long maxFileSize = getBinderModule().getBinderMaxFileSize(binder);
			Long maxUserFileSize = getAdminModule().getUserFileSizeLimit();
			if (maxFileSize != null || maxUserFileSize != null) {
				if (maxFileSize == null) {
					maxFileSize = maxUserFileSize;
				}
				if (maxUserFileSize != null && maxUserFileSize < maxFileSize) {
					maxFileSize = maxUserFileSize;
				}
				//Get bytes
				model.put(WebKeys.BINDER_FILE_MAX_FILE_SIZE, String.valueOf(maxFileSize * ObjectKeys.MEGABYTES));
			}
		}
		
		//response.setContentType("text/xml");
		return new ModelAndView("definition_elements/entry_attachment_options", model);
	}
	
	private ModelAndView reloadEntryAttachment(RenderRequest request, 
			RenderResponse response) throws Exception {
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String entryId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		String namespace = PortletRequestUtils.getStringParameter(request, "namespace", "");

		Map model = new HashMap();
		FolderEntry entry = null;
		Binder binder = getBinderModule().getBinder(folderId);
		model.put((WebKeys.BINDER), binder);

		// User context
		User user = RequestContextHolder.getRequestContext().getUser();
		model.put(WebKeys.USER_PRINCIPAL, user);


		if (!entryId.equals("")) {
			entry  = getFolderModule().getEntry(folderId, Long.valueOf(entryId));
		}
		
		model.put(WebKeys.NAMESPACE, namespace);
		model.put(WebKeys.ENTRY, entry);
		model.put(WebKeys.DEFINITION_ENTRY, entry);

		BinderHelper.setAccessControlForAttachmentList(this, model, entry, user);
		
		response.setContentType("text/xml");
		return new ModelAndView("definition_elements/view_entry_attachments_ajax", model);
	}
	
	private ModelAndView openWebDAVFile(RenderRequest request, 
			RenderResponse response) throws Exception {
		String namespace = PortletRequestUtils.getStringParameter(request, "namespace", "");
		String strURLValue = PortletRequestUtils.getStringParameter(request, WebKeys.ENTRY_ATTACHMENT_URL, "");
		String strOSInfo = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OS_INFO, "");
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));
		String strOpenInEditor = SsfsUtil.openInEditor(strURLValue, strOSInfo, getProfileModule().getUserProperties(null));
		
		Map model = new HashMap();
		model.put(WebKeys.NAMESPACE, namespace);
		model.put(WebKeys.ENTRY_ID, entryId);
		model.put(WebKeys.ENTRY_ATTACHMENT_URL, strURLValue);
		model.put(WebKeys.ENTRY_ATTACHMENT_EDITOR_TYPE, strOpenInEditor);
		model.put(WebKeys.URL_OS_INFO, strOSInfo);
        model.put(WebKeys.IS_LICENSE_REQUIRED_EDITION, Boolean.toString(ReleaseInfo.isLicenseRequiredEdition()));
        model.put(WebKeys.IS_OFFICE_ADD_IN_ALLOWED, (!Utils.checkIfFilr() && !Utils.checkIfIPrint()));
        model.put(WebKeys.USER_PRINCIPAL, RequestContextHolder.getRequestContext().getUser());

		return new ModelAndView("definition_elements/view_entry_openfile", model);
	}
	
	private ModelAndView openWebDAVFileUsingFileId(RenderRequest request, 
			RenderResponse response) throws Exception {
		String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
		String fileId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_ID, "");
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));
		String strOSInfo = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OS_INFO, "");
		
		Entry entry = getFolderModule().getEntry(binderId, entryId);
		Binder binder = entry.getParentBinder();
		FileAttachment topAtt = (FileAttachment)entry.getAttachment(fileId);
		String url = SsfsUtil.getInternalAttachmentUrlEncoded(request, binder, entry, topAtt);
		url = url.replaceAll("\\+", "%20"); 
		String strOpenInEditor = SsfsUtil.openInEditor(url, strOSInfo, getProfileModule().getUserProperties(null));
		
		Map model = new HashMap();
		model.put(WebKeys.NAMESPACE, namespace);
		model.put(WebKeys.ENTRY_ID, entryId);
		model.put(WebKeys.ENTRY_ATTACHMENT_URL, url);
		model.put(WebKeys.ENTRY_ATTACHMENT_EDITOR_TYPE, strOpenInEditor);
		model.put(WebKeys.URL_OS_INFO, strOSInfo);
        model.put(WebKeys.IS_LICENSE_REQUIRED_EDITION, Boolean.toString(ReleaseInfo.isLicenseRequiredEdition()));
        model.put(WebKeys.IS_OFFICE_ADD_IN_ALLOWED, (!Utils.checkIfFilr() && !Utils.checkIfIPrint()));
        model.put(WebKeys.USER_PRINCIPAL, RequestContextHolder.getRequestContext().getUser());

		return new ModelAndView("definition_elements/view_entry_openfile", model);
	}
	
	private ModelAndView addFolderAttachmentOptions(RenderRequest request, 
			RenderResponse response) throws Exception {
	
		String namespace = PortletRequestUtils.getStringParameter(request, "namespace", "");
		String library = PortletRequestUtils.getStringParameter(request, "library", "");
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Binder binder = null;
		if (binderId != null) {
			binder = getBinderModule().getBinder(binderId);
		}
		
		AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", Boolean.parseBoolean("true"));
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_ATTACHMENT);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		adapterUrl.setParameter(WebKeys.URL_IS_LIBRARY_BINDER, library);
		adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_FOLDER_FILES_FROM_APPLET);

		AdaptedPortletURL adapterFolderRefreshUrl = new AdaptedPortletURL(request, "ss_forum", Boolean.parseBoolean("true"));
		adapterFolderRefreshUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_RELOAD_OPENER);
		adapterFolderRefreshUrl.setParameter(WebKeys.FOLDER_APPLET_RELOAD, "yes");
		
		//This replace has been done AJAX does not allow "&"
		String strURL = adapterUrl.toString();
		strURL = strURL.replaceAll("&", "&amp;");
		
		AdaptedPortletURL adapterUrl2 = new AdaptedPortletURL(request, "ss_forum", Boolean.parseBoolean("true"));
		adapterUrl2.setParameter(WebKeys.ACTION, WebKeys.ACTION_AJAX_REQUEST);
		adapterUrl2.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		adapterUrl2.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_CHECK_EXISTS_FILES_FROM_APPLET);

		//This replace has been done AJAX does not allow "&"
		String strURL2 = adapterUrl2.toString();
		strURL2 = strURL2.replaceAll("&", "&amp;");
		
		//This replace has been done AJAX does not allow "&"
		String strRefreshURL = adapterFolderRefreshUrl.toString();
		//strRefreshURL = strRefreshURL.replaceAll("&", "&amp;");
		
		Map model = new HashMap();
		model.put(WebKeys.NAMESPACE, namespace);
		model.put(WebKeys.BINDER_IS_LIBRARY, library);
		model.put(WebKeys.BINDER_ID, binderId);
		model.put(WebKeys.FOLDER_ATTACHMENT_FILE_RECEIVER_URL, strURL);
		model.put(WebKeys.FOLDER_ATTACHMENT_FILE_CHECK_EXISTS_URL, strURL2);
		model.put(WebKeys.FOLDER_ATTACHMENT_APPLET_REFRESH_URL, strRefreshURL);
		model.put(WebKeys.IS_APPLET_CHUNKED_STREAMING_MODE_SUPPORTED, 
				SPropsUtil.getString("applet.is.chunked.streaming.mode.supported", "yes"));
		if (SPropsUtil.getBoolean("preauthentication.by.iis.enable", false)) {
			//If running in IIS, always turn off chunked mode.
			model.put(WebKeys.IS_APPLET_CHUNKED_STREAMING_MODE_SUPPORTED, "no");
		}
		if (binder != null) {
			Long maxFileSize = getBinderModule().getBinderMaxFileSize(binder);
			Long maxUserFileSize = getAdminModule().getUserFileSizeLimit();
			if (maxFileSize != null || maxUserFileSize != null) {
				if (maxFileSize == null) {
					maxFileSize = maxUserFileSize;
				}
				if (maxUserFileSize != null && maxUserFileSize < maxFileSize) {
					maxFileSize = maxUserFileSize;
				}
				//Get bytes
				model.put(WebKeys.BINDER_FILE_MAX_FILE_SIZE, String.valueOf(maxFileSize * ObjectKeys.MEGABYTES));
			}
		}
		
		//response.setContentType("text/xml");
		return new ModelAndView("definition_elements/folder_dropbox_add_attachments", model);
	}
	
	private ModelAndView ajaxStartMeeting(RenderRequest request, 
			RenderResponse response, MeetingType meetingType) throws Exception {
		Map model = new HashMap();
		String meetingUrl = "";

		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		String entryId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		
		SortedSet<User> users = getProfileModule().getUsers( LongIdUtil.getIdsAsLongSet(request.getParameterValues("users")));
		
		Binder binder = null;
		if (binderId != null) {
			binder = getBinderModule().getBinder(binderId);
		}
		Entry entry = null;
		if (Validator.isNotNull(entryId)) {
			entry = getFolderModule().getEntry(binderId, Long.valueOf(entryId));
		}
		
		User user = RequestContextHolder.getRequestContext().getUser();
		
		ConferencingModule conferencingService = getConferencingModule();
		if (conferencingService != null && conferencingService.isEnabled()) {
			try {
				// Make sure we are logged in first
				String conferencingID = "";
				String conferencingPwd = "";
				CustomAttribute ca = user.getCustomAttribute("conferencingID");
				if (ca != null) {
					conferencingID = (String)ca.getValue(); 
				}
				ca = user.getCustomAttribute("conferencingPwd");
				if (ca != null) {
					conferencingPwd = (String)ca.getValue();
				}
				conferencingService.login(conferencingID, conferencingPwd);
				
				MeetingInfo info = new MeetingInfo();
				info.setType(meetingType);

				for (User participant: users) {
					if (participant != null) {
						info.addParticipant(participant.getName(), participant.getEmailAddress());
					}
				}

				String displayName = Utils.getUserTitle(user);
				info.setHostDisplayName(displayName);

		    	String meetingKey = PortletRequestUtils.getStringParameter(request, "meeting_password", "");
		    	info.setMeetingPassword(meetingKey);

		    	if (meetingType == MeetingType.Adhoc) {
		    		if (conferencingService.isMeetingRunning(conferencingID)) {
		    			logger.error("Ending meeting for user" + conferencingID);
		    			conferencingService.endMeeting(conferencingID);
		    		}
					meetingUrl = conferencingService.startMeeting(conferencingID, info);
		    	} else {
		    		String name = PortletRequestUtils.getStringParameter(request, "meeting_name", "");
		    		info.setTitle(name);

		    		String agenda = PortletRequestUtils.getStringParameter(request, "meeting_agenda", "");
		    		info.setAgenda(agenda);

		    		String startDate = PortletRequestUtils.getStringParameter(request, "meeting_start_date", "");
		    		info.setStartDate(startDate);

		    		String startTime = PortletRequestUtils.getStringParameter(request, "meeting_start_time", "");
		    		info.setStartTime(startTime);

		    		String hours = PortletRequestUtils.getStringParameter(request, "meeting_length_hours", "");
		    		String minutes = PortletRequestUtils.getStringParameter(request, "meeting_length_minutes", "");
		    		info.setDuration(Integer.valueOf(hours) * 60 + Integer.valueOf(minutes));
		    		
		    		String recurrance = PortletRequestUtils.getStringParameter(request, "meeting_repeat_option", "");
		    		info.setRecurrance(MeetingRecurrance.None);
		    		if (recurrance.equals("daily")) {
		    			info.setRecurrance(MeetingRecurrance.Daily);
		    		} else if (recurrance.equals("weekly")) {
		    			info.setRecurrance(MeetingRecurrance.Weekly);
		    		} else if (recurrance.equals("monthly")) {
		    			info.setRecurrance(MeetingRecurrance.Monthly);
		    		}
		    		
		    		conferencingService.scheduleMeeting(conferencingID, info);
		    	}
			} catch (ConferencingException ex) {
				logger.error(ex);
				String message;
				switch (ex.getErrorCode()) {
				case ConferencingException.AUTH_FAILED:
					message = NLT.get("meeting.credentials.invalid");
					break;
				default:
					message = NLT.get("meeting.start.error");
				}
				model.put(WebKeys.MEETING_ERROR, message);
				response.setContentType("text/json");
				return new ModelAndView("forum/meeting_return", model);
			}
		}

		model.put(WebKeys.MEETING_TOKEN, JSONUtils.quote(meetingUrl));
		response.setContentType("text/json");
		return new ModelAndView("forum/meeting_return", model);
	}

	private ModelAndView ajaxGetTeamMembers(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		
		Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
		
		if (WebHelper.isUserLoggedIn(request)) {
			try {
				Binder binder = getBinderModule().getBinder(binderId);
				model.put(WebKeys.TEAM_MEMBERS, getBinderModule().getTeamMembers(binder, false));
			} catch (AccessControlException ex) {
				model.put(WebKeys.TEAM_MEMBERS, Collections.emptyList());
			}
		} else {
			model.put(WebKeys.TEAM_MEMBERS, Collections.emptyList());
		}
		
		response.setContentType("text/json");
		return new ModelAndView("forum/team_members", model);
	}
	
	private ModelAndView ajaxGetBinderOwner(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String namespace = PortletRequestUtils.getStringParameter(request, "namespace", "");
		model.put(WebKeys.NAMESPACE, namespace);
		WorkArea workArea = null;
		Long workAreaId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_WORKAREA_ID));				
		String type = PortletRequestUtils.getStringParameter(request, WebKeys.URL_WORKAREA_TYPE);	
		try {
			if (EntityIdentifier.EntityType.valueOf(type).isBinder()) {
				workArea = getBinderModule().getBinder(workAreaId);
			} else {
				workArea = getZoneModule().getZoneConfig(workAreaId);
	
			}
		} catch(Exception e) {
			model.put(WebKeys.ERROR_MESSAGE, e.getMessage());
		}
		model.put(WebKeys.WORKAREA, workArea);
			
		response.setContentType("text/xml");
		return new ModelAndView("binder/access_control_binder_owner", model);
	}
	
	private ModelAndView ajaxGetGroup(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
		Long groupId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID);
		String namespace = PortletRequestUtils.getStringParameter(request, "namespace", "");
		model.put(WebKeys.NAMESPACE, namespace);
		model.put(WebKeys.BINDER_ID, binderId);
		Group group = (Group)getProfileModule().getEntry(groupId);		
		model.put(WebKeys.GROUP, group);
		List memberList = group.getMembers();
		Set ids = new HashSet();
		Iterator itUsers = memberList.iterator();
		while (itUsers.hasNext()) {
			Principal member = (Principal) itUsers.next();
			ids.add(member.getId());
		}
		model.put(WebKeys.USERS, getProfileModule().getUsers(ids));
		model.put(WebKeys.GROUPS, getProfileModule().getGroups(ids));			
			
		return new ModelAndView("administration/modify_group", model);
	}
	
	// TODO: move it to ListFolderHelper(?) and use only ones findCalendarEvents
	private ModelAndView ajaxFindCalendarEvents(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();	
		if (WebHelper.isUserLoggedIn(request)) {
			model.put(WebKeys.NAMESPACE, PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE));
			model.put(WebKeys.USER_PRINCIPAL, RequestContextHolder.getRequestContext().getUser());
			Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
			List binderIds = Arrays.asList(PortletRequestUtils.getStringParameters(request, WebKeys.URL_BINDER_IDS));
			model.put(WebKeys.URL_DASHBOARD_REQUEST, PortletRequestUtils.getBooleanParameter(request, WebKeys.URL_DASHBOARD_REQUEST, false));
			Binder binder = getBinderModule().getBinder(binderId);
			String calendarStickyId = PortletRequestUtils.getStringParameter(request, WebKeys.CALENDAR_STICKY_ID, String.valueOf(binderId) + "_");
			String calendarModeType = PortletRequestUtils.getStringParameter(request, WebKeys.CALENDAR_MODE_TYPE, "");
						
			Map options = new HashMap();
			boolean eventsByEntry = PortletRequestUtils.getBooleanParameter(request, "ssEntryEvents", false);
			if (eventsByEntry) {
				// get events by entry
				Long entryId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID);

				User user = RequestContextHolder.getRequestContext().getUser();
				UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
				if (!calendarModeType.equals(ObjectKeys.CALENDAR_MODE_TYPE_MY_EVENTS)) {
					//See if there is a filter turned on for this folder. But don't do it for the MyEvents display
					options.putAll(ListFolderHelper.getSearchFilter(this, request, binder, userFolderProperties));
				}
				
		       	List entries;
				if (binder instanceof Folder || binder instanceof Workspace) {
					Document searchFilter = SearchFiltersBuilder.buildGetEntryQuery(request, entryId);
					Map retMap = getBinderModule().executeSearchQuery(searchFilter, Constants.SEARCH_MODE_NORMAL, options);
					entries = (List) retMap.get(ObjectKeys.SEARCH_ENTRIES);
				} else {
					//a template
					entries = new ArrayList();
				}
				
				model.putAll(EventsViewHelper.getEntryEventsBeans(binder, entries, response, WebHelper.getRequiredPortletSession(request), false));

			} else {
				// get events by date
			
				int year = PortletRequestUtils.getIntParameter(request, WebKeys.URL_DATE_YEAR, -1);
				int month = PortletRequestUtils.getIntParameter(request, WebKeys.URL_DATE_MONTH, -1);
				int dayOfMonth = PortletRequestUtils.getIntParameter(request, WebKeys.URL_DATE_DAY_OF_MONTH, -1);
				
				PortletSession portletSession = WebHelper.getRequiredPortletSession(request);
				
				Date currentDate = EventsViewHelper.getCalendarCurrentDate(portletSession);
				currentDate = EventsViewHelper.getDate(year, month, dayOfMonth, currentDate);
				model.put(WebKeys.CALENDAR_CURRENT_DATE, currentDate);
				EventsViewHelper.setCalendarCurrentDate(portletSession, currentDate);
				
				User user = RequestContextHolder.getRequestContext().getUser();
				UserProperties userProperties = getProfileModule().getUserProperties(user.getId());
				
				String gridType = PortletRequestUtils.getStringParameter(request, WebKeys.CALENDAR_GRID_TYPE, "");
				Integer gridSize = PortletRequestUtils.getIntParameter(request, WebKeys.CALENDAR_GRID_SIZE, -1);
				
				Map grids = EventsViewHelper.setCalendarGrid(portletSession, userProperties, calendarStickyId, gridType, gridSize);

				getProfileModule().setUserProperty(user.getId(), WebKeys.CALENDAR_CURRENT_GRID, grids);

				model.put(WebKeys.CALENDAR_GRID_TYPE, ((EventsViewHelper.Grid)grids.get(calendarStickyId)).type);
				model.put(WebKeys.CALENDAR_GRID_SIZE, ((EventsViewHelper.Grid)grids.get(calendarStickyId)).size);
				
				Integer weekFirstDay = (Integer)userProperties.getProperty(ObjectKeys.USER_PROPERTY_CALENDAR_FIRST_DAY_OF_WEEK);
				weekFirstDay = weekFirstDay!=null?weekFirstDay:CalendarHelper.getFirstDayOfWeek();
				
				AbstractIntervalView calendarViewRangeDates = new OneMonthView(currentDate, weekFirstDay);
	
				options.put(ObjectKeys.SEARCH_MAX_HITS, 10000);
				
				List intervals = new ArrayList(1);
				intervals.add(calendarViewRangeDates.getVisibleIntervalRaw());

		       	options.put(ObjectKeys.SEARCH_EVENT_DAYS, intervals);

		       	
		       	String start = DateTools.dateToString(calendarViewRangeDates.getVisibleStart(), DateTools.Resolution.SECOND);
		       	String end =  DateTools.dateToString(calendarViewRangeDates.getVisibleEnd(), DateTools.Resolution.SECOND);
		       	
		       	options.put(ObjectKeys.SEARCH_LASTACTIVITY_DATE_START, start);
		       	options.put(ObjectKeys.SEARCH_LASTACTIVITY_DATE_END, end);
	
		       	options.put(ObjectKeys.SEARCH_CREATION_DATE_START, start);
		       	options.put(ObjectKeys.SEARCH_CREATION_DATE_END, end);
			
				UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
				if (!calendarModeType.equals(ObjectKeys.CALENDAR_MODE_TYPE_MY_EVENTS)) {
					//See if there is a filter turned on for this folder. But don't do it for the MyEvents display
					options.putAll(ListFolderHelper.getSearchFilter(this, request, binder, userFolderProperties));
				}
				Document baseFilter = ((Document) options.get(ObjectKeys.SEARCH_SEARCH_FILTER));
				boolean filtered = (null != baseFilter); 
				if (filtered) {
					Element preDeletedOnlyTerm = (Element)baseFilter.getRootElement().selectSingleNode("//filterTerms/filterTerm[@preDeletedOnly='true']");
					if (preDeletedOnlyTerm != null) {
						options.put(ObjectKeys.SEARCH_PRE_DELETED, Boolean.TRUE);
					}
				}				
				
				// Are we searching for events in a folder or workspace?
		       	List entries;
				if (binder instanceof Folder || binder instanceof Workspace) {
					// Yes!  Are we searching for physical events using
					// a filter?
					boolean virtual;
					String eventsType = EventsViewHelper.getCalendarDisplayEventType(this, user.getId(), binderId);
					virtual = ((null != eventsType) && "virtual".equals(eventsType));
					Map retMap;
					if ((!virtual) && filtered) {
						// Yes!  Simply perform the search using that
						// filter.
						retMap = getBinderModule().executeSearchQuery(baseFilter, Constants.SEARCH_MODE_NORMAL, options);
						entries = (List) retMap.get(ObjectKeys.SEARCH_ENTRIES);
					}
					
					else {
						// No, the search is either for virtual event
						// or not using a filter!  Is it a search for
						// physical events?
						if (!virtual) {
							// Yes!  Is it really?  We'll consider it
							// a search for virtual event if there
							// aren't any binders to search through.
							int binderCount = ((null == binderIds) ? 0 : binderIds.size());
							switch (binderCount) {
							case 0:
								virtual = true;
								break;
								
							case 1:
								Object binderO = binderIds.get(0);
								if ((null != binderO) && (binderO instanceof String)) {
									virtual = ("none".equalsIgnoreCase((String)binderO));
								}
								break;
							}
						}

						// Is it a search for virtual events using a
						// defined filter?
						if (virtual && filtered) {
							// Yes!  Instead of searching the current
							// binder, which the filter should have
							// been setup for, we need to search the
							// entire tree.  Adjust the filter
							// accordingly.
							Element foldersListFilterTerm = (Element)baseFilter.getRootElement().selectSingleNode("//filterTerms/filterTerm[@filterType='foldersList']");
							Element filterFolderId = (Element)baseFilter.getRootElement().selectSingleNode("//filterTerms/filterTerm[@filterType='foldersList']/filterFolderId");
							if ((null != foldersListFilterTerm) && (null != filterFolderId)) {
								foldersListFilterTerm.addAttribute("filterType", "ancestriesList");
								filterFolderId.setText(String.valueOf(getWorkspaceModule().getTopWorkspace().getId()));
							}
						}

						// Search for the events that are calendar
						// entries.
						ModeType modeType = (virtual ? ModeType.VIRTUAL : ModeType.PHYSICAL); 
						if (calendarModeType.equals(ObjectKeys.CALENDAR_MODE_TYPE_MY_EVENTS)) {
							//This is a request for calendar events for the current user
							modeType = ModeType.MY_EVENTS;
						}
						Document searchFilter = EventHelper.buildSearchFilterDoc(baseFilter, request, modeType, binderIds, binder, SearchUtils.AssigneeType.CALENDAR);
						retMap = getBinderModule().executeSearchQuery(searchFilter, Constants.SEARCH_MODE_NORMAL, options);
						entries = (List) retMap.get(ObjectKeys.SEARCH_ENTRIES);
						
						// Are we searching for virtual events?
						if (virtual) {
							// Yes!  Search for the events that are
							// task entries...
							searchFilter = EventHelper.buildSearchFilterDoc(baseFilter, request, modeType, binderIds, binder, SearchUtils.AssigneeType.TASK);
							retMap = getBinderModule().executeSearchQuery(searchFilter, Constants.SEARCH_MODE_NORMAL, options);
							List taskEntries = (List) retMap.get(ObjectKeys.SEARCH_ENTRIES);
							int tasks = ((null == taskEntries) ? 0 : taskEntries.size());
							if (0 < tasks) {
								//First, build a list of entryIds we have seen already
								Set entryIds = new HashSet();
								for (int i = 0; i < entries.size(); i++) {
									Map entry = (Map)entries.get(i);
									String docId = (String)entry.get("_docId");
									entryIds.add(docId);
								}
								// ...and add them to the calendar
								// ...events we found above.
								for (int i = 0; i < tasks; i += 1) {
									Map entry = (Map)taskEntries.get(i);
									String docId = (String)entry.get("_docId");
									//Only add the task if it hasn't already been seen
									if (docId != null && !entryIds.contains(docId)) entries.add(taskEntries.get(i));
								}
							}
						}
					}
					
				} else {
					// A template.
					entries = new ArrayList();
				}

				// If we get here, entries will now contain the
				// requested events.
				model.putAll(
					EventsViewHelper.getEventsBeans(
						this,
						user.getId(),
						binderId,
						entries,
						calendarViewRangeDates,
						portletSession,
						false));
			}
			
		} else {
			model.put(WebKeys.CALENDAR_VIEWBEAN , Collections.EMPTY_LIST);
		}
		
		response.setContentType("text/json");
		return new ModelAndView("forum/json/events_commented", model);
	}
	
	private ModelAndView ajaxGetCalendarFreeInfo(RenderRequest request, RenderResponse response) throws PortletRequestBindingException {
		Map model = new HashMap();	
		if (WebHelper.isUserLoggedIn(request)) {
			model.put(WebKeys.NAMESPACE, PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE));
			model.put(WebKeys.USER_PRINCIPAL, RequestContextHolder.getRequestContext().getUser());

			Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
			Binder binder = getBinderModule().getBinder(binderId);
			
			if (binder instanceof Folder || binder instanceof Workspace) {
				DateTime startDate = null;
				DateTime endDate = null;
			
				String entryId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, null);
				
				String[] usersId = PortletRequestUtils.getStringParameters(request, WebKeys.CALENDAR_SCHEDULE_USER_IDS);
				String startS = PortletRequestUtils.getStringParameter(request, WebKeys.CALENDAR_SCHEDULE_START_DATE, null);
				if (startS != null) {
					try {
						DateTimeFormatter formatter = ISODateTimeFormat.date();
						startDate = formatter.parseDateTime(startS);
					} catch (IllegalArgumentException e) {
						// wrong date
					}
				}
				String endS = PortletRequestUtils.getStringParameter(request, WebKeys.CALENDAR_SCHEDULE_END_DATE, null);
				if (endS != null) {
					try {
						DateTimeFormatter formatter = ISODateTimeFormat.date();
						endDate = formatter.parseDateTime(endS);
					} catch (IllegalArgumentException e) {
						// wrong date
					}
				}				
				String userListName = PortletRequestUtils.getStringParameter(request, WebKeys.CALENDAR_SCHEDULE_USER_LIST_NAME, null);
				
				
				if (usersId != null && usersId.length > 0 && 
						startDate != null && endDate != null && userListName != null) {
				
					AbstractIntervalView calendarInterval = new StartEndDatesView(startDate.toDate(), endDate.toDate());
					AbstractIntervalView.VisibleIntervalFormattedDates interval = calendarInterval.getVisibleIntervalRaw();
					
					Criteria crit = new Criteria();
					crit.add(in(ENTRY_TYPE_FIELD, new String[] {Constants.ENTRY_TYPE_ENTRY, 
							Constants.ENTRY_TYPE_REPLY}))
						.add(in(DOC_TYPE_FIELD, new String[] {Constants.DOC_TYPE_ENTRY}))
						.add(in(userListName, usersId))
						.add(between(Constants.EVENT_DATES_FIELD, 
								interval.startDate, interval.endDate));
					crit.addOrder(Order.asc(MODIFICATION_DATE_FIELD));
		
					Map searchResults = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, 0, 1000, null);
					
					OrderedMap usersFreeBusyInfo = new LinkedMap();
					OrderedMap allUsersDates = new LinkedMap();
					allUsersDates.put(Event.FreeBusyType.tentative, new ArrayList());
					allUsersDates.put(Event.FreeBusyType.busy, new ArrayList());
					allUsersDates.put(Event.FreeBusyType.outOfOffice, new ArrayList());
					usersFreeBusyInfo.put("all", allUsersDates);
					for (int i = 0; i < usersId.length; i++) {
						OrderedMap userEvents = new LinkedMap();
						userEvents.put(Event.FreeBusyType.tentative, new ArrayList());
						userEvents.put(Event.FreeBusyType.busy, new ArrayList());
						userEvents.put(Event.FreeBusyType.outOfOffice, new ArrayList());						
						usersFreeBusyInfo.put(usersId[i], userEvents);
					}
					
					Map<Map, List<Event>> events = EventsViewHelper.getEvents((List) searchResults.get(ObjectKeys.SEARCH_ENTRIES), calendarInterval);				
					Iterator<Map.Entry<Map, List<Event>>> it = events.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<Map, List<Event>> mapEntry = it.next();
						Map entry = mapEntry.getKey();
						
						if (entryId != null && (entry.get(Constants.DOCID_FIELD).equals(entryId))) {
							// it's entry modify
							continue;
						}
						
						List<Event> entryEvents = mapEntry.getValue();
						
						Set userIdsSearchResult = new HashSet();
						Object userListValue = entry.get(userListName);
						if (userListValue != null) {
							if (userListValue instanceof String) {
								userIdsSearchResult.add(userListValue);
							} else {
								userIdsSearchResult = ((SearchFieldResult)userListValue).getValueSet();
							}
						}
						for (Event event : entryEvents) {
							Iterator userIdsSearchResultIt = userIdsSearchResult.iterator();
							while (userIdsSearchResultIt.hasNext()) {
								Map userDates = (Map)usersFreeBusyInfo.get(userIdsSearchResultIt.next());
								if (userDates == null) {
									// don't collect data for this user
									continue;
								}
								Calendar start = event.getLogicalStart();
								Calendar end   = event.getLogicalEnd();
								if ((null != start) && (null != end)) {
									Map eventInfo = new HashMap();
									
									eventInfo.put("start", start.getTime());
									eventInfo.put("end", end.getTime());
									eventInfo.put("type", event.getFreeBusy().name());
									eventInfo.put("allDay", event.isAllDayEvent());
									eventInfo.put("timeZoneSensitive", event.isTimeZoneSensitive());
									
									if (event.getFreeBusy() != Event.FreeBusyType.free) {
										((List)userDates.get(event.getFreeBusy())).add(eventInfo);
										((List)allUsersDates.get(event.getFreeBusy())).add(eventInfo);
									}
								}
							}
						}
					}
					
					// remove empty lists from map
					Iterator<Map.Entry<String, OrderedMap>> usersFreeBusyInfoIt = usersFreeBusyInfo.entrySet().iterator();
					while (usersFreeBusyInfoIt.hasNext()) {
						Map.Entry<String, OrderedMap> mapEntry = usersFreeBusyInfoIt.next();
						Iterator<Map.Entry<Event.FreeBusyType, List>> userEventIt = mapEntry.getValue().entrySet().iterator();	
						while (userEventIt.hasNext()) {
							Map.Entry<Event.FreeBusyType, List> userEventMapEntry = userEventIt.next();
							if (userEventMapEntry.getValue() == null || userEventMapEntry.getValue().isEmpty()) {
								userEventIt.remove();
							}
						}
					}
					
					model.put(WebKeys.CALENDAR_FREE_BUSY_INFO , usersFreeBusyInfo);
				}
			}
		} else {
			model.put(WebKeys.CALENDAR_FREE_BUSY_INFO , new HashMap());
		}
		
		response.setContentType("text/json");
		return new ModelAndView("forum/json/freeInfo", model);
	}
	
	
	private ModelAndView ajaxUpdateTask(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		
		if (WebHelper.isUserLoggedIn(request)) {
			Long entryId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID);
			
			// We use null for the binderId here because with the
			// changes to display contained vs. assigned tasks, the
			// tasks being modified can now come from any folder.
			FolderEntry entry = getFolderModule().getEntry(null, entryId);
			String newPriority = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TASK_PRIORITY, "");
			String newStatus = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TASK_STATUS, "");
			String newCompleted = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TASK_COMPLETED, "");
			
			Map formData = new HashMap();
			
			TaskHelper.adjustTaskAttributesDependencies(entry, formData, newPriority, newStatus, newCompleted);
			
			try {
				getFolderModule().modifyEntry(null, entryId, 
						new MapInputData(formData), null, null, null, null);
				
				model.put(WebKeys.ENTRY, entry);
				model.put(WebKeys.ENTRY_DEFINTION_ELEMENT_DATA, getDefinitionModule().getEntryDefinitionElements(entry.getEntryDefId()));
				model.put(WebKeys.USER_PRINCIPAL, RequestContextHolder.getRequestContext().getUser());
			} catch (OperationAccessControlException e) {
				Map statusMap = new HashMap();
				statusMap.put("ss_operation_denied", NLT.get("task.update.unauthorized"));
				model.put(WebKeys.AJAX_STATUS, statusMap);
			}
		}		
		
		response.setContentType("text/json");
		return new ModelAndView("forum/json/update_task", model);
	}


	private ModelAndView ajaxFindEntryForFile(RenderRequest request, RenderResponse response) throws Exception
	{
		Map model = new HashMap();
		if (WebHelper.isUserLoggedIn(request)) {
			model.put(WebKeys.USER_PRINCIPAL, RequestContextHolder.getRequestContext().getUser());
			Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_FOLDER_ID);
			Binder binder = getBinderModule().getBinder(binderId);
			if ((binder instanceof Folder) && binder.isLibrary()) {
				Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID, 0L);
				String path = PortletRequestUtils.getStringParameter(request, WebKeys.URL_AJAX_VALUE,"");
				String repositoryName = PortletRequestUtils.getStringParameter(request, WebKeys.REPOSITORY, "");
				if(Validator.isNotNull(path)) {
					String fileName = new java.io.File(path).getName();
					Folder folder = (Folder)binder;
					FolderEntry entry = getFolderModule().getLibraryFolderEntryByFileName(folder, fileName);
					// First check inter-entry integrity regarding the file name.
					if(entry != null && (entryId == 0L || entryId != entry.getId().longValue())) {
						model.put(WebKeys.AJAX_ERROR_MESSAGE, "entry.duplicateFileInLibrary");
						model.put(WebKeys.AJAX_ERROR_DETAIL, entry.getTitle());
					}
					// 	Next check intra-entry integrity
					else {
						if(folder.isMirrored()) {
							boolean isNetFolder = folder.isAclExternallyControlled();
							if(Validator.isNotNull(repositoryName) && !ObjectKeys.FI_ADAPTER.equals(repositoryName)) {
								model.put(WebKeys.AJAX_ERROR_MESSAGE, "entry.regularFileInMirroredFolder." + (isNetFolder ? "net" : "mirrored"));					
							}
							else if(entryId != 0L) {
							// 	if entry is not null, the above expression guarantees that
							// 	its id is equal to entryId. So we don't have to refetch it.
								if(entry == null)
									entry = getFolderModule().getEntry(binderId, entryId);
								List<FileAttachment> fas = entry.getFileAttachments(ObjectKeys.FI_ADAPTER); // should be at most 1 in size
								for(FileAttachment fa : fas) {
									if(!fileName.equals(fa.getFileItem().getName())) {
										model.put(WebKeys.AJAX_ERROR_MESSAGE, "entry.mirroredFileMultiple." + (isNetFolder ? "net" : "mirrored"));
										model.put(WebKeys.AJAX_ERROR_DETAIL, fa.getFileItem().getName());								
										break;					
									}
								}
							}
						}
						else {
							if(Validator.isNotNull(repositoryName) && ObjectKeys.FI_ADAPTER.equals(repositoryName)) {
								model.put(WebKeys.AJAX_ERROR_MESSAGE, "entry.mirroredFileInRegularFolder." + (Utils.checkIfFilr() ? "filr" : "vibe"));							
							}							
						}
					}
				}
			}
		}
		model.put(WebKeys.URL_AJAX_ID, PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_AJAX_ID));
		model.put(WebKeys.URL_AJAX_VALUE, PortletRequestUtils.getStringParameter(request, WebKeys.URL_AJAX_VALUE,""));
		model.put(WebKeys.URL_AJAX_LABEL_ID, PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_AJAX_LABEL_ID));
		model.put(WebKeys.URL_AJAX_MESSAGE_ID, PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_AJAX_MESSAGE_ID));
		response.setContentType("text/xml");
		return new ModelAndView("binder/ajax_validate_return", model);	
	}
	
	private ModelAndView ajaxCheckTemplateName(RenderRequest request, RenderResponse response) throws Exception
	{
		Map model = new HashMap();
		String name = PortletRequestUtils.getStringParameter(request, WebKeys.URL_AJAX_VALUE,"");
		if (Validator.isNull(name)) {
			model.put(WebKeys.AJAX_ERROR_MESSAGE, "general.required.name");
			model.put(WebKeys.AJAX_ERROR_DETAIL, "");			
		} else {
			try {
				TemplateBinder binder = getTemplateModule().getTemplateByName(name);
				Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
				if (binderId == null || !binder.getId().equals(binderId)) {
					model.put(WebKeys.AJAX_ERROR_MESSAGE, NLT.get("errorcode.notsupported.duplicateTemplateName", new Object[]{name}));
					model.put(WebKeys.AJAX_ERROR_DETAIL, "");
				}
			} catch (org.springframework.dao.IncorrectResultSizeDataAccessException in) {
				//not enforced by db, but try to keep unique for import/export
				model.put(WebKeys.AJAX_ERROR_MESSAGE, NLT.get("errorcode.notsupported.duplicateTemplateName", new Object[]{name}));
				model.put(WebKeys.AJAX_ERROR_MESSAGE_IS_TEXT, true);
				model.put(WebKeys.AJAX_ERROR_DETAIL, "");
				
			} catch (NoBinderByTheNameException nb) {
			}
		}
		model.put(WebKeys.URL_AJAX_ID, PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_AJAX_ID));
		model.put(WebKeys.URL_AJAX_VALUE, PortletRequestUtils.getStringParameter(request, WebKeys.URL_AJAX_VALUE,""));
		model.put(WebKeys.URL_AJAX_LABEL_ID, PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_AJAX_LABEL_ID));
		model.put(WebKeys.URL_AJAX_MESSAGE_ID, PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_AJAX_MESSAGE_ID));
		response.setContentType("text/xml");
		return new ModelAndView("binder/ajax_validate_return", model);	
	}
	private ModelAndView ajaxCheckBinderTitle(RenderRequest request, RenderResponse response) throws Exception
	{
		Map model = new HashMap();
		String title = PortletRequestUtils.getStringParameter(request, WebKeys.URL_AJAX_VALUE, "", false);
		if(Validator.containsPathCharacters(title)) {
			model.put(WebKeys.AJAX_ERROR_MESSAGE, NLT.get("errorcode.title.pathCharacters", new Object[]{title}));
			model.put(WebKeys.AJAX_ERROR_MESSAGE_IS_TEXT, true);
			model.put(WebKeys.AJAX_ERROR_DETAIL, "");
		}
		model.put(WebKeys.URL_AJAX_ID, PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_AJAX_ID));
		model.put(WebKeys.URL_AJAX_VALUE, PortletRequestUtils.getStringParameter(request, WebKeys.URL_AJAX_VALUE,""));
		model.put(WebKeys.URL_AJAX_LABEL_ID, PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_AJAX_LABEL_ID));
		model.put(WebKeys.URL_AJAX_MESSAGE_ID, PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_AJAX_MESSAGE_ID));
		response.setContentType("text/xml");
		return new ModelAndView("binder/ajax_validate_return", model);	
	}


	private void ajaxSaveSearchQuery(ActionRequest request, 
			ActionResponse response) throws PortletRequestBindingException {
		String queryName = PortletRequestUtils.getStringParameter(request, "queryName", "");
		Integer tabId = PortletRequestUtils.getIntParameter(request, WebKeys.URL_TAB_ID, -1);
		Tabs.TabEntry tab = Tabs.getTabs(request).findTab(Tabs.SEARCH, tabId);
		if (tab == null) return;
		
		// get query and options from tab
		String query = tab.getQuery();

		User currentUser = RequestContextHolder.getRequestContext().getUser();
		
		UserProperties userProperties = getProfileModule().getUserProperties(currentUser.getId());
		Map properties = userProperties.getProperties();
		
		Map userQueries = new HashMap();
		if (properties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
			userQueries = (Map)properties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES);
		}
		
		userQueries.put(queryName, query);
		getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES, userQueries);
		Map tabOptions = tab.getData();
		tabOptions.put(Tabs.TITLE, queryName);
		tab.setData(tabOptions);
		
	}

	private void ajaxRemoveSearchQuery(ActionRequest request, 
			ActionResponse response) throws PortletRequestBindingException {
		String queryName = PortletRequestUtils.getStringParameter(request, "queryName", "");
		
		User currentUser = RequestContextHolder.getRequestContext().getUser();
		
		UserProperties userProperties = getProfileModule().getUserProperties(currentUser.getId());
		Map properties = userProperties.getProperties();
		
		if (properties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
			Map userQueries = (Map)properties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES);
			if (userQueries.containsKey(queryName)) {
				userQueries.remove(queryName);
				getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES, userQueries);
			}
		}
	}
	
	private void ajaxSaveUserStatus(AllModulesInjected bs, ActionRequest request, 
			ActionResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		String status = PortletRequestUtils.getStringParameter(request, "status", "");
		BinderHelper.addMiniBlogEntry(bs, status);
	}
	
	private void ajaxSetSidebarVisibility(ActionRequest request, 
			ActionResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		String visibility = PortletRequestUtils.getStringParameter(request, "visibility", "block");
		getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_SIDEBAR_VISIBILITY, visibility);
	}
	
	private void ajaxSetSunburstVisibility(ActionRequest request, 
			ActionResponse response) throws Exception {
		
		User user = RequestContextHolder.getRequestContext().getUser();
		Long entryId = PortletRequestUtils.getLongParameter(request, "entryId");
		Long binderId = PortletRequestUtils.getLongParameter(request, "binderId");
		
		try {
			getProfileModule().setSeen(user.getId(),getFolderModule().getEntry(binderId, entryId));
		} catch(Exception e) {}
	}
	
	private void ajaxUnsetSunburstVisibility(ActionRequest request, 
			ActionResponse response) throws Exception {
		
		User user = RequestContextHolder.getRequestContext().getUser();
		Long entryId = PortletRequestUtils.getLongParameter(request, "entryId");
		List<Long> ids = new ArrayList<Long>();
		ids.add(entryId);
		
		try {
			getProfileModule().setUnseen(user.getId(), ids);
		} catch(Exception e) {}
	}
	
	private void ajaxSetFileStatus(AllModulesInjected bs, ActionRequest request, 
			ActionResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		Long entityId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTITY_ID);				
		String entityType = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_ENTITY_TYPE);				
		String fileId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_ID, "");				
		Integer fileStatusId = PortletRequestUtils.getIntParameter(request, WebKeys.URL_FILE_STATUS);
		DefinableEntity entity = null;
		Binder binder = null;
		if (entityType.equals(EntityType.folderEntry.name())) {
			entity = getFolderModule().getEntry(null, entityId);
		} else if (entityType.equals(EntityType.folder.name()) || entityType.equals(EntityType.workspace.name())) {
			entity = getBinderModule().getBinder(entityId);
		}
		if (entity != null) {
			//Set the file status
			Set<Attachment> attachments = entity.getAttachments();
			FileAttachment fileAtt = null;
			for (Attachment attachment : attachments) {
				if (attachment instanceof FileAttachment) {
					if (attachment.getId().equals(fileId)) {
						fileAtt = (FileAttachment)attachment;
						break;
					}
					fileAtt = ((FileAttachment)attachment).findFileVersionById(fileId);
					if (fileAtt != null) break;
				}
			}
			if (fileAtt != null) {
				bs.getBinderModule().setFileVersionStatus(entity, fileAtt, fileStatusId.intValue());
			}
		}
	}
	
	private void ajaxPinEntry(AllModulesInjected bs, ActionRequest request, 
			ActionResponse response) throws Exception {
		
		User user = RequestContextHolder.getRequestContext().getUser();
		Long entryId = PortletRequestUtils.getLongParameter(request, "entryId");
		Long binderId = PortletRequestUtils.getLongParameter(request, "binderId");
		
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
		Map properties = userFolderProperties.getProperties();
		
		String pinnedEntries = "";
		if (properties.containsKey(ObjectKeys.USER_PROPERTY_PINNED_ENTRIES)) {
			pinnedEntries = (String)properties.get(ObjectKeys.USER_PROPERTY_PINNED_ENTRIES);
		}
		List peList = new ArrayList();
		String[] peArray = pinnedEntries.split(",");
		for (int i = 0; i < peArray.length; i++) {
			if (!peList.contains(peArray[i]) && !peArray[i].equals("")) peList.add(Long.valueOf(peArray[i]));
		}
		//If the entry already is pinned, then this is a request to unpin it
		if (peList.contains(entryId)) {
			peList.remove(entryId);
		} else {
			peList.add(entryId);
		}
		
		//See if there are any pinned entries that shouldn't be in the list anymore
		String finalPinnedEntries = "";
		SortedSet<FolderEntry> pinnedFolderEntriesSet = bs.getFolderModule().getEntries(peList);
		for (FolderEntry entry : pinnedFolderEntriesSet) {
			//Make sure the entry is still in this folder
			if (entry.getParentBinder().getId().equals(binderId)) {
				if (!finalPinnedEntries.equals("")) finalPinnedEntries += ",";
				finalPinnedEntries += entry.getId().toString();
			}
		}
		getProfileModule().setUserProperty(user.getId(), binderId, ObjectKeys.USER_PROPERTY_PINNED_ENTRIES, finalPinnedEntries);
	}
	
	private ModelAndView ajaxGetSearchQueryName(RenderRequest request, RenderResponse response) throws PortletRequestBindingException {
		String queryName = PortletRequestUtils.getStringParameter(request, "queryName", "");

		User currentUser = RequestContextHolder.getRequestContext().getUser();
		
		UserProperties userProperties = getProfileModule().getUserProperties(currentUser.getId());
		Map properties = userProperties.getProperties();
		
		Map userQueries = new HashMap();
		if (properties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
			userQueries = (Map)properties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES);
		}
		
		String savedQueryName = "";
		if (userQueries.containsKey(queryName)) {
			savedQueryName = queryName;
		}
		
		Map model = new HashMap();
		model.put("ss_queryName", savedQueryName);
		
		response.setContentType("text/json");
		return new ModelAndView("forum/json/searchQuery", model);
	}	
	private ModelAndView ajaxGetRemovedQueryName(RenderRequest request, RenderResponse response) throws PortletRequestBindingException {
		String queryName = PortletRequestUtils.getStringParameter(request, "queryName", "");

		User currentUser = RequestContextHolder.getRequestContext().getUser();
		
		UserProperties userProperties = getProfileModule().getUserProperties(currentUser.getId());
		Map properties = userProperties.getProperties();

		Map model = new HashMap();
		if (properties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
			Map userQueries = (Map)properties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES);
			if (!userQueries.containsKey(queryName)) {
				model.put("ss_queryName", queryName);
			}
		}
		response.setContentType("text/json");
		return new ModelAndView("forum/json/removeSearchQuery", model);		
	}
	
	private ModelAndView ajaxListSavedQueries(RenderRequest request, RenderResponse response) {
		User currentUser = RequestContextHolder.getRequestContext().getUser();
		
		UserProperties userProperties = getProfileModule().getUserProperties(currentUser.getId());
		Map properties = userProperties.getProperties();
		
		Map userQueries = new HashMap();
		if (properties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
			userQueries = (Map)properties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES);
		}

		Map model = new HashMap();
		model.put("ss_UserQueries", userQueries);
		return new ModelAndView("forum/json/savedQueries", model);	
	}
	
	
	private ModelAndView ajaxVoteSurvey(RenderRequest request, RenderResponse response) throws AccessControlException, ReservedByAnotherUserException, WriteFilesException {
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID, -1);
		Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID, -1);
		String attributeName = PortletRequestUtils.getStringParameter(request, "attributeName", "");
		User user = RequestContextHolder.getRequestContext().getUser();
		
		String status = null;
			
		if (binderId == -1 || entryId == -1 || Validator.isNull(attributeName)) {
			status = "wrongParameters";
		}
		
		FolderEntry entry = getFolderModule().getEntry(binderId, entryId);
		CustomAttribute surveyAttr = entry.getCustomAttribute(attributeName);
		if (surveyAttr == null || surveyAttr.getValue() == null) {
			status = "noSurvey";
		}
		
		Survey surveyAttrValue = ((Survey)surveyAttr.getValue());
		SurveyModel survey = surveyAttrValue.getSurveyModel();
		if (survey == null) {
			status = "noSurvey";
		}
		
		String guestEmail = PortletRequestUtils.getStringParameter(request, "guest_email", null, false);
		if (user.isShared() && guestEmail == null) {
			status = "missingEmail";
		}
		
		if (survey.isAlreadyVotedCurrentUser(guestEmail) &&
				!survey.isAllowedToChangeVote()) {
			status = "alreadyVoted";
		}
		
		if (status == null) {
				
			survey.removeVote();
			Iterator formDataIt = request.getParameterMap().entrySet().iterator();
			while (formDataIt.hasNext()) {
				Map.Entry mapEntry = (Map.Entry)formDataIt.next();
				String key = (String)mapEntry.getKey();
				String[] value = (String[])mapEntry.getValue();
				if (key.startsWith("answer_")) {
					String[] temp = key.split("_");
					if (temp != null && temp.length == 2) {
						int questionIndex = -1;
						try {
							questionIndex = Integer.parseInt(temp[1]);
						} catch (NumberFormatException e) {
							logger.warn(e);
						}
						
						if (questionIndex != -1) {
							Question question = survey.getQuestionByIndex(questionIndex);
							if (question != null) {
								question.vote(value, guestEmail);
							}
						}
					}
				}
			}
	
			survey.setVoteRequest();
			
			Map formData = new HashMap(); 
			formData.put(attributeName, surveyAttrValue.toString());
			getFolderModule().addVote(binderId, entryId, new MapInputData(formData), null);
			
			status = "ok";
		}
		Map model = new HashMap();
		model.put("status", status);
		
		return new ModelAndView("forum/json/vote_survey", model);
	}
	
	private void ajaxVoteSurveyRemove(ActionRequest request, ActionResponse response) throws AccessControlException, ReservedByAnotherUserException, WriteFilesException {
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID, -1);
		Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID, -1);
		String attributeName = PortletRequestUtils.getStringParameter(request, "attributeName", "");
		User user = RequestContextHolder.getRequestContext().getUser();
		
		if (binderId == -1 || entryId == -1 || Validator.isNull(attributeName)) {
			return;
		}
		
		if (user.isShared()) {
			return;
		}
		
		
		FolderEntry entry = getFolderModule().getEntry(binderId, entryId);
		CustomAttribute surveyAttr = entry.getCustomAttribute(attributeName);
		if (surveyAttr == null || surveyAttr.getValue() == null) {
			return;
		}
		
		Survey surveyAttrValue = ((Survey)surveyAttr.getValue());
		SurveyModel survey = surveyAttrValue.getSurveyModel();
		if (survey == null) {
			return;
		}
		
		survey.removeVote();
		
		survey.setVoteRequest();
		
		Map formData = new HashMap(); 
		formData.put(attributeName, surveyAttrValue.toString());
		getFolderModule().addVote(binderId, entryId, new MapInputData(formData), null);
	}
	
	private ModelAndView ajaxSurveyRemoveVoteStatus(RenderRequest request, RenderResponse response) {
		Map model = new HashMap();
		model.put("status", true);
		return new ModelAndView("forum/json/remove_vote_survey", model);	
	}
	
	private ModelAndView ajaxCheckStatus(RenderRequest request, RenderResponse response)  throws PortletRequestBindingException { 
		Map model = new HashMap();
		Map statusMap = new HashMap();
		model.put(WebKeys.AJAX_STATUS, statusMap);
		model.put("status", true);
		StatusTicket statusTicket = WebStatusTicket.findStatusTicket(
				PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_STATUS_TICKET_ID), request);
		if (statusTicket.isDone() || WebKeys.AJAX_STATUS_STATE_COMPLETED.equals(statusTicket.getState())) statusMap.put(WebKeys.AJAX_STATUS_COMPLETED, "true");
		model.put("ss_operation_status", statusTicket.getStatus());
		model.put("ss_style", PortletRequestUtils.getStringParameter(request, "ss_style", ""));
		response.setContentType("text/xml");
		return new ModelAndView("common/check_status", model);	
	}

	private ModelAndView ajaxCheckIfLoggedIn(RenderRequest request, RenderResponse response)  throws PortletRequestBindingException { 
		User user = RequestContextHolder.getRequestContext().getUser();
		Map model = new HashMap();
        model.put(WebKeys.USER_PRINCIPAL, RequestContextHolder.getRequestContext().getUser());

		if (ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			//Signal that the user is not logged in. 
			Map statusMap = new HashMap();
			statusMap.put(WebKeys.AJAX_STATUS_NOT_LOGGED_IN, new Boolean(true));
			model.put(WebKeys.AJAX_STATUS, statusMap);
		}
		response.setContentType("text/xml");
		return new ModelAndView("forum/check_if_logged_in_return", model);	
	}


	private ModelAndView ajaxWikiLinkForm(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String binderIdText = PortletRequestUtils.getStringParameter(request, "binderId", "");
		model.put("binderId", binderIdText);
		String originalBinderIdText = PortletRequestUtils.getStringParameter(request, "originalBinderId", binderIdText);
		model.put("originalBinderId", originalBinderIdText);
		if (!binderIdText.equals("")) {
			Binder binder = getBinderModule().getBinder(Long.valueOf(binderIdText));
			model.put(WebKeys.BINDER, binder);
			model.put(WebKeys.ORIGINAL_BINDER, binder);
		}
		if (!originalBinderIdText.equals("")) {
			Binder originalBinder = getBinderModule().getBinder(Long.valueOf(originalBinderIdText));
			model.put(WebKeys.ORIGINAL_BINDER, originalBinder);
		}

		return new ModelAndView("binder/wikilink_ajax_return", model);
	}

	private ModelAndView ajaxFindPlaceForm(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String binderIdText = PortletRequestUtils.getStringParameter(request, "binderId", "");
		String propertyIdText = PortletRequestUtils.getStringParameter(request, "propertyId", "");
		model.put("binderId", binderIdText);
		model.put("propertyId", propertyIdText);

		return new ModelAndView("binder/find_place_ajax_return", model);
	}
	
	private ModelAndView ajaxGetUploadProgressStatus(RenderRequest request, RenderResponse response) {
		Map model = new HashMap();
		
		String uploadRequestUid = PortletRequestUtils.getStringParameter(request, WebKeys.URL_UPLOAD_REQUEST_UID, "");
		
		FileUploadProgressListener progressListener = ProgressListenerSessionResolver.get(request.getPortletSession(), uploadRequestUid);
	
		if (progressListener != null) {
			
			model.put("ss_progress", progressListener.getPercentDone());
			model.put("ss_mbytes_read", progressListener.getReadMB());
			model.put("ss_content_length", progressListener.getContentLengthMB());
			model.put("ss_speed", (int)progressListener.getUploadSpeedKBproSec());
			
			int runnigSeconds = progressListener.getRunnigSeconds();
			int runnigHours = (int)runnigSeconds/60/60;
			int runnigMinutes = (int)(runnigSeconds - runnigHours*60*60)/60;
			int runnigOnlySeconds = runnigSeconds - runnigHours*60*60 - runnigMinutes*60;
			
			model.put("ss_running_time", runnigSeconds);
			model.put("ss_running_time_text", String.format((runnigHours > 0 ? "%1$02d:" : "") + "%2$02d:%3$02d", runnigHours, runnigMinutes, runnigOnlySeconds));
			
			int leftSeconds = progressListener.getTimeLeftSeconds();
			int leftHours = (int)leftSeconds/60/60;
			int leftMinutes = (int)(leftSeconds - leftHours*60*60)/60;
			int leftOnlySeconds = leftSeconds - leftHours*60*60 - leftMinutes*60;
			
			model.put("ss_left_time", leftSeconds);
			model.put("ss_left_time_text", String.format((leftHours > 0 ? "%1$02d:" : "") + "%2$02d:%3$02d", leftHours, leftMinutes, leftOnlySeconds));
			
			if (progressListener.isFinished()) {
				ProgressListenerSessionResolver.remove(request.getPortletSession(), uploadRequestUid);
			}
		}
		
		response.setContentType("text/json-comment-filtered");
		return new ModelAndView("forum/json/upload_progress_status", model);
	}

	private ModelAndView ajaxGetWorkflowApplet(RenderRequest request, RenderResponse response) {
		Map model = new HashMap();
		String workflowProcessId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_WORKFLOW_PROCESS_ID, "");
		model.put(WebKeys.WORKFLOW_DEFINITION_ID, workflowProcessId);
		Definition def = DefinitionHelper.getDefinition(workflowProcessId);
		model.put(WebKeys.WORKFLOW_DEFINITION, def);
		return new ModelAndView("forum/view_workflow_process", model);
	}
	private ModelAndView ajaxGetUserAppConfig(RenderRequest request, 
			RenderResponse response) throws Exception {
		UserProperties userProperties = getProfileModule().getUserProperties(null);
		UserAppConfig uac = new UserAppConfig((String)userProperties.getProperty(ObjectKeys.USER_PROPERTY_APPCONFIGS));
		
		Map model = new HashMap();
		String uacJson = (String)uac.getUserAppConfigJson().toString();
		model.put("UserAppConfigJSON",uacJson);
		response.setContentType("text/json");
		return new ModelAndView("forum/user_appconfig", model);
	}
	
	private ModelAndView ajaxGetUserStatus(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		User user = RequestContextHolder.getRequestContext().getUser();
		model.put(WebKeys.USER_PRINCIPAL, user);
		String statusId = PortletRequestUtils.getStringParameter(request, "ss_statusId", "");
		model.put("ss_statusId", statusId);
		return new ModelAndView("forum/save_status_return", model);
	}
	
	private ModelAndView ajaxGetGroupList(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		User user = RequestContextHolder.getRequestContext().getUser();
		Long groupId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_GROUP_ID);
		Long teamId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_TEAM_ID);
		String applicationName = PortletRequestUtils.getStringParameter(request, WebKeys.URL_APPLICATION_GROUP_NAME, "");
		if (groupId != null) {
			Group group = (Group)getProfileModule().getEntry(groupId);		
			model.put(WebKeys.GROUP, group);
			Set ids = new HashSet();
			if (ObjectKeys.ALL_USERS_GROUP_INTERNALID.equals(group.getInternalId()) ||
					ObjectKeys.ALL_EXT_USERS_GROUP_INTERNALID.equals(group.getInternalId())) {
				//We don't try to list all the users or all of the external users. This could take a very long time
			} else {
				List memberList = ((Group)group).getMembers();
				Iterator itUsers = memberList.iterator();
				while (itUsers.hasNext()) {
					Principal member = (Principal) itUsers.next();
					ids.add(member.getId());
				}
			}
			model.put(WebKeys.USERS, getProfileModule().getUsers(ids));
			model.put(WebKeys.GROUPS, getProfileModule().getGroups(ids));
		} else if (teamId != null) {
			Binder binder = getBinderModule().getBinder(teamId);		
			model.put(WebKeys.TEAM_BINDER, binder);
			
			Collection<Principal> usersAndGroups = bs.getBinderModule().getTeamMembers(binder, false);
			SortedMap<String, User> teamUsers = new TreeMap();
			SortedMap<String, Group> teamGroups = new TreeMap();
			for (Principal p : usersAndGroups) {
				if (p instanceof User) {
					teamUsers.put(Utils.getUserTitle(p), (User)p);
				} else if (p instanceof Group) {
					teamGroups.put(p.getTitle(), (Group)p);
				}
			}
			model.put(WebKeys.USERS, teamUsers.values());
			model.put(WebKeys.GROUPS, teamGroups.values());
		} else if (!applicationName.equals("")) {
			ApplicationGroup group = getProfileModule().getApplicationGroup(applicationName);		
			model.put(WebKeys.GROUP, group);
			List applications = new ArrayList();
			Set ids = new HashSet();
			if (ObjectKeys.ALL_APPLICATIONS_GROUP_INTERNALID.equals(group.getInternalId())) {
				applications = BinderHelper.getAllApplications(bs);
				Iterator itUsers = applications.iterator();
				while (itUsers.hasNext()) {
					Map member = (Map) itUsers.next();
					String docId = (String)member.get(Constants.DOCID_FIELD);
					ids.add(Long.valueOf(docId));
				}
			} else {
				applications = group.getMembers();
				Iterator itUsers = applications.iterator();
				while (itUsers.hasNext()) {
					ApplicationPrincipal member = (ApplicationPrincipal) itUsers.next();
					ids.add(member.getId());
				}
			}
			model.put(WebKeys.USERS, getProfileModule().getApplications(ids));
			model.put(WebKeys.GROUPS, getProfileModule().getApplicationGroups(ids));			
			return new ModelAndView("binder/show_application_group_list", model);
		}
		return new ModelAndView("binder/show_group_list", model);
	}
	
	private ModelAndView ajaxViewMiniBlog(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		User user = RequestContextHolder.getRequestContext().getUser();
		String page = PortletRequestUtils.getStringParameter(request, WebKeys.URL_PAGE, "0");
		int pageStart = Integer.valueOf(page) * Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox"));
		Long userId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_USER_ID);
		if (userId == null) userId = user.getId();
		List ids = new ArrayList();
		ids.add(userId);
		SortedSet principals = getProfileModule().getPrincipals(ids);
		Principal p = null;
		if (!principals.isEmpty()) p = (Principal)principals.iterator().next();
		
		model.put(WebKeys.MINIBLOG_USER_ID, p.getId());
		model.put(WebKeys.MINIBLOG_USER, p);
		model.put(WebKeys.MINIBLOG_PAGE, page);
		
		//Get the list of miniblog entries by this user (uses the "miniblog" family attribute to find them)
		Long[] userIds = new Long[]{userId};
		List statuses = getReportModule().getUsersStatuses(userIds, null, null, 
				pageStart + Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox")));
		if (statuses != null && statuses.size() > pageStart) {
			model.put(WebKeys.MINIBLOG_STATUSES, statuses.subList(pageStart, statuses.size()));
		}
		return new ModelAndView("forum/miniblog", model);
	}

	/*
	 * Creates an HttpClient from an HttpURL.
	 */
	private static HttpClient getHttpClient(HttpURL hrl) throws URIException {
		HttpClient client = new HttpClient();
		HostConfiguration hc = client.getHostConfiguration();
		hc.setHost(hrl);
		return client;
	}

	/*
	 * Creates an HttpURL from a URL string.
	 */
	private static HttpURL getHttpURL(String urlStr) throws URIException  {
		HttpURL reply;
		if(urlStr.startsWith("https"))
			 reply = new HttpsURL(urlStr);
		else reply = new HttpURL(urlStr);
		return reply;
	}
}
