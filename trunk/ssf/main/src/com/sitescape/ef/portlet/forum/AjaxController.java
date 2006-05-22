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
import com.sitescape.ef.module.profile.index.ProfileIndexUtils;
import com.sitescape.ef.module.shared.EntryIndexUtils;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.DefinitionUtils;
import com.sitescape.ef.web.util.Favorites;
import com.sitescape.ef.web.util.FilterHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.WebHelper;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.portlet.workspaceTree.WorkspaceTreeController.WsTreeBuilder;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.portlet.forum.SAbstractForumController.TreeBuilder;
/**
 * @author Peter Hurley
 *
 */
public class AjaxController  extends SAbstractController {
	private Map model;
	private Map statusMap;
	private Map unseenCounts;
	private Map formData;
	private User user;
	private String op;
	private String op2;
	
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		if (WebHelper.isUserLoggedIn(request)) {
		   	this.user = RequestContextHolder.getRequestContext().getUser();
			this.formData = request.getParameterMap();
			this.op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
			if (op.equals(WebKeys.FORUM_OPERATION_SAVE_COLUMN_POSITIONS)) {
				ajaxSaveColumnPositions(request, response);
			} else if (op.equals(WebKeys.FORUM_OPERATION_ADD_FAVORITE_BINDER)) {
				ajaxAddFavoriteBinder(request, response);
			} else if (op.equals(WebKeys.FORUM_OPERATION_ADD_FAVORITES_CATEGORY)) {
				ajaxAddFavoritesCategory(request, response);
			} else if (op.equals(WebKeys.FORUM_OPERATION_SAVE_FAVORITES)) {
				ajaxSaveFavorites(request, response);
			}
		}
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		this.model = new HashMap();
		this.unseenCounts = new HashMap();
		this.statusMap = new HashMap();
		this.formData = request.getParameterMap();
		this. op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		this.op2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
		
