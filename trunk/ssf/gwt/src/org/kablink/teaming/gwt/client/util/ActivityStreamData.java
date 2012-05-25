/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
	private String						m_dateTime;		// The date/time string in the user's locale and time zone that this data was produced. 
	
	/**
	 * Inner class used to manage paging through an activity
	 * stream.
	 */
	public static class PagingData implements IsSerializable{
		private int m_entriesPerPage;	//
		private int m_pageIndex;		//
		private int m_totalPages;		//
		private int m_totalRecords;		//

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
		public int getEntriesPerPage() {return m_entriesPerPage;}
		public int getPageIndex()      {return m_pageIndex;     }
		public int getTotalPages()     {return m_totalPages;    }
		public int getTotalRecords()   {return m_totalRecords;  }

		/**
		 * Returns a copy of this PagingData object.
		 * 
		 * @return
		 */
		public PagingData copyBasePD() {
			PagingData reply = new PagingData();
			
			reply.m_entriesPerPage = m_entriesPerPage;
			reply.m_pageIndex      = m_pageIndex;
			reply.m_totalPages     = m_totalPages;
			reply.m_totalRecords   = m_totalRecords;
			
			return reply;
		}
		
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
			
			// Otherwise, store and return it.
			m_pageIndex = pageIndex;
			return m_pageIndex;
		}

		/**
		 * Stores the total number of records and updates the paging
		 * information based on it.
		 * 
		 * @param totalRecords
		 */
		public void setTotalRecords(int totalRecords) {
			m_totalRecords = totalRecords;
			m_totalPages   = (m_totalRecords / m_entriesPerPage);
			if (0 < (m_totalRecords % m_entriesPerPage)) {
				m_totalPages += 1;
			}
		}
	}
	
	/**
	 * Inner class used to expand the criteria used to search for
	 * activity stream data when the ActivityStream being searched is
	 * set to SPECIFIC_FOLDER.
	 * 
	 * The defaults supplied by a newly constructed object will return
	 * the values that have been historically returned by the
	 * GetActivityStreamDataCmd.
	 */
	public static class SpecificFolderData implements IsSerializable {
		private boolean m_applyFolderFilters         = false;			//
		private boolean	m_forcePlainTextDescriptions = false;			//
		private boolean m_returnComments             = true;			//
		private boolean m_sortDescending             = true;			//
		private String	m_quickFilter                = null;			//
		private String	m_sortKey                    = "_lastActivity";	// Default:  Constants.LASTACTIVITY_FIELD.
		private Long	m_creationStartTime			 = null;
		private Long	m_creationEndTime			 = null;

		/**
		 * Constructor method.
		 */
		public SpecificFolderData() {
			// Initialize the super class.
			super();
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean isApplyFolderFilters()         {return m_applyFolderFilters;        }
		public boolean isForcePlainTextDescriptions() {return m_forcePlainTextDescriptions;}
		public boolean isReturnComments()             {return m_returnComments;            }
		public boolean isSortDescending()             {return m_sortDescending;            }
		public Long	   getCreationEndTime()		  	  {return m_creationEndTime;		   }
		public Long	   getCreationStartTime()		  {return m_creationStartTime;		   }
		public String  getQuickFilter()               {return m_quickFilter;               }
		public String  getSortKey()                   {return m_sortKey;                   }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setApplyFolderFilters(        boolean applyFolderFilters        ) {m_applyFolderFilters         = applyFolderFilters;        }
		public void setCreationEndTime(	  	  	  Long creationEndTime				) {m_creationEndTime			= creationEndTime;			 }
		public void setCreationStartTime(		  Long creationStartTime			) {m_creationStartTime			= creationStartTime;		 }
		public void setForcePlainTextDescriptions(boolean forcePlainTextDescriptions) {m_forcePlainTextDescriptions = forcePlainTextDescriptions;}
		public void setReturnComments(            boolean returnComments            ) {m_returnComments             = returnComments;            }
		public void setQuickFilter(               String  quickFilter               ) {m_quickFilter                = quickFilter;               }
		public void setSortDescending(            boolean sortDescending            ) {m_sortDescending             = sortDescending;            }
	}
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public ActivityStreamData() {
		m_pagingData = new PagingData();
	}

	/**
	 * Returns the date/time string in the user's locale/time zone that
	 * this activity stream data was produced.
	 * 
	 * @return
	 */
	public String getDateTime() {
		return m_dateTime;
	}
	
	/**
	 * Returns a list of the activity stream entries from this activity
	 * stream data.
	 * 
	 * @return
	 */
	public List<ActivityStreamEntry> getEntries() {
		if (null == m_entries) {
			m_entries = new ArrayList<ActivityStreamEntry>();
		}
		return m_entries;
	}

	/**
	 * Returns the count of activity stream entries stored in this
	 * activity stream data.
	 * 
	 * @return
	 */
	public int getEntryCount() {
		return getEntries().size();
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
	 * Sets the date/time string in the user's locale/time zone that
	 * this activity stream data was produced.
	 *
	 * @param
	 * 
	 * @return
	 */
	public void setDateTime(String dateTime) {
		m_dateTime = dateTime;
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
