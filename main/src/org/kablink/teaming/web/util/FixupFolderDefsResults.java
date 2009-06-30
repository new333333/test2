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

import java.util.ArrayList;



/**
 * This class is used to keep track of the results of fixing up folder
 * definitions.
 * 
 * @author drfoster@novell.com
 */
public class FixupFolderDefsResults {
	public enum FixupStatus {
		STATUS_ABORTED_BY_ERROR,
		STATUS_COLLECT_RESULTS,
		STATUS_COMPLETED,
		STATUS_READY,
		STATUS_STOP_COLLECTING_RESULTS,
	}
	
	// Define the maximum number of results we can collect for each area.
	public static final int	MAX_RESULTS = 400;
	
	private FixupStatus m_status;
	private PartialFixupFolderDefsResults m_foldersFixed;
	private PartialFixupFolderDefsResults m_entriesFixed;
	private String m_errorDesc;
	private String m_id;
	

	/**
	 * Class constructor
	 */
    public FixupFolderDefsResults(String id, FixupStatus initialStatus) {
    	super();
    	
    	m_id        = id;
    	m_status    = initialStatus;
    	m_errorDesc = null;
    	
    	m_foldersFixed = new PartialFixupFolderDefsResults();
    	m_entriesFixed = new PartialFixupFolderDefsResults();
    }
    
    
    /**
     * Clear all fixup results we have collected so far.
     */
    public void clearResults() {
    	m_foldersFixed.clearResults();
    	m_entriesFixed.clearResults();
    }
    
    
    /**
     * Calling this method will stop all further collection of fixup
     * results.
     */
    public void completed() {
    	m_status = FixupStatus.STATUS_COMPLETED;
    	
    }
    
    
    /**
     * This method is used to record that an error happened during the
     * fixup.
     */
    public void error(String errorDesc) {
    	m_status = FixupStatus.STATUS_ABORTED_BY_ERROR;
    	m_errorDesc = errorDesc;
    }
    
    
    /**
     * Return the object that holds the list of folders whose entry
     * definitions have been fixed. 
     */
    public PartialFixupFolderDefsResults getFoldersFixed() {
    	return m_foldersFixed;
    }


    /**
     * Return the description of the error that happened.
     */
    public String getErrorDesc() {
    	return m_errorDesc;
    }
    
    
    /**
     * Return the id of the object.
     */
    public String getId() {
    	return m_id;
    }
    
    
    /**
     * Return the object that holds the list of entries whose entry
     * definition has been fixed. 
     */
    public PartialFixupFolderDefsResults getEntriesFixed() {
    	return m_entriesFixed;
    }
    

    /**
     * Return the status.
     */
    public FixupStatus getStatus() {
    	return m_status;
    }
    

    /**
     * Calling this method will start collection of fixup
     * results.
     */
    public void collectResults() {
    	m_status = FixupStatus.STATUS_COLLECT_RESULTS;
    }
    
    
    
    /**
     * Calling this method will stop all further collection of fixup
     * results.
     */
    public void stopCollectingResults() {
    	m_status = FixupStatus.STATUS_STOP_COLLECTING_RESULTS;
    	
    	// Clear all fixup results we have collected so far.
    	clearResults();
    }
    
    
    
    /**
     * This class is used to collect partial fixup results.
     */
    public class PartialFixupFolderDefsResults {
    	private ArrayList<Long>	m_results;
    	
    	/**
    	 * Class constructor.
    	 */
    	public PartialFixupFolderDefsResults() {
    		m_results = new ArrayList<Long>();
    	}
    	
    	
    	/**
    	 * Add a result to our list.
    	 */
    	public boolean addResult(Long fixupId) {
    		// Should we collect this result?
    		if (getStatus() == FixupStatus.STATUS_COLLECT_RESULTS) {
    			// Yes!  Have we reached the max results we want to store?
    			if (m_results.size() < MAX_RESULTS) {
    				// No!  Store it.
        			m_results.add(fixupId);
        			return true;
    			}
    		}
    		
    		return false;
    	}
    	
    	
    	/**
    	 * Clear any results we may have collected so far.
    	 */
    	public void clearResults() {
    		m_results.clear();
    	}
    	
    	
    	/**
    	 * Return the list of results we have collected so far.
    	 */
    	public ArrayList<Long> getResults() {
    		return new ArrayList<Long>(m_results);
    	}
    }
}
