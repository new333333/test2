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
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.WebHelper;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.User;

/**
 * @author Peter Hurley
 *
 */
public class AjaxController  extends SAbstractForumController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		Map formData = request.getParameterMap();
		if(WebHelper.isUserLoggedIn(request)) {
			String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
			if (op.equals(WebKeys.FORUM_OPERATION_SAVE_COLUMN_POSITIONS)) {
				String binderId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
				//Save the user's placement of columns in this folder
				String columnPositions = ((String[])formData.get("column_positions"))[0];
				if (!columnPositions.equals("")) {
					//Save the column positions
				   	User user = RequestContextHolder.getRequestContext().getUser();
				   	getProfileModule().setUserFolderProperty(user.getId(), Long.valueOf(binderId), WebKeys.FOLDER_COLUMN_POSITIONS, columnPositions);

				}
			} else if (op.equals(WebKeys.FORUM_OPERATION_SAVE_ENTRY_WIDTH)) {
				//Save the user's selected entry width
				String entryWidth = ((String[])formData.get("entry_width"))[0];
				if (!entryWidth.equals("")) {
					//Save the entry width
				   	User user = RequestContextHolder.getRequestContext().getUser();
				   	getProfileModule().setUserProperty(user.getId(), WebKeys.FOLDER_ENTRY_WIDTH, entryWidth);
				}
			}
		}
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map formData = request.getParameterMap();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if(!WebHelper.isUserLoggedIn(request)) {
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
			}
			return new ModelAndView("forum/ajax_return", model);
		}
		
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
			response.setContentType("text/xml");
			model.put(WebKeys.AJAX_STATUS, statusMap);
			return new ModelAndView("forum/save_entry_width_return", model);
		
		} else if (op.equals(WebKeys.FORUM_OPERATION_USER_LIST_SEARCH)) {
			String searchText = ((String[])formData.get("searchText"))[0];
			String searchType = ((String[])formData.get("searchType"))[0];
			String listDivId = ((String[])formData.get("listDivId"))[0];
			String maxEntries = ((String[])formData.get("maxEntries"))[0];
			
			//Build the search query
			Document qTree = DocumentHelper.createDocument();
			Element rootElement = qTree.addElement(QueryBuilder.QUERY_ELEMENT);
	    	Element boolElement = rootElement.addElement(QueryBuilder.AND_ELEMENT);

	    	//Add the search text
	    	String nameType = IndexUtils.LASTNAME_FIELD;
	    	if (searchType.equals("firstName")) nameType = IndexUtils.FIRSTNAME_FIELD;
	    	if (searchType.equals("loginName")) nameType = IndexUtils.USERNAME_FIELD;
    		Element field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
        	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,nameType);
        	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
        	child.setText(searchText);
	    	
			//Do a search to find the first few users who match the search text
        	User u = RequestContextHolder.getRequestContext().getUser();
        	Map users = getProfileModule().getUsers(u.getParentBinder().getId(), Integer.parseInt(maxEntries), qTree);
    		model.put(WebKeys.USERS, users.get(ObjectKeys.ENTRIES));
    		model.put("listDivId", listDivId);
			response.setContentType("text/xml");
			model.put(WebKeys.AJAX_STATUS, statusMap);
			return new ModelAndView("forum/user_list_search", model);
		}
		
		return new ModelAndView(WebKeys.VIEW_FORUM, model);
	} 
}
