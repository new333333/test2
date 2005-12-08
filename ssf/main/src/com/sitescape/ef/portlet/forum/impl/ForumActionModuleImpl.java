
package com.sitescape.ef.portlet.forum.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.HistoryMap;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.UserPerFolderPK;
import com.sitescape.ef.module.admin.AdminModule;
import com.sitescape.ef.portlet.forum.HistoryCache;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.module.mail.MailModule;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.module.workspace.WorkspaceModule;
import com.sitescape.ef.util.Toolbar;
//import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.portlet.support.ModelUtil;
import com.sitescape.ef.portlet.forum.ActionUtil;
import com.sitescape.ef.portlet.forum.ForumActionModule;
import com.sitescape.ef.domain.DefinitionInvalidException;

import javax.portlet.RenderRequest;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author Janet McCann
 *
 */
public class ForumActionModuleImpl implements ForumActionModule {

	protected WorkspaceModule workspaceModule;;
	protected ProfileModule profileModule;
	protected AdminModule adminModule;
	protected FolderModule folderModule;
	protected DefinitionModule definitionModule;
	protected MailModule mailModule;
	    
	/**
	 * @param adminModule The adminModule to set.
	 */
	public void setAdminModule(AdminModule adminModule) {
		this.adminModule = adminModule;
	}
	/**	
	 * @param definitionModule The definitionModule to set.
	 */
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}

	/**
	 * @param folderModule The folderModule to set.
	 */
	public void setFolderModule(FolderModule folderModule) {
		this.folderModule = folderModule;
	}

	/**
	 * @param mailModule The mailModule to set.
	 */
	public void setMailModule(MailModule mailModule) {
		this.mailModule = mailModule;
	}
	/**
	 * @param profileModule The profileModule to set.
	 */
	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}
		
	/**
	 * @return Returns the adminModule.
	 */
	protected AdminModule getAdminModule() {
		return adminModule;
	}
	/**
	 * @return Returns the definitionModule.
	 */
	protected DefinitionModule getDefinitionModule() {
		return definitionModule;
	}

	/**
	 * @return Returns the folderModule.
	 */
	protected FolderModule getFolderModule() {
		return folderModule;
	}
	/**
	 * @return Returns the mailModule.
	 */
	protected MailModule getMailModule() {
		return mailModule;
	}
	/**
	 * @return Returns the profileModule.
	 */
	protected ProfileModule getProfileModule() {
		return profileModule;
	}

	/**
	 * @return Returns the workspaceModule.
	 */
	protected WorkspaceModule getWorkspaceModule() {
		return workspaceModule;
	}
	/**
	 * @param workspaceModule The workspaceModule to set.
	 */
	public void setWorkspaceModule(WorkspaceModule workspaceModule) {
		this.workspaceModule = workspaceModule;
	}
	protected void getDefinitions(Folder folder, Map model) {
		List folderViewDefs = folder.getForumViewDefs();
		if (!folderViewDefs.isEmpty()) {
			Definition defaultForumDefinition = (Definition)folderViewDefs.get(0);
			model.put("ss_forum_forum_definition_default", defaultForumDefinition);
			model.put("ss_forum_forum_definitionId_default", defaultForumDefinition.getId());
			Document forumViewDoc = defaultForumDefinition.getDefinition();
			if (forumViewDoc != null) {
				Element forumViewElement ;
				forumViewElement = forumViewDoc.getRootElement();
				forumViewElement = (Element) forumViewElement.selectSingleNode("//item[@name='forumView']");
				model.put("ss_forum_config", forumViewElement);
			} else {
				model.put("ss_forum_config", null);
			}
			
		} else {
			model.put("ss_forum_forum_definition_default", null);
			model.put("ss_forum_forum_definitionId_default", "");
			model.put("ss_forum_config", null);
		
		}
		Map defaultEntryDefinitions = ActionUtil.getEntryDefsAsMap(folder);
		model.put("ss_forum_entry_definitions_default", defaultEntryDefinitions);
		model.put("ss_forum_configJspStyle", "view");
		model.put("ss_forum_config_definition", getDefinitionModule().getDefinitionConfig());
	}
	public void getDefinitions(Map model) {
		List defs = getDefinitionModule().getDefinitions();
		model.put("ss_definition_public", defs);
		Iterator itPublicDefinitions = defs.listIterator();
		Map publicEntryDefinitions = new HashMap();
		Map publicForumDefinitions = new HashMap();
		while (itPublicDefinitions.hasNext()) {
			Definition def = (Definition) itPublicDefinitions.next();
			if (def.getType() == Definition.COMMAND) {
				publicEntryDefinitions.put(def.getId(), def);
			} else if (def.getType() == Definition.FORUM_VIEW) {
				publicForumDefinitions.put(def.getId(), def);
			}
		}
		model.put("ss_forum_public_entry_definitions", publicEntryDefinitions);
		model.put("ss_forum_public_forum_definitions", publicForumDefinitions);

	}
	public Map getDeleteEntry(Map formData, RenderRequest req, Long folderId)  {
		Map model = new HashMap();
		String entryId = ActionUtil.getStringValue(formData, ObjectKeys.FORUM_URL_ENTRY_ID);
		FolderEntry entry = getFolderModule().getEntry(folderId, Long.valueOf(entryId));
		model.put("ss_forum_entry", entry);
		model.put("ss_forum_entryId", entry.getId());
		model.put("ss_forum_forum", entry.getParentFolder());
		ModelUtil.processModel(req, model);
		return model;
	}
	public Map getModifyEntry(Map formData, RenderRequest req, Long folderId) {
		Map model = new HashMap();
		FolderEntry entry=null;
		String entryId = ActionUtil.getStringValue(formData, ObjectKeys.FORUM_URL_ENTRY_ID);
		if (!entryId.equals("")) entry  = getFolderModule().getEntry(folderId, Long.valueOf(entryId));
		
		model.put("ss_forum_entry", entry);
		model.put("ss_forum_forum", entry.getParentFolder());
		model.put("ss_forum_configJspStyle", "form");
		model.put("ss_forum_config_definition", getDefinitionModule().getDefinitionConfig());
		model.put("ss_forum_entry_definition", entry.getEntryDef());

		ModelUtil.processModel(req, model);
		return model;
		
	}
	private HistoryMap getHistory(RenderRequest req, Long folderId) {
		HistoryMap history;
		//check if cached first
		HistoryCache cache = (HistoryCache)req.getAttribute("ss_folder_historyCache");
		if (cache == null) {
			history = getProfileModule().getUserHistory(null, folderId);
		} else {
			UserPerFolderPK key = new UserPerFolderPK(RequestContextHolder.getRequestContext().getUser().getId(), folderId);
			if (!key.equals(cache.getId())) {
				history = getProfileModule().getUserHistory(null, folderId);
			} else {
				history = cache.getHistory();
			}
		}
		return history; 
	}
	public Map getShowEntry(Map formData, RenderRequest req, Long folderId)  {
		Map model = new HashMap();
		String entryId = ActionUtil.getStringValue(formData, ObjectKeys.FORUM_URL_ENTRY_ID);
		String op = ActionUtil.getStringValue(formData, ObjectKeys.FORUM_URL_OPERATION);
		Folder folder = null;
		FolderEntry entry = null;
		Map folderEntries = null;
		//load if not already cached
		HistoryMap history = getHistory(req, folderId);
		model.put("ss_folder_historymap", history);
		if (op.equals(ObjectKeys.FORUM_OPERATION_VIEW_ENTRY)) {
			if (!entryId.equals("")) folderEntries  = getFolderModule().getEntryTree(folderId, Long.valueOf(entryId));
		} else if (op.equals(ObjectKeys.FORUM_OPERATION_VIEW_ENTRY_HISTORY_NEXT)) {
			folder = getFolderModule().getFolder(folderId);
			Long currentEntryId = null;
			if (formData.containsKey(ObjectKeys.SESSION_LAST_ENTRY_VIEWED)) {
				currentEntryId = (Long)formData.get(ObjectKeys.SESSION_LAST_ENTRY_VIEWED);
			}
			if (formData.containsKey(ObjectKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED) && 
					(Long)formData.get(ObjectKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED) != null) {
				currentEntryId = (Long)formData.get(ObjectKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED);
			}
			if (currentEntryId == null) {
				Long nextEntryId = history.getNextHistoryEntry();
				if (nextEntryId != null) {
					entryId = nextEntryId.toString();
				} else {
					entryId = "";
				}
			} else {
				Long nextEntryId = history.getNextHistoryEntry(currentEntryId);
				if (nextEntryId != null) {
					entryId = nextEntryId.toString();
				} else {
					entryId = "";
				}
			}
			if (!entryId.equals("")) folderEntries  = getFolderModule().getEntryTree(folderId, Long.valueOf(entryId));
	
		} else if (op.equals(ObjectKeys.FORUM_OPERATION_VIEW_ENTRY_HISTORY_PREVIOUS)) {
			folder = getFolderModule().getFolder(folderId);
			Long currentEntryId = null;
			if (formData.containsKey(ObjectKeys.SESSION_LAST_ENTRY_VIEWED)) {
				currentEntryId = (Long)formData.get(ObjectKeys.SESSION_LAST_ENTRY_VIEWED);
			}
			if (formData.containsKey(ObjectKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED)) {
				currentEntryId = (Long)formData.get(ObjectKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED);
			}
			if (currentEntryId != null) {
				Long previousEntryId = history.getPreviousHistoryEntry(currentEntryId);
				if (previousEntryId != null) {
					entryId = previousEntryId.toString();
				} else {
					entryId = "";
				}
			} else {
				entryId = "";
			}
			if (!entryId.equals("")) folderEntries  = getFolderModule().getEntryTree(folderId, Long.valueOf(entryId));
	
		} else if (op.equals(ObjectKeys.FORUM_OPERATION_VIEW_ENTRY_NEXT)) {
			Long currentEntryId = null;
			if (formData.containsKey(ObjectKeys.SESSION_LAST_ENTRY_VIEWED)) {
				currentEntryId = (Long)formData.get(ObjectKeys.SESSION_LAST_ENTRY_VIEWED);
			}
			if (currentEntryId != null) {
				entryId = currentEntryId.toString();
			}
			if (!entryId.equals("")) folderEntries  = getFolderModule().getEntryTree(folderId, Long.valueOf(entryId), FolderModule.NEXT_ENTRY);
	
		} else if (op.equals(ObjectKeys.FORUM_OPERATION_VIEW_ENTRY_PREVIOUS)) {
			Long currentEntryId = null;
			if (formData.containsKey(ObjectKeys.SESSION_LAST_ENTRY_VIEWED)) {
				currentEntryId = (Long)formData.get(ObjectKeys.SESSION_LAST_ENTRY_VIEWED);
			}
			if (currentEntryId != null) {
				//entryId = seenMap.getPreviousHistoryEntry(currentEntryId).toString();
				entryId = "";
			}
			if (!entryId.equals("")) folderEntries  = getFolderModule().getEntryTree(folderId, Long.valueOf(entryId), FolderModule.PREVIOUS_ENTRY);
		}
		if (entryId.equals("")) {
			folder = getFolderModule().getFolder(folderId);
		}
		if (folderEntries != null) {
			entry = (FolderEntry)folderEntries.get(ObjectKeys.FOLDER_ENTRY);
			folder = entry.getParentFolder();
			model.put("ss_forum_entry_descendants", folderEntries.get(ObjectKeys.FOLDER_ENTRY_DESCENDANTS));
			model.put("ss_forum_entry_ancestors", folderEntries.get(ObjectKeys.FOLDER_ENTRY_ANCESTORS));
		}
		model.put("ss_folder_seenmap", getProfileModule().getUserSeenMap(null, folder.getId()));
		model.put("ss_forum_entry", entry);
		model.put("ss_forum_entryId", entryId);
		model.put("ss_definition_folder_entry", entry);
		model.put("ss_forum_forum", folder);
		model.put("ss_forum_config_definition", getDefinitionModule().getDefinitionConfig());
		model.put("ss_forum_configJspStyle", "view");
		model.put("ss_user_properties", getProfileModule().getUserProperties(null).getProperties());
		if (entry == null) {
			ModelUtil.processModel(req,model);
			return model;
		}
		Element entryViewElement = null;
		Document entryView = null;
		String entryDefId = "";
		Definition entryDefinition = entry.getEntryDef();
		if (entryDefinition != null) {
			entryView = entryDefinition.getDefinition();
			entryDefId = entryDefinition.getId();
		}
		if (entryView == null) {
			//There is no definition for displaying this entry; use the default viewer
			entryView = getDefaultEntryView(entry, req);
		}
		entryViewElement = entryView.getRootElement();
		entryViewElement = (Element) entryViewElement.selectSingleNode("//item[@name='entryView']");
		model.put("ss_forum_config", entryViewElement);

	    //Build the toolbar array
		Toolbar toolbar = new Toolbar();
	    String forumId = folderId.toString();
	    //The "Reply" menu
		String replyStyle = (String) entryView.getRootElement().attributeValue("replyStyle", "");
		if (!replyStyle.equals("")) {
			Map urlParams1 = new HashMap();
	    	urlParams1.put(ObjectKeys.FORUM_URL_STRUTS_ACTION, "/forum/add_entry");
	    	urlParams1.put(ObjectKeys.FORUM_URL_FORUM_ID, forumId);
	    	urlParams1.put(ObjectKeys.FORUM_URL_ENTRY_TYPE, replyStyle);
	    	urlParams1.put(ObjectKeys.FORUM_URL_ENTRY_ID, entryId);
	    	urlParams1.put(ObjectKeys.FORUM_URL_OPERATION, ObjectKeys.FORUM_OPERATION_ADD_REPLY);
			toolbar.addToolbarMenu("1_reply", NLT.get("toolbar_reply"), urlParams1);
		}
	    
	    //The "Modify" menu
		if (entry.getEntryDef() != null) {
			Map urlParams2 = new HashMap();
			urlParams2.put(ObjectKeys.FORUM_URL_STRUTS_ACTION, "/forum/modify_entry");
			urlParams2.put(ObjectKeys.FORUM_URL_FORUM_ID, forumId);
			urlParams2.put(ObjectKeys.FORUM_URL_ENTRY_TYPE, entryDefId);
			urlParams2.put(ObjectKeys.FORUM_URL_ENTRY_ID, entryId);
			urlParams2.put(ObjectKeys.FORUM_URL_OPERATION, ObjectKeys.FORUM_OPERATION_MODIFY_ENTRY);
			toolbar.addToolbarMenu("2_modify", NLT.get("toolbar_modify"), urlParams2);
		}
	    
	    //The "Delete" menu
		Map urlParams3 = new HashMap();
		urlParams3.put(ObjectKeys.FORUM_URL_STRUTS_ACTION, "/forum/delete_entry");
		urlParams3.put(ObjectKeys.FORUM_URL_FORUM_ID, forumId);
		urlParams3.put(ObjectKeys.FORUM_URL_ENTRY_TYPE, entryDefId);
		urlParams3.put(ObjectKeys.FORUM_URL_ENTRY_ID, entryId);
		urlParams3.put(ObjectKeys.FORUM_URL_OPERATION, ObjectKeys.FORUM_OPERATION_DELETE_ENTRY);
		toolbar.addToolbarMenu("3_delete", NLT.get("toolbar_delete"), urlParams3);
	    
		model.put("ss_forum_entry_toolbar", toolbar.getToolbar());
		ModelUtil.processModel(req, model);
		return model;
	}
	public Map getShowFolder(Map formData, RenderRequest req, Long folderId)  {
		Map folderEntries;
		Map model = new HashMap();
		String forumId = folderId.toString();
		folderEntries = getFolderModule().getFolderEntries(folderId);
		Folder folder = (Folder)folderEntries.get(ObjectKeys.FOLDER);
	   	User user = RequestContextHolder.getRequestContext().getUser();
		//Build the beans depending on the operation being done
		model.put("ss_forum_forum", folder);
		HistoryMap history = getHistory(req, folderId);
		model.put("ss_folder_historymap", history);
		model.put("ss_folder_folders", folder.getFolders());
		Folder topFolder = folder.getTopFolder();
		if (topFolder == null) {
			model.put("ss_folder_tree", getFolderModule().getDomFolderTree(folderId));
		} else {
			model.put("ss_folder_tree", getFolderModule().getDomFolderTree(topFolder.getId()));			
		}
		model.put("ss_folder_entries", folderEntries.get(ObjectKeys.FOLDER_ENTRIES));
		model.put("ss_user_properties", getProfileModule().getUserProperties(user.getId()).getProperties());
		model.put("ss_folder_seenmap",getProfileModule().getUserSeenMap(user.getId(), folder.getId()));
		getDefinitions(folder, model);
		req.setAttribute(ObjectKeys.FORUM_URL_FORUM_ID,forumId);
	    //Build the toolbar array
		Toolbar toolbar = new Toolbar();
	    	
	    //The "Add" menu
		List defaultEntryDefinitions = folder.getEntryDefs();
	    if (!defaultEntryDefinitions.isEmpty()) {
	    	toolbar.addToolbarMenu("1_add", NLT.get("toolbar_add"));
	       	for (int i=0; i<defaultEntryDefinitions.size(); ++i) {
	       		Definition def = (Definition) defaultEntryDefinitions.get(i);
	       		Map urlParams = new HashMap();
	       		urlParams.put(ObjectKeys.FORUM_URL_STRUTS_ACTION, "/forum/add_entry");
	       		urlParams.put(ObjectKeys.FORUM_URL_FORUM_ID, forumId);
	       		urlParams.put(ObjectKeys.FORUM_URL_ENTRY_TYPE, def.getId());
	       		urlParams.put(ObjectKeys.FORUM_URL_OPERATION, ObjectKeys.FORUM_OPERATION_ADD_ENTRY);
		        toolbar.addToolbarMenuItem("1_add", "entries", def.getTitle(), urlParams);
	       	}
	    }
	    
	    //The "Administration" menu
	    Map urlParams = null;
	    toolbar.addToolbarMenu("2_administration", NLT.get("toolbar_administration"));
		//Configuration
		urlParams = new HashMap();
		urlParams.put(ObjectKeys.FORUM_URL_STRUTS_ACTION, "/forum/configure_forum");
		urlParams.put(ObjectKeys.FORUM_URL_FORUM_ID, forumId);
		urlParams.put(ObjectKeys.FORUM_URL_OPERATION, ObjectKeys.FORUM_OPERATION_CONFIGURE_FORUM);
		toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar_menu_configuration"), urlParams);
	    //Definition builder
		urlParams = new HashMap();
		urlParams.put(ObjectKeys.FORUM_URL_STRUTS_ACTION, "/forum/definition_builder");
		urlParams.put(ObjectKeys.FORUM_URL_FORUM_ID, forumId);
		urlParams.put(ObjectKeys.FORUM_URL_OPERATION, ObjectKeys.FORUM_OPERATION_DEFINITION_BUILDER);
        toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar_menu_definition_builder"), urlParams);
    	//The "Display styles" menu
    	urlParams = null;
    	toolbar.addToolbarMenu("3_display_styles", NLT.get("toolbar_display_styles"));
		//vertical
		urlParams = new HashMap();
		urlParams.put(ObjectKeys.FORUM_URL_STRUTS_ACTION, "/forum/view_forum");
		urlParams.put(ObjectKeys.FORUM_URL_FORUM_ID, forumId);
		urlParams.put(ObjectKeys.FORUM_URL_OPERATION, ObjectKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		urlParams.put(ObjectKeys.FORUM_URL_VALUE, ObjectKeys.USER_PROPERTY_DISPLAY_STYLE_HORIZONTAL);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar_menu_display_style_horizontal"), urlParams);
		//horizontal
		urlParams = new HashMap();
		urlParams.put(ObjectKeys.FORUM_URL_STRUTS_ACTION, "/forum/view_forum");
		urlParams.put(ObjectKeys.FORUM_URL_FORUM_ID, forumId);
		urlParams.put(ObjectKeys.FORUM_URL_OPERATION, ObjectKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		urlParams.put(ObjectKeys.FORUM_URL_VALUE, ObjectKeys.USER_PROPERTY_DISPLAY_STYLE_VERTICAL);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar_menu_display_style_vertical"), urlParams);
		//accessible
		urlParams = new HashMap();
		urlParams.put(ObjectKeys.FORUM_URL_STRUTS_ACTION, "/forum/view_forum");
		urlParams.put(ObjectKeys.FORUM_URL_FORUM_ID, forumId);
		urlParams.put(ObjectKeys.FORUM_URL_OPERATION, ObjectKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		urlParams.put(ObjectKeys.FORUM_URL_VALUE, ObjectKeys.USER_PROPERTY_DISPLAY_STYLE_ACCESSIBLE);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar_menu_display_style_accessible"), urlParams);
		//iframe
		urlParams = new HashMap();
		urlParams.put(ObjectKeys.FORUM_URL_STRUTS_ACTION, "/forum/view_forum");
		urlParams.put(ObjectKeys.FORUM_URL_FORUM_ID, forumId);
		urlParams.put(ObjectKeys.FORUM_URL_OPERATION, ObjectKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		urlParams.put(ObjectKeys.FORUM_URL_VALUE, ObjectKeys.USER_PROPERTY_DISPLAY_STYLE_IFRAME);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar_menu_display_style_iframe"), urlParams);
		model.put("ss_forum_toolbar", toolbar.getToolbar());
		ModelUtil.processModel(req,model);
		return model;
	}
	public Map getDefinitionBuilder(Map formData, RenderRequest req, String currentId) {
		Map model = new HashMap();
		
		model.put("ss_forum_configJspStyle", "view");
		model.put("ss_forum_config_definition", getDefinitionModule().getDefinitionConfig());
			
		getDefinitions(model);
		if (!currentId.equals("")) {
			model.put("ss_forum_entry_definition", getDefinitionModule().getDefinition(currentId));
		}
		ModelUtil.processModel(req,model);
		return model;
	}
	public Map getConfigureForum(Map formData, RenderRequest req, Long folderId) {
		Map model = new HashMap();
		User user = RequestContextHolder.getRequestContext().getUser();
		Folder folder = getFolderModule().getFolder(folderId);
		
		model.put("ss_forum_forum", folder);
		model.put("ss_forum_configJspStyle", "view");
		model.put("ss_user_properties", getProfileModule().getUserProperties(user.getId()));
			
		getDefinitions(model);
		getDefinitions(folder, model);
		ModelUtil.processModel(req,model);
		return model;
	}
	public Map getAddEntry(Map formData, RenderRequest req, Long folderId) {
		Map model = new HashMap();
		Folder folder = getFolderModule().getFolder(folderId);
		//Adding an entry; get the specific definition
		Map folderEntryDefs = ActionUtil.getEntryDefsAsMap(folder);
		String entryType = ActionUtil.getStringValue(formData, ObjectKeys.FORUM_URL_ENTRY_TYPE);
		model.put("ss_forum_forum", folder);
		model.put("ss_forum_entry_definitions_default", folderEntryDefs);
		model.put("ss_forum_configJspStyle", "form");
		model.put("ss_forum_config_definition", getDefinitionModule().getDefinitionConfig());
		//Make sure the requested definition is legal
		if (folderEntryDefs.containsKey(entryType)) {
			model.put("ss_forum_entry_definition", getDefinitionModule().getDefinition(entryType));
		} else {
			model.put("ss_forum_entry_definition", null);
		}
		ModelUtil.processModel(req,model);
		return model;
		
	}
    public Map getAddReply(Map formData, RenderRequest req, Long folderId) {
    	String entryId = ActionUtil.getStringValue(formData, ObjectKeys.FORUM_URL_ENTRY_ID);
    	req.setAttribute(ObjectKeys.FORUM_URL_ENTRY_ID,entryId);
    	Map model = new HashMap();
    	FolderEntry entry = getFolderModule().getEntry(folderId, Long.valueOf(entryId));
    	model.put("ss_forum_parent_entry", entry);
    	model.put("ss_definition_folder_entry", entry);
    	Folder folder = entry.getParentFolder();
    	model.put("ss_forum_forum", folder); 
		
    	//Get the legal reply types from the parent entry definition
		Document entryView = null;
		Definition entryDefinition = entry.getEntryDef();
		if (entryDefinition != null) {
			entryView = entryDefinition.getDefinition();
		}
		String replyStyle = "";
		if (entryView != null) {
			//See if there is a reply style for this entry definition
			replyStyle = (String) entryView.getRootElement().attributeValue("replyStyle", "");
		}
   	
    	//Adding an entry; get the specific definition
		Map folderEntryDefs = ActionUtil.getEntryDefsAsMap(folder);
    	String entryType = ActionUtil.getStringValue(formData, ObjectKeys.FORUM_URL_ENTRY_TYPE);
    	model.put("ss_forum_entry_definitions_default", folderEntryDefs);
    	model.put("ss_forum_configJspStyle", "form");
    	model.put("ss_forum_config_definition", getDefinitionModule().getDefinitionConfig());
		//Make sure the requested definition is legal
		if (replyStyle.equals(entryType)) {
			model.put("ss_forum_entry_definition", getDefinitionModule().getDefinition(entryType));
		} else {
			model.put("ss_forum_entry_definition", null);
		}
    	ModelUtil.processModel(req,model);
    	return model;
    }
    
	//Routine to build a definition file on the fly for viewing entries with no definition
	private Document getDefaultEntryView(FolderEntry entry, RenderRequest req) {
		//Create an empty entry definition
		Document def = getDefinitionModule().getDefaultDefinition("ss_default_entry_view","__definition_default_entry_view", Definition.COMMAND);
		
		//Add the "default viewer" item
		Element entryView = (Element) def.getRootElement().selectSingleNode("//item[@name='entryView']");
		if (entryView != null) {
			String itemId = entryView.attributeValue("id", "");
			Map formData = new HashMap();
			try {
				Element newItem = getDefinitionModule().addItemToDefinitionDocument("default", def, itemId, "defaultEntryView", formData);
			}
			catch (DefinitionInvalidException e) {
				//An error occurred while processing the operation; pass the error message back to the jsp
				//SessionErrors.add(req, e.getClass().getName(),e.getMessage());
			}
		}
		return def;
	}
public static class NLT {
	public static String get(String v) {return v;}
	
}
}
