/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.web.util;

import static org.kablink.util.search.Restrictions.in;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.util.TrashTraverser;
import org.kablink.teaming.web.WebKeys;
import org.kablink.util.StringPool;
import org.kablink.util.StringUtil;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Order;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.PortletRequestBindingException;

/**
 * Helper class used to manage the trash.
 * 
 * @author drfoster@novell.com
 */
public class TrashHelper {
	// Class data members.
	public static final String[] trashColumns= new String[] {"title", "date", "author", "location"};
	protected static Log logger = LogFactory.getLog(TrashHelper.class);

	/*
	 * Inner class used to manipulate entries in the trash.
	 */
	private static class TrashEntry {
		// Class data members.
		public Long		m_docId;
		public Long		m_locationBinderId;
		public String	m_docType;
		public String	m_entityType;
		
		/**
		 * Constructs a TrashEntry based on the packed string
		 * representation of one.
		 * 
		 * @param paramS
		 */
		public TrashEntry(String paramS) {
			String[] params = paramS.split(StringPool.COLON);
			
			m_docId				= Long.valueOf(params[0]);
			m_locationBinderId	= Long.valueOf(params[1]);
			m_docType			=              params[2];
			m_entityType		=              params[3];
		}
		
		/**
		 * Constructs a TrashEntry based on an results of a search.
		 * 
		 * @param searchResultsMap
		 */
		@SuppressWarnings("unchecked")
		public TrashEntry(Map searchResultsMap) {
			m_docId      = Long.valueOf((String) searchResultsMap.get("_docId"));
			m_docType    =             ((String) searchResultsMap.get("_docType"));
			m_entityType =             ((String) searchResultsMap.get("_entityType"));
			if (isEntry()) {
				m_locationBinderId = Long.valueOf((String) searchResultsMap.get("_binderId"));
			}
			else if (isBinder()) {
				m_locationBinderId = Long.valueOf((String) searchResultsMap.get("_binderParentId"));
			}
		}

		/**
		 * Returns true if this TrashEntry is a binder.
		 * 
		 * @return
		 */
		public boolean isBinder() {
			return "binder".equalsIgnoreCase(m_docType);
		}
		
		/**
		 * Returns true if this TrashEntry is an entry.
		 * 
		 * @return
		 */
		public boolean isEntry() {
			return "entry".equalsIgnoreCase(m_docType);
		}
		
		/**
		 * Returns true if this TrashEntry is a Folder.
		 * 
		 * @return
		 */
		public boolean isFolder(AllModulesInjected bs) {
			if (isBinder()) {
				isBinderFolder(bs.getBinderModule().getBinder(m_docId));
			}
			return false;
		}
		
		/**
		 * Returns true if the TrashEntry is valid and in a predeleted
		 * state and false otherwise.
		 * 
		 * @param bs
		 * @return
		 */
		public boolean isPreDeleted(AllModulesInjected bs) {
			boolean reply = false;
			try {
				if (isEntry()) {
					// Is the entry is still there and predeleted?
					FolderEntry fe = bs.getFolderModule().getEntry(m_locationBinderId, m_docId);
					reply = fe.isPreDeleted();
				}
				else if (isBinder()) {
					// Is the binder is still there and is it a Folder
					// or Workspace and predeleted? 
					Binder binder = bs.getBinderModule().getBinder(m_docId);
					if (isBinderFolder(binder)) {
						reply = ((Folder) binder).isPreDeleted();
					}
					else if (isBinderWorkspace(binder)) {
						reply = ((Workspace) binder).isPreDeleted();
					}
				}
			} catch (Exception ex) {
				// Ignore.
			}
			return reply;
		}
		
		/**
		 * Returns true if this TrashEntry is a Workspace.
		 * 
		 * @return
		 */
		public boolean isWorkspace(AllModulesInjected bs) {
			if (isBinder()) {
				return isBinderWorkspace(bs.getBinderModule().getBinder(m_docId));
			}
			return false;
		}
	}
	
	/*
	 * Inner class used to traverse trash items to predelete them.
	 */
	private static class TrashPreDelete implements TrashTraverser.TraverseCallback {
		public boolean binder(AllModulesInjected bs, Long binderId, Object callbackData) {
			boolean reply;
			try {
				logger.debug("TrashPreDelete.binder(" + binderId + "):  Predeleting binder.");
				bs.getBinderModule().preDeleteBinder(binderId, RequestContextHolder.getRequestContext().getUser().getId(), true);
				reply = true;
			}
			catch (Exception ex) {
//!				...this needs to be implemented...
				logger.debug("TrashPreDelete.binder(" + binderId + "):  ", ex);
				reply = false;
			}
			return reply;
		}
		
