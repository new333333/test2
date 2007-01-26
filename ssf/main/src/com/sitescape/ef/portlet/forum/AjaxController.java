package com.sitescape.ef.portlet.forum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.portlet.bind.PortletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Dashboard;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.domain.Subscription;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.module.profile.index.ProfileIndexUtils;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.module.shared.EntityIndexUtils;
import com.sitescape.ef.portletadapter.AdaptedPortletURL;
import com.sitescape.ef.portletadapter.MultipartFileSupport;
import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.search.QueryBuilder;
import com.sitescape.ef.util.SPropsUtil;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.BinderHelper;
import com.sitescape.ef.web.util.DashboardHelper;
import com.sitescape.ef.web.util.DefinitionHelper;
import com.sitescape.ef.web.util.Favorites;
import com.sitescape.ef.web.util.FilterHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.Tabs;
import com.sitescape.ef.web.util.WebHelper;
import com.sitescape.ef.web.util.WebUrlUtil;
import com.sitescape.ef.module.shared.WsDomTreeBuilder;
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
				op.equals(WebKeys.OPERATION_FIND_TAG_SEARCH)) {
			return ajaxFindUserSearch(request, response);

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
		}
		else if (op.equals(WebKeys.OPERATION_ADD_ATTACHMENT_OPTIONS)) {
			return addAttachmentOptions(request, response); 
		}		
		return ajaxReturn(request, response);
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
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		String entryId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		String operation2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
		String communityTag = PortletRequestUtils.getStringParameter(request, "communityTag", "");
		String personalTag = PortletRequestUtils.getStringParameter(request, "personalTag", "");
		String tagToDelete = PortletRequestUtils.getStringParameter(request, "tagToDelete", "");
		if (!entryId.equals("") && operation2.equals("delete")) {
			getFolderModule().setTagDelete(binderId, Long.valueOf(entryId), tagToDelete);
		} else if (!entryId.equals("") && operation2.equals("add")) {
			if (!communityTag.equals("")) getFolderModule().setTag(binderId, Long.valueOf(entryId), communityTag, true);
			if (!personalTag.equals("")) getFolderModule().setTag(binderId, Long.valueOf(entryId), personalTag, false);
		}
	}
	
	private void ajaxSaveFavorites(ActionRequest request, ActionResponse response) throws Exception {
		//Save the order of the favorites list
		String movedItemId = PortletRequestUtils.getStringParameter(request, "movedItemId", "");
		String favoritesList = PortletRequestUtils.getStringParameter(request, "favorites", "");
		UserProperties userProperties = getProfileModule().getUserProperties(null);
		Favorites f = new Favorites((String)userProperties.getProperty(ObjectKeys.USER_PROPERTY_FAVORITES));
		f.saveOrder(movedItemId, favoritesList);
		getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_FAVORITES, f.toString());
	}
	
	private void ajaxSaveRating(ActionRequest request, ActionResponse response) throws Exception {
		//Save the order of the favorites list
		Long rating = new Long(PortletRequestUtils.getRequiredLongParameter(request, "rating"));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, "entryId"));				
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, "binderId"));				
		getFolderModule().setUserRating(binderId, entryId, rating);
	}
	
	private void ajaxShowHideAllDashboardComponents(ActionRequest request,
			ActionResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		Boolean showAllComponents = Boolean.TRUE;
		if (op.equals(WebKeys.OPERATION_HIDE_ALL_DASHBOARD_COMPONENTS)) showAllComponents = Boolean.FALSE;
		try {
			Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
			getProfileModule().setUserProperty(user.getId(), binderId, 
					ObjectKeys.USER_PROPERTY_DASHBOARD_SHOW_ALL, showAllComponents);
		} catch (Exception ex) {
			String dashboardId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_DASHBOARD_ID);				
			Map updates = new HashMap();
			updates.put("showComponents", showAllComponents);
			getDashboardModule().modifyDashboard(dashboardId, updates);
		}
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
		model.put(WebKeys.FAVORITES_TREE, favTree);
		Document favTreeDelete = f.getFavoritesTreeDelete();
		model.put(WebKeys.FAVORITES_TREE_DELETE, favTreeDelete);
		model.put(WebKeys.NAMESPACE, namespace);

		response.setContentType("text/xml");
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
		Subscription sub = getBinderModule().getSubscription(binderId);
		model.put(WebKeys.SUBSCRIPTION, sub);
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
		model.put(WebKeys.COMMUNITY_TAGS, getFolderModule().getCommunityTags(binderId, entryId));
		model.put(WebKeys.PERSONAL_TAGS, getFolderModule().getPersonalTags(binderId, entryId));
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
		Element sfRoot = searchFilter.addElement(FilterHelper.FilterRootName);
		Element filterTerms = sfRoot.addElement(FilterHelper.FilterTerms);
		Element filterTerm = filterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeEntry);
		filterTerm.addAttribute(FilterHelper.FilterElementName, nameType);
		Element filterTermValueEle = filterTerm.addElement(FilterHelper.FilterElementValue);
		filterTermValueEle.setText(searchText);
	   	
		// check to see if the user has the right to see all users, just users in their community,
		// or no users.
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
		Map model = new HashMap();;
		String searchText = PortletRequestUtils.getStringParameter(request, "searchText", "");
		String findType = PortletRequestUtils.getStringParameter(request, "findType", "");
		String listDivId = PortletRequestUtils.getStringParameter(request, "listDivId", "");
		String maxEntries = PortletRequestUtils.getStringParameter(request, "maxEntries", "10");
		String pageNumber = PortletRequestUtils.getStringParameter(request, "pageNumber", "0");
		String namespace = PortletRequestUtils.getStringParameter(request, "namespace", "");
		Integer startingCount = Integer.parseInt(pageNumber) * Integer.parseInt(maxEntries);
		Integer maxEntriesTags = Integer.valueOf(200);

		User u = RequestContextHolder.getRequestContext().getUser();
		Map options = new HashMap();
		String view;
		options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.parseInt(maxEntries));
		options.put(ObjectKeys.SEARCH_OFFSET, startingCount);
		options.put(ObjectKeys.SEARCH_SORT_BY, EntityIndexUtils.SORT_TITLE_FIELD);
		options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(false));

		//Build the search query
		Document searchFilter = DocumentHelper.createDocument();
		Element sfRoot = searchFilter.addElement(FilterHelper.FilterRootName);
		Element filterTerms = sfRoot.addElement(FilterHelper.FilterTerms);
		options.put(ObjectKeys.SEARCH_SEARCH_FILTER, searchFilter);
		
		if (findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_PLACES)) {
			//Add the title term
			Element filterTerm = filterTerms.addElement(FilterHelper.FilterTerm);
			filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeSearchText);
			filterTerm.setText(searchText.replaceFirst("\\*", ""));
			
			filterTerm = filterTerms.addElement(FilterHelper.FilterTerm);
			filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeSearchText);
			filterTerm.setText(searchText);
			
			//Add terms to search folders and workspaces
			filterTerms = sfRoot.addElement(FilterHelper.FilterTerms);
			filterTerms.addAttribute(FilterHelper.FilterAnd, "true");
			filterTerm = filterTerms.addElement(FilterHelper.FilterTerm);
			filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeEntityTypes);
			Element filterTerm2 = filterTerm.addElement(FilterHelper.FilterEntityType);
			filterTerm2.setText(EntityIdentifier.EntityType.folder.name());
			filterTerm2 = filterTerm.addElement(FilterHelper.FilterEntityType);
			filterTerm2.setText(EntityIdentifier.EntityType.workspace.name());
			
		} else if (findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_TAGS)) {
			//Add terms to search tags
			Element filterTerm = filterTerms.addElement(FilterHelper.FilterTerm);
			filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeEntry);
			filterTerm.addAttribute(FilterHelper.FilterElementName, BasicIndexUtils.TAG_FIELD);
			Element filterTermValueEle = filterTerm.addElement(FilterHelper.FilterElementValue);
			filterTermValueEle.setText(searchText.replaceFirst("\\*", ""));

			filterTerm = filterTerms.addElement(FilterHelper.FilterTerm);
			filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeEntry);
			filterTerm.addAttribute(FilterHelper.FilterElementName, BasicIndexUtils.ACL_TAG_FIELD);
			filterTermValueEle = filterTerm.addElement(FilterHelper.FilterElementValue);
			filterTermValueEle.setText(BasicIndexUtils.buildAclTag(searchText.replaceFirst("\\*", ""), u.getId().toString()));

			filterTerm = filterTerms.addElement(FilterHelper.FilterTerm);
			filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeEntry);
			filterTerm.addAttribute(FilterHelper.FilterElementName, BasicIndexUtils.TAG_FIELD);
			filterTermValueEle = filterTerm.addElement(FilterHelper.FilterElementValue);
			filterTermValueEle.setText(searchText);

			filterTerm = filterTerms.addElement(FilterHelper.FilterTerm);
			filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeEntry);
			filterTerm.addAttribute(FilterHelper.FilterElementName, BasicIndexUtils.ACL_TAG_FIELD);
			filterTermValueEle = filterTerm.addElement(FilterHelper.FilterElementValue);
			filterTermValueEle.setText(BasicIndexUtils.buildAclTag(searchText, u.getId().toString()));

			//Tags are special. Always search for more than needed. They get paginated later
			options.put(ObjectKeys.SEARCH_MAX_HITS, maxEntriesTags);
			options.put(ObjectKeys.SEARCH_OFFSET, Integer.valueOf(0));

		} else {
			//Add the login name term
			Element filterTerm = filterTerms.addElement(FilterHelper.FilterTerm);
			filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeEntry);
			filterTerm.addAttribute(FilterHelper.FilterElementName, ProfileIndexUtils.LOGINNAME_FIELD);
			Element filterTermValueEle = filterTerm.addElement(FilterHelper.FilterElementValue);
			filterTermValueEle.setText(searchText.replaceFirst("\\*", ""));
		
			//Add a term to search the title field
			filterTerm = filterTerms.addElement(FilterHelper.FilterTerm);
			filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeEntry);
			filterTerm.addAttribute(FilterHelper.FilterElementName, EntityIndexUtils.TITLE_FIELD);
			filterTermValueEle = filterTerm.addElement(FilterHelper.FilterElementValue);
			filterTermValueEle.setText(searchText.replaceFirst("\\*", ""));
		
			//Add the login name term
			filterTerm = filterTerms.addElement(FilterHelper.FilterTerm);
			filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeEntry);
			filterTerm.addAttribute(FilterHelper.FilterElementName, ProfileIndexUtils.LOGINNAME_FIELD);
			filterTermValueEle = filterTerm.addElement(FilterHelper.FilterElementValue);
			filterTermValueEle.setText(searchText);
		
			//Add a term to search the title field
			filterTerm = filterTerms.addElement(FilterHelper.FilterTerm);
			filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeEntry);
			filterTerm.addAttribute(FilterHelper.FilterElementName, EntityIndexUtils.TITLE_FIELD);
			filterTermValueEle = filterTerm.addElement(FilterHelper.FilterElementValue);
			filterTermValueEle.setText(searchText);
			
			// check to see if the user has the right to see all users, just users in their community,
			// or no users.
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
		}
	   	
		//Do a search to find the first few items that match the search text
		if (findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_PLACES)) {
			Map retMap = getBinderModule().executeSearchQuery( searchFilter, options);
			List entries = (List)retMap.get(WebKeys.FOLDER_ENTRIES);
			model.put(WebKeys.ENTRIES, entries);
			model.put(WebKeys.SEARCH_TOTAL_HITS, retMap.get(WebKeys.ENTRY_SEARCH_COUNT));
			view = "forum/find_places_search";
		} else if (findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_TAGS)) {
			Map retMap = getBinderModule().executeSearchQuery( searchFilter, options);
			List entries = (List)retMap.get(WebKeys.FOLDER_ENTRIES);
			String wordRoot = searchText;
			int i = wordRoot.indexOf("*");
			if (i > 0) wordRoot = wordRoot.substring(0, i);
			
			List tags = BinderHelper.sortCommunityTags(entries, wordRoot);
			List tagsPage = new ArrayList();
			if (tags.size() > startingCount.intValue()) {
				int endTag = startingCount.intValue() + Integer.valueOf(maxEntries);
				if (tags.size() < endTag) endTag = tags.size();
				tagsPage = tags.subList(startingCount.intValue(), endTag);
			}
			model.put(WebKeys.TAGS, tagsPage);
			model.put(WebKeys.SEARCH_TOTAL_HITS, Integer.valueOf(tags.size()));
			view = "forum/find_tag_search";
		} else if (findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_GROUP)) {
			Map entries = getProfileModule().getGroups(u.getParentBinder().getId(), options);
			model.put(WebKeys.USERS, entries.get(ObjectKeys.SEARCH_ENTRIES));
			model.put(WebKeys.SEARCH_TOTAL_HITS, entries.get(ObjectKeys.SEARCH_COUNT_TOTAL));
			view = "forum/find_user_search";
		} else {
			Map entries = getProfileModule().getUsers(u.getParentBinder().getId(), options);
			model.put(WebKeys.USERS, entries.get(ObjectKeys.SEARCH_ENTRIES));
			model.put(WebKeys.SEARCH_TOTAL_HITS, entries.get(ObjectKeys.SEARCH_COUNT_TOTAL));
			view = "forum/find_user_search";
		}
		model.put(WebKeys.USER_SEARCH_USER_GROUP_TYPE, findType);
		model.put(WebKeys.PAGE_SIZE, maxEntries);
		model.put(WebKeys.PAGE_NUMBER, pageNumber);
		model.put(WebKeys.DIV_ID, listDivId);
		model.put(WebKeys.NAMESPACE, namespace);
		response.setContentType("text/xml");
		return new ModelAndView(view, model);
	}

	private ModelAndView ajaxGetFilterData(RenderRequest request, 
				RenderResponse response) throws Exception {
		Map model = new HashMap();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		String op2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Binder binder = getBinderModule().getBinder(binderId);
		model.put(WebKeys.BINDER, binder);
			
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
		
		String value = PortletRequestUtils.getStringParameter(request, WebKeys.CONDITION_ELEMENT_OPERATION);
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
		Long binderId = PortletRequestUtils.getLongParameter(request, "binderId");
		if (binderId != null) {
			String treeName = PortletRequestUtils.getStringParameter(request, "treeName", "");
			String treeKey = null; 
			int pos = treeName.indexOf("_");
			if (pos != -1)  {
				treeKey = treeName.substring(0, pos);
			}
			model.put("ss_tree_treeName", treeName);
			model.put("ss_tree_showIdRoutine", PortletRequestUtils.getStringParameter(request, "showIdRoutine", ""));
			model.put("ss_tree_parentId", PortletRequestUtils.getStringParameter(request, "parentId", ""));
			model.put("ss_tree_bottom", PortletRequestUtils.getStringParameter(request, "bottom", ""));
			model.put("ss_tree_type", PortletRequestUtils.getStringParameter(request, "type", ""));
			model.put("ss_tree_binderId", binderId.toString());
			model.put("ss_tree_topId", op2);
			model.put("ss_tree_select_id", "");
			model.put("ss_tree_select_type", selectType);
			if (selectType.equals("2")) {
				//multi select
				model.put("ss_tree_select", new ArrayList());
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
				if ((topId != null) && (binder.getParentBinder() != null)) {
					//top must be a workspace
					tree = getWorkspaceModule().getDomWorkspaceTree(topId, binder.getId(), new WsDomTreeBuilder(binder, true, this, treeKey));
				} else {
					tree = getWorkspaceModule().getDomWorkspaceTree(binder.getId(), new WsDomTreeBuilder(binder, true, this, treeKey),1);
				}
			} else {
				Folder topFolder = ((Folder)binder).getTopFolder();
				if (topFolder == null) topFolder = (Folder)binder;
				
				//must be a folder
				if (topId == null) {
					tree = getFolderModule().getDomFolderTree(topFolder.getId(), new WsDomTreeBuilder(topFolder, false, this, treeKey));
				} else {
					Binder top = getBinderModule().getBinder(topId);
					if (top instanceof Folder)
						//just load the whole thing
						tree = getFolderModule().getDomFolderTree(top.getId(), new WsDomTreeBuilder(top, false, this, treeKey));
					else {
						tree = getWorkspaceModule().getDomWorkspaceTree(topId, topFolder.getParentBinder().getId(), new WsDomTreeBuilder(top, false, this, treeKey));
						Element topBinderElement = (Element)tree.selectSingleNode("//" + DomTreeBuilder.NODE_CHILD + "[@id='" + topFolder.getId() + "']");
						Document folderTree = getFolderModule().getDomFolderTree(topFolder.getId(), new WsDomTreeBuilder(topFolder, false, this, treeKey));
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
		try {
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
		} catch (Exception ex) {
			String dashboardId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_DASHBOARD_ID);				
			Dashboard dashboard = getDashboardModule().getDashboard(dashboardId);
			scope = DashboardHelper.Portlet;
	
			if (!componentId.equals("") && op.equals(WebKeys.OPERATION_DASHBOARD_SHOW_COMPONENT)) {
				DashboardHelper.showHideComponent(request, dashboard, componentId, "show");
			} else if (!componentId.equals("") && op.equals(WebKeys.OPERATION_DASHBOARD_HIDE_COMPONENT)) {
				DashboardHelper.showHideComponent(request, dashboard, componentId, "hide");
			} else if (!componentId.equals("") && op.equals(WebKeys.OPERATION_DASHBOARD_DELETE_COMPONENT)) {
				DashboardHelper.deleteComponent(request, dashboard, componentId);
			}
			
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
	
	private ModelAndView ajaxGetDashboardComponent(RenderRequest request, 
				RenderResponse response) throws Exception {
		Map model = new HashMap();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		String op2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
		String componentId = op2;

		if (op.equals(WebKeys.OPERATION_DASHBOARD_SHOW_COMPONENT)) {
			if (!componentId.equals("")) {
				try {
					Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
					Binder binder = getBinderModule().getBinder(binderId);
					model.put(WebKeys.BINDER, binder);
					String scope = PortletRequestUtils.getStringParameter(request, "_scope", "");
					if (scope.equals("")) scope = DashboardHelper.Local;
					User user = RequestContextHolder.getRequestContext().getUser();
					DashboardHelper.getDashboardMap(binder, 
						getProfileModule().getUserProperties(user.getId()).getProperties(), 
						model, scope, componentId);
				} catch (Exception ex) {
					String dashboardId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_DASHBOARD_ID);				
					Dashboard dashboard = getDashboardModule().getDashboard(dashboardId);
					User user = RequestContextHolder.getRequestContext().getUser();
					DashboardHelper.getDashboardMap(dashboard, 
						getProfileModule().getUserProperties(user.getId()).getProperties(), 
						model, componentId);
					
				}
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
	
		if (!componentId.equals("")) {
			try {
				Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
				Binder binder = getBinderModule().getBinder(binderId);
				model.put(WebKeys.BINDER, binder);
				String scope = PortletRequestUtils.getStringParameter(request, "_scope", "");
				if (scope.equals("")) scope = DashboardHelper.Local;
				User user = RequestContextHolder.getRequestContext().getUser();
				DashboardHelper.getDashboardMap(binder, 
					getProfileModule().getUserProperties(user.getId()).getProperties(), 
					model, scope, componentId);
			} catch (Exception ex) {
				String dashboardId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_DASHBOARD_ID);				
				Dashboard dashboard = getDashboardModule().getDashboard(dashboardId);
				User user = RequestContextHolder.getRequestContext().getUser();
				DashboardHelper.getDashboardMap(dashboard, 
					getProfileModule().getUserProperties(user.getId()).getProperties(), 
					model, componentId);
				
			}
		}
		String view = "dashboard/search_view2";
		String displayType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_DISPLAY_TYPE, "search");
		if (displayType.equals("search")) view = "dashboard/search_view2";
		if (displayType.equals("gallery")) view = "dashboard/gallery_view2";
		if (displayType.equals("blog")) view = "dashboard/blog_view2";
		if (displayType.equals("comments")) view = "dashboard/comments_view2";
		return new ModelAndView(view, model);
	}
	
	private ModelAndView ajaxGetBlogReplies(RenderRequest request, 
				RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		FolderEntry entry = null;
		Map folderEntries = null;
		folderEntries  = getFolderModule().getEntryTree(binderId, entryId);
		if (folderEntries != null) {
			entry = (FolderEntry)folderEntries.get(ObjectKeys.FOLDER_ENTRY);
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
			Document query = null;
			try {
				query = FilterHelper.getSearchFilter(request);
			} catch(Exception ex) {}
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
		String namespace = PortletRequestUtils.getStringParameter(request, "namespace", "");
		Map model = new HashMap();
		List functions = getAdminModule().getFunctions();
		List membership;
		if (binder.isFunctionMembershipInherited()) {
			membership = getAdminModule().getWorkAreaFunctionMembershipsInherited(binder);
		} else {
			membership = getAdminModule().getWorkAreaFunctionMemberships(binder);
		}
		BinderHelper.buildAccessControlTableBeans(request, response, binder, functions, 
				membership, model, false);

		if (!binder.isFunctionMembershipInherited()) {
			Binder parentBinder = binder.getParentBinder();
			if (parentBinder != null) {
				List parentMembership;
				if (parentBinder.isFunctionMembershipInherited()) {
					parentMembership = getAdminModule().getWorkAreaFunctionMembershipsInherited(parentBinder);
				} else {
					parentMembership = getAdminModule().getWorkAreaFunctionMemberships(parentBinder);
				}
				Map modelParent = new HashMap();
				BinderHelper.buildAccessControlTableBeans(request, response, parentBinder, 
						functions, parentMembership, modelParent, true);
				model.put(WebKeys.ACCESS_PARENT, modelParent);
				BinderHelper.mergeAccessControlTableBeans(model);
			}
		}
		
		User user = RequestContextHolder.getRequestContext().getUser();
		model.put(WebKeys.USER_PRINCIPAL, user);
		model.put(WebKeys.NAMESPACE, namespace);
		response.setContentType("text/xml");
		return new ModelAndView("binder/access_control_ajax", model);
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
		model.put(WebKeys.ENTRY_ATTACHMENT_FILE_RECEIVER_URL, strURL);
		
		//response.setContentType("text/xml");
		return new ModelAndView("definition_elements/entry_attachment_options", model);
	}
	
	private ModelAndView ajaxGetUploadImageFile(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String url = PortletRequestUtils.getStringParameter(request, WebKeys.IMAGE_FILE_URL, "");
		model.put(WebKeys.UPLOAD_FILE_URL, url);

		return new ModelAndView("binder/upload_image_file_ajax_return", model);
	}
}
