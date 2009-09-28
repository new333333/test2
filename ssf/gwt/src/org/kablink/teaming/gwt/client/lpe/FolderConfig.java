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

package org.kablink.teaming.gwt.client.lpe;


/**
 * This class represents the configuration data for a Folder widget
 * @author jwootton
 *
 */
public class FolderConfig extends ConfigItem
{
	private FolderProperties	m_properties;
	
	/**
	 * 
	 */
	public FolderConfig( String configStr )
	{
		String[] results;
		
		m_properties = new FolderProperties();
		
		// Split the configuration data into its parts.  ie folderId=xxx showTitle=x showFolderDescription=1 showEntriesOpened=1 entriesToShow
		results = configStr.split( "[,;]" );
		if ( results != null )
		{
			int i;
			
			for (i = 0; i < results.length; ++i)
			{
				String[] results2;
				
				results2 = results[i].split( "=" );
				if ( results2.length == 2 )
				{
					if ( results2[0].equalsIgnoreCase( "folderId" ) )
						m_properties.setFolderId( results2[1] );
					else if ( results2[0].equalsIgnoreCase( "showTitle" ) )
						m_properties.setShowTitle( results2[1].equalsIgnoreCase( "1" ) );
					else if ( results2[0].equalsIgnoreCase( "showFolderDescription" ) )
						m_properties.setShowDescValue( results2[1].equalsIgnoreCase( "1" ) );
					else if ( results2[0].equalsIgnoreCase( "showEntriesOpened" ) )
						m_properties.setShowEntriesOpenedValue( results2[1].equalsIgnoreCase( "1" ) );
					else if ( results2[0].equalsIgnoreCase( "entriesToShow" ) )
					{
						int numToShow;
						
						numToShow = Integer.parseInt( results2[1] );
						m_properties.setNumEntriesToBeShownValue( numToShow );
					}
				}
			}
		}
	}// end FolderConfig()
	
	
	/**
	 * 
	 */
	public void addChild( ConfigItem configItem )
	{
		// Nothing to do.
	}// end addChild()
	
	
	/**
	 * 
	 */
	public FolderProperties getProperties()
	{
		return m_properties;
	}// end getProperties()
}// end FolderConfig
