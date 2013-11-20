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
package org.kablink.teaming.gwt.client.util.runasync;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtLdapConnectionConfig;
import org.kablink.teaming.gwt.client.widgets.EditLdapConfigDlg.GwtLdapSyncMode;
import org.kablink.teaming.gwt.client.widgets.LdapSyncResultsDlg;



/**
 * Class that holds all of the information necessary to initialize and show the
 * LdapSyncResults dialog.
 */
public class LdapSyncResultsDlgInitAndShowParams extends RunAsyncInitAndShowParams
{
	private List<GwtLdapConnectionConfig> m_listOfLdapServers;
	private String m_syncId;
	private Boolean m_clearResults;
	private GwtLdapSyncMode m_syncMode;

	/**
	 * 
	 */
	public LdapSyncResultsDlgInitAndShowParams()
	{
	}
	
	/**
	 * 
	 */
	public Boolean getClearResults()
	{
		return m_clearResults;
	}
	
	/**
	 * 
	 */
	public List<GwtLdapConnectionConfig> getListOfLdapServers()
	{
		return m_listOfLdapServers;
	}
	
	/**
	 * 
	 */
	public String getSyncId()
	{
		return m_syncId;
	}
	
	/**
	 * 
	 */
	public GwtLdapSyncMode getSyncMode()
	{
		return m_syncMode;
	}

	/**
	 * 
	 */
	@Override
	public LdapSyncResultsDlg getUIObj()
	{
		if ( m_uiObj instanceof LdapSyncResultsDlg )
			return (LdapSyncResultsDlg) m_uiObj;
		
		return null;
	}
	/**
	 * 
	 */
	public void setClearResults( Boolean clear )
	{
		m_clearResults = clear;
	}
	
	/**
	 * 
	 */
	public void setListOfLdapServers( List<GwtLdapConnectionConfig> list )
	{
		m_listOfLdapServers = list;
	}
	
	/**
	 * 
	 */
	public void setSyncId( String id )
	{
		m_syncId = id;
	}
	
	/**
	 * 
	 */
	public void setSyncMode( GwtLdapSyncMode mode )
	{
		m_syncMode = mode;
	}
}

