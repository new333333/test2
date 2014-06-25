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

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;
import org.kablink.teaming.gwt.client.widgets.VibeWidget;
import org.kablink.teaming.gwt.client.widgets.WidgetStyles;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author jwootton
 *
 */
public class ConfigData
	implements IsSerializable, VibeRpcResponseData
{
	private transient TopLevelConfig m_topLevelConfig;	// Holds all of the ConfigItems that make up the landing page.
	private String m_binderId;
	private String m_configStr;
	private boolean m_isPreviewMode;
	private GwtLandingPageProperties m_lpProperties;
	

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
		@Override
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
		 * This is just a place holder.  Nothing to do.
		 */
		@Override
		public VibeWidget createWidget( WidgetStyles widgetStyles )
		{
			return null;
		}
		
		/**
		 * This is just a place holder.  Nothing to do.
		 */
		@Override
		public DropWidget createDropWidget( LandingPageEditor lpe )
		{
			return null;
		}
		
		
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
	public ConfigData()
	{
		init();
	}
	
	
	/**
	 * 
	 */
	public ConfigData( String configStr, String binderId )
	{
		init();
		
		m_binderId = binderId;
		
		// Does the string that holds the configuration have a ';' at the end?
		m_configStr = configStr;
		if ( m_configStr != null && !m_configStr.endsWith( ";" ) )
			m_configStr += ";";

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
					configItem = new UtilityElementConfig( itemData[i], getBinderId() );
					++i;
				}
				else if ( itemName.equalsIgnoreCase( "url" ) )
				{
					configItem = new LinkToUrlConfig( itemData[i] );
					++i;
				}
				else if ( itemName.equalsIgnoreCase( "customJsp" ) )
				{
					configItem = new CustomJspConfig( itemData[i], getLandingPageStyle(), getBinderId() );
					++i;
				}
				else if ( itemName.equalsIgnoreCase( "tableStart" ) )
				{
					configItem = new TableConfig( itemData[i], getLandingPageStyle() );
					
					// Recursively call ourselves and add items to the TableConfig object we just created.
					i = addConfigItems( configItem, itemData, i+1 );
				}
				else if ( itemName.equalsIgnoreCase( "tableRow" ) )
				{
					configItem = new TableRowConfig( itemData[i] );
					
					// Recursively call ourselves and add items to the TableRowConfig object we just created.
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
					
					// Are we adding the table column to a TableRowConfig?
					if ( parent instanceof TableRowConfig )
					{
						// Yes
						configItem = new TableColConfig( itemData[i] );
					}
					else
					{
						TableRowConfig trConfig;
						
						// No, this happens when we find an old landing page configuration before we
						// added the ability to have multiple rows in a table.
						// Create a TableRowConfig item where all subsequent items will live
						trConfig = new TableRowConfig( "" );
						parent.addChild( trConfig );
						parent = trConfig;
						
						configItem = new TableColConfig( itemData[i] );
					}
					
					// Recursively call ourselves and add items to the TableColConfig object we just created.
					i = addConfigItems( configItem, itemData, i+1 );
				}
				else if ( itemName.equalsIgnoreCase( "tableRowEnd" ) )
				{
					// If this signals the end of a tableCol we want to process this item again because
					// the tableRowEnd signals both the end of a tableCol and the end of tableRow.
					if ( parent instanceof TableColConfig )
						return i;
					
					return i+1;
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
					configItem = new ListConfig( itemData[i], getLandingPageStyle() );
					
					// Recursively call ourselves and add items to the ListConfig object we just created.
					i = addConfigItems( configItem, itemData, i+1 );
				}
				else if ( itemName.equalsIgnoreCase( "listEnd" ) )
				{
					return i+1;
				}
				else if ( itemName.equalsIgnoreCase( "graphic" ) )
				{
					configItem = new GraphicConfig( itemData[i], getLandingPageStyle(), getBinderId() );
					++i;
				}
				else if ( itemName.equalsIgnoreCase( "entry" ) )
				{
					configItem = new EntryConfig( itemData[i], getLandingPageStyle() );
					++i;
				}
				else if ( itemName.equalsIgnoreCase( "entryUrl" ) )
				{
					configItem = new LinkToEntryConfig( itemData[i] );
					++i;
				}
				else if ( itemName.equalsIgnoreCase( "folder" ) )
				{
					configItem = new FolderConfig( itemData[i], getLandingPageStyle() );
					++i;
				}
				else if ( itemName.equalsIgnoreCase( "binderUrl" ) )
				{
					configItem = new LinkToFolderConfig( itemData[i] );
					++i;
				}
				else if ( itemName.equalsIgnoreCase( "html" ) )
				{
					configItem = new HtmlConfig( itemData[i], getBinderId() );
					++i;
				}
				else if ( itemName.equalsIgnoreCase( "enhancedView" ) )
				{
					configItem = new EnhancedViewConfig( itemData[i], getLandingPageStyle(), getBinderId() );
					++i;
				}
				else if ( itemName.equalsIgnoreCase( "iframe" ) )
				{
					configItem = new IFrameConfig( itemData[i], getLandingPageStyle() );
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
	 * Replace all occurrences of "%2c" with ',' and all occurrences of "%3b" with ';'
	 */
	public static String decodeSeparators( String encodedData )
	{
		String results = null;
		
		if ( encodedData != null )
		{
			results = encodedData.replaceAll( "%2c", "," );
			results = results.replaceAll( "%3b", ";" );
			results = results.replaceAll( "%3d", "=" );
		}
		
		return results;
	}
	
	
	/**
	 * Encode the given config data.  We will call URL.encode and then in addition to that
	 * we will encode all ',' and ';' characters.
	 */
	public static String encodeConfigData( String configData )
	{
		String encodedStr;
		
		encodedStr = URL.encode( configData );
		
		// Replace all occurrences of ',' with "%2c" and all occurrences of ';' with "%3b".
		encodedStr = ConfigData.encodeSeparators( encodedStr );

		return encodedStr;
	}// end encodeConfigData()
	
	
	/**
	 * Replace all occurrences of ',' with "%2c" and all occurrences of ';' with "%3b" and
	 * all occurrences of '=' with "%3d".
	 */
	public static String encodeSeparators( String configData )
	{
		StringBuffer finalStr;
		int i;
		
		finalStr = new StringBuffer();
		for (i = 0; i < configData.length(); ++i)
		{
			char nextCh;
			
			nextCh = configData.charAt( i );
			if ( nextCh == ',' )
				finalStr.append( "%2c" );
			else if ( nextCh == ';' )
				finalStr.append( "%3b" );
			else if ( nextCh == '=' )
				finalStr.append( "%3d" );
			else
				finalStr.append( nextCh );
		}

		return finalStr.toString();
	}


	/**
	 * Return the ConfigItem at the given index.
	 */
	public ConfigItem get( int index )
	{
		return m_topLevelConfig.get( index );
	}// end get()
	
	
	/**
	 * 
	 */
	public String getBackgroundColor()
	{
		return m_lpProperties.getBackgroundColor();
	}
	
	/**
	 * 
	 */
	public String getBackgroundImgUrl()
	{
		return m_lpProperties.getBackgroundImageUrl();
	}
	
	
	/**
	 * 
	 */
	public String getBackgroundImgRepeat()
	{
		return m_lpProperties.getBackgroundRepeat();
	}
	
	/**
	 * 
	 */
	public String getBinderId()
	{
		return m_binderId;
	}
	
	
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
	 * 
	 */
	public boolean getHideFooter()
	{
		return m_lpProperties.getHideFooter();
	}
	
	
	/**
	 * 
	 */
	public boolean getHideMasthead()
	{
		return m_lpProperties.getHideMasthead();
	}
	
	
	/**
	 * 
	 */
	public boolean getHideMenu()
	{
		return m_lpProperties.getHideMenu();
	}
	
	/**
	 * 
	 */
	public boolean getHideNavPanel()
	{
		return m_lpProperties.getHideSidebar();
	}
	
	/**
	 * 
	 */
	public boolean getInheritProperties()
	{
		return m_lpProperties.getInheritProperties();
	}
	
	/**
	 * 
	 */
	public GwtLandingPageProperties getLandingPageProperties()
	{
		return m_lpProperties;
	}
	
	/**
	 * Initialize the landing page properties from the given xml
	 */
	public GwtLandingPageProperties initLandingPageProperties( String xml )
	{
		m_lpProperties = new GwtLandingPageProperties( xml );
		
		return m_lpProperties;
	}
	
	/**
	 * Initialize the landing page properties from the given landing page properties
	 */
	public void initLandingPageProperties( GwtLandingPageProperties lpProperties )
	{
		m_lpProperties.copy( lpProperties );
	}
	
	/**
	 * Is this configuration data being used in "preview mode"?
	 */
	public boolean isPreviewMode()
	{
		return m_isPreviewMode;
	}
	
	/**
	 * 
	 */
	public String getLandingPageStyle()
	{
		return m_lpProperties.getStyle();
	}
	
	
	/**
	 * Initialized data members to their default value.
	 */
	private void init()
	{
		m_topLevelConfig = new TopLevelConfig();

		m_binderId = null;
		m_configStr = null;
		m_lpProperties = new GwtLandingPageProperties();
		m_isPreviewMode = false;
	}
	
	
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
	 * 
	 */
	public void setBackgroundColor( String color )
	{
		m_lpProperties.setBackgroundColor( color );
	}
	
	/**
	 * 
	 */
	public void setBackgroundImgRepeat( String repeat )
	{
		m_lpProperties.setBackgroundRepeat( repeat );
	}
	
	
	/**
	 * 
	 */
	public void setBackgroundImgUrl( String imgUrl )
	{
		m_lpProperties.setBackgroundImgUrl( imgUrl );
	}
	
	/**
	 * 
	 */
	public void setBinderId( String binderId )
	{
		m_binderId = binderId;
	}
	
	
	/**
	 * 
	 */
	public void setConfigStr( String configStr )
	{
		m_configStr = configStr;
	}
	
	
	/**
	 * 
	 */
	public void setHideMenu( boolean hideMenu )
	{
		m_lpProperties.setHideMenu( hideMenu );
	}
	
	/**
	 * Set the flag that tells us whether this configuration data is being used in preview mode.
	 */
	public void setPreviewMode( boolean isPreviewMode )
	{
		m_isPreviewMode = isPreviewMode;
	}
	
	
	/**
	 * Return the number of ConfigItems we have in our list.
	 */
	public int size()
	{
		return m_topLevelConfig.numItems();
	}// end size()
	
	
	/**
	 * Split the given config string into its 2 parts, label and data.  The string passed to this
	 * method should be of the format "some text=some data".  Given such a string, this method will
	 * return an array of strings where array=[0] is "some text" and array[1] is "some data"
	 */
	public static String[] splitConfigItem( String configStr )
	{
		String[] results = null;
		int index;
		
		if ( configStr == null )
			return null;
		
		// Find the first occurrance of '='
		index = configStr.indexOf( '=' );
		if ( index >= 0 )
		{
			results = new String[2];
			
			results[0] = configStr.substring( 0, index );
			
			if ( configStr.length() > index+1 )
				results[1] = configStr.substring( index+1 );
		}
		
		return results;
	}// end splitConfigItem()
}// end ConfigData
