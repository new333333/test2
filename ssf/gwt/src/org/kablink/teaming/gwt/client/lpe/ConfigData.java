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

import java.util.ArrayList;

/**
 * 
 * @author jwootton
 *
 */
public class ConfigData
{
	private ArrayList<ConfigItem>	m_configItems;	// Configuration for each of the items that make up the landing page.
	private String m_configStr;
	
	/**
	 * 
	 */
	public ConfigData( String configStr )
	{
		// Does the string that holds the configuration have a ';' at the end?
		m_configStr = configStr;
		if ( m_configStr != null && !m_configStr.endsWith( ";" ) )
			m_configStr += ";";
		
		m_configItems = new ArrayList<ConfigItem>();
	}// end ConfigData()


	/**
	 * For the given string, which defines an item in the landing page,
	 * create the appropriate subclass of the ConfigItem class.
	 */
	public ConfigItem createConfigItem( String configStr )
	{
		String name;
		ConfigItem configItem = null;
		
		// Get the name of this item.
		name = getConfigItemName( configStr );
		
		if ( name == null || name.length() == 0 )
			return null;
		
		// Create the appropriate ConfigItem class based on the name.
		if ( name.equalsIgnoreCase( "utility" ) )
			configItem = new UtilityElementConfig( configStr );
		else if ( name.equalsIgnoreCase( "url" ) )
			configItem = new LinkToUrlConfig( configStr );
		else if ( name.equalsIgnoreCase( "customJsp" ) )
			configItem = new CustomJspConfig( configStr );
		else if ( name.equalsIgnoreCase( "tableStart" ) )
			configItem = new TableConfig( configStr ); 
//!!!
/*
		else if ( name.equalsIgnoreCase( "listStart" ) )
			configItem = new ListConfig( configStr );
*/		
		return configItem;
	}// end createConfigItem()
	
	
	/**
	 * Return the ConfigItem at the given index.
	 */
	public ConfigItem get( int index )
	{
		if ( index < size() )
			return m_configItems.get( index );
		
		return null;
	}// end get()
	
	
	/**
	 * Get the name of the item from the configuration string.  The name will be the
	 * first word followed by a ',' or a ';'
	 */
	public String getConfigItemName( String configStr )
	{
		String name = null;
		String[] results;
		
		if ( configStr == null || configStr.length() == 0 )
			return null;
		
		results = configStr.split( "[,;]" );
		if ( results.length > 0 )
			name = results[0];

		return name;
	}// end getConfigItemName()
	
	
	/**
	 * Get the next string that holds configuration data for an item.
	 */
	public String getConfigItemStr( int startIndex, String configStr )
	{
		int index;
		String itemName;
		String configItemStr;
		
		if ( configStr == null )
			return null;
		
		// Is the starting index valid?
		if ( startIndex >= configStr.length() )
			return null;
		
		// Find the next ';'
		index = configStr.indexOf( ';', startIndex );
		
		if ( index == -1 )
			return null;
		
		if ( startIndex == index )
			return null;
		
		configItemStr = configStr.substring( startIndex, index+1 );
		
		// Get the name of this item.
		itemName = getConfigItemName( configItemStr );
		
		if ( itemName != null )
		{
			boolean done;
			int tmpStartIndex;
			int tmpEndIndex;
			String nextConfigItemStr;
			
			// Are we dealing with a table?
			if ( itemName.equalsIgnoreCase( "tableStart" ) )
			{
				// Yes, find the end of the table configuration by looking for "tableEnd"
				tmpStartIndex = startIndex + configItemStr.length();
				configItemStr = "";
				done = false;
				while ( !done )
				{
					// Get the configuration string for the next item.
					nextConfigItemStr = getConfigItemStr( tmpStartIndex, configStr );
					
					if ( nextConfigItemStr != null )
					{
						tmpStartIndex += nextConfigItemStr.length();
						
						// Get the name of the next item.
						itemName = getConfigItemName( nextConfigItemStr );
						
						if ( itemName != null )
						{
							if ( itemName.equalsIgnoreCase( "tableEnd" ) )
							{
								done = true;
								tmpEndIndex = tmpStartIndex;
								configItemStr = configStr.substring( startIndex, tmpEndIndex );
							}
						}
					}
					else
						done = true;
				}// end while
			}
			// Are we dealing with a list?
			else if ( itemName.equalsIgnoreCase( "listStart" ) )
			{
				// Yes, find the end of the list configuration by looking for "listEnd"
				tmpStartIndex = startIndex + configItemStr.length();
				configItemStr = "";
				done = false;
				while ( !done )
				{
					// Get the configuration string for the next item.
					nextConfigItemStr = getConfigItemStr( tmpStartIndex, configStr );
					
					if ( nextConfigItemStr != null )
					{
						tmpStartIndex += nextConfigItemStr.length();
						
						// Get the name of the next item.
						itemName = getConfigItemName( nextConfigItemStr );
						
						if ( itemName != null )
						{
							if ( itemName.equalsIgnoreCase( "listEnd" ) )
							{
								done = true;
								tmpEndIndex = tmpStartIndex;
								configItemStr = configStr.substring( startIndex, tmpEndIndex );
							}
						}
					}
					else
						done = true;
				}// end while
			}
		}
		
		return configItemStr;
	}// end getConfigItemStr()
	
	
	/**
	 * Parse the string that holds the configuration data and create a ConfigItem
	 * for each item that makes up the landing page.
	 */
	public void parse()
	{
		// Clear our list that holds ConfigItem objects.
		m_configItems.clear();
		
		if ( m_configStr != null && m_configStr.length() > 0 )
		{
			int startIndex;
			boolean done;
			
			startIndex = 0;
			done = false;
			while ( !done )
			{
				String configItemStr;
				
				// Get the substring that holds the configuration for the next item.
				configItemStr = getConfigItemStr( startIndex, m_configStr );
				
				// Did we get a string?
				if ( configItemStr != null )
				{
					ConfigItem configItem;

					// Yes
					// Create the appropriate ConfigItem object based on the configuration string.
					configItem = createConfigItem( configItemStr );
					
					// Did we get a ConfigItem object?
					if ( configItem != null )
						m_configItems.add( configItem );
					
					// Move to the next location in the configuration string.  Add 1 to move past the ';'
					startIndex += configItemStr.length();
				}
				else
				{
					// No
					done = true;
				}
			}// end while()
		}
	}// end parse()
	
	
	/**
	 * Return the number of ConfigItems we have in our list.
	 */
	public int size()
	{
		return m_configItems.size();
	}// end size()
}// end ConfigData
