/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
import java.util.List;
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
import org.kablink.teaming.domain.AuditTrail.AuditType;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FileItem;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.LibraryEntry;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.binder.processor.BinderProcessor;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.folder.processor.FolderCoreProcessor;
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
	 * Inner classes used to traverse trash items to check ACLs on
	 * them.
	 */
	private static class TrashCheckACLs implements TraverseCallback {
		// Class data members.
		private BinderOperation	m_binderOp;
		private FolderOperation	m_entryOp;
		
		/*
		 * Class construction.
		 */
		public TrashCheckACLs(BinderOperation binderOp, FolderOperation entryOp) {
			m_binderOp = binderOp;
			m_entryOp = entryOp;
		}
		
		/*
		 * Called to handle Binder's during the traversal.
		 */
		public boolean binder(AllModulesInjected bs, Long binderId, Object cbDataObject) {
			boolean reply = true;
			try {
				logger.debug("TrashCheckACLs.binder(" + m_binderOp + ":  " + binderId + "):  Checking binder ACLs.");
				Binder binder = bs.getBinderModule().getBinder(binderId);
				boolean opAllowed = true;
				if (isBinderPredeleted(binder)) {
					opAllowed = bs.getBinderModule().testAccess(binder, m_binderOp);
					if (!opAllowed) {
						logger.debug("...ACL violation!");
						((TrashResponse) cbDataObject).setACLViolation(binderId);
					}
				}
				reply = opAllowed;
			}
			catch (Exception ex) {
				logger.debug("...check failed!");
				((TrashResponse) cbDataObject).setException(ex, binderId);
				reply = false;
			}
			
			return reply;
		}
		
		/*
		 * Called to handle Entry's during the traversal.
		 */
		public boolean entry(AllModulesInjected bs, Long folderId, Long entryId, Object cbDataObject) {
			boolean reply = true;
			try {
				logger.debug("TrashCheckACLs.entry(" + m_entryOp + ":  " + folderId + ", " + entryId + "):  Checking entry ACLs.");
				FolderEntry fe = bs.getFolderModule().getEntry(folderId, entryId);
				boolean opAllowed = true;
				if (fe.isPreDeleted()) {
					opAllowed = bs.getFolderModule().testAccess(fe, m_entryOp);
					if (!opAllowed) {
						logger.debug("...ACL violation!");
						((TrashResponse) cbDataObject).setACLViolation(folderId, entryId);
					}
				}
				reply = opAllowed;
			}
			catch (Exception ex) {
				logger.debug("...check failed!");
				((TrashResponse) cbDataObject).setException(ex, folderId, entryId);
				reply = false;
			}
			
			return reply;
		}
	}
	
	/*
	 * Inner class used to manipulate entries in the trash.
	 */
	public static class TrashEntry {
		// Class data members.
		public Long		m_docId;
		public Long		m_locationBinderId;
		public String	m_docType;
		
		/*
		 * Constructs a TrashEntry based on the packed string
		 * representation of one.
		 */
		public TrashEntry(String paramS) {
			String[] params = paramS.split(StringPool.COLON);
			
			m_docId				= Long.valueOf(params[0]);
			m_locationBinderId	= Long.valueOf(params[1]);
			m_docType			=              params[2];
		}
		
		/*
		 * Constructs a TrashEntry based on an results of a search.
		 */
		@SuppressWarnings("unchecked")
		public TrashEntry(Map searchResultsMap) {
			m_docId     = Long.valueOf((String) searchResultsMap.get(Constants.DOCID_FIELD   ));
			m_docType   =             ((String) searchResultsMap.get(Constants.DOC_TYPE_FIELD));
			
			String binderId;
			if      (isEntry())  binderId = ((String) searchResultsMap.get(Constants.BINDER_ID_FIELD        ));
			else if (isBinder()) binderId = ((String) searchResultsMap.get(Constants.BINDERS_PARENT_ID_FIELD));
			else                 binderId = null;
			if (null != binderId) m_locationBinderId = Long.valueOf(binderId);
			else                  m_locationBinderId = null;
		}

		/*
		 * Returns true if the FolderEntry for this TrashEntry can
		 * still be accessed and false otherwise.
		 */
		public boolean exists(AllModulesInjected bs) {
			FolderEntry fe;
			try {
				fe = bs.getFolderModule().getEntry(m_locationBinderId, m_docId);
			}
			catch (Exception e) {
				fe = null;
			}
			return (null != fe);
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
	private static class TrashPreDelete implements TraverseCallback {
		/*
		 * Called to handle Binder's during the traversal.
		 */
		public boolean binder(AllModulesInjected bs, Long binderId, Object cbDataObject) {
			boolean reply;
			try {
				logger.debug("TrashPreDelete.binder(" + binderId + "):  Predeleting binder.");
				bs.getBinderModule().preDeleteBinder(binderId, RequestContextHolder.getRequestContext().getUser().getId(), false);
				changeBinder_Audit(bs, binderId, AuditType.preDelete);
				logger.debug("...predelete succeeded!");
				reply = true;
			}
			catch (Exception ex) {
				logger.debug("...predelete failed!");
				((TrashResponse) cbDataObject).setException(ex, binderId);
				reply = false;
			}
			
			return reply;
		}
		
		/*
		 * Called to handle Entry's during the traversal.
		 */
		public boolean entry(AllModulesInjected bs, Long folderId, Long entryId, Object cbDataObject) {
			boolean reply;
			try {
				logger.debug("TrashPreDelete.entry(" + folderId + ", " + entryId + "):  Predeleting entry.");
				bs.getFolderModule().preDeleteEntry(folderId, entryId, RequestContextHolder.getRequestContext().getUser().getId(), false);
				changeEntry_Audit(bs, folderId, entryId, AuditType.preDelete);
				logger.debug("...predelete succeeded!");
				reply = true;
			}
			catch (Exception ex) {
				logger.debug("...predelete failed!");
				((TrashResponse) cbDataObject).setException(ex, folderId, entryId);
				reply = false;
			}
			
			return reply;
		}
	}
	
	/*
	 * Inner class used to track binders as they get purged. 
	 */
	private static class TrashPurgedBinderTracker {
		// Class data members.
		private long[] m_purgedBinderIds;
		private int nextSlot = 0;
		
		// The following defines the initial size of m_purgedBinderIds
		// as well as how it grows when as the need arises.
		private final static int CHUNK_SIZE = 100;

		/*
		 * Class constructor.
		 */
		public TrashPurgedBinderTracker() {
			// Simply allocate the initial array from tracking purged
			// binder IDs.
			m_purgedBinderIds = new long[CHUNK_SIZE];
		}
		
		/*
		 * Returns true if the binderId is already being tracked and
		 * false otherwise.
		 */
		public boolean isBinderPurged(Long binderId) {
			// Always use the other form of the method.
			return isBinderPurged(binderId.longValue());
		}
		public boolean isBinderPurged(long binderId) {
			// Scan the binderId's that we're already tracking.
			for (int i = 0; i < nextSlot; i += 1) {
				// Is this the binderId in question?
				if (binderId == m_purgedBinderIds[i]) {
					// Yes!  Then we're already tracking it.
					return true;
				}
			}
			
			// If we get here, we're not tracking the binderId in
			// question.
			return false;
		}

		/*
		 * Tracks binderId, if it's not already being tracked.
		 */
		public void track(Long binderId) {
			// Always use the other form of the method.
			track(binderId.longValue());
		}
		public void track(long binderId) {
			// If we're not already tracking this binderId...
			if (!(isBinderPurged(binderId))) {
				// ...track it now.
				validateSpace();
				m_purgedBinderIds[nextSlot++] = binderId;
			}
		}
		
		/*
		 * Validate there's enough space in the tracker array for
		 * another entry. 
		 */
		private void validateSpace() {
			// Do we have enough space for another binder?
			int count = m_purgedBinderIds.length;
			if (nextSlot >= count) {
				// No!  Expand the array.
				long[] biggerArray = new long[count + CHUNK_SIZE];
				System.arraycopy(m_purgedBinderIds, 0, biggerArray, 0, count);
				m_purgedBinderIds = biggerArray;
			}
		}
	}
	
	/*
	 * Inner class used to assist/manage in the renaming of binders,
	 * entries and files that have naming conflicts during a restore. 
	 */
	public static class TrashRenameData {
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
			if (null == entryId) logger.debug("TrashResponse.setACLViolation(" + binderId +                  ")");
			else                 logger.debug("TrashResponse.setACLViolation(" + binderId + ", " + entryId + ")");
			
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
	 * Inner classes used to traverse trash items to restore them.
	 */
	private static class TrashRestore implements TraverseCallback {
		/*
		 * Called to handle Binder's during the traversal.
		 */
		public boolean binder(AllModulesInjected bs, Long binderId, Object cbDataObject) {
			TrashResponse tr = ((TrashResponse) cbDataObject);
			boolean reply;
			try {
				logger.debug("TrashRestore.binder(" + binderId + "):  Restoring binder.");
				bs.getBinderModule().restoreBinder(binderId, tr.m_rd, false);
				changeBinder_Audit(bs, binderId, AuditType.restore);
				logger.debug("...restore succeeded!");
				reply = true;
			}
			catch (Exception ex) {
				logger.debug("...restore failed!");
				tr.setException(ex, binderId);
				reply = false;
			}
			
			return reply;
		}
		
		/*
		 * Called to handle Entry's during the traversal.
		 */
		public boolean entry(AllModulesInjected bs, Long folderId, Long entryId, Object cbDataObject) {
			TrashResponse tr = ((TrashResponse) cbDataObject);
			boolean reply;
			try {
				logger.debug("TrashRestore.entry(" + folderId + ", " + entryId + "):  Restoring entry.");
				bs.getFolderModule().restoreEntry(folderId, entryId, tr.m_rd, false);
				changeEntry_Audit(bs, folderId, entryId, AuditType.restore);
				logger.debug("...restore succeeded!");
				reply = true;
			}
			catch (Exception ex) {
				logger.debug("...restore failed!");
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
	 * @param binder
	 * @param model
	 * @param qualifiers
	 * @param trashToolbar
	 */
	@SuppressWarnings("unchecked")
	public static void buildTrashToolbar(User user, Binder binder, Map model, Map qualifiers, Toolbar trashToolbar) {
		// If the user is allowed to access the trash...
		if (allowUserTrashAccess(user)) {
			// ...and if the Binder isn't mirrored...
			if (!(binder.isMirrored())) {
				// ...show the trash sidebar widget...
				model.put(WebKeys.TOOLBAR_TRASH_SHOW, Boolean.TRUE);
				
				// ...and add trash to the menu bar.
				qualifiers = new HashMap();
				qualifiers.put("title", NLT.get("toolbar.menu.title.trash"));
				qualifiers.put("icon", "trash.png");
				qualifiers.put("iconFloatRight", "true");
				qualifiers.put("onClick", "ss_treeShowId('"+ binder.getId() +"',this,'view_folder_listing','&showTrash=true');return false;");
				trashToolbar.addToolbarMenu("1_trash", NLT.get("toolbar.menu.trash"), "javascript: //;", qualifiers);
			}
		}
	}

	/**
	 * Returns true if the guest user is allowed trash access and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean allowGuestTrashAccess() {
		return SPropsUtil.getBoolean("trash.allowGuestAccess", false);
	}
	
	/**
	 * Returns true if user should have access to the trash and false
	 * otherwise.
	 * 
	 * @param user
	 * @return
	 */
	public static boolean allowUserTrashAccess(User user) {
		boolean reply;
		if (ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId()))
			 reply = allowGuestTrashAccess();
		else reply = true;
		return reply;
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
	 * Writes logging information about a binder change. 
	 */
	private static void changeBinder_Audit(AllModulesInjected bs, Long binderId, AuditType type) {
		bs.getReportModule().addAuditTrail(
			type,
			bs.getBinderModule().getBinder(
				binderId));
	}
	
	/**
	 * Writes logging information about a binder change. 
	 * 
	 * @param processor
	 * @param binder
	 * @param operation
	 */
	public static void changeBinder_Log(BinderProcessor processor, Binder binder, String operation) {
		processor.processChangeLog(binder, operation);
	}

	/*
	 * Writes logging information about an entry change. 
	 */
	private static void changeEntry_Audit(AllModulesInjected bs, Long folderId, Long entryId, AuditType type) {
		bs.getReportModule().addAuditTrail(
			type,
			bs.getFolderModule().getEntry(
				folderId,
				entryId));
	}

	/**
	 * Writes logging information about an entry change. 
	 * 
	 * @param processor
	 * @param de
	 * @param operation
	 */
	public static void changeEntry_Log(FolderCoreProcessor processor, DefinableEntity de, String operation) {
		processor.processChangeLog(de, operation);
	}
	
    /**
     * Returns true if binder contains any sub Binder's that are not
     * preDeleted and false otherwise.
     *
     * @param bs
     * @param binder
     * @return
     */
	@SuppressWarnings("unchecked")
	public static boolean containsVisibleBinders(AllModulesInjected bs, Binder binder) {
		// Does the Binder contain any sub binders?
		boolean reply = false;
    	if (0 < binder.getBinderCount()) {   	
			// Yes!  Search for the visible Binder's with this Binder
	    	// in their hierarchy (note that we search for 2 hits, one
    		// for this binder itself and one for any children)...
			Criteria crit = new Criteria();
			crit.add(in(Constants.DOC_TYPE_FIELD, new String[] {Constants.DOC_TYPE_BINDER}))
			    .add(in(Constants.ENTRY_ANCESTRY, new String[] {String.valueOf(binder.getId())}));

			Map visibleBindersMap = bs.getBinderModule().executeSearchQuery(
				crit,
				0,		// Start at index 0...
				2,		// ...requesting a maximum of 2 hits.
				false);	// false -> Ignore preDeleted entries.

			if (null != visibleBindersMap) {
				// ...and return true if we find any besides this binder.
				List visibleBindersList = ((List) visibleBindersMap.get(ObjectKeys.SEARCH_ENTRIES));
				reply = ((null != visibleBindersList) && (1 < visibleBindersList.size()));
			}
    	}
    	
    	// If we get here, reply is true if there are any visible
    	// Binders in binder and false otherwise.  Return it.
    	return reply;
	}
	
	/*
	 * Scans the Binder's and FolderEntry's in an ArrayList of
	 * DefinableEntity's and re-indexes them.
	 */
	private static void doAdditionalIndexing(AllModulesInjected bs, TrashResponse tr, ArrayList<DefinableEntity> indexAL) {
		// Scan the DefinableEntity's in the ArrayList...
		int count = ((null == indexAL) ? 0 : indexAL.size());
		for (int i = 0; i < count; i += 1) {
			DefinableEntity de = indexAL.get(i);
			Long deId = de.getId();
			try {
				// ...re-indexing each.
				if (de instanceof Binder) {
					// Note that we only re-index the Binder and not
					// its tree since this case is used to restore
					// the parentage of something.
					logger.debug("TrashHelper.doAdditionalIndexing(" + deId + "):  Re-indexing binder (binder only)");
					bs.getBinderModule().indexBinder(deId);
					refreshRssFeed(bs, ((Binder) de));
				}
				else {
					logger.debug("TrashHelper.doAdditionalIndexing(" + de.getParentBinder().getId() + ", " + deId + "):  Re-indexing entry");
					reindexEntry(  bs, ((FolderEntry) de));
					refreshRssFeed(bs, ((FolderEntry) de));
				}
			}
			catch (AccessControlException e) {
				// If we're not already tracking an error for this
				// operation...
				if (!(tr.isError())) {
					// ...track this one.
					if (de instanceof Binder) tr.setACLViolation(((FolderEntry) de).getParentBinder().getId(), deId);
					else                      tr.setACLViolation(deId);
				}
			}
		}
	}
	
	/*
	 * Returns a TrashEntry[] of all the items in the trash (non paged
	 * and non-sorted.)  Used to perform purge/restore alls.
	 */
	private static TrashEntry[] getAllTrashEntries(AllModulesInjected bs, RenderRequest request) {
		// Convert the current trash entries to an ArrayList of TrashEntry's...
		Long binderId = null;
		try {
			binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);				
		} catch(PortletRequestBindingException ex) {}
		return getAllTrashEntries(bs, binderId);
	}

	/**
	 * Returns a TrashEntry[] of all the items in the trash (non paged
	 * and non-sorted.)  Used to perform purge/restore alls.
	 * 
	 * @param bs
	 * @param binderId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static TrashEntry[] getAllTrashEntries(AllModulesInjected bs, Long binderId) {
		Binder binder = bs.getBinderModule().getBinder(binderId);
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_OFFSET,    Integer.valueOf(0));
		options.put(ObjectKeys.SEARCH_MAX_HITS,  (Integer.MAX_VALUE - 1));
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
	
	/*
	 * Returns the DefinableEntity corresponding to a LibraryEntry.
	 */
	private static DefinableEntity getEntityFromLE(LibraryEntry le, AllModulesInjected bs) {
		Long binderId = le.getBinderId();
		Long entityId = le.getEntityId();
		DefinableEntity reply;
		if (null == entityId) reply = bs.getBinderModule().getBinder(binderId);
		else                  reply = bs.getFolderModule().getEntry(null, entityId);
		return reply;
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
		// For the UI...
		if (null != response) {
			// ...we'll always return a JSON jsp.
			response.setContentType("text/json");
		}
		
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

	/**
	 * Returns true if binder is a Folder.
	 * 
	 * @param binder
	 * @return
	 */
	public static boolean isBinderFolder(Binder binder) {
		return (EntityType.folder == binder.getEntityType());
	}

	/**
	 * Returns true if binder is a predeleted folder or workspace and
	 * false otherwise. 
	 * 
	 * @param binder
	 * @return
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

	/**
	 * Returns true if binder is a Workspace.
	 * 
	 * @param binder
	 * @return
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
		TrashCheckACLs tca = new TrashCheckACLs(BinderOperation.preDeleteBinder, FolderOperation.preDeleteEntry);
		TrashTraverser tt = new TrashTraverser(bs, logger, tca, tr);
		tt.doTraverse(TraversalMode.DESCENDING, binderId);
		if (!(tr.isError())) {
			// ...and if they pass, perform the predelete.
			tt.setCallback(new TrashPreDelete());
			tt.doTraverse(TraversalMode.DESCENDING, binderId);

			// After predeleting a binder hierarchy, we need to
			// re-index the binder and everything below.
			try {
				logger.debug("TrashHelper.preDeleteBinder(" + binderId + "):  Re-indexing binder (binder tree)");
				bs.getBinderModule().indexTree(binderId);
				refreshRssFeed(bs, binderId);
			}
			catch (AccessControlException e) {
				if (!(tr.isError())) {
					tr.setACLViolation(binderId);
				}
			}
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
		TrashCheckACLs tca = new TrashCheckACLs(BinderOperation.preDeleteBinder, FolderOperation.preDeleteEntry);
		TrashTraverser tt = new TrashTraverser(bs, logger, tca, tr);
		tt.doTraverse(TraversalMode.DESCENDING, folderId, entryId);
		if (!(tr.isError())) {
			// ...and if they pass, perform the predelete.
			tt.setCallback(new TrashPreDelete());
			tt.doTraverse(TraversalMode.DESCENDING, folderId, entryId);
			
			// After predeleting an entry, we need to re-index it.
			try {
				logger.debug("TrashHelper.preDeleteEntry(" + folderId + "," + entryId + "):  Re-indexing entry");
				FolderEntry fe = bs.getFolderModule().getEntry(folderId, entryId);
				reindexEntry(  bs, fe);
				refreshRssFeed(bs, fe);
			}
			catch (AccessControlException e) {
				if (!(tr.isError())) {
					tr.setACLViolation(folderId, entryId);
				}
			}
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
		int count = ((null == trashEntries) ? 0 : trashEntries.length);
		int purgedBinderCount = 0;
		TrashResponse tr = new TrashResponse(bs);
		boolean purgeMirroredSources = PortletRequestUtils.getBooleanParameter(request, WebKeys.URL_PURGE_MIRRORED_SOURCES, false);
		TrashPurgedBinderTracker purgedBinders = new TrashPurgedBinderTracker();
		
		// Scan the TrashEntry's.
		logger.debug("TrashHelper.purgeEntries()");
		logger.debug("...checking ACLs...");
		for (int i = 0; i < count; i += 1) {
			// Is this trashEntry a FolderEntry?
			TrashEntry trashEntry = trashEntries[i];
			if (null == trashEntry) {
				continue;
			}
			if (trashEntry.isEntry()) {
				try {
					// Yes!  Is it predeleted?
					FolderEntry fe = bs.getFolderModule().getEntry(trashEntry.m_locationBinderId, trashEntry.m_docId);
					if (fe.isPreDeleted()) {
						// Yes!  Does this user have rights to purge it?
						logger.debug("......checking entry: " + trashEntry.m_locationBinderId + ", " + trashEntry.m_docId);
						if (!(bs.getFolderModule().testAccess(fe, FolderOperation.deleteEntry))) {
							// No!  Track the error.
							logger.debug(".........ACL violation!");
							tr.setACLViolation(trashEntry.m_locationBinderId, trashEntry.m_docId);
						}
					}
					else {
						trashEntries[i] = null;
					}
				}
				catch (Exception e) {
					// Something choked trying to do the ACL check.
					// Track the error.
					logger.debug(".........check failed!");
					if (e instanceof AccessControlException) tr.setACLViolation(trashEntry.m_docId);
					else                                     tr.setException(e, trashEntry.m_docId);
				}
			}
					
			// No, it's not a binder!  Is it an entry?
			else if (trashEntry.isBinder()) {
				try {
					// Yes!  Is it predeleted?
					Binder binder = bs.getBinderModule().getBinder(trashEntry.m_docId);
					if (isBinderPredeleted(binder)) {
						// Yes!  Does this user have rights to purge it?
						logger.debug("......checking binder:  " + trashEntry.m_docId);
						if (!(bs.getBinderModule().testAccess(binder, BinderOperation.deleteBinder))) {
							// No!  Track the error.
							logger.debug(".........ACL violation!");
							tr.setACLViolation(trashEntry.m_docId);
						}
					}
					else {
						trashEntries[i] = null;
					}
				}
				catch (Exception e) {
					// Something choked trying to do the ACL check.
					// Track the error.
					logger.debug(".........check failed!");
					if (e instanceof AccessControlException) tr.setACLViolation(trashEntry.m_docId);
					else                                     tr.setException(e, trashEntry.m_docId);
				}
			}
			
			// If we detect an error during the ACL check...
			if (tr.isError()) {
				// ...quit processing items.
				break;
			}
			logger.debug(".........entity is purgable.");
		}

		// Did we detect any ACL violations?
		if (!(tr.isError())) {
			// Scan the TrashEntry's again.
			logger.debug("...purging binders...");
			for (int i = 0; i < count; i += 1) {
				// Is this a binder?
				TrashEntry trashEntry = trashEntries[i];
				if (null == trashEntry) {
					continue;
				}
				if (trashEntry.isBinder()) {
					// Yes!  Was this binder purged because it was
					// contained in a binder that was already purged?
					if (purgedBinders.isBinderPurged(trashEntry.m_docId)) {
						// Yes!  Then we don't want to purge it again.
						// Skip it.
						logger.debug("......skipping " + trashEntry.m_docId + ", binder purged previously...");
						continue;
					}
					
					// Track this binder and it's descendants as having
					// been purged...
					trackPurgedBinders(bs, trashEntry.m_docId, purgedBinders);
					
					// ...and purge the binder.  Note that purging the
					// ...binder will purge its descendants too.
					try {
						// Note:  The true parameter in the call to
						//    BinderModule.deleteBinder() tells that
						//    method to only do the synchronous part of
						//    the delete.  The final part will be
						//    done below in the call to
						//    BinderModule.deleteBinderFinish().
						logger.debug("......purging binder:  " + trashEntry.m_docId + ", Mirrors too:  " + purgeMirroredSources);
						bs.getBinderModule().deleteBinder(trashEntry.m_docId, purgeMirroredSources, null, true);
						purgedBinderCount += 1;
						logger.debug(".........binder purged...");
					}
					catch (Exception e) {
						logger.debug(".........binder purge failed.", e);
						if (e instanceof AccessControlException) tr.setACLViolation(trashEntry.m_docId);
						else                                     tr.setException(e, trashEntry.m_docId);
					}
					
					// If we detect an error doing a purge...
					if (tr.isError()) {
						// ...quit processing items.
						break;
					}
				}
			}
		}

		// Have we detected any errors yet?
		if (!(tr.isError())) {
			// No!  Scan the TrashEntry's one more time.
			logger.debug("...purging entries...");
			for (int i = 0; i < count; i += 1) {
				// Is this an entry?
				TrashEntry trashEntry = trashEntries[i];
				if (null == trashEntry) {
					continue;
				}
				if (trashEntry.isEntry()) {
					// Yes!  Was this entry's binder purged above? 
					if (purgedBinders.isBinderPurged(trashEntry.m_locationBinderId)) {
						// Yes!  Then it will have been purged
						// automatically.  Skip it.
						logger.debug("......skipping " + trashEntry.m_locationBinderId + ", " + trashEntry.m_docId + ", binder purged previously...");
						continue;
					}
					
					// Purge the entry.  Note that purging an entry
					// will purge its replies.
					try {
						logger.debug("......purging entry:  " + trashEntry.m_locationBinderId + ", " + trashEntry.m_docId);
						if (trashEntry.exists(bs)) {
							bs.getFolderModule().deleteEntry(trashEntry.m_locationBinderId, trashEntry.m_docId);
							logger.debug(".........entry purged...");
						}
						else {
							logger.debug(".........entry no longer exists, purging skipped...");
						}
					}
					catch (Exception e) {
						logger.debug(".........entry purge failed.", e);
						if (e instanceof AccessControlException) tr.setACLViolation(trashEntry.m_locationBinderId, trashEntry.m_docId);
						else                                     tr.setException(e, trashEntry.m_locationBinderId, trashEntry.m_docId);
					}
					
					// If we detect an error doing a purge...
					if (tr.isError()) {
						// ...quit processing items.
						break;
					}
				}
			}
		}
		
		// If we purged any binders...
		if (0 < purgedBinderCount) {
			// ...finish the Binder delete operations.  We do this dead
			// ...last to avoid timing errors caused by this happening
			// ...asynchronously.  We were seeing various
			// ...LockAcquisitionException's and errors related to
			// ...deadlocks when trying to get a lock otherwise.
			bs.getBinderModule().deleteBinderFinish();
		}
		
		// Handle any messages based on the purge.
		return getMVBasedOnTrashResponse(response, bs, tr);
	}

	/*
	 * Updates the RSS feeds for a binder and/or entry.
	 */
	@SuppressWarnings("unused")
	private static void refreshRssFeed(AllModulesInjected bs, Long binderId, Long entryId) {
		refreshRssFeed(bs, binderId);
	}
	private static void refreshRssFeed(AllModulesInjected bs, Long binderId) {
		refreshRssFeed(bs, bs.getBinderModule().getBinder(binderId));
	}
	private static void refreshRssFeed(AllModulesInjected bs, FolderEntry fe) {
		refreshRssFeed(bs, fe.getParentBinder());
	}
	private static void refreshRssFeed(AllModulesInjected bs, Binder binder) {
		bs.getRssModule().deleteRssFeed(binder);
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
    	if (entry.isTop()) {
    		registerTitle(      cd, folder, entry,                         ((TrashRenameData) rd));
    	}
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

    /**
     * Registers the name for a FileAttachment taking care of any
     * renaming that must occur to ensure uniqueness. 
     * 
     * @param cd
     * @param fa
     * @param binder
     * @param de
     * @param rd
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
				logger.debug("......name is unique...");
				if (0 == renames) {
					// Yes!  Re-register it and we're done.
					logger.debug("......re-registering original name.");
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
			logger.debug("......naming conflict detected, trying again using:  \"" + fName + "\"");
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
    	// If don't require any type of unique name...
    	boolean uniqueTitles = requiresUniqueTitles(binder);
    	boolean uniqueFNames = (de instanceof Binder);
    	if ((!uniqueTitles) && (!uniqueFNames)) {
    		// ...we don't have to mess with any of the registration
    		// ...stuff.
    		return;
    	}
    	
    	// Keep track of any uses of the initial name(s).
		String deTitle = de.getTitle();
		String deTitle_Normalized = de.getNormalTitle();
		LibraryEntry registeredTitle = (uniqueTitles ? cd.getRegisteredTitle(   binder.getId(), deTitle_Normalized) : null);
		LibraryEntry registeredFName = (uniqueFNames ? cd.getRegisteredFileName(binder.getId(), deTitle           ) : null);

		String deTitle_Original = deTitle;
		int renames = 0;
		do {
			// Is deTitle unique to the Binder's namespace?
			if (0 == renames) {
				logger.debug("TrashHelper.registerTitle(\"" + deTitle + "\")");
			}
			logger.debug("...checking...");
			boolean titleRegistered = (uniqueTitles && cd.isTitleRegistered(   binder.getId(), deTitle_Normalized));
			boolean fNameRegistered = (uniqueFNames && cd.isFileNameRegistered(binder.getId(), deTitle           ));
			if ((!titleRegistered) && (!fNameRegistered)) {
				// Yes!  Is it the entity's original name? 
				logger.debug("......title is unique.");
				if (0 == renames) {
					// Yes!  Re-register it and we're done.
					logger.debug("......re-registering original title.");
					if (uniqueTitles) cd.registerTitle(   binder, de         );
					if (uniqueFNames) cd.registerFileName(binder, de, deTitle);
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
			logger.debug("......naming conflict detected, trying again using:  \"" + deTitle + "\"");
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

		// If we had anything using the original names, we need to put
		// them back into effect as the modifies above will have
		// unregistered them.
		if (null != registeredTitle) cd.addExistingName(registeredTitle, getEntityFromLE(registeredTitle, rd.m_bs));
		if (null != registeredFName) cd.addExistingName(registeredFName, getEntityFromLE(registeredFName, rd.m_bs));
		
		// ...and track what we renamed.
		rd.addRename(
			((de instanceof Binder)               ?
				TrashRenameData.RenameType.Binder :
				TrashRenameData.RenameType.Entry),
			deTitle_Original,
			deTitle);
    }

    /*
     * Re-indexes a FolderEntry.  If the given FolderEntry is NOT a top
     * entry, the top entry is indexed instead.
     */
    private static void reindexEntry(AllModulesInjected bs, FolderEntry fe) {
    	FolderEntry feTop       = fe.getTopEntry();
    	FolderEntry feToReindex = ((null == feTop) ? fe : feTop);
		bs.getFolderModule().indexEntry(feToReindex, true);
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
		TrashCheckACLs tca = new TrashCheckACLs(BinderOperation.restoreBinder, FolderOperation.restoreEntry);
		TrashTraverser tt = new TrashTraverser(bs, logger, tca, tr);
		tt.doTraverse(TraversalMode.ASCENDING, binderId);
		if (!(tr.isError())) {
			// ...and if they pass, perform the restore.
			tt.resetAdditionalTraversalsAL();
			tt.setCallback(new TrashRestore());
			tt.doTraverse(TraversalMode.ASCENDING, binderId);
			
			// After restoring a binder hierarchy, we need to re-index
			// the binder and its children...
			try {
				logger.debug("TrashHelper.restoreBinder(" + binderId + "):  Re-indexing binder (binder tree)");
				bs.getBinderModule().indexTree(binderId);
				refreshRssFeed(bs, binderId);
			}
			catch (AccessControlException e) {
				if (!(tr.isError())) {
					tr.setACLViolation(binderId);
				}
			}
			
			// ...and any parent binders that had to get restored to
			// ...restore the binder.
			doAdditionalIndexing(bs, tr, tt.getAdditionalTraversalsAL());
		}
	}

	/*
	 * Called to restore an entry.
	 */
	private static TrashResponse restoreEntry(AllModulesInjected bs, Long folderId, Long entryId, TrashResponse tr) {
		// Check the ACLs...
		TrashCheckACLs tca = new TrashCheckACLs(BinderOperation.restoreBinder, FolderOperation.restoreEntry);
		TrashTraverser tt = new TrashTraverser(bs, logger, tca, tr);
		tt.doTraverse(TraversalMode.ASCENDING, folderId, entryId);
		if (!(tr.isError())) {
			// ...and if they pass, perform the restore.
			tt.resetAdditionalTraversalsAL();
			tt.setCallback(new TrashRestore());
			tt.doTraverse(TraversalMode.ASCENDING, folderId, entryId);
			
			// After restoring an entry, we need to re-index the
			// entry...
			try {
				logger.debug("TrashHelper.restoreEntry(" + folderId + ", " + entryId + "):  Re-indexing entry");
				FolderEntry fe = bs.getFolderModule().getEntry(folderId, entryId);
				reindexEntry(  bs, fe);
				refreshRssFeed(bs, fe);
			}
			catch (AccessControlException e) {
				if (!(tr.isError())) {
					tr.setACLViolation(folderId, entryId);
				}
			}
			
			// ...and any parent binders that had to get restored to
			// ...restore the entry.
			doAdditionalIndexing(bs, tr, tt.getAdditionalTraversalsAL());
		}
		
		return tr;
	}

	/**
	 * Called to restore the TrashEntry's in trashEntries. 
	 * 
	 * @param bs
	 * @param trashEntries
	 * @param request
	 * @param response
	 * @return
	 */
	public static ModelAndView restoreEntries(AllModulesInjected bs, TrashEntry trashEntry) {
		// Always use the final form of the method.
		return
			restoreEntries(
				bs,
				new TrashEntry[]{trashEntry},
				null,	// null -> No RenderRequest.   Used from web services.
				null);	// null -> No RenderResponse.  Used from web services.
	}
	public static ModelAndView restoreEntries(AllModulesInjected bs, TrashEntry[] trashEntries) {
		// Always use the final form of the method.
		return
			restoreEntries(
				bs,
				trashEntries,
				null,	// null -> No RenderRequest.   Used from web services.
				null);	// null -> No RenderResponse.  Used from web services.
	}
	public static ModelAndView restoreEntries(AllModulesInjected bs, TrashEntry trashEntry, RenderRequest request, RenderResponse response) {
		// Always use the final form of the method.
		return
			restoreEntries(
				bs,
				new TrashEntry[]{trashEntry},
				request,
				response);
	}
	public static ModelAndView restoreEntries(AllModulesInjected bs, TrashEntry[] trashEntries, RenderRequest request, RenderResponse response) {
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

	/*
	 * Tracks a Binder and its descendant Binder's in a Map.  This map
	 * is then used to avoid purging things that have already been
	 * purged because of a container being purged.
	 */
	@SuppressWarnings("unchecked")
	private static void trackPurgedBinders(AllModulesInjected bs, Long binderId, TrashPurgedBinderTracker purgedBinders) {
		// Are we already tracking this binder?
		if (!(purgedBinders.isBinderPurged(binderId))) {
			// No!  Track it now.
			purgedBinders.track(binderId);
			
			// Can we access this Binder as other than a mirrored
			// binder?
			Binder binder = bs.getBinderModule().getBinder(binderId);
			if ((null == binder) || binder.isMirrored()) {
				// No!  Then we're done with it.  Bail.
				return;
			}

			// Is the Binder a Folder or Workspace?
			boolean isFolder    = TrashHelper.isBinderFolder(   binder);
			boolean isWorkspace = TrashHelper.isBinderWorkspace(binder);
			if (isFolder || isWorkspace) {
				// Yes!  Descend its child binders...
				List bindersList = binder.getBinders();
				for (Iterator bindersIT=bindersList.iterator(); bindersIT.hasNext();) {
					// ...and track them.
					trackPurgedBinders(bs, ((Binder) bindersIT.next()).getId(), purgedBinders);
				}
			}
		}
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
    	if (entry.isTop()) {
    		unRegisterTitle(      cd, folder, entry);
    	}
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
		    		String fName = ((FileAttachment) a).getFileItem().getName();
		    		logger.debug("TrashHelper.unRegisterAttachmentNames(Unregistering filename:  \"" + fName + "\" from binder:  \"" + binder.getTitle() + "\")");
		    		cd.unRegisterFileName(binder, fName);
		    	}
		    }
    	}
    }

    /*
     * If necessary, unregisters the entry from a Binder's namespace.
     */
    private static void unRegisterTitle(CoreDao cd, Binder binder, DefinableEntity de) {
    	// If the Binder requires unique titles...
    	if (requiresUniqueTitles(binder)) {
        	// ...unregister the entity's title.
    		String normalizedTitle = de.getNormalTitle();
    		logger.debug("TrashHelper.unRegisterTitle(Unregistering title:  \"" + normalizedTitle + "\" from binder:  \"" + binder.getTitle() + "\")");
    	    cd.unRegisterTitle(binder, normalizedTitle);
    	}

	    // If the entity is a Binder...
	    if (de instanceof Binder) {
    		// ...unregister the title as a filename too.
	    	String title = de.getTitle();
    		logger.debug("TrashHelper.unRegisterTitle(Unregistering filename:  \"" + title + "\" from binder:  \"" + binder.getTitle() + "\")");
    		cd.unRegisterFileName(binder, title);
	    }
    }
}
