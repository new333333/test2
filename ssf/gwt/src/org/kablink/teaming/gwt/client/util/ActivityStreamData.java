/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Class used to communicate the data from an activity stream between
 * the activity stream control and the GWT RPC service methods.
 * 
 * @author drfoster@novell.com
 */
public class ActivityStreamData implements IsSerializable {
	private List<ActivityStreamEntry>	m_entries;		// List<ActivityStreamEntry> to display on the current page.
	private PagingData 					m_pagingData;	// The paging data based on where we're at in paging through the activity stream.
	
	/*
	 * Inner class used to manage paging through an activity
	 * stream.
	 */
	public static class PagingData implements IsSerializable{
		private int m_entriesPerPage;		//
		private int m_nextPageIndex;		//
		private int m_pageEnd;				//
		private int m_pageIndex;			//
		private int m_pageStart;			//
		private int m_previousPageIndex;	//
		private int m_totalPages;			//
		private int m_totalRecords;			//

		/*
		 * Class constructor.
		 * 
		 * No parameters as per GWT serialization requirements.
		 */
		public PagingData() {
			// Nothing to do.
		}

		/*
		 * Get'er methods.
		 */
		public int getEntriesPerPage()    {return m_entriesPerPage;   }
		public int getMaxHits()           {return m_entriesPerPage;   }
		public int getNextPageIndex()     {return m_nextPageIndex;    }
		public int getOffset()            {return m_pageStart;        }
		public int getPageIndex()         {return m_pageIndex;        }
		public int getPreviousPageIndex() {return m_previousPageIndex;}
		public int getTotalPages()        {return m_totalPages;       }
		public int getTotalRecords()      {return m_totalRecords;     }

		/**
		 * Initializes the paging data based on the activity stream
		 * parameters at page 0.
		 * 
		 * @param asp
		 */
		public void initializePaging(ActivityStreamParams asp) {
			m_entriesPerPage = asp.getEntriesPerPage();
			setTotalRecords(-1);
		}
		
		/**
		 * If valid, stores a new, 0 relative page index.  Always
		 * returns the page index set.
		 * 
		 * Note:  If the page index received is invalid, the return
		 *    value will NOT be the page index requested but the actual
		 *    page index being used.
		 * 
		 * @param pageIndex
		 * 
		 * @return
		 */
		public int setPageIndex(int pageIndex) {
			// If the page index is not valid...
			if ((pageIndex > (m_totalPages - 1)) || (pageIndex < 0)) {
				// Bail.
				return m_pageIndex;
			}
			
			// Otherwise, store it and update the next/previous page
			// indexes accordingly...
			m_pageIndex = pageIndex;
			updatePageNumbering();

			// ...and return the page index that we're at.
			return m_pageIndex;
		}

		/**
		 * Stores the total number of records and updates the paging
		 * information based on it.
		 * 
		 * @param totalRecords
		 */
		public void setTotalRecords(int totalRecords) {
			m_pageIndex    = 0;
			m_totalRecords = totalRecords;
			m_totalPages   = (m_totalRecords / m_entriesPerPage);
			if (0 < (m_totalRecords % m_entriesPerPage)) {
				m_totalPages += 1;
			}
			
			updatePageNumbering();
		}
		
		/*
		 * Updates the page start/end and next/previous page indexes.
		 */
		private void updatePageNumbering() {
			// If we don't have any records...
			if (0 >= m_totalRecords) {
				// ...we don't have a next/previous page.
				m_nextPageIndex     =
				m_previousPageIndex = (-1);
			}
			
			else {
				// Otherwise, update the next/previous page indexes
				// using the total records and page index. 
		      	if (m_totalRecords < m_pageStart) {
		      		if (m_pageIndex > 0) {
		      			m_previousPageIndex = (m_pageIndex - 1);
		      		}
		      	}
		      	
		      	else if (m_totalRecords >= m_pageEnd) {
		      		m_nextPageIndex = (m_pageIndex + 1);
		      		if (m_pageIndex > 0) {
		      			m_previousPageIndex = (m_pageIndex - 1);
		      		}
		      	}
		      	
		      	else {
		      		if (m_pageIndex > 0) {
		      			m_previousPageIndex = (m_pageIndex - 1);
		      		}
		      	}
			}

			// Finally, update the page start/end.
	      	m_pageStart = (m_pageIndex * m_entriesPerPage);
	      	m_pageEnd   = (m_pageStart + m_entriesPerPage);
		}
	}
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public ActivityStreamData() {
		m_entries    = new ArrayList<ActivityStreamEntry>();
		m_pagingData = new PagingData();
	}

	/**
	 * Returns a list of the activity stream entries from this activity
	 * stream data.
	 * 
	 * @return
	 */
	public List<ActivityStreamEntry> getEntries() {
		return m_entries;
	}

	/**
	 * Returns the count of activity stream entries stored in this
	 * activity stream data.
	 * 
	 * @return
	 */
	public int getEntryCount() {
		return ((null == m_entries) ? 0 : m_entries.size());
	}
	
	/**
	 * Returns the paging data from this activity stream data.
	 * 
	 * @return
	 */
	public PagingData getPagingData() {
		return m_pagingData;
	}

	/**
	 * Stores a list of activity stream entries into this activity
	 * stream data.
	 * 
	 * @param entries
	 */
	public void setEntries(List<ActivityStreamEntry> entries) {
		m_entries = ((null == entries) ? new ArrayList<ActivityStreamEntry>() : entries);
	}
	
	/**
	 * Stores paging data into this activity stream data.
	 * 
	 * @param pagingData
	 */
	public void setPagingData(PagingData pagingData) {
		m_pagingData = pagingData;
	}
}
