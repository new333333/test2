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

import org.kablink.teaming.gwt.client.widgets.ListWidget;
import org.kablink.teaming.gwt.client.widgets.VibeWidget;
import org.kablink.teaming.gwt.client.widgets.WidgetStyles;

import com.google.gwt.dom.client.Style;
import com.google.gwt.http.client.URL;

/**
 * This class represents the configuration data for a List
 * @author jwootton
 *
 */
public class ListConfig extends ConfigItem
{
	private ListProperties m_properties;
	private ArrayList<ConfigItem> m_configItems;
	
	
	/**
	 * 
	 */
	public ListConfig( String configStr, String landingPageStyle )
	{
		int i;
		String[] propsStr;
		
		m_properties = new ListProperties();
		m_configItems = new ArrayList<ConfigItem>();
		setLandingPageStyle( landingPageStyle );

		// Split the string "listStart,showBorder=1,title=xxx" into its parts.
		propsStr = configStr.split( "[,;]" );
		
		// Get the list properties
		if ( propsStr != null )
		{
			for (i = 0; i < propsStr.length; ++i)
			{
				String[] results2;
				
				results2 = ConfigData.splitConfigItem( propsStr[i] );
				if ( results2 != null && results2.length == 2 && results2[0] != null && results2[1] != null && results2[1].length() > 0 )
				{
					try
					{
						if ( results2[0].equalsIgnoreCase( "showBorder" ) )
							m_properties.setShowBorder( results2[1].equalsIgnoreCase( "1" ) );
						else if ( results2[0].equalsIgnoreCase( "title" ) )
							m_properties.setTitle( URL.decodeComponent( results2[1] ) );
						else if ( results2[0].equalsIgnoreCase( "width" ) )
						{
							String value;
							
							// The string looks like, width=nn% or width=nnpx
							value = results2[1];
							if ( value != null )
							{
								int index;
								
								// Is the width using %?
								index = value.indexOf( "%" );
								if ( index != -1 )
								{
									// Yes
									m_properties.setWidthUnits( Style.Unit.PCT );
								}
								else
								{
									// Is the width using px?
									index = value.indexOf( "px" );
									if ( index != -1 )
									{
										// Yes
										m_properties.setWidthUnits( Style.Unit.PX );
									}
								}
								
								// Did we find the units?
								if ( index != -1 )
								{
									String numValue;
									int width;
									
									// Yes
									numValue = value.substring( 0, index );
									try
									{
										width = Integer.parseInt( numValue );
										m_properties.setWidth( width );
									}
									catch (Exception ex)
									{
										// Nothing to do.  This is here to handle the case when the data is
										// not properly url encoded or an invalid number string was entered.
									}
								}
							}
						}
						else if ( results2[0].equalsIgnoreCase( "height" ) )
						{
							String value;
							
							// The string looks like, height=nn% or height=nnpx
							value = results2[1];
							if ( value != null )
							{
								int index;
								
								// Is the height using %?
								index = value.indexOf( "%" );
								if ( index != -1 )
								{
									// Yes
									m_properties.setHeightUnits( Style.Unit.PCT );
								}
								else
								{
									// Is the height using px?
									index = value.indexOf( "px" );
									if ( index != -1 )
									{
										// Yes
										m_properties.setHeightUnits( Style.Unit.PX );
									}
								}
								
								// Did we find the units?
								if ( index != -1 )
								{
									String numValue;
									int height;
									
									// Yes
									numValue = value.substring( 0, index );
									try
									{
										height = Integer.parseInt( numValue );
										m_properties.setHeight( height );
									}
									catch (Exception ex)
									{
										// Nothing to do.  This is here to handle the case when the data is
										// not properly url encoded or an invalid number string was entered.
									}
								}
							}
						}
						else if ( results2[0].equalsIgnoreCase( "overflow" ) )
						{
							String value;
							
							// The string looks like, overflow=auto or overflow=hidden
							value = results2[1];
							if ( value != null )
							{
								if ( value.equalsIgnoreCase( "auto" ) )
									m_properties.setOverflow( Style.Overflow.AUTO );
								else if ( value.equalsIgnoreCase( "hidden" ) )
									m_properties.setOverflow( Style.Overflow.HIDDEN );
							}
						}
					}
					catch (Exception ex)
					{
						// Nothing to do.  This is here to handle the case when the data is
						// not properly url encoded.
					}
				}
			}// end for()
		}
	}// end ListConfig()
	
	
	/**
	 * 
	 */
	public void addChild( ConfigItem configItem )
	{
		m_configItems.add( configItem );
	}// end addChild()
	
	
	/**
	 * Create a composite that can be used on any page.
	 */
	public VibeWidget createWidget( WidgetStyles widgetStyles )
	{
		ListWidget listWidget;
		
		listWidget = new ListWidget( this, widgetStyles );
		return listWidget;
	}
	
	/**
	 * Create a DropWidget that can be used in the landing page editor.
	 */
	public ListDropWidget createDropWidget( LandingPageEditor lpe )
	{
		return new ListDropWidget( lpe, this );
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
	public ListProperties getProperties()
	{
		return m_properties;
	}// end getProperties()


	/**
	 * 
	 */
	public int numItems()
	{
		return m_configItems.size();
	}// end numItems()
}// end ListConfig
