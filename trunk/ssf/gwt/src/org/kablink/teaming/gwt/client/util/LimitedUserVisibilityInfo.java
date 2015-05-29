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
 * Class used to bundle information about the limited user visibility
 * settings on a principal (user or group) through GWT RPC requests.
 *  
 * @author drfoster@novell.com
 */
public class LimitedUserVisibilityInfo implements IsSerializable {
	private boolean		m_limited;		// Tracks the 'Can only Seem Members of Group That I am In' settings.
	private boolean		m_override;		// Tracks the override for that setting.
	private	Long		m_principalId;	//
	
	/*
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	private LimitedUserVisibilityInfo() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method.
	 *
	 * @param limited
	 * @param override
	 * @param principalId
	 */
	public LimitedUserVisibilityInfo(boolean limited, boolean override, Long principalId) {
		// Initialize this object...
		this();
		
		// ...and store the parameters.
		setLimited(    limited    );
		setOverride(   override   );
		setPrincipalId(principalId);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean isLimited()      {return m_limited;    }
	public boolean isOverride()     {return m_override;   }
	public Long    getPrincipalId() {return m_principalId;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setLimited(    boolean limited)     {m_limited     = limited;    }
	public void setOverride(   boolean override)    {m_override    = override;   }
	public void setPrincipalId(Long    principalId) {m_principalId = principalId;}
}
