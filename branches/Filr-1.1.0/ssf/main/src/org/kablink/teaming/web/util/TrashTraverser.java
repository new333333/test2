/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.util.AllModulesInjected;

/**
 * Helper class used to traverse binders and entries while processing
 * the trash.
 * 
 * @author drfoster@novell.com
 */
public class TrashTraverser {
	// Class data members.
	private AllModulesInjected			m_bs;
	private ArrayList<DefinableEntity>	m_additionalTraversalsAL;
	private Log							m_logger;
	private Object						m_cbData;
	private TraverseCallback			m_cb;
	
	private final static boolean TRAVERSE_MIRRORED_BINDERS	= true;

	/**
	 * Specifies how the traversal will be done.
	 */
	public enum TraversalMode {
		ASCENDING,	// Typically used to restore.
		DESCENDING,	// Typically used to preDelete.
	}

	/**
	 * Interface used to callback into the code requesting the trash
	 * traversal as items are traversed.
	 * 
	 * Traversal of trash items will continue until a call to one of
	 * these returns false or the traversal completes.
	 */
	public interface TraverseCallback {
		public boolean binder(AllModulesInjected bs, Long binderId,               Object cbDataObject);
		public boolean entry( AllModulesInjected bs, Long folderId, Long entryId, Object cbDataObject);
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param bs
	 * @param cb
	 * @param cbData
	 */
	public TrashTraverser(AllModulesInjected bs, Log logger, TraverseCallback cb, Object cbData) {
		// Store the parameters.
		m_bs     = bs;
		m_logger = logger;
		m_cb     = cb;
		m_cbData = cbData;
		
		// ...and initialize everything else.
		resetAdditionalTraversalsAL();
	}

	/**
	 * Called to run the traversal.
	 * 
	 * @param mode
	 * @param binderId
	 * @param entryId
	 */
	public void doTraverse(TraversalMode mode, Long binderId) {
		// Always use the final form of the method.
		doTraverse(mode, binderId, null);
	}
	
	public void doTraverse(TraversalMode mode, Long binderId, Long entryId) {
		if (null == entryId) m_logger.debug("TrashTraverser.doTraverse(" + binderId + "):  Traversing binder.");
		else                 m_logger.debug("TrashTraverser.doTraverse(" + binderId + ", " + entryId + "):  Traversing entry.");
		
		if (mode == TraversalMode.DESCENDING) {
			m_logger.debug("...descending.");
			if (null == entryId) descendBinder(binderId         );
			else                 descendEntry( binderId, entryId);
		}
		else if (mode == TraversalMode.ASCENDING) {
			m_logger.debug("...ascending.");
			if (null == entryId) ascendBinder(binderId, true         );
			else                 ascendEntry( binderId, entryId, true);
		}
	}
	

	/*
	 * Recursively descends the binder identified by binderId.
	 */
	@SuppressWarnings("unchecked")
	private boolean descendBinder(Long binderId) {
		// Can we access the Binder as other than a mirrored binder?
		Binder binder = m_bs.getBinderModule().getBinder(binderId);
		if ((null == binder) || binder.isMirrored()) {
			// No!  Bail.
			return TRAVERSE_MIRRORED_BINDERS;
		}

		// If the Binder is a Folder or Workspace...
		boolean isFolder    = TrashHelper.isBinderFolder(   binder);
		boolean isWorkspace = TrashHelper.isBinderWorkspace(binder);
		if (isFolder || isWorkspace) {
			// ...descend its child binders...
			List<Binder>bindersList = binder.getBinders();
			for (Iterator bindersIT=bindersList.iterator(); bindersIT.hasNext();) {
				if (!(descendBinder(((Binder) bindersIT.next()).getId()))) {
					return false;
				}
			}

			// ...if the binder is a Folder...
			if (isFolder) {
				// ...scan its child entries...
				Map folderEntriesMap = m_bs.getFolderModule().getEntries(binderId, null);
				ArrayList folderEntriesAL = ((ArrayList) folderEntriesMap.get(ObjectKeys.SEARCH_ENTRIES));
		        for (Iterator folderEntriesIT=folderEntriesAL.iterator(); folderEntriesIT.hasNext();) {
					// ...descending them...
					Map folderEntryMap = ((Map) folderEntriesIT.next());
					if (!(descendEntry(binderId, Long.valueOf((String) folderEntryMap.get("_docId"))))) {
						return false;
					}
				}
			}
		
			// ...and handle the binder itself.
			if (!(m_cb.binder(m_bs, binderId, m_cbData))) {
				return false;
			}
		}
		
		// If we get here, we always continue the traversal.
		return true;
	}

