/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds all of the information necessary to execute the
 * 'save multiple public collection settings' command.
 * 
 * @author drfoster@novell.com
 */
public class SaveMultiplePublicCollectionSettingsCmd extends VibeRpcCmd {
	private List<Long>	m_userIds;					//
	private Boolean		m_allowPublicCollection;	//
	
	/**
	 * Constructor method.
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public SaveMultiplePublicCollectionSettingsCmd() {
		// Initialize the super class...
		super();
		
		// ...and initialize anything else that requires it.
		m_userIds = new ArrayList<Long>();
	}
	
	/**
	 * Constructor method
	 * 
	 * @param userId
	 * @param allowPublicCollection
	 */
	public SaveMultiplePublicCollectionSettingsCmd(Long userId, Boolean allowPublicCollection) {
		// Initialize this object...
		this();
		
		// ...and store the parameters.
		addUserId(               userId               );
		setAllowPublicCollection(allowPublicCollection);
	}
	
	/**
	 * Constructor method
	 * 
	 * @param userIds
	 * @param allowPublicCollection
	 */
	public SaveMultiplePublicCollectionSettingsCmd(List<Long> userIds, Boolean allowPublicCollection) {
		// Initialize the super class...
		super();
		
		// ...and store the parameters.
		setUserIds(              userIds              );
		setAllowPublicCollection(allowPublicCollection);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public Boolean    getAllowPublicCollection() {return m_allowPublicCollection;}
	public List<Long> getUserIds()               {return m_userIds;              }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setAllowPublicCollection(Boolean    allowPublicCollection) {m_allowPublicCollection = allowPublicCollection;}
	public void setUserIds(              List<Long> userIds)               {m_userIds               = userIds;              }

	/**
	 * Adds a user ID to the list of them being tracked if it's not
	 * already being tracked.
	 * 
	 * @param userId
	 */
	public void addUserId(Long userId) {
		if (!(m_userIds.contains(userId))) {
			m_userIds.add(userId);
		}
	}
	
	/**
	 * Returns the command's enumeration value.
	 * 
	 * Implements VibeRpcCmd.getCmdType()
	 * 
	 * @return
	 */
	@Override
	public int getCmdType() {
		return VibeRpcCmdType.SAVE_MULTIPLE_PUBLIC_COLLECTION_SETTINGS.ordinal();
	}
}
