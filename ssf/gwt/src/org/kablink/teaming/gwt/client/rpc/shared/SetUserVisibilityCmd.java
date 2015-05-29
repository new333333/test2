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
package org.kablink.teaming.gwt.client.rpc.shared;

import java.util.List;

/**
 * This class holds all of the information necessary to execute the
 * 'set user sharing rights info' command.
 * 
 * @author drfoster@novell.com
 */
public class SetUserVisibilityCmd extends VibeRpcCmd {
	private Boolean		m_limited;		// null -> No change to this for the given Principals.
	private Boolean 	m_override;		// null -> No change to this for the given Principals.
	private List<Long>	m_principalIds;	// The Principals (Users or Groups) the settings are to be applied to.
	
	/*
	 * Constructor method.
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	private SetUserVisibilityCmd() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param principalIds
	 * @param limited
	 * @param override
	 */
	public SetUserVisibilityCmd(List<Long> principalIds, Boolean limited, Boolean override) {
		// Initialize this object...
		this();
		
		// ...and store the parameters.
		setPrincipalIds(principalIds);
		setLimited(     limited     );
		setOverride(    override    );
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public Boolean    getLimited()      {return m_limited;     }
	public Boolean    getOverride()     {return m_override;    }
	public List<Long> getPrincipalIds() {return m_principalIds;}

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setLimited(     Boolean    limited)      {m_limited      = limited;     }
	public void setOverride(    Boolean    override)     {m_override     = override;    }
	public void setPrincipalIds(List<Long> principalIds) {m_principalIds = principalIds;}
	
	/**
	 * Returns the command's enumeration value.
	 * 
	 * Implements VibeRpcCmd.getCmdType()
	 * 
	 * @return
	 */
	@Override
	public int getCmdType() {
		return VibeRpcCmdType.SET_USER_VISIBILITY.ordinal();
	}
}
