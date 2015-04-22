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

package org.kablink.teaming.gwt.client.binderviews.folderdata;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to encapsulate information about a guest book signer
 * in a data table.
 * 
 * @author drfoster@novell.com
 */
public class GuestInfo implements IsSerializable {
	public Long   m_userId;		//
	public String m_avatarUrl;	//
	public String m_ema;		//
	public String m_mobileEMA;	//
	public String m_phone;		//
	public String m_profileUrl;	//
	public String m_textEMA;	//
	public String m_title;		//
	
	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor as per GWT serialization
	 * requirements.
	 */
	public GuestInfo() {
		// Initialize the super class.
		super();
	}

	/**
	 * Constructor method.
	 * 
	 * @param userId
	 * @param title
	 * @param profileUrl
	 */
	public GuestInfo(Long userId, String title, String profileUrl) {
		// Initialize this object...
		this();

		// ...and store the parameters.
		setUserId(    userId    );
		setProfileUrl(profileUrl);
		setTitle(     title     );
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public Long   getUserId()             {return m_userId;    }
	public String getAvatarUrl()          {return m_avatarUrl; }
	public String getEmailAddress()       {return m_ema;       }
	public String getMobileEmailAddress() {return m_mobileEMA; }
	public String getPhone()              {return m_phone;     }
	public String getProfileUrl()         {return m_profileUrl;}
	public String getTextEmailAddress()   {return m_textEMA;   }
	public String getTitle()              {return m_title;     }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setUserId(            Long   userId)     {m_userId     = userId;    }
	public void setAvatarUrl(         String avatarUrl)  {m_avatarUrl  = avatarUrl; }
	public void setEmailAddress(      String ema)        {m_ema        = ema;       }
	public void setMobileEmailAddress(String mobileEMA)  {m_mobileEMA  = mobileEMA; }
	public void setPhone(             String phone)      {m_phone      = phone;     }
	public void setProfileUrl(        String profileUrl) {m_profileUrl = profileUrl;}
	public void setTextEmailAddress(  String textEMA)    {m_textEMA    = textEMA;   }
	public void setTitle(             String title)      {m_title      = title;     }
}