		public boolean entry(AllModulesInjected bs, Long folderId, Long entryId, Object callbackData) {
			boolean reply;
			try {
				logger.debug("TrashPreDelete.entry(" + folderId + ", " + entryId + "):  Predeleting entry.");
				bs.getFolderModule().preDeleteEntry(folderId, entryId, RequestContextHolder.getRequestContext().getUser().getId());
				reply = true;
			}
			catch (Exception ex) {
//!				...this needs to be implemented...
				logger.debug("TrashPreDelete.entry(" + folderId + ", " + entryId + "):  ", ex);
				reply = false;
			}
			return reply;
		}
	}
	
	/*
	 * Inner class used to traverse trash items to restore them.
	 */
	private static class TrashRestore implements TrashTraverser.TraverseCallback {
		public boolean binder(AllModulesInjected bs, Long binderId, Object callbackData) {
			boolean reply;
			try {
				logger.debug("TrashRestore.binder(" + binderId + "):  Restoring binder.");
				bs.getBinderModule().restoreBinder(binderId, true);
				reply = true;
			}
			catch (Exception ex) {
//!				...this needs to be implemented...
				logger.debug("TrashRestore.binder(" + binderId + "):  ", ex);
				reply = false;
			}
			return reply;
		}
		
		public boolean entry(AllModulesInjected bs, Long folderId, Long entryId, Object callbackData) {
			boolean reply;
			try {
				logger.debug("TrashRestore.entry(" + folderId + ", " + entryId + "):  Restoring entry.");
				bs.getFolderModule().restoreEntry(folderId, entryId, true);
				reply = true;
			}
			catch (Exception ex) {
//!				...this needs to be implemented...
				logger.debug("TrashRestore.entry(" + folderId + ", " + entryId + "):  ", ex);
				reply = false;
			}
			return reply;
		}
	}
	
	/**
	 * Called to handle AJAX trash requests received by
	 * AjaxController.java.
	 * 
	 * @param op
	 * @param request
	 * @param response
	 */
	public static ModelAndView ajaxTrashRequest(String op, AllModulesInjected bs, RenderRequest request, RenderResponse response) {
		ModelAndView mv;
		if (op.equals(WebKeys.OPERATION_TRASH_PURGE) || op.equals(WebKeys.OPERATION_TRASH_RESTORE)) {
			String	paramS = PortletRequestUtils.getStringParameter(request, "params", "");
			String[]	params = StringUtil.unpack(paramS);
			int count = ((null == params) ? 0 : params.length);
			TrashEntry[] trashEntries = new TrashEntry[count];
			for (int i = 0; i < count; i += 1) {
				trashEntries[i] = new TrashEntry(params[i]);
			}
			if (op.equals(WebKeys.OPERATION_TRASH_PURGE)) mv = purgeEntries(  bs, trashEntries, request, response);
			else                                          mv = restoreEntries(bs, trashEntries, request, response);
		}
		else if (op.equals(WebKeys.OPERATION_TRASH_PURGE_ALL))   mv = purgeAll(  bs, request, response);
		else if (op.equals(WebKeys.OPERATION_TRASH_RESTORE_ALL)) mv = restoreAll(bs, request, response);
		else {
			response.setContentType("text/xml");
			mv = new ModelAndView("forum/ajax_return");
		}
		
		return mv;
	}

	/**
	 * When required, builds the Trash toolbar for a binder.
	 * 
	 * @param binderId
	 * @param model
	 * @param qualifiers
	 * @param trashToolbar
	 */
	@SuppressWarnings("unchecked")
	public static void buildTrashToolbar(String binderId, Map model, Map qualifiers, Toolbar trashToolbar) {
		// Show the trash sidebar widget...
		model.put(WebKeys.TOOLBAR_TRASH_SHOW, Boolean.TRUE);
		
		// ...and add trash to the menu bar.
		qualifiers = new HashMap();
		qualifiers.put("title", NLT.get("toolbar.menu.title.trash"));
		qualifiers.put("icon", "trash.png");
		qualifiers.put("iconFloatRight", "true");
		qualifiers.put("onClick", "ss_treeShowId('"+binderId+"',this,'view_folder_listing','&showTrash=true');return false;");
		trashToolbar.addToolbarMenu("1_trash", NLT.get("toolbar.menu.trash"), "javascript: //;", qualifiers);	
	}

