/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.domain;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.kablink.teaming.util.XmlUtil;


/**
 * 
 * @author jwootton
 *
 * Used to encapsulate name completion settings information.  A component of ZoneConfig
 * 
 */
public class NameCompletionSettings
{
	private String nameCompletionSettings;
	private NCDisplayField m_groupPrimaryFld;
	private NCDisplayField m_groupSecondaryFld;
	

	/**
	 * 
	 */
	public enum NCDisplayField
	{
		DESCRIPTION,
		FQDN,
		NAME,
		TITLE,
		UNKNOWN;
		
		/**
		 * 
		 */
		public static String getFieldAsString( NCDisplayField field )
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
		public static NCDisplayField getFieldFromString( String field )
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
	 * 
	 */
	public NameCompletionSettings()
	{
		m_groupPrimaryFld = NCDisplayField.NAME;
		m_groupSecondaryFld = NCDisplayField.DESCRIPTION;
	}

	/**
	 * 
	 */
	public NameCompletionSettings( NameCompletionSettings settings )
	{
		if ( settings != null )
		{
			this.nameCompletionSettings = settings.getNameCompletionSettings();
			this.m_groupPrimaryFld = settings.getGroupPrimaryFld();
			this.m_groupSecondaryFld = settings.getGroupSecondaryFld();
		}
	}

	/**
	 * Construct the xml string that will be stored in the db. 
	 */
	private void constructXmlString()
	{
		StringBuffer strBuff;
		
		strBuff = new StringBuffer();
		strBuff.append( "<NameCompletionSettings groupPrimaryDisplay=\"" );
		strBuff.append( NCDisplayField.getFieldAsString( m_groupPrimaryFld ) );
		strBuff.append( "\" groupSecondaryDisplay=\"" );
		strBuff.append( NCDisplayField.getFieldAsString( m_groupSecondaryFld ) );
		strBuff.append( "\" />" );
		
		nameCompletionSettings = strBuff.toString();
	}
	
	/**
	 * Extract the name completion settings from the given xml string.
	 */
	private void extractSettings( String xmlString )
	{
		Document doc;
		
		if ( xmlString == null || xmlString.length() == 0 )
			return;
		
		try
		{
			Node rootNode;
			Node node;
			String value;
			
			doc = XmlUtil.parseText( xmlString );
			
			rootNode = doc.getRootElement();
			
			// Get the value of the "groupPrimaryDisplay" attribute.
			node = rootNode.selectSingleNode( "@groupPrimaryDisplay" );
			if ( node != null )
			{
				value = node.getText();
				m_groupPrimaryFld = NCDisplayField.getFieldFromString( value );
			}
			
			// Get the value of the "groupSecondaryDisplay" attribute.
			node = rootNode.selectSingleNode( "@groupSecondaryDisplay" );
			if ( node != null )
			{
				value = node.getText();
				m_groupSecondaryFld = NCDisplayField.getFieldFromString( value );
			}
		}
		catch ( DocumentException docEx )
		{
		}
	}
	
	/**
	 * 
	 */
	public NCDisplayField getGroupPrimaryFld()
	{
		return m_groupPrimaryFld;
	}
	
	/**
	 * 
	 */
	public NCDisplayField getGroupSecondaryFld()
	{
		return m_groupSecondaryFld;
	}
	
	/**
	 * 
	 */
	public String getNameCompletionSettings()
	{
		return nameCompletionSettings;
	}
	
	/**
	 * 
	 */
	public void setGroupPrimaryFld( NCDisplayField fld )
	{
		m_groupPrimaryFld = fld;
		
		// Reconstruct the xml string that represents the name completion settings
		constructXmlString();
	}
	
	/**
	 * 
	 */
	public void setGroupSecondaryFld( NCDisplayField fld )
	{
		m_groupSecondaryFld = fld;
		
		// Reconstruct the xml string that represents the name completion settings
		constructXmlString();
	}
	
	/**
	 * 
	 */
	public void setNameCompletionSettings( String xmlString )
	{
		nameCompletionSettings = xmlString;
		
		// Parse the xml string and extract the various settings.
		extractSettings( xmlString );
	}
}
