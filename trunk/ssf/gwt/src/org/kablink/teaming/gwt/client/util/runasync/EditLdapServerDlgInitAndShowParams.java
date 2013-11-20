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


import org.kablink.teaming.gwt.client.GwtLdapConnectionConfig;
import org.kablink.teaming.gwt.client.widgets.EditLdapServerConfigDlg;

/**
 * Class that holds all of the information necessary to initialize and show the
 * Edit Ldap Server dialog.
 */
public class EditLdapServerDlgInitAndShowParams extends RunAsyncInitAndShowParams
{
	private GwtLdapConnectionConfig m_config;
	private String m_defaultUserFilter;
	private String m_defaultGroupFilter;
	
	/**
	 * 
	 */
	public EditLdapServerDlgInitAndShowParams()
	{
	}
	
	/**
	 * 
	 */
	public GwtLdapConnectionConfig getConfig()
	{
		return m_config;
	}
	
	/**
	 * 
	 */
	public String getDefaultGroupFilter()
	{
		return m_defaultGroupFilter;
	}
	
	/**
	 * 
	 */
	public String getDefaultUserFilter()
	{
		return m_defaultUserFilter;
	}
	
	/**
	 * 
	 */
	@Override
	public EditLdapServerConfigDlg getUIObj()
	{
		if ( m_uiObj instanceof EditLdapServerConfigDlg )
			return (EditLdapServerConfigDlg) m_uiObj;
		
		return null;
	}

	/**
	 * 
	 */
	public void setConfig( GwtLdapConnectionConfig config )
	{
		m_config = config;
	}
	
	/**
	 * 
	 */
	public void setDefaultGroupFilter( String  filter )
	{
		m_defaultGroupFilter = filter;
	}
	
	/**
	 * 
	 */
	public void setDefaultUserFilter( String filter )
	{
		m_defaultUserFilter = filter;
	}
}

