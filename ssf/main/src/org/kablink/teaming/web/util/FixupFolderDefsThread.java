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

import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.web.util.FixupFolderDefsResults;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.FixupFolderDefsResults.FixupStatus;

/**
 * This class is used to start a thread which will fixup folder and
 * entry definitions.
 * 
 * @author drfoster@novell.com
 */
public class FixupFolderDefsThread extends Thread {
	// Define a default ID to use for the thread if we aren't given
	// one.
	private static final String DEFAULT_THREAD_ID	= "fixupFolderDefsThreadID_Default";
	
	private BinderModule m_binderModule;
	private boolean m_entryFixups;
	private boolean m_folderFixups;
	private Definition m_entryDefinition;
	private FixupFolderDefsResults m_fixupResults;
	private FixupFolderDefsResults.PartialFixupFolderDefsResults m_entryResults;
	private FixupFolderDefsResults.PartialFixupFolderDefsResults m_folderResults;
	private FolderModule m_folderModule;
	private Long m_rootFolderId;
	private PortletSession m_session;
	private String m_threadId;
	
	/**
	 * Create an FixupFolderDefsThread object.
	 */
	public static FixupFolderDefsThread createFixupFolderDefsThread(PortletRequest request, AllModulesInjected bs, Long rootBinderId, boolean folderFixups, boolean entryFixups, String entryDefinition) {
		return
			createFixupFolderDefsThread(
				DEFAULT_THREAD_ID,
				request,
				bs,
				rootBinderId,
				folderFixups,
				entryFixups,
				entryDefinition);
	}
	public static FixupFolderDefsThread createFixupFolderDefsThread(String threadId, PortletRequest request, AllModulesInjected bs, Long rootBinderId, boolean folderFixups, boolean entryFixups, String entryDefinition) {
		FixupFolderDefsThread fixupFolderDefsThread;
		PortletSession session;

		// If we can't access the session...
		session = WebHelper.getRequiredPortletSession(request);
		if (null == session) {
			// ...we can't allocate a FixupFolderDefsThread object.
			return null;
		}

		// Create and return the folder fixup thread.
		fixupFolderDefsThread = new FixupFolderDefsThread(
			threadId,
			session,
			bs.getBinderModule(),
			bs.getFolderModule(),
			rootBinderId,
			folderFixups,
			entryFixups,
			entryDefinition);
		fixupFolderDefsThread.setPriority(Thread.MIN_PRIORITY);
		session.setAttribute(threadId, fixupFolderDefsThread, PortletSession.APPLICATION_SCOPE);
		return fixupFolderDefsThread;
	}
	
	
	/**
	 * Return the FixupFolderDefsThread object with a given id.
	 */
	public static FixupFolderDefsThread getFixupFolderDefsThread(PortletRequest	request) {
		return getFixupFolderDefsThread(DEFAULT_THREAD_ID, request);
	}
	public static FixupFolderDefsThread getFixupFolderDefsThread(String threadId, PortletRequest request) {
		PortletSession session = request.getPortletSession(false);
		if (null == session) {
			return null;
		}
		return ((FixupFolderDefsThread) session.getAttribute(threadId, PortletSession.APPLICATION_SCOPE));
	}
	
	
	/**
	 * Return the FixupFolderDefsResults object for a given id.
	 */
	public static FixupFolderDefsResults getFixupFolderDefsResults(PortletRequest request) {
		return getFixupFolderDefsResults(DEFAULT_THREAD_ID, request);
	}
	public static FixupFolderDefsResults getFixupFolderDefsResults(String threadId, PortletRequest request) {
		FixupFolderDefsThread fixupFolderDefsThread = getFixupFolderDefsThread(threadId, request);
		FixupFolderDefsResults fixupFolderDefsResults = null;
		if (null != fixupFolderDefsThread) {
			fixupFolderDefsResults = fixupFolderDefsThread.getFixupFolderDefsResults();
		}
		return fixupFolderDefsResults;
	}	
	
	
    /**
	 * Class constructor. (1 of 1)
	 */
	private FixupFolderDefsThread(PortletSession session, BinderModule binderModule, FolderModule folderModule, Long rootBinderId, boolean folderFixups, boolean entryFixups, String entryDefinition) {
		this(
			DEFAULT_THREAD_ID,
			session,
			binderModule,
			folderModule,
			rootBinderId,
			folderFixups,
			entryFixups,
			entryDefinition);
	}
	private FixupFolderDefsThread(String threadId, PortletSession session, BinderModule binderModule, FolderModule folderModule, Long rootBinderId, boolean folderFixups, boolean entryFixups, String entryDefinition) {
		// Initialize the super class...
		super(threadId);

		// ...store the parameters...
		m_threadId     = threadId;
		m_session      = session;
		m_binderModule = binderModule;
		m_folderModule = folderModule;
		m_rootFolderId = rootBinderId;
		m_folderFixups = folderFixups;
		m_entryFixups  = entryFixups;
		if (m_entryFixups) {
			m_entryDefinition = DefinitionHelper.getDefinition(entryDefinition);
		}
		
		// ...and create a FixupFolderDefsResults object to hold the
		// ...results of the fixup.
		m_fixupResults  = new FixupFolderDefsResults(threadId, FixupStatus.STATUS_READY);
		m_entryResults  = m_fixupResults.getEntriesFixed();
		m_folderResults = m_fixupResults.getFoldersFixed();
	}
	
	
	/**
	 * Return the FixupFolderDefsResults object associated with this
	 * thread.
	 */
	public FixupFolderDefsResults getFixupFolderDefsResults() {
		return m_fixupResults;
	}