	/*
	 * Called to descend an entry including its descendants.
	 */
	private boolean descendEntry(Long binderId, Long entryId) {
		// Can we access the Binder as other than a mirrored binder?
		FolderEntry entry = m_bs.getFolderModule().getEntry(binderId, entryId);
		Binder binder = entry.getParentBinder();
		if ((null == binder) || binder.isMirrored()) {
			// No!  Bail.
			return TRAVERSE_MIRRORED_BINDERS;
		}
		
		// Descend the entry's descendants (i.e., replies)...
		if (!(descendEntryDescendants(binderId, entryId))) {
			return false;
		}

		// ...and descend the entry itself.
		if (!(m_cb.entry(m_bs, binderId, entryId, m_cbData))) {
			return false;
		}
		
		// If we get here, we always continue the traversal.
		return true;
	}
	
	/*
	 * Called to descend the descendants (i.e., replies) of an entry.
	 * 
	 * Note that the descendants will include all replies, replies to
	 * replies, ...
	 */
	@SuppressWarnings("unchecked")
	private boolean descendEntryDescendants(Long folderId, Long entryId) {
		// Scan this entry's descendants...
		Map entryTreeMap = m_bs.getFolderModule().getEntryTree(folderId, entryId, true);
		List<FolderEntry> descendantsList = ((List<FolderEntry>) entryTreeMap.get(ObjectKeys.FOLDER_ENTRY_DESCENDANTS));
		for (Iterator descendantsIT=descendantsList.iterator(); descendantsIT.hasNext();) {
			// ...and handle them.
			FolderEntry descendantFE = ((FolderEntry) descendantsIT.next());
			if (!(m_cb.entry(m_bs, folderId, descendantFE.getId(), m_cbData))) {
				return false;
			}
		}
		
		// If we get here, we always continue the traversal.
		return true;
	}

	/**
	 * Given a Binder's ID, adds its parent Binder to the additional
	 * traversals list.
	 * 
	 * @param binderId
	 */
	public void addAdditionalBinderParentTraversal(Long binderId) {
		try {
			addAdditionalEntityParentTraversal(
				m_bs.getBinderModule().getBinder(binderId));
		}
		catch (Exception ex) {
			// Ignored.
		}
	}
	
	/**
	 * Given a DefinableEntity, adds its parent Binder to the
	 * additional traversals list.
	 * 
	 * @param de
	 */
	public void addAdditionalEntityParentTraversal(DefinableEntity de) {
		if (null != de) {
			addAdditionalTraversal(de.getParentBinder());
		}
	}
	
	/*
	 * Adds a DefinableEntity to the additional traversals ArrayList if
	 * it is not already there.
	 */
	private void addAdditionalTraversal(DefinableEntity de) {
		if ((null != de) && (!(m_additionalTraversalsAL.contains(de)))) {
			m_additionalTraversalsAL.add(de);
		}
	}

	/*
	 * Called to recursively ascend a binder.
	 */
	@SuppressWarnings("unchecked")
	private boolean ascendBinder(Long binderId, boolean topLevelBinder) {
		// If the Binder is a Folder or Workspace... 
		Binder binder = m_bs.getBinderModule().getBinder(binderId);
		boolean isFolder    = TrashHelper.isBinderFolder(   binder);
		boolean isWorkspace = TrashHelper.isBinderWorkspace(binder);
		if (isFolder || isWorkspace) {
			// ...if the binder is a Folder...
			if (isFolder) {
				// ...scan its child entries...
				Map folderEntriesMap = m_bs.getFolderModule().getEntries(binderId, null);
				ArrayList folderEntriesAL = ((ArrayList) folderEntriesMap.get(ObjectKeys.SEARCH_ENTRIES));
		        for (Iterator folderEntriesIT=folderEntriesAL.iterator(); folderEntriesIT.hasNext();) {
					// ...ascending them...
					Map folderEntryMap = ((Map) folderEntriesIT.next());
					if (!(ascendEntry(
						binderId,
						Long.valueOf((String) folderEntryMap.get("_docId")),
						false))) {	// false -> We're ascending a binder.
						return false;
					}
				}
			}
		}
		
		// ...handle the binder itself...
		if (!(m_cb.binder(m_bs, binderId, m_cbData))) {
			return false;
		}

		// ...and for the top level binder being deleted...
		if (topLevelBinder) {
			// ...ascend its predeleted parentage.
			if (!(ascendBindersPreDeletedParentage(binder.getParentBinder()))) {
				return false;
			}
		}
		
		// If we get here, we always continue the traversal.
		return true;
	}

