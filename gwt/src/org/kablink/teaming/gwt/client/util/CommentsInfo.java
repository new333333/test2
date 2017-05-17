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
package org.kablink.teaming.gwt.client.util;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to bundle information about the comments to an entry
 * through GWT RPC requests.
 *  
 * @author drfoster@novell.com
 */
public class CommentsInfo implements IsSerializable {
	private boolean		m_commentsDisabled;	// true -> Entry entity cannot have comments added to it.  false -> It can.
	private EntityId	m_entityId;			//
	private int			m_commentsCount;	//
	private String		m_entityTitle;		//

	// The following is only used on the client side to push an Image
	// through to the data table for items.
	private transient Object m_clientItemImage;
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public CommentsInfo() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method.
	 *
	 * @param entityId
	 * @param entityTitle
	 * @param commentsCount
	 */
	public CommentsInfo(EntityId entityId, String entityTitle, int commentsCount) {
		// Initialize this object...
		this();
		
		// ...and store the parameters.
		setEntityId(     entityId     );
		setEntityTitle(  entityTitle  );
		setCommentsCount(commentsCount);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean  isCommentsDisabled() {return m_commentsDisabled;}
	public EntityId getEntityId()        {return m_entityId;        }
	public int      getCommentsCount()   {return m_commentsCount;   }
	public String   getEntityTitle()     {return m_entityTitle;     }
	public Object   getClientItemImage() {return m_clientItemImage; }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setCommentsDisabled(boolean  commentsDisabled) {m_commentsDisabled = commentsDisabled;}
	public void setEntityId(        EntityId entityId)         {m_entityId         = entityId;        }
	public void setCommentsCount(   int      commentsCount)    {m_commentsCount    = commentsCount;   }
	public void setEntityTitle(     String   entityTitle)      {m_entityTitle      = entityTitle;     }
	public void setClientItemImage( Object   clientItemImage)  {m_clientItemImage  = clientItemImage; }
}
