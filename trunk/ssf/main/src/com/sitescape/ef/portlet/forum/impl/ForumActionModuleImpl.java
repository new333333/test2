
package com.sitescape.ef.portlet.forum.impl;

import java.util.HashMap;
import java.util.Date;
import javax.portlet.PortletURL;
import javax.portlet.PortletSession;
import javax.portlet.RenderResponse;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.HistoryMap;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Event;
import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.UserPerFolderPK;
import com.sitescape.ef.module.admin.AdminModule;
import com.sitescape.ef.portlet.forum.HistoryCache;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.module.mail.MailModule;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.module.workspace.WorkspaceModule;

import com.sitescape.ef.portlet.forum.ActionUtil;
import com.sitescape.ef.portlet.forum.ForumActionModule;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.util.Toolbar;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.WebHelper;
import com.sitescape.ef.domain.DefinitionInvalidException;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author Janet McCann
 *
 */
public class ForumActionModuleImpl implements ForumActionModule,DomTreeBuilder {

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
			model.put(WebKeys.DEFAULT_FOLDER_DEFINITION, defaultForumDefinition);
			model.put(WebKeys.DEFAULT_FOLDER_DEFINITION_ID, defaultForumDefinition.getId());
			Document forumViewDoc = defaultForumDefinition.getDefinition();
			if (forumViewDoc != null) {
				Element forumViewElement ;
				forumViewElement = forumViewDoc.getRootElement();
				forumViewElement = (Element) forumViewElement.selectSingleNode("//item[@name='forumView']");
				model.put(WebKeys.CONFIG_ELEMENT, forumViewElement);
			} else {
				model.put(WebKeys.CONFIG_ELEMENT, null);
			}
			
		} else {
			model.put(WebKeys.DEFAULT_FOLDER_DEFINITION, null);
			model.put(WebKeys.DEFAULT_FOLDER_DEFINITION_ID, "");
			model.put(WebKeys.CONFIG_ELEMENT, null);
		
		}
		Map defaultEntryDefinitions = ActionUtil.getEntryDefsAsMap(folder);
		model.put(WebKeys.ENTRY_DEFINTION_MAP, defaultEntryDefinitions);
		model.put(WebKeys.CONFIG_JSP_STYLE, "view");
		model.put(WebKeys.CONFIG_DEFINITION, getDefinitionModule().getDefinitionConfig());
	}
	public void getDefinitions(Map model) {
		List defs = getDefinitionModule().getDefinitions();
		model.put(WebKeys.PUBLIC_DEFINITIONS, defs);
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
		model.put(WebKeys.PUBLIC_ENTRY_DEFINITIONS, publicEntryDefinitions);
		model.put(WebKeys.PUBLIC_FOLDER_DEFINITIONS, publicForumDefinitions);

	}
	
	/* 
	 * getEvents ripples through all the entries in the current entry list, finds their
	 * associated events, checks each event against the session's current calendar view mode
	 * and current selected date, and populates the bean with a list of dates that fall in range.
	 */
	public void getEvents(ArrayList entrylist, Map model, RenderRequest req) {
		Iterator entryIterator = entrylist.listIterator();
		PortletSession ps = WebHelper.getRequiredPortletSession(req);
		if (ps.getAttribute(WebKeys.CALENDAR_VIEWMODE) == null) {
			ps.setAttribute(WebKeys.CALENDAR_VIEWMODE, WebKeys.CALENDAR_VIEW_WEEK);			
		}
		Date currentDate = (Date) ps.getAttribute(WebKeys.CALENDAR_CURRENT_DATE);
		if (currentDate == null) {
			ps.setAttribute(WebKeys.CALENDAR_CURRENT_DATE, new Date());	
			currentDate = new Date();
		} 
		ArrayList dateList = new ArrayList();
		while (entryIterator.hasNext()) {
			Entry e = (Entry) entryIterator.next();
			Map customAttrs = e.getCustomAttributes();
			Set keyset = customAttrs.keySet();
			Iterator attIt = keyset.iterator();
			while (attIt.hasNext()) {
				CustomAttribute att = (CustomAttribute) customAttrs.get(attIt.next());
				if (att.getValueType() == CustomAttribute.EVENT) {
					Event ev = (Event) att.getValue();
					dateList.add(ev.getDtStart().getTime());
				}
			}
		}
		model.put(WebKeys.CALENDAR_EVENTDATES, dateList);
	}
	
	/**
	 * Fill in the model values for a definition.  Return false if definition isn't
	 * complete, true otherwise
	 * @param currentDef
	 * @param model
	 * @param node
	 * @return
	 */
	private boolean getDefinition(Definition currentDef, Map model, String node) {
		model.put(WebKeys.ENTRY_DEFINITION, currentDef);
		model.put(WebKeys.CONFIG_DEFINITION, getDefinitionModule().getDefinitionConfig());
		if (currentDef == null) {
			model.put(WebKeys.CONFIG_ELEMENT, null);
			return false;
		}
		Document configDoc = currentDef.getDefinition();
		if (configDoc == null) { 
			model.put(WebKeys.CONFIG_ELEMENT, null);
			return false;
		} else {
			Element configRoot = configDoc.getRootElement();
			if (configRoot == null) {
				model.put(WebKeys.CONFIG_ELEMENT, null);
				return false;
			} else {
				Element configEle = (Element) configRoot.selectSingleNode(node);
				model.put(WebKeys.CONFIG_ELEMENT, configEle);
				if (configEle == null) return false;
			}
		}
		return true;
		
	}
	//Routine to build a definition file on the fly for viewing entries with no definition
	private void getDefaultEntryView(Map model) {
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
		model.put(WebKeys.CONFIG_ELEMENT, entryView);
	}
	private HistoryMap getHistory(RenderRequest req, Long folderId) {
		HistoryCache cache = (HistoryCache)req.getAttribute(WebKeys.HISTORY_CACHE);
		return getHistory(cache, folderId);
	}
	private HistoryMap getHistory(HttpServletRequest req, Long folderId) {
		HistoryCache cache = (HistoryCache)req.getAttribute(WebKeys.HISTORY_CACHE);
		return getHistory(cache, folderId);
	}
	private HistoryMap getHistory(HistoryCache cache, Long folderId) {
		HistoryMap history;
		//check if cached first
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
	protected void buildEntryToolbar(RenderResponse response, Map model, String folderId, String entryId) {
		
		Element entryViewElement = (Element)model.get(WebKeys.CONFIG_ELEMENT);
		Document entryView = entryViewElement.getDocument();
		Definition def = (Definition)model.get(WebKeys.ENTRY_DEFINITION);
		String entryDefId="";
		if (def != null)
			entryDefId= def.getId().toString();
	    //Build the toolbar array
		Toolbar toolbar = new Toolbar();
	    //The "Reply" menu
		String replyStyle = (String) entryView.getRootElement().attributeValue("replyStyle", "");
		PortletURL url;
		if (!replyStyle.equals("")) {
			/**
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_ADD_REPLY);
	    	url.setParameter(WebKeys.FORUM_URL_FORUM_ID, folderId);
	    	url.setParameter(WebKeys.FORUM_URL_ENTRY_TYPE, replyStyle);
	    	url.setParameter(WebKeys.FORUM_URL_ENTRY_ID, entryId);
			toolbar.addToolbarMenu("1_reply", NLT.get("toolbar.reply"), url);
			*/
			Map params = new HashMap();
			params.put(WebKeys.ACTION, WebKeys.FORUM_ACTION_ADD_REPLY);
			params.put(WebKeys.FORUM_URL_FORUM_ID, folderId);
			params.put(WebKeys.FORUM_URL_ENTRY_TYPE, replyStyle);
			params.put(WebKeys.FORUM_URL_ENTRY_ID, entryId);
			Map qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			toolbar.addToolbarMenu("1_reply", NLT.get("toolbar.reply"), params, qualifiers);
		}
	    
	    //The "Modify" menu
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_MODIFY_ENTRY);
		url.setParameter(WebKeys.FORUM_URL_FORUM_ID, folderId);
		url.setParameter(WebKeys.FORUM_URL_ENTRY_TYPE, entryDefId);
		url.setParameter(WebKeys.FORUM_URL_ENTRY_ID, entryId);
		toolbar.addToolbarMenu("2_modify", NLT.get("toolbar.modify"), url);
		
	    
	    //The "Delete" menu
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_DELETE_ENTRY);
		url.setParameter(WebKeys.FORUM_URL_FORUM_ID, folderId);
		url.setParameter(WebKeys.FORUM_URL_ENTRY_TYPE, entryDefId);
		url.setParameter(WebKeys.FORUM_URL_ENTRY_ID, entryId); 
		toolbar.addToolbarMenu("3_delete", NLT.get("toolbar.delete"), url);
	    
		model.put(WebKeys.FOLDER_ENTRY_TOOLBAR, toolbar.getToolbar());
		
	}
	protected void buildFolderToolbar(RenderResponse response, Map model, String folderId) {
		//Build the toolbar array
		Toolbar toolbar = new Toolbar();
		String forumId = folderId.toString();
		//	The "Add" menu
		Folder folder = (Folder)model.get(WebKeys.FOLDER);
		List defaultEntryDefinitions = folder.getEntryDefs();
		PortletURL url;
		if (!defaultEntryDefinitions.isEmpty()) {
			toolbar.addToolbarMenu("1_add", NLT.get("toolbar.add"));
			for (int i=0; i<defaultEntryDefinitions.size(); ++i) {
				Definition def = (Definition) defaultEntryDefinitions.get(i);
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_ADD_ENTRY);
				url.setParameter(WebKeys.FORUM_URL_FORUM_ID, forumId);
				url.setParameter(WebKeys.FORUM_URL_ENTRY_TYPE, def.getId());
				toolbar.addToolbarMenuItem("1_add", "entries", def.getTitle(), url);
			}
		}
    
		//The "Administration" menu
		toolbar.addToolbarMenu("2_administration", NLT.get("toolbar.administration"));
		//Configuration
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_CONFIGURE_FORUM);
		url.setParameter(WebKeys.FORUM_URL_FORUM_ID, forumId);
		toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.configuration"), url);
		//Definition builder
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_DEFINITION_BUILDER);
		url.setParameter(WebKeys.FORUM_URL_FORUM_ID, forumId);
		toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.definition_builder"), url);
		
		//	The "Display styles" menu
		toolbar.addToolbarMenu("3_display_styles", NLT.get("toolbar.display_styles"));
		/**
		//horizontal
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_VIEW_FORUM);
		url.setParameter(WebKeys.FORUM_URL_OPERATION, WebKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.FORUM_URL_FORUM_ID, forumId);
		url.setParameter(WebKeys.FORUM_URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_HORIZONTAL);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_horizontal"), url);
		*/
		//vertical
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_VIEW_FORUM);
		url.setParameter(WebKeys.FORUM_URL_OPERATION, WebKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.FORUM_URL_FORUM_ID, forumId);
		url.setParameter(WebKeys.FORUM_URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_VERTICAL);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_vertical"), url);
		//accessible
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_VIEW_FORUM);
		url.setParameter(WebKeys.FORUM_URL_OPERATION, WebKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.FORUM_URL_FORUM_ID, forumId);
		url.setParameter(WebKeys.FORUM_URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_accessible"), url);
		//iframe
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_VIEW_FORUM);
		url.setParameter(WebKeys.FORUM_URL_OPERATION, WebKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.FORUM_URL_FORUM_ID, forumId);
		url.setParameter(WebKeys.FORUM_URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_IFRAME);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_iframe"), url);
		model.put(WebKeys.FOLDER_TOOLBAR, toolbar.getToolbar());
		//popup
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_VIEW_FORUM);
		url.setParameter(WebKeys.FORUM_URL_OPERATION, WebKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.FORUM_URL_FORUM_ID, forumId);
		url.setParameter(WebKeys.FORUM_URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_POPUP);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_popup"), url);
		model.put(WebKeys.FOLDER_TOOLBAR, toolbar.getToolbar());
		
	}
	public Map getDeleteEntry(Map formData, RenderRequest req, Long folderId)  {
		Map model = new HashMap();
		String entryId = ActionUtil.getStringValue(formData, WebKeys.FORUM_URL_ENTRY_ID);
		FolderEntry entry = getFolderModule().getEntry(folderId, Long.valueOf(entryId));
		model.put(WebKeys.FOLDER_ENTRY, entry);
		model.put(WebKeys.FOLDER, entry.getParentFolder());
		return model;
	}
	public Map getModifyEntry(Map formData, RenderRequest req, Long folderId) {
		Map model = new HashMap();
		FolderEntry entry=null;
		String entryId = ActionUtil.getStringValue(formData, WebKeys.FORUM_URL_ENTRY_ID);
		if (!entryId.equals("")) entry  = getFolderModule().getEntry(folderId, Long.valueOf(entryId));
		
		model.put(WebKeys.FOLDER_ENTRY, entry);
		model.put(WebKeys.FOLDER, entry.getParentFolder());
		model.put(WebKeys.CONFIG_JSP_STYLE, "form");
		getDefinition(entry.getEntryDef(), model, "//item[@name='entryForm']");

		return model;
		
	}

	public Map getShowEntry(Map formData, RenderRequest req, RenderResponse response, Long folderId)  {
		HistoryMap history = getHistory(req, folderId);
		Map model = getShowEntry(formData, history, folderId);
		String entryId = (String) model.get(WebKeys.ENTRY_ID);
		if (!entryId.equals("")) {
			buildEntryToolbar(response, model, folderId.toString(), entryId);
		}
		return model;
	}
	public Map getShowEntry(Map formData, HistoryMap history, Long folderId)  {
		Map model = new HashMap();
		model.put(WebKeys.HISTORY_MAP, history);
		String entryId = ActionUtil.getStringValue(formData, WebKeys.FORUM_URL_ENTRY_ID);
		String op = ActionUtil.getStringValue(formData, WebKeys.FORUM_URL_OPERATION);
		Folder folder = null;
		FolderEntry entry = null;
		Map folderEntries = null;
		if (op.equals("")) {
			if (!entryId.equals("")) folderEntries  = getFolderModule().getEntryTree(folderId, Long.valueOf(entryId));
		
		} else if (op.equals(WebKeys.FORUM_OPERATION_VIEW_ENTRY)) {
			if (!entryId.equals("")) {
				folderEntries  = getFolderModule().getEntryTree(folderId, Long.valueOf(entryId));
			}
	
		} else if (op.equals(WebKeys.FORUM_OPERATION_VIEW_ENTRY_HISTORY_NEXT)) {
			folder = getFolderModule().getFolder(folderId);
			Long currentEntryId = null;
			if (formData.containsKey(WebKeys.SESSION_LAST_ENTRY_VIEWED)) {
				currentEntryId = (Long)formData.get(WebKeys.SESSION_LAST_ENTRY_VIEWED);
			}
			if (formData.containsKey(WebKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED) && 
					(Long)formData.get(WebKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED) != null) {
				currentEntryId = (Long)formData.get(WebKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED);
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
	
		} else if (op.equals(WebKeys.FORUM_OPERATION_VIEW_ENTRY_HISTORY_PREVIOUS)) {
			folder = getFolderModule().getFolder(folderId);
			Long currentEntryId = null;
			if (formData.containsKey(WebKeys.SESSION_LAST_ENTRY_VIEWED)) {
				currentEntryId = (Long)formData.get(WebKeys.SESSION_LAST_ENTRY_VIEWED);
			}
			if (formData.containsKey(WebKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED)) {
				currentEntryId = (Long)formData.get(WebKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED);
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
	
		} else if (op.equals(WebKeys.FORUM_OPERATION_VIEW_ENTRY_NEXT)) {
			Long currentEntryId = null;
			if (formData.containsKey(WebKeys.SESSION_LAST_ENTRY_VIEWED)) {
				currentEntryId = (Long)formData.get(WebKeys.SESSION_LAST_ENTRY_VIEWED);
			}
			if (currentEntryId != null) {
				entryId = currentEntryId.toString();
			}
			if (!entryId.equals("")) folderEntries  = getFolderModule().getEntryTree(folderId, Long.valueOf(entryId), FolderModule.NEXT_ENTRY);
	
		} else if (op.equals(WebKeys.FORUM_OPERATION_VIEW_ENTRY_PREVIOUS)) {
			Long currentEntryId = null;
			if (formData.containsKey(WebKeys.SESSION_LAST_ENTRY_VIEWED)) {
				currentEntryId = (Long)formData.get(WebKeys.SESSION_LAST_ENTRY_VIEWED);
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
			model.put(WebKeys.FOLDER_ENTRY_DESCENDANTS, folderEntries.get(ObjectKeys.FOLDER_ENTRY_DESCENDANTS));
			model.put(WebKeys.FOLDER_ENTRY_ANCESTORS, folderEntries.get(ObjectKeys.FOLDER_ENTRY_ANCESTORS));
		}
		model.put(WebKeys.ENTRY_ID, entryId);
		model.put(WebKeys.SEEN_MAP, getProfileModule().getUserSeenMap(null, folder.getId()));
		model.put(WebKeys.FOLDER_ENTRY, entry);
		model.put(WebKeys.DEFINITION_ENTRY, entry);
		model.put(WebKeys.FOLDER, folder);
		model.put(WebKeys.CONFIG_JSP_STYLE, "view");
		model.put(WebKeys.USER_PROPERTIES, getProfileModule().getUserProperties(null).getProperties());
		if (entry == null) {
			getDefinition(null, model, "//item[@name='entryView']");
			return model;
		}
		if (getDefinition(entry.getEntryDef(), model, "//item[@name='entryView']") == false) {
			getDefaultEntryView(model);
		}
		return model;
	}
	public Map getShowFolder(Map formData, RenderRequest req, RenderResponse response,Long folderId)  {
		Map folderEntries;
		Map model = new HashMap();
		String forumId = folderId.toString();
		folderEntries = getFolderModule().getFolderEntries(folderId);
		Folder folder = (Folder)folderEntries.get(ObjectKeys.FOLDER);
	   	User user = RequestContextHolder.getRequestContext().getUser();
		//Build the beans depending on the operation being done
		model.put(WebKeys.FOLDER, folder);
		HistoryMap history = getHistory(req, folderId);
		model.put(WebKeys.HISTORY_MAP, history);
		Folder topFolder = folder.getTopFolder();
		if (topFolder == null) {
			model.put(WebKeys.FOLDER_DOM_TREE, getFolderModule().getDomFolderTree(folderId, this));
		} else {
			model.put(WebKeys.FOLDER_DOM_TREE, getFolderModule().getDomFolderTree(topFolder.getId(), this));			
		}
		model.put(WebKeys.FOLDER_ENTRIES, folderEntries.get(ObjectKeys.FOLDER_ENTRIES));
		model.put(WebKeys.USER_PROPERTIES, getProfileModule().getUserProperties(user.getId()).getProperties());
		model.put(WebKeys.SEEN_MAP,getProfileModule().getUserSeenMap(user.getId(), folder.getId()));
		getDefinitions(folder, model);
		ArrayList entries = (ArrayList) folderEntries.get(ObjectKeys.FOLDER_ENTRIES);
		getEvents(entries, model, req);
		req.setAttribute(WebKeys.FORUM_URL_FORUM_ID,forumId);
		buildFolderToolbar(response, model, forumId);
		return model;
	}
	public Map getDefinitionBuilder(Map formData, RenderRequest req, String currentId) {
		Map model = new HashMap();
		model.put(WebKeys.CONFIG_JSP_STYLE, "view");
		model.put(WebKeys.CONFIG_DEFINITION, getDefinitionModule().getDefinitionConfig());
			
		getDefinitions(model);
		if (!currentId.equals("")) {
			model.put(WebKeys.DEFINITION, getDefinitionModule().getDefinition(currentId));
		}
		return model;
	}
	public Map getConfigureForum(Map formData, RenderRequest req, Long folderId) {
		Map model = new HashMap();
		User user = RequestContextHolder.getRequestContext().getUser();
		Folder folder = getFolderModule().getFolder(folderId);
		
		model.put(WebKeys.FOLDER, folder);
		model.put(WebKeys.CONFIG_JSP_STYLE, "view");
		model.put(WebKeys.USER_PROPERTIES, getProfileModule().getUserProperties(user.getId()));
			
		getDefinitions(model);
		getDefinitions(folder, model);
		return model;
	}
	public Map getAddEntry(Map formData, RenderRequest req, Long folderId) {
		Map model = new HashMap();
		Folder folder = getFolderModule().getFolder(folderId);
		//Adding an entry; get the specific definition
		Map folderEntryDefs = ActionUtil.getEntryDefsAsMap(folder);
		String entryType = ActionUtil.getStringValue(formData, WebKeys.FORUM_URL_ENTRY_TYPE);
		model.put(WebKeys.FOLDER, folder);
		model.put(WebKeys.ENTRY_DEFINTION_MAP, folderEntryDefs);
		model.put(WebKeys.CONFIG_JSP_STYLE, "form");
		//Make sure the requested definition is legal
		if (folderEntryDefs.containsKey(entryType)) {
			getDefinition(getDefinitionModule().getDefinition(entryType), model, "//item[@name='entryForm']");
		} else {
			getDefinition(null, model, "//item[@name='entryForm']");
		}
		return model;
		
	}
    public Map getAddReply(Map formData, RenderRequest req, Long folderId) {
    	String entryId = ActionUtil.getStringValue(formData, WebKeys.FORUM_URL_ENTRY_ID);
    	req.setAttribute(WebKeys.FORUM_URL_ENTRY_ID,entryId);
    	Map model = new HashMap();
    	FolderEntry entry = getFolderModule().getEntry(folderId, Long.valueOf(entryId));
    	model.put(WebKeys.DEFINITION_ENTRY, entry);
    	Folder folder = entry.getParentFolder();
    	model.put(WebKeys.FOLDER, folder); 
		
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
    	String entryType = ActionUtil.getStringValue(formData, WebKeys.FORUM_URL_ENTRY_TYPE);
    	model.put(WebKeys.ENTRY_DEFINTION_MAP, folderEntryDefs);
    	model.put(WebKeys.CONFIG_JSP_STYLE, "form");
        //Make sure the requested definition is legal
		if (replyStyle.equals(entryType)) {
			getDefinition(getDefinitionModule().getDefinition(entryType), model, "//item[@name='entryForm']");
		} else {
			getDefinition(null, model, "//item[@name='entryForm']");
		}
    	return model;
    }
    


	public Element setupDomElement(String type, Object source, Element element) {
		if (type.equals(DomTreeBuilder.TYPE_FOLDER)) {
			Folder f = (Folder)source;
			element.addAttribute("type", "forum");
			element.addAttribute("title", f.getTitle());
			element.addAttribute("id", f.getId().toString());
			element.addAttribute("image", "forum");
        	Element url = element.addElement("url");
	    	url.addAttribute(WebKeys.ACTION, WebKeys.FORUM_ACTION_VIEW_FORUM);
	     	url.addAttribute(WebKeys.FORUM_URL_FORUM_ID, f.getId().toString());
		} else return null;
		return element;
	}
}
