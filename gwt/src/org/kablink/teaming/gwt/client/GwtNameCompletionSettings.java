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
package org.kablink.teaming.gwt.client;

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This class represents the settings used by the name completion control (FindCtrl.java)
 * The data here should mirror the data found in the class NameCompletionSettings.java
 * @author jwootton
 *
 */
public class GwtNameCompletionSettings
	implements IsSerializable, VibeRpcResponseData
{
	private GwtDisplayField m_groupPrimaryDisplayField;
	private GwtDisplayField m_groupSecondaryDisplayField;
	
	/**
	 * 
	 */
	public enum GwtDisplayField implements IsSerializable
	{
		DESCRIPTION,
		FQDN,
		NAME,
		TITLE,
		UNKNOWN;
		
		/**
		 * 
		 */
		public static String getFieldAsString( GwtDisplayField field )
		{
			if ( field == null )
				return "null";
			
			switch ( field )
			{
			case DESCRIPTION:
				return "desc";
				
			case FQDN:
				return "fqdn";
			
			case NAME:
				return "name";
				
			case TITLE:
				return "title";
				
			case UNKNOWN:
			default:
				return "unknown";
			}
		}
		
		/**
		 * 
		 */
		public static GwtDisplayField getFieldFromString( String field )
		{
			if ( field == null )
				return UNKNOWN;
			
			if ( field.equalsIgnoreCase( "desc" ) )
				return DESCRIPTION;
			
			if ( field.equalsIgnoreCase( "fqdn" ) )
				return FQDN;
			
			if ( field.equalsIgnoreCase( "name" ) )
				return NAME;
			
			if ( field.equalsIgnoreCase( "title" ) )
				return TITLE;
			
			return UNKNOWN;
		}
	}
	
	/**
	 * Constructor method. 
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public GwtNameCompletionSettings()
	{
		m_groupPrimaryDisplayField = GwtDisplayField.NAME;
		m_groupSecondaryDisplayField = GwtDisplayField.DESCRIPTION;
	}
	
	/**
	 * 
	 */
	public GwtDisplayField getGroupPrimaryDisplayField()
	{
		return m_groupPrimaryDisplayField;
	}
	
	/**
	 * 
	 */
	public GwtDisplayField getGroupSecondaryDisplayField()
	{
		return m_groupSecondaryDisplayField;
	}
	
	/**
	 * 
	 */
	public void setGroupPrimaryDisplayField( GwtDisplayField field )
	{
		m_groupPrimaryDisplayField = field;
	}
	
	/**
	 * 
	 */
	public void setGroupSecondaryDisplayField( GwtDisplayField field )
	{
		m_groupSecondaryDisplayField = field;
	}
}
