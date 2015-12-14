/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client;

import com.google.gwt.user.client.rpc.IsSerializable;
	
/**
 * ?
 * 
 * @author jwootton@novell.com
 */
public abstract class GwtPrincipal extends GwtTeamingItem
	implements IsSerializable
{
	private boolean m_internal;
	transient private Object m_additionalData;

	/**
	 * 
	 */
	public enum PrincipalClass implements IsSerializable
	{
		USER,
		GROUP,
		PUBLIC_TYPE,
		UNKNOWN
	}

	/**
	 * 
	 */
	public boolean equals( GwtPrincipal principal )
	{
		Long id1;
		Long id2;
		
		id1 = getIdLong();
		id2 = principal.getIdLong();
		if ( id1 != null && id2 != null && id1.compareTo( id2 ) == 0 )
			return true;
		
		return false;
	}
	
	/**
	 * 
	 */
	public Object getAdditionalData()
	{
		return m_additionalData;
	}
	
	/**
	 * 
	 */
	public abstract Long getIdLong();

	/**
	 * 
	 */
	public boolean isInternal()
	{
		return m_internal;
	}
	
	/**
	 * 
	 */
	public abstract PrincipalClass getPrincipalClass();
	
	/**
	 * 
	 */
	public String getTypeAsString()
	{
		PrincipalClass pClass;
		
		pClass = getPrincipalClass();
		if ( pClass == PrincipalClass.USER )
			return GwtTeaming.getMessages().modifyNetFolderServerDlg_User();

		if ( pClass == PrincipalClass.GROUP )
			return GwtTeaming.getMessages().modifyNetFolderServerDlg_Group();
		
		return "Unknown principal type";
	}

	/**
	 * 
	 */
	public void setAdditionalData( Object data )
	{
		m_additionalData = data;
	}
	
	/**
	 * 
	 */
	public void setInternal( boolean internal )
	{
		this.m_internal = internal;
	}
}
