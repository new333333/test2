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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class represents a command sent to the server via GWT's RPC
 * mechanism.
 * 
 * @author jwootton
 */
public abstract class VibeRpcCmd implements IsSerializable
{
	private Boolean	m_runAsAdmin;	// true            -> If the user has zoneAdministration rights and is not the built-in admin, the command is run as the built-in admin.
									// false (or null) -> The command is run as the requesting user.
	/**
	 * Class constructor.
	 * 
	 * Zero parameter constructor method required for GWT
	 * serialization.
	 */
	public VibeRpcCmd()
	{
		// Initialize the super class.
		super();
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public final Boolean isRunAsAdmin() { return m_runAsAdmin; }
	
	/**
	 * Set'er methods.
	 * 
	 * @param runAsAdmin
	 */
	public final void setRunAsAdmin( Boolean runAsAdmin ) { m_runAsAdmin = runAsAdmin; }
	
	/**
	 * Returns the ordinal of the VibeRpcCmdType enumeration value for
	 * a given command.
	 * 
	 * Classes that extend this class need to return the ordinal of
	 * their VibeRpcCmdType enumeration value from their implementation
	 * of this. 
	 *
	 * @return
	 */
	public abstract int getCmdType();
}
