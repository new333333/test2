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

import org.kablink.teaming.gwt.client.widgets.TableWidget;
import org.kablink.teaming.gwt.client.widgets.VibeWidget;
import org.kablink.teaming.gwt.client.widgets.WidgetStyles;

import com.google.gwt.http.client.URL;

/**
 * This class represents the configuration data for a Table
 * @author jwootton
 *
 */
public class TableConfig extends ConfigItem
{
	private TableProperties	m_properties;
	private ArrayList<ConfigItem> m_configItems;
	
	
	/**
	 * 
	 */
	public TableConfig( String configStr, String landingPageStyle )
	{
		int i;
		int numCols;
		int numRows;
		int width;
		String[] propsStr;
		boolean valid;
		
		m_properties = new TableProperties();
		m_configItems = new ArrayList<ConfigItem>();
		setLandingPageStyle( landingPageStyle );

		// Split the string "tableStart,showBorder=n,rows=n,cols=n,colWidths=xxxx" into its parts.
		propsStr = configStr.split( "[,;]" );
		
		// Default to 1 row in the table.
		numRows = 1;
		
		// Get the table properties
		if ( propsStr != null )
		{
			for (i = 0; i < propsStr.length; ++i)
			{
				String[] results2;
				
				results2 = propsStr[i].split( "=" );
				if ( results2 != null && results2.length == 2 && results2[0] != null && results2[1] != null && results2[1].length() > 0 )
				{
					if ( results2[0].equalsIgnoreCase( "showBorder" ) )
						m_properties.setShowBorder( results2[1].equalsIgnoreCase( "1" ) );
					else if ( results2[0].equalsIgnoreCase( "rows" ) )
					{
						try
						{
							numRows = Integer.parseInt( URL.decodeComponent( results2[1] ) );
						}
						catch (Exception ex)
						{
							// Nothing to do.  This is here to handle the case when the data is
							// not properly url encoded.
						}
					}
					else if ( results2[0].equalsIgnoreCase( "cols" ) )
					{
						numCols = 0;
						try
						{
							numCols = Integer.parseInt( URL.decodeComponent( results2[1] ) );
						}
						catch (Exception ex)
						{
							// Nothing to do.  This is here to handle the case when the data is
							// not properly url encoded.
						}
						m_properties.setNumColumns( numCols );
					}
					else if ( results2[0].equalsIgnoreCase( "colWidths" ) )
					{
						String[] results3;
						String tmp;
					
						try
						{
							// Get the individual column widths.
							tmp = URL.decodeComponent( results2[1] );
							results3 = tmp.split( "[|]" );
							if ( results3 != null )
							{
								int j;
								
								for (j = 0; j < results3.length; ++j)
								{
									m_properties.setColWidth( j, results3[j] );
								}// end for()
							}
						}
						catch (Exception ex)
						{
							// Nothing to do.  This is here to handle the case when the data is
							// not properly url encoded.
						}
					}
					else if ( results2[0].equalsIgnoreCase( "widthUnits" ) )
					{
						String[] results3;
						String tmp;
					
						try
						{
							// Get the individual column width units.
							tmp = URL.decodeComponent( results2[1] );
							results3 = tmp.split( "[|]" );
							if ( results3 != null )
							{
								int j;
								
								for (j = 0; j < results3.length; ++j)
								{
									ColWidthUnit unit;
									String str;
									
									str = results3[j];
									if ( str != null && str.length() > 0 )
									{
										unit = ColWidthUnit.getEnum( Integer.parseInt( str ) );
										m_properties.setColWidthUnit( j, unit );
									}
								}
							}
						}
						catch (Exception ex)
						{
							// Nothing to do.  This is here to handle the case when the data is
							// not properly url encoded.
						}
					}
				}
			}// end for()
		}
		
		// Make sure the columns have a width.
		valid = true;
		numCols = m_properties.getNumColumnsInt();
		for (i = 0; i < numCols && valid == true; ++i)
		{
			String widthStr;
			
			widthStr = m_properties.getColWidth( i );
			if ( widthStr == null || widthStr.length() == 0 )
				valid = false;
		}
		
		// Are the columns valid?
		if ( !valid && numCols > 0 )
		{
			// No, make all the column widths equal.
			width = 100 / numCols;
			
			for (i = 0; i < numCols; ++i)
			{
				m_properties.setColWidth( i, String.valueOf( width ) );
				m_properties.setColWidthUnit( i, ColWidthUnit.PERCENTAGE );
			}
		}
		
		m_properties.setNumRows( numRows );
	}// end TableConfig()
	
	
	/**
	 * 
	 */
	public void addChild( ConfigItem configItem )
	{
		m_configItems.add( configItem );
	}// end addChild()
	
	
	/**
	 * Create a widget that can be used on any page.
	 */
	public VibeWidget createWidget( WidgetStyles widgetStyles )
	{
		TableWidget tableWidget;
		
		tableWidget = new TableWidget( this, widgetStyles );
		return tableWidget;
	}
	
	/**
	 * Create a DropWidget that can be used in the landing page editor.
	 */
	public TableDropWidget createDropWidget( LandingPageEditor lpe )
	{
		return new TableDropWidget( lpe, this );
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
	public TableProperties getProperties()
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
}// end TableConfig
