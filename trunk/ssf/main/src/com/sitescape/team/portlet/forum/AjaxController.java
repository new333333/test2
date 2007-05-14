/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.portlet.forum;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.portlet.bind.PortletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.calendar.CalendarViewRangeDates;
import com.sitescape.team.calendar.EventsViewHelper;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DashboardPortlet;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.SeenMap;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.ic.ICBrokerModule;
import com.sitescape.team.module.profile.index.ProfileIndexUtils;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.portlet.binder.AccessControlController;
import com.sitescape.team.portlet.binder.AdvancedSearchController;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.search.filter.SearchFilterKeys;
import com.sitescape.team.search.filter.SearchFilterToSearchBooleanConverter;
import com.sitescape.team.search.filter.SearchFilter;
import com.sitescape.team.search.filter.SearchFilterRequestParser;
import com.sitescape.team.ssfs.util.SsfsUtil;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.tree.DomTreeBuilder;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.Clipboard;
import com.sitescape.team.web.util.DashboardHelper;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.Favorites;
import com.sitescape.team.web.util.FindIdsHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.Tabs;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.Validator;
/**
 * @author Peter Hurley
 *
 */
public class AjaxController  extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
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
			} else if (op.equals(WebKeys.OPERATION_UPLOAD_IMAGE_FILE)) {
				ajaxUploadImageFile(request, response);
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
					op.equals(WebKeys.OPERATION_DASHBOARD_SEARCH_MORE)) {
				return new ModelAndView("forum/fetch_url_return", model);
			} else if(op.equals(WebKeys.OPERATION_SHOW_BLOG_REPLIES)) {
				return new ModelAndView("forum/fetch_url_return", model);
			} else if (op.equals(WebKeys.OPERATION_CONFIGURE_FOLDER_COLUMNS) ||
					op.equals(WebKeys.OPERATION_SUBSCRIBE)) {
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
			} else if (op.equals(WebKeys.OPERATION_GET_SEARCH_FORM_FILTER_TYPE) || 
					op.equals(WebKeys.OPERATION_GET_SEARCH_FORM_ENTRY_ELEMENTS) || 
					op.equals(WebKeys.OPERATION_GET_SEARCH_FORM_ELEMENT_VALUES) || 
					op.equals(WebKeys.OPERATION_GET_SEARCH_FORM_ELEMENT_VALUE_DATA)) {
				return new ModelAndView("binder/get_condition_entry_element", model);
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

		} else if (op.equals(WebKeys.OPERATION_GET_SEARCH_FORM_FILTER_TYPE) || 
				op.equals(WebKeys.OPERATION_GET_SEARCH_FORM_ENTRY_ELEMENTS) || 
				op.equals(WebKeys.OPERATION_GET_SEARCH_FORM_ELEMENT_VALUES) || 
				op.equals(WebKeys.OPERATION_GET_SEARCH_FORM_ELEMENT_VALUE_DATA)) {
			return ajaxGetSearchFormData(request, response);
		} else if (op.equals(WebKeys.OPERATION_GET_CONDITION_ENTRY_ELEMENTS) || 
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
		} else if (op.equals(WebKeys.OPERATION_SAVE_SEARCH_QUERY)) {
			return ajaxGetSearchQueryName(request, response);
		} else if (op.equals(WebKeys.OPERATION_REMOVE_SEARCH_QUERY)) {
			return ajaxGetRemovedQueryName(request, response);
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
				
				searchTermFilter.addTitleFilter(SearchFilterKeys.FilterTypeEntryDefinition, search);
				searchTermFilter.addLoginNameFilter(SearchFilterKeys.FilterTypeEntryDefinition, search);
							
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
				valuesData = (Map)((Map) fieldsData.get(entryField)).get("values");
				String fieldType = (String)((Map) fieldsData.get(entryField)).get("type");
				if (valuesData == null && "checkbox".equals(fieldType)) {
					valuesData = new HashMap();
					valuesData.put(Boolean.TRUE.toString(), NLT.get("searchForm.checkbox.selected"));
					valuesData.put(Boolean.FALSE.toString(), NLT.get("searchForm.checkbox.unselected"));
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
				tags = tags.subList(startPageNo, endPageNo);
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
		model.put(WebKeys.WORKFLOW_DEFINTION_MAP, workflows);
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
		String entryId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		EntityType entityType = null;
		if (entryId.equals(binderId.toString())) {
			Binder binder = getBinderModule().getBinder(binderId);
			entityType = binder.getEntityIdentifier().getEntityType();
		}
		String operation2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
		String communityTag = PortletRequestUtils.getStringParameter(request, "communityTag", "");
		String personalTag = PortletRequestUtils.getStringParameter(request, "personalTag", "");
		String tagToDelete = PortletRequestUtils.getStringParameter(request, "tagToDelete", "");
		if (EntityIdentifier.EntityType.folder.equals(entityType) || 
				EntityIdentifier.EntityType.workspace.equals(entityType)) {
			if (operation2.equals("delete")) {
				getBinderModule().deleteTag(binderId, tagToDelete);
			} else if (operation2.equals("add")) {
				if (!communityTag.equals("")) getBinderModule().setTag(binderId, communityTag, true);
				if (!personalTag.equals("")) getBinderModule().setTag(binderId, personalTag, false);
			}
		} else if (Validator.isNotNull(entryId)){
			if (operation2.equals("delete")) {
				getFolderModule().deleteTag(binderId, Long.valueOf(entryId), tagToDelete);
			} else if (operation2.equals("add")) {
				if (!communityTag.equals("")) getFolderModule().setTag(binderId, Long.valueOf(entryId), communityTag, true);
				if (!personalTag.equals("")) getFolderModule().setTag(binderId, Long.valueOf(entryId), personalTag, false);
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
		
		UserProperties userProperties;
		Map columns;
		if (binderId == null) {
			userProperties = getProfileModule().getUserProperties(null);
			columns = (Map) userProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_RESULTS_FOLDER_COLUMNS);
		} else {
			userProperties = getProfileModule().getUserProperties(null, binderId);
			columns = (Map) userProperties.getProperty(ObjectKeys.USER_PROPERTY_FOLDER_COLUMNS);
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
		Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
		String namespace = PortletRequestUtils.getStringParameter(request, "namespace", "");
		String tagDivNumber = PortletRequestUtils.getStringParameter(request, "tagDivNumber", "");
	
		Map model = new HashMap();
		
		EntityType entityType = null;
		if (binderId != null && binderId.equals(entryId)) {
			Binder binder = getBinderModule().getBinder(binderId);
			entityType = binder.getEntityIdentifier().getEntityType();
		}
		if (entityType != null && (entityType.equals(EntityType.folder) || entityType.equals(EntityType.workspace))) {
			model.put(WebKeys.COMMUNITY_TAGS, getBinderModule().getCommunityTags(binderId));
			model.put(WebKeys.PERSONAL_TAGS, getBinderModule().getPersonalTags(binderId));
		} else {
			model.put(WebKeys.COMMUNITY_TAGS, getFolderModule().getCommunityTags(binderId, entryId));
			model.put(WebKeys.PERSONAL_TAGS, getFolderModule().getPersonalTags(binderId, entryId));
		}
		model.put(WebKeys.NAMESPACE, namespace);
		model.put(WebKeys.TAG_DIV_NUMBER, tagDivNumber);
		model.put(WebKeys.ENTRY_ID, entryId.toString());
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

	private ModelAndView ajaxFindUserSearch(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String searchText = PortletRequestUtils.getStringParameter(request, "searchText", "");
		String findType = PortletRequestUtils.getStringParameter(request, "findType", "");
		String listDivId = PortletRequestUtils.getStringParameter(request, "listDivId", "");
		String maxEntries = PortletRequestUtils.getStringParameter(request, "maxEntries", "10");
		String pageNumber = PortletRequestUtils.getStringParameter(request, "pageNumber", "0");
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

		//Build the search query
		SearchFilter searchTermFilter = new SearchFilter();
		
		if (findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_PLACES)) {
			searchTermFilter.addPlacesFilter(searchText);

		} else if (findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_ENTRIES)) {
			//Add the title term
			if (searchText.length()>0)
			searchTermFilter.addTitleFilter(SearchFilterKeys.FilterTypeEntryDefinition, searchText);

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
				searchTermFilter.addTitleFilter(SearchFilterKeys.FilterTypeEntryDefinition, searchText);
				searchTermFilter.addLoginNameFilter(SearchFilterKeys.FilterTypeEntryDefinition, searchText);
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
		model.put(WebKeys.DIV_ID, listDivId);
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
			model.put(WebKeys.ENTRY_DEFINTION_MAP, defaultEntryDefinitions);
	    	DefinitionHelper.getDefinitions(Definition.WORKFLOW, WebKeys.PUBLIC_WORKFLOW_DEFINITIONS, model);
			model.put(WebKeys.WORKFLOW_DEFINTION_MAP, model.get(WebKeys.PUBLIC_WORKFLOW_DEFINITIONS));
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
	
	private ModelAndView ajaxGetSearchFormData(RenderRequest request, 
				RenderResponse response) throws Exception {
		Map model = new HashMap();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		String op2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
		String filterTermNumber = PortletRequestUtils.getStringParameter(request, WebKeys.FILTER_ENTRY_FILTER_TERM_NUMBER, "");
		model.put(WebKeys.FILTER_ENTRY_FILTER_TERM_NUMBER, filterTermNumber);
		String filterTermNumberMax = PortletRequestUtils.getStringParameter(request, WebKeys.FILTER_ENTRY_FILTER_TERM_NUMBER_MAX, "");
		model.put(WebKeys.FILTER_ENTRY_FILTER_TERM_NUMBER_MAX, filterTermNumberMax);
		
		//Get the definition id (if present)
		if (op.equals(WebKeys.OPERATION_GET_SEARCH_FORM_ENTRY_ELEMENTS) || 
				op.equals(WebKeys.OPERATION_GET_SEARCH_FORM_ELEMENT_VALUES) || 
				op.equals(WebKeys.OPERATION_GET_SEARCH_FORM_ELEMENT_VALUE_DATA)) {
			String defId = PortletRequestUtils.getStringParameter(request, WebKeys.SEARCH_FORM_ENTRY_DEF_ID+filterTermNumber);
			if (Validator.isNotNull(defId)) {
				if (defId.equals("_common")) {
					model.put(WebKeys.SEARCH_FORM_ENTRY_DEF_ID, "");
					Map elementData = BinderHelper.getCommonEntryElements();
					model.put(WebKeys.ENTRY_DEFINTION_ELEMENT_DATA, elementData);
				} else {
					model.put(WebKeys.SEARCH_FORM_ENTRY_DEF_ID, defId);
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
		
		String elementName = PortletRequestUtils.getStringParameter(request, "elementName" + filterTermNumber);
		if (Validator.isNotNull(elementName)) {
			model.put(WebKeys.FILTER_ENTRY_ELEMENT_NAME, elementName);
		}

		
		response.setContentType("text/xml");
		if (op.equals(WebKeys.OPERATION_GET_SEARCH_FORM_FILTER_TYPE)) {
			model.put(WebKeys.FILTER_TYPE, op2);
			if (op.equals(WebKeys.OPERATION_GET_SEARCH_FORM_FILTER_TYPE) && op2.equals("folders")) {
    			Workspace ws = getWorkspaceModule().getWorkspace();
    			Document tree = getWorkspaceModule().getDomWorkspaceTree(ws.getId(), new WsDomTreeBuilder(ws, true, this),1);
    			model.put(WebKeys.DOM_TREE, tree);
			} else {
				DefinitionHelper.getDefinitions(Definition.FOLDER_ENTRY, WebKeys.PUBLIC_BINDER_ENTRY_DEFINITIONS, model);
		    	DefinitionHelper.getDefinitions(Definition.WORKFLOW, WebKeys.PUBLIC_WORKFLOW_DEFINITIONS, model);
				model.put(WebKeys.WORKFLOW_DEFINTION_MAP, model.get(WebKeys.PUBLIC_WORKFLOW_DEFINITIONS));
			}
			return new ModelAndView("tag_jsps/search_form/get_filter_type", model);
		} else if (op.equals(WebKeys.OPERATION_GET_SEARCH_FORM_ENTRY_ELEMENTS)) {
			model.put(WebKeys.FILTER_TYPE, "entry");
			return new ModelAndView("tag_jsps/search_form/get_entry_elements", model);
		} else if (op.equals(WebKeys.OPERATION_GET_SEARCH_FORM_ELEMENT_VALUES)) {
			model.put(WebKeys.FILTER_TYPE, "entry");
			return new ModelAndView("tag_jsps/search_form/get_element_value", model);
		} else if (op.equals(WebKeys.OPERATION_GET_WORKFLOW_STATES)) {
			model.put(WebKeys.FILTER_TYPE, "workflow");
			return new ModelAndView("binder/get_entry_elements", model);
		} else {
			model.put(WebKeys.FILTER_VALUE_TYPE, PortletRequestUtils.getStringParameter(request,
					"elementValueDateType" + filterTermNumber, ""));
			return new ModelAndView("tag_jsps/search_form/get_element_value_data", model);
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
			if (selectType.equals("2")) {
				//multi select
				String joinedMultiSelect = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TREE_MULTI_SELECT, "");
				
				List multiSelect = new ArrayList();
				if (!joinedMultiSelect.equals("")) {
					multiSelect = Arrays.asList(joinedMultiSelect.split(","));
				}
				model.put("ss_tree_select", multiSelect);
				model.put("ss_tree_select_id", selectId);
			} else if (selectType.equals("1")) {
				//single select, get name and selectedId
				model.put("ss_tree_select_id", selectId);				
				model.put("ss_tree_select", PortletRequestUtils.getStringParameter(request, "select", ""));
			} 
			Binder binder = getBinderModule().getBinder(binderId);
			Long topId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_OPERATION2);
			Document tree;
			if (binder instanceof Workspace) {
				if (0 == 1 && (topId != null) && !binder.isRoot()) {
					//top must be a workspace
					tree = getWorkspaceModule().getDomWorkspaceTree(topId, binder.getId(), 
							new WsDomTreeBuilder(binder, true, this, treeKey, page));
				} else {
					tree = getWorkspaceModule().getDomWorkspaceTree(binder.getId(), 
							new WsDomTreeBuilder(binder, true, this, treeKey, page),1);
				}
			} else {
				Folder topFolder = ((Folder)binder).getTopFolder();
				if (topFolder == null) topFolder = (Folder)binder;
				
				//must be a folder
				if (topId == null) {
					tree = getFolderModule().getDomFolderTree(topFolder.getId(), 
							new WsDomTreeBuilder(topFolder, false, this, treeKey));
				} else {
					Binder top = getBinderModule().getBinder(topId);
					if (top instanceof Folder)
						//just load the whole thing
						tree = getFolderModule().getDomFolderTree(top.getId(), 
								new WsDomTreeBuilder(top, false, this, treeKey));
					else {
						tree = getWorkspaceModule().getDomWorkspaceTree(topId, topFolder.getParentBinder().getId(), 
								new WsDomTreeBuilder(top, false, this, treeKey));
						Element topBinderElement = (Element)tree.selectSingleNode("//" + DomTreeBuilder.NODE_CHILD + "[@id='" + topFolder.getId() + "']");
						Document folderTree = getFolderModule().getDomFolderTree(topFolder.getId(), 
								new WsDomTreeBuilder(topFolder, false, this, treeKey));
						topBinderElement.setContent(folderTree.getRootElement().content());
					}
						
				}
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
			String url = WebUrlUtil.getServletRootURL() + WebKeys.SERVLET_VIEW_FILE + "?" +
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
	
	private void ajaxAddToClipboard(ActionRequest request, 
			ActionResponse response) throws Exception {
		String musterClass = PortletRequestUtils.getStringParameter(request, WebKeys.URL_MUSTER_CLASS, "");
		List musterIds = PortletRequestUtils.getLongListParameters(request, WebKeys.URL_MUSTER_IDS);
		
		Clipboard clipboard = new Clipboard(request);
		clipboard.add(musterClass, musterIds);
		
		
		Boolean addTeamMembers = PortletRequestUtils.getBooleanParameter(request, "add_team_members", false);
		if (addTeamMembers) {
			Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
			if (getBinderModule().testAccess(binderId, "getTeamMembers")) {
				Set teamMemberIds = getBinderModule().getTeamMemberIds(binderId, true);
				clipboard.add(Clipboard.USERS, new ArrayList(teamMemberIds));
			}
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
			getAdminModule().setWorkAreaOwner(binder, Long.valueOf(ownerId));
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
			Set ids = FindIdsHelper.getIdsAsLongSet(request.getParameterValues("users"));
			ids.addAll(FindIdsHelper.getIdsAsLongSet(request.getParameterValues("groups")));
			List principals = getProfileModule().getPrincipals(ids, RequestContextHolder.getRequestContext().getZoneId());
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
		if (displayType.equals("comments")) view = "dashboard/comments_view2";
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
				replies.add(entry);
				for (int i=0; i<replies.size(); i++) {
					FolderEntry reply = (FolderEntry)replies.get(i);
					accessControlMap.put(reply.getId(), entryAccessMap);
					//if any reply is not seen, add it to list - try to avoid update transaction
					if (!seen.checkIfSeen(reply)) {
						getProfileModule().setSeen(null, replies);
						break;
					}
				}
			} else if (!seen.checkIfSeen(entry)) {
				getProfileModule().setSeen(null, entry);
			}
		}
		return new ModelAndView("definition_elements/blog/view_blog_replies_content", model);
	}
	
	private ModelAndView ajaxGetMyTeams(RenderRequest request, 
			RenderResponse response) throws Exception {
	Map model = new HashMap();
	User user = RequestContextHolder.getRequestContext().getUser();
	List myTeams = getAdminModule().getTeamMemberships(user.getId());
	model.put(WebKeys.MY_TEAMS, myTeams);
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
				WebKeys.HELP_PANEL_ID, "ss_help_panel");
		model.put(WebKeys.HELP_PANEL_ID, helpPanelId);
		String op2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
		//See if the site has overridden the mapping for this help page
		String jsp = SPropsUtil.getString("help_system." + op2, "");
		if (jsp.equals("")) {
			//There is no override; use the id as the jsp name directly
			jsp = "/WEB-INF/jsp/help/" + op2 + ".jsp";
		}
		response.setContentType("text/xml");
		model.put(WebKeys.HELP_PANEL_JSP, jsp);

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
			Document query = SearchFilterRequestParser.getSearchQuery(request, getDefinitionModule());
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
		
		Tabs tabs = new Tabs(request);
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
		Folder folder = null;
		FolderEntry entry = null;

		// User context
		User user = RequestContextHolder.getRequestContext().getUser();
		model.put(WebKeys.USER_PRINCIPAL, user);


		if (!entryId.equals("")) {
			entry  = getFolderModule().getEntry(folderId, Long.valueOf(entryId));
			folder = entry.getParentFolder();
		} else {
			folder = getFolderModule().getFolder(folderId);
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

		if (getFolderModule().testAccess(entry, "reserveEntry")) {
			reserveAccessCheck = true;
		}
		if (getFolderModule().testAccess(entry, "overrideReserveEntry")) {
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
		
		if (getFolderModule().testAccess(entry, "addReply")) {
			accessControlEntryMap.put("addReply", new Boolean(true));
		}		
		
		if (getFolderModule().testAccess(entry, "modifyEntry")) {
			if (reserveAccessCheck && isEntryReserved && !(isUserBinderAdministrator || isLockedByAndLoginUserSame) ) {
			} else {
				accessControlEntryMap.put("modifyEntry", new Boolean(true));
			}
		}
		
		if (getFolderModule().testAccess(entry, "deleteEntry")) {
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
		
		Tabs tabs = new Tabs(request);
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
		memberIds.addAll(FindIdsHelper.getIdsAsLongSet(request
				.getParameterValues("users")));
				
		Binder binder = null;
		if (binderId != null) {
			binder = getBinderModule().getBinder(binderId);
		}
		Entry entry = null;
		if (Validator.isNotNull(entryId)) {
			entry = getFolderModule().getEntry(binderId, Long.valueOf(entryId));
		}
		
		String meetingToken = getIcBrokerModule().addMeeting(memberIds,
				binder, entry, "", -1, "", meetingType);

		model.put(WebKeys.MEETING_TOKEN, meetingToken);
		
		response.setContentType("text/json");
		return new ModelAndView("forum/meeting_return", model);		
	}	
	
	private ModelAndView ajaxGetTeamMembers(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		
		Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
		
		if (WebHelper.isUserLoggedIn(request) && getBinderModule().testAccess(binderId, "getTeamMembers")) {
			model.put(WebKeys.TEAM_MEMBERS, getBinderModule().getTeamMembers(binderId, true));
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
			Binder binder = getBinderModule().getBinder(binderId);
			
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

			Map folderEntries = new HashMap();
			Map options = new HashMap();
			
			User user = RequestContextHolder.getRequestContext().getUser();
			UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
			options.putAll(ListFolderController.getSearchFilter(request, userFolderProperties));
			
			options.put(ObjectKeys.SEARCH_MAX_HITS, 10000);
	       	options.put(ObjectKeys.SEARCH_EVENT_DAYS, calendarViewRangeDates.getExtViewDayDates());
	       	
	        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	       	options.put(ObjectKeys.SEARCH_LASTACTIVITY_DATE_START, formatter.format(calendarViewRangeDates.getStartViewExtWindow().getTime()));
	       	options.put(ObjectKeys.SEARCH_LASTACTIVITY_DATE_END, formatter.format(calendarViewRangeDates.getEndViewExtWindow().getTime()));

	       	options.put(ObjectKeys.SEARCH_CREATION_DATE_START, formatter.format(calendarViewRangeDates.getStartViewExtWindow().getTime()));
	       	options.put(ObjectKeys.SEARCH_CREATION_DATE_END, formatter.format(calendarViewRangeDates.getEndViewExtWindow().getTime()));

	       	List entries;
			if (binder instanceof Folder) {
				folderEntries = getFolderModule().getEntries(binderId, options);
				entries = (List) folderEntries.get(ObjectKeys.SEARCH_ENTRIES);
			} else {
				//a template
				entries = new ArrayList();
			}
			
			EventsViewHelper.getEvents(currentDate, calendarViewRangeDates, binder, entries, model, response, portletSession);
		} else {
			model.put(WebKeys.CALENDAR_VIEWBEAN , Collections.EMPTY_LIST);
		}
		
		response.setContentType("text/json");
		return new ModelAndView("forum/json/events", model);
	}
	
	private ModelAndView ajaxFindEntryForFile(RenderRequest request, RenderResponse response) throws Exception
	{
		Map model = new HashMap();
		if (WebHelper.isUserLoggedIn(request)) {
			model.put(WebKeys.USER_PRINCIPAL, RequestContextHolder.getRequestContext().getUser());
			Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_FOLDER_ID);
			Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID, 0L);
			String path = PortletRequestUtils.getStringParameter(request, WebKeys.URL_AJAX_VALUE,"");
			if(Validator.isNotNull(path)) {
				String fileName = new java.io.File(path).getName();
				Folder folder = getFolderModule().getFolder(binderId);
				FolderEntry entry = getFolderModule().getLibraryFolderEntryByFileName(folder, fileName);
				if(entry != null && (entryId == 0L || entryId != entry.getId().longValue())) {
					model.put(WebKeys.AJAX_ERROR_MESSAGE, "entry.duplicateFileInLibrary");
					model.put(WebKeys.AJAX_ERROR_DETAIL, entry.getTitle());
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
}
