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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.portlet.bind.PortletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.calendar.CalendarViewRangeDates;
import com.sitescape.team.calendar.EventsViewHelper;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.DashboardPortlet;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.HistoryStamp;
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
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.folder.FolderModule.FolderOperation;
import com.sitescape.team.module.ic.DocumentDownload;
import com.sitescape.team.module.ic.ICBrokerModule;
import com.sitescape.team.module.ic.ICException;
import com.sitescape.team.module.ic.RecordType;
import com.sitescape.team.module.ical.impl.IcalModuleImpl;
import com.sitescape.team.module.profile.index.ProfileIndexUtils;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.portlet.binder.AccessControlController;
import com.sitescape.team.portlet.binder.AdvancedSearchController;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.search.filter.SearchFilter;
import com.sitescape.team.search.filter.SearchFilterKeys;
import com.sitescape.team.search.filter.SearchFilterRequestParser;
import com.sitescape.team.search.filter.SearchFiltersBuilder;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.OperationAccessControlException;
import com.sitescape.team.ssfs.util.SsfsUtil;
import com.sitescape.team.survey.Answer;
import com.sitescape.team.survey.Question;
import com.sitescape.team.survey.Survey;
import com.sitescape.team.survey.SurveyModel;
import com.sitescape.team.task.TaskHelper;
import com.sitescape.team.task.TaskHelper.FilterType;
import com.sitescape.team.util.LongIdUtil;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SimpleMultipartFile;
import com.sitescape.team.util.TagUtil;
import com.sitescape.team.util.TempFileUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractControllerRetry;
import com.sitescape.team.web.tree.DomTreeBuilder;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.Clipboard;
import com.sitescape.team.web.util.DashboardHelper;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.Favorites;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.Tabs;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.team.web.util.WebStatusTicket;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.Validator;
/**
 * @author Peter Hurley
 *
 */
public class AjaxController  extends SAbstractControllerRetry {
	
	static Pattern replacePtrn = Pattern.compile("([\\p{Punct}&&[^\\*]])");
	
