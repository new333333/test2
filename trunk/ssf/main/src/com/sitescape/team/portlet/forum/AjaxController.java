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

import static com.sitescape.util.search.Constants.DOC_TYPE_FIELD;
import static com.sitescape.util.search.Constants.ENTRY_ANCESTRY;
import static com.sitescape.util.search.Constants.ENTRY_TYPE_FIELD;
import static com.sitescape.util.search.Constants.MODIFICATION_DATE_FIELD;
import static com.sitescape.util.search.Restrictions.between;
import static com.sitescape.util.search.Restrictions.in;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.lucene.document.DateTools;
import org.dom4j.Document;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.portlet.bind.PortletRequestBindingException;
import org.springframework.web.portlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.calendar.AbstractIntervalView;
import com.sitescape.team.calendar.EventsViewHelper;
import com.sitescape.team.calendar.StartEndDatesView;
import com.sitescape.team.calendar.OneDayView;
import com.sitescape.team.calendar.OneMonthView;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.NoBinderByTheIdException;
import com.sitescape.team.domain.NoBinderByTheNameException;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.ReservedByAnotherUserException;
import com.sitescape.team.domain.SeenMap;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.binder.BinderModule.BinderOperation;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.ic.DocumentDownload;
import com.sitescape.team.module.ic.ICBrokerModule;
import com.sitescape.team.module.ic.ICException;
import com.sitescape.team.module.ic.RecordType;
import com.sitescape.team.module.ical.AttendedEntries;

import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.portlet.binder.AccessControlController;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.search.SearchFieldResult;
import com.sitescape.team.search.filter.SearchFiltersBuilder;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.OperationAccessControlException;
import com.sitescape.team.ssfs.util.SsfsUtil;
import com.sitescape.team.survey.Question;
import com.sitescape.team.survey.Survey;
import com.sitescape.team.survey.SurveyModel;
import com.sitescape.team.task.TaskHelper;
import com.sitescape.team.util.AllModulesInjected;
import com.sitescape.team.util.CalendarHelper;
import com.sitescape.team.util.LongIdUtil;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.ResolveIds;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SimpleMultipartFile;
import com.sitescape.team.util.TagUtil;
import com.sitescape.team.util.TempFileUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractControllerRetry;
import com.sitescape.team.web.tree.DomTreeBuilder;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.upload.FileUploadProgressListener;
import com.sitescape.team.web.upload.ProgressListenerSessionResolver;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.Favorites;
import com.sitescape.team.web.util.ListFolderHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.Tabs;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.team.web.util.WebStatusTicket;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.Http;
import com.sitescape.util.Validator;
import com.sitescape.util.search.Constants;
import com.sitescape.util.search.Criteria;
import com.sitescape.util.search.Order;
/**
 * @author Peter Hurley
 *
 */
