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

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Class used to communicate information about a top ranked item
 * between the client (i.e., the MainMenuControl) and the server (i.e.,
 * GwtRpcServiceImpl.getTopRanked().)
 * 
 * @author drfoster@novell.com
 *
 */
public class TopRankedInfo implements IsSerializable {
	/**
	 * Enumeration used to communicate the type of a top ranked item
	 * between the client and the server as part of a GWT RPC request.
	 *
	 */
	public enum TopRankedType implements IsSerializable {
		PERSON,
		PLACE,
		
		UNKNOWN,
	}

	private int           m_topRankedRefCount;						//
	private String        m_topRankedCSS;							//
	private String        m_topRankedHoverText;						//
	private String        m_topRankedName;							//
	private String        m_topRankedPermalinkUrl;					//
	private TopRankedType m_topRankedType = TopRankedType.UNKNOWN;	//
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public TopRankedInfo() {
		// Nothing to do.
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public int           getTopRankedRefCount()     {return m_topRankedRefCount;    }
	public String        getTopRankedCSS()          {return m_topRankedCSS;         }
	public String        getTopRankedHoverText()    {return m_topRankedHoverText;   }
	public String        getTopRankedName()         {return m_topRankedName;        }
	public String        getTopRankedPermalinkUrl() {return m_topRankedPermalinkUrl;}
	public TopRankedType getTopRankedType()         {return m_topRankedType;        }
	
	/**
	 * Returns true of this defines a top ranked person and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean isPerson() {
		return (TopRankedType.PERSON == m_topRankedType);
	}

	/**
	 * Returns true of this defines a top ranked place and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean isPlace() {
		return (TopRankedType.PLACE == m_topRankedType);
	}

	/**
	 * Stores the CSS, if any, of a top ranked item.
	 * 
	 * @param topRankedCSS
	 */
	public void setTopRankedCSS(String topRankedCSS) {
		m_topRankedCSS = topRankedCSS;
	}

	/**
	 * Stores the hover text, if any, of a top ranked item.
	 * 
	 * @param topRankedHoverText
	 */
	public void setTopRankedHoverText(String topRankedHoverText) {
		m_topRankedHoverText = topRankedHoverText;
	}

	/**
	 * Stores the name of a top ranked item.
	 * 
	 * @param topRankedName
	 */
	public void setTopRankedName(String topRankedName) {
		m_topRankedName = topRankedName;
	}

	/**
	 * Stores the permalink URL, if any, of a top ranked item.
	 * 
	 * @param topRankedPermalinkUrl
	 */
	public void setTopRankedPermalinkUrl(String topRankedPermalinkUrl) {
		m_topRankedPermalinkUrl = topRankedPermalinkUrl;
	}

	/**
	 * Stores the reference count to this top ranked item.
	 * 
	 * @param topRankedRefCount
	 */
	public void setTopRankedRefCount(int topRankedRefCount) {
		m_topRankedRefCount = topRankedRefCount;
	}
	
	/**
	 * Stores the type of the top ranked item.
	 * 
	 * @param topRankedType
	 */
	public void setTopRankedType(TopRankedType topRankedType) {
		m_topRankedType = topRankedType;
	}
}
