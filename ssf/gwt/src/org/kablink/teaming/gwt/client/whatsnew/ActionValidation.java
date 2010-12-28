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

package org.kablink.teaming.gwt.client.whatsnew;

import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class is used in a request to see if the user has rights to preform the given action.
 * @author jwootton
 *
 */
public class ActionValidation implements IsSerializable
{
	private boolean m_isValid;
	private int m_action;			// Ordinal value of a TeamingAction
	
	/**
	 * 
	 */
	public ActionValidation()
	{
		m_isValid = false;
		m_action = -1;
	}
	

	public int getAction()
	{
		return m_action;
	}
	
	
	/**
	 * 
	 */
	public boolean isValid()
	{
		return m_isValid;
	}

	
	/**
	 * 
	 */
	public void setAction( int action )
	{
		m_action = action;
	}
	
	
	/**
	 * 
	 */
	public void setIsValid( boolean isValid )
	{
		m_isValid = isValid;
	}
}
