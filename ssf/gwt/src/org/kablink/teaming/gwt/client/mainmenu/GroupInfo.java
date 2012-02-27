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
package org.kablink.teaming.gwt.client.mainmenu;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Class used to communicate group information between the client
 * (i.e., the MainMenuControl) and the server (i.e.,
 * GwtRpcServiceImpl.getMyGroups().)
 * 
 * @author drfoster@novell.com
 *
 */
public class GroupInfo implements IsSerializable {
	private Long m_id;			// The group's id.
	private String m_title;			// The group's title.
	private String m_name;		// The group's name
	private String m_desc;		// The group's description
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public GroupInfo() {
		// Nothing to do.
	}

	/**
	 * 
	 */
	public String getDesc()
	{
		return m_desc;
	}
	
	/**
	 * 
	 */
	public void setDesc( String desc )
	{
		m_desc = desc;
	}
	
	/**
	 * Returns the group's id.
	 * 
	 * @return
	 */
	public Long getId() {
		return m_id;
	}

	/**
	 * Stores the id to the group.
	 *  
	 * @param id
	 */
	public void setId(Long id) {
		m_id = id;
	}
	
	/**
	 * 
	 */
	public String getName()
	{
		return m_name;
	}
	
	/**
	 * 
	 */
	public void setName( String name )
	{
		m_name = name;
	}
	
	/**
	 * Returns the group's title.
	 * 
	 * @return
	 */
	public String getTitle() {
		return m_title;
	}

	/**
	 * Stores the title to the group.
	 *  
	 * @param title
	 */
	public void setTitle(String title) {
		m_title = title;
	}
}
