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
package org.kablink.teaming.gwt.client.rpc.shared;


import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Class used to return a response to the "test KeyShield connection" request.
 * 
 * @author jwootton@novell.com
 */
public class TestKeyShieldConnectionResponse 
	implements IsSerializable, VibeRpcResponseData
{
	GwtKeyShieldConnectionTestStatusCode m_statusCode;
	String m_statusDesc = null;
	String m_stackTrace = null;
	
	/**
	 * This class represents the ConnectionTestStatusCode
	 */
	public enum GwtKeyShieldConnectionTestStatusCode implements IsSerializable
	{
		/**
		 * Indicates a success
		 */
		NORMAL,
		
		FAILED,
		
		UNKNOWN
	}

	/**
	 * Constructor method. 
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public TestKeyShieldConnectionResponse()
	{
		// Nothing to do.
	}
	
	/**
	 * If the test connection failed, we may have a stack trace showing the problem.
	 */
	public String getStackTrace()
	{
		return m_stackTrace;
	}
	
	/**
	 * 
	 */
	public GwtKeyShieldConnectionTestStatusCode getStatusCode()
	{
		return m_statusCode;
	}
	
	/**
	 * 
	 */
	public String getStatusDescription()
	{
		return m_statusDesc;
	}
	
	/**
	 * 
	 */
	public void setStackTrace( String stackTrace )
	{
		m_stackTrace = stackTrace;
	}
	
	/**
	 * 
	 */
	public void setStatusCode( GwtKeyShieldConnectionTestStatusCode statusCode )
	{
		m_statusCode = statusCode;
	}
	
	/**
	 * 
	 */
	public void setStatusDescription( String desc )
	{
		m_statusDesc = desc;
	}
}
