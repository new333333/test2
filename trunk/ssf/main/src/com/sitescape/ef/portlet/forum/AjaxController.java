package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.module.profile.index.IndexUtils;
import com.sitescape.ef.module.shared.EntryIndexUtils;
import com.sitescape.ef.search.QueryBuilder;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.DefinitionUtils;
import com.sitescape.ef.web.util.FilterHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.WebHelper;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Workspace;

/**
 * @author Peter Hurley
 *
 */
public class AjaxController  extends SAbstractForumController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		if (WebHelper.isUserLoggedIn(request)) {
		   	User user = RequestContextHolder.getRequestContext().getUser();
			Map formData = request.getParameterMap();
			String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
			if (op.equals(WebKeys.FORUM_OPERATION_SAVE_COLUMN_POSITIONS)) {
				String binderId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
				//Save the user's placement of columns in this folder
				String columnPositions = ((String[])formData.get("column_positions"))[0];
				if (!columnPositions.equals("")) {
					//Save the column positions
				   	getProfileModule().setUserFolderProperty(user.getId(), Long.valueOf(binderId), WebKeys.FOLDER_COLUMN_POSITIONS, columnPositions);
				}
			}
		}
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map formData = request.getParameterMap();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		String op2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
		if (!WebHelper.isUserLoggedIn(request)) {
			Map model = new HashMap();
			Map unseenCounts = new HashMap();
			Map statusMap = new HashMap();
			
			//Signal that the user is not logged in. 
			//  The code on the calling page will output the proper translated message.
			statusMap.put(WebKeys.AJAX_STATUS_NOT_LOGGED_IN, new Boolean(true));
			model.put(WebKeys.AJAX_STATUS, statusMap);
			
			response.setContentType("text/xml");			
			if (op.equals(WebKeys.FORUM_OPERATION_UNSEEN_COUNTS)) {
				model.put(WebKeys.LIST_UNSEEN_COUNTS, unseenCounts);
				return new ModelAndView("forum/unseen_counts", model);
			} else if (op.equals(WebKeys.FORUM_OPERATION_SAVE_COLUMN_POSITIONS)) {
				return new ModelAndView("forum/save_column_positions_return", model);
			} else if (op.equals(WebKeys.FORUM_OPERATION_SAVE_ENTRY_WIDTH)) {
				return new ModelAndView("forum/save_entry_width_return", model);
			} else if (op.equals(WebKeys.FORUM_OPERATION_SAVE_ENTRY_HEIGHT)) {
				return new ModelAndView("forum/save_entry_height_return", model);
			} else if (op.equals(WebKeys.FORUM_OPERATION_GET_ENTRY_ELEMENTS)) {
				return new ModelAndView("binder/get_entry_elements", model);
			} else if (op.equals(WebKeys.FORUM_OPERATION_WORKSPACE_TREE)) {
				return new ModelAndView("tag_jsps/tree/get_tree_div", model);
			}
			return new ModelAndView("forum/ajax_return", model);
		}
		
	   	User user = RequestContextHolder.getRequestContext().getUser();
		Map model = new HashMap();
		Map statusMap = new HashMap();

		if (op.equals(WebKeys.FORUM_OPERATION_UNSEEN_COUNTS)) {
			List folderIds = new ArrayList();
			String[] forumList = new String[0];
			if (PortletRequestUtils.getStringParameter(request, "forumList") != null) {
				forumList = PortletRequestUtils.getStringParameter(request, "forumList").split(" ");
			}
			for (int i = 0; i < forumList.length; i++) {
				folderIds.add(new Long(forumList[i]));
			}
			Map unseenCounts = getFolderModule().getUnseenCounts(folderIds);

			response.setContentType("text/xml");
			
			model.put(WebKeys.LIST_UNSEEN_COUNTS, unseenCounts);
			model.put(WebKeys.AJAX_STATUS, statusMap);
			return new ModelAndView("forum/unseen_counts", model);
			
		} else if (op.equals(WebKeys.FORUM_OPERATION_UNSEEN_LIST)) {
			
		} else if (op.equals(WebKeys.FORUM_OPERATION_SAVE_COLUMN_POSITIONS)) {
			response.setContentType("text/xml");
			model.put(WebKeys.AJAX_STATUS, statusMap);
			return new ModelAndView("forum/save_column_positions_return", model);
			
		} else if (op.equals(WebKeys.FORUM_OPERATION_SAVE_ENTRY_WIDTH)) {
			//Save the user's selected entry width
			String entryWidth = ((String[])formData.get("entry_width"))[0];
			if (!entryWidth.equals("")) {
				//Save the entry width
			   	getProfileModule().setUserProperty(user.getId(), WebKeys.FOLDER_ENTRY_WIDTH, entryWidth);
			}
			response.setContentType("text/xml");
			model.put(WebKeys.AJAX_STATUS, statusMap);
			return new ModelAndView("forum/save_entry_width_return", model);
		} else if (op.equals(WebKeys.FORUM_OPERATION_SAVE_ENTRY_HEIGHT)) {
			String entryHeight = ((String[])formData.get("entry_height"))[0];
			if (!entryHeight.equals("")) {
				//Save the entry width
				String binderId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
			   	getProfileModule().setUserFolderProperty(user.getId(), Long.valueOf(binderId), WebKeys.FOLDER_ENTRY_HEIGHT, entryHeight);
			}
			response.setContentType("text/xml");
			model.put(WebKeys.AJAX_STATUS, statusMap);
			return new ModelAndView("forum/save_entry_height_return", model);
		
		} else if (op.equals(WebKeys.FORUM_OPERATION_USER_LIST_SEARCH)) {
			String searchText = ((String[])formData.get("searchText"))[0];
			String searchType = ((String[])formData.get("searchType"))[0];
			String userGroupType = ((String[])formData.get("userGroupType"))[0];
			String listDivId = ((String[])formData.get("listDivId"))[0];
			String maxEntries = ((String[])formData.get("maxEntries"))[0];
			String[] idsToSkip = ((String[])formData.get("idsToSkip"))[0].split(" ");
			Map userIdsToSkip = new HashMap();
			for (int i = 0; i < idsToSkip.length; i++) {
				if (!idsToSkip[i].equals("")) userIdsToSkip.put(idsToSkip[i], Long.valueOf(idsToSkip[i]));
			}
			
	    	String nameType = IndexUtils.LASTNAME_FIELD;
	    	if (searchType.equals("firstName")) nameType = IndexUtils.FIRSTNAME_FIELD;
	    	if (searchType.equals("loginName")) nameType = IndexUtils.USERNAME_FIELD;
	    	if (searchType.equals("groupName")) nameType = IndexUtils.GROUPNAME_FIELD;
	    	if (searchType.equals("title")) nameType = EntryIndexUtils.TITLE_FIELD;

	    	//Build the search query
			Document searchFilter = DocumentHelper.createDocument();
			Element sfRoot = searchFilter.addElement(FilterHelper.FilterRootName);
			Element filterTerms = sfRoot.addElement(FilterHelper.FilterTerms);
			Element filterTerm = filterTerms.addElement(FilterHelper.FilterTerm);
			filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeEntry);
			filterTerm.addAttribute(FilterHelper.FilterElementName, nameType);
			Element filterTermValueEle = filterTerm.addElement(FilterHelper.FilterElementValue);
			filterTermValueEle.setText(searchText);
			
			//Do a search to find the first few users who match the search text
        	User u = RequestContextHolder.getRequestContext().getUser();
        	Map users = new HashMap();
        	if (userGroupType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_GROUP)) {
	    		users = getProfileModule().getGroups(u.getParentBinder().getId(), 
		    			Integer.parseInt(maxEntries), searchFilter);
        	} else {
	    		users = getProfileModule().getUsers(u.getParentBinder().getId(), 
		    			Integer.parseInt(maxEntries), searchFilter);
        	}
    		model.put(WebKeys.USERS, users.get(ObjectKeys.ENTRIES));
    		model.put(WebKeys.USER_IDS_TO_SKIP, userIdsToSkip);
    		model.put(WebKeys.USER_SEARCH_USER_GROUP_TYPE, userGroupType);
    		model.put("listDivId", listDivId);
			response.setContentType("text/xml");
			model.put(WebKeys.AJAX_STATUS, statusMap);
			return new ModelAndView("forum/user_list_search", model);

		} else if (op.equals(WebKeys.FORUM_OPERATION_GET_FILTER_TYPE) || 
				op.equals(WebKeys.FORUM_OPERATION_GET_ENTRY_ELEMENTS) || 
				op.equals(WebKeys.FORUM_OPERATION_GET_ELEMENT_VALUES)) {
			Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
			Binder binder = getBinderModule().getBinder(binderId);
			model.put(WebKeys.BINDER, binder);
			model.put(WebKeys.FOLDER_WORKFLOW_ASSOCIATIONS, binder.getProperty(ObjectKeys.BINDER_WORKFLOW_ASSOCIATIONS));
				
			String filterTermNumber = ((String[])formData.get(WebKeys.FILTER_ENTRY_FILTER_TERM_NUMBER))[0];
			model.put(WebKeys.FILTER_ENTRY_FILTER_TERM_NUMBER, filterTermNumber);
			
			//Get the definition id (if present)
			if (op.equals(WebKeys.FORUM_OPERATION_GET_ENTRY_ELEMENTS) || 
					op.equals(WebKeys.FORUM_OPERATION_GET_ELEMENT_VALUES)) {
				String defId = ((String[])formData.get(WebKeys.FILTER_ENTRY_DEF_ID+filterTermNumber))[0];
				if (defId.equals("_common")) {
					model.put(WebKeys.FILTER_ENTRY_DEF_ID, "");
					Map elementData = getFolderModule().getCommonEntryElements(binderId);
					model.put(WebKeys.ENTRY_DEFINTION_ELEMENT_DATA, elementData);
				} else {
					model.put(WebKeys.FILTER_ENTRY_DEF_ID, defId);
					Map elementData = getDefinitionModule().getEntryDefinitionElements(defId);
					model.put(WebKeys.ENTRY_DEFINTION_ELEMENT_DATA, elementData);
				}
			}
			
			if (formData.containsKey("elementName" + filterTermNumber)) {
				String elementName = ((String[])formData.get("elementName" + filterTermNumber))[0];
				model.put(WebKeys.FILTER_ENTRY_ELEMENT_NAME, elementName);
			}

			Map defaultEntryDefinitions = DefinitionUtils.getEntryDefsAsMap(binder);
			model.put(WebKeys.ENTRY_DEFINTION_MAP, defaultEntryDefinitions);

			DefinitionUtils.getDefinitions(model);
			DefinitionUtils.getDefinitions(binder, model);
	    	DefinitionUtils.getDefinitions(Definition.WORKFLOW, WebKeys.PUBLIC_WORKFLOW_DEFINITIONS, model);
	    	Map workflowAssociations = (Map) binder.getProperty(ObjectKeys.BINDER_WORKFLOW_ASSOCIATIONS);
			
	    	model.put(WebKeys.AJAX_STATUS, statusMap);
			response.setContentType("text/xml");
			if (op.equals(WebKeys.FORUM_OPERATION_GET_FILTER_TYPE)) {
				model.put(WebKeys.FILTER_TYPE, op2);
				return new ModelAndView("binder/get_filter_type", model);
			} else if (op.equals(WebKeys.FORUM_OPERATION_GET_ENTRY_ELEMENTS)) {
				return new ModelAndView("binder/get_entry_elements", model);
			} else {
				return new ModelAndView("binder/get_element_value", model);
			}

		} else if (op.equals(WebKeys.FORUM_OPERATION_WORKSPACE_TREE)) {
			if (formData.containsKey("binderId")) {
				model.put("ss_tree_treeName", ((String[])formData.get("treeName"))[0]);
				model.put("ss_tree_binderId", ((String[])formData.get("binderId"))[0]);
				Long binderId = Long.valueOf(((String[])formData.get("binderId"))[0]);
				Binder binder = getBinderModule().getBinder(binderId);
				String view = getShowWorkspace(formData, request, response, (Workspace)binder, null, model);
			}
			response.setContentType("text/xml");
			model.put(WebKeys.AJAX_STATUS, statusMap);
			return new ModelAndView("tag_jsps/tree/get_tree_div", model);
		
		}
		
		return new ModelAndView(WebKeys.VIEW_FORUM, model);
	} 
}
