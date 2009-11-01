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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FileItem;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.util.TrashTraverser;
import org.kablink.teaming.web.util.TrashTraverser.TraverseCallback;
import org.kablink.teaming.web.util.TrashTraverser.TraversalMode;
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
	private final static int DEFAULT_RENAME_LIST_SIZE = 5;

	/*
	 * Inner class used to assist/manage in the renaming of binders,
	 * entries and files that have naming conflicts during a restore. 
	 */
	private static class TrashRenameData {
		// Class data members.
		public AllModulesInjected 		m_bs;
		public HashMap<String, String>	m_renameMap;
		
		public enum RenameType {
			Binder,
			Entry,
			File,
		}

		/*
		 * Class constructor.
		 */
		public TrashRenameData(AllModulesInjected bs) {
			m_bs        = bs;
			m_renameMap = new HashMap<String, String>();
		}

		/*
		 * Adds a rename item to the rename map.
		 */
		public void addRename(RenameType rt, String from, String to) {
			m_renameMap.put(getKey(rt, from), to);
		}

		/*
		 * Removes any items from the rename map.
		 */
		public void clearRenames() {
			m_renameMap.clear();
		}
		
		/*
		 * Generates a key for rename entries in the rename map.
		 */
		private String getKey(RenameType rt, String baseKey) {
			String key;
			if      (RenameType.Binder == rt) key = "B:";
			else if (RenameType.Entry  == rt) key = "E:";
			else if (RenameType.File   == rt) key = "F:";
			else                              key = "";
			return (key + baseKey);
		}
		
		/*
		 * Returns true if there are items in the rename map. 
		 */
		public boolean hasRenames() {
			return (!(m_renameMap.isEmpty()));
		}
	}
	
	/*
	 * Inner class used to communicate information while traversing
	 * binders and entries to predelete or restore them. 
	 */
	private static class TrashResponse {
		// Class data members.
		public Exception		m_exception;
		public Long				m_binderId;
		public Long				m_entryId;
		public Status			m_status;
		public TrashRenameData	m_rd;

		// Used for the current status of the TrashResponse object.
		public enum Status {
			NoError,
			Exception,
			ACLViolation,
		}

		/*
		 * Class constructor.
		 */
		public TrashResponse(AllModulesInjected bs) {
			m_rd = new TrashRenameData(bs);
			reset();
		}

		/*
		 * Returns the display name for the binderId in the
		 * TrashResponse. 
		 */
		public String getBinderDisplayName(AllModulesInjected bs) {
			String reply = String.valueOf(m_binderId);
			try {
				Binder binder = bs.getBinderModule().getBinder(m_binderId);
				if (null != binder) {
					reply = binder.getTitle();
				}
			}
			catch (Exception e) {
				// Ignore.
			}
			return reply;
		}
		
		/*
		 * Returns the display name for the entryId in the TrashResponse. 
		 */
		public String getEntryDisplayName(AllModulesInjected bs) {
			String reply = String.valueOf(m_entryId);
			try {
				FolderEntry fe = bs.getFolderModule().getEntry(m_binderId, m_entryId);
				if (null != fe) {
					reply = fe.getTitle();
				}
			}
			catch (Exception e) {
				// Ignore.
			}
			return reply;
		}
		
		/*
		 * Generates a message string based on the content of this
		 * TrashResponse. 
		 */
		public String getTrashMessage(AllModulesInjected bs) {
			boolean needArgs = false;
			String resourceKey = null;
			String exString = null;
			String reply = null;
			
			// Does the TrashResponse indicate an Exception?
			if (Status.Exception == m_status) {
				// Yes!  Setup to generate an appropriate message.
				needArgs    = true;
				resourceKey = "trash.error.exception.";
				exString    = m_exception.getLocalizedMessage();
				if ((null == exString) || (0 == exString.length())) {
					exString = m_exception.getMessage();
				}
			}
			
			// No, the TrashResponse didn't indicate an Exception!  Did
			// it indicate an ACL violation?
			else if (Status.ACLViolation == m_status) {
				// Yes!  Setup to generate an appropriate message.
				needArgs    = true;
				resourceKey = "trash.error.ACLViolation.";
			}
			
			// No, the TrashResponse didn't indicate an ACL violation
			// either!  Are we tracking rename warnings?
			else if (m_rd.hasRenames()) {
				// Yes!  Generate an appropriate message.
				reply = (NLT.get("trash.warning.RenamedOnRestore") + "\n");
				Set<String> keySet = m_rd.m_renameMap.keySet();
				int rCount = 0;
				int rDisplay = SPropsUtil.getInt("trash.restore.RenameListSize", DEFAULT_RENAME_LIST_SIZE);
				for (Iterator<String> keyIT = keySet.iterator(); keyIT.hasNext();) {
			    	rCount += 1;
			    	if (rCount > rDisplay) {
			    		reply += ("\n\t" + NLT.get("trash.warning.RenamedOnRestore.More"));
			    		break;
			    	}
					String key = keyIT.next();
					String resKey = "trash.warning.RenamedOnRestore.Each";
					String keyPatch;
					if      (0 == key.indexOf("B:")) {keyPatch = key.substring(2); resKey += ".Binder";}
					else if (0 == key.indexOf("E:")) {keyPatch = key.substring(2); resKey += ".Entry"; }
					else if (0 == key.indexOf("F:")) {keyPatch = key.substring(2); resKey += ".File";  }
					else                             {keyPatch = key;                                  }
					String each = NLT.get(resKey, new String[]{keyPatch, m_rd.m_renameMap.get(key)});
					reply += ("\n\t" + each);
				}
			}
			
			else {
				// No, the TrashResponse didn't indicate any rename
				// warnings!  This should never have been called.
				needArgs    = false;
				resourceKey = "trash.error.internalerror";
			}

			// Do we need to generate a messages from the resources?
			if (null == reply) {
				String[] args;
				
				// Yes!  Does the message require arguments to be
				// patched in?
				if (needArgs) {
					// Yes!  Generate the arguments.
					ArrayList<String> argsAL = new ArrayList<String>();
					argsAL.add(getBinderDisplayName(bs));
					if (null != m_entryId) {
						resourceKey += "entry";
						argsAL.add(getEntryDisplayName(bs));
					}
					else { 
						resourceKey += "binder";
					}
					if (null != exString) {
						argsAL.add(exString);
					}
					args = argsAL.toArray(new String[0]);
				}
				else {
					args = null;
				}

				// Setup to return the appropriate message.
				if (null == args) reply = NLT.get(resourceKey      ); 
				else              reply = NLT.get(resourceKey, args);
			}

			// If we get here, reply refers to a String containing an
			// appropriate message to display for this TrashEntry.
			// Return it.
			return reply;
		}
		
		/*
		 * Resets the object's data members to their initial state.
		 */
		public void reset() {
			// Reset the simple data members...
			m_status    = Status.NoError;
			m_exception = null;
			m_binderId  =
			m_entryId   = null;
			
			// ...and clear the rename data.
			m_rd.clearRenames();
		}

		/*
		 * Returns true if the object represents an error and false
		 * otherwise.
		 */
		public boolean isError() {
			return (Status.NoError != m_status);
		}

		/*
		 * Sets the object to contain information about an ACL
		 * violation.
		 */
		public void setACLViolation(Long binderId) {
			// Always use the final form of the method.
			setACLViolation(binderId, null);
		}
		public void setACLViolation(Long binderId, Long entryId) {
			if (null == entryId) logger.debug("TrashResponse.setACLViolation(" + binderId +                  "):  ACL violation.");
			else                 logger.debug("TrashResponse.setACLViolation(" + binderId + ", " + entryId + "):  ACL violation.");
			
			reset();
			m_status    = Status.ACLViolation;
			m_exception = new AccessControlException();
			m_binderId  = binderId;
			m_entryId   = entryId;
		}

		/*
		 * Set the object to contain information about a generic
		 * exception.
		 */
		public void setException(Exception ex, Long binderId) {
			// Always use the final form of the method.
			setException(ex, binderId, null);
		}
		public void setException(Exception ex, Long binderId, Long entryId) {
			if (null == entryId) logger.debug("TrashResponse.setException(" + binderId +                  "):  ", ex);
			else                 logger.debug("TrashResponse.setException(" + binderId + ", " + entryId + "):  ", ex);
			
			reset();
			m_status    = Status.Exception;
			m_exception = ex;
			m_binderId  = binderId;
			m_entryId   = entryId;
		}
	}
	
	/*
	 * Inner class used to manipulate entries in the trash.
	 */
	private static class TrashEntry {
		// Class data members.
		public Long		m_docId;
		public Long		m_locationBinderId;
		public String	m_docType;
		public String	m_entityType;
		
		/*
		 * Constructs a TrashEntry based on the packed string
		 * representation of one.
		 */
		public TrashEntry(String paramS) {
			String[] params = paramS.split(StringPool.COLON);
			
			m_docId				= Long.valueOf(params[0]);
			m_locationBinderId	= Long.valueOf(params[1]);
			m_docType			=              params[2];
			m_entityType		=              params[3];
		}
		
		/*
		 * Constructs a TrashEntry based on an results of a search.
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

		/*
		 * Returns true if this TrashEntry is a binder.
		 */
		public boolean isBinder() {
			return "binder".equalsIgnoreCase(m_docType);
		}
		
		/*
		 * Returns true if this TrashEntry is an entry.
		 */
		public boolean isEntry() {
			return "entry".equalsIgnoreCase(m_docType);
		}
		
		/*
		 * Returns true if this TrashEntry is a Folder.
		 */
		public boolean isFolder(AllModulesInjected bs) {
			if (isBinder()) {
				isBinderFolder(bs.getBinderModule().getBinder(m_docId));
			}
			return false;
		}
		
		/*
		 * Returns true if the TrashEntry is valid and in a predeleted
		 * state and false otherwise.
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
		
		/*
		 * Returns true if this TrashEntry is a Workspace.
		 */
		public boolean isWorkspace(AllModulesInjected bs) {
			if (isBinder()) {
				return isBinderWorkspace(bs.getBinderModule().getBinder(m_docId));
			}
			return false;
		}
	}
	
	/*
	 * Inner classes used to traverse trash items to predelete them.
	 */
	private static class TrashCheckPreDeleteACLs implements TraverseCallback {
		public boolean binder(AllModulesInjected bs, Long binderId, Object cbDataObject) {
			boolean reply = true;
			try {
				logger.debug("TrashCheckPreDeleteACLs.binder(" + binderId + "):  Checking binder ACLs.");
				Binder binder = bs.getBinderModule().getBinder(binderId);
				boolean opAllowed = true;
				if (isBinderPredeleted(binder)) {
					opAllowed = bs.getBinderModule().testAccess(binder, BinderOperation.preDeleteBinder);
					if (!opAllowed) {
						logger.debug("TrashCheckPreDeleteACLs.binder(" + binderId + "):  Failed!");
						((TrashResponse) cbDataObject).setACLViolation(binderId);
					}
				}
				reply = opAllowed;
			}
			catch (Exception ex) {
				logger.debug("TrashCheckPreDeleteACLs.binder(" + binderId + "):  Failed!");
				((TrashResponse) cbDataObject).setException(ex, binderId);
				reply = false;
			}
			
			return reply;
		}
		
		public boolean entry(AllModulesInjected bs, Long folderId, Long entryId, Object cbDataObject) {
			boolean reply = true;
			try {
				logger.debug("TrashCheckPreDeleteACLs.entry(" + folderId + ", " + entryId + "):  Checking entry ACLs.");
				FolderEntry fe = bs.getFolderModule().getEntry(folderId, entryId);
				boolean opAllowed = true;
				if (fe.isPreDeleted()) {
					opAllowed = bs.getFolderModule().testAccess(fe, FolderOperation.preDeleteEntry);
					if (!opAllowed) {
						logger.debug("TrashCheckPreDeleteACLs.entry(" + folderId + ", " + entryId + "):  Failed!");
						((TrashResponse) cbDataObject).setACLViolation(folderId, entryId);
					}
				}
				reply = opAllowed;
			}
			catch (Exception ex) {
				logger.debug("TrashCheckPreDeleteACLs.entry(" + folderId + ", " + entryId + "):  Failed!");
				((TrashResponse) cbDataObject).setException(ex, folderId, entryId);
				reply = false;
			}
			
			return reply;
		}
	}
	
	private static class TrashPreDelete implements TraverseCallback {
		public boolean binder(AllModulesInjected bs, Long binderId, Object cbDataObject) {
			boolean reply;
			try {
				logger.debug("TrashPreDelete.binder(" + binderId + "):  Predeleting binder.");
				bs.getBinderModule().preDeleteBinder(binderId, RequestContextHolder.getRequestContext().getUser().getId(), true);
				reply = true;
			}
			catch (Exception ex) {
				logger.debug("TrashPreDelete.binder(" + binderId + "):  Failed!");
				((TrashResponse) cbDataObject).setException(ex, binderId);
				reply = false;
			}
			return reply;
		}
		
		public boolean entry(AllModulesInjected bs, Long folderId, Long entryId, Object cbDataObject) {
			boolean reply;
			try {
				logger.debug("TrashPreDelete.entry(" + folderId + ", " + entryId + "):  Predeleting entry.");
				bs.getFolderModule().preDeleteEntry(folderId, entryId, RequestContextHolder.getRequestContext().getUser().getId());
				reply = true;
			}
			catch (Exception ex) {
				logger.debug("TrashPreDelete.entry(" + folderId + ", " + entryId + "):  Failed!");
				((TrashResponse) cbDataObject).setException(ex, folderId, entryId);
				reply = false;
			}
			return reply;
		}
	}
	
	/*
	 * Inner classes used to traverse trash items to restore them.
	 */
	private static class TrashCheckRestoreACLs implements TraverseCallback {
		public boolean binder(AllModulesInjected bs, Long binderId, Object cbDataObject) {
			boolean reply = true;
			try {
				logger.debug("TrashCheckRestoreACLs.binder(" + binderId + "):  Checking binder ACLs.");
				Binder binder = bs.getBinderModule().getBinder(binderId);
				boolean opAllowed = true;
				if (isBinderPredeleted(binder)) {
					opAllowed = bs.getBinderModule().testAccess(binder, BinderOperation.restoreBinder);
					if (!opAllowed) {
						logger.debug("TrashCheckRestoreACLs.binder(" + binderId + "):  Failed!");
						((TrashResponse) cbDataObject).setACLViolation(binderId);
					}
				}
				reply = opAllowed;
			}
			catch (Exception ex) {
				logger.debug("TrashCheckRestoreACLs.binder(" + binderId + "):  Failed!");
				((TrashResponse) cbDataObject).setException(ex, binderId);
				reply = false;
			}
			
			return reply;
		}
		
		public boolean entry(AllModulesInjected bs, Long folderId, Long entryId, Object cbDataObject) {
			boolean reply = true;
			try {
				logger.debug("TrashCheckRestoreACLs.entry(" + folderId + ", " + entryId + "):  Checking entry ACLs.");
				FolderEntry fe = bs.getFolderModule().getEntry(folderId, entryId);
				boolean opAllowed = true;
				if (fe.isPreDeleted()) {
					opAllowed = bs.getFolderModule().testAccess(fe, FolderOperation.restoreEntry);
					if (!opAllowed) {
						logger.debug("TrashCheckRestoreACLs.entry(" + folderId + ", " + entryId + "):  Failed!");
						((TrashResponse) cbDataObject).setACLViolation(folderId, entryId);
					}
				}
				reply = opAllowed;
			}
			catch (Exception ex) {
				logger.debug("TrashCheckRestoreACLs.entry(" + folderId + ", " + entryId + "):  Failed!");
				((TrashResponse) cbDataObject).setException(ex, folderId, entryId);
				reply = false;
			}
			
			return reply;
		}
	}
	
	private static class TrashRestore implements TraverseCallback {
		public boolean binder(AllModulesInjected bs, Long binderId, Object cbDataObject) {
			TrashResponse tr = ((TrashResponse) cbDataObject);
			boolean reply;
			try {
				logger.debug("TrashRestore.binder(" + binderId + "):  Restoring binder.");
				bs.getBinderModule().restoreBinder(binderId, tr.m_rd, true);
				reply = true;
			}
			catch (Exception ex) {
				logger.debug("TrashRestore.binder(" + binderId + "):  Failed!");
				tr.setException(ex, binderId);
				reply = false;
			}
			return reply;
		}
		
		public boolean entry(AllModulesInjected bs, Long folderId, Long entryId, Object cbDataObject) {
			TrashResponse tr = ((TrashResponse) cbDataObject);
			boolean reply;
			try {
				logger.debug("TrashRestore.entry(" + folderId + ", " + entryId + "):  Restoring entry.");
				bs.getFolderModule().restoreEntry(folderId, entryId, tr.m_rd, true);
				reply = true;
			}
			catch (Exception ex) {
				logger.debug("TrashRestore.entry(" + folderId + ", " + entryId + "):  Failed!");
				tr.setException(ex, folderId, entryId);
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
			response.setContentType("text/json");
			mv = new ModelAndView("common/json_ajax_return");
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
		
		// ...setup the tabs...
		Tabs.TabEntry tab = buildTrashTabs(request, binder, model, true);

		// Access the user properties... 
		UserProperties userProperties       = ((UserProperties) model.get(WebKeys.USER_PROPERTIES_OBJ));
		UserProperties userFolderProperties = ((UserProperties) model.get(WebKeys.USER_FOLDER_PROPERTIES_OBJ));
		
		// ...initialize the page counts and sort order.
		Map options = ListFolderHelper.getSearchFilter(bs, request, binder, userFolderProperties, true);
		ListFolderHelper.initPageCounts(bs, request, userProperties.getProperties(), tab, options);
		model.put(WebKeys.PAGE_ENTRIES_PER_PAGE, (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS));
		BinderHelper.initSortOrder(bs, userFolderProperties, options);
		return options;
	}

	/**
	 * Builds the tabs for displaying the trash from either a folder
	 * or workspace binder.
	 * 
	 * @param request
	 * @param binder
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Tabs.TabEntry buildTrashTabs(RenderRequest request, Binder binder, Map model) throws Exception {
		return buildTrashTabs(request, binder, model, false);
	}
	@SuppressWarnings("unchecked")
	public static Tabs.TabEntry buildTrashTabs(RenderRequest request, Binder binder, Map model, boolean create) throws Exception {
		Tabs.TabEntry tab;
		if (create) {
			tab = BinderHelper.initTabs(request, binder);
		}
		else {
			tab = Tabs.getTabs(request).getTab(binder.getId());
		}
		model.put(WebKeys.TABS, tab.getTabs());
		Map tabData = tab.getData();
		Integer recordsInPage = ((Integer) tabData.get(Tabs.RECORDS_IN_PAGE));
		if (null == recordsInPage) {
			recordsInPage = Integer.valueOf(SPropsUtil.getString("folder.records.listed"));
			tabData.put(Tabs.RECORDS_IN_PAGE, recordsInPage);
		}
		return tab;
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
	 * Sets up the appropriate ModelAndView response based on a
	 * TrashResponse.
	 */
	@SuppressWarnings("unchecked")
	private static ModelAndView getMVBasedOnTrashResponse(RenderResponse response, AllModulesInjected bs, TrashResponse tr) {
		// We'll always return a JSON jsp.
		response.setContentType("text/json");
		
		// Do we need to display anything to the user?
		ModelAndView mvReply;
		if (tr.isError() || tr.m_rd.hasRenames()) {
			// Yes!  Return the message with the JSON jsp. 
			Map model = new HashMap();
			model.put(WebKeys.AJAX_ERROR_MESSAGE, tr.getTrashMessage(bs));
			model.put(WebKeys.AJAX_ERROR_MESSAGE_IS_TEXT, Boolean.TRUE);
			mvReply = new ModelAndView("common/json_ajax_return", model);
		}
		else {
			// No, we need to display anything to the user!  Return
			// the JSON jsp without any parameters.
			mvReply = new ModelAndView("common/json_ajax_return");
		}

		// If we get here, mvReply refers to the appropriate
		// ModelAndView for the TrashResponse.  Return it.
		return mvReply;
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
	 * Returns true if binder is a predeleted folder or workspace and
	 * false otherwise. 
	 */
	public static boolean isBinderPredeleted(Binder binder) {
		boolean reply = false;
		if (isBinderFolder(binder)) {
			reply = ((Folder) binder).isPreDeleted();
		}
		else if (isBinderWorkspace(binder)) {
			reply = ((Workspace) binder).isPreDeleted();
		}
		return reply;
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
        
		response.setContentType("text/json");
		return new ModelAndView("common/json_ajax_return");
	}

	/**
	 * Called to mark a binder and its contents as predeleted.
	 * 
	 * @param bs
	 * @param binderId
	 * @throws Exception
	 */
	public static void preDeleteBinder(AllModulesInjected bs, Long binderId) throws Exception {
		// Check the ACLs...
		TrashResponse tr = new TrashResponse(bs);
		TrashTraverser tt = new TrashTraverser(bs, logger, new TrashCheckPreDeleteACLs(), tr);
		tt.doTraverse(TraversalMode.DESCENDING, binderId);
		if (!(tr.isError())) {
			// ...and if they pass, perform the predelete.
			tt.setCallback(new TrashPreDelete());
			tt.doTraverse(TraversalMode.DESCENDING, binderId);
		}
		
		// For any error...
		if (tr.isError()) {
			// ...simply re-throw the exception.
			throw tr.m_exception;
		}
	}
	
	/**
	 * Called to mark an entry as predeleted.
	 * 
	 * @param bs
	 * @param folderId
	 * @param entryId
	 * @throws Exception
	 */
	public static void preDeleteEntry(AllModulesInjected bs, Long folderId, Long entryId) throws Exception{
		// Check the ACLs...
		TrashResponse tr = new TrashResponse(bs);
		TrashTraverser tt = new TrashTraverser(bs, logger, new TrashCheckPreDeleteACLs(), tr);
		tt.doTraverse(TraversalMode.DESCENDING, folderId, entryId);
		if (!(tr.isError())) {
			// ...and if they pass, perform the predelete.
			tt.setCallback(new TrashPreDelete());
			tt.doTraverse(TraversalMode.DESCENDING, folderId, entryId);
		}
		
		// For any error...
		if (tr.isError()) {
			// ...simply re-throw the exception.
			throw tr.m_exception;
		}
	}
	
	/*
	 * Called to purge the TrashEntry's in trashEntries. 
	 */
	private static ModelAndView purgeEntries(AllModulesInjected bs, TrashEntry[] trashEntries, RenderRequest request, RenderResponse response) {
		// Scan the TrashEntry's.
		int count = ((null == trashEntries) ? 0 : trashEntries.length);
		TrashResponse tr = new TrashResponse(bs);
		for (int i = 0; i < count; i += 1) {
			// Is this trashEntry valid and predeleted?
			TrashEntry trashEntry = trashEntries[i];
			if (trashEntry.isPreDeleted(bs)) {
				// Yes!  Is it an entry?
				if (trashEntry.isEntry()) {
					// Yes!  Purge it.  Purging the entry should purge
					// its replies.
					try {
						bs.getFolderModule().deleteEntry(trashEntry.m_locationBinderId, trashEntry.m_docId);
					}
					catch (Exception e) {
						if (e instanceof AccessControlException) tr.setACLViolation(trashEntry.m_locationBinderId, trashEntry.m_docId);
						else                                     tr.setException(e, trashEntry.m_locationBinderId, trashEntry.m_docId);
					}
				}
				
				// No, it isn't an entry!  Is it a binder?
				else if (trashEntry.isBinder()) {
					// Yes!  Purge it.  Purging the binder should purge
					// its children.
					try {
						bs.getBinderModule().deleteBinder(trashEntry.m_docId);
					}
					catch (Exception e) {
						if (e instanceof AccessControlException) tr.setACLViolation(trashEntry.m_docId);
						else                                     tr.setException(e, trashEntry.m_docId);
					}
				}
				
				// If we detect an error during the purge...
				if (tr.isError()) {
					// ...quit processing items.
					break;
				}
			}
		}
		
		// Handle any messages based on the purge.
		return getMVBasedOnTrashResponse(response, bs, tr);
	}

	/**
     * Called to register the name of the binder and the filenames of
     * any file attachments on the binder.
     *  
	 * @param cd
	 * @param binder
	 */
    public static void registerBinderNames(CoreDao cd, Binder binder, Object rd) throws WriteEntryDataException, WriteFilesException {
    	registerTitle(          cd, binder.getParentBinder(), binder,                          ((TrashRenameData) rd));
	    registerAttachmentNames(cd, binder.getParentBinder(), binder, binder.getAttachments(), ((TrashRenameData) rd));
    }
    
    /**
     * Called to register the name of the entry and the filenames of
     * any file attachments on the entry.
     *  
     * @param cd
     * @param folder
     * @param entry
     */
    public static void registerEntryNames(CoreDao cd, Folder folder, FolderEntry entry, Object rd) throws WriteEntryDataException, WriteFilesException {
    	registerTitle(          cd, folder, entry,                         ((TrashRenameData) rd));
	    registerAttachmentNames(cd, folder, entry, entry.getAttachments(), ((TrashRenameData) rd));
    }
    
    /*
     * If necessary, scans the Attachment's in aSet and registers the
     * filename from any FileAttachments in the Binder's namespace,
     * taking care of rename things as necessary. 
     */
    private static void registerAttachmentNames(CoreDao cd, Binder binder, DefinableEntity de, Set<Attachment> aSet, TrashRenameData rd) {
    	// If the Binder doesn't require unique filenames...
    	if (!(requiresUniqueFilenames(binder))) {
    		// ...we don't have to mess with any of the registration
    		// ...stuff.
    		return;
    	}
    	
    	// If we have Attachments...
    	if (null != aSet) {
    		// ...scan them...
		    for (Iterator<Attachment> aIT = aSet.iterator(); aIT.hasNext();) {
		    	// ...and for FileAttachment's...
		    	Attachment a = aIT.next();
		    	if (a instanceof FileAttachment) {
		    		// ...re-register their name.
		    		registerAttachmentName(cd, ((FileAttachment) a), binder, de, rd);
		    	}
		    }
    	}
    }
    
    /*
     * Registers the name for a FileAttachment taking care of any
     * renaming that must occur to ensure uniqueness. 
     */
    public static void registerAttachmentName(CoreDao cd, FileAttachment fa, Binder binder, DefinableEntity de, TrashRenameData rd) {
    	FileItem fi = fa.getFileItem();
		String fName = fi.getName();
		String fNameOriginal = fName;
		int renames = 0;
		do {
			// Is this filename registered?
			if (0 == renames) {
				logger.debug("TrashHelper.registerAttachmentName(\"" + fName + "\")");
			}
			logger.debug("...checking...");
			boolean fNameRegistered = cd.isFileNameRegistered(binder.getId(), fName);
			if (!fNameRegistered) {
				// No!  Is it the original filename?
				logger.debug("...name is unique...");
				if (0 == renames) {
					// Yes!  Re-register it and we're done.
					logger.debug("...re-registering original name.");
					cd.registerFileName(binder, de, fName);
					return;
				}
			
				// ...otherwise, we have a synthesized name that we
				// ...need to put into effect
				break;
			}

			// If we get here, fName conflicts with something in this
			// Binder's namespace.  Synthesize a new name and try
			// again.
			fName = synthesizeName(fNameOriginal, ("-" + String.valueOf(++renames)), true);
			logger.debug("...naming conflict detected, trying again using:  \"" + fName + "\"");
		} while (true);

		// If we get here, we've got a synthesized name for the file.
		// Rename it...
		logger.debug("...putting synthesized name into effect.");
		rd.m_bs.getFileModule().renameFile(binder, de, fa, fName);
		
		// ...and track what we renamed.
		rd.addRename(
			TrashRenameData.RenameType.File,
			fNameOriginal,
			fName);
    }
    
    /*
     * If necessary, registers the title of a DefinableEntity within a
     * Binder taking care of any renaming necessary.
     */
    @SuppressWarnings("unchecked")
	private static void registerTitle(CoreDao cd, Binder binder, DefinableEntity de, TrashRenameData rd) throws WriteEntryDataException, WriteFilesException {
    	// If the Binder doesn't require unique titles...
    	if (!(requiresUniqueTitles(binder))) {
    		// ...we don't have to mess with any of the registration
    		// ...stuff.
    		return;
    	}
    	
    	
		String deTitle_Original   = de.getTitle();
		String deTitle            = deTitle_Original;
		String deTitle_Normalized = de.getNormalTitle();
		int renames = 0;
		do {
			// Is deTitle unique to the Binder's namespace?
			if (0 == renames) {
				logger.debug("TrashHelper.registerTitle(\"" + deTitle + "\")");
			}
			logger.debug("...checking...");
			boolean titleRegistered = cd.isTitleRegistered(binder.getId(), deTitle_Normalized);
			if (!titleRegistered) {
				// Yes!  Is it the entity's original name? 
				logger.debug("...title is unique.");
				if (0 == renames) {
					// Yes!  Re-register it and we're done.
					logger.debug("...re-registering original title.");
					cd.registerTitle(binder, de);
					return;
				}
				
				// ...otherwise, we have a synthesized name that we
				// ...need to put it into effect.
				break;
			}

			// If we get here, deTitle conflicts with something in this
			// Binder's namespace.  Synthesize a new name and try
			// again.
			deTitle = synthesizeName(deTitle_Original, ("-" + String.valueOf(++renames)), false);
			deTitle_Normalized = WebHelper.getNormalizedTitle(deTitle);
			logger.debug("...naming conflict detected, trying again using:  \"" + deTitle + "\"");
		} while (true);

		// If we get here, we have a new title to use for the entity.
		// Rename it....
		logger.debug("...putting synthesized title into effect.");
		HashMap rdMap = new HashMap();
		rdMap.put(ObjectKeys.FIELD_ENTITY_TITLE, deTitle);
		MapInputData mid = new MapInputData(rdMap);
		HashMap fiMap = new HashMap();
		HashSet daSet = new HashSet();
		if (de instanceof Binder) {
			rd.m_bs.getBinderModule().modifyBinder(
				((Binder) de).getId(),
				mid,
				fiMap,	// fileItems.
				daSet,	// deleteAttachments.
				null);	// options.
		}
		else {
			rd.m_bs.getFolderModule().modifyEntry(
				binder.getId(),
				de.getId(),
				mid,
				fiMap,									// fileItems.
				daSet,									// deleteAttachments.
				new HashMap<FileAttachment, String>(),	// fileRenamesTo.
				null);									// options.
		}
		
		// ...and track what we renamed.
		rd.addRename(
			((de instanceof Binder)               ?
				TrashRenameData.RenameType.Binder :
				TrashRenameData.RenameType.Entry),
			deTitle_Original,
			deTitle);
    }

    /*
     * Returns true if the Binder requires filenames be unique within
     * its namespace and false otherwise. 
     */
    private static boolean requiresUniqueFilenames(Binder binder) {
    	return binder.isLibrary();
    }
    
    /*
     * Returns true if the Binder requires titles be unique within its
     * namespace and false otherwise. 
     */
    private static boolean requiresUniqueTitles(Binder binder) {
    	return binder.isUniqueTitles();
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
        
		response.setContentType("text/json");
		return new ModelAndView("common/json_ajax_return");
	}
	
	/*
	 * Called to restore a binder.
	 */
	private static void restoreBinder(AllModulesInjected bs, Long binderId, TrashResponse tr) {
		// Check the ACLs...
		TrashTraverser tt = new TrashTraverser(bs, logger, new TrashCheckRestoreACLs(), tr);
		tt.doTraverse(TraversalMode.ASCENDING, binderId);
		if (!(tr.isError())) {
			// ...and if they pass, perform the restore.
			tt.setCallback(new TrashRestore());
			tt.doTraverse(TraversalMode.ASCENDING, binderId);
		}
	}

	/*
	 * Called to restore an entry.
	 */
	private static void restoreEntry(AllModulesInjected bs, Long folderId, Long entryId, TrashResponse tr) {
		restoreEntry(bs, folderId, entryId, true, tr);
	}
	private static TrashResponse restoreEntry(AllModulesInjected bs, Long folderId, Long entryId, boolean restoreParentage, TrashResponse tr) {
		// Check the ACLs...
		TrashTraverser tt = new TrashTraverser(bs, logger, new TrashCheckRestoreACLs(), tr);
		tt.doTraverse(TraversalMode.ASCENDING, folderId, entryId);
		if (!(tr.isError())) {
			// ...and if they pass, perform the restore.
			tt.setCallback(new TrashRestore());
			tt.doTraverse(TraversalMode.ASCENDING, folderId, entryId);
		}
		
		return tr;
	}
	
	/*
	 * Called to restore the TrashEntry's in trashEntries. 
	 */
	private static ModelAndView restoreEntries(AllModulesInjected bs, TrashEntry[] trashEntries, RenderRequest request, RenderResponse response) {
		// Scan the TrashEntry's.
		int count = ((null == trashEntries) ? 0 : trashEntries.length);
		TrashResponse tr = new TrashResponse(bs);
		for (int i = 0; i < count; i += 1) {
			// Is this trashEntry valid and predeleted?
			TrashEntry trashEntry = trashEntries[i];
			if (trashEntry.isPreDeleted(bs)) {
				// Yes!  Is it an entry?
				if (trashEntry.isEntry()) {
					// Yes!  Restore the entry itself...
					restoreEntry(bs, trashEntry.m_locationBinderId, trashEntry.m_docId, tr);
					if (tr.isError()) {
						break;
					}
				}
				
				// No, it isn't an entry!  Is it a binder?
				else if (trashEntry.isBinder()) {
					// Yes!  Restore the binder itself...
					restoreBinder(bs, trashEntry.m_docId, tr);
					if (tr.isError()) {
						break;
					}
				}
			}
		}
		
		// Handle any messages based on the restore.
		return getMVBasedOnTrashResponse(response, bs, tr);
	}

	/*
	 * Synthesizes a new name for a binder, entry or file.
	 */
	private static String synthesizeName(String baseName, String patch, boolean fileName) {
		// Are we synthesizing a filename?
		String synthesizedName = baseName;
		if (fileName) {
			// Yes!  Does it contain a '.' separator for an extension?
			int dotPos = baseName.indexOf(".");
			if (0 > dotPos) {
				// No!  Handle it the same as non-filenames.
				fileName = false;
			}
			else {
				synthesizedName = (baseName.substring(0, dotPos) + patch + baseName.substring(dotPos));
			}
		}
		
		// If we're synthesizing a non-filename...
		if (!fileName) {
			// ...simply append the patch string.
			synthesizedName += patch;
		}
		
		// If we get here, synthesizedName refers to the synthesized
		// name.  Return it.
		return synthesizedName;
	}
	
	/**
     * Called to unregister the name of the binder and the filenames of
     * any file attachments on the binder.
     *  
	 * @param cd
	 * @param binder
	 */
    public static void unRegisterBinderNames(CoreDao cd, Binder binder) {
	    unRegisterTitle(          cd, binder.getParentBinder(), binder);
	    unRegisterAttachmentNames(cd, binder.getParentBinder(), binder.getAttachments());
    }
    
    /**
     * Called to unregister the name of the entry and the filenames of
     * any file attachments on the entry.
     *  
     * @param cd
     * @param folder
     * @param entry
     */
    public static void unRegisterEntryNames(CoreDao cd, Folder folder, FolderEntry entry) {
    	unRegisterTitle(          cd, folder, entry);
	    unRegisterAttachmentNames(cd, folder, entry.getAttachments());
    }
    
    /*
     * If necessary, scans the Attachment's in aSet and unregisters the
     * filename from any FileAttachments from the Binder's namespace. 
     */
    private static void unRegisterAttachmentNames(CoreDao cd, Binder binder, Set<Attachment> aSet) {
    	// If the Binder doesn't require unique filenames...
    	if (!(requiresUniqueFilenames(binder))) {
    		// ...we don't have to mess with any of the registration
    		// ...stuff.
    		return;
    	}
    	
    	// If we have Attachments...
    	if (null != aSet) {
    		// ...scan them...
		    for (Iterator<Attachment> aIT = aSet.iterator(); aIT.hasNext();) {
		    	// ...and for file attachments...
		    	Attachment a = aIT.next();
		    	if (a instanceof FileAttachment) {
		    		// ...unregister them.
		    		cd.unRegisterFileName(binder, ((FileAttachment) a).getFileItem().getName());
		    	}
		    }
    	}
    }

    /*
     * If necessary, unregisters the entry from a Binder's namespace.
     */
    private static void unRegisterTitle(CoreDao cd, Binder binder, DefinableEntity de) {
    	// If the Binder doesn't require unique titles...
    	if (!(requiresUniqueTitles(binder))) {
    		// ...we don't have to mess with any of the registration
    		// ...stuff.
    		return;
    	}

    	// Unregister the entity's title.
	    cd.unRegisterTitle(binder, de.getNormalTitle());
    }
}