	/**
	 * Build the menu bar within the trash viewer.
	 * 
	 * @param model
	 */
	@SuppressWarnings("unchecked")
	public static void buildTrashViewToolbar(Map model) {
		Toolbar trashViewToolbar = new Toolbar();

		Map qualifiers = new HashMap();
		qualifiers.put("title", NLT.get("toolbar.menu.title.trashRestore"));
		qualifiers.put("onClick", "ss_trashRestore();return false;");
		trashViewToolbar.addToolbarMenu("1_trashRestore", NLT.get("toolbar.menu.trash.restore"), "javascript: //;", qualifiers);	
		
		qualifiers = new HashMap();
		qualifiers.put("title", NLT.get("toolbar.menu.title.trashPurge"));
		qualifiers.put("onClick", "ss_trashPurge();return false;");
		trashViewToolbar.addToolbarMenu("2_trashPurge", NLT.get("toolbar.menu.trash.purge"), "javascript: //;", qualifiers);	
		
		qualifiers = new HashMap();
		qualifiers.put("title", NLT.get("toolbar.menu.title.trashRestoreAll"));
		qualifiers.put("onClick", "ss_trashRestoreAll();return false;");
		trashViewToolbar.addToolbarMenu("3_trashRestoreAll", NLT.get("toolbar.menu.trash.restoreAll"), "javascript: //;", qualifiers);	
		
		qualifiers = new HashMap();
		qualifiers.put("title", NLT.get("toolbar.menu.title.trashPurgeAll"));
		qualifiers.put("onClick", "ss_trashPurgeAll();return false;");
		trashViewToolbar.addToolbarMenu("4_trashPurgeAll", NLT.get("toolbar.menu.trash.purgeAll"), "javascript: //;", qualifiers);	
		
		model.put(WebKeys.TRASH_VIEW_TOOLBAR, trashViewToolbar.getToolbar());
	}