public class AjaxController  extends SAbstractControllerRetry {
	
	
	//caller will retry on OptimisiticLockExceptions
	public void handleActionRequestWithRetry(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		if (WebHelper.isUserLoggedIn(request)) {
			String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
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
				ajaxModifyTags(request, response);
			} else if (op.equals(WebKeys.OPERATION_SAVE_FAVORITES)) {
				ajaxSaveFavorites(request, response);
			} else if (op.equals(WebKeys.OPERATION_SAVE_RATING)) {
				ajaxSaveRating(request, response);
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
				ajaxUploadImageFile(request, response); 
			} else if (op.equals(WebKeys.OPERATION_UPLOAD_ICALENDAR_FILE)) {
				ajaxUploadICalendarFile(request, response);
			} else if (op.equals(WebKeys.OPERATION_LOAD_ICALENDAR_BY_URL)) {
				ajaxLoadICalendarByURL(request, response);				
			} else if (op.equals(WebKeys.OPERATION_SAVE_CALENDAR_CONFIGURATION)) {
				ajaxSaveCalendarConfiguration(request, response);				
			} else if (op.equals(WebKeys.OPERATION_SET_BINDER_OWNER_ID)) {
				ajaxSetBinderOwnerId(request, response);
			} else if (op.equals(WebKeys.OPERATION_MODIFY_GROUP)) {
				ajaxModifyGroup(request, response);
			} else if (op.equals(WebKeys.OPERATION_STICKY_CALENDAR_DISPLAY_SETTINGS)) {
				ajaxStickyCalendarDisplaySettings(request, response);
			} else if (op.equals(WebKeys.OPERATION_SAVE_SEARCH_QUERY)) {
				ajaxSaveSearchQuery(request, response);
			} else if (op.equals(WebKeys.OPERATION_REMOVE_SEARCH_QUERY)) {
				ajaxRemoveSearchQuery(request, response);
			} else if (op.equals(WebKeys.OPERATION_VOTE_SURVEY)) {
				ajaxVoteSurvey(request, response);
			} else if (op.equals(WebKeys.OPERATION_VOTE_SURVEY_REMOVE)) {
				ajaxVoteSurveyRemove(request, response);				
			} else if (op.equals(WebKeys.OPERATION_ATTACHE_MEETING_RECORDS)) {
				ajaxAttacheMeetingRecords(request, response);
			} else if (op.equals(WebKeys.OPERATION_SUBSCRIBE)) {
				Map formData = request.getParameterMap();
				if (formData.containsKey("okBtn")) ajaxDoSubscription(request, response);
			} else if (op.equals(WebKeys.OPERATION_SAVE_UESR_STATUS)) {
				ajaxSaveUserStatus(request, response);
			} else if (op.equals(WebKeys.OPERATION_SET_SIDEBAR_VISIBILITY)) {
				ajaxSetSidebarVisibility(request, response);
			} else if (op.equals(WebKeys.OPERATION_SET_SUNBURST_VISIBILITY)) {
				ajaxSetSunburstVisibility(request, response);
			} else if (op.equals(WebKeys.OPERATION_PIN_ENTRY)) {
				ajaxPinEntry(request, response);
			}
		}
	}
	
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");

		if (!WebHelper.isUserLoggedIn(request)) {
			Map model = new HashMap();
			Map statusMap = new HashMap();
			
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
					op.equals(WebKeys.OPERATION_SAVE_FAVORITES)) {
				model.put(WebKeys.AJAX_ERROR_MESSAGE, "general.notLoggedIn");	
				response.setContentType("text/json");
				return new ModelAndView("common/json_ajax_return", model);
			}
			try {
				User user = RequestContextHolder.getRequestContext().getUser();
				String displayStyle = user.getDisplayStyle();
				if (displayStyle != null && displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
					if (op.equals(WebKeys.OPERATION_WORKSPACE_TREE) && 
							!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
						return new ModelAndView("forum/fetch_url_return", model);
					}
				}
			} catch(Exception e) {}
			
			response.setContentType("text/xml");			
			if (op.equals(WebKeys.OPERATION_UNSEEN_COUNTS)) {
				return new ModelAndView("forum/unseen_counts", model);
			} else if (op.equals(WebKeys.OPERATION_CHECK_IF_LOGGED_IN)) {
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
				op.equals(WebKeys.OPERATION_GET_CONDITION_ENTRY_VALUE_LIST)) {
				return new ModelAndView("definition_builder/get_condition_element", model);
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
			return ajaxConfigureFolderColumns(request, response);
		} else if (op.equals(WebKeys.OPERATION_SUBSCRIBE)) {
			return ajaxSubscribe(request, response); 
		} else if (op.equals(WebKeys.OPERATION_SAVE_ENTRY_WIDTH)) {
			return ajaxSaveEntryWidth(request, response);
			
		} else if (op.equals(WebKeys.OPERATION_SAVE_ENTRY_HEIGHT)) {
			return ajaxSaveEntryHeight(request, response);
			
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
			return ajaxStartMeeting(request, response, ICBrokerModule.REGULAR_MEETING);
		} else if (op.equals(WebKeys.OPERATION_SCHEDULE_MEETING)) {
			return ajaxStartMeeting(request, response, ICBrokerModule.SCHEDULED_MEETING);
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
			return ajaxVoteSurveyStatus(request, response);	
		} else if (op.equals(WebKeys.OPERATION_VOTE_SURVEY_REMOVE)) {
			return ajaxVoteSurveyStatus(request, response);				
		} else if (op.equals(WebKeys.OPERATION_CHECK_STATUS)) {
			return ajaxCheckStatus(request, response);
		} else if (op.equals(WebKeys.OPERATION_WIKILINK_FORM)) {
			return ajaxWikiLinkForm(request, response);
		} else if (op.equals(WebKeys.OPERATION_FIND_PLACE_FORM)) {
			return ajaxFindPlaceForm(request, response);
		} else if (op.equals(WebKeys.OPERATION_UPLOAD_ICALENDAR_FILE)) {
			return ajaxUploadICalendarFileStatus(request, response);
		} else if (op.equals(WebKeys.OPERATION_LOAD_ICALENDAR_BY_URL)) {
			return ajaxLoadICalendarByURL(request, response);			
		} else if (op.equals(WebKeys.OPERATION_SAVE_CALENDAR_CONFIGURATION)) {
			return ajaxSaveCalendarConfigurationStatus(request, response);			
		} else if (op.equals(WebKeys.OPERATION_GET_CHANGE_LOG_ENTRY_FORM)) {
			return ajaxGetChangeLogEntryForm(request, response);
		} else if (op.equals(WebKeys.OPERATION_GET_MEETING_RECORDS)) {
			return ajaxGetMeetingRecords(request, response);
		} else if (op.equals(WebKeys.OPERATION_GET_FILTER_TYPE) || 
				op.equals(WebKeys.OPERATION_GET_ENTRY_ELEMENTS) || 
				op.equals(WebKeys.OPERATION_GET_ELEMENT_VALUES) || 
				op.equals(WebKeys.OPERATION_GET_ELEMENT_VALUE_DATA) || 
				op.equals(WebKeys.OPERATION_GET_WORKFLOW_STATES)) {
			return ajaxGetFilterData(request, response);
		} else if (op.equals(WebKeys.OPERATION_GET_CONDITION_ENTRY_ELEMENTS) || 
				op.equals(WebKeys.OPERATION_GET_CONDITION_ENTRY_USER_LIST_ELEMENTS) || 
				op.equals(WebKeys.OPERATION_GET_CONDITION_ENTRY_OPERATIONS) || 
				op.equals(WebKeys.OPERATION_GET_CONDITION_ENTRY_VALUE_LIST)) {
			return ajaxGetConditionData(request, response);
		} else if (op.equals(WebKeys.OPERATION_SHOW_HELP_CPANEL) || 
					op.equals(WebKeys.OPERATION_HIDE_HELP_CPANEL) ||
					op.equals(WebKeys.OPERATION_SHOW_BUSINESS_CARD) ||
					op.equals(WebKeys.OPERATION_HIDE_BUSINESS_CARD) ||
					op.equals(WebKeys.OPERATION_SHOW_SIDEBAR_PANEL) || 
					op.equals(WebKeys.OPERATION_HIDE_SIDEBAR_PANEL)) {
			return new ModelAndView("forum/fetch_url_return");			
		} else if (op.equals(WebKeys.OPERATION_SAVE_UESR_STATUS)) {
			return ajaxGetUserStatus(request, response);
		} else if (op.equals(WebKeys.OPERATION_VIEW_MINIBLOG)) {
			return ajaxViewMiniBlog(request, response);
		} else if (op.equals(WebKeys.OPERATION_GET_UPLOAD_PROGRESS_STATUS)) {
			return ajaxGetUploadProgressStatus(request, response);
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
			f.addFavorite(binder.getTitle(), Favorites.FAVORITE_BINDER, binderId.toString(), PortletRequestUtils.getStringParameter(request, "viewAction", ""), "");
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
		ModelAndView modelAndView = ListFolderHelper.BuildFolderBeans(bs, request, response, binderId);
		modelAndView.setView("definition_elements/folder_view_common_page");
		return modelAndView;
	}

	private ModelAndView ajaxGetWikiFolderPage(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
		ModelAndView modelAndView = ListFolderHelper.BuildFolderBeans(bs, request, response, binderId);
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
		
	private ModelAndView ajaxConfigureFolderColumns(RenderRequest request, 
				RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long binderId = null;
		try {
			binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);				
		} catch(PortletRequestBindingException ex) {}
		
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
			String[] address = PortletRequestUtils.getStringParameters(request, "_subscribe"+i);
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
		if (Validator.isNotNull(entryWidth)) values.put(WebKeys.FOLDER_ENTRY_WIDTH, entryWidth);
		if (Validator.isNotNull(entryHeight)) values.put(WebKeys.FOLDER_ENTRY_HEIGHT, entryHeight);
		if (Validator.isNotNull(entryTop)) values.put(WebKeys.FOLDER_ENTRY_TOP, entryTop);
		if (Validator.isNotNull(entryLeft)) values.put(WebKeys.FOLDER_ENTRY_LEFT, entryLeft);
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
				model.put(WebKeys.WORKFLOW_DEFINTION_STATE_DATA, stateData);
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
		
		value = PortletRequestUtils.getStringParameter(request, WebKeys.CONDITION_ELEMENT_VALUE);
		if (Validator.isNotNull(value)) {
			model.put(WebKeys.CONDITION_ELEMENT_VALUE, value);
		}
		
		response.setContentType("text/xml");
		if (op.equals(WebKeys.OPERATION_GET_CONDITION_ENTRY_ELEMENTS)) {
			return new ModelAndView("definition_builder/get_condition_entry_element", model);
		} else if (op.equals(WebKeys.OPERATION_GET_CONDITION_ENTRY_USER_LIST_ELEMENTS)) {
			return new ModelAndView("definition_builder/get_condition_entry_user_list_element", model);
		} else if (op.equals(WebKeys.OPERATION_GET_CONDITION_ENTRY_OPERATIONS)) {
			return new ModelAndView("definition_builder/get_condition_entry_element_operations", model);
		} else {
			return new ModelAndView("definition_builder/get_condition_entry_element_value", model);
		}
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
		
		User user = RequestContextHolder.getRequestContext().getUser();
		String view = "tag_jsps/tree/get_tree_div";
		if (user.getDisplayStyle() != null && 
				!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId()) &&
				user.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
			view = "tag_jsps/tree/get_tree_div_accessible";
		} else {
			response.setContentType("text/xml");
		}
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
			ActionResponse response) throws Exception {
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
					response.setRenderParameter("ssICalendarException", "parseException");
				}
			}
			WebHelper.releaseFileHandle(fileHandle);
		}
		response.setRenderParameter("ssICalendarEntryAddedIdsSize", Integer.toString(attendedEntries.added.size()));
		response.setRenderParameter("ssICalendarEntryModifiedIdsSize", Integer.toString(attendedEntries.modified.size()));
		
		List addedEntriesIdsAsStrings = ResolveIds.longsToString(attendedEntries.added);
		String[] ids = new String[addedEntriesIdsAsStrings.size()];
		ids = (String[])addedEntriesIdsAsStrings.toArray(ids);
		response.setRenderParameter("ssICalendarAddedEntryIds", ids);
		
		List modifiedEntriesIdsAsStrings = ResolveIds.longsToString(attendedEntries.modified);
		ids = new String[modifiedEntriesIdsAsStrings.size()];
		ids = (String[])modifiedEntriesIdsAsStrings.toArray(ids);
		response.setRenderParameter("ssICalendarModifiedEntryIds", ids);
		
	}
	
	private void ajaxLoadICalendarByURL(ActionRequest request, 
			ActionResponse response) throws Exception {
		AttendedEntries attendedEntries = new AttendedEntries();
		
		Long folderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_FOLDER_ID, -1);
		String iCalURL = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ICAL_URL, null);
		
		if (folderId != -1) {
			HttpClient httpClient = new HttpClient();
			GetMethod getMethod = new GetMethod(iCalURL);
			try {
				int statusCode = httpClient.executeMethod(getMethod);
				if (statusCode == 200) {
					// get the resonse as an InputStream
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
				logger.debug("Get iCalendar by URL [" + iCalURL + "].", e);
				response.setRenderParameter("ssICalendarException", "wrongURL");
			} finally{
				//release the connection
				getMethod.releaseConnection();
			}
		}
			
		response.setRenderParameter("ssICalendarEntryAddedIdsSize", Integer.toString(attendedEntries.added.size()));
		response.setRenderParameter("ssICalendarEntryModifiedIdsSize", Integer.toString(attendedEntries.modified.size()));
		
		List addedEntriesIdsAsStrings = ResolveIds.longsToString(attendedEntries.added);
		String[] ids = new String[addedEntriesIdsAsStrings.size()];
		ids = (String[])addedEntriesIdsAsStrings.toArray(ids);
		response.setRenderParameter("ssICalendarAddedEntryIds", ids);
		
		List modifiedEntriesIdsAsStrings = ResolveIds.longsToString(attendedEntries.modified);
		ids = new String[modifiedEntriesIdsAsStrings.size()];
		ids = (String[])modifiedEntriesIdsAsStrings.toArray(ids);
		response.setRenderParameter("ssICalendarModifiedEntryIds", ids);
		
	}
	
	private ModelAndView ajaxUploadICalendarFileStatus(RenderRequest request, RenderResponse response) {
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
		return new ModelAndView("forum/json/icalendar_upload", model);
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
		return new ModelAndView("administration/get_change_log_entry_form", model);
	}
	
	private void ajaxSetBinderOwnerId(ActionRequest request, 
			ActionResponse response) throws Exception {
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		String ownerId = PortletRequestUtils.getStringParameter(request, "ownerId", "");
		String sPropagate = PortletRequestUtils.getStringParameter(request, "propagate", "");
		if (!ownerId.equals("")) {
			Binder binder = getBinderModule().getBinder(binderId);
			boolean bPropagate = false;
			if (sPropagate.equals("on") || sPropagate.equals("true")) bPropagate = true;
			getAdminModule().setWorkAreaOwner(binder, Long.valueOf(ownerId), bPropagate);
		}
	}
	
	private void ajaxModifyGroup(ActionRequest request, 
			ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("applyBtn") || formData.containsKey("okBtn")) {
			Long groupId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID);
			String title = PortletRequestUtils.getStringParameter(request, "title", "");
			String description = PortletRequestUtils.getStringParameter(request, "description", "");
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
		String calendarStickyId = PortletRequestUtils.getStringParameter(request, WebKeys.CALENDAR_STICKY_ID, null);
		
		String eventType = PortletRequestUtils.getStringParameter(request, "eventType", "");
		if (!"".equals(eventType)) {
			EventsViewHelper.setCalendarDisplayEventType(portletSession, eventType);
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
			if (DefinitionHelper.getDefinition(entry.getEntryDef(), model, "//item[@name='entryBlogView']") == false) {
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
	Collection myTeams = getBinderModule().getTeamMemberships(user.getId());
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
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Binder binder = getBinderModule().getBinder(binderId);
		Map model = new HashMap();
		AccessControlController.setupAccess(this, request, response, binder, model);
		
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
		
		AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", Boolean.parseBoolean("true"));
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_ENTRY_ATTACHMENT);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
		adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_FILES_FROM_APPLET);

		//This replace has been done AJAX does not allow "&"
		String strURL = adapterUrl.toString();
		strURL = strURL.replaceAll("&", "&amp;");
		
		Map model = new HashMap();
		model.put(WebKeys.NAMESPACE, namespace);
		model.put(WebKeys.BINDER_ID, binderId);
		model.put(WebKeys.ENTRY_ID, entryId);
		model.put(WebKeys.ENTRY_ATTACHMENT_FILE_RECEIVER_URL, strURL);
		
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
		String strOpenInEditor = SsfsUtil.openInEditor(strURLValue, strOSInfo);
		
		Map model = new HashMap();
		model.put(WebKeys.NAMESPACE, namespace);
		model.put(WebKeys.ENTRY_ID, entryId);
		model.put(WebKeys.ENTRY_ATTACHMENT_URL, strURLValue);
		model.put(WebKeys.ENTRY_ATTACHMENT_EDITOR_TYPE, strOpenInEditor);
		model.put(WebKeys.URL_OS_INFO, strOSInfo);

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
		String strOpenInEditor = SsfsUtil.openInEditor(url, strOSInfo);
		
		Map model = new HashMap();
		model.put(WebKeys.NAMESPACE, namespace);
		model.put(WebKeys.ENTRY_ID, entryId);
		model.put(WebKeys.ENTRY_ATTACHMENT_URL, url);
		model.put(WebKeys.ENTRY_ATTACHMENT_EDITOR_TYPE, strOpenInEditor);
		model.put(WebKeys.URL_OS_INFO, strOSInfo);

		return new ModelAndView("definition_elements/view_entry_openfile", model);
	}
	
	private ModelAndView addFolderAttachmentOptions(RenderRequest request, 
			RenderResponse response) throws Exception {
	
		String namespace = PortletRequestUtils.getStringParameter(request, "namespace", "");
		String library = PortletRequestUtils.getStringParameter(request, "library", "");
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		
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
		
		//This replace has been done AJAX does not allow "&"
		String strRefreshURL = adapterFolderRefreshUrl.toString();
		//strRefreshURL = strRefreshURL.replaceAll("&", "&amp;");
		
		Map model = new HashMap();
		model.put(WebKeys.NAMESPACE, namespace);
		model.put(WebKeys.BINDER_IS_LIBRARY, library);
		model.put(WebKeys.BINDER_ID, binderId);
		model.put(WebKeys.FOLDER_ATTACHMENT_FILE_RECEIVER_URL, strURL);
		model.put(WebKeys.FOLDER_ATTACHMENT_APPLET_REFRESH_URL, strRefreshURL);
		
		//response.setContentType("text/xml");
		return new ModelAndView("definition_elements/folder_dropbox_add_attachments", model);
	}
	
	private ModelAndView ajaxStartMeeting(RenderRequest request, 
			RenderResponse response, int[] meetingType) throws Exception {
		Map model = new HashMap();
		
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		String entryId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		
		Set<Long> memberIds = new HashSet();
		memberIds.addAll(LongIdUtil.getIdsAsLongSet(request
				.getParameterValues("users")));
				
		Binder binder = null;
		if (binderId != null) {
			binder = getBinderModule().getBinder(binderId);
		}
		Entry entry = null;
		if (Validator.isNotNull(entryId)) {
			entry = getFolderModule().getEntry(binderId, Long.valueOf(entryId));
		}
		
		try {
			String meetingToken = getIcBrokerModule().addMeeting(memberIds,
					binder, entry, "", -1, "", meetingType);
			model.put(WebKeys.MEETING_TOKEN, meetingToken);
			response.setContentType("text/json");
			return new ModelAndView("forum/meeting_return", model);	
		} catch (ICException e) {
			model.put(WebKeys.MEETING_ERROR, NLT.get("meeting.start.error"));
			response.setContentType("text/json");
			return new ModelAndView("forum/meeting_return", model);	
		}
		
		
	
	}	
	
	private ModelAndView ajaxGetTeamMembers(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		
		Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
		
		if (WebHelper.isUserLoggedIn(request)) {
			try {
				Binder binder = getBinderModule().getBinder(binderId);
				model.put(WebKeys.TEAM_MEMBERS, getBinderModule().getTeamMembers(binder, true));
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
		Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
		if (binderId != null) {
			Binder binder = getBinderModule().getBinder(binderId);
			model.put(WebKeys.BINDER, binder);
			}
			
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
			String calendarStickyId = PortletRequestUtils.getStringParameter(request, WebKeys.CALENDAR_STICKY_ID, null);
			
			
			Map options = new HashMap();
			boolean eventsByEntry = PortletRequestUtils.getBooleanParameter(request, "ssEntryEvents", false);
			if (eventsByEntry) {
				// get events by entry
				Long entryId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID);

				User user = RequestContextHolder.getRequestContext().getUser();
				UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
				options.putAll(ListFolderHelper.getSearchFilter(this, request, userFolderProperties));
				
		       	List entries;
				if (binder instanceof Folder || binder instanceof Workspace) {
					Document searchFilter = SearchFiltersBuilder.buildGetEntryQuery(request, entryId);
					Map retMap = getBinderModule().executeSearchQuery(searchFilter, options);
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
				intervals.add(calendarViewRangeDates.getVisibleInterval());

		       	options.put(ObjectKeys.SEARCH_EVENT_DAYS, intervals);

		       	
		       	String start = DateTools.dateToString(calendarViewRangeDates.getVisibleStart(), DateTools.Resolution.SECOND);
		       	String end =  DateTools.dateToString(calendarViewRangeDates.getVisibleEnd(), DateTools.Resolution.SECOND);
		       	
		       	options.put(ObjectKeys.SEARCH_LASTACTIVITY_DATE_START, start);
		       	options.put(ObjectKeys.SEARCH_LASTACTIVITY_DATE_END, end);
	
		       	options.put(ObjectKeys.SEARCH_CREATION_DATE_START, start);
		       	options.put(ObjectKeys.SEARCH_CREATION_DATE_END, end);
			
				UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
				options.putAll(ListFolderHelper.getSearchFilter(this, request, userFolderProperties));
				
				
		       	List entries;
				if (binder instanceof Folder || binder instanceof Workspace) {
					Document searchFilter = SearchFiltersBuilder.buildFolderListQuery(request, binderIds);
					Map retMap = getBinderModule().executeSearchQuery(searchFilter, options);
					entries = (List) retMap.get(ObjectKeys.SEARCH_ENTRIES);
				} else {
					//a template
					entries = new ArrayList();
				}
				
				model.putAll(EventsViewHelper.getEventsBeans(entries, calendarViewRangeDates, portletSession, false));
			}
		} else {
			model.put(WebKeys.CALENDAR_VIEWBEAN , Collections.EMPTY_LIST);
		}
		
		response.setContentType("text/json");
		return new ModelAndView("forum/json/events", model);
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
					AbstractIntervalView.VisibleIntervalFormattedDates interval = calendarInterval.getVisibleInterval();
					
					Criteria crit = new Criteria();
					crit.add(in(ENTRY_TYPE_FIELD, new String[] {Constants.ENTRY_TYPE_ENTRY, 
							Constants.ENTRY_TYPE_REPLY}))
						.add(in(DOC_TYPE_FIELD, new String[] {Constants.DOC_TYPE_ENTRY}))
						.add(in(userListName, usersId))
						.add(between(Constants.EVENT_DATES_FIELD, 
								interval.startDate, interval.endDate));
					crit.addOrder(Order.asc(MODIFICATION_DATE_FIELD));
		
					Map searchResults = getBinderModule().executeSearchQuery(crit, 0, 1000);
					
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
								Map eventInfo = new HashMap();
								
								eventInfo.put("start", event.getDtStart().getTime());
								eventInfo.put("end", event.getDtEnd().getTime());
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
			Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
			Long entryId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID);
			
			FolderEntry entry = getFolderModule().getEntry(binderId, entryId);
			String newPriority = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TASK_PRIORITY, "");
			String newStatus = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TASK_STATUS, "");
			String newCompleted = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TASK_COMPLETED, "");
			
			Map formData = new HashMap();
			
			TaskHelper.adjustTaskAttributesDependencies(entry, formData, newPriority, newStatus, newCompleted);
			
			try {
				getFolderModule().modifyEntry(binderId, entryId, 
						new MapInputData(formData), null, null, null, null);
				
				model.put(WebKeys.ENTRY, entry);
				model.put(WebKeys.ENTRY_DEFINTION_ELEMENT_DATA, getDefinitionModule().getEntryDefinitionElements(entry.getEntryDef().getId()));
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
					else if(Validator.isNotNull(repositoryName)) {
						if(folder.isMirrored()) {
							if(!ObjectKeys.FI_ADAPTER.equals(repositoryName)) {
								model.put(WebKeys.AJAX_ERROR_MESSAGE, "entry.regularFileInMirroredFolder");					
							}
							else if(entryId != 0L) {
							// 	if entry is not null, the above expression guarantees that
							// 	its id is equal to entryId. So we don't have to refetch it.
								if(entry == null)
									entry = getFolderModule().getEntry(binderId, entryId);
								List<FileAttachment> fas = entry.getFileAttachments(ObjectKeys.FI_ADAPTER); // should be at most 1 in size
								for(FileAttachment fa : fas) {
									if(!fileName.equals(fa.getFileItem().getName())) {
										model.put(WebKeys.AJAX_ERROR_MESSAGE, "entry.mirroredFileMultiple");
										model.put(WebKeys.AJAX_ERROR_DETAIL, fa.getFileItem().getName());								
										break;					
									}
								}
							}
						}
						else {
							if(ObjectKeys.FI_ADAPTER.equals(repositoryName)) {
								model.put(WebKeys.AJAX_ERROR_MESSAGE, "entry.mirroredFileInRegularFolder");							
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
		String title = PortletRequestUtils.getStringParameter(request, WebKeys.URL_AJAX_VALUE,"");
		if(Validator.containsPathCharacters(title)) {
			model.put(WebKeys.AJAX_ERROR_MESSAGE, NLT.get("errorcode.title.pathCharacters", new Object[]{title}));
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
	
	private void ajaxSaveUserStatus(ActionRequest request, 
			ActionResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		String status = PortletRequestUtils.getStringParameter(request, "status", "");
    	Pattern p = Pattern.compile("([\\s]*)$");
    	Matcher m = p.matcher(status);
    	if (m.find()) {
			//Trim any trailing whitespace
    		status = status.substring(0, m.start(0));
    	}
		if (!status.equals(user.getStatus())) {
			if (status.length() > ObjectKeys.USER_STATUS_DATABASE_FIELD_LENGTH) {
				status = status.substring(0, ObjectKeys.USER_STATUS_DATABASE_FIELD_LENGTH);
			}
			getProfileModule().setStatus(status);
			getProfileModule().setStatusDate(new Date());
			getReportModule().addStatusInfo(user);
			//Add this to the user's mini blog folder
			Long miniBlogId = user.getMiniBlogId();
			Folder miniBlog = null;
			if (miniBlogId == null) {
				//The miniblog folder doesn't exist, so create it
				miniBlog = getProfileModule().addUserMiniBlog(user);
				
			} else {
				try {
					miniBlog = (Folder) getBinderModule().getBinder(miniBlogId);
					if (miniBlog.isDeleted()) {
						//The miniblog folder doesn't exist anymore, so try create it again
						miniBlog = getProfileModule().addUserMiniBlog(user);
					}
				} catch(NoBinderByTheIdException e) {
					//The miniblog folder doesn't exist anymore, so try create it again
					miniBlog = getProfileModule().addUserMiniBlog(user);
				}
			}
			if (miniBlog != null) {
				//Found the mini blog folder, go add this new entry
		        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, 
		        		DateFormat.SHORT, user.getLocale());
		        dateFormat.setTimeZone(user.getTimeZone());
				String mbTitle = dateFormat.format(new Date());
				Map data = new HashMap(); // Input data
				data.put(ObjectKeys.FIELD_ENTITY_TITLE, mbTitle);
				data.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION, status);
				Definition def = miniBlog.getDefaultEntryDef();
				if (def == null) {
					try {
						def = getDefinitionModule().getDefinitionByReservedId(ObjectKeys.DEFAULT_ENTRY_MINIBLOG_DEF);
					} catch (Exception ex) {}
				}
				if (def != null) {
					getFolderModule().addEntry(miniBlog.getId(), def.getId(), new MapInputData(data), null, null);
  				}
			}
		}
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
		
		getProfileModule().setSeen(user.getId(),getFolderModule().getEntry(binderId, entryId));
	}
	
	private void ajaxPinEntry(ActionRequest request, 
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
		int len = String.valueOf(entryId).length() + 1;
		int i = pinnedEntries.indexOf(String.valueOf(entryId) + ",");
		if (i >= 0) {
			pinnedEntries = pinnedEntries.substring(0, i) + pinnedEntries.substring(i+len);
		} else {
			pinnedEntries = String.valueOf(entryId) + "," + pinnedEntries;
		}
		
		getProfileModule().setUserProperty(user.getId(), binderId, ObjectKeys.USER_PROPERTY_PINNED_ENTRIES, pinnedEntries);
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
	
	private void ajaxVoteSurvey(ActionRequest request, ActionResponse response) throws AccessControlException, ReservedByAnotherUserException, WriteFilesException {
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID, -1);
		Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID, -1);
		String attributeName = PortletRequestUtils.getStringParameter(request, "attributeName", "");
		
		if (binderId == -1 || entryId == -1 || Validator.isNull(attributeName)) {
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
							question.vote(value);
						}
					}
				}
			}
		}

		survey.setVoteRequest();
		
		Map formData = new HashMap(); 
		formData.put(attributeName, surveyAttrValue.toString());
		getFolderModule().addVote(binderId, entryId, new MapInputData(formData), null);
	}
	
	private void ajaxVoteSurveyRemove(ActionRequest request, ActionResponse response) throws AccessControlException, ReservedByAnotherUserException, WriteFilesException {
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID, -1);
		Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID, -1);
		String attributeName = PortletRequestUtils.getStringParameter(request, "attributeName", "");
		
		if (binderId == -1 || entryId == -1 || Validator.isNull(attributeName)) {
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
	
	private ModelAndView ajaxVoteSurveyStatus(RenderRequest request, RenderResponse response) {
		Map model = new HashMap();
		model.put("status", true);
		return new ModelAndView("forum/json/vote_survey", model);	
	}
	
	private ModelAndView ajaxCheckStatus(RenderRequest request, RenderResponse response)  throws PortletRequestBindingException { 
		Map model = new HashMap();
		model.put("status", true);
		model.put("ss_operation_status", WebStatusTicket.findStatusTicket(PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_STATUS_TICKET_ID), request).getStatus());
		response.setContentType("text/xml");
		return new ModelAndView("common/check_status", model);	
	}

	private ModelAndView ajaxCheckIfLoggedIn(RenderRequest request, RenderResponse response)  throws PortletRequestBindingException { 
		User user = RequestContextHolder.getRequestContext().getUser();
		Map model = new HashMap();

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
	
	private ModelAndView ajaxGetMeetingRecords(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String recordsDivId = PortletRequestUtils.getStringParameter(request, "recordsDivId");
		String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.NAMING_PREFIX);
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
		int held = PortletRequestUtils.getIntParameter(request, "ssHeld", -1);
		
		model.put(WebKeys.RECORDS_DIV_ID, recordsDivId);
		model.put(WebKeys.URL_BINDER_ID, binderId);
		model.put(WebKeys.URL_ENTRY_ID, entryId);
		model.put(WebKeys.NAMING_PREFIX, namespace);
		
		User user = RequestContextHolder.getRequestContext().getUser();
		
		PortletSession portletSession = WebHelper.getRequiredPortletSession(request);
		if (held == -1) {
			if (portletSession.getAttribute("ssMeetingRecordsHeld") != null) {
				held = (Integer)portletSession.getAttribute("ssMeetingRecordsHeld");
			}
		}
		if (held == -1) {
			held = 31;
		}
		portletSession.setAttribute("ssMeetingRecordsHeld", held);
		
		
		Map meetingAttachments = new HashMap();
		try {
			meetingAttachments = getIcBrokerModule().getUserMeetingAttachments(user.getZonName(), held);
		} catch(ICException e) {
			logger.warn("Cannot communicate with Zon meeting server: " + e.getLocalizedMessage());
		}
	
		model.put("ss_meeting_records", meetingAttachments);
		model.put("ssHeld", held);
		
		response.setContentType("text/xml");
		return new ModelAndView("forum/meeting_records_return", model);
	}
	
	private void ajaxAttacheMeetingRecords(ActionRequest request, ActionResponse response) throws Exception {
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
		Map formData = request.getParameterMap();
		
		
		List meetingRecordIds = Arrays.asList(PortletRequestUtils.getStringParameters(request, "ssMeetingRecordId"));
		List meetingDocumentIds = Arrays.asList(PortletRequestUtils.getStringParameters(request, "ssMeetingDocumentId"));
		
		// sort documents and records by meeting id
		Map<String, Map<String, Object>> attachmentIds = new HashMap();
		Iterator<String> meetingDocumentIdsIt = meetingDocumentIds.iterator();
		while (meetingDocumentIdsIt.hasNext()) {
			String meetingDocumentId = meetingDocumentIdsIt.next();
			if (meetingDocumentId.indexOf("/") == -1) {
				continue;
			}
			String meetingId = meetingDocumentId.substring(0, meetingDocumentId.indexOf("/"));
			String documentId = meetingDocumentId.substring(meetingDocumentId.indexOf("/") + 1);
			if (attachmentIds.get(meetingId) == null) {
				attachmentIds.put(meetingId, new HashMap());
				attachmentIds.get(meetingId).put("docs", new ArrayList());
				attachmentIds.get(meetingId).put("records", new HashMap());
				((Map)attachmentIds.get(meetingId).get("records")).put("add", new ArrayList());
				((Map)attachmentIds.get(meetingId).get("records")).put("addAndDelete", new ArrayList());
			}
			((List)attachmentIds.get(meetingId).get("docs")).add(documentId);
		}
		
		Iterator<String> meetingRecordIdsIt = meetingRecordIds.iterator();
		while (meetingRecordIdsIt.hasNext()) {
			String meetingRecordId = meetingRecordIdsIt.next();
			String meetingRecordOperation = PortletRequestUtils.getStringParameter(request, "ssMeetingRecordsOperation" + meetingRecordId, "");
			if (!"".equals(meetingRecordOperation)) {
				String meetingId = meetingRecordId.substring(0, meetingRecordId.indexOf("-"));
				if (attachmentIds.get(meetingId) == null) {
					attachmentIds.put(meetingId, new HashMap());
					attachmentIds.get(meetingId).put("docs", new ArrayList());
					attachmentIds.get(meetingId).put("records", new HashMap());
					((Map)attachmentIds.get(meetingId).get("records")).put("add", new ArrayList());
					((Map)attachmentIds.get(meetingId).get("records")).put("addAndDelete", new ArrayList());
				}
				((List)((Map)attachmentIds.get(meetingId).get("records")).get(meetingRecordOperation)).add(meetingRecordId);
			}
		}
		
		List<DocumentDownload> documents = new ArrayList();
		
		Iterator<Map.Entry<String, Map<String, Object>>> attachmentIdsIt = attachmentIds.entrySet().iterator();
		while (attachmentIdsIt.hasNext()) {
			Map.Entry<String, Map<String, Object>> meetingAttachments = attachmentIdsIt.next();
			String meetingId = meetingAttachments.getKey();
			Map<String, Object> recordsAndDocs = meetingAttachments.getValue();
			List docIds  = (List)recordsAndDocs.get("docs");
			List addRecords = (List)((Map)recordsAndDocs.get("records")).get("add");
			List addAndDeleteRecords = (List)((Map)recordsAndDocs.get("records")).get("addAndDelete");
			
			Map meetingRecords = getIcBrokerModule().getMeetingRecords(meetingId);
			List meetingDocs = getIcBrokerModule().getDocumentList(meetingId);
			
			// list add records
			Iterator<String> addRecordsIt = addRecords.iterator();
			while (addRecordsIt.hasNext()) {
				String recordId = addRecordsIt.next();
				Map recordsToAddAsMap = (Map)meetingRecords.get(recordId);
				documents.addAll(DocumentDownload.fromRecordsList(meetingId, "add", (List)recordsToAddAsMap.get(RecordType.flash.name())));
				documents.addAll(DocumentDownload.fromRecordsList(meetingId, "add", (List)recordsToAddAsMap.get(RecordType.audio.name())));
				documents.addAll(DocumentDownload.fromRecordsList(meetingId, "add", (List)recordsToAddAsMap.get(RecordType.chat.name())));
			}
			
			// list addAndDelete records
			Iterator<String> addAndDeleteRecordsIt = addAndDeleteRecords.iterator();
			while (addAndDeleteRecordsIt.hasNext()) {
				String recordId = addAndDeleteRecordsIt.next();
				Map recordsToAddAndDeleteAsMap = (Map)meetingRecords.get(recordId);
				
				documents.addAll(DocumentDownload.fromRecordsList(meetingId, "addAndDelete", (List)recordsToAddAndDeleteAsMap.get(RecordType.flash.name())));
				documents.addAll(DocumentDownload.fromRecordsList(meetingId, "addAndDelete", (List)recordsToAddAndDeleteAsMap.get(RecordType.audio.name())));
				documents.addAll(DocumentDownload.fromRecordsList(meetingId, "addAndDelete", (List)recordsToAddAndDeleteAsMap.get(RecordType.chat.name())));
			}
			
            // Prune originals if merged version exists
			//
			// Zon meeting archiver stores original files as
			//   foo.jpg
			// Any markup is then applied and the file is called
			//   foo.jpg.merged
			//
			// If no markup has occurred, only the bare jpg file is
			// there.  So we walk through the array looking for merged
			// files and removing the original from the list to retrieve.
			List docsToRemove = new ArrayList();
			Iterator<List> meetingDocsIt = meetingDocs.iterator();
			while (meetingDocsIt.hasNext()) {
				List<String> doc = meetingDocsIt.next();
				String docId = doc.get(0);
				if (docId.endsWith(".merged")) {
					docsToRemove.add(docId.substring(0, docId.length() - 7));
				}
			}
			
			// collect documents list
			meetingDocsIt = meetingDocs.iterator();
			while (meetingDocsIt.hasNext()) {
				List<String> doc = meetingDocsIt.next();
				String docId = doc.get(0);
				if (docsToRemove.contains(docId)) {
					continue;
				}
				
				String shortId = docId;
				if (docId.indexOf("/") > -1) {
					shortId = docId.substring(0, docId.indexOf("/"));
				}
				if (docIds.contains(shortId) && 
						(docId.endsWith(".jpg") || 
						docId.endsWith(".jpg.merged") || 
						docId.endsWith(".gif"))) {
					documents.add(DocumentDownload.fromDocument(meetingId, doc));
				}
			}
		}
		
		Map fileMap = new HashMap();
		int fileCounter = 1;
		Iterator<DocumentDownload> documentsIt = documents.iterator();
		while (documentsIt.hasNext()) {
			DocumentDownload doc = documentsIt.next();
			if (doc.getType() != null && doc.getType().equals(RecordType.flash)) {
				doc.setUrl(doc.getUrl().substring(0, doc.getUrl().lastIndexOf("/") + 1) + "movie.zip");
			}
			
			HttpClient httpClient = new HttpClient();
			GetMethod getMethod = new GetMethod(doc.getUrl());
			getMethod.setRequestHeader("Authorization", "Basic " + getIcBrokerModule().getBASE64AuthorizationToken());			
			try{

		          //execute the method
		          int statusCode =
		                 httpClient.executeMethod(getMethod);

		          if (statusCode != 200) {
		        	  continue;
		          }

		          //get the resonse as an InputStream
		          InputStream in =
		                 getMethod.getResponseBodyAsStream();

		          String prefix = "";
		          String orginalFileName = "";
		          if (doc.getType() == null) {
		        	  prefix = doc.getId().substring(doc.getId().indexOf("/") + 1, doc.getId().length());
		        	  if (prefix.endsWith(".merged")) {
		        		  prefix = prefix.substring(0, prefix.indexOf(".merged"));
		        	  }
		        	  orginalFileName = doc.getMeetingId() + " " + prefix;
		        	  prefix += "_";
		          } else if (doc.getType().equals(RecordType.flash)) {
		        	  prefix = doc.getMeetingId() + "_movie.zip_";
		        	  orginalFileName = doc.getMeetingId() + " movie.zip";
		          } else if (doc.getType().equals(RecordType.audio)) {
		        	  prefix = doc.getMeetingId() + "_audio.mp3_";
		        	  orginalFileName = doc.getMeetingId() + " audio.mp3";
		          } else if (doc.getType().equals(RecordType.chat)) {
			       	  prefix = doc.getMeetingId() + "_chat.txt_";
			       	  orginalFileName = doc.getMeetingId() + " chat.txt";
			      } else {
			    	  continue;
			      }
		          
		          File file = TempFileUtil.createTempFileWithContent(prefix, in);
		          fileMap.put("ss_attachFile" + fileCounter++, new SimpleMultipartFile(orginalFileName, TempFileUtil.openTempFile(file.getName())));

		          in.close();

			} catch(HttpException e){
				logger.error(e);
			} catch(IOException e){
				logger.error(e);
			} finally{
				//release the connection
				getMethod.releaseConnection();
			}
		}
		
		String strFilesErrors = "";
		try {
			getFolderModule().modifyEntry(binderId, entryId, new MapInputData(formData), fileMap, null, null, null);
		} catch (WriteFilesException wf) {
			strFilesErrors = wf.toString();
		}
		
		if ("".equals(strFilesErrors)) {
			documentsIt = documents.iterator();
			while (documentsIt.hasNext()) {
				DocumentDownload doc = documentsIt.next();
		          if (doc.getOperation() != null && doc.getOperation().equals("addAndDelete")) {
		        	  getIcBrokerModule().removeRecordings(doc.getMeetingId(), doc.getOrginalUrl());
		          }
			}
		}
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
		
		response.setContentType("text/xml");
		return new ModelAndView("forum/json/upload_progress_status", model);
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
	
}