	/*
	 * Recursively ascends the predeleted parentage of a binder.
	 */
	private boolean ascendBindersPreDeletedParentage(Binder binder) {
		// Is the binder a Folder or Workspace?
		boolean isFolder    = TrashHelper.isBinderFolder(   binder);
		boolean isWorkspace = TrashHelper.isBinderWorkspace(binder);
		if (isFolder || isWorkspace) {
			// Yes!  Has it been predeleted?
			boolean isPreDeleted;
			if (isFolder) isPreDeleted = ((Folder)    binder).isPreDeleted();
			else          isPreDeleted = ((Workspace) binder).isPreDeleted();
			if (isPreDeleted) {
				// Yes!  Handle it...
				addAdditionalTraversal(binder);
				if (!(m_cb.binder(m_bs, binder.getId(), m_cbData))) {
					return false;
				}
				
				// ...and ascend its parentage.
				if (!(ascendBindersPreDeletedParentage(binder.getParentBinder()))) {
					return false;
				}
			}
		}
		
		// If we get here, we always continue the traversal.
		return true;
	}

	/*
	 * Called to ascend an entry (ancestors, parentage, ...)
	 */
	private boolean ascendEntry(Long folderId, Long entryId, boolean traversingEntry) {
		// Ascend the entry's ancestors (for replies to replies)...
		FolderEntry entry = m_bs.getFolderModule().getEntry(folderId, entryId);
		Binder binder = entry.getParentBinder();
		if (!(ascendEntryAncestors(folderId, entryId, traversingEntry))) {
			return false;
		}
		
		// ...ascend the entry itself...
		if (!(m_cb.entry(m_bs, folderId, entryId, m_cbData))) {
			return false;
		}

		// ...and if we're traversing an entry...
		if (traversingEntry) {
			// ...ascend it's parentage.
			if (!(ascendBindersPreDeletedParentage(binder))) {
				return false;
			}
		}
		
		// If we get here, we always continue the traversal.
		return true;
	}
	
	/*
	 * Called to ascend the ancestors of an entry (for replies to replies.)
	 * 
	 * Note that the ancestors will include all ancestors of a given
	 * entry.
	 */
	@SuppressWarnings("unchecked")
	private boolean ascendEntryAncestors(Long folderId, Long entryId, boolean traversingEntry) {
		// Scan this entry's ancestors...
		Map entryTreeMap = m_bs.getFolderModule().getEntryTree(folderId, entryId, true);
		List<FolderEntry> ancestorsList = ((List<FolderEntry>) entryTreeMap.get(ObjectKeys.FOLDER_ENTRY_ANCESTORS));
		for (Iterator ancestorsIT=ancestorsList.iterator(); ancestorsIT.hasNext();) {
			// ...and ascend them.
			FolderEntry ancestorFE = ((FolderEntry) ancestorsIT.next());
			addAdditionalTraversal(ancestorFE);
			if (!(m_cb.entry(m_bs, folderId, ancestorFE.getId(), m_cbData))) {
				return false;
			}
		}
		
		// If we get here, we always continue the traversal.
		return true;
	}

	/**
	 * When performing an ascending traversal, we traverse a
	 * Binder/Entry's hierarchy/ancestry.  This returns an ArrayList
	 * of the additional DefinableEntity's that were traversed.
	 * 
	 * @return
	 */
	public ArrayList<DefinableEntity> getAdditionalTraversalsAL() {
		return m_additionalTraversalsAL;
	}

	/**
	 * Resets the addition indexing ArrayList.
	 */
	public void resetAdditionalTraversalsAL() {
		if (null == m_additionalTraversalsAL) m_additionalTraversalsAL = new ArrayList<DefinableEntity>();
		else                                  m_additionalTraversalsAL.clear();
	}
	
	/**
	 * Stores a new TraverseCallback in this TrashTraverser object.
	 * 
	 * @param cb
	 */
	public void setCallback(TraverseCallback cb) {
		m_cb = cb;
	}
	
	/**
	 * Stores a new callback data object in this TrashTraverser object.
	 * 
	 * @param cbData
	 */
	public void setCallbackData(Object cbData) {
		m_cbData = cbData;
	}
}
