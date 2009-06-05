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

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.web.util.FixupFolderDefsResults;
import org.kablink.teaming.web.util.WebHelper;

/**
 * This class is used to start a thread which will fixup folder and
 * entry definitions.
 * 
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class FixupFolderDefsThread extends Thread {
	// Define the default ID we'll use for the tread of we aren't given
	// one.
	private static final String DEFAULT_THREAD_ID	= "fixupFolderDefsThreadID_Default";
	
	private FixupFolderDefsResults	m_fixupFolderSyncResults;	// The results of the fixup will be stored here as it progresses.
	private PortletSession m_session = null;
	private String m_id;
	private AllModulesInjected m_bs;
	private Long m_binderId;
	private String m_binderType;
	private boolean m_folderFixups;
	private boolean m_entryFixups;
	private String m_entryDefinition;
	
	/**
	 * Create an FixupFolderDefsThread object.
	 */
	public static FixupFolderDefsThread createFixupFolderDefsThread(PortletRequest request, AllModulesInjected bs, Long binderId, String binderType, boolean folderFixups, boolean entryFixups, String entryDefinition) {
		return
			createFixupFolderDefsThread(
				DEFAULT_THREAD_ID,
				request,
				bs,
				binderId,
				binderType,
				folderFixups,
				entryFixups,
				entryDefinition);
	}
	public static FixupFolderDefsThread createFixupFolderDefsThread(String id, PortletRequest request, AllModulesInjected bs, Long binderId, String binderType, boolean folderFixups, boolean entryFixups, String entryDefinition) {
		FixupFolderDefsThread	fixupFolderDefsThread;
		PortletSession 	session;

		// If we can't access the session...
		session = WebHelper.getRequiredPortletSession(request);
		if (session == null) {
			// ...we can't allocate a FixupFolderDefsThread object.
			return null;
		}

		// Create and return the folder fixup thread.
		fixupFolderDefsThread = new FixupFolderDefsThread(
			id,
			session,
			bs,
			binderId,
			binderType,
			folderFixups,
			entryFixups,
			entryDefinition);
		fixupFolderDefsThread.setPriority(Thread.MIN_PRIORITY);
		session.setAttribute(id, fixupFolderDefsThread, PortletSession.APPLICATION_SCOPE);
		return fixupFolderDefsThread;
	}
	
	
	/**
	 * Return the FixupFolderDefsThread object with the given id.
	 */
	public static FixupFolderDefsThread getFixupFolderDefsThread(PortletRequest	request) {
		return getFixupFolderDefsThread(DEFAULT_THREAD_ID, request);
	}
	public static FixupFolderDefsThread getFixupFolderDefsThread(String id, PortletRequest request) {
		PortletSession session = request.getPortletSession(false);
		if (session == null) {
			return null;
		}
		return ((FixupFolderDefsThread) session.getAttribute(id, PortletSession.APPLICATION_SCOPE));
	}
	
	
	/**
	 * Return the FixupFolderDefsResults object for the given
	 * FixupFolderDefsThread id.
	 */
	public static FixupFolderDefsResults getFixupFolderDefsResults(PortletRequest request) {
		return getFixupFolderDefsResults(DEFAULT_THREAD_ID, request);
	}
	public static FixupFolderDefsResults getFixupFolderDefsResults(String id, PortletRequest request) {
		FixupFolderDefsThread fixupFolderDefsThread = getFixupFolderDefsThread(id, request);
		FixupFolderDefsResults fixupFolderDefsResults = null;
		if (fixupFolderDefsThread != null) {
			fixupFolderDefsResults = fixupFolderDefsThread.getFixupFolderDefsResults();
		}
		return fixupFolderDefsResults;
	}	
	
	
    /**
	 * Class constructor. (1 of 1)
	 */
	private FixupFolderDefsThread(PortletSession session, AllModulesInjected bs, Long binderId, String binderType, boolean folderFixups, boolean entryFixups, String entryDefinition) {
		this(
			DEFAULT_THREAD_ID,
			session,
			bs,
			binderId,
			binderType,
			folderFixups,
			entryFixups,
			entryDefinition);
	}
	private FixupFolderDefsThread(String id, PortletSession session, AllModulesInjected bs, Long binderId, String binderType, boolean folderFixups, boolean entryFixups, String entryDefinition) {
		// Initialize the super class...
		super(id);

		// ...store the parameters...
		m_id              = id;
		m_session         = session;
		m_bs              = bs;
		m_binderId        = binderId;
		m_binderType      = binderType;
		m_folderFixups    = folderFixups;
		m_entryFixups     = entryFixups;
		m_entryDefinition = entryDefinition;
		
		// ...and create a FixupFolderDefsResults object to hold the
		// ...results of the fixup.
		m_fixupFolderSyncResults = new FixupFolderDefsResults(id);
	}
	
	
	/**
	 * Execute the code that will perform the ldap sync.
	 */
	public void doFixups() {
		FixupFolderDefsResults fixupResults = getFixupFolderDefsResults();

		// Disable the schedule
		try {
			// Perform the fixups and then signal that they've
			// completed.
			doFixups_Impl();
			fixupResults.completed();
		}
		catch (Exception ex) {
			fixupResults.error(ex.getLocalizedMessage());
		}
	}
	
	
	/**
	 * Return the FixupFolderDefsResults object associated with this
	 * thread.
	 */
	public FixupFolderDefsResults getFixupFolderDefsResults() {
		return m_fixupFolderSyncResults;
	}


    /**
     * Remove this FixupFolderDefsThread object from the session.
     */
    public void removeFromSession() {
    	// Remove this object from the session object.
		if (m_session != null) {
    		m_session.removeAttribute(m_id);
		}
    }
    
    
    /**
     * Implement the Thread::run method.  Do the work of the folder
     * fixups.
     */
    public void run() {
    	doFixups();
    }
    
    
    /*
     */
    private void doFixups_Impl() {
//!		...this needs to be implemented...
    }
}
