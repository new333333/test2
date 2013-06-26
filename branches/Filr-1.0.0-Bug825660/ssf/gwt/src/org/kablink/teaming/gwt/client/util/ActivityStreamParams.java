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

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to communicate parameters maintained on the sever
 * regarding activity streams between between the ActivityStreamCtrl
 * and the GWT RPC service methods.
 * 
 * @author drfoster@novell.com
 */
public class ActivityStreamParams implements IsSerializable, VibeRpcResponseData {
	private ActivityStreamDataType	m_showSetting;				// What to show, show all or show unread.
	private boolean					m_activityStreamsOnLogin;	//
	private int     				m_activeComments;			//
	private int     				m_cacheRefresh;				//
	private int     				m_clientRefresh;			//
	private int						m_displayWords;				//
	private int     				m_entriesPerPage;			//
	private int     				m_lookback;					//
	private int     				m_maxHits;					//
	private int    					m_readEntryMax;				//
	private long    				m_readEntryDays;			//
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public ActivityStreamParams() {
		// Nothing to do.
	}

	/**
	 * Get'er methods.
	 */
	public ActivityStreamDataType getShowSetting()            {return m_showSetting;           }
	public boolean                getActivityStreamsOnLogin() {return m_activityStreamsOnLogin;}
	public int                    getActiveComments()         {return m_activeComments;        }
	public int                    getCacheRefresh()           {return m_cacheRefresh;          }
	public int                    getClientRefresh()          {return m_clientRefresh;         }
	public int                    getDisplayWords()           {return m_displayWords;          }
	public int                    getEntriesPerPage()         {return m_entriesPerPage;        }
	public int                    getLookback()               {return m_lookback;              }
	public int                    getMaxHits()                {return m_maxHits;               }
	public int                    getReadEntryMax()           {return m_readEntryMax;          }
	public long                   getReadEntryDays()          {return m_readEntryDays;         }
	
	/**
	 * Set'er methods.
	 */
	public void setShowSetting(           ActivityStreamDataType showSetting)            {m_showSetting            = showSetting;                 }
	public void setActivityStreamsOnLogin(boolean                activityStreamsOnLogin) {m_activityStreamsOnLogin = activityStreamsOnLogin;      }
	public void setActiveComments(        int                    activeComments)         {m_activeComments         = activeComments;              }
	public void setCacheRefresh(          int                    cacheRefresh)           {m_cacheRefresh           = cacheRefresh;                }
	public void setClientRefresh(         int                    clientRefresh)          {m_clientRefresh          = clientRefresh;               }
	public void setDisplayWords(          int                    displayWords)           {m_displayWords           = displayWords;                }
	public void setEntriesPerPage(        int                    entriesPerPage)         {m_entriesPerPage         = entriesPerPage;              }
	public void setLookback(              int                    lookback)               {m_lookback               = lookback;                    }
	public void setMaxHits(               int                    maxHits)                {m_maxHits                = maxHits;                     }
	public void setReadEntryMax(          int                    readEntryMax)           {m_readEntryMax           = readEntryMax;                }
	public void setReadEntryDays(         long                   readEntryDays)          {m_readEntryDays          = Math.min(readEntryDays, 30L);}	// See ObjectKeys.SEEN_TIMEOUT_DAYS.
	
	/**
	 * Creates a copy ActivityStreamParams with the base information
	 * from this ActivityStreamParams.
	 * 
	 * @return
	 */
	public ActivityStreamParams copyBaseASP() {
		ActivityStreamParams reply = new ActivityStreamParams();
		
		reply.setShowSetting(           getShowSetting()           );
		reply.setActivityStreamsOnLogin(getActivityStreamsOnLogin());
		reply.setActiveComments(        getActiveComments()        );
		reply.setCacheRefresh(          getCacheRefresh()          );
		reply.setClientRefresh(         getClientRefresh()         );
		reply.setDisplayWords(          getDisplayWords()          );
		reply.setEntriesPerPage(        getEntriesPerPage()        );
		reply.setLookback(              getLookback()              );
		reply.setMaxHits(               getMaxHits()               );
		reply.setReadEntryMax(          getReadEntryMax()          );
		reply.setReadEntryDays(         getReadEntryDays()         );
		
		return reply;
	}
}