	//caller will retry on OptimisiticLockExceptions
	public void handleActionRequestWithRetry(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		if (WebHelper.isUserLoggedIn(request)) {
			String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
			if (op.equals(WebKeys.OPERATION_SAVE_COLUMN_POSITIONS)) {
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
			} else if (op.equals(WebKeys.OPERATION_SAVE_DASHBOARD_LAYOUT)) {
				ajaxSaveDashboardLayout(request, response);
			} else if (op.equals(WebKeys.OPERATION_SHOW_ALL_DASHBOARD_COMPONENTS) || 
					op.equals(WebKeys.OPERATION_HIDE_ALL_DASHBOARD_COMPONENTS)) {
				ajaxShowHideAllDashboardComponents(request, response);
			} else if (op.equals(WebKeys.OPERATION_DASHBOARD_HIDE_COMPONENT) || 
					op.equals(WebKeys.OPERATION_DASHBOARD_SHOW_COMPONENT) ||
					op.equals(WebKeys.OPERATION_DASHBOARD_DELETE_COMPONENT)) {
				ajaxChangeDashboardComponent(request, response);
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
			} else if (op.equals(WebKeys.OPERATION_ADD_TO_CLIPBOARD)) {
				ajaxAddToClipboard(request, response);
			} else if (op.equals(WebKeys.OPERATION_CLEAR_CLIPBOARD)) {
				ajaxClearClipboard(request, response);
			} else if (op.equals(WebKeys.OPERATION_REMOVE_FROM_CLIPBOARD)) {
				ajaxRemoveFromClipboard(request, response);
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
			} else if (op.equals(WebKeys.OPERATION_ATTACHE_MEETING_RECORDS)) {
				ajaxAttacheMeetingRecords(request, response);
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

			//Check for calls from "ss_fetch_url" (which don't output in xml format)
			if (op.equals(WebKeys.OPERATION_DASHBOARD_HIDE_COMPONENT) || 
					op.equals(WebKeys.OPERATION_DASHBOARD_SHOW_COMPONENT) ||
					op.equals(WebKeys.OPERATION_DASHBOARD_DELETE_COMPONENT) || 
					op.equals(WebKeys.OPERATION_DASHBOARD_SEARCH_MORE) || 
					op.equals(WebKeys.OPERATION_DASHBOARD_TEAM_MORE)) {
				return new ModelAndView("forum/fetch_url_return", model);
			} else if(op.equals(WebKeys.OPERATION_SHOW_BLOG_REPLIES)) {
				return new ModelAndView("forum/fetch_url_return", model);
			} else if (op.equals(WebKeys.OPERATION_CONFIGURE_FOLDER_COLUMNS) ||
					op.equals(WebKeys.OPERATION_SUBSCRIBE) || op.equals(WebKeys.OPERATION_ENTRY_SUBSCRIBE)) {
				return new ModelAndView("forum/fetch_url_return", model);
			} else if (op.equals(WebKeys.OPERATION_UPLOAD_IMAGE_FILE)) {
				return new ModelAndView("forum/fetch_url_return", model);
			} else if (op.equals(WebKeys.OPERATION_MODIFY_GROUP)) {
				return new ModelAndView("forum/fetch_url_return", model);
			} else if (op.equals(WebKeys.OPERATION_SHOW_MY_TEAMS)) {
				return new ModelAndView("forum/fetch_url_return", model);
			}
			
			response.setContentType("text/xml");			
			if (op.equals(WebKeys.OPERATION_UNSEEN_COUNTS)) {
				return new ModelAndView("forum/unseen_counts", model);
			} else if (op.equals(WebKeys.OPERATION_SAVE_COLUMN_POSITIONS)) {
				return new ModelAndView("forum/save_column_positions_return", model);
			} else if (op.equals(WebKeys.OPERATION_SAVE_ENTRY_WIDTH)) {
				return new ModelAndView("forum/save_entry_width_return", model);
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
			} else if (op.equals(WebKeys.OPERATION_ADD_FAVORITE_BINDER) || 
					op.equals(WebKeys.OPERATION_ADD_FAVORITES_CATEGORY) || 
					op.equals(WebKeys.OPERATION_SAVE_FAVORITES)) {
				return new ModelAndView("forum/favorites_return", model);
			} else if (op.equals(WebKeys.OPERATION_GET_FAVORITES_TREE)) {
				return new ModelAndView("forum/favorites_tree", model);
			} else if (op.equals(WebKeys.OPERATION_SHOW_HELP_PANEL)) {
				return new ModelAndView("forum/ajax_return", model);
			} else if (op.equals(WebKeys.OPERATION_GET_ACCESS_CONTROL_TABLE)) {
				return new ModelAndView("binder/access_control_table", model);
			} else if (op.equals(WebKeys.OPERATION_START_MEETING)) {
				return new ModelAndView("forum/meeting_return", model);	
			} else if (op.equals(WebKeys.OPERATION_SCHEDULE_MEETING)) {
				return new ModelAndView("forum/meeting_return", model);	
			} else if (op.equals(WebKeys.OPERATION_VOTE_SURVEY)) {
				return new ModelAndView("forum/json/vote_survey", model);	
			} else if (op.equals(WebKeys.OPERATION_UPLOAD_ICALENDAR_FILE)) {
				return new ModelAndView("forum/json/icalendar_upload", model);
			} else if (op.equals(WebKeys.OPERATION_UPDATE_TASK)) {
				return  new ModelAndView("forum/json/update_task", model);
			}

			return new ModelAndView("forum/ajax_return", model);
		}
		
		//The user is logged in
		if (op.equals(WebKeys.OPERATION_UNSEEN_COUNTS)) {
			return ajaxGetUnseenCounts(request, response);
			
		} else if (op.equals(WebKeys.OPERATION_ADD_FAVORITE_BINDER) || 
				op.equals(WebKeys.OPERATION_ADD_FAVORITES_CATEGORY) ||
				op.equals(WebKeys.OPERATION_GET_FAVORITES_TREE) ||
				op.equals(WebKeys.OPERATION_SAVE_FAVORITES)) {
			return ajaxGetFavoritesTree(request, response);
		} else if (op.equals(WebKeys.OPERATION_SAVE_COLUMN_POSITIONS)) {
			return new ModelAndView("forum/save_column_positions_return");
			
		} else if (op.equals(WebKeys.OPERATION_CONFIGURE_FOLDER_COLUMNS)) {
			return ajaxConfigureFolderColumns(request, response);
		} else if (op.equals(WebKeys.OPERATION_SUBSCRIBE)) {
			return ajaxSubscribe(request, response);
		} else if (op.equals(WebKeys.OPERATION_ENTRY_SUBSCRIBE)) {
			return ajaxEntrySubscribe(request, response);
		} else if (op.equals(WebKeys.OPERATION_SAVE_ENTRY_WIDTH)) {
			return ajaxSaveEntryWidth(request, response);
			
		} else if (op.equals(WebKeys.OPERATION_SAVE_ENTRY_HEIGHT)) {
			return ajaxSaveEntryHeight(request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MODIFY_TAGS)) {
			return ajaxShowTags(request, response);

		} else if (op.equals(WebKeys.OPERATION_USER_LIST_SEARCH)) {
			return ajaxUserListSearch(request, response);

		} else if (op.equals(WebKeys.OPERATION_FIND_USER_SEARCH) ||
				op.equals(WebKeys.OPERATION_FIND_PLACES_SEARCH) || 
				op.equals(WebKeys.OPERATION_FIND_ENTRIES_SEARCH) || 
				op.equals(WebKeys.OPERATION_FIND_TAG_SEARCH)) {
			return ajaxFindUserSearch(request, response);
		} else if (op.equals(WebKeys.OPERATION_FIND_TAG_WIDGET)) {
			return ajaxGetTags(request, response);
		} else if (op.equals(WebKeys.OPERATION_FIND_WORKFLOWS_WIDGET)) {
			return ajaxGetWorkflows(request, response);
		} else if (op.equals(WebKeys.OPERATION_FIND_WORKFLOW_STEP_WIDGET)) {
			return ajaxGetWorkflowSteps(request, response);
		} else if (op.equals(WebKeys.OPERATION_FIND_ENTRY_TYPES_WIDGET)) {
			return ajaxGetEntryTypes(request, response);
		} else if (op.equals(WebKeys.OPERATION_FIND_ENTRY_FIELDS_WIDGET)) {
			return ajaxGetEntryFields(request, response);
		} else if (op.equals(WebKeys.OPERATION_FIND_USERS_WIDGET)) {
			return ajaxGetUsers(request, response);
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

		} else if (op.equals(WebKeys.OPERATION_WORKSPACE_TREE)) {
			return ajaxGetWorkspaceTree(request, response);

		} else if (op.equals(WebKeys.OPERATION_DASHBOARD_HIDE_COMPONENT) || 
				op.equals(WebKeys.OPERATION_DASHBOARD_SHOW_COMPONENT) || 
				op.equals(WebKeys.OPERATION_DASHBOARD_DELETE_COMPONENT)) {
			return ajaxGetDashboardComponent(request, response);

		} else if (op.equals(WebKeys.OPERATION_DASHBOARD_SEARCH_MORE)) {
			return ajaxGetDashboardSearchMore(request, response);
			
		} else if (op.equals(WebKeys.OPERATION_DASHBOARD_TEAM_MORE)) {
			return ajaxGetDashboardTeamMore(request, response);

		} else if (op.equals(WebKeys.OPERATION_SHOW_MY_TEAMS)) {
			return ajaxGetMyTeams(request, response);

		} else if(op.equals(WebKeys.OPERATION_SHOW_BLOG_REPLIES)) {
			return ajaxGetBlogReplies(request, response);
		} else if (op.equals(WebKeys.OPERATION_SAVE_RATING)) {
			return ajaxGetEntryRating(request, response);
		
		} else if (op.equals(WebKeys.OPERATION_SHOW_HELP_PANEL)) {
			return ajaxShowHelpPanel(request, response);
		
		} else if (op.equals(WebKeys.OPERATION_ADD_TAB)) {
			return ajaxAddTab(request, response);
			
		} else if (op.equals(WebKeys.OPERATION_DELETE_TAB)) {
			return ajaxDeleteTab(request, response);
			
		} else if (op.equals(WebKeys.OPERATION_SET_CURRENT_TAB)) {
			return ajaxSetCurrentTab(request, response);
			
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
		} else if (op.equals(WebKeys.OPERATION_GET_CLIPBOARD_USERS)) {
			return ajaxGetClipboardUsers(request, response);
		} else if (op.equals(WebKeys.OPERATION_SET_BINDER_OWNER_ID)) {
			return ajaxGetBinderOwner(request, response);
		} else if (op.equals(WebKeys.OPERATION_MODIFY_GROUP)) {
			return ajaxGetGroup(request, response);			
		} else if (op.equals(WebKeys.OPERATION_FIND_CALENDAR_EVENTS)) {
			return ajaxFindCalendarEvents(request, response);
		} else if (op.equals(WebKeys.OPERATION_FIND_ENTRY_FOR_FILE)) {
			return ajaxFindEntryForFile(request, response);
		} else if (op.equals(WebKeys.OPERATION_CHECK_BINDER_TITLE)) {
			return ajaxCheckBinderTitle(request, response);
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
		} else if (op.equals(WebKeys.OPERATION_CHECK_STATUS)) {
			return ajaxCheckStatus(request, response);
		} else if (op.equals(WebKeys.OPERATION_WIKILINK_FORM)) {
			return ajaxWikiLinkForm(request, response);
		} else if (op.equals(WebKeys.OPERATION_FIND_PLACE_FORM)) {
			return ajaxFindPlaceForm(request, response);
		} else if (op.equals(WebKeys.OPERATION_UPLOAD_ICALENDAR_FILE)) {
			return ajaxUploadICalendarFileStatus(request, response);
		} else if (op.equals(WebKeys.OPERATION_GET_CHANGE_LOG_ENTRY_FORM)) {
			return ajaxGetChangeLogEntryForm(request, response);
		} else if (op.equals(WebKeys.OPERATION_GET_MEETING_RECORDS)) {
			return ajaxGetMeetingRecords(request, response);
		}
		
		return ajaxReturn(request, response);
	} 

	private ModelAndView ajaxGetUsers(RenderRequest request, RenderResponse response) {
		Map model = new HashMap();
		User currentUser = RequestContextHolder.getRequestContext().getUser();
		String search = PortletRequestUtils.getStringParameter(request, "searchText", "");
		String pagerText = PortletRequestUtils.getStringParameter(request, "pager", "");

		List users = new ArrayList();
		if (WebHelper.isUserLoggedIn(request)) {
				
			Map options = new HashMap();
			options.put(ObjectKeys.SEARCH_SORT_BY, EntityIndexUtils.SORT_TITLE_FIELD);
			options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(false));
			
			int startPageNo = 1;
			int endPageNo = 10;
			if (!pagerText.equals("")) {
				String[] pagesNos = pagerText.split(";");
				startPageNo = Integer.parseInt(pagesNos[0]);
				endPageNo = Integer.parseInt(pagesNos[1]);
			}
	
			options.put(ObjectKeys.SEARCH_MAX_HITS, (endPageNo - startPageNo) + 1);
			options.put(ObjectKeys.SEARCH_OFFSET, startPageNo - 1);
			
			if (!search.equals("")) {
				SearchFilter searchTermFilter = new SearchFilter();
				search += "*";
				
				searchTermFilter.addTitleFilter(search);
				searchTermFilter.addLoginNameFilter(search);
							
				options.put(ObjectKeys.SEARCH_SEARCH_FILTER, searchTermFilter.getFilter());
			}
			
			Map entries = getProfileModule().getUsers(currentUser.getParentBinder().getId(), options);
			users = (List)entries.get(ObjectKeys.SEARCH_ENTRIES);
		
			
			int searchCountTotal = ((Integer)entries.get(ObjectKeys.SEARCH_COUNT_TOTAL)).intValue();
			int totalSearchRecordsReturned = ((Integer)entries.get(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED)).intValue();
			
			
			if (startPageNo + totalSearchRecordsReturned < searchCountTotal) {
				Map next = new HashMap();
				next.put("start", startPageNo + totalSearchRecordsReturned);
				if (startPageNo + totalSearchRecordsReturned + 10 < searchCountTotal) {
					next.put("end",  startPageNo + totalSearchRecordsReturned + 10);
				} else {
					next.put("end",  searchCountTotal);
				}
				model.put(WebKeys.PAGE_NEXT, next);
			}
	
			if (startPageNo > 1) {
				Map prev = new HashMap();
				prev.put("start", startPageNo - 10);
				prev.put("end", startPageNo - 1);
				model.put(WebKeys.PAGE_PREVIOUS, prev);
			}
			if (startPageNo == 1 && (search.equals("") || search.equals("*"))) {
				// add relative option "current user" and "me"
				Map currentUserPlaceholder = new HashMap();
				currentUserPlaceholder.put("title", NLT.get("searchForm.currentUserTitle"));
				currentUserPlaceholder.put("_docId", SearchFilterKeys.CurrentUserId);
				
				
				Map mePlaceholder = new HashMap();
				mePlaceholder.put("title", NLT.get("searchForm.meTitle") + " (" + currentUser.getTitle() + ")");
				mePlaceholder.put("_docId", currentUser.getId());
				
				
				users.add(0, mePlaceholder);
				users.add(0, currentUserPlaceholder);
			} 
		}

		model.put(WebKeys.USERS, users);
		response.setContentType("text/json");
		return new ModelAndView("forum/json/find_users_widget", model);
	}
	private ModelAndView ajaxGetEntryTypes(RenderRequest request, RenderResponse response) {
		
		Map model = new HashMap();
		List entryTypes = new ArrayList();
		if (WebHelper.isUserLoggedIn(request)) {
			entryTypes = DefinitionHelper.getDefinitions(Definition.FOLDER_ENTRY);
		}
		model.put(WebKeys.ENTRY, entryTypes);
		response.setContentType("text/json");
		return new ModelAndView("forum/json/find_entry_types_widget", model);
	}
	
