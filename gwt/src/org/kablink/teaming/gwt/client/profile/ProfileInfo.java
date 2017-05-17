/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.profile;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * ?
 * 
 * @author ?
 */
public class ProfileInfo implements IsSerializable, VibeRpcResponseData  {
	private boolean						m_canAccessUserWS;		//
	private boolean						m_conferencingEnabled;	//
	private boolean						m_hasUserWS;			//
	private boolean						m_pictureEnabled;		//
	private boolean						m_presenceEnabled;		//
	private ArrayList<ProfileCategory>	m_categories;			//
	private ArrayList<String>			m_pictureUrls;			//
	private ArrayList<String>			m_pictureScaledUrls;	//
	private String						m_title;				//
	private String						m_binderId;				//
	private String						m_userId;				//
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public ProfileInfo() {
		// Initialize the super class...
		super();
		
		// ...and initialize everything else.
		m_categories        = new ArrayList<ProfileCategory>();
		m_pictureUrls       = new ArrayList<String>();
		m_pictureScaledUrls = new ArrayList<String>();
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public ArrayList<ProfileCategory> getCategories()         {return m_categories;         }
	public List<String>               getPicutres()           {return m_pictureUrls;        }
	public List<String>               getPicutreScaleds()     {return m_pictureScaledUrls;  }
	public boolean                    canAccessUserWS()       {return m_canAccessUserWS;    }
	public boolean                    hasUserWS()             {return m_hasUserWS;          }
	public boolean                    isConferencingEnabled() {return m_conferencingEnabled;}
	public boolean                    isPictureEnabled()      {return m_pictureEnabled;     }
	public boolean                    isPresenceEnabled()     {return m_presenceEnabled;    }
	public String                     getBinderId()           {return m_binderId;           }
	public String                     getTitle()              {return m_title;              }
	public String                     getUserId()             {return m_userId;             }

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setCanAccessUserWS(    boolean canAccessUserWS) {m_canAccessUserWS     = canAccessUserWS;}
	public void setConferencingEnabled(boolean enabled)         {m_conferencingEnabled = enabled;        }
	public void setHasUserWS(          boolean hasUserWS)       {m_hasUserWS           = hasUserWS;      }
	public void setPictureEnabled(     boolean enabled)         {m_pictureEnabled      = enabled;        }
	public void setPresenceEnabled(    boolean enabled)         {m_presenceEnabled     = enabled;        }
	public void setBinderId(           String  binderId)        {m_binderId            = binderId;       }
	public void setTitle(              String  title)           {m_title               = title;          }
	public void setUserId(             String  userId)          {m_userId              = userId;         }

	/**
	 * Adds a ProfileCategory.
	 * 
	 * @param cat
	 */
	public void add(ProfileCategory cat) {
		m_categories.add(cat);
	}

	/**
	 * Adds a picture URL.
	 * 
	 * @param pictureUrl
	 */
	public void addPictureUrl(String pictureUrl) {
		m_pictureUrls.add(pictureUrl);
	}
	
	/**
	 * Adds a scaled picture URL.
	 * 
	 * @param pictureUrl
	 */
	public void addPictureScaledUrl(String pictureScaledUrl) {
		m_pictureScaledUrls.add(pictureScaledUrl);
	}
	
	/**
	 * Returns a ProfileCategory based on its name.
	 * 
	 * @param name
	 * 
	 * @return
	 */
	public ProfileCategory get(String name) {
		ProfileCategory category = null;
		for(ProfileCategory cat:  m_categories) {
			if (cat.getName().equals(name)) {
				category = cat;
				break;
			}
		}
		return category;
	}

	/**
	 * Returns a ProfileCategory based on its index.
	 * 
	 * @param index
	 * 
	 * @return
	 */
	public ProfileCategory get(int index) {
		return m_categories.get(index);
	}

	/**
	 * Returns the URL of the first picture.
	 * 
	 * @return
	 */
	public String getPictureUrl() {
		String pictureUrl = null;
		if (!(m_pictureUrls.isEmpty())) {
			pictureUrl = m_pictureUrls.get(0);
		};
		return pictureUrl;
	}

	/**
	 * Returns the URL of the first scaled picture.
	 * 
	 * @return
	 */
	public String getPictureScaledUrl() {
		String pictureScaledUrl = null;
		if (!(m_pictureScaledUrls.isEmpty())) {
			pictureScaledUrl = m_pictureScaledUrls.get(0);
		};
		return pictureScaledUrl;
	}
}
