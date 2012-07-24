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
package org.kablink.teaming.gwt.server.util;

import java.util.Date;

import org.kablink.teaming.domain.ShareItemMember.RecipientType;
import org.kablink.teaming.gwt.client.util.ShareRights;

/**
 * Used to track shares in SharedWithMeItem and SharedByMeItem.
 *
 * @author drfoster@novell.com
 */
public class GwtPerShareInfo {
	private Date			m_rightsExpire;		//
	private Date			m_shareDate;		//
	private Long			m_recipientId;		//
	private RecipientType	m_recipientType;	//
	private ShareRights		m_rights;			//
	private String			m_comment;			//
	
	/**
	 * Constructor method.
	 * 
	 * @param recipientId
	 * @param recipientType
	 * @param shareDate
	 * @param rights
	 * @param rightsExpire
	 * @param comment
	 */
	public GwtPerShareInfo(Long recipientId, RecipientType recipientType, Date shareDate, ShareRights rights, Date rightsExpire, String comment) {
		// Initialize the super class...
		super();
		
		// ...and store the parameters.
		setRecipientId(  recipientId  );
		setRecipientType(recipientType);
		setShareDate(    shareDate    );
		setRights(       rights       );
		setRightsExpire( rightsExpire );
		setComment(      comment      );
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public Date          getRightsExpire()  {return m_rightsExpire; }
	public Date          getShareDate()     {return m_shareDate;    }
	public Long          getRecipientId()   {return m_recipientId;  }
	public RecipientType getRecipientType() {return m_recipientType;}
	public ShareRights   getRights()        {return m_rights;       }
	public String        getComment()       {return m_comment;      }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setRightsExpire( Date          rightsExpire)  {m_rightsExpire  = rightsExpire; }
	public void setShareDate(    Date          shareDate)     {m_shareDate     = shareDate;    }
	public void setRecipientId(  Long          recipientId)   {m_recipientId   = recipientId;  }
	public void setRecipientType(RecipientType recipientType) {m_recipientType = recipientType;}
	public void setRights(       ShareRights   rights)        {m_rights        = rights;       }
	public void setComment(      String        comment)       {m_comment       = comment;      }
}
