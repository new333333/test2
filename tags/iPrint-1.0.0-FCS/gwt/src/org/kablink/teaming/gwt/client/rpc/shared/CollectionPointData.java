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
package org.kablink.teaming.gwt.client.rpc.shared;

import org.kablink.teaming.gwt.client.util.CollectionType;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds information about collection points.  At this time the only information
 * we have are the urls needed to invoke each collection point.
 * 
 * @author jwootton
 */
public class CollectionPointData
	implements IsSerializable, VibeRpcResponseData
{
	private String m_showMyFilesUrl = null;
	private String m_showNetFoldersUrl = null;
	private String m_showSharedByMeUrl = null;
	private String m_showSharedWithMeUrl = null;
	
	/**
	 * 
	 */
	public CollectionPointData()
	{
		super();
	}
	
	/**
	 * 
	 */
	public String getUrl( CollectionType collectionType )
	{
		// Remember the url we just retrieved
		switch ( collectionType )
		{
		case MY_FILES:
			return m_showMyFilesUrl;
			
		case NET_FOLDERS:
			return m_showNetFoldersUrl;
		
		case SHARED_BY_ME:
			return m_showSharedByMeUrl;
			
		case SHARED_WITH_ME:
			return m_showSharedWithMeUrl;
			
		default:
			return null;
		}
	}
	
	/**
	 * 
	 */
	public void setUrl( CollectionType collectionType, String url )
	{
		// Remember the url we just retrieved
		switch ( collectionType )
		{
		case MY_FILES:
			m_showMyFilesUrl = url;
			break;
			
		case NET_FOLDERS:
			m_showNetFoldersUrl = url;
			break;
		
		case SHARED_BY_ME:
			m_showSharedByMeUrl = url;
			break;
			
		case SHARED_WITH_ME:
			m_showSharedWithMeUrl = url;
			break;
		}
	}
}
