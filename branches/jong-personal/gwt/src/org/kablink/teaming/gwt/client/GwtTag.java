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
package org.kablink.teaming.gwt.client;


import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Class used in GWT RPC calls to represent a tag.
 * 
 * @author drfoster@novell.com
 */
public class GwtTag extends GwtTeamingItem implements IsSerializable {
	private String m_tagName;
	
	/**
	 * Constructor method. 
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public GwtTag() {
		// Nothing to do.
	}
		
	/**
	 * 
	 */
	@Override
	public String getImageUrl()
	{
		return "";
	}
	
	/**
	 * Returns the tag name.
	 */
	public String getTagName() {
		return m_tagName;
	}
	
	/**
	 * 
	 */
	@Override
	public String getName()
	{
		return getTagName();
	}
	
	/**
	 * Return the secondary display name of the tag.
	 * 
	 * Implements the GwtTeamingItem.getSecondaryDisplayText() abstract
	 * method.
	 */
	@Override
	public String getSecondaryDisplayText() {
		return "";
	}
		
	/**
	 * Return the name that should be displayed when this tag is
	 * displayed.
	 * 
	 * Implements the GwtTeamingItem.getShortDisplayName() abstract
	 * method.
	 */
	@Override
	public String getShortDisplayName() {
		return getTagName();
	}

	/**
	 * 
	 */
	@Override
	public String getTitle()
	{
		return "";
	}
	
	/**
	 * Stores the name for this tag.
	 * 
	 * @param tagName
	 */
	public void setTagName(String tagName) {
		m_tagName = tagName;
	}
}