	private ModelAndView ajaxGetEntryFields(RenderRequest request, RenderResponse response) {
		String entryTypeId = PortletRequestUtils.getStringParameter(request,WebKeys.FILTER_ENTRY_DEF_ID, "");
		String entryField = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.FilterElementNameField, "");
		
		
		Map model = new HashMap();
		response.setContentType("text/json");
		
		Map fieldsData = new HashMap();
		if (WebHelper.isUserLoggedIn(request)) {
			fieldsData = getDefinitionModule().getEntryDefinitionElements(entryTypeId);
		}
	
		if (entryField.equals("")) {
			model.put(WebKeys.ENTRY_DEFINTION_ELEMENT_DATA, fieldsData);
			return new ModelAndView("forum/json/find_entry_fields_widget", model);
		} else {
			Map valuesData = new HashMap();
			if (WebHelper.isUserLoggedIn(request)) {
				Map entryFieldMap = (Map) fieldsData.get(entryField); 
				if (entryFieldMap != null) {
					valuesData = (Map) entryFieldMap.get("values");
					String fieldType = (String)entryFieldMap.get("type");
					if (valuesData == null && "checkbox".equals(fieldType)) {
						valuesData = new HashMap();
						valuesData.put(Boolean.TRUE.toString(), NLT.get("searchForm.checkbox.selected"));
						valuesData.put(Boolean.FALSE.toString(), NLT.get("searchForm.checkbox.unselected"));
					}
				}
			}
			model.put(WebKeys.ENTRY_DEFINTION_ELEMENT_DATA, valuesData);
			return new ModelAndView("forum/json/find_entry_field_values_widget", model);
		}
 	}

	private ModelAndView ajaxGetWorkflowSteps(RenderRequest request, RenderResponse response) {
		String workflowId = PortletRequestUtils.getStringParameter(request, "workflowId", "");
		Map model = new HashMap();
		
		Map stateData = new HashMap();
		if (WebHelper.isUserLoggedIn(request)) {
			stateData = getDefinitionModule().getWorkflowDefinitionStates(workflowId);
		}
		model.put(WebKeys.WORKFLOW_DEFINTION_STATE_DATA, stateData);
		
		response.setContentType("text/json");
		return new ModelAndView("forum/json/find_workflow_steps_widget", model);
	}
	private ModelAndView ajaxGetTags(RenderRequest request, RenderResponse response) {
		String searchText = PortletRequestUtils.getStringParameter(request, "searchText", "");
		String findType = PortletRequestUtils.getStringParameter(request, "findType", "tags");
		String pagerText = PortletRequestUtils.getStringParameter(request, "pager", "");
		
		Map model = new HashMap();
		List tags = new ArrayList();
			
		if (WebHelper.isUserLoggedIn(request)) {
			int startPageNo = 1;
			int endPageNo = 10;
			if (!pagerText.equals("")) {
				String[] pagesNos = pagerText.split(";");
				startPageNo = Integer.parseInt(pagesNos[0]);
				endPageNo = Integer.parseInt(pagesNos[1]);
			}
			
			String wordRoot = searchText;
			int i = wordRoot.indexOf("*");
			if (i > 0) wordRoot = wordRoot.substring(0, i);
			
			tags = getBinderModule().getSearchTags(wordRoot, findType);
			int searchCountTotal = tags.size();
			
			if (tags.size() > startPageNo) {
				if (tags.size() < endPageNo) endPageNo = tags.size();
				tags = tags.subList(startPageNo - 1, endPageNo);
			}
			
			if (endPageNo < searchCountTotal) {
				Map next = new HashMap();
				next.put("start", endPageNo + 1);
				if (endPageNo + 10 < searchCountTotal) {
					next.put("end",  endPageNo + 10);
				} else {
					next.put("end",  searchCountTotal);
				}
				model.put(WebKeys.PAGE_NEXT, next);
			}
	
			if (startPageNo > 1) {
				Map prev = new HashMap();
				prev.put("start", startPageNo - 10);
				prev.put("end", startPageNo - 1);
				model.put(WebKeys.PAGE_PREVIOUS, prev);
			}
		
		}
		model.put(WebKeys.TAGS, tags);
		
		response.setContentType("text/json");
		return new ModelAndView("forum/json/find_tags_widget", model);
	}
	
	private ModelAndView ajaxGetWorkflows(RenderRequest request, RenderResponse response) {
		
		List workflows = new ArrayList();
		if (WebHelper.isUserLoggedIn(request)) {
			workflows = DefinitionHelper.getDefinitions(Definition.WORKFLOW);
		}
		
		Map model = new HashMap();
		model.put(WebKeys.WORKFLOW_DEFINITION_MAP, workflows);
		response.setContentType("text/json");
		return new ModelAndView("forum/json/find_workflows_widget", model);
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
		getFolderModule().setUserRating(binderId, entryId, rating);
	}
	
	private String getModelLink(ActionResponse response, Binder binder,
			Entry entry) {
		AdaptedPortletURL adapterUrl = AdaptedPortletURL
				.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);

		if (entry == null && binder != null) {
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binder.getId()
					.toString());
			adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, binder
					.getEntityType().toString());
		} else if (entry != null && binder != null) {
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binder.getId()
					.toString());
			adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entry.getId()
					.toString());
			adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, entry
					.getEntityType().toString());
		} else {
			// no model no link
			return "";
		}

		return adapterUrl.toString();
	}
	
	private void ajaxSetUiTheme(ActionRequest request,
			ActionResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		String uiTheme = PortletRequestUtils.getStringParameter(request, "theme", "");
		if (uiTheme.length() > 50) {
			uiTheme = uiTheme.substring(0,50);
		} 
		
		Map updates = new HashMap();
		updates.put(ObjectKeys.FIELD_PRINCIPAL_THEME, uiTheme);
		getProfileModule().modifyEntry(user.getParentBinder().getId(), user.getId(), new MapInputData(updates));
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


	private void ajaxShowHideAllDashboardComponents(ActionRequest request,
			ActionResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		Boolean showAllComponents = Boolean.TRUE;
		if (op.equals(WebKeys.OPERATION_HIDE_ALL_DASHBOARD_COMPONENTS)) showAllComponents = Boolean.FALSE;
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		getProfileModule().setUserProperty(user.getId(), binderId, 
					ObjectKeys.USER_PROPERTY_DASHBOARD_SHOW_ALL, showAllComponents);
	}
	private void ajaxSaveDashboardLayout(ActionRequest request, 
			ActionResponse response) throws Exception {
		//Save the order of the dashboard components
		String layout = PortletRequestUtils.getStringParameter(request, "dashboard_layout", "");
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
		Binder binder = getBinderModule().getBinder(binderId);
		String scope = PortletRequestUtils.getStringParameter(request, "scope", DashboardHelper.Local);
		DashboardHelper.saveComponentOrder(layout, binder, scope);
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
		if (binder instanceof TemplateBinder) model.put(WebKeys.CONFIG_JSP_STYLE, "template");
		UserProperties userProperties;
		Map columns = null;
		if (binderId == null) {
			userProperties = getProfileModule().getUserProperties(null);
			columns = (Map) userProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_RESULTS_FOLDER_COLUMNS);
		} else {
			userProperties = getProfileModule().getUserProperties(null, binderId);
			columns = (Map) userProperties.getProperty(ObjectKeys.USER_PROPERTY_FOLDER_COLUMNS);
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
	
	private ModelAndView ajaxSubscribe(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);				
		Binder binder = getBinderModule().getBinder(binderId);
		Subscription sub = getBinderModule().getSubscription(binderId);
		model.put(WebKeys.SUBSCRIPTION, sub);
		model.put(WebKeys.SCHEDULE_INFO, getBinderModule().getNotificationConfig(binderId));
		model.put(WebKeys.BINDER, binder);
		return new ModelAndView("forum/subscribe_return", model);
	}

	private ModelAndView ajaxEntrySubscribe(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
		String namespace = PortletRequestUtils.getStringParameter(request, "namespace", "");
		
		FolderEntry entry = getFolderModule().getEntry(binderId, entryId);
		Subscription sub = getFolderModule().getSubscription(entry);
		
		model.put(WebKeys.SUBSCRIPTION, sub);
		model.put(WebKeys.SCHEDULE_INFO, getBinderModule().getNotificationConfig(binderId));
		model.put(WebKeys.ENTRY, entry);
		model.put(WebKeys.NAMESPACE, namespace);
		
		return new ModelAndView("forum/subscribe_entry_return", model);
	}	
	
	private ModelAndView ajaxSaveEntryWidth(RenderRequest request, 
				RenderResponse response) throws Exception {
		Map model = new HashMap();
		//Save the user's selected entry width, etc.
		String entryWidth = PortletRequestUtils.getStringParameter(request, "entry_width");
		String entryHeight = PortletRequestUtils.getStringParameter(request, "entry_height");
		String entryTop = PortletRequestUtils.getStringParameter(request, "entry_top");
		String entryLeft = PortletRequestUtils.getStringParameter(request, "entry_left");
		
		if (Validator.isNotNull(entryWidth)) getProfileModule().setUserProperty(null, WebKeys.FOLDER_ENTRY_WIDTH, entryWidth);
		if (Validator.isNotNull(entryHeight)) getProfileModule().setUserProperty(null, WebKeys.FOLDER_ENTRY_HEIGHT, entryHeight);
		if (Validator.isNotNull(entryTop)) getProfileModule().setUserProperty(null, WebKeys.FOLDER_ENTRY_TOP, entryTop);
		if (Validator.isNotNull(entryLeft)) getProfileModule().setUserProperty(null, WebKeys.FOLDER_ENTRY_LEFT, entryLeft);
		
		response.setContentType("text/xml");
		return new ModelAndView("forum/save_entry_width_return", model);
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

	private ModelAndView ajaxUserListSearch(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();;
		String searchText = PortletRequestUtils.getStringParameter(request, "searchText", "");
		String searchType = PortletRequestUtils.getStringParameter(request, "searchType", "");
		String findType = PortletRequestUtils.getStringParameter(request, "findType", "");
		String listDivId = PortletRequestUtils.getStringParameter(request, "listDivId", "");
		String maxEntries = PortletRequestUtils.getStringParameter(request, "maxEntries", "");
		String[] idsToSkip = PortletRequestUtils.getStringParameter(request, "idsToSkip", "").split(" ");
		
		Map userIdsToSkip = new HashMap();
		for (int i = 0; i < idsToSkip.length; i++) {
			if (!idsToSkip[i].equals("")) userIdsToSkip.put(idsToSkip[i], Long.valueOf(idsToSkip[i]));
		}
		
		String nameType = ProfileIndexUtils.LASTNAME_FIELD;
		if (searchType.equals("firstName")) nameType = ProfileIndexUtils.FIRSTNAME_FIELD;
		if (searchType.equals("loginName")) nameType = ProfileIndexUtils.LOGINNAME_FIELD;
		if (searchType.equals("groupName")) nameType = ProfileIndexUtils.GROUPNAME_FIELD;
		if (searchType.equals("title")) nameType = EntityIndexUtils.TITLE_FIELD;
		    	     	
		//Build the search query
		Document searchFilter = DocumentHelper.createDocument();
		Element sfRoot = searchFilter.addElement(SearchFilterKeys.FilterRootName);
		Element filterTerms = sfRoot.addElement(SearchFilterKeys.FilterTerms);
		Element filterTerm = filterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeEntryDefinition);
		filterTerm.addAttribute(SearchFilterKeys.FilterElementName, nameType);
		if (searchText.length() > 0) {
			Element filterTermValueEle = filterTerm.addElement(SearchFilterKeys.FilterElementValue);
			filterTermValueEle.setText(searchText);
		}
	   	
		// check to see if the user has the right to see all users, just users in their community,
		// or no users.
/* Need a beter implemenation - disable for now
		if (!getProfileModule().checkUserSeeAll()) {
			Element field = sfRoot.addElement(QueryBuilder.GROUP_VISIBILITY_ELEMENT);
			if (getProfileModule().checkUserSeeCommunity())
	    	{
	    		// Add the group visibility element to the filter terms document
				field.addAttribute(QueryBuilder.GROUP_VISIBILITY_ATTRIBUTE,EntityIndexUtils.GROUP_SEE_COMMUNITY);
	    	} else {
	    		field.addAttribute(QueryBuilder.GROUP_VISIBILITY_ATTRIBUTE,EntityIndexUtils.GROUP_SEE_ANY);
	    	}
		}
*/	   	
		//Do a search to find the first few users who match the search text
		User u = RequestContextHolder.getRequestContext().getUser();
		Map users = new HashMap();
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.parseInt(maxEntries));
		options.put(ObjectKeys.SEARCH_SEARCH_FILTER, searchFilter);
		if (findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_GROUP)) {
			users = getProfileModule().getGroups(u.getParentBinder().getId(), options);
		} else {
			users = getProfileModule().getUsers(u.getParentBinder().getId(), options);
		}
		model.put(WebKeys.USERS, users.get(ObjectKeys.SEARCH_ENTRIES));
		model.put(WebKeys.USER_IDS_TO_SKIP, userIdsToSkip);
		model.put(WebKeys.USER_SEARCH_USER_GROUP_TYPE, findType);
		model.put(WebKeys.DIV_ID, listDivId);
		response.setContentType("text/xml");
		return new ModelAndView("forum/user_list_search", model);
	}
	
	private boolean ajaxCheckCurrentTag(String newTag){
		if (Validator.isNull(newTag)) return false;
		 
		newTag = newTag.replaceAll("[\\p{Punct}]", " ").trim().replaceAll("\\s+"," ");
		String[] newTags = newTag.split(" ");
		if (newTags.length == 0) return false;
		
	   	String tagName = newTags[newTags.length - 1].trim();
	   	if (tagName.length() > ObjectKeys.MAX_TAG_LENGTH) {
	   		return true;
	   	}
	   		
	   	return false;
	}
	
	private ModelAndView ajaxFindUserSearch(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		String searchText = PortletRequestUtils.getStringParameter(request, "searchText", "");
		String findType = PortletRequestUtils.getStringParameter(request, "findType", "");
		String listDivId = PortletRequestUtils.getStringParameter(request, "listDivId", "");
		String maxEntries = PortletRequestUtils.getStringParameter(request, "maxEntries", "10");
		String pageNumber = PortletRequestUtils.getStringParameter(request, "pageNumber", "0");
		String foldersOnly = PortletRequestUtils.getStringParameter(request, "foldersOnly", "false");
		String namespace = PortletRequestUtils.getStringParameter(request, "namespace", "");
		String binderId = PortletRequestUtils.getStringParameter(request, "binderId", "");
		String searchSubFolders = PortletRequestUtils.getStringParameter(request, "searchSubFolders", "");
		Integer startingCount = Integer.parseInt(pageNumber) * Integer.parseInt(maxEntries);
		Integer maxEntriesTags = Integer.valueOf(200);

		User u = RequestContextHolder.getRequestContext().getUser();
		Map options = new HashMap();
		String view, viewAccessible;
		options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.parseInt(maxEntries));
		options.put(ObjectKeys.SEARCH_OFFSET, startingCount);
		options.put(ObjectKeys.SEARCH_SORT_BY, EntityIndexUtils.SORT_TITLE_FIELD);
		options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(false));
		
		model.put(WebKeys.DIV_ID, listDivId);
		
		if(op.equals("find_tag_search")) {
		
			boolean result = ajaxCheckCurrentTag(searchText);
		
			if(result) {
				List thelist = null;
				view = "forum/find_tag_search";
				viewAccessible = "forum/find_tag_search_accessible";
				
				model.put(WebKeys.TAG_LENGTH_WARNING, NLT.get("tags.maxLengthWarning"));
				model.put(WebKeys.TAGS, thelist);
				
				if (u.getDisplayStyle() != null && 
						u.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
					view = viewAccessible;
				} else {
					response.setContentType("text/xml");
				}
				return new ModelAndView(view, model);	
			}
		}
		
		//Build the search query
		SearchFilter searchTermFilter = new SearchFilter();
		
		String newStr = searchText;
		Matcher matcher = replacePtrn.matcher(newStr);
		while (matcher.find()) {
			newStr = matcher.replaceFirst(" ");
			matcher = replacePtrn.matcher(newStr);
		}
		newStr = newStr.replaceAll(" \\*", "\\*");
		
	    searchText = newStr;
	     
		if (findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_PLACES)) {
			searchTermFilter.addPlacesFilter(searchText, Boolean.valueOf(foldersOnly));

		} else if (findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_ENTRIES)) {
			//Add the title term
			if (searchText.length()>0)
			searchTermFilter.addTitleFilter(searchText);

			List searchTerms = new ArrayList();
			searchTerms.add(EntityIdentifier.EntityType.folderEntry.name());
			searchTermFilter.addAndNestedTerms(SearchFilterKeys.FilterTypeEntityTypes, SearchFilterKeys.FilterEntityType, searchTerms);
			
			searchTermFilter.addAndFilter(SearchFilterKeys.FilterTypeTopEntry);
			
			//Add terms to search this folder
			if (!binderId.equals("")) {
				
				searchTermFilter.addAndFolderId(binderId);
				
				//TODO Need to implement "searchSubFolders"
			}
			
		} else if (findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_TAGS)) {
			// this has been replaced by a getTags method in the search engine.
			// searchTermFilter.addTagsFilter(FilterHelper.FilterTypeTags, searchText);
		} else {
			//Add the login name term
			if (searchText.length()>0) {
				searchTermFilter.addTitleFilter(searchText);
				searchTermFilter.addLoginNameFilter(searchText);
			}
			// check to see if the user has the right to see all users, just users in their community,
			// or no users.
/* Need a beter implemenation - disable for now
 * 			if (!getProfileModule().checkUserSeeAll()) {
				if (getProfileModule().checkUserSeeCommunity())	{
		    		searchTermFilter.addTerm(QueryBuilder.GROUP_VISIBILITY_ELEMENT, QueryBuilder.GROUP_VISIBILITY_ATTRIBUTE,EntityIndexUtils.GROUP_SEE_COMMUNITY);
		    	} else {
		    		searchTermFilter.addTerm(QueryBuilder.GROUP_VISIBILITY_ELEMENT, QueryBuilder.GROUP_VISIBILITY_ATTRIBUTE,EntityIndexUtils.GROUP_SEE_ANY);
		    	}
			}
*/
			}
	   	
		//Do a search to find the first few items that match the search text
		options.put(ObjectKeys.SEARCH_SEARCH_FILTER, searchTermFilter.getFilter());
		if (findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_PLACES)) {
			Map retMap = getBinderModule().executeSearchQuery( searchTermFilter.getFilter(), options);
			List entries = (List)retMap.get(ObjectKeys.SEARCH_ENTRIES);
			model.put(WebKeys.ENTRIES, entries);
			model.put(WebKeys.SEARCH_TOTAL_HITS, retMap.get(ObjectKeys.SEARCH_COUNT_TOTAL));
			view = "forum/find_places_search";
			viewAccessible = "forum/find_places_search_accessible";
		} else if (findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_ENTRIES)) {
			Map retMap = getBinderModule().executeSearchQuery( searchTermFilter.getFilter(), options);
			List entries = (List)retMap.get(ObjectKeys.SEARCH_ENTRIES);
			model.put(WebKeys.ENTRIES, entries);
			model.put(WebKeys.SEARCH_TOTAL_HITS, retMap.get(ObjectKeys.SEARCH_COUNT_TOTAL));
			view = "forum/find_entries_search";
			viewAccessible = "forum/find_entries_search_accessible";
		} else if (findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_TAGS) || findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_PERSONAL_TAGS) || findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_COMMUNITY_TAGS)) {
			
			String wordRoot = searchText;
			int i = wordRoot.indexOf("*");
			if (i > 0) wordRoot = wordRoot.substring(0, i);
			
			List tags = getBinderModule().getSearchTags(wordRoot, findType);
			
			List tagsPage = new ArrayList();
			
			// Map retMap = getBinderModule().executeSearchQuery( searchTermFilter.getFilter(), options);
			// List tags = (List)retMap.get(WebKeys.FOLDER_ENTRIES);
			
			if (tags.size() > startingCount.intValue()) {
				int endTag = startingCount.intValue() + Integer.valueOf(maxEntries);
				if (tags.size() < endTag) endTag = tags.size();
				tagsPage = tags.subList(startingCount.intValue(), endTag);
			}
			model.put(WebKeys.TAGS, tagsPage);
			model.put(WebKeys.SEARCH_TOTAL_HITS, Integer.valueOf(tags.size()));
			view = "forum/find_tag_search";
			viewAccessible = "forum/find_tag_search_accessible";
		} else if (findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_GROUP)) {
			Map entries = getProfileModule().getGroups(u.getParentBinder().getId(), options);
			model.put(WebKeys.USERS, entries.get(ObjectKeys.SEARCH_ENTRIES));
			model.put(WebKeys.SEARCH_TOTAL_HITS, entries.get(ObjectKeys.SEARCH_COUNT_TOTAL));
			view = "forum/find_user_search";
			viewAccessible = "forum/find_user_search_accessible";
		} else {
			Map entries = getProfileModule().getUsers(u.getParentBinder().getId(), options);
			model.put(WebKeys.USERS, entries.get(ObjectKeys.SEARCH_ENTRIES));
			model.put(WebKeys.SEARCH_TOTAL_HITS, entries.get(ObjectKeys.SEARCH_COUNT_TOTAL));
			view = "forum/find_user_search";
			viewAccessible = "forum/find_user_search_accessible";
		}
		model.put(WebKeys.USER_SEARCH_USER_GROUP_TYPE, findType);
		model.put(WebKeys.PAGE_SIZE, maxEntries);
		model.put(WebKeys.PAGE_NUMBER, pageNumber);
		
		model.put(WebKeys.NAMESPACE, namespace);
		User user = RequestContextHolder.getRequestContext().getUser();
		if (user.getDisplayStyle() != null && 
				user.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
			view = viewAccessible;
		} else {
			response.setContentType("text/xml");
		}
		return new ModelAndView(view, model);
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
	    	DefinitionHelper.getDefinitions(Definition.WORKFLOW, WebKeys.PUBLIC_WORKFLOW_DEFINITIONS, model);
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
		String defId = PortletRequestUtils.getStringParameter(request,WebKeys.CONDITION_ENTRY_DEF_ID);
		if (Validator.isNotNull(defId)) {
			model.put(WebKeys.CONDITION_ENTRY_DEF_ID, defId);
			Map elementData = getDefinitionModule().getEntryDefinitionElements(defId);
			model.put(WebKeys.ENTRY_DEFINTION_ELEMENT_DATA, elementData);
		}
		
		String name = PortletRequestUtils.getStringParameter(request, WebKeys.CONDITION_ELEMENT_NAME);
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
		String binderIdText = PortletRequestUtils.getStringParameter(request, "binderId", "");
		Long binderId = null;
		if (!binderIdText.equals("")) {
			int i = binderIdText.indexOf(".");
			if (i >= 0) {
				binderId = Long.valueOf(binderIdText.substring(0, i));
			} else {
				binderId = Long.valueOf(binderIdText);
			}
		}
		if (binderId != null) {
			String indentKey = PortletRequestUtils.getStringParameter(request, "indentKey", "");
			String page = PortletRequestUtils.getStringParameter(request, "page", "");
			String pageNumber = "";
			int i = page.indexOf(DomTreeBuilder.PAGE_DELIMITER);
	    	if (!page.equals("") && i >= 0) {
	    		pageNumber = "." + page.substring(0, i);
	    	}
			String treeName = PortletRequestUtils.getStringParameter(request, "treeName", "");
			String treeKey = PortletRequestUtils.getStringParameter(request, "treeKey", "");
			model.put("ss_tree_treeName", treeName);
			model.put("ss_tree_showIdRoutine", PortletRequestUtils.getStringParameter(request, "showIdRoutine", ""));
			model.put("ss_tree_parentId", PortletRequestUtils.getStringParameter(request, "parentId", ""));
			model.put("ss_tree_bottom", PortletRequestUtils.getStringParameter(request, "bottom", ""));
			model.put("ss_tree_type", PortletRequestUtils.getStringParameter(request, "type", ""));
			model.put("ss_tree_binderId", binderId.toString());
			model.put("ss_tree_id", binderId.toString() + pageNumber);
			model.put("ss_tree_indentKey", indentKey);
			model.put("ss_tree_topId", op2);
			model.put("ss_tree_select_id", "");
			model.put("ss_tree_select_type", selectType);
			model.put("ss_tree_fixedMultiSelectParamsMode", false);
			if (selectType.equals("2")) {
				//multi select
				String joinedMultiSelect = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TREE_MULTI_SELECT, "");
				
				List multiSelect = new ArrayList();
				if (!joinedMultiSelect.equals("")) {
					multiSelect = Arrays.asList(joinedMultiSelect.split(","));
				}
				model.put("ss_tree_select", multiSelect);
				model.put("ss_tree_select_id", selectId);
				
				boolean fixedMultiSelectParamsMode = PortletRequestUtils.getBooleanParameter(request, "fixedMultiSelectParamsMode", false);
				model.put("ss_tree_fixedMultiSelectParamsMode", fixedMultiSelectParamsMode);
			} else if (selectType.equals("1")) {
				//single select, get name and selectedId
				model.put("ss_tree_select_id", selectId);				
				model.put("ss_tree_select", PortletRequestUtils.getStringParameter(request, "select", ""));
			} 
			Binder binder = getBinderModule().getBinder(binderId);
			Document tree;
			if (binder instanceof Workspace) {
				tree = getWorkspaceModule().getDomWorkspaceTree(binder.getId(), 
							new WsDomTreeBuilder(binder, true, this, treeKey, page),1);
			} else {
				tree = getBinderModule().getDomBinderTree(binder.getId(), 
						new WsDomTreeBuilder(binder, true, this, treeKey, page),1);
			}
			model.put(WebKeys.WORKSPACE_DOM_TREE, tree);
		}
		User user = RequestContextHolder.getRequestContext().getUser();
		String view = "tag_jsps/tree/get_tree_div";
		if (user.getDisplayStyle() != null && 
				user.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
			view = "tag_jsps/tree/get_tree_div_accessible";
		} else {
			response.setContentType("text/xml");
		}
		return new ModelAndView(view, model);
	}
	
	private void ajaxChangeDashboardComponent(ActionRequest request, 
			ActionResponse response) throws Exception {
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		String op2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
		String componentId = op2;
		String scope = PortletRequestUtils.getStringParameter(request, "_scope", "");
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Binder binder = getBinderModule().getBinder(binderId);
		if (scope.equals("")) scope = DashboardHelper.Local;
	
		if (!componentId.equals("") && op.equals(WebKeys.OPERATION_DASHBOARD_SHOW_COMPONENT)) {
			DashboardHelper.showHideComponent(request, binder, componentId, scope, "show");
		} else if (!componentId.equals("") && op.equals(WebKeys.OPERATION_DASHBOARD_HIDE_COMPONENT)) {
			DashboardHelper.showHideComponent(request, binder, componentId, scope, "hide");
		} else if (!componentId.equals("") && op.equals(WebKeys.OPERATION_DASHBOARD_DELETE_COMPONENT)) {
			DashboardHelper.deleteComponent(request, binder, componentId, scope);
		}
	}
	
	private void ajaxUploadImageFile(ActionRequest request, 
			ActionResponse response) throws Exception {
		// Get a handle on the uploaded file
		String fileHandle = WebHelper.getFileHandleOnUploadedFile(request);
		if (fileHandle != null) {
			// Create a URL containing the handle
			String url = WebUrlUtil.getServletRootURL(request) + WebKeys.SERVLET_VIEW_FILE + "?" +
			"&" + WebKeys.URL_FILE_VIEW_TYPE + "=" + WebKeys.FILE_VIEW_TYPE_UPLOAD_FILE + 
			"&" + WebKeys.URL_FILE_ID + "=" + fileHandle; 

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
		List createdEntryIds = Collections.EMPTY_LIST;
		String fileHandle = WebHelper.getFileHandleOnUploadedCalendarFile(request);
		if (fileHandle != null) {
			MultipartFile file = WebHelper.wrapFileHandleInMultipartFile(fileHandle);
			Long folderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_FOLDER_ID, -1);
			if (folderId != -1) {
				createdEntryIds = getIcalModule().parseToEntries(folderId, file.getInputStream());
			}
			WebHelper.releaseFileHandle(fileHandle);
		}
		response.setRenderParameter("ssICalendarEntryIdsSize", Integer.toString(createdEntryIds.size()));
		
		List entryIdsAsStrings = new ArrayList();
		Iterator it = createdEntryIds.iterator();
		while (it.hasNext()) {
			entryIdsAsStrings.add(it.next().toString());
		}
		String[] ids = new String[entryIdsAsStrings.size()];
		ids = (String[])entryIdsAsStrings.toArray(ids);
		response.setRenderParameter("ssICalendarEntryIds", ids);
	}
	
	private ModelAndView ajaxUploadICalendarFileStatus(RenderRequest request, RenderResponse response) {
		int entriesAmount = PortletRequestUtils.getIntParameter(request, "ssICalendarEntryIdsSize", 0);
		long[] entryIds = PortletRequestUtils.getLongParameters(request, "ssICalendarEntryIds");
		Map model = new HashMap();
		model.put("entriesAmount", entriesAmount);
		model.put("entryIds", entryIds);
		
		return new ModelAndView("forum/json/icalendar_upload", model);
	}
	
	private ModelAndView ajaxGetChangeLogEntryForm(RenderRequest request, RenderResponse response) {
		Map model = new HashMap();
		String binderId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
		model.put(WebKeys.BINDER_ID, binderId);
		return new ModelAndView("administration/get_change_log_entry_form", model);
	}
	
	private void ajaxAddToClipboard(ActionRequest request, 
			ActionResponse response) throws Exception {
		String musterClass = PortletRequestUtils.getStringParameter(request, WebKeys.URL_MUSTER_CLASS, "");
		List musterIds = PortletRequestUtils.getLongListParameters(request, WebKeys.URL_MUSTER_IDS);
		
		Clipboard clipboard = new Clipboard(request);
		clipboard.add(musterClass, musterIds);
		
		
		Boolean addTeamMembers = PortletRequestUtils.getBooleanParameter(request, "add_team_members", false);
		if (addTeamMembers) {
			Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
			try {
				Collection teamMemberIds = getBinderModule().getTeamMemberIds(binderId, true);
				clipboard.add(Clipboard.USERS, new ArrayList(teamMemberIds));				
			} catch (AccessControlException ac) {} //no access, just skip
		}
	}

	private void ajaxRemoveFromClipboard(ActionRequest request, 
			ActionResponse response) throws Exception {
		String musterClass = PortletRequestUtils.getStringParameter(request, WebKeys.URL_MUSTER_CLASS, "");
		String[] musterIds = new String[0];
		if (PortletRequestUtils.getStringParameters(request, WebKeys.URL_MUSTER_IDS) != null) {
			musterIds = PortletRequestUtils.getStringParameters(request, WebKeys.URL_MUSTER_IDS);
		}
		Clipboard clipboard = new Clipboard(request);
		Map clipboardMap = clipboard.getClipboard();
		if (clipboardMap.containsKey(musterClass)) {
			Set idList = (Set) clipboardMap.get(musterClass);
			for (int i = 0; i < musterIds.length; i++) {
				Long id = Long.valueOf(musterIds[i]);
				if (idList.contains(id)) idList.remove(id);
			}
		}
	}
	
	private void ajaxClearClipboard(ActionRequest request, 
			ActionResponse response) throws Exception {
		Clipboard clipboard = new Clipboard(request);
		Map clipboardMap = clipboard.getClipboard();
		String musterClass = PortletRequestUtils.getStringParameter(request, WebKeys.URL_MUSTER_CLASS, "");
		String[] musterClasses = musterClass.split(" ");
		for (int i = 0; i < musterClasses.length; i++) {
			if (!musterClasses[i].equals("")) {
				if (clipboardMap.containsKey(musterClasses[i])) clipboardMap.remove(musterClasses[i]);
			}
		}
	}
	
	private void ajaxSetBinderOwnerId(ActionRequest request, 
			ActionResponse response) throws Exception {
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		String ownerId = PortletRequestUtils.getStringParameter(request, "ownerId", "");
		if (!ownerId.equals("")) {
			Binder binder = getBinderModule().getBinder(binderId);
			getAdminModule().setWorkAreaOwner(binder, Long.valueOf(ownerId), false);
		}
	}
	
	private void ajaxModifyGroup(ActionRequest request, 
			ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("applyBtn") || formData.containsKey("okBtn")) {
			Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
			Long groupId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID);
			String title = PortletRequestUtils.getStringParameter(request, "title", "");
			String description = PortletRequestUtils.getStringParameter(request, "description", "");
			Set ids = LongIdUtil.getIdsAsLongSet(request.getParameterValues("users"));
			ids.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("groups")));
			SortedSet principals = getProfileModule().getPrincipals(ids, RequestContextHolder.getRequestContext().getZoneId());
			Map updates = new HashMap();
			updates.put(ObjectKeys.FIELD_ENTITY_TITLE, title);
			updates.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION, description);
			updates.put(ObjectKeys.FIELD_GROUP_MEMBERS, principals);
			getProfileModule().modifyEntry(binderId, groupId, new MapInputData(updates));
		}
	}
	
	private void ajaxStickyCalendarDisplaySettings(ActionRequest request, 
			ActionResponse response) {
		PortletSession portletSession = WebHelper.getRequiredPortletSession(request);
		
		String eventType = PortletRequestUtils.getStringParameter(request, "eventType", "");
		if (!"".equals(eventType)) {
			EventsViewHelper.setCalendarDisplayEventType(portletSession, eventType);
		}
		
		String gridType = PortletRequestUtils.getStringParameter(request, WebKeys.CALENDAR_GRID_TYPE, "");
		if (!"".equals(gridType)) {
			int gridSize = PortletRequestUtils.getIntParameter(request, WebKeys.CALENDAR_GRID_SIZE, -1);
			EventsViewHelper.setCalendarGridType(portletSession, gridType);
			EventsViewHelper.setCalendarGridSize(portletSession, gridSize);
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
	
	private ModelAndView ajaxGetDashboardComponent(RenderRequest request, 
				RenderResponse response) throws Exception {
		Map model = new HashMap();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		String op2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
		String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
		String componentId = op2;
		model.put(WebKeys.NAMESPACE, namespace);

		if (op.equals(WebKeys.OPERATION_DASHBOARD_SHOW_COMPONENT)) {
			if (!componentId.equals("")) {
				Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
				Binder binder = getBinderModule().getBinder(binderId);
				model.put(WebKeys.BINDER, binder);
				String scope = PortletRequestUtils.getStringParameter(request, "_scope", "");
				if (scope.equals("")) scope = DashboardHelper.Local;
				User user = RequestContextHolder.getRequestContext().getUser();
				DashboardHelper.getDashboardMap(binder, 
					getProfileModule().getUserProperties(user.getId()).getProperties(), 
					model, scope, componentId, false);
			}
		} else if (op.equals(WebKeys.OPERATION_DASHBOARD_HIDE_COMPONENT) ||
				op.equals(WebKeys.OPERATION_DASHBOARD_DELETE_COMPONENT)) {
			return new ModelAndView("forum/fetch_url_return", model);
		}
		return new ModelAndView("definition_elements/view_dashboard_component", model);
	}
	
	private ModelAndView ajaxGetDashboardSearchMore(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String op2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
		String componentId = op2;
		model.put(WebKeys.DIV_ID, PortletRequestUtils.getStringParameter(request, WebKeys.URL_DIV_ID, ""));
		model.put(WebKeys.PAGE_SIZE, PortletRequestUtils.getStringParameter(request, WebKeys.URL_PAGE_SIZE, "10"));
		model.put(WebKeys.PAGE_NUMBER, PortletRequestUtils.getStringParameter(request, WebKeys.URL_PAGE_NUMBER, "0"));
		model.put(WebKeys.NAMESPACE, PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, ""));
		model.put(WebKeys.DASHBOARD_COMPONENT_ID, op2);
	
		if (Validator.isNotNull(componentId)) {
			String scope = PortletRequestUtils.getStringParameter(request, "_scope", null);
			if (Validator.isNull(scope)) {
				if (componentId.contains("_")) scope = componentId.split("_")[0];
			}
			if (!DashboardHelper.Portlet.equals(scope)) {
				Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
				Binder binder = getBinderModule().getBinder(binderId);
				model.put(WebKeys.BINDER, binder);
				User user = RequestContextHolder.getRequestContext().getUser();
				if (Validator.isNull(scope)) scope = DashboardHelper.Local;
				DashboardHelper.getDashboardMap(binder, 
					getProfileModule().getUserProperties(user.getId()).getProperties(), 
					model, scope, componentId, false);
			} else {
				String dashboardId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID);				
				DashboardPortlet dashboard = (DashboardPortlet)getDashboardModule().getDashboard(dashboardId);
				model.put(WebKeys.DASHBOARD_PORTLET, dashboard);
				User user = RequestContextHolder.getRequestContext().getUser();
				DashboardHelper.getDashboardMap(dashboard, 
					getProfileModule().getUserProperties(user.getId()).getProperties(), 
					model, false);
				
			}
		}
		String view = "dashboard/search_view2";
		String displayType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_DISPLAY_TYPE, "search");
		if (displayType.equals(WebKeys.DISPLAY_STYLE_SEARCH)) view = "dashboard/search_view2";
		if (displayType.equals(WebKeys.DISPLAY_STYLE_GALLERY)) view = "dashboard/gallery_view2";
		if (displayType.equals(WebKeys.DISPLAY_STYLE_BLOG)) view = "dashboard/blog_view2";
		if (displayType.equals(WebKeys.DISPLAY_STYLE_GUESTBOOK)) view = "dashboard/guestbook_view2";
		if (displayType.equals(WebKeys.DISPLAY_STYLE_TASK)) view = "dashboard/task_view2";
		if (displayType.equals("comments")) view = "dashboard/comments_view2";
		return new ModelAndView(view, model);
	}
	
	private ModelAndView ajaxGetDashboardTeamMore(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String op2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
		String componentId = op2;
		model.put(WebKeys.DIV_ID, PortletRequestUtils.getStringParameter(request, WebKeys.URL_DIV_ID, ""));
		model.put(WebKeys.PAGE_SIZE, PortletRequestUtils.getStringParameter(request, WebKeys.URL_PAGE_SIZE, "10"));
		model.put(WebKeys.PAGE_NUMBER, PortletRequestUtils.getStringParameter(request, WebKeys.URL_PAGE_NUMBER, "0"));
		model.put(WebKeys.NAMESPACE, PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, ""));
		model.put(WebKeys.DASHBOARD_COMPONENT_ID, op2);
	
		if (Validator.isNotNull(componentId)) {
			String scope = PortletRequestUtils.getStringParameter(request, "_scope", null);
			if (Validator.isNull(scope)) {
				if (componentId.contains("_")) scope = componentId.split("_")[0];
			}
			if (!DashboardHelper.Portlet.equals(scope)) {
				Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
				Binder binder = getBinderModule().getBinder(binderId);
				model.put(WebKeys.BINDER, binder);
				User user = RequestContextHolder.getRequestContext().getUser();
				if (Validator.isNull(scope)) scope = DashboardHelper.Local;
				DashboardHelper.getDashboardMap(binder, 
					getProfileModule().getUserProperties(user.getId()).getProperties(), 
					model, scope, componentId, false);
			} else {
				String dashboardId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID);				
				DashboardPortlet dashboard = (DashboardPortlet)getDashboardModule().getDashboard(dashboardId);
				model.put(WebKeys.DASHBOARD_PORTLET, dashboard);
				User user = RequestContextHolder.getRequestContext().getUser();
				DashboardHelper.getDashboardMap(dashboard, 
					getProfileModule().getUserProperties(user.getId()).getProperties(), 
					model, false);
				
			}
		}
		String view = "dashboard/team_members_list_view";
		return new ModelAndView(view, model);
	}
	
	public HashMap getEntryAccessMap(Map model, FolderEntry entry) {
		Map accessControlMap = (Map) model.get(WebKeys.ACCESS_CONTROL_MAP);
		HashMap entryAccessMap = new HashMap();
		if (accessControlMap.containsKey(entry.getId())) {
			entryAccessMap = (HashMap) accessControlMap.get(entry.getId());
		}
		return entryAccessMap;
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
			setAccessControlForAttachmentList(model, entry, user);
			Map accessControlMap = (Map) model.get(WebKeys.ACCESS_CONTROL_MAP);
			HashMap entryAccessMap = getEntryAccessMap(model, entry);
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
		response.setContentType("text/json");
		model.put(WebKeys.HELP_PANEL_JSP, jsp);
		model.put(WebKeys.HELP_PANEL_TAG, tagId);

		//Put in the product name
		model.put(WebKeys.PRODUCT_NAME, SPropsUtil.getString("product.name", ObjectKeys.PRODUCT_NAME_DEFAULT));
		
		return new ModelAndView("forum/help_panel", model);
	}

	
	
	private ModelAndView ajaxAddTab(RenderRequest request, 
			RenderResponse response) throws Exception {
		Tabs tabs = new Tabs(request);
		Map model = new HashMap();
		String type = PortletRequestUtils.getRequiredStringParameter(request, "type");
		int tabId = 0;
		if (type.equals(Tabs.BINDER)) {
			Long binderId = PortletRequestUtils.getLongParameter(request, "binderId");				
			Binder binder = getBinderModule().getBinder(binderId);
			tabId = tabs.findTab(binder);
		} else if (type.equals(Tabs.ENTRY)) {
			Long binderId = PortletRequestUtils.getLongParameter(request, "binderId");				
			Long entryId = PortletRequestUtils.getLongParameter(request, "binderId");				
			Entry entry = getFolderModule().getEntry(binderId, entryId);
			tabId = tabs.findTab(entry);
		} else if (type.equals(Tabs.QUERY)) {
			SearchFilterRequestParser requestParser = new SearchFilterRequestParser(request, getDefinitionModule());
			Document query = requestParser.getSearchQuery();
			tabId = tabs.findTab(query);
		}
		tabs.setCurrentTab(tabId);
		response.setContentType("text/xml");
		model.put(WebKeys.TABS, tabs.getTabs());
		model.put(WebKeys.TAB_ID, tabId);
		return new ModelAndView("binder/show_tabs", model);
	}

	private ModelAndView ajaxDeleteTab(RenderRequest request, 
			RenderResponse response) throws Exception {
		Tabs tabs = new Tabs(request);
		Map model = new HashMap();
		Integer tabId = PortletRequestUtils.getIntParameter(request, "tabId");
		if (tabId != null) tabs.deleteTab(tabId.intValue());
		response.setContentType("text/xml");
		model.put(WebKeys.TABS, tabs.getTabs());
		model.put(WebKeys.TAB_ID, -1);
		return new ModelAndView("binder/show_tabs", model);
	}

	private ModelAndView ajaxSetCurrentTab(RenderRequest request, 
			RenderResponse response) throws Exception {
		Tabs tabs = new Tabs(request);
		Map model = new HashMap();
		Integer tabId = PortletRequestUtils.getIntParameter(request, "tabId");
		if (tabId != null) tabs.setCurrentTab(tabId.intValue());
		response.setContentType("text/xml");
		model.put(WebKeys.TABS, tabs.getTabs());
		model.put(WebKeys.TAB_ID, -1);
		return new ModelAndView("binder/show_tabs", model);
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

		// This is a hack for iChain which doesn't always rewrite the
		// applet URL properly.  We force https if we're leaning that way.
		String protocol;
		protocol = SPropsUtil.getString("adapter.web.protocol", "context");
		if (protocol.equalsIgnoreCase("https") || protocol.equalsIgnoreCase("context-https")) {
			strURL = strURL.replaceFirst("http://","https://");
		}
		
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

		setAccessControlForAttachmentList(model, entry, user);
		
		response.setContentType("text/xml");
		return new ModelAndView("definition_elements/view_entry_attachments_ajax", model);
	}
	
	private void setAccessControlForAttachmentList(Map model, FolderEntry entry, User user) {

		Map accessControlEntryMap = BinderHelper.getAccessControlEntityMapBean(model, entry);

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
		
		if (getFolderModule().testAccess(entry, FolderOperation.addReply)) {
			accessControlEntryMap.put("addReply", new Boolean(true));
		}		
		
		if (getFolderModule().testAccess(entry, FolderOperation.modifyEntry)) {
			if (reserveAccessCheck && isEntryReserved && !(isUserBinderAdministrator || isLockedByAndLoginUserSame) ) {
			} else {
				accessControlEntryMap.put("modifyEntry", new Boolean(true));
			}
		}
		
		if (getFolderModule().testAccess(entry, FolderOperation.deleteEntry)) {
			if (reserveAccessCheck && isEntryReserved && !(isUserBinderAdministrator || isLockedByAndLoginUserSame) ) {
			} else {
				accessControlEntryMap.put("deleteEntry", new Boolean(true));
			}
		}		
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

		// This is a hack for iChain which doesn't always rewrite the
		// applet URL properly.  We force https if we're leaning that way.
		String protocol;
		protocol = SPropsUtil.getString("adapter.web.protocol", "context");
		if (protocol.equalsIgnoreCase("https") || protocol.equalsIgnoreCase("context-https")) {
			strURL = strURL.replaceFirst("http://","https://");
		}
		
		//This replace has been done AJAX does not allow "&"
		String strRefreshURL = adapterFolderRefreshUrl.toString();
		//strRefreshURL = strRefreshURL.replaceAll("&", "&amp;");
		
		// Same iChain hack as above...
		if (protocol.equalsIgnoreCase("https") || protocol.equalsIgnoreCase("context-https")) {
			strRefreshURL = strRefreshURL.replaceFirst("http://","https://");
		}
		
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
		Group group = (Group)getProfileModule().getEntry(binderId, groupId);		
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
	
	private ModelAndView ajaxGetClipboardUsers(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		
		if (WebHelper.isUserLoggedIn(request)) {
			Clipboard clipboard = new Clipboard(request);
			Map clipboardMap = clipboard.getClipboard();
			Set clipboardUsers = (Set) clipboardMap.get(Clipboard.USERS);
			model.put(WebKeys.CLIPBOARD_PRINCIPALS , getProfileModule().getUsersFromPrincipals(
					clipboardUsers));
		} else {
			model.put(WebKeys.CLIPBOARD_PRINCIPALS , 0);			
		}
		
		response.setContentType("text/json");
		return new ModelAndView("forum/clipboard_users", model);
	}

	private ModelAndView ajaxFindCalendarEvents(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		
		if (WebHelper.isUserLoggedIn(request)) {
			model.put(WebKeys.USER_PRINCIPAL, RequestContextHolder.getRequestContext().getUser());
			Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
			List binderIds = Arrays.asList(PortletRequestUtils.getStringParameters(request, WebKeys.URL_BINDER_IDS));
			model.put(WebKeys.URL_DASHBOARD_REQUEST, PortletRequestUtils.getBooleanParameter(request, WebKeys.URL_DASHBOARD_REQUEST, false));
			Binder binder = getBinderModule().getBinder(binderId);
			
			Map options = new HashMap();
			boolean eventsByEntry = PortletRequestUtils.getBooleanParameter(request, "ssEntryEvents", false);
			if (eventsByEntry) {
				// get events by entry
				Long entryId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID);

				User user = RequestContextHolder.getRequestContext().getUser();
				UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
				options.putAll(ListFolderController.getSearchFilter(request, userFolderProperties));
				
		       	List entries;
				if (binder instanceof Folder || binder instanceof Workspace) {
					Document searchFilter = SearchFiltersBuilder.buildGetEntryQuery(request, entryId);
					Map retMap = getBinderModule().executeSearchQuery(searchFilter, options);
					entries = (List) retMap.get(ObjectKeys.SEARCH_ENTRIES);
				} else {
					//a template
					entries = new ArrayList();
				}
				
				EventsViewHelper.getEntryEvents(binder, entries, model, response, WebHelper.getRequiredPortletSession(request));

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
				
				String gridType = PortletRequestUtils.getStringParameter(request, WebKeys.CALENDAR_GRID_TYPE, "");
				Integer gridSize = PortletRequestUtils.getIntParameter(request, WebKeys.CALENDAR_GRID_SIZE, -1);
				model.put(WebKeys.CALENDAR_CURRENT_GRID_TYPE, EventsViewHelper.setCalendarGridType(portletSession, gridType));
				model.put(WebKeys.CALENDAR_CURRENT_GRID_SIZE, EventsViewHelper.setCalendarGridSize(portletSession, gridSize));
				
				CalendarViewRangeDates calendarViewRangeDates = new CalendarViewRangeDates(currentDate);
	
				options.put(ObjectKeys.SEARCH_MAX_HITS, 10000);
		       	options.put(ObjectKeys.SEARCH_EVENT_DAYS, calendarViewRangeDates.getExtViewDayDates());
		       	
		        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		       	options.put(ObjectKeys.SEARCH_LASTACTIVITY_DATE_START, formatter.format(calendarViewRangeDates.getStartViewExtWindow().getTime()));
		       	options.put(ObjectKeys.SEARCH_LASTACTIVITY_DATE_END, formatter.format(calendarViewRangeDates.getEndViewExtWindow().getTime()));
	
		       	options.put(ObjectKeys.SEARCH_CREATION_DATE_START, formatter.format(calendarViewRangeDates.getStartViewExtWindow().getTime()));
		       	options.put(ObjectKeys.SEARCH_CREATION_DATE_END, formatter.format(calendarViewRangeDates.getEndViewExtWindow().getTime()));
			
				User user = RequestContextHolder.getRequestContext().getUser();
				UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
				options.putAll(ListFolderController.getSearchFilter(request, userFolderProperties));
				
				
		       	List entries;
				if (binder instanceof Folder || binder instanceof Workspace) {
					Document searchFilter = SearchFiltersBuilder.buildFolderListQuery(request, binderIds);
					Map retMap = getBinderModule().executeSearchQuery(searchFilter, options);
					entries = (List) retMap.get(ObjectKeys.SEARCH_ENTRIES);
				} else {
					//a template
					entries = new ArrayList();
				}
				
				EventsViewHelper.getEvents(currentDate, calendarViewRangeDates, binder, entries, model, response, portletSession);
			}
		} else {
			model.put(WebKeys.CALENDAR_VIEWBEAN , Collections.EMPTY_LIST);
		}
		
		response.setContentType("text/json");
		return new ModelAndView("forum/json/events", model);
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
						new MapInputData(formData), new HashMap(), new HashSet(), null);
				
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
		
		Tabs tabs = AdvancedSearchController.setupTabs(request);
		int tabId = tabs.getCurrentTab();  
		Map currentTab = tabs.getTab(tabId);
		
		// get query and options from tab
		Document query = AdvancedSearchController.getQueryFromTab(currentTab);

		User currentUser = RequestContextHolder.getRequestContext().getUser();
		
		UserProperties userProperties = getProfileModule().getUserProperties(currentUser.getId());
		Map properties = userProperties.getProperties();
		
		Map userQueries = new HashMap();
		if (properties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
			userQueries = (Map)properties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES);
		}
		
		userQueries.put(queryName, query);
		userProperties.setProperty(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES, userQueries);
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
			}
		}
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
		getFolderModule().addVote(binderId, entryId, new MapInputData(formData));
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
			getFolderModule().modifyEntry(binderId, entryId, new MapInputData(formData), fileMap, new HashSet(), null);
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
	
	
}