		if (!WebHelper.isUserLoggedIn(request)) {
			
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
			} else if (op.equals(WebKeys.FORUM_OPERATION_GET_FILTER_TYPE) || 
					op.equals(WebKeys.FORUM_OPERATION_GET_ENTRY_ELEMENTS) || 
					op.equals(WebKeys.FORUM_OPERATION_GET_ELEMENT_VALUES) || 
					op.equals(WebKeys.FORUM_OPERATION_GET_ELEMENT_VALUE_DATA)) {
				return new ModelAndView("binder/get_entry_elements", model);
			} else if (op.equals(WebKeys.FORUM_OPERATION_GET_CONDITION_ENTRY_ELEMENTS)) {
				return new ModelAndView("definition_builder/get_condition_element", model);
			} else if (op.equals(WebKeys.FORUM_OPERATION_WORKSPACE_TREE)) {
				return new ModelAndView("tag_jsps/tree/get_tree_div", model);
			} else if (op.equals(WebKeys.FORUM_OPERATION_ADD_FAVORITE_BINDER) || 
					op.equals(WebKeys.FORUM_OPERATION_ADD_FAVORITES_CATEGORY) || 
					op.equals(WebKeys.FORUM_OPERATION_SAVE_FAVORITES)) {
				return new ModelAndView("forum/favorites_return", model);
			} else if (op.equals(WebKeys.FORUM_OPERATION_GET_FAVORITES_TREE)) {
				return new ModelAndView("forum/favorites_tree", model);
			}
			return new ModelAndView("forum/ajax_return", model);
		}
	   	this.user = RequestContextHolder.getRequestContext().getUser();		
		
		if (op.equals(WebKeys.FORUM_OPERATION_UNSEEN_COUNTS)) {
			return ajaxGetUnseenCounts(request, response);
			
		} else if (op.equals(WebKeys.FORUM_OPERATION_ADD_FAVORITE_BINDER) || 
				op.equals(WebKeys.FORUM_OPERATION_ADD_FAVORITES_CATEGORY) || 
				op.equals(WebKeys.FORUM_OPERATION_SAVE_FAVORITES)) {
			return ajaxGetFavoritesTree(request, response);
			
		} else if (op.equals(WebKeys.FORUM_OPERATION_SAVE_COLUMN_POSITIONS)) {
			return ajaxSaveColumnPositions(request, response);
			
		} else if (op.equals(WebKeys.FORUM_OPERATION_SAVE_ENTRY_WIDTH)) {
			return ajaxSaveEntryWidth(request, response);
			
		} else if (op.equals(WebKeys.FORUM_OPERATION_SAVE_ENTRY_HEIGHT)) {
			return ajaxSaveEntryHeight(request, response);
			
		} else if (op.equals(WebKeys.FORUM_OPERATION_USER_LIST_SEARCH)) {
			return ajaxUserListSearch(request, response);

		} else if (op.equals(WebKeys.FORUM_OPERATION_GET_FILTER_TYPE) || 
				op.equals(WebKeys.FORUM_OPERATION_GET_ENTRY_ELEMENTS) || 
				op.equals(WebKeys.FORUM_OPERATION_GET_ELEMENT_VALUES) || 
				op.equals(WebKeys.FORUM_OPERATION_GET_ELEMENT_VALUE_DATA)) {
			return ajaxGetFilterData(request, response);

		} else if (op.equals(WebKeys.FORUM_OPERATION_GET_CONDITION_ENTRY_ELEMENTS)) {
			return ajaxGetConditionData(request, response);

		} else if (op.equals(WebKeys.FORUM_OPERATION_WORKSPACE_TREE)) {
			return ajaxGetWorkspaceTree(request, response);

		} else if (op.equals(WebKeys.FORUM_OPERATION_GET_FAVORITES_TREE)) {
			return ajaxGetFavoritesTree(request, response);
		}
		
		return ajaxReturn(request, response);
	} 
	
	private void ajaxSaveColumnPositions(ActionRequest request, ActionResponse response) {
		String binderId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
		//Save the user's placement of columns in this folder
		String columnPositions = ((String[])formData.get("column_positions"))[0];
		if (!columnPositions.equals("")) {
			//Save the column positions
		   	getProfileModule().setUserProperty(user.getId(), Long.valueOf(binderId), WebKeys.FOLDER_COLUMN_POSITIONS, columnPositions);
		}
	}
	
	private void ajaxAddFavoriteBinder(ActionRequest request, ActionResponse response) {
		//Add a binder to the favorites list
		String binderId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
		Binder binder = getBinderModule().getBinder(Long.valueOf(binderId));
		UserProperties userProperties = getProfileModule().getUserProperties(user.getId());
		Document favorites = (Document) userProperties.getProperty(ObjectKeys.USER_PROPERTY_FAVORITES);
		Favorites f = new Favorites(favorites);
		favorites = f.addFavorite(binder.getTitle(), Favorites.FAVORITE_BINDER, binderId, PortletRequestUtils.getStringParameter(request, "viewAction", ""), "");
		getProfileModule().setUserProperty(user.getId(), ObjectKeys.USER_PROPERTY_FAVORITES, favorites);
	}
	
	private void ajaxAddFavoritesCategory(ActionRequest request, ActionResponse response) {
		//Add a category to the favorites list
		String category = ((String[])formData.get("category"))[0];
		UserProperties userProperties = getProfileModule().getUserProperties(user.getId());
		Document favorites = (Document) userProperties.getProperty(ObjectKeys.USER_PROPERTY_FAVORITES);
		Favorites f = new Favorites(favorites);
		favorites = f.addCategory(category, "");
		getProfileModule().setUserProperty(user.getId(), ObjectKeys.USER_PROPERTY_FAVORITES, favorites);
	}
	
	private void ajaxSaveFavorites(ActionRequest request, ActionResponse response) {
		//Save the order of the favorites list
		String movedItemId = ((String[])formData.get("movedItemId"))[0];
		String favoritesList = ((String[])formData.get("favorites"))[0];
		UserProperties userProperties = getProfileModule().getUserProperties(user.getId());
		Document favorites = (Document) userProperties.getProperty(ObjectKeys.USER_PROPERTY_FAVORITES);
		Favorites f = new Favorites(favorites);
		favorites = f.saveOrder(movedItemId, favoritesList);
		getProfileModule().setUserProperty(user.getId(), ObjectKeys.USER_PROPERTY_FAVORITES, favorites);
	}
	
	private ModelAndView ajaxGetFavoritesTree(RenderRequest request, 
			RenderResponse response) throws Exception {
		UserProperties userProperties = getProfileModule().getUserProperties(user.getId());
		Document favorites = (Document) userProperties.getProperty(ObjectKeys.USER_PROPERTY_FAVORITES);
		Favorites f = new Favorites(favorites);
		Document favTree = f.getFavoritesTree();
		model.put(WebKeys.FAVORITES_TREE, favTree);
		Document favTreeDelete = f.getFavoritesTreeDelete();
		model.put(WebKeys.FAVORITES_TREE_DELETE, favTreeDelete);

		response.setContentType("text/xml");
		model.put(WebKeys.AJAX_STATUS, statusMap);
		return new ModelAndView("forum/favorites_tree", model);
	}
	
	private ModelAndView ajaxGetUnseenCounts(RenderRequest request, 
			RenderResponse response) throws Exception {
		List folderIds = new ArrayList();
		String[] forumList = new String[0];
		if (PortletRequestUtils.getStringParameter(request, "forumList") != null) {
			forumList = PortletRequestUtils.getStringParameter(request, "forumList").split(" ");
		}
		for (int i = 0; i < forumList.length; i++) {
			folderIds.add(new Long(forumList[i]));
		}
		unseenCounts = getFolderModule().getUnseenCounts(folderIds);

		response.setContentType("text/xml");
		
		model.put(WebKeys.LIST_UNSEEN_COUNTS, unseenCounts);
		model.put(WebKeys.AJAX_STATUS, statusMap);
		return new ModelAndView("forum/unseen_counts", model);

	}

	private ModelAndView ajaxReturn(RenderRequest request, 
			RenderResponse response) throws Exception {
		response.setContentType("text/xml");
		model.put(WebKeys.AJAX_STATUS, statusMap);
		return new ModelAndView("forum/ajax_return", model);
	}
	
	private ModelAndView ajaxSaveColumnPositions(RenderRequest request, 
			RenderResponse response) throws Exception {
		response.setContentType("text/xml");
		model.put(WebKeys.AJAX_STATUS, statusMap);
		return new ModelAndView("forum/save_column_positions_return", model);
	}
	
	private ModelAndView ajaxSaveEntryWidth(RenderRequest request, 
			RenderResponse response) throws Exception {
		//Save the user's selected entry width
		String entryWidth = ((String[])formData.get("entry_width"))[0];
		if (!entryWidth.equals("")) {
			//Save the entry width
		   	getProfileModule().setUserProperty(user.getId(), WebKeys.FOLDER_ENTRY_WIDTH, entryWidth);
		}
		response.setContentType("text/xml");
		model.put(WebKeys.AJAX_STATUS, statusMap);
		return new ModelAndView("forum/save_entry_width_return", model);
	}
	
	private ModelAndView ajaxSaveEntryHeight(RenderRequest request, 
			RenderResponse response) throws Exception {
		String entryHeight = ((String[])formData.get("entry_height"))[0];
		if (!entryHeight.equals("")) {
			//Save the entry width
			String binderId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
		   	getProfileModule().setUserProperty(user.getId(), Long.valueOf(binderId), WebKeys.FOLDER_ENTRY_HEIGHT, entryHeight);
		}
		response.setContentType("text/xml");
		model.put(WebKeys.AJAX_STATUS, statusMap);
		return new ModelAndView("forum/save_entry_height_return", model);
	}
	
	private ModelAndView ajaxUserListSearch(RenderRequest request, 
			RenderResponse response) throws Exception {
		String searchText = PortletRequestUtils.getStringParameter(request, "searchText", "");
		String searchType = PortletRequestUtils.getStringParameter(request, "searchType", "");
		String userGroupType = PortletRequestUtils.getStringParameter(request, "userGroupType", "");
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
	}
	
	private ModelAndView ajaxGetFilterData(RenderRequest request, 
			RenderResponse response) throws Exception {
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Binder binder = getBinderModule().getBinder(binderId);
		model.put(WebKeys.BINDER, binder);
			
		String filterTermNumber = ((String[])formData.get(WebKeys.FILTER_ENTRY_FILTER_TERM_NUMBER))[0];
		model.put(WebKeys.FILTER_ENTRY_FILTER_TERM_NUMBER, filterTermNumber);
		String filterTermNumberMax = ((String[])formData.get(WebKeys.FILTER_ENTRY_FILTER_TERM_NUMBER_MAX))[0];
		model.put(WebKeys.FILTER_ENTRY_FILTER_TERM_NUMBER_MAX, filterTermNumberMax);
		
		//Get the definition id (if present)
		if (op.equals(WebKeys.FORUM_OPERATION_GET_ENTRY_ELEMENTS) || 
				op.equals(WebKeys.FORUM_OPERATION_GET_ELEMENT_VALUES) || 
				op.equals(WebKeys.FORUM_OPERATION_GET_ELEMENT_VALUE_DATA)) {
			if (formData.containsKey(WebKeys.FILTER_ENTRY_DEF_ID+filterTermNumber)) {
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
		
    	model.put(WebKeys.AJAX_STATUS, statusMap);
		response.setContentType("text/xml");
		if (op.equals(WebKeys.FORUM_OPERATION_GET_FILTER_TYPE)) {
			model.put(WebKeys.FILTER_TYPE, op2);
			return new ModelAndView("binder/get_filter_type", model);
		} else if (op.equals(WebKeys.FORUM_OPERATION_GET_ENTRY_ELEMENTS)) {
			return new ModelAndView("binder/get_entry_elements", model);
		} else if (op.equals(WebKeys.FORUM_OPERATION_GET_ELEMENT_VALUES)) {
			return new ModelAndView("binder/get_element_value", model);
		} else {
			model.put(WebKeys.FILTER_VALUE_TYPE, "");
			if (formData.containsKey("elementValueDateType" + filterTermNumber)) {
				model.put(WebKeys.FILTER_VALUE_TYPE, 
						((String[])formData.get("elementValueDateType" + filterTermNumber))[0]);
			}
			return new ModelAndView("binder/get_element_value_data", model);
		}
	}
	
	private ModelAndView ajaxGetConditionData(RenderRequest request, 
			RenderResponse response) throws Exception {
		//Get the definition id (if present)
		if (op.equals(WebKeys.FORUM_OPERATION_GET_CONDITION_ENTRY_ELEMENTS)) {
			if (formData.containsKey(WebKeys.CONDITION_ENTRY_DEF_ID)) {
				String defId = ((String[])formData.get(WebKeys.CONDITION_ENTRY_DEF_ID))[0];
				model.put(WebKeys.CONDITION_ENTRY_DEF_ID, defId);
				Map elementData = getDefinitionModule().getEntryDefinitionElements(defId);
				model.put(WebKeys.ENTRY_DEFINTION_ELEMENT_DATA, elementData);
			}
		} else if (op.equals(WebKeys.FORUM_OPERATION_GET_CONDITION_ENTRY_VALUE_LIST)) {
			if (formData.containsKey(WebKeys.CONDITION_ENTRY_DEF_ID)) {
				String defId = ((String[])formData.get(WebKeys.CONDITION_ENTRY_DEF_ID))[0];
				model.put(WebKeys.CONDITION_ENTRY_DEF_ID, defId);
			}
		}
		
		model.put(WebKeys.AJAX_STATUS, statusMap);
		response.setContentType("text/xml");
		if (op.equals(WebKeys.FORUM_OPERATION_GET_CONDITION_ENTRY_ELEMENTS)) {
			return new ModelAndView("definition_builder/get_condition_entry_element", model);
		} else {
			return new ModelAndView("definition_builder/get_condition_entry_element_value", model);
		}
	}
	
	private ModelAndView ajaxGetWorkspaceTree(RenderRequest request, 
			RenderResponse response) throws Exception {
		if (formData.containsKey("binderId")) {
			model.put("ss_tree_treeName", ((String[])formData.get("treeName"))[0]);
			model.put("ss_tree_binderId", ((String[])formData.get("binderId"))[0]);
			Long binderId = PortletRequestUtils.getRequiredLongParameter(request, "binderId");
			model.put("ss_tree_topId", op2);
			Binder binder = getBinderModule().getBinder(binderId);
			Long topId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_OPERATION2);
			Document tree;
			if (binder instanceof Workspace) {
				if ((topId != null) && (binder.getParentBinder() != null)) {
					//top must be a workspace
					tree = getWorkspaceModule().getDomWorkspaceTree(topId, binder.getId(), new WsTreeBuilder((Workspace)binder, true, getBinderModule()));
				} else {
					tree = getWorkspaceModule().getDomWorkspaceTree(binder.getId(), new WsTreeBuilder((Workspace)binder, true, getBinderModule()),1);
				}
			} else {
				Folder topFolder = ((Folder)binder).getTopFolder();
				if (topFolder == null) topFolder = (Folder)binder;
				
				//must be a folder
				if (topId == null) {
					tree = getFolderModule().getDomFolderTree(topFolder.getId(), new TreeBuilder());
				} else {
					Binder top = getBinderModule().getBinder(topId);
					if (top instanceof Folder)
						//just load the whole thing
						tree = getFolderModule().getDomFolderTree(top.getId(), new TreeBuilder());
					else {
						tree = getWorkspaceModule().getDomWorkspaceTree(topId, topFolder.getParentBinder().getId(), new WsTreeBuilder((Workspace)top, false, getBinderModule()));
						Element topBinderElement = (Element)tree.selectSingleNode("//" + DomTreeBuilder.NODE_CHILD + "[@id='" + topFolder.getId() + "']");
						Document folderTree = getFolderModule().getDomFolderTree(topFolder.getId(), new TreeBuilder());
						topBinderElement.setContent(folderTree.getRootElement().content());
					}
						
				}
			}
			model.put(WebKeys.WORKSPACE_DOM_TREE, tree);
		}
		response.setContentType("text/xml");
		model.put(WebKeys.AJAX_STATUS, statusMap);
		return new ModelAndView("tag_jsps/tree/get_tree_div", model);
	}
	
}
