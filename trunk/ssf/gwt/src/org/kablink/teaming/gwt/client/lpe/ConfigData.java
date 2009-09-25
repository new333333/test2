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
	private TopLevelConfig m_topLevelConfig;	// Holds all of the ConfigItems that make up the landing page.
	private String m_configStr;
	

	/**
	 * 
	 */
	public class TopLevelConfig extends ConfigItem
	{
		private ArrayList<ConfigItem> m_configItems;
		
		/**
		 * 
		 */
		public TopLevelConfig()
		{
			m_configItems = new ArrayList<ConfigItem>();
		}// end TopLevelConfig()
		
		
		/**
		 * 
		 */
		public void addChild( ConfigItem configItem )
		{
			m_configItems.add( configItem );
		}// end addChild()
		
		
		/**
		 * 
		 */
		public void clear()
		{
			m_configItems.clear();
		}// end clear()
		
		
		/**
		 * 
		 */
		public ConfigItem get( int index )
		{
			if ( index < m_configItems.size() )
				return m_configItems.get( index );
			
			return null;
		}// end get()
		
		
		/**
		 * 
		 */
		public int numItems()
		{
			return m_configItems.size();
		}// end numItems()
	}// end TopLevelConfig
	
	
	/**
	 * 
	 */
	public ConfigData( String configStr )
	{
		// Does the string that holds the configuration have a ';' at the end?
		m_configStr = configStr;
		if ( m_configStr != null && !m_configStr.endsWith( ";" ) )
			m_configStr += ";";
		
		m_topLevelConfig = new TopLevelConfig();
	}// end ConfigData()
	
	
	/**
	 * 
	 */
	public int addConfigItems( ConfigItem parent, String[] itemData, int startIndex )
	{
		int i;
		
		// Spin through the list of strings and create a ConfigItem object for each one and add the item to the given parent.
		i = startIndex;
		while ( i < itemData.length )
		{
			String itemName;
			
			// Get the name of this item.
			itemName = getConfigItemName( itemData[i] );
			
			if ( itemName != null && itemName.length() > 0 )
			{
				ConfigItem configItem;
				
				configItem = null;
				
				if ( itemName.equalsIgnoreCase( "utility" ) )
				{
					configItem = new UtilityElementConfig( itemData[i] );
					++i;
				}
				else if ( itemName.equalsIgnoreCase( "url" ) )
				{
					configItem = new LinkToUrlConfig( itemData[i] );
					++i;
				}
				else if ( itemName.equalsIgnoreCase( "customJsp" ) )
				{
					configItem = new CustomJspConfig( itemData[i] );
					++i;
				}
				else if ( itemName.equalsIgnoreCase( "tableStart" ) )
				{
					configItem = new TableConfig( itemData[i] );
					
					// Recursively call ourselves and add items to the TableConfig object we just created.
					i = addConfigItems( configItem, itemData, i+1 );
				}
				else if ( itemName.equalsIgnoreCase( "tableCol" ) )
				{
					// Are we adding items to a TableCol object?
					if ( parent instanceof TableColConfig )
					{
						// Yes, this mean we need to start a new TableCol
						return i;
					}
					
					configItem = new TableColConfig( itemData[i] );
					
					// Recursively call ourselves and add items to the TableColConfig object we just created.
					i = addConfigItems( configItem, itemData, i+1 );
				}
				else if ( itemName.equalsIgnoreCase( "tableEnd" ) )
				{
					// If this signals the end of a tableCol we want to process this item again because
					// the tableEnd signals both the end of a tableCol and the end of tableStart.
					if ( parent instanceof TableColConfig )
						return i;
					
					return i+1;
				}
				else if ( itemName.equalsIgnoreCase( "listStart" ) )
				{
					configItem = new ListConfig( itemData[i] );
					
					// Recursively call ourselves and add items to the ListConfig object we just created.
					i = addConfigItems( configItem, itemData, i+1 );
				}
				else if ( itemName.equalsIgnoreCase( "listEnd" ) )
				{
					return i+1;
				}
				else if ( itemName.equalsIgnoreCase( "graphic" ) )
				{
					configItem = new GraphicConfig( itemData[i] );
					++i;
				}
				else if ( itemName.equalsIgnoreCase( "entry" ) )
				{
					configItem = new EntryConfig( itemData[i] );
					++i;
				}
				else if ( itemName.equalsIgnoreCase( "entryUrl" ) )
				{
					configItem = new LinkToEntryConfig( itemData[i] );
					++i;
				}
				else
				{
					// We don't know what this item is.  Skip it.
					++i;
				}
				
				if ( configItem != null )
					parent.addChild( configItem );
			}
			else
				++i;
		}// end while()
		
		return i;
	}// end addConfigItems();


	/**
	 * Return the ConfigItem at the given index.
	 */
	public ConfigItem get( int index )
	{
		return m_topLevelConfig.get( index );
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
	 * Parse the string that holds the configuration data and create a ConfigItem
	 * for each item that makes up the landing page.
	 */
	public void parse()
	{
		// Clear our list that holds ConfigItem objects.
		m_topLevelConfig.clear();
		
		if ( m_configStr != null && m_configStr.length() > 0 )
		{
			String[] results;
			
			// Split the configuration data into its parts.  Where each part represents an item.
			results = m_configStr.split( "[;]" );
			if ( results != null )
			{
				addConfigItems( m_topLevelConfig, results, 0 );
			}
		}
	}// end parse()
	
	
	/**
	 * Return the number of ConfigItems we have in our list.
	 */
	public int size()
	{
		return m_topLevelConfig.numItems();
	}// end size()
}// end ConfigData
