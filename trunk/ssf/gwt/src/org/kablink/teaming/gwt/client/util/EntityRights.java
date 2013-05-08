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
package org.kablink.teaming.gwt.client.util;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to encapsulate an individual's rights to an entity.
 * 
 * @author drfoster@novell.com
 */
public class EntityRights implements IsSerializable {
	private boolean	m_canAddReplies;	//
	private boolean	m_canModify;		//
	private boolean	m_canPurge;			//
	private boolean	m_canShare;			//
	private boolean	m_canTrash;			//

	/**
	 * Constructor method.
	 */
	public EntityRights() {
		// Simply initialize the super class.
		super();
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean isCanAddReplies() {return m_canAddReplies;}
	public boolean isCanModify()     {return m_canModify    ;}
	public boolean isCanPurge()      {return m_canPurge     ;}
	public boolean isCanShare()      {return m_canShare     ;}
	public boolean isCanTrash()      {return m_canTrash     ;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setCanAddReplies(boolean canAddReplies) {m_canAddReplies = canAddReplies;}
	public void setCanModify(    boolean canModify)     {m_canModify     = canModify;    }
	public void setCanPurge(     boolean canPurge)      {m_canPurge      = canPurge;     }
	public void setCanShare(     boolean canShare)      {m_canShare      = canShare;     }
	public void setCanTrash(     boolean canTrash)      {m_canTrash      = canTrash;     }
}