	/**
	 * Builds the beans for displaying the trash from either a folder
	 * or workspace binder.
	 *  
	 * @param bs
	 * @param request
	 * @param response
	 * @param binderId
	 * @param zoneUUID
	 * @param model
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map buildTrashBeans(AllModulesInjected bs, RenderRequest request, RenderResponse response, Long binderId, Map model) throws Exception{
		// Access the binder...
		Binder binder = bs.getBinderModule().getBinder(binderId);
		
		// ...store the tabs...
		Tabs.TabEntry tab= BinderHelper.initTabs(request, binder);
		model.put(WebKeys.TABS, tab.getTabs());

		
		// Access the user properties... 
		UserProperties userProperties       = ((UserProperties) model.get(WebKeys.USER_PROPERTIES_OBJ));
		UserProperties userFolderProperties = ((UserProperties) model.get(WebKeys.USER_FOLDER_PROPERTIES_OBJ));

		// ...initialize the page counts and sort order.
		Map options = ListFolderHelper.getSearchFilter(bs, request, binder, userFolderProperties, true);
		ListFolderHelper.initPageCounts(bs, request, userProperties.getProperties(), tab, options);
		BinderHelper.initSortOrder(bs, userFolderProperties, options);
		return options;
	}

	/*
	 * Returns a TrashEntry[] of all the items in the trash (non paged
	 * and non-sorted.)  Used to perform purge/restore alls.
	 */
	@SuppressWarnings("unchecked")
	private static TrashEntry[] getAllTrashEntries(AllModulesInjected bs, RenderRequest request) {
		// Convert the current trash entries to an ArrayList of TrashEntry's...
		Long binderId = null;
		try {
			binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);				
		} catch(PortletRequestBindingException ex) {}
		Binder binder = bs.getBinderModule().getBinder(binderId);
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_OFFSET,    Integer.valueOf(0));
		options.put(ObjectKeys.SEARCH_MAX_HITS,  Integer.MAX_VALUE);
		options.put(ObjectKeys.SEARCH_SORT_NONE, Boolean.TRUE);
		Map trashSearchMap = getTrashEntries(bs, binder, options);
		ArrayList trashSearchAL = ((ArrayList) trashSearchMap.get(ObjectKeys.SEARCH_ENTRIES));
		ArrayList<TrashEntry> trashEntriesAL = new ArrayList<TrashEntry>();
        for (Iterator trashEntriesIT=trashSearchAL.iterator(); trashEntriesIT.hasNext();) {
			trashEntriesAL.add(new TrashEntry((Map) trashEntriesIT.next()));
		}

        // ...and return them as a TrashEntry[].
       	return ((TrashEntry[]) trashEntriesAL.toArray(new TrashEntry[0]));
	}
	
	/**
	 * Returns the trash entries for the given binder.
	 * 
	 * @param bs
	 * @param binder
	 * @param options
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map getTrashEntries(AllModulesInjected bs, Binder binder, Map options) {
		return getTrashEntries(bs, null, binder, options);
	}
	@SuppressWarnings("unchecked")
	public static Map getTrashEntries(AllModulesInjected bs, Map<String,Object>model, Binder binder, Map options) {
		// Construct the search Criteria...
		Criteria crit = new Criteria();
		crit.add(in(Constants.DOC_TYPE_FIELD, new String[] {Constants.DOC_TYPE_ENTRY, Constants.DOC_TYPE_BINDER}))
		    .add(in(Constants.ENTRY_ANCESTRY, new String[] {String.valueOf(binder.getId())}));

		// ...if sorting is enabled...
		boolean sortDisabled = getOptionBoolean(options, ObjectKeys.SEARCH_SORT_NONE, false);
		if (!sortDisabled) {
			// ...add in the sort information...
			boolean sortDescend = getOptionBoolean(options, ObjectKeys.SEARCH_SORT_DESCEND, false);
			String  sortBy      = getOptionString( options, ObjectKeys.SEARCH_SORT_BY,      Constants.SORT_TITLE_FIELD);
			crit.addOrder(new Order(sortBy, sortDescend));
		}
		
		// ...and issue the query and return the entries.
		return
			bs.getBinderModule().executeSearchQuery(
				crit,
				getOptionInt(options, ObjectKeys.SEARCH_OFFSET,   0),
				getOptionInt(options, ObjectKeys.SEARCH_MAX_HITS, ObjectKeys.SEARCH_MAX_HITS_SUB_BINDERS),
				true);	// true -> Search deleted entries.
	}

	/*
	 * Returns an Integer based value from an options Map.  If a value
	 * for key isn't found, defInt is returned. 
	 */
	@SuppressWarnings("unchecked")
	private static int getOptionInt(Map options, String key, int defInt) {
		Integer obj = ((Integer) options.get(key));
		return ((null == obj) ? defInt : obj.intValue());
	}
	
	/*
	 * Returns a Boolean based value from an options Map.  If a value
	 * for key isn't found, defBool is returned. 
	 */
	@SuppressWarnings("unchecked")
	private static boolean getOptionBoolean(Map options, String key, boolean defBool) {
		Boolean obj = ((Boolean) options.get(key));
		return ((null == obj) ? defBool : obj.booleanValue());
	}
	
	/*
	 * Returns a String based value from an options Map.  If a value
	 * for key isn't found, defStr is returned. 
	 */
	@SuppressWarnings("unchecked")
	private static String getOptionString(Map options, String key, String defStr) {
		String obj = ((String) options.get(key));
		return (((null == obj) || (0 == obj.length())) ? defStr : obj);
	}

	/*
	 * Returns true if binder is a Folder.
	 */
	public static boolean isBinderFolder(Binder binder) {
		return (EntityType.folder == binder.getEntityType());
	}
	
	/*
	 * Returns true if binder is a Workspace.
	 */
	public static boolean isBinderWorkspace(Binder binder) {
		return (EntityType.workspace == binder.getEntityType());
	}
	
	/*
	 * Called to purge all the entries in the trash. 
	 */
	private static ModelAndView purgeAll(AllModulesInjected bs, RenderRequest request, RenderResponse response) {
		// If there are any TrashEntry's...
		TrashEntry[] trashEntries = getAllTrashEntries(bs, request);
        if (0 < trashEntries.length) {
            // ...purge them.
        	return purgeEntries(bs, trashEntries, request, response);
        }
        
		response.setContentType("text/xml");
		return new ModelAndView("forum/ajax_return");
	}

	/**
	 * Called to mark a binder and its contents as predeleted.
	 * 
	 * @param bs
	 * @param folderId
	 * @param entryId
	 */
	public static void preDeleteBinder(AllModulesInjected bs, Long binderId) {
		TrashPreDelete trashPD = new TrashPreDelete();
		TrashTraverser tt = new TrashTraverser(bs, logger, trashPD, null);
		tt.doTraverse(TrashTraverser.Mode.DESCENDING, binderId);
	}
	
	/**
	 * Called to mark an entry as predeleted.
	 * 
	 * @param bs
	 * @param folderId
	 * @param entryId
	 */
	public static void preDeleteEntry(AllModulesInjected bs, Long folderId, Long entryId) {
		TrashPreDelete trashPD = new TrashPreDelete();
		TrashTraverser tt = new TrashTraverser(bs, logger, trashPD, null);
		tt.doTraverse(TrashTraverser.Mode.DESCENDING, folderId, entryId);
	}
	
	/*
	 * Called to purge the TrashEntry's in trashEntries. 
	 */
	private static ModelAndView purgeEntries(AllModulesInjected bs, TrashEntry[] trashEntries, RenderRequest request, RenderResponse response) {
		// Scan the TrashEntry's.
		int count = ((null == trashEntries) ? 0 : trashEntries.length);
		for (int i = 0; i < count; i += 1) {
			// Is this trashEntry valid and predeleted?
			TrashEntry trashEntry = trashEntries[i];
			if (trashEntry.isPreDeleted(bs)) {
				// Yes!  Is it an entry?
				if (trashEntry.isEntry()) {
					// Yes!  Purge it.  Purging the entry should purge
					// its replies.
					bs.getFolderModule().deleteEntry(trashEntry.m_locationBinderId, trashEntry.m_docId);
				}
				
				// No, it isn't an entry!  Is it a binder?
				else if (trashEntry.isBinder()) {
					// Yes!  Purge it.  Purging the binder should purge
					// its children.
					bs.getBinderModule().deleteBinder(trashEntry.m_docId);
				}
			}
		}
		
		response.setContentType("text/xml");
		return new ModelAndView("forum/ajax_return");
	}

	/*
	 * Called to restore the entries in the trash. 
	 */
	private static ModelAndView restoreAll(AllModulesInjected bs, RenderRequest request, RenderResponse response) {
		// If there are any TrashEntry's...
		TrashEntry[] trashEntries = getAllTrashEntries(bs, request);
        if (0 < trashEntries.length) {
            // ...restore them.
        	return restoreEntries(bs, trashEntries, request, response);
        }
        
		response.setContentType("text/xml");
		return new ModelAndView("forum/ajax_return");
	}
	
	/*
	 * Called to restore a binder.
	 */
	private static void restoreBinder(AllModulesInjected bs, Long binderId) {
		TrashRestore trashR = new TrashRestore();
		TrashTraverser tt = new TrashTraverser(bs, logger, trashR, null);
		tt.doTraverse(TrashTraverser.Mode.ASCENDING, binderId);
	}

	/*
	 * Called to restore an entry.
	 */
	private static void restoreEntry(AllModulesInjected bs, Long folderId, Long entryId) {
		restoreEntry(bs, folderId, entryId, true);
	}
	private static void restoreEntry(AllModulesInjected bs, Long folderId, Long entryId, boolean restoreParentage) {
		TrashRestore trashR = new TrashRestore();
		TrashTraverser tt = new TrashTraverser(bs, logger, trashR, null);
		tt.doTraverse(TrashTraverser.Mode.ASCENDING, folderId, entryId);
	}
	
	/*
	 * Called to restore the TrashEntry's in trashEntries. 
	 */
	private static ModelAndView restoreEntries(AllModulesInjected bs, TrashEntry[] trashEntries, RenderRequest request, RenderResponse response) {
		// Scan the TrashEntry's.
		int count = ((null == trashEntries) ? 0 : trashEntries.length);
		for (int i = 0; i < count; i += 1) {
			// Is this trashEntry valid and predeleted?
			TrashEntry trashEntry = trashEntries[i];
			if (trashEntry.isPreDeleted(bs)) {
				// Yes!  Is it an entry?
				if (trashEntry.isEntry()) {
					// Yes!  Restore the entry itself...
					restoreEntry(bs, trashEntry.m_locationBinderId, trashEntry.m_docId);
				}
				
				// No, it isn't an entry!  Is it a binder?
				else if (trashEntry.isBinder()) {
					// Yes!  Restore the binder itself...
					restoreBinder(bs, trashEntry.m_docId);
				}
			}
		}
		
		response.setContentType("text/xml");
		return new ModelAndView("forum/ajax_return");
	}
}