	/**
	 * Returns true if the folder fixup is ready to be run or is
	 * running.
	 */
	public boolean isFolderFixupInProgress() {
		// Is this thread running?
		FixupFolderDefsResults.FixupStatus fixupFolderDefsStatus = m_fixupResults.getStatus();
		if ((FixupFolderDefsResults.FixupStatus.STATUS_COMPLETED != fixupFolderDefsStatus) &&
		    (FixupFolderDefsResults.FixupStatus.STATUS_ABORTED_BY_ERROR != fixupFolderDefsStatus)) {
			// Yes!  Return true.
			return true;
		}
		
		// The most recent fixup thread has completed.  Forget about
		// it.
		removeFromSession();
		return false;
	}
	
	
	/**
	 * Returns true if the folder fixup is ready to be run.
	 */
	public boolean isFolderFixupReady() {
		// Return true if this thread is ready to run.
		FixupFolderDefsResults.FixupStatus fixupFolderDefsStatus = m_fixupResults.getStatus();
		return (FixupFolderDefsResults.FixupStatus.STATUS_READY == fixupFolderDefsStatus);
	}
	
	
    /**
     * Remove this FixupFolderDefsThread object from the session.
     */
    public void removeFromSession() {
    	// Remove this object from the session object.
		if (null != m_session) {
    		m_session.removeAttribute(m_threadId);
		}
    }
    
    
    /**
     * Implement the Thread::run method.  Do the work of the folder
     * fixups.
     */
    public void run() {
		FixupFolderDefsResults fixupResults = getFixupFolderDefsResults();

		// Disable the schedule
		try {
			// Perform the fixups and then signal that they've
			// completed.
			fixupResults.collectResults();
			processFolder(m_rootFolderId);
			fixupResults.completed();
		}
		catch (Exception ex) {
			fixupResults.error(ex.getLocalizedMessage());
		}
    }
    
    
    /*
     */
    @SuppressWarnings("unchecked")
	private void processFolder(Long folderId) {
    	// Are we fixing the entries in this folder?
    	if (m_entryFixups) {
    		// Yes!  Scan the folder's entries...
			Map folderEntriesMap = m_folderModule.getFullEntries(folderId, null);
			List<FolderEntry> folderEntries = ((List) folderEntriesMap.get(ObjectKeys.FULL_ENTRIES));
			for (FolderEntry folderEntry:  folderEntries) {
				// ...setting the entry definition on each.
				folderEntry.setEntryDef(m_entryDefinition);
				m_entryResults.addResult(folderEntry.getId());
			}
    	}
    	
    	// Are we recursively fixing the nested folders in this folder?
    	if (m_folderFixups) {
    		// Yes!  Scan the nested folders.
    		Folder folderIn = m_folderModule.getFolder(folderId);
    		List<Folder> folderList = folderIn.getFolders();
    		for (Folder folder:  folderList) {
				// Yes!  Mark it has inheriting its parent's
    			// definitions...
    			folderId = folder.getId();
   				m_binderModule.setDefinitionsInherited(folderId, true);
   				m_folderResults.addResult(folderId);
    				
   				// ...and process its children recursively.
       			processFolder(folderId);
    		}
    	}
    }
}
