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

	private boolean m_entryFixups;
	private boolean m_folderFixups;
	private FixupFolderDefsResults m_fixupResults;
	private FixupFolderDefsResults.PartialFixupFolderDefsResults m_entryResults;
	private FixupFolderDefsResults.PartialFixupFolderDefsResults m_folderResults;
	private Long m_rootFolderId;
	private ModuleAccess m_modules;
	private PortletSession m_session;
	private String m_entryDef;
	private String m_threadId;

	
	/*
	 * Inner class used to encapsulate access the the modules used
	 * for performing the fixup.
	 */
	private static class ModuleAccess {
		public BinderModule m_binderModule;
		public FolderModule m_folderModule;
		
		public ModuleAccess(AllModulesInjected bs) {
			m_binderModule = bs.getBinderModule();
			m_folderModule = bs.getFolderModule();
		}
	}
	
	
    /**
	 * Class constructors.
	 */
	private FixupFolderDefsThread(PortletSession session, Long rootBinderId, boolean folderFixups, boolean entryFixups, String entryDef) {
		this(
			DEFAULT_THREAD_ID,
			session,
			rootBinderId,
			folderFixups,
			entryFixups,
			entryDef);
	}
	private FixupFolderDefsThread(String threadId, PortletSession session, Long rootBinderId, boolean folderFixups, boolean entryFixups, String entryDef) {
		// Initialize the super class...
		super(threadId);

		// ...store the parameters...
		m_threadId     = threadId;
		m_session      = session;
		m_rootFolderId = rootBinderId;
		m_folderFixups = folderFixups;
		m_entryFixups  = entryFixups;
		m_entryDef     = entryDef;
		
		// ...and create a FixupFolderDefsResults object to hold the
		// ...results of the fixup.
		m_fixupResults  = new FixupFolderDefsResults(threadId, FixupStatus.STATUS_READY);
		m_entryResults  = m_fixupResults.getEntriesFixed();
		m_folderResults = m_fixupResults.getFoldersFixed();
	}
	
	
	/**
	 * Create an FixupFolderDefsThread object.
	 */
	public static FixupFolderDefsThread createFixupFolderDefsThread(PortletRequest request, Long rootBinderId, boolean folderFixups, boolean entryFixups, String entryDef) {
		return
			createFixupFolderDefsThread(
				DEFAULT_THREAD_ID,
				request,
				rootBinderId,
				folderFixups,
				entryFixups,
				entryDef);
	}
	public static FixupFolderDefsThread createFixupFolderDefsThread(String threadId, PortletRequest request, Long rootBinderId, boolean folderFixups, boolean entryFixups, String entryDef) {
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
			rootBinderId,
			folderFixups,
			entryFixups,
			entryDef);
		fixupFolderDefsThread.setPriority(Thread.MIN_PRIORITY);
		session.setAttribute(threadId, fixupFolderDefsThread, PortletSession.APPLICATION_SCOPE);
		return fixupFolderDefsThread;
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
	 * Return the FixupFolderDefsResults object associated with this
	 * thread.
	 */
	public FixupFolderDefsResults getFixupFolderDefsResults() {
		return m_fixupResults;
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
	
	
    /*
     * Recursive method that actually runs the fixup process on a
     * folder.
     */
    @SuppressWarnings("unchecked")
	private void processFolder(Long folderId) {
    	// Are we fixing the entries in this folder?
    	if (m_entryFixups) {
    		// Yes!  Scan the folder's entries...
			Map folderEntriesMap = m_modules.m_folderModule.getFullEntries(folderId, null);
			List<FolderEntry> folderEntries = ((List) folderEntriesMap.get(ObjectKeys.FULL_ENTRIES));
			for (FolderEntry folderEntry:  folderEntries) {
				// ...setting the entry definition on each.
				Long entryId = folderEntry.getId();
				m_modules.m_folderModule.setEntryDef(folderId, entryId, m_entryDef);
				m_entryResults.addResult(entryId);
			}
    	}
    	
    	// Are we recursively fixing the nested folders in this folder?
    	if (m_folderFixups) {
    		// Yes!  Scan the nested folders.
    		Folder folderIn = m_modules.m_folderModule.getFolder(folderId);
    		List<Folder> folderList = folderIn.getFolders();
    		for (Folder folder:  folderList) {
				// Yes!  Mark it has inheriting its parent's
    			// definitions...
    			folderId = folder.getId();
    			m_modules.m_binderModule.setDefinitionsInherited(folderId, true);
   				m_folderResults.addResult(folderId);
    				
   				// ...and process its children recursively.
       			processFolder(folderId);
    		}
    	}
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
    
    
	/**
	 * Starts the fixup thread to perform the fixups.
	 * 
	 * Note:  Currently, doing the fixups on a separate thread does not
	 *    work.  When doing the work on a separate thread works, we
	 *    need to replace the call to run() with a call to start().
	 */
	public void startFixups(AllModulesInjected bs) {
		m_modules = new ModuleAccess(bs);
//!			start();
			run();
		m_modules = null;
	}
}
